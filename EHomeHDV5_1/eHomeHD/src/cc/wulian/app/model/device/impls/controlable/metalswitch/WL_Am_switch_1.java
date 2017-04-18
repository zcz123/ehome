package cc.wulian.app.model.device.impls.controlable.metalswitch;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.SceneManager;

/**
 * 一键金属开关
 */
@DeviceClassify(devTypes = {"Am"}, category = Category.C_CONTROL)
public class WL_Am_switch_1 extends AbstractMetalSwitch
{

	protected String mSwitchMode;
	private static final String STATE_OPEN_CMD = "101";
	private static final String STATE_CLOSE_CMD = "100";

	private int SMALL_OPEN_D = cc.wulian.app.model.device.R.drawable.device_button_1_open;
	private int SMALL_CLOSE_D = cc.wulian.app.model.device.R.drawable.device_button_1_close;

	public WL_Am_switch_1(Context context, String type )
	{
		super(context, type);
	}

	@Override
	public void onResume() {
		super.onResume();
		controlDevice(ep, epType, "102");
	}

	@Override
	public String getDefaultDeviceName() {
		String defaultName = getDeviceInfo().getDevEPInfo().getEpName();
		if (isNull(defaultName)) {
			defaultName = getString(R.string.add_device_name_switch_1_key);
		}
		return defaultName;
	}

	@Override
	public boolean isOpened() {
		if(epData!=null&&epData.length()>5){
			return isSameAs("01",(epData.substring(4)));
		}else{
			return false;
		}
	}

	@Override
	public Drawable getStateSmallIcon() {
		return isOpened() ? getDrawable(getOpenSmallIcon()) : isClosed() ? getDrawable(getCloseSmallIcon()) : this
				.getDefaultStateSmallIcon();
	}

	public boolean isClosed() {
		return !isOpened();
	}

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

	@Override
	public String[] getTouchEPResources() {
		return new String[]{"14"};
	}

	@Override
	public String[] getTouchEPNames() {
		return new String[]{"key1"};
	}

	@Override
	public String[] getSwitchModes() {
		return new String[]{mSwitchMode};
	}

	@Override
	public String[] getSwitchStatus() {
		return new String[]{mSwitchStatus};
	}

	@Override
	protected int getOpenSwitchImage() {
		return super.getOpenSwitchImage();
	}

	@Override
	protected int getCloseSwitchImage() {
		return super.getCloseSwitchImage();
	}

	@Override
	public void handleEpData(String mEpData){
		Log.d("---ccc---", "mEpData:::"+mEpData);
		if(mEpData.length() == 6){
			if(isSameAs(mEpData.substring(2 ,4) ,"01")){
				if(isSameAs(mEpData.substring(0 ,2) ,"00")){
					if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_TURN)){
						mSwitchMode = SWTICH_MODE_TURN;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_SCENCE)){
						mSwitchMode = SWTICH_MODE_SCENCE;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_BIND)){
						mSwitchMode = SWTICH_MODE_BIND;
					}
				}
				if(isSameAs(mEpData.substring(0 ,2) ,"01")){
					mSwitchMode = SWTICH_MODE_TURN;
					if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_STATUS_ON)){
						mSwitchStatus = SWTICH_STATUS_ON;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_STATUS_OFF)){
						mSwitchStatus = SWTICH_STATUS_OFF;
					}
				}
			}
		}
	}

	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,AutoActionInfo autoActionInfo) {
		return getControlDeviceSelectDataShortCutView(item, inflater,autoActionInfo);
	}
	protected DeviceShortCutSelectDataItem getControlDeviceSelectDataShortCutView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,AutoActionInfo autoActionInfo) {
		if(item == null){
			item = new ControlableDeviceImpl.ShortCutControlableDeviceSelectDataItem(inflater.getContext());
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

	protected String mSwitchStatus;

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
		mLinearLayout.addView(addChildView(autoActionInfo));
		holder.setShowDialog(true);
		holder.setContentView(contentView);
		holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}

	private View addChildView(final AutoActionInfo autoActionInfo) {
		// TODO 动态添加布局(xml方式)
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(cc.wulian.app.model.device.R.layout.task_manager_common_light_setting_view, null);

		final TextView dev_name = (TextView) view.findViewById(cc.wulian.app.model.device.R.id.device_common_light_setting_dev_name);
		final ImageView switch_status_button_on = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_on);
		final ImageView switch_status_button_off = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_off);
		final ImageView switch_status_button_convert = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_convert);
		final ImageView switch_status_button_unchange = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_unchange);

		dev_name.setVisibility(View.INVISIBLE);
		final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
		mSwitchStatus = "";
		if (!StringUtil.isNullOrEmpty(autoActionInfo.getEpData())) {
			mSwitchStatus = autoActionInfo.getEpData();
			if (mSwitchStatus.equals("102")) {//不变。。默认为不变
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);

			} else if (mSwitchStatus.equals("101")) {
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
			} else if (mSwitchStatus.equals("100")) {
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
			}else if (mSwitchStatus.equals("103")) {
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
			mSwitchStatus = "102";
			autoActionInfo.setEpData(mSwitchStatus);
			autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
					+ getDeviceType() + SPLIT_SYMBOL + EP_14 + SPLIT_SYMBOL
					+ getDeviceType());
		}

		switch_status_button_on.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				mSwitchStatus = "101";
				setautoActionInfo(autoActionInfo,mSwitchStatus);
			}
		});
		switch_status_button_off.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				mSwitchStatus = "100";
				setautoActionInfo(autoActionInfo,mSwitchStatus);
			}
		});
		switch_status_button_unchange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				mSwitchStatus = "102";
				setautoActionInfo(autoActionInfo,mSwitchStatus);
			}
		});
		switch_status_button_convert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
				switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
				mSwitchStatus = "103";
				setautoActionInfo(autoActionInfo,mSwitchStatus);
			}
		});
		view.setLayoutParams(lp);
		return view;
	}

	private void setautoActionInfo(AutoActionInfo autoActionInfo,String selectData) {
		autoActionInfo.setEpData(selectData);
		autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
				+ getDeviceType() + SPLIT_SYMBOL + EP_14 + SPLIT_SYMBOL
				+ getDeviceType());
//		}
	}
}