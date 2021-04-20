package com.dai.myapplication.utils;

import android.app.Activity;
import android.os.Looper;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ToastUtil {

    public static void show(Activity activity, String massage) {
        if (Looper.myLooper() == null) Looper.prepare();
        Toast.makeText(activity.getApplicationContext(), massage, Toast.LENGTH_SHORT).show();
    }

    public static void show(Activity activity, String massage, int duration) {
        if (Looper.myLooper() == null) Looper.prepare();
        Toast toast = Toast.makeText(activity.getApplicationContext(), massage, Toast.LENGTH_LONG);
        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, duration);
    }

    public static void loop() {
        Looper.loop();
    }
}
