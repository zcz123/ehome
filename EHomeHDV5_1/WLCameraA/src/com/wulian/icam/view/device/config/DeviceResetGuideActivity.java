/**
 * Project Name:  iCam
 * File Name:     V2DeviceResetActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年7月26日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * @ClassName: DeviceResetGuideActivity
 * @Function: 设备复位指导界面
 * @Date: 2015年7月26日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceResetGuideActivity extends BaseFragmentActivity implements
		OnClickListener {
	private TextView tv_tips;
	private ImageView iv_device_type;
	private Button btn_next_step;

	private ConfigWiFiInfoModel mData;
	private String deviceId;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		tv_tips = (TextView) findViewById(R.id.tv_tips);
		iv_device_type = (ImageView) findViewById(R.id.iv_device_type);
		btn_next_step = (Button) findViewById(R.id.btn_next_step);

	}

	private void initData() {
		mData = getIntent().getParcelableExtra("configInfo");
		if (mData == null) {
			this.finish();
			return;
		}
		deviceId = mData.getDeviceId();
		if (TextUtils.isEmpty(deviceId)) {
			this.finish();
			return;
		}
		handleDevice();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		initData();
	}

	private void handleDevice() {
		DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
		switch (type) {
		case INDOOR:
		case OUTDOOR:
			iv_device_type.setImageResource(R.drawable.icon_04_device_sys);
			tv_tips.setText(Html.fromHtml(getResources().getString(
					R.string.config_reset_device_01_tips)));
			break;
		case SIMPLE:
		case SIMPLE_N:
			iv_device_type.setImageResource(R.drawable.icon_03_device_set);
			tv_tips.setText(Html.fromHtml(getResources().getString(
					R.string.config_reset_device_set_tips)));
			break;
		case INDOOR2:
			iv_device_type.setImageResource(R.drawable.icon_04_device_sys);
			tv_tips.setText(Html.fromHtml(getResources().getString(
					R.string.config_reset_device_sys_tips)));
			break;
		case DESKTOP_C:
			iv_device_type.setImageResource(R.drawable.type_06_device);
			tv_tips.setText(Html.fromHtml(getResources().getString(
					R.string.config_wait_led_light_06)));
			break;
		default:
			showMsg(R.string.config_not_support_device);
			this.finish();
			break;
		}
	}

	private void setListener() {
		// btn_next_step.setOnClickListener(this);
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_device_reset_guide);
	}

	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.config_reset_camera);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			this.finish();
		} else if (id == R.id.btn_next_step) {
			Intent it = new Intent(DeviceResetGuideActivity.this,
					DeviceLaunchGuideActivity.class);
			it.putExtra("configInfo", mData);
			startActivity(it);
			this.finish();
		}
	}
}
