/**
 * Project Name:  iCam
 * File Name:     WifiSettingActivity.java
 * Package Name:  com.wulian.icam.view.setting
 * @Date:         2014年10月17日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.WiFiScanResult;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.utils.WifiAdmin;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.view.widget.PullListView;
import com.wulian.icam.view.widget.PullListView.OnRefreshListener;
import com.wulian.lanlibrary.LanController;
import com.wulian.lanlibrary.WulianLANApi;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.utils.LibraryLoger;

/**
 * @ClassName: WifiSettingActivity
 * @Function: wifi设置
 * @Date: 2014年10月17日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class WifiSettingActivity extends BaseFragmentActivity implements
		OnClickListener {
	private PullListView wifi_signal_list;// WiFi信号列表
	private WifiAdmin wifiAdmin;
	private WifiManager mWifiManager;
	private WifiAdapter mWifiAdapter;
	private WifiInfo mWifiInfo;
	private List<ScanResult> mScanResults;
	private List<WifiConfiguration> mWifiConfiguration;
	private List<WiFiScanResult> mScanresultList;
	EditText et_wifi_name;
	EditText et_wifi_pwd;
	Button btn_start_linkwifi;
	private Device device;
	private String deviceId, deviceSsid, devicePwd, originSsid, localMac,
			remoteIp, remoteMac, wifi_name, wifi_pwd;
	private WifiConfiguration originConfig;// 原始连接的网络
	private boolean isFirstRegisterBroadCast = true;
	private NetConnectChangedReceiver receiver;

	private int hasRetryTimes = 0, linkRetryTimes = 0;
	private ProgressDialog progressDialog;// 对话框
	private View progressView;
	private ProgressBar pb_loadingBar;
	private TextView tv_desc, tv_step1, tv_step2, tv_step3, tv_step4, tv_desc1,
			tv_desc2, tv_desc3, tv_desc4;
	private ProgressBar pb_step1, pb_step2, pb_step3, pb_step4;
	private ImageView iv_step_result1, iv_step_result2, iv_step_result3,
			iv_step_result4;
	private View head2, head3, head4;
	private static final int STEP1 = 1;
	private static final int STEP2 = 2;
	private static final int STEP3 = 3;
	private static final int STEP4 = 4;
	private boolean isLinkedToDevice = false, isRefreshed = false,
			isVerified = false, isCancleLinkRetry = false, isTimeOut = false,
			isNeedOfflineToast = false,/* 超时提示 */
			isChangeWifiSuccess = true/*
									 * 切换网络是否成功，兼容5.0的变量，本质是为了仅仅消耗第一次的连接成功;
									 * 默认true表示一开始忽略网络变化。
									 */;

	private long startSetting;
	private static int SETTING_TIMEOUT = 100;
	// base fail ; 发送2次定位; 配置点击的时机;
	// 兼容5.0要点：连接目标wifi，只消耗首次广播
	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				WifiSettingActivity.this.finish();
				break;
			case 1:// 检测连接情况
				Utils.sysoInfo("检测连接情况");
				if (!isLinkedToDevice) {
					// if (progressDialog != null && progressDialog.isShowing())
					// {
					// progressDialog.dismiss();
					// }
					CustomToast.show(WifiSettingActivity.this,
							R.string.config_device_link_longtime);
				}
				break;
			case 2:// 重新连接设备wifi
				Utils.sysoInfo("开始重新连接设备wifi");

				if (progressDialog != null && !progressDialog.isShowing()) {
					progressDialog.show();
				}
				// pb_loadingBar.setProgress(0);//
				// 在首次连接时，有重连的机会，不用还原初始值，继续计时，直到超时
				// myHandler.sendEmptyMessage(5);
				isChangeWifiSuccess = false;
				wifiAdmin.addNetworkAndLink(wifiAdmin.createWifiConfiguration(
						deviceSsid, devicePwd, WifiAdmin.TYPE_WPA));
				break;// 这个break已经加了，又被谁去了？
			case 3:// 120秒配置超时
				Utils.sysoInfo("120秒配置超时，超时后，不再拥有重试的机会");
				isTimeOut = true;// 超时后，说明板子有问题，永久失去重试的机会
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					CustomToast.show(WifiSettingActivity.this,
							R.string.setting_config_timeout);
				}
				break;
			case 4:// 开始验证wifi配置的正确性
				Utils.sysoInfo("延迟时间到，真正地开始验证wifi配置的正确性.");
				sendRequest(
						RouteApiType.getWirelessWifiConnectInformationForDevice,
						RouteLibraryParams
								.getWirelessWifiConnectInformationForDevice(
										remoteIp, localMac, remoteMac), false);
				break;
			case 5:
				if (pb_loadingBar.getProgress() < SETTING_TIMEOUT) {
					pb_loadingBar.setProgress(pb_loadingBar.getProgress() + 1);
					myHandler.sendEmptyMessageDelayed(5, 1000);
				} else {
					myHandler.removeMessages(5);
					pb_loadingBar.setProgress(0);
				}

				break;
			case 9:// 获取设备信息
				Utils.sysoInfo("获取设备信息");
				sendRequest(RouteApiType.getCurrentDeviceInformation,
						RouteLibraryParams
								.getCurrentDeviceInformation(localMac), false);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_setting);
		initViews();
		initListeners();
		initDatas();
		// showList();
		// mWifiAdmin.getEncryption("HiWiFi_Willin");
	}

	private void showList() {
		// mWifiManager.setWifiEnabled(true);
		// mWifiManager.startScan();
		mScanResults = mWifiManager.getScanResults();
		WiFiScanResult itemModel = null;
		ScanResult itemResult = null;
		mScanresultList.clear();
		if (mScanResults != null && mWifiInfo != null) {
			ComparatorScanResult com = new ComparatorScanResult();
			Collections.sort(mScanResults, com);
			int scanNum = mScanResults.size();
			int configNum = mWifiConfiguration.size();
			for (int i = 0; i < scanNum; i++) {
				itemResult = mScanResults.get(i);
				// 过滤掉 WL_Camera_xxxx
				if (itemResult.SSID.contains(APPConfig.DEVICE_WIFI_SSID_PREFIX)) {
					continue;
				}
				// 过滤掉空字符
				if (TextUtils.isEmpty(itemResult.SSID.trim())) {
					continue;
				}
				itemModel = new WiFiScanResult();
				itemModel.setSecurity(getSecurity(itemResult.capabilities));
				itemModel.setSsid(itemResult.SSID);
				itemModel.setSignalLevel(calculateSignalLevel(itemResult.level,
						4));

				itemModel.setMac_address("");
				itemModel.setNetId(-1);
				for (int j = 0; j < configNum; j++) {
					if (mWifiConfiguration.get(j).SSID.replace("\"", "")
							.equals(itemResult.SSID)) {
						itemModel.setNetId(mWifiConfiguration.get(j).networkId);
						break;
					}
				}
				if (!itemResult.SSID.equals(mWifiInfo.getSSID())) {// 非当前网络
					itemModel.setMac_address("");
					mScanresultList.add(itemModel);

				} else {// 当前网络
					itemModel.setMac_address(mWifiInfo.getMacAddress());
					mScanresultList.add(0, itemModel);
				}
			}
		}
		if (mWifiConfiguration != null) {

		}
		mWifiAdapter.refreshList(mScanresultList);
		// if (mWifiConfiguration != null && mWifiConfiguration.get(0) != null)
		// {
		// addNetwork(mWifiConfiguration.get(0));
		// }
	}

	private void initViews() {

		((TextView) findViewById(R.id.titlebar_title))
				.setText(R.string.setting_wifi_setting);
		wifi_signal_list = (PullListView) findViewById(R.id.wifi_signal_list);
		et_wifi_name = (EditText) findViewById(R.id.et_wifi_name);
		et_wifi_pwd = (EditText) findViewById(R.id.et_wifi_pwd);
		btn_start_linkwifi = (Button) findViewById(R.id.btn_start_linkwifi);

		progressDialog = new ProgressDialog(this, R.style.dialog);
		progressDialog.setCancelable(true);// 不可取消，避免打乱流程=>可以取消！
		progressDialog.setCanceledOnTouchOutside(false);// 不可取消，避免打乱流程
		progressDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				Utils.sysoInfo("progerssDialog dismiss");
				resetAllStep();
				myHandler.removeMessages(1);// 移除连接消息
				myHandler.removeMessages(3);// 移除配置超时
				myHandler.removeMessages(5);// 移除计时器
				hasRetryTimes = 0;// 重置获取设备信息重试次数
				linkRetryTimes = APPConfig.LINK_RETRAY_TIMES;// 重置连接设备重试次数=>dismiss后，不再拥有连接设备重试的机会，即首次有重试机会，好处就是可以取消对话框了，并且避免了在没有连接上设备时取消对话框后的自动重连死循环。
				btn_start_linkwifi.setEnabled(true);// 还原成原网络前，也可重新点击？
				// isLinkedToDevice = false;// 便于下次流程=>如果配置错误，照成不断重试死循环
				// 需要再加个变量isCancleLinkRetry=>这里重置为false，不合理，在开始设置和验证后重置。
				// 无需恢复网络便于下次的 查询设备绑定情况=> 恢复 便于下次完整流程
				if (originConfig != null) {
					isChangeWifiSuccess = false;
					wifiAdmin.addNetworkAndLink(originConfig);
				}
			}
		});
		progressView = getLayoutInflater().inflate(
				R.layout.custom_progress_dialog_wifi_step,
				(ViewGroup) findViewById(R.id.custom_progressdialog));
		pb_loadingBar = (ProgressBar) progressView.findViewById(R.id.progress);
		pb_loadingBar.setMax(SETTING_TIMEOUT);
		tv_desc = ((TextView) progressView.findViewById(R.id.tv_desc));
		tv_desc.setText(getResources().getText(
				R.string.setting_wifi_in_processing));

		tv_step1 = (TextView) progressView.findViewById(R.id.tv_step1);
		tv_step2 = (TextView) progressView.findViewById(R.id.tv_step2);
		tv_step3 = (TextView) progressView.findViewById(R.id.tv_step3);
		tv_step4 = (TextView) progressView.findViewById(R.id.tv_step4);

		tv_desc1 = (TextView) progressView.findViewById(R.id.tv_desc1);
		tv_desc2 = (TextView) progressView.findViewById(R.id.tv_desc2);
		tv_desc3 = (TextView) progressView.findViewById(R.id.tv_desc3);
		tv_desc4 = (TextView) progressView.findViewById(R.id.tv_desc4);

		pb_step1 = (ProgressBar) progressView.findViewById(R.id.pb_step1);
		pb_step2 = (ProgressBar) progressView.findViewById(R.id.pb_step2);
		pb_step3 = (ProgressBar) progressView.findViewById(R.id.pb_step3);
		pb_step4 = (ProgressBar) progressView.findViewById(R.id.pb_step4);

		iv_step_result1 = (ImageView) progressView
				.findViewById(R.id.iv_step_result1);
		iv_step_result2 = (ImageView) progressView
				.findViewById(R.id.iv_step_result2);
		iv_step_result3 = (ImageView) progressView
				.findViewById(R.id.iv_step_result3);
		iv_step_result4 = (ImageView) progressView
				.findViewById(R.id.iv_step_result4);

		head2 = progressView.findViewById(R.id.head2);
		head3 = progressView.findViewById(R.id.head3);
		head4 = progressView.findViewById(R.id.head4);
	}

	public void startStep(int step) {
		switch (step) {
		case STEP1:
			tv_step1.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc1.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step1.setVisibility(View.VISIBLE);
			iv_step_result1.setVisibility(View.GONE);
			break;
		case STEP2:
			head2.setVisibility(View.VISIBLE);
			tv_step2.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc2.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step2.setVisibility(View.VISIBLE);
			iv_step_result2.setVisibility(View.GONE);
			break;
		case STEP3:
			head3.setVisibility(View.VISIBLE);
			tv_step3.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc3.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step3.setVisibility(View.VISIBLE);
			iv_step_result3.setVisibility(View.GONE);
			break;
		case STEP4:
			head4.setVisibility(View.VISIBLE);
			tv_step4.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc4.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step4.setVisibility(View.VISIBLE);
			iv_step_result4.setVisibility(View.GONE);
			break;

		default:
			break;
		}

	}

	public void resultStep(int step, boolean isSuccess) {
		switch (step) {
		case STEP1:
			tv_step1.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc1.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step1.setVisibility(View.GONE);
			iv_step_result1.setVisibility(View.VISIBLE);

			if (isSuccess) {
				iv_step_result1.setImageResource(R.drawable.pb_finish_success);
			} else {
				iv_step_result1.setImageResource(R.drawable.pb_finish_fail);
			}
			break;
		case STEP2:
			head2.setVisibility(View.VISIBLE);
			tv_step2.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc2.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step2.setVisibility(View.GONE);
			iv_step_result2.setVisibility(View.VISIBLE);
			if (isSuccess) {
				iv_step_result2.setImageResource(R.drawable.pb_finish_success);
			} else {
				iv_step_result2.setImageResource(R.drawable.pb_finish_fail);
			}
			break;
		case STEP3:
			head3.setVisibility(View.VISIBLE);
			tv_step3.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc3.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step3.setVisibility(View.GONE);
			iv_step_result3.setVisibility(View.VISIBLE);
			if (isSuccess) {
				iv_step_result3.setImageResource(R.drawable.pb_finish_success);
			} else {
				iv_step_result3.setImageResource(R.drawable.pb_finish_fail);
			}
			break;
		case STEP4:
			head4.setVisibility(View.VISIBLE);
			tv_step4.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc4.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step4.setVisibility(View.GONE);
			iv_step_result4.setVisibility(View.VISIBLE);
			if (isSuccess) {
				iv_step_result4.setImageResource(R.drawable.pb_finish_success);
			} else {
				iv_step_result4.setImageResource(R.drawable.pb_finish_fail);
			}
			break;

		default:
			break;
		}

	}

	public void resetStep(int step) {
		switch (step) {
		case STEP1:
			tv_step1.setBackgroundResource(R.drawable.shape_gray_circle);
			tv_desc1.setTextColor(getResources().getColor(
					R.color.wifi_setting_gray));
			pb_step1.setVisibility(View.GONE);
			iv_step_result1.setVisibility(View.GONE);
			break;
		case STEP2:
			head2.setVisibility(View.GONE);
			tv_step2.setBackgroundResource(R.drawable.shape_gray_circle);
			tv_desc2.setTextColor(getResources().getColor(
					R.color.wifi_setting_gray));
			pb_step2.setVisibility(View.GONE);
			iv_step_result2.setVisibility(View.GONE);
			break;
		case STEP3:
			head3.setVisibility(View.GONE);
			tv_step3.setBackgroundResource(R.drawable.shape_gray_circle);
			tv_desc3.setTextColor(getResources().getColor(
					R.color.wifi_setting_gray));
			pb_step3.setVisibility(View.GONE);
			iv_step_result3.setVisibility(View.GONE);
			break;
		case STEP4:
			head4.setVisibility(View.GONE);
			tv_step4.setBackgroundResource(R.drawable.shape_gray_circle);
			tv_desc4.setTextColor(getResources().getColor(
					R.color.wifi_setting_gray));
			pb_step4.setVisibility(View.GONE);
			iv_step_result4.setVisibility(View.GONE);
			break;

		default:
			break;
		}

	}

	public void resetAllStep() {
		resetStep(STEP1);
		resetStep(STEP2);
		resetStep(STEP3);
		resetStep(STEP4);
	}

	private void initListeners() {

		((ImageView) findViewById(R.id.titlebar_back)).setOnClickListener(this);
		btn_start_linkwifi.setOnClickListener(this);
		wifi_signal_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				WiFiScanResult item = mScanresultList.get(position - 1);
				et_wifi_name.setText(item.getSsid());
				if ("none".equals(wifiAdmin.getEncryption(item.getSsid()))) {
					et_wifi_pwd.setVisibility(View.GONE);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						rect_all();
					} else {
						rect_all_low();
					}
				} else {
					et_wifi_pwd.setVisibility(View.VISIBLE);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						rect_top();
					} else {
						rect_top_low();
					}
				}

			}

		});
		wifi_signal_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				isRefreshed = false;
				wifiAdmin.startScan();
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void rect_all() {
		et_wifi_name.setBackground(getResources().getDrawable(
				R.drawable.selector_shape_round_rect_all));

	}

	private void rect_all_low() {
		et_wifi_name.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_shape_round_rect_all));
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	// 4.1.2
	private void rect_top() {
		et_wifi_name.setBackground(getResources().getDrawable(
				R.drawable.selector_shape_round_rect_top));
	}

	private void rect_top_low() {
		et_wifi_name.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_shape_round_rect_top));
	}

	private void initDatas() {
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (!mWifiManager.isWifiEnabled()) {
			CustomToast.show(this, R.string.setting_link_wifi);
			finish();
			return;
		}
		wifiAdmin = new WifiAdmin(this);
		// 移动网络下 ssid=null or <unknown ssid>
		String ssid = wifiAdmin.getCurrentWifiInfo().getSSID();
		if (ssid != null && !"<unknown ssid>".equals(ssid)) {
			originSsid = ssid.replace("\"", "");
			et_wifi_name.setText(originSsid);
			originConfig = wifiAdmin.getConfiguredNetwork(originSsid);
		}

		mScanresultList = new ArrayList<WiFiScanResult>();
		mWifiInfo = mWifiManager.getConnectionInfo();
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
		mWifiAdapter = new WifiAdapter(this);
		wifi_signal_list.setAdapter(mWifiAdapter);
		device = (Device) getIntent().getExtras().getSerializable("device");
		deviceId = device.getDevice_id();

		localMac = wifiAdmin.getCurrentWifiInfo().getMacAddress();
		remoteMac = Utils.deviceIdToMac(deviceId);
		deviceSsid = APPConfig.DEVICE_WIFI_SSID_PREFIX
				+ deviceId.subSequence(16, 20).toString()
						.toUpperCase(Locale.ENGLISH);
		devicePwd = LanController.getFourStringPassword(deviceId
				.subSequence(16, 20).toString().toUpperCase(Locale.ENGLISH));
		Utils.sysoInfo(deviceSsid + "的wifi密码:" + devicePwd);

		showList();// 立即显示,避免在扫描期间不显示数据
	}

	@Override
	protected void onStart() {
		super.onStart();

		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		if (receiver == null) {
			receiver = new NetConnectChangedReceiver();
		}
		registerReceiver(receiver, filter);
		wifiAdmin.startScan();// 大概1s后，刷新数据显示
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			this.finish();
		} else if (id == R.id.btn_start_linkwifi) {
			wifi_name = et_wifi_name.getText().toString().trim();
			wifi_pwd = et_wifi_pwd.getText().toString().trim();
			if (TextUtils.isEmpty(wifi_name)
					|| (et_wifi_pwd.getVisibility() == View.VISIBLE && TextUtils
							.isEmpty(wifi_pwd))) {
				if (et_wifi_pwd.getVisibility() == View.VISIBLE) {
					CustomToast
							.show(this, R.string.setting_input_wifi_name_pwd);
				} else {
					CustomToast.show(this, R.string.setting_input_wifi_name);
				}
				return;
			}
			btn_start_linkwifi.setEnabled(false);
			isCancleLinkRetry = false;// 点击配置时，不取消连接重试机会
			isNeedOfflineToast = true;
			Utils.hideKeyboard(this);
			// if (!mWifiAdmin.isDeviceOnScanResult(deviceSsid,
			// mWifiAdmin.getScanResultList())) {
			// CustomToast.show(this, R.string.device_is_offline);
			// mWifiAdmin.startScan();// 1s
			// return;
			// }
			if ("lan".equals(getIntent().getExtras().getString("type"))) {
				sendRequest(RouteApiType.setWirelessWifiForDevice,
						RouteLibraryParams.setWirelessWifiForDevice(
								device.getIp(), localMac, remoteMac, wifi_name,
								wifiAdmin.getEncryption(wifi_name), wifi_pwd),
						false);
				return;
			}
			hasRetryTimes = 0;
			linkRetryTimes = 0;// 手动配置，恢复重新连接的机会！
			startSetting = System.currentTimeMillis();
			// 1、连接到wl_camera
			if (originSsid != null && !originSsid.equals(deviceSsid)) {// 连上wifi，不等
				if (!progressDialog.isShowing()) {
					progressDialog.show();
					progressDialog.setContentView(progressView);// show之后
				}

				Utils.sysoInfo("切换到设备wifi " + deviceSsid);
				if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
					tv_desc.setText(R.string.config_linking_to_camera);
				} else {
					tv_desc.setText(getResources().getString(
							R.string.setting_has_finish));// 连接摄像头 15
				}
				startStep(STEP1);
				myHandler.sendEmptyMessageDelayed(1, APPConfig.LINK_TIME_OUT);
				WifiConfiguration wcg = wifiAdmin.createWifiConfiguration(
						deviceSsid, devicePwd, WifiAdmin.TYPE_WPA);
				isChangeWifiSuccess = false;
				wifiAdmin.addNetworkAndLink(wcg);// 如果失败，自动重连？高版本会，低版本不会重连会卡住。
				pb_loadingBar.setProgress(0);// 还原初始化值
				myHandler.sendEmptyMessage(5);
				myHandler.removeMessages(3);
				myHandler.sendEmptyMessageDelayed(3, SETTING_TIMEOUT * 1000);// 120秒配置超时
			} else if (originSsid != null && originSsid.equals(deviceSsid)) {// 连上wifi,相等
				CustomToast.show(WifiSettingActivity.this,
						R.string.setting_error_change_wifi);
				finish();

			} else if (originSsid == null) {// 移动网络 不允许，必须先打开wifi
				if (!progressDialog.isShowing()) {
					progressDialog.show();
					progressDialog.setContentView(progressView);// show之后
				}
				if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
					tv_desc.setText(R.string.config_linking_to_camera);
				} else {
					tv_desc.setText(getResources().getString(
							R.string.setting_has_finish));// 连接摄像头 15
				}
				WifiConfiguration wcg = wifiAdmin.createWifiConfiguration(
						deviceSsid, devicePwd, WifiAdmin.TYPE_WPA);
				isChangeWifiSuccess = false;
				wifiAdmin.addNetworkAndLink(wcg);// 如果失败，自动重连？
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		myHandler.removeMessages(1);
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		isFirstRegisterBroadCast = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myHandler.removeMessages(0);
		myHandler.removeMessages(1);
		myHandler.removeMessages(2);
		myHandler.removeMessages(3);
		myHandler.removeMessages(5);
		myHandler.removeMessages(9);
		// 还原网络
		restoreOriginWiFi();
	}

	public void restoreOriginWiFi() {
		wifiAdmin.removeConfiguredNetwork(deviceSsid);
		String ssid = wifiAdmin.getCurrentWifiInfo().getSSID();
		if (ssid != null && !ssid.replace("\"", "").equals(originSsid)) {// 检查一次wifi还原情况
			Utils.sysoInfo("ondestory 还原原网络 " + originSsid);
			isChangeWifiSuccess = false;
			wifiAdmin.addNetworkAndLink(originConfig);
		}
	}

	@Override
	protected void progressDialogDissmissed() {
		super.progressDialogDissmissed();
		myHandler.removeMessages(1);
	}

	// // 获取当前Wifi mac地址
	// public static String getLocalMacAddressFromWifiInfo(Context context) {
	// WifiManager wifi = (WifiManager) context
	// .getSystemService(Context.WIFI_SERVICE);
	// if (wifi.isWifiEnabled()) {
	// WifiInfo info = wifi.getConnectionInfo();
	// if (info != null) {
	// return info.getMacAddress();
	// } else {
	// return "";
	// }
	// } else {
	// return "";
	// }
	// }

	class NetConnectChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 问题原因：wifi监控状态不熟悉
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
					.getAction())) {// 4次: 1、原始 2、断开 3、获取ip 4、连接上
				NetworkInfo networkInfo = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				Utils.sysoInfo("NETWORK_STATE_CHANGED_ACTION "
						+ networkInfo.getState() + " "
						+ networkInfo.getExtraInfo());
				if (isFirstRegisterBroadCast) {// 如果是注册时的首次广播，则消费掉
					isFirstRegisterBroadCast = false;
					Utils.sysoInfo("消耗首次广播");
					return;
				}

				if (networkInfo.isConnected() && networkInfo.isAvailable()
						&& !isChangeWifiSuccess) {// isConnected可以连接并传输数据，但是不一定可以上网！
					Utils.sysoInfo("isConnected && isAvailable");
					isChangeWifiSuccess = true;
					WifiInfo wInfo = intent
							.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);// api14
					// WifiInfo wInfo = wifiAdmin.getCurrentWifiInfo();
					LibraryLoger.d("deviceSsid is:" + deviceSsid + ";wInfo is:"
							+ wInfo.getSSID() + ";"
							+ Formatter.formatIpAddress(wInfo.getIpAddress()));
					if (deviceSsid.equals(wInfo.getSSID().replace("\"", ""))) {
						Utils.sysoInfo("连接上摄像头热点了");
						if (!isLinkedToDevice) {
							Utils.sysoInfo("连接上摄像头热点，之前没有连接过");
							if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
								tv_desc.setText(R.string.setting_link_camera_success_get_remoteip);
							} else {
								tv_desc.setText(getResources().getString(
										R.string.setting_has_finish));// 已经连接上摄像头
																		// 30
							}
							resultStep(STEP1, true);
							startStep(STEP2);
							// 已经连接上摄像头设备
							Utils.sysoInfo("已经连接上摄像头设备" + wInfo.getSSID()
									+ ",开始搜寻设备获取远程ip");
							isLinkedToDevice = true;
							String localIp = wifiAdmin.getLocalIpAddress();
							LanController.setLocalIpV4(localIp);

							myHandler.sendEmptyMessageDelayed(9,
									APPConfig.DEVICE_INFO_DELAY);
						} else {
							Utils.sysoInfo("连接上摄像头热点，但是之前已经连接过，这里算是自动恢复连接,直接验证wifi有效性");
							myHandler.sendEmptyMessage(4);
						}

					} else if (originSsid != null
							&& originSsid.equals(wInfo.getSSID().replace("\"",
									"")) && isLinkedToDevice) {// 连接到设备的网络还原为手动还原
						Utils.sysoInfo("连接上原来的wifi热点，连接上设备过");
						progressDialog.dismiss();// =>如果已经不显示，直接return；如果执行后，当前即为配置网络，切网逻辑会return
						// CustomToast.show(WifiSettingActivity.this, "网络已经恢复:"
						// + wInfo.getSSID());
						Utils.sysoInfo("正常还原为原网络:" + originSsid);
						resultStep(STEP4, true);
						long time = (System.currentTimeMillis() - startSetting) / 1000;
						Utils.sysoInfo("本次耗时:" + time + "秒");
						wifiAdmin.removeConfiguredNetwork(deviceSsid);
						isLinkedToDevice = false;// 只有在还原为原来的网络后，重置为false，才符合逻辑
						// CustomToast.show(WifiSettingActivity.this, "耗时:" +
						// time
						// + "秒");
						btn_start_linkwifi.setEnabled(true);// 还原成原网络后，即可重新点击
						if (isVerified) {
							CustomToast.show(WifiSettingActivity.this,
									R.string.setting_wifi_success);// 成功提示都一样
						} else if (deviceId != null) {
							// CustomToast
							// .show(WifiSettingActivity.this,
							// R.string.setting_error_right_wifiname_wifipwd);
							if (deviceId.toLowerCase(Locale.ENGLISH)
									.startsWith("cmic01")) {
								CustomToast.show(WifiSettingActivity.this,
										R.string.setting_wifi_success2);
							} else {
								CustomToast.show(WifiSettingActivity.this,
										R.string.setting_wifi_success3);
							}
						}
						myHandler.sendEmptyMessageDelayed(0, 200);
					} else {// isLinkedToDevice==false;没有连接到设备的网络还原为自动还原

						Utils.sysoInfo("WiFiSettingActivity (信号弱自动、业务逻辑手动、配置超时)还原为原网络:"
								+ originSsid);
						btn_start_linkwifi.setEnabled(true);// 还原成原网络后，即可重新点击
						// 如果是人为手动触发并且此时wifi名称为空 则return
						if (TextUtils.isEmpty(wifi_name)) {
							return;
						}

						if (linkRetryTimes < APPConfig.LINK_RETRAY_TIMES
								&& !isLinkedToDevice && !isCancleLinkRetry
								&& !isTimeOut) {
							Utils.sysoInfo("连接上原来的wifi热点，没有超过重试机会，没有连接上设备过，没有取消重连，没有超时，开始重试");// isLinkedToDevice也可能在progressDialog
							// dissmis时
							// 重置为false=>已经注释掉了
							linkRetryTimes++;
							Utils.sysoInfo("连接设备，重试" + linkRetryTimes);
							if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
								tv_desc.setText("连接摄像头,重试" + linkRetryTimes);
							} else {
								tv_desc.setText(getResources().getString(
										R.string.setting_has_finish));// 连接摄像头
																		// 15
							}
							myHandler.sendEmptyMessageDelayed(2,
									APPConfig.CAMERA_WIFI_DELAY);
						} else {
							if (!isCancleLinkRetry && !isLinkedToDevice
									&& isNeedOfflineToast) {
								CustomToast.show(WifiSettingActivity.this,
										R.string.setting_device_is_offline);
								isNeedOfflineToast = false;
								resultStep(STEP1, false);
							}
							progressDialog.dismiss();
						}

					}
				}

			}

			if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent
					.getAction()) && !isRefreshed) {
				Utils.sysoInfo("扫描结束,刷新wifi列表,避免列表数据较少的问题");
				for (ScanResult sr : mWifiManager.getScanResults()) {
					Utils.sysoInfo(sr.SSID);
				}
				isRefreshed = true;
				wifi_signal_list.onRefreshComplete();
				showList();
			}
		}
	}

	@Override
	protected void DataReturn(boolean success, RouteApiType apiType, String json) {
		super.DataReturn(success, apiType, json);
		if (success) {
			switch (apiType) {
			case getCurrentDeviceInformation:// 2、搜寻设备获取远程ip
				try {
					JSONObject jsonObj = new JSONObject(json);
					String data = jsonObj.optString("data");
					if (!TextUtils.isEmpty(data)) {
						JSONObject dataJson = jsonObj.getJSONArray("data")
								.getJSONObject(0);
						remoteIp = dataJson.optString("ip");
						Utils.sysoInfo("成功获取远程固定ip" + remoteIp);
						// 3、获取连接信息(忽略)
						// 4、配置
						if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
							tv_desc.setText(R.string.config_get_remoteip_success_setting_wifi);
						} else {
							tv_desc.setText(getResources().getString(
									R.string.setting_has_finish));// 成功发送配置 60
						}
						resultStep(STEP2, true);
						startStep(STEP3);
						Utils.sysoInfo("发送配置请求");
						sendRequest(RouteApiType.setWirelessWifiForDevice,
								RouteLibraryParams.setWirelessWifiForDevice(
										remoteIp, localMac, remoteMac,
										wifi_name,
										wifiAdmin.getEncryption(wifi_name),
										wifi_pwd), false);
					} else {
						// 获取失败，重新请求
						// 2、搜寻设备获取远程ip
						Utils.sysoInfo("ip解析失败 重试" + json);

						if (hasRetryTimes < APPConfig.REMOTEIP_RETRAY_TIMES) {
							// String localIp = wifiAdmin.getLocalIpAddress();
							// WulianLANApi.setLocalIpV4(localIp);
							// localMac=getLocalMacAddressFromWifiInfo(WifiSettingActivity.this);

							// sendRequest(
							// RouteApiType.getCurrentDeviceInformation,
							// RouteLibraryParams
							// .getCurrentDeviceInformation(localMac),
							// false);
							myHandler.sendEmptyMessageDelayed(9,
									APPConfig.DEVICE_INFO_DELAY);
							hasRetryTimes++;
							if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
								tv_desc.setText(getResources().getString(
										R.string.setting_parse_ip_error_trying)
										+ hasRetryTimes
										+ getResources().getString(
												R.string.config_retry_times));
							}
						} else {
							progressDialog.dismiss();
							CustomToast.show(WifiSettingActivity.this,
									R.string.setting_parse_ip_fail);
							hasRetryTimes = 0;
							// 恢复网络，便于下次流程 progressDialog dissmiss的接口中做
							// String ssid = wifiAdmin.getCurrentWifiInfo()
							// .getSSID();
							// if (ssid != null
							// && !ssid.replace("\"", "").equals(
							// originSsid)) {// 检查一次wifi还原情况
							// wifiAdmin.addNetworkAndLink(originConfig);
							// }
						}
					}
				} catch (JSONException e) {
					CustomToast.show(this,
							R.string.setting_server_return_ip_error);
					progressDialog.dismiss();
					e.printStackTrace();

				}
				break;
			case setWirelessWifiForDevice:// 配置请求成功返回，但不一定配置成功
				Utils.sysoInfo("配置请求成功返回,深bug:设备在wifi配置时，如果与先前一个配置不同，会对当前的连接造成影响，重连！");
				// 开始耗时的验证
				// CustomToast.show(WifiSettingActivity.this, "WiFi配置请求发送成功");
				// if (ICamApplication.CURRENT_VERSION ==
				// ICamApplication.DEVING_VERSION) {
				// tv_desc.setText(R.string.verify_wifi_validity);// 延迟1秒
				// } else {
				// tv_desc.setText(getResources().getString(
				// R.string.has_finish));// 验证wifi有效性 70
				// }
				// Utils.sysoInfo("延迟5秒后发送验证消息");
				// myHandler.sendEmptyMessageDelayed(4,
				// APPConfig.DEVICE_INFO_DELAY * 2);
				resultStep(STEP3, true);
				startStep(STEP4);
				if ("lan".equals(getIntent().getExtras().getString("type"))) {
					wifiSetSuccess();
					return;
				}
				// 跳过验证这一步
				tv_desc.setText(R.string.setting_send_success_restore_origin_wifi);
				isVerified = false;
				isCancleLinkRetry = true;
				btn_start_linkwifi.setEnabled(true);
				wifiSetSuccess();
				break;
			case getWirelessWifiConnectInformationForDevice:
				try {
					JSONObject jsonObj = new JSONObject(json);
					String data = jsonObj.optString("data");
					if (!TextUtils.isEmpty(data)) {
						JSONObject dataJson = new JSONObject(jsonObj
								.getJSONArray("data").getJSONObject(0)
								.getString("item"));
						String ipaddress = dataJson.optString("ipaddress");
						String essid = dataJson.optString("essid");
						Utils.sysoInfo("验证ipaddress:" + ipaddress);
						Utils.sysoInfo("验证essid:" + essid);
						Utils.sysoInfo("originSsid:" + originSsid);// 如果用originSsid，则只能配置原始wifi热点
						if (!"none".equals(ipaddress)
								&& !TextUtils.isEmpty(ipaddress)
								&& et_wifi_name.getText().toString()
										.equals(essid)) {// 配置失败，需要再次配置？5次限制?
							// 配置了ssid 且 有ip地址
							isVerified = true;
							isCancleLinkRetry = true;// 已经验证过了，恢复网络后，取消重试机会
							// Utils.sysoInfo("发送配置请求,不一定配置成功");
							// sendRequest(RouteApiType.setWirelessWifiForDevice,
							// RouteLibraryParams.setWirelessWifiForDevice(
							// remoteIp, localMac,
							// remoteMac,
							// wifiName, "psk", wifiPwd), false);
							if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
								tv_desc.setText(R.string.setting_send_success_restore_origin_wifi);
							} else {
								tv_desc.setText(getResources().getString(
										R.string.setting_has_finish));// 验证成功，恢复网络
																		// 90
							}
							wifiSetSuccess();

						} else {
							if ((TextUtils.isEmpty(ipaddress) || "none"
									.equals(ipaddress))
									&& !TextUtils.isEmpty(essid)) {
								Utils.sysoInfo("配置设备IP地址失败,请重试!");
							} else if (!TextUtils.isEmpty(ipaddress)
									&& !"none".equals(ipaddress)
									&& TextUtils.isEmpty(essid)) {
								Utils.sysoInfo("配置设备WiFi名称失败,请重试!");
							} else if ((TextUtils.isEmpty(ipaddress) || "none"
									.equals(ipaddress))
									&& TextUtils.isEmpty(essid)) {
								Utils.sysoInfo("配置设备IP地址和名称都失败了");
							}
							isVerified = false;
							isCancleLinkRetry = true;// 已经验证过了，恢复网络后，取消重试机会
							btn_start_linkwifi.setEnabled(true);
							// progressDialog.dismiss();//消失后，切网停顿，体验不好。
							if (originConfig != null) {// 直接切网，暂不消失。相当于wifiSetSuccess
								isChangeWifiSuccess = false;
								wifiAdmin.addNetworkAndLink(originConfig);
							}
						}
					} else {// data为空也要处理，否则会一直等到超时
						isVerified = false;
						isCancleLinkRetry = true;
						btn_start_linkwifi.setEnabled(true);
						if (originConfig != null) {
							isChangeWifiSuccess = false;
							wifiAdmin.addNetworkAndLink(originConfig);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					// 异常也要处理，否则会一直等到超时
					isVerified = false;
					isCancleLinkRetry = true;
					btn_start_linkwifi.setEnabled(true);
					if (originConfig != null) {
						isChangeWifiSuccess = false;
						wifiAdmin.addNetworkAndLink(originConfig);
					}
				}
				break;
			default:
				break;
			}
		} else {
			Utils.sysoInfo("data return false");
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				CustomToast.show(WifiSettingActivity.this,
						R.string.common_retry);
			}
		}
	}

	private void wifiSetSuccess() {
		// Utils.sysoInfo("WiFi配置请求验证成功 开始还原成原来网络");
		if ("lan".equals(getIntent().getExtras().getString("type"))) {
			ICamGlobal.isNeedRefreshDeviceList = true;// 局域网的wifi配置，触发自动刷新，因为配置到其他网络，设备会消失。
			CustomToast.show(WifiSettingActivity.this,
					R.string.setting_wifi_success);
			myHandler.sendEmptyMessageDelayed(0, 200);
			return;
		}
		if (originConfig != null) {
			isChangeWifiSuccess = false;
			wifiAdmin.addNetworkAndLink(originConfig);
		} else {
			// 没有网络配置，直接结束
			WifiSettingActivity.this.finish();
		}

	}

	public static int getSecurity(String result) {
		if (result.contains("WEP")) {
			return 1;
		} else if (result.contains("PSK")) {
			return 2;
		} else if (result.contains("EAP")) {
			return 3;
		}
		return 0;
	}

	private class ComparatorScanResult implements Comparator<ScanResult> {

		@Override
		public int compare(ScanResult lhs, ScanResult rhs) {
			// TODO Auto-generated method stub
			int item1 = calculateSignalLevel(lhs.level, 4);
			int item2 = calculateSignalLevel(rhs.level, 4);
			if (item1 == item2) {
				return 0;
			} else {
				return item1 > item2 ? -1 : 1;
			}
		}
	}

	/** Anything worse than or equal to this will show 0 bars. */
	private static final int MIN_RSSI = -100;

	/** Anything better than or equal to this will show the max bars. */
	private static final int MAX_RSSI = -55;

	private static int calculateSignalLevel(int rssi, int numLevels) {
		if (rssi <= MIN_RSSI) {
			return 0;
		} else if (rssi >= MAX_RSSI) {
			return numLevels - 1;
		} else {
			int partitionSize = (MAX_RSSI - MIN_RSSI) / (numLevels - 1);
			return (rssi - MIN_RSSI) / partitionSize;
		}
	}
}
