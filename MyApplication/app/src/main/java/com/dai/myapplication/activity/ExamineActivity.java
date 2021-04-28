package com.dai.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dai.myapplication.R;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExamineActivity extends AppCompatActivity {

    private boolean isRefresh = false;

    private SwipeRefreshLayout swipeRefreshLayout;

    private UserInfoService userInfoService;

    private List<UserInfo> list;

    private ListView listView;

    private EditText searchEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_examine);
        listView = findViewById(R.id.examine_list);

        searchEdit = findViewById(R.id.register_search);
        searchEdit.addTextChangedListener(onSearchEditChange());

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                initView();
            }
        });
    }

    private TextWatcher onSearchEditChange() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                List<HashMap<String, Object>> data = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if(text.equals("") || list.get(i).getUserName().contains(text)){
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("userName", list.get(i).getUserName());
                        item.put("id", list.get(i).getId());
                        data.add(item);
                    }
                }
                SimpleAdapter adapter = new SimpleAdapter(ExamineActivity.this,
                        data,
                        R.layout.register_examine_row,
                        new String[]{"userName"},
                        new int[]{R.id.examine_user_name}) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final View view = super.getView(position, convertView, parent);
                        view.setTag(position);
                        return view;
                    }
                };

                listView.setAdapter(adapter);
            }
        };
    }

    private void initView() {
        Handler listLoadHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                listView.setAdapter(StringUtils.cast(msg.obj));
                if (isRefresh) swipeRefreshLayout.setRefreshing(false);
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                userInfoService = new UserInfoService();
                list = userInfoService.selectUsers(false);
                if (list == null) return;

                List<HashMap<String, Object>> data = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("userName", list.get(i).getUserName());
                    item.put("id", list.get(i).getId());
                    data.add(item);
                }
                SimpleAdapter adapter = new SimpleAdapter(ExamineActivity.this,
                        data,
                        R.layout.register_examine_row,
                        new String[]{"userName"},
                        new int[]{R.id.examine_user_name}) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final View view = super.getView(position, convertView, parent);
                        view.setTag(position);
                        return view;
                    }
                };

                Message msg = new Message();
                msg.obj = adapter;
                listLoadHandler.sendMessage(msg);
            }
        }).start();
    }

    public void onSubmitClick(View v) {
        View view = (View) v.getParent();
        int position = Integer.parseInt(view.getTag().toString());
        Intent intent = new Intent(ExamineActivity.this, ExamineSubmitActivity.class);
        intent.putExtra("user_info", GsonUtil.toJson(list.get(position)));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRefresh = false;
        initView();
    }
}
