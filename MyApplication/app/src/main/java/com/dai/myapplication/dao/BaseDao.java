package com.dai.myapplication.dao;

import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.dai.myapplication.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            stringBuffer.append(";characterEncoding=utf8");
            stringBuffer.append(";useLOBs=false");
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

            if (params != null) for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i + 1, params[i].toString());
            }

            resultSet = preparedStatement.executeQuery();

            Field[] fields = tClass.getDeclaredFields();
            while (resultSet.next()) {
                T entity = tClass.newInstance();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String columnName = StringUtils.humpToLine(field.getName());

                    if (isExistsColumn(resultSet, columnName)) {
                        Object obj = resultSet.getObject(columnName);
                        if (obj != null)
                            field.set(entity, typeHandle(StringUtils.cast(obj), field));
                    }
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

    public byte[] queryFile(String sql, Object... params) {
        try {
            preparedStatement = conn.prepareStatement(sql);

            if (params != null) for (int i = 0; i < params.length; i++) {
                preparedStatement.setInt(i + 1, StringUtils.cast(params[i]));
            }

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getBytes("contract_doc");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return null;
    }

    public boolean execute(String sql, Object... params) {
        int result = 0;
        try {
            preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            if (params != null) for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i + 1, params[i] != null ? params[i].toString() : "");
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

    public long executeOfId(String sql, Object... params) {
        long result = 0;
        try {
            preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            if (params != null) for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i + 1, params[i] != null ? params[i].toString() : "");
            }

            result = preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                result = rs.getLong(1);
            }
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return result;
    }

    public boolean updateFile(String sql, int id, InputStream inputStream) {
        int result = 0;

        try {
            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setBinaryStream(1, inputStream, inputStream.available());
            preparedStatement.setInt(2, id);

            result = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return result > 0;
    }

    private boolean isExistsColumn(ResultSet rs, String columnName) {
        try {
            if (rs.findColumn(columnName) > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private Object typeHandle(Object obj, Field field) {
        Log.d("fieldName", field.getType().getName());
        switch (field.getType().getName()) {
            case "java.time.LocalDateTime":
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                obj = LocalDateTime.parse(obj.toString(), df);
                break;
            case "java.lang.String":
                obj = obj.toString();
                break;
            case "float":
                obj = Float.parseFloat(obj.toString());
                break;
        }
        return obj;
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
