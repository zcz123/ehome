package cc.wulian.app.model.device.interfaces;

import java.util.Map;
import java.util.zip.Inflater;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class DeviceShortCutControlItem {
	protected static final String UNIT_MORE = "[";
	protected static final String UNIT_LESS = "]";
	
	protected static Map<String, Integer> mRssiMap;
	protected static final String CONSTANT_COLOR_START = "<font color=#f31961>";
	protected static final String CONSTANT_COLOR_END = "</font>";

	protected LinearLayout lineLayout;
	protected ImageView deviceIconImageView;
	protected RelativeLayout divice_short_cut_content;
	protected TextView nameTextView;
	protected ImageView stateImageView;
	protected TextView stateTextView;
	protected TextView areaTextView;
	protected LinearLayout controlLineLayout;
	protected WulianDevice mDevice;
	protected Resources mResources;
	protected LayoutInflater inflater;
	protected Context mContext;
	protected DeviceCache mDeviceCache;

	public DeviceShortCutControlItem(Context context) {
		mContext = context;
		mRssiMap = MainApplication.getApplication().queryRssiInfoMap;
		mDeviceCache = DeviceCache.getInstance(context);
		inflater = LayoutInflater.from(context);
		mResources = context.getResources();
		lineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_item, null);
		deviceIconImageView = (ImageView)lineLayout.findViewById(R.id.device_short_cut_icon_iv);
		nameTextView = (TextView)lineLayout.findViewById(R.id.device_short_cut_name_tv);
		stateTextView = (TextView)lineLayout.findViewById(R.id.device_short_cut_state_tv);
		stateImageView = (ImageView) lineLayout.findViewById(R.id.device_short_cut_state_image);
		areaTextView = (TextView) lineLayout.findViewById(R.id.device_short_cut_areas_tv);
		controlLineLayout = (LinearLayout)lineLayout.findViewById(R.id.device_short_cut_control_ll);
	}

	public void setWulianDevice(WulianDevice device) {
		this.mDevice = device;
		refreshDeviceState(mDevice);
	}

	public View getView() {
		return lineLayout;
	}


	private void refreshDeviceState(WulianDevice device) {
		if(lineLayout.getParent() != null){
			((ViewGroup)lineLayout.getParent()).removeAllViews();
		}
		setDeviceIcon(device.getStateSmallIcon());
//		setDeviceIconContent(0);
		setDeviceName();
		setAreaName();
		if(!device.isDeviceOnLine()){
			controlLineLayout.setVisibility(View.INVISIBLE);
		}else{
			controlLineLayout.setVisibility(View.VISIBLE);
		}
	}

	private void setDeviceIcon(Drawable icon) {
		if(icon == null)
			return ;
		if (!mDevice.isDeviceOnLine()) {
//			Log.d("Device", "setDeviceIcon: "+mDevice.getDeviceType());
			icon = DisplayUtil.toGrayscaleDrawable(mContext, icon);
			if(icon!=null){
				icon.setAlpha(150);
			}
		}
		deviceIconImageView.setImageDrawable(icon);
	}

	private void setDeviceName() {
		if (!mDevice.isDeviceOnLine()) {
			stateImageView.setVisibility(View.GONE);
			stateTextView.setVisibility(View.VISIBLE);
			stateTextView.setText(mResources.getString(R.string.home_device_offline_red));
		}else{
			stateTextView.setVisibility(View.GONE);
			stateImageView.setVisibility(View.VISIBLE);
			Drawable signalDrawable = null;
			signalDrawable = DeviceTool.getSignalDrawer(mContext,mRssiMap.get(mDevice.getDeviceGwID() + mDevice.getDeviceID()));
			stateImageView.setImageDrawable(signalDrawable);
		}
		
		nameTextView.setText(DeviceTool.getDeviceShowName(mDevice));
	}

	private void setAreaName(){
		StringBuilder sb = new StringBuilder();
		DeviceAreaEntity entity = AreaGroupManager.getInstance().getDeviceAreaEntity(mDevice.getDeviceGwID(),mDevice.getDeviceRoomID());
		if (entity != null){
			sb.append(UNIT_MORE);
			sb.append(entity.getName());
			sb.append(UNIT_LESS);
			if(!entity.getName().equals("未分区")){
				areaTextView.setTextColor(Color.parseColor("#F1B129"));
			}
			areaTextView.setText(sb.toString());
		}
		else {
			sb.append(UNIT_MORE);
			sb.append(mContext.getResources().getString(
					R.string.device_config_edit_dev_area_type_other_default));
			sb.append(UNIT_LESS);
			areaTextView.setText(sb.toString());
		}
	}
}
