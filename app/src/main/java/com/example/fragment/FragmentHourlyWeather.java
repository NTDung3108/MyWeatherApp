package com.example.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.Common.Common;
import com.example.adapter.HourlyAdapter;
import com.example.model.HourlyWeather;
import com.example.myweatherapp.FileHanding;
import com.example.myweatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



@SuppressLint("StaticFieldLeak")
public class FragmentHourlyWeather extends Fragment {

    RecyclerView recyclerViewHourly;
    public static HourlyAdapter hourlyAdapter;
    private static TextView txtPOP, txtVisibility, txtHumdity, txtPressure, txtUV;
    public static TextView txtWind;
    public static ArrayList<HourlyWeather> hourlys = new ArrayList<>();
    FileHanding fileHanding;
    public FragmentHourlyWeather() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hourly, container, false);

        txtPOP = view.findViewById(R.id.txtPOP);
        txtHumdity = view.findViewById(R.id.txtHumdity);
        txtPressure = view.findViewById(R.id.txtPressure);
        txtVisibility = view.findViewById(R.id.txtVisibility);
        txtUV = view.findViewById(R.id.txtUV);
        txtWind = view.findViewById(R.id.txtWind);

        recyclerViewHourly = view.findViewById(R.id.recyclerViewHourly);
        recyclerViewHourly.setHasFixedSize(true);

        fileHanding = new FileHanding();
        if (fileHanding.readFile(getContext()).equals("")){
            Log.d("file1", "Không thể đọc đc file");
        }else {
            String json = fileHanding.readFile(getContext());
            getHourlyWeather(json);
            Log.d("file1", json);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @SuppressLint("SetTextI18n")
    private void getHourlyWeather(String json) {
                    try {
                        hourlys.clear();
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArrayHourly = jsonObject.getJSONArray("hourly");
                        for(int i = 0; i< jsonArrayHourly.length(); i++){
                            JSONObject jsonObjectHourly = jsonArrayHourly.getJSONObject(i);
                            String hourlydt = jsonObjectHourly.getString("dt");
                            String hourlyTemp = jsonObjectHourly.getString("temp");
                            Double temp = Double.valueOf(hourlyTemp);

                            String hourlyPressure = jsonObjectHourly.getString("pressure");
                            int pressure = Integer.parseInt(hourlyPressure);

                            String hourlyHumidity = jsonObjectHourly.getString("humidity");
                            int humidity = Integer.parseInt(hourlyHumidity);

                            String hourlyUvi = jsonObjectHourly.getString("uvi");
                            Double uvi = Double.valueOf(hourlyUvi);

                            String hourlyVisibility = jsonObjectHourly.getString("visibility");
                            int visibility = Integer.parseInt(hourlyVisibility);

                            JSONArray jsonArrayWeather = jsonObjectHourly.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String hourlyStatus  = jsonObjectWeather.getString("main");
                            String hourlyIcon= jsonObjectWeather.getString("icon");
                            String pop = jsonObjectHourly.getString("pop");
                            Double nPOP = Double.parseDouble(pop)*100;

                            String windSpeed = jsonObjectHourly.getString("wind_speed");
                            Double wind = Double.valueOf(windSpeed);
                                hourlys.add(new HourlyWeather(hourlydt, temp, pressure, humidity
                                        , uvi, visibility, hourlyStatus, hourlyIcon, nPOP, wind));
                        }
                        hourlyAdapter = new HourlyAdapter(getContext(), hourlys);
                        recyclerViewHourly.setAdapter(hourlyAdapter);
                        recyclerViewHourly.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                        txtPOP.setText(Math.round(hourlys.get(0).getPop())+"%");
                        txtHumdity.setText(hourlys.get(0).getHumidity()+"%");
                        txtVisibility.setText(hourlys.get(0).getVisibility()+" m");
                        txtPressure.setText(hourlys.get(0).getPressure()+" hPa");
                        txtUV.setText(hourlys.get(0).getUvi()+"");
                        if (Common.selectUnitWind.equals("m/s")){
                            txtWind.setText(hourlys.get(0).getWindSpeed()+" m/s");
                        }else if (Common.selectUnitWind.equals("MPH")) {
                            double windF = hourlys.get(0).getWindSpeed() * 2.24;
                            txtWind.setText(((double) Math.round(windF * 100) / 100) + "MPH");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

}