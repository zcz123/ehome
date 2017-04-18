package cc.wulian.smarthomev5.fragment.house;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.house.HouseKeeperConditionSelectDeviceActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperTriggerSceneActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperConditionSceneActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperConditionTimeActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperTriggerTimeActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionSelectDeviceFragment.ConditionDeviceListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerSceneFragment.SceneChooseListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionSceneFragment.SceneConditionListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionTimeFragment.ConditionTimeListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerTimeFragment.TriggerTimeListener;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
/**
 * 管家中触发事件添加和限制条件添加的公共页, 包括不同的场景选择、设备和时间选择，都是通过相关回调接口实现数据的选择。
 * @author Administrator
 *
 */
public class HouseKeeperConditionFragment extends WulianFragment{
	
	public static final String TRIGGER_OR_CONDITION = "TRIGGER_OR_CONDITION";
	public static final String TREE_AND_OR = "TREE_AND_OR";
	private String condition;
	
	private TextView sceneItem;
	private TextView sceneItemRemind;
	private TextView deviceItem;
	private TextView deviceItemRemind;
	private TextView timeItem;
	
	private TextView timeItemRemind;
	private static ConditionListener conditionListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getActivity().getIntent().getExtras();
		condition = bundle.getString(TRIGGER_OR_CONDITION);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_manager_fragment_trigger_and_condition, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		sceneItem = (TextView) view.findViewById(R.id.house_keeper_task_condition_scene);
		deviceItem = (TextView) view.findViewById(R.id.house_keeper_task_condition_device);
		timeItem = (TextView) view.findViewById(R.id.house_keeper_task_condition_time);
		sceneItemRemind = (TextView) view.findViewById(R.id.house_keeper_task_condition_scene_remind);
		deviceItemRemind = (TextView) view.findViewById(R.id.house_keeper_task_condition_device_remind);
		timeItemRemind = (TextView) view.findViewById(R.id.house_keeper_task_condition_time_remind);
		if(StringUtil.equals(condition, "trigger")){
			timeItem.setText(R.string.house_rule_add_new_condition_moment);
			sceneItemRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_trigger_scene_text_remind));
			deviceItemRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_trigger_device_text_remind));
			timeItemRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_trigger_moment_remind));
		}
		else if(StringUtil.equals(condition, "condition")){
			timeItem.setText(R.string.house_rule_add_new_condition_time);
			sceneItemRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_scene_text_remind));
			deviceItemRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_device_text_remind));
			timeItemRemind.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_time_remind));
		}
		sceneItem.setOnClickListener(conditionClickListener);
		deviceItem.setOnClickListener(conditionClickListener);
		timeItem.setOnClickListener(conditionClickListener);
	}

	private OnClickListener conditionClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			if(v == sceneItem){
				if(StringUtil.equals(condition, "trigger")){
					HouseKeeperTriggerSceneFragment.setSceneChooseListener(new SceneChooseListener() {
						
						@Override
						public void onSceneChoseChanged(String sceneID, String sceneTrigger,String des) {
							if(conditionListener != null){
								if(sceneID == null || sceneTrigger == null){
									
								}else{
									mActivity.finish();
									conditionListener.onConditionListenerChanged("0", sceneID, sceneTrigger,des);
								}
							}
						}
					});
					intent.putExtra(HouseKeeperTriggerSceneFragment.TRIGGER_OR_CONDITION, condition);
					intent.setClass(mActivity, HouseKeeperTriggerSceneActivity.class);
				}
				else if(StringUtil.equals(condition, "condition")){
					HouseKeeperConditionSceneFragment.setSceneChooseListener(new SceneConditionListener() {
						
						@Override
						public void onSceneConditionChanged(String sceneID, String sceneCondition,
								String des) {
							if(conditionListener != null){
								if(sceneID == null || sceneCondition == null){
									
								}else{
									mActivity.finish();
									conditionListener.onConditionListenerChanged("0", sceneID, sceneCondition,des);
								}
							}
						}
					});
					
					intent.putExtra(HouseKeeperConditionSceneFragment.TRIGGER_OR_CONDITION, condition);
					intent.setClass(mActivity, HouseKeeperConditionSceneActivity.class);
				}
			}else if(v == deviceItem){
//				if(typeListener != null){
//					typeListener.onTypeChanged("2");
//				}
				HouseKeeperConditionSelectDeviceFragment.setConditionDeviceListener(new ConditionDeviceListener() {
					
					@Override
					public void onConditionDeviceListenerChanged(String deviceData, String value, String des) {
						if(conditionListener != null){
							if(StringUtil.isNullOrEmpty(deviceData) ||  StringUtil.isNullOrEmpty(value)){
								
							}else{
								mActivity.finish();
								conditionListener.onConditionListenerChanged("2", deviceData, value, des);
							}
						}
					}
				});
				intent.putExtra(HouseKeeperConditionSelectDeviceFragment.TRIGGER_OR_CONDITION, condition);
				intent.setClass(mActivity, HouseKeeperConditionSelectDeviceActivity.class);
			}else if(v == timeItem){
				if(StringUtil.equals(condition, "trigger")){
//					timeItem.setText(R.string.house_rule_add_new_condition_moment);
					
					HouseKeeperTriggerTimeFragment.setTriggerTimeListener(new TriggerTimeListener() {
						
						@Override
						public void onTriggerTimeListenerChanged(String curtime, String time,String des) {
							if(conditionListener != null){
								if(curtime == null || time == null){
									
								}else{
									mActivity.finish();
									conditionListener.onConditionListenerChanged("1", curtime, time,des);
								}
							}
						}
					});
					intent.setClass(mActivity, HouseKeeperTriggerTimeActivity.class);
				}
				else if(StringUtil.equals(condition, "condition")){
//					timeItem.setText(R.string.house_rule_add_new_condition_time);
					HouseKeeperConditionTimeFragment.setConditionTimeListener(new ConditionTimeListener() {
						
						@Override
						public void onConditionTimeListenerChanged(String curtime, String time,String des) {
							if(conditionListener != null){
								if(curtime == null || time == null){
									
								}else{
									mActivity.finish();
									conditionListener.onConditionListenerChanged("1", curtime, time, des);
								}
							}
						}
					});
					intent.setClass(mActivity, HouseKeeperConditionTimeActivity.class);
				}
			}
			mActivity.startActivity(intent);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
//		if(!StringUtil.equals(HouseKeeperAddRulesFragment.CURRENT_SHOW_FRAGMENT, CURRENT_SHOW_FRAGMENT_CONDITION)){
//			mActivity.finish();
//		}
	}

	@Override
	public void onShow() {
		super.onShow();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.house_rule_add_rule);
		if(StringUtil.equals(condition, "trigger")){
			getSupportActionBar().setTitle(R.string.house_rule_add_new_trigger_condition);
		}
		else if(StringUtil.equals(condition, "condition")){
			getSupportActionBar().setTitle(R.string.house_rule_add_new_limit_condition);
		}
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				mActivity.finish();
				if(conditionListener != null){
					conditionListener.onConditionListenerChanged(null, null, null,null);
				}
//				HouseKeeperAddRulesFragment.CURRENT_SHOW_FRAGMENT = HouseKeeperConditionFragment.CURRENT_SHOW_FRAGMENT_CONDITION;
			}
		});
	}
	
	public static void setConditionListener(ConditionListener conditionListener) {
		HouseKeeperConditionFragment.conditionListener = conditionListener;
	}


	public interface ConditionListener{
		public void onConditionListenerChanged(String type,String object,String exp,String des);
	}
}
