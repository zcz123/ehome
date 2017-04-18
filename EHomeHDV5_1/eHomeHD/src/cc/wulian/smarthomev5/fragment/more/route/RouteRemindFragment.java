package cc.wulian.smarthomev5.fragment.more.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter;
import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter.WifiInfoEntity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class RouteRemindFragment extends WulianFragment implements H5PlusWebViewContainer {

	public WifiManager wifiManager; // 管理wifi
	private ListView wifiListView;
	public RouteRemindWifiAdapter mRouteSettingWifiAdapter;
	protected AccountManager mAccountManger = AccountManager.getAccountManger();

	@ViewInject(R.id.more_zigbee_bssid)
	private TextView zigbeeBssid;
	@ViewInject(R.id.more_zigbee_chanl)
	private TextView zigbeeChanl;
	@ViewInject(R.id.more_zigbee_ssid)
	private TextView zigbeeSsid;
	@ViewInject(R.id.more_zigbee_imgview)
	private ImageView zigbeeImaView;

	private String zigbeeChanlString = "";
	private H5PlusWebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wifiManager = (WifiManager) mActivity
				.getSystemService(Context.WIFI_SERVICE); // 获得系统wifi服务
		mRouteSettingWifiAdapter = new RouteRemindWifiAdapter(mActivity, null);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.more_route_setting_content,
				container, false);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		webView = (H5PlusWebView) view.findViewById(R.id.more_route_setting_wifi_webview);
