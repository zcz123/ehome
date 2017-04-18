package cc.wulian.app.model.device.impls.controlable.module;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

public class ModuleLightSettingFragment extends WulianFragment{

	private WulianDevice MuduleDevice;
	private LinearLayout mMOduleLayout;
	
	public static final String MODULE_LIGHT_COLOR = "MODULE_LIGHT_COLOR";
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initEditDevice();
		MuduleDevice.onAttachView(mActivity);
		initBar();
	}

	private void initEditDevice() {
		String gwID = getArguments().getString(GWID);
		String devID = getArguments().getString(DEVICEID);
		MuduleDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
				mActivity, gwID, devID);
	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.device_module_light_setting_layout, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMOduleLayout = (LinearLayout) view
				.findViewById(R.id.device_module_light_setting_ll);
		View settingDoorView = MuduleDevice.onCreateSettingView(
				LayoutInflater.from(mActivity), null);
		mMOduleLayout.addView(settingDoorView);

	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_ir_setting));
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {

					@Override
					public void onClick(View v) {
						mActivity.finish();
					}
				});
	}
}
