package com.dai.myapplication.service;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.WorkExamine;
import com.dai.myapplication.utils.StringUtils;

import java.util.List;

public class WorkExamineService {

    private BaseDao baseDao;

    public List<WorkExamine> list() {
        String sql = "select * from work_examine";

        baseDao = new BaseDao();
        return baseDao.query(WorkExamine.class, sql);
    }

    public boolean save(WorkExamine workExamine) {
        String sql = "insert into work_examine(project_id,t_name,team_name,unit,plan_work,actual_work,price,finish_price)" +
                "values(?,?,?,?,?,?,?,?)";

        baseDao = new BaseDao();
        long result = baseDao.executeOfId(sql,
                workExamine.getProjectId(),
                workExamine.getTName(),
                workExamine.getTeamName(),
                workExamine.getUnit(),
                workExamine.getPlanWork(),
                workExamine.getActualWork(),
                workExamine.getPrice(),
                workExamine.getFinishPrice());
        workExamine.setId(Integer.parseInt(Long.toString(result)));
        return result > 0;
    }

    public boolean update(WorkExamine workExamine) {
        String sql = "update work_examine set t_name = ?,team_name = ?,unit = ?,plan_work = ?,actual_work = ?,price = ?,finish_price = ?" +
                " where id = ?";

        baseDao = new BaseDao();
        return baseDao.execute(sql,
                workExamine.getTName(),
                workExamine.getTeamName(),
                workExamine.getUnit(),
                workExamine.getPlanWork(),
                workExamine.getActualWork(),
                workExamine.getPrice(),
                workExamine.getFinishPrice(),
                workExamine.getId());
    }

    public boolean remove(int id) {
        String sql = "delete from work_examine where id = ?";

        baseDao = new BaseDao();
        return baseDao.execute(sql, id);
    }
}
