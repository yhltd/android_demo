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

        //姓名
        userNameEdit = findViewById(R.id.info_user_name);
        userNameEdit.setText(userInfo.getUserName());
        //性别
        sexSpinner = findViewById(R.id.info_sex);
        sexSpinner.setSelection(userInfo.getSex() == null ? 0 : userInfo.getSex().equals("女") ? 1 : 0);
        //用户名
        userCodeEdit = findViewById(R.id.info_user_code);
        userCodeEdit.setText(userInfo.getUserCode());
        //密码
        passwordEdit = findViewById(R.id.info_password);
        passwordEdit.setText(userInfo.getPwd());
        //手机号码
        phoneNumberEdit = findViewById(R.id.info_phone_number);
        phoneNumberEdit.setText(userInfo.getPhoneNumber());
        //地址
        addressEdit = findViewById(R.id.info_address);
        addressEdit.setText(userInfo.getAddress());
        //工种
        typeSpinner = findViewById(R.id.info_type);
        setEmployeeInfo(employeeInfoHandler());
        //等级
        gradeEdit = findViewById(R.id.info_grade);
        gradeEdit.setText(userInfo.getGrade());
        //岗位证书
        numberEdit = findViewById(R.id.info_number);
        numberEdit.setText(userInfo.getNumber());
        //身份证号
        idCodeEdit = findViewById(R.id.info_id_code);
        idCodeEdit.setText(userInfo.getIdCode());
        //合同编号
        contractNumberEdit = findViewById(R.id.info_contract_number);
        contractNumberEdit.setText(userInfo.getContractNumber());
        //银行卡号
        bankCodeEdit = findViewById(R.id.info_bank_code);
        bankCodeEdit.setText(userInfo.getBankCode());
        //开户行
        bankAddressEdit = findViewById(R.id.info_bank_address);
        bankAddressEdit.setText(userInfo.getBankAddress());

        //保存按钮
        Button saveBtn = findViewById(R.id.info_save);
        saveBtn.setOnClickListener(saveBtnClick());

        //身份验证按钮
        Button idCheckBtn = findViewById(R.id.info_id_check);
        idCheckBtn.setOnClickListener(idCheckClick());
    }

    //主线程set员工信息
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

    //子线程查询员工信息
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

    //主线程处理工种类别
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

    //子线程查询工种类别
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

    //子线程保存个人信息
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
                                ToastUtil.show(UserInfoActivity.this, "保存成功");
                                MyApplication myApplication = (MyApplication) getApplication();
                                myApplication.setUserInfo(userInfo);
                                back();
                            }else{
                                ToastUtil.show(UserInfoActivity.this, "保存失败，稍后再试");
                            }

                        } else {
                            ToastUtil.show(UserInfoActivity.this, "用户名已存在");
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
                    ToastUtil.show(UserInfoActivity.this, "请先保存基本个人信息");
                }
            }
        };
    }

    private boolean checkForm() {
        if (userNameEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "请输入姓名");
            return false;
        } else {
            userInfo.setUserName(userNameEdit.getText().toString());
        }

        if (userCodeEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "请输入账号");
            return false;
        } else {
            userInfo.setUserCode(userCodeEdit.getText().toString());
        }

        if (gradeEdit.getText().toString().equals("")) {
//            ToastUtil.show(UserInfoActivity.this, "请输入等级");
//            return false;
        } else {
            userInfo.setGrade(gradeEdit.getText().toString());
        }

        if (numberEdit.getText().toString().equals("")) {
//            ToastUtil.show(UserInfoActivity.this, "请输入岗位证书/编号");
//            return false;
        } else {
            userInfo.setNumber(numberEdit.getText().toString());
        }

        if (passwordEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "请输入密码");
            return false;
        } else {
            userInfo.setPwd(passwordEdit.getText().toString());
        }

        if (phoneNumberEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "请输入手机号");
            return false;
        } else {
            userInfo.setPhoneNumber(phoneNumberEdit.getText().toString());
        }

        if (addressEdit.getText().toString().equals("")) {
//            ToastUtil.show(UserInfoActivity.this, "请输入地址");
//            return false;
        } else {
            userInfo.setAddress(addressEdit.getText().toString());
        }

        if (idCodeEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "请输入身份证号");
            return false;
        } else {
            userInfo.setIdCode(idCodeEdit.getText().toString());
        }

        if (contractNumberEdit.getText().toString().equals("")) {
//            ToastUtil.show(UserInfoActivity.this, "请输入合同编号");
//            return false;
        } else {
            userInfo.setContractNumber(contractNumberEdit.getText().toString());
        }

        if (bankCodeEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "请输入银行卡号");
            return false;
        } else {
            userInfo.setBankCode(bankCodeEdit.getText().toString());
        }

        if (bankAddressEdit.getText().toString().equals("")) {
            ToastUtil.show(UserInfoActivity.this, "请输入开户行");
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
