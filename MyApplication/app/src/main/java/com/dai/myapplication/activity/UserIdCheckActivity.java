package com.dai.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.R;
import com.dai.myapplication.entity.EmployeeInfo;
import com.dai.myapplication.utils.GsonUtil;

public class UserIdCheckActivity extends AppCompatActivity {

    private EmployeeInfo employeeInfo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_id_check);

        Intent intent = getIntent();
        employeeInfo = GsonUtil.toEntity(intent.getStringExtra("employee_info"), EmployeeInfo.class);
    }
}
