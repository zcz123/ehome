package cc.wulian.h5plus.feature.ajax;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;
import android.webkit.JavascriptInterface;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;

/**
 * Created by Administrator on 2016/2/17 0017.
 */
public class HtmlRequestFeature {

	public static long current_id_suffix = 0;
	public static Map<String, HttpRequestEntity> requestEntityMap = new ConcurrentHashMap<String, HttpRequestEntity>();

	@JavascriptInterface
	public String open(H5PlusWebView pWebview, String param) {
		String id = String.valueOf(current_id_suffix++);
		try {
			JSONArray array = new JSONArray(param);
			String method = array.getString(0);
			String url = array.getString(1);
			int timeout = array.getInt(2);
			HttpRequestEntity requestEntity = new HttpRequestEntity();
			requestEntity.open(pWebview.getContext(), method, url, timeout);
			requestEntityMap.put(id, requestEntity);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@JavascriptInterface
	public void setRequestHeader(H5PlusWebView pWebview, String param) {
		try {
			JSONArray array = new JSONArray(param);
			String id = array.getString(0);
			String header = array.getString(1);
			String type = array.getString(2);
			if (requestEntityMap.containsKey(id)) {
				requestEntityMap.get(id).setRequestHeader(header, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@JavascriptInterface
	public String getResponseHeader(H5PlusWebView pWebview, String param) {
		try {
			JSONArray array = new JSONArray(param);
			String id = array.getString(0);
			String header = array.getString(1);
			if (requestEntityMap.containsKey(id)) {
				return requestEntityMap.get(id).getResponseHeader(header);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@JavascriptInterface
	// String id,String param
	public void send(H5PlusWebView pWebview, String param) {
		String callbackId = null;
		try {
			Log.d("loadData", "执行HtmlRequestFeature.send 1 param="+param);
			JSONArray array = new JSONArray(param);
			callbackId = array.getString(0);
			String id = array.getString(1);
			String sendData = array.getString(2);
			if (requestEntityMap.containsKey(id)) {
				HttpRequestEntity entity = requestEntityMap.get(id);
				int statusCode = entity.send(sendData);
				JSONObject obj = new JSONObject();
				if (statusCode == 200) {
					String result = entity.getResult();
					obj.put("data", result);
				}
				obj.put("status", statusCode);
				Log.d("loadData", "执行HtmlRequestFeature.send 2");
				JsUtil.getInstance().execCallback(pWebview, callbackId, obj.toString(), JsUtil.OK, true);
				Log.d("loadData", "执行HtmlRequestFeature.send 3");
			}
		} catch (IOException e) {
			Log.e("loadData", "执行HtmlRequestFeature.send 4");
			Log.e("ajaxSend", "Call HTTPXMLRequest error, maybe network is not ok.", e);
			JsUtil.getInstance().execCallback(pWebview, callbackId, "Network error", JsUtil.ERROR, true);
		} catch (Exception e) {
			Log.e("loadData", "执行HtmlRequestFeature.send 5");
			Log.e("ajaxSend", "", e);
			JsUtil.getInstance().execCallback(pWebview, callbackId, "Unknown error", JsUtil.ERROR, true);
		}
	}

	@JavascriptInterface
	// String id
	public void close(H5PlusWebView pWebview, String param) {
		try {
			JSONArray array = new JSONArray(param);
			String id = array.getString(0);
			if (requestEntityMap.containsKey(id)) {
				HttpRequestEntity entity = requestEntityMap.get(id);
				entity.disconnect();
				requestEntityMap.remove(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
