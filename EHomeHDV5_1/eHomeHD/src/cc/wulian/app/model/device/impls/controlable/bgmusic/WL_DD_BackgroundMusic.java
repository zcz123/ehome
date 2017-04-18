package cc.wulian.app.model.device.impls.controlable.bgmusic;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectControlDeviceDataFragment;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectControlDeviceDataFragment.ActionBarClickRightListener;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.LanguageUtil;

@DeviceClassify(devTypes = { "DD" }, category = Category.C_OTHER)
public class WL_DD_BackgroundMusic extends ControlableDeviceImpl{

	private H5PlusWebView webView;
	private TextView textView;
	private String pluginName = "BackGroundMusic.zip";

	public WL_DD_BackgroundMusic(Context context, String type) {
		super(context, type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle saveState) {
		View view = inflater.inflate(R.layout.device_background_music, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		webView = (H5PlusWebView) view.findViewById(R.id.bg_ground_webview);
		textView= (TextView) view.findViewById(R.id.search_tv);
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
		Engine.bindWebviewToContainer((H5PlusWebViewContainer)this.getCurrentFragment(), webView);
		if (!Preference.getPreferences().getBackgroundMusicHtmlUri().equals("noUri")) {
			textView.setVisibility(View.GONE);
			webView.loadUrl(Preference.getPreferences().getBackgroundMusicHtmlUri()+"/musicIndex.html");
			getPlugin("musicIndex.html","BackGroundMusicIndex",mContext);
		}else{
			getPlugin("musicIndex.html","BackGroundMusicIndex",mContext);
		}
	}

	@Override
	public void registerEPDataToHTML(H5PlusWebView pWebview, String callBackId,String cmd) {
//		super.registerEPDataToHTML(pWebview,callBackId);
//		setCallbackID(callBackId);
//		setpWebview(pWebview);
		this.pWebview = pWebview;
		if(mapCallbackID==null){
			mapCallbackID=new ArrayMap<>();
		}
		mapCallbackID.put(cmd,callBackId);

	}

	@Override
	public synchronized void onDeviceData(String gwID, String devID, DeviceEPInfo devEPInfo, String cmd, String mode) {
		if (!mDeviceCreated)
			return;
		String ep = devEPInfo.getEp();
		DeviceEPInfo epInDevice = getDeviceInfo().getDevEPInfoByEP(ep);
		// add_by_yanzy_at_2016-6-21:当设备EP分多次上报时，应该能动态生成相应的EP，否则会导致超过14的EP无法控制。
		Log.d("WL_62", "onDeviceUp: Ep1="+devEPInfo.getEp()+" EpData1="+devEPInfo.getEpData());
		if (epInDevice == null && childDeviceMap != null) {
			getDeviceInfo().getDeviceEPInfoMap().put(ep, devEPInfo);
			this.addSubdevice(getDeviceInfo(), devEPInfo);
		}else {
			if(!StringUtil.isNullOrEmpty(devEPInfo.getEpData())){
				if(epInDevice!=null){
					epInDevice.setEpData(devEPInfo.getEpData());
				}
			}
		}
		String oldEpType=mCurrentEpInfo.getEpType();
		String oldEpData=mCurrentEpInfo.getEpData();
		mCurrentEpInfo = devEPInfo;
		if(StringUtil.isNullOrEmpty(mCurrentEpInfo.getEpType())){
			mCurrentEpInfo.setEpType(oldEpType);
		}
		if(StringUtil.isNullOrEmpty(mCurrentEpInfo.getEpData())){
			mCurrentEpInfo.setEpData(oldEpData);
		}
		getDeviceInfo().setDevEPInfo(mCurrentEpInfo);
		fireDeviceRequestControlData();
		refreshDevice();
		removeCallbacks(mRefreshStateRunnable);
		post(mRefreshStateRunnable);
		Log.d("run21IsSendEpdata","devID::"+devID+",cmd:"+cmd);
		String data="";
		if(!StringUtil.isNullOrEmpty(devEPInfo.getEpData())){
			data=devEPInfo.getEpData();
		}
		if(isCallBackWithEp(cmd,mode)){
//			data = data + devEPInfo.getEp();
			data = data +"-"+ devEPInfo.getEp()+"-"+devEPInfo.getEpName();
		}
		String callbackID="";
		if(pWebview!=null){
			List<H5PlusWebView> webViewList=Engine.getWebviewList((H5PlusWebViewContainer)this.getCurrentFragment());
			for(H5PlusWebView webView:webViewList){
				callbackID=webView.getCallbackID();
				JsUtil.getInstance().execCallback(webView, callbackID, data, JsUtil.OK, false);
				Log.d("run21IsSendEpdata","callbackID::"+callbackID);
			}
		}
		callbackID=getCallBackId(cmd,devEPInfo.getEp(),mode,devID);
		if(pWebview!=null&& callbackID!=null&&callbackID.equals("BackGroundMusicSetting")){
			JsUtil.getInstance().execCallback(pWebview, callbackID, data, JsUtil.OK, false);
		}
	}

	@Override
	protected List<MenuItem> getDeviceMenuItems(
			final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.set_titel));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				Intent intent = getSettingIntent();
				mContext.startActivity(intent);
				manager.dismiss();
			}
		};
		if (isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	protected Intent getSettingIntent() {
		Intent intent = new Intent(mContext, DeviceSettingActivity.class);
		intent.putExtra(BgMusicSettingFragment.GWID, gwID);
		intent.putExtra(BgMusicSettingFragment.DEVICEID, devID);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
				BgMusicSettingFragment.class.getName());
		return intent;
	}
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, AutoActionInfo autoActionInfo) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setShowDialog(false);
		if(HouseKeeperSelectControlDeviceDataFragment.isShowHouseKeeperSelectControlDeviceDataView){
			HouseKeeperSelectControlDeviceDataFragment.isShowHouseKeeperSelectControlDeviceDataView=false;
			View contentView = inflater.inflate(R.layout.device_background_music,
					null);
			webView=(H5PlusWebView) contentView.findViewById(R.id.bg_ground_webview);
			textView= (TextView) contentView.findViewById(R.id.search_tv);
			if(autoActionInfo.getEpData()!=null&&autoActionInfo.getEpData().equals("")){
				SmarthomeFeatureImpl.setData("kBulterEPData", "");
			}else if(autoActionInfo.getEpData()!=null){
				SmarthomeFeatureImpl.setData("kBulterEPData", autoActionInfo.getEpData());
			}
			webView.setWebviewId("BackGroundMusicBulter");
			SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
			SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
			Engine.bindWebviewToContainer((H5PlusWebViewContainer)this.getCurrentFragment(), webView);
			if (!Preference.getPreferences().getBackgroundMusicHtmlUri().equals("noUri")) {
				textView.setVisibility(View.GONE);
				webView.loadUrl(Preference.getPreferences().getBackgroundMusicHtmlUri()+"/musicbulter.html");
				getPlugin("musicbulter.html","BackGroundMusicBulter",DeviceSettingActivity.instance);
			}else{
				getPlugin("musicbulter.html","BackGroundMusicBulter",DeviceSettingActivity.instance);
			}

			holder.setContentView(contentView);
			HouseKeeperSelectControlDeviceDataFragment.setActionBarClickRightListener(new ActionBarClickRightListener() {

				@Override
				public void doSomething(AutoActionInfo autoActionInfo) {
					JSONObject jsonObject=JSONObject.parseObject(SmarthomeFeatureImpl.getData("kBackGroundMusicBulterJson", ""));
					String epData=jsonObject.toString();
					autoActionInfo.setEpData(epData);
					SmarthomeFeatureImpl.setData("kBulterEPData", epData);
				}
			});
		}
		return holder;
	}

	private void getPlugin(final String urlName, final String htmlID, final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(context, pluginName,
						new PluginsManagerCallback() {

							@Override
							public void onGetPluginSuccess(PluginModel model) {
								/*if((!Preference.getPreferences().getBackgroundMusicHtmlUri().equals("noUri"))){
									return;
								}*/
								textView.setVisibility(View.GONE);
								File file = new File(model.getFolder(),
										urlName);
								String backgroundMusicDir="file:///"+file.getParent();
								Preference.getPreferences().saveBackgroundMusicHtmlUri(backgroundMusicDir);
								String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
								if (file.exists()) {
									uri = "file:///" + file.getAbsolutePath();
								} else if (LanguageUtil.isChina()) {
									uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
								}
								final String uriString = uri;

								Handler handler = new Handler(Looper
										.getMainLooper());
								handler.post(new Runnable() {
									@Override
									public void run() {
										webView.loadUrl(Preference.getPreferences().getBackgroundMusicHtmlUri()+"/"+urlName);
									}
								});
							}

							@Override
							public void onGetPluginFailed(final String hint) {
								if((!Preference.getPreferences().getBackgroundMusicHtmlUri().equals("noUri"))){
									return;
								}
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

	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		if (item == null) {
			item = new ShortCutDesktopCameraSelectDataItem(
					inflater.getContext());
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}

	public static class ShortCutDesktopCameraSelectDataItem extends
			DeviceShortCutSelectDataItem {

		protected LinearLayout defaultLineLayout;

		public ShortCutDesktopCameraSelectDataItem(Context context) {
			super(context);
			controlLineLayout.removeAllViews();
		}

		@Override
		public void setWulianDeviceAndSelectData(final WulianDevice device,
												 final AutoActionInfo autoActionInfo) {
			super.setWulianDeviceAndSelectData(device, autoActionInfo);
		}


	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new ControlableDeviceShortCutControlItem(
					inflater.getContext());
		}
		item.setWulianDevice(this);
		return item;
	}

	public class ControlableDeviceShortCutControlItem extends
			DeviceShortCutControlItem {

		public ControlableDeviceShortCutControlItem(Context context) {
			super(context);
			controlLineLayout.removeAllViews();;
		}
	}
	@Override
	public boolean run21IsSendEpdata(String cmd,String ep,String mode,String devID){
		if(cmd.equals("21")){
			return false;
		}else {
			return true;
		}
	}

	@Override
	public boolean isHouseKeeperSelectControlDeviceActionBarUseable() {
		return true;
	}
}
