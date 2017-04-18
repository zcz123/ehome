package cc.wulian.smarthomev5.tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wulian.icam.R;
import com.wulian.iot.HandlerConstant;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.core.http.HttpProvider;
import cc.wulian.ihome.wan.core.http.Result;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.sdk.user.AMSConstants;
import cc.wulian.ihome.wan.sdk.user.entity.AMSDeviceInfo;
import cc.wulian.ihome.wan.sdk.user.entity.UserResultModel;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.account.WLUserManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 *
 * @author mabo 2016/6/22
 *
 */
public class DevicesUserManage {
	private static final String TAG = "DevicesUserManage";
	private static String deviceType = AMSConstants.DEVICE_TYPE_CAMERA;
	private static String deviceModel = "MY001";
	private static TaskExecutor mTaskExecutor  = null;
	private static WLUserManager um  = null;
	private static List<AMSDeviceInfo> amsDeviceInfos = null;//设备信息
	private static List<GatewayInfo> lGatewayInfos  = null;//网关信息
	private  static  Message msg = null;
	private  static boolean isSend = true;
	private static Object outRes = null;
	private static int delRes = -1;
	private static int authStatus = -1;
	static{
		lGatewayInfos = new ArrayList<>();//网关信息
		amsDeviceInfos = new ArrayList<>();//设备信息
		mTaskExecutor = TaskExecutor.getInstance();//工作线程
		authStatus = -1;
		delRes =-1;
		um = WLUserManager.getInstance();
	}
	/**
	 * 猫眼绑定设备
	 * @param deviceId
	 * @param devicePasswd
	 * @param handler
	 */
	public static void bindDevice(final String deviceId,final String devicePasswd, final String deviceType,final String deviceModel,final Handler handler) {
		mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					msg = new Message();
					Result res = um.getStub().bindDevice(deviceId, devicePasswd, deviceType,deviceModel);
					msg.what = res.status;
					msg.arg1 = res.status;
					Log.e(TAG,"bindDevice("+ res.status+")");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
				msg = null;
				outRes = null;
			}
		});
	}
	/**
	 * @author syf
	 */
	public  static void authUser(final String account,final List<String> deviceIds,final boolean auth,final Handler mHandler){
		mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try{
					msg = new Message();
					msg.what = auth?um.getStub().bindUser(account, deviceIds).status:um.getStub().unbindUser(account, deviceIds).status;
				}catch(Exception ex){
					ex.printStackTrace();
					msg.what = HandlerConstant.ERROR;
				}
				if(mHandler!=null){
					mHandler.sendMessage(msg);
				}
				msg = null;
			}
		});
	}
	public static void queryUserByDevice(final String deviceId,final Handler mHandler,final int constant){
		mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try{
					msg = new Message();
					msg.obj = um.getStub().getUserByDevice(deviceId);
					msg.what = constant;
				}catch(Exception ex){
					msg.what = HandlerConstant.ERROR;
				}
				mHandler.sendMessage(msg);
				msg = null;
			}
		});
	}
	/**
	 * 根据网关iD 获取设备详细列表
	 * @author syf
	 */
	public static void getDeviceInfoBywld(final List<GatewayInfo> dInfos,
										  final Handler mHandler, final int what) {
		mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					amsDeviceInfos.clear();
					msg = new Message();
					for (GatewayInfo obj : dInfos) {
						if (obj.getGwID() != null
								&& !obj.getGwID().trim().equals(" ")) {
							amsDeviceInfos.add(um.getStub().getDeviceInfo(
									obj.getGwID()));
						}
					}
					msg.obj = amsDeviceInfos;
					msg.what = what;
				} catch (Exception ex) {
					msg.obj = "gateDeviceInfoByGwId";
					msg.what = HandlerConstant.ERROR;
				}
				mHandler.sendMessage(msg);
				msg = null;
			}
		});
	}
	/**
	 * 获取用户绑定网关列表
	 * @author syf
	 * @param  handler what
	 * @return 网关
	 */
	public static void gatewayInfoOfUser( final Handler handler,final int what,final String type){
		mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try{
					msg = new Message();
					clearlGatewayInfos();
					for (GatewayInfo obj : um.getStub().getSimpleDeviceByUser()) {
						if(obj.getDeviceType().equals(type)){
							lGatewayInfos.add(obj);
						}
					}
					msg.obj = lGatewayInfos;
					msg.what = what;
				}catch(Exception ex){
					ex.printStackTrace();
					msg.arg1 = ex.hashCode();
					msg.obj = "gatewayInfoOfUser";
					msg .what = HandlerConstant.ERROR;
				}
				handler.sendMessage(msg);
				msg = null;
			}
		});
	}
	public static int unBindDevice(final String deviceId){
		mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					delRes = um.getStub().unbindDevice(deviceId).status;
				} catch (Exception e) {
					return;
				}
			}
		});
		return delRes;
	}
	public static int unBindShareEagle(final String deviceId){
		HttpProvider httpProvider=HttpManager.getWulianCloudProvider();
		Result retResult = new Result();
		retResult.status = -1;
		Map e = createRequestHeader("unbindDevice", SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.TOKEN));
		JSONObject obj = new JSONObject();
		obj.put("deviceId", deviceId);
		JSONObject result=httpProvider.post("https://v2.wuliancloud.com:52182/AMS/user/device", e, obj.toJSONString());
		int status = statusFromJsonObject(result);
//		return delRes = um.getStub().unbindDevice(deviceId).status;  //这个是以前的方法 服务器地址不对 是52189的 会报token失效
		return status;
	}
	private static Map<String, String> createRequestHeader(String cmd, String token) {
		HashMap headers = new HashMap();
		if(!StringUtil.isNullOrEmpty(cmd)) {
			headers.put("cmd", cmd);
		}

		if(!StringUtil.isNullOrEmpty(token)) {
			headers.put("token", token);
		}

		return headers;
	}
	private static int statusFromJsonObject(JSONObject result) {
		int status = -1;
		if(result != null) {
			JSONObject head = result.getJSONObject("header");
			if(head != null && head.containsKey("status")) {
				try {
					status = Integer.parseInt(head.getString("status"));
				} catch (Exception var4) {
					Logger.error(var4);
				}
			}
		}

		return status;
	}
	public static void clearAmsDeviceInfo(){
		amsDeviceInfos.clear();
	}
	public static void clearlGatewayInfos(){
		lGatewayInfos.clear();
	}
	public static void bindDevice(final String deviceId,final String tutkData){
		Log.i(TAG, tutkData);
//		mTaskExecutor.execute(new Runnable() {
//			@Override
//			public void run() {
				NetSDK.sendCommonDeviceConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(),deviceId, "1", null, deviceId+"UID",tutkData);
//			}
//		});
	}
}
