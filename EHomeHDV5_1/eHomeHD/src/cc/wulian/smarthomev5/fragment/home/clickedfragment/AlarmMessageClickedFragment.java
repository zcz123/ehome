package cc.wulian.smarthomev5.fragment.home.clickedfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.yuantuo.customview.ui.KCalendar;
import com.yuantuo.customview.ui.KCalendar.OnCalendarClickListener;
import com.yuantuo.customview.ui.KCalendar.OnCalendarDateChangedListener;
import com.yuantuo.customview.ui.WLDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.AlarmMessageClickedAdapter;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.view.AutoRefreshListView;
import cc.wulian.smarthomev5.view.AutoRefreshListView.OnLoadListener;

//单击首页报警信息模块，进入的具体报警信息模块

@SuppressWarnings("deprecation")
public class AlarmMessageClickedFragment extends WulianFragment {

	// public static final String ALARMMESSAGE_URL =
	// "http://58.222.12.99:7007/eHomeService/LogManagerCtr/queryDeviceLog.do";//测试服务器
	public static final String ALARM_KEY = "alarm_key";
	public static final long DayTime13 = 86399999;
	public static final String TAG = "wlcloud";
	public AccountManager mAccountManager = AccountManager.getAccountManger();
	public List<MessageEventEntity> entites = new ArrayList<MessageEventEntity>();

