package com.tejasdadhe.convokit;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


import com.tejasdadhe.convokit.Interfaces.CustomPermissionListener;

/**
 * Created by Tejas on 02/12/17.
 */


public class CustomPermissionManager implements ActivityCompat.OnRequestPermissionsResultCallback{



    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public static final int REQUEST_CALL_PHONE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS =22;
    public static final int INTERNET_PERMISSION = 16;
    public static final int READ_STORAGE = 26;
    public static final int WRITE_STORAGE = 36;
    public static final int H = 16;
    private static final int RECORD_AUDIO = 98;

    public static final String date_format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    Activity activity;
    CustomPermissionListener listener;

    public CustomPermissionManager(Activity activity) {
        this.activity = activity;
    }



    public void checkPermmission()
    {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_CONTACTS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        } else {
            // Permission has already been granted
        }




    }

    public void adListener(CustomPermissionListener listener)
    {
        this.listener = listener;
    }


    public CustomPermissionManager getPermission(@NonNull String permission)
    {

        return this;
    }

    public void AndThen()
    {

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }








}