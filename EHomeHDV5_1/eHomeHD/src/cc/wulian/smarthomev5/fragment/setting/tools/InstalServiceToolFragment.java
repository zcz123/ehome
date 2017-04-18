package cc.wulian.smarthomev5.fragment.setting.tools;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class InstalServiceToolFragment extends WulianFragment implements H5PlusWebViewContainer{

	private H5PlusWebView webView;
	private View rootView;
	private TextView textView;

	private String pluginName = "InstallTool.zip";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.instal_service_tool_fragment_layout, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		webView = (H5PlusWebView) view.findViewById(R.id.intal_service_tool_webview);
		textView= (TextView) view.findViewById(R.id.intal_service_tool_tv);
		initBar();
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		webView.setWebviewId("InstallToolIndex");
		Engine.bindWebviewToContainer(this, webView);
		webView.loadUrl("file:///android_asset/InstallTool/toolIndex.html");
//		getPlugin();
	}

	private void initBar() {
		getSupportActionBar().hide();
	}

	private void getPlugin() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(mActivity, pluginName,
						new PluginsManager.PluginsManagerCallback() {

							@Override
							public void onGetPluginSuccess(PluginModel model) {
								/*if((!Preference.getPreferences().getManagerGatewayUri().equals("noUri"))){
									return;
								}*/

								textView.setVisibility(View.GONE);
								File file = new File(model.getFolder(),
										"toolIndex.html");
								String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
								if (file.exists()) {
									uri = "file:///" + file.getAbsolutePath();
								} else if (LanguageUtil.isChina()) {
									uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
								}
								final String uriString = uri;
								Preference.getPreferences().saveInstalServiceToolUri(uri);
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
								if((!Preference.getPreferences().getInstalServiceToolUri().equals("noUri"))){
									return;
								}
								if (hint != null && hint.length() > 0) {
									Handler handler = new Handler(Looper
											.getMainLooper());
									handler.post(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(mActivity, hint,
													Toast.LENGTH_SHORT).show();
										}
									});
								}
								mActivity.finish();
							}
						});
			}
		}).start();
	}


	@Override
	public void addH5PlusWebView(H5PlusWebView webview) {

	}

	@Override
	public void destroyContainer() {
		Engine.destroyPager(this);
		this.getActivity().finish();
	}

	@Override
	public ViewGroup getContainerRootView() {
		return (ViewGroup) rootView;
	}
}
