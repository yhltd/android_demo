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

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.entity.WorkExamine;
import com.dai.myapplication.service.WorkExamineService;
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkExamineActivity extends AppCompatActivity {
    
    private UserInfo userInfo;

    private final static int REQUEST_CODE_CHANG = 100;

    private WorkExamineService workExamineService;

    private List<WorkExamine> list;

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_examine);

        MyApplication myApplication = (MyApplication) getApplication();
        userInfo = myApplication.getUserInfo();

        workExamineService = new WorkExamineService();

        listView = findViewById(R.id.work_examine_list);
        initList();
    }

    private void initList() {
        getSupportActionBar().setTitle("正在加载...");

        Handler initHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.admin_user_examine);

                try {
                    listView.setAdapter(StringUtils.cast(msg.obj));
                    listView.setOnItemClickListener(onItemClick());
                    listView.setOnItemLongClickListener(onItemLongClick());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                list = workExamineService.list(userInfo.getProjectId());

                SimpleAdapter adapter = null;

                if (list != null) {
                    List<HashMap<String, Object>> data = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("type", list.get(i).getTName());
                        item.put("team_name", list.get(i).getTeamName());
                        data.add(item);
                    }
                    adapter = new SimpleAdapter(WorkExamineActivity.this,
                            data,
                            R.layout.work_examine_row,
                            new String[]{"type", "team_name"},
                            new int[]{R.id.work_type, R.id.work_team_name});
                }

                Message msg = new Message();
                msg.obj = adapter;
                initHandler.sendMessage(msg);
            }
        }).start();
    }

    private AdapterView.OnItemClickListener onItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WorkExamineActivity.this, WorkExamineChangeActivity.class);
                intent.putExtra("type", R.id.work_examine_update);
                intent.putExtra("class", GsonUtil.toJson(list.get(position)));
                startActivityForResult(intent, REQUEST_CODE_CHANG);
            }
        };
    }

    private AdapterView.OnItemLongClickListener onItemLongClick(){
        return new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkExamineActivity.this);

                Handler deleteHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if((boolean) msg.obj){
                            ToastUtil.show(WorkExamineActivity.this, "删除成功");
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
                                msg.obj = workExamineService.remove(list.get(position).getId());
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

    public void onInsertClick(View v) {
        Intent intent = new Intent(WorkExamineActivity.this, WorkExamineChangeActivity.class);
        intent.putExtra("type", R.id.work_examine_save);
        startActivityForResult(intent, REQUEST_CODE_CHANG);
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
