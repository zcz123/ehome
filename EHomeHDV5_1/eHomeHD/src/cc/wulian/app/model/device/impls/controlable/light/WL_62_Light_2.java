package cc.wulian.app.model.device.impls.controlable.light;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.DisplayUtil;

/**
 * 0:关,1:开,255:异常
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_LIGHT_2 }, category = Category.C_LIGHT)
public class WL_62_Light_2 extends ControlableDeviceImpl {
	private static final String DATA_CTRL_STATE_OPEN_1 = "1";
	private static final String DATA_CTRL_STATE_CLOSE_0 = "0";

	private static final int SMALL_OPEN_D = R.drawable.device_button_2_open;
	private static final int SMALL_CLOSE_D = R.drawable.device_button_2_close;

	private static final String SPLIT_SYMBOL = ">";
	
	private static final int SMALL_STATE_BACKGROUND = R.drawable.device_button_state_background;
    protected String[] mSwitchStatus;
	
    protected LinearLayout mLinearLayout ;
	protected LinearLayout mLightLayout;

	public WL_62_Light_2(Context context, String type) {
		super(context, type);
	}

	private static final String[] EP_SEQUENCE = { EP_14, EP_15 };
	private String mState="";

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
	}

	@Override
	protected boolean isMultiepDevice() {
		return true;
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
		} else {
			super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
		}
	}

	@Override
	public String getOpenProtocol() {
		return DATA_CTRL_STATE_OPEN_1;
	}

	@Override
	public String getCloseProtocol() {
		return DATA_CTRL_STATE_CLOSE_0;
	}

	@Override
	public String getOpenSendCmd() {
		return getOpenProtocol();
	}

	@Override
	public String getCloseSendCmd() {
		return getCloseProtocol();
	}

	public int getOpenSmallIcon() {
		return SMALL_OPEN_D;
	}

	public int getCloseSmallIcon() {
		return SMALL_CLOSE_D;
	}
	@Override
	public boolean isOpened() {
		if (getChildDevices() != null && !getChildDevices().isEmpty()) {
			boolean isOpened=false;
			for (WulianDevice device : getChildDevices().values()) {
				if (device instanceof Controlable) {
					DeviceEPInfo deviceEpInfo=device.getDeviceInfo().getDevEPInfo();
					if(getOpenProtocol().equals(deviceEpInfo.getEpData())){
						isOpened=true;
						break;
					}

				}
			}
			return isOpened;
		} else {
			Log.d("WL_62", "isOpened 1: mState="+mState);
			return getOpenProtocol().equals(mState);
		}
	}

	public boolean isClosed() {
		return !isOpened();
	}

	public Drawable getStateSmallIcon() {
		List<Drawable> drawers = new ArrayList<Drawable>();
		for (String ep : childDeviceMap.keySet()) {
			final WulianDevice device = childDeviceMap.get(ep);
			if (device instanceof Controlable) {
				Controlable controlable = (Controlable) device;
				if (controlable.isOpened()) {
					drawers.add(mResources.getDrawable(getOpenSmallIcon()));
				} else {
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

	public String[] getLightEPResources() {
		return EP_SEQUENCE;
	}

	public String[] getLightEPNames() {
		String ep14Name = DeviceUtil.ep2IndexString(EP_14)
				+ getResources().getString(R.string.device_key_scene_bind);
		String ep15Name = DeviceUtil.ep2IndexString(EP_15)
				+ getResources().getString(R.string.device_key_scene_bind);
		return new String[] { ep14Name, ep15Name };
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		String state = "";
		int color = COLOR_NORMAL_ORANGE;

		if (isOpened()) {
			state = getString(R.string.device_state_open);
			color = COLOR_CONTROL_GREEN;
		} else if (isClosed()) {
			state = getString(R.string.device_state_close);
			color = COLOR_NORMAL_ORANGE;
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(
				getColor(color)));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		mLightLayout = (LinearLayout) inflater.inflate(
				R.layout.device_other_touch_scene, container, false);
		return mLightLayout;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
	}

	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(epData);
	}

	private void disassembleCompoundCmd(String mEpData) {
		if(!StringUtil.isNullOrEmpty(mEpData)){
			Log.d("WL_62", "disassembleCompoundCmd 1: mState="+mState);
			mState = mEpData;
			Log.d("WL_62", "disassembleCompoundCmd 2: mState="+mState);
		}
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		showView();
	}

	/**
	 * 添加界面与数据处理
	 */

	public void showView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		int lightSwitchLength = getLightEPResources().length;
		int accountRow = lightSwitchLength;
		mLightLayout.removeAllViews();

		for (int i = 0; i < accountRow; i++) {
			LinearLayout rowLinearLayout = new LinearLayout(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 0);
			lp.weight = 1;
			rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			rowLinearLayout.setGravity(Gravity.CENTER);
			rowLinearLayout.setLayoutParams(lp);
			mLightLayout.addView(rowLinearLayout);
		}
		for (int j = 0; j < lightSwitchLength; j++) {
			final String ep = getLightEPResources()[j];
			int rowIndex = j;
			LinearLayout rowLineLayout = (LinearLayout) mLightLayout
					.getChildAt(rowIndex);
			LinearLayout itemView = (LinearLayout) inflater.inflate(
					R.layout.device_light_switch_chilid, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			itemView.setGravity(Gravity.CENTER_HORIZONTAL);
			itemView.setLayoutParams(lp);

			ImageView mSwitchLight = (ImageView) itemView
					.findViewById(R.id.dev_light_switch_imageview);
			TextView mLightText = (TextView) itemView
					.findViewById(R.id.dev_light_switch_textview);
			if(getChildDevice(ep) != null){
				final WulianDevice device = getChildDevice(ep);
				if (device instanceof Controlable) {
					Controlable controlable = (Controlable) device;
					DeviceEPInfo deviceEpInfo=device.getDeviceInfo().getDevEPInfo();
					Log.d("WL_62", "showView: ep="+deviceEpInfo.getEp()+" epdata="+deviceEpInfo.getEpData());
					String epName = device.getDeviceInfo().getDevEPInfo()
							.getEpName();
					if (getOpenProtocol().equals(deviceEpInfo.getEpData())) {
						mSwitchLight.setImageDrawable(getResources().getDrawable(
								R.drawable.device_light_module_open));
					} else {
						mSwitchLight.setImageDrawable(getResources().getDrawable(
								R.drawable.device_light_module_close));
					}
					if (!StringUtil.isNullOrEmpty(epName)) {
						mLightText.setText((j + 1) + "." + epName);
					} else {
						mLightText
								.setText((j + 1)
										+ "."
										+ getResources().getString(
												R.string.device_type_11));
					}
					mSwitchLight.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							fireWulianDeviceRequestControlSelf();
							controlDevice(ep,device.getDeviceInfo().getDevEPInfo().getEpType(), null);
						}
					});
				}
			}else{
				mSwitchLight.setImageDrawable(getResources().getDrawable(
						R.drawable.device_light_module_close));
				mLightText
				.setText((j + 1)
						+ "."
						+ getResources().getString(
								R.string.device_type_11));
			}
			
			rowLineLayout.addView(itemView);
		}
	}

	protected DeviceShortCutControlItem getContrableShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new ControlableDeviceShortCutControlItem(
					inflater.getContext()) {

				@Override
				protected void clickClose() {
					Map<String,DeviceEPInfo> infoMap = mDevice.getDeviceInfo().getDeviceEPInfoMap();
					if(infoMap != null && mDevice instanceof Controlable){
						Controlable controlable = (Controlable)mDevice;
						String epData = "";
						for(DeviceEPInfo info : infoMap.values()){
							epData += controlable.getCloseProtocol();
						}
						SendMessage.sendControlDevMsg(mDevice.getDeviceGwID(),
								mDevice.getDeviceID(),  EP_0,getDeviceType(), epData);
					}
				}

				@Override
				protected void clickOpen() {
					Map<String,DeviceEPInfo> infoMap = mDevice.getDeviceInfo().getDeviceEPInfoMap();
					if(infoMap != null && mDevice instanceof Controlable){
						Controlable controlable = (Controlable)mDevice;
						String epData = "";
						for(DeviceEPInfo info : infoMap.values()){
							epData += controlable.getOpenProtocol();
						}
						SendMessage.sendControlDevMsg(mDevice.getDeviceGwID(),
								mDevice.getDeviceID(), EP_0, getDeviceType(), epData);
					}
				}
			};
		}
		item.setWulianDevice(this);
		return item;
	}
	   /**
	    * 设置dialog
	    */
		@Override
		public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
				LayoutInflater inflater,   AutoActionInfo autoActionInfo) {
			DialogOrActivityHolder holder = new DialogOrActivityHolder();
			View contentView =  inflater.inflate(R.layout.task_manager_common_light_setting_view_layout, null);
			mLinearLayout =  (LinearLayout) contentView.findViewById(R.id.task_manager_common_light_setting_view_layout);
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
			View view = inflater.inflate(R.layout.task_manager_common_light_setting_view, null);
			final WulianDevice device = getChildDevice(str);
			
			final TextView dev_name = (TextView) view.findViewById(R.id.device_common_light_setting_dev_name);
			final ImageView switch_status_button_on = (ImageView) view.findViewById(R.id.task_manager_common_light_button_on);
			final ImageView switch_status_button_off = (ImageView) view.findViewById(R.id.task_manager_common_light_button_off);
			final ImageView switch_status_button_convert = (ImageView) view.findViewById(R.id.task_manager_common_light_button_convert);
			final ImageView switch_status_button_unchange = (ImageView) view.findViewById(R.id.task_manager_common_light_button_unchange);
			
			
			final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
			mSwitchStatus[i] = "2";
		if (!StringUtil.isNullOrEmpty(autoActionInfo.getEpData())) {
			String epdata = autoActionInfo.getEpData();
			
			if (epdata.length() > 1) {
				mSwitchStatus[i] = epdata.substring(i, i + 1);
			}else{
				mSwitchStatus[i] = "2";
			}
			
			if (StringUtil.equals(type[2], getLightEPResources()[i])) {
				mSwitchStatus[i] = epdata;
			}

			if (!StringUtil.isNullOrEmpty(mSwitchStatus[i])) {
				if (mSwitchStatus[i].equals("2")) {//不变。。默认为不变
					switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_selected);
					
				} else if (mSwitchStatus[i].equals("1")) {
					switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_selected);
					switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				} else if (mSwitchStatus[i].equals("0")) {
					switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_selected);
					switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				}else if (mSwitchStatus[i].equals("3")) {
					switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_selected);
					switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				}
			} else {
				// mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_open).setVisibility(View.GONE);
				// mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_close).setVisibility(View.VISIBLE);
				switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_selected);
				mSwitchStatus[i] = "2";
			}
		}else{
			String mSetSwitchStatus = "" ;
			for(int j = 0; j < getLightEPResources().length; j++){
				mSetSwitchStatus += "2";
			}
			autoActionInfo.setEpData(mSetSwitchStatus);
			autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
					+ getDeviceType() + SPLIT_SYMBOL + EP_0 + SPLIT_SYMBOL
					+ getDeviceType());
		}
//			else{
//				mSwitchStatus[i] = "2";
//			}
//
//			dev_name.setText(device.getDeviceInfo().getDevEPInfo().getEpName());
			if (!StringUtil.isNullOrEmpty(device.getDeviceInfo().getDevEPInfo().getEpName())) {
				dev_name.setText((i + 1) + "." + device.getDeviceInfo().getDevEPInfo().getEpName());
			} else {
				dev_name
						.setText((i + 1)
								+ "."
								+ getResources().getString(
										R.string.device_type_11));
			}
			switch_status_button_on.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_selected);
						switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
						switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
						switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
						mSwitchStatus[i] = "1";
						setautoActionInfo(autoActionInfo);
					}
				});
			switch_status_button_off.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Logger.debug("i="+i);
					switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_selected);
					switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					mSwitchStatus[i] = "0";
					setautoActionInfo(autoActionInfo);
				}
			});
			switch_status_button_unchange.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Logger.debug("i="+i);
					switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_selected);
					mSwitchStatus[i] = "2";
					setautoActionInfo(autoActionInfo);
				}
			});
			switch_status_button_convert.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Logger.debug("i="+i);
					switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_selected);
					switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					mSwitchStatus[i] = "3";
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
}