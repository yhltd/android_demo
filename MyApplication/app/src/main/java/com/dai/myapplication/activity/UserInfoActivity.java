package com.dai.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceControl;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.EmployeeInfo;
import com.dai.myapplication.entity.EmployeeType;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.EmployeeInfoService;
import com.dai.myapplication.service.EmployeeTypeService;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private UserInfo userInfo;
    private EmployeeInfo employeeInfo;
    private List<EmployeeType> nameArray;
    private Map<String, Integer> nameMap;

    private EditText userNameEdit;
    private Spinner sexSpinner;
    private EditText gradeEdit;
    private EditText numberEdit;
    private EditText contractNumberEdit;
    private EditText userCodeEdit;
    private EditText passwordEdit;
    private EditText phoneNumberEdit;
    private EditText addressEdit;
    private Spinner typeSpinner;
    private EditText idCodeEdit;
    private EditText bankCodeEdit;
    private EditText bankAddressEdit;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        MyApplication myApplication = (MyApplication) getApplication();
        userInfo = new UserInfo(myApplication.getUserInfo());

        //??????
        userNameEdit = findViewById(R.id.info_user_name);
        userNameEdit.setText(userInfo.getUserName());
        //??????
        sexSpinner = findViewById(R.id.info_sex);
        sexSpinner.setSelection(userInfo.getSex() == null ? 0 : userInfo.getSex().equals("???") ? 1 : 0);
        //?????????
        userCodeEdit = findViewById(R.id.info_user_code);
        userCodeEdit.setText(userInfo.getUserCode());
        //??????
        passwordEdit = findViewById(R.id.info_password);
        passwordEdit.setText(userInfo.getPwd());
        //????????????
        phoneNumberEdit = findViewById(R.id.info_phone_number);
        phoneNumberEdit.setText(userInfo.getPhoneNumber());
        //??????
        addressEdit = findViewById(R.id.info_address);
        addressEdit.setText(userInfo.getAddress());
        //??????
        typeSpinner = findViewById(R.id.info_type);
        setEmployeeInfo(employeeInfoHandler());
        //??????
        gradeEdit = findViewById(R.id.info_grade);
        gradeEdit.setText(userInfo.getGrade());
        //????????????
        numberEdit = findViewById(R.id.info_number);
        numberEdit.setText(userInfo.getNumber());
        //????????????
        idCodeEdit = findViewById(R.id.info_id_code);
        idCodeEdit.setText(userInfo.getIdCode());
        //????????????
        contractNumberEdit = findViewById(R.id.info_contract_number);
        contractNumberEdit.setText(userInfo.getContractNumber());
        //????????????
        bankCodeEdit = findViewById(R.id.info_bank_code);
        bankCodeEdit.setText(userInfo.getBankCode());
        //?????????
        bankAddressEdit = findViewById(R.id.info_bank_address);
        bankAddressEdit.setText(userInfo.getBankAddress());

        //????????????
        Button saveBtn = findViewById(R.id.info_save);
        saveBtn.setOnClickListener(saveBtnClick());

        //??????????????????
        Button idCheckBtn = findViewById(R.id.info_id_check);
        idCheckBtn.setOnClickListener(idCheckClick());
    }

    //?????????set????????????
    private Handler employeeInfoHandler() {
        return new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg != null) {
                    employeeInfo = StringUtils.cast(msg.obj);
                    setTypeSpinner(employeeTypeHandler());
                }
                return true;
            }
        });
    }

    //???????????????????????????
    private void setEmployeeInfo(Handler EmployeeInfoHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmployeeInfoService employeeInfoService = new EmployeeInfoService();
                Message msg = new Message();
                msg.obj = employeeInfoService.getOneByUserId(userInfo.getId());
                EmployeeInfoHandler.sendMessage(msg);
            }
        }).start();
    }

    //???????????????????????????
    private Handler employeeTypeHandler() {
        return new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.obj != null) {
                    nameArray = StringUtils.cast(msg.obj);
                    nameMap = new HashMap<>();

                    List<String> list = new ArrayList<>();

                    int selectedPosition = -1;
                    for (int i = 0; i < nameArray.size(); i++) {
                        if (employeeInfo != null && employeeInfo.getTypeId() == nameArray.get(i).getId()) {
                            selectedPosition = i;
                        }
                        list.add(nameArray.get(i).getName());
                        nameMap.put(nameArray.get(i).getName(), nameArray.get(i).getId());
                    }

                    SpinnerAdapter adapter = new ArrayAdapter<String>(UserInfoActivity.this,
                            R.layout.simple_spinner_item,
                            list);
                    typeSpinner.setAdapter(adapter);
                    if (selectedPosition > 0) {
                        typeSpinner.setSelection(selectedPosition);
                    }
                }
                return true;
            }
        });
    }

    //???????????????????????????
    private void setTypeSpinner(Handler EmployeeTypeHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmployeeTypeService employeeTypeService = new EmployeeTypeService();
                Message msg = new Message();
                msg.obj = employeeTypeService.nameArray();
                EmployeeTypeHandler.sendMessage(msg);
            }
        }).start();
    }

    //???????????????????????????
    private View.OnClickListener saveBtnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkForm()) return;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfoService userInfoService = new UserInfoService();
                        if (userInfoService.update(userInfo)) {
                            EmployeeInfoService employeeInfoService = new EmployeeInfoService();
                            boolean result = employeeInfo.getId() > 0 ?
                                    employeeInfoService.update(employeeInfo) :
                                    employeeInfoService.save(employeeInfo);

                            if (result) {
                                ToastUtil.show(UserInfoActivity.this, "????????????");
                                MyApplication myApplication = (MyApplication) getApplication();
                                myApplication.setUserInfo(userInfo);
                                back();
                            }else{
                                ToastUtil.show(UserInfoActivity.this, "???????????????????????????");
                            }

                        } else {
                            ToastUtil.show(UserInfoActivity.this, "??????????????????");
                        }

                        ToastUtil.loop();
                    }
                }).start();
            }
        };
    }

    private View.OnClickListener idCheckClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(employeeInfo != null && employeeInfo.getId() > 0){
                    Intent intent = new Intent(UserInfoActivity.this, UserIdCheckActivity.class);
                    intent.putExtra("employee_info", GsonUtil.toJson(employeeInfo));
                    startActivity(intent);
                }else{
                    ToastUtil.show(UserInfoActivity.this, "??????????????????????????????");
                }
            }
        };
    }

    private boolean checkForm() {
        if (userNameEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "???????????????");
            return false;
        } else {
            userInfo.setUserName(userNameEdit.getText().toString());
        }

        if (userCodeEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "???????????????");
            return false;
        } else {
            userInfo.setUserCode(userCodeEdit.getText().toString());
        }

        if (gradeEdit.getText().toString().equals("")) {
//            ToastUtil.show(UserInfoActivity.this, "???????????????");
//            return false;
        } else {
            userInfo.setGrade(gradeEdit.getText().toString());
        }

        if (numberEdit.getText().toString().equals("")) {
//            ToastUtil.show(UserInfoActivity.this, "?????????????????????/??????");
//            return false;
        } else {
            userInfo.setNumber(numberEdit.getText().toString());
        }

        if (passwordEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "???????????????");
            return false;
        } else {
            userInfo.setPwd(passwordEdit.getText().toString());
        }

        if (phoneNumberEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "??????????????????");
            return false;
        } else {
            userInfo.setPhoneNumber(phoneNumberEdit.getText().toString());
        }

        if (addressEdit.getText().toString().equals("")) {
//            ToastUtil.show(UserInfoActivity.this, "???????????????");
//            return false;
        } else {
            userInfo.setAddress(addressEdit.getText().toString());
        }

        if (idCodeEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "?????????????????????");
            return false;
        } else {
            userInfo.setIdCode(idCodeEdit.getText().toString());
        }

        if (contractNumberEdit.getText().toString().equals("")) {
//            ToastUtil.show(UserInfoActivity.this, "?????????????????????");
//            return false;
        } else {
            userInfo.setContractNumber(contractNumberEdit.getText().toString());
        }

        if (bankCodeEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "?????????????????????");
            return false;
        } else {
            userInfo.setBankCode(bankCodeEdit.getText().toString());
        }

        if (bankAddressEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "??????????????????");
            return false;
        } else {
            userInfo.setBankAddress(bankAddressEdit.getText().toString());
        }

        String typeName = typeSpinner.getSelectedItem().toString();
        if (employeeInfo == null) {
            employeeInfo = new EmployeeInfo();
            employeeInfo.setUserId(userInfo.getId());
        }
        employeeInfo.setTypeId(nameMap.get(typeName));
        userInfo.setSex(sexSpinner.getSelectedItem().toString());

        return true;
    }

    private void back() {
        setResult(RESULT_OK, new Intent());
        finish();
    }
}
