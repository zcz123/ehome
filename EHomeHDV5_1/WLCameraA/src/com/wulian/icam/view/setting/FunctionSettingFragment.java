/**
 * Project Name:  FamilyRoute
 * File Name:     MoreActivity.java
 * Package Name:  com.wulian.familyroute.view.main
 * @Date:         2014年10月9日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.view.widget.CustomToast;

/**
 * @Function: 功能设置
 * @date: 2015年6月25日
 * @author Wangjj
 */

public class FunctionSettingFragment extends Fragment implements
		OnClickListener, OnCheckedChangeListener {
	private LinearLayout ll_network_protect, ll_alarm_push, ll_gesture_pwd;
	private CheckBox ck_network_protect, ck_alarm_push, ck_gesture_pwd;
	private View fragmentView;
	private ImageView titlebar_back;
	private SharedPreferences sp;
	private String uuid = "";
	private static final int REQUEST_SET_GESTURE = 0;
	private static final int REQUEST_CANCLE_GESTURE = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_function_setting,
				container, false);
		return fragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}

	public String getUUID() {
		if (TextUtils.isEmpty(uuid)) {
			uuid = ICamGlobal.getInstance().getUserinfo().getUuid();
		}
		return uuid;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		initViews();
		initListeners();
	}

	private void initViews() {
		((TextView) fragmentView.findViewById(R.id.titlebar_title))
				.setText(R.string.setting_function_setting);
		titlebar_back = (ImageView) fragmentView
				.findViewById(R.id.titlebar_back);

		sp = getActivity().getSharedPreferences(APPConfig.SP_CONFIG,
				Context.MODE_PRIVATE);
		ll_network_protect = (LinearLayout) fragmentView
				.findViewById(R.id.ll_network_protect);
		ck_network_protect = (CheckBox) fragmentView
				.findViewById(R.id.ck_network_protect);
		ck_network_protect.setChecked(sp.getBoolean(getUUID()
				+ APPConfig.NETWORK_PROTECT, true));

		ll_alarm_push = (LinearLayout) fragmentView
				.findViewById(R.id.ll_alarm_push);
		ck_alarm_push = (CheckBox) fragmentView
				.findViewById(R.id.ck_alarm_push);
		ck_alarm_push.setChecked(sp.getBoolean(
				getUUID() + APPConfig.ALARM_PUSH, true));

		ll_gesture_pwd = (LinearLayout) fragmentView
				.findViewById(R.id.ll_gesture_pwd);
		ck_gesture_pwd = (CheckBox) fragmentView
				.findViewById(R.id.ck_gesture_pwd);
		ck_gesture_pwd.setChecked(sp.getBoolean(getUUID()
				+ APPConfig.IS_GESTURE_PROTECT, false));
	}

	private void initListeners() {
		titlebar_back.setOnClickListener(this);
		ll_network_protect.setOnClickListener(this);
		ck_network_protect.setOnCheckedChangeListener(this);
		ll_alarm_push.setOnClickListener(this);
		ck_alarm_push.setOnCheckedChangeListener(this);
		ll_gesture_pwd.setOnClickListener(this);
		ck_gesture_pwd.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.ll_network_protect) {
			ck_network_protect.toggle();
		} else if (id == R.id.ll_alarm_push) {
			ck_alarm_push.toggle();
		} else if (id == R.id.ll_gesture_pwd) {
			ck_gesture_pwd.toggle();
		} else if (id == R.id.titlebar_back) {
			this.getActivity().finish();
		}
		getActivity().overridePendingTransition(R.anim.push_right_in,
				R.anim.push_left_out);

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		if (id == R.id.ck_network_protect) {
			Editor editor = sp.edit();
			if (isChecked) {
				editor.putBoolean(getUUID() + APPConfig.NETWORK_PROTECT, true);
			} else {
				editor.putBoolean(getUUID() + APPConfig.NETWORK_PROTECT, false);
			}
			editor.commit();
		} else if (id == R.id.ck_alarm_push) {
			Editor aleditor = sp.edit();
			if (isChecked) {
				aleditor.putBoolean(getUUID() + APPConfig.ALARM_PUSH, true);
			} else {
				aleditor.putBoolean(getUUID() + APPConfig.ALARM_PUSH, false);
			}
			aleditor.commit();
		} else if (id == R.id.ck_gesture_pwd) {
			if (isChecked) {// 设置手势

				if (!sp.getBoolean(getUUID() + APPConfig.IS_GESTURE_PROTECT,
						false)) {
					startActivityForResult(new Intent(this.getActivity(),
							GesturePwdActivity.class), REQUEST_SET_GESTURE);
				}
			} else {// 取消手势
//				if (sp.getBoolean(getUUID() + APPConfig.IS_GESTURE_PROTECT,
//						false)) {
//					startActivityForResult(new Intent(this.getActivity(),
//							GesturePwdCancelActivity.class),
//							REQUEST_CANCLE_GESTURE);
//				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_SET_GESTURE:
			if (resultCode == Activity.RESULT_OK) {
				CustomToast.show(getActivity(), R.string.gesture_success);
			} else {
				ck_gesture_pwd.setChecked(false);// 触发监听器处理,仅仅是ui变化
			}
			break;
		case REQUEST_CANCLE_GESTURE:
			if (resultCode == Activity.RESULT_OK) {
				CustomToast
						.show(getActivity(), R.string.gesture_cancle_success);
			} else {
				ck_gesture_pwd.setChecked(true);// 触发监听器处理,仅仅是ui变化
			}
			break;

		default:
			break;
		}
	}
}
