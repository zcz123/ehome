package cc.wulian.smarthomev5.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import cc.wulian.app.model.device.impls.configureable.ir.xml.IRSupportSTB;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.STBParse.STB;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.collect.Lists;
import cc.wulian.smarthomev5.entity.GpsEntity;
import cc.wulian.smarthomev5.entity.UserLocation;
import cc.wulian.smarthomev5.event.GPSEvent;
import cc.wulian.smarthomev5.event.LocationEvent;
import cc.wulian.smarthomev5.event.SigninEvent;
import cc.wulian.smarthomev5.service.location.LocationClient;
import cc.wulian.smarthomev5.service.location.LocationClientOption;
import cc.wulian.smarthomev5.service.location.LocationHint;
import cc.wulian.smarthomev5.service.location.LocationRunner;
import cc.wulian.smarthomev5.service.location.LocationRunner.LocationHintAction;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.LogUtil;
import cc.wulian.smarthomev5.utils.StringCompareUtil;
import cc.wulian.smarthomev5.utils.StringCompareUtil.ResultInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import de.greenrobot.event.EventBus;

public class LocationService extends Service implements LocationHintAction
{
	private EventBus mEventBus;

	private LocationClient mLocationClient;
	private LocationHint mLocationHint;
	private LocationRunner mLocationRunner;
	private Preference preference = Preference.getPreferences();

	private List<GpsEntity> mLocationEntities;

	@Override
	public IBinder onBind( Intent intent ){
		return null;
	}

