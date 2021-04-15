package com.dai.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dai.myapplication.R;
import com.dai.myapplication.dao.BaseDao;
import com.dai.myapplication.entity.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = findViewById(R.id.list);
        list();
    }

    private void list(){
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                BaseDao baseDao = new BaseDao();
                try {
                    List<Test> list = baseDao.query("select * from test", Test.class);
                    ArrayAdapter<Test> adapter = new ArrayAdapter(MainActivity.this, R.layout.item, list);
                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }
}