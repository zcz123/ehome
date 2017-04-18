package cc.wulian.app.model.device.impls.controlable.metalswitch;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SceneList.OnSceneListItemClickListener;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.DisplayUtil;


public abstract class AbstractMetalSwitch extends ControlableDeviceImpl {

	protected LinearLayout contentLineLayout;
	private LinearLayout mainView;
	protected Map<String, SceneInfo> bindScenesMap;
	protected Map<String, DeviceInfo> bindDevicesMap;

	//开关模式
	public static final String SWTICH_MODE_TURN = "01";
	//开关开
	public static final String SWTICH_STATUS_ON= "01";
	//开关 关
	public static final String SWTICH_STATUS_OFF = "00";
	//场景模式
	public static final String SWTICH_MODE_SCENCE = "02";
	//绑定模式
	public static final String SWTICH_MODE_BIND = "03";

	public static final String SWTICH_TYPE_01 = "switch_type_01";
	public static final String SWTICH_TYPE_02 = "switch_type_02";
	public static final String SWTICH_TYPE_03 = "switch_type_03";

	private static final String STATE_OPEN_CMD = "101";
	private static final String STATE_CLOSE_CMD = "100";

	private static final int SMALL_OPEN_D = cc.wulian.app.model.device.R.drawable.device_button_2_open;
	private static final int SMALL_CLOSE_D = cc.wulian.app.model.device.R.drawable.device_button_2_close;
	private int BIG_OPEN_D = cc.wulian.app.model.device.R.drawable.device_measure_switch_open;
	private int BIG_CLOSE_D = cc.wulian.app.model.device.R.drawable.device_measure_switch_close;