//		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
		Engine.bindWebviewToContainer((H5PlusWebViewContainer)RouteRemindFragment.this, webView);
		webView.loadUrl("file:///android_asset/route/channel.html");
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.nav_more));
		getSupportActionBar().setTitle(
				R.string.gateway_router_setting_wifi_channel);
	}

	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}

	private void loadData() {
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				List<ScanResult> wifilist = wifiManager.getScanResults();
				final List<WifiInfoEntity> entites = new ArrayList<WifiInfoEntity>();
				for (ScanResult scanResult : wifilist) {
					WifiInfoEntity entity = new WifiInfoEntity();
					entity.setChanel(WifiInfoEntity
							.getChannelByFrequency(scanResult.frequency) + "");
					entity.setSsid(scanResult.SSID);
					entity.setLevel(scanResult.level + "");
					entity.setBSSID(scanResult.BSSID + "");
					entity.setCapabilities(scanResult.capabilities + "");
					entity.setType(WifiInfoEntity.TYPE_NORMAL);
					entites.add(entity);
				}
				if (!StringUtil.isNullOrEmpty(mAccountManger.getmCurrentInfo()
						.getGwChanel())) {
					try {
						int gwChanelInt = Integer
								.parseInt(mAccountManger.getmCurrentInfo()
										.getGwChanel());
						int centerFrequence = 2410 + 5 * (gwChanelInt - 11);
						int toWifiChanelInt = (centerFrequence - 2412) / 5;
						if (toWifiChanelInt < 1) {
							toWifiChanelInt = 1;
						} else if (toWifiChanelInt > 13) {
							toWifiChanelInt = 13;
						}
						zigbeeChanlString = toWifiChanelInt + "";
						zigbeeChanl.setText("chan:" + zigbeeChanlString);
					} catch (NumberFormatException e) {
						zigbeeChanlString = mAccountManger.getmCurrentInfo()
								.getGwChanel();
						zigbeeChanl.setText("chan:" + zigbeeChanlString);
					}
				}
				zigbeeSsid.setText(mAccountManger
						.getGatewayName(mAccountManger.getmCurrentInfo().getGwID()));
				zigbeeBssid.setText(mAccountManger.getmCurrentInfo().getGwIP());
				// 对链表的数据进行数据的排序
				final List<WifiInfoEntity> newEntites = new ArrayList<WifiInfoEntity>();
				for (int i=0;i<entites.size();i++) {
					WifiInfoEntity entite=entites.get(i);
					if (zigbeeChanlString.equals(entite.getChanel())) {
						entite.setType(WifiInfoEntity.TYPE_CLASH);
						newEntites.add(entite);
					}
				}
				entites.removeAll(newEntites);
				System.out.println("sadjkflaskdfj"+zigbeeChanlString);
				for (int i=0;i<entites.size();i++) {
					WifiInfoEntity entite=entites.get(i);
					if ((Integer.valueOf(zigbeeChanlString) - Integer.valueOf(entite.getChanel()) )< 3
							&& Integer.valueOf(zigbeeChanlString)
							- Integer.valueOf(entite.getChanel()) > -3) {
						entite.setType(WifiInfoEntity.TYPE_WARNING);
						newEntites.add(entite);
					}
				}
				entites.removeAll(newEntites);
				Collections.sort(entites);
				newEntites.addAll(entites);
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mRouteSettingWifiAdapter.swapData(newEntites);
					}
				});
			}
		});
	}

	@Override
	public void addH5PlusWebView(H5PlusWebView webview) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroyContainer() {
		Engine.destroyPager(this);
		this.getActivity().finish();
	}

	@Override
	public ViewGroup getContainerRootView() {
		// TODO Auto-generated method stub
		return  (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	}

}
//package cc.wulian.smarthomev5.fragment.more.route;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import android.content.Context;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//import cc.wulian.ihome.wan.util.StringUtil;
//import cc.wulian.ihome.wan.util.TaskExecutor;
//import cc.wulian.smarthomev5.R;
//import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter;
//import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter.WifiInfoEntity;
//import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
//import cc.wulian.smarthomev5.tools.AccountManager;
//
//import com.lidroid.xutils.ViewUtils;
//import com.lidroid.xutils.view.annotation.ViewInject;
//
//public class RouteRemindFragment extends WulianFragment {
//
//	public WifiManager wifiManager; // 管理wifi
//	private ListView wifiListView;
//	public RouteRemindWifiAdapter mRouteSettingWifiAdapter;
//	protected AccountManager mAccountManger = AccountManager.getAccountManger();
//
//	@ViewInject(R.id.more_zigbee_bssid)
//	private TextView zigbeeBssid;
//	@ViewInject(R.id.more_zigbee_chanl)
//	private TextView zigbeeChanl;
//	@ViewInject(R.id.more_zigbee_ssid)
//	private TextView zigbeeSsid;
//	@ViewInject(R.id.more_zigbee_imgview)
//	private ImageView zigbeeImaView;
//
//	private String zigbeeChanlString = "";
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		wifiManager = (WifiManager) mActivity
//				.getSystemService(Context.WIFI_SERVICE); // 获得系统wifi服务
//		mRouteSettingWifiAdapter = new RouteRemindWifiAdapter(mActivity, null);
//		initBar();
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.more_route_setting_content,
//				container, false);
//		ViewUtils.inject(this, view);
//		return view;
//	}
//
//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		wifiListView = (ListView) view
//				.findViewById(R.id.more_route_setting_wifi_listview);
//		wifiListView.setAdapter(mRouteSettingWifiAdapter);
//	}
//
//	private void initBar() {
//		mActivity.resetActionMenu();
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setIconText(
//				mApplication.getResources().getString(R.string.nav_more));
//		getSupportActionBar().setTitle(
//				R.string.gateway_router_setting_wifi_channel);
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		loadData();
//	}
//
//	private void loadData() {
//		TaskExecutor.getInstance().execute(new Runnable() {
//
//			@Override
//			public void run() {
//				List<ScanResult> wifilist = wifiManager.getScanResults();
//				final List<WifiInfoEntity> entites = new ArrayList<WifiInfoEntity>();
//				for (ScanResult scanResult : wifilist) {
//					WifiInfoEntity entity = new WifiInfoEntity();
//					entity.setChanel(WifiInfoEntity
//							.getChannelByFrequency(scanResult.frequency) + "");
//					entity.setSsid(scanResult.SSID);
//					entity.setLevel(scanResult.level + "");
//					entity.setBSSID(scanResult.BSSID + "");
//					entity.setCapabilities(scanResult.capabilities + "");
//					entity.setType(WifiInfoEntity.TYPE_NORMAL);
//					entites.add(entity);
//				}
//				if (!StringUtil.isNullOrEmpty(mAccountManger.mCurrentInfo
//						.getGwChanel())) {
//					try {
//						int gwChanelInt = Integer
//								.parseInt(mAccountManger.mCurrentInfo
//										.getGwChanel());
//						int centerFrequence = 2410 + 5 * (gwChanelInt - 11);
//						int toWifiChanelInt = (centerFrequence - 2412) / 5;
//						if (toWifiChanelInt < 1) {
//							toWifiChanelInt = 1;
//						} else if (toWifiChanelInt > 13) {
//							toWifiChanelInt = 13;
//						}
//						zigbeeChanlString = toWifiChanelInt + "";
//						zigbeeChanl.setText("chan:" + zigbeeChanlString);
//					} catch (NumberFormatException e) {
//						zigbeeChanlString = mAccountManger.mCurrentInfo
//								.getGwChanel();
//						zigbeeChanl.setText("chan:" + zigbeeChanlString);
//					}
//				}
//				zigbeeSsid.setText(mAccountManger
//						.getGatewayName(mAccountManger.mCurrentInfo.getGwID()));
//				zigbeeBssid.setText(mAccountManger.mCurrentInfo.getGwIP());
//				// 对链表的数据进行数据的排序
//				final List<WifiInfoEntity> newEntites = new ArrayList<WifiInfoEntity>();
//				for (int i=0;i<entites.size();i++) {
//					WifiInfoEntity entite=entites.get(i);
//					if (zigbeeChanlString.equals(entite.getChanel())) {
//						entite.setType(WifiInfoEntity.TYPE_CLASH);
//						newEntites.add(entite);
//					}
//				}
//				entites.removeAll(newEntites);
//				System.out.println("sadjkflaskdfj"+zigbeeChanlString);
//				for (int i=0;i<entites.size();i++) {
//					WifiInfoEntity entite=entites.get(i);
//					if ((Integer.valueOf(zigbeeChanlString) - Integer.valueOf(entite.getChanel()) )< 3
//							&& Integer.valueOf(zigbeeChanlString)
//							- Integer.valueOf(entite.getChanel()) > -3) {
//						entite.setType(WifiInfoEntity.TYPE_WARNING);
//						newEntites.add(entite);
//					}
//				}
//				entites.removeAll(newEntites);
//				Collections.sort(entites);
//				newEntites.addAll(entites);
//				mActivity.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						mRouteSettingWifiAdapter.swapData(newEntites);
//					}
//				});
//			}
//		});
//	}
//}
