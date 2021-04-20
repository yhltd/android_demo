package com.dai.myapplication.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.ToastUtil;

public class RegisterActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Button registerBtn = findViewById(R.id.register_submit);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userInfo = new UserInfo();
                if(!checkForm(userInfo)) return;

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UserInfoService userInfoService = new UserInfoService();
                                if(userInfoService.register(userInfo)){
                                    ToastUtil.show(RegisterActivity.this,
                                            "提交成功，请耐心等待管理员审核。",
                                            5000);

                                    MyApplication myApplication = (MyApplication) getApplicationContext();
                                    myApplication.setUserInfo(userInfo);
                                }else{
                                    ToastUtil.show(RegisterActivity.this,
                                            "用户名已存在");
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

                builder.setMessage("确定提交吗？");
                builder.setTitle("提示");
                builder.show();
            }
        });
    }

    private boolean checkForm(UserInfo userInfo){
        MyApplication myApplication = (MyApplication) getApplicationContext();
        if(myApplication.getUserInfo() != null &&
                myApplication.getUserInfo().getIsUser() == 0 &&
                myApplication.getUserInfo().getUserCode() != ""){
            ToastUtil.show(RegisterActivity.this,"您已经注册过了");
            return false;
        }

        EditText userNameEdit = findViewById(R.id.register_user_name);
        if(userNameEdit.getText().toString().equals("")){
            ToastUtil.show(RegisterActivity.this, "请输入姓名");
            return false;
        }else{
            userInfo.setUserName(userNameEdit.getText().toString());
        }

        EditText userCodeEdit = findViewById(R.id.register_user_code);
        if(userCodeEdit.getText().toString().equals("")){
            ToastUtil.show(RegisterActivity.this, "请输入用户名");
            return false;
        }else{
            userInfo.setUserCode(userCodeEdit.getText().toString());
        }

        EditText passwordEdit = findViewById(R.id.register_password);
        if(passwordEdit.getText().toString().equals("")){
            ToastUtil.show(RegisterActivity.this, "请输入密码");
            return false;
        }

        EditText passwordAgainEdit = findViewById(R.id.register_password_again);
        if(passwordAgainEdit.getText().toString().equals("")){
            ToastUtil.show(RegisterActivity.this, "请确认密码");
            return false;
        }

        if(!passwordEdit.getText().toString().equals(passwordAgainEdit.getText().toString())){
            ToastUtil.show(RegisterActivity.this, "两次输入的密码不一致");
            return false;
        }else{
            userInfo.setPwd(passwordEdit.getText().toString());
        }

        EditText phoneNumberEdit = findViewById(R.id.register_phone_number);
        if(phoneNumberEdit.getText().toString().equals("")){
            ToastUtil.show(RegisterActivity.this, "请输入手机号");
            return false;
        }else{
            userInfo.setPhoneNumber(phoneNumberEdit.getText().toString());
        }

        return true;
    }
}
