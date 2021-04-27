package com.dai.myapplication.service;

import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.WorkAttendance;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AttendanceService {

    private BaseDao baseDao;

    public List<WorkAttendance> list(int projectId, Date workDate) {
        String sql = "select * from work_attendance " +
                "where project_id = ? and year(work_date) = ? and month(work_date) = ? and day(work_date) = ?";

        baseDao = new BaseDao();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(workDate);
        return baseDao.query(WorkAttendance.class, sql,
                projectId,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE));
    }

    public boolean save(WorkAttendance workAttendance) {
        String sql = "insert into work_attendance(user_name,work_date,attendance,project_id) " +
                "values(?,?,?,?)";

        baseDao = new BaseDao();
        long result = baseDao.executeOfId(sql,
                workAttendance.getUserName(),
                workAttendance.getWorkDate(),
                workAttendance.getAttendance(),
                workAttendance.getProjectId());
        if (result > 0) {
            workAttendance.setId(Integer.parseInt(Long.toString(result)));
            return true;
        } else {
            return false;
        }
    }

    public boolean update(WorkAttendance workAttendance) {
        String sql = "update work_attendance set attendance = ? where id = ?";

        baseDao = new BaseDao();
        return baseDao.execute(sql,
                workAttendance.getAttendance(),
                workAttendance.getId());
    }

    public boolean remove(int id) {
        String sql = "delete from work_attendance where id = ?";

        baseDao = new BaseDao();
        return baseDao.execute(sql, id);
    }
}
