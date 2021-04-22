package com.dai.myapplication;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.dai.myapplication.entity.UserInfo;

public class MyApplication extends Application {

    private UserInfo userInfo;

    public UserInfo getUserInfo(){
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
}
