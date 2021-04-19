package com.dai.myapplication.utils;

import android.app.Activity;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {

    public static void show(Activity activity, String massage){
        if (Looper.myLooper() == null) Looper.prepare();
        Toast.makeText(activity.getApplicationContext(), massage, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
