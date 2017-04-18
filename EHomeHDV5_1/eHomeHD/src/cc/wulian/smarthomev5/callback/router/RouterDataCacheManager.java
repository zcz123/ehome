package cc.wulian.smarthomev5.callback.router;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.callback.router.entity.BlackAndWhiteData;
import cc.wulian.smarthomev5.callback.router.entity.BlackAndWhiteEntity;
import cc.wulian.smarthomev5.callback.router.entity.DeviceData;
import cc.wulian.smarthomev5.callback.router.entity.DeviceInfo;
import cc.wulian.smarthomev5.callback.router.entity.GatewayCloseRouter;
import cc.wulian.smarthomev5.callback.router.entity.GatewayModeData;
import cc.wulian.smarthomev5.callback.router.entity.Get2_4GData;
import cc.wulian.smarthomev5.callback.router.entity.Get5GData;
import cc.wulian.smarthomev5.callback.router.entity.GetRadioEntity;
import cc.wulian.smarthomev5.callback.router.entity.GetWifi_ifaceEntity;
import cc.wulian.smarthomev5.callback.router.entity.SpeedBandEntity;
import cc.wulian.smarthomev5.callback.router.entity.SpeedData;
import cc.wulian.smarthomev5.callback.router.entity.SpeedListEntity;
import cc.wulian.smarthomev5.callback.router.entity.SpeedListQosEntity;
import cc.wulian.smarthomev5.callback.router.entity.SpeedStatusEntity;
import cc.wulian.smarthomev5.event.RouterBWModeEvent;
import cc.wulian.smarthomev5.event.RouterBlackListEvent;
import cc.wulian.smarthomev5.event.RouterDevcieInfoEvent;
import cc.wulian.smarthomev5.event.RouterWifiSettingEvent;
import cc.wulian.smarthomev5.event.RouterWifiSpeedEvent;
import cc.wulian.smarthomev5.event.RouterZigbeeChannelEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import de.greenrobot.event.EventBus;

public class RouterDataCacheManager {
	private static RouterDataCacheManager instance = null;
	// 设备列表
	private List<DeviceData> deviceInfos = new ArrayList<DeviceData>();
	// 黑名单
	private List<BlackAndWhiteData> balckLists = new ArrayList<BlackAndWhiteData>();
	// 白名单
	private List<BlackAndWhiteData> whiteLists = new ArrayList<BlackAndWhiteData>();
	// 限速
	private List<SpeedData> speedLists = new ArrayList<SpeedData>();
	// 获取2.4G返回数据
	private List<Get2_4GData> get2_4GLists = new ArrayList<Get2_4GData>();
	// 获取5G返回数据
	private List<Get5GData> get5GLists = new ArrayList<Get5GData>();
	//gw003模式判断
	private List<GatewayModeData> modedatas = new ArrayList<GatewayModeData>();
	//mini网关关闭路由判断
	private List<GatewayCloseRouter> routerdatas = new ArrayList<GatewayCloseRouter>(); 
	

	private Map<String, SpeedListQosEntity> curDeviceSpeedMaps = new HashMap<String, SpeedListQosEntity>();
	private String mode;
	private int curZigbeeChannel;
	private static final String NO_SHASH_ZIGBEE_00 = "00";

	private RouterDataCacheManager() {
	}

	public String getCurMode() {
		return mode;
	}

	public List<DeviceData> getDeviceInfos() {
		return deviceInfos;
	}

	public List<BlackAndWhiteData> getBalckLists() {
		return balckLists;
	}

	public List<BlackAndWhiteData> getWhiteLists() {
		return whiteLists;
	}

	public List<SpeedData> getSpeedLists() {
		return speedLists;
	}

	public List<Get2_4GData> getGet2_4GLists() {
		return get2_4GLists;
	}

	public List<Get5GData> getGet5GLists() {
		return get5GLists;
	}

	public SpeedListQosEntity getDeviceQosEntity(String mac) {
		return curDeviceSpeedMaps.get(mac);
	}

	// TODO zigbee信道转换2.4Wifi
	public int getZigbeeChannel() {
		if (curZigbeeChannel != 0) {
			return zigbeeToWifiChannel(curZigbeeChannel);
		}
		return 0;
	}

