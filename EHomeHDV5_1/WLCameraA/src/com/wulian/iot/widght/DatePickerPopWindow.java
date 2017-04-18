package com.wulian.iot.widght;
import com.wulian.icam.R;
import com.wulian.iot.widght.VCalendar.OnCalendarClickListener;
import com.wulian.iot.widght.VCalendar.OnCalendarDateChangedListener;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
public abstract class DatePickerPopWindow extends PopupWindow implements OnCalendarClickListener ,OnCalendarDateChangedListener,OnClickListener {
	public static final String NORMAL_DATE_FORMAT = "yyyy-MM-dd";
	public DatePickerPopWindow(Context mContext){
		super(mContext);
		DatePickerPopWindow.this.setmContext(mContext);
		DatePickerPopWindow.this.onCreate();
	}
	private Context mContext = null;
	private View view = null;
	private VCalendar calendar  = null;
	private TextView popupwindow_calendar_month = null;
	private String srarchTime = null;
	private RelativeLayout  mHomeCheckDate ,popupwindow_calendar_last_month,popupwindow_calendar_next_month=null;
	private LinearLayout ll_popup = null;
	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}
	private void onCreate(){
		view = View.inflate(mContext,R.layout.popupwindow_calendar, null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_in));
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new ColorDrawable(R.color.white));
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		update();
		this.initView();
	}
	private void initView(){
		popupwindow_calendar_month = (TextView) view.findViewById(R.id.popupwindow_calendar_month);
	    calendar = (VCalendar) view.findViewById(R.id.popupwindow_calendar);
        popupwindow_calendar_month.setText(calendar.getCalendarYear() + "."+ calendar.getCalendarMonth());
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.calendar_push));
        mHomeCheckDate = (RelativeLayout) view.findViewById(R.id.home_check_date);
        popupwindow_calendar_last_month = (RelativeLayout) view.findViewById(R.id.popupwindow_calendar_last_month);
        popupwindow_calendar_next_month = (RelativeLayout) view.findViewById(R.id.popupwindow_calendar_next_month);
		this.initEvents();
	}
	private void initEvents(){
		calendar.setOnCalendarClickListener(this);
		calendar.setOnCalendarDateChangedListener(this);
		popupwindow_calendar_last_month.setOnClickListener(this);
		popupwindow_calendar_next_month.setOnClickListener(this);
		mHomeCheckDate.setOnClickListener(this);
	}
	@Override
	public void onCalendarClick(int row, int col, String dateFormat) {
		int month = Integer.parseInt(dateFormat.substring(
				dateFormat.indexOf("-") + 1,
				dateFormat.lastIndexOf("-")));
		if (calendar.getCalendarMonth() - month == 1// 跨年跳转
				|| calendar.getCalendarMonth() - month == -11) {
			calendar.lastMonth();
		} else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
				|| month - calendar.getCalendarMonth() == -11) {
			calendar.nextMonth();
		} else {
			calendar.removeAllBgColor();
			calendar.setCalendarDayBgColor(dateFormat,
					R.drawable.calendar_datetime_focused);
			    srarchTime=dateFormat;
			    DatePickerPopWindow.this.callBackData(srarchTime);
		}
	}
	@Override
	public void onCalendarDateChanged(int year, int month) {
		popupwindow_calendar_month.setText(year + "." + month);
	}
	@Override
	public void onClick(View v) {
		if(v == popupwindow_calendar_last_month){
			calendar.lastMonth();
		} else if(v == popupwindow_calendar_next_month){
			calendar.nextMonth();
		}else if(v == mHomeCheckDate){
			dismiss();
		}
	}
	public void show(View parent){
		showAtLocation(parent, Gravity.CENTER, 0, 0);
	}
	public abstract void callBackData(String data);
}
