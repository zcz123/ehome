package cc.wulian.app.model.device.impls.sensorable;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.SensorAirChooseView;
import cc.wulian.app.model.device.interfaces.SensorDustChooseView;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_CTHV }, category = Category.C_ENVIRONMENT)
public class WL_42_CTHV extends SensorableDeviceCirlce270 {
	private static final int srcId = R.drawable.device_progerss_270;
	private static final int CTHV_PPM_350 = 350;
	private static final int CTHV_PPM_1000 = 1000;
	private static final int CTHV_PPM_2500 = 2500;
	private static final int CTHV_PPM_800 = 800;
	private static final int CTHV_PPM_1500 = 1500;

	private final String pluginName="Indepent_environmental.zip";

	private static final int 	 SMALL_NORMAL_D = R.drawable.device_co2_normal;
	private static final int 	 SMALL_ALARM_D = R.drawable.device_co2_alarm;
	private static final int 	 SMALL_MID_D = R.drawable.device_co2_mid;

	private H5PlusWebView webView;

	public WL_42_CTHV(Context context, String type) {
		super(context, type);
	}
	
	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
		return inflater.inflate(R.layout.device_voc, null);
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
		int curPPMData = StringUtil.toInteger(getChildDevice(EP_14)
				.getDeviceInfo().getDevEPInfo().getEpData());
		String flag = FLAG_RATIO_NORMAL;
//		if (curPPMData <= CTHV_PPM_350) {
//			flag = FLAG_RATIO_NORMAL;
//		} else if (curPPMData > CTHV_PPM_350 && curPPMData <= CTHV_PPM_1000) {
//			flag = FLAG_RATIO_MID;
//		} else if (curPPMData > CTHV_PPM_1000 && curPPMData <= CTHV_PPM_2500) {
//			flag = FLAG_RATIO_ALARM;
//		} else if (curPPMData > CTHV_PPM_2500 ) {
//			flag = FLAG_RATIO_BAD;
//		}

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
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
	}

	@Override
	public void onDeviceData(String gwID, String devID, DeviceEPInfo devEPInfo,String cmd,String mode) {
		String ep = devEPInfo.getEp();
		WulianDevice device = getChildDevice(ep);
		if (device != null) {
			device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			removeCallbacks(mRefreshStateRunnable);
			post(mRefreshStateRunnable);
			fireDeviceRequestControlData();
		}
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(getChildDevice(EP_14).getDeviceInfo().getDevEPInfo().getEpData());
		sb.append(" PPM");
		return sb;
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		String data = getChildDevice(EP_14).getDeviceInfo()
				.getDevEPInfo().getEpData();
		if (StringUtil.isNullOrEmpty(data)) {
			return;
		} else {
			double angleUnit = 5000 / 270;
			int curNum = StringUtil.toInteger(data);
			double degree = curNum / angleUnit;
			if (degree > 270) {
				degree = 270;

			}
//			mNumText.setText(curNum+"");
//			mCustomView.setDegree(degree, srcId);
		}
	}
	public String getIntroductionFilePath() {
		String url = DeviceUtil.getFileURLByLocaleAndCountry(mContext,
				"wl_42_cthv.html");
		return url;
	}
	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items =  super.getDeviceMenuItems(manager);
		/**
		 * 设备介绍
		 */
		MenuItem deviceIntroductionItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.device_config_edit_dev_help));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_help);
			}

			@Override
			public void doSomething() {
				String introductionFilePath = getIntroductionFilePath();
				IntentUtil.startCustomBrowser(mContext, introductionFilePath,
						getDefaultDeviceName(),
						mContext.getString(cc.wulian.smarthomev5.R.string.about_back));
				manager.dismiss();
			}
		};
		if(isDeviceOnLine()){
			items.add(deviceIntroductionItem);
		}
		return items;
	}

	private void getPlugin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm=PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext,pluginName,new PluginsManager.PluginsManagerCallback() {

					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file=new File(model.getFolder(),"device_42.html");
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri = "file:///" + file.getAbsolutePath();
						}else if(LanguageUtil.isChina()){
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}

						final  String urlString=uri;
						Preference.getPreferences().savePMHtmlUri(uri);
						Handler handler=new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							@Override
							public void run() {
								webView.loadUrl(urlString);
							}
						});
					}

					@Override
					public void onGetPluginFailed(final String hint) {
						if(hint!=null&&hint.length()>0){
							Handler handler=new Handler(Looper.getMainLooper());
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				});
			}
		}).start();
	}


	@Override
	public String unit(String ep,String epType) {
		return "PPM";
	}

	@Override
	public String unitName() {
		return "CO2";
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, AutoConditionInfo autoConditionInfo,boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		SensorAirChooseView sensorAirChooseView = new SensorAirChooseView(inflater.getContext(), ConstUtil.DEV_TYPE_FROM_GW_CTHV);
		sensorAirChooseView.setmSensorDeviceValues(autoConditionInfo.getExp(),autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(sensorAirChooseView.getView());
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
}
