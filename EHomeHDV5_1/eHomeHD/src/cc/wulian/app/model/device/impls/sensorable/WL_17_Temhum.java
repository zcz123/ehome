package cc.wulian.app.model.device.impls.sensorable;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import cc.wulian.app.model.device.interfaces.LinkTaskTempHumView;
import cc.wulian.app.model.device.interfaces.TemhumWheelView;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
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
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * float,float(单位℃,%RH) <br/>
 * epType：温度 1702， 湿度 1703 <br/>
 * 
 * 人体最适宜的温度在18℃－24℃，人体最适宜的健康湿度在45％RH－65％RH之间；湿度低于35％RH 湿度高于50％RH<br/>
 * 
 * <b>Chang Log</b> <br/>
 * 1.去除报警等级划分
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_TEMHUM }, category = Category.C_ENVIRONMENT)
public class WL_17_Temhum extends SensorableDeviceImpl {
	private static final String UNIT_DOU = ",";
	private static final String UNIT_SPACE = "\b\b";
	private static final String UNIT_POSITIVE = "+";
	private static final String UNIT_NEGATIVE = "-";
	public static final String UNIT_C = "\u2103";
	public static final String UNIT_RH = "%RH";

	private boolean positive = false;
	public CustomProgressBar_270 mCustomViewTem;
	public CustomProgressBar_360 mCustomViewHem;
	public TextView mNumTextTem;
	public ImageView mImageViewTem;
	public TextView mNumTextHem;
	public ImageView mImageViewHem;
	public TextView mUnitTem;
	public TextView mUnitHem;
	
	private final static String ALREADY_GO_TO_DEVICE_HTML="already_go_to_device_html";
	private final static String NOT_GO_TO_DEVICE_HTML="not_go_to_device_html";
	private static String status=NOT_GO_TO_DEVICE_HTML;

	private final String[] mTemhumValue = new String[2];

	private int deviceIdTem = R.drawable.device_progerss_light_17_tem;
	private int deviceIdHem = R.drawable.device_progerss_360;
	
	private String pluginName="environmental.zip";
	
	private H5PlusWebView webView;

	public WL_17_Temhum(Context context, String type) {
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
		if (!Preference.getPreferences().getTemhumHtmlUri().equals("noUri")) {
			getPlugin();
		}else{
			getPlugin();
		}
	}

//	@Override
//	public void onResume() {
//		getPlugin();
//	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		if (!isNull(epData) && epData.contains(UNIT_DOU)) {
			String curTemp  = "0";
			String[] temp = epData.split(UNIT_DOU, 2);
			if(temp[0].startsWith("-")||temp[0].startsWith("+")){
				curTemp = temp[0].substring(1);
			}else{
				curTemp = temp[0];
			}
			
			String roundTemp = curTemp;
			String roundHumidity = temp[1];
			if(temp[0].contains(UNIT_NEGATIVE)){
				sb.append(UNIT_NEGATIVE);
				sb.append(roundTemp);
			} else {
				sb.append(roundTemp);
			}
			sb.append(UNIT_C);
			sb.append(UNIT_SPACE);
			sb.append(UNIT_DOU);
			sb.append(UNIT_SPACE);
			sb.append(roundHumidity);
			sb.append(UNIT_RH);
			return sb;
		}
		return null;
	}

	@Override
	public synchronized void onDeviceData(String gwID, String devID,
										  DeviceEPInfo devEPInfo, String cmd, String mode) {
		super.onDeviceData(gwID,devID,devEPInfo,cmd,mode);
	}

//	@Override
//	public void initViewStatus() {
//		super.initViewStatus();
//		if (!isNull(epData) && epData.contains(UNIT_DOU)) {
//			String[] temp = epData.split(UNIT_DOU);
//			if (temp.length != 2) {
//				return;
//			}
//			mTemhumValue[0] = temp[0];
//			mTemhumValue[1] = temp[1];
//			if (mTemhumValue[0].contains(UNIT_POSITIVE)) {
//				positive = true;
//				mTemhumValue[0] = mTemhumValue[0].replace(UNIT_POSITIVE, "");
//			} else if (mTemhumValue[0].contains(UNIT_NEGATIVE)) {
//				positive = false;
//				mTemhumValue[0] = mTemhumValue[0].replace(UNIT_NEGATIVE, "");
//			} else {
//				positive = true;
//			}
//		} else {
//			return;
//			// mTemhumValue[0] = UNIT_NA;//温度
//			// mTemhumValue[1] = UNIT_NA;//湿度
//		}
//
//		double angleUnitTem = 100.0 / 270.0;
//		double curNumTem = Double.parseDouble(mTemhumValue[0]);
//
//		double degreeTem = 0.0;
//		if (!positive) {
//			degreeTem = 135 - curNumTem / angleUnitTem;
//		} else {
//			degreeTem = curNumTem / angleUnitTem + 135;
//		}
//		double angleUnitHem = 100.0 / 360.0;
//		double curNumHem = Double.parseDouble(mTemhumValue[1]);
//		double degreeHem = curNumHem / angleUnitHem;
//
//		if (degreeTem > 270) {
//			degreeTem = 270;
//		} else if (degreeTem < 0) {
//			degreeTem = 0;
//		}
//		if (degreeHem > 360) {
//			degreeHem = 360;
//		} else if (degreeHem < 0) {
//			degreeHem = 0;
//		}
//		if (!positive) {
//			mNumTextTem.setText(UNIT_NEGATIVE + mTemhumValue[0] + UNIT_C);
//			mCustomViewTem.setDegree(degreeTem, deviceIdTem);
//		} else {
//			mNumTextTem.setText(UNIT_POSITIVE + mTemhumValue[0] + UNIT_C);
//			mCustomViewTem.setDegree(degreeTem, deviceIdTem);
//		}
//
//		mNumTextHem.setText(mTemhumValue[1] + UNIT_RH);
//		mCustomViewHem.setDegree(degreeHem, deviceIdHem);
//		mUnitTem.setText(getString(R.string.device_tempure));
//		mUnitHem.setText(getString(R.string.device_humidity));
//	}

	public String getTempValue() {
		return mTemhumValue[0];
	}

	public String getHumValue() {
		return mTemhumValue[1];
	}

	@Override
	public String unit(String ep,String epType) {
		if(StringUtil.equals(epType, "1702")){
			return UNIT_C;
		}else if(StringUtil.equals(epType, "1703")){
			return UNIT_RH;
		}else{
			return null;
		}
	}

	@Override
	public String unitName() {
		return mResources.getString(R.string.device_tempure);
	}

	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		AbstractLinkTaskView taskView = new LinkTaskTempHumView(context,
				taskInfo);
		taskView.onCreateView();
		return taskView;
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, final AutoConditionInfo autoConditionInfo,boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		TemhumWheelView temhumWheelView = new TemhumWheelView(inflater.getContext());
		temhumWheelView.setmSensorDeviceValues(autoConditionInfo.getExp(),autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(temhumWheelView.getView());
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
						File file=new File(model.getFolder(),"tah.html");
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri="file:///"+file.getAbsolutePath();
						}else if(LanguageUtil.isChina()){
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}	
						final String uriString = uri;
						Preference.getPreferences().saveTemhumHtmlUri(uri);
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