	public AbstractMetalSwitch(Context context, String type) {
		super(context, type);
	}
	/**
	 * 创建界面
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ){
		getBindScenesMap();
		mainView = (LinearLayout)inflater.inflate(R.layout.device_other_touch_scene, container, false);
		return mainView;

	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
	}

	@Override
	public void onResume() {
		super.onResume();
		getBindScenesMap();
	}

	public Drawable getStateSmallIcon() {
		List<Drawable> drawers = new ArrayList<Drawable>();
		for (String ep : childDeviceMap.keySet()) {
			final WulianDevice device = childDeviceMap.get(ep);
			if (device instanceof Controlable) {
				Controlable controlable = (Controlable) device;
				String epData=device.getDeviceInfo().getDevEPInfo().getEpData();
				if(epData!=null&&epData.length()>5){
					if(epData.substring(4).equals("01")){
						drawers.add(mResources.getDrawable(getOpenSmallIcon()));
					}else {
						drawers.add(mResources.getDrawable(getCloseSmallIcon()));
					}
				}else{
					drawers.add(mResources.getDrawable(getCloseSmallIcon()));
				}
			}
		}
//		return DisplayUtil.getDrawablesMerge(
//				drawers.toArray(new Drawable[] {}),
//				mResources.getDrawable(SMALL_STATE_BACKGROUND));
		return DisplayUtil.getDrawablesMerge(
				drawers.toArray(new Drawable[] {}));
	}

	@Override
	public boolean isOpened() {
		if (getChildDevices() != null && !getChildDevices().isEmpty()) {
			boolean isOpened=false;
			for (WulianDevice device : getChildDevices().values()) {
				if (device instanceof Controlable) {
					DeviceEPInfo deviceEpInfo=device.getDeviceInfo().getDevEPInfo();
					String epData=deviceEpInfo.getEpData();
					if(epData!=null&&epData.length()>5){
						if(epData.substring(4).equals("01")){
							isOpened=true;
							break;
						}
					}
				}
			}
			return isOpened;
		} else {
			return false;
		}
	}

	public boolean isClosed() {
		return !isOpened();
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if(!isNull(epData)){
			handleEpData(epData);
		}
		showView();
	}

//	@Override
//	public Drawable getStateSmallIcon() {
//		return isOpened() ? getDrawable(getOpenSmallIcon()) : isClosed() ? getDrawable(getCloseSmallIcon()) : this
//				.getDefaultStateSmallIcon();
//	}

	@Override
	public String getOpenSendCmd() {
		return STATE_OPEN_CMD;
	}

	@Override
	public String getCloseSendCmd() {
		return STATE_CLOSE_CMD;
	}

	@Override
	public String getOpenProtocol() {
		return getOpenSendCmd();
	}

	@Override
	public String getCloseProtocol() {
		return getCloseSendCmd();
	}

	//开启时小图标
	protected int getOpenSmallIcon(){
		return SMALL_OPEN_D;
	}

	//关闭时小图标
	protected   int getCloseSmallIcon(){
		return SMALL_CLOSE_D;
	}
	//开关开图标
	protected int getOpenSwitchImage(){
		return BIG_OPEN_D;
	}

	//开关关图标
	protected   int getCloseSwitchImage(){
		return BIG_CLOSE_D;
	}

	/**
	 * 界面的添加与界面的数据处理
	 *
	 */
	public void showView(){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		int devicesLength = getTouchEPResources().length;
		mainView.removeAllViews();

		for(int j = 0; j < devicesLength; j++){
			final String ep = getTouchEPResources()[j];
			LinearLayout itemView = (LinearLayout) inflater.inflate(R.layout.device_am_switch,null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			itemView.setGravity(Gravity.CENTER_HORIZONTAL);
			itemView.setLayoutParams(lp);

			LinearLayout  imageBackground = (LinearLayout)itemView.findViewById(R.id.dev_metalswitch_iamge_layout);
			ImageView deviceImageView = (ImageView) itemView.findViewById(R.id.dev_metalswitch_image);
			TextView deviceNameTextView = (TextView) itemView.findViewById(R.id.dev_metalswitch_name);
			final String switchMode = getSwitchModes()[j];
			final String switchStstus = getSwitchStatus()[j];
			Log.d("---ccc---", "switchMode:"+switchMode+",,switchStstus:"+switchStstus);
			Drawable imageDrawable = null;
			String switchName = "";
			if(!isNull(switchMode)){
				if(isSameAs(switchMode ,SWTICH_MODE_TURN)){
					if(!isNull(switchStstus)){
						if(isSameAs(switchStstus, "01")){
							imageDrawable = getDrawable(getOpenSwitchImage());
						}else{
							imageDrawable = getDrawable(getCloseSwitchImage());
						}
					}else{
						imageDrawable = getDrawable(getCloseSwitchImage());
					}
				//	imageDrawable = isOpened() ? getDrawable(getOpenSwitchImage()) : getDrawable(getCloseSwitchImage());

					if(getChildDevice(ep) != null){
						final WulianDevice device = getChildDevice(ep);
						if (device instanceof Controlable) {
							Controlable controlable = (Controlable) device;
							String epName = device.getDeviceInfo().getDevEPInfo()
									.getEpName();

							if (!StringUtil.isNullOrEmpty(epName)) {
								switchName = epName;
							} else {
								switchName = getTouchEPNames()[j];
							}
						}
					}else{
						switchName = getTouchEPNames()[j];
					}
				}else if(isSameAs(switchMode ,SWTICH_MODE_SCENCE)){
					if (bindScenesMap.containsKey(ep)) {
						SceneInfo sceneInfo = bindScenesMap.get(ep);
						if(sceneInfo != null){
							if(isSameAs(sceneInfo.getStatus(), "1")){
								imageDrawable = SceneManager.getSceneIconDrawable_Black(mContext, sceneInfo.getIcon());
							}else if(isSameAs(sceneInfo.getStatus(), "2")){
								imageDrawable = SceneManager.getSceneIconDrawable_Light_Small(mContext, sceneInfo.getIcon());
							}
							switchName =sceneInfo.getName();
						}
						else{
							imageDrawable = getDrawable(cc.wulian.app.model.device.R.drawable.device_other_touch_scene_blackbind);
							switchName =getString(cc.wulian.app.model.device.R.string.device_no_bind_scene);
						}
					}else{
						imageDrawable = getDrawable(cc.wulian.app.model.device.R.drawable.device_other_touch_scene_blackbind);
						switchName =getString(cc.wulian.app.model.device.R.string.device_no_bind_scene);
					}

				}else if(isSameAs(switchMode ,SWTICH_MODE_BIND)){
					imageDrawable = isOpened() ? getDrawable(getOpenSmallIcon()) : getDrawable(getCloseSmallIcon());
					switchName = "绑定";
				}

			}else{
				imageDrawable = getDrawable(getCloseSwitchImage());
				switchName = "key"+(j+1);
			}
			deviceImageView.setImageDrawable(imageDrawable);
			deviceNameTextView.setText((j+1)+"."+switchName);
			final int finalJ = j;
			imageBackground.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!isNull(switchMode)){
						if(isSameAs(switchMode ,SWTICH_MODE_TURN)){
							controlDevice(ep, epType, "1"+(finalJ +1)+"3");
						}else{
							WLToast.showToast(mContext,"当前模式不支持此操作",WLToast.TOAST_SHORT);
						}
					}
				}
			});
			mainView.addView(itemView);
		  }
	}

	/**
	 * 给子类实现多端口
	 *
	 * @return
	 */
	public abstract String[] getTouchEPResources();

	public abstract String[] getTouchEPNames();

	public abstract String[] getSwitchModes();
	public abstract String[] getSwitchStatus();

	public abstract void handleEpData(String mEpData);

	protected void getBindScenesMap() {
		bindScenesMap = MainApplication.getApplication().bindSceneInfoMap
				.get(getDeviceGwID() + getDeviceID());
		if (bindScenesMap == null) {
			bindScenesMap = new HashMap<String, SceneInfo>();
		}
	}
	
	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext, ModeSettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("gwID", gwID);
		bundle.putString("devID", devID);
		bundle.putString("ep", ep);
		bundle.putString("epType", epType);
		bundle.putString("switchType",SWTICH_TYPE_01);
		intent.putExtra("ModeSettingFragmentInfo", bundle);
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
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_SET)) {
					return;
				}

				Intent i = getSettingIntent();
				mContext.startActivity(i);
				manager.dismiss();
			}
		};
