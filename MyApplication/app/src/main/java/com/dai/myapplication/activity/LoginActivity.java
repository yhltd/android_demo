package com.dai.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.R;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.InputUtil;
import com.dai.myapplication.utils.ToastUtil;

public class LoginActivity extends AppCompatActivity {

    private UserInfoService userInfoService;

    private EditText userCodeText;
    private EditText passwordText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userCodeText = findViewById(R.id.user_code);
        passwordText = findViewById(R.id.password);
        Button signBtn = findViewById(R.id.action_sign_in);

        signBtn.setOnClickListener(signBtnClick());
    }

    //登录点击事件
    View.OnClickListener signBtnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputUtil.hideInput(LoginActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userInfoService = new UserInfoService();
                        UserInfo user = userInfoService.login(userCodeText.getText().toString(),
                                passwordText.getText().toString());
                        if (user != null) {
                            ToastUtil.show(LoginActivity.this,"登录成功");
                            Intent intent = new Intent();
                            if(user.getPower() > 0) {
                                intent.setClass(LoginActivity.this, AdminActivity.class);
                            }else{
                                intent.setClass(LoginActivity.this, UserActivity.class);
                            }
                            startActivity(intent);
                        }else{
                            ToastUtil.show(LoginActivity.this,"用户名密码错误");
                        }
                    }
                }).start();
            }
        };
    }
}