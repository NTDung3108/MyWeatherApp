package com.example.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.model.CurrentWeather;
import com.example.myweatherapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Activity context;
    CurrentWeather currentWeather;
    double latitude;
    double longitude;

    public MyInfoWindowAdapter(Activity context,CurrentWeather currentWeather, double latitude, double longitude) {
        this.context = context;
        this.currentWeather = currentWeather;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        View view = this.context.getLayoutInflater().inflate(R.layout.item_map, null);

        TextView txtLocation = view.findViewById(R.id.txtLocation);
        TextView txtMTime = view.findViewById(R.id.txtMTime);
        TextView txtMWeather = view.findViewById(R.id.txtMWeather);
        TextView txtMTemp = view.findViewById(R.id.txtMTemp);
        TextView txtMMinMaxTemp = view.findViewById(R.id.txtMMinMaxTemp);
        TextView txtMFeelsLike = view.findViewById(R.id.txtMFeelsLike);
        TextView txtMHumidity = view.findViewById(R.id.txtMHumidity);
        TextView txtMCloud = view.findViewById(R.id.txtMCloud);
        TextView txtMWind = view.findViewById(R.id.txtMWind);

        ImageView imgMWeather = view.findViewById(R.id.imgMWeather);

        long l = Long.parseLong(currentWeather.getTime());
        Date date = new Date(l * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
        txtMTime.setText(simpleDateFormat.format(date));

        txtMWeather.setText(currentWeather.getStatus());
        txtMTemp.setText(Math.round(currentWeather.getTemp())+"°C");
        txtMMinMaxTemp.setText(Math.round(currentWeather.getMax())+"°C/"+Math.round(currentWeather.getMin())+"°C");
        txtMFeelsLike.setText(Math.round(currentWeather.getFeels_like())+"°C");
        txtMHumidity.setText(currentWeather.getHumidity()+"%");
        txtMCloud.setText(currentWeather.getClouds()+"%");
        txtMWind.setText(currentWeather.getWindspeed()+"m/s");

        Glide.with(context)
                .load("https://openweathermap.org/img/wn/"+currentWeather.getIcon()+".png")
                .into(imgMWeather);
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this.context, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            Address address=null;
            if(addresses.size()>0)
                address=addresses.get(0);
            if(address!=null)
            {
                txtLocation.setText(address.getAddressLine(0));
                /*String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();
                String postalCode = address.getPostalCode();
                String knownName = address.getFeatureName();*/
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }
}
