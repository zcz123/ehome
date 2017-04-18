
/**
 * Project Name:  iCam
 * File Name:     V2DeviceAlreadyBindedResultActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015年7月24日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import java.util.Locale;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * @ClassName: DeviceAlreadyBindedResultActivity
 * @Function: V2设备已经绑定的结果界面
 * @Date: 2015年7月24日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceAlreadyBindedResultActivity extends BaseFragmentActivity
        implements OnClickListener {
    ImageView iv_device_picture;
    TextView tv_device_name;
    TextView tv_back_home;

    private String deviceId;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        iv_device_picture = (ImageView) findViewById(R.id.iv_device_picture);
        tv_device_name = (TextView) findViewById(R.id.tv_device_name);
        tv_back_home = (TextView) findViewById(R.id.tv_back_home);
        tv_back_home.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private void initData() {
        deviceId = getIntent().getStringExtra("deviceId");
        if (TextUtils.isEmpty(deviceId)) {
            this.finish();
            return;
        }
        handleDevice();
    }

    private void setListener() {
        tv_back_home.setOnClickListener(this);
    }

    private void handleDevice() {
        DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
        if (type != DeviceType.NONE) {
            tv_device_name.setText(deviceId.toUpperCase(Locale.getDefault()));
        }
        switch (type) {
            case INDOOR:
            case OUTDOOR:
                iv_device_picture.setImageResource(R.drawable.type_04_device);
                break;
            case SIMPLE:
            case SIMPLE_N:
                iv_device_picture.setImageResource(R.drawable.type_03_device);
                break;
            case INDOOR2:
                iv_device_picture.setImageResource(R.drawable.type_04_device);
                break;
            case DESKTOP_C:
                iv_device_picture.setImageResource(R.drawable.type_06_device);
                break;
            case NewEagle:
                iv_device_picture.setImageResource(R.drawable.type_08_device);
                break;
            default:
                showMsg(R.string.config_not_support_device);
                this.finish();
                break;
        }
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_device_already_binded_result);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.config_scan_result);
    }

    @Override
    protected OnClickListener getLeftClick() {
        return this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            this.finish();

        } else if (id == R.id.tv_back_home) {
            this.finish();
        }
    }
}
