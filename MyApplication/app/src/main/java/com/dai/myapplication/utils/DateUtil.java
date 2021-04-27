package com.dai.myapplication.utils;

import android.app.DatePickerDialog;
import android.content.Context;

import com.dai.myapplication.R;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String hInteger(int p) {
        return p < 10 ? "0" + p : Integer.toString(p);
    }
}