	public int zigbeeToWifiChannel(int zigbeeChannel) {
		int zigbeeFrequency = 2401 + 5 * (zigbeeChannel - 11);
		int wifiChannel = Math.round((zigbeeFrequency - 2412) / 5.0f) + 1;
		if (wifiChannel < 1) {
			return 1;
		}
		if (wifiChannel > 13) {
			return 13;
		}
		return wifiChannel;

	}

	public synchronized static RouterDataCacheManager getInstance() {
		if (instance == null) {
			instance = new RouterDataCacheManager();
		}
		return instance;
	}

	public synchronized void callBackZigbeeChannel(String gwID, String mode,
			String gwChannel) {
		if (CmdindexTools.SET_DATA_0.equals(mode)) {
			if (!NO_SHASH_ZIGBEE_00.equals(gwChannel)) {
				curZigbeeChannel = StringUtil.toInteger(gwChannel, 16);
				EventBus.getDefault().post(
						new RouterZigbeeChannelEvent(
								RouterZigbeeChannelEvent.ACTION_REFRESH,
								curZigbeeChannel));
			}
		}
	}

	// 解析路由返回数据
	public void callBackRouterData(String gwID, String cmdIndex,
			String cmdType, JSONObject data) {
		if (data == null || StringUtil.isNullOrEmpty(data.toJSONString())) {
			return;
		}
		// 解析获取***的返回数据,cmdIndex 1,2,3,4,5,11,14
		if (ConstUtil.KEY_CMD_TYPE_GET.equals(cmdType) && data != null) {
			if (CmdindexTools.CMDINDEX_1.equals(cmdIndex)) {
				// 获取设备列表
				initGetDeviceList(data);
			} else if (CmdindexTools.CMDINDEX_2.equals(cmdIndex)) {
				// 获取白名单
				initGetWhiteList(data);
			} else if (CmdindexTools.CMDINDEX_3.equals(cmdIndex)) {
				// 获取限速
				initGetSpeedData(data);
			} else if (CmdindexTools.CMDINDEX_4.equals(cmdIndex)) {
				// 获取2.4G返回数据
				initGet2_4GData(data);
			} else if (CmdindexTools.CMDINDEX_5.equals(cmdIndex)) {
				// 获取5G返回数据
				initGet5GData(data);
			} else if(CmdindexTools.CMDINDEX_7.equals(cmdIndex)){
				//关闭路由功能判断
				initCloseRouterData(data);
			} else if (CmdindexTools.CMDINDEX_11.equals(cmdIndex)) {
				// 获取访问控制状态
				initGetVisitControlStatus(data);
			} else if (CmdindexTools.CMDINDEX_14.equals(cmdIndex)) {
				// 获取黑名单
				initGetBlackList(data);

			}else if(CmdindexTools.CMDINDEX_15.equals(cmdIndex)){
				//获取gw003模式判断
				initGetGatewayMode(data);
			}

		}
		/**
		 * 返回设置数据命令的话,目的是重新获取当前状态数据.原因是设置操作后返回设置成功的数据,
		 * 但调试时发现存在部分数据未刷新的情况,所以此处使用该方式重新请求最新转台只做状态数据的解析
		 */
		else if (ConstUtil.KEY_CMD_TYPE_SET.equals(cmdType) && data != null) {
			if (CmdindexTools.CMDINDEX_2.equals(cmdIndex)) {

			} else if (CmdindexTools.CMDINDEX_3.equals(cmdIndex)) {
				NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_3);

			} else if (CmdindexTools.CMDINDEX_4.equals(cmdIndex)) {
				NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_4);

			} else if (CmdindexTools.CMDINDEX_5.equals(cmdIndex)) {
				NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_5);

			} else if (CmdindexTools.CMDINDEX_6.equals(cmdIndex)) {

			} else if (CmdindexTools.CMDINDEX_7.equals(cmdIndex)) {
				NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_4);
				// set2_4GSwitchStatus(data);

			} else if (CmdindexTools.CMDINDEX_8.equals(cmdIndex)) {
				NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_5);
				// set5GSwitchStatus(data);

			} else if (CmdindexTools.CMDINDEX_9.equals(cmdIndex)) {
				// setBlackAndWhiteSwitchStatus(data);

			} else if (CmdindexTools.CMDINDEX_10.equals(cmdIndex)) {
				// setBlackAndWhiteSwitchEffectiveStatus(data);

			} else if (CmdindexTools.CMDINDEX_12.equals(cmdIndex)) {
				// setQosSwitchStatus(data);
				NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_3);

			} else if (CmdindexTools.CMDINDEX_13.equals(cmdIndex)) {
				// setSpeedModel(data);
				NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_3);

			} else if (CmdindexTools.CMDINDEX_14.equals(cmdIndex)) {
				NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_14);
				// 设置黑名单
				// setBlackList(data);
			}
		}
	}

	/**
	 * {"cmd":"401","cmdindex":"1","cmdtype":"get","data":
	 * [{"code":0,"info":[{"assoc":1,"down":0,"ip":"192.168.188.146",
	 * "mac":"8C:2D:AA:78:9D:80","name":"unknown","up":0,"uptime":2845},
	 * {"assoc":0,"down":0,"ip":"192.168.188.6","mac":"00:E0:4C:F3:09:66",
	 * "name":"unknown","up":0,"uptime":4302},{"assoc":0,"down":0,
	 * "ip":"192.168.188.178","mac":"0C:4D:E9:BD:76:44","name":"unknown"
	 * ,"up":0,"uptime":2227}],"msg":""}],"gwID":"50294DFFFFFD"}
	 * 
	 * @param data
	 */
	private void initGetDeviceList(JSONObject data) {
		try {
			deviceInfos.clear();
			JSONArray dataCmdIndex1 = data.getJSONArray(ConstUtil.KEY_DATA);
			// data Array
			for (int i = 0; i < dataCmdIndex1.size(); i++) {
				DeviceData deviceData = new DeviceData();
				List<DeviceInfo> info = new ArrayList<DeviceInfo>();
				// 从JsonArray中获取jsonObject
				JSONObject dataJsonObject = (JSONObject) dataCmdIndex1.get(i);

				// deviceData.setCode(dataJsonObject.getInt(KeyTools.code));
				// deviceData.setMsg(dataJsonObject.getString(KeyTools.msg));
				// JsonObject获取Info的JsonObject
				JSONArray infoArray = dataJsonObject
						.getJSONArray(KeyTools.info);
				// info Array
				for (int j = 0; j < infoArray.size(); j++) {
					JSONObject infoJsonObject = (JSONObject) infoArray.get(j);
					DeviceInfo deviceInfo = new DeviceInfo();

					deviceInfo.setMac(infoJsonObject.getString(KeyTools.mac));
					deviceInfo.setName(infoJsonObject.getString(KeyTools.name));
					deviceInfo.setUp(infoJsonObject.getInteger(KeyTools.up));
					deviceInfo.setAssoc(infoJsonObject.getInteger(KeyTools.assoc));
					deviceInfo
							.setUptime(infoJsonObject.getInteger(KeyTools.uptime));
					deviceInfo.setDown(infoJsonObject.getInteger(KeyTools.down));
					deviceInfo.setIp(infoJsonObject.getString(KeyTools.ip));
					info.add(deviceInfo);
				}
				EventBus.getDefault().post(
						new RouterDevcieInfoEvent(
								RouterDevcieInfoEvent.ACTION_REFRESH, info));
				deviceData.setInfo(info);
				deviceInfos.add(deviceData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initGetWhiteList(JSONObject data) {
		whiteLists.clear();
		try {
			JSONArray dataCmdIndex2 = data.getJSONArray(ConstUtil.KEY_DATA);
			for (int i = 0; i < dataCmdIndex2.size(); i++) {
				JSONObject dataObject = dataCmdIndex2.getJSONObject(i);
				List<BlackAndWhiteEntity> list = new ArrayList<BlackAndWhiteEntity>();
				BlackAndWhiteData whiteData = new BlackAndWhiteData();

				whiteData.setCode(dataObject.getInteger(KeyTools.code));
				whiteData.setMsg(dataObject.getString(KeyTools.msg));
				JSONArray listArray = dataObject.getJSONArray(KeyTools.list);
				for (int j = 0; j < listArray.size(); j++) {
					JSONObject listJsonObject = (JSONObject) listArray.get(j);
					BlackAndWhiteEntity entity = new BlackAndWhiteEntity();

					entity.setMac(listJsonObject.getString(KeyTools.mac));
					entity.setName(listJsonObject.getString(KeyTools.name));
					list.add(entity);
				}
				whiteData.setList(list);
				whiteLists.add(whiteData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initGetBlackList(JSONObject data) {
		balckLists.clear();
		try {
			JSONArray dataCmdIndex14 = data.getJSONArray(ConstUtil.KEY_DATA);
			for (int i = 0; i < dataCmdIndex14.size(); i++) {
				JSONObject dataObject = dataCmdIndex14.getJSONObject(i);
				List<BlackAndWhiteEntity> blackList = new ArrayList<BlackAndWhiteEntity>();
				BlackAndWhiteData balckData = new BlackAndWhiteData();

				balckData.setCode(dataObject.getInteger(KeyTools.code));
				balckData.setMsg(dataObject.getString(KeyTools.msg));
				JSONArray listArray = dataObject.getJSONArray(KeyTools.list);
				for (int j = 0; j < listArray.size(); j++) {
					JSONObject listJsonObject = (JSONObject) listArray.get(j);
					BlackAndWhiteEntity entity = new BlackAndWhiteEntity();

					entity.setMac(listJsonObject.getString(KeyTools.mac));
					entity.setName(listJsonObject.getString(KeyTools.name));
					blackList.add(entity);
				}
				EventBus.getDefault()
						.post(new RouterBlackListEvent(
								RouterBlackListEvent.ACTION_REFRESH, blackList));
				balckData.setList(blackList);
				balckLists.add(balckData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	//竖形网关
	private void initGetGatewayMode(JSONObject data){
		modedatas.clear();
		try {
			JSONArray dataCmdIndex15 = data.getJSONArray(ConstUtil.KEY_DATA);
			for (int i = 0; i < dataCmdIndex15.size(); i++) {
				JSONObject dataObject = dataCmdIndex15.getJSONObject(i);
			   GatewayModeData modedata = new GatewayModeData();
			   modedata.setMsg(dataObject.getString("mode"));
			   EventBus.getDefault().post(modedata);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	//mini网关
	private void initCloseRouterData(JSONObject data){
		routerdatas.clear();
		try {
			JSONArray dataCmdIndex7 = data.getJSONArray(ConstUtil.KEY_DATA);
			for (int i = 0; i < dataCmdIndex7.size(); i++) {
				JSONObject dataObject = dataCmdIndex7.getJSONObject(i);
			   GatewayCloseRouter routerdata = new GatewayCloseRouter();
			   routerdata.setMsg(dataObject.getString("on"));
			   EventBus.getDefault().post(routerdata);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 刷新限速数据
	/*
	 * //获取限速返回指令 {"cmd":"401","cmdindex":"3","cmdtype":"get",
	 * "data":[{"band":{"download":0,"upload":0},"code":0,
	 * "list":[{"assoc":0,"down":0,"ip":"192.168.188.6",
	 * "mac":"00:E0:4C:F3:09:66","name":"unknown",
	 * "qos":{"downmax":0,"downmin":0,"level":2,"maxdownper":100,
	 * "upmax":0,"upmaxper":100,"upmin":0},"up":0,"uptime":242}],
	 * "status":{"mode":1,"on":1}}],"gwID":"50294DFFFFFD","subtype":""}
	 */
	/*
	 * read<--{"cmd":"401","cmdindex":"3","cmdtype":"get",
	 * "data":[{"band":{"download":0,"upload":0},"code":0,
	 * "status":{"mode":0,"on":0}}],"gwID":"50294DFF8624"}
	 */
	private void initGetSpeedData(JSONObject data) {
		speedLists.clear();
		try {
			JSONArray dataCmdIndex3 = data.getJSONArray(ConstUtil.KEY_DATA);
			for (int i = 0; i < dataCmdIndex3.size(); i++) {
				JSONObject dataObject = dataCmdIndex3.getJSONObject(i);
				SpeedData speedData = new SpeedData();
				List<SpeedListEntity> list = new ArrayList<SpeedListEntity>();
				SpeedBandEntity bandEntity = new SpeedBandEntity();
				SpeedStatusEntity statusEntity = new SpeedStatusEntity();
				speedData.setCode(dataObject.getInteger(KeyTools.code));

				/**
				 * "data":[{"band":{"download":0,"upload":0},"code":0,
				 * "status":{"mode":0,"on":0}}],"gwID":"50294DFF8624"}
				 */
				JSONObject bandObject = dataObject.getJSONObject(KeyTools.band);
				bandEntity.setDownload(bandObject.getInteger(KeyTools.download));
				bandEntity.setUpload(bandObject.getInteger(KeyTools.upload));
				speedData.setBand(bandEntity);

				JSONObject statusObject = dataObject
						.getJSONObject(KeyTools.status);
				statusEntity.setMode(statusObject.getInteger(KeyTools.mode));
				statusEntity.setOn(statusObject.getInteger(KeyTools.on));
				speedData.setStatus(statusEntity);
				// list可能不存在
				List<String> noObtainSpeedKeyList = new ArrayList<String>();
				for(String key : curDeviceSpeedMaps.keySet()){
					noObtainSpeedKeyList.add(key);
				}
				if (dataObject.containsKey(KeyTools.list)) {
					JSONArray speedlistArray = dataObject
							.getJSONArray(KeyTools.list);
					for (int j = 0; j < speedlistArray.size(); j++) {
						SpeedListEntity speedListEntity = new SpeedListEntity();
						SpeedListQosEntity speedListQosEntity = new SpeedListQosEntity();
						JSONObject listJsonObject = speedlistArray
								.getJSONObject(j);
						speedListEntity.setAssoc(listJsonObject
								.getInteger(KeyTools.assoc));
						speedListEntity.setDown(listJsonObject
								.getInteger(KeyTools.down));
						speedListEntity.setIp(listJsonObject
								.getString(KeyTools.ip));
						speedListEntity.setMac(listJsonObject
								.getString(KeyTools.mac));
						speedListEntity.setName(listJsonObject
								.getString(KeyTools.name));
						speedListEntity.setUp(listJsonObject
								.getInteger(KeyTools.up));
						speedListEntity.setUptime(listJsonObject
								.getInteger(KeyTools.uptime));

						JSONObject speedListQosObject = listJsonObject
								.getJSONObject(KeyTools.qos);
						speedListQosEntity.setDownmax(speedListQosObject
								.getInteger(KeyTools.downmax));
						speedListQosEntity.setDownmin(speedListQosObject
								.getInteger(KeyTools.downmin));
						speedListQosEntity.setLevel(speedListQosObject
								.getInteger(KeyTools.level));
						speedListQosEntity.setMaxdownper(speedListQosObject
								.getInteger(KeyTools.maxdownper));
						speedListQosEntity.setUpmax(speedListQosObject
								.getInteger(KeyTools.upmax));
						speedListQosEntity.setUpmaxper(speedListQosObject
								.getInteger(KeyTools.upmaxper));
						speedListQosEntity.setUpmin(speedListQosObject
								.getInteger(KeyTools.upmin));
						speedListEntity.setQos(speedListQosEntity);
						list.add(speedListEntity);
						if(noObtainSpeedKeyList.contains(speedListEntity.getMac())){
							noObtainSpeedKeyList.remove(speedListEntity.getMac());
						}
						curDeviceSpeedMaps.put(speedListEntity.getMac(),
								speedListQosEntity);
					}
				}
				for(String key : noObtainSpeedKeyList){
					if(curDeviceSpeedMaps.containsKey(key)){
						curDeviceSpeedMaps.remove(key);
					}
				}
				EventBus.getDefault().post(new RouterWifiSpeedEvent(RouterWifiSpeedEvent.ACTION_REFRESH,statusEntity, list, bandEntity));
				speedData.setList(list);
				speedLists.add(speedData);

			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * {"cmd":"401","cmdindex":"4","cmdtype":"get","data":
	 * [{"code":0,"msg":"","radio0":[{"channel":"0","disabled":"0"}]
	 * ,"wifi_iface":[{"encryption":"none","key":"",
	 * "mode":"ap","ssid":"DreamFlower_xiejz_2.4G"}]}], "gwID":"50294DFFFFFD"}
	 * 
	 * @param data
	 */
	private void initGet2_4GData(JSONObject data) {
		get2_4GLists.clear();
		try {
			JSONArray jsonCmdIndex4 = data.getJSONArray(ConstUtil.KEY_DATA);
			for (int i = 0; i < jsonCmdIndex4.size(); i++) {
				Get2_4GData get2_4gData = new Get2_4GData();
				JSONObject dataObject = jsonCmdIndex4.getJSONObject(i);
				List<GetRadioEntity> radio0Entities = new ArrayList<GetRadioEntity>();
				List<GetWifi_ifaceEntity> wifi_ifaceEntities = new ArrayList<GetWifi_ifaceEntity>();

				get2_4gData.setCode(dataObject.getInteger(KeyTools.code));
				get2_4gData.setMsg(dataObject.getString(KeyTools.msg));

				JSONArray radio0Array = dataObject
						.getJSONArray(KeyTools.radio0);
				for (int j = 0; j < radio0Array.size(); j++) {
					GetRadioEntity radio0Entity = new GetRadioEntity();
					JSONObject radio0Object = radio0Array.getJSONObject(j);
					radio0Entity.setChannel(radio0Object
							.getString(KeyTools.channel));
					radio0Entity.setDisabled(radio0Object
							.getString(KeyTools.disabled));

					radio0Entities.add(radio0Entity);
				}
				get2_4gData.setRadio0(radio0Entities);

				JSONArray wifi_ifaceArray = dataObject
						.getJSONArray(KeyTools.wifi_iface);
				for (int k = 0; k < wifi_ifaceArray.size(); k++) {
					GetWifi_ifaceEntity wifi_ifaceEntity = new GetWifi_ifaceEntity();
					JSONObject wifi_ifaceObject = wifi_ifaceArray
							.getJSONObject(k);
					wifi_ifaceEntity.setEncryption(wifi_ifaceObject
							.getString(KeyTools.encryption));
					wifi_ifaceEntity.setKey(wifi_ifaceObject
							.getString(KeyTools.key));
					wifi_ifaceEntity.setMode(wifi_ifaceObject
							.getString(KeyTools.mode));
					wifi_ifaceEntity.setSsid(wifi_ifaceObject
							.getString(KeyTools.ssid));
					wifi_ifaceEntity.setChannel(wifi_ifaceObject.getString(KeyTools.channel));
					wifi_ifaceEntity.setSet_channel(wifi_ifaceObject.getString(KeyTools.set_channel));
					wifi_ifaceEntities.add(wifi_ifaceEntity);
				}
				get2_4gData.setWifi_iface(wifi_ifaceEntities);
				get2_4GLists.add(get2_4gData);
				EventBus.getDefault().post(
						new RouterWifiSettingEvent(
								RouterWifiSettingEvent.ACTION_REFRESH,
								RouterWifiSettingEvent.TYPE_2_4G_WIFI,
								wifi_ifaceEntities, radio0Entities));
				NetSDK.setGatewayInfo(
						AccountManager.getAccountManger().getmCurrentInfo()
								.getGwID(), "0", null, null,null, null, null,
						radio0Entities.get(0).getChannel(), null,null);
			}
		} catch (JSONException e) {
//			e.printStackTrace();
			Log.e("initGet2_4GData", "initGet2_4GData: ",e);
		}

	}

	/**
	 * {"cmd":"401","cmdindex":"5","cmdtype":"get",
	 * "data":[{"code":0,"msg":"","radio1":[{"channel":"0","disabled":"0"}],
	 * "wifi_iface"
	 * :[{"encryption":"psk-mixed","key":"12345678","mode":"ap","ssid"
	 * :"8624_5G"}]}], "gwID":"50294DFF8624"}
	 * 
	 * {"cmd":"401","cmdindex":"5","cmdtype":"get","data":
	 * [{"code":0,"msg":"","radio1":[{"channel":"0","disabled":"0"}],
	 * "wifi_iface"
	 * :[{"encryption":"psk-mixed","key":"12345678","mode":"ap","ssid"
	 * :"8624_5G"}]}], "gwID":"50294DFF8624"}
	 * 
	 * @param data
	 */
	private void initGet5GData(JSONObject data) {
		get5GLists.clear();
		try {
			JSONArray jsonCmdIndex5 = data.getJSONArray(ConstUtil.KEY_DATA);
			for (int i = 0; i < jsonCmdIndex5.size(); i++) {
				Get5GData get5gData = new Get5GData();
				JSONObject dataObject = jsonCmdIndex5.getJSONObject(i);
				List<GetRadioEntity> radio1Entities = new ArrayList<GetRadioEntity>();
				List<GetWifi_ifaceEntity> wifi_ifaceEntities = new ArrayList<GetWifi_ifaceEntity>();

				get5gData.setCode(dataObject.getString(KeyTools.code));
				get5gData.setMsg(dataObject.getString(KeyTools.msg));

				JSONArray radio1Array = dataObject
						.getJSONArray(KeyTools.radio1);
				for (int j = 0; j < radio1Array.size(); j++) {
					GetRadioEntity radio1Entity = new GetRadioEntity();
					JSONObject radio1Object = radio1Array.getJSONObject(j);
					radio1Entity.setChannel(radio1Object
							.getString(KeyTools.channel));
					radio1Entity.setDisabled(radio1Object
							.getString(KeyTools.disabled));

					radio1Entities.add(radio1Entity);
				}
				get5gData.setRadio1(radio1Entities);

				JSONArray wifi_ifaceArray = dataObject
						.getJSONArray(KeyTools.wifi_iface);
				for (int k = 0; k < wifi_ifaceArray.size(); k++) {
					GetWifi_ifaceEntity wifi_ifaceEntity = new GetWifi_ifaceEntity();
					JSONObject wifi_ifaceObject = wifi_ifaceArray
							.getJSONObject(k);
					wifi_ifaceEntity.setEncryption(wifi_ifaceObject
							.getString(KeyTools.encryption));
					wifi_ifaceEntity.setKey(wifi_ifaceObject
							.getString(KeyTools.key));
					wifi_ifaceEntity.setMode(wifi_ifaceObject
							.getString(KeyTools.mode));
					wifi_ifaceEntity.setSsid(wifi_ifaceObject
							.getString(KeyTools.ssid));

					wifi_ifaceEntities.add(wifi_ifaceEntity);
				}
				get5gData.setWifi_iface(wifi_ifaceEntities);
				get5GLists.add(get5gData);
				EventBus.getDefault().post(
						new RouterWifiSettingEvent(
								RouterWifiSettingEvent.ACTION_REFRESH,
								RouterWifiSettingEvent.TYPE_5G_WIFI,
								wifi_ifaceEntities, radio1Entities));

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initGetVisitControlStatus(JSONObject data) {
		try {
			JSONArray jsonCmdIndex11 = data.getJSONArray(ConstUtil.KEY_DATA);
			for (int i = 0; i < jsonCmdIndex11.size(); i++) {
				JSONObject jsonObject = jsonCmdIndex11.getJSONObject(i);
				mode = jsonObject.getString(KeyTools.mode);
				EventBus.getDefault().post(
						new RouterBWModeEvent(RouterBWModeEvent.ACTION_REFRESH,
								mode));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void set2_4GSwitchStatus(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void set5GSwitchStatus(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void setBlackAndWhiteSwitchStatus(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void setBlackAndWhiteSwitchEffectiveStatus(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void setQosSwitchStatus(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void setSpeedModel(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void setBlackList(JSONObject data) {
		// TODO Auto-generated method stub

	}

}
