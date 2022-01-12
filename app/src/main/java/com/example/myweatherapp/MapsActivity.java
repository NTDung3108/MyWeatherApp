package com.example.myweatherapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Common.Common;
import com.example.adapter.MyInfoWindowAdapter;
import com.example.model.CurrentWeather;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ProgressDialog dialog;

    EditText edSearch;
    ImageView imgMBack;
    CurrentWeather currentWeather;
    Marker marker = null;
    Place place;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        dialog = new ProgressDialog(MapsActivity.this);
        dialog.setTitle("Thông báo");
        dialog.setMessage("Đang xử lý, vui lòng chờ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), "AIzaSyAP9ViAFSCQHr4i_DjkbKcj0Lj2BarZNIk");
        PlacesClient placesClient = Places.createClient(this);

        edSearch = findViewById(R.id.edSearch);
        imgMBack = findViewById(R.id.imgMBack);
        addEvents();
    }

    private void addEvents() {
        edSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(MapsActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        imgMBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                mMap.clear();
                marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));

                getCurrentWeather(place.getLatLng().latitude, place.getLatLng().longitude, marker);
            } else if (requestCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void getCurrentWeather(double lat, double lon, Marker marker) {
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&lang=vi&exclude=minutely&units=metric&appid=53fbf527d52d4d773e828243b90c1f8e";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObjectCurrent = jsonObject.getJSONObject("current");
                        String currendt = jsonObjectCurrent.getString("dt");
                        double temp = Double.parseDouble(jsonObjectCurrent.getString("temp"));
                        double feels = Double.parseDouble(jsonObjectCurrent.getString("feels_like"));
                        double curentHumidity = Double.parseDouble(jsonObjectCurrent.getString("humidity"));
                        double currentClouds = Double.parseDouble(jsonObjectCurrent.getString("clouds"));
                        double currentWind = Double.parseDouble(jsonObjectCurrent.getString("wind_speed"));

                        JSONArray jsonArrayWeather = jsonObjectCurrent.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String currentMain = jsonObjectWeather.getString("main");
                        String currentIcon = jsonObjectWeather.getString("icon");

                        JSONArray jsonArray = jsonObject.getJSONArray("daily");
                        JSONObject jsonObjectDay = jsonArray.getJSONObject(0);
                        JSONObject jsonObjectTemp = jsonObjectDay.getJSONObject("temp");
                        double max = Double.parseDouble(jsonObjectTemp.getString("max"));

                        double min = Double.parseDouble(jsonObjectTemp.getString("min"));

                        currentWeather = new CurrentWeather(currendt, temp, feels, currentMain
                                , currentIcon, max, min, curentHumidity,currentClouds,currentWind);

                        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(MapsActivity.this,currentWeather
                                , lat, lon));
                        marker.showInfoWindow();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("MapTag", error.toString()));
        requestQueue.add(stringRequest);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
            if (Common.Latitude != null && Common.Longitude != null){
                LatLng loc = new LatLng(Common.Latitude, Common.Longitude);
                marker = mMap.addMarker(new MarkerOptions().position(loc));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,13));
                Intent intent = getIntent();
                currentWeather = (CurrentWeather) intent.getSerializableExtra("Weather");
                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(MapsActivity.this,currentWeather
                , Common.Latitude, Common.Longitude));
                marker.showInfoWindow();
                dialog.dismiss();
        }
        if (mMap == null) return;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                marker.remove();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
                marker = mMap.addMarker(markerOptions);
                getCurrentWeather(latLng.latitude, latLng.longitude, marker);
            }
        });
    }
}