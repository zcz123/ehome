/**
 * Project Name:  iCam
 * File Name:     V2DeviceInfoConfigFailActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年7月26日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * @ClassName: DeviceConfigFailResultActivity
 * @Function: 配置失败界面
 * @Date: 2015年7月26日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceConfigFailResultActivity extends BaseFragmentActivity
		implements OnClickListener {
	// RelativeLayout rl_retry_two;
	private RelativeLayout rl_retry_one;
	private RelativeLayout rl_barcode_add_tip;
	// Button btn_retry_link;
	// Button btn_barcode_link;
	private Button btn_retry;

	private TextView tv_more_solutions;
	private TextView tv_barcode_add_tip;
	private TextView tv_config_wifi_fail;
	private TextView tv_config_wifi_fail_tip;

	private ConfigWiFiInfoModel mConfigInfo;

	// private boolean isFromResult;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		// rl_retry_two = (RelativeLayout) findViewById(R.id.rl_retry_two);
		rl_retry_one = (RelativeLayout) findViewById(R.id.rl_retry_one);
		rl_barcode_add_tip = (RelativeLayout) findViewById(R.id.rl_barcode_add_tip);

		// btn_retry_link = (Button) findViewById(R.id.btn_retry_link);
		// btn_barcode_link = (Button) findViewById(R.id.btn_barcode_link);
		btn_retry = (Button) findViewById(R.id.btn_retry);

		tv_more_solutions = (TextView) findViewById(R.id.tv_more_solutions);
		tv_barcode_add_tip = (TextView) findViewById(R.id.tv_barcode_add_tip);
		tv_config_wifi_fail = (TextView) findViewById(R.id.tv_config_wifi_fail);
		tv_config_wifi_fail_tip = (TextView) findViewById(R.id.tv_config_wifi_fail_tip);

		tv_more_solutions.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

	private void initData() {
		mConfigInfo = getIntent().getParcelableExtra("configInfo");
		// isFromResult = getIntent().getBooleanExtra("isFromResult", false);

		if (mConfigInfo.getConfigWiFiType() == iCamConstants.CONFIG_SOFT_AP_SETTING)
			tv_barcode_add_tip.setVisibility(View.GONE);
		else
			tv_barcode_add_tip.setVisibility(View.VISIBLE);
		if (mConfigInfo.isAddDevice())
			tv_config_wifi_fail.setText(getResources().getString(
					R.string.config_add_fail));
		else
			tv_config_wifi_fail.setText(getResources().getString(
					R.string.config_wifi_fail));


		tv_config_wifi_fail_tip.setVisibility(View.VISIBLE);
		btn_retry.setText(getResources().getString(
				R.string.config_retry_add));
		tv_more_solutions.setVisibility(View.VISIBLE);
	}

	private void setListener() {
		// btn_retry_link.setOnClickListener(this);
		// btn_barcode_link.setOnClickListener(this);
		btn_retry.setOnClickListener(this);
		rl_barcode_add_tip.setOnClickListener(this);
		tv_more_solutions.setOnClickListener(this);
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_device_config_fail);
	}

	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.common_result);
	}

	@Override
	protected OnClickListener getLeftClick() {
		return this;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			// if (configInfo.isAddDevice()) {
			// Intent it = new Intent(this, MainActivity.class);
			// startActivity(it);
			// } else {
			// Intent it = new Intent(this, DeviceSettingActivity.class);
			// it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(it);
			// }
			finish();
		} else if (id == R.id.btn_retry) {
			Intent it = new Intent(this, V2EmptyActivity.class);
			it.putExtra("configInfo", mConfigInfo);
			startActivity(it);
			this.finish();
		} else if (id == R.id.rl_barcode_add_tip) {
			mConfigInfo
					.setConfigWiFiType(iCamConstants.CONFIG_BARCODE_WIFI_SETTING);
			Intent it = new Intent(this, V2EmptyActivity.class);
			it.putExtra("configInfo", mConfigInfo);
			// it.putExtra("isFirstAdd", false);
			// it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(it);
			this.finish();
		} else if (id == R.id.tv_more_solutions) {
			DialogUtils.showCommonInstructionsWebViewTipDialog(this,
					getResources().getString(R.string.config_more_solutions),
					"more_solutions");
		}
	}
}
