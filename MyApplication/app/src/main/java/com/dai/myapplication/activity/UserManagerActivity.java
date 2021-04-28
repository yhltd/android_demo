package com.dai.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserManagerActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_CHANG = 100;

    private UserInfo userInfo;

    private UserInfoService userInfoService;

    private List<UserInfo> list;

    private List<UserInfo>newList;

    private ListView listView;

    private EditText searchEdit;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_manager);

        MyApplication myApplication = (MyApplication) getApplication();
        userInfo = myApplication.getUserInfo();

        listView = findViewById(R.id.user_manager_list);

        searchEdit = findViewById(R.id.user_search);
        searchEdit.addTextChangedListener(onSearchUser());

        initList();
    }

    private void initList(){
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
                list = userInfoService.selectUsers(false,true, userInfo.getProjectId());

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
                        new int[]{R.id.manager_user_code, R.id.manager_user_name}){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final LinearLayout view = (LinearLayout)super.getView(position, convertView, parent);
                        LinearLayout linearLayout= (LinearLayout)view.getChildAt(0);
                        linearLayout.setOnClickListener(onClick());
                        linearLayout.setOnLongClickListener(onLongClickListener());
                        linearLayout.setTag(position);
                        Button btn=(Button) view.getChildAt(1);
                        btn.setOnClickListener(onItemClick());
                        btn.setTag(position);
                        return view;
                    }
                };

                Message msg = new Message();
                msg.obj = adapter;
                listLoadHandler.sendMessage(msg);

            }
        }).start();
    }

    public View.OnClickListener onClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = Integer.parseInt(view.getTag().toString());
                Intent intent = new Intent(UserManagerActivity.this, UserInfoActivity.class);
                MyApplication myApplication=(MyApplication) getApplication();
                myApplication.setUserInfo(list.get(position));
                startActivityForResult(intent, REQUEST_CODE_CHANG);
            }
        };
    }

    public View.OnClickListener onItemClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = Integer.parseInt(view.getTag().toString());
                Intent intent = new Intent(UserManagerActivity.this, UserProjectActivity.class);
                intent.putExtra("user_info", GsonUtil.toJson(list.get(position)));
                startActivity(intent);
            }
        };
    }

    public View.OnLongClickListener onLongClickListener(){
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserManagerActivity.this);
                int position = Integer.parseInt(view.getTag().toString());
                Handler deleteHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if((boolean) msg.obj){
                            ToastUtil.show(UserManagerActivity.this, "删除成功");
                            initList();
                        }
                        return true;
                    }
                });

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.obj = userInfoService.delete(list.get(position).getId());
                                deleteHandler.sendMessage(msg);
                            }
                        }).start();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setMessage("确定删除吗？");
                builder.setTitle("提示");
                builder.show();
                return true;
            }
        };
    }

    private TextWatcher onSearchUser(){
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
                        item.put("userCode", list.get(i).getUserCode());
                        item.put("userName", list.get(i).getUserName());
                        data.add(item);
                        UserInfo itemNew=new UserInfo();
                        itemNew.setUserCode(list.get(i).getUserCode());
                        newList.add(itemNew);
                    }else{
                        LinearLayout linearLayout= (LinearLayout)listView.getChildAt(0);
                        //listView.setVisibility(View.INVISIBLE);
                    }
                }

//                SimpleAdapter adapter = new SimpleAdapter(UserManagerActivity.this,
//                        data,
//                        R.layout.user_manager_row,
//                        new String[]{"userCode","userName"},
//                        new int[]{R.id.manager_user_code,R.id.manager_user_name}) {
//                    @Override
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        //final View view = super.getView(position, convertView, parent);
////                        view.setTag(position);
////                        return view;
//
//                        final LinearLayout view = (LinearLayout)super.getView(position, convertView, parent);
//                        LinearLayout linearLayout= (LinearLayout)view.getChildAt(0);
//                        linearLayout.setOnClickListener(onClick());
//                        linearLayout.setOnLongClickListener(onLongClickListener());
//                        linearLayout.setTag(position);
//                        Button btn=(Button) view.getChildAt(1);
//                        btn.setOnClickListener(onItemClick());
//                        btn.setTag(position);
//
//                        return view;
//                    }
//                };
//                listView.setAdapter(adapter);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_CHANG:
                if(resultCode == RESULT_OK){
                    initList();
                }
                break;
        }
    }

}
