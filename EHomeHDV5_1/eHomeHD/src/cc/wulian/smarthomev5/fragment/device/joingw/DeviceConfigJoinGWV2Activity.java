package cc.wulian.smarthomev5.fragment.device.joingw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yuantuo.customview.ui.CustomProgressDialog;

import java.io.File;

import cc.wulian.app.model.device.impls.controlable.WL_Oa_Rangehood;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.activity.WebBrowserActivity;
import cc.wulian.smarthomev5.event.JoinGatewayEvent;
import cc.wulian.smarthomev5.fragment.common.WebBrowserFragment;
import cc.wulian.smarthomev5.fragment.device.DeviceActionBarManager;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.URLConstants;
import de.greenrobot.event.EventBus;

public class DeviceConfigJoinGWV2Activity extends EventBusActivity {

	private View contentView;
	private LayoutInflater inflater;
	private LinearLayout layout_addZigbee;
	private LinearLayout layout_addWifi;
	public static final String KEY_JOIN_GW_DIALOG = "KEY_JOIN_GW_DIALOG";
	private String wifiWebPagePath="file:///android_asset/AddDevice/addWifiDevice.html";
	private String helpWebPagePath="file:///android_asset/AddDevice/help.html";
	private static boolean isUsePlugin=true;
	private String pluginName="AddDevice.zip";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		initView();
	}

	private void initView(){
		inflater= LayoutInflater.from(this);
		contentView=inflater.inflate(R.layout.fragment_guide_device_join_gwv2,null);
		setContentView(contentView);
		layout_addZigbee= (LinearLayout) contentView.findViewById(R.id.layout_addZigbee);
		layout_addWifi= (LinearLayout) contentView.findViewById(R.id.layout_addWifi);
		layout_addZigbee.setOnClickListener(onClick_addZigbee);
		layout_addWifi.setOnClickListener(onClick_addWifi);
	}
	public void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.nav_device_title));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.device_common_new_hint));
		getCompatActionBar().setDisplayShowMenuTextEnabled(true);
		//帮助
		getCompatActionBar().setRightIconText(getString(R.string.device_config_edit_dev_help));
		getCompatActionBar().setRightMenuClickListener(onClick_RightMenu);
	}
	View.OnClickListener onClick_addWifi=new View.OnClickListener(){

		@Override
		public void onClick(View view) {
			if(isUsePlugin){
				getPlugin("addWifiDevice.html","AddDevice",DeviceConfigJoinGWV2Activity.this);
			}else{
				Intent intent = new Intent();
				intent.setClass(DeviceConfigJoinGWV2Activity.this, Html5PlusWebViewActvity.class);
				intent.putExtra(Html5PlusWebViewActvity.KEY_URL, wifiWebPagePath);
				DeviceConfigJoinGWV2Activity.this.startActivity(intent);
			}
		}
	};
	View.OnClickListener onClick_addZigbee=new View.OnClickListener(){
		@Override
		public void onClick(View view) {
		AccountManager accountManager = AccountManager
						.getAccountManger();
				String gwID = accountManager.getmCurrentInfo().getGwID();
				NetSDK.sendPermitDevJoinMsg(gwID, null, "250");
				ProgressDialogManager dialogManager = ProgressDialogManager
						.getDialogManager();
//				getRunningActivityName();
				dialogManager.showDialog(KEY_JOIN_GW_DIALOG, DeviceConfigJoinGWV2Activity.this,
						getString(R.string.device_guide_join_gw_hint),
						dialogListener);
		}
	};
	ActionBarCompat.OnRightMenuClickListener onClick_RightMenu=new ActionBarCompat.OnRightMenuClickListener(){
		@Override
		public void onClick(View view) {
			if(isUsePlugin){
				getPlugin("help.html","AddDevice",DeviceConfigJoinGWV2Activity.this);
			}else{
				Intent intent = new Intent();
				intent.setClass(DeviceConfigJoinGWV2Activity.this, Html5PlusWebViewActvity.class);
				intent.putExtra(Html5PlusWebViewActvity.KEY_URL, helpWebPagePath);
				DeviceConfigJoinGWV2Activity.this.startActivity(intent);
			}
		}
	};

	private CustomProgressDialog.OnDialogDismissListener dialogListener = new CustomProgressDialog.OnDialogDismissListener() {

		@Override
		public void onDismiss(CustomProgressDialog progressDialog, int result) {
			if (result == -1) {
				jumpToJoinGWFailActivity();
			} else {
				jumpToJoinGWSuccessActivity();
			}

		}
	};
	private void jumpToJoinGWSuccessActivity() {
		Intent intent = new Intent();
		intent.setClass(DeviceConfigJoinGWV2Activity.this, DeviceGuideJoinGWSuccessActivity.class);
		startActivity(intent);

	}

	private void jumpToJoinGWFailActivity() {
		Intent intent = new Intent();
		intent.setClass(DeviceConfigJoinGWV2Activity.this, DeviceGuideJoinGWFailActivity.class);
		startActivity(intent);

	}
	public void onEventMainThread(JoinGatewayEvent event) {
		if (event != null) {
			ProgressDialogManager dialogManager = ProgressDialogManager
					.getDialogManager();
			dialogManager.dimissDialog(
					DeviceActionBarManager.KEY_JOIN_GW_DIALOG, 0);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		getCompatActionBar().show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getCompatActionBar().show();
		String binding_wifi_suc=SmarthomeFeatureImpl.getData("BINDING_WIFI_SUC","0");
		if(binding_wifi_suc.equals("1")){
			this.finish();
		}
	}

	private void getPlugin(final String urlName, final String htmlID, final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(context, pluginName,
						new PluginsManager.PluginsManagerCallback() {

							@Override
							public void onGetPluginSuccess(PluginModel model) {
								File file = new File(model.getFolder(),
										urlName);
								String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
								if (file.exists()) {
									uri = "file:///" + file.getAbsolutePath();
								} else if (LanguageUtil.isChina()) {
									uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
								}
								final String uriString = uri;
								Handler handler = new Handler(Looper.getMainLooper());
								handler.post(new Runnable() {
									@Override
									public void run() {
										Intent intent = new Intent();
										intent.setClass(DeviceConfigJoinGWV2Activity.this, Html5PlusWebViewActvity.class);
										intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uriString);
										DeviceConfigJoinGWV2Activity.this.startActivity(intent);
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
											Toast.makeText(DeviceConfigJoinGWV2Activity.this, hint,
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
