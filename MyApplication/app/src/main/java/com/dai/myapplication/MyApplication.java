package com.dai.myapplication;

import android.app.Application;

import com.dai.myapplication.entity.UserInfo;

public class MyApplication extends Application {

    private static UserInfo userInfo;

    public UserInfo getUserInfo(){
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
