/**
 * Project Name:  iCam
 * File Name:     GesturePwd.java
 * Package Name:  com.wulian.icam.view.setting
 * @Date:         2015年6月8日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.setting;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.view.base.BaseActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.view.widget.Lock9View;
import com.wulian.icam.view.widget.Lock9View.CallBack;

/**
 * @ClassName: GesturePwdActivity
 * @Function: 手势密码
 * @Date: 2015年6月8日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class GesturePwdActivity extends BaseActivity implements OnClickListener {
	private TextView titlebar_title, tv_info;
	private ImageView titlebar_back;
	private Lock9View lockView;
	private String firstPwd, secondPwd;
	private int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_pwd);
		initViews();
		initListeners();
		initData();
	}

	private void initViews() {
		titlebar_title = (TextView) findViewById(R.id.titlebar_title);
		titlebar_title.setText(R.string.gesture_setting);
		tv_info = (TextView) findViewById(R.id.tv_info);
		tv_info.setText(R.string.gesture_firstpwd);
		titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
		lockView = (Lock9View) findViewById(R.id.lockView);
	}

	String uuid;

	public String getUUID() {
		if (TextUtils.isEmpty(uuid)) {
			uuid = ICamGlobal.getInstance().getUserinfo().getUuid();
		}
		return uuid;
	}

	private void initListeners() {
		titlebar_back.setOnClickListener(this);
		lockView.setCallBack(new CallBack() {

			@Override
			public void onFinish(String password) {
				if (i == 0) {
					firstPwd = password;
					tv_info.setText(R.string.gesture_secondpwd);
					i++;
				} else if (i == 1) {
					secondPwd = password;
					if (!TextUtils.isEmpty(firstPwd)
							&& firstPwd.equals(secondPwd)) {
						getSharedPreferences(APPConfig.SP_CONFIG,
								Context.MODE_PRIVATE)
								.edit()
								.putBoolean(
										getUUID()
												+ APPConfig.IS_GESTURE_PROTECT,
										true)
								.putString(getUUID() + APPConfig.GESTURE_PWD,
										firstPwd).commit();
						setResult(RESULT_OK);
						GesturePwdActivity.this.finish();

					} else {
						CustomToast.show(GesturePwdActivity.this,
								R.string.gesture_notmatch);
						tv_info.setText(R.string.gesture_firstpwd);
						i--;
					}
				}
			}
		});
	}

	private void initData() {

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			this.finish();
		}
	}
}
