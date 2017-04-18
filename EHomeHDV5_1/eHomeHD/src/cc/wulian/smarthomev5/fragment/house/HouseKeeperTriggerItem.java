package cc.wulian.smarthomev5.fragment.house;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.alarmable.WL_03_Door_Window_Sensors;
import cc.wulian.app.model.device.impls.sensorable.Sensorable;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.house.HouseKeeperConditionSelectDeviceActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperTriggerSceneActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperTriggerTimeActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionSelectDeviceFragment.ConditionDeviceListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerSceneFragment.SceneChooseListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerTimeFragment.TriggerTimeListener;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.view.SwipeTouchViewListener;

public class HouseKeeperTriggerItem {

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

	public HouseKeeperTriggerItem(BaseActivity mActivity, final AutoConditionInfo info) {
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
//				HouseKeeperAddRulesFragment.autoProgramTaskInfo
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
				condition = "trigger";
				if(StringUtil.equals("0", info.getType())){
					HouseKeeperTriggerSceneFragment.setSceneChooseListener(new SceneChooseListener() {
						
						@Override
						public void onSceneChoseChanged(String sceneID, String sceneTrigger,String des) {
							if(sceneID == null || sceneTrigger == null){
								
							}else{
								AutoConditionInfo newcTriggerInfo = new AutoConditionInfo();
								newcTriggerInfo.setType("0");
								newcTriggerInfo.setObject(sceneID);
								newcTriggerInfo.setExp(sceneTrigger);
								HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateTrigger(info,newcTriggerInfo);
								HouseKeeperAddRulesFragment.isEditChange = true;
							}
						}
					});
					args.putString(HouseKeeperTriggerSceneFragment.TRIGGER_OR_CONDITION, condition);
					args.putSerializable(
							HouseKeeperTriggerSceneFragment.TRIGGER_INFO_SCENE_SERIAL,
							info);
					mActivity.JumpTo(HouseKeeperTriggerSceneActivity.class, args);
				}else if(StringUtil.equals("1", info.getType())){
					HouseKeeperTriggerTimeFragment.setTriggerTimeListener(new TriggerTimeListener() {
						
						@Override
						public void onTriggerTimeListenerChanged(String curtime, String time,String des) {
							if(curtime == null || time == null){
								
							}else{
								AutoConditionInfo newcTriggerInfo = new AutoConditionInfo();
								newcTriggerInfo.setType("1");
								newcTriggerInfo.setObject(curtime);
								newcTriggerInfo.setExp(time);
								HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateTrigger(info,newcTriggerInfo);
								HouseKeeperAddRulesFragment.isEditChange = true;
							}
						}
					});
					args.putSerializable(
							HouseKeeperTriggerTimeFragment.TRIGGER_INFO_TIME_SERIAL,
							info);
					mActivity.JumpTo(HouseKeeperTriggerTimeActivity.class, args);
				}else if(StringUtil.equals("2", info.getType())){
					HouseKeeperConditionSelectDeviceFragment.setConditionDeviceListener(new ConditionDeviceListener() {
						
						@Override
						public void onConditionDeviceListenerChanged(String deviceData, String value,String des) {
							if(deviceData == null || value == null){
								
							}else{
								AutoConditionInfo newcTriggerInfo = new AutoConditionInfo();
								newcTriggerInfo.setType("2");
								newcTriggerInfo.setObject(deviceData);
								newcTriggerInfo.setExp(value);
								HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateTrigger(info,newcTriggerInfo);
								HouseKeeperAddRulesFragment.isEditChange = true;
							}
						}
					});
					args.putSerializable(
							HouseKeeperConditionSelectDeviceFragment.TRIGGER_INFO_DEVICE_SERIAL,
							info);
					args.putString(HouseKeeperConditionSelectDeviceFragment.TRIGGER_OR_CONDITION, condition);
					mActivity.JumpTo(HouseKeeperConditionSelectDeviceActivity.class, args);
				}
			}
		});
	}

	private void initTriggerItemView(AutoConditionInfo info) {
		if (StringUtil.equals("0", info.getType())) {
			SceneInfo sceneInfo = MainApplication.getApplication().sceneInfoMap
					.get(AccountManager.getAccountManger().getmCurrentInfo()
							.getGwID() + info.getObject());
			weekdayLayout.setVisibility(View.GONE);
			triggerStatus.setVisibility(View.VISIBLE);
			if (StringUtil.equals("on", info.getExp())) {
				triggerStatus.setText(mResources.getString(R.string.house_rule_add_new_condition_scene_trigger));
			} else {
				triggerStatus.setText(mResources.getString(R.string.house_rule_add_new_condition_scene_no_trigger));
			}
			if(sceneInfo != null){
				triggerName.setText(sceneInfo.getName());
			}else{
				triggerStatus.setVisibility(View.GONE);
				triggerName.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_no_setting_scene));
			}
		} else if (StringUtil.equals("1", info.getType())) {
			// 解析某个时刻，cron表达式
			weekdayLayout.setVisibility(View.VISIBLE);
			triggerStatus.setVisibility(View.GONE);
			
			String cronTime = info.getExp();
			String triggerWeekDays = "";
			String[] chooseTimes = cronTime.split(SPLIT_SPACE);
			String triggerTime = chooseTimes[1] + ":" + chooseTimes[0];
			String[] weeks = chooseTimes[4].split(SPLIT_REGULAR);
			String[] WeekDay = new String[]{"0","0","0","0","0","0","0"};
			if(weeks.length == 7){
				TextView triggerWeekDay = new TextView(mActivity);
				triggerWeekDay.setText(mResources.getString(R.string.house_condition_time_all_weekday));
				triggerWeekDay.setTextSize(15);
				triggerWeekDay.setTextColor(mResources.getColor(R.color.house_keeper_rule_text_color));
				triggerWeekDay.setPadding(10, 1, 10, 1);
				weekdayLayout.addView(triggerWeekDay);
			}else{
				for(int i = 0; i < weeks.length; i++){
					WeekDay[StringUtil.toInteger(weeks[i]) - 1] = "1";
				}
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
						TextView triggerWeekDay = new TextView(mActivity);
						triggerWeekDay.setText(mResources.getString(mWeekValues[i])+" ");
						triggerWeekDay.setTextSize(15);
						triggerWeekDay.setTextColor(mResources.getColor(R.color.house_keeper_rule_text_color));
						triggerWeekDay.setPadding(10, 1, 10, 1);
						weekdayLayout.addView(triggerWeekDay);
					}
				}
			}
			triggerName.setText(triggerTime);
			
		} else if (StringUtil.equals("2", info.getType())) {
			// 解析 报警设备以及检测类设备
			weekdayLayout.setVisibility(View.GONE);
			triggerStatus.setVisibility(View.VISIBLE);
			
			String type = info.getObject();
			String[] deviceDatas = type.split(SPLIT_MORE);
			String deviceId = deviceDatas[0];
			String ep = deviceDatas[2];
			String epType = "";
			WulianDevice device = mDeviceCache.getDeviceByID(mActivity, AccountManager.getAccountManger().getmCurrentInfo()
					.getGwID(), deviceId);
			if(deviceDatas.length > 3){
				epType = deviceDatas[3];
			}
			
			String exp = info.getExp();
			String value = "";
			if(StringUtil.equals(exp.substring(exp.length()-1), "$")){
				value = exp.substring(1,exp.length()-1);
			}else{
				value = exp.substring(1);
			}
//			String value = exp.substring(1);
			String symbol = exp.substring(0, 1);
			String epNumber="";
			if(device != null){
				if(!TextUtils.isEmpty(ep)&&device.getDeviceInfo().getDeviceEPInfoMap()!=null&&device.getDeviceInfo().getDeviceEPInfoMap().size()>1){
					epNumber=getEpNumberString(ep);
				}
				if(StringUtil.isNullOrEmpty(device.getDeviceName())){
					triggerName.setText(getTriggerName(device.getDefaultDeviceName(),epNumber));
				}else{
					triggerName.setText(getTriggerName(device.getDeviceName(),epNumber));
				}
				if(device instanceof Sensorable){
					String des = "";
					Sensorable sensor = (Sensorable) device;
					des = sensor.unit(ep, epType);
					if(StringUtil.equals(symbol, ">")){
						triggerStatus.setText(mResources.getString(R.string.house_rule_add_new_condition_item_symbol_more) + value + des);
					}else if(StringUtil.equals(symbol, "<")){
						triggerStatus.setText(mResources.getString(R.string.house_rule_add_new_condition_item_symbol_less) + value + des);
					}else if(StringUtil.equals(symbol, "=")){
						if(device instanceof Alarmable){
							Alarmable alarm  = (Alarmable)device;
							if(StringUtil.equals(alarm.getAlarmProtocol(), value)){
								triggerStatus.setText(alarm.getAlarmString());
							}else if(StringUtil.equals(alarm.getNormalProtocol(), value)){
								triggerStatus.setText(alarm.getNormalString());
							}
						}
							
					}else{
						triggerStatus.setText(mResources.getString(R.string.house_rule_add_new_condition_item_symbol_equal) + value + des);
					}
				}else if(device instanceof Alarmable){
					Alarmable alarm  = (Alarmable)device;
					if(StringUtil.equals(alarm.getAlarmProtocol(), value)){
						triggerStatus.setText(alarm.getAlarmString());
					}else if(StringUtil.equals(alarm.getNormalProtocol(), value)){
						triggerStatus.setText(alarm.getNormalString());
					}
				}
			}else{
				triggerName.setText(mActivity.getResources().getString(R.string.house_rule_add_new_no_find_device));
			}
		}
	}
	//增加端口显示
	private String getTriggerName(String deviceName, String epNumber) {
		// TODO Auto-generated method stub
		return deviceName+"   "+epNumber;
	}

	private String getEpNumberString(String ep) {
		// TODO Auto-generated method stub
		return (Integer.parseInt(ep)-13)+"";
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
