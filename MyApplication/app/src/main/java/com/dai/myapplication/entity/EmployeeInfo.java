package com.dai.myapplication.entity;

public class EmployeeInfo {

    private int id;

    private int userId;

    private String idImage;

    private String bankImage;

    private String idImageReverse;

    private String bankImageReverse;

    private int typeId;

    private String contractDoc;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIdImage() {
        return idImage;
    }

    public void setIdImage(String idImage) {
        this.idImage = idImage;
    }

    public String getBankImage() {
        return bankImage;
    }

    public void setBankImage(String bankImage) {
        this.bankImage = bankImage;
    }

    public String getIdImageReverse() {
        return idImageReverse;
    }

    public void setIdImageReverse(String idImageReverse) {
        this.idImageReverse = idImageReverse;
    }

    public String getBankImageReverse() {
        return bankImageReverse;
    }

    public void setBankImageReverse(String bankImageReverse) {
        this.bankImageReverse = bankImageReverse;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getContractDoc() {
        return contractDoc;
    }

    public void setContractDoc(String contractDoc) {
        this.contractDoc = contractDoc;
    }
}
