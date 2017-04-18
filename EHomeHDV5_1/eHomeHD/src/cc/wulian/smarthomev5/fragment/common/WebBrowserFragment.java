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
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.view.ProgressWebView;

@SuppressLint("NewApi")
public class WebBrowserFragment extends WulianFragment {

	public static final String URL = "url";
	public static final String TITLE = "title";
	public static final String LEFT_ICON_TEXT = "left_icon_text";
	private ProgressWebView webView;
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
		View view = inflater.inflate(R.layout.activity_advertise_webview,
				container, false);
		webView = (ProgressWebView) view
				.findViewById(R.id.activity_adver_webview);
		webView.setWebViewClient(new MyWebViewClient());
		// webView.setWebChromeClient(new WebViewChromeClientDemo());
		// 设置支持javascript脚本
		webView.getSettings().setJavaScriptEnabled(true);
		// 允许访问文件
		webView.getSettings().setAllowFileAccess(true);
		// 支持缩放
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true); // 设置显示缩放按钮
		webView.getSettings().setDisplayZoomControls(false);// 隐藏webView缩放按钮
		// 双击后变小，双击后变初始
		webView.getSettings().setUseWideViewPort(true);
		// 适应屏幕，内容将自动缩放(适应显示图片)
		// webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.setInitialScale(39);
		webView.loadUrl(url);

		// 如果需要下载文件，需要设置setDownloadListener方法
		webView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}

		});
		webView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK&& event.getAction() == KeyEvent.ACTION_UP) {
					if (webView.canGoBack()) {
						webView.goBack();
						return true;
					} else {
						webView.destroy();
						getActivity().finish();
					}
				}
				return false;
			}
		});
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
