package com.dai.myapplication.service;

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

    public boolean registerUser(UserInfo userInfo){
        String sql = "update user_info set user_code = ?,pwd = ?,phone_number = ?,project_id = ?,entry_time = ?,is_user = '1' where id = ?";
        base = new BaseDao();
        return base.execute(sql,
                userInfo.getUserCode(),
                userInfo.getPwd(),
                userInfo.getPhoneNumber(),
                userInfo.getProjectId(),
                userInfo.getEntryTime(),
                userInfo.getId());
    }

    public List<UserInfo> selectUsers(boolean isUser) {

        String sql = "select * from user_info where is_user " + (isUser ? ">" : "=") + " 0";
        base = new BaseDao();
        return base.query(UserInfo.class, sql);
    }

    public boolean delete(int id){
        String sql = "delete from user_info where id = ?";
        base = new BaseDao();
        return base.execute(sql, id);
    }

    public boolean update(UserInfo userInfo){
        String sql = "update user_info set user_name = ?,user_code = ?,pwd = ?,phone_number = ?,bank_code = ?," +
                "bank_address = ?,id_code = ? where id = ?";
        base = new BaseDao();
        return base.execute(sql, userInfo.getUserName(),
                userInfo.getUserCode(),
                userInfo.getPwd(),
                userInfo.getPhoneNumber(),
                userInfo.getBankCode(),
                userInfo.getBankAddress(),
                userInfo.getIdCode(),
                userInfo.getId());
    }
}
