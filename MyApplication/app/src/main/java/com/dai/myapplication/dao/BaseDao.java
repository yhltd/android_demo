package com.dai.myapplication.dao;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dai.myapplication.utils.StringUtils;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BaseDao<T> {

    private static Connection conn;

    private static Statement statement;

    private static ResultSet resultSet;

    //服务器地址
    private static final String SERVER_NAME = "yhocn.cn";
    //数据库名称
    private static final String DATABASE_NAME = "d_test";
    //用户名
    private static final String USER_NAME = "sa";
    //密码
    private static final String PASSWORD = "Lyh07910_001";

    //数据库连接是否成功
    private boolean isConn = false;

    public boolean getIsConn() {
        return this.isConn;
    }

    public BaseDao() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            StringBuffer stringBuffer = new StringBuffer("jdbc:jtds:sqlserver://");
            stringBuffer.append(SERVER_NAME);
            stringBuffer.append(":1433/");
            stringBuffer.append(DATABASE_NAME);
            stringBuffer.append(";charset=utf8");
            conn = DriverManager.getConnection(stringBuffer.toString(), USER_NAME, PASSWORD);
            isConn = true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<T> query(String sql, Class<T> tClass) throws Exception {
        List<T> list = new ArrayList<>();

        statement = conn.createStatement();
        resultSet = statement.executeQuery(sql);

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

        close();
        return list;
    }

    private void close() {
        try {
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
