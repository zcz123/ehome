package cc.wulian.smarthomev5.fragment.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.view.ProgressWebView;

@SuppressLint("NewApi")
public class Html5PlusWebViewV2Fragment extends WulianFragment  {

	public static final String URL = "url";
	public static final String TITLE = "title";
	public static final String LEFT_ICON_TEXT = "left_icon_text";
	private H5PlusWebView webView;
	private String url;
	private String leftIconText;
	private String title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getActivity().getIntent().getExtras();
		url = bundle.getString(URL);
		leftIconText = bundle.getString(LEFT_ICON_TEXT);
		title = bundle.getString(TITLE);
		initBar();
	}

	protected void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if (!StringUtil.isNullOrEmpty(leftIconText))
			getSupportActionBar().setIconText(leftIconText);
		if (!StringUtil.isNullOrEmpty(title))
			getSupportActionBar().setTitle(title);
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {

			@Override
			public void onClick(View v) {
				if(webView.canGoBack()){
					webView.goBack();
				}else{
					getActivity().finish();
					webView.destroy();
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.html5plus_single_view,
				container, false);
		webView = (H5PlusWebView) view
				.findViewById(R.id.html5plus_view);
		webView.loadUrl(url);
		Engine.bindWebviewToContainer(this.mActivity, webView);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	// 重写shouldOverrideUrlLoading()当打开链接时，使用当前webView，不会使用系统其他浏览器
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			view.loadUrl(url);
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			String url = "file:///android_asset/disclaimer/error_page_404_en.html";
			if (LanguageUtil.isChina()){
				url = "file:///android_asset/disclaimer/error_page_404_zh.html";
			}
			view.loadUrl(url);

		}
	}

	// 与JavaScript的其他交互
	private class WebViewChromeClientDemo extends WebChromeClient {

		// 设置网页加载的进度条
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);
		}

		// 获取网页的标题
		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
		}

		// JavaScript弹出框
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
								 JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsAlert(view, url, message, result);
		}

		// JavaScript确认框
		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
								   JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsConfirm(view, url, message, result);
		}

		// JavaScript输入框
		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
								  String defaultValue, JsPromptResult result) {
			// TODO Auto-generated method stub
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}

	}
}
