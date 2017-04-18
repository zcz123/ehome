package cc.wulian.app.model.device.impls.sensorable;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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

/**
 * (十六进制) 位1～2:设备类型(01:状态) <br/>
 * 位3～6:设备数据(检测值,ug/m3,ppm) <br/>
 * 注:<100优,100-200良,200-300轻度污染,>300重度污染<br/>
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_PM2P5 }, category = Category.C_ENVIRONMENT)
public class WL_D5_PM2P5 extends SensorableDeviceCirlce270 {
	private static final int PPM_PM_120 = 120;
	private static final int PPM_PM_180 = 180;
	private static final int PPM_PM_240 = 240;
	private static final int PPM_PM_75 = 75;
	private static final int PPM_PM_150 = 150;
	public static final String UNIT = "ug/m3";
	private int curNum;
	private int deviceResId = R.drawable.device_progerss_pm2p5;

	private final static String ALREADY_GO_TO_DEVICE_HTML = "already_go_to_device_html";
	private final static String NOT_GO_TO_DEVICE_HTML = "not_go_to_device_html";
	private static String status = NOT_GO_TO_DEVICE_HTML;

	private String pluginName = "environmental.zip";

	private H5PlusWebView webView;

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		if(epData==null){
			return sb;
		}
		sb.append(StringUtil.toInteger(StringUtil.toInteger(
				substring(epData, epData.length() - 4), 16))
				+ " ");
		sb.append(UNIT);
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
		int ratio = StringUtil.toInteger(substring(epData, 2), 16);

//		if (ratio <= PPM_PM_120) {
//			flag = FLAG_RATIO_NORMAL;
//		} else if (ratio <= PPM_PM_120 && ratio <= PPM_PM_180) {
//			flag = FLAG_RATIO_MID;
//		} else if (ratio <= PPM_PM_180 && ratio <= PPM_PM_240) {
//			flag = FLAG_RATIO_ALARM;
//		} else if (ratio > PPM_PM_240) {
//			flag = FLAG_RATIO_BAD;
//		}
		if (ratio <= PPM_PM_75) {
			flag = FLAG_RATIO_NORMAL;
		} else if (ratio <= PPM_PM_75 && ratio <= PPM_PM_150) {
			flag = FLAG_RATIO_ALARM;
		}else if (ratio > PPM_PM_150) {
			flag = FLAG_RATIO_BAD;
		}
		return flag;

	}

	public WL_D5_PM2P5(Context context, String type) {
		super(context, type);
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
	public String unit(String ep, String epType) {
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
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, AutoConditionInfo autoConditionInfo,
			boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		SensorDustChooseView sensorDustChooseView = new SensorDustChooseView(
				inflater.getContext(), ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_PM2P5);
		sensorDustChooseView.setmSensorDeviceValues(autoConditionInfo.getExp(),
				autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(sensorDustChooseView.getView());
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}

	private void getPlugin() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				((Activity) mContext).runOnUiThread(new Runnable() {

					@Override
					public void run() {

					}
				});

				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext, pluginName,
						new PluginsManagerCallback() {

							@Override
							public void onGetPluginSuccess(PluginModel model) {
								File file = new File(model.getFolder(),
										"dust.html");
								String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
								if (file.exists()) {
									uri = "file:///" + file.getAbsolutePath();
								} else if (LanguageUtil.isChina()) {
									uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
								}
								final String uriString = uri;
								Preference.getPreferences().savePMHtmlUri(uri);
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