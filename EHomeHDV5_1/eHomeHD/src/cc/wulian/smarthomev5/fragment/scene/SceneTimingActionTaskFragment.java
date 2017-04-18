package cc.wulian.smarthomev5.fragment.scene;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.house.HouseKeeperTriggerTimeActivity;
import cc.wulian.smarthomev5.databases.entitys.AutoTask;
import cc.wulian.smarthomev5.event.TimingSceneEvent;
import cc.wulian.smarthomev5.fragment.house.AutoProgramTaskManager;
import cc.wulian.smarthomev5.fragment.house.AutoTaskEvent;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerItem;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerTimeFragment;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerTimeFragment.TriggerTimeListener;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.CmdUtil;
import de.greenrobot.event.EventBus;

public class SceneTimingActionTaskFragment extends WulianFragment{

	private AutoProgramTaskManager autoProgramTaskManager = AutoProgramTaskManager
			.getInstance();
	public static final String SCENE_INFO_TIMING = "scene_info_timing";
	private LinearLayout sceneTimingLayout;
	public static AutoProgramTaskInfo autoProgramTaskInfo;
	private Preference preference = Preference.getPreferences();
	private AccountManager accountManager = AccountManager.getAccountManger();
	private String programID;
	private String houseSceneID;
	private String gwID;
	private List<AutoConditionInfo> triggerSceneList;
	private boolean isRequestTasks = false;
	public static boolean isSaveTime = false;
	private static final String SCENE_TIMING_KEY = "scene_timing_key";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arg = getActivity().getIntent().getExtras();
		if(arg != null){
			houseSceneID = arg.getString(SCENE_INFO_TIMING);
		}
		initBar();
		isSaveTime = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_manager_fragment_scene_timing, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		sceneTimingLayout = (LinearLayout) view
				.findViewById(R.id.house_keeper_task_scene_timing_listview);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!isRequestTasks){
			programID = autoProgramTaskInfo.getProgramID();
			gwID = mAccountManger.getmCurrentInfo().getGwID();
			if(!StringUtil.isNullOrEmpty(programID)){
				JsonTool.deleteAndQueryAutoTaskList("R", autoProgramTaskInfo);
				preference.putBoolean(gwID + programID + IPreferenceKey.P_KEY_HOUSE_SCENE_TASK_SEND_QUERY,true);
				mDialogManager.showDialog(SCENE_TIMING_KEY, mActivity, null, null);
			}
			isRequestTasks = true;
		}
		initHouseSceneTriggerItem();
		
		getView().setFocusableInTouchMode(true);
	    getView().requestFocus();
	    getView().setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if(arg1 == KeyEvent.KEYCODE_BACK && arg2.getAction() == KeyEvent.ACTION_UP){
					goBack();
					return true;
				}
				return false;
			}
	    });
		
	}
	
	private void initHouseSceneTriggerItem() {
		triggerSceneList = autoProgramTaskInfo.getTriggerList();
		sceneTimingLayout.removeAllViews();
		for(int i= 0; i < triggerSceneList.size(); i++){
			final AutoConditionInfo triggerinfo = triggerSceneList.get(i);
			SceneTimingRuleItem sceneTimingitem = new SceneTimingRuleItem(mActivity, triggerinfo);
			sceneTimingLayout.addView(sceneTimingitem.getView());
			sceneTimingitem.getDeleteButton().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					isSaveTime = true;
					autoProgramTaskInfo.getTriggerList().remove(triggerinfo);
					initHouseSceneTriggerItem();
				}
			});
		}	
	}
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.scene_info_timing_scene));
		getSupportActionBar().setIconText(R.string.nav_scene_title);
		getSupportActionBar().setRightIcon(R.drawable.common_use_add);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						HouseKeeperTriggerTimeFragment.setTriggerTimeListener(new TriggerTimeListener() {
							
							@Override
							public void onTriggerTimeListenerChanged(String curtime, String time,String des) {
								if(curtime == null || time == null){
									
								}else{
									isSaveTime = true;
									AutoConditionInfo triggerInfo = new AutoConditionInfo();
									triggerInfo.setType("1");
									triggerInfo.setObject(curtime);
									triggerInfo.setExp(time);
									triggerInfo.setDes(des);
									autoProgramTaskInfo.addTrigger(triggerInfo);
								}
							}
						});
						String condition = "scene";
						intent.putExtra(HouseKeeperTriggerTimeFragment.TRIGGER_OR_CONDITION, condition);
						intent.setClass(mActivity, HouseKeeperTriggerTimeActivity.class);
						mActivity.startActivity(intent);
						
					}
			});
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				goBack();
			}
		});
	}
	
	protected void goBack() {
		if(isSaveTime){
			if(autoProgramTaskInfo.getTriggerList().size() == 0 && !StringUtil.isNullOrEmpty( autoProgramTaskInfo.getProgramID())){
				preference.putBoolean(gwID + programID + IPreferenceKey.P_KEY_HOUSE_SCENE_TASK_SEND_QUERY,true);
				JsonTool.deleteAndQueryAutoTaskList("D", autoProgramTaskInfo);
//				mDialogManager.showDialog(SCENE_TIMING_KEY, mActivity, null, null);
			}else if(autoProgramTaskInfo.getTriggerList().size() != 0){
				String gwID = accountManager.getmCurrentInfo().getGwID();
				String programID = autoProgramTaskInfo.getProgramID();
				String programName = houseSceneID;
				String programType = "1"; 
				String operType = getoperType(programID);
				String status = getRuleStatus(gwID, programType);
				
				JSONArray triggerArray = new JSONArray();
				for(int i=0; i < triggerSceneList.size(); i++){
					AutoConditionInfo info = triggerSceneList.get(i);
					JSONObject obj = new JSONObject();
					JsonTool.makeTaskTriggerJSONObject(obj,info);
					triggerArray.add(obj);
				}
				
				JSONArray actionArray = new JSONArray();
				actionArray.add(getActionJsonObj());
				
				preference.putBoolean(gwID + programID + IPreferenceKey.P_KEY_HOUSE_SCENE_TASK_SEND_QUERY,true);
				NetSDK.sendSetProgramTask(gwID, operType, programID, programName, null,programType, status, triggerArray, null, actionArray);
//				mDialogManager.showDialog(SCENE_TIMING_KEY, mActivity, null, null);
			}
			mActivity.finish();
		}else{
			mActivity.finish();
		}
//		EventBus.getDefault().post(new TimingSceneEvent());
	}
	
	private JSONObject getActionJsonObj() {
		JSONObject actionjsonObj = new JSONObject();
		actionjsonObj.put(ConstUtil.KEY_TRIGGER_TYPE, "0");
		actionjsonObj.put(ConstUtil.KEY_ACTION_OBJECT, houseSceneID);
		actionjsonObj.put(ConstUtil.KEY_ACTION_EPDATA, "2");
		actionjsonObj.put(ConstUtil.KEY_ACTION_DELAY, "");
		return actionjsonObj;
	}

	private String getRuleStatus(String gwID, String programType) {
		String status = "";
		if(preference.getBoolean(gwID + programType + IPreferenceKey.P_KEY_HOUSE_RULE_TIMING_STATUS, true)){
			status = CmdUtil.HOUSE_RULES_USING;
		}else{
			status = CmdUtil.HOUSE_RULES_UNUSE;
		}
		return status;
	}

	private String getoperType(String programID) {
		String operType = "";
		if(!StringUtil.isNullOrEmpty(programID)){
			operType = AutoTask.AUTO_TASK_OPER_TYPE_MODIFY;
		}else{
			operType = AutoTask.AUTO_TASK_OPER_TYPE_ADD;
		}
		return operType;
	}

	public void onEventMainThread(AutoTaskEvent event) {
		if(preference.getBoolean(gwID + programID + IPreferenceKey.P_KEY_HOUSE_SCENE_TASK_SEND_QUERY, false)){
			if (AutoTaskEvent.QUERY.equals(event.action)
					&& event.taskInfo != null) {
				preference.putBoolean(gwID + programID + IPreferenceKey.P_KEY_HOUSE_SCENE_TASK_SEND_QUERY,false);
				autoProgramTaskInfo = autoProgramTaskManager.getProgramTaskinfo(gwID, programID);
				mDialogManager.dimissDialog(SCENE_TIMING_KEY, 0);
				initHouseSceneTriggerItem();
			}
//			else{
//				mDialogManager.dimissDialog(SCENE_TIMING_KEY, 0);
//				mActivity.finish();
//			}
		}
	}
}
