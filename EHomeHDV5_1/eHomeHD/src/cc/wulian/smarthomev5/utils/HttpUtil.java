package cc.wulian.smarthomev5.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.util.DesUtil;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.AccountManager;


public class HttpUtil
{

	public static final String TAG = "HttpUtil";
	private static DesUtil desUtil;
    public static byte[] getPicture(String url) {
    	return HttpManager.getDefaultProvider().getPicture(url);
    }
    public static String post(String url){
    	JSONObject result = HttpManager.getDefaultProvider().post(url);
    	if(result != null)
    		return result.getString("body");
    	return null;
    	
    }
    public static boolean verificationResponse(String url){
    	return HttpManager.getDefaultProvider().verificationResponse(url);
    }
    public static String postWulianCloud(String url,  JSONObject jsonObject){
    	String json = null;
    	try{
    		if(StringUtil.isNullOrEmpty(url))
    			return "";
    		if(desUtil == null){
    			desUtil = createWulianDesKey(AccountManager.getAccountManger().getRegisterInfo().getDeviceId());
    		}
	    	Map<String,String> headerMap = new HashMap<String,String>();
	    	headerMap.put("OAUTH_APP_KEY", desUtil.getKey());
	    	byte[] body = jsonObject == null ? null :desUtil.Encode(jsonObject.toJSONString()).getBytes();
	    	JSONObject result = HttpManager.getDefaultProvider().post(url, headerMap, body);
			if(result != null){
				json = result.getString("body");
			}
	    	if(!StringUtil.isNullOrEmpty(json) ){
				json = desUtil.Decode(json);
			}
    	}catch(Throwable e){
			if(TargetConfigure.LOG_LEVEL <= Log.ERROR) {
				Log.e(TAG, "", e);
			}
    		json = null;
    	}
		return json;
    }
    
    public static JSONObject postWulianCloudOrigin(String url,  JSONObject jsonObject){
    	JSONObject json = new JSONObject();
    	try{
    		if(StringUtil.isNullOrEmpty(url))
    			return json;
    		if(desUtil == null){
    			desUtil = createWulianDesKey(AccountManager.getAccountManger().getRegisterInfo().getDeviceId());
    		}
	    	Map<String,String> headerMap = new HashMap<String,String>();
	    	headerMap.put("OAUTH_APP_KEY", desUtil.getKey());
	    	byte[] body = jsonObject == null ? null :desUtil.Encode(jsonObject.toJSONString()).getBytes();
	    	JSONObject result = HttpManager.getDefaultProvider().post(url, headerMap, body);
			if(result != null){
				String bodyStr = result.getString("body");
				if(!StringUtil.isNullOrEmpty(bodyStr) ){
					bodyStr = desUtil.Decode(bodyStr);
					if(!StringUtil.isNullOrEmpty(bodyStr)){
						json.put("body",JSON.parseObject(bodyStr));
					}
				}
				if(result.containsKey("header")){
					json.put("header", result.getJSONObject("header"));
				}
			}
	    	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return json;
    }
    private static DesUtil createWulianDesKey(String imei) {
		String md5Key = MD5Util.encrypt(imei);
		String desKey = md5Key.substring(0, 8);
		DesUtil  desUtil = new DesUtil();
		desUtil.setKey(desKey);
		return desUtil;
	}


	public static String postWulianCloudLocation(String url, String key, JSONObject jsonObject) {
		String json = null;
		try {
			if (StringUtil.isNullOrEmpty(url))
				return "";
			Map<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("cmd", key);
			byte[] body = jsonObject == null ? null : jsonObject.toJSONString().getBytes();
			JSONObject result = HttpManager.getWulianCloudProvider().post(url, headerMap, body);
			if (result != null) {
				json = result.getString("body");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}


}

