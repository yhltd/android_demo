package com.dai.myapplication.service;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.FinishDetail;

import java.util.List;

public class FinishDetailService {

    private BaseDao baseDao;

    public List<FinishDetail> list(int projectId){
        String sql = "select * from finish_detail where project_id = ? order by user_name";

        baseDao = new BaseDao();
        return baseDao.query(FinishDetail.class, sql, projectId);
    }

    public boolean save(FinishDetail finishDetail){
        String sql = "insert into finish_detail(user_name,type_name,day_num,day_price,finish_time,last_update_time,project_id)" +
                "values(?,?,?,?,?,?,?)";

        baseDao = new BaseDao();
        long result = baseDao.executeOfId(sql,
                finishDetail.getUserName(),
                finishDetail.getTypeName(),
                finishDetail.getDayNum(),
                finishDetail.getDayPrice(),
                finishDetail.getFinishTime(),
                finishDetail.getLastFinishTime(),
                finishDetail.getProjectId());

        finishDetail.setId(Integer.parseInt(Long.toString(result)));
        return result > 0;
    }

    public boolean update(FinishDetail finishDetail){
        String sql = "update finish_detail set user_name = ?,type_name = ?,day_num = ?,day_price = ?," +
                "finish_time = ?,last_update_time = ? where id = ?";

        baseDao = new BaseDao();
        return baseDao.execute(sql,
                finishDetail.getUserName(),
                finishDetail.getTypeName(),
                finishDetail.getDayNum(),
                finishDetail.getDayPrice(),
                finishDetail.getFinishTime(),
                finishDetail.getLastFinishTime(),
                finishDetail.getId());
    }

    public boolean remove(int id){
        String sql = "delete from finish_detail where id = ?";

        baseDao = new BaseDao();
        return baseDao.execute(sql, id);
    }
}
