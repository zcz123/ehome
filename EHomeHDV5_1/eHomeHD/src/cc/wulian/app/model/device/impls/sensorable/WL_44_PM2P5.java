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
import cc.wulian.app.model.device.interfaces.LinkTaskDustView;
import cc.wulian.app.model.device.interfaces.SensorDustChooseView;
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
 * (十六进制) 位1～2:设备类型(01:状态) <br/>
 * 位3～6:设备数据(检测值,ug/m3,ppm) <br/>
 * 注:<100优,100-200良,200-300轻度污染,>300重度污染<br/>
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_PM2P5 }, category = Category.C_ENVIRONMENT)
public class WL_44_PM2P5 extends SensorableDeviceCirlce270 {
	private static final int PPM_PM_80 = 80;
	private static final int PPM_PM_120 = 120;
	private static final int PPM_PM_180 = 180;
	private static final int PPM_PM_240 = 240;
	private static final int PPM_PM_320 = 320;
	public static final String UNIT = "ug/m3";
	private int curNum;
	private int deviceResId = R.drawable.device_progerss_pm2p5;
	private final  String pluginName="Indepent_environmental.zip";

	private H5PlusWebView webView;

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(StringUtil.toInteger(StringUtil.toInteger(substring(epData, 2), 16)) + " ");
		sb.append(UNIT);
		return sb;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
		return inflater.inflate(R.layout.device_voc, null);
	}


	@Override
	public String checkDataRatioFlag() {
		String flag = FLAG_RATIO_NORMAL;
		int ratio = StringUtil.toInteger(substring(epData, 2), 16);

		if (ratio <= PPM_PM_120) {
			flag = FLAG_RATIO_NORMAL;
		} else if (ratio <= PPM_PM_120 && ratio <= PPM_PM_180) {
			flag = FLAG_RATIO_MID;
		} else if (ratio <= PPM_PM_180 && ratio <= PPM_PM_240) {
			flag = FLAG_RATIO_ALARM;
		} else if (ratio > PPM_PM_240) {
			flag = FLAG_RATIO_BAD;
		}
		return flag;

	}

	public WL_44_PM2P5(Context context, String type) {
		super(context, type);
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

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if (StringUtil.isNullOrEmpty(epData))
			return;
		String pmData = substring(epData, 2);
		curNum = StringUtil.toInteger(pmData, 16);
		double angleUnit = 320 / 270;
		double degree = curNum / angleUnit;
		if (degree > 270) {
			degree = 270;
		}
		
//		mNumText.setText(getPMDescription(curNum));
//		mCustomView.setDegree(degree, deviceResId);
	}

	private String getPMDescription(int num){
		String result;
		if(num < PPM_PM_80){
			result = getString(R.string.device_link_task_detection_degree_a);
		}else if(num < PPM_PM_120){
			result = getString(R.string.device_pm2p5_good);
		}else if(num < PPM_PM_180){
			result = getString(R.string.device_pm2p5_light_pollution);
		}else if(num <PPM_PM_240){
			result = getString(R.string.device_pm2p5_moderately_polluted);
		}else if(num < PPM_PM_320){
			result = getString(R.string.device_pm2p5_severe_pollution);
		}else{
			result = getString(R.string.device_pm2p5_serious_pollution);
		}
		return result;
	}
	@Override
	public String unit(String ep,String epType) {
		return UNIT;
	}

	@Override
	public String unitName() {
		return "PPM";
	}

	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		LinkTaskDustView taskView = new LinkTaskDustView(context, taskInfo);
		taskView.onCreateView();
		return taskView;
	}

	public String getIntroductionFilePath() {
		String url = DeviceUtil.getFileURLByLocaleAndCountry(mContext,
				"wl_44_pm2p5.html");
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
	private void getPlugin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm=PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext,pluginName,new PluginsManager.PluginsManagerCallback() {

					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file=new File(model.getFolder(),"device_44.html");
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri="file:///"+file.getAbsolutePath();
						}else if(LanguageUtil.isChina()){
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}

//						Intent intent= new Intent();
//						intent.setClass(mContext, Html5PlusWebViewActvity.class);
//						intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
//						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
//						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
//						mContext.startActivity(intent);
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
		SensorDustChooseView sensorDustChooseView = new SensorDustChooseView(inflater.getContext(), ConstUtil.DEV_TYPE_FROM_GW_PM2P5);
		sensorDustChooseView.setmSensorDeviceValues(autoConditionInfo.getExp(),autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(sensorDustChooseView.getView());
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}

	
}