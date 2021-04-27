package com.dai.myapplication.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.entity.WorkAttendance;
import com.dai.myapplication.service.AttendanceService;
import com.dai.myapplication.service.UserInfoService;
import com.dai.myapplication.utils.DateUtil;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class AttendanceActivity extends AppCompatActivity {

    //当前登录项目负责人信息
    private UserInfo userInfo;
    //listView实例
    private ListView listView;
    //当前登录负责人所在项目下所有员工信息
    private List<UserInfo> userInfoList;
    //当前选择日期
    private TextView dateText;
    //当天考勤信息
    private List<WorkAttendance> attendanceList;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);

        MyApplication myApplication = (MyApplication) getApplication();
        userInfo = myApplication.getUserInfo();

        listView = findViewById(R.id.attendance_list);

        dateText = findViewById(R.id.attendance_date);
        dateText.addTextChangedListener(onDateTextViewChange());
        dateText.setText(simpleDateFormat.format(new Date()));
    }

    public TextWatcher onDateTextViewChange() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    initList(simpleDateFormat.parse(s.toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public void onDownClick(View v) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(dateText.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(AttendanceActivity.this, R.style.ThemeDialog);
        datePickerDialog.updateDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));
        datePickerDialog.setButton(BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {

            @Override
            @SuppressLint("SetTextI18n")
            public void onClick(DialogInterface dialog, int which) {
                DatePickerDialog now = (DatePickerDialog) dialog;
                DatePicker datePicker = now.getDatePicker();
                dateText.setText(datePicker.getYear() +
                        "-" +
                        DateUtil.hInteger(datePicker.getMonth() + 1) +
                        "-" +
                        DateUtil.hInteger(datePicker.getDayOfMonth()));
            }
        });
        //隐藏取消按钮
        datePickerDialog.setButton(BUTTON_NEGATIVE, "", new Message());
        datePickerDialog.show();
    }

    private void initList(Date workDate) {
        getSupportActionBar().setTitle("正在加载...");

        Handler initHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.admin_user_work);

                if (msg.what < 0) {
                    ToastUtil.show(AttendanceActivity.this, "您负责的项目下没有员工", 5000);
                    Button saveBtn = findViewById(R.id.attendance_save);
                    saveBtn.setEnabled(false);
                } else {
                    SimpleAdapter adapter = (SimpleAdapter) msg.obj;
                    if (adapter != null) {
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(onListItemClick());
                        listView.setOnItemLongClickListener(onListItemLongClick());
                    }
                }

                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();

                if (userInfoList == null) {
                    UserInfoService userInfoService = new UserInfoService();
                    userInfoList = userInfoService.selectUsers(false, true, userInfo.getProjectId());
                }

                AttendanceService attendanceService = new AttendanceService();
                attendanceList = attendanceService.list(userInfo.getProjectId(), workDate);

                if (userInfoList == null || userInfoList.size() == 0) {
                    msg.what = -1;
                } else {
                    Map<String, WorkAttendance> attendanceMap = new HashMap<>();
                    if (attendanceList != null && attendanceList.size() > 0) {
                        for (WorkAttendance workAttendance : attendanceList) {
                            String key = workAttendance.getUserName();
                            attendanceMap.put(key, workAttendance);
                        }
                    }

                    List<HashMap<String, Object>> data = new ArrayList<>();
                    for (int i = 0; i < userInfoList.size(); i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        String attendanceMapKey = userInfoList.get(i).getUserName() + " | " + userInfoList.get(i).getUserCode();
                        map.put("userName", attendanceMapKey);
                        if (attendanceMap.size() > 0 && attendanceMap.containsKey(attendanceMapKey)) {
                            WorkAttendance workAttendance = attendanceMap.get(attendanceMapKey);
                            map.put("attendance", getAttendance(workAttendance.getAttendance()));
                            map.put("class", workAttendance);
                        } else {
                            map.put("attendance", "");
                        }

                        data.add(map);
                    }

                    SimpleAdapter adapter = new SimpleAdapter(AttendanceActivity.this,
                            data,
                            R.layout.attendance_row,
                            new String[]{"userName", "attendance"},
                            new int[]{R.id.user_name, R.id.attendance});

                    msg.obj = adapter;
                }
                initHandler.sendMessage(msg);
            }
        }).start();
    }

    private AdapterView.OnItemClickListener onListItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleAdapter adapter = (SimpleAdapter) listView.getAdapter();
                HashMap<String, Object> obj = StringUtils.cast(adapter.getItem(position));

                final String[] items = getResources().getStringArray(R.array.attendance);

                AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
                builder.setTitle("选择出勤状态");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        obj.remove("attendance");
                        obj.put("attendance", items[i]);

                        obj.put("isUpdate", true);
                        listView.setAdapter(adapter);
                    }
                });
                builder.create().show();
            }
        };
    }

    private AdapterView.OnItemLongClickListener onListItemLongClick(){
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleAdapter adapter = (SimpleAdapter) listView.getAdapter();
                HashMap<String, Object> obj = StringUtils.cast(adapter.getItem(position));
                if(!obj.containsKey("class")) return false;

                AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Handler deleteHandler = new Handler(new Handler.Callback() {
                            @Override
                            public boolean handleMessage(@NonNull Message msg) {
                                if((boolean) msg.obj){
                                    ToastUtil.show(AttendanceActivity.this, "删除成功");
                                    obj.remove("isUpdate");
                                    obj.remove("class");
                                    obj.remove("attendance");
                                    obj.put("attendance", "");
                                    listView.setAdapter(adapter);
                                }
                                return true;
                            }
                        });

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AttendanceService attendanceService = new AttendanceService();
                                WorkAttendance workAttendance = StringUtils.cast(obj.get("class"));
                                Message msg = new Message();
                                msg.obj = attendanceService.remove(workAttendance.getId());
                                deleteHandler.sendMessage(msg);
                            }
                        }).start();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setMessage("确定删除吗？");
                builder.setTitle("提示");
                builder.show();

                return true;
            }
        };
    }

    public void onAttendanceSaveClick(View v) {
        getSupportActionBar().setTitle("正在加载...");

        Handler saveHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.admin_user_work);

                SimpleAdapter adapter = (SimpleAdapter) msg.obj;
                try {
                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtil.show(AttendanceActivity.this, "保存成功");
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                AttendanceService attendanceService = new AttendanceService();

                SimpleAdapter adapter = (SimpleAdapter) listView.getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    HashMap<String, Object> map = StringUtils.cast(adapter.getItem(i));
                    if (map.containsKey("isUpdate")) {
                        if (map.containsKey("class")) {
                            WorkAttendance workAttendance = StringUtils.cast(map.get("class"));
                            workAttendance.setAttendance(getPosition(map.get("attendance").toString()));
                            attendanceService.update(workAttendance);
                        } else {
                            WorkAttendance workAttendance = new WorkAttendance();
                            workAttendance.setUserName(map.get("userName").toString());
                            workAttendance.setAttendance(getPosition(map.get("attendance").toString()));
                            try {
                                workAttendance.setWorkDate(simpleDateFormat.parse(dateText.getText().toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            workAttendance.setProjectId(userInfo.getProjectId());
                            attendanceService.save(workAttendance);
                            map.put("class", workAttendance);
                        }

                        map.remove("isUpdate");
                    }
                }

                Message msg = new Message();
                msg.obj = adapter;
                saveHandler.sendMessage(msg);
            }
        }).start();
    }

    private String getAttendance(int position) {
        if (position > 0) {
            String[] attendanceArray = getResources().getStringArray(R.array.attendance);
            return attendanceArray[position - 1];
        }
        return "";
    }

    private int getPosition(String attendance) {
        String[] attendanceArray = getResources().getStringArray(R.array.attendance);
        for (int i = 0; i < attendanceArray.length; i++) {
            if (attendance.equals(attendanceArray[i])) {
                return i + 1;
            }
        }
        return 0;
    }
}
