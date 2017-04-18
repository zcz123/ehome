package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.Toast;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.RegisterInfo;
import cc.wulian.ihome.wan.sdk.CfgNetSDK;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import com.alibaba.fastjson.JSONArray;
import com.yuantuo.customview.ui.CustomProgressDialog;
import com.yuantuo.customview.ui.CustomToast;
import com.yuantuo.customview.ui.WLToast;

public class SendMessage {
	private static final int DEFAULT_DELAY_TIME = 100;
	public static String customIp = null;
	public static String firstServerIp = ConstUtil.SVR_HOST_ARR[0];
	public static Map<String, String> requestMap = new HashMap<String, String>();

	public static void addRequest(String key) {
		requestMap.put(key, key);
	}

	public static void removeRequest(String key) {
		requestMap.remove(key);
	}

	public static boolean containRequest(String key) {
		return requestMap.containsKey(key);
	}

	static {
		if (!LanguageUtil.isChina()) {
			firstServerIp = ConstUtil.SVR_HOST_ARR[1];
		}
	}

	/**
	 * make a ip array with LAN ip and WAN ip
	 * 
	 * @param
	 * @return
	 */
	public static void connect(String gwID, String gwPassword, RegisterInfo info) {
		List<String> ips = new ArrayList<String>();
		for (String domain : ConstUtil.SVR_HOST_ARR) {
			ips.add(domain);
		}
		if (customIp != null)
			ips.add(0, customIp);
		NetSDK.connectSpecial(gwID, ips, gwPassword, info);
	}

	public static final String ACTION_CONTROL_DEV = "Control Dev: ";

	public static final String ACTION_CONNECT_GW = "Connect Gw: ";

	public static final String ACTION_CHANGE_GW_PWD = "Gw Data: ";

	public static void sendChangeGwPwdMsg(Context context, String gwID,
			String gwPwd, String gwNewPwd) {
		// app.mProgressDialog.showDialog(gwID);
		String key = ACTION_CHANGE_GW_PWD + gwID;
		ProgressDialogManager.getDialogManager().showDialog(key, context, null,
				null);

		NetSDK.sendChangeGwPwdMsg(gwID, gwPwd, gwNewPwd);
	}

	public static final String ACTION_SET_TASK = "Set Task: ";

	public static void sendSetTaskMsg(Context context, String gwID,
			String sceneID, String devID, String type, String ep,
			String epType, JSONArray data) {
		// app.mProgressDialog.showDialog(gwID + sceneID + devID + ep,
		// CustomProgressDialog.DELAYMILLIS_30);
		String key = ACTION_SET_TASK + sceneID + devID + ep;
		ProgressDialogManager.getDialogManager().showDialog(key, context, null,
				null);

		NetSDK.sendSetTaskMsg(gwID, sceneID, devID, type, ep, epType, data);
	}

	public static final String ACTION_GET_TASK = "Get Task: ";

	public static void sendGetTaskMsg(Context context, String gwID,
			String version, String sceneID) {
		ProgressDialogManager.getDialogManager().showDialog(ACTION_GET_TASK,
				context, null, null);
		NetSDK.sendGetTaskMsg(gwID, version, sceneID);
	}

	public static void sendGetRoomMsg(String gwId) {
		NetSDK.sendGetRoomMsg(gwId);
	}

	public static void sendGetSceneMsg(String gwId) {
		NetSDK.sendGetSceneMsg(gwId);
	}

	public static void sendGetProgramTaskMsg(String gwId) {
		NetSDK.sendGetProgramTask(gwId);
	}

	public static final String ACTION_SET_ROOM = "Set Room: ";

	public static void sendSetRoomMsg(Context context, String gwID,
			String mode, String roomID, String name, String icon) {
		// app.mProgressDialog.showDialog(gwID);
		String key = ACTION_SET_ROOM + mode + gwID;
		ProgressDialogManager.getDialogManager().showDialog(key, context, null,
				null);

		NetSDK.sendSetRoomMsg(gwID, mode, roomID, name, icon, null);
	}

	public static final String ACTION_SET_SCENE = "Set Scene: ";

