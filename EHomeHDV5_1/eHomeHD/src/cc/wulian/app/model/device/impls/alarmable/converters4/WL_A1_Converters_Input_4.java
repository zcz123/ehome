package cc.wulian.app.model.device.impls.alarmable.converters4;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.alarmable.AlarmableDeviceImpl;
import cc.wulian.app.model.device.impls.alarmable.Defenseable;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.dao.MessageDao;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_CONVERTERS_INPUT_4}, 
		category = Category.C_SECURITY)
public class WL_A1_Converters_Input_4 extends   AlarmableDeviceImpl
{
	public String epType = "A1";
	public static final String OPERATION_MODE_DEFENSE = "1";
	public static final String OPERATION_MODE_RELEASE = "0";
	public static final String DATA_ALARM = "0301";
	public static final String DATA_NORMAL = "0300";
	public static final String DATA_STATUS_OPEN = "1";
	public static final String DATA_STATUS_CLOSE = "0";
	
	public static String STATE_DEFENSE = "00";
	public static String STATE_RELEASE = "64";
	private String currentEP = EP_14;
	
	private static final String SPLIT_SYMBOL = ">";
	
    protected String[] mSwitchStatus;
	
	public String  data_alarm;
	public String  data_switch;
	
    protected LinearLayout mLinearLayout ;
	protected LinearLayout mLightLayout;
	
	@ViewInject(R.id.device_converters_input_btn1)
	private Button converters1Btn;
	@ViewInject(R.id.device_converters_input_btn2)
	private Button converters2Btn;
	@ViewInject(R.id.device_converters_input_btn3)
	private Button converters3Btn;
	@ViewInject(R.id.device_converters_input_btn4)
	private Button converters4Btn;
	
	@ViewInject(R.id.device_one_wried_wireless_switch_png_input)
	private ImageView switchImageView;
	
	
	@ViewInject(R.id.device_one_wried_wireless_epname)
	private TextView epNameTextView;
	
	@ViewInject(R.id.device_one_wried_wireless_ctrl_defense)
	private ImageView convertersDefenseBtn;
	
	@ViewInject(R.id.device_one_wried_wireless_alarm)
	private ProgressBar convertersAlarmProgressBar;
	
	private static final String[] EP_SEQUENCE = { EP_14, EP_15, EP_16, EP_17 };
	
	private String controlMode;
	
	public String[] getLightEPResources() {
		return EP_SEQUENCE;
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(StringUtil.isNullOrEmpty(controlMode)){
				controlMode = "0";
			}
			/**
			 * EP选择
			 */
			if(v == converters1Btn){
				setConvertersChecked(EP_14);
			}else if(v == converters2Btn){
				setConvertersChecked(EP_15);
			}else if(v == converters3Btn){
				setConvertersChecked(EP_16);
			}else if(v == converters4Btn){
				setConvertersChecked(EP_17);
			}
			/**
			 * 设防与撤防
			 */
			else if(v == convertersDefenseBtn){
				WulianDevice currentDevice = getChildDevice(currentEP);
				WL_A1_Converters_Input_4 childDevice = (WL_A1_Converters_Input_4)getChildDevice(currentEP);
				if(childDevice.isDefenseSetup()){
					controlDevice(currentEP,currentDevice.getDeviceInfo().getDevEPInfo().getEpType(),"0");
				}else{
					controlDevice(currentEP,currentDevice.getDeviceInfo().getDevEPInfo().getEpType(),"1");
				}
			}
		}
	};
	public WL_A1_Converters_Input_4( Context context, String type )
	{
		super(context, type);
	}

