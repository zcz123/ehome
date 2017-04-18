package cc.wulian.smarthomev5.service.html5plus.plugins;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLToast;

import org.json.JSONArray;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.AccountManager;

public class CmdControlFeatureImpl {
	private Handler mainHandler = new Handler(Looper.getMainLooper());

	private MainApplication application = MainApplication.getApplication();
	private DeviceCache deviceCache;
	public static String mCallBackId;
	public static H5PlusWebView mWebview;

	// 取消设备定时
	@JavascriptInterface
	public void delDeviceTimed(H5PlusWebView pWebview, String webparam) {
		com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
		String callBackId=null;
		try {
			callBackId= array.getString(0);
			String devID = array.getString(1);
			String ep = array.getString(2);
			if(StringUtil.isNullOrEmpty(ep)){
				ep = "14";
			}
			String gwID = AccountManager.getAccountManger().getmCurrentInfo()
					.getGwID();
			NetSDK.sendSetDevMsg(gwID, "4", devID, null, null, null, null,
					ep, "DD", null, null, null, null,null);
		} catch (Exception e) {
			JsUtil.getInstance().execCallback(pWebview, callBackId, "",
					JsUtil.ERROR, true);
		}
	}

	// 设置设备定时
	@JavascriptInterface
	public void setDeviceTimed(H5PlusWebView pWebview, String webparam) {
		com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
		String callBackId=null;
		try {
			callBackId = array.getString(0);
			String devID = array.getString(1);
			String ep = array.getString(2);
			String epData = array.getString(3);
			String interval = array.getString(4);
			if(StringUtil.isNullOrEmpty(ep)){
				ep = "14";
			}
			String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
			NetSDK.sendSetDevMsg(gwID, "5", devID, null, null, null, null, ep,
					"DD", null, null, epData, interval,null);
		} catch (Exception e) {
			JsUtil.getInstance().execCallback(pWebview, callBackId, "",
					JsUtil.ERROR, true);
		}
	}

	// 查询设备定时
	@JavascriptInterface
	public void queryDeviceTimed(H5PlusWebView pWebview, String webparam) {
		com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
		String callBackId=null;
		try {
			callBackId = array.getString(0);
			String devID = array.getString(1);
			String ep = array.getString(2);
			if(StringUtil.isNullOrEmpty(ep)){
				ep = "14";
			}
			String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
			deviceCache = DeviceCache.getInstance(application);
			WulianDevice wulianDevice = deviceCache.getDeviceByID(application,
					gwID, devID);
			wulianDevice.registerEPDataToHTML(pWebview, callBackId, ConstUtil.CMD_SET_DEV);
			NetSDK.sendSetDevMsg(gwID, "6", devID, null, null, null, null, ep,
					"DD", null, null, null, null,null);
		} catch (Exception e) {
			JsUtil.getInstance().execCallback(pWebview, callBackId, "",
					JsUtil.ERROR, true);
		}
	}

	//  执行普通的控制命令
	@JavascriptInterface
	public void controlDeviceEPData(final H5PlusWebView pWebview, final String webparam) {
		try {
			final JSONArray array = new JSONArray(webparam);
			TaskExecutor.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					String callBackId = array.optString(0);
					try {
						String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
						String devID = array.getString(1);
						String ep=array.getString(2);
						String epdata=array.getString(3);
						String eptype=array.getString(4);
						if(StringUtil.isNullOrEmpty(ep)){
							ep="14";
						}

						//为设备注册H5页面
						AccountManager accountManager = AccountManager.getAccountManger();
//						GatewayInfo gatewayInfo = accountManager.getmCurrentInfo();
						deviceCache = DeviceCache.getInstance(application);
						WulianDevice wulianDevice = deviceCache.getDeviceByID(application, gwID, devID);
						wulianDevice.registerEPDataToHTML(pWebview, callBackId);
						//若没有epdata则不发送相关命令
						if(!StringUtil.isNullOrEmpty(epdata)){
							JSONObject jsonObj = new JSONObject();
							jsonObj.put("cmd","12");
							jsonObj.put("gwID", gwID);
							jsonObj.put("devID", devID);
							jsonObj.put("ep", ep);
							jsonObj.put("epType", eptype);
							jsonObj.put("epData", epdata.toUpperCase());
							String jsonData = jsonObj.toString();
							com.alibaba.fastjson.JSONObject msgBody = com.alibaba.fastjson.JSONObject.parseObject(jsonData);
							if (UserRightUtil.getInstance().canControlDevice(devID)) {
								NetSDK.sendDevMsg(gwID, msgBody);
							} else {
								Handler mainHandler = new Handler(Looper.getMainLooper());
								mainHandler.post(new Runnable() {
									@Override
									public void run() {
										WLToast.showToast(
												pWebview.getContext(),
												pWebview.getContext().getResources().getString(
														cc.wulian.smarthomev5.R.string.common_no_right),
												Toast.LENGTH_SHORT);
									}
								});
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
						String result = MainApplication.getApplication().getString(
								cc.wulian.smarthomev5.R.string.html_user_operation_failed);
						JsUtil.getInstance().execCallback(pWebview, callBackId, result, JsUtil.ERROR, true);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//409命令
	@JavascriptInterface
	public void DeviceJoinGateWay(final H5PlusWebView plusWebView,final String webparam){
		Log.d("MDKT", "deviceJoinGateWay: "+webparam);
		try {
			final JSONArray array = new JSONArray(webparam);
			TaskExecutor.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					String callBackId = array.optString(0);
					try {
						//callbackID, devType, typeID, opt, mode, data
						String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
						String appID=AccountManager.getAccountManger().getmCurrentInfo().getAppID();
						JSONArray paramarr=array.getJSONArray(1);
						String devType = paramarr.getString(0);
//						String devType="OZ";
						String typeID=paramarr.getString(1);
						String opt=paramarr.getString(2);
						String mode=paramarr.getString(3);
						String data=paramarr.getString(4);
						CmdControlFeatureImpl.mWebview=plusWebView;
						CmdControlFeatureImpl.mCallBackId=callBackId;
						NetSDK.sendWifiJionNetwork(gwID,appID,devType,typeID,opt,mode,data);
					} catch (Exception e) {
						e.printStackTrace();
						String result = MainApplication.getApplication().getString(
								cc.wulian.smarthomev5.R.string.html_user_operation_failed);
						JsUtil.getInstance().execCallback(plusWebView, callBackId, result, JsUtil.ERROR, true);
					}
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	//网关角色切换
	@JavascriptInterface
	public void switchGateWayType(final H5PlusWebView pWebview, final String webparam) {
		try {
			com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
			String callBackId = "204-0-"+AccountManager.getAccountManger().getmCurrentInfo().getGwID();
			String gwID = array.getString(0);
			String  switchGWType= array.getString(1);
			this.mCallBackId = callBackId;
			this.mWebview = pWebview;
			NetSDK.sendSetGatewayMasterslaveType(gwID,switchGWType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@JavascriptInterface
	public void resetGateWay(final H5PlusWebView pWebview, final String webparam) {
		try {
			com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
			String gwID = array.getString(0);
			String mode = array.getString(1);
			NetSDK.setGatewayInfo(gwID, mode, null, null, null, null, null, null, null,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
