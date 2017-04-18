package cc.wulian.smarthomev5.fragment.house;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.sensorable.Sensorable;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.house.HouseKeeperConditionSelectDeviceActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperConditionSceneActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperConditionTimeActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionSelectDeviceFragment.ConditionDeviceListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionSceneFragment.SceneConditionListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionTimeFragment.ConditionTimeListener;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.view.SwipeTouchViewListener;

public class HouseKeeperConditionItem {

	protected BaseActivity mActivity;
	protected LayoutInflater inflater;
	protected Resources mResources;
	protected FrameLayout frameLayout;
	protected LinearLayout lineLayout;
	protected Button deleteButton;
	private LinearLayout itemLayout;
	private LinearLayout weekdayLayout;
	private LinearLayout detailLayout;

	private TextView conditionName;
	private TextView conditionStatus;
//	private TextView conditionWeekDay;
	
	private DeviceCache mDeviceCache ;
	
	private AutoConditionInfo info;
	private static final String REPEAT_ON = "1";
	private static final String SPLIT_MORE = ">";
	private static final String SPLIT_SPACE = " ";
	private static final String SPLIT_COMMA = ",";
	private String condition;
	
	public HouseKeeperConditionItem(BaseActivity mActivity,final AutoConditionInfo info){
		this.info = info;
		this.mActivity = mActivity;
//		mContext = context;
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
		conditionName = (TextView) lineLayout
				.findViewById(R.id.task_manager_trigger_item_name);
		conditionStatus = (TextView) lineLayout
				.findViewById(R.id.task_manager_trigger_item_status);
		weekdayLayout = (LinearLayout) lineLayout
				.findViewById(R.id.task_manager_trigger_item_weekday_layout);
//		conditionWeekDay = (TextView) lineLayout
//				.findViewById(R.id.task_manager_trigger_item_weekday);
		itemLayout.setOnTouchListener(new SwipeTouchViewListener(
				itemLayout, deleteButton));
		detailLayout = (LinearLayout) lineLayout
				.findViewById(R.id.task_manager_trigger_item_imv);
//		itemLayout.requestDisallowInterceptTouchEvent(true);
//		deleteButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				String deleteCondition;
//				if(StringUtil.equals(info.getType(), "0")){
//					deleteCondition =  info.getType() + "." + info.getObject() + " " +info.getExp();
//				}else if(StringUtil.equals(info.getType(), "1")){
//					deleteCondition =  info.getType() + "." + info.getObject() + " " +info.getExp();
//				}else{
//					deleteCondition =  info.getType() + "." + info.getObject() + " " +info.getExp().substring(0,1) + " " + info.getExp().substring(1);
//				}
//				HouseKeeperAddRulesFragment.autoProgramTaskInfo.deleteConditionTree(deleteCondition);
//				EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.REMOVE));
//			}
//		});
		initControlableView(info);
		initConditionItemView(info);
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
				condition = "condition";
				if(StringUtil.equals("0", info.getType())){
					final String oldStringTree = info.getType() + "." + info.getObject() + " " + info.getExp();
					HouseKeeperConditionSceneFragment.setSceneChooseListener(new SceneConditionListener() {
						
						@Override
						public void onSceneConditionChanged(String sceneID, String sceneCondition,
								String des) {
							if(sceneID == null || sceneCondition == null){
								
							}else{
								String addConditionString = "0" + "." + "CURSCENE" + " " + "in" + " " + "(" + sceneID + ")" ;
//								AutoConditionInfo newconditionInfo = new AutoConditionInfo();
//								newconditionInfo.setType("0");
//								newconditionInfo.setObject(sceneID);
//								newconditionInfo.setExp(sceneTrigger);
								HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateConditionTree(oldStringTree, addConditionString);
								HouseKeeperAddRulesFragment.isEditChange = true;
//								EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.MODIFY));
							}
						}
					});
					args.putString(HouseKeeperConditionSceneFragment.TRIGGER_OR_CONDITION, condition);
					args.putSerializable(
							HouseKeeperConditionSceneFragment.TRIGGER_INFO_SCENE_SERIAL,
							info);
					mActivity.JumpTo(HouseKeeperConditionSceneActivity.class, args);
				}else if(StringUtil.equals("1", info.getType())){
					final String oldStringTree = info.getType() + "." + info.getObject() + " " + info.getExp();
					HouseKeeperConditionTimeFragment.setConditionTimeListener(new ConditionTimeListener() {
						
						@Override
						public void onConditionTimeListenerChanged(String curtime, String time,String des) {
							if(curtime == null || time == null){
								
							}else{
//								AutoConditionInfo newconditionInfo = new AutoConditionInfo();
//								newconditionInfo.setType("1");
//								newconditionInfo.setObject(curtime);
//								newconditionInfo.setExp(time);
//								HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateCondition(info,newconditionInfo);
								String addConditionString = "1" + "." + curtime + " " + time;
								HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateConditionTree(oldStringTree, addConditionString);
								HouseKeeperAddRulesFragment.isEditChange = true;
//								EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.MODIFY));
							}
						}
					});
					args.putSerializable(
							HouseKeeperConditionTimeFragment.CONDITION_INFO_TIME_SERIAL,
							info);
					mActivity.JumpTo(HouseKeeperConditionTimeActivity.class, args);
				}else if(StringUtil.equals("2", info.getType())){
					final String oldStringTree = info.getType() + "." + info.getObject() + " " + info.getExp().substring(0, 1) + " " + info.getExp().substring(1);
					HouseKeeperConditionSelectDeviceFragment.setConditionDeviceListener(new ConditionDeviceListener() {
						
						@Override
						public void onConditionDeviceListenerChanged(String deviceData, String value,String des) {
							if(deviceData == null || value == null){
								
							}else{
//								AutoConditionInfo newconditionInfo = new AutoConditionInfo();
//								newconditionInfo.setType("2");
//								newconditionInfo.setObject(deviceData);
//								newconditionInfo.setExp(value);
//								newconditionInfo.setDes(des);
//								HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateTrigger(info,newconditionInfo);
								String addConditionString = "2" + "." + deviceData + " " + value.substring(0,1) + " " + value.substring(1) ;
								HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateConditionTree(oldStringTree, addConditionString);
								HouseKeeperAddRulesFragment.isEditChange = true;
//								EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.MODIFY));
							}
						}
					});
					args.putString(HouseKeeperConditionSelectDeviceFragment.TRIGGER_OR_CONDITION, condition);
					args.putSerializable(
							HouseKeeperConditionSelectDeviceFragment.TRIGGER_INFO_DEVICE_SERIAL,
							info);
					mActivity.JumpTo(HouseKeeperConditionSelectDeviceActivity.class, args);
				}
			}
		});
	}
	
	private void initConditionItemView(AutoConditionInfo info) {
		if (StringUtil.equals("0", info.getType())) {
			weekdayLayout.setVisibility(View.GONE);
			conditionStatus.setVisibility(View.GONE);
			String[] splits = info.getExp().split(SPLIT_SPACE);
			String sceneId;
			if(StringUtil.equals(splits[0], "in")){
				sceneId = splits[1].substring(1, splits[1].length() - 1);
			}else{
				sceneId = splits[2].substring(1, splits[2].length() - 1);
			}
			String conditionSceneText = "";
			if(sceneId != null){
				String[] sceneIdStr = sceneId.split(SPLIT_COMMA);
				for(int i = 0; i < sceneIdStr.length; i++){
					SceneInfo sceneInfo = MainApplication.getApplication().sceneInfoMap
							.get(AccountManager.getAccountManger().getmCurrentInfo()
									.getGwID() + sceneIdStr[i]);
					if(sceneInfo != null){
						if((i + 1) == sceneIdStr.length){
							conditionSceneText += sceneInfo.getName();
							break;
						}else{
							conditionSceneText += sceneInfo.getName() + "/";
						}
					}
				}
			}
			if(!StringUtil.isNullOrEmpty(conditionSceneText)){
				conditionName.setText(conditionSceneText);
			}else{
				conditionName.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_no_setting_scene));
			}
