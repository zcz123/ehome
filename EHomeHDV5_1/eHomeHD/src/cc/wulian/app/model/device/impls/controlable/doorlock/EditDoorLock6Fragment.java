package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wulian.iot.Config;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye.HawkeyeReadyConnectionActivity;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingItemClickActivity;
import cc.wulian.smarthomev5.activity.iotc.config.IOTCDevConfigActivity;
import cc.wulian.smarthomev5.activity.iotc.res.IOTCDevConfigWinActivity;
import cc.wulian.smarthomev5.adapter.DoorLockAlarmSettingAdapter;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.event.DeviceBindSceneEvent;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

public class EditDoorLock6Fragment extends WulianFragment implements
		OnClickListener, OnCheckedChangeListener {

	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	private static final String TAG = "IOTCamera";
	public static final String DEVICE_DOOR_LOCK_TYPE = "device_door_lock_type";
	public static final String DOOR_LOCK_EDITDOORLOCK_KEY = "device_door_lock_type";
	public static final String TOKEN = "token";
	private String tutkUid=null;
	private DoorLockAlarmSettingAdapter settingDoorLockAdapter;
	private static String gwID;
	private static String devID;
	private static String mToken;
	
	@ViewInject(R.id.device_door_lock_setting_account)
	private RelativeLayout mAccountRelativeLayout;
	@ViewInject(R.id.device_door_lock_setting_alarm)
	private RelativeLayout mAlarmRelativeLayout;
	@ViewInject(R.id.device_door_lock_setting_stay)
	private RelativeLayout mStayRelativeLayout;
	@ViewInject(R.id.device_door_lock_setting_video)
	private RelativeLayout mVideoRelativeLayout;
	@ViewInject(R.id.device_door_lock_setting_scene)
	private RelativeLayout mSceneRelativeLayout;
	@ViewInject(R.id.device_door_lock_setting_history)
	private RelativeLayout mHistoryRelativeLayout;
	@ViewInject(R.id.device_door_lock_setting_wifi)
	private RelativeLayout mWifiRelativeLayout;
	@ViewInject(R.id.device_door_lock_setting_version)
	private RelativeLayout mVersionRelativeLayout;
	
	private TextView settingSceneItemDescripeTextView;
	private SceneDao sceneDao = SceneDao.getInstance();
	private ToggleButton settingStaySwitchButton;
	private ToggleButton settingVideoSwitchButton;
	private String bindSceneID="";

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initEditDevice();
		settingDoorLockAdapter = new DoorLockAlarmSettingAdapter(mActivity);
		Bundle bundle = getActivity().getIntent().getExtras();
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(getString(R.string.set_titel));
		getSupportActionBar().setRightIcon(null);
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {
					@Override
					public void onClick(View v) {
						// DeviceDetailsActivity.instance.finish();
						// Bundle args = new Bundle();
						// args.putString(DeviceDetailsFragment.EXTRA_DEV_GW_ID,
						// DoorDevice.getDeviceGwID());
						// args.putString(DeviceDetailsFragment.EXTRA_DEV_ID,
						// DoorDevice.getDeviceID());
						// Intent intent = new Intent();
						// intent.setClass(mActivity,
						// DeviceDetailsActivity.class);
						// if (args != null)
						// intent.putExtras(args);
						// mActivity.startActivity(intent);
						mActivity.finish();

					}
				});
	}

	private void initEditDevice() {
		gwID = getArguments().getString(GWID);
		devID = getArguments().getString(DEVICEID);
		mToken = getArguments().getString(TOKEN);
		tutkUid=getArguments().getString(Config.tutkUid);
		Log.e("DoorLock", mToken+" "+gwID+" "+devID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View contentView = inflater.inflate(
				R.layout.device_new_door_lock_setting, container, false);
		ViewUtils.inject(this, contentView);
		return contentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mDialogManager.showDialog(DOOR_LOCK_EDITDOORLOCK_KEY, mActivity, null, null);
		NetSDK.sendGetBindSceneMsg(gwID, devID);
		SendMessage.sendControlDevMsg(gwID, devID, "14", "89", "36");
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		TextView settingAccountItemProjectTextView = (TextView) mAccountRelativeLayout
				.findViewById(R.id.device_new_door_lock_setting_item_project_tv);
		settingAccountItemProjectTextView.setText(getString(R.string.device_lock_user_manage));
		TextView settingAlarmItemProjectTextView = (TextView) mAlarmRelativeLayout
				.findViewById(R.id.device_new_door_lock_setting_item_project_tv);
		settingAlarmItemProjectTextView.setText(getString(R.string.smartLock_alarm_setting));
		TextView settingStayItemProjectTextView = (TextView) mStayRelativeLayout
				.findViewById(R.id.device_new_door_lock_setting_item_project_tv);
		settingStaySwitchButton=(ToggleButton) mStayRelativeLayout.findViewById(R.id.device_new_door_lock_setting_item_switch);
		settingStaySwitchButton.setVisibility(View.VISIBLE);
		settingStayItemProjectTextView.setText(getString(R.string.smartLock_setting_stay_detection));
		TextView settingVideoItemProjectTextView = (TextView) mVideoRelativeLayout
				.findViewById(R.id.device_new_door_lock_setting_item_project_tv);
		settingVideoSwitchButton=(ToggleButton) mVideoRelativeLayout.findViewById(R.id.device_new_door_lock_setting_item_switch);
		settingVideoSwitchButton.setVisibility(View.VISIBLE);
		settingVideoItemProjectTextView.setVisibility(View.VISIBLE);
		settingVideoItemProjectTextView.setText(getString(R.string.videotape));
		TextView settingSceneItemProjectTextView = (TextView) mSceneRelativeLayout
				.findViewById(R.id.device_new_door_lock_setting_item_project_tv);
		settingSceneItemDescripeTextView = (TextView) mSceneRelativeLayout
				.findViewById(R.id.device_new_door_lock_setting_item_bind_tv);
		settingSceneItemDescripeTextView.setVisibility(View.VISIBLE);
		settingSceneItemProjectTextView.setText(getString(R.string.nav_scene_title)+getString(R.string.set_titel));
		TextView settingHistoryItemProjectTextView = (TextView) mHistoryRelativeLayout
				.findViewById(R.id.device_new_door_lock_setting_item_project_tv);
		settingHistoryItemProjectTextView.setText(getString(R.string.smartLock_unlock_history));
		TextView settingWifiItemProjectTextView = (TextView) mWifiRelativeLayout
				.findViewById(R.id.device_new_door_lock_setting_item_project_tv);
		settingWifiItemProjectTextView.setText(getString(R.string.setting_wifi_setting));
		TextView settingVersionItemProjectTextView = (TextView) mVersionRelativeLayout
				.findViewById(R.id.device_new_door_lock_setting_item_project_tv);
		settingVersionItemProjectTextView.setText(getString(R.string.camera_firmware_version));
		mAccountRelativeLayout.setOnClickListener(this);
		mAlarmRelativeLayout.setOnClickListener(this);
		mSceneRelativeLayout.setOnClickListener(this);
		mHistoryRelativeLayout.setOnClickListener(this);
		mWifiRelativeLayout.setOnClickListener(this);
		mVersionRelativeLayout.setOnClickListener(this);
		settingStaySwitchButton.setOnCheckedChangeListener(this);
		settingVideoSwitchButton.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(mActivity, DeviceSettingItemClickActivity.class);
		intent.putExtra(EditDoorLock6Fragment.GWID, gwID);
		intent.putExtra(EditDoorLock6Fragment.DEVICEID, devID);
		intent.putExtra(EditDoorLock6Fragment.DEVICE_DOOR_LOCK_TYPE, "89");
		intent.putExtra(EditDoorLock6Fragment.TOKEN, mToken);
		switch (arg0.getId()) {
		case R.id.device_door_lock_setting_account:
			intent.putExtra(
					DeviceSettingItemClickActivity.SETTING_ITEM_FRAGMENT_CLASSNAME,
					EditDoorLock6AccountFragment.class.getName());
			startActivity(intent);
			break;
		case R.id.device_door_lock_setting_alarm:
			intent.putExtra(
					DeviceSettingItemClickActivity.SETTING_ITEM_FRAGMENT_CLASSNAME,
					EditDoorLock6AlarmFragment.class.getName());
			startActivity(intent);
			break;
		case R.id.device_door_lock_setting_scene:
			intent.putExtra(
					EditDoorLock6SceneFragment.SCENEID,bindSceneID);
			intent.putExtra(
					EditDoorLock6SceneFragment.BINDED_SCENE_NAME,
					settingSceneItemDescripeTextView.getText().toString().trim());
			intent.putExtra(
					DeviceSettingItemClickActivity.SETTING_ITEM_FRAGMENT_CLASSNAME,
					EditDoorLock6SceneFragment.class.getName());
			startActivity(intent);
			break;
		case R.id.device_door_lock_setting_history:
			intent.putExtra(
					DeviceSettingItemClickActivity.SETTING_ITEM_FRAGMENT_CLASSNAME,
					EditDoorLock6HistoryFragment.class.getName());
			startActivity(intent);
			break;
		case R.id.device_door_lock_setting_wifi:           //wifi 配置   		
			Log.i(TAG, "------------wifi 配置 ");
			Intent intentWifi = new Intent(mActivity, IOTCDevConfigActivity.class);
			intentWifi.putExtra(IOTCDevConfigActivity.WIFI_CONFIG_TYPE, 1);
			intentWifi.putExtra(IOTCDevConfigActivity.DOOR_89_DEVICEID, devID);
			Log.i("WL_89_DoorLock_6", "===EditDoorLock6Fragment=devID==="+devID);
			startActivity(intentWifi);
			break;
		case R.id.device_door_lock_setting_version:            //版本号获取。		
			Log.i(TAG, "------------版本号获取");
			if (Config.tutkUid==null){
				WLToast.showToast(getActivity(),getString(R.string.html_map_2107_error),0);
				return;
			}
			intent.putExtra(Config.tutkUid, tutkUid);
			intent.putExtra(
					DeviceSettingItemClickActivity.SETTING_ITEM_FRAGMENT_CLASSNAME,
					EditDoorLock6VersionFragment.class.getName());
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	public void onEventMainThread(DeviceBindSceneEvent event) {
		if(event.data!=null){
			JSONArray jsonArray=event.data;
			JSONObject jsonObject=(JSONObject) jsonArray.get(0);
			String sceneID=jsonObject.getString("sceneID");
			bindSceneID=sceneID;
			SceneInfo info = new SceneInfo();
			info.setGwID(mAccountManger.getmCurrentInfo().getGwID());
			List<SceneInfo> infos = sceneDao.findListAll(info);
			settingSceneItemDescripeTextView.setText(getString(R.string.device_no_bind));
			for(int i=0;i<infos.size();i++){
				if(infos.get(i).getSceneID().equals(sceneID)){
					settingSceneItemDescripeTextView.setVisibility(View.VISIBLE);
					settingSceneItemDescripeTextView.setText(infos.get(i).getName());
					
				}
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if(arg0==settingVideoSwitchButton){
			if(arg1){
				SendMessage.sendControlDevMsg(gwID, devID, "14", "89", "271");
			}else{
				SendMessage.sendControlDevMsg(gwID, devID, "14", "89", "270");
			}
		}
		if(arg0==settingStaySwitchButton){
			if(arg1){
				mVideoRelativeLayout.setVisibility(View.VISIBLE);
				SendMessage.sendControlDevMsg(gwID, devID, "14", "89", "281");
			}else{
				mVideoRelativeLayout.setVisibility(View.GONE);
				SendMessage.sendControlDevMsg(gwID, devID, "14", "89", "280");
			}
		}
	}
	
	public void onEventMainThread(DeviceEvent event) {
		String type = event.deviceInfo.getType();
		if(type.equals("89")){
			DeviceCache deviceCache = DeviceCache.getInstance(getActivity());
			WulianDevice wulianDevice = deviceCache.getDeviceByID(getActivity(),
					gwID, devID);
			String epData = wulianDevice.getDeviceInfo().getDevEPInfo().getEpData();
			if(epData!=null){
				switch (epData.substring(0, 4)) {
//			case "0226":
//				showResult("设置成功");
//				break;
					case "0A09":
						settingStaySwitchButton.setChecked(!settingStaySwitchButton.isChecked());
						showResult(getString(R.string.smartLock_setting_fail_hint));
						break;
//			case "0225":
//				showResult("设置成功");
//				break;
					case "0A08":
						settingVideoSwitchButton.setChecked(!settingVideoSwitchButton.isChecked());
						showResult(getString(R.string.smartLock_setting_fail_hint));
						break;
					case "080A":
						mDialogManager.dimissDialog(DOOR_LOCK_EDITDOORLOCK_KEY, 0);
						switch (epData.substring(4,6)) {
							case "01":
								mVideoRelativeLayout.setVisibility(View.VISIBLE);
								settingStaySwitchButton.setChecked(true);
								break;
							case "00":
								mVideoRelativeLayout.setVisibility(View.GONE);
								settingStaySwitchButton.setChecked(false);
								break;
						}
						switch (epData.substring(6)) {
							case "01":
								settingVideoSwitchButton.setChecked(true);
								break;
							case "00":
								settingVideoSwitchButton.setChecked(false);
								break;
						}
						break;

					default:
						break;
				}
			}
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
	
}
