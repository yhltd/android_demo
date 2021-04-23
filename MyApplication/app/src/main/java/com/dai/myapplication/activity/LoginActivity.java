package com.dai.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.InputUtil;
import com.dai.myapplication.utils.ToastUtil;

public class LoginActivity extends AppCompatActivity {

    private UserInfoService userInfoService;

    private EditText userCodeText;
    private EditText passwordText;

    private Button signBtn;

    private Button registerBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userCodeText = findViewById(R.id.login_user_code);
        passwordText = findViewById(R.id.login_password);

        signBtn = findViewById(R.id.action_sign_in);
        signBtn.setOnClickListener(onSignClick());

        registerBtn = findViewById(R.id.action_register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public View.OnClickListener onSignClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkForm()) return;

                InputUtil.hideInput(LoginActivity.this);

                getSupportActionBar().setTitle("正在登录...");
                signBtn.setEnabled(false);

                Handler signHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        getSupportActionBar().setTitle(R.string.action_sign_in);
                        signBtn.setEnabled(true);

                        if (msg.obj != null) {
                            UserInfo user = (UserInfo) msg.obj;

                            MyApplication application = (MyApplication) getApplicationContext();
                            application.setUserInfo(user);

                            ToastUtil.show(LoginActivity.this, "登录成功");

                            Intent intent = new Intent(LoginActivity.this,
                                    user.getPower() > 0 ? AdminActivity.class : UserActivity.class);
                            startActivity(intent);
                        } else {
                            ToastUtil.show(LoginActivity.this, "用户名密码错误");
                        }

                        return true;
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();

                        try {
                            userInfoService = new UserInfoService();
                            msg.obj = userInfoService.login(userCodeText.getText().toString(),
                                    passwordText.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        signHandler.sendMessage(msg);
                    }
                }).start();
            }
        };
    }

    public boolean checkForm(){
        if(userCodeText.getText().toString().equals("")){
            ToastUtil.show(LoginActivity.this, "请输入用户名");
            return false;
        }

        if(passwordText.getText().toString().equals("")){
            ToastUtil.show(LoginActivity.this, "请输入密码");
            return false;
        }

        return true;
    }
}