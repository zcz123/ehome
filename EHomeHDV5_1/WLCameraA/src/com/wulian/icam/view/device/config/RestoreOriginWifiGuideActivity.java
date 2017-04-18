/**
 * Project Name:  iCam
 * File Name:     V2ConnectDeviceWifiActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年8月31日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import com.wulian.icam.R;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.view.base.BaseFragmentActivity;
import android.app.Activity;
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

/**
 * @ClassName: RestoreOriginWifiGuideActivity
 * @Function: 手动回到原始wifi指导界面
 * @Date: 2015年8月31日
 * @author Yanmin
 * @email min.yan@wuliangroup.cn
 */
public class RestoreOriginWifiGuideActivity extends BaseFragmentActivity
		implements OnClickListener {
	private ImageView iv_wifi_guide;
	private TextView tv_wifi_tip;
	private TextView tv_wifi_name;
	private TextView tv_wifi_psw;
	private TextView tv_help;
	private Button btn_copy_name;
	private Button btn_copy_psw;

	private ConfigWiFiInfoModel configInfo;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
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

		tv_wifi_psw.setVisibility(View.GONE);
		btn_copy_name.setVisibility(View.GONE);
		btn_copy_psw.setVisibility(View.GONE);
		tv_help.setVisibility(View.GONE);
	}

	private void initData() {
		String ssid = getIntent().getStringExtra("ssid");

		iv_wifi_guide.setImageResource(R.drawable.restore_wifi_guide);
		tv_wifi_name.setText(Html.fromHtml(getResources().getString(
				R.string.config_connect_wifi_name, ssid)));
		tv_wifi_tip.setText(getResources().getString(
				R.string.config_restore_wifi_manual_tip));
	}

	private void setListener() {
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_connect_device_wifi_guide);
	}

	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.config_restore_wifi);
	}

	@Override
	protected OnClickListener getLeftClick() {
		return this;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back || id == R.id.btn_next_step) {
			setResult(RESULT_OK);
			finish();
		}
	}
}
