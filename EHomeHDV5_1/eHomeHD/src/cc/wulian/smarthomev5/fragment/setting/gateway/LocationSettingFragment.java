package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;

import java.io.File;

import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class LocationSettingFragment extends WulianFragment implements H5PlusWebViewContainer{

	private H5PlusWebView webView;
	private View rootView;

	private final String HTML_BASEURI = "file:///android_asset/gwsettinglocation/";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(cc.wulian.app.model.device.R.layout.location_setting_layout, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		webView = (H5PlusWebView) view.findViewById(cc.wulian.app.model.device.R.id.location_setting_webview);
		initBar();
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		Engine.bindWebviewToContainer(this, webView);
		webView.setWebviewId("Control_Center_Setting_Location");
		webView.loadUrl(HTML_BASEURI+"controlCentrelLocation.html");
	}

	private void initBar() {
		getSupportActionBar().hide();
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
