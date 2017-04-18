package cc.wulian.h5plus.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.common.SysEventType;
import cc.wulian.h5plus.common.client.IWebChromeClient;
import cc.wulian.h5plus.common.client.IWebViewClient;
import cc.wulian.h5plus.feature.Bridge;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.utils.H5PlusConstants;

/**
 * Created by Administrator on 2015/11/20.
 */
@SuppressLint("NewApi")
public class H5PlusWebView extends WebView {
	private static AtomicLong nextWebViewPagerId = new AtomicLong(0);

	private String webviewId;

	private H5PlusWebViewContainer container;

	private Set<String> listenedEvent = new HashSet<String>();

	private Map<String, H5PlusWebView> subWebViewMap = new HashMap<String, H5PlusWebView>();

	private H5PlusWebView parent = null;

	private String url = null;
	private String callbackID="";

	public String getCallbackID() {
		return callbackID;
	}

	public void setCallbackID(String callbackID) {
		this.callbackID = callbackID;
	}

	public static void enableDebug() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
	}

	public static void disableDebug() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(false);
		}
	}
	
	public H5PlusWebView(Context context) {
		super(context);
		init();
	}

	public H5PlusWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public H5PlusWebView(H5PlusWebView parent, String url, String id) {
		super(parent.getContext());
		init();
		this.parent = parent;
		this.url = url;
		this.webviewId = id;
		parent.addSub(this.webviewId, this);
	}

	private void init() {
		this.webviewId = String.valueOf(nextWebViewPagerId.incrementAndGet());
		setSettings();
	}

	public void setContainer(H5PlusWebViewContainer container) {
		this.container = container;
	}

	public H5PlusWebViewContainer getContainer() {
		return container;
	}

	public String getWebviewId() {
		return webviewId;
	}
	
	public void setWebviewId(String webviewId) {
		this.webviewId = webviewId;
	}

	public void registerEvent(String event) {
		listenedEvent.add(event);
	}

	public boolean isEventListened(String event) {
		return listenedEvent.contains(event);
	}

	public void unregisterEvent(String event) {
		listenedEvent.remove(event);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setSettings() {
		// 设置支持javascript脚本
		getSettings().setJavaScriptEnabled(true);
		// 允许访问文件
		getSettings().setAllowFileAccess(true);
		// 支持缩放
		getSettings().setSupportZoom(true);
		getSettings().setBuiltInZoomControls(true); // 设置显示缩放按钮
		getSettings().setDisplayZoomControls(false);// 隐藏webView缩放按钮
		getSettings().setSaveFormData(false);
		// 双击后变小，双击后变初始
		getSettings().setUseWideViewPort(true);
		getSettings().setLoadWithOverviewMode(true);
		setInitialScale(39);
		getSettings().setUserAgentString(getSettings().getUserAgentString());
		getSettings().setDomStorageEnabled(true);//使浏览器数据库允许使用
		// 添加代理
		setWebViewClient(new IWebViewClient());
		setWebChromeClient(new IWebChromeClient());
		// 添加接口
		this.addJavascriptInterface(new Bridge(this), "bridge");

	}

	public void addSub(String id, H5PlusWebView iWebView) {
		subWebViewMap.put(id, iWebView);
	}

	public List<H5PlusWebView> getAllWebView() {
		List<H5PlusWebView> list = new ArrayList<H5PlusWebView>();
		list.add(this);
		Iterator<Map.Entry<String, H5PlusWebView>> iterator = subWebViewMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, H5PlusWebView> entry = iterator.next();
			list.addAll(entry.getValue().getAllWebView());
		}
		return list;
	}

	public void onRootViewGlobalLayout(ViewGroup rootView, String width, String height) {
		int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
		int mHeight = ViewGroup.LayoutParams.MATCH_PARENT;
		try {
			if(width!=null&&!width.isEmpty()){
				mWidth = Integer.parseInt(width);
			}
			if(height!=null&&!height.isEmpty()){
				mHeight = Integer.parseInt(height);
			}
		} catch (Exception e) {
			Log.e(H5PlusConstants.TAG, "", e);
		}
		this.onRootViewGlobalLayout(rootView, mWidth, mHeight);
	}

	public void onRootViewGlobalLayout(final ViewGroup rootView, final int width, final int height) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
//				ViewGroup parent = (ViewGroup) H5PlusWebView.this.getParent();
//				if (parent != null) {
//					parent.removeAllViews();
//				}
				ViewGroup viewGroup= (ViewGroup) rootView.getParent();
				viewGroup.addView(H5PlusWebView.this, layoutParams);
				H5PlusWebView.this.loadUrl(url);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean _ret = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			_ret = JsUtil.getInstance().fireJsEventListener(this, SysEventType.KEYCODE_MENU.getValue(),
					String.valueOf(keyCode));
			break;
		case KeyEvent.KEYCODE_HOME:
			_ret = JsUtil.getInstance().fireJsEventListener(this, SysEventType.KEYCODE_HOME.getValue(),
					String.valueOf(keyCode));
			break;
		case KeyEvent.KEYCODE_BACK:
			_ret = JsUtil.getInstance().fireJsEventListener(this, SysEventType.KEYCODE_BACK.getValue(),
					String.valueOf(keyCode));
			if(_ret == false && this.canGoBack()) {
				this.goBackOrForward(-1);
			}
			break;
		}
		if (_ret) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean _ret = JsUtil.getInstance().fireJsEventListener(this, SysEventType.onKeyUp.getValue(),
				String.valueOf(keyCode));
		if (_ret) {
			return true;
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		boolean _ret = JsUtil.getInstance().fireJsEventListener(this, SysEventType.onKeyLongPress.getValue(),
				String.valueOf(keyCode));
		if (_ret) {
			return true;
		} else {
			return super.onKeyLongPress(keyCode, event);
		}
	}

	public void show() {
		setVisibility(View.VISIBLE);
	}

	public void hide() {
		setVisibility(View.INVISIBLE);
	}

	public void close() {
		this.post(new Runnable() {

			@Override
			public void run() {
				if(getParent()!=null){
					((ViewGroup) getParent()).removeView(H5PlusWebView.this);
				}
				destroy();
			}
		});
	}

	@Override
	public void destroy() {
//		super.destroy();
		this.post(new Runnable() {

			@Override
			public void run() {

				for (H5PlusWebView webView : subWebViewMap.values()) {
					webView.destroy();
				}
				subWebViewMap.clear();
				if (parent != null) {
					parent.subWebViewMap.remove(getWebviewId());
				}
				ViewGroup root = (ViewGroup)H5PlusWebView.this.getParent();
				if(root != null) {
					root.removeView(H5PlusWebView.this);
				}
				Engine.removeWebview(H5PlusWebView.this);
				H5PlusWebView.super.destroy();

			}
		});
	}
}
