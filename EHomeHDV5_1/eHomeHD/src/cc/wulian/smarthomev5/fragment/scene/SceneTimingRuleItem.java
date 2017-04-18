package cc.wulian.smarthomev5.fragment.scene;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperTriggerTimeActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerTimeFragment;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerTimeFragment.TriggerTimeListener;
import cc.wulian.smarthomev5.view.SwipeTouchViewListener;

public class SceneTimingRuleItem {

	protected BaseActivity mActivity;
	protected LayoutInflater inflater;
	protected Resources mResources;
	protected FrameLayout frameLayout;
	protected LinearLayout lineLayout;
	protected Button deleteButton;
	private LinearLayout itemLayout;
	private LinearLayout weekdayLayout;
	private LinearLayout detailLayout;

	protected TextView triggerName;
	protected TextView triggerStatus;
//	protected TextView triggerWeekDay;
	
	private DeviceCache mDeviceCache ;

	private AutoConditionInfo info;
	private static final String SPLIT_REGULAR = ",";
	private static final String SPLIT_SPACE = " ";
	private static final String REPEAT_ON = "1";
	private static final String SPLIT_MORE = ">";
	private String condition;

	public SceneTimingRuleItem(BaseActivity mActivity, final AutoConditionInfo info) {
		this.mActivity = mActivity;
		this.info = info;
		inflater = LayoutInflater.from(mActivity);
		mResources = mActivity.getResources();
		mDeviceCache = DeviceCache.getInstance(mActivity);
		lineLayout = (LinearLayout) inflater.inflate(
				R.layout.task_manager_trigger_list_item, null);
		frameLayout = (FrameLayout) lineLayout
				.findViewById(R.id.task_manager_trigger_item_framelayout);
		deleteButton = (Button) lineLayout
				.findViewById(R.id.task_manager_trigger_item_delete);
		itemLayout = (LinearLayout) lineLayout
				.findViewById(R.id.task_manager_trigger_item_layout);
		triggerName = (TextView) lineLayout
				.findViewById(R.id.task_manager_trigger_item_name);
		triggerStatus = (TextView) lineLayout
				.findViewById(R.id.task_manager_trigger_item_status);
		weekdayLayout = (LinearLayout) lineLayout
				.findViewById(R.id.task_manager_trigger_item_weekday_layout);
		weekdayLayout.removeAllViews();
//		triggerWeekDay = (TextView) lineLayout
//				.findViewById(R.id.task_manager_trigger_item_weekday);
		detailLayout = (LinearLayout) lineLayout
				.findViewById(R.id.task_manager_trigger_item_imv);
		// triggerStatus.setVisibility(View.INVISIBLE);
		// triggerWeekDay.setVisibility(View.GONE);
//		deleteButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				HouseSceneTimingTaskFragment.autoProgramTaskInfo
//						.getTriggerList().remove(info);
//				EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.REMOVE));
//			}
//		});
//		lineLayout.getParent().requestDisallowInterceptTouchEvent(true);
		itemLayout.setOnTouchListener(new SwipeTouchViewListener(itemLayout,
				deleteButton));
		initTriggerItemView(info);
		initControlableView(info);
	}
	
	
	public Button getDeleteButton() {
		return deleteButton;
	}


	public void setDeleteButton(Button deleteButton) {
		this.deleteButton = deleteButton;
	}


	private void initControlableView(final AutoConditionInfo info) {
		detailLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Bundle args = new Bundle();
				condition = "scene";
				HouseKeeperTriggerTimeFragment.setTriggerTimeListener(new TriggerTimeListener() {
					
					@Override
					public void onTriggerTimeListenerChanged(String curtime, String time,String des) {
						if(curtime == null || time == null){
							
						}else{
							SceneTimingActionTaskFragment.isSaveTime = true;
							AutoConditionInfo newcTriggerInfo = new AutoConditionInfo();
							newcTriggerInfo.setType("1");
							newcTriggerInfo.setObject(curtime);
							newcTriggerInfo.setExp(time);
							SceneTimingActionTaskFragment.autoProgramTaskInfo.updateTrigger(info,newcTriggerInfo);
						}
					}
				});
				args.putString(HouseKeeperTriggerTimeFragment.TRIGGER_OR_CONDITION, condition);
				args.putSerializable(
						HouseKeeperTriggerTimeFragment.TRIGGER_INFO_TIME_SERIAL,
						info);
				mActivity.JumpTo(HouseKeeperTriggerTimeActivity.class, args);
			}
		});
	}

	private void initTriggerItemView(AutoConditionInfo info) {
		// 解析某个时刻，cron表达式
		weekdayLayout.setVisibility(View.VISIBLE);
		triggerStatus.setVisibility(View.GONE);
		
		String cronTime = info.getExp();
		String triggerWeekDays = "";
		String[] chooseTimes = cronTime.split(SPLIT_SPACE);
		String triggerTime = chooseTimes[1] + ":" + chooseTimes[0];
		String[] weeks = chooseTimes[4].split(SPLIT_REGULAR);
		String[] WeekDay = new String[]{"0","0","0","0","0","0","0"};
		for(int i = 0; i < weeks.length; i++){
			WeekDay[StringUtil.toInteger(weeks[i]) - 1] = "1";
		}
		int weekdayNum = 0;
		int[] mWeekValues = new int[] {
			    R.string.Sunday,
				R.string.Monday,
				R.string.Tuesday,
				R.string.Wednesday,
				R.string.Thursday,
				R.string.Friday,
				R.string.Saturday };
		for (int i = 0; i < WeekDay.length; i++) {
			if (WeekDay[i].equals(REPEAT_ON)) {
				weekdayNum++;
				final TextView triggerWeekDay = new TextView(mActivity);
				weekdayNum++;
				triggerWeekDay.setText(mResources.getString(mWeekValues[i])+" ");
				triggerWeekDay.setTextColor(mResources.getColor(R.color.house_keeper_rule_text_color));
				triggerWeekDay.setTextSize(15);
				triggerWeekDay.setPadding(10, 1, 10, 1);
				weekdayLayout.addView(triggerWeekDay);
			}
		}
		if(weekdayNum==0){
			final TextView triggerWeekDay = new TextView(mActivity);
			triggerWeekDay.setText(mResources.getString(R.string.scene_no_weekday_bind));
			triggerWeekDay.setTextColor(mResources.getColor(R.color.house_keeper_rule_text_color));
			triggerWeekDay.setTextSize(15);
			triggerWeekDay.setPadding(10, 1, 10, 1);
			weekdayLayout.addView(triggerWeekDay);
		}
		triggerName.setText(triggerTime);
		
	}

	public View getView() {
		return lineLayout;
	}

	public AutoConditionInfo getInfo() {
		return info;
	}

	public void setInfo(AutoConditionInfo info) {
		this.info = info;
	}

}