//			if(StringUtil.equals("on", info.getExp())){
//				conditionStatus.setText("激活");
//			}else{
//				conditionStatus.setText("反激活");
//			}
		} else if (StringUtil.equals("1", info.getType())) {
			//解析某个时刻，cron表达式
			weekdayLayout.setVisibility(View.VISIBLE);
			conditionStatus.setVisibility(View.GONE);
			//170024007F(晚上晚于5点)--->000008007F(早于早上8点)
			
			String[] exp = info.getExp().split(SPLIT_SPACE);
			String conditionTime = exp[1]; //in 85785702
			//170024007F
			String weekDays16 = conditionTime.substring(8,10);
			String weekDays = "";
			long weekDayslong = Long.parseLong(weekDays16,16);
			String weekDays2 = Long.toBinaryString(weekDayslong);
			if(weekDayslong != 127){
				weekDays2 = StringUtil.appendLeft(weekDays2, 8, '0');
				weekDays = weekDays2.substring(0, 2) + reverseIt(weekDays2.substring(2));
			}else{
				weekDays = "11111111";
			}
			int startHour = StringUtil.toInteger(conditionTime.substring(0, 2));
			int startMinute = StringUtil.toInteger(conditionTime.substring(2, 4));
			int endHour = 0;
			int endMinute = 0;
			int continute = StringUtil.toInteger(conditionTime.substring(4, 8));
			int continuteHour = continute / 60;
			int continuteMinute = continute % 60;
			String str = "";
			if(continuteMinute > (60 - startMinute)){
				endMinute = continuteMinute - (60 - startMinute);
				continuteHour = continuteHour + 1;
				if(continuteHour >= (24 - startHour)){
					str = "(+1D)";
					endHour = continuteHour - (24 - startHour);
				}else{
					endHour = continuteHour + startHour;
				}
			}else{
				endMinute = continuteMinute + startMinute;
				if(continuteHour > (24 - startHour)){
					str = "(+1D)";
					endHour = continuteHour - (24 - startHour);
				}else{
					endHour = continuteHour + startHour;
				}
			}
			if(endMinute == 60){
				endMinute = 0;
				endHour = endHour + 1;
			}
			if(endHour == 24){
				endHour = 0;
			}if(endHour > 24){
				endHour = endHour - 24;
				str = "(+1D)";
			}
			if(endHour == 0 && endMinute == 0){
				str = "(+1D)";
			}
			conditionName.setText(conditionTime.substring(0, 2) + ":" +  conditionTime.substring(2, 4) + "~" + 
					StringUtil.appendLeft(endHour + "", 2, '0') + ":" +  StringUtil.appendLeft(endMinute + "", 2, '0') + str);
			
			int[] mWeekValues = new int[] {
					R.string.house_condition_time_all_weekday,
				    R.string.Sunday,
					R.string.Monday,
					R.string.Tuesday,
					R.string.Wednesday,
					R.string.Thursday,
					R.string.Friday,
					R.string.Saturday };
			if(weekDays.substring(0, 1).equals(REPEAT_ON)){
				final TextView conditionWeekDay = new TextView(mActivity);
				conditionWeekDay.setText(mResources.getString(mWeekValues[0]));
				conditionWeekDay.setTextSize(15);
				conditionWeekDay.setTextColor(mResources.getColor(R.color.house_keeper_rule_text_color));
				conditionWeekDay.setPadding(10, 1, 10, 1);
				weekdayLayout.addView(conditionWeekDay);
			}else{
				for (int i = 1; i < weekDays.length(); i++) {
					if (weekDays.substring(i, i+1).equals(REPEAT_ON)) {
						final TextView conditionWeekDay = new TextView(mActivity);
						conditionWeekDay.setText(mResources.getString(mWeekValues[i])+" ");
						conditionWeekDay.setTextSize(15);
						conditionWeekDay.setTextColor(mResources.getColor(R.color.house_keeper_rule_text_color));
						conditionWeekDay.setPadding(10, 1, 10, 1);
						weekdayLayout.addView(conditionWeekDay);
					}
				}
			}
			
		} else if (StringUtil.equals("2", info.getType())) {
			//解析 报警设备以及检测类设备
			weekdayLayout.setVisibility(View.GONE);
			conditionStatus.setVisibility(View.VISIBLE);
			
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
			String symbol = exp.substring(0, 1);
			if(device != null){
				if(StringUtil.isNullOrEmpty(device.getDeviceName())){
					conditionName.setText(device.getDefaultDeviceName());
				}else{
					conditionName.setText(device.getDeviceName());
				}
				
				if(device instanceof Sensorable){
					if(device.getDeviceType().endsWith("a1")){
						conditionStatus.setText(device.parseDataWithProtocol(Integer.parseInt(value)+""));
					}else{
					
					String des = "";
					Sensorable sensor = (Sensorable) device;
					des = sensor.unit(ep, epType);
					if(StringUtil.equals(symbol, ">")){
						conditionStatus.setText(mResources.getString(R.string.house_rule_add_new_condition_item_symbol_more) + value + des);
					}else if(StringUtil.equals(symbol, "<")){
						conditionStatus.setText(mResources.getString(R.string.house_rule_add_new_condition_item_symbol_less) + value + des);
					}else if(StringUtil.equals(symbol, "=")){
						if(device instanceof Alarmable){
							Alarmable alarm  = (Alarmable)device;
							if(StringUtil.equals(alarm.getAlarmProtocol(), value)){
								conditionStatus.setText(alarm.getAlarmString());
							}else if(StringUtil.equals(alarm.getNormalProtocol(), value)){
								conditionStatus.setText(alarm.getNormalString());
							}
						}else{
							conditionStatus.setText(mResources.getString(R.string.house_rule_add_new_condition_item_symbol_equal) + value + des);
						}
						
					}
					
				}
				}else if(device instanceof Alarmable){
					Alarmable alarm  = (Alarmable)device;
					if(StringUtil.equals(symbol, "=")){
						if(StringUtil.equals(alarm.getAlarmProtocol(), value)){
							conditionStatus.setText(alarm.getAlarmString());
						}else if(StringUtil.equals(alarm.getNormalProtocol(), value)){
							conditionStatus.setText(alarm.getNormalString());
						}
					}
					
				}
			}else{
				conditionName.setText(mActivity.getResources().getString(R.string.house_rule_add_new_no_find_device));
			}
			
		}
	}

	public View getView() {
		return lineLayout;
	}
	
	//字符串反转
	 public static String reverseIt(String str){  
	        int i;  
	        int len=str.length();  
	        StringBuffer sb = new StringBuffer(len);  
	        for(i=(len-1);i>=0;i--){  
	            sb.append(str.charAt(i));  
	        }  
	        return sb.toString();  
	    }  
}
