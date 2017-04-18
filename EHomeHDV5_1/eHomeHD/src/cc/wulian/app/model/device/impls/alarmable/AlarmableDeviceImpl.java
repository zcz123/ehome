package cc.wulian.app.model.device.impls.alarmable;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.LinkTaskAlarmView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public abstract class AlarmableDeviceImpl extends AbstractDevice implements Alarmable, Defenseable{

	protected static final String DATA_ALARM_STATE_NORMAL_0 = "0";
	protected static final String DATA_ALARM_STATE_ALARM_1  = "1";
	
	protected static final String DEFENSE_STATE_UNSET_0 		= "0";
	protected static final String DEFENSE_STATE_SET_1 			= "1";
	
	protected String epType;
	protected String epData;
	protected String epStatus;
	
	public AlarmableDeviceImpl(Context context, String type) {
		super(context, type);
	}

	@Override
	public void refreshDevice() {
		DeviceEPInfo epInfo = getCurrentEpInfo();
		if(epInfo!=null){
			epType = epInfo.getEpType();
			epData = epInfo.getEpData();
			epStatus = epInfo.getEpStatus();
		}

	}

	@Override
	public boolean isDefenseSetup() {
		return isSameAs(DEFENSE_STATE_SET_1, epStatus);
	}

	@Override
	public boolean isDefenseUnSetup() {
		return !isDefenseSetup();
	}

	@Override
	public String getDefenseSetupCmd() {
		return DEFENSE_STATE_SET_1;
	}

	@Override
	public boolean isLongDefenSetup() {
		return false;
	}

	@Override
	public String getDefenseUnSetupCmd() {
		return DEFENSE_STATE_UNSET_0;
	}

	@Override
	public String getDefenseSetupProtocol() {
		return getDefenseSetupCmd();
	}

	@Override
	public String getDefenseUnSetupProtocol() {
		return getDefenseUnSetupCmd();
	}

	public String setDefenseState(String ep,String epType,String state) {
		return !isNull(state) ? state : isDefenseSetup() ? DEFENSE_STATE_UNSET_0 : isDefenseUnSetup() ? DEFENSE_STATE_SET_1 : DEFENSE_STATE_SET_1;
	}

	@Override
	public boolean isAlarming() {
		if (isNull(epData)) return false;

//		if (StringUtil.toInteger(epStatus) == 0) return false;
		if (StringUtil.toInteger(epStatus) != 1) return false;
		return StringUtil.toInteger(epData) == 1;
	}

	@Override
	public boolean isNormal() {
		if (isNull(epData)) return true;

		// return epData.startsWith(getNormalProtocol());
		return StringUtil.toInteger(epData) == 0;
	}

	@Override
	public String getAlarmProtocol() {
		return DATA_ALARM_STATE_ALARM_1;
	}

	@Override
	public String getNormalProtocol() {
		return DATA_ALARM_STATE_NORMAL_0;
	}
	public String getCancleAlarmProtocol(){
		return "";
	}
	@Override
	public boolean isDestory() {
		return false;
	}

	@Override
	public boolean isLowPower() {
		return false;
	}

	@Override
	public void controlDevice(String ep, String epType, String epData) {
		setDeviceWidthEpData(ep, epType, epData);
	}

	@Override
	public boolean isLinkControl() {
		return true;
	}

	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context, TaskInfo taskInfo) {
		AbstractLinkTaskView taskView = new LinkTaskAlarmView(context, taskInfo);
		taskView.onCreateView();
		return taskView;
	}

	protected Drawable getStateSmallIconDrawable(Drawable unDefensableDrawable,Drawable alarmingDrawable){
		if(isDefenseUnSetup()){
			return unDefensableDrawable;
		}else if(isAlarming()){
			return alarmingDrawable;
		}else{
			return getDefaultStateSmallIcon();
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
		sb.append(mContext.getString(R.string.home_device_alarm_default_voice_notification));
		return sb.toString();
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		ssb.append(mContext.getString(R.string.device_state_alarm));
		return ssb;
	}

	/**
	 * new int[2] { alarm, disalarm } for small icon( like listView item)
	 */
	/* package */static final Map<String, int[]> RES_STATE_NOT_NORMAL_SMALL = new HashMap<String, int[]>();


	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater){
		return getDefensableShortCutView(item, inflater);
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, final AutoConditionInfo autoConditionInfo,final boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		View contentView =  inflater.inflate(R.layout.task_manager_trigger_setup_device_select, null);
		RadioGroup radiogroup = (RadioGroup) contentView.findViewById(R.id.task_manager_select_radiogroup);
		TextView noticeRemind = (TextView) contentView.findViewById(R.id.task_manager_device_notice_remind_textview);
		final RadioButton alarmbutton = (RadioButton) contentView.findViewById(R.id.task_manager_select_alarm_status);
		final RadioButton normalbutton = (RadioButton) contentView.findViewById(R.id.task_manager_select_normal_status);
		final LinearLayout noticeLayout = (LinearLayout) contentView.findViewById(R.id.task_manager_device_select_notice_layout);
		final ImageView noticeImageView = (ImageView) contentView.findViewById(R.id.task_manager_device_select_img);
		noticeRemind.setText(mContext.getResources().getString(R.string.house_rule_add_new_condition_device_when)+ " " + DeviceTool.getDeviceShowName(this)
				+  " " +mContext.getResources().getString(R.string.home_device_alarm_default_voice_detect));
		alarmbutton.setText(getAlarmString());
		normalbutton.setText(getNormalString());
		alarmbutton.setChecked(true);
		if(isTriggerCondition){
			noticeLayout.setVisibility(View.VISIBLE);
			noticeImageView.setSelected(true);
		}else{
			noticeLayout.setVisibility(View.GONE);
		}
		autoConditionInfo.setExp("=" + getAlarmProtocol());
		OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if(isChecked){
					String selectstatus = "";
					if(arg0 == alarmbutton){
						if(isTriggerCondition){
							noticeLayout.setVisibility(View.VISIBLE);
						}
						selectstatus = "=" + getAlarmProtocol();
					}else{
						if(isTriggerCondition){
							noticeLayout.setVisibility(View.GONE);
						}
						selectstatus = "=" + getNormalProtocol();
					}
					autoConditionInfo.setExp(selectstatus);
				}
			}
		};
		alarmbutton.setOnCheckedChangeListener(checkListener);
		normalbutton.setOnCheckedChangeListener(checkListener);
		noticeImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String selectstatus = "";
				if(noticeImageView.isSelected()){
					selectstatus = "=" + getAlarmProtocol() + "$";
					noticeImageView.setSelected(false);
				}else{
					selectstatus = "=" + getAlarmProtocol();
					noticeImageView.setSelected(true);
				}
				autoConditionInfo.setExp(selectstatus);
			}
		});
		holder.setShowDialog(true);
		holder.setContentView(contentView);
		holder.setDialogTitle(mContext.getResources().getString(R.string.house_rule_add_new_condition_select_alarm));
		return holder;
	}

	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		return getDefenseDeviceSelectDataShortCutView(item, inflater,autoActionInfo);
	}
	protected DeviceShortCutSelectDataItem getDefenseDeviceSelectDataShortCutView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,AutoActionInfo autoActionInfo) {
		if(item == null){
			item = new DefenseableDeviceShortCutSelectDataItem(inflater.getContext());
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}
	
	public static class DefenseableDeviceShortCutSelectDataItem extends DeviceShortCutSelectDataItem{
		private LinearLayout controlableLineLayout;
		private ImageView setupImageView;
		private ImageView unSetupImageView;
		private OnClickListener cliclListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v == setupImageView){
					clickSetup();
				}else if(v == unSetupImageView){
					clickUnsetup();
				}
			}
			
		};
		public DefenseableDeviceShortCutSelectDataItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_defenseable, null);
			setupImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_defense_setup_iv);
			unSetupImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_defense_unsetup_iv);
			controlLineLayout.addView(controlableLineLayout);
		}
		@Override
		public void setWulianDeviceAndSelectData(WulianDevice device,
				AutoActionInfo autoActionInfo) {
			super.setWulianDeviceAndSelectData(device, autoActionInfo);
			if(device instanceof Defenseable){
				Map<String,DeviceEPInfo> infoMap = device.getDeviceInfo().getDeviceEPInfoMap();
				if(infoMap == null)
					return ;
			
				if(isDefenseSetup()){
					setupImageView.setSelected(true);
					unSetupImageView.setSelected(false);
					contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_background);
					unSetupImageView.setOnClickListener(cliclListener);
				}else if(isDefenseUnSetup()){
					setupImageView.setSelected(false);
					unSetupImageView.setSelected(true);
					contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_background);
					setupImageView.setOnClickListener(cliclListener);
				}else{
					setupImageView.setSelected(false);
					unSetupImageView.setSelected(false);
					Defenseable defenseable = (Defenseable)mDevice;
					String ep = autoActionInfo.getEp();
					if(StringUtil.isNullOrEmpty(ep))
						ep = mDevice.getDefaultEndPoint();
					String epData = autoActionInfo.getEpData();
//					if(StringUtil.isNullOrEmpty(epData)){
//						epData = defenseable.getDefenseUnSetupProtocol();
//					}
					if(!StringUtil.isNullOrEmpty(epData)){
						this.autoActionInfo.setEpData(epData);
					}
					contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_red_background);
					this.autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+ep+">"+mDevice.getDeviceType());
					setupImageView.setOnClickListener(cliclListener);
					unSetupImageView.setOnClickListener(cliclListener);
				}
			}
		}
		protected boolean isDefenseUnSetup() {
			if(mDevice instanceof Defenseable){
				Defenseable defenseable = (Defenseable)mDevice;
				String epData = defenseable.getDefenseUnSetupProtocol();
				if(StringUtil.equals(epData, this.autoActionInfo.getEpData())){
					return true;
				}
			}
			return false;
		}
		protected boolean isDefenseSetup() {
			if(mDevice instanceof Defenseable){
				Defenseable defenseable = (Defenseable)mDevice;
				String epData = defenseable.getDefenseSetupProtocol();
				if(StringUtil.equals(epData, this.autoActionInfo.getEpData())){
					return true;
				}
			}
			return false;
		}
		protected void clickUnsetup() {
			if(mDevice instanceof Defenseable){
				Defenseable defenseable = (Defenseable)mDevice;
				String ep = mDevice.getDefaultEndPoint();
				String epData = defenseable.getDefenseUnSetupProtocol();
				this.autoActionInfo.setEpData(epData);
				this.autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+ep+">"+mDevice.getDeviceType());
				fireShortCutSelectDataListener();
			}
		}

		protected void clickSetup() {
			if(mDevice instanceof Defenseable){
				Defenseable defenseable = (Defenseable)mDevice;
				String epData = defenseable.getDefenseSetupProtocol();
				String ep = mDevice.getDefaultEndPoint();
				this.autoActionInfo.setEpData(epData);
				this.autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+ep+">"+mDevice.getDeviceType());
				fireShortCutSelectDataListener();
			}
		}
	}
	
	public static class DefenseableDeviceShortCutControlItem extends DeviceShortCutControlItem{


		private LinearLayout controlableLineLayout;
		private ImageView setupImageView;
		private ImageView unSetupImageView;
		private OnClickListener cliclListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v == setupImageView){
					clickSetup();
				}else if(v == unSetupImageView){
					clickUnsetup();
				}
			}
			
		};
		public DefenseableDeviceShortCutControlItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_defenseable, null);
			setupImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_defense_setup_iv);
			unSetupImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_defense_unsetup_iv);
			controlLineLayout.addView(controlableLineLayout);
		}
		protected void clickUnsetup() {
			Map<String,DeviceEPInfo> infoMap = mDevice.getDeviceInfo().getDeviceEPInfoMap();
			if(infoMap != null){
				if(mDevice instanceof Defenseable){
					Defenseable defenseable = (Defenseable)mDevice;
					for(DeviceEPInfo info : infoMap.values()){
						mDevice.controlDevice(info.getEp() , info.getEpType(), defenseable.getDefenseUnSetupProtocol());
					}
				}
			}
		}

		protected void clickSetup() {
			Map<String,DeviceEPInfo> infoMap = mDevice.getDeviceInfo().getDeviceEPInfoMap();
			if(infoMap != null){
				if(mDevice instanceof Defenseable){
					Defenseable defenseable = (Defenseable)mDevice;
					for(DeviceEPInfo info : infoMap.values()){
						mDevice.controlDevice(info.getEp() , info.getEpType(), defenseable.getDefenseSetupProtocol());
					}
				}
			}
		}
		@Override
		public void setWulianDevice(WulianDevice device) {
			super.setWulianDevice(device);
			if(device instanceof Defenseable){
				Map<String,DeviceEPInfo> infoMap = device.getDeviceInfo().getDeviceEPInfoMap();
				if(infoMap == null)
					return ;
			
				if(isDefenseUnSetup()){
					setupImageView.setSelected(false);
					unSetupImageView.setSelected(true);
					setupImageView.setOnClickListener(cliclListener);
				}
				if(isDefenseSetup()){
					setupImageView.setSelected(true);
					unSetupImageView.setSelected(false);
					unSetupImageView.setOnClickListener(cliclListener);
				}
			}
		}
		protected boolean isDefenseUnSetup() {
			Defenseable defenseable = (Defenseable)mDevice;
			return defenseable.isDefenseUnSetup();
		}
		protected boolean isDefenseSetup() {
			Defenseable defenseable = (Defenseable)mDevice;
			return defenseable.isDefenseSetup();
		}
	
	}

	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, AutoActionInfo autoActionInfo) {
		return null;
	}
	
	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_default_voice_notification);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.device_state_normal);
	}
	@Override
	public void OnRefreshResultData(Intent data){
		
	}
}
