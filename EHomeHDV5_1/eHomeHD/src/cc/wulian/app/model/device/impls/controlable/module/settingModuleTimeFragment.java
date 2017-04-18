package cc.wulian.app.model.device.impls.controlable.module;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class settingModuleTimeFragment extends WulianFragment{

	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	
	public static WL_93_Module_Color_Light moduleLight;
	private String gwID;
	private String deviceID;
	private WulianDevice mDevice;
	
//	protected BaseActivity mActivity;
	
	private SettingModuleTimeView timingSettingView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getArguments();
		gwID = (String) bundle.getString(GWID);
		deviceID = (String) bundle.getString(DEVICEID);
		mDevice = DeviceCache.getInstance(mActivity).getDeviceByID(mActivity,gwID, deviceID);
		moduleLight = (WL_93_Module_Color_Light) mDevice;
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_type_93));
		getSupportActionBar().setRightIconText(
				getResources().getString(R.string.device_ir_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						moduleLight.createControlOrSetDeviceSendData(WL_93_Module_Color_Light.DEVICE_OPERATION_CTRL, 3 + timingSettingView.getSettingTime(),true);
						mActivity.finish();
					}
				});

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		timingSettingView = new SettingModuleTimeView(mActivity);
		return timingSettingView;
	}
	
	
}
