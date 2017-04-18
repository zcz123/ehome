package cc.wulian.smarthomev5.fragment.house;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;

public class HouseKeeperConditionSelectDeviceItem {

	private WulianDevice device;
	protected BaseActivity mActivity;
	protected LayoutInflater inflater;
	protected Resources mResources;
	protected LinearLayout lineLayout;
	
	private TextView deviceName;
	private TextView deviceRoominfo;
	private TextView deviceStatus;
	private LinearLayout deviceLayout;
	
	public HouseKeeperConditionSelectDeviceItem(BaseActivity mActivity, final WulianDevice device){
		
		this.device = device;
		this.mActivity = mActivity;
		inflater = LayoutInflater.from(mActivity);
		mResources = mActivity.getResources();
		lineLayout = (LinearLayout) inflater.inflate(
				R.layout.task_manager_choose_device_item, null);
		deviceName = (TextView) lineLayout.findViewById(R.id.task_manager_device_name);
		deviceRoominfo = (TextView) lineLayout.findViewById(R.id.device_roominfo);
		deviceStatus = (TextView) lineLayout.findViewById(R.id.device_status);
		deviceLayout = (LinearLayout) lineLayout.findViewById(R.id.task_manager_device_detail_layout);
		initDeviceItem(device);
	}
	
	private void initDeviceItem(WulianDevice device) {
		DeviceAreaEntity entity = AreaGroupManager.getInstance().getDeviceAreaEntity(device.getDeviceGwID(),device.getDeviceRoomID());
		String  roomName = String.format("[%s]",entity.getName());
		if(device != null){
			if(StringUtil.isNullOrEmpty(device.getDeviceName())){
				deviceName.setText(device.getDefaultDeviceName());
				deviceRoominfo.setText(roomName);
			}else{
				deviceName.setText(device.getDeviceName());
				deviceRoominfo.setText(roomName);
			}
			if(!device.isDeviceOnLine()){
				deviceStatus.setVisibility(View.VISIBLE);
				deviceStatus.setText(mResources.getString(R.string.home_device_offline_red));
			}else {
				deviceStatus.setVisibility(View.GONE);
			}
		}
		
	}

	public View getView() {
		return lineLayout;
	}
}
