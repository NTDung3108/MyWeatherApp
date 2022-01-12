package com.example.model;
/*{
            "dt": 1620583200,
            "sunrise": 1620559219,
            "sunset": 1620608694,
            "moonrise": 1620555900,
            "moonset": 1620602460,
            "moon_phase": 0.94,
            "temp": {
                "day": 23.62,
                "min": 16.34,
                "max": 25.27,
                "night": 16.34,
                "eve": 22.12,
                "morn": 21.78
            },
            "feels_like": {
                "day": 24.31,
                "night": 16.35,
                "eve": 22.63,
                "morn": 22.08
            },
            "pressure": 1010,
            "humidity": 87,
            "dew_point": 21.31,
            "wind_speed": 7.56,
            "wind_deg": 188,
            "wind_gust": 16.71,
            "weather": [
                {
                    "id": 501,
                    "main": "Rain",
                    "description": "mưa vừa",
                    "icon": "10d"
                }
            ],
            "clouds": 100,
            "pop": 1,
            "rain": 5.83,
            "uvi": 9.03
        },*/

public class DailyWeather {
    private String time;
    private Double maxTemp;
    private Double minTemp;
    private String icon;

    public DailyWeather() {
    }

    public DailyWeather(String time, Double maxTemp, Double minTemp, String icon) {
        this.time = time;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(Double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public Double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(Double minTemp) {
        this.minTemp = minTemp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
