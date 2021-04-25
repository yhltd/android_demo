package com.dai.myapplication;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.widget.EditText;

import com.dai.myapplication.entity.UserInfo;

public class MyApplication extends Application {

    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    private Object obj;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    public static void setEditTextReadOnly(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setCursorVisible(false);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
        }
    }
}
