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

    public String nameOfUserId(int userId){
        String sql = "select name from employee_type where id = " +
                "(select top 1 type_id from employee_info where user_id = ?)";

        baseDao = new BaseDao();
        List<EmployeeType> list = baseDao.query(EmployeeType.class, sql, userId);
        return list != null && list.size() > 0 ? list.get(0).getName() : null;
    }
}
