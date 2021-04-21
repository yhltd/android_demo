package com.dai.myapplication.entity;

public class ProjectInfo {

    private int id;

    private String code;

    private String pName;

    private String address;

    private float price;

    private int isSetRisk;

    private int isAgreement;

    private int isFinish;

    private int isSettlement;

    private double labourCost;

    private float labourCostRatio;

    private double materialCost;

    private float machineryCost;

    private double machineryCostRatio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPName() {
        return pName;
    }

    public void setPName(String pName) {
        this.pName = pName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getIsSetRisk() {
        return isSetRisk;
    }

    public void setIsSetRisk(int isSetRisk) {
        this.isSetRisk = isSetRisk;
    }

    public int getIsAgreement() {
        return isAgreement;
    }

    public void setIsAgreement(int isAgreement) {
        this.isAgreement = isAgreement;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

    public int getIsSettlement() {
        return isSettlement;
    }

    public void setIsSettlement(int isSettlement) {
        this.isSettlement = isSettlement;
    }

    public double getLabourCost() {
        return labourCost;
    }

    public void setLabourCost(double labourCost) {
        this.labourCost = labourCost;
    }

    public float getLabourCostRatio() {
        return labourCostRatio;
    }

    public void setLabourCostRatio(float labourCostRatio) {
        this.labourCostRatio = labourCostRatio;
    }

    public double getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(double materialCost) {
        materialCost = materialCost;
    }

    public float getMachineryCost() {
        return machineryCost;
    }

    public void setMachineryCost(float machineryCost) {
        this.machineryCost = machineryCost;
    }

    public double getMachineryCostRatio() {
        return machineryCostRatio;
    }

    public void setMachineryCostRatio(double machineryCostRatio) {
        this.machineryCostRatio = machineryCostRatio;
    }
}
