package cc.wulian.smarthomev5.thirdparty.uei_yaokan;

import android.content.Context;
import android.os.Looper;

import cc.wulian.ihome.wan.util.StringUtil;

public class YkanSDKManager {

	private static String TAG = YkanSDKManager.class.getSimpleName();
	
	private Context ctx;
	
//	private String appId = "fc61c67dae2";

	private String appId="14649204849260";
	private String deviceId = "test_device2016";
	
	private boolean initFinished = false;
	
	public static YkanSDKManager yKanSDKManager;
	
	private YkanSDKManager(Context ctx,String appID,String deviceId){
		this.ctx = ctx.getApplicationContext() ;
		if(!StringUtil.isNullOrEmpty(appID)){
			this.appId = appID;
		}
		if(!StringUtil.isNullOrEmpty(deviceId)){
			this.deviceId = deviceId;
		}
	}
	
	public static YkanSDKManager init(Context ctx, String appID,String deviceId) {
		if( yKanSDKManager == null ){
			yKanSDKManager = new YkanSDKManager(ctx, appID,deviceId);
		}
		return yKanSDKManager;
	}
	public static YkanSDKManager getInstance() {
		if( yKanSDKManager != null ){
			return yKanSDKManager;
		}else{
			Looper.prepare();
			Logger.e(TAG,"没有调用  YkanSDKManager.init(Context  ctx,String appID)方法，请先执行");
			return null;
		}
	}

   /**
    *获取上下文
    * @return
    */
	public Context getContext() {
		return ctx;
	}
	/**
	 * 获取AppId
	 * @return
	 */
	public String getAppId() {
		return appId;
	}
	
	/**
	 * 获取设备ID
	 * @return
	 */
	public String getDeviceId() {
		return deviceId;
	}
	
	/**
	 * 设置设备ID
	 * @return
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	

	/**
	 *判断是否已经完成
	 * @return
	 */
	public boolean isInitFinished() {
		return initFinished;
	}

	public void setLogger(boolean b) {
		Logger.mLogGrade = b;
	}

}
