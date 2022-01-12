package com.example.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.Common.SharePreferenceUtil;
import com.example.myweatherapp.FileHanding;
import com.example.myweatherapp.ImageMap;
import com.example.myweatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UpdateWidgetService extends JobIntentService {

    private static final String TAG = "UpdateWidgetService";

    public static final int JOB_ID = 3;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, UpdateWidgetService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String request = intent.getStringExtra("request");
        SharePreferenceUtil mSpUtil = new SharePreferenceUtil(this, "SPdata");
        if (request.equals("Refresh")){
            if (!isNetworkConnected()){
                toast("You need an internet connection to use this function");
            }else {
                double lat = Double.parseDouble(mSpUtil.getLat());
                double lon = Double.parseDouble(mSpUtil.getLon());
                String unitTemp = mSpUtil.getUnitTemp();
                getCurrentWeather(lat,lon,unitTemp);
            }
        }else if(request.equals("Update")){
            double temp = Double.parseDouble(mSpUtil.getSPTemp());
            String icon = mSpUtil.getSPIcon();
            String location = mSpUtil.getLocation();
            String weather = mSpUtil.getWeatherStatus();
            String unitTemp = mSpUtil.getUnitTemp();
            double min = Double.parseDouble(mSpUtil.getMinTemp());
            double max = Double.parseDouble(mSpUtil.getMaxTemp());
            updateWidget(location,temp,icon,weather,unitTemp, min, max);
        }
    }




    private static void showWeatherIcon(Context context, int appWidgetId, String iconUrl, RemoteViews views) {
        AppWidgetTarget widgetTarget = new AppWidgetTarget(context, R.id.appwidget_icon, views, appWidgetId) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);
            }
        };

        RequestOptions options = new RequestOptions().
                override(300, 300).placeholder(R.drawable.day).error(R.drawable.day);

        ImageMap imageMap = new ImageMap();
        HashMap<String, Integer> icWidget = imageMap.weatherIconMap();

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(icWidget.get(iconUrl))
                .apply(options)
                .into(widgetTarget);
    }

    private static void showWeatherBG(Context context, int appWidgetId, String iconUrl, RemoteViews views) {
        AppWidgetTarget widgetTarget = new AppWidgetTarget(context, R.id.appwidget_bg, views, appWidgetId) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);
            }
        };

        RequestOptions options = new RequestOptions().
                override(300, 300).placeholder(R.drawable.clear_widget_day).error(R.drawable.clear_widget_day);

        ImageMap imageMap = new ImageMap();
        HashMap<String, Integer> bgWidget = imageMap.weatherWidetMap();

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(bgWidget.get(iconUrl))
                .apply(options)
                .into(widgetTarget);
    }

    private void getCurrentWeather(double lat, double lon, String unitTemp) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&lang=vi&exclude=minutely&units=metric&appid=53fbf527d52d4d773e828243b90c1f8e";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        FileHanding fileHanding = new FileHanding();
                        fileHanding.writerFile(response,this);

                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObjectCurrent = jsonObject.getJSONObject("current");
                        String currendt = jsonObjectCurrent.getString("dt");
                        double temp = Double.parseDouble(jsonObjectCurrent.getString("temp"));

                        JSONArray jsonArrayWeather = jsonObjectCurrent.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String weather = jsonObjectWeather.getString("main");
                        String icon = jsonObjectWeather.getString("icon");

                        long l = Long.parseLong(currendt);
                        Date date = new Date(l * 1000L);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
                        JSONArray jsonArray = jsonObject.getJSONArray("daily");
                        JSONObject jsonObjectDay = jsonArray.getJSONObject(0);
                        JSONObject jsonObjectTemp = jsonObjectDay.getJSONObject("temp");
                        double min = Double.parseDouble(jsonObjectTemp.getString("min"));
                        double max = Double.parseDouble(jsonObjectTemp.getString("max"));

                        Geocoder geocoder;
                        List<Address> addresses = null;
                        geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(lat, lon, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Address address=null;
                        if(addresses.size()>0)
                            address=addresses.get(0);
                        if(address!=null)
                        {
                           String location = locationFormat(address.getAddressLine(0));
                           updateWidget(location, temp, icon, weather, unitTemp, min, max);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d(TAG, error.toString()));
        requestQueue.add(stringRequest);
    }

    private void updateWidget(String location, double temp, String icon, String weather, String unitTemp, double min, double max) {

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        ComponentName widgetComponent = new ComponentName(this, MyWeatherAppWidget.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        for (int appWidgetId : widgetIds) {
            RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(),
                    R.layout.my_weather_app_widget);

            remoteViews.setTextViewText(R.id.appwidget_Location, location);
            remoteViews.setTextViewText(R.id.appwidget_Weather, weather);
            if (unitTemp.equals("°F")){
                double tempF = temp*1.8+32;
                double maxF = max*1.8+32;
                double minF = min*1.8+32;
                String minMax = Math.round(minF)+"°F/"+Math.round(maxF)+"°F";
                remoteViews.setTextViewText(R.id.appwidget_MinMax, minMax);
                remoteViews.setTextViewText(R.id.appwidget_Temp, Math.round(tempF)+"°F");
            }else if (unitTemp.equals("°C")){
                String minMax = Math.round(min)+"°C/"+Math.round(max)+"°C";
                remoteViews.setTextViewText(R.id.appwidget_MinMax, minMax);
                remoteViews.setTextViewText(R.id.appwidget_Temp, Math.round(temp)+"°C");
            }
            showWeatherIcon(this, appWidgetId, icon, remoteViews);
            showWeatherBG(this, appWidgetId, icon, remoteViews);
            widgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    @SuppressWarnings("deprecation")
    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(UpdateWidgetService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String locationFormat(String s){

        String s1 = s.substring(s.lastIndexOf(","), s.length());
        String s2 = s.substring(0, s.lastIndexOf(","));

        String s3 = s2.substring(s2.lastIndexOf(","), s2.length());
        String s4 = s2.substring(0, s2.lastIndexOf(","));

        String s5 = s4.substring(s4.lastIndexOf(",")+1, s4.length());

        return s5+s3+s1;
    }
}
