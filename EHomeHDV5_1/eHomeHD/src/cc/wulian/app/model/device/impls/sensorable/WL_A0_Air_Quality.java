package cc.wulian.app.model.device.impls.sensorable;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.LinkTaskTempHumView;
import cc.wulian.app.model.device.interfaces.SensorAirChooseView;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_A0 }, category = Category.C_ENVIRONMENT)
public class WL_A0_Air_Quality extends SensorableDeviceImpl {

	public WL_A0_Air_Quality(Context context, String type) {
		super(context, type);
	}

	public CustomProgressBar_270 mCustomView;
	public TextView mNumText;
	public ImageView mImageView;
	public TextView mUnit;

	private final static String ALREADY_GO_TO_DEVICE_HTML = "already_go_to_device_html";
	private final static String NOT_GO_TO_DEVICE_HTML = "not_go_to_device_html";
	private static String status = NOT_GO_TO_DEVICE_HTML;

	private int deviceImageId = R.drawable.device_progerss_voc;

	private String UNIT_PPM = "PPM";

	private String pluginName = "environmental.zip";
	
	private H5PlusWebView webView;

	private static final int CTHV_PPM_800 = 800;
	private static final int CTHV_PPM_1500 = 1500;

	private static final int 	 SMALL_NORMAL_D = R.drawable.device_co2_normal;
	private static final int 	 SMALL_ALARM_D = R.drawable.device_co2_alarm;
	private static final int 	 SMALL_MID_D = R.drawable.device_co2_mid;

	@Override
	public void refreshDevice() {
		super.refreshDevice();
		sendDataToDesktop(epData,epType);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		View view = inflater.inflate(R.layout.device_voc, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		if(!isDeviceOnLine()){
			return;
		}
		webView = (H5PlusWebView) view.findViewById(R.id.voc_webview);
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
		getPlugin();
	}

//	@Override
//	public void onResume() {
//		getPlugin();
//	}


	@Override
	public Drawable getSensorStateSmallIcon() {
		String flag = checkDataRatioFlag();
		int res = SMALL_NORMAL_D;
		if (FLAG_RATIO_NORMAL == flag){
			res = SMALL_NORMAL_D;
		}
		else if (FLAG_RATIO_MID == flag){
			res = SMALL_MID_D;
		}
		else if (FLAG_RATIO_ALARM == flag){
			res = SMALL_ALARM_D;
		}
		return getDrawable(res);
	}

	@Override
	public String checkDataRatioFlag() {
		String flag = FLAG_RATIO_NORMAL;
		int curPPMData =0;
		//当前空气质量数值
		if(!StringUtil.isNullOrEmpty(epData) &&(epData.length() == 12)){
			curPPMData = Integer.parseInt(epData.substring(8,12),16);
		}
		if (curPPMData <= CTHV_PPM_800) {
			flag = FLAG_RATIO_NORMAL;
		} else if (curPPMData > CTHV_PPM_800 && curPPMData <= CTHV_PPM_1500) {
			flag = FLAG_RATIO_MID;
		} else if (curPPMData > CTHV_PPM_1500 ) {
			flag = FLAG_RATIO_ALARM;
		}
		return flag;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		if(epData == null) {
			return "";
		}
		SpannableStringBuilder sb = new SpannableStringBuilder();
		if (epData.length() == 12) {
			sb.append(StringUtil.toInteger(epData.substring(8, 12), 16) + "");
		} else {
			sb.append(0 + "");
		}
		sb.append(" PPM");
		return sb;
	}

	// @Override
	// public void initViewStatus() {
	// super.initViewStatus();
	// String mData = "0";
	// if (!isNull(epData)) {
	//
	// if(epData.length()==12){
	// mData = epData.substring(8,12);
	// }else{
	// mData = "0";
	// }
	// } else {
	// return;
	// }
	//
	// double angleUnit = 200.0 / 270.0;
	// double curNum = StringUtil.toInteger(mData, 16).doubleValue();
	//
	// double degree = curNum / angleUnit;
	//
	// if (degree > 270) {
	// degree = 270;
	// } else if (degree < 0) {
	// degree = 0;
	// }
	//
	// mNumText.setText(curNum+"");
	//
	// mCustomView.setDegree(degree, deviceImageId);
	// mUnit.setText(UNIT_PPM);
	// }
	public String getIntroductionFilePath() {
		String url = DeviceUtil.getFileURLByLocaleAndCountry(mContext,
				"wl_a0_air_quality.html");
		return url;
	}

	@Override
	protected List<MenuItem> getDeviceMenuItems(
			final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		/**
		 * 设备介绍
		 */
		MenuItem deviceIntroductionItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView
						.setText(mContext
								.getString(cc.wulian.smarthomev5.R.string.device_config_edit_dev_help));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_help);
			}

			@Override
			public void doSomething() {
				String introductionFilePath = getIntroductionFilePath();
				IntentUtil
						.startCustomBrowser(
								mContext,
								introductionFilePath,
								getDefaultDeviceName(),
								mContext.getString(cc.wulian.smarthomev5.R.string.about_back));
				manager.dismiss();
			}
		};
		if (isDeviceOnLine()) {
			items.add(deviceIntroductionItem);
		}
		return items;
	}

	@Override
	public String unit(String ep, String epType) {
		return "PPM";
	}

	@Override
	public String unitName() {
		return "CO2";
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, AutoConditionInfo autoConditionInfo,
			boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		SensorAirChooseView sensorAirChooseView = new SensorAirChooseView(
				inflater.getContext(), ConstUtil.DEV_TYPE_FROM_GW_A0);
		sensorAirChooseView.setmSensorDeviceValues(autoConditionInfo.getExp(),
				autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(sensorAirChooseView.getView());
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}

	private void getPlugin() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext, pluginName,
						new PluginsManagerCallback() {

							@Override
							public void onGetPluginSuccess(PluginModel model) {
								File file = new File(model.getFolder(),
										"air.html");
								String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
								if (file.exists()) {
									uri = "file:///" + file.getAbsolutePath();
								} else if (LanguageUtil.isChina()) {
									uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
								}
								final String uriString = uri;
								Preference.getPreferences().saveAirHtmlUri(uri);
								Handler handler = new Handler(Looper
										.getMainLooper());
								handler.post(new Runnable() {
									@Override
									public void run() {
										webView.loadUrl(uriString);
									}
								});
							}

							@Override
							public void onGetPluginFailed(final String hint) {
								if (hint != null && hint.length() > 0) {
									Handler handler = new Handler(Looper
											.getMainLooper());
									handler.post(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(mContext, hint,
													Toast.LENGTH_SHORT).show();
										}
									});
								}
							}
						});
			}
		}).start();
	}

}
