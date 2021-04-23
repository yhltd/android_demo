package com.dai.myapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserManagerActivity extends AppCompatActivity {

    private UserInfoService userInfoService;

    private List<UserInfo> list;

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_manager);

        listView = findViewById(R.id.user_manager_list);

        Handler listLoadHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                listView.setAdapter(StringUtils.cast(msg.obj));
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                userInfoService = new UserInfoService();
                list = userInfoService.selectUsers(true);

                if(list == null) return;

                List<HashMap<String, Object>> data = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("userCode", list.get(i).getUserCode());
                    item.put("userName", list.get(i).getUserName());
                    data.add(item);
                }
                SimpleAdapter adapter = new SimpleAdapter(UserManagerActivity.this,
                        data,
                        R.layout.user_manager_row,
                        new String[]{"userCode", "userName"},
                        new int[]{R.id.manager_user_code, R.id.manager_user_name});

                Message msg = new Message();
                msg.obj = adapter;
                listLoadHandler.sendMessage(msg);
            }
        }).start();
    }
}
