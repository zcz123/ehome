/**
 * Project Name:  iCam
 * File Name:     AddDeviceActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年9月2日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.model.UserInfo;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.utils.WifiAdmin;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.help.AddDeviceHelpActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.wifidirect.model.DeviceDescriptionModel;
import com.wulian.icam.wifidirect.utils.XMLHandler;
import com.wulian.lanlibrary.LanController;
import com.wulian.lanlibrary.WulianLANApi;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;

/**
 * @ClassName: SoftApSettingActivity
 * @Function: 软AP模式添加设备
 * @Date: 2015年9月2日
 * @author Yanmin
 * @email min.yan@wuliangroup.cn
 */
public class SoftApSettingActivity extends BaseFragmentActivity implements
		OnClickListener {
	private static final int REQUEST_CONNECT_DEVICE_WIFI = 1;
	private static final int REQUEST_RESTORE_WIFI_MANUAL = 2;
	private static final int STEP_NONE = -1;
	private static final int STEP0 = 0;
	private static final int STEP1 = 1;
	private static final int STEP2 = 2;
	private static final int STEP3 = 3;
	private static final int STEP4 = 4;
	private static final int STEP5 = 5;
	private static final int STEP6 = 6;
	private static final int STEP7 = 7;
	private static final int STEP8 = 8;
	private static final int STEP9 = 9;// Finish

	private static final int MSG_START_CONNECT = 0;
	private static final int MSG_TOAST_CONNECT_WIFI_LONGTIME = 1;
	private static final int MSG_CONNECT_WIFI_TIMEOUT = 2;
	private static final int MSG_RESTORE_WIFI_TIMEOUT = 3;
	private static final int MSG_SEND_BINDING_BIDN = 4;
	private static final int MSG_SEND_GET_CURRENTDEVICE_INFORMATION = 5;
	private static final int MSG_SEND_CHECK_DEVICE_ONLINE = 6;
	private static final int MSG_SEND_SET_WIRELESSWIFI_FORDEVICE = 7;
	private static final int MSG_SEND_START_MULTICAST = 8;
	private static final int MSG_SEND_STOP_MULTICAST = 9;
	private static final int MSG_MULTICAST_TIMEOUT = 10;

	private static final long START_ANIM_DELAY = 1000;
	private static final long DEVICE_INFO_DELAY = 2000;// 获取设备信息延迟
	private static final long START_CONNECT_DELAY = 500;

	private int mCurrentStep = STEP_NONE;

	private ImageView titlebar_back;
	private ImageView iv_config_wifi_step_state;
	private TextView tv_prompt, tv_step1, tv_step2, tv_step3, tv_step4,
			tv_step5, tv_step6, tv_step7, tv_desc1, tv_desc2, tv_desc3,
			tv_desc4, tv_desc5, tv_desc6, tv_desc7;
	private ProgressBar pb_step1, pb_step2, pb_step3, pb_step4, pb_step5,
			pb_step6, pb_step7;
	private ImageView iv_step_result1, iv_step_result2, iv_step_result3,
			iv_step_result4, iv_step_result5, iv_step_result6, iv_step_result7;
	private View head2, head3, head4, head5, head6, head7;
	private Dialog mExitDialog;
	// private Dialog mOauthDialog;
	private View progressView;
	private AnimationDrawable mAnimation;

	private ConfigWiFiInfoModel mConfigInfo;
	private static UserInfo userInfo;

	private int hasRetryTimes = 0;
	// private int whatRequestType = 0; // 用于判断是什么类型的请求发送失败，要重发
	// private String oauthDesc = "";// 授权描述信息
	private String originSsid, localMac, deviceSsid, devicePwd, deviceId,
			remoteMac, remoteIp;
	private WifiConfiguration originConfig;// 原始连接的网络

	private WifiAdmin mWifiAdmin;
	private ConnectivityManager mConnManager;
	private NetConnectChangedReceiver receiver;
	private int mcTimeout = 0;
	private static final int MCTIMEOUT_MAX = 90;

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_CONNECT:
				connectDeviceNetwork();
				break;
			case MSG_TOAST_CONNECT_WIFI_LONGTIME:
				CustomToast.show(SoftApSettingActivity.this,
						R.string.config_device_link_longtime);
				break;
			case MSG_CONNECT_WIFI_TIMEOUT:// WIFI连接超时
				connectDeviceNetworkManually();
				break;
			case MSG_RESTORE_WIFI_TIMEOUT:// WIFI恢复超时
				restoreOriginNetManually();
				break;
			case MSG_SEND_GET_CURRENTDEVICE_INFORMATION:
				sendRequest(RouteApiType.getCurrentDeviceInformation,
						RouteLibraryParams
								.getCurrentDeviceInformation(localMac), false);
				break;
			case MSG_SEND_SET_WIRELESSWIFI_FORDEVICE:
				sendRequest(RouteApiType.setWirelessWifiForDevice,
						RouteLibraryParams.setWirelessWifiForDevice(remoteIp,
								localMac, Utils.deviceIdToMac(deviceId),
								mConfigInfo.getWifiName(),
								mConfigInfo.getSecurity(),
								// wifiAdmin.getEncryption(wifiName),
								mConfigInfo.getWifiPwd()), false);
				break;
			case MSG_SEND_START_MULTICAST:
				sendRequest(RouteApiType.getAllDeviceInformationByMulticast,
						RouteLibraryParams.getAllDeviceInformation(localMac),
						false);
				mcTimeout = 0;
				myHandler.sendEmptyMessageDelayed(MSG_MULTICAST_TIMEOUT, 1000);
				break;
			case MSG_MULTICAST_TIMEOUT:
				mcTimeout++;
				if (mcTimeout > MCTIMEOUT_MAX) {
					configFail();
				} else {
					myHandler.sendEmptyMessageDelayed(MSG_MULTICAST_TIMEOUT,
							1000);
				}
				break;
			case MSG_SEND_STOP_MULTICAST:
				bindDeviceToAccount();
				break;
			case MSG_SEND_BINDING_BIDN:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initListeners();
		initData();
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_softap_device_setting);
	}

	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.config_link_camera);
	}

	private void initViews() {
		titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
		iv_config_wifi_step_state = (ImageView) findViewById(R.id.iv_config_wifi_step_state);
		mAnimation = (AnimationDrawable) iv_config_wifi_step_state
				.getDrawable();
		progressView = findViewById(R.id.rl_config_wifi_step);
		tv_prompt = ((TextView) findViewById(R.id.tv_prompt));
		tv_step1 = (TextView) progressView.findViewById(R.id.tv_step1);
		tv_step2 = (TextView) progressView.findViewById(R.id.tv_step2);
		tv_step3 = (TextView) progressView.findViewById(R.id.tv_step3);
		tv_step4 = (TextView) progressView.findViewById(R.id.tv_step4);
		tv_step5 = (TextView) progressView.findViewById(R.id.tv_step5);
		tv_step6 = (TextView) progressView.findViewById(R.id.tv_step6);
		tv_step7 = (TextView) progressView.findViewById(R.id.tv_step7);

		tv_desc1 = (TextView) progressView.findViewById(R.id.tv_desc1);
		tv_desc2 = (TextView) progressView.findViewById(R.id.tv_desc2);
		tv_desc3 = (TextView) progressView.findViewById(R.id.tv_desc3);
		tv_desc4 = (TextView) progressView.findViewById(R.id.tv_desc4);
		tv_desc5 = (TextView) progressView.findViewById(R.id.tv_desc5);
		tv_desc6 = (TextView) progressView.findViewById(R.id.tv_desc6);
		tv_desc7 = (TextView) progressView.findViewById(R.id.tv_desc7);

		pb_step1 = (ProgressBar) progressView.findViewById(R.id.pb_step1);
		pb_step2 = (ProgressBar) progressView.findViewById(R.id.pb_step2);
		pb_step3 = (ProgressBar) progressView.findViewById(R.id.pb_step3);
		pb_step4 = (ProgressBar) progressView.findViewById(R.id.pb_step4);
		pb_step5 = (ProgressBar) progressView.findViewById(R.id.pb_step5);
		pb_step6 = (ProgressBar) progressView.findViewById(R.id.pb_step6);
		pb_step7 = (ProgressBar) progressView.findViewById(R.id.pb_step7);

		iv_step_result1 = (ImageView) progressView
				.findViewById(R.id.iv_step_result1);
		iv_step_result2 = (ImageView) progressView
				.findViewById(R.id.iv_step_result2);
		iv_step_result3 = (ImageView) progressView
				.findViewById(R.id.iv_step_result3);
		iv_step_result4 = (ImageView) progressView
				.findViewById(R.id.iv_step_result4);
		iv_step_result5 = (ImageView) progressView
				.findViewById(R.id.iv_step_result5);
		iv_step_result6 = (ImageView) progressView
				.findViewById(R.id.iv_step_result6);
		iv_step_result7 = (ImageView) progressView
				.findViewById(R.id.iv_step_result7);
		head2 = progressView.findViewById(R.id.head2);
		head3 = progressView.findViewById(R.id.head3);
		head4 = progressView.findViewById(R.id.head4);
		head5 = progressView.findViewById(R.id.head5);
		head6 = progressView.findViewById(R.id.head6);
		head7 = progressView.findViewById(R.id.head7);
	}

	private void startStep(int step) {
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
			resultStep(STEP1, true);
			break;
		case STEP3:
			head3.setVisibility(View.VISIBLE);
			tv_step3.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc3.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step3.setVisibility(View.VISIBLE);
			iv_step_result3.setVisibility(View.GONE);
			resultStep(STEP2, true);
			break;
		case STEP4:
			head4.setVisibility(View.VISIBLE);
			tv_step4.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc4.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step4.setVisibility(View.VISIBLE);
			iv_step_result4.setVisibility(View.GONE);
			resultStep(STEP3, true);
			break;
		case STEP5:
			head5.setVisibility(View.VISIBLE);
			tv_step5.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc5.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step5.setVisibility(View.VISIBLE);
			iv_step_result5.setVisibility(View.GONE);
			resultStep(STEP4, true);
			break;
		case STEP7:
			head6.setVisibility(View.VISIBLE);
			tv_step6.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc6.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step6.setVisibility(View.VISIBLE);
			iv_step_result6.setVisibility(View.GONE);
			resultStep(STEP5, true);
			break;
		case STEP8:
			head7.setVisibility(View.VISIBLE);
			tv_step7.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc7.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step7.setVisibility(View.VISIBLE);
			iv_step_result7.setVisibility(View.GONE);
			resultStep(STEP7, true);
			break;
		default:
			break;
		}

	}

	private void resultStep(int step, boolean isSuccess) {
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
		case STEP5:
			head5.setVisibility(View.VISIBLE);
			tv_step5.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc5.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step5.setVisibility(View.GONE);
			iv_step_result5.setVisibility(View.VISIBLE);
			if (isSuccess) {
				iv_step_result5.setImageResource(R.drawable.pb_finish_success);
			} else {
				iv_step_result5.setImageResource(R.drawable.pb_finish_fail);
			}

		case STEP7:
			head6.setVisibility(View.VISIBLE);
			tv_step6.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc6.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step6.setVisibility(View.GONE);
			iv_step_result6.setVisibility(View.VISIBLE);
			if (isSuccess) {
				iv_step_result6.setImageResource(R.drawable.pb_finish_success);
			} else {
				iv_step_result6.setImageResource(R.drawable.pb_finish_fail);
			}
			break;

		case STEP8:
			head7.setVisibility(View.VISIBLE);
			tv_step7.setBackgroundResource(R.drawable.shape_theme_circle);
			tv_desc7.setTextColor(getResources().getColor(
					R.color.wifi_setting_theme));
			pb_step7.setVisibility(View.GONE);
			iv_step_result7.setVisibility(View.VISIBLE);
			if (isSuccess) {
				iv_step_result7.setImageResource(R.drawable.pb_finish_success);
			} else {
				iv_step_result7.setImageResource(R.drawable.pb_finish_fail);
			}
			break;
		default:
			break;
		}

	}

	private void initListeners() {
		titlebar_back.setOnClickListener(this);
	}

	private void initData() {
		Bundle bd = getIntent().getExtras();
		mConfigInfo = bd.getParcelable("configInfo");
		deviceId = mConfigInfo.getDeviceId();
		Log.e("initData", deviceId);
		if (deviceId == null || deviceId.length() != 20) {
			CustomToast.show(this, R.string.config_error_deviceid);
			finish();
		}

		userInfo = app.getUserinfo();
		mConnManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifiAdmin = new WifiAdmin(this);

		// mWifiAdmin.openWifi();
		localMac = mWifiAdmin.getCurrentWifiInfo().getMacAddress();
		String ssid = mWifiAdmin.getCurrentWifiInfo().getSSID();
		Utils.sysoInfo("originSsid:" + ssid + ";originState:"
				+ mWifiAdmin.getCurrentWifiInfo().getSupplicantState());
		if (!TextUtils.isEmpty(ssid) && !"<unknown ssid>".equals(ssid)) {
			originSsid = ssid.replace("\"", "");
			originConfig = mWifiAdmin.getConfiguredNetwork(originSsid);
		}

		deviceSsid = APPConfig.DEVICE_WIFI_SSID_PREFIX
				+ deviceId.subSequence(16, 20).toString()
						.toUpperCase(Locale.ENGLISH);
		devicePwd = LanController.getFourStringPassword(deviceId
				.subSequence(16, 20).toString().toUpperCase(Locale.ENGLISH));
		remoteMac = Utils.deviceIdToMac(deviceId);

		// Start Connect after 500ms
		myHandler.sendEmptyMessageDelayed(MSG_START_CONNECT,
				START_CONNECT_DELAY);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Utils.sysoInfo("onStart registerReceiver");

		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		// filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		if (receiver == null) {
			receiver = new NetConnectChangedReceiver();
		}
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onRestart() {
		Utils.sysoInfo("onRestart");
		super.onRestart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Utils.sysoInfo("onStop unregtisterReceiver");
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}

	protected void onResume() {
		Utils.sysoInfo("onResume");
		super.onResume();
		mDrawHandler.postDelayed(mRunnable, START_ANIM_DELAY);
	};

	@Override
	protected void onPause() {
		super.onPause();
		stopAnimation(mAnimation);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		myHandler.removeMessages(MSG_START_CONNECT);
		myHandler.removeMessages(MSG_SEND_BINDING_BIDN);
		myHandler.removeMessages(MSG_SEND_GET_CURRENTDEVICE_INFORMATION);
		myHandler.removeMessages(MSG_SEND_CHECK_DEVICE_ONLINE);
		myHandler.removeMessages(MSG_TOAST_CONNECT_WIFI_LONGTIME);
		myHandler.removeMessages(MSG_RESTORE_WIFI_TIMEOUT);
		myHandler.removeMessages(MSG_CONNECT_WIFI_TIMEOUT);
		myHandler.removeMessages(MSG_SEND_SET_WIRELESSWIFI_FORDEVICE);
		myHandler.removeMessages(MSG_MULTICAST_TIMEOUT);

		restoreOriginWiFi();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			showExitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			showExitDialog();
		} else if (id == R.id.titlebar_help) {
			startActivity(new Intent(this, AddDeviceHelpActivity.class));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Utils.sysoInfo("onActivityResult");
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_WIFI:
			if (RESULT_OK == resultCode) {
				if (mCurrentStep == STEP1) {
					if (checkWifi(deviceSsid)) {
						getCurrentDeviceInformation();
					} else {
						configFail();
					}
				}
			} else {
				configFail();
			}
		case REQUEST_RESTORE_WIFI_MANUAL:
			if (RESULT_OK == resultCode) {
				if (mCurrentStep == STEP5) {
					if (checkWifi(originSsid)) {
						startMultcast();
						// bindDeviceToAccount();
					} else {
						configFail();
					}
				}
			} else {
				configFail();
			}
		default:
			break;
		}
	}

	@Override
	protected void DataReturn(boolean success, RouteApiType apiType, String json) {
		super.DataReturn(success, apiType, json);
		if (success) {
			Utils.sysoInfo("apiType = " + apiType + " Success data=" + json);
			switch (apiType) {
//			case V3_BIND_UNBIND:
//				checkDeviceOnline(json);
//				break;
			case getCurrentDeviceInformation:
				try {
					JSONObject jsonObj = new JSONObject(json);
					String data = jsonObj.optString("data");
					if (!TextUtils.isEmpty(data)) {
						getWirelessWifiConnectInformationForDevice(jsonObj);
					} else {
						getCurrentDeviceInformationFail();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					getCurrentDeviceInformationFail();
				}
				break;
			case getAllDeviceInformationByMulticast:
				Utils.sysoInfo("getAllDeviceInformationByMulticast json = "
						+ json);
				boolean result = false;
				List<DeviceDescriptionModel> tempDeviceDesList = XMLHandler
						.getDeviceList(json);
				for (DeviceDescriptionModel item : tempDeviceDesList) {
					String d_id = item.getSipaccount();
					int start = d_id.indexOf("cmic");
					int end = d_id.indexOf("@");
					d_id = d_id.substring(start, end);
					if (d_id.equalsIgnoreCase(deviceId)) {
						result = true;
						break;
					}
				}
				if (result) {
					myHandler.sendEmptyMessage(MSG_SEND_STOP_MULTICAST);
				}
				break;
			case setWirelessWifiForDevice:
				restoreOriginNet();
				break;
			default:
				break;
			}
		} else {
			Utils.sysoInfo("apiType = " + apiType + " Fail data=" + json);
			switch (apiType) {
//			case BINDING_BIND:
//				bindDeviceToAccountFail();
//				break;
			case getCurrentDeviceInformation:
				getCurrentDeviceInformationFail();
				break;
			case setWirelessWifiForDevice:
				setWirelessWifiForDeviceFail();
				break;
			default:
				break;
			}
		}
	}

	private class NetConnectChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
					.getAction())) {
				NetworkInfo networkInfo = (NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

				if ((mCurrentStep == STEP1 || mCurrentStep == STEP5)
						&& networkInfo.isConnected()
						&& networkInfo.isAvailable()) {
					WifiInfo wInfo = intent
							.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);// api14

					if (deviceSsid != null
							&& wInfo.getSSID() != null
							&& deviceSsid.equals(wInfo.getSSID().replace("\"",
									"")) && (mCurrentStep == STEP1)) {
						getCurrentDeviceInformation();
					} else if (originSsid != null
							&& wInfo.getSSID() != null
							&& originSsid.equals(wInfo.getSSID().replace("\"",
									"")) && (mCurrentStep == STEP5)) {

						startMultcast();
						// bindDeviceToAccount();
					}
				}
			}
		}
	}

	protected void startAnimation(final AnimationDrawable animation) {
		if (animation != null && !animation.isRunning()) {
			animation.run();
		}
	}

	protected void stopAnimation(final AnimationDrawable animation) {
		if (animation != null && animation.isRunning())
			animation.stop();
	}

	private Handler mDrawHandler = new Handler();

	private Runnable mRunnable = new Runnable() {
		public void run() {
			startAnimation(mAnimation);
		}
	};

	private void showExitDialog() {
		Resources rs = getResources();
		mExitDialog = DialogUtils.showCommonDialog(this, true,
				rs.getString(R.string.common_tip),
				rs.getString(R.string.config_is_exit_current_config),
				rs.getString(R.string.config_exit),
				rs.getString(R.string.common_cancel), new OnClickListener() {

					@Override
					public void onClick(View v) {
						int id = v.getId();
						if (id == R.id.btn_positive) {
							mExitDialog.dismiss();
							finish();
						} else if (id == R.id.btn_negative) {
							mExitDialog.dismiss();
						}
					}
				});
	}

	/**
	 * @Function 第1步：开始连接设备网络(WIFI)
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void connectDeviceNetwork() {
		NetworkInfo mobileInfo = mConnManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		Utils.sysoInfo("移动网络:" + mobileInfo);
		if (mobileInfo == null || !mobileInfo.isConnected()) {// 移动网络未连接，此时则为wifi模式
			// 确保绑定设备时， 先连接上一个有效wifi
			if (TextUtils.isEmpty(originSsid)) {
				CustomToast.show(this, R.string.config_link_valid_wifi);
				configFail();
				return;
			}
			if (originSsid != null && originSsid.equals(deviceSsid)) {
				CustomToast.show(this, R.string.error_wifi_change_wifi);
				configFail();
				return;
			}
		}

		// 尝试自动连接
		if (mWifiAdmin.addNetworkAndLink(mWifiAdmin.createWifiConfiguration(
				deviceSsid, devicePwd, WifiAdmin.TYPE_WPA))) {
			myHandler.sendEmptyMessageDelayed(MSG_TOAST_CONNECT_WIFI_LONGTIME,
					APPConfig.LINK_TIME_OUT);// 连接提醒
			myHandler.sendEmptyMessageDelayed(MSG_CONNECT_WIFI_TIMEOUT,
					APPConfig.WIFI_TIME_OUT);// 超时//
		} else {
			connectDeviceNetworkManually();
			return;
		}

		// Update View
		tv_prompt.setText(getResources().getString(
				R.string.config_linking_to_camera));
		startStep(STEP1);
		mCurrentStep = STEP1;
		hasRetryTimes = 0;
		Utils.sysoInfo("1. connectDeviceNetwork");
	}

	/**
	 * @Function 第1.1步：连接设备网络(WIFI)错误后重试
	 * @Premise 连接设备网络超时
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void connectDeviceNetworkFail() {
		// connectDeviceNetworkManually();
		if (hasRetryTimes < APPConfig.LINK_RETRAY_TIMES) {
			hasRetryTimes++;
			Utils.sysoInfo("连接设备Wifi，重试" + hasRetryTimes);

			if (mWifiAdmin.addNetworkAndLink(mWifiAdmin
					.createWifiConfiguration(deviceSsid, devicePwd,
							WifiAdmin.TYPE_WPA))) {
				myHandler.sendEmptyMessageDelayed(MSG_CONNECT_WIFI_TIMEOUT,
						APPConfig.WIFI_TIME_OUT);
			} else {
				connectDeviceNetworkManually();
			}
		} else {
			connectDeviceNetworkManually();
		}
	}

	/**
	 * @Function 第1.2步：用户手动连接设备网络
	 * @Premise 无法自动连接设备网络
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void connectDeviceNetworkManually() {
		Utils.sysoInfo("connectDeviceNetworkManually");
		myHandler.removeMessages(MSG_TOAST_CONNECT_WIFI_LONGTIME);
		myHandler.removeMessages(MSG_CONNECT_WIFI_TIMEOUT);

		Intent it = new Intent(this, ConnectDeviceWifiGuideActivity.class);
		it.putExtra("configInfo", mConfigInfo);
		it.putExtra("deviceSsid", deviceSsid);
		it.putExtra("devicePwd", devicePwd);
		startActivityForResult(it, REQUEST_CONNECT_DEVICE_WIFI);
	}

	/**
	 * @Function 第2步：获取当前设备信息
	 * @Premise 已连接上了设备Wifi
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void getCurrentDeviceInformation() {
		myHandler.removeMessages(MSG_TOAST_CONNECT_WIFI_LONGTIME);
		myHandler.removeMessages(MSG_CONNECT_WIFI_TIMEOUT);

		String localIp = mWifiAdmin.getLocalIpAddress();
		Utils.sysoInfo("state:"
				+ mWifiAdmin.getCurrentWifiInfo().getSupplicantState()
				+ " localIp:" + localIp);
		LanController.setLocalIpV4(localIp);
		myHandler.sendEmptyMessageDelayed(
				MSG_SEND_GET_CURRENTDEVICE_INFORMATION, DEVICE_INFO_DELAY);

		// Update View
		tv_prompt.setText(R.string.setting_link_camera_success_get_remoteip);
		mCurrentStep = STEP2;
		startStep(STEP2);
		hasRetryTimes = 0;
		Utils.sysoInfo("2. getCurrentDeviceInformation");
	}

	/**
	 * @Function 第2.1步：获取当前设备信息失败后重试
	 * @Premise 获取当前设备信息失败
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void getCurrentDeviceInformationFail() {
		if (hasRetryTimes < APPConfig.REMOTEIP_RETRAY_TIMES) {
			// 获取失败，重新请求
			String localIp = mWifiAdmin.getLocalIpAddress();
			LanController.setLocalIpV4(localIp);
			Utils.sysoInfo("state:"
					+ mWifiAdmin.getCurrentWifiInfo().getSupplicantState()
					+ " localIp:" + localIp);
			myHandler.sendEmptyMessage(MSG_SEND_GET_CURRENTDEVICE_INFORMATION);
			hasRetryTimes++;

			if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
				tv_prompt
						.setText(getResources().getString(
								R.string.setting_parse_ip_error_trying)
								+ hasRetryTimes
								+ getResources().getString(
										R.string.config_retry_times));
			}
		} else {
			configFail();
		}
	}

	/**
	 * @Function 第3步：获取当前设备Wifi配置信息
	 * @Premise 已收到getcurrentdeviceinformation消息响应
	 * @author Yanmin
	 * @throws JSONException
	 * @date 2015年09月06日
	 */
	private void getWirelessWifiConnectInformationForDevice(JSONObject jsonObj)
			throws JSONException {
		myHandler.removeMessages(MSG_SEND_GET_CURRENTDEVICE_INFORMATION);

		JSONObject dataJson = jsonObj.getJSONArray("data").getJSONObject(0);
		remoteIp = dataJson.optString("ip");
		String sipAccount = Utils.getParamFromXml(dataJson.optString("item"),
				"sipaccount");
		Utils.sysoInfo("Get Remote ip:" + remoteIp + ",sipAccount:"
				+ sipAccount);

		if (sipAccount.contains(deviceId)) {
			// 不管之前设备有没有配置过Wifi，这里直接配置Wifi
			// sendRequest(
			// RouteApiType.getWirelessWifiConnectInformationForDevice,
			// RouteLibraryParams
			// .getWirelessWifiConnectInformationForDevice(
			// remoteIp, localMac, remoteMac), false);
			setWirelessWifiForDevice();
		} else {
			Utils.sysoInfo("sipAccount and deviceId are not matched");
			CustomToast.show(this, R.string.config_device_not_match_scan);
			configFail();
			return;
		}

		// Update View
		// tv_prompt.setText(R.string.getip_success_get_linkinfo);
		startStep(STEP3);
		mCurrentStep = STEP3;
		hasRetryTimes = 0;
		Utils.sysoInfo("3. getWirelessWifiConnectInformationForDevice");
	}

	/**
	 * @Function 第3.1步：获取当前设备信息失败后重试
	 * @Premise 获取当前设备Wifi信息失败
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void getWirelessWifiConnectInformationForDeviceFail() {
		if (hasRetryTimes < APPConfig.REMOTEIP_RETRAY_TIMES) {
			sendRequest(
					RouteApiType.getWirelessWifiConnectInformationForDevice,
					RouteLibraryParams
							.getWirelessWifiConnectInformationForDevice(
									remoteIp, localMac, remoteMac), false);
			hasRetryTimes++;

			if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
				tv_prompt
						.setText(getResources().getString(
								R.string.setting_parse_ip_error_trying)
								+ hasRetryTimes
								+ getResources().getString(
										R.string.config_retry_times));
			}
		} else {
			configFail();
		}
	}

	/**
	 * @Function 第4步：为设备配置wifi
	 * @Premise 已获取当前设备Wifi信息
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void setWirelessWifiForDevice() {
		myHandler.sendEmptyMessage(MSG_SEND_SET_WIRELESSWIFI_FORDEVICE);

		// Update View
		tv_prompt.setText(R.string.config_get_remoteip_success_setting_wifi);
		startStep(STEP4);
		mCurrentStep = STEP4;
		hasRetryTimes = 0;
		Utils.sysoInfo("4. setWirelessWifiForDevice");
	}

	/**
	 * @Function 第4.1步：为设备配置wifishi
	 * @Premise 已获取当前设备Wifi信息
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void setWirelessWifiForDeviceFail() {
		if (hasRetryTimes < APPConfig.REMOTEIP_RETRAY_TIMES) {
			myHandler.sendEmptyMessage(MSG_SEND_SET_WIRELESSWIFI_FORDEVICE);
			hasRetryTimes++;
		} else {
			configFail();
		}
	}

	/**
	 * @Function 第5步：恢复手机原网络
	 * @Premise 已配置设备的wifi成功
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void restoreOriginNet() {
		if (restoreOriginWiFi()) {
			Utils.sysoInfo("5. restoreOriginNet");
			myHandler.sendEmptyMessageDelayed(MSG_RESTORE_WIFI_TIMEOUT,
					APPConfig.WIFI_TIME_OUT);
		} else {
			restoreOriginNetManually();
		}

		// Update View
		tv_prompt.setText(R.string.setting_send_success_restore_origin_wifi);
		startStep(STEP5);
		mCurrentStep = STEP5;
		hasRetryTimes = 0;
	}

	/**
	 * @Function 第5.1步：恢复手机原网络失败后手动切换
	 * @Premise 恢复手机原网络失败
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void restoreOriginNetManually() {
		Utils.sysoInfo("5.1 restoreOriginNetManually");

		Intent it = new Intent(this, RestoreOriginWifiGuideActivity.class);
		it.putExtra("ssid", originSsid);
		startActivityForResult(it, REQUEST_RESTORE_WIFI_MANUAL);
	}

	/**
	 * @Function 第6步：组播验证Wifi配置是否成功
	 * @Premise 已恢复手机原网络
	 * @author Yanmin
	 * @date 2015年10月12日
	 */
	private void startMultcast() {
		myHandler.removeMessages(MSG_RESTORE_WIFI_TIMEOUT);// 移除超时提醒
		myHandler.sendEmptyMessageDelayed(MSG_SEND_START_MULTICAST,
				DEVICE_INFO_DELAY);

		// Update View
		tv_prompt.setText(R.string.config_verifiy_wifi_info);
		startStep(STEP6);
		mCurrentStep = STEP6;
		hasRetryTimes = 0;
		Utils.sysoInfo("6. startMultcast");
	}

	private void stopMultcast() {
		if (mCurrentStep == STEP6) {
			LanController.stopRequest();
		}
	}

	/**
	 * @Function 第7步：绑定账号和设备
	 * @Premise 已恢复到原网络下
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void bindDeviceToAccount() {
		Utils.sysoInfo("7. bindDeviceToAccount");
		// myHandler.removeMessages(MSG_RESTORE_WIFI_TIMEOUT);// 移除超时提醒

		stopMultcast();

		if (!mConfigInfo.isAddDevice()) {
			configSuccess();
			return;
		}

		myHandler.sendEmptyMessageDelayed(MSG_SEND_BINDING_BIDN,
				DEVICE_INFO_DELAY);

		// Update View
		tv_prompt.setText(R.string.config_binding_to_your_account);
		startStep(STEP7);
		mCurrentStep = STEP7;
		hasRetryTimes = 0;
	}

	/**
	 * @Function 第7.1步：绑定账号和设备失败后重试
	 * @Premise 绑定账号和设备失败
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void bindDeviceToAccountFail() {
		if (hasRetryTimes < APPConfig.REMOTEIP_RETRAY_TIMES) {
			myHandler.sendEmptyMessage(MSG_SEND_BINDING_BIDN);
			hasRetryTimes++;
		} else {
			configFail();
		}
	}

	/**
	 * @Function 第8步：检查设备是否在线
	 * @Premise 已绑定设备和账号
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void checkDeviceOnline(String data) {
		ICamGlobal.isNeedRefreshDeviceList = true;
		// myHandler.sendEmptyMessage(MSG_SEND_CHECK_DEVICE_ONLINE);
		// Utils.sysoInfo("8. checkDeviceOnline");

		// Update View
		// CustomToast.show(getApplicationContext(),
		// R.string.device_bind_success);// 全局通知，避免被后面的土司冲掉
		tv_prompt.setText(R.string.config_device_bind_success);
		startStep(STEP8);
		mCurrentStep = STEP8;
		hasRetryTimes = 0;
		configSuccess();
	}

	/**
	 * @Function 第8.1步：检查设备是否在线失败重试
	 * @Premise 检查设备是否在线失败
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void checkDeviceOnlineFail() {
		Utils.sysoInfo("8.1. checkDeviceOnlineFail");
		if (hasRetryTimes < APPConfig.REMOTEIP_RETRAY_TIMES) {
			hasRetryTimes++;
			myHandler.sendEmptyMessageDelayed(MSG_SEND_CHECK_DEVICE_ONLINE,
					1000);
		} else {
			configSuccess();
		}
	}

	/**
	 * @Function 第9步：配置成功
	 * @Premise 设备已上线
	 * @author Yanmin
	 * @date 2015年09月06日
	 */
	private void configSuccess() {
		Utils.sysoInfo("9. configSuccess");

		// Update View
		// CustomToast.show(getApplicationContext(),
		// R.string.device_bind_success);// 全局通知，避免被后面的土司冲掉
		tv_prompt.setText(R.string.config_device_bind_success);
		resultStep(STEP8, true);
		mCurrentStep = STEP9;
		hasRetryTimes = 0;

		Intent it = new Intent();
		it.putExtra("configInfo", mConfigInfo);
		it.setClass(this, DeviceConfigSuccessActivity.class);
		startActivity(it);
		finish();
	}

	/**
	 * @Function 第8.1步：配置失败
	 * @Premise 过程出现无法复原的错误，配置失败
	 * @author Yanmin
	 * @date 2015年09月07日
	 */
	private void configFail() {
		if (mCurrentStep > STEP0 && mCurrentStep < STEP6) {
			restoreOriginWiFi();
		}

		stopMultcast();

		Intent it = new Intent();
		it.setClass(this, DeviceConfigFailResultActivity.class);
		it.putExtra("configInfo", mConfigInfo);
		startActivity(it);
		finish();
	}

	private boolean restoreOriginWiFi() {
		mWifiAdmin.removeConfiguredNetwork(deviceSsid);
		String ssid = mWifiAdmin.getCurrentWifiInfo().getSSID();
		if (!TextUtils.isEmpty(ssid)
				&& !ssid.replace("\"", "").equals(originSsid)) {// 检查一次wifi还原情况
			Utils.sysoInfo("开始还原原网络 " + originSsid);
			if (mWifiAdmin.addNetworkAndLink(originConfig))
				return true;
		}
		return false;
	}

	private boolean checkWifi(String ssid) {
		if (!mWifiAdmin.isWiFiEnabled())
			return false;

		String curSsid = mWifiAdmin.getCurrentWifiInfo().getSSID();
		if (curSsid != null)
			curSsid = curSsid.replace("\"", "");

		if (!TextUtils.isEmpty(curSsid) && curSsid.equals(ssid))
			return true;

		return false;
	}
}
