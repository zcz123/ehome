/**
 * Project Name:  iCam
 * File Name:     V2DeviceInfoConfigSuccessActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年7月26日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * @ClassName: DeviceConfigSuccessActivity
 * @Function: TODO
 * @Date: 2015年7月26日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceConfigSuccessActivity extends BaseFragmentActivity implements
		OnClickListener {
	private Button btn_config_success;
	private TextView tv_config_wifi_success;
	private TextView tv_config_wifi_success_tip;

	// private boolean isAddDevice;
	private ConfigWiFiInfoModel mConfigInfo;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		btn_config_success = (Button) findViewById(R.id.btn_config_success);
		tv_config_wifi_success = (TextView) findViewById(R.id.tv_config_wifi_success);
		tv_config_wifi_success_tip = (TextView) findViewById(R.id.tv_config_wifi_success_tip);
	}

	private void initData() {
		mConfigInfo = getIntent().getParcelableExtra("configInfo");

		if (mConfigInfo.isAddDevice()) {
			tv_config_wifi_success.setText(R.string.config_success);
		} else {
			tv_config_wifi_success.setText(R.string.config_camera_wifi_success);
		}
		// handleDevice();
	}

	private void setListener() {
		btn_config_success.setOnClickListener(this);
	}

	private void handleDevice() {
		DeviceType type = DeviceType.getDevivceTypeByDeviceID(mConfigInfo
				.getDeviceId());

		switch (type) {
		case INDOOR:
		case OUTDOOR:
			if (mConfigInfo.getConfigWiFiType() == iCamConstants.CONFIG_SOFT_AP_SETTING) {
				tv_config_wifi_success_tip.setText(Html.fromHtml(getResources()
						.getString(R.string.config_success_01_tips)));
			}
			break;
		case SIMPLE:
		case SIMPLE_N:
			if (mConfigInfo.getConfigWiFiType() == iCamConstants.CONFIG_SOFT_AP_SETTING) {
				tv_config_wifi_success_tip.setText(Html.fromHtml(getResources()
						.getString(R.string.config_success_03_tips)));
			}
			break;
		case INDOOR2:
			break;
		case DESKTOP_C:
			if (mConfigInfo.getConfigWiFiType() == iCamConstants.CONFIG_SOFT_AP_SETTING) {
				tv_config_wifi_success_tip.setText(Html.fromHtml(getResources()
						.getString(R.string.config_success_06_tips)));
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_device_config_success);
	}

	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.common_result);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back || id == R.id.btn_config_success) {
			 if (mConfigInfo.isAddDevice()) {
//				 Intent it = new Intent();
//				 it.setAction("cc.wulian.smarthonev5.activity.MONITORACTIVITY");
//				 it.addCategory(Intent.ACTION_DEFAULT);
//				 it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				 startActivity(it);
			 } 
//			 else {
//			 Intent it = new Intent(this, DeviceSettingActivity.class);
//			 it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			 startActivity(it);
//			 }
			finish();
		}
	}

}
