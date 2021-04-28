package com.dai.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.R;
import com.dai.myapplication.entity.ProjectInfo;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.ProjectService;
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserProjectActivity extends AppCompatActivity {
    private ProjectService projectService;

    private ListView listView;

    private List<ProjectInfo> list;

    private UserInfo userInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_project);
        listView = findViewById(R.id.user_project_list);
        Intent intent = getIntent();
        userInfo = GsonUtil.toEntity(intent.getStringExtra("user_info"), UserInfo.class);
        initList();
    }

    private void initList() {
        getSupportActionBar().setTitle("正在加载...");

        Handler initHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.project);

                try {
                    listView.setAdapter(StringUtils.cast(msg.obj));
                    listView.setOnItemClickListener(onItemClick());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                projectService=new ProjectService();
                list = projectService.nameArray();

                SimpleAdapter adapter = null;

                if (list != null) {
                    List<HashMap<String, Object>> data = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("p_name", list.get(i).getPName());
                        data.add(item);
                    }
                    adapter = new SimpleAdapter(UserProjectActivity.this,
                            data,
                            R.layout.user_project_row,
                            new String[]{"p_name"},
                            new int[]{R.id.user_project});
                }
                Message msg = new Message();
                msg.obj = adapter;
                initHandler.sendMessage(msg);
            }
        }).start();
    }

    private AdapterView.OnItemClickListener onItemClick(){
        return new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id){
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProjectActivity.this);

                Handler deleteHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if((boolean) msg.obj){
                            ToastUtil.show(UserProjectActivity.this, "修改成功");
                            Intent intent = new Intent(UserProjectActivity.this, UserManagerActivity.class);
                            startActivity(intent);
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
                                msg.obj = projectService.updateProject(list.get(position).getId(),userInfo.getId());
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

                builder.setMessage("确定修改吗？");
                builder.setTitle("提示");
                builder.show();
            }
        };
    }
}
