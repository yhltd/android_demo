package com.dai.myapplication.service;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.EmployeeInfo;
import com.dai.myapplication.utils.StringUtils;

import java.util.List;

public class EmployeeInfoService {

    private BaseDao baseDao;

    public EmployeeInfo getOneByUserId(int userId) {
        String sql = "select * from employee_info where user_id = ?";

        baseDao = new BaseDao();
        List<EmployeeInfo> list = baseDao.query(EmployeeInfo.class, sql, userId);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public boolean save(EmployeeInfo employeeInfo) {
        String sql = "insert into employee_info(user_id,id_image,bank_image,id_image_reverse,bank_image_reverse,type_id) " +
                "values(?,?,?,?,?,?)";
        baseDao = new BaseDao();
        long result = baseDao.executeOfId(sql, employeeInfo.getUserId(),
                employeeInfo.getIdImage(),
                employeeInfo.getBankImage(),
                employeeInfo.getIdImageReverse(),
                employeeInfo.getBankImageReverse(),
                employeeInfo.getTypeId());
        if (result > 0) {
            employeeInfo.setId(StringUtils.cast(result));
            return true;
        } else {
            return false;
        }
    }

    public boolean update(EmployeeInfo employeeInfo) {
        String sql = "update employee_info set id_image = ?,bank_image = ?,id_image_reverse = ?," +
                "bank_image_reverse = ?,type_id = ? where id = ?";

        baseDao = new BaseDao();
        return baseDao.execute(sql, employeeInfo.getIdImage(),
                employeeInfo.getBankImage(),
                employeeInfo.getIdImageReverse(),
                employeeInfo.getBankImageReverse(),
                employeeInfo.getTypeId(),
                employeeInfo.getId());
    }
}
