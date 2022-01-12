package com.example.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapter.DailyAdapter;

import com.example.model.DailyWeather;

import com.example.myweatherapp.FileHanding;
import com.example.myweatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentDailyWeather extends Fragment {

    RecyclerView recyclerViewDaily;
    public static DailyAdapter dailyAdapter;
    ArrayList<DailyWeather> dailys = new ArrayList<>();
    FileHanding fileHanding;

    public FragmentDailyWeather() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_weather, container, false);
        recyclerViewDaily = view.findViewById(R.id.recyclerViewDaily);
        recyclerViewDaily.setHasFixedSize(true);

        fileHanding = new FileHanding();
        if (fileHanding.readFile(getContext()).equals("")){
            Log.d("file2", "Không thể đọc đc file");
        }else {
            String json = fileHanding.readFile(getContext());
            getDailyWeather(json);
            Log.d("file2", json);
        }

        return view;
    }
    private void getDailyWeather(String json) {
                    try {
                        dailys.clear();
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArrayDaily = jsonObject.getJSONArray("daily");
                        for(int i = 1; i< jsonArrayDaily.length(); i++){
                            JSONObject jsonObjectDaily = jsonArrayDaily.getJSONObject(i);
                            String dt = jsonObjectDaily.getString("dt");
                            JSONObject jsonObjectTemp = jsonObjectDaily.getJSONObject("temp");
                            String max = jsonObjectTemp.getString("max");
                            Double maxTemp = Double.valueOf(max);

                            String min = jsonObjectTemp.getString("min");
                            Double minTemp = Double.valueOf(min);

                            JSONArray jsonArrayWeather = jsonObjectDaily.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String icon = jsonObjectWeather.getString("icon");
                            dailys.add(new DailyWeather(dt, maxTemp, minTemp, icon));
                        }
                        dailyAdapter = new DailyAdapter(getContext(), dailys);
                        recyclerViewDaily.setAdapter(dailyAdapter);
                        recyclerViewDaily.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
    }
}