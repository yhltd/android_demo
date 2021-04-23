package com.dai.myapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.R;
import com.dai.myapplication.service.FinishDetailService;

import java.util.List;

public class FinishActivity extends AppCompatActivity {

    private FinishDetailService finishDetailService;

    List<FinishActivity> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_detail);

        finishDetailService = new FinishDetailService();

        initList();
    }

    private void initList(){
        getSupportActionBar().setTitle("正在加载...");

        Handler initHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.admin_user_finish);

                if(msg.obj != null){

                }

                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.obj = finishDetailService.list();
                initHandler.sendMessage(msg);
            }
        }).start();
    }
}
