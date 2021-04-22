package com.dai.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtil {

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = "";
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            if (bitmap != null) {
                byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);

                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();

                byte[] bitmapBytes = byteArrayOutputStream.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        if (base64Data == null || base64Data.equals("")) return null;

        Bitmap bitmap = null;
        try {
            byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
