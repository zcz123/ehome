package cc.wulian.app.model.device.impls.alarmable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.sensorable.Sensorable;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * 报警状态(0:正常,1:报警)<br/>
 * 光感:int(单位LUX)
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_MOTION_LIGHT_S }, category = Category.C_SECURITY)
public class WL_40_Motion_Light_S extends AlarmableDeviceImpl implements OnClickListener,Sensorable{
	private static final String UNIT_LUX = " LUX";
	private ProgressBar mSecurityAlarm;
	private ImageView mSecurityNormal;
	private ImageView mSecuritySetUp;
	private ImageView mSecurityUnSetUp;
//	private FrameLayout mRemoveAlarmLayout;
//	private ImageView mRemoveAlarmView;
	
	private TextView mSecurityLight1;
	private TextView mSecurityLight2;
	private TextView mSecurityLight3;
	private TextView mSecurityLight4;
	private TextView mSecurityLight5;

	private static final int SMALL_ALARM_D = R.drawable.device_motion_light_sensor_alarm;
	private static final int SMALL_UNDEFENSE_D = R.drawable.device_motion_light_sensor_disarm;


	private LinearLayout mLightIntensity;
	private LinearLayout mLightTextLayout;

	public WL_40_Motion_Light_S(Context context, String type) {
		super(context, type);
	}
	
	
	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
	}
	
	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		Drawable normalStateDrawable = getDrawable(R.drawable.device_pir_sensor_normal_icon);
		drawables[0] = normalStateDrawable;
		return drawables;
	}

	@Override
	public synchronized void onDeviceData(String gwID, String devID,
			DeviceEPInfo devEPInfo,String cmd,String mode) {
		String ep = devEPInfo.getEp();
		WulianDevice device = getChildDevice(ep);
		if (device != null) {
			device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			removeCallbacks(mRefreshStateRunnable);
			post(mRefreshStateRunnable);
			fireDeviceRequestControlData();
		}else{
			super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
		}
	}

