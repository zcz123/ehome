package cc.wulian.smarthomev5.fragment.house;

import java.util.Calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.cooker.ElectricCookerTimeView;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class HouseKeeperConditionTimeFragment extends WulianFragment{

	public static final String CONDITION_INFO_TIME_SERIAL = "condition_info_time_serial";
	public static final String TRIGGER_OR_CONDITION = "trigger_or_condition";
	
	private static final String SPLIT_SEMICOLON = ":";
	private static final String SPLIT_SPLACE = " ";
	
	private ElectricCookerTimeView timingSettingView;
	private HouseKeeperRepeatWeekDayView weekDayView;
	private TextView startTime;
	private TextView endTime;
	private TextView endTimeRemind;
	private WLDialog dialog;
	private String selectTime;
	private Button ensureChooseTime;
	private AutoConditionInfo conditionInfo;
	private String condition;
	private String weekDays = "";
	
	private int starthour;
	private int startminite;
	private int endhour;
	private int endminite;
	
	private boolean isChooseWeekDay = true;
	
	private static ConditionTimeListener conditionTimeListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getActivity().getIntent().getExtras();
		if (bundle != null) {
			condition = bundle.getString(TRIGGER_OR_CONDITION);
			conditionInfo = (AutoConditionInfo) bundle
					.getSerializable(CONDITION_INFO_TIME_SERIAL);
		}
		initBar();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_manager_fragment_choose_time, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		startTime = (TextView) view.findViewById(R.id.house_keeper_task_condition_time_start);
		endTime = (TextView) view.findViewById(R.id.house_keeper_task_condition_time_end);
		endTimeRemind = (TextView) view.findViewById(R.id.house_rule_add_new_condition_select_time_text);
		weekDayView = (HouseKeeperRepeatWeekDayView) view.findViewById(R.id.house_keeper_choose_weekday);
		startTime.setOnClickListener(timeListener);
		endTime.setOnClickListener(timeListener);
		weekDayView.setOnRepeatWeekChangedListener(chooseWeekDaylistener);
		ensureChooseTime = (Button) view.findViewById(R.id.house_keeper_task_scene_ensure);
		initTimeChooseSceneView();
		ensureChooseTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String time = getConditionTime();
				if(isChooseWeekDay){
					if(conditionTimeListener != null){
						conditionTimeListener.onConditionTimeListenerChanged("CURTIME", time, null);
					}
					mActivity.finish();
				}else{
					WLToast.showToast(mActivity, mActivity.getResources().getString(R.string.house_rule_add_new_condition_moment_no_weeday),WLToast.TOAST_SHORT);
//					WLToast.showToast(mActivity, mActivity.getResources().getString(R.string.hint_not_null_edittext),WLToast.TOAST_SHORT);
				}
			}
		});
	}
	
	private String getConditionTime(){
		String chooseStratTime = startTime.getText().toString();
		String[] splitsStratTime = chooseStratTime.split(SPLIT_SEMICOLON);
		String chooseEndTime = endTime.getText().toString();
		String[] splitsEndTime = chooseEndTime.split(SPLIT_SEMICOLON);
		String stratTime = StringUtil.appendLeft(splitsStratTime[0], 2, '0') + StringUtil.appendLeft(splitsStratTime[1], 2, '0');
		String endTime = StringUtil.appendLeft(splitsEndTime[0], 2, '0') + StringUtil.appendLeft(splitsEndTime[1], 2, '0');
//		if(StringUtil.toInteger(splitsStratTime[0]) > StringUtil.toInteger(splitsEndTime[0]) ||(StringUtil.toInteger(splitsStratTime[0]) == StringUtil.toInteger(splitsEndTime[0])) 
//				&& StringUtil.toInteger(splitsStratTime[1]) > StringUtil.toInteger(splitsEndTime[1])){
//			endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end));
//		}else{
//			endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end_tomorrow));
//		}
		int startTimeHour = StringUtil.toInteger(splitsStratTime[0]);
		int startTimeMinute = StringUtil.toInteger(splitsStratTime[1]);
		int endTimeHour = StringUtil.toInteger(splitsEndTime[0]);
		int endTimeMinute = StringUtil.toInteger(splitsEndTime[1]);
		int continuedTimeHouur = 0;
		int continuedTimeMinute = 0;
		
		if(startTimeHour == endTimeHour){
			if(startTimeMinute < endTimeMinute){
				continuedTimeMinute = endTimeMinute - startTimeMinute;
				continuedTimeHouur = 0;
			}else{
				continuedTimeMinute = 60 - startTimeMinute + endTimeMinute;
				continuedTimeHouur = 24 - startTimeHour + endTimeHour;
				continuedTimeHouur = continuedTimeHouur - 1;
			}
		}else{
			if(startTimeHour < endTimeHour){
				continuedTimeHouur = endTimeHour - startTimeHour;
			}else{
				continuedTimeHouur = 24 - startTimeHour + endTimeHour;
			}
			if(startTimeMinute < endTimeMinute){
				continuedTimeMinute = endTimeMinute - startTimeMinute;
			}else{
				continuedTimeMinute = 60 - startTimeMinute + endTimeMinute;
				continuedTimeHouur = continuedTimeHouur - 1;
			}
		}
		String continuedTime = StringUtil.appendLeft((continuedTimeHouur * 60 + continuedTimeMinute) + "", 4, '0');
		
		String condition = null;
		if(StringUtil.equals(weekDays, "00000000") || StringUtil.equals(weekDays, "")){
			isChooseWeekDay = false;
		}else{
			isChooseWeekDay = true;
			String ChooseWeekDays = weekDays.substring(1, 8);
			ChooseWeekDays = ChooseWeekDays.substring(0, 1) + HouseKeeperConditionItem.reverseIt(ChooseWeekDays.substring(1, 7));
			String WeekDay = Long.toHexString(Long.parseLong(ChooseWeekDays,2));
			condition = "in" + " " + stratTime + continuedTime + StringUtil.appendLeft(WeekDay, 2, '0');
		}
		
		return condition;
	}

	private void initTimeChooseSceneView() {
		if(conditionInfo != null){
			String[] condition = conditionInfo.getExp().split(SPLIT_SPLACE);//in 0923092302
			String conditionTime = "";
			if(StringUtil.equals(condition[0], "in")){
				conditionTime = condition[1];
			}else{
				conditionTime = condition[2];
			}
			
			int startHour = StringUtil.toInteger(conditionTime.substring(0, 2));
			int startMinute = StringUtil.toInteger(conditionTime.substring(2, 4));
			int endHour = 0;
			int endMinute = 0;
			int continute = StringUtil.toInteger(conditionTime.substring(4, 8));
			int continuteHour = continute / 60;
			int continuteMinute = continute % 60;
			
			if(continuteMinute > (60 - startMinute)){
				endMinute = continuteMinute - (60 - startMinute);
				continuteHour = continuteHour + 1;
				if(continuteHour >= (24 - startHour)){
					endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end_tomorrow));
					endHour = continuteHour - (24 - startHour);
				}else{
					endHour = continuteHour + startHour;
					endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end));
				}
			}else{
				endMinute = continuteMinute + startMinute;
				if(continuteHour > (24 - startHour)){
					endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end_tomorrow));
					endHour = continuteHour - (24 - startHour);
				}else{
					endHour = continuteHour + startHour;
					endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end));
				}
			}
			if(endMinute == 60){
				endMinute = 0;
				endHour = endHour + 1;
			}
			if(endHour == 24){
				endHour = 0;
			}else if(endHour > 24){
				endHour = endHour - 24;
				endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end_tomorrow));
			}
			if(endHour == 0 && endMinute == 0){
				endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end_tomorrow));
			}
			//170024007F
			String weekDays16 = conditionTime.substring(8);
			
			long weekDayslong=Long.parseLong(weekDays16,16);
			String weekDays2 = Long.toBinaryString(weekDayslong);
			if(StringUtil.equals(weekDays2, "1111111")){
				weekDays = "11111111";
			}else{
				weekDays= StringUtil.appendLeft(weekDays2, 8, '0');//--->0111111
				weekDays = weekDays.substring(0, 2) + HouseKeeperConditionItem.reverseIt(weekDays.substring(2));
			}
			startTime.setText(StringUtil.appendLeft(startHour + "", 2, '0') + ":" + StringUtil.appendLeft(startMinute + "", 2, '0'));
			endTime.setText(StringUtil.appendLeft(endHour + "", 2, '0')+ ":" + StringUtil.appendLeft(endMinute + "", 2, '0'));
			initWeek(weekDays);
		}else{
			startTime.setText(StringUtil.appendLeft(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) +"", 2, '0') 
					+ ":" + StringUtil.appendLeft(Calendar.getInstance().get(Calendar.MINUTE) + "", 2, '0'));
			endTime.setText(StringUtil.appendLeft(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "", 2, '0')
					+ ":" +StringUtil.appendLeft(Calendar.getInstance().get(Calendar.MINUTE) + "", 2, '0'));
			initWeek(weekDays);
		}
	}

	private void initWeek(String newValue) {
		if (!StringUtil.isNullOrEmpty((String) newValue)) {
			weekDayView.setRepeatWeekDay((String) newValue);
		}
		else {
			weekDayView.setRepeatWeekDayDefault();
		}
	}
	private final HouseKeeperRepeatWeekDayView.OnRepeatWeekChangedListener chooseWeekDaylistener = new HouseKeeperRepeatWeekDayView.OnRepeatWeekChangedListener() {
		
		@Override
		public void onWeekDayChanged(HouseKeeperRepeatWeekDayView weekView,
				String weekDay) {
			//获取7F-->解析成二进制，然后补成8位后，前两位不变，后六位倒叙排列
			//更新相应的界面
			
			String oldWeek = weekDays;
			if (!TextUtils.equals(oldWeek, weekDay)) {
				weekDays = weekDay;
				weekDayView.setRepeatWeekDay(weekDays);
			}
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		initTimeChooseSceneView();
	}
	private OnClickListener timeListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == startTime){
				showChooseTimeDialog(startTime);
			}else if(v == endTime){
				showChooseTimeDialog(endTime);
			}else if(v == ensureChooseTime){
				mActivity.finish();
			}
		}
	};
	
	
	private void showChooseTimeDialog(final View v){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(R.string.gateway_dream_flower_time_show_select_time);
		builder.setContentView(createViewTime());
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
				
				if(v == startTime){
					starthour = timingSettingView.getSettingHourTime();
					startminite =timingSettingView.getSettingMinuesTime(); 
					selectTime = StringUtil.appendLeft(starthour + "", 2, '0') + ":" + StringUtil.appendLeft(startminite + "", 2, '0');
					
					String chooseEndTime = endTime.getText().toString();
					String[] splitsEndTime = chooseEndTime.split(SPLIT_SEMICOLON);
					if(StringUtil.toInteger(splitsEndTime[0]) > starthour || 
							(StringUtil.toInteger(splitsEndTime[0]) == starthour && StringUtil.toInteger(splitsEndTime[1]) > startminite)){
						endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end));
					}else{
						endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end_tomorrow));
					}
					
					startTime.setText(selectTime);
				}else if(v == endTime){
					endhour = timingSettingView.getSettingHourTime();
					endminite = timingSettingView.getSettingMinuesTime();
					selectTime = StringUtil.appendLeft(endhour + "", 2, '0') + ":" + StringUtil.appendLeft(endminite + "", 2, '0');
//					if(endhour <  starthour || (endhour == starthour && endminite < startminite)){
//						WLToast.showToast(mActivity, "结束时间不能小于开始时间",WLToast.TOAST_SHORT);
//					}
					String chooseStratTime = startTime.getText().toString();
					String[] splitsStratTime = chooseStratTime.split(SPLIT_SEMICOLON);
					if(endhour > StringUtil.toInteger(splitsStratTime[0]) || 
							(endhour == StringUtil.toInteger(splitsStratTime[0]) && endminite > StringUtil.toInteger(splitsStratTime[1]))){
						endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end));
					}else{
						endTimeRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_select_time_end_tomorrow));
					}
					endTime.setText(selectTime);
				}
			}
			@Override
			public void onClickNegative(View contentViewLayout) {
				dialog.dismiss();
			}
		});

		dialog = builder.create();
		dialog.show();
	}
	
	
	protected View createViewTime() {
		timingSettingView = new ElectricCookerTimeView(mActivity);
		return timingSettingView;
	}
	
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.house_rule_add_new_limit_condition);
		getSupportActionBar().setTitle(R.string.house_rule_add_new_condition_select_time);
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				if(conditionTimeListener != null){
					conditionTimeListener.onConditionTimeListenerChanged(null, null, null);
				}
				mActivity.finish();
			}
		});
	}
	
	
	public static void setConditionTimeListener(ConditionTimeListener conditionTimeListener) {
		HouseKeeperConditionTimeFragment.conditionTimeListener = conditionTimeListener;
	}


	public interface ConditionTimeListener{
		public void onConditionTimeListenerChanged(String curtime,String time,String des);
	}
}
