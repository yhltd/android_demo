package com.dai.myapplication.service;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.ProjectInfo;
import com.dai.myapplication.entity.UserInfo;

import java.util.List;

public class ProjectService {

    private BaseDao base;

    public List<ProjectInfo> nameArray(){
        String sql = "select id,p_name from project_info order by p_name";
        base = new BaseDao();
        return base.query(ProjectInfo.class, sql);
    }

    public ProjectInfo getOne(int id){
        String sql = "select * from project_info where id = ?";

        base = new BaseDao();
        List<ProjectInfo> list = base.query(ProjectInfo.class, sql, id);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public ProjectInfo surplus(int id){
        String sql = "select p.id," +
                "(labour_cost-isnull((select sum(day_num*day_price) from finish_detail as f where f.project_id = p.id and f.finish_type = '人工费'),0)) as labour_cost," +
                "(material_cost-isnull((select sum(day_num*day_price) from finish_detail as f where f.project_id = p.id and f.finish_type = '材料费'),0)) as material_cost," +
                "(machinery_cost-isnull((select sum(day_num*day_price) from finish_detail as f where f.project_id = p.id and f.finish_type = '机械费'),0)) as machinery_cost " +
                "from project_info as p where p.id = ?";

        base = new BaseDao();
        List<ProjectInfo> list = base.query(ProjectInfo.class, sql, id);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public boolean updateProject(int projectId,int id){
        String sql="update user_info set project_id=? where id=?";
        base=new BaseDao();
        return base.execute(sql,projectId,id);
    }

}
