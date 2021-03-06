package com.dai.myapplication.service;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.UserInfo;

import java.util.List;

public class UserInfoService {

    private BaseDao base;

    public UserInfo login(String userCode, String password) {

        String sql = "select * from user_info where user_code = ? and pwd = ? and is_user > 0 order by user_name";
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

    public List<UserInfo> selectUsers(boolean isUser, int projectId) {

        String sql = "select * from user_info where is_user " + (isUser ? ">" : "=") + " 0 and project_id = ? order by user_name";
        base = new BaseDao();
        return base.query(UserInfo.class, sql, projectId);
    }

    public List<UserInfo> selectUsers(boolean isPower, boolean isUser, int projectId) {

        String sql = "select * from user_info where power " + (isPower ? ">" : "=") + " 0 and is_user " + (isPower || isUser ? ">" : "=") + " 0 and project_id = ? order by user_name";
        base = new BaseDao();
        return base.query(UserInfo.class, sql, projectId);
    }

    public List<UserInfo> selectUsers(boolean isUser) {

        String sql = "select * from user_info where is_user " + (isUser ? ">" : "=") + " 0 order by user_name";
        base = new BaseDao();
        return base.query(UserInfo.class, sql);
    }

    public boolean delete(int id){
        String sql = "delete from user_info where id = ?";
        base = new BaseDao();
        return base.execute(sql, id);
    }

    public boolean update(UserInfo userInfo){
        String sql = "update user_info set user_name = ?,grade = ?,number = ?,sex = ?,contract_number = ?,user_code = ?,pwd = ?,phone_number = ?,address = ?,bank_code = ?," +
                "bank_address = ?,id_code = ? where id = ?";
        base = new BaseDao();
        return base.execute(sql, userInfo.getUserName(),
                userInfo.getGrade(),
                userInfo.getNumber(),
                userInfo.getSex(),
                userInfo.getContractNumber(),
                userInfo.getUserCode(),
                userInfo.getPwd(),
                userInfo.getPhoneNumber(),
                userInfo.getAddress(),
                userInfo.getBankCode(),
                userInfo.getBankAddress(),
                userInfo.getIdCode(),
                userInfo.getId());
    }
}
