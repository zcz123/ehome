package cc.wulian.app.model.device.impls.controlable.musicbox;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceRequestListener;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class DeviceMusicBoxFragment extends WulianFragment {

	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	public static final String DEVICE_MUSIC_BOX = "DEVICE_MUSIC_BOX";

	private EditText mEditTextName;
	private EditText mEditTextPassword;
	private static String wifiName;
	private static String wifiPassword;
	private String hhname;
	private String hhpassword;

	private String gwID;
	private String deviceID;
	private WulianDevice musicDevice;
	// 判断wifi返回值
	public static boolean ob;
	public static boolean oc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		gwID = (String) bundle.getString(GWID);
		deviceID = (String) bundle.getString(DEVICEID);
		musicDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
				mActivity, gwID, deviceID);

		musicDevice
				.registerControlRequestListener(new OnWulianDeviceRequestListener() {

					@Override
					public void onDeviceRequestControlSelf(WulianDevice device) {

					}

					@Override
					public void onDeviceRequestControlData(WulianDevice device) {
						String epData = device.getDeviceInfo().getDevEPInfo().getEpData();
						if (epData.startsWith("0B")) {
							ob = true;
							if (ob && oc) {
								ob = false;
								mActivity.finish();

							}

						} else if (epData.startsWith("0C")) {
							oc = true;
							if (ob && oc) {
								oc = false;
								mActivity.finish();

							}
						}

					}
				});

		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_type_E4));
		getSupportActionBar().setRightIconText(
				getResources().getString(R.string.device_ir_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						saveIRKeys();
					}
				});

	}

	private void saveIRKeys() {

		wifiName = mEditTextName.getText().toString();
		wifiPassword = mEditTextPassword.getText().toString();
		hhname = StringUtil.toDecimalString(wifiName.length(), 2);
		hhpassword = StringUtil.toDecimalString(wifiPassword.length(), 2);

		if (!StringUtil.isNullOrEmpty(wifiName)) {
			String strName = "B" + hhname + wifiName;
			String strPassword = "C" + hhpassword + wifiPassword;

			musicDevice.controlDevice(musicDevice.getDeviceInfo()
					.getDevEPInfo().getEp(), musicDevice.getDeviceInfo()
					.getDevEPInfo().getEpType(), strName);
			musicDevice.controlDevice(musicDevice.getDeviceInfo()
					.getDevEPInfo().getEp(), musicDevice.getDeviceInfo()
					.getDevEPInfo().getEpType(), strPassword);

		} else if (StringUtil.isNullOrEmpty(wifiName)) {
			Toast.makeText(getActivity(), R.string.device_E4_warning,
					Toast.LENGTH_SHORT).show();

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.device_musicbox_setting_fragment,
				null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEditTextName = (EditText) view
				.findViewById(R.id.device_musicbox_wifi_name_edit);
		mEditTextPassword = (EditText) view
				.findViewById(R.id.device_musicbox_wifi_password_edit);

		WifiManager wifi = (WifiManager) mActivity
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (!StringUtil.isNullOrEmpty(info.getSSID())) {
			String str = info.getSSID();
			if (str.startsWith("\"") && str.endsWith("\"")) {
				str = str.substring(1, str.length() - 1);

			}
			mEditTextName.setText(str);

		}
		ob = false;
		oc = false;

	}
}
