package com.example.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Receive","Receiver rungning "+intent.getAction());
        Intent serviceIntent = new Intent(context, NotificationService.class);
        NotificationService.enqueueWork(context, serviceIntent);
    }
}
