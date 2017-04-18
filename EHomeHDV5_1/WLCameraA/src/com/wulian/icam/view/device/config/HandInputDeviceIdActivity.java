/**
 * Project Name:  iCam
 * File Name:     HandInputDeviceIdActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2014年10月28日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.zip.Inflater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.Device;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseActivity;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomToast;

/**
 * @ClassName: HandInputDeviceIdActivity
 * @Function: 手动输入设备ID
 * @Date: 2014年10月28日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class HandInputDeviceIdActivity extends BaseFragmentActivity implements
		OnClickListener {
	private ImageView btn_titlebar_back;
	private Button btn_ok;
	private TextView tv_titlebar_title;
	private AutoCompleteTextView et_deviceid;
	String[] deviceIds;// 本地所有设备id
	String[] enter_deviceIds;// 以存取的所有设备id
	String[] data;// 能呈现数据
	SharedPreferences sp;
	StringBuffer sBuffer = new StringBuffer();
	private ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hand_input_deviceid);
		initViews();
		initData();
		initListeners();
	}

	private void initViews() {
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.titlebar_title);
		tv_titlebar_title.setText(getResources().getString(
				R.string.config_hand_input));
		et_deviceid = (AutoCompleteTextView) findViewById(R.id.et_deviceid);
		et_deviceid.setDropDownVerticalOffset(et_deviceid.getHeight());
		et_deviceid.setDropDownBackgroundResource(R.color.transparent);
		et_deviceid.setDropDownHeight(this.getResources()
				.getDimensionPixelSize(R.dimen.v2_config_wifi_prog_height));
	}

	private void initData() {
		sp = getSharedPreferences(APPConfig.SP_CONFIG, Context.MODE_PRIVATE);
		String idsCache = sp.getString(app.getUserinfo().getUuid()
				+ APPConfig.HAND_INPUT_DEVICEID_CACHE, "");
		if (idsCache != null && !idsCache.equals("")) {
			sBuffer.append(idsCache);
			enter_deviceIds = idsCache.split("##");
		}
		ArrayList<Device> deviceList = app.getDeviceList();// ((ICamApplication)
															// getApplication())
		if (deviceList != null) {
			deviceIds = new String[deviceList.size()];
			Device tempDevice = null;
			for (int i = 0; i < deviceIds.length; i++) {
				tempDevice = deviceList.get(i);
				// 统一不显示cmic
				deviceIds[i] = tempDevice.getDevice_id().substring(4);
			}
		}
		// 比较，取出本地已有设备id
		data = new String[0];
		if (enter_deviceIds != null && enter_deviceIds.length > 0) {
			if (deviceIds == null) {
				// 无需检索
				data = enter_deviceIds;
			} else if (deviceIds.length == 0) {
				// 无需检索
				data = enter_deviceIds;
			} else {
				for (int i = 0; i < enter_deviceIds.length; i++) {
					boolean flag = false;
					String temp = enter_deviceIds[i];
					for (int j = 0; j < deviceIds.length; j++) {
						if (enter_deviceIds[i].equals(deviceIds[j])) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						data = Arrays.copyOf(data, data.length + 1);
						data[data.length - 1] = temp;
					}
				}
			}
		}
		arrayAdapter = new ArrayAdapter<>(this, R.layout.item_simple_textview,
				data);
		et_deviceid.setAdapter(arrayAdapter);
	}

	private void initListeners() {
		btn_ok.setOnClickListener(this);
		btn_titlebar_back.setOnClickListener(this);
		et_deviceid.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			this.finish();
		} else if (id == R.id.et_deviceid) {
			et_deviceid.setCursorVisible(true);
		} else if (id == R.id.btn_ok) {
			et_deviceid.setCursorVisible(false);
			String deviceid = et_deviceid.getText().toString().trim();
			if (TextUtils.isEmpty(deviceid)) {
				CustomToast.show(
						this,
						getResources().getString(
								R.string.config_please_input_device_id));
				return;
			}
			if (deviceid.length() < 16) {
				CustomToast.show(this, R.string.config_error_deviceid);
				return;
			}
			if (!deviceid.toLowerCase(Locale.ENGLISH).startsWith("cmic")) {
				deviceid = "cmic" + deviceid;
			}
			setResult(1, new Intent().putExtra("deviceid", deviceid));
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Utils.hideKeyboard(this);
	}
}
