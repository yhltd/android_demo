package com.dai.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FinishActivity extends AppCompatActivity {

    private UserInfo userInfo;

    private final static int REQUEST_CODE_CHANG = 1;

    private FinishDetailService finishDetailService;

    List<FinishDetail> list;

    private ListView listView;

    private EditText searchEdit;

    private Map<Integer, Integer> idList;

    private boolean isMultiChoice = false;
    private FloatingActionButton insertBtn;
    private FloatingActionButton deleteBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_detail);

        MyApplication myApplication = (MyApplication) getApplication();
        userInfo = myApplication.getUserInfo();

        insertBtn = findViewById(R.id.finish_insert);
        deleteBtn = findViewById(R.id.finish_delete);

        listView = findViewById(R.id.finish_list);

        finishDetailService = new FinishDetailService();

        searchEdit = findViewById(R.id.finish_search);
        searchEdit.addTextChangedListener(onSearchEditChange());

        initList();
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
                int a=list.size();
                int b=0;
                String text = s.toString();
                List<HashMap<String, Object>> data = new ArrayList<>();
                if(text.equals("")){
                    initList();
                }else{
                    for (int i = 0; i < a; i++) {
                        if( list.get(b).getUserName().contains(text)){
                            HashMap<String, Object> item = new HashMap<>();
                            item.put("user_name", list.get(b).getUserName());
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                            item.put("finish_time", formatter.format(list.get(b).getFinishTime()));
                            data.add(item);
                            b=b+1;
                        }else{
                            list.remove(b);
                        }
                    }
                }

                SimpleAdapter adapter = new SimpleAdapter(FinishActivity.this,
                        data,
                        R.layout.finish_detail_row,
                        new String[]{"user_name", "finish_time"},
                        new int[]{R.id.user_name, R.id.finish_time});

                listView.setAdapter(adapter);
            }
        };
    }

    private void initList() {
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

                if (list != null) {
                    List<HashMap<String, Object>> data = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("user_name", list.get(i).getUserName());
                        @SuppressLint("SimpleDateFormat")
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
                myOnItemClick(view, position);
            }
        };
    }

    private void myOnItemClick(View view, int position) {
        if (isMultiChoice) {
            if (view.getBackground() == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    idList.put(position, list.get(position).getId());
                    view.setBackground(getDrawable(R.drawable.background_primary));
                }
            } else {
                view.setBackground(null);
                idList.remove(position);
            }
        } else {
            Intent intent = new Intent(FinishActivity.this, FinishChangeActivity.class);
            intent.putExtra("type", R.id.finish_update);
            MyApplication myApplication = (MyApplication) getApplication();
            myApplication.setObj(list.get(position));
            startActivityForResult(intent, REQUEST_CODE_CHANG);
        }
    }

    private AdapterView.OnItemLongClickListener onItemLongClick() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                isMultiChoice = true;
                insertBtn.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.VISIBLE);
                idList = new HashMap<>();
                myOnItemClick(view, position);
                return true;
            }
        };
    }

    public void onInsertClick(View v) {
        Intent intent = new Intent(FinishActivity.this, FinishChangeActivity.class);
        intent.putExtra("type", R.id.finish_save);
        startActivityForResult(intent, REQUEST_CODE_CHANG);
    }

    public void onDeleteClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FinishActivity.this);

        Handler deleteHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if ((boolean) msg.obj) {
                    insertBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.GONE);
                    isMultiChoice = false;
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
                        msg.obj = finishDetailService.remove(idList);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHANG:
                if (resultCode == RESULT_OK) {
                    initList();
                }
                break;
        }
    }
}