/**
 * 设备上线调用此方法
 * 只调用一次
 */
	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		if(devInfo.getDevEPInfo()!=null){
			initChildDeviceData(devInfo.getDevEPInfo());
		}
		super.onDeviceUp(devInfo);
	}
	/**
	 * 数据刷新操作
	 */
	@Override
	public synchronized void onDeviceData(String gwID, String devID,
			DeviceEPInfo devEPInfo,String cmd,String mode) {
		String ep = devEPInfo.getEp();
		WulianDevice device = getChildDevice(ep);
		if(device!=null){
			device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			removeCallbacks(mRefreshStateRunnable);
			post(mRefreshStateRunnable);
			fireDeviceRequestControlData();
		}
		else{
			initChildDeviceData(devEPInfo);
			super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			//checkAlarm(devEPInfo);
		}
		
	}


	private void initChildDeviceData(DeviceEPInfo devEPInfo) {
		/**
		 * 报警状态
		 */
		if (DATA_ALARM.equals(devEPInfo.getEpData())) {
			data_alarm = DATA_ALARM;
		} else{
			data_alarm = DATA_NORMAL;
		}
		/**
		 * switch输入状态
		 */
		if ("0201".equals(devEPInfo.getEpData())||"0101".equals(devEPInfo.getEpData())) {
			data_switch = DATA_STATUS_OPEN;
		} else if("0200".equals(devEPInfo.getEpData())||"0100".equals(devEPInfo.getEpData())){
			data_switch = DATA_STATUS_CLOSE;
		}
	}

	private void checkAlarm(DeviceEPInfo devEPInfo) {
		if(isDefenseSetup()&&isAlarming()){
			MessageEventEntity mMessageEventEntity = new MessageEventEntity();
			mMessageEventEntity.setGwID(getDeviceGwID());
			mMessageEventEntity.setDevID(getDeviceID());
			mMessageEventEntity.setEp(devEPInfo.getEp());
			mMessageEventEntity.setEpType(devEPInfo.getEpType());
			mMessageEventEntity.setEpName(devEPInfo.getEpName());
			mMessageEventEntity.setEpData(devEPInfo.getEpData());
			mMessageEventEntity.setTime(System.currentTimeMillis()+"");
			mMessageEventEntity.setPriority(Messages.PRIORITY_DEFAULT);
			mMessageEventEntity.setType(Messages.TYPE_DEV_ALARM);
			mMessageEventEntity.setSmile(Messages.SMILE_DEFAULT);
			
			MessageDao.getInstance().insert(mMessageEventEntity);
		}

	}
	@Override
	public CharSequence parseAlarmProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		sb.append(DeviceTool.getDeviceAlarmAreaName(this));
		Map<String, DeviceEPInfo> epMap = getDeviceInfo().getDeviceEPInfoMap();
		String ep = "0";
		String epmsg = "";
		for(DeviceEPInfo info : epMap.values()){
			WulianDevice device = getChildDevice(info.getEp());
			if(device instanceof WL_A1_Converters_Input_4){
				WL_A1_Converters_Input_4 devcieA1 = (WL_A1_Converters_Input_4)device;
				if( devcieA1.isDefenseSetup() && DATA_ALARM.equals(devcieA1.epData)){
					ep = info.getEp();
					epmsg = devcieA1.getCurrentEpInfo().getEpMsg();
					break;
				}
			}
		}
		String deviceName = getDeviceInfo().getDevEPInfoByEP(ep).getEpName();
		//根据epMsg 判断是否有报警通知
		if(!isNull(epmsg) && isSameAs(epmsg , "N")){
			return null;
		}
		if(StringUtil.isNullOrEmpty(deviceName)){
			deviceName = DeviceTool.getDeviceShowName(this);
		}
		sb.append(deviceName);
		if(LanguageUtil.isChina() || LanguageUtil.isTaiWan()){
			sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
		}else{
			sb.append(" "+ mContext.getString(R.string.home_device_alarm_default_voice_detect) + " ");
		}
		sb.append(mContext.getString(R.string.home_device_alarm_default_voice_notification));
		return sb.toString();
	}

	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	@Override
	public boolean isAlarming() {
		boolean isAlarm = false;
		Map<String, DeviceEPInfo> epMap = getDeviceInfo().getDeviceEPInfoMap();
		for(DeviceEPInfo info : epMap.values()){
			WulianDevice device = getChildDevice(info.getEp());
			if(device instanceof WL_A1_Converters_Input_4){
				WL_A1_Converters_Input_4 devcieA1 = (WL_A1_Converters_Input_4)device;
				if( devcieA1.isDefenseSetup() && DATA_ALARM.equals(devcieA1.epData)){
					isAlarm = true;
					break;
				}
			}
		}
//		WulianDevice device = getChildDevice(getCurrentEpInfo().getEp());
//		if(device instanceof WL_A1_Converters_Input_4){
//			WL_A1_Converters_Input_4 devcieA1 = (WL_A1_Converters_Input_4)device;
//			if( devcieA1.isDefenseSetup() && DATA_ALARM.equals(devcieA1.epData)){
//				isAlarm = true;
//			}
//		}
		return isAlarm;
	}

	@Override
	public boolean isNormal() {
		return !isAlarming();
	}
