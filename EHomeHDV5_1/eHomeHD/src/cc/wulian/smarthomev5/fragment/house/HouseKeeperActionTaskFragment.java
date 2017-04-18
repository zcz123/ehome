package cc.wulian.smarthomev5.fragment.house;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.wulian.app.model.device.DesktopCameraDevice;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperActionSceneActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperActionSelectDeviceActivity;
import cc.wulian.smarthomev5.adapter.house.AutoActionTaskAdapter;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionSelectDeviceFragment.AddLinkDeviceListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectControlDeviceDataFragment.SelectControlDeviceDataListener;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.Trans2PinYin;
import cc.wulian.smarthomev5.view.DragListView;

public class HouseKeeperActionTaskFragment extends WulianFragment implements H5PlusWebViewContainer{

	public static final String LINK_LIST_PROGRAMTYPE_KEY = "LINK_LIST_PROGRAMTYPE_KEY";
	public static final String LINK_LIST_SCENCE_NAME = "LINK_LIST_SCENCE_NAME";
	private LinearLayout addDeviceLinkTask;
	private LinearLayout addSceneLinkTask;
	private LinearLayout addDeviceLinkTaskLayout;
	private DragListView taskList;
	private TextView tvScenceName;
	public  AutoProgramTaskInfo taskInfo;
	private AutoActionTaskAdapter taskLinkAdapter;
	private DeviceCache deviceCache;
	private static final String SPLIT_SYMBOL = ">";
	private boolean isRequestTasks = false;
	private String programTypeKey;
	private WLDialog dialog = null;
	private static final String SCENE_TASK_KEY = "scene_task_key";
	private static final String PRE_LOAD_KEY = "preload_date_key";
	public static boolean isSaveTask = false;
	public boolean isClickJump = false;
	private AutoProgramTaskManager autoProgramTaskManager = AutoProgramTaskManager.getInstance();
	private Preference preference = Preference.getPreferences();
	
	private List<AutoActionInfo> filterActionInfos;		//筛选后的集合
	
