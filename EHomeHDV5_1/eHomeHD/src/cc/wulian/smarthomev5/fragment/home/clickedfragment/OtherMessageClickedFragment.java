package cc.wulian.smarthomev5.fragment.home.clickedfragment;

/*
 * 单击首页其他信息模块，进入的其他信息详细模块
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.OtherMessageClickedAdapter;
import cc.wulian.smarthomev5.adapter.OtherMessageClickedAdapter.State;
import cc.wulian.smarthomev5.dao.MessageDao;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.event.MessageEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.utils.DateUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.KCalendar;
import com.yuantuo.customview.ui.KCalendar.OnCalendarClickListener;
import com.yuantuo.customview.ui.KCalendar.OnCalendarDateChangedListener;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

@SuppressWarnings("deprecation")
public class OtherMessageClickedFragment extends WulianFragment {

	@ViewInject(R.id.home_other_message_head_ll)
	private LinearLayout headLineLayout;
	private ListView otherListView;
	private OtherMessageClickedAdapter adapter;
	private Date selectedDatime;
	private List<Object> DateList = new ArrayList<Object>();
	private MessageDao messageDao = MessageDao.getInstance();
	private TextView mHomeMessageSelectTime;
	private TextView mMonthText;
	private LinearLayout mHomeOtherDeletell;
	private WLDialog dialog;
	private PopupWindows popupWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectedDatime = new Date();
		adapter = new OtherMessageClickedAdapter(getActivity(),
				getMessages(selectedDatime));

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_other_message_click,
				container, false);
		LinearLayout mOthermessagell = (LinearLayout) view
				.findViewById(R.id.choose_date_background);
		mOthermessagell.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.light_green_fragment));
		mMonthText = (TextView) view.findViewById(R.id.month_text);
		mMonthText.setText((selectedDatime.getMonth()+1)
				+ getResources().getString(R.string.home_alarm_message_month));
		mHomeMessageSelectTime = (TextView) view
				.findViewById(R.id.home_message_selectTime);
		mHomeOtherDeletell = (LinearLayout) view
				.findViewById(R.id.home_other_delete_item);
		ViewUtils.inject(this, view);
		for (int i = 0; i < 6; i++) {
			final LinearLayout itemLineLayout = (LinearLayout) inflater
					.inflate(R.layout.home_other_message_date_time_item, null);
			TextView dayTextView = (TextView) itemLineLayout
					.findViewById(R.id.home_other_message_item_day_tv);
			LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			itemParams.setMargins(10, 0, 10, 0);
			itemLineLayout.setLayoutParams(itemParams);
			if (i == 0) {
				itemLineLayout.setSelected(true);
			}
			if (i == 5) {
				DateList.add(new Object());
				dayTextView.setText("...");
				headLineLayout.addView(itemLineLayout);
				itemLineLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(popupWindow == null){
							popupWindow = new PopupWindows(getActivity());
						}
						popupWindow.show(v);
					}
				});
				break;
			}
			final Date day = DateUtil.getDateBefore(selectedDatime, i);
			DateList.add(day);
			dayTextView.setText(day.getDate() + "");
			itemLineLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					selectedDatime = day;
					setSelectedDay();
					mHomeMessageSelectTime.setText(DateUtil.getFormatSimpleDate(selectedDatime));
					mMonthText.setText(selectedDatime.getMonth() + getResources().getString(R.string.home_alarm_message_month));
					adapter.swapData(getMessages(selectedDatime));
				}

			});
			headLineLayout.addView(itemLineLayout);
		}
		initBar();
		return view;
	}

	private void setSelectedDay() {

		for (int i = 0; i < headLineLayout.getChildCount(); i++) {
			headLineLayout.getChildAt(i).setSelected(false);
		}
		int j = 0;
		for (j = 0; j < DateList.size(); j++) {
			Object dayObject = DateList.get(j);
			if (dayObject instanceof Date) {
				Date dayShow = (Date) dayObject;
				if (dayShow.getYear() == selectedDatime.getYear()
						&& dayShow.getMonth() == selectedDatime
								.getMonth()
						&& dayShow.getDate() == selectedDatime
								.getDate()) {
					headLineLayout.getChildAt(j).setSelected(true);
					break;
				} else {
					headLineLayout.getChildAt(j).setSelected(false);
				}
			}
		}
		if (j == DateList.size()) {
			headLineLayout.getChildAt(5).setSelected(true);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		otherListView = (ListView) view.findViewById(R.id.home_other_list);
		otherListView.setAdapter(adapter);
		otherListView.setDividerHeight(0);

		otherListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				adapter.toggleEditMode();
				mHomeOtherDeletell.setVisibility(View.VISIBLE);

				iniChangeBar();

				return true;
			}
		});

		otherListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				State state = adapter.getState(position);
				state.setDeleted(!state.isDeleted());
				adapter.notifyDataSetChanged();
			}
		});

		mHomeOtherDeletell.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				deleteMessageDialog();
			}
		});
	}

	private void deleteMessageDialog() {
		WLDialog.Builder builder = new Builder(this.getActivity());
		builder.setContentView(R.layout.fragment_message_select_delete)
				.setTitle(R.string.device_config_edit_dev_area_create_item_delete)
				.setPositiveButton(R.string.common_ok)
				.setNegativeButton(R.string.cancel)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {

						String ids = adapter.getSelectedIds();
						if (!StringUtil.isNullOrEmpty(ids)) {
							MessageEventEntity entity = new MessageEventEntity();
							entity.setGwID(mAccountManger.getmCurrentInfo()
									.getGwID());
							entity.setMsgID(ids);
							messageDao.delete(entity);
						}
						List<MessageEventEntity> messages = getMessages(selectedDatime
								);
						adapter.swapData(messages);
						adapter.clearState();
						initBar();
						adapter.setEditMode(false);
						mHomeOtherDeletell.setVisibility(View.GONE);
					}

					public void onClickNegative(View contentViewLayout) {

					}

				});
		dialog = builder.create();
		dialog.show();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.home_other_message));
		getSupportActionBar().setIconText(R.string.nav_home_title);
	}

	private void iniChangeBar() {
		getSupportActionBar().setDisplayIconEnabled(false);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.cancel));
		getSupportActionBar().setRightIconText(
				getResources().getString(
						R.string.home_message_check_all_warn));
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {
					@Override
					public void onClick(View v) {
						adapter.setAllSelect(false);
						adapter.setEditMode(false);
						mHomeOtherDeletell.setVisibility(View.GONE);
						initBar();
					}
				});
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						adapter.setAllSelect(true);
					}
				});
	}

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
			setBackgroundDrawable(new ColorDrawable(R.color.white));
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
					mHomeMessageSelectTime.setText(selectDayFormat
							.format(selectedDatime));
					mMonthText.setText((selectedDatime.getMonth() + 1)+ getResources().getString(R.string.home_alarm_message_month));
					adapter.swapData(getMessages(selectedDatime));
					setSelectedDay();
					dismiss();
				}
			});
			
		}
		public void show( View parent){
			showAtLocation(parent, Gravity.CENTER, 0, 0);
		}
	}

	/**
	 * Listview的消息注入与显示
	 */
	@Override
	public void onResume() {
		super.onResume();
	}
	private List<MessageEventEntity> getMessages(Date date) {
		MessageEventEntity messageEventEntity = new MessageEventEntity();
		messageEventEntity.setGwID(mAccountManger.getmCurrentInfo().getGwID());
		String timeStr = String.valueOf(date.getTime());
		messageEventEntity.setTime(timeStr);
		messageEventEntity.setType("'" + Messages.TYPE_DEV_SENSOR_DATA + "'"
				+ "," + "'" + Messages.TYPE_SCENE_OPERATION + "'"+","+ "'"+ Messages.TYPE_DEV_LOW_POWER + "'"+","+ "'"+ Messages.TYPE_DEV_ONLINE + "'"+","+ "'"+ Messages.TYPE_SCENE_OPERATION + "'");
		return messageDao.findListAll(messageEventEntity);
	}
	public void onEventMainThread(MessageEvent event) {
		if(Messages.TYPE_DEV_SENSOR_DATA.equals(event.action) || Messages.TYPE_DEV_LOW_POWER.equals(event.action) || Messages.TYPE_DEV_ONLINE.equals(event.action) || Messages.TYPE_SCENE_OPERATION.equals(event.action)){
			List<MessageEventEntity> messages = getMessages(selectedDatime);
			adapter.swapData(messages);
		}
	}

	
}