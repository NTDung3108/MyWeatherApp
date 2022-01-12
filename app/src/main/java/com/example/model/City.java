package com.example.model;

public class City {
    private String country;
    private String name;
    private String lon;
    private String lat;

    public City() {
    }

    public City(String country, String name, String lon, String lat) {
        this.country = country;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
