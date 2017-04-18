package cc.wulian.app.model.device.impls.controlable;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.IProperties;
import cc.wulian.app.model.device.interfaces.IViewResource;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.SendMessage;

/**
 * Note:
 * <p>
 * 如果设备含有多键，需要重写getOpenProtocol和getCloseProtocol以指定单独ep的协议
 * <p>
 * 单独ep设备无需重写
 */
public abstract class ControlableDeviceImpl extends AbstractDevice implements Controlable
{
	protected String ep;
	protected String epType;
	protected String epData;
	protected String epStatus;
	protected String epMsg;
	
	public ControlableDeviceImpl( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		return null;
	}

	@Override
	public String getCloseSendCmd() {
		return null;
	}

	@Override
	public boolean isStoped() {
		return false;
	}

	@Override
	public String getStopSendCmd() {
		return getStopSendCmd();
	}

	@Override
	public String getStopProtocol() {
		return getStopSendCmd();
	}

	@Override
	protected IProperties createPropertiesProxy() {
		return null;
	}

	@Override
	protected IViewResource createViewResourceProxy() {
		return null;
	}
	public String vertifyDeviceData(String ep,String epType,String sendData) {
		if(!isNull(sendData))
			return sendData;
		if(getChildDevices() !=null && getChildDevice(ep) != null){
			try{
				String epData = getChildDevice(ep).getDeviceInfo().getDevEPInfo().getEpData();
				if(StringUtil.equals(epData, getOpenSendCmd())){
					sendData = getCloseSendCmd();
				}else{
					sendData = getOpenSendCmd();
				}
				return sendData;
			}catch(Exception e){
				
			}
			return null;
			
		}else{
			return  isOpened() ? getCloseSendCmd() : isClosed() ? getOpenSendCmd() : getOpenSendCmd();
		}
	}
	@Override
	public void refreshDevice(){
		DeviceEPInfo epInfo = getCurrentEpInfo();
		if(epInfo!=null){
			ep = epInfo.getEp();
			epType = epInfo.getEpType();
			epData = epInfo.getEpData();
			epStatus = epInfo.getEpStatus();
			epMsg=epInfo.getEpMsg();
		}
	}

	// as a general sense, each device protocol same as control cmd
	@Override
	public String getOpenProtocol(){
		return getOpenSendCmd();
	}

	// as a general sense, each device protocol same as control cmd
	@Override
	public String getCloseProtocol(){
		return getCloseSendCmd();
	}

	@Override
	public boolean isOpened() {
		return false;
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater){
		return getContrableShortCutView(item, inflater);
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
		private OnClickListener cliclListener = new OnClickListener() {
			
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
			controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_controlable, null);
			openImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
			stopImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
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
				if(isStopVisiable){
					if(isStoped()){
						stopImageView.setSelected(true);
						contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_background);
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
							Controlable c = (Controlable)childDevice;
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

		protected void clickStop() {
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
							epData += c.getStopProtocol();
						}
					}
				}else{
					ep = mDevice.getDefaultEndPoint();
					epData += controlable.getStopProtocol();
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
							Controlable c = (Controlable)childDevice;
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
		protected boolean isStoped() {
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
							epData += c.getStopProtocol();
						}
					}
				}else{
					ep = mDevice.getDefaultEndPoint();
					epData += controlable.getStopProtocol();
				}
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
		
	}
	public static class ControlableDeviceShortCutControlItem extends DeviceShortCutControlItem{

		protected boolean isStopVisiable = false;
		protected boolean isOpenVisiable = true;
		protected boolean isCloseVisiable = true;
		protected LinearLayout controlableLineLayout;
		protected ImageView openImageView;
		protected ImageView stopImageView;
		protected ImageView closeImageView;
		private OnClickListener cliclListener = new OnClickListener() {
			
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
		public ControlableDeviceShortCutControlItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_controlable, null);
			openImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
			stopImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
			closeImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
			controlLineLayout.addView(controlableLineLayout);
		}
		protected void clickClose() {
			Map<String,DeviceEPInfo> infoMap = mDevice.getDeviceInfo().getDeviceEPInfoMap();
			if(infoMap != null && mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				for(DeviceEPInfo info : infoMap.values()){
					SendMessage.sendControlDevMsg(mDevice.getDeviceGwID(), mDevice.getDeviceID(), info.getEp(), info.getEpType(), controlable.getCloseProtocol());
				}
			}
		}

		protected void clickStop() {
			Map<String,DeviceEPInfo> infoMap = mDevice.getDeviceInfo().getDeviceEPInfoMap();
			if(infoMap != null && mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				for(DeviceEPInfo info : infoMap.values()){
					SendMessage.sendControlDevMsg(mDevice.getDeviceGwID(), mDevice.getDeviceID(), info.getEp(), info.getEpType(), controlable.getStopProtocol());
				}
			}
		}

		protected void clickOpen() {
			Map<String,DeviceEPInfo> infoMap = mDevice.getDeviceInfo().getDeviceEPInfoMap();
			if(infoMap != null && mDevice instanceof Controlable){
				Controlable controlable = (Controlable)mDevice;
				for(DeviceEPInfo info : infoMap.values()){
					SendMessage.sendControlDevMsg(mDevice.getDeviceGwID(), mDevice.getDeviceID(), info.getEp(), info.getEpType(), controlable.getOpenProtocol());
				}
			}
		}
		@Override
		public void setWulianDevice(WulianDevice device) {
			super.setWulianDevice(device);
			if(device instanceof Controlable){
				
				if(isOpenVisiable){
					openImageView.setVisibility(View.VISIBLE);
					if(isOpened()){
						openImageView.setSelected(true);
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
					}else{
						closeImageView.setSelected(false);
						closeImageView.setOnClickListener(cliclListener);
					}
				}else{
					closeImageView.setVisibility(View.INVISIBLE);
				}
			}
		}
		protected boolean isOpened() {
			Controlable controlable = (Controlable)mDevice;
			return controlable.isOpened();
		}
		protected boolean isClosed() {
			Controlable controlable = (Controlable)mDevice;
			return controlable.isClosed();
		}
		protected boolean isStoped() {
			Controlable controlable = (Controlable)mDevice;
			return controlable.isStoped();
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
		
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, AutoActionInfo autoActionInfo) {
		return null;
	}
}