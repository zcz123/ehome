package cc.wulian.smarthomev5.callback;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.wulian.icam.model.Scene;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.entity.RulesGroupInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.dao.DeviceDao;
import cc.wulian.smarthomev5.dao.MessageDao;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.databases.entitys.AutoTask;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.entity.CombindDeviceEntity;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.entity.SocialEntity;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.entity.TimingSceneGroupEntity;
import cc.wulian.smarthomev5.event.CommondDeviceConfigurationEvent;
import cc.wulian.smarthomev5.event.DeviceBindSceneEvent;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.DeviceUeiItemEvent;
import cc.wulian.smarthomev5.event.DialogEvent;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.event.MessageEvent;
import cc.wulian.smarthomev5.event.RoomEvent;
import cc.wulian.smarthomev5.event.SceneEvent;
import cc.wulian.smarthomev5.event.SocialEvent;
import cc.wulian.smarthomev5.event.TaskEvent;
import cc.wulian.smarthomev5.event.TimingSceneEvent;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.device.CombindDeviceManager;
import cc.wulian.smarthomev5.fragment.home.HomeManager;
import cc.wulian.smarthomev5.fragment.house.AutoProgramTaskManager;
import cc.wulian.smarthomev5.fragment.house.AutoTaskEvent;
import cc.wulian.smarthomev5.fragment.scene.SceneTaskManager;
import cc.wulian.smarthomev5.fragment.scene.TimingSceneManager;
import cc.wulian.smarthomev5.fragment.setting.flower.FlowerManager;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.FlowerVoiceControlEntity;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.WLCameraOperationManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.LogUtil;
import cc.wulian.smarthomev5.utils.TargetConfigure;
import de.greenrobot.event.EventBus;

public class CallBackModul {
	public static final String TAG = "wlsdk";
	private final MainApplication mApp = MainApplication.getApplication();
	private final Context mContext;
	private final AreaGroupManager areaManager = AreaGroupManager.getInstance();
	private final TreeMap<String, SceneInfo> mSceneInfoMap = mApp.sceneInfoMap;
	private SceneDao sceneDao = SceneDao.getInstance();
	private MessageDao messageDao = MessageDao.getInstance();
	private final AccountManager mAccountManger = AccountManager
			.getAccountManger();
	private DeviceDao deviceDao = DeviceDao.getInstance();
	private DeviceCache deviceCache;
	private Preference preference = Preference.getPreferences();
	private HomeManager homeManager = HomeManager.getInstance();
	private FlowerManager flowerManager = FlowerManager.getInstance();
	public CallBackModul(Context context, ServiceCallback callback) {
		mContext = context;
		deviceCache = DeviceCache.getInstance(context);
	}