/**
 * 联动任务
 */
	@Override
	public String getAlarmProtocol() {
		return DATA_ALARM;
	}

	@Override
	public String getNormalProtocol() {
		return DATA_NORMAL;
	}

/**
 * 显示列表中信息
 */
	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater) {
		return getDefaultShortCutControlView(item,inflater);
	}
	
	/**
	 * 显示小图
	 */
	@Override
	public Drawable getStateSmallIcon() {
		Drawable drawable =mResources.getDrawable(R.drawable.device_four_convert);
		return drawable;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		View view = inflater.inflate(R.layout.device_four_converters, null);
//		deviceContainerlineLayout = (LinearLayout)view.findViewById(R.id.curtain_device_content);
//		View deviceView  = inflater.inflate(R.layout.device_four_converters, null);
//		deviceContainerlineLayout.addView(deviceView);
		ViewUtils.inject(this, view);
		return view;
	}


	@Override
	public void onViewCreated(View view, Bundle saveState) {
		
		converters1Btn.setOnClickListener(clickListener);
		converters2Btn.setOnClickListener(clickListener);
		converters3Btn.setOnClickListener(clickListener);
		converters4Btn.setOnClickListener(clickListener);
		convertersDefenseBtn.setOnClickListener(clickListener);
		mViewCreated = true;
		SendMessage.sendControlDevMsg(gwID, devID, EP_0, "A1", "2");
	}
