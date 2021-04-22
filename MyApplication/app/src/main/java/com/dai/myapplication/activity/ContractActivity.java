package com.dai.myapplication.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.MyApplication;
import com.dai.myapplication.R;
import com.dai.myapplication.entity.EmployeeInfo;
import com.dai.myapplication.entity.UserInfo;
import com.dai.myapplication.service.EmployeeInfoService;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ContractActivity extends AppCompatActivity {

    private UserInfo userInfo;

    private EmployeeInfoService employeeInfoService;

    private EmployeeInfo employeeInfo;

    private final static int REQUEST_CHOOSE_FILE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_doc);

        employeeInfoService = new EmployeeInfoService();

        MyApplication myApplication = (MyApplication) getApplication();
        userInfo = myApplication.getUserInfo();

        getSupportActionBar().setTitle("正在加载...");
        Button downLoadBtn = findViewById(R.id.download);
        Button upload = findViewById(R.id.upload);

        downLoadBtn.setEnabled(false);
        upload.setEnabled(false);

        Handler loadHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.contract_doc);
                employeeInfo = (EmployeeInfo) msg.obj;

                if (employeeInfo == null || employeeInfo.getId() <= 0) {
                    ToastUtil.show(ContractActivity.this, "请先完善个人信息");
                    ContractActivity.this.finish();
                } else {
                    downLoadBtn.setEnabled(true);
                    upload.setEnabled(true);
                }
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.obj = employeeInfoService.getOneByUserId(userInfo.getId());
                loadHandler.sendMessage(msg);
            }
        }).start();
    }

    public void onDownLoadClick(View v) {
        Handler downLoadHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 1 && msg.obj != null) {
                    File file = (File) msg.obj;
                    ToastUtil.show(ContractActivity.this,
                            "下载成功：" + file.getPath(),
                            5000);
                } else {
                    ToastUtil.show(ContractActivity.this, "管理员未上传合同书");
                }
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;

                byte[] result = employeeInfoService.selectFile(userInfo.getId());
                if (result != null && result.length > 0) {
                    File file = downLoadFile(result);
                    msg.what = file != null ? 1 : 0;
                    msg.obj = file;
                } else {
                    msg.what = 0;
                }

                downLoadHandler.sendMessage(msg);
            }
        }).start();
    }

    private File downLoadFile(byte[] bytes) {
        //写入路径
        String path = null;
        try {
            path = Environment.getExternalStorageDirectory().getCanonicalPath() + "/"
                    + getString(R.string.app_name) + "/Contract/";
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = getString(R.string.contract_name) + System.currentTimeMillis() + ".docx";
        //创建File对象，其中包含文件所在的目录以及文件的命名
        File file = new File(path, fileName);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;

        InputStream inputStream = null;

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            inputStream = new ByteArrayInputStream(bytes);

            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    public void onUpLoadClick(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/msword");
        startActivityForResult(intent, REQUEST_CHOOSE_FILE);
    }

    private void uploadFile(Uri uri) {
        getSupportActionBar().setTitle("正在上传...");

        Handler upLoadHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.contract_doc);

                ToastUtil.show(ContractActivity.this,
                        StringUtils.cast(msg.obj) ?
                                "上传成功" :
                                "上传失败，请稍后再试");
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;

                try {
                    ContentResolver resolver = getContentResolver();
                    inputStream = resolver.openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.obj = inputStream != null &&
                        employeeInfoService.updateFile(inputStream, employeeInfo.getId());
                upLoadHandler.sendMessage(msg);
            }
        }).start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CHOOSE_FILE && intent != null) uploadFile(intent.getData());
    }
}
