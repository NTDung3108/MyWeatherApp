package com.example.notification;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Common.SharePreferenceUtil;
import com.example.model.WeatherNotification;
import com.example.myweatherapp.FileHanding;
import com.example.myweatherapp.ImageMap;
import com.example.myweatherapp.MainActivity;
import com.example.myweatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.example.myweatherapp.ApplicationClass.CHANNEL_ID;

public class NotificationService extends JobIntentService {

    private static final String TAG = "NotificationsService";
    FileHanding fileHanding = new FileHanding();

    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (!isNetworkConnected()){
            return;
        }else {
            SharePreferenceUtil mSPUtil = new SharePreferenceUtil(this,"SPdata");
            String lat = mSPUtil.getLat();
            String lon = mSPUtil.getLon();
            getWeatherToday(lat,lon);
        }
    }

    private void getWeatherToday(String lat, String lon) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&exclude=minutely&units=metric&appid=53fbf527d52d4d773e828243b90c1f8e";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        fileHanding.writerFile(response,this);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArrayDaily = jsonObject.getJSONArray("daily");
                        JSONObject jsonObjectDaily = jsonArrayDaily.getJSONObject(0);
                        String dt = jsonObjectDaily.getString("dt");
                        JSONObject jsonObjectTemp = jsonObjectDaily.getJSONObject("temp");
                        String max = jsonObjectTemp.getString("max");
                        String min = jsonObjectTemp.getString("min");

                        JSONArray jsonArrayWeather = jsonObjectDaily.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String weather = jsonObjectWeather.getString("main");
                        String description = jsonObjectWeather.getString("description");
                        String icon = jsonObjectWeather.getString("icon");
                        WeatherNotification weatherNotification = new WeatherNotification(dt,max,min,weather,description,icon);
                        showNoticaiton(weatherNotification);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d(TAG, error.toString()));
        requestQueue.add(stringRequest);
    }

    private void showNoticaiton(WeatherNotification weatherNotification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        ImageMap imageMap = new ImageMap();
        HashMap<String, Integer> icNotification = imageMap.weatherIconMap();
        Bitmap icon = BitmapFactory.decodeResource(getResources(), icNotification.get(weatherNotification.getWnIcon()));

        int min = Math.round(Float.parseFloat(weatherNotification.getWnMin()));
        int max = Math.round(Float.parseFloat(weatherNotification.getWnMax()));

        String contentText = "Description: "+weatherNotification.getWnDescription()+"\n"+"Temperature: "+min+"°C - "+max+"°C";


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentInfo("My Weather App")
                .setSmallIcon(R.drawable.cloudy)
                .setContentTitle(weatherNotification.getWnWeather())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setLargeIcon(icon)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
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
}
