package cc.wulian.app.model.device.impls;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.AlarmableDeviceImpl.DefenseableDeviceShortCutControlItem;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl.ControlableDeviceShortCutControlItem;
import cc.wulian.app.model.device.impls.sensorable.SensorableDeviceImpl.SensorableDeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.ControlEPDataListener;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.interfaces.IProperties;
import cc.wulian.app.model.device.interfaces.IViewResource;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceRequestListener;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceStateChangeListener;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceException;
import cc.wulian.app.model.device.utils.DeviceResource;
import cc.wulian.app.model.device.utils.DeviceResource.ResourceInfo;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.DeleteDeviceHelpActivity;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.dao.FavorityDao;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.FavorityEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.device.DeviceDetailsFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.AreaList;
import cc.wulian.smarthomev5.tools.AreaList.OnAreaListItemClickListener;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public abstract class AbstractDevice implements WulianDevice {
	public static final String SETTING_LINK_TYPE = "setting_link_type";
	public static final String SETTING_LINK_TYPE_HEAD_CONFIG = "setting_link_type_head_config";
	public static final String SETTING_LINK_TYPE_HEAD_DETAIL = "setting_link_type_head_detail";

	protected static final int COLOR_CONTROL_GREEN = R.color.green;
	protected static final int COLOR_NORMAL_ORANGE = R.color.orange;
	protected static final int COLOR_ALARM_RED = R.color.red_orange;

	protected static final char TAB_SEP = '\t';
	private boolean isSubDevice = false;
	protected Context mContext;
	protected Resources mResources;
	protected DeviceInfo mDeviceInfo;
	protected Map<String, WulianDevice> childDeviceMap = null;
	private static DeviceCache deviceCache;
	protected String gwID;
	protected String devID;
	protected String type;
	protected String category;
	protected String name;
	protected String roomID;
	protected DeviceEPInfo mCurrentEpInfo;
	protected boolean mDevOnLine;
	protected ControlEPDataListener controlEPDataListener;
	private Drawable mDefaultSmalIcon;
	protected StringBuffer linkTaskControlEPData = new StringBuffer();
	private WulianDevice mParent;
	private Fragment mainFrameMent;
	protected H5PlusWebView pWebview;
	private ActionBarCompat actionBarCompat;
	//	private String callbackID;
	//维护同一个设备多个callbackID,key cmd;value callbackid
	protected Map<String,String> mapCallbackID;
	protected ProgressDialogManager progressDialogManager = ProgressDialogManager
			.getDialogManager();
	private final ArrayList<OnWulianDeviceStateChangeListener> mStaeChangeObservers = new ArrayList<OnWulianDeviceStateChangeListener>();
	private final Handler mHandler = new Handler(Looper.getMainLooper());
	protected final Runnable mRefreshStateRunnable = new Runnable() {
		@Override
		public void run() {
			if (mDeviceCreated && mViewCreated) {
				initViewStatus();
			}
		}
	};

	protected final Runnable mNotifyOnLineStateRunnable = new Runnable() {
		@Override
		public void run() {
			for (OnWulianDeviceStateChangeListener listener : mStaeChangeObservers) {
				listener.onDeviceOnLineStateChange(mDevOnLine);
			}
		}
	};

	protected OnWulianDeviceRequestListener mRequestListener;

	protected boolean mDeviceCreated;
	protected boolean mViewCreated;

	public AbstractDevice(Context context, String type) {
		if (deviceCache == null) {
			deviceCache = DeviceCache.getInstance(context);
		}
		mContext = context;
		mResources = context.getResources();
		this.type = type;
	}

	/**
	 * create device's properties proxy
	 */
	protected IProperties createPropertiesProxy() {
		return null;
	}

	/**
	 * create device's view resource proxy
	 */
	protected IViewResource createViewResourceProxy() {
		return null;
	}

	@Override
	public int compareTo(WulianDevice another) {
		return StringUtil.toInteger(getDeviceType())
				- StringUtil.toInteger(another.getDeviceType());
	}

	protected boolean isMultiepDevice() {
		return false;
	}

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		mDeviceInfo = devInfo;
		gwID = mDeviceInfo.getGwID();
		devID = mDeviceInfo.getDevID();
		type = mDeviceInfo.getType();
		category = mDeviceInfo.getCategory();
		if (!StringUtil.isNullOrEmpty(category) && category.length() >= 3) {
			this.setResourceByCategory();
		}
		name = mDeviceInfo.getName();
		if (StringUtil.isNullOrEmpty(name)) {
			name = "";
			mDeviceInfo.setName(name);
		}
		roomID = mDeviceInfo.getRoomID();
		if (StringUtil.isNullOrEmpty(roomID)) {
			roomID = Area.AREA_DEFAULT;
			mDeviceInfo.setRoomID(roomID);
		}
		mCurrentEpInfo = devInfo.getDevEPInfo();
		if (mCurrentEpInfo == null) {
			mCurrentEpInfo = devInfo.getDevEPInfoByEP(getDefaultEndPoint());
			devInfo.setDevEPInfo(mCurrentEpInfo);
		}

		if (mCurrentEpInfo == null) {
			Iterator<DeviceEPInfo> iterator = devInfo.getDeviceEPInfoMap()
					.values().iterator();
			if (iterator.hasNext())
				mCurrentEpInfo = iterator.next();
			devInfo.setDevEPInfo(mCurrentEpInfo);
		}
		if (this.isMultiepDevice() && !this.isSubDevice) {
			createDeviceMap(devInfo);
		}
		refreshDeviceUpData();
		removeCallbacks(mRefreshStateRunnable);
		post(mRefreshStateRunnable);

		if (!mDeviceCreated) {
			mDeviceCreated = true;
		}
	}

	private void createDeviceMap(DeviceInfo devInfo) {
		if (childDeviceMap == null) {
			childDeviceMap = new LinkedHashMap<String, WulianDevice>();
			for (Iterator<DeviceEPInfo> iterator = devInfo.getDeviceEPInfoMap()
					.values().iterator(); iterator.hasNext();) {
				DeviceEPInfo deviceEPInfo = iterator.next();
				addSubdevice(devInfo, deviceEPInfo);
			}
		}
	}

	public void addSubdevice(DeviceInfo devInfo, DeviceEPInfo deviceEPInfo) {

		DeviceInfo info = devInfo.clone();
		info.getDeviceEPInfoMap().clear();
		info.setDevEPInfo(deviceEPInfo);
		info.setName(deviceEPInfo.getEpName());
		WulianDevice device = deviceCache.createDeviceWithType(mContext,
				deviceEPInfo.getEpType());
		device.setDeviceParent(this);
		device.onDeviceUp(info);
		((AbstractDevice) device).childDeviceMap = null;
		((AbstractDevice) device).isSubDevice = true;
		childDeviceMap.put(deviceEPInfo.getEp(), device);
	}

	protected void refreshDeviceUpData() {
		refreshDevice();
	}


