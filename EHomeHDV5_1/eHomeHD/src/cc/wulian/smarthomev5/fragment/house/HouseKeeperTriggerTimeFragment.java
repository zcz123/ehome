package cc.wulian.smarthomev5.fragment.house;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.HouseKeeperEntity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

import com.yuantuo.customview.ui.WLToast;

public class HouseKeeperTriggerTimeFragment extends WulianFragment{

	private HouseKeeperTimingView timingTriggerView;
	public static final String TRIGGER_INFO_TIME_SERIAL = "trigger_info_time_serial";
	public static final String TRIGGER_OR_CONDITION = "trigger_or_condition";
	
	private String condition;
	private AutoConditionInfo triggerInfo;
	private AutoConditionInfo newTriggerInfo;
	
	private static final String SPLIT_SPACE = " ";
	private static final String SPLIT_REGULAR = ",";
	private static final String SPLIT_SEMICOLON = ":";
	protected View rootView;
	private Button ensureButton;
	private boolean isChooseWeekDay = true;
	
	
	private static TriggerTimeListener triggerTimeListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			condition = bundle.getString(TRIGGER_OR_CONDITION);
			triggerInfo = (AutoConditionInfo) bundle
					.getSerializable(TRIGGER_INFO_TIME_SERIAL);
		}
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		timingTriggerView = new HouseKeeperTimingView(mActivity);
		rootView = inflater.inflate(R.layout.task_manager_time_choose_view,null);
		LinearLayout timeLayout = (LinearLayout) rootView.findViewById(R.id.house_keeper_task_time_layout);
		ensureButton = (Button) rootView.findViewById(R.id.house_keeper_task_time_ensure);
		if(triggerInfo != null){
			String cronTime = triggerInfo.getExp();
			String triggerWeekDay = "";
			String[] splits = cronTime.split(SPLIT_SPACE);
			String triggerTime = splits[1] + SPLIT_SEMICOLON + splits[0];
			String[] weeks = splits[4].split(SPLIT_REGULAR);
			String[] WeekDay = new String[]{"0","0","0","0","0","0","0"};
			for(int i = 0; i < weeks.length; i++){
				WeekDay[StringUtil.toInteger(weeks[i]) - 1] = "1";
			}
			//[1,1,1,0,0,0,0]-->1110000
//			for(int j = 0; j < WeekDay.length; j++){
//				triggerWeekDay += WeekDay[j];//1110000-->1,1,1,0,0,0,0
//			}
			
			StringBuilder sb = new StringBuilder();
			int size = WeekDay.length;
			for (int i = 0; i < size; i++){
				sb.append(WeekDay[i]);
				if (i != size - 1) sb.append(SPLIT_REGULAR);
			}
			triggerWeekDay = sb.toString();
			
			HouseKeeperEntity entity = new HouseKeeperEntity();
			entity.setTime(triggerTime);
			entity.setWeekDay(triggerWeekDay);
			timingTriggerView = new HouseKeeperTimingView(mActivity);
			//解析某一时刻数据
			timingTriggerView.setmTimingTrigger(entity);
		}else{
			HouseKeeperEntity entity = new HouseKeeperEntity();
			timingTriggerView.setmTimingTrigger(entity);
		}
		
		timeLayout.addView(timingTriggerView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ensureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String time = getTriggerTime();
				if(isChooseWeekDay){
					if(triggerTimeListener != null){
						triggerTimeListener.onTriggerTimeListenerChanged("CURTIME", time,null);
					}
					mActivity.finish();
				}else{
					WLToast.showToast(mActivity, mActivity.getResources().getString(R.string.house_rule_add_new_condition_moment_no_weeday),WLToast.TOAST_SHORT);
				}
			}
		});
	}
	
	private String getTriggerTime(){
		String chooseTime = timingTriggerView.getTimingTrigger().getTime();
		String chooseWeekDay = timingTriggerView.getTimingTrigger().getWeekDay();
		String[] times = chooseTime.split(SPLIT_SEMICOLON);
		//不足两位的补全
		String hour = times[0];
		String minite = times[1];
		String[] week = chooseWeekDay.split(SPLIT_REGULAR);
		String weeks = "";
		for(int i =0; i < week.length; i++){
			if("1".equals(week[i])){
				weeks = weeks+ (i + 1);
			}
		}
		String triggerTimeWeeks = "";
		StringBuilder sb = new StringBuilder();
		for(int j = 0; j< weeks.length(); j++){
			sb.append(weeks.substring(j, j+1));
			if (j != weeks.length() - 1) sb.append(SPLIT_REGULAR);
		}
		triggerTimeWeeks = sb.toString();
		if("".equals(triggerTimeWeeks)){
			isChooseWeekDay = false;
		}else{
			isChooseWeekDay = true;
		}
		String triggerTime = minite + " " + hour + " " + "?" + " " + "*" + " " + triggerTimeWeeks;
		return triggerTime;
	}
	
	public void initBar(){
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(StringUtil.equals(condition, "scene")){
			getSupportActionBar().setIconText(R.string.scene_info_timing_scene);
		}else{
			getSupportActionBar().setIconText(R.string.house_rule_add_new_trigger_condition);
		}
		getSupportActionBar().setTitle(R.string.house_rule_add_new_condition_select_moment);
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				if(triggerTimeListener != null){
					triggerTimeListener.onTriggerTimeListenerChanged(null, null,null);
				}
				mActivity.finish();
			}
		});
	}
	
	
	public static void setTriggerTimeListener(TriggerTimeListener triggerTimeListener) {
		HouseKeeperTriggerTimeFragment.triggerTimeListener = triggerTimeListener;
	}


	public interface TriggerTimeListener{
		public void onTriggerTimeListenerChanged(String curtime,String time,String des);
	}
}