	public static void sendSetSceneMsg(Context context, String gwID,
			String mode, String sceneID, String name, String icon,
			String status, boolean wantDialogShow) {
		if (!UserRightUtil.getInstance().canControlScene(sceneID)) {
			// add_by_yanzy_at_2016-5-13:没有权限
			WLToast.showToast(
					context,
					context.getResources().getString(
							cc.wulian.smarthomev5.R.string.common_no_right),
					Toast.LENGTH_SHORT);
			return;
		}

		if (wantDialogShow) {
			String key = ACTION_SET_SCENE + mode + gwID;
			ProgressDialogManager.getDialogManager()
					.showDialog(key, context, null, null).setCancelable(false);
		}

		NetSDK.sendSetSceneMsg(gwID, mode, sceneID, name, icon, status);
	}

	public static final String ACTION_SET_DEVICE = "Set Dev: ";

	public static void sendSetDevMsg(Context context, String gwID, String mode,
			String devID, String ep, String type, String name, String category,
			String roomID, String epType, String epName, String epStatus,
			boolean wantDialogShow, boolean wantDelay) {
		try {
			if (wantDelay) {
				Thread.sleep(DEFAULT_DELAY_TIME);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		boolean goOn = SendMessage.sendSetDevMsg(gwID, mode, devID, type, name,
				category, roomID, ep, epType, epName, epStatus);
		if (!goOn) {
			return;
		}

		String key = ACTION_SET_DEVICE + gwID + devID;
		if (wantDialogShow) {
			ProgressDialogManager.getDialogManager().showDialog(key, context,
					null, null);
		}
	}

	public static void sendRefreshDevListMsg(String gwID) {
		NetSDK.sendRefreshDevListMsg(gwID, null);
	}

	public static void sendGetDevIRMsg(String gwID, String devID, String ep,
			String mode) {
		NetSDK.sendGetDevIRMsg(gwID, devID, ep, mode);
	}

	public static final String ACTION_SET_DEVICE_IR = "Set Device IR: ";

	public static void sendSetDevIRMsg(Context context, String gwID,
			String mode, String devID, String ep, String irType, JSONArray data) {
		String key = ACTION_SET_DEVICE_IR + gwID + devID;
		ProgressDialogManager.getDialogManager().showDialog(key, context, null,
				null, CustomProgressDialog.DELAYMILLIS_25);
		NetSDK.sendSetDevIRMsg(gwID, mode, devID, ep, irType, data);

	}

	public static void sendGetBindSceneMsg(String gwID, String devID) {
		NetSDK.sendGetBindSceneMsg(gwID, devID);
	}

	public static final String ACTION_SET_BIND_SCENE = "Set Bind Scene: ";

	public static void sendSetBindSceneMsg(Context context, String gwID,
			String mode, String devID, String type, JSONArray data) {
		String key = ACTION_SET_BIND_SCENE + gwID + devID;
		ProgressDialogManager.getDialogManager().showDialog(key, context, null,
				null, CustomProgressDialog.DELAYMILLIS_10);
		NetSDK.sendSetBindSceneMsg(gwID, mode, devID, type, data);
	}

	public static void sendGetBindDeviceMsg(String gwID, String devID) {
		NetSDK.sendGetBindDevMsg(gwID, devID);
	}

	public static final String ACTION_SET_BIND_DEVICE = "Set Bind Device: ";

	public static void sendSetBindDeviceMsg(Context context, String gwID,
			String mode, String devID, String type, JSONArray data) {
		String key = ACTION_SET_BIND_DEVICE + gwID + devID;
		ProgressDialogManager.getDialogManager().showDialog(key, context, null,
				null, CustomProgressDialog.DELAYMILLIS_25);
		NetSDK.sendSetBindDevMsg(gwID, mode, devID, type, data);
	}

	public static void sendGetTimingSceneMsg(String gwID) {
		NetSDK.sendGetTimerSceneMsg(gwID);
	}

	public static void sendGetBindDevMsg(String gwID, String devID) {
		NetSDK.sendGetBindDevMsg(gwID, devID);
	}

	public static void sendSetBindDevMsg(String gwID, String mode,
			String devID, String type, JSONArray data) {
		NetSDK.sendSetBindDevMsg(gwID, mode, devID, type, data);
	}

	public static void sendQueryDevRssiMsg(String gwID, String devID,
			boolean wantDelay) {
		try {
			NetSDK.sendQueryDevRssiMsg(gwID, devID);
			if (wantDelay)
				Thread.sleep(DEFAULT_DELAY_TIME * 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void sendMakeDevBlinkMsg(Context context, WulianDevice device) {
		if (!device.isDeviceOnLine()) {
			CustomToast.showToast(context,
					context.getString(R.string.device_offline),
					Toast.LENGTH_SHORT, false);
			return;
		}
		NetSDK.sendMakeDevBlinkMsg(device.getDeviceGwID(),
				device.getDeviceID(), CmdUtil.SEARCH_DEVICE_DEFAULT_SHOW);
		/*
		 * String msg = context.getString(R.string.hint_search_device_hint,
		 * show);
		 * 
		 * TextView tv = new TextView(context); tv.setText(msg);
		 * tv.setTextSize(17); Toast toast = new Toast(context);
		 * toast.setView(tv); toast.setGravity(Gravity.CENTER, 0, 0);
		 * toast.setDuration(Toast.LENGTH_LONG); toast.show();
		 */
	}

	/*
	 * public static void sendGetDevRecordMsg( Context mContext, String gwID,
	 * String mode, String userID, String devID ) { if
	 * (StaticContent.getInstance().add(gwID + devID)) { long nowTime =
	 * System.currentTimeMillis(); String lastQueryDeviceOfflineDate =
	 * mContext.mPreference
	 * .getString(IPreferenceKey.P_KEY_QUERY_DEVICE_OFFLINE_LAST_DATE + "_" +
	 * gwID + devID, CmdUtil.COMPANY_ZERO); long lastQueryDateLong =
	 * Long.parseLong(lastQueryDeviceOfflineDate); if (Math.abs(nowTime -
	 * lastQueryDateLong) > 2 * 60 * 1000) { NetSDK.sendGetDevRecordMsg(gwID,
	 * mode, userID, devID, lastQueryDeviceOfflineDate,
	 * String.valueOf(nowTime)); } } }
	 */

	public static void sendSocialMsg(String gwID, String userType,
			String userID, String appID, String userName, String time,
			String data) {
		NetSDK.sendPushUserChatAllMsg(gwID, userType, userID, appID, userName,
				time, data);
	}

	// 梦想之花设置
	public static void sendSetFlowerConfigMsg(String gwID, String cmdindex,
			Object data) {
		CfgNetSDK.sendSetDreamFlowerConfigMsg(gwID, cmdindex, data);
	}

	public static void sendGetFlowerConfigMsg(String gwID, String cmdindex) {
		CfgNetSDK.sendGetDreamFlowerConfigMsg(gwID, cmdindex);
	}

	// 时区设置
	public static void sendSetTimeZoneConfigMsg(String gwID, String zoneID,String zoneName,String zone,
			String time) {
		CfgNetSDK.sendSetTimeZoneConfigMsg(gwID, zoneID, zoneName, zone, time);
	}

	public static void sendGetTimeZoneConfigMsg(String gwID) {
		CfgNetSDK.sendGetTimeZoneConfigMsg(gwID);
	}

	private static Context mContext = MainApplication.getApplication()
			.getApplicationContext();

	public static void sendControlDevMsg(String gwID, String devID, String ep,
			String epType, String data) {
		if (UserRightUtil.getInstance().canControlDevice(devID)) {
			NetSDK.sendControlDevMsg(gwID, devID, ep, epType, data);
		} else {
			WLToast.showToast(
					mContext,
					mContext.getResources().getString(
							cc.wulian.smarthomev5.R.string.common_no_right),
					Toast.LENGTH_SHORT);

		}
	}

	/**
	 * 往设备发送消息，会检查是否有权限，通过返回值表示。
	 * 
	 * @param gwID
	 * @param mode
	 * @param devID
	 * @param type
	 * @param name
	 * @param category
	 * @param roomID
	 * @param ep
	 * @param epType
	 * @param epName
	 * @param epStatus
	 * @return 当有控制权限时，返回true；没有控制权限，则返回false
	 */
	public static boolean sendSetDevMsg(String gwID, String mode, String devID,
			String type, String name, String category, String roomID,
			String ep, String epType, String epName, String epStatus) {
		if (UserRightUtil.getInstance().canControlDevice(devID)) {
			NetSDK.sendSetDevMsg(gwID, mode, devID, type, name, category,
					roomID, ep, epType, epName, epStatus, null, null,null);
			return true;
		} else {
			WLToast.showToast(
					mContext,
					mContext.getResources().getString(
							cc.wulian.smarthomev5.R.string.common_no_right),
					Toast.LENGTH_SHORT);
			return false;

		}
	}
}