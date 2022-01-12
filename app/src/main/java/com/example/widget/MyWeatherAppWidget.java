package com.example.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.Common.SharePreferenceUtil;
import com.example.myweatherapp.ImageMap;
import com.example.myweatherapp.MainActivity;
import com.example.myweatherapp.R;

import java.util.HashMap;


/**
 * Implementation of App Widget functionality.
 */
public class MyWeatherAppWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i("WeatherWidget", "onEnabled");
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.i("WeatherWidget", "onReceive action = " + action);
        if (action.equals(MainActivity.UPDATE_WIDGET_WEATHER_ACTION)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, MyWeatherAppWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
            onUpdate(context, appWidgetManager, appWidgetIds);
            Intent intentUpdate = new Intent(context, UpdateWidgetService.class);
            intentUpdate.putExtra("request", "Update");
            UpdateWidgetService.enqueueWork(context, intentUpdate);
        } else if (action.equals("com.example.BTN_UPDATE_WIDGET")) {
            Intent intentRefresh = new Intent(context, UpdateWidgetService.class);
            intentRefresh.putExtra("request", "Refresh");
            UpdateWidgetService.enqueueWork(context, intentRefresh);
        }else if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent intentRefresh = new Intent(context, UpdateWidgetService.class);
            intentRefresh.putExtra("request", "Refresh");
            UpdateWidgetService.enqueueWork(context, intentRefresh);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_weather_app_widget);
            perLoadWeather(context, remoteViews, appWidgetId);

            Intent btnIntent = new Intent(context, MyWeatherAppWidget.class);
            btnIntent.setAction("com.example.BTN_UPDATE_WIDGET");
            PendingIntent btnPendingIntent = PendingIntent.getBroadcast(context, 0, btnIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_btnRefresh, btnPendingIntent);

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_root, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

    }

    private void perLoadWeather(Context context, RemoteViews remoteViews, int appWidgetId) {
            SharePreferenceUtil mSpUtil = new SharePreferenceUtil(context, "SPdata");
            double temp = Double.parseDouble(mSpUtil.getSPTemp());
            String icon = mSpUtil.getSPIcon();
            String location = mSpUtil.getLocation();
            double min = Double.parseDouble(mSpUtil.getMinTemp());
            double max = Double.parseDouble(mSpUtil.getMaxTemp());

            String weather = mSpUtil.getWeatherStatus();
            remoteViews.setTextViewText(R.id.appwidget_Location, location);
            remoteViews.setTextViewText(R.id.appwidget_Weather, weather);
            if (mSpUtil.getUnitTemp().equals("°F")){
                double tempF = temp*1.8+32;
                double maxF = max*1.8+32;
                double minF = min*1.8+32;
                String minMax = Math.round(minF)+"°F/"+Math.round(maxF)+"°F";
                remoteViews.setTextViewText(R.id.appwidget_MinMax, minMax);
                remoteViews.setTextViewText(R.id.appwidget_Temp, Math.round(tempF)+"°F");
            }else if (mSpUtil.getUnitTemp().equals("°C")){
                String minMax = Math.round(min)+"°C/"+Math.round(max)+"°C";
                remoteViews.setTextViewText(R.id.appwidget_MinMax, minMax);
                remoteViews.setTextViewText(R.id.appwidget_Temp, Math.round(temp)+"°C");
            }
            showWeatherIcon(context, appWidgetId, icon, remoteViews);
            showWeatherBG(context, appWidgetId, icon, remoteViews);
    }
    private static void showWeatherIcon(Context context, int appWidgetId, String iconUrl, RemoteViews views) {
        AppWidgetTarget widgetTarget = new AppWidgetTarget(context, R.id.appwidget_icon, views, appWidgetId) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);
            }
        };
        ImageMap imageMap = new ImageMap();
        HashMap<String, Integer> icWidget = imageMap.weatherIconMap();

        RequestOptions options = new RequestOptions().
                override(300, 300).placeholder(R.drawable.day).error(R.drawable.day);

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

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

}