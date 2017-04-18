package cc.wulian.app.model.device.interfaces;

import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class DeviceShortCutSelectDataItem {
	protected static final String UNIT_MORE = "[";
	protected static final String UNIT_LESS = "]";
	
	protected static Map<String, Integer> mRssiMap;
	protected static final String CONSTANT_COLOR_START = "<font color=#f31961>";
	protected static final String CONSTANT_COLOR_END = "</font>";

	protected LinearLayout lineLayout;
	protected LinearLayout deviceIconLayoutw;
	protected TextView nameTextView;
//	protected ImageView stateImageView;
	protected TextView stateTextView;
	protected TextView areaTextView;
	protected LinearLayout controlLineLayout;
	
	protected LinearLayout delayLineLayout;
	protected TextView delaySencondsText;
	protected TextView delayDescripeText;
	protected TextView delayMinuteText;
	protected WulianDevice mDevice;
	protected Resources mResources;
	protected LayoutInflater inflater;
	protected Context mContext;
	protected DeviceCache mDeviceCache;
	protected AutoActionInfo autoActionInfo;
	protected ShortCutSelectDataListener shortCutSelectDataListener;
	protected ShortCutSelectDataDeleteListener shortCutSelectDataDeleteListener;
//	protected ShortCutSelectDataSortListener shortCutSelectDataSortListener;
	protected boolean isEditMode = false;

	protected LinearLayout contentBackgroundLayout;
	protected LinearLayout contentLayout;
	protected LinearLayout menuDeleteView;
	protected LinearLayout menuSortView;
	
	public DeviceShortCutSelectDataItem(Context context) {
		mContext = context;
		mRssiMap = MainApplication.getApplication().queryRssiInfoMap;
		mDeviceCache = DeviceCache.getInstance(context);
		inflater = LayoutInflater.from(context);
		mResources = context.getResources();
		lineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_select_data_item, null);
		
		contentBackgroundLayout = (LinearLayout) lineLayout.findViewById(R.id.device_short_cut_device_background_layout);
		contentLayout = (LinearLayout) lineLayout.findViewById(R.id.device_short_cut_content_layout);
		menuDeleteView = (LinearLayout)lineLayout.findViewById(R.id.device_short_cut_menu_delete_ll);
		menuSortView = (LinearLayout)lineLayout.findViewById(R.id.device_short_cut_menu_sort_ll);
		
		
		deviceIconLayoutw = (LinearLayout)lineLayout.findViewById(R.id.device_short_cut_icon_layout);
		nameTextView = (TextView)lineLayout.findViewById(R.id.device_short_cut_name_tv);
		stateTextView = (TextView)lineLayout.findViewById(R.id.device_short_cut_state_tv);
//		stateImageView = (ImageView) lineLayout.findViewById(R.id.device_short_cut_state_image);
		areaTextView = (TextView) lineLayout.findViewById(R.id.device_short_cut_areas_tv);
		controlLineLayout = (LinearLayout)lineLayout.findViewById(R.id.device_short_cut_control_ll);
		delayLineLayout = (LinearLayout)lineLayout.findViewById(R.id.device_short_cut_delay_ll);
		delayMinuteText = (TextView) lineLayout.findViewById(R.id.device_short_cut_delay_minute_text);
		delaySencondsText = (TextView) lineLayout.findViewById(R.id.device_short_cut_delay_sencond_text);
		delayDescripeText = (TextView) lineLayout.findViewById(R.id.device_short_cut_delay_descripe_text);
		menuDeleteView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fireShortCutSelectDataDeleteListener();
			}
		});
		
	}

	
	
	public LinearLayout getContentBackgroundLayout() {
		return contentBackgroundLayout;
	}



	public void setContentBackgroundLayout(LinearLayout contentBackgroundLayout) {
		this.contentBackgroundLayout = contentBackgroundLayout;
	}



	public LinearLayout getDelayLineLayout() {
		return delayLineLayout;
	}



	public void setDelayLineLayout(LinearLayout delayLineLayout) {
		this.delayLineLayout = delayLineLayout;
	}



	public TextView getDelaySencondsText() {
		return delaySencondsText;
	}



	public void setDelaySencondsText(TextView delaySencondsText) {
		this.delaySencondsText = delaySencondsText;
	}


	public TextView getDelayDescripeText() {
		return delayDescripeText;
	}



	public void setDelayDescripeText(TextView delaySencondsText) {
		this.delayDescripeText = delaySencondsText;
	}
	

	public TextView getDelayMinuteText() {
		return delayMinuteText;
	}



	public void setDelayMinuteText(TextView delayMinuteText) {
		this.delayMinuteText = delayMinuteText;
	}



	public LinearLayout getContentLayout() {
		return contentLayout;
	}



	public void setContentLayout(LinearLayout contentLayout) {
		this.contentLayout = contentLayout;
	}



	public LinearLayout getMenuDeleteView() {
		return menuDeleteView;
	}



	public void setMenuDeleteView(LinearLayout menuDeleteView) {
		this.menuDeleteView = menuDeleteView;
	}



	public LinearLayout getMenuSortView() {
		return menuSortView;
	}



	public void setMenuSortView(LinearLayout menuSortView) {
		this.menuSortView = menuSortView;
	}



	public void setWulianDeviceAndSelectData(WulianDevice device,AutoActionInfo autoActionInfo) {
		this.mDevice = device;
		this.autoActionInfo = autoActionInfo;
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
		setDeviceName();
		setAreaName();
//		if(!device.isDeviceOnLine()){
//			controlLineLayout.setVisibility(View.INVISIBLE);
//		}else{
//			controlLineLayout.setVisibility(View.VISIBLE);
//		}
		if(isEditMode){
			menuDeleteView.setVisibility(View.VISIBLE);
			menuSortView.setVisibility(View.VISIBLE);
		}else{
			menuDeleteView.setVisibility(View.GONE);
			menuSortView.setVisibility(View.GONE);
		}
	}

	private void setDeviceIcon(Drawable icon) {
		if(icon == null)
			return ;
		if (!mDevice.isDeviceOnLine()) {
			icon = DisplayUtil.toGrayscaleDrawable(mContext, icon);
			icon.setAlpha(150);
		}
		deviceIconLayoutw.setBackgroundDrawable(icon);
	}

	private void setDeviceName() {
		if (!mDevice.isDeviceOnLine()) {
//			stateImageView.setVisibility(View.GONE);
			stateTextView.setVisibility(View.VISIBLE);
			stateTextView.setText(mResources.getString(R.string.home_device_offline_red));
		}else{
			stateTextView.setVisibility(View.GONE);
//			stateImageView.setVisibility(View.VISIBLE);
//			Drawable signalDrawable = null;
//			signalDrawable = DeviceTool.getSignalDrawer(mContext,mRssiMap.get(mDevice.getDeviceGwID() + mDevice.getDeviceID()));
//			stateImageView.setImageDrawable(signalDrawable);
		}
		
		nameTextView.setText(DeviceTool.getDeviceShowName(mDevice));
	}

	public ShortCutSelectDataListener getShortCutSelectDataListener() {
		return shortCutSelectDataListener;
	}

	public void setShortCutSelectDataListener(
			ShortCutSelectDataListener shortCutSelectDataListener) {
		this.shortCutSelectDataListener = shortCutSelectDataListener;
	}
	public void fireShortCutSelectDataListener(){
		if(this.shortCutSelectDataListener != null){
			this.shortCutSelectDataListener.onSelectData(this.autoActionInfo);
		}
	}
	public void fireShortCutSelectDataDeleteListener(){
		if(this.shortCutSelectDataDeleteListener != null){
			this.shortCutSelectDataDeleteListener.onDelete();
		}
	}
