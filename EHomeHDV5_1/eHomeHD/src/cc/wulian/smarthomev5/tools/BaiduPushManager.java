package cc.wulian.smarthomev5.tools;
import java.net.URI;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import cc.wulian.smarthomev5.utils.PushUtils;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.wulian.iot.HandlerConstant;
/***
 * 百度推送 注册连接
 * @author syf
 */
public class BaiduPushManager{
	private final static String TAG = "BaiduPushManager";
	private static BaiduPushManager instance;
	private static String appid = "cc.wulian.androidpush"; //推送id  cc.wulian.androidpush
	private static String udid = AccountManager.getAccountManger().getRegisterInfo().getDeviceId();//AN50a72b2b8578ca85848250a7  AccountManager.getAccountManger().getRegisterInfo().getDeviceId()
	public  static String reg_client = "reg_client";
	public  static String reg_mapping = "reg_mapping";
	public static String unreg_mapping = "unreg_mapping";
	private static String apiKey = "api_key";
	private BaiduPushManager(){

	}
	public static synchronized BaiduPushManager getInstance() {
		if (instance == null) {
			synchronized (BaiduPushManager.class){
				if(instance == null){
					instance = new BaiduPushManager();
				}
			}
		}
		return instance;
	}
	/**启动服务*/
	public void startWork(Context context){
		PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY,PushUtils.getMetaValue(context, apiKey));
	}
	/**停止服务*/
	public  void stopWork(Context context){
		PushManager.stopWork(context);
	}
	/**判断是可以推送*/
	public  boolean isPush(Context context){
		return PushManager.isPushEnabled(context);
	}
	private String getRegClient(String token,String uid){
		return  "https://push.iotcplatform.com:7380/tpns?cmd=reg_client&token="+token+"&udid="+udid+"&appid="+appid+"&uid="+uid+"&os=baidu"+"&lang=zh"+"&dev=0&interval=0";
	}
	private String getMappingClient(String token,String uid){
		return  "https://push.iotcplatform.com:7380/tpns?cmd=mapping&token="+ token+ "&appid="+ appid+ "&udid="+ udid+ "&uid=" + uid + "&os=baidu" + "&sound=&interval=0";
	}
	private String getMappingRemove(String uid){
		return "https://push.iotcplatform.com:7380/tpns?cmd=unreg_mapping&appid="+ appid+ "&udid="+ udid+ "&uid=" + uid + "&os=baidu" ;
	}
	private HttpParam getHttpURL(String token,String uid,String method){
		switch(method){
			case "reg_client":
				return new HttpParam(HandlerConstant.BAIDU_REGISTER_MAPPING_BY_GET,getRegClient(token,uid));
			case "reg_mapping":
				return new HttpParam(HandlerConstant.SUCCESS,getMappingClient(token,uid));
			case "unreg_mapping":
				return new HttpParam(HandlerConstant.BAIDU_UNRE_MAPPING,getMappingRemove(uid));
		}
		return null;
	}
	/**注册推送*/
	public  void registerTutkServer(String token,String uid,String method,Handler mHandler){
		HttpParam  httpParam = null;
		Message msg = new Message();
		try {
			DefaultHttpClient http = new DefaultHttpClient();
			HttpGet httpMethod = new HttpGet();
			HttpResponse response;
			httpParam = getHttpURL(token,uid,method);
			Log.i(TAG, httpParam.getUrl());
			httpMethod.setURI(new URI(httpParam.getUrl()));
			response = http.execute(httpMethod);
			int responseCode = response.getStatusLine().getStatusCode();
			if(responseCode == 200){
				msg.what = httpParam.getCode();
			} else {
				msg.obj = method;
				msg.what = HandlerConstant.ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.obj = method;
			msg.what = HandlerConstant.ERROR;
		}
		mHandler.sendMessage(msg);
	}
	private class HttpParam{
		public HttpParam(int code,String url){
			this.url = url;
			this.code = code;
		}
		private  int code;
		private String url;
		public int getCode() {
			return code;
		}
		public String getUrl() {
			return url;
		}
	}
}
