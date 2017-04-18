package cc.wulian.app.model.device.impls.sensorable;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.LinkTaskBodyLightView;
import cc.wulian.app.model.device.interfaces.SensorLightChooseView;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.BaseActivity;
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

/**
 * int(单位LUX)
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_LIGHT_S }, category = Category.C_ENVIRONMENT)
public class WL_19_Light_S extends SensorableDeviceCirlce270 {
	private static final String UNIT_LUX = " LUX";
	private int deviceIdRes = R.drawable.device_progerss_light19;

	private final  String pluginName="Indepent_environmental.zip";

	private H5PlusWebView webView;

	public WL_19_Light_S(Context context, String type) {
		super(context, type);
	}

	@Override
	public void refreshDevice() {
		super.refreshDevice();
	}
	
	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		if(StringUtil.isNullOrEmpty(epData)) {
			epData = "0";
		}
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(epData);
		sb.append(UNIT_LUX);
		return sb;
	}
	
	@Override
	public void initViewStatus() {
		super.initViewStatus();
		double angleUnit = 3000 / 270;
		int curNum = StringUtil.toInteger(epData);
		double degree = curNum / angleUnit;
		if (degree > 270) {
			degree = 270;
		}
//		mNumText.setText(epData);
//		mCustomView.setDegree(degree, deviceIdRes);
//		mUnit.setText(unit(getCurrentEpInfo().getEp(),getCurrentEpInfo().getEpType()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
		return inflater.inflate(R.layout.device_voc, null);
	}

	@Override
	public String unit(String ep,String epType) {
		return UNIT_LUX;
	}

	@Override
	public String unitName() {
		return mResources.getString(R.string.device_illumination);
	}
	
	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		LinkTaskBodyLightView taskView = new LinkTaskBodyLightView(context, taskInfo);
		taskView.onCreateView();
		return taskView;
	}

	public String getIntroductionFilePath(){
		String url = DeviceUtil.getFileURLByLocaleAndCountry(mContext,
				"wl_19_light_s.html");
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
//		final MoreMenuPopupWindow.MenuItem settingItem = new MoreMenuPopupWindow.MenuItem(mContext) {
//
//			@Override
//			public void initSystemState() {
//				titleTextView.setText(mContext
//						.getString(cc.wulian.smarthomev5.R.string.device_human_traffic_statistics));
//				iconImageView
//						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
//			}
//
//			@Override
//			public void doSomething() {
//				getPlugin();
//				manager.dismiss();
//			}
//		};
//		if(isDeviceOnLine())
//			items.add(settingItem);


		return items;
	}
	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
//		mUnit.setVisibility(View.GONE);
		if(!isDeviceOnLine()){
			return;
		}
		webView = (H5PlusWebView) view.findViewById(R.id.voc_webview);
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
		getPlugin();
	}
	private void getPlugin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm= PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext,pluginName,new PluginsManager.PluginsManagerCallback() {

					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file=new File(model.getFolder(),"device_19.html");
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri="file:///"+file.getAbsolutePath();
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
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, AutoConditionInfo autoConditionInfo,boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		SensorLightChooseView  SensorLightlView = new SensorLightChooseView (inflater.getContext());
		SensorLightlView.setmSensorDeviceValues(autoConditionInfo.getExp(),autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(SensorLightlView.getView());
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
}