//	public void fireShortCutSelectDataSortListener(){
//		if(this.shortCutSelectDataSortListener != null){
//			this.shortCutSelectDataSortListener.onSort();
//		}
//	}
	public ShortCutSelectDataDeleteListener getShortCutSelectDataDeleteListener() {
		return shortCutSelectDataDeleteListener;
	}

	public void setShortCutSelectDataDeleteListener(
			ShortCutSelectDataDeleteListener shortCutSelectDataDeleteListener) {
		this.shortCutSelectDataDeleteListener = shortCutSelectDataDeleteListener;
	}

//	public ShortCutSelectDataSortListener getShortCutSelectDataSortListener() {
//		return shortCutSelectDataSortListener;
//	}
//
//	public void setShortCutSelectDataSortListener(
//			ShortCutSelectDataSortListener shortCutSelectDataSortListener) {
//		this.shortCutSelectDataSortListener = shortCutSelectDataSortListener;
//	}

	private void setAreaName(){
		StringBuilder sb = new StringBuilder();
		DeviceAreaEntity entity = AreaGroupManager.getInstance().getDeviceAreaEntity(mDevice.getDeviceGwID(),mDevice.getDeviceRoomID());
		if (entity != null){
			sb.append(UNIT_MORE);
			sb.append(entity.getName());
			sb.append(UNIT_LESS);
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
	
	public interface ShortCutSelectDataListener{
		public void onSelectData(AutoActionInfo autoActionInfo);
	}
	public interface ShortCutSelectDataDeleteListener{
		public void onDelete();
	}
//	public interface ShortCutSelectDataSortListener{
//		public void onSort();
//	}
}
