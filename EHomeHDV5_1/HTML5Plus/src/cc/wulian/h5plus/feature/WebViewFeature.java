package cc.wulian.h5plus.feature;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2016/2/16 0016.
 */
public class WebViewFeature {

	// 取所有Webview窗口
	@JavascriptInterface
	public String all(H5PlusWebView pWebview, final String param) {
		try {
			JSONArray array = new JSONArray();
			List<H5PlusWebView> list = Engine.getWebviewList(pWebview.getContainer());
			for (H5PlusWebView webView : list) {
				array.put(webView.getId());
			}
			return array.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 创建新的Webview窗口
	// TODO::未测试
	@JavascriptInterface
	public void create(H5PlusWebView pWebview, final String param) {
		String callbackId = null;
		try {
			JSONArray array = new JSONArray(param);
			callbackId = array.getString(0);
			Engine.createWebView(pWebview, array.getString(1), array.getString(2));
			JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.OK, true);
		} catch (Exception e) {
			JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.ERROR, true);
			e.printStackTrace();
		}
	}

	// 获取当前窗口的WebviewObject对象
	@JavascriptInterface
	public String currentWebview(H5PlusWebView pWebview, final String param) {
		return pWebview.getWebviewId();
	}

	// 查找指定标识的WebviewObject窗口
	@JavascriptInterface
	public String getWebviewById(H5PlusWebView pWebview, final String param) {
		try {
			H5PlusWebView webview = Engine.getWebview(pWebview.getContainer(), param);
			if (webview != null) {
				return param;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获取应用首页WebviewObject窗口对象
	@JavascriptInterface
	public String getLaunchWebview(H5PlusWebView pWebview, final String param) {
		return pWebview.getWebviewId();
	}

	// 创建并打开Webview窗口 String url,String id,int width,int height
	@JavascriptInterface
	public void open(final H5PlusWebView pWebview, final String param) {
		String callbackId = null;
		try {
			final JSONArray array = new JSONArray(param);
			callbackId = array.getString(0);
			final String callbackString=callbackId;
			final String webViewId = array.getString(2);
			Handler handler=new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					H5PlusWebView webView;
					try {
						webView = Engine.createWebView(pWebview, array.getString(1), webViewId);
//						webView.getContainer().getContainerRootView().removeAllViews();
//						ViewGroup viewGroup;
//						if(webView.getContainer().getContainerRootView()!=null){
//							viewGroup=webView.getContainer().getContainerRootView();
//						}else{
//							viewGroup=pWebview;
//						}
						webView.onRootViewGlobalLayout(pWebview, array.getString(3), array.getString(4));
						webView.setFocusable(true);
						webView.setFocusableInTouchMode(true);
						webView.requestFocus();
						webView.requestFocusFromTouch();
						JsUtil.getInstance().execCallback(pWebview, callbackString, webViewId, JsUtil.OK, true);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.ERROR, true);
			e.printStackTrace();
		}
	}

	// 关闭Webview窗口
	@JavascriptInterface
	public void close(H5PlusWebView pWebview, final String param) {
		String callbackId = null;
		try {
			JSONArray array = new JSONArray(param);
			callbackId = array.getString(0);
			String webViewId = array.getString(1);
			H5PlusWebView webview = Engine.getWebview(pWebview.getContainer(), webViewId);
			if (webview != null) {
				H5PlusWebViewContainer act = webview.getContainer();
				Engine.destroyPager(act);
				act.destroyContainer();
				//TODO::后面要改成这个接口只关闭一个webview，关闭整个页面要调用destroyContainer
//				Engine.removeWebview(webview);
//				webview.close();
			}

			JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.OK, true);
		} catch (Exception e) {
			JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.ERROR, true);
			e.printStackTrace();
		}
	}
	
	// 关闭Webview窗口
		@JavascriptInterface
		public void closeWebview(final H5PlusWebView pWebview, final String param) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				
				@Override
				public void run() {
					List<H5PlusWebView> webViews=Engine.getWebviewList(pWebview.getContainer());
					String webViewId = param;
					H5PlusWebView webview = Engine.getWebview(pWebview.getContainer(), webViewId);
					if(webview != null){
						H5PlusWebViewContainer act = webview.getContainer();
//					Engine.destroyPager(act);
//					act.destroyContainer();
						Engine.removeWebview(webview);
						webview.close();
						if(webViews.size()==0){
							act.destroyContainer();
							return;
						}
					}
//					webview=webViews.get(webViews.size()-1);
//					webview.getContainer().getContainerRootView().removeAllViews();
//					webview.onRootViewGlobalLayout(webview.getContainer().getContainerRootView(), "", "");
				}
			});
		}

	// 关闭acitivy
	@JavascriptInterface
	public void destroyContainer(H5PlusWebView pWebview, final String param) {
		try {
			H5PlusWebViewContainer act = pWebview.getContainer();
			Engine.destroyPager(act);
			act.destroyContainer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 显示Webview窗口
	@JavascriptInterface
	public void show(H5PlusWebView pWebview, final String param) {
		String callbackId = null;
		try {
			JSONArray array = new JSONArray(param);
			callbackId = array.getString(0);
			String webViewId = array.getString(1);
			H5PlusWebView webview = Engine.getWebview(pWebview.getContainer(), webViewId);
			if (webview != null) {
				webview.show();
			}
			JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.OK, true);
		} catch (Exception e) {
			JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.ERROR, true);
			e.printStackTrace();
		}
	}

	// 隐藏Webview窗口
	@JavascriptInterface
	public void hide(H5PlusWebView pWebview, final String param) {
		String callbackId = null;
		try {
			JSONArray array = new JSONArray(param);
			callbackId = array.getString(0);
			String webViewId = array.getString(1);
			H5PlusWebView webview = Engine.getWebview(pWebview.getContainer(), webViewId);
			if (webview != null) {
				webview.hide();
			}
			JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.OK, true);
		} catch (Exception e) {
			JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.ERROR, true);
			e.printStackTrace();
		}
	}

	// webview 是否可以返回
	@JavascriptInterface
	public void canBack(final H5PlusWebView pWebview, final String param) {
		pWebview.post(new Runnable() {

			@Override
			public void run() {
				if (pWebview.canGoBack()) {
					JsUtil.getInstance().execCallback(pWebview, param, "", JsUtil.OK, true);
					pWebview.goBack();
				} else {
					JsUtil.getInstance().execCallback(pWebview, param, "", JsUtil.ERROR, true);
				}
			}
		});
	}

	// webview 是否可以返回
	@JavascriptInterface
	public void canBackWithID(final H5PlusWebView pWebview, final String param) {
		pWebview.post(new Runnable() {
			@Override
			public void run() {
				JSONObject ret = new JSONObject();
				String callbackId = null;
				boolean canBack = false;
				try {
					JSONArray array = new JSONArray(param);
					callbackId = array.getString(0);
					String id = array.getString(1);
					H5PlusWebView webview = Engine.getWebview(pWebview.getContainer(), id);
					if (webview != null) {
						canBack = webview.canGoBack();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				ret.put("canBack", canBack);
				JsUtil.getInstance().execCallback(pWebview, callbackId, ret.toJSONString(), JsUtil.OK, true);
			}
		});
	}

}
