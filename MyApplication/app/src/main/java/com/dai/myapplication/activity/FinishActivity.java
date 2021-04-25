package com.dai.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.FinishDetail;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.FinishDetailService;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FinishActivity extends AppCompatActivity {

    private UserInfo userInfo;

    private final static int REQUEST_CODE_CHANG = 1;

    private FinishDetailService finishDetailService;

    List<FinishDetail> list;

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_detail);

        MyApplication myApplication = (MyApplication) getApplication();
        userInfo = myApplication.getUserInfo();

        listView = findViewById(R.id.finish_list);

        finishDetailService = new FinishDetailService();

        initList();
    }

    private void initList(){
        getSupportActionBar().setTitle("正在加载...");

        Handler initHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.admin_user_finish);

                try {
                    listView.setAdapter(StringUtils.cast(msg.obj));
                    listView.setOnItemClickListener(onItemClick());
                    listView.setOnItemLongClickListener(onItemLongClick());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                list = finishDetailService.list(userInfo.getProjectId());

                SimpleAdapter adapter = null;

                if(list != null){
                    List<HashMap<String, Object>> data = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("user_name", list.get(i).getUserName());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        item.put("finish_time", formatter.format(list.get(i).getFinishTime()));
                        data.add(item);
                    }
                    adapter = new SimpleAdapter(FinishActivity.this,
                            data,
                            R.layout.finish_detail_row,
                            new String[]{"user_name", "finish_time"},
                            new int[]{R.id.user_name, R.id.finish_time});
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
                Intent intent = new Intent(FinishActivity.this, FinishChangeActivity.class);
                intent.putExtra("type", R.id.finish_update);
                MyApplication myApplication = (MyApplication) getApplication();
                myApplication.setObj(list.get(position));
                startActivityForResult(intent, REQUEST_CODE_CHANG);
            }
        };
    }

    private AdapterView.OnItemLongClickListener onItemLongClick(){
        return new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                AlertDialog.Builder builder = new AlertDialog.Builder(FinishActivity.this);

                Handler deleteHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if((boolean) msg.obj){
                            ToastUtil.show(FinishActivity.this, "删除成功");
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
                                msg.obj = finishDetailService.remove(list.get(position).getId());
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

    public void onInsertClick(View v){
        Intent intent = new Intent(FinishActivity.this, FinishChangeActivity.class);
        intent.putExtra("type", R.id.finish_save);
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
