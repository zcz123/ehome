package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.doorlock.DoorLockAccountItem.IClickDosomething;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingItemClickActivity;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingshowActivity;
import cc.wulian.smarthomev5.adapter.SettingManagerAdapter;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.NewDoorLockEvent;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperCustomMessageItem;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.smarthomev5.fragment.setting.SpaceItem;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.view.DropDownListView;
import cc.wulian.smarthomev5.view.DropDownListView.OnRefreshListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

public class EditDoorLock6AccountFragment extends WulianFragment {
	@ViewInject(R.id.more_items_lv)
	private DropDownListView moreManagerListView;
	private SettingManagerAdapter moreManagerAdapter;

	private String gwID;
	private String devID;
	private String mToken;
	private static final String DOOR_LOCK_ACCOUNT_KEY = "door_lock_account_key";
	private String AdminUserType = "00";
	private String CommonUserType = "01";
	private String TemporaryUserType = "02";
	private List<Map<String, String>> adminUserList;
	private List<Map<String, String>> commonUserList;
	private List<Map<String, String>> temporaryUserList;
	private boolean isTemporaryInvalid = false;
	private int currentTemporaryUserItemPosition;
	
	private SceneDao sceneDao = SceneDao.getInstance();
	private List<SceneInfo> infos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		moreManagerAdapter = new SettingManagerAdapter(mActivity);
		initEditDevice();
		initBar();
		adminUserList = new ArrayList<Map<String, String>>();
		commonUserList = new ArrayList<Map<String, String>>();
		temporaryUserList = new ArrayList<Map<String, String>>();
	}

	@Override
	public void onResume() {
		super.onResume();
		mDialogManager.showDialog(DOOR_LOCK_ACCOUNT_KEY, mActivity, null, null);
//		SendMessage.sendControlDevMsg(gwID, devID, "14", "89", "34");
		SceneInfo info = new SceneInfo();
		info.setGwID(mAccountManger.getmCurrentInfo().getGwID());
		infos=sceneDao.findListAll(info);
		getViewstatus();
	}

	private void getViewstatus() {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("token", mToken);
		jsonObject.put("userType", "");
//		jsonObject.put("force","1");
		NetSDK.sendSetDoorLockData(gwID, devID, "1", jsonObject);
	}

	private void initItemsView() {
		moreManagerAdapter = new SettingManagerAdapter(mActivity);
		moreManagerListView.setAdapter(moreManagerAdapter);
		showAdminUserItemView();
		showCommonUserItemView();
		showTemporaryUserItemView();
		moreManagerAdapter.notifyDataSetChanged();
	}

	private void showTemporaryUserItemView() {
		SpaceItem spaceUserItem = new SpaceItem(mActivity);
		spaceUserItem.setSpaceValue(10);
		spaceUserItem.initSystemState();
		moreManagerAdapter.addSettingItem(spaceUserItem);
		// 临时用户
		DoorLockItem temporaryUserNameItem = new DoorLockItem(mActivity);
		temporaryUserNameItem.setNameDescribe(getString(R.string.device_lock_user_temp));
		temporaryUserNameItem.initSystemState();
		temporaryUserNameItem.setNameSize(18);
		if (isTemporaryInvalid) {
			temporaryUserNameItem.setInfoTextViewVisible(View.VISIBLE);
		}
		temporaryUserNameItem.changeViewBackground();
		moreManagerAdapter.addSettingItem(temporaryUserNameItem);
		if(temporaryUserList!=null&&temporaryUserList.size()==0){
			DoorLockItem adminAccountItem = new DoorLockItem(mActivity);
				adminAccountItem.setNameDescribe(getString(R.string.device_lock_op_noUser_Info));
			// adminAccountItem.setNameDescribe("admin"+i);
			adminAccountItem.initSystemState();
			moreManagerAdapter.addSettingItem(adminAccountItem);
		}
		// 临时用户账户依次加入到列表中
		for (int i = 0; i < temporaryUserList.size(); i++) {
			int currentPosition = i;
			addTemporaryUserItemView(currentPosition);
		}
	}

	private void showCommonUserItemView() {
		SpaceItem spaceUserItem = new SpaceItem(mActivity);
		spaceUserItem.setSpaceValue(10);
		spaceUserItem.initSystemState();
		moreManagerAdapter.addSettingItem(spaceUserItem);
		// 普通用户
		DoorLockItem commonUserNameItem = new DoorLockItem(mActivity);
		commonUserNameItem.setNameDescribe(getString(R.string.device_lock_user_common));
		commonUserNameItem.initSystemState();
		commonUserNameItem.setNameSize(18);
		commonUserNameItem.changeViewBackground();
		moreManagerAdapter.addSettingItem(commonUserNameItem);
		if(commonUserList!=null&&commonUserList.size()==0){
			DoorLockItem adminAccountItem = new DoorLockItem(mActivity);
				adminAccountItem.setNameDescribe(getString(R.string.device_lock_op_noUser_Info));
			// adminAccountItem.setNameDescribe("admin"+i);
			adminAccountItem.initSystemState();
			moreManagerAdapter.addSettingItem(adminAccountItem);
		}
		// 普通用户账户依次加入到列表中
		for (int i = 0; i < commonUserList.size(); i++) {
			DoorLockItem commonAccountItem = new DoorLockItem(mActivity);
			if (!commonUserList.get(i).get("cname").equals("")) {
				commonAccountItem.setNameDescribe(commonUserList.get(i).get(
						"cname"));
			} else {
				int number=Integer.parseInt(commonUserList.get(i).get("userID"), 16);
				String numString=number>9?number+"":"0"+number;
				commonAccountItem.setNameDescribe(numString);
			}
			commonAccountItem.initSystemState();
			commonAccountItem.setInfoTextViewVisible(View.VISIBLE);
			commonAccountItem.getInfoTextView().setText(getSceceNameBySceneId(commonUserList.get(i).get("sceneId")));
			final int currentUserPosition=i;
			final String bindSceneName=commonAccountItem.getInfoTextView().getText().toString().trim();
			commonAccountItem.setCallBack(new IClickDosomething() {
				
				@Override
				public void dosomething() {
					modifyUserMessage(commonUserList.get(currentUserPosition),bindSceneName);
				}
			});
			moreManagerAdapter.addSettingItem(commonAccountItem);
			SpaceItem spaceItem = new SpaceItem(mActivity);
			spaceItem.initSystemState();
			moreManagerAdapter.addSettingItem(spaceItem);
		}
	}

	protected void modifyUserMessage(Map<String, String> userMessage,String bindSceneName) {
		Intent intent = new Intent(mActivity, DeviceSettingItemClickActivity.class);
		intent.putExtra(
				DeviceSettingItemClickActivity.SETTING_ITEM_FRAGMENT_CLASSNAME,
				EditDoorLock6BindSceneFragment.class.getName());
		intent.putExtra(EditDoorLock6BindSceneFragment.TOKEN, mToken);
		intent.putExtra(EditDoorLock6BindSceneFragment.USERID, userMessage.get("userID"));
		intent.putExtra(EditDoorLock6BindSceneFragment.USERTYPE, userMessage.get("userType"));
		intent.putExtra(EditDoorLock6BindSceneFragment.CNAME, userMessage.get("cname"));
		intent.putExtra(EditDoorLock6BindSceneFragment.SCENEID, userMessage.get("sceneId"));
		intent.putExtra(EditDoorLock6BindSceneFragment.PEROID, userMessage.get("peroid"));
		intent.putExtra(EditDoorLock6BindSceneFragment.PASSWORD, userMessage.get("password"));
		intent.putExtra(EditDoorLock6BindSceneFragment.DEVICEID, devID);
		intent.putExtra(EditDoorLock6BindSceneFragment.GWID, gwID);
		intent.putExtra(EditDoorLock6BindSceneFragment.BINDED_SCENE_NAME, bindSceneName);
		mActivity.startActivity(intent);
	}

	private CharSequence getSceceNameBySceneId(String sceneId) {
		for(SceneInfo sceneInfo:infos){
			if(sceneId!=null && sceneId.equals(sceneInfo.getSceneID())){
				return sceneInfo.getName();
			}
		}
		return getString(R.string.device_no_bind);
	}

	private void showAdminUserItemView() {
		SpaceItem spaceUserItem = new SpaceItem(mActivity);
		spaceUserItem.setSpaceValue(10);
		spaceUserItem.initSystemState();
		moreManagerAdapter.addSettingItem(spaceUserItem);
		// 管理员
		DoorLockItem adminUserNameItem = new DoorLockItem(mActivity);
		adminUserNameItem.setNameDescribe(getString(R.string.device_lock_user_manager));
		adminUserNameItem.initSystemState();
		adminUserNameItem.setNameSize(18);
		adminUserNameItem.changeViewBackground();
		moreManagerAdapter.addSettingItem(adminUserNameItem);
		if(adminUserList!=null&&adminUserList.size()==0){
			DoorLockItem adminAccountItem = new DoorLockItem(mActivity);
				adminAccountItem.setNameDescribe(getString(R.string.device_lock_op_noUser_Info));
			adminAccountItem.initSystemState();
			moreManagerAdapter.addSettingItem(adminAccountItem);
		}
//		 管理员账户依次加入到列表中
		for (int i = 0; i < adminUserList.size(); i++) {
			DoorLockItem adminAccountItem = new DoorLockItem(mActivity);
			if (!adminUserList.get(i).get("cname").equals("")) {
				adminAccountItem.setNameDescribe(adminUserList.get(i).get(
						"cname"));
			} else {
				int number=Integer.parseInt(adminUserList.get(i).get("userID"), 16);
				String numString=number>9?number+"":"0"+number;
				adminAccountItem.setNameDescribe(numString);
			}
			adminAccountItem.initSystemState();
			adminAccountItem.setInfoTextViewVisible(View.VISIBLE);
			adminAccountItem.getInfoTextView().setText(getSceceNameBySceneId(adminUserList.get(i).get("sceneId")));
			final int currentUserPosition=i;
			final String bindSceneName=adminAccountItem.getInfoTextView().getText().toString().trim();
			adminAccountItem.setCallBack(new IClickDosomething() {
				
				@Override
				public void dosomething() {
					modifyUserMessage(adminUserList.get(currentUserPosition),bindSceneName);
				}
			});
			moreManagerAdapter.addSettingItem(adminAccountItem);
			SpaceItem spaceItem = new SpaceItem(mActivity);
			spaceItem.initSystemState();
			moreManagerAdapter.addSettingItem(spaceItem);
		}
	}

	private void addTemporaryUserItemView(final int currentPosition) {
		DoorLockItem temporaryAccountItem = new DoorLockItem(mActivity);
		if (!temporaryUserList.get(currentPosition).get("cname").equals("")) {
			temporaryAccountItem.setNameDescribe(temporaryUserList.get(
					currentPosition).get("cname"));
		} else {
			int number=Integer.parseInt(temporaryUserList.get(currentPosition).get("userID"), 16);
			String numString=number>9?number+"":"0"+number;
			temporaryAccountItem.setNameDescribe(numString);
		}
		temporaryAccountItem.initSystemState();
		if (!isTemporaryInvalid) {
			LinearLayout linearLayout = (LinearLayout) temporaryAccountItem
					.getShowView();
			linearLayout.removeAllViews();
			// linearLayout.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			// // 跳转到添加临时用户界面
			//
			// }
			// });
			DoorLockAccountItem doorLockAccountItem;

			String peroid = temporaryUserList.get(currentPosition)
					.get("peroid");
			String uneffectTime = peroid.substring(peroid.length() / 2);
			boolean isTemporaryUserUneffect = compare_date(
					"20"
							+ Integer.parseInt(uneffectTime.substring(0, 2), 16)
							+ "-"
							+ Integer.parseInt(uneffectTime.substring(2, 4), 16)
							+ "-"
							+ Integer.parseInt(uneffectTime.substring(4, 6), 16)
							+ " "
							+ Integer.parseInt(uneffectTime.substring(6, 8), 16)
							+ ":"
							+ Integer.parseInt(uneffectTime.substring(8, 10),
									16), new Date(System.currentTimeMillis()));
			if (!temporaryUserList.get(currentPosition).get("cname").equals("")) {
				doorLockAccountItem = new DoorLockAccountItem(mActivity,
						isTemporaryUserUneffect, temporaryUserList.get(
								currentPosition).get("cname"));
			} else {
				doorLockAccountItem = new DoorLockAccountItem(mActivity,
						isTemporaryUserUneffect, getString(R.string.device_lock_user_temp)
								+ temporaryUserList.get(currentPosition).get(
										"userID"));
			}
			linearLayout.addView(doorLockAccountItem.getView());
			doorLockAccountItem.setCallBack(new IClickDosomething() {

				@Override
				public void dosomething() {
					goToDetailsInterface(currentPosition);
				}
			});
			doorLockAccountItem.getDeleteButton().setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// 删除用户
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("token", mToken);
							jsonObject.put(
									"userID",
									""+Integer.parseInt(temporaryUserList.get(currentPosition).get(
											"userID"),16));
							jsonObject.put(
									"userType",
									Integer.parseInt(temporaryUserList.get(currentPosition).get(
											"userType"))+"");
							NetSDK.sendSetDoorLockData(gwID,  devID, "3",
									jsonObject);
							mDialogManager.showDialog(DOOR_LOCK_ACCOUNT_KEY,
									mActivity, null, null);
						}
					});
		}
		moreManagerAdapter.addSettingItem(temporaryAccountItem);
		SpaceItem spaceItem = new SpaceItem(mActivity);
		spaceItem.initSystemState();
		moreManagerAdapter.addSettingItem(spaceItem);
	}

	protected void goToDetailsInterface(int currentPosition) {
		Intent intentAccount = new Intent(mActivity,
				DeviceSettingshowActivity.class);
		intentAccount.putExtra(
				DeviceSettingshowActivity.SETTING_ITEM_SHOW_FRAGMENT_CLASSNAME,
				EditDoorLockAccountTemporaryFragment.class.getName());
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.GWID, gwID);
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.DEVICEID,
				devID);
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.CNAME,
				temporaryUserList.get(currentPosition).get("cname"));
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.PEROID,
				temporaryUserList.get(currentPosition).get("peroid"));
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.PASSWORD,
				temporaryUserList.get(currentPosition).get("password"));
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.TOKEN,
				mToken);
		startActivity(intentAccount);
	}

	private void initEditDevice() {
		gwID = getArguments().getString(EditDoorLock6Fragment.GWID);
		devID = getArguments().getString(EditDoorLock6Fragment.DEVICEID);
		mToken = getArguments().getString(EditDoorLock6Fragment.TOKEN);
		Log.e("DoorLock", mToken + " " + gwID + " " + devID);
	}

	private void deleteTemporaryUser(int currentPosition) {
		currentTemporaryUserItemPosition = currentPosition;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("token", mToken);
		jsonObject.put("userID",
				temporaryUserList.get(currentPosition).get("userID"));
		jsonObject.put("userType",
				temporaryUserList.get(currentPosition).get("userType"));
		NetSDK.sendSetDoorLockData(gwID, devID, "3", jsonObject);
		mDialogManager.showDialog(DOOR_LOCK_ACCOUNT_KEY, mActivity, null, null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.more_manager_content,
				container, false);
		rootView.setPadding(0, 0, 0, 0);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(true);
		getSupportActionBar().setIconText(getString(R.string.set_titel));
		getSupportActionBar().setRightIcon(R.drawable.common_use_add);
		getSupportActionBar().setTitle(getString(R.string.device_lock_user_manage));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						// 跳转到添加临时用户界面
						goToAddTemporaryUserInterface();
					}
				});
	}

	protected void goToAddTemporaryUserInterface() {
		Intent intentAccount = new Intent(mActivity,
				DeviceSettingshowActivity.class);
		intentAccount.putExtra(
				DeviceSettingshowActivity.SETTING_ITEM_SHOW_FRAGMENT_CLASSNAME,
				EditDoorLockAccountTemporaryFragment.class.getName());
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.GWID, gwID);
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.DEVICEID,
				devID);
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.CNAME,
				"noData");
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.PEROID,
				"noData");
		intentAccount.putExtra(EditDoorLockAccountTemporaryFragment.TOKEN,
				mToken);
		startActivity(intentAccount);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		moreManagerListView.setAdapter(moreManagerAdapter);
		moreManagerListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				JSONObject jsonObject=new JSONObject();
				jsonObject.put("token", mToken);
				jsonObject.put("userType", "");
				jsonObject.put("force", "1");
				NetSDK.sendSetDoorLockData(gwID, devID, "1", jsonObject);
			}
		});
	}

	private void refreshVeiwStatus(JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.getJSONArray("list");
		if (jsonArray!=null&&jsonArray.size() > 0) {
			initItem(jsonArray);
		}else{
			jsonArray=new JSONArray();
			initItem(jsonArray);
		}
	}

	private void initItem(JSONArray jsonArray) {
		adminUserList.clear();
		commonUserList.clear();
		temporaryUserList.clear();
		// TODO Auto-generated method stub
		for (int i = 0; i < jsonArray.size(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			map.put("userID", jsonObject.getString("userID"));
			map.put("userType", jsonObject.getString("userType"));
			map.put("cname", jsonObject.getString("cname"));
			map.put("sceneId", jsonObject.getString("sceneId"));
			map.put("peroid", jsonObject.getString("peroid"));
			map.put("password", jsonObject.getString("password"));
			String userType = jsonObject.getString("userType");
			if (userType.equals(AdminUserType)) {
				adminUserList.add(map);
			} else if (userType.equals(CommonUserType)) {
				commonUserList.add(map);
			} else if (userType.equals(TemporaryUserType)) {
				temporaryUserList.add(map);
			}
		}
		
		if(temporaryUserList.size()>5){
			getSupportActionBar().setRightMenuClickListener(
					new OnRightMenuClickListener() {

						@Override
						public void onClick(View v) {
							WLToast.showToast(mActivity, getString(R.string.smartLock_provisional_number_of_users_tips_hint), WLToast.TOAST_SHORT);
						}
					});
		}
		
		mDialogManager.dimissDialog(DOOR_LOCK_ACCOUNT_KEY, 0);
		initItemsView();
	}

	public void onEventMainThread(NewDoorLockEvent event) {
		String showResult = "";
		if(event.data!=null&&event.data.getString("result")!=null&&event.data.getString("result").equals("token_error")){
			mDialogManager.dimissDialog(DOOR_LOCK_ACCOUNT_KEY, 0);
			showResult(getString(R.string.smartLock_administrator_fail_hint));
		}

		if(event.data==null){
			initItemsView();
		}

		if (StringUtil.equals(event.operType, "1")) {
			mDialogManager.dimissDialog(DOOR_LOCK_ACCOUNT_KEY, 0);
			SendMessage.sendControlDevMsg(gwID, devID, "14", "89", "34");
			refreshVeiwStatus(event.data);
		} else if (StringUtil.equals(event.operType, "3")) {
			switch (event.data.getString("result")) {
			case "-1":
				showResult = getString(R.string.smartLock_deleting_temporary_user_fail_hint);
				mDialogManager.dimissDialog(DOOR_LOCK_ACCOUNT_KEY, 0);
				break;
			case "0":
				showResult = getString(R.string.smartLock_deleting_temporary_user_success_hint);
				temporaryUserList.remove(temporaryUserList
						.get(currentTemporaryUserItemPosition));
				mDialogManager.dimissDialog(DOOR_LOCK_ACCOUNT_KEY, 0);
				initItemsView();
				break;
			}
			showResult(showResult);
		}
	}

	private void showResult(String showResult) {
		// 弹出含有动态密码的对话框
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(getString(R.string.gateway_router_setting_dialog_toast));
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(
				R.layout.device_door_lock_setting_account_dynamic, null);
		TextView textView = (TextView) view
				.findViewById(R.id.device_new_door_lock_account_dynamic_textview);
		textView.setText(showResult);

		builder.setContentView(view);
		builder.setPositiveButton(null);
		builder.setNegativeButton(null);
		WLDialog mMessageDialog = builder.create();
		mMessageDialog.show();
	}

	public void onEventMainThread(DeviceEvent event) {
		if(event.deviceInfo!=null){
			DeviceCache deviceCache = DeviceCache.getInstance(getActivity());
			WulianDevice wulianDevice = deviceCache.getDeviceByID(getActivity(),
					gwID, devID);
			String epData = wulianDevice.getDeviceInfo().getDevEPInfo().getEpData();
			if (epData.startsWith("080C")) {
				String temporaryUserSwitchStatus = epData.substring(12, 14);
				if (temporaryUserSwitchStatus.equals("00")) {
					isTemporaryInvalid = true;
					getSupportActionBar().setRightMenuClickListener(null);
					getSupportActionBar().setRightIcon(R.drawable.common_use_add_grey);
				} else if (temporaryUserSwitchStatus.equals("01")) {
					isTemporaryInvalid = false;
				}
				initItemsView();
			}
		}
	}

	// 比较时间大小
	private boolean compare_date(String DATE2, Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		try {
			Date dt2 = df.parse(DATE2);
			if (dt2.getTime() > date.getTime()) {
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}
}