	@Override
	public void onCreate(){
		super.onCreate();

		mEventBus = EventBus.getDefault();
		mLocationClient = LocationClient.getInstance();
		mLocationHint = LocationHint.getIntance();
		mLocationRunner = LocationRunner.getInstance();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId ){
		mEventBus.unregister(this);
		mEventBus.register(this);
		this.restartLocation();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy(){
		if(mEventBus != null) {
			mEventBus.unregister(this);
		}
		stopLocation();
		super.onDestroy();
	}

	@Override
	public void runHintAction( Location originalLocation, boolean isFaraway ){
		LogUtil.logWarn(this.toString() + "---->>>runHintAction--->start");// LOG
		List<String> sceneIDList = Lists.newArrayList();
		for (GpsEntity locationEntity : mLocationEntities){
			if (originalLocation.getLatitude() != locationEntity.latitude
					|| originalLocation.getLongitude() != locationEntity.longitude){
				sceneIDList.add(isFaraway ? locationEntity.leaveSceneID : locationEntity.enterSceneID);
				// break; // want break for control only one scene?? Mark:
				LogUtil.logWarn(this.toString() + "---->>>runHintAction--->runHintSceneIDList--->" + sceneIDList);// LOG
			}
		}
	};

	private void stopLocation(){
		if(mLocationClient == null) {
			return;
		}
		LocationClientOption option = mLocationClient.getClientOption();
		option.setLocationNotify(false);
		mLocationClient.setLocOption(option);
		mLocationClient.stop();
	}

	private void restartLocation(){
		mLocationHint.removeAllHintOriginalLocation();
		mLocationRunner.removeAllLocationHintAction();

		LocationClientOption option = mLocationClient.getClientOption();
		option.setPriority(LocationClientOption.GpsFirst);
		option.setLocationNotify(true);
		option.setOpenGps(true);
		mLocationClient.setLocOption(option);
		mLocationClient.restart();
	}
	
	public void onEventMainThread( SigninEvent event ){
		if (SigninEvent.ACTION_SIGNIN_RESULT.equals(event.action)){
			if (event.isSigninSuccess){
				//boolean open = mApplication.mPreference.getBoolean(IPreferenceKey.P_KEY_OPEN_GPS, false);
				//UserLocation location = mApplication.mPreference.getLocation();
				//open |= location.needSyncNextTime;
				LocationClientOption option = LocationClient.getInstance().getClientOption();
				option.setOpenGps(true);
				restartLocation();
			}
			else{
				stopLocation();
			}
		}
		else if (SigninEvent.ACTION_SIGNIN_REQUEST.equals(event.action)){
			stopLocation();
		}
	}

	
	public void onEventMainThread( LocationEvent event ){
		if (event.mRestart){
			restartLocation();
		}
		else{
			stopLocation();
		}
	}

	private static final String GEOCODE_STATUS = "status";
	private static final String GEOCODE_RESULT_OK = "0";
	private static final String GEOCODE_RESULT = "result";

	private static final String GEOCODE_ADD_COMPONENT = "addressComponent";
	/*{"status":0,
	"result":{
	"location":{"lng":116.32298703399,"lat":39.983424051248},
	"formatted_address":"北京市海淀区中关村大街27号1101-08室",
	"business":"中关村,人民大学,苏州街",
	"addressComponent":{"city":"北京市","direction":"附近","distance":"7","district":"海淀区","province":"北京市","street":"中关村大街","street_number":"27号1101-08室"},
	"poiRegions":[],
	"cityCode":131}}*/
	private UserLocation getLocation(double lat,double lng){
		try {
			String url = "http://api.map.baidu.com/geocoder/v2/?ak=Rj8iHfmt48tzdDbjMXH3eK7O&location="+lat+","+lng+"&output=json&pois=0";
			String json = HttpUtil.post(url);
			if(!StringUtil.isNullOrEmpty(json)){
				Logger.debug("location is :"+json);
				JSONObject object = JSON.parseObject(json);
				String status = object.getString(GEOCODE_STATUS);
				if (GEOCODE_RESULT_OK.equals(status)){
					if (object.containsKey(GEOCODE_RESULT)){
						JSONObject addresssObject = object.getJSONObject(GEOCODE_RESULT);
						JSONObject addressComponents = addresssObject.getJSONObject(GEOCODE_ADD_COMPONENT);
						String country = addressComponents.getString("country");
						String province = addressComponents.getString("province");
						String city = addressComponents.getString("city");
						String cityCode = addresssObject.getString("cityCode");
						int country_code = addressComponents.getInteger("country_code");
						UserLocation address = new UserLocation();
						if(country_code == 0){
							address.setCountryCode("CN");
							address.setCountryName(country);
						}else{
							address.setCountryCode("US");
							address.setCountryName("US");
						}
						address.setProvince(province);
						address.setCity(city);
						address.setCityCode(cityCode);
						address.setLatitude(lat);
						address.setLongitude(lng);
						return address;
						}
					}
				}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void onEventAsync( GPSEvent event ){
		UserLocation address = preference.getAdvertisementLocation();
		if(address.isFarFromLastPosition(event) == false) {
			return;
		}
		address = getLocation(event.mLatitude, event.mLongitude);
		if(address != null) {
			updateSTBLocation(address);
			preference.saveAdvertisementLocation(address);
			stopLocation();
		}
	}

	private void updateSTBLocation(UserLocation address) {
		boolean hasSTBLocation = preference.isHasSTBLocation();
		Logger.debug("has location:"+hasSTBLocation);
		if(!hasSTBLocation){
			String province = address.getProvince();
			Logger.debug("location province"+province);
			if (!StringUtil.isNullOrEmpty(province)){
				IRSupportSTB supportSTB = IRSupportSTB.getInstance(getApplicationContext());
				List<STB> stbs = supportSTB.getSupportSTBsList();

				StringCompareUtil compareUtil = new StringCompareUtil();
				for (STB stb : stbs){
					compareUtil.startCompare(stb.getAreaName(), province, stb);
				}
				ResultInfo resultInfo = compareUtil.getBestSimilarResultInfo();
				if (resultInfo != null){
					STB bestSTB = (STB) resultInfo.obj;
					UserLocation stbLocation = new UserLocation();
					stbLocation.setProvinceCode(bestSTB.getAreaID());
					stbLocation.setProvince(bestSTB.getAreaName());
					stbLocation.setTime(System.currentTimeMillis());
					preference.saveSTBLocation(stbLocation);
					Logger.debug("location province code is "+stbLocation.getProvinceCode());
				}
			}
		}
	}
}