//	public boolean isNullChildDevice() {
//		return deviceMap.containsKey(EP_14);
//	}

	@Override
	public boolean isDefenseSetup() {
		WulianDevice device  = getChildDevice(EP_14);
		if(device instanceof Defenseable){
			Defenseable defensse = (Defenseable)device;
			return defensse.isDefenseSetup();
		}
		return false;
	}

	@Override
	public boolean isDefenseUnSetup() {
		WulianDevice device  = getChildDevice(EP_14);
		if(device instanceof Defenseable){
			Defenseable defensse = (Defenseable)device;
			return defensse.isDefenseUnSetup();
		}

		return false;
	}

	@Override
	public String getDefenseSetupCmd() {
		WulianDevice device  = getChildDevice(EP_14);
		if(device instanceof Defenseable){
			Defenseable defensse = (Defenseable)device;
			return defensse.getDefenseSetupCmd();
		}
		return "";
	}

	@Override
	public String getDefenseUnSetupCmd() {
		WulianDevice device  = getChildDevice(EP_14);
		if(device instanceof Defenseable){
			Defenseable defensse = (Defenseable)device;
			return defensse.getDefenseUnSetupCmd();
		}

		return "";
	}

	@Override
	public String getDefenseSetupProtocol() {
		WulianDevice device  = getChildDevice(EP_14);
		if(device instanceof Defenseable){
			Defenseable defensse = (Defenseable)device;
			return defensse.getDefenseSetupProtocol();
		}
		return "";
	}

	@Override
	public String getDefenseUnSetupProtocol() {
		WulianDevice device  = getChildDevice(EP_14);
		if(device instanceof Defenseable){
			Defenseable defensse = (Defenseable)device;
			return defensse.getDefenseUnSetupProtocol();
		}
		return "";
	}

	@Override
	public boolean isAlarming() {
		String epData = getChildDevice(EP_14).getDeviceInfo().getDevEPInfo().getEpData();
		String epStatus = getChildDevice(EP_14).getDeviceInfo().getDevEPInfo().getEpStatus();
		if (isNull(epData))
			return false;
		if (StringUtil.toInteger(epStatus) == 0)
			return false;
		return DATA_ALARM_STATE_ALARM_1.equals(epData);
	}
	@Override
	public boolean isNormal() {
		WulianDevice device  = getChildDevice(EP_14);
		if(device instanceof Defenseable){
			AlarmableDeviceImpl defensse = (AlarmableDeviceImpl) device;
			return defensse.isNormal();
		}

		return false;
	}

	@Override
	public String getAlarmProtocol() {
		WulianDevice device  = getChildDevice(EP_14);
		if(device instanceof Defenseable){
			AlarmableDeviceImpl defensse = (AlarmableDeviceImpl) device;
			return defensse.getAlarmProtocol();
		}
		return getAlarmProtocol();
	}

	@Override
	public String getNormalProtocol() {
		WulianDevice device  = getChildDevice(EP_14);
		if(device instanceof Defenseable){
			AlarmableDeviceImpl defensse = (AlarmableDeviceImpl) device;
			return defensse.getNormalProtocol();
		}
		return getNormalProtocol();
	}

	 @Override
		public CharSequence parseDataWithProtocol(String epData) {
		 if(StringUtil.isNullOrEmpty(epData) || DATA_ALARM_STATE_NORMAL_0.equals(epData))
			 return null;
		 if(DATA_ALARM_STATE_ALARM_1.equals(epData)){
			 return super.parseDataWithProtocol(epData);
		 }else{
				 WulianDevice device = getChildDevice(EP_15);
					String mData = device.getDeviceInfo().getDevEPInfo().getEpData();
					return mData + " "+ "LUX";
		 }
		}
	@Override
	public Drawable getStateSmallIcon() {
		return isDefenseUnSetup() ? getDrawable(SMALL_UNDEFENSE_D)
				: isAlarming() ? getDrawable(SMALL_ALARM_D)
						: WL_40_Motion_Light_S.this.getDefaultStateSmallIcon();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_security_alarm_common_layout, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		mSecurityAlarm = (ProgressBar) view.findViewById(R.id.device_security_alarm);
		mSecurityNormal = (ImageView) view.findViewById(R.id.device_security_normal);
//		mRemoveAlarmLayout = (FrameLayout) view.findViewById(R.id.device_security_remove);
//		mRemoveAlarmLayout.setVisibility(View.VISIBLE);
//		mRemoveAlarmView = (ImageView) view.findViewById(R.id.device_security_remove_image);
		
		mSecuritySetUp = (ImageView) view.findViewById(R.id.device_security_setup);
		mSecurityUnSetUp = (ImageView) view.findViewById(R.id.device_security_unsetup);
		mSecuritySetUp.setOnClickListener(this);
		mSecurityUnSetUp.setOnClickListener(this);
//		mRemoveAlarmView.setOnClickListener(this);
		
		mLightIntensity = (LinearLayout) view.findViewById(R.id.device_security_center_under);
		mLightTextLayout = (LinearLayout) view.findViewById(R.id.device_security_text);
		mLightIntensity.setVisibility(View.VISIBLE);
		mLightTextLayout.setVisibility(View.VISIBLE);
		mSecurityLight1 = (TextView) view.findViewById(R.id.device_security_light_1);
		mSecurityLight2 = (TextView) view.findViewById(R.id.device_security_light_2);
		mSecurityLight3 = (TextView) view.findViewById(R.id.device_security_light_3);
		mSecurityLight4 = (TextView) view.findViewById(R.id.device_security_light_4);
		mSecurityLight5 = (TextView) view.findViewById(R.id.device_security_light_5);
		WulianDevice device = getChildDevice(EP_15);
		if(device != null){
			String epType = device.getDeviceInfo().getDevEPInfo().getEpType();
			controlDeviceWidthEpData(EP_15, epType, "12");
		}
	}

	@Override
	public void initViewStatus() {
		Drawable drawable = getStateBigPictureArray()[0];
		mSecurityNormal.setImageDrawable(drawable);
		drawable = mSecurityNormal.getDrawable();
		
		if (isDefenseUnSetup()) {
			mSecurityUnSetUp.setVisibility(View.VISIBLE);
			mSecuritySetUp.setVisibility(View.INVISIBLE);
			mSecurityAlarm.setVisibility(View.INVISIBLE);
		} else {
			mSecuritySetUp.setVisibility(View.VISIBLE);
			mSecurityUnSetUp.setVisibility(View.INVISIBLE);
			if (isNormal()) {
				mSecurityAlarm.setVisibility(View.INVISIBLE);
			} else if (isAlarming()) {
				mSecurityAlarm.setVisibility(View.VISIBLE);
			}
		}
		
		WulianDevice device = getChildDevice(EP_15);
		String mData = "0";
		if(device != null){
			mData = device.getDeviceInfo().getDevEPInfo().getEpData();
		}
		if(!StringUtil.isNullOrEmpty(mData)){
			int mDataInt = StringUtil.toInteger(mData);
			if(mDataInt >= 0 && mDataInt<= 100){
				mSecurityLight1.setVisibility(View.VISIBLE);
				mSecurityLight1.setText(getResources().getString(R.string.device_security_light_1));
			}else if(mDataInt > 100 && mDataInt<= 200){
				mSecurityLight1.setVisibility(View.VISIBLE);
				mSecurityLight2.setVisibility(View.VISIBLE);
				mSecurityLight2.setText(getResources().getString(R.string.device_security_light_2));
			}else if(mDataInt > 200 && mDataInt<= 300){
				mSecurityLight1.setVisibility(View.VISIBLE);
				mSecurityLight2.setVisibility(View.VISIBLE);
				mSecurityLight3.setVisibility(View.VISIBLE);
				mSecurityLight3.setText(getResources().getString(R.string.device_security_light_3));
			}else if(mDataInt > 300 && mDataInt<= 500){
				mSecurityLight1.setVisibility(View.VISIBLE);
				mSecurityLight2.setVisibility(View.VISIBLE);
				mSecurityLight3.setVisibility(View.VISIBLE);
				mSecurityLight4.setVisibility(View.VISIBLE);
				mSecurityLight4.setText(getResources().getString(R.string.device_security_light_4));
			}else if(mDataInt> 500){
				mSecurityLight1.setVisibility(View.VISIBLE);
				mSecurityLight2.setVisibility(View.VISIBLE);
				mSecurityLight3.setVisibility(View.VISIBLE);
				mSecurityLight4.setVisibility(View.VISIBLE);
				mSecurityLight5.setVisibility(View.VISIBLE);
				mSecurityLight5.setText(getResources().getString(R.string.device_security_light_5));
			}
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.device_security_setup:
			WulianDevice device0 = getChildDevice(EP_14);
			fireWulianDeviceRequestControlSelf();
			controlDevice(EP_14, device0.getDeviceInfo().getDevEPInfo().getEpType(), DEFENSE_STATE_UNSET_0);
			break;
		case R.id.device_security_unsetup:
			WulianDevice device1 = getChildDevice(EP_14);
			fireWulianDeviceRequestControlSelf();
			controlDevice(EP_14, device1.getDeviceInfo().getDevEPInfo().getEpType(), DEFENSE_STATE_SET_1);
			break;
		default:
			break;
		}
		
	}

	@Override
	public CharSequence parseAlarmProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		sb.append(DeviceTool.getDeviceAlarmAreaName(this));
		sb.append(DeviceTool.getDeviceShowName(this));
		if(LanguageUtil.isChina() || LanguageUtil.isTaiWan()){
			sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
		}else{
			sb.append(" "+ mContext.getString(R.string.home_device_alarm_default_voice_detect) + " ");
		}
		sb.append(mContext.getString(R.string.home_device_alarm_type_02_voice));
		return sb.toString();
	}
	@Override
	public String checkDataRatioFlag() {
		return Messages.SMILE_DEFAULT;
	}

	@Override
	public String unit(String ep,String epType) {
		return UNIT_LUX;
	}

	@Override
	public String unitName() {
		return mResources.getString(R.string.device_illumination);
	}
	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_type_02_voice);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.device_state_normal);
	}
}