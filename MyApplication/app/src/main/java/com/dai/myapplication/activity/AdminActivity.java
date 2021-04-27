package com.dai.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;

import com.dai.myapplication.R;
import com.dai.myapplication.entity.WorkExamine;

public class AdminActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        LinearLayout manager = findViewById(R.id.user_manager);
        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, UserManagerActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ExamineActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout examine = findViewById(R.id.examine);
        examine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, WorkExamineActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout finish = findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, FinishActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout work = findViewById(R.id.user_work);
        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AttendanceActivity.class);
                startActivity(intent);
            }
        });
    }
}
