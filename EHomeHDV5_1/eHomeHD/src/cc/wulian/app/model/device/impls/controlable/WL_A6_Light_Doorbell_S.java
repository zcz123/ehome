package cc.wulian.app.model.device.impls.controlable;

import java.util.Map;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_DOORBELL_S }, category = Category.C_SECURITY)
public class WL_A6_Light_Doorbell_S extends AbstractSwitchDevice {

	private static final String DATA_LINKAGE_FORBID = "00";
	private static final String DATA_LINKAGE_ALLOW = "01";
	private static final String DATA_CTRL_PLAY_MUSIC = "11";
	private static final String DATA_CTRL_SET_RING = "21";
	private static final String DATA_CTRL_SET_VOLUME = "31";
	private static final String DATA_CTRL_SET_LINKAGE = "4";
	private static final String DATA_CTRL_0 = "0";
	private static final String DATA_CTRL_1 = "1";
	private static final String STATE_START_WITH = "09";

	private int SMALL_ALLOW_D = R.drawable.device_doorbell_s_allow;
	private int SMALL_FORBID_D = R.drawable.device_doorbell_s_forbid;

	private int BIG_ALLOW_D = R.drawable.device_doorbell_s_allow_big;
	private int BIG_FORBID_D = R.drawable.device_doorbell_s_forbid_big;
	private String mLinkageState = "";

	public WL_A6_Light_Doorbell_S(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_PLAY_MUSIC;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_PLAY_MUSIC;
	}

	@Override
	public int getOpenSmallIcon() {
		return SMALL_ALLOW_D;
	}

	@Override
	public int getCloseSmallIcon() {
		return SMALL_FORBID_D;
	}

	@Override
	public int getOpenBigPic() {
		return BIG_ALLOW_D;
	}

	@Override
	public int getCloseBigPic() {
		return BIG_FORBID_D;
	}

	@Override
	public boolean isOpened() {
		if (epData!=null&&epData.length() >= 8)
			return StringUtil.toInteger(epData.substring(2, 4), 16) == 1;
		return false;
	}

	@Override
	public boolean isClosed() {
		if (epData!=null&&epData.length() >= 8)
			return StringUtil.toInteger(epData.substring(2, 4), 16) == 0;
		return false;
	}

	private void disassembleCompoundCmd(
			String epData) {
		if (epData != null && epData.length() >0 && epData.startsWith(STATE_START_WITH)) {
			mLinkageState = epData.substring(2, 4);
			// device.mRingStr = epData.substring(4, 6);
			// device.mVolume = epData.substring(6, 8);
		}
	}
	
	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	private ImageView mBottomView;
	private ImageView mPlayView;
	private ImageView mSwitchRingView;
	private ImageView mAdjustolumeView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_doorbell_layout, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		mBottomView = (ImageView) view.findViewById(R.id.dev_state_imageview_0);
		mBottomView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createControlOrSetDeviceSendData(
						DEVICE_OPERATION_CTRL,
						StringUtil.equals(mLinkageState, DATA_LINKAGE_FORBID) ? DATA_CTRL_SET_LINKAGE
								+ DATA_CTRL_1
								: DATA_CTRL_SET_LINKAGE
										+ DATA_CTRL_0,
						true);
			}
		});

		mPlayView = (ImageView) view.findViewById(R.id.dev_state_button_0);
		mSwitchRingView = (ImageView) view
				.findViewById(R.id.dev_state_button_1);
		mAdjustolumeView = (ImageView) view
				.findViewById(R.id.dev_state_button_2);

		mPlayView.setImageDrawable(getResources().getDrawable(
				R.drawable.device_doorbell_s_play_selector));
		mSwitchRingView.setImageDrawable(getResources().getDrawable(
				R.drawable.device_doorbell_s_switch_ring_selector));
		mAdjustolumeView.setImageDrawable(getResources().getDrawable(
				R.drawable.device_doorbell_s_adjust_volume_selector));

		mPlayView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,
						DATA_CTRL_PLAY_MUSIC, true);
			}
		});
		mSwitchRingView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,
						DATA_CTRL_SET_RING, true);
			}
		});

		mAdjustolumeView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,
						DATA_CTRL_SET_VOLUME, true);
			}
		});
		mViewCreated = true;
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		Drawable drawable = getStateBigPictureArray()[0];
		mBottomView.setImageDrawable(drawable);
		drawable = mBottomView.getDrawable();
		if (drawable instanceof AnimationDrawable) {
			((AnimationDrawable) drawable).start();
		}
	}
	@Override
	public DeviceShortCutControlItem onCreateShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		return getDefaultShortCutControlView(item, inflater);
	}
	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(epData);
	}
	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		if(item == null){
			item = new ShortCutLightDoorBellSelectDataItem(inflater.getContext());
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}
	private class ShortCutLightDoorBellSelectDataItem extends DeviceShortCutSelectDataItem{

		protected boolean isOpenVisiable = true;
		protected LinearLayout controlableLineLayout;
		protected ImageView openImageView;
		protected ImageView stopImageView;
		protected ImageView closeImageView;
		public ShortCutLightDoorBellSelectDataItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_controlable, null);
			openImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
			stopImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
			closeImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
			closeImageView.setVisibility(View.GONE);
			stopImageView.setVisibility(View.GONE);
			controlLineLayout.addView(controlableLineLayout);
		}
		@Override
		public void setWulianDeviceAndSelectData(WulianDevice device,
				AutoActionInfo autoActionInfo) {
			super.setWulianDeviceAndSelectData(device, autoActionInfo);
			if(device instanceof Controlable){
				if(isOpenVisiable){
					openImageView.setVisibility(View.VISIBLE);
					if(isOpened()){
						openImageView.setSelected(true);
						contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_background);
					}else{
						openImageView.setSelected(false);
						openImageView.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								clickOpen();
							}
						});
					}
				}else{
					openImageView.setVisibility(View.INVISIBLE);
				}
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
					epData += controlable.getOpenProtocol();
				}
				return StringUtil.equals(epData, this.autoActionInfo.getEpData());
			}
			return false;
		}
	}
}