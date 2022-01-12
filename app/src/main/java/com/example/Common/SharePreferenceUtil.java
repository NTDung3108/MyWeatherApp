package com.example.Common;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharePreferenceUtil(Context context, String file) {
        sharedPreferences = context.getSharedPreferences(file, 0);
        editor = sharedPreferences.edit();
    }

    public void setUnitTemp(String unitTemp){
        editor.putString("unitTemp", unitTemp);
        editor.commit();
    }

    public String getUnitTemp(){
        return sharedPreferences.getString("unitTemp", "°C");
    }

    public void setUnitWind(String unitWind){
        editor.putString("unitWind", unitWind);
        editor.commit();
    }

    public String getUnitWind(){
        return sharedPreferences.getString("unitWind", "m/s");
    }

    public void setStatus(String status){
        editor.putString("status", status);
        editor.commit();
    }

    public String getStatus(){
        return sharedPreferences.getString("status", "default");
    }

    public void setLat(String lat){
        editor.putString("lat", lat);
        editor.commit();
    }

    public String getLat(){
        return sharedPreferences.getString("lat", "0");
    }

    public void setLon(String lon){
        editor.putString("lon", lon);
        editor.commit();
    }

    public String getLon(){
        return sharedPreferences.getString("lon", "0");
    }

    public void setLocation(String location){
        editor.putString("location", location);
        editor.commit();
    }

    public String getLocation(){
        return sharedPreferences.getString("location", "");
    }

    public void setSPTemp(String spTemp){
        editor.putString("spTemp", spTemp);
        editor.commit();
    }

    public String getSPTemp(){
        return sharedPreferences.getString("spTemp", "");
    }

    public void setSPIcon(String icon){
        editor.putString("icon", icon);
        editor.commit();
    }

    public String getSPIcon(){
        return sharedPreferences.getString("icon", "10n");
    }

    public void setWeatherStatus(String weatherStatus){
        editor.putString("weatherStatus", weatherStatus);
        editor.commit();
    }

    public String getWeatherStatus(){
        return sharedPreferences.getString("weatherStatus", "");
    }

    public void setMinTemp(String minTemp){
        editor.putString("minTemp", minTemp);
        editor.commit();
    }

    public String getMinTemp(){
        return sharedPreferences.getString("minTemp", "");
    }
    public void setMaxTemp(String maxTemp){
        editor.putString("maxTemp", maxTemp);
        editor.commit();
    }

    public String getMaxTemp(){
        return sharedPreferences.getString("maxTemp", "");
    }
}
