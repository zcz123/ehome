package cc.wulian.h5plus.common;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import cc.wulian.h5plus.utils.H5PlusConstants;
import cc.wulian.h5plus.view.H5PlusWebView;

public class JsUtil {

	public static String OK = "ok";

	public static String ERROR = "error";

	private static JsUtil instance;

	private Handler handler;

	private Map<String, JsCallBackModel> callBackMap;

	private JsUtil() {
		handler = new Handler(Looper.getMainLooper());
		callBackMap = new HashMap<String, JsCallBackModel>();
	}

	public static JsUtil getInstance() {
		if (instance == null) {
			instance = new JsUtil();
		}
		return instance;
	}

	public void putCallback(String key, H5PlusWebView pWebview) {
		callBackMap.put(key, new JsCallBackModel(key, pWebview));
	}

	public void removeCallback(String key) {
		if (callBackMap.containsKey(key)) {
			callBackMap.remove(key);
		}
	}

	public void clearCallBackMap() {
		callBackMap.clear();
	}

	/**
	 * 执行存数到本地的js回调
	 *
	 * @param key
	 *            回调的id
	 * @param result
	 * @param callBackType
	 * @param remove
	 */
	public void execSavedCallback(String key, String result, String callBackType, boolean remove) {
		if (callBackMap.containsKey(key)) {
			final JsCallBackModel model = callBackMap.get(key);
			execCallback(model.pWebview, key, result, callBackType, remove);
		}
	}

	/**
	 * 直接执行 key对应的 js回调函数
	 *
	 * @param pWebview
	 * @param key
	 * @param result
	 * @param callBackType
	 * @param remove
	 */
	public void execCallback(H5PlusWebView pWebview, String key, String result, String callBackType, boolean remove) {
		final JSONObject object = new JSONObject();
		try {
			object.put("key", key);
			object.put("result", result);
			object.put("type", callBackType);
			object.put("remove", remove);
			execJSFunction(pWebview, "javascript:plus.callbackUtil.exec(" + object.toString() + ")");
			if (remove)
				removeCallback(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execJSFunction(final WebView webView, final String Uri) {
		if(webView!=null&&Uri!=null&&Uri!=""){
			try {
				handler.post(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl(Uri);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public boolean fireJsEventListener(H5PlusWebView iWebView, String eventType, String data) {
		try {
			Object obj = Engine.getFeature("eventListener");
			if (obj != null) {
				JSONObject object = new JSONObject();
				object.put("eventType", eventType);
				object.put("data", data);
				if (iWebView.isEventListened(eventType)) {
					iWebView.loadUrl("javascript:plus.event.fireEventListener(" + object.toString() + ")");
				}
				return true;
			}
		} catch (Exception e) {
			Log.e(H5PlusConstants.TAG, "", e);
		}
		return false;
	}

	public class JsCallBackModel {
		public H5PlusWebView pWebview;
		public String callBackId;

		public JsCallBackModel(String callBackId, H5PlusWebView pWebview) {
			this.callBackId = callBackId;
			this.pWebview = pWebview;
		}
	}
}
