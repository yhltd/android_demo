package com.dai.myapplication.service;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.ProjectInfo;

import java.util.List;

public class ProjectService {

    private BaseDao base;

    public List<ProjectInfo> nameArray(){
        String sql = "select id,p_name from project_info order by p_name";
        base = new BaseDao();
        return base.query(ProjectInfo.class, sql);
    }
}
