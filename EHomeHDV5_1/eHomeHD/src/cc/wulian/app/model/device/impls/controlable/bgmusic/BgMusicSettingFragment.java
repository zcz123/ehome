package cc.wulian.app.model.device.impls.controlable.bgmusic;

import java.io.File;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.CmdControlFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class BgMusicSettingFragment extends WulianFragment implements
		H5PlusWebViewContainer {

	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";

	private String gwID;
	private String devID;

	private H5PlusWebView webView;

	private LinearLayout mLinearLayout;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gwID = getArguments().getString(GWID);
		devID = getArguments().getString(DEVICEID);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.device_background_music_setting,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		webView = (H5PlusWebView) view.findViewById(R.id.bg_ground_webview);
		webView.setWebviewId("BackGroundMusicSetting");
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID,
				this.devID);
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID,
				this.gwID);
		Engine.bindWebviewToContainer((H5PlusWebViewContainer) this, webView);
		/*if (!Preference.getPreferences().getBackgroundMusicHtmlUri()
				.equals("noUri")) {
			*//*webView.loadUrl(Preference.getPreferences()
					.getBackgroundMusicHtmlUri() + "/musicSetting.html");*//*
		} else {
			getPlugin("musicSetting.html", "BackGroundMusicSetting");
		}*/
		getPlugin("musicSetting.html", "BackGroundMusicSetting");
	}

	private String pluginName = "BackGroundMusic.zip";

	private void getPlugin(final String urlName, final String htmlID) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(mActivity, pluginName,
						new PluginsManagerCallback() {

							@Override
							public void onGetPluginSuccess(PluginModel model) {
								File file = new File(model.getFolder(), urlName);
								String backgroundMusicDir = "file:///"
										+ file.getParent();
								Preference.getPreferences()
										.saveBackgroundMusicHtmlUri(
												backgroundMusicDir);
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
										webView.loadUrl(uriString);
//										H5PlusWebView wwebView = Engine
//												.createWebView(webView,
//														uriString, htmlID);
//										wwebView.getContainer()
//												.getContainerRootView()
//												.removeAllViews();
//										ViewGroup viewGroup = wwebView
//												.getContainer()
//												.getContainerRootView();
//										wwebView.onRootViewGlobalLayout(
//												viewGroup, "", "");
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
											Toast.makeText(mActivity, hint,
													Toast.LENGTH_SHORT).show();
										}
									});
								}
							}
						});
			}
		}).start();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle("设置");
		getSupportActionBar().setRightIcon(null);
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {
					@Override
					public void onClick(View v) {
						Engine.removeWebview(webView);
						webView.close();
						mActivity.finish();
					}
				});
	}

	@Override
	public void addH5PlusWebView(H5PlusWebView webview) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyContainer() {
		// TODO Auto-generated method stub
		Engine.destroyPager(this);
		this.getActivity().finish();
	}

	@Override
	public ViewGroup getContainerRootView() {
		// TODO Auto-generated method stub
		return mLinearLayout;
	}

}
