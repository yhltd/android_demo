package com.dai.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.EmployeeType;
import com.dai.myapplication.entity.WorkExamine;
import com.dai.myapplication.service.EmployeeTypeService;
import com.dai.myapplication.service.WorkExamineService;
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class WorkExamineChangeActivity extends AppCompatActivity {

    private WorkExamineService workExamineService;

    private WorkExamine workExamine;

    private Spinner tNameSpinner;
    private EditText teamNameEdit;
    private EditText unitEdit;
    private EditText planEdit;
    private EditText actualEdit;
    private EditText priceEdit;
    private EditText finishPriceEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_examine_change);

        workExamineService = new WorkExamineService();

        Intent intent = getIntent();
        int id = intent.getIntExtra("type", 0);
        switch (id) {
            case R.id.work_examine_save:
                workExamine = new WorkExamine();
                break;
            case R.id.work_examine_update:
                workExamine = GsonUtil.toEntity(intent.getStringExtra("class"), WorkExamine.class);
                break;
        }

        initEditText(workExamine);
        try {
            Button btn = findViewById(id);
            btn.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("error", "type值为0");
        }
    }

    private void setTNameArray(String tName) {
        getSupportActionBar().setTitle("正在加载...");

        Handler tNameHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.admin_user_examine);

                List<EmployeeType> typeList = (List<EmployeeType>) msg.obj;

                List<String> data = new ArrayList<>();
                int position = -1;

                for (int i = 0; i < typeList.size(); i++) {
                    data.add(typeList.get(i).getName());
                    if(typeList.get(i).getName().equals(tName)){
                        position = i;
                    }
                }

                ArrayAdapter adapter = new ArrayAdapter(WorkExamineChangeActivity.this,
                        R.layout.simple_spinner_item,
                        data);

                tNameSpinner.setAdapter(adapter);
                if(position >= 0) tNameSpinner.setSelection(position);

                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                EmployeeTypeService employeeTypeService = new EmployeeTypeService();
                List<EmployeeType> typeList = employeeTypeService.nameArray();
                if (typeList != null) {
                    Message msg = new Message();
                    msg.obj = typeList;
                    tNameHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void initEditText(WorkExamine workExamine) {
        MyApplication myApplication = (MyApplication) getApplication();
        workExamine.setProjectId(myApplication.getUserInfo().getProjectId());

        tNameSpinner = findViewById(R.id.work_examine_t_name);
        setTNameArray(workExamine.getTName());

        teamNameEdit = findViewById(R.id.work_examine_team_name);
        if (workExamine.getTeamName() != null && !workExamine.getTeamName().equals("")) {
            teamNameEdit.setText(workExamine.getTeamName());
        }

        unitEdit = findViewById(R.id.work_examine_unit);
        if (workExamine.getUnit() != null && !workExamine.getUnit().equals("")) {
            unitEdit.setText(workExamine.getUnit());
        }

        planEdit = findViewById(R.id.work_examine_plan);
        if (workExamine.getPlanWork() > 0) {
            planEdit.setText(String.valueOf(workExamine.getPlanWork()));
        }

        actualEdit = findViewById(R.id.work_examine_actual);
        if (workExamine.getActualWork() > 0) {
            actualEdit.setText(String.valueOf(workExamine.getActualWork()));
        }

        priceEdit = findViewById(R.id.work_examine_price);
        if (workExamine.getPrice() > 0) {
            priceEdit.setText(String.valueOf(workExamine.getPrice()));
        }

        finishPriceEdit = findViewById(R.id.work_examine_finish_price);
        if (workExamine.getFinishPrice() > 0) {
            finishPriceEdit.setText(String.valueOf(workExamine.getFinishPrice()));
        }
    }

    private boolean check() {
        workExamine.setTName(tNameSpinner.getSelectedItem().toString());

        if (teamNameEdit.getText().toString().equals("")) {
            ToastUtil.show(WorkExamineChangeActivity.this,
                    "请输入" + getString(R.string.work_team_name));
            return false;
        } else {
            workExamine.setTeamName(teamNameEdit.getText().toString());
        }

        workExamine.setUnit(unitEdit.getText().toString());

        workExamine.setPlanWork(planEdit.getText().toString().equals("") ? 0 :
                Float.parseFloat(planEdit.getText().toString()));
        workExamine.setActualWork(actualEdit.getText().toString().equals("") ? 0 :
                Float.parseFloat(actualEdit.getText().toString()));
        workExamine.setPrice(priceEdit.getText().toString().equals("") ? 0 :
                Float.parseFloat(priceEdit.getText().toString()));
        workExamine.setFinishPrice(finishPriceEdit.getText().toString().equals("") ? 0 :
                Float.parseFloat(finishPriceEdit.getText().toString()));

        return true;
    }

    public void onWorkExamineUpdateClick(View v) {
        if (!check()) return;

        Handler saveHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if ((boolean) msg.obj) {
                    ToastUtil.show(WorkExamineChangeActivity.this, "提交成功");
                    back();
                } else {
                    ToastUtil.show(WorkExamineChangeActivity.this, "提交失败");
                }
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.obj = workExamineService.update(workExamine);
                saveHandler.sendMessage(msg);
            }
        }).start();
    }

    public void onWorkExamineSaveClick(View v) {
        if (!check()) return;

        Handler saveHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if ((boolean) msg.obj) {
                    ToastUtil.show(WorkExamineChangeActivity.this, "保存成功");
                    back();
                } else {
                    ToastUtil.show(WorkExamineChangeActivity.this, "保存失败");
                }
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.obj = workExamineService.save(workExamine);
                saveHandler.sendMessage(msg);
            }
        }).start();
    }

    private void back() {
        setResult(RESULT_OK, new Intent());
        finish();
    }
}
