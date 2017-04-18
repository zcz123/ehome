package cc.wulian.app.model.device.impls.controlable.dock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
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
import cc.wulian.app.model.device.impls.controlable.AbstractSwitchDevice;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.utils.DisplayUtil;

/**
 *	0:关,1:开,255:异常
 */
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_DOCK_2}, 
		category = Category.C_CONTROL)
public class WL_51_Dock_2 extends AbstractSwitchDevice
{
	private static final String DATA_CTRL_STATE_OPEN_1 		= "1";
	private static final String DATA_CTRL_STATE_CLOSE_0 	= "0";
	
	private static final int 		SMALL_OPEN_D 							= R.drawable.device_button_2_open;
	private static final int 		SMALL_CLOSE_D 						= R.drawable.device_button_2_close;

	private static final int 		BIG_OPEN_D 								= R.drawable.device_button_2_open_big;
	private static final int 		BIG_CLOSE_D 							= R.drawable.device_button_2_close_big;

	private static final int 		SMALL_STATE_BACKGROUND 		= R.drawable.device_button_state_background;
	private static final int 		BIG_STATE_BACKGROUND 			= R.drawable.device_button_state_background_big;
	private DeviceCache deviceCache;
	private Map<String,WulianDevice> deviceMap = new LinkedHashMap<String, WulianDevice>();
	protected LinearLayout containerLineLayout ;
	public WL_51_Dock_2( Context context, String type )
	{
		super(context, type);
	}
	
	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
		Iterator<DeviceEPInfo> iterator = devInfo.getDeviceEPInfoMap().values().iterator();
		while (iterator.hasNext()) {
			DeviceEPInfo deviceEPInfo = iterator.next();
			DeviceInfo info = devInfo.clone();
			info.getDeviceEPInfoMap().clear();
			info.setDevEPInfo(deviceEPInfo);
			WulianDevice device = deviceCache.createDeviceWithType(mContext, deviceEPInfo.getEpType());
			device.setDeviceParent(this);
			device.onDeviceUp(info);
			deviceMap.put(deviceEPInfo.getEp(), device);
		}
	}

	@Override
	public synchronized void onDeviceData(String gwID, String devID,
			DeviceEPInfo devEPInfo,String cmd,String mode) {
		String ep = devEPInfo.getEp();
		WulianDevice device = getChildDevice(ep);
		if(device != null){
			device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			removeCallbacks(mRefreshStateRunnable);
			post(mRefreshStateRunnable);
			fireDeviceRequestControlData();
		}else{
			super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
		}
	}
	@Override
	public boolean isOpened() {
		if(getChildDevices() != null && !getChildDevices().isEmpty()){
			for(WulianDevice device : getChildDevices().values()){
				if(device instanceof Controlable){
					Controlable controlable = (Controlable)device;
					if(controlable.isOpened()){
						return true;
					}
				}
			}
			return false;
		}else{
			return getOpenProtocol().equals(epData);
		}
	}

	@Override
	public boolean isClosed() {
		return !isOpened();
	}
	@Override
	public String getOpenProtocol(){
		return DATA_CTRL_STATE_OPEN_1;
	}

	@Override
	public String getCloseProtocol(){
		return DATA_CTRL_STATE_CLOSE_0;
	}
	@Override
	public int getOpenSmallIcon(){
		return SMALL_OPEN_D;
	}
	
	@Override
	public int getCloseSmallIcon(){
		return SMALL_CLOSE_D;
	}
	@Override
	public int getOpenBigPic(){
		return BIG_OPEN_D;
	}

	@Override
	public int getCloseBigPic(){
		return BIG_CLOSE_D;
	}

	@Override
	public Drawable getStateSmallIcon() {
		List<Drawable> drawers = new ArrayList<Drawable>();
		for(String ep : deviceMap.keySet()){
			final WulianDevice device = deviceMap.get(ep);
			if(device instanceof Controlable){
				Controlable controlable = (Controlable)device;
				if(controlable.isOpened()){
					drawers.add(mResources.getDrawable(getOpenSmallIcon()));
				}else{
					drawers.add(mResources.getDrawable(getCloseSmallIcon()));
				}
			}
		}
		return DisplayUtil.getDrawablesMerge(drawers.toArray(new Drawable[]{}), mResources.getDrawable(SMALL_STATE_BACKGROUND));
	}
	private ViewGroup onCreateBigViewGroup(){
		LinearLayout mergeGroup = new LinearLayout(getContext());
		mergeGroup.setOrientation(LinearLayout.HORIZONTAL);
		mergeGroup.setBackgroundResource(BIG_STATE_BACKGROUND);
		for(String ep : deviceMap.keySet()){
			final WulianDevice device = deviceMap.get(ep);
			if(device instanceof Controlable){
				Controlable controlable = (Controlable)device;
				ImageView imageView = new ImageView(mContext);
				if(controlable.isOpened()){
					imageView.setImageDrawable(mResources.getDrawable(getOpenBigPic()));
				}else{
					imageView.setImageDrawable(mResources.getDrawable(getCloseBigPic()));
				}
				imageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DeviceEPInfo info = device.getDeviceInfo().getDevEPInfo();
						fireWulianDeviceRequestControlSelf();
						controlDevice(info.getEp(), info.getEpType(), null);
					}
				});
				mergeGroup.addView(imageView);
			}
		}
		return mergeGroup;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		containerLineLayout = (LinearLayout)inflater.inflate(R.layout.device_light_continer, null);
		return containerLineLayout;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		mViewCreated = true;
	}

	@Override
	public void initViewStatus() {
		containerLineLayout.removeAllViews();
		containerLineLayout.addView(onCreateBigViewGroup());
	}

	@Override
	public Map<String, WulianDevice> getChildDevices() {
		return deviceMap;
	}

}
