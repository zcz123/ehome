package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.doorlock.AbstractDoorLockAlarmItem;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EditMonitorInfoActivity;
import cc.wulian.smarthomev5.adapter.DoorLockAlarmSettingAdapter;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.event.NewDoorLockEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.monitor.EditMonitorInfoFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class EditDoorLock6AlarmFragment extends WulianFragment implements
		OnClickListener {
	private Context mContext;
	@ViewInject(R.id.device_new_door_lock_setting_alarm_defense_btn)
	private Button mAlarmDefencseButton;
	@ViewInject(R.id.device_new_door_lock_setting_alarm_undefense_btn)
	private Button mAlarmUndefencseButton;
	@ViewInject(R.id.device_new_door_lock_setting_alarm_layout)
	private LinearLayout mLinearLayout;
	// private DoorLockAlarmSettingAdapter settingDoorLockAdapter;

	private String mToken;

	private boolean isDenfence = true;

	String[] itemNames={};

	int[] hInteger = {  0, 0, 0, 0, 0, 0, 0, 0 ,0,0};
	int[] lInteger = {  0, 0, 0, 0, 0, 0, 0, 0 ,0,0};

	private WulianDevice DoorDevice;
	private LinearLayout mDoorLayout;
	private String gwID;
	private String devID;
	public static final String DEVICE_DOOR_LOCK_4 = "DEVICE_DOOR_LOCK_4";
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initEditDevice();
		intiBar();
	}

	private void initEditDevice() {
		mToken = getArguments().getString("token");
		gwID = getArguments().getString(GWID);
		devID = getArguments().getString(DEVICEID);
		DoorDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
				mActivity, gwID, devID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View contentView = inflater.inflate(
				R.layout.device_new_door_lock_setting_alarm, container, false);
		ViewUtils.inject(this, contentView);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		String []newItemNames={ getString(R.string.device_lock_op_app_alarm), getString(R.string.smartLock_safing_lock_door)+getString(R.string.device_state_alarm),getString(R.string.smartLock_safing_unlock_door)+getString(R.string.device_state_alarm), getString(R.string.device_lock_op_card_alarm), getString(R.string.device_lock_op_password_alarm),
				getString(R.string.device_lock_op_finger_alarm), getString(R.string.device_lock_op_locked_alarm), getString(R.string.device_lock_op_lift_locked_alarm),getString(R.string.smartLock_closedoor_alarm), getString(R.string.smartLock_door_unclosed_alarm)};
		itemNames=newItemNames;
		mAlarmDefencseButton
				.setBackgroundResource(R.drawable.device_musicbox_sd_pressed);
		// settingDoorLockAdapter = new DoorLockAlarmSettingAdapter(mActivity);
		// mAlarmListView.setAdapter(settingDoorLockAdapter);
		mAlarmDefencseButton.setOnClickListener(this);
		mAlarmUndefencseButton.setOnClickListener(this);
		initItem();
	}

	private void refreshVeiwStatus(JSONObject jsonObject) {
		String h = jsonObject.getString("h").substring(0,7)+jsonObject.getString("h").substring(8);
		h=h.substring(0,2)+jsonObject.getString("h").substring(7,8)+h.substring(2);
		String l = jsonObject.getString("l").substring(0,7)+jsonObject.getString("l").substring(8);
		l=l.substring(0,2)+jsonObject.getString("l").substring(7,8)+l.substring(2);
		hInteger = stringToIntArray(h);
		lInteger = stringToIntArray(l);
		initItem();
	}

	private int[] stringToIntArray(String str) {
		System.out.println(str);
		char[] cs = str.toCharArray();
		int[] is = { 1, 1, 1, 1, 1, 1 , 1, 1, 1, 1};
		for (int i = 0; i < cs.length; i++) {
			is[i] = Integer.valueOf(cs[i] + "");
		}
		for (int s : is) {
			System.out.print(s);
		}
		return is;
	}

	private String IntArrayToString(int[] array) {
		String str = "";
		String threePosition="";
		for (int i = 0; i < array.length; i++) {
			if(i==2){
				threePosition=array[i]+"";
				continue;
			}
			str = str + array[i];
			if(i==7){
				str=str+threePosition;
			}
		}
		return str;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void intiBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(getString(R.string.set_titel));
		getSupportActionBar().setTitle(getString(R.string.device_lock_op_alarm_setting));
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("token", mToken);
		NetSDK.sendSetDoorLockData(gwID, devID, "12", jsonObject);
	}

	private void initItem() {
		mLinearLayout.removeAllViews();
		for (int i = 0; i < itemNames.length; i++) {
			mLinearLayout.addView(addOtherCameraView(i, itemNames[i],
					isDenfence ? hInteger[i] : lInteger[i]));
		}
	}

	private View addOtherCameraView(final int itemID, String itemName,
			final int status) {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(
				R.layout.device_door_lock_setting_alarm_remind, null);
		final TextView otherCameraNameTextView = (TextView) view
				.findViewById(R.id.monitor_name);

		ImageView iconImageView = (ImageView) view
				.findViewById(R.id.device_door_item_name_iv);
		iconImageView.setImageDrawable(null);
		TextView nameTextView = (TextView) view
				.findViewById(R.id.device_door_item_name_tv);
		nameTextView.setText(itemName);
		ToggleButton chooseToggleButton = (ToggleButton) view
				.findViewById(R.id.device_door_item_switch);
		iconImageView.setVisibility(View.GONE);
		chooseToggleButton.setChecked(status == 1);
		chooseToggleButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("token", mToken);
						if (isDenfence) {
							if (status == 1) {
								hInteger[itemID] = 0;
							} else {
								hInteger[itemID] = 1;
							}
						} else {
							if (status == 1) {
								lInteger[itemID] = 0;
							} else {
								lInteger[itemID] = 1;
							}
						}
						//默认的第一位为逗留检测，始终为打开
						jsonObject.put("h", IntArrayToString(hInteger));
						jsonObject.put("l",IntArrayToString(lInteger));
						NetSDK.sendSetDoorLockData(gwID, devID, "13",
								jsonObject);
					}
				});
		view.setLayoutParams(lp);
		return view;
	}

	public void onEventMainThread(NewDoorLockEvent event) {
		if (StringUtil.equals(event.operType, "12")) {
			if (StringUtil.isNullOrEmpty(event.data.getString("h"))
					&& StringUtil.isNullOrEmpty(event.data.getString("l"))) {
				return;
			} else {
				refreshVeiwStatus(event.data);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.device_new_door_lock_setting_alarm_defense_btn:
			mAlarmDefencseButton
					.setBackgroundResource(R.drawable.device_musicbox_sd_pressed);
			mAlarmUndefencseButton
					.setBackgroundResource(R.drawable.device_musicbox_sta_normal);
			isDenfence = true;
			initItem();
			break;
		case R.id.device_new_door_lock_setting_alarm_undefense_btn:
			isDenfence = false;
			mAlarmDefencseButton
					.setBackgroundResource(R.drawable.device_musicbox_sd_normal);
			mAlarmUndefencseButton
					.setBackgroundResource(R.drawable.device_musicbox_sta_pressed);
			initItem();
			break;
		default:
			break;
		}
	}

}
