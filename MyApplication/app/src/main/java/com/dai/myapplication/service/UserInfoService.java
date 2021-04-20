package com.dai.myapplication.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.UserInfo;

import java.util.List;

public class UserInfoService {

    private BaseDao base;

    public UserInfo login(String userCode, String password) {

        String sql = "select * from user_info where user_code = ? and pwd = ? and is_user > 0";
        base = new BaseDao();
        List<UserInfo> uList = base.query(UserInfo.class, sql, userCode, password);
        return uList != null && uList.size() > 0 ? uList.get(0) : null;
    }

    public boolean register(UserInfo userInfo) {
        String sql = "insert into user_info(user_name,user_code,pwd,phone_number,is_user,power) values(?,?,?,?,?,?)";
        base = new BaseDao();
        return base.execute(sql,
                userInfo.getUserName(),
                userInfo.getUserCode(),
                userInfo.getPwd(),
                userInfo.getPhoneNumber(),
                userInfo.getIsUser(),
                userInfo.getPower());
    }

    public List<UserInfo> selectUsers() {

        String sql = "select * from user_info where is_user > 0";
        base = new BaseDao();
        List<UserInfo> uList = base.query(UserInfo.class, sql);
        return uList != null && uList.size() > 0 ? uList : null;
    }
}