/**
 * 数据返回后界面的刷新
 * @param ep
 */
	private void setConvertersChecked(String ep){
		currentEP = ep;
		if(EP_14.equals(currentEP)){
			converters1Btn.setBackgroundResource(R.drawable.curtain_title_bg_selected);
		}else{
			converters1Btn.setBackgroundResource(R.drawable.curtain_title_bg_normal);
		}
		if(EP_15.equals(currentEP)){
			converters2Btn.setBackgroundResource(R.drawable.curtain_title_bg_selected);
		}else{
			converters2Btn.setBackgroundResource(R.drawable.curtain_title_bg_normal);
		}
		if(EP_16.equals(currentEP)){
			converters3Btn.setBackgroundResource(R.drawable.curtain_title_bg_selected);
		}else{
			converters3Btn.setBackgroundResource(R.drawable.curtain_title_bg_normal);
		}
		if(EP_17.equals(currentEP)){
			converters4Btn.setBackgroundResource(R.drawable.curtain_title_bg_selected);
		}else{
			converters4Btn.setBackgroundResource(R.drawable.curtain_title_bg_normal);
		}
		
		WL_A1_Converters_Input_4 childDevice = (WL_A1_Converters_Input_4)getChildDevice(currentEP);
		String epName = childDevice.getDeviceInfo().getDevEPInfo().getEpName();
		
		if(!StringUtil.isNullOrEmpty(epName)){
		epNameTextView.setText(epName);
		}else {
		epNameTextView.setText(R.string.device_type_A1);
		}
		if(childDevice.isDefenseUnSetup()){
			convertersDefenseBtn.setImageResource(R.drawable.device_one_wried_wireless_undefense);//锁开
			convertersAlarmProgressBar.setVisibility(View.INVISIBLE);
		}
		else if(childDevice.isDefenseSetup()){
			convertersDefenseBtn.setImageResource(R.drawable.device_one_wried_wireless_defense);//锁关
			
			if (DATA_ALARM.equals(childDevice.data_alarm)) {
				convertersAlarmProgressBar.setVisibility(View.VISIBLE);
			} else {
				convertersAlarmProgressBar.setVisibility(View.INVISIBLE);
			}
		}
		/**
		 * switch输入状态
		 */
		if(DATA_STATUS_OPEN.equals(childDevice.data_switch)){
			switchImageView.setImageResource(R.drawable.device_one_wried_wireless_switch_open);
		} else {
			switchImageView.setImageResource(R.drawable.device_one_wried_wireless_switch_close);
		}
	}
	@Override
	public void initViewStatus() {
		setConvertersChecked(currentEP);
	}
	
	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext, DeviceSettingActivity.class);
		intent.putExtra(DeviceFourConvertersFragment.GWID, gwID);
		intent.putExtra(DeviceFourConvertersFragment.DEVICEID, devID);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
				AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
				DeviceFourConvertersFragment.class.getName());
		return intent;
	}
	
	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.set_titel));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				Intent i = getSettingIntent();
				mContext.startActivity(i);
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_default_voice_notification);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.device_state_normal);
	}
	   /**
	    * 设置dialog
	    */
		@Override
		public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
				LayoutInflater inflater,   AutoActionInfo autoActionInfo) {
			DialogOrActivityHolder holder = new DialogOrActivityHolder();
			View contentView =  inflater.inflate(R.layout.task_manager_common_light_setting_view_layout, null);
			LinearLayout mLinearLayout =  (LinearLayout) contentView.findViewById(R.id.task_manager_common_light_setting_view_layout);
			mSwitchStatus = new String[getLightEPResources().length]; //动态初始化
			for (int i = 0;i<getLightEPResources().length;i++) {
				mLinearLayout.addView(addChildView(i,getLightEPResources()[i],autoActionInfo));
			}
			holder.setShowDialog(true);
			holder.setContentView(contentView);
			holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
			return holder;
		}
		private View addChildView(final int i,String str,final AutoActionInfo autoActionInfo) {
			// TODO 动态添加布局(xml方式)
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.task_manager_4convert_setting_view, null);
			final WulianDevice device = getChildDevice(str);
			
			final TextView deviceNameTextView = (TextView) view.findViewById(R.id.device_common_light_setting_dev_name);
			final FrameLayout switchOpenView = (FrameLayout) view.findViewById(R.id.device_common_light_setting_switch_open);
			final FrameLayout switchCloseView = (FrameLayout) view.findViewById(R.id.device_common_light_setting_switch_close);
			
			if (!StringUtil.isNullOrEmpty(device.getDeviceInfo().getDevEPInfo().getEpName())) {
				deviceNameTextView.setText((i + 1) + "." + device.getDeviceInfo().getDevEPInfo().getEpName());
			} else {
				deviceNameTextView
						.setText((i + 1)
								+ "."
								+ getResources().getString(
										R.string.device_type_A1));
			}
			final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
			mSwitchStatus[i] = "0";
		if (!StringUtil.isNullOrEmpty(autoActionInfo.getEpData())) {
			String epdata = autoActionInfo.getEpData();
			
			if (epdata.length() > 1) {
				mSwitchStatus[i] = epdata.substring(i, i + 1);
			}else{
				mSwitchStatus[i] = "0";
			}
			
			if (StringUtil.equals(type[2], getLightEPResources()[i])) {
				mSwitchStatus[i] = epdata;
			}

			if (!StringUtil.isNullOrEmpty(mSwitchStatus[i])) {
				if (mSwitchStatus[i].equals("0")) {//撤防
					switchOpenView.setVisibility(View.GONE);
					switchCloseView.setVisibility(View.VISIBLE);
					
				} else if (mSwitchStatus[i].equals("1")) {
					switchOpenView.setVisibility(View.VISIBLE);
					switchCloseView.setVisibility(View.GONE);
				} 
			} else {
				// mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_open).setVisibility(View.GONE);
				// mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_close).setVisibility(View.VISIBLE);
				switchOpenView.setVisibility(View.GONE);
				switchCloseView.setVisibility(View.VISIBLE);
				mSwitchStatus[i] = "0";
			}
		}else{
				autoActionInfo.setEpData("0000");
				autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
						+ getDeviceType() + SPLIT_SYMBOL + EP_0 + SPLIT_SYMBOL
						+ getDeviceType());
		}
