package com.yuantuo.customview.wheel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import com.yuantuo.customview.R;
import com.yuantuo.customview.wheel.NumericWheelAdapter;
import com.yuantuo.customview.wheel.WheelView;

/**
 * @auther:summer 时间： 2012-7-19 下午2:59:56
 */
public class WL_28_WaterValue_TimePickerDialog extends AlertDialog implements
		OnClickListener {

	private final OnDateTimeSetListener mCallBack;
	private final Calendar mCalendar;
	private int curr_hour, curr_minute, curr_second;
	final WheelView wv_hours, wv_mins, wv_second;

	public WL_28_WaterValue_TimePickerDialog(Context context,
			final Calendar calendar, OnDateTimeSetListener callBack) {
		super(context);
		// mCalendar = Calendar.getInstance();
		if (null == calendar) {
			mCalendar = Calendar.getInstance();
		} else
			mCalendar = calendar;
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);
		int second = mCalendar.get(Calendar.SECOND);
		mCallBack = callBack;
		setButton(context.getText(R.string.switch_on), this);
		setButton2(context.getText(R.string.switch_off), (OnClickListener) null);
		// setIcon(R.drawable.ic_launcher);
		setTitle(context.getResources().getString(R.string.WL_28_time_chose));
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.wl_3timepicker_dialog, null);

		int textSize = 0;
		textSize = adjustFontSize(getWindow().getWindowManager());
		// 时
		wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_hours.setAdapter(new NumericWheelAdapter(0, 1));
		wv_hours.setCyclic(true);
		wv_hours.setCurrentItem(hour);
		// 分
		wv_mins = (WheelView) view.findViewById(R.id.mins);
		wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		wv_mins.setCyclic(true);
		wv_mins.setCurrentItem(minute);

		// 秒
		wv_second = (WheelView) view.findViewById(R.id.second);
		wv_second.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		wv_second.setCyclic(true);
		wv_second.setCurrentItem(second);
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;
		wv_second.TEXT_SIZE = textSize;
		setView(view);
	}

	public void onClick(DialogInterface dialog, int which) {

		curr_hour = wv_hours.getCurrentItem();
		curr_minute = wv_mins.getCurrentItem();
		curr_second = wv_second.getCurrentItem();
		if (mCallBack != null) {
			mCallBack.onDateTimeSet(curr_hour, curr_minute, curr_second);
		}
	}

	public void show() {
		super.show();
	}

	public interface OnDateTimeSetListener {
		void onDateTimeSet(int hour, int minute, int second);
	}

	public static int adjustFontSize(WindowManager windowmanager) {

		int screenWidth = windowmanager.getDefaultDisplay().getWidth();
		int screenHeight = windowmanager.getDefaultDisplay().getHeight();

		if (screenWidth <= 240) { // 240X320 屏幕
			return 10;
		} else if (screenWidth <= 320) { // 320X480 屏幕
			return 14;
		} else if (screenWidth <= 480) { // 480X800 或 480X854 屏幕
			return 24;
		} else if (screenWidth <= 540) { // 540X960 屏幕
			return 26;
		} else if (screenWidth <= 800) { // 800X1280 屏幕
			return 30;
		} else { // 大于 800X1280
			return 70;
		}
	}
}