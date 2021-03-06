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
import com.dai.myapplication.entity.ProjectInfo;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.EmployeeTypeService;
import com.dai.myapplication.service.FinishDetailService;
import com.dai.myapplication.service.ProjectService;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.ToastUtil;

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
    private Spinner typeSpinner;

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
            Log.d("error", "type??????0");
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEditText(FinishDetail finishDetail, Button btn) {
        userTypeEdit = findViewById(R.id.finish_type_name);
        dayNumEdit = findViewById(R.id.finish_day_num);
        dayPriceEdit = findViewById(R.id.finish_day_price);
        sumEdit = findViewById(R.id.finish_sum);
        typeSpinner = findViewById(R.id.finish_type);
        if(finishDetail.getFinishType()!=null){
            typeSpinner.setSelection(getFinishTypePosition(finishDetail.getFinishType()));
        }

        //??????readonly
        MyApplication.setEditTextReadOnly(userTypeEdit, sumEdit);
        //??????chang??????????????????
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
                        userNameSpinner.setSelection(msg.what);
                    }
                } else {
                    ToastUtil.show(FinishChangeActivity.this,
                            "???????????????????????????????????????????????????",
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
                        if (finishDetail.getUserName() != null &&
                                finishDetail.getUserName().equals(userInfoList.get(i).getUserName())) {
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

    private int getFinishTypePosition(String finishType) {
        String[] types = getResources().getStringArray(R.array.finish_type);
        for (int i = 0; i < types.length; i++) {
            if(finishType.equals(types[i])){
                return i;
            }
        }
        return 0;
    }

    private AdapterView.OnItemSelectedListener onUserNameSelected() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSupportActionBar().setTitle("????????????...");

                Handler selectedHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        getSupportActionBar().setTitle(R.string.admin_user_finish);

                        if (msg.obj != null) {
                            userTypeEdit.setText(msg.obj.toString());
                        } else {
                            userTypeEdit.setText("");
                            ToastUtil.show(FinishChangeActivity.this,
                                    "???????????????????????????????????????????????????",
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
            ToastUtil.show(FinishChangeActivity.this, "???????????????");
            return false;
        } else {
            finishDetail.setTypeName(userTypeEdit.getText().toString());
        }

        if (dayNumEdit.getText().toString().equals("")) {
            ToastUtil.show(FinishChangeActivity.this, "??????????????????");
            return false;
        } else {
            finishDetail.setDayNum(Integer.parseInt(dayNumEdit.getText().toString()));
        }

        if (dayPriceEdit.getText().toString().equals("")) {
            ToastUtil.show(FinishChangeActivity.this, "??????????????????");
            return false;
        } else {
            finishDetail.setDayPrice(Float.parseFloat(dayPriceEdit.getText().toString()));
        }

        finishDetail.setFinishType(typeSpinner.getSelectedItem().toString());

        return true;
    }

    public void onFinishUpdateClick(View v) {
        if (!checkForm()) return;

        Handler updateHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if ((boolean) msg.obj) {
                    ToastUtil.show(FinishChangeActivity.this, "????????????");
                    back();
                } else {
                    ToastUtil.show(FinishChangeActivity.this,
                            "??????????????????????????????");
                }

                return true;
            }
        });

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (!checkCost()) return;

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
                    ToastUtil.show(FinishChangeActivity.this, "????????????");
                    back();
                } else {
                    ToastUtil.show(FinishChangeActivity.this,
                            "??????????????????????????????");
                }

                return true;
            }
        });

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (!checkCost()) return;

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

    private boolean checkCost() {
        boolean result = false;
        float cost = Float.parseFloat(sumEdit.getText().toString());

        ProjectService projectService = new ProjectService();
        ProjectInfo projectInfo = projectService.surplus(userInfo.getProjectId());
        if (projectInfo != null) {

            String[] finishTypes = getResources().getStringArray(R.array.finish_type);
            if (finishTypes[0].equals(finishDetail.getFinishType())) {
                result = projectInfo.getLabourCost() >= cost;
            } else if (finishTypes[1].equals(finishDetail.getFinishType())) {
                result = projectInfo.getMaterialCost() >= cost;
            } else if (finishTypes[2].equals(finishDetail.getFinishType())) {
                result = projectInfo.getMachineryCost() >= cost;
            }

            if (!result) {
                ToastUtil.show(FinishChangeActivity.this,
                        "????????????" + finishDetail.getFinishType() + "?????????????????????????????????");
                ToastUtil.loop();
            }
        } else {
            ToastUtil.show(FinishChangeActivity.this,
                    "?????????????????????????????????????????????");
            ToastUtil.loop();
        }
        return result;
    }

    private void back() {
        setResult(RESULT_OK, new Intent());
        finish();
    }
}