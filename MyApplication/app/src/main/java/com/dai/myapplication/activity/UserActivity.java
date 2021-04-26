package com.dai.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.R;

public class UserActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main);

        LinearLayout userInfoView = findViewById(R.id.user_info);
        userInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout contractView = findViewById(R.id.contract_doc);
        contractView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, ContractActivity.class);
                startActivity(intent);
            }
        });
    }
}