//	public synchronized void onDeviceData(String gwID, String devID,
//										  DeviceEPInfo devEPInfo) {
//		onDeviceData(gwID,devID,devEPInfo,"","");
//	}
	@Override
	public synchronized void onDeviceData(String gwID, String devID,
										  DeviceEPInfo devEPInfo,String cmd,String mode) {
		if (!mDeviceCreated)
			return;
		String ep = devEPInfo.getEp();
		DeviceEPInfo epInDevice = mDeviceInfo.getDevEPInfoByEP(ep);
		// add_by_yanzy_at_2016-6-21:当设备EP分多次上报时，应该能动态生成相应的EP，否则会导致超过14的EP无法控制。
		Log.d("WL_62", "onDeviceUp: Ep1="+devEPInfo.getEp()+" EpData1="+devEPInfo.getEpData());
		if (epInDevice == null && childDeviceMap != null) {
			mDeviceInfo.getDeviceEPInfoMap().put(ep, devEPInfo);
			this.addSubdevice(mDeviceInfo, devEPInfo);
		}else {
			if(!StringUtil.isNullOrEmpty(devEPInfo.getEpData())){
				if(epInDevice!=null){
					epInDevice.setEpData(devEPInfo.getEpData());
				}
			}
		}
		String oldEpType=mCurrentEpInfo.getEpType();
		String oldEpData=mCurrentEpInfo.getEpData();
		mCurrentEpInfo = devEPInfo;
		if(StringUtil.isNullOrEmpty(mCurrentEpInfo.getEpType())){
			mCurrentEpInfo.setEpType(oldEpType);
		}
		if(StringUtil.isNullOrEmpty(mCurrentEpInfo.getEpData())){
			mCurrentEpInfo.setEpData(oldEpData);
		}
		mDeviceInfo.setDevEPInfo(mCurrentEpInfo);
		fireDeviceRequestControlData();
		refreshDevice();
		removeCallbacks(mRefreshStateRunnable);
		post(mRefreshStateRunnable);
		Log.d("run21IsSendEpdata","devID::"+devID+",cmd:"+cmd);
		String data="";
		if(!StringUtil.isNullOrEmpty(devEPInfo.getEpData())){
			data=devEPInfo.getEpData();
		}
		if(isCallBackWithEp(cmd,mode)){
//			data = data + devEPInfo.getEp();
			data = data +"-"+ devEPInfo.getEp()+"-"+devEPInfo.getEpName();
		}
		String callbackID=getCallBackId(cmd,devEPInfo.getEp(),mode,devID);
		if(pWebview!=null){
			JsUtil.getInstance().execCallback(pWebview, callbackID, data, JsUtil.OK, false);
			Log.d("run21IsSendEpdata","callbackID::"+callbackID);
		}

	}

	/**
	 * 获取callbackid</br>
	 * 为了处理同一个webview监听多个callbackid的情况，规范了callbackid的格式为 cmd-[ep]-mode-devid
	 * 其中[ep]只针对个别设备起作用。
	 * @return
	 */
	public String getCallBackId(String cmd,String ep,String mode,String devID){
		if(mapCallbackID==null){
			mapCallbackID=new ArrayMap<>();
		}
		String callbackID=getDevWebViewCallBackId(cmd,ep,mode,devID);
		if(StringUtil.isNullOrEmpty(callbackID)){
			//非遵循新规范的设备，从mapCallbackID取callbackID
			if(mapCallbackID.containsKey(cmd)){
				callbackID=mapCallbackID.get(cmd);
			}else {//若是仍然取不到，那么就默认为13，该命令是12命令返回的
				callbackID=mapCallbackID.get("13");
			}
		}
		return callbackID;
	}
	public H5PlusWebView getpWebview() {
		return pWebview;
	}

	public void setpWebview(H5PlusWebView pWebview) {
		this.pWebview = pWebview;
	}

	/**
	 * 规范了callbackid的格式为 cmd-[ep]-mode-devid，[ep]
	 * @param cmd 命令
	 * @param ep 端口
	 * @param mode 模式
	 * @param devID 设备ID
	 * @return
	 */
	public String getDevWebViewCallBackId(String cmd,String ep,String mode,String devID){
		String callbackId="";
		switch (cmd) {
			case "13":
				callbackId="cmd13-"+devID;
				break;
			case "21":
				callbackId="cmd21-"+devID;
				break;
			default:
				break;
		}
		return callbackId;
	}
//	public String getCallbackID() {
//		return callbackID;
//	}

