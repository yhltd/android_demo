package com.dai.myapplication.dao;

import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;

import androidx.annotation.RequiresApi;

import com.dai.myapplication.utils.StringUtils;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BaseDao<T> {

    private static Connection conn;

    private static PreparedStatement preparedStatement;

    private static ResultSet resultSet;

    //服务器地址
    private static final String SERVER_NAME = "yhocn.cn";
    //数据库名称
    private static final String DATABASE_NAME = "weiyi";
    //用户名
    private static final String USER_NAME = "sa";
    //密码
    private static final String PASSWORD = "Lyh07910_001";

    public BaseDao() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            StringBuffer stringBuffer = new StringBuffer("jdbc:jtds:sqlserver://");
            stringBuffer.append(SERVER_NAME);
            stringBuffer.append(":1433/");
            stringBuffer.append(DATABASE_NAME);
            stringBuffer.append(";charset=utf8");
            conn = DriverManager.getConnection(stringBuffer.toString(), USER_NAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<T> query(Class<T> tClass, String sql, Object... params) {
        List<T> list = null;
        try {
            list = new ArrayList<>();
            preparedStatement = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i + 1, params[i].toString());
            }
            resultSet = preparedStatement.executeQuery();

            Field[] fields = tClass.getDeclaredFields();
            while (resultSet.next()) {
                T entity = tClass.newInstance();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object obj = resultSet.getObject(StringUtils.humpToLine(field.getName()));
                    field.set(entity, StringUtils.cast(obj));
                }
                list.add(entity);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }
    }

    public boolean execute(String sql, Object... params) {
        int result = 0;
        try {
            preparedStatement = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i + 1, params[i].toString());
            }
            result = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return result > 0;
    }

    public void close() {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
