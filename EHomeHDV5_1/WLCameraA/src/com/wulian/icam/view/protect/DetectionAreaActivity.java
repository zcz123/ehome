/**
 * Project Name:  iCam
 * File Name:     MoveDetectionActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2014年11月19日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.protect;

import java.lang.ref.SoftReference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.MonitorArea;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomOverlayView;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipProfile;

/**
 * @ClassName: DetectionAreaActivity
 * @Function: 布防区域
 * @Date: 2015年2月5日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class DetectionAreaActivity extends BaseFragmentActivity implements
		OnClickListener {
	private Device device;
	private SipProfile account;
	String deviceSipAccount;// 设备sip账号
	String deviceControlUrl;// 设备控制sip地址
	String deviceCallUrl;// 设备呼叫sip地址
	CustomOverlayView cov;
	RelativeLayout rl_bg;
	int seq = 1;
	Button btn_sure, btn_reset;
	int type;
	//SharedPreferences sp;
	String area;
	Handler myHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detection_area);
		//onSendSipRemoteAccess();
		initViews();
		initData();
	}

	private void initViews() {
		cov = (CustomOverlayView) findViewById(R.id.cov);
		btn_sure = (Button) findViewById(R.id.btn_sure);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		btn_reset.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
		rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
	}

	private void initData() {
		//sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
		device = (Device) getIntent().getSerializableExtra("device");
		SoftReference<Bitmap> bgBitmap = Utils.getBitmap(device.getDevice_id(), this);
		if (bgBitmap != null) {
			rl_bg.setBackgroundDrawable(new BitmapDrawable(bgBitmap.get()));
		}
		type = getIntent().getIntExtra("type", 1);
		area = getIntent().getStringExtra("area");

		// deviceSipAccount = device.getSip_username();
		deviceSipAccount = device.getDevice_id();// 1044
		deviceCallUrl = deviceSipAccount + "@" + device.getSip_domain();
		deviceControlUrl = deviceCallUrl;
		// 1、初始化sip
		app.initSip();
		// 2、用户注册账号
		account = app.registerAccount();
		if (account == null) {
			CustomToast.show(this, R.string.login_user_account_register_fail);
			this.finish();
		}

		myHandler = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				switch (msg.what) {
				case 1:
					cov.restoreMonitorArea(area.split(";"));
					break;
				case 2:
					cov.restoreMonitorArea(area.split(";"));
					break;
				}
			}
		};

		// 3、还原原先配置的数据
		// showBaseDialog();
		switch (type) {
		case SafeProtectSettingActivity.REQUESTCODE_MOVE_AREA:
			// 远程服务器
			// SipController.getInstance().sendMessage(
			// deviceCallUrl,
			// SipHandler.QueryMovementDetectionInfo(deviceControlUrl,
			// seq++), account);

			// 本地存储
			// cov.restoreMonitorArea(sp.getString(APPConfig.MOVE_AREA,
			// "").split(
			// ";"));

			myHandler.sendEmptyMessageDelayed(1, 500);
			break;
		case SafeProtectSettingActivity.REQUESTCODE_COVER_AREA:
			// 远程服务器
			// SipController.getInstance()
			// .sendMessage(
			// deviceCallUrl,
			// SipHandler.QueryBlockDetectionInfo(
			// deviceControlUrl, seq++), account);

			// 本地存储
			// cov.restoreMonitorArea(sp.getString(APPConfig.COVER_AREA, "")
			// .split(";"));

			MonitorArea first = cov.mas.getFirst();
			cov.mas.clear();
			cov.mas.add(first);
			myHandler.sendEmptyMessageDelayed(2, 500);
			break;
		}

	}

	public void sure() {
		// if (device.getIs_online() == 0) {
		// CustomToast.show(this, R.string.device_offline);
		// return;
		// }
		if (cov.getPointResult().length >= 0) {
			for (String i : cov.getPointResult()) {
				Utils.sysoInfo(i);
			}
			// SipController.getInstance()
			// .sendMessage(
			// deviceSipAccount,
			// SipHandler.NotifySynchroPermission(
			// deviceControlUrl, seq++), account);

			// showBaseDialog();
			// SipController.getInstance().sendMessage(
			// deviceCallUrl,
			// SipHandler.ConfigMovementDetection(deviceControlUrl, seq++,
			// true, 50, cov.getPointResult()), account);

//			Editor editor = sp.edit();
//			switch (type) {
//			case SafeProtectSettingActivity.REQUESTCODE_MOVE_AREA:
//				editor.putString(device.getDevice_id() + APPConfig.MOVE_AREA,
//						cov.getPointResultString());
//				break;
//			case SafeProtectSettingActivity.REQUESTCODE_COVER_AREA:
//				editor.putString(device.getDevice_id()+APPConfig.COVER_AREA,
//						cov.getPointResultString());
//				break;
//			}
//			editor.commit();
//			setResult(RESULT_OK);
			Intent it = new Intent();
			it.putExtra("area", cov.getPointResultString());
			setResult(RESULT_OK, it);

			this.finish();
		} else {
			CustomToast.show(this, R.string.protect_detection_no_field);
		}
	}

	@Override
	protected void SipDataReturn(boolean isSuccess, SipMsgApiType apiType,
			String xmlData, String from, String to) {
		super.SipDataReturn(isSuccess, apiType, xmlData, from, to);
		dismissBaseDialog();
		if (isSuccess) {
			switch (apiType) {
			case CONFIG_MOVEMENT_DETECTION:
				if (Utils.getParamFromXml(xmlData, "status").equalsIgnoreCase(
						"ok")) {
					CustomToast.show(this, R.string.common_setting_success);
					this.finish();
				} else {
					CustomToast.show(this, R.string.common_setting_fail);
				}
				break;

			case QUERY_MOVEMENT_DETECTION_INFO:
				// Utils.sysoInfo("result:"+xmlData);//底层又打印了一次？
				String[] results1 = Utils.getMotionArea(xmlData,
						APPConfig.MAX_MONITOR_AREA);
				for (String s : results1) {
					Utils.sysoInfo("还原监测区:" + s);
				}
				// 还原现场
				cov.restoreMonitorArea(results1);
				break;
			case QUERY_BLOCK_DETECTION_INFO://目前只有一个区域
				// Utils.sysoInfo("result:"+xmlData);//底层又打印了一次？
				String[] results2 = Utils.getMotionArea(xmlData,
						APPConfig.MAX_MONITOR_AREA);
				for (String s : results2) {
					Utils.sysoInfo("还原监测区:" + s);
				}
				// 还原现场
				cov.restoreMonitorArea(results2);
				break;
			default:
				break;
			}

		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_reset) {
			cov.reset();
		} else if (id == R.id.btn_sure) {
			sure();
		} else {
		}

	}
}
