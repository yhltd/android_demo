package com.dai.myapplication.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dai.myapplication.R;
import com.dai.myapplication.entity.EmployeeInfo;
import com.dai.myapplication.service.EmployeeInfoService;
import com.dai.myapplication.utils.BitmapUtil;
import com.dai.myapplication.utils.GsonUtil;
import com.dai.myapplication.utils.StringUtils;
import com.dai.myapplication.utils.ToastUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserIdCheckActivity extends AppCompatActivity {

    private EmployeeInfoService employeeInfoService;

    private EmployeeInfo employeeInfo;

    private ImageView idImageView;
    private ImageView idReverseImageView;
    private ImageView imageView;

    private Bitmap idImageBitmap;
    private Bitmap idImageReverseBitmap;

    private static final int REQUEST_CODE_CHOOSE_IMAGE = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_id_check);
        getSupportActionBar().setTitle("正在加载...");

        Button saveBtn = findViewById(R.id.id_check_save);
        saveBtn.setOnClickListener(onSaveClick());

        Intent intent = getIntent();
        employeeInfo = GsonUtil.toEntity(intent.getStringExtra("employee_info"), EmployeeInfo.class);

        idImageView = findViewById(R.id.check_id_image);
        idReverseImageView = findViewById(R.id.check_id_image_reverse);

        employeeInfoService = new EmployeeInfoService();

        Handler loadHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                getSupportActionBar().setTitle(R.string.id_check);

                Map<String, Bitmap> objMap = StringUtils.cast(msg.obj);
                try {
                    idImageView.setImageBitmap(objMap.get("id_image"));
                    idReverseImageView.setImageBitmap(objMap.get("id_image_reverse"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                employeeInfo = employeeInfoService.getOne(employeeInfo.getId());

                idImageBitmap = BitmapUtil.base64ToBitmap(employeeInfo.getIdImage());
                idImageReverseBitmap = BitmapUtil.base64ToBitmap(employeeInfo.getIdImageReverse());

                Map<String, Bitmap> map = new HashMap<>();
                map.put("id_image", idImageBitmap);
                map.put("id_image_reverse", idImageReverseBitmap);

                Message msg = new Message();
                msg.obj = map;
                loadHandler.sendMessage(msg);
            }
        }).start();
    }

    public void onOpenAlbumClick(View v) {
        Resources resources = getResources();
        int imageViewId = resources.getIdentifier(v.getTag().toString(), "id", getPackageName());
        imageView = findViewById(imageViewId);
        imageView.setDrawingCacheEnabled(true);

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && intent != null) {
            Uri uri = intent.getData();
            imageView.setImageURI(uri);
            imageView.setTag(uri);
        }
    }

    public View.OnClickListener onSaveClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (idImageView.getTag() == null) {
                        idImageBitmap = ((BitmapDrawable) ((ImageView) idImageView).getDrawable()).getBitmap();
                    } else {
                        idImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                StringUtils.cast(idImageView.getTag()));
                    }

                    if (idReverseImageView.getTag() == null) {
                        idImageReverseBitmap = ((BitmapDrawable) ((ImageView) idReverseImageView).getDrawable()).getBitmap();
                    } else {
                        idImageReverseBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                StringUtils.cast(idReverseImageView.getTag()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (idImageBitmap == null || idImageBitmap.equals("")) {
                    ToastUtil.show(UserIdCheckActivity.this, "请选择身份证正面照片");
                    return;
                }

                if (idImageReverseBitmap == null && idImageReverseBitmap.equals("")) {
                    ToastUtil.show(UserIdCheckActivity.this, "请选择身份证反面照片");
                    return;
                }

                employeeInfo.setIdImage(BitmapUtil.bitmapToBase64(idImageBitmap));
                employeeInfo.setIdImageReverse(BitmapUtil.bitmapToBase64(idImageReverseBitmap));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (employeeInfoService.update(employeeInfo)) {
                            ToastUtil.show(UserIdCheckActivity.this, "保存成功");
                        } else {
                            ToastUtil.show(UserIdCheckActivity.this, "保存失败，请稍后再试");
                        }
                        ToastUtil.loop();
                    }
                }).start();
            }
        };
    }
}
