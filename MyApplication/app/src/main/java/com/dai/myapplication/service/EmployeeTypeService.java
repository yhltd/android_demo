package com.dai.myapplication.service;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.EmployeeType;

import java.util.List;

public class EmployeeTypeService {

    private BaseDao baseDao;

    public List<EmployeeType> nameArray(){
        String sql = "select id,name from employee_type order by name";
        baseDao = new BaseDao();
        return baseDao.query(EmployeeType.class, sql);
    }
}
