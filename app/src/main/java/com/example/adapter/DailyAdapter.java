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
import com.example.model.DailyWeather;
import com.example.myweatherapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.myweatherapp.MainActivity.icons;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.MyHolder> {

    private Context mContext;
    public static ArrayList<DailyWeather> dailyWeathers;

    public DailyAdapter(Context mContext, ArrayList<DailyWeather> dailyWeathers) {
        this.mContext = mContext;
        this.dailyWeathers = dailyWeathers;
    }

    @NonNull
    @Override
    public DailyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_daily, parent, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        long l = Long.parseLong(dailyWeathers.get(position).getTime());
        Date date = new Date(l * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        holder.txtDay.setText(simpleDateFormat.format(date));
        if (Common.selectUnitTemp.equals("°C")){
            holder.txtMaxTemp.setText(Math.round(dailyWeathers.get(position).getMaxTemp())+"°C");
            holder.txtMinTemp.setText(Math.round(dailyWeathers.get(position).getMinTemp())+"°C");
        }else if (Common.selectUnitTemp.equals("°F")){
            double maxF = dailyWeathers.get(position).getMaxTemp()*1.8+32;
            double minF = dailyWeathers.get(position).getMinTemp()*1.8+32;
            holder.txtMaxTemp.setText(Math.round(maxF)+"°F");
            holder.txtMinTemp.setText(Math.round(minF)+"°F");
        }

        Glide.with(mContext)
                .load(icons.get(dailyWeathers.get(position).getIcon()))
                .into(holder.imgDailyIcon);
    }

    @Override
    public int getItemCount() {
        return dailyWeathers.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder{

        TextView txtDay, txtMinTemp, txtMaxTemp;
        ImageView imgDailyIcon;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txtDay);
            txtMinTemp = itemView.findViewById(R.id.txtMinTemp);
            txtMaxTemp = itemView.findViewById(R.id.txtMaxTemp);
            imgDailyIcon = itemView.findViewById(R.id.imgDailyIcon);
        }
    }

}
