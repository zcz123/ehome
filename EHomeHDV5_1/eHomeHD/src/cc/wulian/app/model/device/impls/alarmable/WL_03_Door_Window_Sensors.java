package cc.wulian.app.model.device.impls.alarmable;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.LanguageUtil;

@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_CONTACT}, category = Category.C_SECURITY)
public class WL_03_Door_Window_Sensors extends DefaultAlarmableDeviceImpl{
	
	private String pluginName = "DoorMagnetic.zip";

	public WL_03_Door_Window_Sensors(Context context, String type) {
		super(context, type);
	}
	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		Drawable normalStateDrawable = getDrawable(R.drawable.device_doorwin_normal_icon);
		drawables[0] = normalStateDrawable;
		return drawables;
	}
	@Override
	public Drawable getStateSmallIcon() {
		return getStateSmallIconDrawable(getDrawable(R.drawable.device_doorwin_disarm),getDrawable(R.drawable.device_doorwin_alarm));
	}
	
	@Override
	public CharSequence parseAlarmProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		sb.append(DeviceTool.getDeviceAlarmAreaName(this));
		sb.append(DeviceTool.getDeviceShowName(this));
		if(LanguageUtil.isChina() || LanguageUtil.isTaiWan()){
			sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
		}else{
			sb.append(" "+ mContext.getString(R.string.home_device_alarm_default_voice_detect) + " ");
		}
		sb.append(mContext.getString(R.string.home_device_alarm_type_03_voice));
		return sb.toString();
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		return mContext.getString(R.string.home_device_alarm_type_03_voice);
	}

	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_type_03_voice);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.home_device_alarm_type_03_voice_close);
	}
	
	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.device_human_traffic_statistics));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				getPlugin();
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	
	private void getPlugin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm=PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext,pluginName,new PluginsManagerCallback() {
					
					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file=new File(model.getFolder(),model.getEntry());
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri="file:///"+file.getAbsolutePath();
						}else if(LanguageUtil.isChina()){
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}	
						
						Intent intent= new Intent();	
						intent.setClass(mContext, Html5PlusWebViewActvity.class);
						intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
						mContext.startActivity(intent);
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
