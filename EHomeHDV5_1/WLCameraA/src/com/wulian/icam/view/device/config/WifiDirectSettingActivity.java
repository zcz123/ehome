/**
 * Project Name:  iCam
 * File Name:     DeviceInfoSettingActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年6月30日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.realtek.simpleconfiglib.SCLibrary;
import com.realtek.simpleconfiglib.wulian.SimpleConfigController;
import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.CheckBind;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.wifidirect.config.SCParams;
import com.wulian.icam.wifidirect.config.WiFiSetting;
import com.wulian.icam.wifidirect.model.DeviceDescriptionModel;
import com.wulian.icam.wifidirect.utils.DirectUtils;
import com.wulian.icam.wifidirect.utils.WiFiLinker;
import com.wulian.icam.wifidirect.utils.XMLHandler;
import com.wulian.lanlibrary.LanController;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;

/**
 * @ClassName: DeviceInfoSettingActivity
 * @Function: TODO
 * @Date: 2015年6月30日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WifiDirectSettingActivity extends BaseFragmentActivity implements
		OnClickListener {
	private static final int STEP1 = 1;
	private static final int STEP2 = 2;
	private static final int STEP3 = 3;
	private static final int MSG_STOP_SCLIB = 1;
	private static final int MSG_UPDATE_VIEW = 3;

	private ImageView iv_oval_left_device;
	private ImageView iv_oval_rigth_device;
	private ImageView iv_config_wifi_step_state;
	private RelativeLayout rl_step_1;
	private RelativeLayout rl_step_2;
	private RelativeLayout rl_step_3;
	private TextView tv_prompt;

	private TextView tv_left_draw_1;
	private TextView tv_left_draw_2;
	private TextView tv_left_draw_3;

	private TextView tv_center_name_1;
	private TextView tv_center_name_2;
	private TextView tv_center_name_3;

	private TextView tv_right_draw_1;
	private TextView tv_right_draw_2;
	private TextView tv_right_draw_3;

	private Dialog mExitDialog;

	private String originDeviceId;
	private String wifipwd;
	private String bssid;
	private String wifiname;
	private int configWiFiType;
	private boolean bAddDevice;
	private ConfigWiFiInfoModel mInfoData;
	private int mCurrentStep;
	private int hasRetryTimes = 0;

	private SimpleConfigController mSimpleConfigController;

	private WiFiLinker mWiFiLinker;
	private boolean isInitRtk = false;
	private boolean bStartMultcase = false;
	private int mCurrentNum;
	// private boolean isFromResult;
	private AnimationDrawable mAnimation;
	private static final long START_DELAY = 1000;

	protected void onCreate(android.os.Bundle arg0) {
		super.onCreate(arg0);
		initView();
		setListener();
		initData();
	};

	@Override
	protected void onResume() {
		super.onResume();
		mDrawHandler.postDelayed(mRunnable, START_DELAY);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopAnimation(mAnimation);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy(); 

		stopSCLib();
		stopMultcase();

		myHandler.removeMessages(MSG_STOP_SCLIB);
		myHandler.removeMessages(MSG_UPDATE_VIEW);
		countDownHandler.removeMessages(STEP1);
		countDownHandler.removeMessages(STEP2);
		countDownHandler.removeMessages(STEP3);
	}

	private void initView() {
		tv_prompt = ((TextView) findViewById(R.id.tv_prompt));
		iv_oval_left_device = (ImageView) findViewById(R.id.iv_oval_left_device);
		iv_oval_rigth_device = (ImageView) findViewById(R.id.iv_oval_rigth_device);
		iv_config_wifi_step_state = (ImageView) findViewById(R.id.iv_config_wifi_step_state);

		initStepView();
	}

	private void initData() {
		isInitRtk = false;
		bStartMultcase = false;

		Bundle bd = getIntent().getExtras();
		mInfoData = bd.getParcelable("configInfo");
		if (mInfoData == null) {
			this.finish();
			return;
		}

		bAddDevice = mInfoData.isAddDevice();
		originDeviceId = mInfoData.getDeviceId();
		wifipwd = mInfoData.getWifiPwd();
		wifiname = mInfoData.getWifiName();
		bssid = mInfoData.getBssid();

		configWiFiType = mInfoData.getConfigWiFiType();
		if (TextUtils.isEmpty(originDeviceId)) {
			this.finish();
			return;
		}

		mSimpleConfigController = new SimpleConfigController();

		mWiFiLinker = new WiFiLinker();
		mWiFiLinker.WifiInit(this);

		handleDevice();
		mAnimation = (AnimationDrawable) iv_config_wifi_step_state
				.getDrawable();

		mCurrentStep = STEP1;
		// if (mInfoData.getRetry() > 0)
		// mCurrentStep = STEP2;

		if (configWiFiType == iCamConstants.CONFIG_BARCODE_WIFI_SETTING)
			mCurrentStep = STEP2;

		if (mCurrentStep == STEP1) {
			System.out.println("------>wifi直连");
			startWifiDirect();
		} else if (mCurrentStep == STEP2) {
			startMultcast();
		}
	}

	/**
	 * @Function 第1步：Wifi Direct, 30s后自动关闭
	 * @author Yanmin
	 * @date 2015年09月15日
	 */
	private void startWifiDirect() {
		Utils.sysoInfo("startWifiDirect");
		startSCLib();

		myHandler.sendEmptyMessage(MSG_UPDATE_VIEW);
		mCurrentNum = 0;
		countDownHandler.sendEmptyMessage(STEP1);
	}

	/**
	 * @Function 第2步：组播, 已收到信息为成功，60s后为收到即失败
	 * @author Yanmin
	 * @date 2015年09月15日
	 */
	private void startMultcast() {
		Utils.sysoInfo("startMultcast");
		mCurrentStep = STEP2;

		if (mWiFiLinker.isWiFiEnable()) {
			String localMac = "";
			WifiInfo wifiInfo = mWiFiLinker.getWifiInfo();
			if (wifiInfo != null) {
				localMac = wifiInfo.getMacAddress();
				if (!TextUtils.isEmpty(localMac)) {
					Utils.sysoInfo("Send getAllDeviceInformationByMulticast");
					sendRequest(
							RouteApiType.getAllDeviceInformationByMulticast,
							RouteLibraryParams
									.getAllDeviceInformation(localMac), false);
					bStartMultcase = true;
				} else {
					jumpToResult(false);
				}
			} else {
				jumpToResult(false);
			}
		} else {
			jumpToResult(false);
		}

		myHandler.sendEmptyMessage(MSG_UPDATE_VIEW);
		mCurrentNum = 0;
		countDownHandler.sendEmptyMessage(STEP2);
	}

	private void stopMultcase() {
		if (bStartMultcase) {
			LanController.stopRequest();
			bStartMultcase = false;
		}
	}

	/**
	 * @Function 第3步：绑定账号和设备
	 * @author Yanmin
	 * @date 2015年09月15日
	 */
	private void bindDeviceToAccount() {
		Utils.sysoInfo("bindDeviceToAccount");
		if (mCurrentStep != STEP2)
			return;

		mCurrentStep = STEP3;
		countDownHandler.removeMessages(STEP2);

		stopMultcase();

//		sendRequest(RouteApiType.BINDING_BIDN, RouteLibraryParams.BindingBidn(userInfo.getAuth(), originDeviceId), false);

		// sendRequest(RouteApiType.BINDING_CHECK,
		// RouteLibraryParams.BindingCheck(
		// V2DeviceInfoSettingActivity.this,
		// originDeviceId), false);

		myHandler.sendEmptyMessage(MSG_UPDATE_VIEW);
		hasRetryTimes = 0;
	}

	/**
	 * @Function 第3.1步：绑定账号和设备失败后重试
	 * @Premise 绑定账号和设备失败
	 * @author Yanmin
	 * @date 2015年09月15日
	 */
	private void bindDeviceToAccountFail() {
		if (hasRetryTimes < APPConfig.REMOTEIP_RETRAY_TIMES) {
//			sendRequest(RouteApiType.BINDING_BIDN,
//					RouteLibraryParams.BindingBidn(userInfo.getAuth(),
//							originDeviceId), false);
			hasRetryTimes++;
		} else {
			jumpToResult(false);
		}
	}
	
	private void startSCLib() {
		if (isInitRtk)
			return;
		mSimpleConfigController.initData(this);
		mSimpleConfigController.StartConfig(wifiname, wifipwd, bssid);
		isInitRtk = true;
	}

	private void stopSCLib() {
		if (isInitRtk) {
			mSimpleConfigController.stopConfig();
		}
	}

	private void destroySCLib() {
		if (isInitRtk) {
			mSimpleConfigController.DestroyData();
			isInitRtk = false;
		}
	}

	private void handleDevice() {
		DeviceType type = DeviceType.getDevivceTypeByDeviceID(originDeviceId);
		switch (type) {
		case INDOOR:
		case OUTDOOR:
			iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_4);
			break;
		case SIMPLE:
		case SIMPLE_N:
			iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_3);
			break;
		case INDOOR2:
			iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_4);
			break;
		case DESKTOP_C:
			iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_3);
			break;
		default:
			showMsg(R.string.config_not_support_device);
			this.finish();
			break;
		}

		if (!bAddDevice) {
			rl_step_2.setVisibility(View.INVISIBLE);
			tv_left_draw_3.setText("2");
			tv_center_name_3.setText(R.string.config_search_wifi);
		} else {
			rl_step_3.setVisibility(View.VISIBLE);
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

	private Handler countDownHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STEP1:
				tv_right_draw_1.setText(""
						+ (APPConfig.WIFI_CONFIG_TIME - mCurrentNum));
				mCurrentNum += 1;
				if (mCurrentNum >= APPConfig.WIFI_CONFIG_TIME) {
					myHandler.sendEmptyMessage(MSG_STOP_SCLIB);
					countDownHandler.removeMessages(STEP1);
				} else {
					countDownHandler.sendEmptyMessageDelayed(STEP1, 1000);
				}
				break;
			case STEP2:
				if (bAddDevice) {
					tv_right_draw_2
							.setText(""
									+ (APPConfig.WIFI_CHECK_ADD_DEVICE_TIME - mCurrentNum));
				} else {
					tv_right_draw_3
							.setText(""
									+ (APPConfig.WIFI_CHECK_ADD_DEVICE_TIME - mCurrentNum));
				}
				mCurrentNum += 1;
				if (mCurrentNum >= APPConfig.WIFI_CHECK_ADD_DEVICE_TIME) {
					jumpToResult(false);
				} else {
					countDownHandler.sendEmptyMessageDelayed(STEP2, 1000);
				}
				break;
			case STEP3:
				tv_right_draw_3.setText(""
						+ (APPConfig.DEVICE_BIND_TIME - mCurrentNum));
				mCurrentNum += 1;
				if (mCurrentNum >= APPConfig.DEVICE_BIND_TIME) {
					jumpToResult(false);
				} else {
					countDownHandler.sendEmptyMessageDelayed(STEP3, 1000);
				}
				break;
			}
		}
	};

	private void initStepView() {
		tv_left_draw_1 = (TextView) findViewById(R.id.tv_left_draw_1);
		tv_left_draw_2 = (TextView) findViewById(R.id.tv_left_draw_2);
		tv_left_draw_3 = (TextView) findViewById(R.id.tv_left_draw_3);
		tv_center_name_1 = (TextView) findViewById(R.id.tv_center_name_1);
		tv_center_name_2 = (TextView) findViewById(R.id.tv_center_name_2);
		tv_center_name_3 = (TextView) findViewById(R.id.tv_center_name_3);
		tv_right_draw_1 = (TextView) findViewById(R.id.tv_right_draw_1);
		tv_right_draw_2 = (TextView) findViewById(R.id.tv_right_draw_2);
		tv_right_draw_3 = (TextView) findViewById(R.id.tv_right_draw_3);
		rl_step_1 = (RelativeLayout) findViewById(R.id.rl_step_1);
		rl_step_2 = (RelativeLayout) findViewById(R.id.rl_step_2);
		rl_step_3 = (RelativeLayout) findViewById(R.id.rl_step_3);
	}

	private void showStep1Enable(boolean enabled, int step) {
		tv_left_draw_1.setEnabled(enabled);
		tv_center_name_1.setEnabled(enabled);

		tv_right_draw_1.setEnabled(enabled);
		if (!enabled) {
			tv_right_draw_1.setText("");
		} else {

			tv_right_draw_1.setVisibility(View.VISIBLE);
			tv_right_draw_2.setVisibility(View.INVISIBLE);
			tv_right_draw_3.setVisibility(View.INVISIBLE);
		}
	}

	private void showStep2Enable(boolean enabled, int step) {
		tv_left_draw_2.setEnabled(enabled);
		tv_center_name_2.setEnabled(enabled);
		tv_right_draw_2.setEnabled(enabled);
		if (!enabled) {
			tv_right_draw_2.setText("");
		} else {
			tv_right_draw_1.setVisibility(View.VISIBLE);
			tv_right_draw_2.setVisibility(View.VISIBLE);
			tv_right_draw_3.setVisibility(View.INVISIBLE);
		}
	}

	private void showStep3Enable(boolean enabled, int step) {
		tv_left_draw_3.setEnabled(enabled);
		tv_center_name_3.setEnabled(enabled);
		tv_right_draw_3.setEnabled(enabled);
		if (enabled) {
			tv_right_draw_1.setVisibility(View.VISIBLE);
			tv_right_draw_2.setVisibility(View.VISIBLE);
			tv_right_draw_3.setVisibility(View.VISIBLE);
		}
	}

	private void showStepView() {
		switch (mCurrentStep) {
		case STEP1:
			if (bAddDevice) {
				showStep1Enable(true, 1);
				showStep2Enable(false, 1);
				showStep3Enable(false, 1);
			} else {
				showStep1Enable(true, 1);
				showStep3Enable(false, 1);
			}
			iv_oval_rigth_device.setImageResource(R.drawable.icon_oval_step_1);
			tv_prompt.setText(getResources().getString(
					R.string.config_get_remoteip_success_setting_wifi));
			break;
		case 2:
			if (bAddDevice) {
				showStep1Enable(false, 2);
				showStep2Enable(true, 2);
				showStep3Enable(false, 2);
			} else {
				showStep1Enable(false, 2);
				showStep3Enable(true, 2);
			}
			iv_oval_rigth_device.setImageResource(R.drawable.icon_oval_step_2);
			tv_prompt.setText(getResources().getString(
					R.string.config_verifiy_wifi_info));
			break;
		case 3:
			if (bAddDevice) {
				showStep1Enable(false, 3);
				showStep2Enable(false, 3);
				showStep3Enable(true, 3);
			}
			iv_oval_rigth_device.setImageResource(R.drawable.icon_oval_step_3);
			tv_prompt.setText(getResources().getString(
					R.string.config_binding_to_your_account));
			break;
		default:
			break;
		}
	}

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_STOP_SCLIB:
				System.out.println("------>停止直联");
				stopSCLib();
				startMultcast();
				System.out.println("------>开始组播");
				break;
			case MSG_UPDATE_VIEW:
				showStepView();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void DataReturn(boolean success, RouteApiType apiType, String json) {
		super.DataReturn(success, apiType, json);
		if (success) {
			switch (apiType) {
			case getAllDeviceInformationByMulticast:
				Utils.sysoInfo("getAllDeviceInformationByMulticast json = "
						+ json);
				boolean result = false;
				List<DeviceDescriptionModel> tempDeviceDesList = XMLHandler
						.getDeviceList(json);
				for (DeviceDescriptionModel item : tempDeviceDesList) {
					String deviceId = item.getSipaccount();
					int start = deviceId.indexOf("cmic");
					int end = deviceId.indexOf("@");
					deviceId = deviceId.substring(start, end);
					System.out.println("deviceId------>"  +deviceId);
					System.out.println("originDeviceId------>"  +originDeviceId);
					if (deviceId.equalsIgnoreCase(originDeviceId)) {
						result = true;
						break;
					}
				}
				if (result) {
					if (mInfoData.isAddDevice()) {
						System.out.println("------>未绑定");
						if(mInfoData.getDeviceId().startsWith("CMIC06")){
							System.out.println("------>CMIC06");
							jumpToResult(true);
						} else {
							bindDeviceToAccount();
							System.out.println("------>进行绑定");
						}				
					} else {
						jumpToResult(true);
					}				
				}
				break;
			/*
			 * case BINDING_CHECK: if (userInfo == null) { userInfo =
			 * ((ICamApplication) getApplication()) .getUserinfo(); } cb =
			 * Utils.parseBean(CheckBind.class, json); if (cb != null) { if
			 * (TextUtils.isEmpty(cb.getUuid())) { bindDevice(); } else {
			 * jumpToResult(false); } } break;
//			 */
//			case BINDING_BIDN:
//				jumpToResult(true);
//				break;
			default:
				break;
			}
		} else {
			switch (apiType) {
			case getAllDeviceInformation:
				break;
			// case BINDING_CHECK:
			// jumpToResult(false);
			// break;
//			case BINDING_BIDN:
//				bindDeviceToAccountFail();
//				break;
			default:
				break;
			}
		}
	}

	private void setListener() {

	}

	private class MsgHandler extends Handler {
		// byte ret;

		@Override
		public void handleMessage(Message msg) {
			// switch (msg.what) {
			// case ~SCParams.Flag.CfgSuccessACK:
			// // Toast.makeText(TestWifiDirect.this, "Config Timeout",
			// // Toast.LENGTH_SHORT).show();
			// // SCLib.rtk_sc_stop();
			// break;
			// case SCParams.Flag.CfgSuccessACK:
			// // SCLib.rtk_sc_stop();
			// // handleRecConfigMsg();
			// break;
			// case SCParams.Flag.CFGTimeSendBack:
			// break;
			// case SCParams.Flag.DiscoverACK:
			// // SCCtlOps.handle_discover_ack((byte[]) msg.obj);
			// // if (SCParams.DiscoveredNew) {
			// // ShowConnectedDevs();
			// // }
			// break;
			// case ~SCParams.Flag.DiscoverACK:
			// // Log.d(TAG, "Discovery timeout.");
			// // Toast.makeText(TestWifiDirect.this,
			// // "Discover device Timeout",
			// // Toast.LENGTH_SHORT).show();
			// break;
			// case SCParams.Flag.DelProfACK:
			// break;
			// case SCParams.Flag.RenameDevACK:
			// break;
			// default:
			// break;
			// }
		}
	}

	private void jumpToResult(boolean isSuccess) {
		stopSCLib();
		stopMultcase();

		Intent it = new Intent();
		it.putExtra("configInfo", mInfoData);
		if (!isSuccess) {
			it.setClass(this, DeviceConfigFailResultActivity.class);
		} else {
			it.setClass(this, DeviceConfigSuccessActivity.class);
		}
		startActivity(it);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			showExitDialog();
		}
	}

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
	protected void setViewContent() {
		setContentView(R.layout.activity_wifi_direct_setting);
	}

	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.config_link_camera);
	}

	@Override
	protected OnClickListener getLeftClick() {
		return this;
	}

	static {
		System.loadLibrary("simpleconfiglib");
	}
}
