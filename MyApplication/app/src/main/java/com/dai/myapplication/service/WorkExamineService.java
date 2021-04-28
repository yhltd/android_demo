package com.dai.myapplication.service;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.WorkExamine;
import com.dai.myapplication.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkExamineService {

    private BaseDao baseDao;

    public List<WorkExamine> list(int projectId) {
        String sql = "select * from work_examine where project_id = ?";

        baseDao = new BaseDao();
        return baseDao.query(WorkExamine.class, sql, projectId);
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

    public boolean remove(Map<Integer, Integer> idMap) {
        StringBuilder idStr = new StringBuilder();
        for (Integer key : idMap.keySet()) {
            idStr.append(idMap.get(key)).append(",");
        }
        String sql = "delete from work_examine where id in (" + idStr.substring(0, idStr.length() - 1) + ")";

        baseDao = new BaseDao();
        return baseDao.execute(sql);
    }
}
