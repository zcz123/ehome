package cc.wulian.smarthomev5.fragment.more.wifi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.wulian.icam.utils.WifiAdmin;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.dao.WifiDao;
import cc.wulian.smarthomev5.entity.WifiEntity;
import cc.wulian.smarthomev5.fragment.scene.SceneTaskManager;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class WifiDataManager {
	private static WifiDataManager instance;
	public static List<WifiEntity> wifiEntities = new ArrayList<WifiEntity>();
	public static List<String> wifiList = new ArrayList<String>();
	public static AccountManager accountManager = AccountManager.getAccountManger();
	private WifiDao wifiDao = WifiDao.getInstance();
	private WifiManager wifiManager;
	private MainApplication application = MainApplication.getApplication();
	private boolean hasWifiSSID = false;

	public boolean hasWifiSSID() {
		return hasWifiSSID;
	}

	private WifiDataManager() {
		wifiManager = (WifiManager) application
				.getSystemService(Context.WIFI_SERVICE);
		initWifiEntities();
	}

	public static WifiDataManager getInstance() {
		if (instance == null) {
			instance = new WifiDataManager();
		}
		return instance;
	}

	private void initWifiEntities() {
		wifiEntities.clear();
		WifiEntity defaultEntity = new WifiEntity();
		defaultEntity.setGwID(accountManager.getmCurrentInfo()
				.getGwID());
		defaultEntity.setOperateID(CmdUtil.ID_UNKNOW);
		defaultEntity.setOperateType(WifiEntity.TYPE_SCENE);
//		defaultEntity.setSSID(mContext.getResources().getString(
//				R.string.more_wifi_choose_wifi_toast));
		wifiEntities.add(0, defaultEntity);
		WifiEntity wifiEntity = new WifiEntity();
		wifiEntity.setGwID(accountManager.getmCurrentInfo()
				.getGwID());
		List<WifiEntity> entities = wifiDao.findListAll(wifiEntity);
		for (WifiEntity entity : entities) {
			// 确定当前wifi场景为一个对象,即只有一个wifi场景
			if (WifiEntity.TYPE_SCENE.equals(entity.getOperateType())) {
				wifiEntities.remove(0);
				wifiEntities.add(0, entity);
				wifiList.add(0, entity.getSSID());
				hasWifiSSID = true;
			} else {
				wifiEntities.add(entity);
			}

		}
	}

	// 获取扫描当前可连接wifi列表
	public List<String> getWifiScanResultList() {
		List<String> unitWifiList = new ArrayList<String>();
		for (ScanResult result : wifiManager.getScanResults()) {
			if (!StringUtil.isNullOrEmpty(result.SSID)) {
				wifiList.add(result.SSID);
			}
		}
		for (String str : wifiList) {
			// 去除wifi的ssid重复item
			if (Collections.frequency(unitWifiList, str) < 1)
				unitWifiList.add(str);
		}

		return unitWifiList;
	}

	// 执行wifi场景
	public void excuteWifiScene() {
		SceneTaskManager sceneTaskManager = SceneTaskManager.getInstance();
		String curSceneID = sceneTaskManager.getCurUsingSceneID();
		WifiInfo curConnectWifiInfo = wifiManager.getConnectionInfo();
		WifiEntity wifiEntity = new WifiEntity();
		wifiEntity.setGwID(accountManager.getmCurrentInfo()
				.getGwID());
		boolean opWifi = Preference.getPreferences().getBoolean(
				IPreferenceKey.P_KEY_OPEN_WIFI, false);
		// 未开启Wifi场景
		if (!opWifi) {
			return;
		}
		// 当前未连接Wifi
		if (null == curConnectWifiInfo) {
			return;
		}
		// wifi场景未设置
		if (null == wifiDao.findListAll(wifiEntity)) {
			return;
		}
		String curConnectWifiSSID = curConnectWifiInfo.getSSID();
		// 当前连接wifi的SSID为空
		if (StringUtil.isNullOrEmpty(curConnectWifiSSID)) {
			return;
		}
		if (curConnectWifiSSID.startsWith("\"")
				&& curConnectWifiSSID.endsWith("\"")) {
			curConnectWifiSSID = curConnectWifiSSID.substring(1,
					curConnectWifiSSID.length() - 1);
		}
		// 当前连接wifi与保存Wifi的SSID不等
		if (!curConnectWifiSSID.equals(wifiEntities.get(0).SSID)) {
			return;
		}
		// 当前执行的场景,与设置限定条件不等
		if (wifiEntities.get(0).getConditionContent() != null
				&& StringUtil.isNullOrEmpty(curSceneID)) {
			return;
		}
		// 当前保存了条件场景,并且场景有效.并且当前执行场景也不为空
		if ((wifiEntities.get(0).getConditionContent() != null && StringUtil
				.toInteger(wifiEntities.get(0).getConditionContent()) > 0)
				&& !StringUtil.isNullOrEmpty(curSceneID)) {
			if (!wifiEntities.get(0).getConditionContent().equals(curSceneID)) {
				return;
			}
		}
		// 当前设定场景已执行
		if (!StringUtil.isNullOrEmpty(curSceneID)
				&& curSceneID.equals(wifiEntities.get(0).operateID)) {
			return;
		}

		for (WifiEntity entity : wifiEntities) {
			if (WifiEntity.TYPE_SCENE.equals(entity.getOperateType())) {// 执行场景
				SceneInfo sceneInfo = application.sceneInfoMap.get(entity.gwID
						+ entity.operateID);
				if (sceneInfo != null) {
					SceneInfo newInfo = sceneInfo.clone();
					newInfo.setStatus(CmdUtil.SCENE_USING);
					SceneManager.switchSceneInfo(application, newInfo, false);
				}
			} else {// 执行设备
				WulianDevice device = DeviceCache.getInstance(application)
						.getDeviceByID(application, entity.gwID, entity.operateID);
				if (device != null && device.isDeviceOnLine()) {
					if (StringUtil.isNullOrEmpty(entity.ep)) {
						entity.ep = WulianDevice.EP_0;
					}
					device.controlDevice(entity.ep, entity.epType,
							entity.epData);
				}
			}
			// createSuccessDialog(context);

		}
	}
	// private void createSuccessDialog(Context context) {
	// WLDialog.Builder builder = new WLDialog.Builder(context);
	// builder.setPositiveButton(R.string.monitor_cloud_1_ok);
	// builder.setMessage(R.string.more_shake_execute_success);
	// builder.setNegativeButton(null);
	// WLDialog sucessDialog = builder.create();
	// sucessDialog.show();
	// }

	//add syf
	/**连接指定wifi*/
	public boolean connectWifi(String ssid,Context context){
		try{
			 WifiAdmin wifiAdmin  = 
					 new WifiAdmin(context);
			 wifiAdmin.openWifi();
		     WifiConfiguration apConfig = wifiAdmin.
		    		 createWifiConfiguration(ssid,null,1);
		     return wifiAdmin.addNetworkAndLink(apConfig);	  
		}catch(Exception ex){
			ex.printStackTrace();
			Log.e("WifiManager", ex.getMessage());
			return false;
		}
	}
	public String getSSID(Context context){
		return new WifiAdmin(context).getCurrentWifiInfo().getSSID();
	}
	public int wifiState(Context context){
		  return new WifiAdmin(context).wifiState();
	}
}
