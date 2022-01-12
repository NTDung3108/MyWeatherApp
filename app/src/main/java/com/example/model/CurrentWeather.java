package com.example.model;

import java.io.Serializable;

public class CurrentWeather implements Serializable {
    private String time;
    private Double temp;
    private Double feels_like;
    private String status;
    private String icon;
    private Double max;
    private Double min;
    private Double humidity;
    private Double clouds;
    private Double windspeed;

    public CurrentWeather() {
    }

    public CurrentWeather(String time, Double temp, Double feels_like, String status, String icon,
                          Double max, Double min, Double humidity, Double clouds, Double windspeed) {
        this.time = time;
        this.temp = temp;
        this.feels_like = feels_like;
        this.status = status;
        this.icon = icon;
        this.max = max;
        this.min = min;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windspeed = windspeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(Double feels_like) {
        this.feels_like = feels_like;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getClouds() {
        return clouds;
    }

    public void setClouds(Double clouds) {
        this.clouds = clouds;
    }

    public Double getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(Double windspeed) {
        this.windspeed = windspeed;
    }
}
