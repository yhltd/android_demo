package com.dai.myapplication.service;

import com.dai.myapplication.activity.UserActivity;
import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.UserInfo;

import java.util.List;

public class UserInfoService {

    private BaseDao base;

    public UserInfo login(String userCode, String password){

        String sql = "select * from user_info where user_code = ? and pwd = ? and is_user > 0";
        base = new BaseDao();
        List<UserInfo> uList = base.query(UserInfo.class, sql, userCode, password);
        return uList != null && uList.size() > 0 ? uList.get(0) : null;
    }
}
