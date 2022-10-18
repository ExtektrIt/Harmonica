package com.andrey.guryanov.harmonica.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permission {
    public static boolean hasPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageLegacy();
            return Environment.isExternalStorageManager();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }

//        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED;

    }

    public static void requestPermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                //Intent intent = new Intent(Settings.ACTION_REQUEST_MANAGE_MEDIA);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", activity.getPackageName())));
                activity.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                //intent.setAction(Settings.ACTION_REQUEST_MANAGE_MEDIA);
                activity.startActivityForResult(intent, requestCode);
            }
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    requestCode);
        }

//        ActivityCompat.requestPermissions(activity,
//                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
//                requestCode);

    }
}
