package cc.wulian.smarthomev5.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev5.activity.MainApplication;

public final class WifiUtil
{
	public interface OnWifiChangeListener
	{
		public void onWifiStateChanged( int state );

		public void onWifiScanCompleted( List<ScanResult> results );
	}

	private Context mContext;
	private WifiManager mWifiManager;
	private IntentFilter mWifiIntentFilter;
	private WifiStateBroadcast mWifiStateBroadcast;
	private OnWifiChangeListener mChangeListener;

	public WifiUtil() {
		mContext = MainApplication.getApplication();
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}

	public WifiUtil( Context context )
	{
		mContext = context;

		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		mWifiIntentFilter = new IntentFilter();
		mWifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mWifiIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

		mWifiStateBroadcast = new WifiStateBroadcast();
		context.registerReceiver(mWifiStateBroadcast, mWifiIntentFilter);
	}
	public WifiUtil (Context context,String type){
		mContext = context;
	}
	public String getGatewayIP(){
		DhcpInfo di = mWifiManager.getDhcpInfo();
		long getewayIpL = di.gateway;
		return long2ip(getewayIpL);
	}

	public String getSSID() {
		return mWifiManager.getConnectionInfo().getSSID();
	}

	private static final String long2ip ( long ip){
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf((int) (ip & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
		return sb.toString();
	}

	public void uninit(){
		mContext.unregisterReceiver(mWifiStateBroadcast);
	}

	public int getWifiState(){
		return mWifiManager.getWifiState();
	}

	public boolean isWifiEnabled(){
		return mWifiManager.isWifiEnabled();
	}

	public boolean enableWifi(){
		return mWifiManager.setWifiEnabled(true);
	}

	public boolean diableWifi(){
		return mWifiManager.setWifiEnabled(false);
	}

	public boolean startScan(){
		return mWifiManager.startScan();
	}
	
	public WifiInfo getWifiInfo(){
		return mWifiManager.getConnectionInfo();
	}
	
	public List<WifiConfiguration> getConfiguredNetworks(){
		List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
		return configurations == null ? new ArrayList<WifiConfiguration>() : configurations;
	}

	public void setOnWifiStateChangeListener( OnWifiChangeListener listener ){
		mChangeListener = listener;
	}

	private class WifiStateBroadcast extends BroadcastReceiver
	{
		@Override
		public void onReceive( Context context, Intent intent ){
			String action = intent.getAction();
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
				int state = intent
						.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
				if (mChangeListener != null) mChangeListener.onWifiStateChanged(state);
			}
			else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){
				if (mChangeListener != null)
					mChangeListener.onWifiScanCompleted(mWifiManager.getScanResults());
			}
		}
	}

	public boolean isWifi(){
		boolean iswifi=false;

		return iswifi;
	}
	public static boolean getIsWifi(Context context) {
		boolean iswifi=false;
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();
			if (type.equalsIgnoreCase("WIFI")) {
				iswifi=true;
			}
		}
		return iswifi;
	}
}
