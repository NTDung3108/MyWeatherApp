package com.example.myweatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.Common.Common;
import com.example.Common.SharePreferenceUtil;
import com.example.adapter.ViewPagerAdapter;
import com.example.fragment.FragmentDailyWeather;
import com.example.fragment.FragmentHourlyWeather;
import com.example.model.City;
import com.example.model.CurrentWeather;
import com.example.notification.NotificationReceiver;
import com.example.widget.MyWeatherAppWidget;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.fragment.FragmentHourlyWeather.hourlys;
import static com.example.myweatherapp.ApplicationClass.CHANNEL_ID;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "@@@@@#######::";
    public static final String UPDATE_WIDGET_WEATHER_ACTION = "com.example.UPDATE_WIDGET";
    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_CODE_SEND=0;
    public static final int RESULT_CODE_C_MS=1;
    public static final int RESULT_CODE_F_MS=2;
    public static final int RESULT_CODE_C_MPH=3;
    public static final int RESULT_CODE_F_MPH=4;

    ImageView imgLocation, imgSearch, imgMenu, imgWeather, imgBG;
    TextView txtCityName, txtTime, txtTemp, txtMinMaxTemp, txtFeelsLike, txtWeather;
    ViewPager viewPager;
    TabLayout tabLayout;
    AutoCompleteTextView autoCompleteTextView;

    public static ArrayList<City> cityArrayList = new ArrayList<>();
    public static ArrayList<String> cityName = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private String icon = "";
    double temp, min, max;

    ProgressDialog dialog;
    CurrentWeather currentWeather;
    SharePreferenceUtil preferenceUtil;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    Thread addCityThread;

    ImageMap imageMap = new ImageMap();

    public static HashMap<String, Integer> icons = new HashMap<>();
    private HashMap<String, Integer> backgroup = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Getting data from sever, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        addControls();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        preferenceUtil = new SharePreferenceUtil(this, "SPdata");
        Common.selectUnitWind = preferenceUtil.getUnitWind();
        Common.selectUnitTemp = preferenceUtil.getUnitTemp();
        if(!isNetworkConnected())
        {
            Log.d("network", "offline");
            FileHanding fileHanding = new FileHanding();
            if (fileHanding.readFile(this).equals("")){
                Log.d("file3", "Không thể đọc đc file");
                dialog.dismiss();
            }else {
                String json = fileHanding.readFile(this);
                ViewPagerAdapter viewPagerAdapter
                        = new ViewPagerAdapter(getSupportFragmentManager()
                        , FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
                getCurrentWeatherFromFile(json);
                Log.d("file3", json);
            }
        }else {
            Common.status = preferenceUtil.getStatus();
            if (Common.status.equals("default")) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
                    dialog.dismiss();
                    return;
                } else {
                    getMyLocation();
                }
            } else if (Common.status.equals("searching")) {
                Common.Latitude = Double.parseDouble(preferenceUtil.getLat());
                Common.Longitude = Double.parseDouble(preferenceUtil.getLon());
                getCurrentWeather(Common.Latitude, Common.Longitude);
                ViewPagerAdapter viewPagerAdapter
                        = new ViewPagerAdapter(getSupportFragmentManager()
                        , FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        }
        addEvents();
    }

    @SuppressLint("SetTextI18n")
    private void getCurrentWeatherFromFile(String json) {
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonObjectCurrent = jsonObject.getJSONObject("current");
            String currendt = jsonObjectCurrent.getString("dt");
            String currentTemp = jsonObjectCurrent.getString("temp");
            String currentFeels = jsonObjectCurrent.getString("feels_like");
            double curentHumidity = Double.parseDouble(jsonObjectCurrent.getString("humidity"));
            double currentClouds = Double.parseDouble(jsonObjectCurrent.getString("clouds"));
            double currentWind = Double.parseDouble(jsonObjectCurrent.getString("wind_speed"));

            JSONArray jsonArrayWeather = jsonObjectCurrent.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
            String currentMain = jsonObjectWeather.getString("main");
            String currentIcon = jsonObjectWeather.getString("icon");
            icon = currentIcon;

            if (icon.contains("n")){
                txtCityName.setTextColor(Color.WHITE);
                txtTime.setTextColor(Color.WHITE);
                txtWeather.setTextColor(Color.WHITE);
                txtTemp.setTextColor(Color.WHITE);
                txtFeelsLike.setTextColor(Color.WHITE);
                txtMinMaxTemp.setTextColor(Color.WHITE);
                autoCompleteTextView.setHintTextColor(Color.WHITE);
                imgSearch.setImageResource(R.drawable.ic_search_24_white);
                imgMenu.setImageResource(R.drawable.ic_more_vert_24_white);
            }

            long l = Long.parseLong(currendt);
            Date date = new Date(l * 1000L);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);

            temp = Double.parseDouble(currentTemp);
            double feels = Double.parseDouble(currentFeels);

            JSONArray jsonArray = jsonObject.getJSONArray("daily");
            JSONObject jsonObjectDay = jsonArray.getJSONObject(0);
            JSONObject jsonObjectTemp = jsonObjectDay.getJSONObject("temp");
            max = Double.parseDouble(jsonObjectTemp.getString("max"));

            min = Double.parseDouble(jsonObjectTemp.getString("min"));
            currentWeather = new CurrentWeather(currendt, temp, feels, currentMain
                    , currentIcon, max, min, curentHumidity,currentClouds,currentWind);
            txtCityName.setText(preferenceUtil.getLocation());
            txtTime.setText(simpleDateFormat.format(date));
            txtWeather.setText(currentMain);
            Glide.with(MainActivity.this).load(icons.get(icon)).into(imgWeather);
            Glide.with(MainActivity.this).load(backgroup.get(icon)).into(imgBG);
            if (Common.selectUnitTemp.equals("°F")){
                double feelsF = feels*1.8+32;
                double tempF = temp*1.8+32;
                double maxF = max*1.8+32;
                double minF = min*1.8+32;
                txtTemp.setText(Math.round(tempF)+"°F");
                txtFeelsLike.setText("Feel like "+Math.round(feelsF)+"°F");
                txtMinMaxTemp.setText(Math.round(minF)+"°F/"+Math.round(maxF)+"°F");
            }else if(Common.selectUnitTemp.equals("°C")) {
                txtMinMaxTemp.setText(Math.round(min) + "°C/" + Math.round(max) + "°C");
                txtTemp.setText(Math.round(temp) + "°C");
                txtFeelsLike.setText("Feel like " + Math.round(feels) + "°C");
            }
            currentWeather = new CurrentWeather(currendt, temp, feels, currentMain
                    , currentIcon, max, min, curentHumidity,currentClouds,currentWind);
            viewPager.getAdapter().notifyDataSetChanged();
            dialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"NonConstantResourceId", "MissingPermission"})
    private void addEvents() {
        imgSearch.setOnClickListener(v -> {
            if(!isNetworkConnected()){
                Toast.makeText(MainActivity.this,"You need to connect to your internet",Toast.LENGTH_LONG).show();
                return;
            }
            String input = autoCompleteTextView.getText().toString();
            if (input.equals("")){
                Toast.makeText(MainActivity.this,"You need to enter a location ",Toast.LENGTH_LONG).show();
                return;
            }
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Getting data from sever, please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            for (City city : cityArrayList) {
                if (city.getName().contains(input)) {
                    Common.Latitude = Double.valueOf(city.getLat());
                    Common.Longitude = Double.valueOf(city.getLon());
                }
            }
            getCurrentWeather(Common.Latitude, Common.Longitude);
            viewPager.getAdapter().notifyDataSetChanged();
            Common.status = "searching";
            autoCompleteTextView.setText("");
        });
        imgMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.setting:
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_SEND);
                        Toast.makeText(MainActivity.this,"setting",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.update:
                        if(!isNetworkConnected()){
                            Toast.makeText(MainActivity.this,"You need to connect to your internet",Toast.LENGTH_LONG).show();
                            break;
                        }
                        dialog = new ProgressDialog(MainActivity.this);
                        dialog.setMessage("Getting data from sever, please wait...");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Toast.makeText(MainActivity.this,"update",Toast.LENGTH_LONG).show();
                        getCurrentWeather(Common.Latitude, Common.Longitude);
                        viewPager.getAdapter().notifyDataSetChanged();
                        break;
                    case R.id.map:
                        if(!isNetworkConnected()){
                            Toast.makeText(MainActivity.this,"You need to connect to your internet",Toast.LENGTH_LONG).show();
                            break;
                        }
                        Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
                        mapIntent.putExtra("Weather", currentWeather);
                        startActivity(mapIntent);
                        Toast.makeText(MainActivity.this,"map",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.share:
                        verifyStoragePermission(MainActivity.this);
                        ShareImage shareImage = new ShareImage();
                        View view = getWindow().getDecorView().getRootView();
                        String filepath = Environment.getExternalStorageDirectory()+ "/Download/";
                        shareImage.takeScreenShot(view,MainActivity.this, filepath);

                        break;
                }
                return true;
            });
        });
        imgLocation.setOnClickListener(v -> {
            if(!isNetworkConnected()){
                Toast.makeText(MainActivity.this,"You need to connect to your internet",Toast.LENGTH_LONG).show();
                return;
            }
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Getting data from sever, please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Common.Latitude = task.getResult().getLatitude();
                    Common.Longitude = task.getResult().getLongitude();
                    Log.d("Location", Common.Latitude + "," + Common.Longitude);
                    getCurrentWeather(Common.Latitude, Common.Longitude);
                    viewPager.getAdapter().notifyDataSetChanged();
                    Common.status = "default";
                } else {
                    Log.i(TAG, "Inside getLocation function. Error while getting location");
                    System.out.println(TAG + task.getException());
                    dialog.dismiss();
                }
            });
        });
    }

    public static void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        double tempFRS, maxFRS, minFRS, feelsFRS;
        if (requestCode == REQUEST_CODE_SEND){
            switch (resultCode){
                case RESULT_CODE_C_MS:
                    txtTemp.setText(Math.round(currentWeather.getTemp())+"°C");
                    txtFeelsLike.setText("Feel like "+Math.round(currentWeather.getFeels_like())+"°C");
                    txtMinMaxTemp.setText(Math.round(currentWeather.getMin())+"°C/"+Math.round(currentWeather.getMax())+"°C");
                    FragmentHourlyWeather.txtWind.setText(hourlys.get(0).getWindSpeed()+" m/s");
                    FragmentHourlyWeather.hourlyAdapter.notifyDataSetChanged();
                    FragmentDailyWeather.dailyAdapter.notifyDataSetChanged();
                    break;
                case RESULT_CODE_F_MS:
                    tempFRS = currentWeather.getTemp()*1.8+32;
                    maxFRS= currentWeather.getMax()*1.8+32;
                    minFRS = currentWeather.getMin()*1.8+32;
                    feelsFRS = currentWeather.getFeels_like()*1.8+32;
                    txtTemp.setText(Math.round(tempFRS)+"°F");
                    txtFeelsLike.setText("Feel like "+Math.round(feelsFRS)+"°F");
                    txtMinMaxTemp.setText(Math.round(minFRS)+"°F/"+Math.round(maxFRS)+"°F");
                    FragmentHourlyWeather.txtWind.setText(hourlys.get(0).getWindSpeed()+" m/s");
                    FragmentHourlyWeather.hourlyAdapter.notifyDataSetChanged();
                    FragmentDailyWeather.dailyAdapter.notifyDataSetChanged();
                    break;
                case RESULT_CODE_C_MPH:
                    txtTemp.setText(Math.round(currentWeather.getTemp())+"°C");
                    txtFeelsLike.setText("Feel like "+Math.round(currentWeather.getFeels_like())+"°C");
                    txtMinMaxTemp.setText(Math.round(currentWeather.getMin())+"°C/"+Math.round(currentWeather.getMax())+"°C");
                    FragmentHourlyWeather.hourlyAdapter.notifyDataSetChanged();
                    FragmentDailyWeather.dailyAdapter.notifyDataSetChanged();
                    double wind = hourlys.get(0).getWindSpeed()*2.24;
                    FragmentHourlyWeather.txtWind.setText(((double)Math.round(wind*100)/100)+" MPH");
                    FragmentHourlyWeather.hourlyAdapter.notifyDataSetChanged();
                    FragmentDailyWeather.dailyAdapter.notifyDataSetChanged();
                    break;
                case RESULT_CODE_F_MPH:
                    tempFRS = currentWeather.getTemp()*1.8+32;
                    maxFRS = currentWeather.getMax()*1.8+32;
                    minFRS = currentWeather.getMin()*1.8+32;
                    feelsFRS = currentWeather.getFeels_like()*1.8+32;
                    txtTemp.setText(Math.round(tempFRS)+"°F");
                    txtFeelsLike.setText("Feel like "+Math.round(feelsFRS)+"°F");
                    txtMinMaxTemp.setText(Math.round(minFRS)+"°F/"+Math.round(maxFRS)+"°F");
                    double windF = hourlys.get(0).getWindSpeed()*2.24;
                    FragmentHourlyWeather.txtWind.setText(((double)Math.round(windF*100)/100)+"MPH");
                    FragmentHourlyWeather.hourlyAdapter.notifyDataSetChanged();
                    FragmentDailyWeather.dailyAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Location location = task.getResult();
                    if (location != null) {
                        Common.Latitude = location.getLatitude();
                        Common.Longitude = location.getLongitude();
                        getCurrentWeather(Common.Latitude, Common.Longitude);
                        ViewPagerAdapter viewPagerAdapter
                                = new ViewPagerAdapter(getSupportFragmentManager()
                                , FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

                        viewPager.setAdapter(viewPagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                    }else {
                        Log.i(TAG, "Inside getLocation function. Error while getting location");
                        System.out.println(TAG + task.getException());
                        dialog.dismiss();
                    }

                } else {
                    Log.i(TAG, "Inside getLocation function. Error while getting location");
                    System.out.println(TAG + task.getException());
                    dialog.dismiss();
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void getCurrentWeather(double lat , double lon) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&lang=vi&exclude=minutely&units=metric&appid=53fbf527d52d4d773e828243b90c1f8e";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        FileHanding fileHanding = new FileHanding();
                        fileHanding.writerFile(response,this);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObjectCurrent = jsonObject.getJSONObject("current");
                        String currendt = jsonObjectCurrent.getString("dt");
                        String currentTemp = jsonObjectCurrent.getString("temp");
                        String currentFeels = jsonObjectCurrent.getString("feels_like");
                        double curentHumidity = Double.parseDouble(jsonObjectCurrent.getString("humidity"));
                        double currentClouds = Double.parseDouble(jsonObjectCurrent.getString("clouds"));
                        double currentWind = Double.parseDouble(jsonObjectCurrent.getString("wind_speed"));

                        JSONArray jsonArrayWeather = jsonObjectCurrent.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String currentMain = jsonObjectWeather.getString("main");
                        String currentIcon = jsonObjectWeather.getString("icon");
                        icon = currentIcon;

                        if (icon.contains("n")){
                            txtCityName.setTextColor(Color.WHITE);
                            txtTime.setTextColor(Color.WHITE);
                            txtWeather.setTextColor(Color.WHITE);
                            txtTemp.setTextColor(Color.WHITE);
                            txtFeelsLike.setTextColor(Color.WHITE);
                            txtMinMaxTemp.setTextColor(Color.WHITE);
                        }

                        long l = Long.parseLong(currendt);
                        Date date = new Date(l * 1000L);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
                        txtTime.setText(simpleDateFormat.format(date));

                        temp = Double.parseDouble(currentTemp);
                        double feels = Double.parseDouble(currentFeels);

                        txtWeather.setText(currentMain);
                        Glide.with(MainActivity.this).load(icons.get(icon)).into(imgWeather);
                        Glide.with(MainActivity.this).load(backgroup.get(icon)).into(imgBG);
                        JSONArray jsonArray = jsonObject.getJSONArray("daily");
                        JSONObject jsonObjectDay = jsonArray.getJSONObject(0);
                        JSONObject jsonObjectTemp = jsonObjectDay.getJSONObject("temp");
                        max = Double.parseDouble(jsonObjectTemp.getString("max"));

                        min = Double.parseDouble(jsonObjectTemp.getString("min"));

                        if (Common.selectUnitTemp.equals("°F")){
                            double feelsF = feels*1.8+32;
                            double tempF = temp*1.8+32;
                            double maxF = max*1.8+32;
                            double minF = min*1.8+32;
                            txtTemp.setText(Math.round(tempF)+"°F");
                            txtFeelsLike.setText("Feel like "+Math.round(feelsF)+"°F");
                            txtMinMaxTemp.setText(Math.round(minF)+"°F/"+Math.round(maxF)+"°F");
                        }else if(Common.selectUnitTemp.equals("°C")) {
                            txtMinMaxTemp.setText(Math.round(min) + "°C/" + Math.round(max) + "°C");
                            txtTemp.setText(Math.round(temp) + "°C");
                            txtFeelsLike.setText("Feel like " + Math.round(feels) + "°C");
                        }
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        Address address=null;
                        if(addresses.size()>0)
                            address=addresses.get(0);
                        if(address!=null)
                        {
                            String location = address.getAddressLine(0);
                            txtCityName.setText(locationFormat(location));
                        }

                        currentWeather = new CurrentWeather(currendt, temp, feels, currentMain
                                , currentIcon, max, min, curentHumidity,currentClouds,currentWind);
                        viewPager.getAdapter().notifyDataSetChanged();
                        dialog.dismiss();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d(TAG, error.toString()));
        requestQueue.add(stringRequest);
    }

    private void addControls(){
        txtCityName = findViewById(R.id.txtCityName);
        txtTime = findViewById(R.id.txtTime);
        txtTemp = findViewById(R.id.txtTemp);
        txtMinMaxTemp = findViewById(R.id.txtMinMaxTemp);
        txtFeelsLike = findViewById(R.id.txtFeelsLike);
        txtWeather = findViewById(R.id.txtWeather);
        imgLocation = findViewById(R.id.imgLocation);
        imgSearch = findViewById(R.id.imgSearch);
        imgMenu = findViewById(R.id.imgMenu);
        imgWeather = findViewById(R.id.imgWeather);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        imgBG = findViewById(R.id.imgBG);
        icons = imageMap.weatherIconMap();
        backgroup = imageMap.weatherBGMap();
        boolean isNotificationChannelEnabled = isNotificationChannelEnabled(MainActivity.this,CHANNEL_ID);
        setNotificationServiceAlarm(MainActivity.this,isNotificationChannelEnabled);
        addCityList();
    }

    public void addCityList(){
        addCityThread = new Thread(){
            @Override
            public void run() {
                super.run();
                getJSONFromAsset(loadJSONFromAsset());
                arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, cityName);
                MainActivity.this.runOnUiThread(() -> autoCompleteTextView.setAdapter(arrayAdapter));
            }
        };
        addCityThread.start();
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream inputStream = getAssets().open("cities.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private void getJSONFromAsset(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String country = jsonObject.getString("country");
                String lon = jsonObject.getString("lng");
                String lat = jsonObject.getString("lat");
                cityArrayList.add(new City(country, name, lon, lat));
                cityName.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        // There are no active networks.
        return ni != null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferenceUtil.setLat(String.valueOf(Common.Latitude));
        preferenceUtil.setLon(String.valueOf(Common.Longitude));
        preferenceUtil.setStatus(Common.status);
        preferenceUtil.setLocation(txtCityName.getText().toString());
        preferenceUtil.setSPTemp(String.valueOf(Math.round(temp)));
        preferenceUtil.setSPIcon(icon);
        preferenceUtil.setWeatherStatus(txtWeather.getText().toString());
        preferenceUtil.setMinTemp(String.valueOf(Math.round(min)));
        preferenceUtil.setMaxTemp(String.valueOf(Math.round(max)));
        preferenceUtil.setUnitTemp(Common.selectUnitTemp);
        preferenceUtil.setUnitWind(Common.selectUnitWind);
        updateWidgetWeather();
    }

    private void updateWidgetWeather() {
        Intent intent = new Intent(this, MyWeatherAppWidget.class);
        intent.setAction(UPDATE_WIDGET_WEATHER_ACTION);
        sendBroadcast(intent);
    }

    private void setNotificationServiceAlarm(Context context,
                                             boolean isNotificationChannelEnabled) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,8);
        calendar.set(Calendar.MINUTE,15);
        calendar.set(Calendar.SECOND,0);
        if (isNotificationChannelEnabled) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
            Log.i("notification", "alarm start");
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private boolean isNotificationChannelEnabled(Context context, @Nullable String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!TextUtils.isEmpty(channelId)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = manager.getNotificationChannel(channelId);
                return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
            return false;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }

    private String locationFormat(String s){

        String s1 = s.substring(s.lastIndexOf(","));
        String s2 = s.substring(0, s.lastIndexOf(","));

        String s3 = s2.substring(s2.lastIndexOf(","));
        String s4 = s2.substring(0, s2.lastIndexOf(","));

        String s5 = s4.substring(s4.lastIndexOf(",")+1);

        return s5+s3+s1;
    }
}