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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.LinkTaskNoiseView;
import cc.wulian.app.model.device.interfaces.SensorVoiceChooseView;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
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


@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_NOISE }, category = Category.C_ENVIRONMENT)
public class WL_D4_Noise extends SensorableDeviceCirlce270 {

	/**
	 * (十六进制)
	 * 0AD4040PQQQQ：0A数据长度；D4设备类型；04功能码，表示传感器数据；0P表示数据类型，基本为01，表示正整数；
	 * QQQQ表示噪音值，十六进制，单位db
	 */
	private static final int QUIET = 35;
	private static final int NOISY = 65;
	
	private final static String ALREADY_GO_TO_DEVICE_HTML="already_go_to_device_html";
	private final static String NOT_GO_TO_DEVICE_HTML="not_go_to_device_html";
	private static String status=NOT_GO_TO_DEVICE_HTML;
	
	private int curNum;
	private int deviceResId = R.drawable.device_progerss_noise65;
	
	private String pluginName="environmental.zip";
	
	private H5PlusWebView webView;
	
	public WL_D4_Noise(Context context, String type) {
		super(context, type);
	}
	
	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		if (epData==null){ //增加非空判断 mabo
			return sb;
		}
		sb.append(StringUtil.toInteger(StringUtil.toInteger(substring(epData, epData.length()-4), 16)) + "");
		sb.append(" dB");
		return sb;
	}
	@Override
	public void refreshDevice() {
		super.refreshDevice();
		sendDataToDesktop(epData,epType);
	}

	@Override
	public String checkDataRatioFlag() {
		String flag = FLAG_RATIO_NORMAL;
		int ratio = StringUtil.toInteger(substring(epData, epData.length()-4), 16);
		if (ratio <= QUIET) {
			flag = FLAG_RATIO_NORMAL;
		} else if (ratio <= QUIET && ratio <= NOISY) {
			flag = FLAG_RATIO_MID;
		} else if (ratio > NOISY) {
			flag = FLAG_RATIO_ALARM;
		}
		return flag;

	}

	@Override
	public Drawable getSensorStateSmallIcon() {
		Drawable drawable=null;
		if (epData==null){  //增加非空判断 mabo
			return getResources().getDrawable(R.drawable.device_noise_normal);
		}
		int ratio = StringUtil.toInteger(substring(epData, epData.length()-4), 16);
		if (ratio <= QUIET) {
			drawable=getResources().getDrawable(R.drawable.device_noise_normal);
		} else if (ratio <= QUIET && ratio <= NOISY) {
			drawable=getResources().getDrawable(R.drawable.device_noise_mid);
		} else if (ratio > NOISY) {
			drawable=getResources().getDrawable(R.drawable.device_noise_alarm);
		}
		return drawable;
		
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

	private String getPMDescription(int num){
		String result;
		if(num < QUIET){
			result = getString(R.string.device_d4_quiet);
		}else if(num < NOISY){				
			result = getString(R.string.scene_normal_hint);
		}else{
			result = getString(R.string.device_d4_noisy);
		}
		return result;
	}

	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		LinkTaskNoiseView taskView = new LinkTaskNoiseView(context, taskInfo);
		taskView.onCreateView();
		return taskView;
	}
	@Override
	public String unitName() {
		return mContext.getResources().getString(R.string.device_noise_unit_name);
	}
	@Override
	public String unit(String ep,String epType) {
		return "db";
	}

	public String getIntroductionFilePath() {
		String url = DeviceUtil.getFileURLByLocaleAndCountry(mContext,
				"wl_d4_noise.html");
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
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, AutoConditionInfo autoConditionInfo,boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		SensorVoiceChooseView sensorVoiceChooseView = new SensorVoiceChooseView(inflater.getContext());
		sensorVoiceChooseView.setmSensorDeviceValues(autoConditionInfo.getExp(),autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(sensorVoiceChooseView.getView());
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}

	private void getPlugin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm=PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext,pluginName,new PluginsManagerCallback() {
					
					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file=new File(model.getFolder(),"noise.html");
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri="file:///"+file.getAbsolutePath();
						}else if(LanguageUtil.isChina()){
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}
						final String uriString = uri;
						Preference.getPreferences().saveNoiseHtmlUri(uri);
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
	
}
