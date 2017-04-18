package cc.wulian.smarthomev5.fragment.house;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class HouseKeeperSelectSensorDeviceDataFragment extends WulianFragment {
	public static final String DEV_GW_ID = "extra_dev_gwID";
	public static final String DEV_ID = "extra_dev_ID";
	public static SelectSensorDeviceDataListener listener;
	private WulianDevice device;
	private DeviceCache deviceCache;
	private DialogOrActivityHolder holder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceCache = DeviceCache.getInstance(mActivity);
		String gwID = getArguments().getString(DEV_GW_ID);
		String devID = getArguments().getString(DEV_ID);
		device = deviceCache.getDeviceByID(mActivity, gwID, devID);
		holder = device.onCreateHouseKeeperSelectSensorDeviceDataView(
				mActivity.getLayoutInflater(), HouseKeeperConditionSelectDeviceFragment.conditionInfo,true);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return holder.getContentView();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.about_back);
		getSupportActionBar().setTitle(holder.getFragementTitle());
	}
	public static void setSelectSensorDeviceDataListener(SelectSensorDeviceDataListener listener){
		HouseKeeperSelectSensorDeviceDataFragment.listener = listener;
	}
	public static void fireSelectDeviceDataListener(String ep,String epType,String value,String des){
		if(HouseKeeperSelectSensorDeviceDataFragment.listener != null){
			HouseKeeperSelectSensorDeviceDataFragment.listener.onSelectDeviceDataChanged(ep,epType,value, des);
		}
	}
	
	public interface SelectSensorDeviceDataListener{
		public void onSelectDeviceDataChanged(String ep,String epType,String value,String des);
	}
	
}
