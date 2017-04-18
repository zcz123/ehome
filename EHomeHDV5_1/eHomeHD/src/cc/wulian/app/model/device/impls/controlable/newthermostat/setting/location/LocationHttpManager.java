package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.utils.URLConstants;

/**
 * Created by hanx on 2016/12/21.
 */

public class LocationHttpManager {

    public static final String LOCATION_BASE_URI = URLConstants.AMS_URLBASE_VALUE + URLConstants.AMS_PATH;
    public static final String SET_LOCATION_URI = LOCATION_BASE_URI+"/user/device";
    public static final String GET_LOCATION_URI = LOCATION_BASE_URI+"/user/access";

    //根据国家编码获取对应省份
    public static String postCloudLocationProvinvce(String countryCode){
        String result = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("level", "country");
        jsonObject.put("countryCode", countryCode);
        result = postWulianCloudLocation(GET_LOCATION_URI,"getAreaInfo", jsonObject);
        return result;
    }

    //根据省份获取对应城市列表
    public static String postCloudLocationCitys(String countryCode,String eProvince){
        String result = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("level", "province");
        jsonObject.put("countryCode", countryCode);
        jsonObject.put("eProvince", eProvince);
        result = postWulianCloudLocation(GET_LOCATION_URI,"getAreaInfo", jsonObject);
        return result;
    }

    private static String postWulianCloudLocation(String url, String key, JSONObject jsonObject){

        String json = "";
        try{
            if(StringUtil.isNullOrEmpty(url)) {
                return "";
            }
            Map<String,String> headerMap = new HashMap<String,String>();
            headerMap.put("cmd", key);
            byte[] body = jsonObject == null ? null :jsonObject.toJSONString().getBytes();
            JSONObject result = HttpManager.getWulianCloudProvider().post(url, headerMap, body);
            if(result != null){
                json = result.getString("body");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }

   //  设置地理位置
    public static String setCloudLocation(String gwId ,String wCityId,String token){
        String result = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", gwId);
        jsonObject.put("wCityId", wCityId);
        result = setWulianCloudLocation(SET_LOCATION_URI,token,"deviceUpdate", jsonObject);
        return result;
    }

    private static String setWulianCloudLocation(String url,String token, String key, JSONObject jsonObject){
        String json = "";
        try{
            if(StringUtil.isNullOrEmpty(url)) {
                return "";
            }
            Map<String,String> headerMap = new HashMap<String,String>();
            headerMap.put("token", token);
            headerMap.put("cmd", key);
            byte[] body = jsonObject == null ? null :jsonObject.toJSONString().getBytes();
            JSONObject result = HttpManager.getWulianCloudProvider().post(url, headerMap, body);
            if(result != null){
                json = result.getString("header");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }

    //根据CityId查询CityName
    public static String getCloudLocation(String cityId){
        String cityName = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("level","city");
        jsonObject.put("cityId", cityId);
        String result = getWuLianCloudLocation(GET_LOCATION_URI,"getAreaInfo", jsonObject);
        if(!StringUtil.isNullOrEmpty(result)){
            com.alibaba.fastjson.JSONArray jsonArray = JSON.parseObject(result).getJSONArray("records");
            String cityInfo = jsonArray.getString(0);
            cityName = JSON.parseObject(cityInfo).getString("eCityName");
        }
        return cityName;
    }

    private static String getWuLianCloudLocation(String url,String key, JSONObject jsonObject){
        String json = "";
        try{
            if(StringUtil.isNullOrEmpty(url)) {
                return "";
            }
            Map<String,String> headerMap = new HashMap<String,String>();
            headerMap.put("cmd",key);
            byte[] body = jsonObject == null ? null :jsonObject.toJSONString().getBytes();
            JSONObject result = HttpManager.getWulianCloudProvider().post(url, headerMap, body);
            if(result != null){
                json = result.getString("body");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;

    }

    //请求  获取CityId
    public static String getCloudCityId(String gwId,String token){
        String cityId = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId",gwId);
        String result = postWuLianCloudCityId(GET_LOCATION_URI,"getDeviceInfo", jsonObject,token);
        if(!StringUtil.isNullOrEmpty(result)){
            cityId = JSON.parseObject(result).getString("wCityId");
        }
        return cityId;
    }

    private static String postWuLianCloudCityId(String url,String key, JSONObject jsonObject,String token){
        String json = "";
        try{
            if(StringUtil.isNullOrEmpty(url)) {
                return "";
            }
            Map<String,String> headerMap = new HashMap<String,String>();
            headerMap.put("token",token);
            headerMap.put("cmd",key);
            byte[] body = jsonObject == null ? null :jsonObject.toJSONString().getBytes();
            JSONObject result = HttpManager.getWulianCloudProvider().post(url, headerMap, body);
            if(result != null){
                json = result.getString("body");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }

}
