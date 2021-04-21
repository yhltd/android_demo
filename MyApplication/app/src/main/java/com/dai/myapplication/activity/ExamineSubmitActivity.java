package com.dai.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.TestLooperManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.ProjectInfo;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.ProjectService;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamineSubmitActivity extends AppCompatActivity {

    private ProjectService projectService;

    private UserInfoService userInfoService;

    private UserInfo userInfo;

    private List<ProjectInfo> nameArray;
    private Map<String, Integer> nameMap;

    private EditText userCodeEdit;
    private EditText passwordEdit;
    private EditText phoneNumberEdit;
    private Spinner projectSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_examine_submit);
        Intent intent = getIntent();
        userInfo = GsonUtil.toEntity(intent.getStringExtra("user_info"), UserInfo.class);
        getSupportActionBar().setTitle(userInfo.getUserName());

        userCodeEdit = findViewById(R.id.examine_user_code);
        passwordEdit = findViewById(R.id.examine_password);
        phoneNumberEdit = findViewById(R.id.examine_phone_number);
        projectSpinner = findViewById(R.id.examine_project);

        userCodeEdit.setText(userInfo.getUserCode());
        passwordEdit.setText(userInfo.getPwd());
        phoneNumberEdit.setText(userInfo.getPhoneNumber());


        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                nameArray = StringUtils.cast(msg.obj);
                nameMap = new HashMap<>();
                List<String> list = new ArrayList<>();
                for (ProjectInfo projectInfo : nameArray) {
                    list.add(projectInfo.getPName());
                    nameMap.put(projectInfo.getPName(), projectInfo.getId());
                }
                SpinnerAdapter adapter = new ArrayAdapter<String>(ExamineSubmitActivity.this,
                        R.layout.simple_spinner_item,
                        list);
                projectSpinner.setAdapter(adapter);
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                projectService = new ProjectService();
                Message msg = new Message();
                msg.obj = projectService.nameArray();
                handler.sendMessage(msg);
            }
        }).start();

        Button submitBtn = findViewById(R.id.examine_submit);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkForm(userInfo)) return;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userInfoService = new UserInfoService();
                        if (userInfoService.registerUser(userInfo)) {
                            ToastUtil.show(ExamineSubmitActivity.this, "审核成功");
                            finish();
                        }else{
                            ToastUtil.show(ExamineSubmitActivity.this, "用户名已存在");
                        }

                        ToastUtil.loop();
                    }
                }).start();
            }
        });

        Button deleteBtn = findViewById(R.id.examine_delete);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExamineSubmitActivity.this);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                userInfoService = new UserInfoService();
                                if(userInfoService.delete(userInfo.getId())){
                                    ToastUtil.show(ExamineSubmitActivity.this,"删除成功");
                                    finish();
                                }
                                ToastUtil.loop();
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
        });
    }

    private boolean checkForm(UserInfo userInfo) {
        if (userCodeEdit.getText().toString().equals("")) {
            ToastUtil.show(ExamineSubmitActivity.this, "请输入用户名");
            return false;
        } else {
            userInfo.setUserCode(userCodeEdit.getText().toString());
        }

        if (passwordEdit.getText().toString().equals("")) {
            ToastUtil.show(ExamineSubmitActivity.this, "请输入密码");
            return false;
        } else {
            userInfo.setPwd(passwordEdit.getText().toString());
        }

        if (phoneNumberEdit.getText().toString().equals("")) {
            ToastUtil.show(ExamineSubmitActivity.this, "请输入手机号");
            return false;
        } else {
            userInfo.setPhoneNumber(phoneNumberEdit.getText().toString());
        }

        String pName = projectSpinner.getSelectedItem().toString();
        userInfo.setProjectId(nameMap.get(pName));
        userInfo.setEntryTime(LocalDateTime.now());
        return true;
    }
}