	private static AddLinkTaskListener addLinkTaskListener;
	private String scenceName;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (isClickJump && HouseKeeperActionSelectDeviceFragment.preloadDeviceList != null){
				if (mDialogManager.containsDialog(PRE_LOAD_KEY))
					mDialogManager.dimissDialog(PRE_LOAD_KEY, 0);
				jumpToActivity();
			}
		}
	};

	private Comparator<WulianDevice> deviceComparator = new Comparator<WulianDevice>() {

		@Override
		public int compare(WulianDevice lhs, WulianDevice rhs) {
			String leftDeviceName = DeviceTool.getDeviceShowName(lhs);
			String leftDeviceRoomID = lhs.getDeviceRoomID();
			String rightDeviceName = DeviceTool.getDeviceShowName(rhs);
			String rightDeviceRoomID = rhs.getDeviceRoomID();
			int result = Trans2PinYin
					.trans2PinYin(leftDeviceName.trim())
					.toLowerCase()
					.compareTo(
							Trans2PinYin.trans2PinYin(rightDeviceName.trim())
									.toLowerCase());
			if (result != 0) {
				return result;
			} else {
				return leftDeviceRoomID.compareTo(rightDeviceRoomID);
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arg = getActivity().getIntent().getExtras();
		if (arg != null) {
			programTypeKey = arg.getString(LINK_LIST_PROGRAMTYPE_KEY);
			scenceName = arg.getString(LINK_LIST_SCENCE_NAME);
		}
		taskInfo = (AutoProgramTaskInfo) arg.getSerializable("AutoProgramTaskInfo");
		
		filterActionInfos = getFilterActionList(taskInfo);
		taskLinkAdapter = new AutoActionTaskAdapter(mActivity,filterActionInfos);
		
		deviceCache = DeviceCache.getInstance(mActivity);
		initBar();
		isSaveTask = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_manager_add_link_list, null);
	}
	
	public void setDeviceFragment(int position){
		final AutoActionInfo actionInfo = taskLinkAdapter.getItem(position);
		System.out.println("-------->"+actionInfo.getEpData());
		String[] type = actionInfo.getObject().split(SPLIT_SYMBOL);
		WulianDevice device = deviceCache.getDeviceByID(mActivity,
				AccountManager.getAccountManger().getmCurrentInfo().getGwID(), type[0]);
		if(device != null) {
			device.setCurrentFragment(this);
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		addDeviceLinkTaskLayout = (LinearLayout) view.findViewById(R.id.house_keeper_task_show_add_execute_link_layout);
		addDeviceLinkTask = (LinearLayout) view.findViewById(R.id.house_keeper_task_add_execute_link_layout);
		addSceneLinkTask = (LinearLayout) view.findViewById(R.id.house_keeper_task_add_scene_link_layout);
		taskList = (DragListView) view.findViewById(R.id.house_keeper_task_add_link_list);
		tvScenceName = (TextView) view.findViewById(R.id.house_keeper_task_scence_name_tv);
		if(StringUtil.equals(programTypeKey, "0")){
			tvScenceName.setText(mActivity.getResources().getString(R.string.scene_edit_the_current_editing_scene)+scenceName);
			addSceneLinkTask.setVisibility(View.GONE);
		}else{
			tvScenceName.setText("");
			addSceneLinkTask.setVisibility(View.VISIBLE);
			addSceneLinkTask.setOnClickListener(chooseTaskListener);
		}
		addDeviceLinkTask.setOnClickListener(chooseTaskListener);
		taskLinkAdapter.setIsShowEdit(false);
		taskList.setAdapter(taskLinkAdapter);
		//点击任务列表的单项出现的界面
		taskList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				final AutoActionInfo actionInfo = taskLinkAdapter.getItem(position);
				if (StringUtil.equals("0", actionInfo.getType()) && mApplication.sceneInfoMap.containsKey(mAccountManger.getmCurrentInfo().getGwID() + actionInfo.getObject())){
					isSaveTask = true;
					HouseKeeperActionSceneFragment.setAddLinkSceneListener(new HouseKeeperActionSceneFragment.AddLinkSceneListener() {
						@Override
						public void onAddLinkSceneListenerChanged(List<AutoActionInfo> infos) {
							if (!infos.isEmpty() || infos != null) {
								for (AutoActionInfo info : infos) {
									taskInfo.getActionList().remove(0);
									taskInfo.getActionList().add(0, info);
									taskLinkAdapter.setIsShowEdit(false);
									filterActionInfos = getFilterActionList(taskInfo);
									taskLinkAdapter.swapData(filterActionInfos);
								}
							}
						}
					});
					Intent intent = new Intent();
					intent.putExtra(HouseKeeperConditionSceneFragment.TRIGGER_OR_CONDITION, "condition");
					intent.setClass(mActivity, HouseKeeperActionSceneActivity.class);
					mActivity.startActivity(intent);
				}else {
					System.out.println("-------->"+actionInfo.getEpData());
					String[] type = actionInfo.getObject().split(SPLIT_SYMBOL);
					WulianDevice device = deviceCache.getDeviceByID(mActivity,
							AccountManager.getAccountManger().getmCurrentInfo().getGwID(), type[0]);
					final AutoActionInfo oldActionInfo = actionInfo.clone();
					//增加对桌面摄像机的判断
					if(actionInfo.getObject().equals("self")){
						device=new DesktopCameraDevice(mActivity, "camera");
					}
					isSaveTask = true;
					if(device != null){
						setDeviceFragment(position);
						DialogOrActivityHolder holder = device.onCreateHouseKeeperSelectControlDeviceDataView(inflater, actionInfo);
						if(holder == null){
							return ;
						}
						if(holder.isShowDialog()){
							holder.createSelectControlDataDialog(mActivity, holder.getContentView(), new MessageListener() {


								@Override
								public void onClickPositive(View contentViewLayout) {
									/**
									 * 这里可变更
									 */

									filterActionInfos = getFilterActionList(taskInfo);
									taskLinkAdapter.swapData(filterActionInfos);
								}


								@Override
								public void onClickNegative(View contentViewLayout) {
									actionInfo.setSortNum(oldActionInfo.getSortNum());
									actionInfo.setType(oldActionInfo.getType());
									actionInfo.setObject(oldActionInfo.getObject());
									actionInfo.setEpData(oldActionInfo.getEpData());
									actionInfo.setDescription(oldActionInfo.getDescription());
									actionInfo.setDelay(oldActionInfo.getDelay());
								}
							}).show();

						}else{
							Bundle bundle = new Bundle();
							bundle.putString(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, HouseKeeperSelectControlDeviceDataFragment.class.getName());
							bundle.putString(HouseKeeperSelectControlDeviceDataFragment.DEV_GW_ID, device.getDeviceGwID());
							bundle.putString(HouseKeeperSelectControlDeviceDataFragment.DEV_ID, device.getDeviceID());
							bundle.putSerializable(
									HouseKeeperSelectControlDeviceDataFragment.LINK_LIST_ACTIONINFO_INFO,
									actionInfo);
							holder.startActivity(mActivity, bundle);
							HouseKeeperSelectControlDeviceDataFragment.setSelectControlDeviceDataListener(new SelectControlDeviceDataListener() {

								@Override
								public void onSelectDeviceDataChanged(String obj,String epData, String des) {
									actionInfo.setEpData(epData);
									actionInfo.setObject(obj);
									actionInfo.setDescription(des);

									filterActionInfos = getFilterActionList(taskInfo);
									taskLinkAdapter.swapData(filterActionInfos);
								}
							});

						}
					}
				}
			}
		});
	}
	
	/**
	 * 获得筛选的任务集合，去除自定义消息
	 * @param taskInfo
	 * @return
	 */
	private List<AutoActionInfo> getFilterActionList(
			AutoProgramTaskInfo taskInfo) {
		List<AutoActionInfo> actionList = taskInfo.getActionList();
		List<AutoActionInfo> filterActionList = new ArrayList<AutoActionInfo>();
		for (AutoActionInfo autoActionInfo : actionList) {
			if(!StringUtil.equals("3", autoActionInfo.getType())){
				filterActionList.add(autoActionInfo);
			}
		}
		return filterActionList;
	}
	
	/**
	 * 点击添加按钮的监听
	 */
	private OnClickListener chooseTaskListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()){
				case R.id.house_keeper_task_add_scene_link_layout:
					boolean hasSceneTask = false;
					for (AutoActionInfo info : filterActionInfos){
						if (StringUtil.equals("0", info.getType())){
							hasSceneTask = true;
							break;
						}
					}
					if (hasSceneTask){
						WLToast.showToast(mActivity, "只能添加一个场景任务", Toast.LENGTH_SHORT);
						return;
					}
					isSaveTask = true;
					HouseKeeperActionSceneFragment.setAddLinkSceneListener(new HouseKeeperActionSceneFragment.AddLinkSceneListener() {
						@Override
						public void onAddLinkSceneListenerChanged(List<AutoActionInfo> infos) {
							if (!infos.isEmpty() || infos != null) {
								for (AutoActionInfo info : infos) {
									taskInfo.getActionList().add(0, info);
									taskLinkAdapter.setIsShowEdit(false);
									filterActionInfos = getFilterActionList(taskInfo);
									taskLinkAdapter.swapData(filterActionInfos);
								}
							}
						}
					});
					Intent intent = new Intent();
					intent.putExtra(HouseKeeperConditionSceneFragment.TRIGGER_OR_CONDITION, "condition");
					intent.setClass(mActivity, HouseKeeperActionSceneActivity.class);
					mActivity.startActivity(intent);
					break;
				case R.id.house_keeper_task_add_execute_link_layout:
					if (taskInfo.getActionList().size() >= 60){
						showTaskDeviceNumberDialog();
					}else if (HouseKeeperActionSelectDeviceFragment.preloadDeviceList == null && !deviceCache.isEmpty()){
						mDialogManager.showDialog(PRE_LOAD_KEY, mActivity, null, null);
						isClickJump = true;
						handler.sendEmptyMessage(0);
					}else {
						jumpToActivity();
					}
					break;
			}
		}
	};

	private void jumpToActivity(){
		isClickJump = false;
		isSaveTask = true;
		//任务数小于60
		HouseKeeperActionSelectDeviceFragment.setAddLinkDeviceListener(new AddLinkDeviceListener() {

			@Override
			public void onAddLinkDeviceListenerChanged(List<AutoActionInfo> infos) {
				if (!infos.isEmpty() || infos != null) {
					for (AutoActionInfo info : infos) {
						taskInfo.getActionList().add(info);
						taskLinkAdapter.setIsShowEdit(false);
						filterActionInfos = getFilterActionList(taskInfo);
						taskLinkAdapter.swapData(filterActionInfos);
					}
				}
			}
		});
		filterActionInfos = getFilterActionList(taskInfo);
		Intent intent = new Intent();
		intent.putExtra(HouseKeeperActionSelectDeviceFragment.ACTION_TASK_DEVICE_NUMBER, filterActionInfos.size());
		intent.setClass(mActivity, HouseKeeperActionSelectDeviceActivity.class);
		mActivity.startActivity(intent);
	}

	/**
	 * 弹出提示设备数量对话框，任务设备不能超过60个
	 */
	private  void showTaskDeviceNumberDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		View deviceNumberView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_dialog, null);
		TextView tvToast = (TextView) deviceNumberView
				.findViewById(R.id.house_keeper_upgrade_promt);
		tvToast.setText(mApplication.getResources().getString(R.string.house_rule_tasklist_count_hint));
		builder.setTitle(mApplication.getResources().getString(R.string.gateway_router_setting_dialog_toast))
		.setContentView(deviceNumberView)
		.setPositiveButton(android.R.string.ok)
		.setCancelOnTouchOutSide(false);
		dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * 初始化ActionBar
	 */
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//0代表简单的场景任务执行程序，特征是Trigger为只有一个场景条目，Condition为空
		if(StringUtil.equals(programTypeKey, "0")){
			getSupportActionBar().setIconText(R.string.nav_scene_title);
		}else{
			getSupportActionBar().setIconText(R.string.house_rule_add_rule);
		}
		getSupportActionBar().setTitle(R.string.scene_task_list_hint);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(R.string.house_rule_add_new_Link_task_edit);
		/**
		 * 点击编辑按钮进入编辑状态
		 */
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						isSaveTask = true;
						initChangeBar();
						//隐藏添加任务按钮
						addDeviceLinkTaskLayout.setVisibility(View.INVISIBLE);
						taskList.setLock(false);
						LinearLayout.LayoutParams layoutParams = (LayoutParams) taskList.getLayoutParams();
						layoutParams.bottomMargin=0;//将默认的距离底部60dp，改为0，这样底部区域全被listview填满。
						taskList.setLayoutParams(layoutParams);
						taskLinkAdapter.setIsShowEdit(true);
						
						/**
						 * 这里可修改
						 */
						filterActionInfos = getFilterActionList(taskInfo);
						taskLinkAdapter.swapData(filterActionInfos);
					}
		});
		/**
		 * 点击左上角返回添加规则页
		 */
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(isSaveTask){
					backSaveSceneTask();
				}else{
					mActivity.finish();
				}
			}
		});
	}
	
	public void backSaveSceneTask(){
		AutoActionInfo actionInfo = null;
		for (AutoActionInfo info:filterActionInfos){
			if (StringUtil.equals("0", info.getType())){
				actionInfo = info;
				break;
			}
		}
		if(addLinkTaskListener != null){
			/**
			 * 这里可以修改
			 */
			List<AutoActionInfo> listInfo = taskInfo.getActionList();
			for(int i = 0; i < listInfo.size(); i++){
				listInfo.get(i).setSortNum(i+"");
//				taskInfo.addActionTask(taskInfo.getActionList().get(i));
				if(StringUtil.isNullOrEmpty(listInfo.get(i).getEpData())&&StringUtil.isNullOrEmpty(listInfo.get(i).getCancelDelay())){
//					WLToast.showToast(mActivity, mApplication.getResources().getString(R.string.house_rule_tasklist_edit_hint), WLToast.TOAST_SHORT);
					showDeviceEditDialog();
					return;
				}
			}
			addLinkTaskListener.onAddLinkTaskListenerChanged(taskInfo);
			addLinkTaskListener = null;
			mActivity.finish();
		}
	}
	
	/**
	 * 显示编辑未完成的提示对话框
	 */
	private  void showDeviceEditDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		View deviceEditView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_dialog, null);
		TextView tvToast = (TextView) deviceEditView
				.findViewById(R.id.house_keeper_upgrade_promt);
		tvToast.setText(mApplication.getResources().getString(R.string.house_rule_tasklist_edit_hint));
		builder.setTitle(mApplication.getResources().getString(R.string.gateway_router_setting_dialog_toast))
		.setContentView(deviceEditView)
		.setPositiveButton(android.R.string.ok)
		.setCancelOnTouchOutSide(false);
		dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * 任务列表为空时的ActionBar
	 */
	private void initBarHideEdit() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(StringUtil.equals(programTypeKey, "0")){
			getSupportActionBar().setIconText(R.string.nav_scene_title);
		}else{
			getSupportActionBar().setIconText(R.string.house_rule_add_rule);
		}
		getSupportActionBar().setTitle(R.string.scene_task_list_hint);
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isSaveTask){
					backSaveSceneTask();
				}else{
					mActivity.finish();
				}
			}
		});
	}
	
	/**
	 * 编辑状态下的ActionBar
	 */
	private void initChangeBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(StringUtil.equals(programTypeKey, "0")){
			getSupportActionBar().setIconText(R.string.nav_scene_title);
		}else{
			getSupportActionBar().setIconText(R.string.house_rule_add_rule);
		}
		getSupportActionBar().setTitle(R.string.scene_task_list_hint);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(R.string.set_sound_notification_bell_prompt_choose_complete);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						synTaskInfos(filterActionInfos);
						taskInfo.setActionList(filterActionInfos);
						filterActionInfos = getFilterActionList(taskInfo);
						if(filterActionInfos.size() == 0){
							initBarHideEdit();
						}else{
							initBar();
						}
						addDeviceLinkTaskLayout.setVisibility(View.VISIBLE);
						taskList.setLock(true);
						LinearLayout.LayoutParams layoutParams = (LayoutParams) taskList.getLayoutParams();
						layoutParams.bottomMargin= DisplayUtil.dip2Sp(mActivity, 60);
						taskList.setLayoutParams(layoutParams);
						taskLinkAdapter.setIsShowEdit(false);
						/**
						 * 这里可修改
						 */
						filterActionInfos = getFilterActionList(taskInfo);
						taskLinkAdapter.swapData(filterActionInfos);
					}

					
		});
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
//				if(addLinkTaskListener != null){
//					AutoProgramTaskInfo taskInfo = new AutoProgramTaskInfo();
//					taskInfo.setActionList(actionList);
//					addLinkTaskListener.onAddLinkTaskListenerChanged(taskInfo);
//				}
//				mActivity.finish();
				WLToast.showToast(mActivity, mActivity.getResources().getString(R.string.house_rule_add_new_action_no_edit),WLToast.TOAST_SHORT);
			}
		});
	}
	
	//同步taskInfo
	private void synTaskInfos(
			List<AutoActionInfo> filterActionInfos) {
		List<AutoActionInfo> actionList = taskInfo.getActionList();
		
		List<AutoActionInfo> copyActionList = new ArrayList<AutoActionInfo>();
		copyActionList.addAll(actionList);
		
		List<String> relatedDevIds = new ArrayList<>();
		
		for (AutoActionInfo autoActionInfo : copyActionList) {
			if(!filterActionInfos.contains(autoActionInfo)){
				if(StringUtil.equals("2", autoActionInfo.getType())||StringUtil.equals("1", autoActionInfo.getType())){
					relatedDevIds.add(autoActionInfo.getObject().split(">")[0]);
					actionList.remove(autoActionInfo);
				}
				
			}
		}
		
		for (AutoActionInfo autoActionInfo : copyActionList) {
			if(!filterActionInfos.contains(autoActionInfo)){
				if(StringUtil.equals("3", autoActionInfo.getType())){
					for (String devId : relatedDevIds) {
						if(devId.equals(autoActionInfo.getObject().split(">")[0])){
							actionList.remove(autoActionInfo);
						}
					}
				}
				
			}
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		TaskExecutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				loadData();
			}
		});
		if(!isRequestTasks && StringUtil.equals(programTypeKey, "0")){
//			programID = taskInfo.getProgramID();
			if(!StringUtil.isNullOrEmpty(taskInfo.getProgramID())){
				JsonTool.deleteAndQueryAutoTaskList("R", taskInfo);
				
				preference.putBoolean(mAccountManger.getmCurrentInfo().getGwID() + taskInfo.getProgramID() + IPreferenceKey.P_KEY_HOUSE_SCENE_TASK_SEND_QUERY,true);
				mDialogManager.showDialog(SCENE_TASK_KEY, mActivity, null, null);
			}
			isRequestTasks = true;
		}
		filterActionInfos = getFilterActionList(taskInfo);
		if(filterActionInfos.size() == 0){
			initBarHideEdit();
		}else{
			initBar();
		}
		taskLinkAdapter.notifyDataSetChanged();
	}

	public void onEventMainThread(AutoTaskEvent event) {
		if (AutoTaskEvent.QUERY.equals(event.action)
				&& event.taskInfo != null && preference.getBoolean(mAccountManger.getmCurrentInfo().getGwID() + event.taskInfo.getProgramID() + IPreferenceKey.P_KEY_HOUSE_SCENE_TASK_SEND_QUERY, false)) {
			preference.putBoolean(mAccountManger.getmCurrentInfo().getGwID() + event.taskInfo.getProgramID() + IPreferenceKey.P_KEY_HOUSE_SCENE_TASK_SEND_QUERY,false);
			taskInfo = autoProgramTaskManager.getProgramTaskinfo(mAccountManger.getmCurrentInfo().getGwID(), event.taskInfo.getProgramID());
			
			filterActionInfos = getFilterActionList(taskInfo);
			taskLinkAdapter.swapData(filterActionInfos);
			mDialogManager.dimissDialog(SCENE_TASK_KEY, 0);
			/**
			 * 这里可以改
			 */
			if(filterActionInfos.size() == 0){
				initBarHideEdit();
			}else{
				initBar();
			}
		}
	}
	
	public static void setAddLinkDeviceListener(AddLinkTaskListener linkTaskListener) {
		addLinkTaskListener = linkTaskListener;
	}


	public interface AddLinkTaskListener{
		public void onAddLinkTaskListenerChanged(AutoProgramTaskInfo taskInfo);
	}


	@Override
	public void addH5PlusWebView(H5PlusWebView webview) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroyContainer() {
		Engine.destroyPager(this);
		this.getActivity().finish();
	}

	@Override
	public ViewGroup getContainerRootView() {
		// TODO Auto-generated method stub
		return null;
	}

	private void loadData(){
		HouseKeeperActionSelectDeviceFragment.preloadDeviceList = null;
		Collection<WulianDevice> result = deviceCache.getAllDevice();
		List<WulianDevice> devices = new ArrayList<WulianDevice>();
		for (WulianDevice device : result) {
			String isvalidate=device.getDeviceInfo().getIsvalidate();
			if(isvalidate!=null&&isvalidate.equals("2")) {
				continue;
			}
			if (device.isAutoControl(true)) {
				devices.add(device);
			}
		}
		// 首先判断是否是桌面摄像机
		//判断是是否选择的桌面摄像机选项或者全部设备从而决定是否添加桌面摄像机设备
		if(isDesktopCameraExist()){
			DesktopCameraDevice cameraDevice = new DesktopCameraDevice(
					mActivity,"camera");
			devices.add(cameraDevice);
		}
		Collections.sort(devices, deviceComparator);
		HouseKeeperActionSelectDeviceFragment.preloadDeviceList = devices;
		handler.sendEmptyMessage(0);
	}

	// 判断是否是桌面摄像机
	private boolean isDesktopCameraExist() {
		AccountManager accountManager = AccountManager.getAccountManger();
		GatewayInfo info = accountManager.getmCurrentInfo();
		if (("" + info.getGwVer().charAt(2)).equals("9")) {
			return true;
		}
		return false;
	}
}
