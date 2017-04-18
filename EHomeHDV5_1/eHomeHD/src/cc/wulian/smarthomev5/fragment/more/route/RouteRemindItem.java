package cc.wulian.smarthomev5.fragment.more.route;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.NFCActivity;
import cc.wulian.smarthomev5.activity.RouteRemindActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class RouteRemindItem extends AbstractSettingItem {
	
	private String pluginName="channel.zip";
	
	public RouteRemindItem(Context context) {
		super(context, R.drawable.account_information_router_icon, context.getResources()
				.getString(R.string.gateway_router_setting_wifi_channel));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.system_intent_right);
	}

	@Override
	public void doSomethingAboutSystem() {
		if (!Preference.getPreferences().getChannelUri().equals("noUri")) {
			String uri=Preference.getPreferences().getChannelUri();
			Intent intent= new Intent();
			intent.setClass(mContext, Html5PlusWebViewActvity.class);
			intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
			getPlugin();
		}else{
			getPlugin();
		}
	}

	private void getPlugin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm=PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext,pluginName,false,new PluginsManagerCallback() {
					
					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file=new File(model.getFolder(),model.getEntry());
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri="file:///"+file.getAbsolutePath();
							Preference.getPreferences().saveChannelUri(uri);
						}else if(LanguageUtil.isChina()){
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}	
						
						Intent intent= new Intent();	
						intent.setClass(mContext, Html5PlusWebViewActvity.class);
						intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
						mContext.startActivity(intent);
					}

					@Override
					public void onGetPluginFailed(final String hint) {
						if((!Preference.getPreferences().getChannelUri().equals("noUri"))){
							return;
						}
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
