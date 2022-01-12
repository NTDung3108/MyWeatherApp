package com.example.model;
/* {
            "dt": 1620720000,
            "temp": 12.95,
            "feels_like": 12.73,
            "pressure": 1018,
            "humidity": 93,
            "dew_point": 12,
            "uvi": 0,
            "clouds": 100,
            "visibility": 10000,
            "wind_speed": 2.75,
            "wind_deg": 68,
            "wind_gust": 8.3,
            "weather": [
                {
                    "id": 500,
                    "main": "Rain",
                    "description": "mưa nhẹ",
                    "icon": "10n"
                }
            ],
            "pop": 0.93,
            "rain": {
                "1h": 0.58
            }
        }*/

public class HourlyWeather {
    private String time;
    private Double temp;
    private int pressure;
    private int humidity;
    private Double uvi;
    private int visibility;
    private String status;
    private String icon;
    private Double pop;
    private Double windSpeed;

    public HourlyWeather() {
    }

    public HourlyWeather(String time, Double temp, int pressure, int humidity, Double uvi,
                         int visibility, String status, String icon, Double pop, Double windSpeed) {
        this.time = time;
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.uvi = uvi;
        this.visibility = visibility;
        this.status = status;
        this.icon = icon;
        this.pop = pop;
        this.windSpeed = windSpeed;
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

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public Double getUvi() {
        return uvi;
    }

    public void setUvi(Double uvi) {
        this.uvi = uvi;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
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

    public Double getPop() {
        return pop;
    }

    public void setPop(Double pop) {
        this.pop = pop;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }
}
