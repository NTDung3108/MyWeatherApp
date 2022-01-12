package com.example.myweatherapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShareImage {
    public void takeScreenShot(View view, Activity activity,String filePath) {

        String path = filePath + Calendar.getInstance().getTime().toString() + ".jpg";

        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        File imageFile = new File(path);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            shareScreenShot(imageFile, activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareScreenShot(File imageFile, Activity activity) {
        Uri uri = FileProvider.getUriForFile(
                activity,
                BuildConfig.APPLICATION_ID + "." + activity.getLocalClassName() + ".provider",
                imageFile);
        Log.d("buildData", BuildConfig.APPLICATION_ID + "." + activity.getLocalClassName() + ".provider");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, "Weather in my location");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            Intent chooser = Intent.createChooser(intent, "Share With");
            List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            activity.startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }
}
