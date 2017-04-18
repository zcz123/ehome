package cc.wulian.app.model.device.impls.alarmable;

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

import com.alibaba.fastjson.JSON;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.sensorable.Sensorable;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.HumanInductorChooseView;
import cc.wulian.app.model.device.utils.DeviceResource;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.LanguageUtil;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_HUMANINDUCTOR, DeviceResource.DEVICETYPE_AD}, category = Category.C_SECURITY)
public class WL_C0_Human_Inductor extends AlarmableDeviceImpl implements OnClickListener,Sensorable {

	public static final String PROTOCOL_ALARM = "06C00101";
	public static final String PROTOCOL_NORMAL = "06C00100";
	private static final String UNIT_C = "\u2103";
	private static final String UNIT_RH = "%";
	private static final String UNIT_LIGHT = "LUX";
	
	private static final int SMALL_NORMAL_H = R.drawable.device_human_inductor_disdefence;
	private static final int SMALL_DEFENCE_H = R.drawable.device_human_inductor_defence;
	
	private ProgressBar mSecurityAlarm;
	private ImageView mSecurityNormal;
	private ImageView mSecuritySetUp;
	private ImageView mSecurityUnSetUp;
	
	private TextView mSecurityLight1;
	private TextView mSecurityLight2;
	private TextView mSecurityLight3;
	private TextView mSecurityLight4;
	private TextView mSecurityLight5;
	
	private LinearLayout mLightIntensity;
	private LinearLayout mLightTextLayout;
	private LinearLayout mCeneterData;
	private TextView mSecurityTemp;
	private TextView mSecurityHumi;
	private TextView mSecurityLight;
	
	public WL_C0_Human_Inductor(Context context, String type) {
		super(context, type);
	}
	@Override
	public boolean isAlarming() {
		String epData = getChildDevice(EP_14).getDeviceInfo().getDevEPInfo().getEpData();
		String epStatus = getChildDevice(EP_14).getDeviceInfo().getDevEPInfo().getEpStatus();
		if (isNull(epData))
			return false;
		if (StringUtil.toInteger(epStatus) == 0)
			return false;
		return PROTOCOL_ALARM.equals(epData);
	}
	
	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	@Override
	public boolean isNormal() {
		return !isAlarming();
	}

	@Override
	public String getAlarmProtocol() {
		return PROTOCOL_ALARM;
	}

	@Override
	public String getNormalProtocol() {

		return PROTOCOL_NORMAL;
	}
	

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		Logger.debug("device up:"+JSON.toJSONString(devInfo));
		super.onDeviceUp(devInfo);
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

	@Override
	public Drawable getStateSmallIcon() {
		Drawable drawable = getDefaultStateSmallIcon();
		if (isDefenseSetup()) {
			drawable = getDrawable(SMALL_DEFENCE_H);

		} else if (isDefenseUnSetup()) {
			drawable = getDrawable(SMALL_NORMAL_H);
		}
		return drawable;
	}

	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		Drawable normalStateDrawable = getDrawable(R.drawable.device_human_inductor_alarm);
		drawables[0] = normalStateDrawable;
		return drawables;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_security_alarm_common_layout, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		if(getChildDevice(EP_15) != null){
			controlDeviceWidthEpData(EP_15, getChildDevice(EP_15).getDeviceInfo().getDevEPInfo().getEpType(), "04D142");
		}
		if(getChildDevice(EP_16) != null){
			controlDeviceWidthEpData(EP_16, getChildDevice(EP_16).getDeviceInfo().getDevEPInfo().getEpType(), "04D242");
		}
		if(getChildDevice(EP_17) != null){
			controlDeviceWidthEpData(EP_17, getChildDevice(EP_17).getDeviceInfo().getDevEPInfo().getEpType(), "04D342");
		}

		mSecurityAlarm = (ProgressBar) view.findViewById(R.id.device_security_alarm);
		mSecurityAlarm.setVisibility(View.INVISIBLE);
		mSecurityNormal = (ImageView) view.findViewById(R.id.device_security_normal);
		
		mSecuritySetUp = (ImageView) view.findViewById(R.id.device_security_setup);
		mSecurityUnSetUp = (ImageView) view.findViewById(R.id.device_security_unsetup);
		mSecuritySetUp.setOnClickListener(this);
		mSecurityUnSetUp.setOnClickListener(this);
		
		mLightIntensity = (LinearLayout) view.findViewById(R.id.device_security_center_under);
		mLightTextLayout = (LinearLayout) view.findViewById(R.id.device_security_text);
		mLightIntensity.setVisibility(View.VISIBLE);
		mLightTextLayout.setVisibility(View.VISIBLE);
		mSecurityLight1 = (TextView) view.findViewById(R.id.device_security_light_1);
		mSecurityLight2 = (TextView) view.findViewById(R.id.device_security_light_2);
		mSecurityLight3 = (TextView) view.findViewById(R.id.device_security_light_3);
		mSecurityLight4 = (TextView) view.findViewById(R.id.device_security_light_4);
		mSecurityLight5 = (TextView) view.findViewById(R.id.device_security_light_5);
		
