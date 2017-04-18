package cc.wulian.app.model.device.impls.controlable.led;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class SettingColorTimeFragment extends WulianFragment{

	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	
	public static WL_90_Light_Led led;
	private String gwID;
	private String deviceID;
	private WulianDevice mDevice;
	
//	protected BaseActivity mActivity;
	
	private SettingColorTimeView timingSettingView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getArguments();
		gwID = (String) bundle.getString(GWID);
		deviceID = (String) bundle.getString(DEVICEID);
		mDevice = DeviceCache.getInstance(mActivity).getDeviceByID(mActivity,gwID, deviceID);
		led = (WL_90_Light_Led) mDevice;
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_type_90));
		getSupportActionBar().setRightIconText(
				getResources().getString(R.string.device_ir_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						led.createControlOrSetDeviceSendData(WL_90_Light_Led.DEVICE_OPERATION_CTRL, 3 + timingSettingView.getSettingTime(),true);
						mActivity.finish();
					}
				});

	}


	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		timingSettingView = new SettingColorTimeView(mActivity);
		timingSettingView.setTime(((WL_90_Light_Led)mDevice).time);
		return timingSettingView;
	}

}