//
//			dev_name.setText(device.getDeviceInfo().getDevEPInfo().getEpName());
			switchOpenView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						switchOpenView.setVisibility(View.GONE);
						switchCloseView.setVisibility(View.VISIBLE);
						mSwitchStatus[i] = "0";
						setautoActionInfo(autoActionInfo);
					}
				});
			switchCloseView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Logger.debug("i="+i);
					switchOpenView.setVisibility(View.VISIBLE);
					switchCloseView.setVisibility(View.GONE);
					mSwitchStatus[i] = "1";
					setautoActionInfo(autoActionInfo);
				}
			});
			view.setLayoutParams(lp);
			return view;
			
		   }
		/**
		 * 判断不需要设定的数目时候为总数-1
		 * 若是，则发送ep端口加对应位置数据
		 * 若不是则发送拼接数据；
		 * @param autoActionInfo
		 */
		private void setautoActionInfo(AutoActionInfo autoActionInfo) {
			String mSetSwitchStatus = "" ;
//			int needNotSetNumber = 0;
//			int needSetIndex = 0;
			for(int i = 0;i<getLightEPResources().length;i++){
//				if(mSwitchStatus[i].equals("2")){
//					needNotSetNumber++;
//				}
//				else{
//					needSetIndex = i;
//				}
				mSetSwitchStatus = mSetSwitchStatus + mSwitchStatus[i];
			}
//			if(needNotSetNumber==(getLightEPResources().length-1)){
//				autoActionInfo.setEpData(mSwitchStatus[needSetIndex]);
//				autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL+ getDeviceType() + SPLIT_SYMBOL + getLightEPResources()[needSetIndex] + SPLIT_SYMBOL+ getDeviceType());
//				} 
//			else {
				autoActionInfo.setEpData(mSetSwitchStatus);
				autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
						+ getDeviceType() + SPLIT_SYMBOL + EP_0 + SPLIT_SYMBOL
						+ getDeviceType());
