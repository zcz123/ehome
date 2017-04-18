package cc.wulian.app.model.device.impls.controlable.dimmerlight;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl.ShortCutControlableDeviceSelectDataItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceRequestListener;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.DisplayUtil;

/**
 * 0:关,100:开,1～99:亮度
 * 
 * <p>
 * <b>Chang Log</b>
 * <p>
 * 1.大图显示布局改为上下放置
 * <p>
 */
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_DUAL_D_LIGHT}, 
		category = Category.C_LIGHT)
public class WL_13_Dual_D_Light extends ControlableDeviceImpl
{
	private static final String DATA_CTRL_STATE_OPEN_100 	= "100";
	private static final String DATA_CTRL_STATE_CLOSE_000 	= "000";
	
	private static final int SMALL_CLOSE_D = R.drawable.device_d_light_close;
	private static final int SMALL_OPEN_D = R.drawable.device_d_light_open;
	private static final String SPLIT_SYMBOL = ">";
	private DeviceCache deviceCache;
	private Map<String,WulianDevice> deviceMap = new LinkedHashMap<String,WulianDevice>();
	
	private int mSeekProgress1 = 0;
	private int mSeekProgress2 = 0;
	public WL_13_Dual_D_Light( Context context, String type )
	{
		super(context, type);
		deviceCache = DeviceCache.getInstance(context);
	}
	
	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	@Override
	public void onDeviceUp( DeviceInfo devInfo ){
		super.onDeviceUp(devInfo);
		Iterator<DeviceEPInfo> iterator = devInfo.getDeviceEPInfoMap().values().iterator();
		while (iterator.hasNext()) {
			DeviceEPInfo deviceEPInfo = iterator.next();
			DeviceInfo info = devInfo.clone();
			info.setDevEPInfo(deviceEPInfo);
			WulianDevice device = deviceCache.createDeviceWithType(mContext, deviceEPInfo.getEpType());
			device.setDeviceParent(this);
			device.onDeviceUp(info);
			deviceMap.put(deviceEPInfo.getEp(), device);
		}
	}
	@Override
	public  void onDeviceData(String gwID, String devID,
			DeviceEPInfo devEPInfo,String cmd,String mode) {
		String ep = devEPInfo.getEp();
		WulianDevice device = getChildDevice(ep);
		device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
		removeCallbacks(mRefreshStateRunnable);
		post(mRefreshStateRunnable);
		fireDeviceRequestControlData();
	}

	@Override
	public String getOpenProtocol(){
		return DATA_CTRL_STATE_OPEN_100;
	}

	@Override
	public String getCloseProtocol(){
		return DATA_CTRL_STATE_CLOSE_000;
	}

	@Override
	public String getOpenSendCmd(){
		return DATA_CTRL_STATE_OPEN_100;
	}

	@Override
	public String getCloseSendCmd(){
		return DATA_CTRL_STATE_CLOSE_000;
	}

	@Override
	public boolean isOpened(){
		return !isClosed();
	}

	@Override
	public boolean isClosed(){
		boolean isClosed = true;;
		for(String ep : getChildDevices().keySet()){
			WulianDevice device =  getChildDevice(ep);
			String epData = device.getDeviceInfo().getDevEPInfo().getEpData();
			if(!DATA_CTRL_STATE_CLOSE_000.equals(epData)){	
				isClosed =  false;
			}
		}
		return isClosed;
	}
	@Override
	public Map<String,WulianDevice> getChildDevices() {
		return deviceMap;
	}
	public Drawable getStateSmallIcon(){
		List<Drawable> drawers = new ArrayList<Drawable>();
		for(String ep : deviceMap.keySet()){
			final WulianDevice device = deviceMap.get(ep);
			if(device instanceof Controlable){
				Controlable controlable = (Controlable)device;
				if(controlable.isOpened()){
					drawers.add(mResources.getDrawable(SMALL_OPEN_D));
				}else{
					drawers.add(mResources.getDrawable(SMALL_CLOSE_D));
				}
			}
		}
		return DisplayUtil.getDrawablesMerge(drawers.toArray(new Drawable[]{}));
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ){
		LinearLayout mergeGroup = new LinearLayout(mContext);
//		mergeGroup.setGravity(Gravity.CENTER);
		for(String ep : getChildDevices().keySet()){
			WulianDevice device =  getChildDevice(ep);
			device.onAttachView(mContext);
			View view = device.onCreateView(inflater, container, saveState);
			device.onViewCreated(view, saveState);
			device.registerControlRequestListener(new OnWulianDeviceRequestListener() {
				
				@Override
				public void onDeviceRequestControlSelf(WulianDevice device) {
					fireWulianDeviceRequestControlSelf();
				}
				
				@Override
				public void onDeviceRequestControlData(WulianDevice device) {
					
				}
			});
			LinearLayout group = new LinearLayout(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1.0f);
			group.setLayoutParams(lp);
			group.setGravity(Gravity.CENTER);
			group.addView(view);
			mergeGroup.setOrientation(mergeGroup.VERTICAL);
			mergeGroup.setPadding(50, 50, 50, 0);
			mergeGroup.addView(group);
		}
		return mergeGroup;
	}

