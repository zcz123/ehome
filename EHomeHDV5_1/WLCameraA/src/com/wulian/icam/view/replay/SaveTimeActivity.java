package com.wulian.icam.view.replay;

import java.util.Calendar;
import java.util.Locale;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.wheel.NumericWheelAdapter;
import com.wheel.WheelView;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.Device;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomToast;

/**
 * @ClassName: SaveTimeActivity
 * @Function: 保存时间
 * @Date: 2015年5月27日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class SaveTimeActivity extends BaseFragmentActivity {
	WheelView start_time_hour, start_time_min, end_time_hour, end_time_min;
	Calendar calender;
	CheckBox cb_sun, cb_sat, cb_fri, cb_thurs, cb_wed, cb_tue, cb_mon;
	// CheckBox weekdays[] = { cb_sun, cb_sat, cb_fri, cb_thurs, cb_wed, cb_tue,
	// cb_mon };//赋值太早，全为null
	CheckBox weekdays[];
	SharedPreferences sp;
	Handler myHandler;
	Device device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initListeners();
		initData();

	}

	@Override
	protected OnClickListener getRightClick() {

		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 时间判断
				if (start_time_hour.getCurrentItem() * 60
						+ start_time_min.getCurrentItem() < end_time_hour
						.getCurrentItem() * 60 + end_time_min.getCurrentItem()) {
					Editor editor = sp.edit();
					// 时间格式: 3,4,5,6
					editor.putString(device.getDevice_id()
							+ APPConfig.HISTORY_SAVE_TIME,
							start_time_hour.getCurrentItem() + ","
									+ start_time_min.getCurrentItem() + ","
									+ end_time_hour.getCurrentItem() + ","
									+ end_time_min.getCurrentItem());
					// 星期格式: , 或 7,6,5,
					editor.putString(device.getDevice_id()
							+ APPConfig.HISTORY_SAVE_WEEKDAY, getWeekdayNums());
					editor.commit();
					setResult(RESULT_OK);
					SaveTimeActivity.this.finish();

				} else {
					CustomToast.show(SaveTimeActivity.this,
							R.string.common_end_than_start);
					return;
				}

			}
		};
	}

	public String getWeekdayNums() {
		StringBuilder sb = new StringBuilder();
		for (CheckBox cb : weekdays) {
			if (cb.isChecked()) {
				sb.append(cb.getTag()).append(",");
			}
		}

		String result = sb.toString();
		if ("".equals(result))// 单独处理
			return ",";
		return result;
	}

	private void initViews() {
		start_time_hour = (WheelView) findViewById(R.id.start_time_hour);
		start_time_min = (WheelView) findViewById(R.id.start_time_min);
		end_time_hour = (WheelView) findViewById(R.id.end_time_hour);
		end_time_min = (WheelView) findViewById(R.id.end_time_min);
		// ((WheelView) findViewById(R.id.colon1))
		// .setAdapter(new ArrayWheelAdapter<String>(new String[] { ":" }));

		// start_time_hour.setLabel(getResources().getString(R.string.hour));
		// start_time_min.setLabel(getResources().getString(R.string.min));
		// end_time_hour.setLabel(getResources().getString(R.string.hour));
		// end_time_min.setLabel(getResources().getString(R.string.min));

		cb_sun = (CheckBox) findViewById(R.id.cb_sun);
		cb_sat = (CheckBox) findViewById(R.id.cb_sat);
		cb_fri = (CheckBox) findViewById(R.id.cb_fri);
		cb_thurs = (CheckBox) findViewById(R.id.cb_thurs);
		cb_wed = (CheckBox) findViewById(R.id.cb_wed);
		cb_tue = (CheckBox) findViewById(R.id.cb_tue);
		cb_mon = (CheckBox) findViewById(R.id.cb_mon);

		cb_sun.setTag(7);
		cb_sat.setTag(6);
		cb_fri.setTag(5);
		cb_thurs.setTag(4);
		cb_wed.setTag(3);
		cb_tue.setTag(2);
		cb_mon.setTag(1);

		weekdays = new CheckBox[] { cb_sun, cb_mon, cb_tue, cb_wed, cb_thurs,
				cb_fri, cb_sat };// 遍历顺序，决定了显示的效果

	}

	private void initListeners() {

	}

	private void initData() {
		device = (Device) getIntent().getSerializableExtra("device");
		sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
		calender = Calendar.getInstance(Locale.getDefault());

		start_time_hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
		start_time_min.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		end_time_hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
		end_time_min.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));

		myHandler = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				String timeNum[] = (String[]) msg.obj;
				start_time_hour.setCurrentItem(Integer.parseInt(timeNum[0]),
						true);
				start_time_min.setCurrentItem(Integer.parseInt(timeNum[1]),
						true);
				end_time_hour
						.setCurrentItem(Integer.parseInt(timeNum[2]), true);
				end_time_min.setCurrentItem(Integer.parseInt(timeNum[3]), true);
			}
		};

		// 恢复sp数据
		String time = sp.getString(device.getDevice_id()
				+ APPConfig.HISTORY_SAVE_TIME, "");
		if (!TextUtils.isEmpty(time)) {
			String timeNum[] = time.split(",");
			if (timeNum.length == 4) {
				Message msg = myHandler.obtainMessage();
				msg.obj = timeNum;
				myHandler.sendMessageDelayed(msg, 500);
			} else {
				defaultTime();
			}
		} else {
			defaultTime();
		}
		restoreWeekdays(sp.getString(device.getDevice_id()
				+ APPConfig.HISTORY_SAVE_WEEKDAY, ""));

	}

	public void defaultTime() {
		start_time_hour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
		start_time_min.setCurrentItem(calender.get(Calendar.MINUTE));
		end_time_hour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
		end_time_min.setCurrentItem(calender.get(Calendar.MINUTE));
	}

	public void restoreWeekdays(String weekdayNums) {
		if (TextUtils.isEmpty(weekdayNums)) {
			return;
		}
		String nums[] = weekdayNums.split(",");
		for (String s : nums) {
			int sValue = Integer.parseInt(s);
			switch (sValue) {
			case 7:
				cb_sun.setChecked(true);
				break;
			case 1:
				cb_mon.setChecked(true);
				break;
			case 2:
				cb_tue.setChecked(true);
				break;
			case 3:
				cb_wed.setChecked(true);
				break;
			case 4:
				cb_thurs.setChecked(true);
				break;
			case 5:
				cb_fri.setChecked(true);
				break;
			case 6:
				cb_sat.setChecked(true);
				break;
			}
		}
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_history_timeset);
	}

	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.replay_save_time);
	}
}