	public LinearLayout headLineLayout;
	public AlarmMessageClickedAdapter adapter;
	public Date selectedDatime;
	public List<Object> dateTimeList = new ArrayList<Object>();
	public TextView mHomeAlarmSelectTime;
	public TextView mMonthText;
	public LinearLayout mAlarmLayout;
	public LinearLayout mAlarmDeletell;
	public AutoRefreshListView mHomeAlarmList;
	public WLDialog dialog;
	public PopupWindows popupWindow;
	public long currentCompareTime;
	public long tmpCompareTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectedDatime = new Date();
		adapter = new AlarmMessageClickedAdapter(mActivity, null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_alarm_message_clicked,
				container, false);
		headLineLayout = (LinearLayout) view
				.findViewById(R.id.home_other_message_head_ll);
		mAlarmLayout = (LinearLayout) view
				.findViewById(R.id.choose_date_background);
		mAlarmLayout.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.light_red_fragment));
		mMonthText = (TextView) view.findViewById(R.id.month_text);
		mMonthText.setText((selectedDatime.getMonth() + 1)
				+ getResources().getString(R.string.home_alarm_message_month));
		mHomeAlarmSelectTime = (TextView) view
				.findViewById(R.id.home_message_selectTime);
		mAlarmDeletell = (LinearLayout) view
				.findViewById(R.id.home_other_delete_item);
		mHomeAlarmList = (AutoRefreshListView) view
				.findViewById(R.id.home_alarm_list);
		mHomeAlarmList.setAdapter(adapter);
		mHomeAlarmList.setDividerHeight(0);
		ViewUtils.inject(this, view);

		for (int i = 0; i < 6; i++) {
			final LinearLayout itemLinearLayout = (LinearLayout) inflater
					.inflate(R.layout.home_other_message_date_time_item, null);
			TextView dayTextView = (TextView) itemLinearLayout
					.findViewById(R.id.home_other_message_item_day_tv);
			LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			itemParams.setMargins(10, 0, 10, 0);
			itemLinearLayout.setLayoutParams(itemParams);
			if (i == 0) {
				itemLinearLayout.setSelected(true);
				SimpleDateFormat selectDayFormat = new SimpleDateFormat(
						"yyyy-MM-dd");
				Date selectDay = null;
				selectDay = DateUtil.getDate0H0M0S(selectedDatime);
				selectedDatime = new Date(selectDay.getTime());
				mHomeAlarmList.closeAllLoad();
				loadDeviceAlarmMessage(selectedDatime);
				mHomeAlarmList.setOnLoadListener(new OnLoadListener() {

					@Override
					public void onLoad() {
						loadTenMessages(selectedDatime);
						mHomeAlarmList.onLoadComplete();
					}
				});
			}

			if (i == 5) {
				dateTimeList.add(new Object());
				dayTextView.setText("...");
				headLineLayout.addView(itemLinearLayout);
				itemLinearLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (popupWindow == null) {
							popupWindow = new PopupWindows(getActivity());
						}
						popupWindow.show(headLineLayout);
					}
				});
				break;
			}
			final Date day = DateUtil.getDateBefore(selectedDatime, i);
			dateTimeList.add(day);
			dayTextView.setText(day.getDate() + "");
			itemLinearLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					selectedDatime = day;
					setSelectedDay();
					mHomeAlarmSelectTime.setText(DateUtil
							.getFormatSimpleDate(selectedDatime));
					mMonthText.setText(selectedDatime.getMonth()
							+ 1
							+ getResources().getString(
									R.string.home_alarm_message_month));
					selectedDatime = DateUtil.getDate0H0M0S(selectedDatime);
					mHomeAlarmList.closeAllLoad();
					loadDeviceAlarmMessage(selectedDatime);
					mHomeAlarmList.setOnLoadListener(new OnLoadListener() {

						@Override
						public void onLoad() {
							loadTenMessages(selectedDatime);
							mHomeAlarmList.onLoadComplete();
						}
					});
				}

			});
			headLineLayout.addView(itemLinearLayout);
		}
		initBar();
		return view;
	}

	public void setSelectedDay() {
		for (int i = 0; i < headLineLayout.getChildCount(); i++) {
			headLineLayout.getChildAt(i).setSelected(false);
		}
		int j = 0;
		for (j = 0; j < dateTimeList.size(); j++) {
			Object dayObject = dateTimeList.get(j);
			if (dayObject instanceof Date) {
				Date dayShow = (Date) dayObject;
				if (dayShow.getYear() == selectedDatime.getYear()
						&& dayShow.getMonth() == selectedDatime.getMonth()
						&& dayShow.getDate() == selectedDatime.getDate()) {
					headLineLayout.getChildAt(j).setSelected(true);
					break;
				} else {
					headLineLayout.getChildAt(j).setSelected(false);
				}
			}
		}
		if (j == dateTimeList.size()) {
			headLineLayout.getChildAt(5).setSelected(true);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		/**
		 * 长按事件
		 */
		// mHomeAlarmList
		// .setOnItemLongClickListener(new OnItemLongClickListener() {
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent,
		// View view, int position, long id) {
		// adapter.setEditMode(true);
		// mAlarmDeletell.setVisibility(View.VISIBLE);
		// iniChangeBar();
		// return true;
		// }
		//
		// });

		// mHomeAlarmList.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// State state = adapter.getState(position);
		// state.setDeleted(!state.isDeleted());
		// adapter.notifyDataSetChanged();
		// }
		// });

		// mAlarmDeletell.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// deleteMessageDialog();
		// }
		// });
	}

	// public void deleteMessageDialog() {
	// WLDialog.Builder builder = new Builder(this.getActivity());
	// builder.setContentView(R.layout.fragment_message_select_delete)
	// .setTitle(R.string.hint_del)
	// .setPositiveButton(R.string.more_gps_ensure)
	// .setNegativeButton(R.string.more_gps_cancle)
	// .setListener(new MessageListener() {
	// @Override
	// public void onClickPositive(View contentViewLayout) {
	//
	// String ids = adapter.getSelectedIds();
	// if (!StringUtil.isNullOrEmpty(ids)) {
	// MessageEventEntity entity = new MessageEventEntity();
	// entity.setGwID(mAccountManger.getmCurrentInfo()
	// .getGwID());
	// entity.setMsgID(ids);
	// messageDao.delete(entity);
	// }
	// // List<MessageEventEntity> messages = getMessages(selectedDatime
	// // .toDate());
	// // adapter.swapData(messages);
	// loadDeviceAlarmMessage(selectedDatime
	// .toDate());
	// adapter.clearState();
	// initBar();
	// adapter.setEditMode(false);
	// mAlarmDeletell.setVisibility(View.GONE);
	//
	// }
	//
	// @Override
	// public void onClickNegative(View contentViewLayout) {
	//
	// }
	// });
	// dialog = builder.create();
	// dialog.show();
	// }

	public void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.home_warning_message));
		getSupportActionBar().setIconText(R.string.nav_home_title);
	}

	// public void iniChangeBar() {
	// getSupportActionBar().setDisplayIconEnabled(false);
	// getSupportActionBar().setDisplayIconTextEnabled(true);
	// getSupportActionBar().setDisplayShowTitleEnabled(false);
	// getSupportActionBar().setDisplayShowMenuEnabled(false);
	// getSupportActionBar().setDisplayShowMenuTextEnabled(true);
	// getSupportActionBar().setIconText(
	// getResources().getString(R.string.home_warning_message_cancel));
	// getSupportActionBar().setRightIconText(
	// getResources().getString(
	// R.string.home_warning_message_check_all));
	// getSupportActionBar().setLeftIconClickListener(
	// new OnLeftIconClickListener() {
	// @Override
	// public void onClick(View v) {
	// adapter.setAllSelect(false);
	// adapter.setEditMode(false);
	// mAlarmDeletell.setVisibility(View.GONE);
	// initBar();
	// }
	// });
	// getSupportActionBar().setRightMenuClickListener(
	// new OnRightMenuClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// adapter.setAllSelect(true);
	//
	// }
	// });
	// }

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext) {

			View view = View.inflate(mContext,
					R.layout.fragment_popupwindow_calendar, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_in));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_1));
			RelativeLayout mHomeCheckDate = (RelativeLayout) view
					.findViewById(R.id.home_check_date);
			setWidth(LayoutParams.WRAP_CONTENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			update();

			final TextView popupwindow_calendar_month = (TextView) view
					.findViewById(R.id.popupwindow_calendar_month);
			final KCalendar calendar = (KCalendar) view
					.findViewById(R.id.popupwindow_calendar);

			popupwindow_calendar_month.setText(calendar.getCalendarYear() + "."
					+ calendar.getCalendarMonth());

			// 监听所选中的日期
			calendar.setOnCalendarClickListener(new OnCalendarClickListener() {

				public void onCalendarClick(int row, int col, String dateFormat) {
					int month = StringUtil.toInteger(dateFormat.substring(
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
								R.drawable.calendar_date_focused);
						SimpleDateFormat selectDayFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						Date selectDay = null;
						try {
							selectDay = selectDayFormat.parse(dateFormat);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						selectedDatime = new Date(selectDay.getTime());
					}
				}
			});

			// 监听当前月份
			calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
				public void onCalendarDateChanged(int year, int month) {
					popupwindow_calendar_month.setText(year + "." + month);
				}
			});

			// 上月监听按钮
			RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) view
					.findViewById(R.id.popupwindow_calendar_last_month);
			popupwindow_calendar_last_month
					.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							calendar.lastMonth();
						}

					});

			// 下月监听按钮
			RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) view
					.findViewById(R.id.popupwindow_calendar_next_month);
			popupwindow_calendar_next_month
					.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							calendar.nextMonth();
						}
					});

			// 关闭窗口
			mHomeCheckDate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SimpleDateFormat selectDayFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					mHomeAlarmSelectTime.setText(selectDayFormat
							.format(selectedDatime));
					mMonthText.setText((selectedDatime.getMonth() + 1)
							+ getResources().getString(
									R.string.home_alarm_message_month));
					// adapter.swapData(getMessages(selectedDatime.toDate()));
					mHomeAlarmList.closeAllLoad();
					loadDeviceAlarmMessage(selectedDatime);
					mHomeAlarmList.setOnLoadListener(new OnLoadListener() {

						@Override
						public void onLoad() {
							mHomeAlarmList.onLoadComplete();
							loadTenMessages(selectedDatime);
						}
					});
					setSelectedDay();
					dismiss();
				}
			});
		}

		@SuppressLint("NewApi")
		public void show(View parent) {
			showAtLocation(parent, Gravity.CENTER, 0, 0);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public synchronized List<MessageEventEntity> getMessages(long starttime,
			long comparetime) {

		List<MessageEventEntity> entites = new ArrayList<MessageEventEntity>();
		try {
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("gwID", mAccountManager.getmCurrentInfo().getGwID());
			jsonObject.put("time", String.valueOf(starttime));// 1426867199999

			String json = HttpUtil.postWulianCloud(
					WulianCloudURLManager.getDeviceInfoURL(), jsonObject);

			if (json != null) {
				Logger.debug("json" + json);
				JSONObject obj = JSON.parseObject(json);
				JSONArray array = obj.getJSONArray("retData");
				// 选择的日期的时间戳+一天的时间戳
				if (array != null) {// 1426831354388(3/20 14:2:34)
									// 1426830397348(3/20 13:46:37)
									// 1426757988159 > 1426734845456(3/19
									// 11:14:5)
					for (int i = 0; i < array.size(); i++) {
						JSONObject alarmObj = array.getJSONObject(i);
						String epMsg=alarmObj.getString("epMsg");
						if(epMsg!=null&&epMsg.equals("N")){
							continue;
						}
						MessageEventEntity entity = new MessageEventEntity();
						// long outl = l;
						if (Long.parseLong(alarmObj.getString("time")) < comparetime) {
							Log.d("TAG", "------>getMessages");
							break;
						} else {
							entity.setTime(alarmObj.getString("time"));
							// entity.setSmile(alarmObj.getString("epStatus"));
							entity.setGwID(alarmObj.getString("gwID"));
							entity.setEpData(alarmObj.getString("epData"));
							entity.setEpType(alarmObj.getString("epType"));
							entity.setEp(alarmObj.getString("ep"));
							entity.setType(alarmObj.getString("type"));
							entity.setDevID(alarmObj.getString("devID"));
							entity.setEpMsg(alarmObj.getString("epMsg"));
							entity.setExtData(alarmObj.getString("extData"));
							entites.add(entity);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
		return entites;

	}

	/**
	 * 加载设备报警信息
	 */
	public synchronized void loadDeviceAlarmMessage(final Date date) {
		mDialogManager.showDialog(ALARM_KEY, mActivity, null, null);
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				entites.clear();
				currentCompareTime = date.getTime();
				List<MessageEventEntity> list = getMessages(
						(date.getTime() + DayTime13), date.getTime());
				if (!list.isEmpty()) {
					entites = list;
					MessageEventEntity message = list.get(list.size() - 1);
					selectedDatime = new Date(StringUtil.toLong(message
							.getTime()));
				}
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mDialogManager.dimissDialog(ALARM_KEY, 0);
						adapter.swapData(entites);
						mHomeAlarmList.setSelection(adapter.getCount());
						mHomeAlarmList.setResultSize(entites.size());
					}
				});
			}
		});
	}

	// 上拉默认加载十条数据
	public synchronized void loadTenMessages(final Date date) {

		mHomeAlarmList.showLoad();
		TaskExecutor.getInstance().executeDelay(new Runnable() {

			@Override
			public void run() {
				final List<MessageEventEntity> list = getMessages(
						date.getTime(), currentCompareTime);
				if (!list.isEmpty()) {
					MessageEventEntity message = list.get(list.size() - 1);
					selectedDatime = new Date(StringUtil.toLong(message
							.getTime()));
				}
				if (selectedDatime.getTime() != tmpCompareTime) {// 避免ui没有跟新完成时多次上滑造成重复加载的现象
																	// add by hxc
															
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							entites.addAll(list);
							mHomeAlarmList.setResultSize(list.size());
							adapter.swapData(entites);
							mHomeAlarmList.closeAllLoad();
							tmpCompareTime = selectedDatime.getTime();
						}
					});

				}
			}
		}, 1000);

	}

}