	@Override
	public void onViewCreated( View view, Bundle saveState ){
		super.onViewCreated(view, saveState);
		
	}

	@Override
	public void initViewStatus(){
		for(String ep : getChildDevices().keySet()){
			WulianDevice device =  getChildDevice(ep);
			device.initViewStatus();
		}
	}
	
	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		return getTwoLightSelectDataShortCutView(item, inflater, autoActionInfo);
	}
	
	protected DeviceShortCutSelectDataItem getTwoLightSelectDataShortCutView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,AutoActionInfo autoActionInfo) {
		if(item == null){
			item = new ShortCutControlableTwoLightSelectDataItem(inflater.getContext());
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}
	
	public static class ShortCutControlableTwoLightSelectDataItem extends DeviceShortCutSelectDataItem{

		protected boolean isOpenVisiable = true;
		protected boolean isCloseVisiable = true;
		protected LinearLayout controlableLineLayout;
		protected ImageView openImageView;
		protected ImageView closeImageView;
		protected ImageView stopImageView;
		private OnClickListener cliclListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v == openImageView){
					clickOpen();
				}else if(v == closeImageView){
					clickClose();
				}
			}
		};
		
		public ShortCutControlableTwoLightSelectDataItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_controlable, null);
			openImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
			stopImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
			stopImageView.setVisibility(View.INVISIBLE);
			closeImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
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
						contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_background);
					}else{
						openImageView.setSelected(false);
						openImageView.setOnClickListener(cliclListener);
					}
				}else{
					openImageView.setVisibility(View.INVISIBLE);
				}
				if(isCloseVisiable){
					if(isClosed()){
						closeImageView.setSelected(true);
						contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_background);
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
				Map<String,WulianDevice> childDevices = mDevice.getChildDevices();
				String epData = "";
				String ep = "";
				if(childDevices != null){
					ep = WulianDevice.EP_0;
					for(WulianDevice childDevice : childDevices.values()){
						if(childDevice instanceof Controlable){
							Controlable c = (Controlable)mDevice;
							epData += c.getCloseProtocol();
						}
					}
				}else{
					ep = mDevice.getDefaultEndPoint();
					epData += controlable.getCloseProtocol();
				}
				this.autoActionInfo.setEpData(epData);
				this.autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+ep+">"+mDevice.getDeviceType());
				fireShortCutSelectDataListener();
			}
		}

		protected void clickOpen() {
			if( mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				Map<String,WulianDevice> childDevices = mDevice.getChildDevices();
				String epData = "";
				String ep = "";
				if(childDevices != null){
					ep = WulianDevice.EP_0;
					for(WulianDevice childDevice : childDevices.values()){
						if(childDevice instanceof Controlable){
							Controlable c = (Controlable)childDevice;
							epData += c.getOpenProtocol();
						}
					}
				}else{
					ep = mDevice.getDefaultEndPoint();
					epData += controlable.getOpenProtocol();
				}
				this.autoActionInfo.setEpData(epData);
				this.autoActionInfo.setObject(mDevice.getDeviceID()+">"+mDevice.getDeviceType()+">"+ep+">"+mDevice.getDeviceType());
				fireShortCutSelectDataListener();
			}
		}
		protected boolean isOpened() {
			if( mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				Map<String,WulianDevice> childDevices = mDevice.getChildDevices();
				String epData = "";
				String ep = "";
				if(childDevices != null){
					ep = WulianDevice.EP_0;
					for(WulianDevice childDevice : childDevices.values()){
						if(childDevice instanceof Controlable){
							Controlable c = (Controlable)childDevice;
							epData += c.getOpenProtocol();
						}
					}
				}else{
					ep = mDevice.getDefaultEndPoint();
					epData = controlable.getOpenProtocol();
				}
				return StringUtil.equals(epData, this.autoActionInfo.getEpData());
			}
			return false;
		}
		protected boolean isClosed() {
			if( mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				Map<String,WulianDevice> childDevices = mDevice.getChildDevices();
				String epData = "";
				String ep = "";
				if(childDevices != null){
					ep = WulianDevice.EP_0;
					for(WulianDevice childDevice : childDevices.values()){
						if(childDevice instanceof Controlable){
							Controlable c = (Controlable)mDevice;
							epData += c.getCloseProtocol();
						}
					}
				}else{
					ep = mDevice.getDefaultEndPoint();
					epData += controlable.getCloseProtocol();
				}
				return StringUtil.equals(epData, this.autoActionInfo.getEpData());
			}
			return false;
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
		
	
		
	}
	
	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,String ep,
			String epData) {
		if(epData == null)
			epData = "";
		View view =  inflater.inflate(cc.wulian.smarthomev5.R.layout.scene_task_control_dimmer_switch, null);
		
		linkTaskControlEPData = new StringBuffer(epData);
		final TextView lightTextView = (TextView)view.findViewById(R.id.dev_state_textview_0);
		SeekBar seekBarLight = (SeekBar)view.findViewById(R.id.dev_state_seekbar_0);
		seekBarLight.setProgress(0);
		
		if(!StringUtil.isNullOrEmpty(epData) && StringUtil.toInteger(epData) >= 0){
			int lightProcess = StringUtil.toInteger(linkTaskControlEPData);
			seekBarLight.setProgress(lightProcess);
			lightTextView.setText(lightProcess+"%");
		}
		
		seekBarLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int mSeekProgress = seekBar.getProgress();
				lightTextView.setText(mSeekProgress+"%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
//				linkTaskControlEPData.delete(0,linkTaskControlEPData.length());
//				linkTaskControlEPData.append(StringUtil.appendLeft(seekBar.getProgress()+"", 3, '0'));
//				lightTextView.setText((int)seekBar.getProgress()+"%");
				int mSeekProgress = seekBar.getProgress();
				linkTaskControlEPData =  new StringBuffer(mSeekProgress + "");
				lightTextView.setText(mSeekProgress+"%");
			}
			
		});
		return createControlDataDialog(inflater.getContext(), view);
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		View contentview =  inflater.inflate(R.layout.task_manager_two_light_device_choose, null);
		final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
		String epdata = autoActionInfo.getEpData();
		TextView oneLightTextView = (TextView)contentview.findViewById(R.id.house_link_task_two_light_text_1);
		TextView twoLightTextView = (TextView)contentview.findViewById(R.id.house_link_task_two_light_text_2);
		final ImageView oneLightImageVIew = (ImageView)contentview.findViewById(R.id.house_link_task_two_light_imageview_1);
		final ImageView twoLightImageVIew = (ImageView)contentview.findViewById(R.id.house_link_task_two_light_imageview_2);
		final TextView oneLightSeek1 = (TextView)contentview.findViewById(R.id.house_link_task_two_light_percent_text_1);
		final TextView twoLightSeek2 = (TextView)contentview.findViewById(R.id.house_link_task_two_light_percent_text_2);
		SeekBar seekBarLight1 = (SeekBar)contentview.findViewById(R.id.house_link_task_two_light_seekbar_1);
		SeekBar seekBarLight2 = (SeekBar)contentview.findViewById(R.id.house_link_task_two_light_seekbar_2);
		oneLightTextView.setText(childDeviceName(EP_14));
		twoLightTextView.setText(childDeviceName(EP_15));
		
		if(!StringUtil.isNullOrEmpty(epdata) && StringUtil.toInteger(epdata) >= 0){
			if(StringUtil.equals(type[2], EP_14)){
				oneLightImageVIew.setSelected(true);
				int lightProcess = StringUtil.toInteger(epdata);
				seekBarLight1.setProgress(lightProcess);
				oneLightSeek1.setText(lightProcess+"%");
			}else if(StringUtil.equals(type[2], EP_15)){
				twoLightImageVIew.setSelected(true);
				int lightProcess = StringUtil.toInteger(epdata);
				seekBarLight2.setProgress(lightProcess);
				twoLightSeek2.setText(lightProcess+"%");
			}else{
				if(epdata.length() >= 6){
					oneLightImageVIew.setSelected(true);
					int onelightProcess = StringUtil.toInteger(epdata.substring(0,3));
					seekBarLight1.setProgress(onelightProcess);
					oneLightSeek1.setText(onelightProcess+"%");
					twoLightImageVIew.setSelected(true);
					int twolightProcess = StringUtil.toInteger(epdata.substring(3,6));
					seekBarLight2.setProgress(twolightProcess);
					twoLightSeek2.setText(twolightProcess+"%");
				}
			}
		}
		OnClickListener selectListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v == oneLightImageVIew){
					if(oneLightImageVIew.isSelected()){
						oneLightImageVIew.setSelected(false);
					}else{
						oneLightImageVIew.setSelected(true);
					}
				}else if(v == twoLightImageVIew){
					if(twoLightImageVIew.isSelected()){
						twoLightImageVIew.setSelected(false);
					}else{
						twoLightImageVIew.setSelected(true);
					}
				}
				setautoActionInfo(oneLightImageVIew,twoLightImageVIew,autoActionInfo);
			}
		};
		oneLightImageVIew.setOnClickListener(selectListener);
		twoLightImageVIew.setOnClickListener(selectListener);
		seekBarLight1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				oneLightImageVIew.setSelected(true);
				int mSeekProgress = seekBar.getProgress();
				oneLightSeek1.setText(mSeekProgress+"%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
//				linkTaskControlEPData.delete(0,linkTaskControlEPData.length());
//				linkTaskControlEPData.append(StringUtil.appendLeft(seekBar.getProgress()+"", 3, '0'));
//				lightTextView.setText((int)seekBar.getProgress()+"%");
				mSeekProgress1 = seekBar.getProgress();
				oneLightSeek1.setText(mSeekProgress1+"%");
				setautoActionInfo(oneLightImageVIew,twoLightImageVIew,autoActionInfo);
			}
			
		});
		seekBarLight2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				twoLightImageVIew.setSelected(true);
				int mSeekProgress = seekBar.getProgress();
				twoLightSeek2.setText(mSeekProgress+"%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
//				linkTaskControlEPData.delete(0,linkTaskControlEPData.length());
//				linkTaskControlEPData.append(StringUtil.appendLeft(seekBar.getProgress()+"", 3, '0'));
//				lightTextView.setText((int)seekBar.getProgress()+"%");
				mSeekProgress2 = seekBar.getProgress();
				twoLightSeek2.setText(mSeekProgress2+"%");
				setautoActionInfo(oneLightImageVIew,twoLightImageVIew,autoActionInfo);
			}
			
		});
		holder.setShowDialog(true);
		holder.setContentView(contentview);
		holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}

	private String childDeviceName(String ep){
		StringBuilder sb = new StringBuilder();
		sb.append(DeviceTool.getDeviceShowName(this));
		sb.append("-");
		sb.append(DeviceUtil.ep2IndexString(ep));
		return sb.toString();
	}
	
	private void setautoActionInfo(ImageView img1,ImageView img2,AutoActionInfo autoActionInfo) {
		if(img1.isSelected() && img2.isSelected()){
			autoActionInfo.setEpData(StringUtil.appendLeft(mSeekProgress1 + "", 3, '0') + StringUtil.appendLeft(mSeekProgress2 + "", 3, '0'));
			autoActionInfo.changeEpAndEpType(EP_0,getDeviceType());
		}else{
			if(img1.isSelected()){
				autoActionInfo.setEpData(mSeekProgress1 + "");
				autoActionInfo.changeEpAndEpType(EP_14,getDeviceInfo().getDevEPInfoByEP(EP_14).getEpType());
			}else if(img2.isSelected()){
				autoActionInfo.setEpData(mSeekProgress2 + "");
				autoActionInfo.changeEpAndEpType(EP_15,getDeviceInfo().getDevEPInfoByEP(EP_15).getEpType());
			}else{
				autoActionInfo.setEpData("");
				autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL + getDeviceType() + SPLIT_SYMBOL + EP_0 + SPLIT_SYMBOL + getDeviceType());
				autoActionInfo.changeEpAndEpType(EP_0,getDeviceType());
			}
		}
	}
	
}