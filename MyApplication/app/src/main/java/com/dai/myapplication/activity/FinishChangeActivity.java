package com.dai.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.FinishDetail;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.EmployeeTypeService;
import com.dai.myapplication.service.FinishDetailService;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.ToastUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FinishChangeActivity extends AppCompatActivity {

    private UserInfoService userInfoService;

    private UserInfo userInfo;

    private List<UserInfo> userInfoList;

    private FinishDetailService finishDetailService;

    private FinishDetail finishDetail;

    private Spinner userNameSpinner;
    private EditText userTypeEdit;
    private EditText dayNumEdit;
    private EditText dayPriceEdit;
    private EditText sumEdit;

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_detail_change);

        MyApplication myApplication = (MyApplication) getApplication();
        userInfo = myApplication.getUserInfo();

        finishDetailService = new FinishDetailService();

        Intent intent = getIntent();
        int id = intent.getIntExtra("type", 0);
        switch (id) {
            case R.id.finish_save:
                finishDetail = new FinishDetail();
                break;
            case R.id.finish_update:
                finishDetail = (FinishDetail) myApplication.getObj();
                break;
        }

        try {
            Button btn = findViewById(id);
            btn.setVisibility(View.VISIBLE);
            initEditText(finishDetail, btn);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("error", "type值为0");
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEditText(FinishDetail finishDetail, Button btn) {
        userTypeEdit = findViewById(R.id.finish_type_name);
        dayNumEdit = findViewById(R.id.finish_day_num);
        dayPriceEdit = findViewById(R.id.finish_day_price);
        sumEdit = findViewById(R.id.finish_sum);

        //设置readonly
        MyApplication.setEditTextReadOnly(userTypeEdit, sumEdit);
        //设置chang事件计算合计
        dayNumEdit.addTextChangedListener(setSumText());
        dayPriceEdit.addTextChangedListener(setSumText());

        userTypeEdit.setText(finishDetail.getTypeName());
        dayNumEdit.setText(Integer.toString(finishDetail.getDayNum()));
        dayPriceEdit.setText(Float.toString(finishDetail.getDayPrice()));

        userInfoService = new UserInfoService();
        userNameSpinner = findViewById(R.id.finish_user_name);
        Handler initHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.obj != null) {
                    userNameSpinner.setAdapter((SpinnerAdapter) msg.obj);
                    userNameSpinner.setOnItemSelectedListener(onUserNameSelected());
                    if (msg.what >= 0) {
                        userNameSpinner.setSelection(msg.what + 1);
                    }
                } else {
                    ToastUtil.show(FinishChangeActivity.this,
                            "您所在项目下没有员工，无法申请结付",
                            5000);
                    btn.setEnabled(false);
                }
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                userInfoList = userInfoService.selectUsers(false, true, userInfo.getProjectId());

                SpinnerAdapter adapter = null;
                int position = -1;

                if (userInfoList != null && userInfoList.size() > 0) {
                    List<String> data = new ArrayList<>();
                    for (int i = 0; i < userInfoList.size(); i++) {
                        data.add(userInfoList.get(i).getUserName());
                        if (userInfo.getUserName() != null && !userInfo.getUserName().equals("") &&
                                userInfo.getUserName().equals(userInfoList.get(i).getUserName())) {
                            position = i;
                        }
                    }

                    adapter = new ArrayAdapter<String>(FinishChangeActivity.this,
                            R.layout.simple_spinner_item,
                            data);
                }

                Message msg = new Message();
                msg.obj = adapter;
                msg.what = position;
                initHandler.sendMessage(msg);
            }
        }).start();
    }

    private AdapterView.OnItemSelectedListener onUserNameSelected() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSupportActionBar().setTitle("正在加载...");

                Handler selectedHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        getSupportActionBar().setTitle(R.string.admin_user_finish);

                        if (msg.obj != null) {
                            userTypeEdit.setText(msg.obj.toString());
                        } else {
                            userTypeEdit.setText("");
                            ToastUtil.show(FinishChangeActivity.this,
                                    "该员工未填写个人信息，无法申请结付",
                                    5000);
                        }
                        return true;
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EmployeeTypeService employeeTypeService = new EmployeeTypeService();
                        String typeName = employeeTypeService.nameOfUserId(userInfoList.get(position).getId());
                        Message msg = new Message();
                        msg.obj = typeName;
                        selectedHandler.sendMessage(msg);
                    }
                }).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private TextWatcher setSumText() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float dayNum = dayNumEdit.getText().toString().equals("") ?
                        0 :
                        Float.parseFloat(dayNumEdit.getText().toString());

                float dayPrice = dayPriceEdit.getText().toString().equals("") ?
                        0 :
                        Float.parseFloat(dayPriceEdit.getText().toString());

                sumEdit.setText(Float.valueOf(dayNum * dayPrice).toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private boolean checkForm() {
        finishDetail.setUserName(userNameSpinner.getSelectedItem().toString());

        if (userTypeEdit.getText().toString().equals("")) {
            ToastUtil.show(FinishChangeActivity.this, "请输入工种");
            return false;
        } else {
            finishDetail.setTypeName(userTypeEdit.getText().toString());
        }

        if (dayNumEdit.getText().toString().equals("")) {
            ToastUtil.show(FinishChangeActivity.this, "请输入出勤日");
            return false;
        } else {
            finishDetail.setDayNum(Integer.parseInt(dayNumEdit.getText().toString()));
        }

        if (dayPriceEdit.getText().toString().equals("")) {
            ToastUtil.show(FinishChangeActivity.this, "请输入日工资");
            return false;
        } else {
            finishDetail.setDayPrice(Float.parseFloat(dayPriceEdit.getText().toString()));
        }

        return true;
    }

    public void onFinishUpdateClick(View v) {
        if (!checkForm()) return;

        Handler updateHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if ((boolean) msg.obj) {
                    ToastUtil.show(FinishChangeActivity.this, "修改成功");
                    back();
                } else {
                    ToastUtil.show(FinishChangeActivity.this,
                            "修改失败，请稍后再试");
                }

                return true;
            }
        });

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Message msg = new Message();
                finishDetail.setLastFinishTime(new Date());
                msg.obj = finishDetailService.update(finishDetail);
                updateHandler.sendMessage(msg);
            }
        }).start();
    }

    public void onFinishSaveClick(View v) {
        if (!checkForm()) return;

        Handler saveHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if ((boolean) msg.obj) {
                    ToastUtil.show(FinishChangeActivity.this, "保存成功");
                    back();
                } else {
                    ToastUtil.show(FinishChangeActivity.this,
                            "保存失败，请稍后再试");
                }

                return true;
            }
        });

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Message msg = new Message();
                Date now = new Date();
                finishDetail.setFinishTime(now);
                finishDetail.setLastFinishTime(now);
                finishDetail.setProjectId(userInfo.getProjectId());
                msg.obj = finishDetailService.save(finishDetail);
                saveHandler.sendMessage(msg);
            }
        }).start();
    }

    private void back() {
        setResult(RESULT_OK, new Intent());
        finish();
    }
}