/**
 * Project Name:  iCam
 * File Name:     TestSettingActivity.java
 * Package Name:  com.wulian.icam.view.setting
 * @Date:         2015年9月30日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;

/**
 * @ClassName: TestSettingActivity
 * @Function: TODO
 * @Date: 2015年9月30日
 * @author Yanmin
 * @email min.yan@wuliangroup.cn
 */
public class TestSettingActivity extends Activity {
	private TextView tv_titlebar_title;
	private CheckBox cb_test_server, cb_open_log;
	private Button btn_sure;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_setting);
		initView();
		initData();
	}

	private void initView() {
		tv_titlebar_title = (TextView) findViewById(R.id.titlebar_title);
		tv_titlebar_title.setText("后台测试设置界面");

		cb_test_server = (CheckBox) findViewById(R.id.cb_test_server);
		cb_open_log = (CheckBox) findViewById(R.id.cb_open_log);

		ImageView titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
		titlebar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_sure = (Button) findViewById(R.id.btn_sure);
	}

	private void initData() {
		sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
		boolean bTestServer = sp.getBoolean(APPConfig.KEY_TEST_SERVER, false);
		boolean bOpenLog = sp.getBoolean(APPConfig.KEY_OPEN_LOG, false);

		cb_test_server.setChecked(bTestServer);
		cb_open_log.setChecked(bOpenLog);

	}

	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_sure) {
			boolean bTestServer = cb_test_server.isChecked();
			boolean bOpenLog = cb_open_log.isChecked();
			Editor editor = sp.edit();
			editor.putBoolean(APPConfig.KEY_TEST_SERVER, bTestServer);
			editor.putBoolean(APPConfig.KEY_OPEN_LOG, bOpenLog);
			editor.commit();
			System.exit(0);
		} else if (id == R.id.tv_titlebar_title) {
			finish();
		}
	}

}