		mCeneterData = (LinearLayout) view.findViewById(R.id.device_security_center);
		mCeneterData.setVisibility(View.VISIBLE);
		mSecurityTemp = (TextView) view.findViewById(R.id.device_security_temperature);
		mSecurityHumi = (TextView) view.findViewById(R.id.device_security_humidity);
		mSecurityLight = (TextView) view.findViewById(R.id.device_security_value_text);
		mSecurityLight.setVisibility(View.VISIBLE);
	}
	@Override
	public boolean isDefenseSetup() {
		String epStatus = getChildDevice(EP_14).getDeviceInfo().getDevEPInfo().getEpStatus();
		return StringUtil.equals(DEFENSE_STATE_SET_1, epStatus);
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

		if(getChildDevice(EP_15) != null){
			WulianDevice device1 = getChildDevice(EP_15);
			String mData1 = device1.getDeviceInfo().getDevEPInfo().getEpData();
			if(!StringUtil.isNullOrEmpty(mData1)){
				// 0AD1040200CA
				if (mData1.substring(6, 8).equals("02")) {
					String str = mData1.substring(8, 12);
					float f = StringUtil.toInteger(str, 16) / 10;
					String temp = String.valueOf(f);
					mSecurityTemp.setText(temp + "℃");
				} else if (mData1.substring(6, 8).equals("05")) {
					String str = mData1.substring(8, 12);
					float f = StringUtil.toInteger(str, 16) / 10;
					String temp = String.valueOf(f);
					mSecurityTemp.setText("-" + temp + "℃");
				}
			}
		}else{
			mSecurityTemp.setText(0 + "℃");
		}
		
		
		if(getChildDevice(EP_16) != null){
			WulianDevice device2 = getChildDevice(EP_16);
			String mData2 = device2.getDeviceInfo().getDevEPInfo().getEpData();
			if(!StringUtil.isNullOrEmpty(mData2)){
				if (mData2.substring(6, 8).equals("02")
						|| mData2.substring(6, 8).equals("01")) {
					// 0AD204010104
					String str = mData2.substring(8, 12);
					float f = StringUtil.toInteger(str, 16) / 10;
					String humidity = String.valueOf(f);
					mSecurityHumi.setText(humidity + "%RH");
				}
				
			}
		}else{
			mSecurityHumi.setText(0 + "%RH");
		}
		
//		if (epData.startsWith("0AD304") && epType.equals("D3")
//				&& epData.length() == 12) {
//			if (epData.substring(6, 8).equals("01")) {
//				String str = epData.substring(8, 12);
//				String intensity = StringUtil.toInteger(str, 16).toString();
//
//				
////				NetSDKProxy.sendControlDevMsg(gwID, devID, EP_17, epType, "04D342");
//			}
//		}
		if(getChildDevice(EP_17) != null){
			WulianDevice device3 = getChildDevice(EP_17);
			String mData3 = device3.getDeviceInfo().getDevEPInfo().getEpData();
			if(!StringUtil.isNullOrEmpty(mData3)){
				if (mData3.substring(6, 8).equals("01")) {
					String str = mData3.substring(8, 12);
					String intensity = StringUtil.toInteger(str, 16).toString();

					int mDataInt = StringUtil.toInteger(intensity);
					mSecurityLight.setText(String.valueOf(mDataInt) + "Lux");
					if(mDataInt >= 0 && mDataInt<= 100){
						mSecurityLight1.setVisibility(View.VISIBLE);
						mSecurityLight1.setText(getResources().getString(R.string.device_security_light_1));
					}else if(mDataInt > 100 && mDataInt<= 200){
						mSecurityLight1.setVisibility(View.VISIBLE);
						mSecurityLight2.setVisibility(View.VISIBLE);
						mSecurityLight1.setText("");
						mSecurityLight2.setText(getResources().getString(R.string.device_security_light_2));
					}else if(mDataInt > 200 && mDataInt<= 300){
						mSecurityLight1.setVisibility(View.VISIBLE);
						mSecurityLight2.setVisibility(View.VISIBLE);
						mSecurityLight3.setVisibility(View.VISIBLE);
						mSecurityLight1.setText("");
						mSecurityLight2.setText("");
						mSecurityLight3.setText(getResources().getString(R.string.device_security_light_3));
					}else if(mDataInt > 300 && mDataInt<= 500){
						mSecurityLight1.setVisibility(View.VISIBLE);
						mSecurityLight2.setVisibility(View.VISIBLE);
						mSecurityLight3.setVisibility(View.VISIBLE);
						mSecurityLight4.setVisibility(View.VISIBLE);
						mSecurityLight1.setText("");
						mSecurityLight2.setText("");
						mSecurityLight3.setText("");
						mSecurityLight4.setText(getResources().getString(R.string.device_security_light_4));
					}else if(mDataInt> 500){
						mSecurityLight1.setVisibility(View.VISIBLE);
						mSecurityLight2.setVisibility(View.VISIBLE);
						mSecurityLight3.setVisibility(View.VISIBLE);
						mSecurityLight4.setVisibility(View.VISIBLE);
						mSecurityLight5.setVisibility(View.VISIBLE);
						mSecurityLight1.setText("");
						mSecurityLight2.setText("");
						mSecurityLight3.setText("");
						mSecurityLight4.setText("");
						mSecurityLight5.setText(getResources().getString(R.string.device_security_light_5));
					}
				}else{
					int mDataInt = 0;
					mSecurityLight.setText(String.valueOf(mDataInt) + "Lux");
					mSecurityLight1.setVisibility(View.VISIBLE);
					mSecurityLight1.setText(getResources().getString(R.string.device_security_light_1));
				}
			}
		}else{
			mSecurityLight.setText(String.valueOf(0) + "Lux");
			mSecurityLight1.setVisibility(View.VISIBLE);
			mSecurityLight1.setText(getResources().getString(R.string.device_security_light_1));
		}
	}

	 @Override
	 public void onClick(View v) {
		 controlDevice(EP_14, null, null);
		 //鍙戦�佸涓嬪懡浠ゆ椂锛岀涓�娆¤繘鍏ュぇ鍥惧竷闃叉椂eptype涓篋3寮傚父
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
	public CharSequence parseDataWithProtocol(String epData) {
		 if(StringUtil.isNullOrEmpty(epData) || PROTOCOL_NORMAL.equals(epData))
			 return null;
		 if(PROTOCOL_ALARM.equals(epData)){
			 return super.parseDataWithProtocol(epData);
		 }else{
			 String temp = "";
			 String humidity = "";
			 String light = "";
			if(getChildDevice(EP_15) != null){
				WulianDevice device1 = getChildDevice(EP_15);
				String mData1 = device1.getDeviceInfo().getDevEPInfo().getEpData();
				if(!StringUtil.isNullOrEmpty(mData1)){
					// 0AD1040200CA
					if (mData1.substring(6, 8).equals("02")) {
						String str = mData1.substring(8, 12);
						float f = StringUtil.toInteger(str, 16) / 10;
						temp = f+"℃";
					} else if (mData1.substring(6, 8).equals("05")) {
						String str = mData1.substring(8, 12);
						float f = StringUtil.toInteger(str, 16) / 10;
						temp = "-"+f+ "℃";
					}else{
						temp = 0 + "℃";
					}
				}else{
					temp = 0 + "℃";
				}
			}else{
				temp = 0 + "℃";
			}
			
			if(getChildDevice(EP_16) != null){
				WulianDevice device2 = getChildDevice(EP_16);
				String mData2 = device2.getDeviceInfo().getDevEPInfo().getEpData();
				if(!StringUtil.isNullOrEmpty(mData2)){
					if (mData2.substring(6, 8).equals("02")
							|| mData2.substring(6, 8).equals("01")) {
						// 0AD204010104
						String str = mData2.substring(8, 12);
						float f = StringUtil.toInteger(str, 16) / 10;
						humidity = f+"%RH";
					}else{
						humidity = 0 + "%RH";
					}
				}else{
					humidity = 0 + "%RH";
				}
			}else{
				humidity = 0 + "%RH";
			}
			
			if(getChildDevice(EP_17) != null){
				WulianDevice device3 = getChildDevice(EP_17);
				String mData3 = device3.getDeviceInfo().getDevEPInfo().getEpData();
				if(!StringUtil.isNullOrEmpty(mData3)){
					if (mData3.substring(6, 8).equals("01")) {
						String str = mData3.substring(8, 12);
						String intensity = StringUtil.toInteger(str, 16).toString();
						int mDataInt = StringUtil.toInteger(intensity);
						light = mDataInt + "Lux";
					}else{
						light = "0Lux";
					}
				} else {
					light = "0Lux";
				}
			} else {
				light = "0Lux";
			}
			return temp + "," + humidity + "," + light;
		}
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, final AutoConditionInfo autoConditionInfo,
			boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		HumanInductorChooseView humanInductorChooseView = new HumanInductorChooseView(
				inflater.getContext());
		humanInductorChooseView.setmAlarmAndNormal(getAlarmProtocol(),
				getNormalProtocol());
		humanInductorChooseView.setmAlarmDeviceValues(
				autoConditionInfo.getExp(), autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(humanInductorChooseView.getView());
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}

	@Override
	public String checkDataRatioFlag() {
		return Messages.SMILE_DEFAULT;
	}

	@Override
	public String unit(String ep, String epType) {
		if (StringUtil.equals(epType, "D1")) {
			return UNIT_C;
		} else if (StringUtil.equals(epType, "D2")) {
			return UNIT_RH;
		} else if (StringUtil.equals(epType, "D3")) {
			return UNIT_LIGHT;
		} else {
			return null;
		}
	}

	@Override
	public String unitName() {
		return null;
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
