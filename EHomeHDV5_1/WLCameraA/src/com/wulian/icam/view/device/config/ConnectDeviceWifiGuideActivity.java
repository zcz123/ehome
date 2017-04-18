/**
 * Project Name:  iCam
 * File Name:     V2ConnectDeviceWifiActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年8月31日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * @ClassName: ConnectDeviceWifiGuideActivity
 * @Function: 手动切换到设备Wifi指导界面
 * @Date: 2015年8月31日
 * @author Yanmin
 * @email min.yan@wuliangroup.cn
 */
public class ConnectDeviceWifiGuideActivity extends BaseFragmentActivity implements
		OnClickListener {
	private ImageView iv_wifi_guide;
	private TextView tv_wifi_tip;
	private TextView tv_wifi_name;
	private TextView tv_wifi_psw;
	private TextView tv_help;
	private Button btn_copy_name;
	private Button btn_copy_psw;

	private ConfigWiFiInfoModel mConfigInfo;
	private String deviceSsid;
	private String devicePwd;

	private ClipboardManager clip;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		initView();
		initData();
		setListener();
	}

	private void initView() {
		iv_wifi_guide = (ImageView) findViewById(R.id.iv_wifi_guide);
		tv_wifi_tip = (TextView) findViewById(R.id.tv_wifi_tip);
		tv_wifi_name = (TextView) findViewById(R.id.tv_wifi_name);
		tv_wifi_psw = (TextView) findViewById(R.id.tv_wifi_psw);
		tv_help = (TextView) findViewById(R.id.tv_help);
		btn_copy_name = (Button) findViewById(R.id.btn_copy_name);
		btn_copy_psw = (Button) findViewById(R.id.btn_copy_psw);
		btn_copy_name.setVisibility(View.GONE);
	}

	private void initData() {
		Intent it = getIntent();
		mConfigInfo = it.getParcelableExtra("configInfo");
		deviceSsid = it.getStringExtra("deviceSsid");
		devicePwd = it.getStringExtra("devicePwd");

		tv_wifi_name.setText(Html.fromHtml(getResources().getString(
				R.string.config_connect_wifi_name, deviceSsid)));
		tv_wifi_psw.setText(Html.fromHtml(getResources().getString(
				R.string.config_connect_wifi_psw, devicePwd)));
		tv_wifi_tip.setText(Html.fromHtml(getResources().getString(
				R.string.config_connect_wifi_tip)));
		tv_help.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

	private void setListener() {
		tv_help.setOnClickListener(this);
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_connect_device_wifi_guide);
	}

	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.config_connect_wifi);
	}

	@Override
	protected OnClickListener getLeftClick() {
		return this;
	}

	private void changeWifiManuallyView() {
		setActivityTitle(getResources().getString(R.string.config_connect_wifi_manual));
		iv_wifi_guide.setImageResource(R.drawable.connect_wifi_manual_guide);
		tv_wifi_tip.setText(Html.fromHtml(getResources().getString(
				R.string.config_connect_wifi_manual_tip)));
		btn_copy_name.setVisibility(View.VISIBLE);
		tv_help.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			setResult(RESULT_OK);
			finish();
		} else if (id == R.id.btn_next_step) {
			setResult(RESULT_OK);
			finish();
		} else if (id == R.id.tv_help) {
			changeWifiManuallyView();
		} else if (id == R.id.btn_copy_name) {
			ClipData ssid = ClipData.newPlainText("text", deviceSsid);
			clip.setPrimaryClip(ssid);
			showMsg(R.string.config_copy_wifi_name);
		} else if (id == R.id.btn_copy_psw) {
			ClipData psw = ClipData.newPlainText("text", devicePwd);
			clip.setPrimaryClip(psw);
			showMsg(R.string.config_copy_wifi_psw);
		} else {
		}
	}
}
