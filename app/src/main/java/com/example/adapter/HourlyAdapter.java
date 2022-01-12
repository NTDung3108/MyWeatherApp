package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Common.Common;
import com.example.model.HourlyWeather;
import com.example.myweatherapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.myweatherapp.MainActivity.icons;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.MyViewHolder> {

    private Context mContext;
    public static ArrayList<HourlyWeather> hourlyWeathers;

    public HourlyAdapter(Context mContext, ArrayList<HourlyWeather> hourlyWeathers) {
        this.mContext = mContext;
        this.hourlyWeathers = hourlyWeathers;
    }

    @NonNull
    @Override
    public HourlyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_hourly, parent, false);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        long l = Long.parseLong(hourlyWeathers.get(position).getTime());
        Date date = new Date(l * 1000L);
        if (position == 0){
            holder.txtHour.setText("Now");
        }else {
            SimpleDateFormat simpleDateFormathour = new SimpleDateFormat("HH");
            holder.txtHour.setText(simpleDateFormathour.format(date)+"H");
        }
        if (Common.selectUnitTemp.equals("°C")){
            holder.txtHourTemp.setText(Math.round(hourlyWeathers.get(position).getTemp())+"°C");
        }else if (Common.selectUnitTemp.equals("°F")){
            double tempF = hourlyWeathers.get(position).getTemp()*1.8+32;
            holder.txtHourTemp.setText(Math.round(tempF)+"°F");
        }
        Glide.with(mContext)
                .load(icons.get(hourlyWeathers.get(position).getIcon()))
                .into(holder.imgIcon);
    }

    @Override
    public int getItemCount() {
        return hourlyWeathers.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtHour, txtHourTemp;
        ImageView imgIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHour = itemView.findViewById(R.id.txtHour);
            txtHourTemp = itemView.findViewById(R.id.txtHourTemp);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }

}
