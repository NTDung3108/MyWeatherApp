package com.example.model;

public class WeatherNotification {
    private String wnDT;
    private String wnMax;
    private String wnMin;
    private String wnWeather;
    private String wnDescription;
    private String wnIcon;

    public WeatherNotification() {
    }

    public WeatherNotification(String wnDT, String wnMax, String wnMin, String wnWeather, String wnDescription, String wnIcon) {
        this.wnDT = wnDT;
        this.wnMax = wnMax;
        this.wnMin = wnMin;
        this.wnWeather = wnWeather;
        this.wnDescription = wnDescription;
        this.wnIcon = wnIcon;
    }

    public String getWnDT() {
        return wnDT;
    }

    public void setWnDT(String wnDT) {
        this.wnDT = wnDT;
    }

    public String getWnMax() {
        return wnMax;
    }

    public void setWnMax(String wnMax) {
        this.wnMax = wnMax;
    }

    public String getWnMin() {
        return wnMin;
    }

    public void setWnMin(String wnMin) {
        this.wnMin = wnMin;
    }

    public String getWnWeather() {
        return wnWeather;
    }

    public void setWnWeather(String wnWeather) {
        this.wnWeather = wnWeather;
    }

    public String getWnDescription() {
        return wnDescription;
    }

    public void setWnDescription(String wnDescription) {
        this.wnDescription = wnDescription;
    }

    public String getWnIcon() {
        return wnIcon;
    }

    public void setWnIcon(String wnIcon) {
        this.wnIcon = wnIcon;
    }
}
