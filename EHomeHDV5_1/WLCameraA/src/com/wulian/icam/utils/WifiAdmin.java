package com.wulian.icam.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
/**
 * WIFI管理器
 */
public class WifiAdmin {
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	ConnectivityManager connManager;
	// 已经配置的网络连接列表(初始化时)
	private List<WifiConfiguration> mWifiConfigurationList;
	// 定义一个WifiLock
	WifiLock mWifiLock;
	private Context mContext;

	public static final int TYPE_NONE = 1;
	public static final int TYPE_WEP = 2;
	public static final int TYPE_WPA = 3;
	private List<String> unitWifiList = new ArrayList<String>();
	private List<String> wifiList = new ArrayList<>();
	private final static String TAG = "WifiAdmin";
	// 构造器
	public WifiAdmin(Context context) {
		mContext = context;
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 取得当前WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
		// 获得已经配置好的网络连接
		mWifiConfigurationList = mWifiManager.getConfiguredNetworks();
	}
	//获取当期那Wi-Fi的IP
	public final int getWifiIpInt() {
		WifiInfo info=getCurrentWifiInfo();
		if(info!=null) {
			return info.getIpAddress();
		}else {
			return 0;
		}
	}
	// WiFi 是否打开
	public boolean WifiIsOpen() {
		return mWifiManager.isWifiEnabled();
	}
	// 打开WIFI
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}
	public boolean isWiFiEnabled() {
		return mWifiManager.isWifiEnabled();
	}
	// 关闭WIFI
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}
	// 检查当前WIFI状态
	public int wifiState() {
		return mWifiManager.getWifiState();
	}
	// 锁定WifiLock
	public void acquireWifiLock() {
		if (mWifiLock == null) {
			creatWifiLock();
		}
		mWifiLock.acquire();
	}
	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock == null) {
			return;
		}
		if (mWifiLock.isHeld()) {
			mWifiLock.release();
		}
	}
	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}
	// 得到配置好的网络
	public List<WifiConfiguration> getConfigurationList() {
		return mWifiConfigurationList;
	}
	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index > mWifiConfigurationList.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfigurationList.get(index).networkId,
				true);
	}
	public void startScan() {
		openWifi();
		mWifiManager.startScan();// 请求扫描wifi，事件通知 SCAN_RESULTS_AVAILABLE_ACTION
	}
	// 得到网络列表(在广播之后调用)
	public List<ScanResult> getScanResultList() {
		return mWifiManager.getScanResults();
	}
	// 查看扫描结果
	public String viewScanResult() {
		List<ScanResult> mScanResultList = mWifiManager.getScanResults();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mScanResultList.size(); i++) {
			stringBuilder.append("Index_" + Integer.valueOf(i + 1) + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			stringBuilder.append((mScanResultList.get(i)).toString());
			stringBuilder.append("/n");
		}
		return stringBuilder.toString();
	}
	// 得到当前连接的WiFiInfo
	public WifiInfo getCurrentWifiInfo() {
		return mWifiManager.getConnectionInfo();
	}
	/**
	 * @Function 向系统发送一个请求,添加一个网络并连接 需要时间
	 * @author Wangjj
	 * @date 2014年11月29日
	 * @param wcg
	 */
	public boolean addNetworkAndLink(WifiConfiguration wcg) {
		if (wcg == null) {
			Log.d(TAG, "===wcg is null===");
			return false;
		}
		// 检查是否配置过
		WifiConfiguration wcg_exist = getConfiguredNetwork(wcg.SSID);//
		// ssid有双引号即"WL_Camera_4566"
		int wcgId;
		if (wcg_exist != null) {// bug:存在的配置也可能是手动错误的配置。对于wifi：手动输入正确的密码
			// 对于设备wifi：手动输入正确的密码 或 删除ap，执行else重新添加
			wcgId = wcg_exist.networkId;
			Utils.sysoInfo("切换的wifi" + wcg.SSID + "已经在配置列表里，直接使用,wcgId="
					+ wcgId);
		} else {
			wcgId = mWifiManager.addNetwork(wcg);// 无论调用多少次都没问题
			Utils.sysoInfo("切换的wifi不在配置列表里，add新的,wcgId=" + wcgId);
			Log.d(TAG, "===check wifi not in the list===");
		}

		// int wcgId = mWifiManager.addNetwork(wcg);// 内部 addOrUpdateNetwork.
		// 设备ap没问题，还原网络有问题！
		// 如果需要连接的网络即为当前已经连接的网络，则return
		WifiInfo currentInfo = mWifiManager.getConnectionInfo();// 即使获取一下，也会触发广播？也必须是这样！
		//Utils.sysoInfo("当前网络 "+currentInfo);
		if (currentInfo != null && wcgId != -1
				&& wcgId == currentInfo.getNetworkId()) {
			Utils.sysoInfo("如果需要切换的网络即为当前已经连接的网络" + currentInfo.getSSID()
					+ "，则return");
			return false;
		}
		// if (wcg_exist != null) {
		// mWifiManager.removeNetwork(wcg_exist.networkId);//还原原网络有问题
		// }
		// int wcgId = mWifiManager.addNetwork(wcg);

		if (wcgId != -1) {// 成功
			boolean b = mWifiManager.enableNetwork(wcgId, true);//如果没开wifi，会自动打开。 深bug:低版本会阻塞等待，高版本会立即还原网络。如果没有插电，高版本则直接还原为原网络，接受到还原网络广播，直接绑定->不插电也能绑定！
			if (b) {// 操作成功，但是不一定能立即连接上， 需要一个广播来接受状态信息
				// CustomToast.show(mContext, "正在校验...");//stack dump
				Utils.sysoInfo("正在切换wifi...");
				Log.d(TAG, "===正在切换wifi...===");
				return true;
			} else {
				// CustomToast.show(mContext, "启用指定网络失败");
				Utils.sysoInfo("启用指定网络失败");
				Log.d(TAG, "===切换失败===");
				return false;
			}
		} else {
			Utils.sysoInfo("wcgId=-1,切换网络" + wcg.SSID + "失败,可能是wifi没有打开");
			mWifiManager.setWifiEnabled(true);
			// CustomToast.show(mContext, "切换网络" + wcg.SSID + "失败");
			return false;
		}

	}
	// 断开指定ID的网络
	public void disconnectWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}
	// 然后是一个实际应用方法，只验证过没有密码的情况：
	// 分为三种情况：1没有密码2用wep加密3用wpa加密
	public WifiConfiguration createWifiConfiguration(String SSID,
													 String Password, int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		// 不要删除已经存在的配置，设备的配置哥会手动删除
		// WifiConfiguration tempConfig = getConfiguredNetwork(SSID);
		// if (tempConfig != null) {
		// mWifiManager.removeNetwork(tempConfig.networkId);// 删除旧的配置
		// }

		if (Type == TYPE_NONE) // WIFICIPHER_NOPASS
		{
//			config.wepKeys[0] = "";
			config.SSID="\"" + SSID + "\"";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//			config.wepTxKeyIndex = 0;
			config.status = WifiConfiguration.Status.ENABLED;
		}
		if (Type == TYPE_WEP) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.wepTxKeyIndex = 0;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		}
		if (Type == TYPE_WPA) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;// 不广播ssid？
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}
	/**
	 * 如果已经存在，则直接返回该配置
	 * @param SSID
	 * @return
	 */
	public WifiConfiguration getConfiguredNetwork(String SSID) {
		// 某些机型上（魅族）在3G 4G网络环境下 mWifiManager.getConfiguredNetworks() 为null
		// Upon failure to fetch or when when Wi-Fi is turned off, it can be
		// null
		// 获取失败或者wifi关闭了
		if (mWifiManager.getConfiguredNetworks() != null) {
			for (WifiConfiguration config : mWifiManager
					.getConfiguredNetworks()) {
				if (config.SSID.equals(SSID)
						|| config.SSID.equals("\"" + SSID + "\"")) {
					return config;
				}
			}
		} else {
			mWifiManager.setWifiEnabled(true);
		}
		return null;
	}

	/**
	 * @Function 删除配置的、自动保存的 设备热点
	 * @author Wangjj
	 * @date 2015年1月6日
	 * @param cameraSsid
	 */
	public void removeConfiguredNetwork(String cameraSsid) {
		if (TextUtils.isEmpty(cameraSsid)) {
			return;
		}
		if (mWifiManager.getConfiguredNetworks() != null) {
			for (WifiConfiguration config : mWifiManager
					.getConfiguredNetworks()) {
				// if (config.SSID.equals("\"" + cameraSsid + "\"")
				// || config.SSID.equals(cameraSsid)) {
				// mWifiManager.removeNetwork(config.networkId);
				// Utils.sysoInfo(cameraSsid + ">>删除配置热点的密码" + config.SSID);
				// return;
				// }
				// 清空所有摄像头热点
				if (config.SSID.replace("\"", "").matches(
						"Wulian_Camera_\\w{4}")) {
					mWifiManager.removeNetwork(config.networkId);
					Utils.sysoInfo(">>删除配置热点的密码" + config.SSID);
				}
			}
		}
	}

	/**
	 *
	 * @Function 从扫描结果中判断设备是否上线
	 * @author Wangjj
	 * @date 2014年12月1日
	 */
	public boolean isDeviceOnScanResult(String deviceSsid,
										List<ScanResult> scanResults) {
		if (deviceSsid == null || scanResults == null) {// 不开wifi 或则 不及时
			// scanResults则为
			// null
			return false;
		}
		if (TextUtils.isEmpty(deviceSsid)) {
			Utils.sysoInfo("deviceSsid=null，不在线！");
			return false;
		}

		for (ScanResult sr : scanResults) {
			if (deviceSsid.equals(sr.SSID)) {
				Utils.sysoInfo("设备在扫描结果中，已经上线！");
				return true;
			}
		}
		Utils.sysoInfo("设备不在扫描结果中，不在线！");
		return false;
	}

	public String getLocalIpAddress() {
		String result = "";
		if (mWifiManager.isWifiEnabled()) {
			WifiInfo mInfo = mWifiManager.getConnectionInfo();
			int ipAddress = mInfo.getIpAddress();
			if (ipAddress > 0) {
				result = Formatter.formatIpAddress(ipAddress);
			}
		}
		return result;
	}

	// WPA-PSK psk
	// WPA2-PSK psk2
	// WPA-PSK/WPA2-PSK psk
	// WEP wep
	// NONE none
	// OTHER psk
	public String getEncryption(String ssid) {

		for (ScanResult sr : mWifiManager.getScanResults()) {
			if (!TextUtils.isEmpty(sr.SSID)
					&& sr.SSID.replace("\"", "").equals(ssid)) {

				String cap = sr.capabilities;
				if (cap.contains("WPA-PSK") && !cap.contains("WPA2-PSK")) {
					return "psk";
				}
				if (!cap.contains("WPA-PSK") && cap.contains("WPA2-PSK")) {
					return "psk2";
				}
				if (cap.contains("WPA-PSK") && cap.contains("WPA2-PSK")) {
					return "psk";
				}
				if (cap.contains("WEP")) {
					return "wep";
				}
				if (!cap.contains("WPA-PSK") && !cap.contains("WPA2-PSK")
						&& !cap.contains("WEP")) {
					return "none";
				}
			}
		}
		return "psk";
	}
	// add syf
	/**
	 *  获取wifi列表
	 * @return
	 */
	public List<String> getWifiScanResultList(){
		startScan();
		for (ScanResult result : getScanResultList()) {
			if (result.SSID!=null&&!result.SSID.trim().equals("  ")) {
				wifiList.add(result.SSID);
			}
		}
		for (String str : wifiList) {
			if (Collections.frequency(unitWifiList, str) < 1)
				unitWifiList.add(str);
		}
		return unitWifiList;
	}
	public boolean connectWifi(String ssid,String pwd,int type){
		if(ssid!=null&&!ssid.trim().equals(" ")&&!pwd.isEmpty()){
			this.openWifi();
			WifiConfiguration apConfig = 	createWifiConfiguration(ssid,pwd,type);
			if(apConfig != null){
				return this.addNetworkAndLink(apConfig);
			}
			return false;
		}
		return false;
	}
	public String  getSSID(){
		return mWifiManager.getConnectionInfo().getSSID();
	}
	/**
	 * add mabo 2016/7/26
	 * 在无密码的情况下，让未配置的wifi执行此方法切换wifi
	 * @param ssid
	 * @return
	 */
	public boolean ConnectionUnConfigForNoPsw(String ssid)
	{
		try
		{
			WifiConfiguration wc = new WifiConfiguration();


			wc.hiddenSSID = false;
			wc.status = WifiConfiguration.Status.ENABLED;

			wc.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			wc.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
//            wc.allowedKeyManagement.set(KeyMgmt.NONE);

			wc.allowedProtocols.set(Protocol.WPA);
			wc.allowedProtocols.set(Protocol.RSN);

			wc.allowedPairwiseCiphers.set(PairwiseCipher.TKIP);
			wc.allowedPairwiseCiphers.set(PairwiseCipher.CCMP);

			wc.allowedGroupCiphers.set(GroupCipher.WEP40);
			wc.allowedGroupCiphers.set(GroupCipher.WEP104);
			wc.allowedGroupCiphers.set(GroupCipher.TKIP);
			wc.allowedGroupCiphers.set(GroupCipher.CCMP);

			wc.SSID = "\"" + ssid + "\"";
			wc.preSharedKey = null;


			int res = mWifiManager.addNetwork(wc);
//
			return  mWifiManager.enableNetwork(res, true);
		}
		catch(Exception ex)
		{
			return false;
		}


	}

}