package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.DoorLockHistoryAdapter;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.fragment.home.clickedfragment.AlarmMessageClickedFragment;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.view.AutoRefreshListView;
import cc.wulian.smarthomev5.view.AutoRefreshListView.OnLoadListener;

public class EditDoorLock6HistoryFragment extends AlarmMessageClickedFragment {
	private String gwID;
	private String devID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		gwID = getArguments().getString(EditDoorLock6Fragment.GWID);
		devID = getArguments().getString(EditDoorLock6Fragment.DEVICEID);
		adapter = new DoorLockHistoryAdapter(mActivity, null);
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
		mAlarmLayout.setBackgroundColor(getResources().getColor(R.color.grey_3));
		mMonthText = (TextView) view.findViewById(R.id.month_text);
		mMonthText.setTextColor(getResources().getColor(R.color.black));
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
			//TODO 改图片
			itemLinearLayout.setBackgroundResource(R.drawable.doorlock_radiobutton);
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
					mMonthText.setText(selectedDatime.getMonth()+1
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

	@Override
	public void initBar() {
		// TODO Auto-generated method stub
		super.initBar();
		getSupportActionBar().setTitle(getString(R.string.smartLock_unlock_history));
		getSupportActionBar().setIconText(getString(R.string.set_titel));
	}

	@Override
	public synchronized List<MessageEventEntity> getMessages(long starttime,
			long comparetime) {
		List<MessageEventEntity> entites = new ArrayList<MessageEventEntity>();
		try {
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("gwID", gwID);
			jsonObject.put("devID", devID);
			jsonObject.put("dataRegex", "0808");
			jsonObject.put("time", String.valueOf(starttime));// 1426867199999

			// String json = HttpUtil.postWulianCloud(
			// WulianCloudURLManager.getDeviceInfoURL(), jsonObject);
			String json = HttpUtil
					.postWulianCloud(
							"https://acs.wuliancloud.com:33443/acs/gateway/queryDeviceData",
							jsonObject);

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
						MessageEventEntity entity = new MessageEventEntity();
						// long outl = l;
						if (Long.parseLong(alarmObj.getString("time")) < comparetime) {
							System.out.println("------>getMessages");
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
							entites.add(entity);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// if(entites.size()<10){
		// loadTenMessages(selectedDatime);
		// }else{
		// currentNumberr=0;
		// }
		return entites;

	}

	/**
	 * 加载设备报警信息
	 */
	@Override
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
					MessageEventEntity message = list.get(list.size() - 1);
					selectedDatime = new Date(StringUtil.toLong(message
							.getTime()));
				}
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getEpData().startsWith("0808")) {
						entites.add(list.get(i));
					}
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
//				if (currentNumberr < 10) {
//					loadTenMessages(selectedDatime);
//				}
			}
		});
	}

	// 上拉默认加载十条数据
	@Override
	public synchronized void loadTenMessages(final Date date) {
//		currentReloadTime++;
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
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getEpData().startsWith("0808")) {
						System.out.println("----------------epdata"
								+ list.get(i).getEpData());
						entites.add(list.get(i));
					}
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
//		if (currentNumberr < 10 && currentReloadTime < 10) {
//			loadTenMessages(selectedDatime);
//		} else {
//			currentNumberr = 0;
//			currentReloadTime = 0;
//		}
	}

}