//			}
		}
		
		@Override
		public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
				LayoutInflater inflater, final AutoConditionInfo autoConditionInfo,final boolean isTriggerCondition) {
			DialogOrActivityHolder holder = new DialogOrActivityHolder();
			View contentView =  inflater.inflate(R.layout.device_a1_trigger_setup_device_select, null);
			RadioGroup radiogroup = (RadioGroup) contentView.findViewById(R.id.task_manager_select_radiogroup);
			TextView noticeRemind = (TextView) contentView.findViewById(R.id.task_manager_device_notice_remind_textview);
			final RadioButton alarmbutton = (RadioButton) contentView.findViewById(R.id.task_manager_select_alarm_status);
			final RadioButton normalbutton = (RadioButton) contentView.findViewById(R.id.task_manager_select_normal_status);
			final LinearLayout noticeLayout = (LinearLayout) contentView.findViewById(R.id.task_manager_device_select_notice_layout);
			final ImageView noticeImageView = (ImageView) contentView.findViewById(R.id.task_manager_device_select_img);
			final Button deviceSelectZero = (Button) contentView.findViewById(R.id.a1_device_select_zero);
			final Button deviceSelectTwo= (Button) contentView.findViewById(R.id.a1_device_select_two);
			final Button deviceSelectThree = (Button) contentView.findViewById(R.id.a1_device_select_three);
			final Button deviceSelectFour = (Button) contentView.findViewById(R.id.a1_device_select_four);
			OnClickListener clickListener=new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					initSelectImagViewStatus();
					if(arg0==deviceSelectZero){
						autoConditionInfo.setObject(getObjectString(EP_14));
						deviceSelectZero.setBackgroundResource(R.drawable.curtain_title_bg_selected);
					}
					if(arg0==deviceSelectTwo){
						autoConditionInfo.setObject(getObjectString(EP_15));
						deviceSelectTwo.setBackgroundResource(R.drawable.curtain_title_bg_selected);
					}
					if(arg0==deviceSelectThree){
						autoConditionInfo.setObject(getObjectString(EP_16));
						deviceSelectThree.setBackgroundResource(R.drawable.curtain_title_bg_selected);
					}
					if(arg0==deviceSelectFour){
						autoConditionInfo.setObject(getObjectString(EP_17));
						deviceSelectFour.setBackgroundResource(R.drawable.curtain_title_bg_selected);						
					}
				}

				private String getObjectString(String ep) {
					// TODO Auto-generated method stub
					return devID+">"+type+">"+ep+">"+epType;
				}

				private void initSelectImagViewStatus() {
					// TODO Auto-generated method stub
					deviceSelectZero.setBackgroundResource(R.drawable.curtain_title_bg_normal);
					deviceSelectTwo.setBackgroundResource(R.drawable.curtain_title_bg_normal);
					deviceSelectThree.setBackgroundResource(R.drawable.curtain_title_bg_normal);
					deviceSelectFour.setBackgroundResource(R.drawable.curtain_title_bg_normal);
				}
			};
			deviceSelectZero.setOnClickListener(clickListener);
			deviceSelectTwo.setOnClickListener(clickListener);
			deviceSelectThree.setOnClickListener(clickListener);
			deviceSelectFour.setOnClickListener(clickListener);
			noticeRemind.setText(mContext.getResources().getString(R.string.house_rule_add_new_condition_device_when)+ " " + DeviceTool.getDeviceShowName(this));
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
			controlableLineLayout = (LinearLayout)inflater.inflate(cc.wulian.smarthomev5.R.layout.device_short_cut_control_defenseable, null);
			setupImageView = (ImageView)controlableLineLayout.findViewById(cc.wulian.smarthomev5.R.id.device_short_cut_defense_setup_iv);
			unSetupImageView = (ImageView)controlableLineLayout.findViewById(cc.wulian.smarthomev5.R.id.device_short_cut_defense_unsetup_iv);
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
					contentBackgroundLayout.setBackgroundResource(cc.wulian.smarthomev5.R.drawable.account_manager_item_background);
					unSetupImageView.setOnClickListener(cliclListener);
				}else if(isDefenseUnSetup()){
					setupImageView.setSelected(false);
					unSetupImageView.setSelected(true);
					contentBackgroundLayout.setBackgroundResource(cc.wulian.smarthomev5.R.drawable.account_manager_item_background);
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
					contentBackgroundLayout.setBackgroundResource(cc.wulian.smarthomev5.R.drawable.account_manager_item_red_background);
					this.autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+ep+">"+mDevice.getDeviceType());
					setupImageView.setOnClickListener(cliclListener);
					unSetupImageView.setOnClickListener(cliclListener);
				}
			}
		}
		protected boolean isDefenseUnSetup() {
			String epData=autoActionInfo.getEpData();
			if("0000".equals(epData)){
				return true;
			}
			return false;
		}
		protected boolean isDefenseSetup() {
			String epData=autoActionInfo.getEpData();
			if("1111".equals(epData)){
				return true;
			}
			return false;
		}
		protected void clickUnsetup() {
			if(mDevice instanceof Defenseable){
				autoActionInfo.setEpData("0000");
				autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+EP_0+">"+mDevice.getDeviceType());
				fireShortCutSelectDataListener();
			}
		}

		protected void clickSetup() {
			if(mDevice instanceof Defenseable){
				autoActionInfo.setEpData("1111");
				autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+EP_0+">"+mDevice.getDeviceType());
				fireShortCutSelectDataListener();
			}
		}
	}

}