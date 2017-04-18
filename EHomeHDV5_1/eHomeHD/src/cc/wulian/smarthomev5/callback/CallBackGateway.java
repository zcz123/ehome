package cc.wulian.smarthomev5.callback;

import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.util.Log;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.callback.router.CmdindexTools;
import cc.wulian.smarthomev5.callback.router.RouterDataCacheManager;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.event.AlarmEvent;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.DialogEvent;
import cc.wulian.smarthomev5.event.GatewaInfoEvent;
import cc.wulian.smarthomev5.event.GatewayCityEvent;
import cc.wulian.smarthomev5.event.GatewayEvent;
import cc.wulian.smarthomev5.event.MigrationTaskEvent;
import cc.wulian.smarthomev5.event.MiniGatewayEvent;
import cc.wulian.smarthomev5.event.NewDoorLockEvent;
import cc.wulian.smarthomev5.event.SigninEvent;
import cc.wulian.smarthomev5.event.SocialEvent;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.home.HomeManager;
import cc.wulian.smarthomev5.fragment.more.wifi.WifiDataManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.CmdControlFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DateUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import de.greenrobot.event.EventBus;

public class CallBackGateway {
	private final Context mContext;
	private final MainApplication mApp = MainApplication.getApplication();
	private final AccountManager mAccountManger = AccountManager
			.getAccountManger();
	private final EventBus mEventBus = EventBus.getDefault();
	public static long HEAT_HEART_TIME = 60000;
	private DeviceCache devcieCache;
	private HomeManager homeManager = HomeManager.getInstance();
	private GatewayInfo info = mAccountManger.getmCurrentInfo();
	boolean isBackround() {
		return mApp.isTaskRunBack;
	}

	public CallBackGateway(Context context, ServiceCallback callback) {
		mContext = context;
		devcieCache = DeviceCache.getInstance(mContext);
	}

	private void whenConnectFailed(Context mContext, int result) {
		mAccountManger.setConnectedGW(false);
		if (mAccountManger.getmCurrentInfo() == null)
			return;
		if (mAccountManger.isConnectedGW())
			return;
		if (result == ResultUtil.RESULT_EXCEPTION) {
			mAccountManger.signinDefaultAccount();
		} else {
			Iterator<WulianDevice> iterator = DeviceCache.getInstance(mContext)
					.getAllDevice().iterator();
			while (iterator.hasNext()) {
				WulianDevice device = iterator.next();
				if (device != null) {
					device.setDeviceOnLineState(false);
				}
			}
			mEventBus.post(new DeviceEvent(DeviceEvent.REFRESH, null, false));
			mEventBus.post(new GatewayEvent(GatewayEvent.ACTION_DISCONNECTED,
					mAccountManger.getmCurrentInfo().getGwID(), result));
			mEventBus.post(new SigninEvent(SigninEvent.ACTION_SIGNIN_RESULT,
					mAccountManger.getmCurrentInfo(), result));
		}
	}

	private String getRightTimezone(String zone) {
		if (zone == null)
			return TimeZone.getDefault().getID();
		String ids[] = TimeZone.getAvailableIDs();
		for (String timeZone : ids) {
			if (timeZone.endsWith(zone))
				return timeZone;
		}
		return zone;
	}

