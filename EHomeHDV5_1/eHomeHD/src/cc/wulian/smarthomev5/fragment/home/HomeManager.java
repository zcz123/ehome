package cc.wulian.smarthomev5.fragment.home;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.entity.AdvertisementEntity;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.entity.SocialEntity;
import cc.wulian.smarthomev5.entity.UserLocation;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class HomeManager {
	//一天时间毫秒数
	private Preference preference = Preference.getPreferences();
	private List<MessageEventEntity> alarmMessageEntites = new ArrayList<MessageEventEntity>();
	private List<SocialEntity> socialMessageEntites = new ArrayList<SocialEntity>(10);
	private MainApplication application = MainApplication.getApplication();
	private AccountManager accountManager = AccountManager.getAccountManger();
	private static HomeManager instance;
	private volatile boolean isHomeRefresh = false;
	private DeviceCache mDeviceCache;

	private List<AdvertisementEntity> advertiseEntites = null;

	public List<AdvertisementEntity> getAdvertisementEntites() {
		return advertiseEntites;
	}

	private HomeManager(){
		mDeviceCache = DeviceCache.getInstance(application);
	}
	public static HomeManager getInstance(){
		if(instance == null){
			instance = new HomeManager();
		}
		return instance;
	}
	public List<MessageEventEntity> getHomeAlarmMessages(long startTime) {

		List<MessageEventEntity> entites = new ArrayList<MessageEventEntity>();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("gwID", accountManager.getmCurrentInfo().getGwID());
			jsonObject.put("time", startTime+"");
			jsonObject.put("mode", "1");
			String json = HttpUtil.postWulianCloud(
					WulianCloudURLManager.getDeviceInfoURL(), jsonObject);

			if (!StringUtil.isNullOrEmpty(json)) {
//				Logger.debug("getHomeAlarmMessagesjson" + json);
				JSONObject obj = JSON.parseObject(json);
				JSONArray array = obj.getJSONArray("retData");
				// 选择的日期的时间戳+一天的时间戳
				if (array != null) {// 1426831354388(3/20 14:2:34)
									// 1426830397348(3/20 13:46:37)
									// 1426757988159 > 1426734845456(3/19
									// 11:14:5)
					for (int i = 0; i < array.size(); i++) {
						JSONObject alarmObj = array.getJSONObject(i);
						MessageEventEntity entity = new MessageEventEntity();
						entity.setTime(alarmObj.getString("time"));
						entity.setGwID(alarmObj.getString("gwID"));
//						entity.setEpData(alarmObj.getString("epData"));
						WulianDevice device = mDeviceCache.getDeviceByID(application, alarmObj.getString("gwID"), alarmObj.getString("devID"));
						String parseData = application.getString(R.string.device_state_alarm);
						if(device != null){
							String epData=alarmObj.getString("epData");
							parseData= device.parseDataWithProtocol(epData).toString() ;
						}
						entity.setEpData(parseData);
						entity.setEp(alarmObj.getString("ep"));
						entity.setEpType(alarmObj.getString("epType"));
						entity.setType(Messages.TYPE_DEV_ALARM);
						entity.setDevID(alarmObj.getString("devID"));
						entites.add(entity);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entites;

	}

	/**
	 * 加载设备报警信息
	 */
	public void loadHomeAlarmMessage() {
		List<MessageEventEntity> enties = new ArrayList<MessageEventEntity>();
		long beginTime = DateUtil.getTime0H0M0S(new Date());
		long startTime = new Date().getTime();
		for(int i= alarmMessageEntites.size() -1 ;i>= 0;i--){
			MessageEventEntity e = alarmMessageEntites.get(i);
			if(StringUtil.toLong(e.getTime()) < beginTime || !StringUtil.equals(AccountManager.getAccountManger().getmCurrentInfo().getGwID(),e.getGwID())){
				alarmMessageEntites.remove(i);
			}
		}
		try{
			List<MessageEventEntity> list = getHomeAlarmMessages(startTime);
			for(int i = 0 ;i< list.size() ;i++){
				MessageEventEntity e  = list.get(i);
				long time = StringUtil.toLong(e.getTime());
				if(time > beginTime) {
					enties.add(e);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		for(MessageEventEntity e :enties ){
			addAlarmMessageEntity(e);
		}
	}

	private void addAlarmMessageEntity(MessageEventEntity entity) {
		boolean isContain = false;
		for(int i= alarmMessageEntites.size() -1 ;i>= 0;i--){
			MessageEventEntity e = alarmMessageEntites.get(i);
			if(StringUtil.equals(entity.getDevID(), e.getDevID()) && StringUtil.equals(entity.getType(), e.getType())){
				isContain = true;
			}
		}
		if(!isContain)
			alarmMessageEntites.add(entity);
	}

	public void addSingleAlarmMessageEntity(MessageEventEntity entity) {
		for(int i= alarmMessageEntites.size() -1 ;i>= 0;i--){
			MessageEventEntity e = alarmMessageEntites.get(i);
			if(StringUtil.equals(entity.getDevID(), e.getDevID()) && StringUtil.equals(entity.getType(), e.getType())){
				alarmMessageEntites.remove(i);
			}
		}
		alarmMessageEntites.add(0,entity);
	}

	public List<MessageEventEntity> getAlarmMessageEntities(){
		return new ArrayList<MessageEventEntity>(alarmMessageEntites);
	}

	public void loadHomeSocialMessage(){
		List<SocialEntity>  entites = new ArrayList<SocialEntity>();
		try {
			socialMessageEntites.clear();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("gwID", accountManager.getmCurrentInfo().getGwID());
			jsonObject.put("time", DateUtil.getDateBefore(0).getTime()+"");
			String json = HttpUtil.postWulianCloud(WulianCloudURLManager.getSocialInfoURL(),jsonObject);
			if(json != null){
				Logger.debug("socialjson:" + json);
				//Logger.debug("json" + json);
				JSONObject obj = JSON.parseObject(json);
				JSONArray array = obj.getJSONArray("retData");
				if(array != null){
					for(int i = 0; i < array.size(); i++){
						JSONObject socialObj = array.getJSONObject(i);
						SocialEntity entity = new SocialEntity();
							//{"data":"你好","cmd":"90","time":1427867611132,"gwID":"DC16EB8ECDC1","from":"HD865002025926973","alias":"MI 3W"}
							entity.setData(socialObj.getString("data"));
							entity.setmCmd(socialObj.getString("cmd"));
							entity.setTime(socialObj.getString("time"));
							entity.setGwID(socialObj.getString("gwID"));
							entity.setAppID(socialObj.getString("from"));
							entity.setUserName(socialObj.getString("alias"));
							entites.add(entity);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for(int i= entites.size()-1;i >=0 ; i--){
			addSocialMessage(entites.get(i));
		}
	}

	public void addSocialMessage(SocialEntity entity){
		if(socialMessageEntites.size() >=10)
			socialMessageEntites.remove(0);
		socialMessageEntites.add(entity);
	}
	public List<SocialEntity> getSocialMessages(){
		List<SocialEntity> entites = new ArrayList<SocialEntity>();
		for(SocialEntity e:socialMessageEntites){
			entites.add(e);
		}
		return entites;
	}

	private void parseAdvData(JSONObject obj) {
		if(obj == null) {
			return;
		}
		advertiseEntites = new ArrayList<AdvertisementEntity>();
		JSONArray array = obj.getJSONArray("data");
		if (array != null) {
			for (int i = 0; i < array.size(); i++) {
				JSONObject adverObj = array.getJSONObject(i);
				AdvertisementEntity entity = new AdvertisementEntity();
				entity.setPictureIndex(adverObj.getIntValue("pictureIndex"));
				entity.setPictureLinkURL(adverObj
						.getString("pictureLinkUrl"));
				entity.setPictureURL(adverObj.getString("pictureUrl"));
				entity.setVersion(adverObj.getString("version"));
				advertiseEntites.add(entity);
			}
		}
		if(advertiseEntites!=null&&advArrivedListener!=null){
			advArrivedListener.showAdvPicture();
		}
	}

	public boolean isHomeRefresh() {
		return isHomeRefresh;
	}
	public void setHomeRefresh(boolean isHomeRefresh) {
		this.isHomeRefresh = isHomeRefresh;
	}

//	public List<AdvertisementEntity> getAdevertisementEntites(){
//	String json = preference.getString(IPreferenceKey.P_KEY_ADV_CONTENT,
//				null);
//	if (json != null) {
//		//Logger.debug("json:" + json);
//		JSONObject obj = JSON.parseObject(json);
//		JSONArray array = obj.getJSONArray("data");
//		if (array != null) {
//			for (int i = 0; i < array.size(); i++) {
//				JSONObject adverObj = array.getJSONObject(i);
//				AdvertisementEntity entity = new AdvertisementEntity();
//				entity.setPictureIndex(adverObj.getIntValue("pictureIndex"));
//				entity.setPictureLinkURL(adverObj
//						.getString("pictureLinkUrl"));
//				entity.setPictureURL(adverObj.getString("pictureUrl"));
//				entity.setVersion(adverObj.getString("version"));
//			}
//		}
//	}
//	}
	public void loadBaseData(){
		UserLocation location = preference.getAdvertisementLocation();
		try{
			JSONObject object = new JSONObject();
			object.put("gwID", accountManager.getmCurrentInfo().getGwID());
			object.put("appID", accountManager.getRegisterInfo().getAppID());
			object.put("userToken", accountManager.getRegisterInfo().getSdkToken());
			if(!StringUtil.isNullOrEmpty(location.getCountryCode())){
				object.put("country", location.getCountryCode());
			}
			object.put("language", LanguageUtil.getLanguage());
			if(!StringUtil.isNullOrEmpty(location.getProvince())){
				object.put("province", location.getProvince());
			}
			if(!StringUtil.isNullOrEmpty(location.getCityCode())){
				object.put("city", location.getCityCode());
			}
			object.put("language", LanguageUtil.getWulianCloudLanguage());
			object.put("advPicType", "1");
			object.put("appType", accountManager.getRegisterInfo().getAppType());
			object.put("appFrom", application.getResources().getString(R.string.app_from));
			object.put("appVer", accountManager.getRegisterInfo().getAppVersion());
			object.put("appToken", "0");
			object.put("simSerialNo", accountManager.getRegisterInfo().getSimSerialNo());
			object.put("phoneOS", accountManager.getRegisterInfo().getPhoneOS());
			object.put("phoneType", accountManager.getRegisterInfo().getPhoneType());
			object.put("simOperatorName", accountManager.getRegisterInfo().getSimOperatorName());
			object.put("simCountryIso", accountManager.getRegisterInfo().getSimCountryIso());
			object.put("imsiId", accountManager.getRegisterInfo().getSimId());
			JSONObject result = HttpUtil.postWulianCloudOrigin(WulianCloudURLManager.getBasicDataURL(), object);
			Logger.debug("basic data:"+result.toJSONString());
			JSONObject bodyObject = result.getJSONObject("body");
			if(bodyObject != null){
				JSONObject retDataObject = bodyObject.getJSONObject("retData");
				if(retDataObject != null){
					parseAdvData(retDataObject.getJSONObject("advData"));
					//todo:替换通知获取方式
					JSONObject noticeDataObject = retDataObject.getJSONObject("noticeData");

					JSONObject gatewayDataObject = retDataObject.getJSONObject("gatewayData");
					if(gatewayDataObject != null){
						String isGwLegal = gatewayDataObject.getString("isGwLegal");
						AccountManager.getAccountManger().getmCurrentInfo().setLegal(isGwLegal);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void initLanguage(Locale locale) {
        try {
            Configuration config = application.getResources().getConfiguration();
            DisplayMetrics dm = application.getResources().getDisplayMetrics();
            config.locale = locale;
            application.getResources().updateConfiguration(config, dm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void notifyLogined(){
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				long lastTime = preference.getLong(IPreferenceKey.P_KEY_USE_LOGINED_LOG, 0);
				long currentTime = System.currentTimeMillis();
				if(currentTime - lastTime > 30*60*60*1000){
					notifyLoginOrExit("1");
					preference.putLong(IPreferenceKey.P_KEY_USE_LOGINED_LOG, currentTime);
				}
			}
		});
	}
	public void notifyLoginOrExit(String type){
		JSONObject object = new JSONObject();
		object.put("appID", accountManager.getRegisterInfo().getAppID());
		object.put("userToken", "");
		object.put("gwID", accountManager.getmCurrentInfo().getGwID());
		object.put("type",type);
		object.put("appType", accountManager.getRegisterInfo().getAppType());
		object.put("appVer",accountManager.getRegisterInfo().getAppVersion());
		object.put("appFrom",application.getResources().getString(R.string.app_from));
		object.put("simOperatorName", accountManager.getRegisterInfo().getSimOperatorName());
		JSONObject result = HttpUtil.postWulianCloudOrigin(WulianCloudURLManager.getLoginOrExitURL(), object);
		Logger.debug("login log:"+result.toJSONString());
	}
	public void notifyExit(){
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				long lastTime = preference.getLong(IPreferenceKey.P_KEY_USE_EXIT_LOG, 0);
				long currentTime = System.currentTimeMillis();
				if(currentTime - lastTime > 30*60*60*1000){
					notifyLoginOrExit("2");
					preference.putLong(IPreferenceKey.P_KEY_USE_EXIT_LOG, currentTime);
				}
			}
		});
	}

	public void checkAdvVersion(){
        UserLocation location = preference.getAdvertisementLocation();
        AdvertisementEntity entity = new AdvertisementEntity();
        try{
            JSONObject object = new JSONObject();
            object.put("gwID", AccountManager.getAccountManger().getmCurrentInfo().getGwID());
            object.put("appID", AccountManager.getAccountManger().getRegisterInfo().getAppID());
            if(!StringUtil.isNullOrEmpty(location.getCountryCode())){
                object.put("country", location.getCountryCode());
            }
            object.put("language", LanguageUtil.getLanguage());
            object.put("advPicType", "1");
            object.put("advType", "1");
            JSONObject result = HttpUtil.postWulianCloudOrigin(WulianCloudURLManager.getBasicDataURL(), object);
            Logger.debug("advertisementTest:"+result.toJSONString());
            JSONObject bodyObject = result.getJSONObject("body");
            if(bodyObject != null) {
                JSONObject retDataObject = bodyObject.getJSONObject("retData");
                if (retDataObject != null) {
                    JSONObject advObject = retDataObject.getJSONObject("advData");
                    JSONArray array = advObject.getJSONArray("data");
                    if (array != null) {
                        for (int i = 0; i < array.size(); i++) {
                            JSONObject adverObj = array.getJSONObject(i);
                            entity.setPictureIndex(adverObj.getIntValue("pictureIndex"));
                            entity.setPictureLinkURL(adverObj
                                    .getString("pictureLinkUrl"));
                            entity.setPictureURL(adverObj.getString("pictureUrl"));
                            entity.setVersion(adverObj.getString("version"));
                            entity.setStartDate(adverObj.getString("startDate"));
                            entity.setEndDate(adverObj.getString("endDate"));
                        }
                    }
                }
            }
            if (Integer.valueOf(entity.getVersion()) > Integer.valueOf(preference.getAdvertisement_version())){
                String fileName = "welAdvertisement.png";
                String floder = FileUtil.getAdvertisementPath();
                if (FileUtil.checkFileExistedAndAvailable(floder + "/"
                        + fileName)) {
                    FileUtil.delFile(floder + "/" + fileName);
                }
                String url = entity.getPictureURL();
                byte[] bytes = HttpUtil.getPicture(url);
                if (bytes != null) {
                    Bitmap bitMap = FileUtil.Bytes2Bitmap(bytes);
                    FileUtil.saveBitmapToPng(bitMap, floder, fileName);
					preference.saveAdvertisement_version(entity.getVersion());
					preference.saveAdvertisement_s_time(entity.getStartDate());
					preference.saveAdvertisement_e_time(entity.getEndDate());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
	}

	private AdvArrivedListener advArrivedListener;
	public void setAdvArrivedListener(AdvArrivedListener advArrivedListener){
		this.advArrivedListener=advArrivedListener;
	}

	public interface AdvArrivedListener{
		public void showAdvPicture();
	}

	public static void getWeatherMessage(final String cityID, final GetWeatherListener getWeatherListener) {
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				try {
					JSONObject jsonObject = new JSONObject();

                    jsonObject.put("cityId", cityID);
//					jsonObject.put("deviceid", gwID);
					String json = HttpUtil.postWulianCloudLocation("https://v2.wuliancloud.com:52182/AMS/device", "getWeatherInfo", jsonObject);

					if (!StringUtil.isNullOrEmpty(json)) {

						JSONObject object = JSONObject.parseObject(json);
						JSONObject dataObject = object.getJSONObject("data");
						if(dataObject!=null){
							double temp = Double.parseDouble(dataObject.getString("temp"));
							temp = temp - 273.15;
							DecimalFormat df = new DecimalFormat(".#");
							temp = Double.valueOf(df.format(temp));
							String tempString = temp + "℃";

							String pm25String = dataObject.getString("pm25");

							Time t = new Time();
							t.setToNow(); // 取得系统时间。
							int year = t.year;
							int month = t.month + 1;
							int day = t.monthDay;
							String dateString = year + "/" + month + "/" + day;

							String status = dataObject.getString("icon");
							String[] weatherStatus = WeatherEntity.getWeatherStatus(status);
							String statusString = weatherStatus[0];
							int statusImg = Integer.parseInt(weatherStatus[1]);
							String cityString;
							if (LanguageUtil.isChina()) {
								cityString=dataObject.getString("cityName");
							}else{
								cityString=dataObject.getString("eCityName");
							}

							final WeatherEntity weatherEntity = new WeatherEntity(dateString, cityString, statusString, pm25String, tempString, statusImg);
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								@Override
								public void run() {
									getWeatherListener.doSomeThing(weatherEntity);
								}
							});
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public interface GetWeatherListener {
		void doSomeThing(WeatherEntity entity);
	}

}