	public void SetRoomInfo(String mode, RoomInfo roomInfo, boolean needNotify) {
		String gwID = roomInfo.getGwID();
		String roomID = roomInfo.getRoomID();
		String actionKey = SendMessage.ACTION_SET_ROOM + mode + gwID;

		String action = null;
		boolean operateSuccess = false;
		if (TextUtils.equals(CmdUtil.MODE_DEL, mode)) {
			action = mode;

			// update device room info to default with id
			operateSuccess |= mApp.mDataBaseHelper.updateDeviceRoomInfo(gwID,
					Area.AREA_DEFAULT, roomID);
			// clear soft cache with id
			areaManager.remove(gwID, roomID);
			// clear room info in db with id
			operateSuccess |= mApp.mDataBaseHelper.deleteFromRoomInfo(gwID,
					roomID);
		} else if (TextUtils.equals(CmdUtil.MODE_ADD, mode)) {
			action = mode;
			areaManager.addDeviceAreaEntity(new DeviceAreaEntity(roomInfo));
			operateSuccess |= mApp.mDataBaseHelper.insertOrUpdateRoomInfo(
					roomInfo, true);
		} else if (TextUtils.equals(CmdUtil.MODE_UPD, mode)) {
			action = mode;
			areaManager.update(new DeviceAreaEntity(roomInfo));
			operateSuccess |= mApp.mDataBaseHelper.insertOrUpdateRoomInfo(
					roomInfo, true);
		}
		if (needNotify && operateSuccess && action != null) {
			EventBus.getDefault().post(new RoomEvent(action, false, roomID));
			EventBus.getDefault().post(
					new DialogEvent(actionKey, ResultUtil.RESULT_SUCCESS));
		}
		if(SmarthomeFeatureImpl.isAddAreaHere){
			JSONArray jsonArray = new JSONArray();
			List<DeviceAreaEntity> areaEntityList = areaManager.getDeviceAreaEnties();
			int areaEntityListSize = areaEntityList.size();
			for (int j = 0; j < areaEntityListSize; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("areaName", areaEntityList.get(j).getName());
				jsonObject.put("areaID", areaEntityList.get(j).getRoomID());
				jsonArray.add(jsonObject);
			}
			JSONObject dataJsonObject = new JSONObject();
			dataJsonObject.put("area", jsonArray);
			JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId, dataJsonObject.toString(), JsUtil.OK, true);
			SmarthomeFeatureImpl.isAddAreaHere=false;
		}
	}

	public void GetRoomInfo(String gwID, Set<RoomInfo> roomInfos) {
		// clear all db cache
		mApp.mDataBaseHelper.deleteFromRoomInfo(gwID, null);
		// clear soft cache
		areaManager.clear();

		// when set is empty, ui did not refresh it's state itself
		if (roomInfos.isEmpty()) {
			EventBus.getDefault().post(
					new RoomEvent(CmdUtil.MODE_DEL, false, null));
			return;
		}

		SQLiteDatabase db = mApp.mDataBaseHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (RoomInfo roomInfo : roomInfos) {
				// needNotify = false means when in batch operation not notify
				// ui
				SetRoomInfo(CmdUtil.MODE_ADD, roomInfo, false);
			}
			// we notify ui when batch end
			EventBus.getDefault().post(
					new RoomEvent(CmdUtil.MODE_ADD, false, null));
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void SetSceneInfo(String mode, SceneInfo sceneInfo,
			boolean needNotify) {
		String gwID = sceneInfo.getGwID();
		String sceneID = sceneInfo.getSceneID();

		String mapKey = gwID + sceneID;

		String actionKey = SendMessage.ACTION_SET_SCENE + mode + gwID;
		String action = null;
		boolean operateSuccess = false;

		SceneInfo toastEventSceneInfo = null;

		if (TextUtils.equals(CmdUtil.MODE_DEL, mode)) {
			action = mode;
			toastEventSceneInfo = sceneInfo;

			// clear timer & auto task cache in db with id
			operateSuccess |= mApp.mDataBaseHelper.deleteFromTaskTimerAndAuto(
					gwID, sceneID);
			// clear soft cache with id
			operateSuccess |= mSceneInfoMap.remove(mapKey) != null;
			// clear scene info in db with id
			SceneInfo info = new SceneInfo();
			info.setGwID(gwID);
			info.setSceneID(sceneID);
			sceneDao.delete(info);
			// clear task version cache with id
			operateSuccess |= preference.clearCustomKeyData(sceneID);
			// clear favority set with id

			onSceneDelete(gwID, sceneID, false);
		}
		// required id state
		else if (TextUtils.equals(CmdUtil.MODE_SWITCH, mode)) {
			action = mode;

			String sceneName = null;
			Iterator<SceneInfo> iterator = mSceneInfoMap.values().iterator();
			while (iterator.hasNext()) {
				SceneInfo info = iterator.next();
				// make all default unuse state
				info.setStatus(CmdUtil.SCENE_UNUSE);

				// make the current scene info in using state
				if (TextUtils.equals(sceneID, info.getSceneID())) {
					info.setStatus(sceneInfo.getStatus());
					sceneName = info.getName();
					toastEventSceneInfo = info;
				}
			}

			// scene already been deleted
			if (sceneName == null)
				return;

			// update db info to default state
			SceneInfo defaultInfo = new SceneInfo();
			defaultInfo.setGwID(gwID);
			defaultInfo.setStatus(CmdUtil.SCENE_UNUSE);
			operateSuccess |= mApp.mDataBaseHelper.insertOrUpdateSceneInfo(
					defaultInfo, false);
			operateSuccess |= mApp.mDataBaseHelper.insertOrUpdateSceneInfo(
					sceneInfo, false);

		} else if (TextUtils.equals(CmdUtil.MODE_ADD, mode)
				|| TextUtils.equals(CmdUtil.MODE_UPD, mode)) {
			action = mode;

			String name = sceneInfo.getName();
			String icon = sceneInfo.getIcon();
			String status = sceneInfo.getStatus();

			SceneInfo newSceneInfo = mSceneInfoMap.get(mapKey);
			if (newSceneInfo == null) {
				newSceneInfo = sceneInfo;
				mSceneInfoMap.put(mapKey, newSceneInfo);
			} else {
				if (!TextUtils.isEmpty(name))
					newSceneInfo.setName(name);
				if (!TextUtils.isEmpty(icon))
					newSceneInfo.setIcon(icon);
				if (!TextUtils.isEmpty(status))
					newSceneInfo.setStatus(status);
			}

			// for event judge or set some ui show data
			toastEventSceneInfo = newSceneInfo;

			operateSuccess |= mApp.mDataBaseHelper.insertOrUpdateSceneInfo(
					newSceneInfo, true);
		}
		if (needNotify && operateSuccess && action != null) {
			EventBus.getDefault().post(
					new SceneEvent(action, false, toastEventSceneInfo));
			EventBus.getDefault().post(
					new DialogEvent(actionKey, ResultUtil.RESULT_SUCCESS));
			Intent it=new Intent();
			it.setAction("playDeskScene");  
            mContext.sendBroadcast(it);
		}
		 Scene scene = Scene.getInstance();//查看是否处于摄像机播放界面
		 if(scene.isInPlayVideoUI()){
			 WLCameraOperationManager mWLCameraOperationManager = WLCameraOperationManager.getInstance();
			 mWLCameraOperationManager.setChangedSceneToWulianCamera();
			 scene.setResult(true);
		 }
	}

	private void onSceneDelete(String gwID, String sceneID, boolean isFromMe) {
		Map<String, Map<String, SceneInfo>> bindSceneInfoMap = mApp.bindSceneInfoMap;
		Iterator<Entry<String, Map<String, SceneInfo>>> devIterator = bindSceneInfoMap
				.entrySet().iterator();
		while (devIterator.hasNext()) {
			Entry<String, Map<String, SceneInfo>> entry = devIterator.next();
			Map<String, SceneInfo> epBindSceneMap = entry.getValue();

			boolean needSyncBindMap = false;
			Iterator<SceneInfo> epIterator = epBindSceneMap.values().iterator();
			while (epIterator.hasNext()) {
				SceneInfo epSceneInfo = epIterator.next();
				if (epSceneInfo != null
						&& StringUtil.equals(sceneID, epSceneInfo.getSceneID())) {
					epIterator.remove();
					needSyncBindMap = true;
				}
			}

		}
	}

	public void GetSceneInfo(String gwID, Set<SceneInfo> sceneInfos) {

		// clear all db cache
		SceneInfo info = new SceneInfo();
		info.setGwID(gwID);
		sceneDao.delete(info);
		// clear soft cache
		mSceneInfoMap.clear();

		// when set is empty, ui did not refresh it's state itself
		if (sceneInfos.isEmpty()) {
			EventBus.getDefault().post(
					new SceneEvent(CmdUtil.MODE_DEL, false, null));
			return;
		}

		SQLiteDatabase db = mApp.mDataBaseHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (SceneInfo sceneInfo : sceneInfos) {
				// needNotify = false means when in batch operation not notify
				// ui
				if(!UserRightUtil.getInstance().canSeeScene(sceneInfo.getSceneID())) {
					//没有权限
					continue;
				}
				SetSceneInfo(CmdUtil.MODE_ADD, sceneInfo, false);
			}
			// we notify ui when batch end
			EventBus.getDefault().post(
					new SceneEvent(CmdUtil.MODE_ADD, false, null));
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void SetTaskInfo(String gwID, String version, String sceneID,
			String devID, String type, String ep, String epType,
			Set<TaskInfo> taskInfoSet) {
		SceneTaskManager manager = SceneTaskManager.getInstance();
		String actionKey = SendMessage.ACTION_SET_TASK + sceneID + devID + ep;
		TaskEntity entity = manager.getTaskEntity(gwID, sceneID);
		if(entity == null) {
			if(TargetConfigure.LOG_LEVEL <= Log.WARN) {
				Log.w(TAG, "Can't get TaskEntity of scene, sceneID is "+sceneID);
			}
			return;
		}
		if (taskInfoSet.isEmpty()) {
			mApp.mDataBaseHelper.deleteFromTaskAuto(gwID, sceneID, devID, ep,
					null);
			entity.getNormalGroup().removeTask(devID, ep);
		} else {
			for (TaskInfo taskInfo : taskInfoSet) {
				entity.updateTaskInfo(taskInfo);
			}
		}
		preference.putString(sceneID, version);
		EventBus.getDefault().post(
				new DialogEvent(actionKey, ResultUtil.RESULT_SUCCESS));
		TaskEvent taskEvent = new TaskEvent(gwID, CmdUtil.MODE_UPD, false,
				sceneID, devID, ep);
		EventBus.getDefault().post(taskEvent);
	}

	public void GetTaskInfo(String gwID, String version, String sceneID,
			Set<TaskInfo> taskInfos) {
		String savedTaskVersion = preference.getString(sceneID, "0");
		int sceneIdInt = StringUtil.toInteger(sceneID);

		// when task version changed, clear local database's cache
		if (!savedTaskVersion.equals(version) && sceneIdInt != -1) {
			preference.putString(sceneID, version);
			LogUtil.logWarn("clear local database's taskInfo cache successful");
			mApp.mDataBaseHelper.deleteFromTaskTimerAndAuto(gwID, sceneID);
		}
		/**
		 * 加入缓存
		 */
		SceneTaskManager manager = SceneTaskManager.getInstance();
		TaskEntity entity = manager.getTaskEntity(gwID, sceneID);
		if (entity == null)
			return;
		TaskEvent taskEvent = new TaskEvent(gwID, CmdUtil.MODE_UPD, false,
				sceneID, null, null);
		if (!taskInfos.isEmpty()) {
			SQLiteDatabase db = mApp.mDataBaseHelper.getWritableDatabase();
			db.beginTransaction();
			try {
				for (TaskInfo taskInfo : taskInfos) {
					if (TaskEntity.VALUE_AVAILABL_NO.equals(taskInfo
							.getAvailable()))
						continue;
					saveTaskInfo(gwID, sceneID, taskInfo);
					if (TaskEntity.VALUE_TASK_MODE_REALTIME.equals(taskInfo
							.getTaskMode())) {
						if (!StringUtil.isNullOrEmpty(taskInfo.getSensorID())
								&& !TaskEntity.VALUE_SENSOR_ID_NORMAL
										.equals(taskInfo.getSensorID())) {
							entity.getLinkGroup().addSensorTask(taskInfo);
							entity.getLinkGroup().addTask(taskInfo.clone());
						} else {
							entity.getNormalGroup().addTask(taskInfo);
						}
					}
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			LogUtil.logWarn("get taskInfo saved to database successful");
		}
		EventBus.getDefault().post(taskEvent);
		EventBus.getDefault().post(new DialogEvent(SendMessage.ACTION_GET_TASK, 0));

	}

	// save task//
	private void saveTaskInfo(String gwID, String sceneID, TaskInfo taskInfo) {
		String taskMode = taskInfo.getTaskMode();

		if (TaskEntity.VALUE_TASK_MODE_TIMING.equals(taskMode)) {
			try {
				String time = taskInfo.getTime();
				String weekday = taskInfo.getWeekday();
				TimeZone timeZone = TimeZone
						.getTimeZone(mAccountManger.getmCurrentInfo().getZoneID());
				String localTime = DateUtil.convert2LocalTime(time, timeZone);
				String localWeekDay = DateUtil.convert2LocalWeekday(weekday,time);
				taskInfo.setTime(localTime);
				taskInfo.setWeekday(localWeekDay);
				mApp.mDataBaseHelper.insertOrUpdateTaskTimer(gwID, sceneID,
						taskInfo);

			} catch (Exception e) {
				LogUtil.logException("update Timer TaskInfo Failed ", e);
			}
		} else if (TaskEntity.VALUE_TASK_MODE_REALTIME.equals(taskMode)) {
			try {
				mApp.mDataBaseHelper.insertOrUpdateTaskAuto(gwID, sceneID,
						taskInfo);
			} catch (Exception e) {
			}
		}

	}

	public void SetCombindDevInfo(String gwID, String mode, String bindID,
			String name, String roomID, String devIDLeft, String devIDRight) {
		CombindDeviceManager manager = CombindDeviceManager.getInstance();
		if (CmdUtil.MODE_DEL.equals(mode)) {
			manager.removeCombindDevice(bindID);
		} else {
			CombindDeviceEntity entity = new CombindDeviceEntity();
			entity.setBindID(bindID);
			entity.setGwID(gwID);
			entity.setLeftDevID(devIDLeft);
			entity.setName(name);
			entity.setRightDevID(devIDRight);
			entity.setRoomID(roomID);
			manager.putCombindDevice(entity);
		}
		EventBus.getDefault().post(
				new DeviceEvent(DeviceEvent.REFRESH, null, false));
	}

	public void GetCombindDevInfo(String gwID, JSONArray data) {
		CombindDeviceManager manager = CombindDeviceManager.getInstance();
		if (data == null)
			return;
		for (int i = 0; i < data.size(); i++) {
			try {
				JSONObject jsonObj = data.getJSONObject(i);
				String bindID = jsonObj
						.getString(ConstUtil.KEY_COMBIND_BIND_ID);
				String name = jsonObj.getString(ConstUtil.KEY_NAME);
				String roomID = jsonObj.getString(ConstUtil.KEY_ROOM_ID);
				String devIDLeft = jsonObj
						.getString(ConstUtil.KEY_COMBIND_DEV_LEFT);
				String devIDRight = jsonObj
						.getString(ConstUtil.KEY_COMBIND_DEV_RIGHT);
				CombindDeviceEntity entity = new CombindDeviceEntity();
				entity.setBindID(bindID);
				entity.setGwID(gwID);
				entity.setLeftDevID(devIDLeft);
				entity.setName(name);
				entity.setRightDevID(devIDRight);
				entity.setRoomID(roomID);
				manager.putCombindDevice(entity);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		EventBus.getDefault().post(
				new DeviceEvent(DeviceEvent.REFRESH, null, false));
	}

	public void SetBindSceneInfo(String gwID, String mode, String devID,
			JSONArray data) {
		saveBindSceneInfo(gwID, mode, devID, data);
	}

	public void GetBindSceneInfo(String gwID, String devID, JSONArray data) {
		saveBindSceneInfo(gwID, null, devID, data);
	}

	private void saveBindSceneInfo(String gwID, String mode, String devID,
			JSONArray data) {
		String actionKey = SendMessage.ACTION_SET_BIND_SCENE + gwID + devID;
		// 初始化
		final Map<String, SceneInfo> sceneMap = mSceneInfoMap;
		final Map<String, Map<String, SceneInfo>> bindSceneInfoMap = mApp.bindSceneInfoMap;
		final Map<String, Map<String, DeviceInfo>> bindDeviceInfoMap = mApp.bindDeviceInfoMap;
		Map<String, SceneInfo> bindSceInfoMap = bindSceneInfoMap.get(gwID
				+ devID);
		if (bindSceInfoMap == null) {
			bindSceInfoMap = new TreeMap<String, SceneInfo>();
			bindSceneInfoMap.put(gwID + devID, bindSceInfoMap);
		} else {
			if (!bindSceInfoMap.isEmpty())
				bindSceInfoMap.clear();
		}
		Map<String, DeviceInfo> bindDevInfoMap = bindDeviceInfoMap.get(gwID
				+ devID);
		if (bindDevInfoMap == null) {
			bindDevInfoMap = new TreeMap<String, DeviceInfo>();
			bindDeviceInfoMap.put(gwID + devID, bindDevInfoMap);
		} else {
			bindDevInfoMap.clear();
		}

		if (CmdUtil.MODE_ADD.equals(mode) || StringUtil.isNullOrEmpty(mode)) {
			final JSONArray jsonArray = data;
			int len = jsonArray.size();
			for (int i = 0; i < len; i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				String ep = object.getString(ConstUtil.KEY_EP);
				String sceneID = object.getString(ConstUtil.KEY_SCENE_ID);
				String bindDeviceID = object
						.getString(ConstUtil.KEY_BIND_DEV_ID);
				if (StringUtil.isNullOrEmpty(ep)) {
					continue;
				}
				if (!StringUtil.equals(sceneID, "-1")
						&& !StringUtil.isNullOrEmpty(sceneID)) {
					SceneInfo existSceneInfo = sceneMap.get(gwID + sceneID);
					bindSceInfoMap.put(ep, existSceneInfo);
				} else if (!StringUtil.equals(bindDeviceID, "-1")
						&& !StringUtil.isNullOrEmpty(bindDeviceID)) {
					String bindDevID = object
							.getString(ConstUtil.KEY_BIND_DEV_ID);
					String bindData = object.getString(ConstUtil.KEY_BIND_DATA);
					DeviceInfo deviceInfo = new DeviceInfo();
					deviceInfo.setDevID(bindDevID);
					deviceInfo.setGwID(gwID);
					DeviceEPInfo deviceEPInfo = new DeviceEPInfo();
					deviceEPInfo.setEp(ep);
					deviceEPInfo.setEpData(bindData);
					deviceInfo.setDevEPInfo(deviceEPInfo);
					WulianDevice device = deviceCache.getDeviceByIDEp(mContext,
							gwID, bindDevID, ep);
					if (device != null)
						deviceInfo.setName(device.getDeviceName());
					bindDevInfoMap.put(ep, deviceInfo);
				}
			}
			mApp.bindSceneInfoMap.put(gwID + devID, bindSceInfoMap);
			mApp.bindDeviceInfoMap.put(gwID + devID, bindDevInfoMap);
		} else if (CmdUtil.MODE_DEL.equals(mode)) {
			bindSceInfoMap.clear();
		}
		// EventBus.getDefault().post(new DeviceBindSceneEvent(gwID, devID,
		// isFromMe));
		EventBus.getDefault().post(
				new DialogEvent(actionKey, ResultUtil.RESULT_SUCCESS));
		EventBus.getDefault().post(new DeviceBindSceneEvent(data));
	}

	public void SetTimerSceneInfo(String gwID, String mode, String groupID,
			String groupName, String status, JSONArray data) {
		TimingSceneGroupEntity timingGroup = TimingSceneManager.getInstance()
				.getDefaultGroup();
		String action = null;
		if (CmdUtil.MODE_DEL.equals(mode)) {
			action = mode;
			// if(timingGroup.getGroupID().equals(groupID)){
			timingGroup.clear();
			EventBus.getDefault().post(new TimingSceneEvent(action));
			// }
		} else if (CmdUtil.MODE_SWITCH.equals(mode)) {
			action = mode;
			timingGroup.setGroupID(groupID);
			timingGroup.setGroupName(groupName);
			timingGroup.setGroupStatus(status);
		} else if (CmdUtil.MODE_ADD.equals(mode)) {
			action = mode;
			saveTimingScene(data);
			EventBus.getDefault().post(new TimingSceneEvent(action));
		}

	}

	public void GetTimerSceneInfo(String gwID, JSONArray data) {
		if (data == null)
			return;
		saveTimingScene(data);
		EventBus.getDefault().post(new TimingSceneEvent());
	}

	private void saveTimingScene(JSONArray data) {
		TimingSceneGroupEntity timingGroup = TimingSceneManager.getInstance()
				.getDefaultGroup();
		timingGroup.clear();
		final AccountManager mAccountManger = AccountManager.getAccountManger();
		TimeZone timeZone = TimeZone.getTimeZone(mAccountManger.getmCurrentInfo()
				.getZoneID());
		for (int i = 0; i < data.size(); i++) {
			JSONObject jsonObject = data.getJSONObject(i);
			String groupID = jsonObject.getString(ConstUtil.KEY_GROUP_ID);
			String groupName = jsonObject.getString(ConstUtil.KEY_GROUP_NAME);
			String groupStatus = jsonObject.getString(ConstUtil.KEY_STUS);
			String sceneID = jsonObject.getString(ConstUtil.KEY_SCENE_ID);
			if (!mApp.sceneInfoMap.containsKey(mAccountManger.getmCurrentInfo()
					.getGwID() + sceneID))
				continue;
			String time = jsonObject.getString(ConstUtil.KEY_TIME);
			String localTime = DateUtil.convert2LocalTime(time, timeZone);
			Logger.debug("server time:" + time + ";local time" + localTime);
			String weekday = jsonObject.getString(ConstUtil.KEY_WEEKDAY);
			String localWeekDay = DateUtil.convert2LocalWeekday(weekday,time);
			TimingSceneEntity entity = new TimingSceneEntity();
			if (!StringUtil.isNullOrEmpty(groupName)) {
				entity.setGroupName(groupName);
				timingGroup.setGroupName(groupName);
			}
			if (!StringUtil.isNullOrEmpty(groupStatus)) {
				entity.setGroupStatus(groupStatus);
				timingGroup.setGroupStatus(groupStatus);
			}
			entity.setGroupID(groupID);
			entity.setSceneID(sceneID);
			entity.setTime(localTime);
			entity.setWeekDay(localWeekDay);
			timingGroup.addTimingSceneEntity(entity);
		}
	}

	public void ReportTimerSceneInfo(String gwID, JSONArray data) {
		final Context context = mApp;
		JSONObject object = data.getJSONObject(0);
		String sceneID = object.getString(ConstUtil.KEY_SCENE_ID);
		SceneInfo sceneInfo = mApp.sceneInfoMap.get(gwID + sceneID);
		if (sceneInfo == null)
			return;

		String sceneName = sceneInfo.getName();
		String messageType = Messages.TYPE_SCENE_OPERATION;
		MessageEventEntity entity = new MessageEventEntity();
		entity.setGwID(gwID);
		entity.setDevID(sceneID);
		entity.setEpName(sceneName);
		entity.setPriority(Messages.PRIORITY_DEFAULT);
		entity.setTime(System.currentTimeMillis() + "");
		entity.setSmile(Messages.SMILE_DEFAULT);
		entity.setType(messageType);
		messageDao.deleteAndInsert(entity);
		EventBus.getDefault().post(new MessageEvent(messageType));
		if(mApp.mBackNotification != null)
			mApp.mBackNotification
				.showNormalNotification(
						R.id.radio_mon,
						0,
						context.getString(R.string.home_notification_timing_scene_exe_ticker),
						context.getString(R.string.scene_info_timing_scene),
						context.getString(
								R.string.home_notification_timing_scene_exe_content_2,
								sceneName));
	}

	public void GetChatMsg(String gwID, String userType, String userID,
			String from, String alias, String time, String data,
			boolean isSendBackData) {
		SocialEntity socialEntity = new SocialEntity();
		socialEntity.gwID = gwID;
		socialEntity.userType = userType;
		socialEntity.userID = userID;
		socialEntity.appID = from;
		socialEntity.userName = alias;
		socialEntity.time = time;
		socialEntity.data = data;
		homeManager.addSocialMessage(socialEntity);
		EventBus.getDefault().post(new SocialEvent(socialEntity));
	}

	public void readOfflineDevices(String gwID, String status) {

	}

	public void offlineDevicesBack(DeviceInfo devcieInfo,
			Set<DeviceEPInfo> devEPInfoSet) {
		if(!UserRightUtil.getInstance().canSeeDevice(devcieInfo.getDevID())) {
			//没有权限
			return;
		}
		WulianDevice device = deviceCache.getDeviceByID(mContext,
				devcieInfo.getGwID(), devcieInfo.getDevID());
		if (device == null)
			device = deviceCache.startUpDevice(mContext, devcieInfo,
					devEPInfoSet);
		Logger.debug("devcie offline:" + device + ";" + devcieInfo.getType());
		device.setDeviceOnLineState(false);
		for (DeviceEPInfo info : devEPInfoSet) {
			deviceDao.insertOrUpdate(devcieInfo, info);
		}
		EventBus.getDefault().post(
				new DeviceEvent(DeviceEvent.REFRESH, devcieInfo, false));
	}

	public void GetAutoProgramTaskInfo(String gwID,
			List<AutoProgramTaskInfo> autoTaskInfos) {

//		AutoProgramTaskManager manager = AutoProgramTaskManager.getInstance();
//		manager.clear();
		if (!autoTaskInfos.isEmpty()) {
			for (AutoProgramTaskInfo taskInfo : autoTaskInfos) {
				manager.updateBasicAutoTaskInfo(taskInfo);
			}
		}
		EventBus.getDefault().post(new AutoTaskEvent());
	}
	
	/**
	 * 
	 * @param gwID
	 * @param cmdtype
	 * @param rulesGroupInfos
	 */
	//暂时只解析groupID为1的场景定时规则是否有效
	public void getAutoProgramRulesEffectStatus(String gwID, String cmdtype,
			List<RulesGroupInfo> rulesGroupInfos) {
		
		if(!rulesGroupInfos.isEmpty()){
			for(int i = 0; i < rulesGroupInfos.size(); i++){
				RulesGroupInfo groupInfo = rulesGroupInfos.get(i);
				if(StringUtil.equals(groupInfo.getGroupID(), "1")){
					String timingStatus = groupInfo.getStatus();
					boolean status = true;
					if(StringUtil.equals(timingStatus, "2")){
						status = true;
					}else if(StringUtil.equals(timingStatus, "1")){
						status = false;
					}
					Preference.getPreferences().putBoolean(gwID + groupInfo.getGroupID() + IPreferenceKey.P_KEY_HOUSE_RULE_TIMING_STATUS, status);
				}
			}
		}
		
	}
	AutoProgramTaskManager manager = AutoProgramTaskManager.getInstance();
	public void SetAutoProgramTaskInfo(String gwID, String operType,
			AutoProgramTaskInfo autoProgramTaskInfo) {
		if (StringUtil.equals(operType, AutoTask.AUTO_TASK_OPER_TYPE_DELETE)) {
			manager.deleteAutoTaskInfo(gwID, autoProgramTaskInfo.getProgramID());
			EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.REMOVE));
		} else if (StringUtil
				.equals(operType, AutoTask.AUTO_TASK_OPER_TYPE_ADD)) {
			manager.updateAutoTaskInfo(autoProgramTaskInfo);
			EventBus.getDefault()
					.post(new AutoTaskEvent(AutoTaskEvent.ADDRULE));
		} else if (StringUtil.equals(operType,
				AutoTask.AUTO_TASK_OPER_TYPE_QUERY)) {
			manager.updateAutoTaskInfo(autoProgramTaskInfo);
			EventBus.getDefault()
					.post(new AutoTaskEvent(AutoTaskEvent.QUERY,
							autoProgramTaskInfo));
		} else if (StringUtil.equals(operType,
				AutoTask.AUTO_TASK_OPER_TYPE_MODIFY)) {
			manager.updateAutoTaskInfo(autoProgramTaskInfo);
			EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.MODIFY));
		}else if (StringUtil.equals(operType,
				AutoTask.AUTO_TASK_OPER_TYPE_STATUS)) {
			manager.updateAutoTaskInfo(autoProgramTaskInfo);
			EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.STATUS));
		}
	}

	// 梦想之花设置
	public void getDreamFlowerConfigMsg(String gwID, String cmdindex,
			String cmdtype, JSONObject data) {
		switch (StringUtil.toInteger(cmdindex)) {
		case 1: // 立即播报
			parseJsonForSingleData(data, FlowerEvent.ACTION_FLOWER_IMMEDIATELY_BROADCAST);
			break;
		case 2: // 敲击灯效
			parseJsonForSingleData(data, FlowerEvent.ACTION_FLOWER_SELECT_LIGHT_EFFECT);
			break;
		case 21:
			if(data.containsKey(ConstUtil.KEY_DATA)){
				JSONArray aray = data.getJSONArray(ConstUtil.KEY_DATA);
				for(int i=0;i< aray.size();i++){
					JSONObject jsonObject = aray.getJSONObject(i);
					FlowerVoiceControlEntity entity = new FlowerVoiceControlEntity();
					entity.setIndex(jsonObject.getString("index"));
					entity.setBindScene(jsonObject.getString("bindscene"));
					entity.setStudy(jsonObject.getString("studied"));
					entity.setGwID(gwID);
					flowerManager.putVoiceControlEntity(entity);
				}
			}
			EventBus.getDefault().post(new FlowerEvent(FlowerEvent.ACTION_VOICE_CONTROL_GET));
			break;
		case 22:
			if(data.containsKey(ConstUtil.KEY_DATA)){
				JSONObject object = data.getJSONObject(ConstUtil.KEY_DATA);
				FlowerVoiceControlEntity entity = flowerManager.getVoiceControlEntity(object.getString("index"));
				if(entity != null)
					entity.setStudy(FlowerVoiceControlEntity.VALUE_STUDYED);
				EventBus.getDefault().post(new FlowerEvent(FlowerEvent.ACTION_VOICE_CONTROL_STATE));
			}
			break;
		case 23:
			if(data.containsKey(ConstUtil.KEY_DATA)){
				JSONObject object = data.getJSONObject(ConstUtil.KEY_DATA);
				FlowerEvent  event = new FlowerEvent(FlowerEvent.ACTION_VOICE_CONTROL_STATE);
				String reason = object.getString("reason");
				event.setEventStr(reason);
				EventBus.getDefault().post(event);
			}
			break;
		case 24:
			if(data.containsKey(ConstUtil.KEY_DATA)){
				JSONObject object = data.getJSONObject(ConstUtil.KEY_DATA);
				FlowerVoiceControlEntity entity = flowerManager.getVoiceControlEntity(object.getString("index"));
				if(entity != null)
					entity.clear();
				EventBus.getDefault().post(new FlowerEvent(FlowerEvent.ACTION_VOICE_CONTROL_CLEAR));
			}
			break;
		case 25:
			if(data.containsKey(ConstUtil.KEY_DATA)){
				JSONObject object = data.getJSONObject(ConstUtil.KEY_DATA);
				FlowerVoiceControlEntity entity = flowerManager.getVoiceControlEntity(object.getString("index"));
				if(entity == null){
					entity = new FlowerVoiceControlEntity();
					entity.setStudy(FlowerVoiceControlEntity.VALUE_STUDYED);
					entity.setGwID(gwID);
				}
				entity.setBindScene(object.getString("bindscene"));
				flowerManager.putVoiceControlEntity(entity);
				EventBus.getDefault().post(new FlowerEvent(FlowerEvent.ACTION_VOICE_CONTROL_BIND));
			}
			break;
		case 3: // 敲击灯效持续时间
			parseJsonForSingleData(data, FlowerEvent.ACTION_FLOWER_SET_LIGHT_TIME);
			break;
		case 4: // 定时播报
			StorageFlowerTimes(data, FlowerEvent.ACTION_FLOWER_SET_BROADCAST_TIME, cmdtype);
			break;
		case 5:
			StorageFlowerShowTimes(data, FlowerEvent.ACTION_FLOWER_SET_SHOW_TIME, cmdtype);// 定时显示
			break;
		case 61: // //播报设置-播报开关
			parseJsonForSingleData(data, FlowerEvent.ACTION_FLOWER_BROADCAST_SWITCH);
			break;
		case 62: // 播报设置-常规播报
			parseJsonForSingleData(data, FlowerEvent.ACTION_FLOWER_BROADCAST_CONVENTIONAL);
			break;
		case 63: // 播报设置-网络提示音
			parseJsonForSingleData(data, FlowerEvent.ACTION_FLOWER_BROADCAST_NETWORK_PROMPT);
			break;
		case 64: // 播报设置-辅助提示音
			parseJsonForSingleData(data, FlowerEvent.ACTION_FLOWER_BROADCAST_AUXILIARY_CUE);
			break;
		case 65: // 播报设置-音量设置
			parseJsonForSingleData(data, FlowerEvent.ACTION_FLOWER_BROADCAST_VOLUME);
			break;
		case 7: // 位置设置
			parseJsonForSingleData(data, FlowerEvent.ACTION_FLOWER_POSITION_SET);
			break;
		}
	}

	// 保存定时播报时间列表到FlowerManager
	private synchronized void StorageFlowerTimes(JSONObject jsonObject, String key,String type) {
		
		JSONArray data = jsonObject.getJSONArray(ConstUtil.KEY_DATA);	
		if (ConstUtil.KEY_CMD_TYPE_SET.equals(type)) {
			SendMessage.sendGetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), CmdUtil.FLOWER_TIMING_BROADCAST);
		} else if (ConstUtil.KEY_CMD_TYPE_GET.equals(type)) {
			FlowerManager flowerTimeManager = FlowerManager.getInstance();
			List<TimingFlowerEntity> list = flowerTimeManager.getFlowerTimingEntities(key);
			list.clear();
			try {
				if(data!=null){
					for (int i = 0; i < data.size(); i++) {					
						TimingFlowerEntity entity = new TimingFlowerEntity();
						JSONObject obj = data.getJSONObject(i);
						String time = obj.getString("playTime");			
						if (time != null&& !time.contains("null")) {
							entity.setTime(time.substring(0, time.length() - 2));
							entity.setWeekDay(time.substring(time.length() - 2,time.length()));
							list.add(entity);
						}					
					}	
				}							
			}catch (JSONException e) {
				e.printStackTrace();
			}
			EventBus.getDefault().post(new FlowerEvent(key));
		}		
	}

	// 保存定时显示时间列表到FlowerManager
	private synchronized void StorageFlowerShowTimes(JSONObject jsonObject,String key, String type) {
		
		JSONArray data = jsonObject.getJSONArray(ConstUtil.KEY_DATA);				
		if (ConstUtil.KEY_CMD_TYPE_SET.equals(type)) {
			SendMessage.sendGetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(),CmdUtil.FLOWER_SET_SHOW_TIME);
		}else if (ConstUtil.KEY_CMD_TYPE_GET.equals(type)) {
			FlowerManager flowerTimeManager = FlowerManager.getInstance();
			List<TimingFlowerEntity> list = flowerTimeManager.getFlowerTimingEntities(key);
			list.clear();
			try {
				if(data!=null){
					for (int i = 0; i < data.size(); i++) {					
						TimingFlowerEntity entity = new TimingFlowerEntity();
						JSONObject obj = data.getJSONObject(i);
						String time = obj.getString("showTimeRegin");					
						if (time != null&& !time.contains("null")) {
							entity.setTime(time.substring(0, time.length() - 2));
							entity.setWeekDay(time.substring(time.length() - 2,time.length()));
							list.add(entity);
						}										
					} 					
				}				
			}catch (JSONException e) {
				e.printStackTrace();
			}
			EventBus.getDefault().post(new FlowerEvent(key));
		}				
	}
	
	/**
	 * JSONObject 的data项不是数组而是字符串时调用
	 * @param jsonObject
	 * @param action 
	 */
	private synchronized void parseJsonForSingleData(JSONObject jsonObject, String action) {
		String data = jsonObject.getString(ConstUtil.KEY_DATA);
		if (data != null) {
			FlowerEvent flowerEvent = new FlowerEvent();
			flowerEvent.setAction(action);
			flowerEvent.setEventStr(data);
			EventBus.getDefault().post(flowerEvent);
		}
	}

	// 时区设置
	public void getTimezonConfigMsg(String gwID, String cmdtype, JSONObject data) {
		FlowerEvent event = new FlowerEvent();
		event.setData(data);
		if(ConstUtil.KEY_CMD_TYPE_SET.equals(cmdtype)){
			event.setAction(FlowerEvent.ACTION_FLOWER_TIMEZONE_SET);
		}else if(ConstUtil.KEY_CMD_TYPE_GET.equals(cmdtype)){
			event.setAction(FlowerEvent.ACTION_FLOWER_TIMEZONE_GET);
		}
		EventBus.getDefault().post(event);
	}

	//云盘
	public void cloudConfigMsg(String gwID, String cmd, String cmdindex,JSONObject data) {
		StringBuffer key=new StringBuffer();
		key.append(gwID).append(cmd);
		if(cmdindex!=null&&cmdindex.length()>0)key.append(cmdindex);
		FlowerEvent event = new FlowerEvent();
		try{
			int status=data.getIntValue("status");
			if(status!=0){
				JsUtil.getInstance().execSavedCallback(key.toString(), data.getString("msg"), JsUtil.ERROR, true);
			}else{
				switch (StringUtil.toInteger(cmdindex)) {
				case 1:
				case 8:
					try{
						JSONObject page=data.getJSONObject("page");
						if(page!=null){
							int total=page.getIntValue("total");
						    int fromNum=page.getIntValue("fromnum");
						    JsUtil.getInstance().execSavedCallback(key.toString(), data.toJSONString(), JsUtil.OK, (fromNum*20)>=total);
						}else{
							JsUtil.getInstance().execSavedCallback(key.toString(), data.toJSONString(), JsUtil.OK, true);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
				case 9: //9.硬盘使用情况
					event.setAction(FlowerEvent.ACTION_FLOWER_HARD_DISK_INFO);
					JSONObject result=data.getJSONArray("data").getJSONObject(0);
					String used=result.getString("used");
					String title=result.getString("total");
					if(used==null||used.length()==0){
						used="0G";
					}
					if(title==null||title.length()==0){
						title="0G";
					}
					event.setEventStr(used+"/"+title);
					EventBus.getDefault().post(event);
					break;
				default :
					JsUtil.getInstance().execSavedCallback(key.toString(), data.toJSONString(), JsUtil.OK, true);
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//通用设备配置，下面两个方法哪个正确？
	public void commondDeviceConfiguration(String gwID, String cmd,JSONObject data) {
		if(data!=null){
			System.out.println(data.toJSONString());
			EventBus.getDefault().post(new CommondDeviceConfigurationEvent(gwID,data.toJSONString()));
		}
	}
	
	public void commondDeviceConfiguration(String gwID, String devID,
			String mode, long time, String key, String data)
	{
//		String callBackId=gwID+"406";
		EventBus.getDefault().post(new DeviceUeiItemEvent(gwID, devID, mode, time, key, data));
		EventBus.getDefault().post(new Command406Result(gwID, devID, mode, time+"", key, data));
		JSONObject jsonObject = JSON.parseObject(data);
		Log.i("406Data",data);
		Intent it = new Intent();
		it.setAction("send406Data");//// TODO: 2017/1/19  需要限制一下发广播的时机，不能每次406回来都发送广播，影响性能
		it.putExtra("406Data", jsonObject.getString("pass"));
		mContext.sendBroadcast(it);
	}
}