	public void ConnectGateway(int result, final String gwID,
			GatewayInfo gatewayInfo) {
		String actionKey = SendMessage.ACTION_CONNECT_GW + gwID;
		if (ResultUtil.RESULT_SUCCESS == result) {
			mAccountManger.setConnectedGW(true);
			devcieCache.removeAllDevice();
			mAccountManger.removeAllHouseRule();
			gatewayInfo.setZoneID(getRightTimezone(gatewayInfo.getZoneID()));
			mAccountManger.updateCurrentAccount(gatewayInfo);
			mAccountManger.updateAutoLogin(gatewayInfo.getGwID());
			mEventBus.post(new GatewayEvent(GatewayEvent.ACTION_CONNECTED,
					gwID, result));
			mEventBus.post(new SigninEvent(SigninEvent.ACTION_SIGNIN_RESULT,
					gatewayInfo, result));
			DateUtil.setDiffTime(mAccountManger.getmCurrentInfo().getZoneID(),
					Long.parseLong(gatewayInfo.getTime()));
			TaskExecutor.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					try {
						String gatewayVersion = Preference.getPreferences().getGateWayVersion(gwID);
						mAccountManger.getmCurrentInfo().setGwVer(gatewayVersion);
						SendMessage.sendRefreshDevListMsg(gwID);
						Thread.sleep(1000);
						NetSDK.sendGetRouterConfigMsg(mAccountManger.getmCurrentInfo().getGwID(), CmdindexTools.CMDINDEX_4);
						Thread.sleep(1000);
						NetSDK.setGatewayInfo(gwID, CmdUtil.MODE_SWITCH, null,
								null, null, null, null, null,null,null);
						if(mContext.getResources().getBoolean(R.bool.use_house)){
							SendMessage.sendGetProgramTaskMsg(gwID);
						}
						SendMessage.sendGetSceneMsg(gwID);
						Thread.sleep(1000);
						if(mContext.getResources().getBoolean(R.bool.use_house)){
							if(SendMessage.containRequest(REQUEST_KEY_HOUSE_SEND_UPGRADE_GET)){
								SendMessage.removeRequest(REQUEST_KEY_HOUSE_SEND_UPGRADE_GET);
							}
							NetSDK.sendOperateMigrationTaskMsg(gwID,
							CmdUtil.GET_HOUSE_STATUS, null);
							Thread.sleep(1000);
							NetSDK.sendSetRuleTimerSceneMsg(gwID, CmdUtil.GET_HOUSE_STATUS, "1", null, null);
						}
						SendMessage.sendGetTimingSceneMsg(gwID);
						Thread.sleep(1000);
						SendMessage.sendGetRoomMsg(gwID);
						Thread.sleep(1000);
						WifiDataManager.getInstance().excuteWifiScene();
					} catch (Exception e) {

					}
				}
			});
			TaskExecutor.getInstance().executeDelay(new Runnable() {

				@Override
				public void run() {
					try {
						WulianCloudURLManager.getInstance()
								.checkWulianCloudURL();
						homeManager.loadHomeAlarmMessage();
						homeManager.loadHomeSocialMessage();
						EventBus.getDefault().post(
								new AlarmEvent(Messages.TYPE_DEV_ALARM, ""));
						EventBus.getDefault().post(new SocialEvent(""));
					} catch (Exception e) {

					}
				}
			},2000);
		} else if (result == ResultUtil.RESULT_CONNECTING) {
			mEventBus.post(new GatewayEvent(GatewayEvent.ACTION_CONNECTING,
					gwID, result));
		} else if (result == ResultUtil.RESULT_FAILED || result == ResultUtil.RESULT_EXCEPTION) {
			whenConnectFailed(mContext, result);
		} else {
			mAccountManger.setConnectedGW(false);
			if (result == ResultUtil.EXC_GW_PASSWORD_WRONG) {
				Preference.getPreferences().saveAutoLoginChecked(false, gwID);
			}
			mEventBus.post(new GatewayEvent(GatewayEvent.ACTION_DISCONNECTED,
					gwID, result));
			if(gatewayInfo != null) {
				mEventBus.post(new SigninEvent(SigninEvent.ACTION_SIGNIN_RESULT,
						gatewayInfo, result));
			}
		}
		mEventBus.post(new DialogEvent(actionKey, result));
	}

	public void GatewayData(int result, String gwID) {
		String actionKey = SendMessage.ACTION_CHANGE_GW_PWD + gwID;
		mAccountManger.logoutAccount();
		Preference.getPreferences().saveAutoLoginChecked(false, gwID);
		mEventBus.post(new DialogEvent(actionKey, result));
		mEventBus.post(new GatewayEvent(GatewayEvent.ACTION_CHANGE_PWD, gwID,
				result));
	}

	public void GatewayDown(String gwID) {
		whenConnectFailed(mContext, ResultUtil.EXC_GW_OFFLINE);
	}

	/**
	 * 请求与设置网关信息,如网关名、zigbee信道等信息返回
	 * 
	 * @param gwID
	 * @param mode
	 * @param gwVer
	 * @param gwName
	 * @param roomID
	 * @param gwLocation
	 * @param gwPath
	 * @param gwChannel
	 * @param tutkPASSWD
	 * @param tutkUID
	 */
	public void setGatewayInfo(String gwID, String mode, String gwVer,
			String gwName, String roomID, String gwLocation, String gwPath,
			String gwChannel, String tutkUID, String tutkPASSWD, String gwCityID,String bn) {

		if (CmdUtil.MODE_UPD.equals(mode) || CmdUtil.MODE_SWITCH.equals(mode)) {
			mAccountManger.getmCurrentInfo().setGwName(gwName);
			mAccountManger.getmCurrentInfo().setGwVer(gwVer);
			mAccountManger.getmCurrentInfo().setGwPath(gwPath);
			mAccountManger.getmCurrentInfo().setGwRoomID(roomID);
			mAccountManger.getmCurrentInfo().setGwChanel(gwChannel);
			mAccountManger.getmCurrentInfo().setGwCityID(gwCityID);
			mAccountManger.getmCurrentInfo().setBn(bn);
		    // deskcamera add uid and pwd modfi syf
			
			if(tutkUID != null&&tutkPASSWD!=null){
				mAccountManger.getmCurrentInfo().setTutkUID(tutkUID.trim());
				mAccountManger.getmCurrentInfo().setTutkPASSWD(tutkPASSWD.trim());
			}
			if (!StringUtil.isNullOrEmpty(gwName)) {
			}
			Preference.getPreferences().saveGateWayName(gwID, gwName);
			if (!StringUtil.isNullOrEmpty(gwVer)) {
				Preference.getPreferences().saveGateWayVersion(gwID, gwVer);
			}
			mEventBus.post(new GatewaInfoEvent(mode, gwID, gwName,gwChannel));
			if(!StringUtil.isNullOrEmpty(gwCityID)){
				mEventBus.post(new GatewayCityEvent(mode , gwID , gwCityID));
			}
		}
	}

	public void RouterData(String gwID, String cmdIndex, String cmdType,
			JSONObject data) {
		RouterDataCacheManager.getInstance().callBackRouterData(gwID, cmdIndex,
				cmdType, data);

	}

	/**
	 * zigbee信道
	 * 
	 * @param gwID
	 * @param mode
	 * @param gwChannel
	 */
	public void RouterZigbeeChannel(String gwID, String mode, String gwChannel) {
		RouterDataCacheManager.getInstance().callBackZigbeeChannel(gwID, mode,
				gwChannel);
	}

	/**
	 * 管家升级信息
	 * 
	 * @param gwID
	 * @param data
	 * @param cmdtype
	 */

	public static String REQUEST_KEY_HOUSE_SEND_UPGRADE_DO = "REQUEST_KEY_HOUSE_SEND_UPGRADE_DO";
	public static String REQUEST_KEY_HOUSE_SEND_UPGRADE_GET = "REQUEST_KEY_HOUSE_SEND_UPGRADE_GET";
	public static String REQUEST_KEY_HOUSE_SEND_UPGRADE_CLEAR = "REQUEST_KEY_HOUSE_SEND_UPGRADE_CLEAR";

	public void MigrationTaskMsg(String gwID, String data, String cmdtype) {// get操作数据返回
		// get do clear操作数据返回
		if (CmdUtil.GET_HOUSE_STATUS.equals(cmdtype)) {
			if (MigrationTaskEvent.ACTION_CONPLETE_MIGRATION_SUCCESS
					.equals(data)) {
				Preference.getPreferences().putBoolean(
						IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, true);
			} else {
				Preference.getPreferences().putBoolean(
						IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false);
			}
			if (SendMessage.containRequest(REQUEST_KEY_HOUSE_SEND_UPGRADE_GET)) {
				mEventBus.post(new MigrationTaskEvent(cmdtype, data));
				SendMessage.removeRequest(REQUEST_KEY_HOUSE_SEND_UPGRADE_GET);
			}
		} else if (CmdUtil.DO_HOUSE_STATUS.equals(cmdtype)) {
			if (MigrationTaskEvent.ACTION_CONPLETE_MIGRATION_SUCCESS
					.equals(data)) {
				Preference.getPreferences().putBoolean(
						IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, true);
				SendMessage.sendGetProgramTaskMsg(gwID);
			} else {
				Preference.getPreferences().putBoolean(
						IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false);
			}
			if (SendMessage.containRequest(REQUEST_KEY_HOUSE_SEND_UPGRADE_DO)) {
				mEventBus.post(new MigrationTaskEvent(cmdtype, data));
				SendMessage.removeRequest(REQUEST_KEY_HOUSE_SEND_UPGRADE_DO);
			}
		} else if (CmdUtil.CLEAR_HOUSE_STATUS.equals(cmdtype)) {
			if (MigrationTaskEvent.ACTION_CONPLETE_MIGRATION_SUCCESS
					.equals(data)) {
				Preference.getPreferences().putBoolean(
						IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, true);
				SendMessage.sendGetProgramTaskMsg(gwID);
			}
			if (SendMessage
					.containRequest(REQUEST_KEY_HOUSE_SEND_UPGRADE_CLEAR)) {
				mEventBus.post(new MigrationTaskEvent(cmdtype, data));
				SendMessage.removeRequest(REQUEST_KEY_HOUSE_SEND_UPGRADE_CLEAR);
			}
		}
	}

	/**
	 * Mini网关返回区域内网络
	 * 
	 * @param gwID
	 * @param data
	 * @param cmdtype
	 */
	public void MiniGatewaySearchWifiList(String gwID, String data,
			String cmdtype, String cmdindex, String disable) {
		// if(CmdUtil.MINIGATEWAY_SETTING_RELAY_SEARCH.equals(cmdindex)){
		EventBus.getDefault().post(
				new MiniGatewayEvent(gwID, cmdindex, cmdtype, data, disable));
		// }
	}

	/**
	 * 门锁用户管理
	 * 
	 */
	public void NewDoorLockAccountSetting(String gwID, String devID, String operType, JSONObject data) {
		EventBus.getDefault().post(new NewDoorLockEvent(gwID ,  devID , data,operType));
	}

	public void queryChildGatewayList(String gwID,String data) {
		JSONArray jsonArray=JSONArray.parseArray(data);
		JSONArray dataJsonArray=new JSONArray();
		int  arraySize=jsonArray.size();
		for(int i=0;i<arraySize;i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			String subGWName=jsonObject.getString("subGWName");
			String status=jsonObject.getString("status");
			String manageGWID=jsonObject.getString("manageGWID");
			String subGWRoomID=jsonObject.getString("subGWRoomID");
			String subGWID=jsonObject.getString("subGWID");

			String subGWRoomName="";
			List<DeviceAreaEntity> areaEntityList= AreaGroupManager.getInstance().getDeviceAreaEnties();
			int areaEntityListSize=areaEntityList.size();
			for(int j=0;j<areaEntityListSize;j++){
				if(areaEntityList.get(j).getRoomID().equals(subGWRoomID)){
					subGWRoomName=areaEntityList.get(j).getName();
				}
			}
			JSONObject object=new JSONObject();
			object.put("subGWID",subGWID);
			object.put("subGWName",subGWName);
			if(StringUtil.isNullOrEmpty(subGWRoomName)){
				subGWRoomName="["+MainApplication.getApplication().getResources().getString(R.string.device_config_edit_dev_area_type_other_default)+"]";
			}
			object.put("manageGWID",manageGWID);
			object.put("subGWRoomName",subGWRoomName);
			object.put("subGWRoomID",subGWRoomID);
			object.put("status",status);
			dataJsonArray.add(object);
		}
		JSONObject dataJsonObject=new JSONObject();
		dataJsonObject.put("data",dataJsonArray);
		JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId,dataJsonObject.toString(), JsUtil.OK, true);
	}

	public void managerChildGateway(String gwID,String subGwID,String subGWPwd, String type, String result) {
		if(result.equals("1")){
			JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId,"1", JsUtil.OK, true);
		}else if(result.equals("0")){
			JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId,"0", JsUtil.ERROR, true);
		}

	}

	public void setChildGateWayInfo(String gwID, String subGwID, String mode, String gwVer, String gwName, String gwRoomID) {
		if(mode.equals(0+"")){
			//查询子网关
		}else if(mode.equals(2+"")){
			//修改子网关信息
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("gwID",subGwID);
			jsonObject.put("gwName",gwName);
			jsonObject.put("gwRoomID",gwRoomID);
			List<DeviceAreaEntity> areaEntityList= AreaGroupManager.getInstance().getDeviceAreaEnties();
			int areaEntityListSize=areaEntityList.size();
			String gwRoomName="";
			for(int j=0;j<areaEntityListSize;j++){
				if(areaEntityList.get(j).getRoomID().equals(gwRoomID)){
					gwRoomName=areaEntityList.get(j).getName();
				}
			}
			jsonObject.put("gwRoomName",gwRoomName);
			JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId,jsonObject.toString(), JsUtil.OK, true);
		}
	}

	public void gatewayCloneAndBackup(String gwID, String appID, String operType, String oldGWID, String result, String step) {
		if(result.equals("0")){
			JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId,step, JsUtil.OK, false);
		}else{
			JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId,"3", JsUtil.ERROR, true);
		}
	}

	public void setGatewayMasterslaveType(String gwID, String gwType, String result){
		if(!StringUtil.isNullOrEmpty(result)){
			JsUtil.getInstance().execCallback(CmdControlFeatureImpl.mWebview, CmdControlFeatureImpl.mCallBackId,result, JsUtil.OK, true);
		}
	}


}
