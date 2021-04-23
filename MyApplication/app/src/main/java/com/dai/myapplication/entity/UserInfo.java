package com.dai.myapplication.entity;

import java.time.LocalDateTime;

public class UserInfo {

    public UserInfo(){}

    public UserInfo(UserInfo userInfo){
        this.id = userInfo.getId();
        this.userCode = userInfo.getUserCode();
        this.userName = userInfo.getUserName();
        this.sex = userInfo.getSex();
        this.pwd = userInfo.getPwd();
        this.power = userInfo.getPower();
        this.grade = userInfo.getGrade();
        this.number = userInfo.getNumber();
        this.isUser = userInfo.getIsUser();
        this.phoneNumber = userInfo.getPhoneNumber();
        this.address = userInfo.getAddress();
        this.bankCode = userInfo.getBankCode();
        this.bankAddress = userInfo.getBankAddress();
        this.entryTime = userInfo.getEntryTime();
        this.idCode = userInfo.getIdCode();
        this.projectId = userInfo.getProjectId();
        this.contractNumber = userInfo.getContractNumber();
    }

    private int id;

    private String userCode;

    private String userName;

    private String sex;

    private String pwd;

    private String grade;

    private String number;

    private int power;

    private int isUser;

    private String phoneNumber;

    private String address;

    private String bankCode;

    private String bankAddress;

    private LocalDateTime entryTime;

    private String idCode;

    private int projectId;

    private String contractNumber;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getIsUser() {
        return isUser;
    }

    public void setIsUser(int isUser) {
        this.isUser = isUser;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