//		if(isDeviceOnLine())
//			items.add(settingItem);
		return items;
	}


	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,AutoActionInfo autoActionInfo) {
		return getControlDeviceSelectDataShortCutView(item, inflater,autoActionInfo);
	}
	protected DeviceShortCutSelectDataItem getControlDeviceSelectDataShortCutView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,AutoActionInfo autoActionInfo) {
		if(item == null){
			item = new ShortCutControlableDeviceSelectDataItem(inflater.getContext());
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}
	public static class ShortCutControlableDeviceSelectDataItem extends DeviceShortCutSelectDataItem{
		protected boolean isStopVisiable = false;
		protected boolean isOpenVisiable = true;
		protected boolean isCloseVisiable = true;
		protected LinearLayout controlableLineLayout;
		protected ImageView openImageView;
		protected ImageView stopImageView;
		protected ImageView closeImageView;
		private View.OnClickListener cliclListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(v == openImageView){
					clickOpen();
				}else if(v == stopImageView){
					clickStop();

				}else if(v == closeImageView){
					clickClose();
				}
			}
		};
		public ShortCutControlableDeviceSelectDataItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout)inflater.inflate(cc.wulian.app.model.device.R.layout.device_short_cut_control_controlable, null);
			openImageView = (ImageView)controlableLineLayout.findViewById(cc.wulian.app.model.device.R.id.device_short_cut_control_open_iv);
			stopImageView = (ImageView)controlableLineLayout.findViewById(cc.wulian.app.model.device.R.id.device_short_cut_control_stop_iv);
			closeImageView = (ImageView)controlableLineLayout.findViewById(cc.wulian.app.model.device.R.id.device_short_cut_control_close_iv);
			controlLineLayout.addView(controlableLineLayout);
		}
		@Override
		public void setWulianDeviceAndSelectData(WulianDevice device,
												 AutoActionInfo autoActionInfo) {
			super.setWulianDeviceAndSelectData(device, autoActionInfo);
			final String SPLIT_SYMBOL = ">";
			final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
			if(device instanceof Controlable){
				if(isOpenVisiable){
					openImageView.setVisibility(View.VISIBLE);
					if(isOpened()){
						openImageView.setSelected(true);
						contentBackgroundLayout.setBackgroundResource(cc.wulian.app.model.device.R.drawable.account_manager_item_background);
					}else{
						openImageView.setSelected(false);
						openImageView.setOnClickListener(cliclListener);
					}
				}else{
					openImageView.setVisibility(View.INVISIBLE);
				}
				if(isStopVisiable){
					if(isStoped()){
						stopImageView.setSelected(true);
						contentBackgroundLayout.setBackgroundResource(cc.wulian.app.model.device.R.drawable.account_manager_item_background);
					}else{
						stopImageView.setSelected(false);
						stopImageView.setOnClickListener(cliclListener);
					}
				}
				else{
					stopImageView.setVisibility(View.INVISIBLE);
				}
				if(isCloseVisiable){
					if(isClosed()){
						closeImageView.setSelected(true);
						contentBackgroundLayout.setBackgroundResource(cc.wulian.app.model.device.R.drawable.account_manager_item_background);
					}else{
						closeImageView.setSelected(false);
						closeImageView.setOnClickListener(cliclListener);
					}
				}else{
					closeImageView.setVisibility(View.INVISIBLE);
				}
			}
		}
		protected void clickClose() {
			if( mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				String epData = "";
				String ep = WulianDevice.EP_0;
				epData += controlable.getCloseProtocol();
				this.autoActionInfo.setEpData(epData);
				this.autoActionInfo.setEtrDataArr(null);
				this.autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+ep+">"+mDevice.getDeviceType());
				fireShortCutSelectDataListener();
			}
		}

		protected void clickStop() {
			if( mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				String epData = "";
				String ep = WulianDevice.EP_0;
				epData += controlable.getStopProtocol();
				this.autoActionInfo.setEpData(epData);
				this.autoActionInfo.setEtrDataArr(null);
				this.autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+ep+">"+mDevice.getDeviceType());
				fireShortCutSelectDataListener();
			}
		}

		protected void clickOpen() {
			if( mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				String epData = "";
				String ep = WulianDevice.EP_0;
				epData += controlable.getOpenProtocol();
				this.autoActionInfo.setEpData(epData);
				this.autoActionInfo.setEtrDataArr(null);
				this.autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+ep+">"+mDevice.getDeviceType());
				fireShortCutSelectDataListener();
			}
		}
		protected boolean isOpened() {
			return judgeStatus("1");
		}
		protected boolean isClosed() {
			return judgeStatus("0");
		}
		protected boolean isStoped() {
			if( mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				String epData = "";
				epData += controlable.getStopProtocol();
				return StringUtil.equals(epData, this.autoActionInfo.getEpData());
			}
			return false;
		}
		public boolean isStopVisiable() {
			return isStopVisiable;
		}
		public void setStopVisiable(boolean isStopVisiable) {
			this.isStopVisiable = isStopVisiable;
		}
		public boolean isOpenVisiable() {
			return isOpenVisiable;
		}
		public void setOpenVisiable(boolean isOpenVisiable) {
			this.isOpenVisiable = isOpenVisiable;
		}
		public boolean isCloseVisiable() {
			return isCloseVisiable;
		}
		public void setCloseVisiable(boolean isCloseVisiable) {
			this.isCloseVisiable = isCloseVisiable;
		}

		public boolean judgeStatus(String status){
			String epData=this.autoActionInfo.getEpData();
			if(!StringUtil.isNullOrEmpty(epData)){
				String type=epData.substring(2,3)+"";
				if(!type.equals(status)){
					return false;
				}
			}
			JSONArray etrDataArr = autoActionInfo.getEtrDataArr();
			if(etrDataArr!=null&&etrDataArr.size()>0){
				for(int i=0;i<etrDataArr.size();i++){
					String data=((String)etrDataArr.get(i));
					String type=data.substring(2,3)+"";
					if(!type.equals(status)){
						return false;
					}
				}
			}

			if(StringUtil.isNullOrEmpty(epData)&&etrDataArr==null){
				return false;
			}

			return true;
		}

	}

	protected String[] mSwitchStatus;

	protected LinearLayout mLinearLayout ;

	private static final String SPLIT_SYMBOL = ">";

	/**
	 * 设置dialog
	 */
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater,   AutoActionInfo autoActionInfo) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		View contentView =  inflater.inflate(cc.wulian.app.model.device.R.layout.task_manager_common_light_setting_view_layout, null);
		mLinearLayout =  (LinearLayout) contentView.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_setting_view_layout);
		mSwitchStatus = new String[getTouchEPResources().length]; //动态初始化
		for(int i=0;i<mSwitchStatus.length;i++){
			mSwitchStatus[i]="1"+(i+1)+"2";
		}
		String epData = autoActionInfo.getEpData();
		int position;
		if(!StringUtil.isNullOrEmpty(epData)){
			String type=epData.substring(1,2)+"";
			if(type.equals("0")){
				for(int i=0;i<mSwitchStatus.length;i++){
					mSwitchStatus[i]="1"+(i+1)+epData.substring(2);
				}
			}else{
				position=Integer.parseInt(epData.substring(1,2)+"");
				mSwitchStatus[position-1]=epData;
			}

		}
		JSONArray etrDataArr = autoActionInfo.getEtrDataArr();
		if(etrDataArr!=null&&etrDataArr.size()>0){
			for(int i=0;i<etrDataArr.size();i++){
				String data=((String)etrDataArr.get(i));
				position=Integer.parseInt(data.substring(1,2)+"");
				mSwitchStatus[position-1]=data;
			}
		}
		for (int i = 0;i<getTouchEPResources().length;i++) {
			mLinearLayout.addView(addChildView(i,getTouchEPResources()[i],autoActionInfo));
		}
		holder.setShowDialog(true);
		holder.setContentView(contentView);
		holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
	private View addChildView(final int i,String str,final AutoActionInfo autoActionInfo) {
		// TODO 动态添加布局(xml方式)
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(cc.wulian.app.model.device.R.layout.task_manager_common_light_setting_view, null);
		final WulianDevice device = getChildDevice(str);

		final TextView dev_name = (TextView) view.findViewById(cc.wulian.app.model.device.R.id.device_common_light_setting_dev_name);
		final ImageView switch_status_button_on = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_on);
		final ImageView switch_status_button_off = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_off);
		final ImageView switch_status_button_convert = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_convert);
		final ImageView switch_status_button_unchange = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_unchange);

		final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
		if (!StringUtil.isNullOrEmpty(autoActionInfo.getEpData())) {
			String stateData=mSwitchStatus[i].substring(2);
			if (!StringUtil.isNullOrEmpty(mSwitchStatus[i])) {
				if (stateData.equals("2")) {//不变。。默认为不变
					switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);

				} else if (stateData.equals("1")) {
					switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
					switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				} else if (stateData.equals("0")) {
					switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
					switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				}else if (stateData.equals("3")) {
					switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
					switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				}
			} else {
				// mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_open).setVisibility(View.GONE);
				// mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_close).setVisibility(View.VISIBLE);
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				mSwitchStatus[i] = "1"+(i+1)+"2";
			}
		}
		if (!StringUtil.isNullOrEmpty(device.getDeviceInfo().getDevEPInfo().getEpName())) {
			dev_name.setText((i + 1) + "." + device.getDeviceInfo().getDevEPInfo().getEpName());
		} else {
			dev_name
					.setText((i + 1)
							+ "."
							+ getResources().getString(
							cc.wulian.app.model.device.R.string.device_type_11));
		}
		switch_status_button_on.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				mSwitchStatus[i] = "1"+(i+1)+"1";
				setautoActionInfo(autoActionInfo);
			}
		});
		switch_status_button_off.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Logger.debug("i="+i);
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				mSwitchStatus[i] = "1"+(i+1)+"0";
				setautoActionInfo(autoActionInfo);
			}
		});
		switch_status_button_unchange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Logger.debug("i="+i);
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				mSwitchStatus[i] = "1"+(i+1)+"2";
				setautoActionInfo(autoActionInfo);
			}
		});
		switch_status_button_convert.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Logger.debug("i="+i);
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				mSwitchStatus[i] = "1"+(i+1)+"3";
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
		JSONArray etrDataArr=new JSONArray();
		for(int i=0;i<mSwitchStatus.length;i++){
			if(i==0){
				autoActionInfo.setEpData(mSwitchStatus[0]);
			}else{
				etrDataArr.add(mSwitchStatus[i]);
			}
		}
		autoActionInfo.setEtrDataArr(etrDataArr);
		autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
				+ getDeviceType() + SPLIT_SYMBOL + EP_0 + SPLIT_SYMBOL
				+ getDeviceType());
	}

}
