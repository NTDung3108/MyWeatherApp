package com.example.myweatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Common.Common;
import com.example.Common.SharePreferenceUtil;
import com.google.android.gms.common.util.SharedPreferencesUtils;

public class SettingActivity extends AppCompatActivity {

    LinearLayout layoutTemp, layoutWind;
    TextView txtTempChange, txtWidChange;
    ImageView imgBack;
    SharePreferenceUtil mSPUntil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        layoutTemp = findViewById(R.id.layoutTemp);
        layoutWind = findViewById(R.id.layoutWind);
        txtTempChange = findViewById(R.id.txtTempChange);
        txtWidChange = findViewById(R.id.txtWidChange);
        imgBack = findViewById(R.id.imgBack);

        layoutTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Temp change", Toast.LENGTH_LONG).show();
                showTempChangeDialog();
            }
        });
        layoutWind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Wind change", Toast.LENGTH_LONG).show();
                showWindChangeDialog();
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.selectUnitTemp.equals("°C") && Common.selectUnitWind.equals("m/s")){
                    setResult(MainActivity.RESULT_CODE_C_MS);
                    finish();
                }else if (Common.selectUnitTemp.equals("°F") && Common.selectUnitWind.equals("m/s")){
                    setResult(MainActivity.RESULT_CODE_F_MS);
                    finish();
                }else if (Common.selectUnitTemp.equals("°C") && Common.selectUnitWind.equals("MPH")){
                    setResult(MainActivity.RESULT_CODE_C_MPH);
                    finish();
                }else if(Common.selectUnitTemp.equals("°F") && Common.selectUnitWind.equals("MPH")){
                    setResult(MainActivity.RESULT_CODE_F_MPH);
                    finish();
                }
            }
        });
    }

    private void showWindChangeDialog() {
        final String[] unitWind = {"m/s", "MPH"};
        int checked = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Unit of win speed measurement");
        if (Common.selectUnitWind.equals("m/s")){
            checked = 0;
        }else if (Common.selectUnitWind.equals("MPH")){
            checked = 1;
        }
        builder.setSingleChoiceItems(unitWind, checked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Common.selectUnitWind = unitWind[which];
                Toast.makeText(SettingActivity.this, "you chose" + which, Toast.LENGTH_LONG).show();
                txtWidChange.setText(unitWind[which]);
                mSPUntil = new SharePreferenceUtil(SettingActivity.this, "SPdata");
                mSPUntil.setUnitWind(unitWind[which]);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showTempChangeDialog() {
        final String[] unitTemp = {"°C", "°F"};
        int checked = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Unit of temperature measurement");
        if (Common.selectUnitTemp.equals("°C")){
            checked = 0;
        }else if (Common.selectUnitTemp.equals("°F")){
            checked = 1;
        }
        builder.setSingleChoiceItems(unitTemp, checked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Common.selectUnitTemp = unitTemp[which];
                Toast.makeText(SettingActivity.this, "you chose" + which, Toast.LENGTH_LONG).show();
                txtTempChange.setText(unitTemp[which]);
                mSPUntil = new SharePreferenceUtil(SettingActivity.this, "SPdata");
                mSPUntil.setUnitTemp(unitTemp[which]);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Common.selectUnitTemp.equals("°C")){
            txtTempChange.setText("°C");
        }else if (Common.selectUnitTemp.equals("°F")){
            txtTempChange.setText("°F");
        }
        if (Common.selectUnitWind.equals("m/s")){
            txtWidChange.setText("m/s");
        }else if (Common.selectUnitWind.equals("MPH")){
            txtWidChange.setText("MPH");
        }
    }
}