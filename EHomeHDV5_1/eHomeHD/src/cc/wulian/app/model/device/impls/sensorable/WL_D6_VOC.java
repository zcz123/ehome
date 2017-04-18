package cc.wulian.app.model.device.impls.sensorable;

import java.io.File;
import java.util.List;

import com.wulian.icam.view.device.setting.DeviceDetailActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
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
import cc.wulian.app.model.device.interfaces.LinkTaskVOCView;
import cc.wulian.app.model.device.interfaces.SensorVOCChooseView;
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

/**
 * int(单位PPM)
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_VOC }, category = Category.C_ENVIRONMENT)
public class WL_D6_VOC extends SensorableDeviceImpl {

	public CustomProgressBar_270 mCustomView;
	public TextView mNumText;
	public ImageView mImageView;
	public TextView mUnit;

	private final static String ALREADY_GO_TO_DEVICE_HTML = "already_go_to_device_html";
	private final static String NOT_GO_TO_DEVICE_HTML = "not_go_to_device_html";
	private static String status = NOT_GO_TO_DEVICE_HTML;

	private static final float PPB_VOC_0 = 0;
	private static final float PPB_VOC_300 = 300;
	private static final float PPB_VOC_600 = 600;

	private static final int PPB_VOC_INT_0 = 0;
	private static final int PPB_VOC_INT_100 = 100;
	private static final int PPB_VOC_INT_200 = 200;

	private static final int SMALL_NORMAL_D = R.drawable.device_small_voc_normal;
	private static final int SMALL_ALARM_D = R.drawable.device_small_voc_alarm;
	private static final int SMALL_MID_D = R.drawable.device_small_voc_mid;

	private int deviceImageId = R.drawable.device_progerss_voc;

	private String UNIT_PPB = " ppb";

	private String pluginName = "environmental.zip";

	private H5PlusWebView webView;

	public WL_D6_VOC(Context context, String type) {
		super(context, type);
	}

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

	// @Override
	// public void initViewStatus() {
	// super.initViewStatus();
	// String mVOCData = "0";
	// if (!isNull(epData) && epData.length()==12) {
	// mVOCData = epData.substring(epData.length() - 4);
	// double angleUnit = 200.0 / 270.0;
	// double curNum = StringUtil.toInteger(mVOCData, 16).doubleValue();
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
	// mUnit.setText(UNIT_PPB);
	// } else {
	//
	// mNumText.setText(0+"");
	//
	// mCustomView.setDegree(0, deviceImageId);
	// mUnit.setText(UNIT_PPB);
	// }
	//
	// }
	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		LinkTaskVOCView taskView = new LinkTaskVOCView(context, taskInfo);
		taskView.onCreateView();
		return taskView;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		if (!isNull(epData) && epData.length() == 12) {
			sb.append(StringUtil.toInteger(
					epData.substring(epData.length() - 4), 16)
					+ "");
		} else {
			sb.append(0 + "");
		}
		sb.append(UNIT_PPB);
		return sb;
	}

	@Override
	public Drawable getSensorStateSmallIcon() {
		String flag = checkDataRatioFlag();
		int res = SMALL_NORMAL_D;
		if (FLAG_RATIO_NORMAL == flag) {
			res = SMALL_NORMAL_D;
		} else if (FLAG_RATIO_MID == flag) {
			res = SMALL_MID_D;
		} else if (FLAG_RATIO_ALARM == flag) {
			res = SMALL_ALARM_D;
		}
		return getDrawable(res);
	}

	@Override
	public String checkDataRatioFlag() {
		String flag = FLAG_RATIO_NORMAL;
		if (StringUtil.isNullOrEmpty(epData)) {
			return flag;
		}
		int ratio = 0;
		if (epData.length() == 12) {
			ratio = StringUtil.toInteger(StringUtil.toInteger(
					epData.substring(epData.length() - 4), 16));
		}

		// nice
		if (ratio <= PPB_VOC_300) {
			flag = FLAG_RATIO_NORMAL;
		}
		// bad
		else if (ratio > PPB_VOC_300 && ratio <= PPB_VOC_600) {
			flag = FLAG_RATIO_MID;
		}
		// very bad
		else if (ratio > PPB_VOC_600) {
			flag = FLAG_RATIO_ALARM;
		}
		return flag;
	}

	public String getIntroductionFilePath() {
		String url = DeviceUtil.getFileURLByLocaleAndCountry(mContext,
				"wl_d6_voc.html");
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

		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView
						.setText(mContext
								.getString(cc.wulian.smarthomev5.R.string.device_human_traffic_statistics));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				manager.dismiss();
				getPlugin();
			}
		};

		if (isDeviceOnLine()) {
			items.add(deviceIntroductionItem);
//			items.add(settingItem);
		}
		return items;
	}

	@Override
	public String unit(String ep, String epType) {
		return "PPB";
	}

	@Override
	public String unitName() {
		return mResources.getString(R.string.device_voc);
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, AutoConditionInfo autoConditionInfo,
			boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		SensorVOCChooseView sensorVOCChooseView = new SensorVOCChooseView(
				inflater.getContext());
		sensorVOCChooseView.setmSensorDeviceValues(autoConditionInfo.getExp(),
				autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(sensorVOCChooseView.getView());
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
										"voc.html");
								String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
								if (file.exists()) {
									uri = "file:///" + file.getAbsolutePath();
								} else if (LanguageUtil.isChina()) {
									uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
								}
								final String uriString = uri;
								Preference.getPreferences().saveVocHtmlUri(uri);
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