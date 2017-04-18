package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;


public class EditDoorLock6WifiSettingFragment extends WulianFragment implements
		OnClickListener {
	private static final String TAG = "IOTCamera";
	private Context mContext;
	// private DoorLockAlarmSettingAdapter settingDoorLockAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//initEditDevice();
		intiBar();
	}

	private void initEditDevice() {
		
		
		Log.i(TAG, "-------------");
//		mToken = getArguments().getString("token");
//		gwID = getArguments().getString(GWID);
//		devID = getArguments().getString(DEVICEID);
//		DoorDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
//				mActivity, gwID, devID);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View contentView = inflater.inflate(
				R.layout.device_new_door_lock_setting_version, container, false);
		ViewUtils.inject(this, contentView);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		
	}

	private void refreshVeiwStatus(JSONObject jsonObject) {
//		String h = jsonObject.getString("h");
//		String l = jsonObject.getString("l");
//		hInteger = stringToIntArray(h);
//		lInteger = stringToIntArray(l);
//		initItem();
	}

	

	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void intiBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("设置");
		getSupportActionBar().setTitle("查看版本信息");
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
	}

	@Override
	public void onResume() {
		super.onResume();

	}




	


	@Override
	public void onClick(View arg0) {
		
		}
	

}

