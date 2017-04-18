/**
 * Project Name:  iCam
 * File Name:     SafePlan.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年2月3日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.protect;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wheel.NumericWheelAdapter;
import com.wheel.WheelView;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.siplibrary.utils.WulianLog;

/**
 * @ClassName: SafeDurationActivity
 * @Function: 安防计划
 * @Date: 2015年2月3日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class SafeDurationActivity extends BaseFragmentActivity implements
		OnClickListener {
	public static final String TIME_ALL_DAY = "00,00,23,59";
	public static final String TIME_DAY = "08,00,20,00";
	public static final String TIME_NIGHT = "20,00,08,00";

	public static final String DAY_EVERY = "7,1,2,3,4,5,6,";
	public static final String DAY_WORKDAY = "1,2,3,4,5,";

	private static final int NONE = 0;
	private static final int ALLDAY = 1;
	private static final int DAY = 2;
	private static final int NIGHT = 3;
	private static final int USER_DEFINED = 4;

	private static final int EVERY = 1;
	private static final int WORKDAY = 2;

	private ImageView iv_allday_yes, iv_day_yes, iv_night_yes,
			iv_user_defined_yes;
	private LinearLayout ll_day, ll_allday, ll_night, ll_user_defined;
	private Button btn_sure;
	private CheckBox cb_workday;
	private TextView tv_user_defined;
	private Dialog mTimePeriodDialog;
	// private WheelView start_time_hour1;
	// private WheelView start_time_min1;
	// private WheelView end_time_hour1;
	// private WheelView end_time_min1;

	// private Calendar calender;
	// private AlertDialog dialogDeviceRename;
	// private View dialogDeviceRenameView;

	private String moveTime;
	private String moveWeekday;
	private int moveTimeType = NONE;
	private int moveWeekdayType = EVERY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initListeners();
		initData();
	}

	private void initViews() {
		ll_allday = (LinearLayout) findViewById(R.id.ll_allday);
		ll_day = (LinearLayout) findViewById(R.id.ll_day);
		ll_night = (LinearLayout) findViewById(R.id.ll_night);
		ll_user_defined = (LinearLayout) findViewById(R.id.ll_user_defined);

		iv_allday_yes = (ImageView) findViewById(R.id.iv_allday_yes);
		iv_day_yes = (ImageView) findViewById(R.id.iv_day_yes);
		iv_night_yes = (ImageView) findViewById(R.id.iv_night_yes);
		iv_user_defined_yes = (ImageView) findViewById(R.id.iv_user_defined_yes);

		tv_user_defined = (TextView) findViewById(R.id.tv_user_defined);
		cb_workday = (CheckBox) findViewById(R.id.cb_workday);
		btn_sure = ((Button) findViewById(R.id.btn_sure));
	}

	private void initListeners() {
		ll_allday.setOnClickListener(this);
		ll_day.setOnClickListener(this);
		ll_night.setOnClickListener(this);
		ll_user_defined.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
	}

	private void seleteItem() {
		ll_allday.setBackgroundResource(R.drawable.bg_timeset_normal);
		ll_day.setBackgroundResource(R.drawable.bg_timeset_normal);
		ll_night.setBackgroundResource(R.drawable.bg_timeset_normal);
		ll_user_defined.setBackgroundResource(R.drawable.bg_timeset_normal);

		iv_allday_yes.setVisibility(View.GONE);
		iv_day_yes.setVisibility(View.GONE);
		iv_night_yes.setVisibility(View.GONE);
		iv_user_defined_yes.setVisibility(View.GONE);

		switch (moveTimeType) {
		case ALLDAY:
			ll_allday.setBackgroundResource(R.drawable.bg_timeset_selected);
			iv_allday_yes.setVisibility(View.VISIBLE);
			break;
		case DAY:
			ll_day.setBackgroundResource(R.drawable.bg_timeset_selected);
			iv_day_yes.setVisibility(View.VISIBLE);
			break;
		case NIGHT:
			ll_night.setBackgroundResource(R.drawable.bg_timeset_selected);
			iv_night_yes.setVisibility(View.VISIBLE);
			break;
		case USER_DEFINED:
			ll_user_defined
					.setBackgroundResource(R.drawable.bg_timeset_selected);
			iv_user_defined_yes.setVisibility(View.VISIBLE);
			String time[] = moveTime.split(",");
			if (time.length == 4) {
				Utils.formatSingleNum(time);
				tv_user_defined.setText(time[0] + ":" + time[1] + "  --  "
						+ time[2] + ":" + time[3]);
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			this.finish();
		} else if (id == R.id.ll_allday) {
			moveTimeType = ALLDAY;
			moveTime = TIME_ALL_DAY;
			seleteItem();
		} else if (id == R.id.ll_day) {
			moveTimeType = DAY;
			moveTime = TIME_DAY;
			seleteItem();
		} else if (id == R.id.ll_night) {
			moveTimeType = NIGHT;
			moveTime = TIME_NIGHT;
			seleteItem();
		} else if (id == R.id.ll_user_defined) {
			showDurationSeletor();
		} else if (id == R.id.btn_sure) {
			if (cb_workday.isChecked())
				moveWeekday = DAY_WORKDAY;
			else
				moveWeekday = DAY_EVERY;
			Intent it = new Intent();
			it.putExtra("time", moveTime);
			it.putExtra("weekday", moveWeekday);
			setResult(RESULT_OK, it);
			finish();
		}
	}

	private void initData() {
//		moveTime = getIntent().getStringExtra("time");
//		moveWeekday = getIntent().getStringExtra("weekday");

		if (TextUtils.isEmpty(moveTime)) {
			moveTimeType = ALLDAY;
			moveTime = TIME_ALL_DAY;
		} else if (moveTime.equalsIgnoreCase(TIME_ALL_DAY)) {
			moveTimeType = ALLDAY;
		} else if (moveTime.equalsIgnoreCase(TIME_DAY)) {
			moveTimeType = DAY;
		} else if (moveTime.equalsIgnoreCase(TIME_NIGHT)) {
			moveTimeType = NIGHT;
		} else {
			moveTimeType = USER_DEFINED;
		}

		seleteItem();

		if (TextUtils.isEmpty(moveWeekday)) {
			moveWeekdayType = EVERY;
		} else if (moveWeekday.equalsIgnoreCase(DAY_WORKDAY)) {
			moveWeekdayType = WORKDAY;
		} else {
			moveWeekdayType = EVERY;
		}

		if (moveWeekdayType == EVERY)
			cb_workday.setChecked(false);
		else
			cb_workday.setChecked(true);

		// calender = Calendar.getInstance(Locale.getDefault());
	}

	private void showDurationSeletor() {
		Resources rs = getResources();
		mTimePeriodDialog = DialogUtils.showCommonTimePeriodDialog(this, true,
				rs.getString(R.string.protect_user_defined), null, null,
				moveTime, new OnClickListener() {
					@Override
					public void onClick(View v) {
						int id = v.getId();
						if (id == R.id.btn_positive) {
							String timeStr = (String) v.getTag();
							String time[] = timeStr.split(",");
							if (time.length == 4) {
								// TODO 暂时不支持跨夜设置，此处加个判断
								if (Integer.parseInt(time[0]) > Integer.parseInt(time[2])
										|| (Integer.parseInt(time[0]) == Integer
												.parseInt(time[2]) && Integer.parseInt(time[1]) >= Integer
												.parseInt(time[3]))) {
									CustomToast.show(SafeDurationActivity.this, R.string.protect_time_invalid);
									return;
								}
							}
							mTimePeriodDialog.dismiss();
							moveTime = timeStr;
							moveTimeType = USER_DEFINED;
							seleteItem();
						} else if (id == R.id.btn_negative) {
							mTimePeriodDialog.dismiss();
						}
					}
				});

		// // 弹出窗体，进行设备命名
		// if (dialogDeviceRename == null) {
		// dialogDeviceRename = new AlertDialog.Builder(this,
		// R.style.alertDialog).create();
		// }
		//
		// if (dialogDeviceRenameView == null) {
		// dialogDeviceRenameView = LinearLayout.inflate(this,
		// R.layout.custom_common_timeperiod_alertdialog,
		// (ViewGroup) findViewById(R.id.ll_custom_alertdialog));
		//
		// start_time_hour1 = (WheelView) dialogDeviceRenameView
		// .findViewById(R.id.start_time_hour);
		// start_time_min1 = (WheelView) dialogDeviceRenameView
		// .findViewById(R.id.start_time_min);
		// end_time_hour1 = (WheelView) dialogDeviceRenameView
		// .findViewById(R.id.end_time_hour);
		// end_time_min1 = (WheelView) dialogDeviceRenameView
		// .findViewById(R.id.end_time_min);
		// TextView tv_title = (TextView) dialogDeviceRenameView
		// .findViewById(R.id.tv_title);
		//
		// start_time_hour1.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
		// start_time_min1.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		// end_time_hour1.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
		// end_time_min1.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		//
		// start_time_hour1.setCyclic(true);
		// start_time_min1.setCyclic(true);
		// end_time_hour1.setCyclic(true);
		// end_time_min1.setCyclic(true);
		//
		// start_time_hour1.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
		// start_time_min1.setCurrentItem(calender.get(Calendar.MINUTE));
		// end_time_hour1.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
		// end_time_min1.setCurrentItem(calender.get(Calendar.MINUTE));
		//
		// tv_title.setText(R.string.protect_user_defined);
		//
		// ((Button) dialogDeviceRenameView.findViewById(R.id.btn_positive))
		// .setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // if (start_time_hour1.getCurrentItem() * 60
		// // + start_time_min1.getCurrentItem() <
		// // end_time_hour1
		// // .getCurrentItem()
		// // * 60
		// // + end_time_min1.getCurrentItem()) {
		//
		// moveTime = start_time_hour1.getCurrentItem() + ","
		// + start_time_min1.getCurrentItem() + ","
		// + end_time_hour1.getCurrentItem() + ","
		// + end_time_min1.getCurrentItem();
		//
		// // moveTime = (String)v.getTag();
		// moveTimeType = USER_DEFINED;
		// seleteItem();
		// dialogDeviceRename.dismiss();
		// // } else {
		// // CustomToast.show(SafeDurationActivity.this,
		// // R.string.end_than_start);
		// // return;
		// // }
		// }
		// });
		// ((Button) dialogDeviceRenameView.findViewById(R.id.btn_negative))
		// .setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// dialogDeviceRename.dismiss();
		// }
		// });
		// }
		//
		// if (!TextUtils.isEmpty(moveTime)) {
		// String timeNum[] = moveTime.split(",");
		// if (timeNum.length == 4) {
		// start_time_hour1.setCurrentItem(Integer.parseInt(timeNum[0]),
		// false);
		// start_time_min1.setCurrentItem(Integer.parseInt(timeNum[1]),
		// false);
		// end_time_hour1.setCurrentItem(Integer.parseInt(timeNum[2]),
		// false);
		// end_time_min1.setCurrentItem(Integer.parseInt(timeNum[3]),
		// false);
		// } else {
		// defaultTime();
		// }
		// } else {
		// defaultTime();
		// }
		//
		// // moveTime
		//
		// dialogDeviceRename.show();
		// dialogDeviceRename.setContentView(dialogDeviceRenameView);
	}

	public void defaultTime() {
		// start_time_hour1.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
		// start_time_min1.setCurrentItem(calender.get(Calendar.MINUTE));
		// end_time_hour1.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
		// end_time_min1.setCurrentItem(calender.get(Calendar.MINUTE));
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_safe_duration);
	}

	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.protect_time);
	}
}