//	public void setCallbackID(String callbackID) {
//
//		this.callbackID = callbackID;
//	}

	private void createDeviceMapWithCurrentEpInfo() {
	}

	public void fireDeviceRequestControlData() {
		if (mRequestListener != null) {
			mRequestListener.onDeviceRequestControlData(this);
		}
	}

	@Override
	public void onDeviceSet(String mode,DeviceInfo devInfo, DeviceEPInfo devEPInfo) {
		onDeviceSet(mode,devInfo,devEPInfo,"13");
	}

	@Override
	public void onDeviceSet(String mode,DeviceInfo devInfo, DeviceEPInfo devEPInfo,String cmd) {
		if (!mDeviceCreated)
			return;
		String gwID = devInfo.getGwID();
		String devID = devInfo.getDevID();
		String category = devInfo.getCategory();
		String name = devInfo.getName();
		String isvalidate=devInfo.getIsvalidate();
		String callbackId=getDevWebViewCallBackId(cmd,devEPInfo.getEp(),mode,devID);
		String interval = "";
		if(StringUtil.isNullOrEmpty(callbackId)){
			if(mode.equals(CmdUtil.MODE_DEL_TIME)){
				callbackId="delDeviceTimed";
			}else if(mode.equals(CmdUtil.MODE_ADD_TIME)){
				callbackId="setDeviceTimed";
			}else if(mode.equals(CmdUtil.MODE_SEARCH_TIME)){
				callbackId="queryDeviceTimed";
			}
		}
		if(!StringUtil.isNullOrEmpty(devInfo.getInterval())){
			interval = devInfo.getInterval();
		}
		if(isCallBackWithEp(cmd,mode)){
			interval = interval + devEPInfo.getEp();
		}
		if(pWebview!=null){
			JsUtil.getInstance().execCallback(pWebview, callbackId,
					interval, JsUtil.OK, false);
		}
		//用于多ep At设备 返回epName -->html5
		if(isReturnEpNameToHtml()){
			String returnEpName = devEPInfo.getEpName();
			if(returnEpName != null){
				if(StringUtil.equals(returnEpName,"null")){
					returnEpName = "";
				}
				if(pWebview!=null){
					JsUtil.getInstance().execCallback(pWebview, "At_changeName",
							devEPInfo.getEp()+returnEpName, JsUtil.OK, false);
				}
			}

		}

		if (name != null) {
			this.name = name;
			mDeviceInfo.setName(this.name);
		}
		if (isvalidate!=null) {
			mDeviceInfo.setIsvalidate(isvalidate);
		}
		if (category != null && !StringUtil.equals(category, this.category)) {
			this.category = category;
			mDeviceInfo.setCategory(this.category);
			this.setResourceByCategory();
		}
		String newRoomID = devInfo.getRoomID();
		if (newRoomID != null && !StringUtil.equals(newRoomID, this.roomID)) {
			this.roomID = newRoomID;
			mDeviceInfo.setRoomID(this.roomID);
		}

		if (StringUtil.isNullOrEmpty(devEPInfo.getEpData())
				&& StringUtil.isNullOrEmpty(devEPInfo.getEpStatus())) {
			String test=devEPInfo.getEpData()
					+"--"+devEPInfo.getEpStatus();
			Log.d("Tag",test);
			return;
		} else {
			if(run21IsSendEpdata(cmd,devEPInfo.getEp(),mode,devID)){
				onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			}
		}
	}

	@Override
	public void onDeviceDestory(String gwID, String devID) {

		mDeviceCreated = false;
		mViewCreated = false;
		mDevOnLine = false;
	}

	@Override
	public final Context getContext() {
		return mContext;
	}

	@Override
	public final DeviceInfo getDeviceInfo() {
		return mDeviceInfo;
	}

	/**
	 * some device default ep is not 14, so it can set this
	 */
	@Override
	public String getDefaultEndPoint() {
		return EP_14;
	}

	@Override
	public String getDeviceGwID() {
		return gwID;
	}

	@Override
	public String getDeviceID() {
		return devID;
	}

	@Override
	public String getDeviceType() {
		return type;
	}

	@Override
	public String getDeviceName() {
		return name;
	}

	@Override
	public String getDeviceRoomID() {
		if (getDeviceInfo().isFlowerDevice()) {
			this.roomID = AccountManager.getAccountManger().getmCurrentInfo()
					.getGwRoomID();
		}
		this.roomID = AreaGroupManager.getInstance()
				.getDeviceAreaEntity(gwID, this.roomID).getRoomID();
		return roomID;
	}

	@Override
	public boolean isDeviceOnLine() {
		return mDevOnLine;
	}

	@Override
	public void setDeviceOnLineState(boolean isOnLine) {
		if (mDevOnLine != isOnLine) {
			mDevOnLine = isOnLine;
			post(mNotifyOnLineStateRunnable);
		}
	}

	@Override
	public boolean isDeviceUseable() {
		return true;
	}

	@Override
	public void setDeviceParent(WulianDevice parent) {
		if (mParent != null)
			throw new DeviceException(
					"this device child already had a parent :" + mParent);

		mParent = parent;
	}

	@Override
	public WulianDevice getDeviceParent() {
		return mParent;
	}

	@Override
	public void registerStateChangeListener(
			OnWulianDeviceStateChangeListener listener) {
		if (listener == null)
			throw new DeviceException("listener can not be null");

		synchronized (mStaeChangeObservers) {
			if (!mStaeChangeObservers.contains(listener)) {
				mStaeChangeObservers.add(listener);
			}
		}
	}

	@Override
	public void unregisterStateChangeListener(
			OnWulianDeviceStateChangeListener listener) {
		if (listener == null)
			throw new DeviceException("listener can not be null");

		synchronized (mStaeChangeObservers) {
			if (mStaeChangeObservers.contains(listener))
				mStaeChangeObservers.remove(listener);
		}
	}

	@Override
	public void registerControlRequestListener(
			OnWulianDeviceRequestListener listener) {
		if (listener == null)
			throw new DeviceException("listener can not be null");

		if (mRequestListener != listener) {
			mRequestListener = listener;
		}
	}

	@Override
	public void unregisterControlRequestListener(
			OnWulianDeviceRequestListener listener) {
		if (listener == null)
			throw new DeviceException("listener can not be null");

		mRequestListener = null;
	}

	/**
	 * some device has more than one ep<br/>
	 * so it can override this method to return all child<br/>
	 * normal device always return null;
	 */
	@Override
	public Map<String, WulianDevice> getChildDevices() {
		return childDeviceMap;
	}

	public WulianDevice getChildDevice(String ep) {
		if (childDeviceMap == null) {
			return null;
		} else {
			return childDeviceMap.get(ep);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{" + "\r\n");
		sb.append("\t gwID=" + getDeviceGwID() + "\r\n");
		sb.append("\t devID=" + getDeviceID() + "\r\n");
		sb.append("\t type=" + getDeviceType() + "\r\n");
		sb.append("\t name=" + getDeviceName() + "\r\n");
		sb.append("\t roomID=" + getDeviceRoomID() + "\r\n");
		sb.append("\t onLine=" + isDeviceOnLine() + "\r\n");
		sb.append("}");
		return sb.toString();
	}

	public String toShortString() {
		StringBuilder sb = new StringBuilder();
		sb.append("device class name:: " + this.getClass().getSimpleName());
		return sb.toString();
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Drawable getDefaultStateSmallIcon() {
		if (mDefaultSmalIcon == null) {
			ResourceInfo ri = DeviceResource.getResourceInfo(type);
			mDefaultSmalIcon = getDrawable(ri.smallIcon);
		}
		return mDefaultSmalIcon;
	}

	@Override
	public String getDefaultDeviceName() {
		String defaultName = "";
		ResourceInfo info = DeviceResource.getResourceInfo(getDeviceType());
		if (info != null) {
			defaultName = mContext.getString(info.name);
		}
		return defaultName;
	}

	@Override
	public Drawable getStateSmallIcon() {
		Drawable icon = getDefaultStateSmallIcon();
		return icon;
	}

	@Override
	public Drawable[] getStateBigPictureArray() {
		return null;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		return getDefaultDeviceName();
	}

	@Override
	public void onAttachView(Context context) {
		this.mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle saveState) {
		mViewCreated = true;
		return null;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		mViewCreated = true;
	}

	@Override
	public void initViewStatus() {
	}

	@Override
	public void onDetachView() {
		mViewCreated = false;
		removeCallbacks(mNotifyOnLineStateRunnable);
		this.mContext = MainApplication.getApplication();
	}

	protected void fireWulianDeviceRequestControlSelf() {
		if (mRequestListener != null) {
			mRequestListener.onDeviceRequestControlSelf(this);
		}
	}

	public final Resources getResources() {
		return mResources;
	}

	public final String getString(int res) {
		return mResources.getString(res);
	}

	public final Drawable getDrawable(int res) {
		try {
			return mResources.getDrawable(res);
		} catch (NotFoundException e) {
		}
		return null;
	}

	public final int getColor(int res) {
		try {
			return mResources.getColor(res);
		} catch (NotFoundException e) {
		}
		return 0;
	}

	public final String substring(String whitch, int[] range) {
		return substring(whitch, range[0], range[1]);
	}

	public final String substring(String whitch, int start, int end) {
		if (isNull(whitch))
			return whitch;

		String newString = null;
		try {
			newString = TextUtils.substring(whitch, start, end);
		} catch (Exception e) {
		}
		return newString;
	}

	public final String substring(String whitch, int start) {
		if (isNull(whitch))
			return whitch;

		String newString = null;
		try {
			newString = whitch.substring(start);
		} catch (Exception e) {
		}
		return newString;
	}

	public static String createCompoundCmd(String prefix, String cmd) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		if (cmd != null)
			sb.append(cmd);
		return sb.toString();
	}

	public static final boolean isSameAs(CharSequence c1, CharSequence c2) {
		return DeviceUtil.isSameAs(c1, c2);
	}

	public static final boolean isNull(CharSequence c) {
		return DeviceUtil.isNull(c);
	}

	public final boolean post(Runnable r) {
		return mHandler.post(r);
	}

	public final boolean postDelayed(Runnable r, long delayMillis) {
		return mHandler.postDelayed(r, delayMillis);
	}

	public final void removeCallbacks(Runnable r) {
		mHandler.removeCallbacks(r);
	}

	public final AnimationDrawable createAnimationFrame(int[] frames,
														boolean skipFirstFrame) {
		return createAnimationFrame(frames, skipFirstFrame, 500);
	}

	public final AnimationDrawable createAnimationFrame(int[] frames,
														boolean skipFirstFrame, int duration) {
		AnimationDrawable ad = new AnimationDrawable();
		ad.setOneShot(false);
		for (int i = 0; i < frames.length; i++) {
			if (i == 0 && skipFirstFrame)
				continue;

			ad.addFrame(getDrawable(frames[i]), duration);
		}
		return ad;
	}

	protected final DeviceEPInfo getCurrentEpInfo() {
		return mCurrentEpInfo;
	}

	/**
	 * 控制设备，如果是设备设备的请重写该方法，比如撤防和设防
	 */
	@Override
	public void controlDevice(String ep, String epType, String epData) {
		controlDeviceWidthEpData(ep, epType, epData);
	}

	/**
	 * 控制设备
	 *
	 * @param ep
	 * @param epType
	 * @param epData
	 */
	protected void controlDeviceWidthEpData(String ep, String epType,
											String epData) {
		String sendData = vertifyDeviceData(ep, epType, epData);
		if (sendData != null) {
			SendMessage.sendControlDevMsg(gwID, devID, ep, epType, sendData);
		}
	}

	/**
	 * 设置设备信息
	 *
	 * @param ep
	 * @param epType
	 * @param epData
	 */
	protected void setDeviceWidthEpData(String ep, String epType, String epData) {
		epData = setDefenseState(ep, epType, epData);
		if (epData != null) {
			SendMessage.sendSetDevMsg(gwID, CmdUtil.MODE_SWITCH, devID, null,
					null, null, null, ep, epType, null, epData);
		}
	}

	protected String setDefenseState(String ep, String epType, String epData) {
		return epData;
	}

	public static final int DEVICE_OPERATION_UNKNOW = -1;
	public static final int DEVICE_OPERATION_SETUP = 0;
	public static final int DEVICE_OPERATION_CTRL = 1;

	public void createControlOrSetDeviceSendData(int operation,
												 String sendData, boolean showDialog) {
		createControlOrSetDeviceSendData(operation, sendData, showDialog,
				DEVICE_OPERATION_UNKNOW);
	}

	/**
	 * only for big resource control, (if the deviceType is defense but also you
	 * can control it, like cancel alarm, then the dialog you show may have
	 * problem like not dismiss, so we need to specify operation intent, like
	 * the method before)
	 */
	public void createControlOrSetDeviceSendData(int operation,
												 String sendData, boolean showDialog, int setOrControl) {
		if (!isDeviceOnLine()) {
			// Toast.makeText(getContext(), R.string.device_offline,
			// Toast.LENGTH_SHORT).show();
			return;
		}

		DeviceEPInfo epInfo = getCurrentEpInfo();
		String ep = epInfo.getEp();
		String epType = epInfo.getEpType();
		fireWulianDeviceRequestControlSelf();

		switch (operation) {
			case DEVICE_OPERATION_SETUP:
				sendData = setDefenseState(ep, epType, sendData);
				if (sendData != null) {
					SendMessage.sendSetDevMsg(gwID, "0", devID, null, null, null,
							null, ep, epType, null, sendData);
				}
				break;
			case DEVICE_OPERATION_CTRL:
				sendData = vertifyDeviceData(ep, epType, sendData);
				if (sendData != null) {
					SendMessage
							.sendControlDevMsg(gwID, devID, ep, epType, sendData);
				}
				break;
			default:
				break;
		}
	}

	public String vertifyDeviceData(String ep, String epType, String sendData) {
		return sendData;
	}

	@Override
	public String getDeviceCategory() {
		return category;
	}

	public void setResourceByCategory() {
	}

	@Override
	public View onCreateSettingView(LayoutInflater inflater, ViewGroup container) {
		return null;
	}

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public boolean isAutoControl(boolean isSimple) {
		return true;
	}

	public boolean isLinkControl() {
		return false;
	}

	@Override
	public boolean isLinkControlCondition() {
		return isLinkControl();
	}

	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,
												 String ep, String epData) {
		return null;
	}

	protected Dialog createControlDataDialog(Context context, View contentView) {
		WLDialog.Builder builder = createDefaultDialogBuilder(context,
				contentView);
		builder.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
				fireControlEPData(linkTaskControlEPData.toString());
			}

			@Override
			public void onClickNegative(View contentViewLayout) {

			}
		});
		WLDialog dialog = builder.create();
		return dialog;
	}

	protected WLDialog.Builder createDefaultDialogBuilder(Context context,
														  View contentView) {
		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setTitle(DeviceTool.getDeviceShowName(this));
		builder.setContentView(contentView);
		builder.setPositiveButton(context.getString(R.string.device_ok));
		builder.setNegativeButton(context.getString(R.string.device_cancel));
		return builder;
	}

	@Override
	public void setControlEPDataListener(ControlEPDataListener listener) {
		controlEPDataListener = listener;
	}

	protected void fireControlEPData(String epData) {
		if (controlEPDataListener != null) {
			controlEPDataListener.onControlEPData(epData);
		}
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		return getDefaultShortCutControlView(item, inflater);
	}

	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		return getControlDeviceSelectDataShortCutView(item, inflater,
				autoActionInfo);
	}

	protected DeviceShortCutSelectDataItem getControlDeviceSelectDataShortCutView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		if (item == null) {
			item = new ShortCutDefaultDeviceSelectDataItem(
					inflater.getContext());
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}

	public static class ShortCutDefaultDeviceSelectDataItem extends
			DeviceShortCutSelectDataItem {

		protected LinearLayout defaultLineLayout;

		public ShortCutDefaultDeviceSelectDataItem(Context context) {
			super(context);
			defaultLineLayout = (LinearLayout) inflater.inflate(
					R.layout.device_short_cut_default_controlable, null);
			ImageView detailDataImageView = (ImageView) defaultLineLayout
					.findViewById(R.id.device_short_cut_click_detail_iv);
			controlLineLayout.addView(defaultLineLayout);
		}

		@Override
		public void setWulianDeviceAndSelectData(final WulianDevice device,
												 final AutoActionInfo autoActionInfo) {
			super.setWulianDeviceAndSelectData(device, autoActionInfo);
		}

	}

	protected DeviceShortCutControlItem getDefaultShortCutControlView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new DeviceShortCutControlItem(inflater.getContext());
		}
		item.setWulianDevice(this);
		return item;
	}

	protected DeviceShortCutControlItem getSensorableDeviceShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new SensorableDeviceShortCutControlItem(
					inflater.getContext());
		}
		item.setWulianDevice(this);
		return item;
	}

	protected DeviceShortCutControlItem getDefensableShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new DefenseableDeviceShortCutControlItem(
					inflater.getContext());
		}
		item.setWulianDevice(this);
		return item;
	}

	protected DeviceShortCutControlItem getContrableShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new ControlableDeviceShortCutControlItem(
					inflater.getContext());
		}
		item.setWulianDevice(this);
		return item;
	}

	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
													 TaskInfo taskInfo) {
		return null;
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, AutoConditionInfo autoConditionInfo,
			boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		return holder;
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, AutoActionInfo autoActionInfo) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		return holder;
	}

	@Override
	public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater) {
		EditDeviceInfoView editDeviceInfoView = new EditDeviceInfoView(
				inflater.getContext());
		editDeviceInfoView.setDevice(this);
		return editDeviceInfoView;
	}

	@Override
	public CharSequence parseDestoryProtocol(String epData) {
		String str = DeviceTool.getDeviceAlarmAreaName(this)
				+ DeviceTool.getDeviceShowName(this);
		if (LanguageUtil.isChina() || LanguageUtil.isTaiWan()) {
			str = str
					+ mContext
					.getString(R.string.home_device_alarm_default_voice_detect)
					+ mContext
					.getString(R.string.home_device_alarm_type_doorlock_destroy);
		} else {
			str = str
					+ " "
					+ mContext
					.getString(R.string.home_device_alarm_default_voice_detect)
					+ " "
					+ mContext
					.getString(R.string.home_device_alarm_type_doorlock_destroy);
		}
		return str;
	}

	@Override
	public MoreMenuPopupWindow getDeviceMenu() {
		MoreMenuPopupWindow popupWindow = new MoreMenuPopupWindow(mContext);
		popupWindow.setMenuItems(getDeviceMenuItems(popupWindow));
		return popupWindow;
	}

	/**
	 * 获取设备编辑菜单
	 *
	 * @Title: getDeviceDetailsMenuItems
	 * @Description:
	 * @param @return 设定文件
	 * @return List<MenuItem> 返回类型
	 * @throws
	 */
	protected List<MenuItem> getDeviceMenuItems(
			final MoreMenuPopupWindow manager) {
		List<MenuItem> mDeviceDetailsMenuItems = new ArrayList<MenuItem>();
		/**
		 * 常用设备
		 */
		MenuItem deviceDetialsFavoriteItem = new MenuItem(mContext) {
			@Override
			public void initSystemState() {
				FavorityEntity entity = FavorityDao.getInstance().getById(
						createDeviceFavorityEntity());
				if (Favority.OPERATION_USER.equals(entity.getOrder())) {
					iconImageView
							.setImageResource(cc.wulian.smarthomev5.R.drawable.wl_menu_favority_checked);
				} else {
					iconImageView
							.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_favority);
				}
				titleTextView
						.setText(mContext
								.getString(cc.wulian.smarthomev5.R.string.device_config_edit_dev_favority));
			}

			@Override
			public void doSomething() {
				try {
					FavorityDao.getInstance().operateFavorityDao(
							createDeviceFavorityEntity());
					manager.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private FavorityEntity createDeviceFavorityEntity() {
				FavorityEntity mFavorityEntity = new FavorityEntity();
				mFavorityEntity.setGwID(getDeviceGwID());
				mFavorityEntity.setOperationID(getDeviceID());
				mFavorityEntity.setOrder(Favority.OPERATION_USER);
				mFavorityEntity.setType(Favority.TYPE_DEVICE);
				if(getDeviceInfo()!=null&&getDeviceInfo().getDevEPInfo()!=null){
					mFavorityEntity.setEp(getDeviceInfo().getDevEPInfo().getEp());
					mFavorityEntity.setEpType(getDeviceInfo().getDevEPInfo()
							.getEpType());
					mFavorityEntity.setEpData(getDeviceInfo().getDevEPInfo()
							.getEpData());
				}
				mFavorityEntity.setTime(System.currentTimeMillis() + "");
				return mFavorityEntity;
			}
		};
		/**
		 * 设备分区
		 */
		MenuItem deviceDetialsAreaSettingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				String roomID = getDeviceRoomID();
				DeviceAreaEntity areaEntity = AreaGroupManager
						.getInstance()
						.getDeviceAreaEntity(getDeviceGwID(), getDeviceRoomID());
				if (areaEntity != null) {
					iconImageView.setImageResource(DeviceTool
							.PressgetAreaIconResourceByIconIndex(areaEntity
									.getIcon()));
					titleTextView.setText(areaEntity.getName());
				} else {
					iconImageView
							.setImageResource(cc.wulian.smarthomev5.R.drawable.area_icon_other_room);
					titleTextView
							.setText(mContext
									.getString(cc.wulian.smarthomev5.R.string.device_config_edit_dev_area_type_other_default));
				}
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if (!UserRightUtil.getInstance().canDo(
						UserRightUtil.EntryPoint.DEVICE_SET_ROOM)) {
					return;
				}

				manager.dismiss();
				if (getDeviceInfo().isFlowerDevice()) {

					showRemindDeviceIsDreamFlowerDevice();

					return;
				}
				final AreaList areaList = new AreaList(
						DeviceDetailsActivity.instance, true);
				areaList.setOnAreaListItemClickListener(new OnAreaListItemClickListener() {
					@Override
					public void onAreaListItemClicked(AreaList list, int pos,
													  RoomInfo info) {
						SendMessage.sendSetDevMsg(
								DeviceDetailsActivity.instance,
								getDeviceGwID(), CmdUtil.MODE_UPD,
								getDeviceID(), WulianDevice.EP_14,
								getDeviceType(), getDeviceName(),
								getDeviceCategory(), info.getRoomID(), "", "",
								null, true, false);
						Logger.debug("info.getRoomID()  ==" + info.getRoomID());

						areaList.dismiss();
					}
				});
				areaList.show(((Activity) mContext).getWindow().getDecorView());
			}

		};
		/**
		 * 重命名
		 */
		MenuItem deviceDetialsRenameItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView
						.setText(mContext
								.getString(cc.wulian.smarthomev5.R.string.device_config_edit_dev_rename));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_rename);
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if (!UserRightUtil.getInstance().canDo(
						UserRightUtil.EntryPoint.DEVICE_RENAME)) {
					return;
				}

				editDevice();
				manager.dismiss();
			}

			/**
			 * 设备重命名
			 *
			 * @Title: editDevice
			 * @return void 返回类型
			 * @throws
			 */
			private void editDevice() {
				final EditDeviceInfoView editDeviceInfoView = onCreateEditDeviceInfoView(inflater);
				WLDialog.Builder builder = new WLDialog.Builder(
						DeviceDetailsActivity.instance);
				builder.setTitle(DeviceTool
						.getDeviceShowName(AbstractDevice.this));
				builder.setContentView(editDeviceInfoView.getView());
				builder.setPositiveButton(android.R.string.ok);
				builder.setNegativeButton(android.R.string.cancel);
				builder.setCancelOnTouchOutSide(true).setListener(
						new MessageListener() {

							@Override
							public void onClickPositive(View contentViewLayout) {
								editDeviceInfoView.updateDeviceInfo();
							}

							@Override
							public void onClickNegative(View contentViewLayout) {

							}
						});
				builder.create().show();
			}

		};
		/**
		 * 查找设备
		 */
		MenuItem deviceDetialsFindItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView
						.setText(mContext
								.getString(cc.wulian.smarthomev5.R.string.device_config_edit_dev_search));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_search);
			}

			@Override
			public void doSomething() {
				manager.dismiss();
				if (getDeviceInfo().isFlowerDevice()) {
					showRemindDeviceIsDreamFlowerDevice();
					return;
				}
				searchDevice();
			}

			/**
			 * 搜索设备
			 */
			private void searchDevice() {
				if (!isDeviceOnLine()) {
					WLToast.showToast(
							mContext,
							mContext.getResources()
									.getString(
											cc.wulian.smarthomev5.R.string.device_config_edit_dev_offline_dev_unable_find),
							Toast.LENGTH_SHORT);
					return;
				}
				SendMessage.sendMakeDevBlinkMsg(mContext, AbstractDevice.this);
				WLDialog.Builder builder = new Builder(
						DeviceDetailsActivity.instance);
				builder.setMessage(
						mContext.getResources()
								.getString(
										cc.wulian.smarthomev5.R.string.device_config_edit_dev_click_seek_dev_text))
						.setDismissAfterDone(false)
						.setPositiveButton(
								mContext.getResources()
										.getString(
												cc.wulian.smarthomev5.R.string.device_config_edit_dev_click_seek_dev_flicker_again))
						.setNegativeButton(
								mContext.getResources()
										.getString(
												cc.wulian.smarthomev5.R.string.device_config_edit_dev_click_seek_dev_have_find))
						.setListener(new MessageListener() {
							@Override
							public void onClickPositive(View contentViewLayout) {
								SendMessage.sendMakeDevBlinkMsg(mContext,
										AbstractDevice.this);
							}

							@Override
							public void onClickNegative(View contentViewLayout) {

							}
						})

						.create().show();
			}

		};
		/**
		 * 刷新设备
		 */
		MenuItem deviceDetialsRefreshItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView
						.setText(mContext
								.getString(cc.wulian.smarthomev5.R.string.device_config_edit_dev_refresh));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_refresh);
			}

			@Override
			public void doSomething() {
				manager.dismiss();
				if (getDeviceInfo().isFlowerDevice()) {
					showRemindDeviceIsDreamFlowerDevice();
					return;
				}
				refresh();
			}

			/**
			 * 刷新
			 */
			private void refresh() {
				TaskExecutor.getInstance().execute(new Runnable() {
					@Override
					public void run() {
						SendMessage.sendQueryDevRssiMsg(getDeviceGwID(),
								getDeviceID(), true);
					}
				});
			}
		};

		/**
		 * 添加快捷方式
		 */
		MenuItem deviceDetialsAddShortCut = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView
						.setText(getResources()
								.getString(
										cc.wulian.smarthomev5.R.string.device_config_edit_dev_add_shortcut));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_shortcut);
			}

			@Override
			public void doSomething() {
				manager.dismiss();
				Intent intent = new Intent();
				intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

				// 快捷方式图标
				Bitmap icon;
				// 快捷方式名称
				String name;

				DeviceCache deviceCache = DeviceCache.getInstance(mContext);
				WulianDevice device = deviceCache.getDeviceByID(mContext, gwID,
						devID);

				if (StringUtil.isNullOrEmpty(device.getDeviceName())) {
					name = device.getDefaultDeviceName();
				} else {
					name = device.getDeviceName();
				}

				icon = drawable2Bitmap(device.getDefaultStateSmallIcon());

				// 快捷方式名称
				intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
				intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
				intent.putExtra("duplicate", false);

				// 点击快捷方式后的意图
				Intent shortcut_intent = new Intent();
				shortcut_intent.setAction("cc.wulian.smarthomev5.shortcut");
				shortcut_intent.addCategory("android.intent.category.DEFAULT");

				Bundle bundle = new Bundle();
				// 将设备ID与网管ID传入，区分打开的界面
				bundle.putString(DeviceDetailsFragment.EXTRA_DEV_GW_ID, gwID);
				bundle.putString(DeviceDetailsFragment.EXTRA_DEV_ID, devID);
				shortcut_intent.putExtras(bundle);
				intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent);

				mContext.sendBroadcast(intent);
			}

		};

		/**
		 * 删除设备
		 */
		MenuItem deviceDetialsDeleteItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView
						.setText(mContext
								.getString(cc.wulian.smarthomev5.R.string.device_config_edit_dev_area_create_item_delete));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_delate);
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if (!UserRightUtil.getInstance().canDo(
						UserRightUtil.EntryPoint.DEVICE_DELETE)) {
					return;
				}

				deleteDevice();
				manager.dismiss();
			}
		};

		if (isDeviceOnLine()) {
			mDeviceDetailsMenuItems.add(deviceDetialsFavoriteItem);
			mDeviceDetailsMenuItems.add(deviceDetialsAreaSettingItem);
			mDeviceDetailsMenuItems.add(deviceDetialsRenameItem);
			mDeviceDetailsMenuItems.add(deviceDetialsFindItem);
			mDeviceDetailsMenuItems.add(deviceDetialsRefreshItem);

			// comment by yanzy: 快捷方式还不成熟，暂时屏蔽
			// mDeviceDetailsMenuItems.add(deviceDetialsAddShortCut);
		}
		if (!getDeviceInfo().isFlowerDevice()) {// 判断是否是梦想之花的内部设备，如果是则不添加删除功能

			mDeviceDetailsMenuItems.add(deviceDetialsDeleteItem);
		}
		return mDeviceDetailsMenuItems;
	}

	/**
	 * Drawable转为Bitmap
	 *
	 * @param drawable
	 * @return
	 */
	private Bitmap drawable2Bitmap(Drawable drawable) {

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}

	}

	/**
	 * 删除该设备
	 */
	private void deleteDevice() {
		if (isDeviceOnLine()) {
			showDeleteDeviceDialog(cc.wulian.smarthomev5.R.string.device_config_edit_dev_delete_online_dev_text);
		} else {
			showDeleteDeviceDialog(cc.wulian.smarthomev5.R.string.device_config_edit_dev_delete_offline_dev_text);
		}
	}

	private void showDeleteDeviceDialog(
			int deviceConfigEditDevDeleteCurrentDevText) {
		WLDialog.Builder builder = new Builder(mContext);
		final View view = LayoutInflater.from(mContext).inflate(
				R.layout.delete_device_help_item_layout, null);
		TextView deleteItemHelp = (TextView) view
				.findViewById(R.id.device_delete_item_help_tv);
		TextView deleteItemContent = (TextView) view
				.findViewById(R.id.device_delete_item_content_tv);
		builder.setContentView(view)

				.setTitle(
						mContext.getResources()
								.getString(
										cc.wulian.smarthomev5.R.string.device_config_edit_dev_area_create_item_delete))
				.setPositiveButton(
						mContext.getResources().getString(
								cc.wulian.smarthomev5.R.string.common_ok))
				.setNegativeButton(
						mContext.getResources().getString(
								cc.wulian.smarthomev5.R.string.common_cancel))
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
						SendMessage.sendSetDevMsg(mContext, getDeviceGwID(),
								CmdUtil.MODE_DEL, getDeviceID(), "",
								getDeviceType(), getDeviceName(), "",
								getDeviceRoomID(), "", "", "", true, false);
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
//						Intent intent = new Intent(mContext,
//								DeleteDeviceHelpActivity.class);
//						mContext.startActivity(intent);
					}
				});
		final WLDialog wlDialog = builder.create();
		wlDialog.show();
		deleteItemContent.setText(deviceConfigEditDevDeleteCurrentDevText);
		deleteItemHelp.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		deleteItemHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext,
						DeleteDeviceHelpActivity.class);
				mContext.startActivity(intent);
				wlDialog.dismiss();
			}
		});
	}

	/**
	 * 提示此设备为梦想之花内部设备Dialog
	 */
	private void showRemindDeviceIsDreamFlowerDevice() {
		WLDialog.Builder builder = new WLDialog.Builder(
				DeviceDetailsActivity.instance);
		builder.setMessage(cc.wulian.smarthomev5.R.string.gateway_dream_flower_internal_device);
		builder.setPositiveButton(android.R.string.ok);
		builder.create().show();
	}

	@Override
	public void onDeviceDetailsFragmentDestroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnRefreshResultData(Intent data){

	}

	@Override
	public Fragment getCurrentFragment() {
		return mainFrameMent;
	}

	@Override
	public void setCurrentFragment(Fragment fragment) {
		mainFrameMent = fragment;
	}

	@Override
	public void registerEPDataToHTML(H5PlusWebView pWebview, String callBackId) {
		registerEPDataToHTML(pWebview, callBackId, ConstUtil.CMD_RETN_DATA);
	}

	@Override
	public void registerEPDataToHTML(H5PlusWebView pWebview, String callBackId,String cmd) {
		this.pWebview = pWebview;
		if(mapCallbackID==null){
			mapCallbackID=new ArrayMap<>();
		}
		mapCallbackID.put(cmd,callBackId);

		if(StringUtil.equals(cmd, ConstUtil.CMD_RETN_DATA)) {
			JsUtil.getInstance().execCallback(pWebview, callBackId,
					this.getDeviceInfo().getDevEPInfo().getEpData(), JsUtil.OK, false);
		}
	}


	@Override
	public ActionBarCompat getCurrentActionBar() {
		return actionBarCompat;
	}

	@Override
	public void setCurrentActionBar(ActionBarCompat actionBarCompat) {
		this.actionBarCompat=actionBarCompat;
	}

	public boolean run21IsSendEpdata(String cmd,String ep,String mode,String devID){
		return true;
	}

	//用于多EP设备重写此方法，return true,则向html返回数据epData后加上ep
	public boolean isCallBackWithEp(String cmd,String mode){
		return false;
	}

	//用于多EP设备重写此方法，return true,则向html返回数据epName
	public boolean isReturnEpNameToHtml(){
		return false;
	}

	@Override
	public boolean isHouseKeeperSelectControlDeviceActionBarUseable() {
		return false;
	}

	@Override
	public CharSequence parseDataWithExtData(String extData) {
		return "";
	}
}
