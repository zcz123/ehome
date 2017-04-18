package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.wulian.iot.Config;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.LinkTaskSensorView;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.databases.entitys.Messages;

import static android.content.Context.MODE_PRIVATE;

public abstract class SensorableDeviceImpl extends AbstractDevice implements Sensorable
{
	public static String FLAG_RATIO_DEFAULT = Messages.SMILE_DEFAULT;
	public static String FLAG_RATIO_NORMAL = Messages.SMILE_A;
	public static String FLAG_RATIO_MID = Messages.SMILE_B;
	public static String FLAG_RATIO_ALARM = Messages.SMILE_C;
	public static String FLAG_RATIO_BAD = Messages.SMILE_D;
	protected static final String UNIT_NA 	= "N/A";
	private SharedPreferences sharedPreferences;
	private boolean isEnvironmentChecked;
	
	protected String ep;
	protected String epType;
	protected String epData;
	protected String epStatus;
	public SensorableDeviceImpl( Context context, String type )
	{
		super(context, type);
	}
	
	public Drawable getSensorStateSmallIcon(){
		return null;
	}
	@Override
	public void refreshDevice(){
		DeviceEPInfo epInfo = getCurrentEpInfo();
		ep = epInfo.getEp();
		epType = epInfo.getEpType();
		epData = epInfo.getEpData();
		epStatus = epInfo.getEpStatus();
	}
	@Override
	public Drawable getStateSmallIcon(){
		Drawable icon = super.getStateSmallIcon();
		if (icon == super.getDefaultStateSmallIcon()){
			Drawable sensorIcon = getSensorStateSmallIcon();
			if (sensorIcon != null) icon = sensorIcon;
		}
		return icon;
	}
	
	@Override
	public boolean isLinkControl() {
		return true;
	}

	@Override
	public boolean isAutoControl(boolean isNormal) {
		return false;
	}

	@Override
	public String checkDataRatioFlag() {
		return Messages.SMILE_DEFAULT;
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater) {
		return getSensorableDeviceShortCutView(item, inflater);
	}
	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		AbstractLinkTaskView taskView = new LinkTaskSensorView(context, taskInfo);
		taskView.onCreateView();
		return taskView;
	}
	@Override
	public String unit(String ep,String epType) {
		return "";
	}

	@Override
	public String unitName() {
		return "";
	}
	public static class SensorableDeviceShortCutControlItem extends DeviceShortCutControlItem{

		private TextView textView;
		public SensorableDeviceShortCutControlItem(Context context) {
			super(context);
			textView = (TextView)inflater.inflate(R.layout.device_short_cut_control_sensor, null);
			controlLineLayout.addView(textView);
		}
		
		@Override
		public void setWulianDevice(WulianDevice device) {
			super.setWulianDevice(device);
			boolean isNotNull=device!=null
							&&device.getDeviceInfo()!=null
							&&device.getDeviceInfo().getDevEPInfo()!=null
							&&device.getDeviceInfo().getDevEPInfo().getEpData()!=null;
			if(isNotNull){
				textView.setText(device.parseDataWithProtocol(device.getDeviceInfo().getDevEPInfo().getEpData()));
			}

		}

		
	}
	//桌面摄像机环境监测数据发送（D4,D5,D6,A0,17) add by hxc
	public void sendDataToDesktop(String epData, String epType) {
		sharedPreferences = mContext.getSharedPreferences(Config.COMMON_SHARED, MODE_PRIVATE);
		isEnvironmentChecked = sharedPreferences.getBoolean("status", false);
		if(isEnvironmentChecked){
			Intent it = new Intent();
			it.setAction("sendepData");
			it.putExtra("epData", epData);
			it.putExtra("epType", epType);
			mContext.sendBroadcast(it);
		}
	}
}
