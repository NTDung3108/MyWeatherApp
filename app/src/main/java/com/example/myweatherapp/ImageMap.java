package com.example.myweatherapp;

import java.util.HashMap;
import java.util.Map;

public class ImageMap{

    public HashMap<String, Integer> weatherIconMap() {
        HashMap<String, Integer> mWeatherIcon = new HashMap<String, Integer>();
        mWeatherIcon.put("01d", R.drawable.d01);
        mWeatherIcon.put("01n", R.drawable.n01);
        mWeatherIcon.put("02d", R.drawable.d02);
        mWeatherIcon.put("02n", R.drawable.n02);
        mWeatherIcon.put("03d", R.drawable.d03);
        mWeatherIcon.put("03n", R.drawable.n03);
        mWeatherIcon.put("04d", R.drawable.d04);
        mWeatherIcon.put("04n", R.drawable.n04);
        mWeatherIcon.put("09d", R.drawable.d09);
        mWeatherIcon.put("09n", R.drawable.n09);
        mWeatherIcon.put("10d", R.drawable.d10);
        mWeatherIcon.put("10n", R.drawable.n10);
        mWeatherIcon.put("11d", R.drawable.d11);
        mWeatherIcon.put("11n", R.drawable.n11);
        mWeatherIcon.put("13d", R.drawable.d13);
        mWeatherIcon.put("13n", R.drawable.n13);
        mWeatherIcon.put("50d", R.drawable.d50);
        mWeatherIcon.put("50n", R.drawable.n50);
        return mWeatherIcon;
    }

    public HashMap<String, Integer> weatherBGMap() {
        HashMap<String, Integer> mWeatherBG = new HashMap<String, Integer>();
        mWeatherBG.put("01d", R.drawable.clear_01d);
        mWeatherBG.put("01n", R.drawable.clear_01n);
        mWeatherBG.put("02d", R.drawable.few_clouds_02d);
        mWeatherBG.put("02n", R.drawable.few_clouds_02n);
        mWeatherBG.put("03d", R.drawable.scattered_clouds_03d);
        mWeatherBG.put("03n", R.drawable.scattered_clouds_03n);
        mWeatherBG.put("04d", R.drawable.broken_clouds_04d);
        mWeatherBG.put("04n", R.drawable.broken_clouds_04n);
        mWeatherBG.put("09d", R.drawable.rain_10d);
        mWeatherBG.put("09n", R.drawable.rain_10n);
        mWeatherBG.put("10d", R.drawable.rain_10d);
        mWeatherBG.put("10n", R.drawable.rain_10n);
        mWeatherBG.put("11d", R.drawable.snow_day_13d);
        mWeatherBG.put("11n", R.drawable.snow_night_13n);
        mWeatherBG.put("50d", R.drawable.mist_50d);
        mWeatherBG.put("50n", R.drawable.mist_50n);
        return mWeatherBG;
    }

    public HashMap<String, Integer> weatherWidetMap() {
        HashMap<String, Integer> mWeatherWidget = new HashMap<String, Integer>();
        mWeatherWidget.put("01d", R.drawable.clear_widget_day);
        mWeatherWidget.put("01n", R.drawable.clear_widget_night);
        mWeatherWidget.put("02d", R.drawable.cloud_widget_day);
        mWeatherWidget.put("02n", R.drawable.cloud_widget_night);
        mWeatherWidget.put("03d", R.drawable.cloud_widget_day);
        mWeatherWidget.put("03n", R.drawable.cloud_widget_night);
        mWeatherWidget.put("04d", R.drawable.cloud_widget_day);
        mWeatherWidget.put("04n", R.drawable.cloud_widget_night);
        mWeatherWidget.put("09d", R.drawable.rain_widget_day);
        mWeatherWidget.put("09n", R.drawable.rain_widget_night);
        mWeatherWidget.put("10d", R.drawable.rain_widget_day);
        mWeatherWidget.put("10n", R.drawable.rain_widget_night);
        mWeatherWidget.put("11d", R.drawable.snow_widget_day);
        mWeatherWidget.put("11n", R.drawable.snow_widget_night);
        mWeatherWidget.put("50d", R.drawable.mist_widget_day);
        mWeatherWidget.put("50n", R.drawable.mist_widget_night);
        return mWeatherWidget;
    }
}
