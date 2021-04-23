package com.dai.myapplication.entity;

public class WorkExamine {

    private int id;

    private int projectId;

    private String tName;

    private String teamName;

    private String unit;

    private float planWork;

    private float actualWork;

    private float price;

    private float finishPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTName() {
        return tName;
    }

    public void setTName(String tName) {
        this.tName = tName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getPlanWork() {
        return planWork;
    }

    public void setPlanWork(float planWork) {
        this.planWork = planWork;
    }

    public float getActualWork() {
        return actualWork;
    }

    public void setActualWork(float actualWork) {
        this.actualWork = actualWork;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getFinishPrice() {
        return finishPrice;
    }

    public void setFinishPrice(float finishPrice) {
        this.finishPrice = finishPrice;
    }
}
