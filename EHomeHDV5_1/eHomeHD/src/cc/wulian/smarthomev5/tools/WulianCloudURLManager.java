package cc.wulian.smarthomev5.tools;

import java.net.InetAddress;

import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

import com.alibaba.fastjson.JSONObject;

public class WulianCloudURLManager {

	private static WulianCloudURLManager instance = new WulianCloudURLManager();
	private static String baseURL = "";
	private Preference preference = Preference.getPreferences();
	private AccountManager accountManager = AccountManager.getAccountManger();
	private WulianCloudURLManager(){
	}
	public String getIpsFromDomain (String domain){
		String ip = "";
		try{
			ip = StringUtil.getIpFromString(domain);
			if(StringUtil.isNullOrEmpty(ip)){
				InetAddress[] ips =  InetAddress.getAllByName(domain);
				if(ips != null && ips.length >0)
					ip =ips[0].getHostAddress();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ip;
	}
	public void checkWulianCloudURL(){
		try{
			
			String cloudURL = URLConstants.DEFAULT_ACS_BASEURL+"/acs/gateway/getConnectAddr";
			baseURL = preference.getString(IPreferenceKey.p_KEY_WULIAN_CLOUD_BASE_URL,"");
			JSONObject obj = new JSONObject();
			obj.put("gwID", accountManager.getmCurrentInfo().getGwID());
			String ip=accountManager.getmCurrentInfo().getGwSerIP();
			if(StringUtil.isNullOrEmpty(ip)){
				ip=accountManager.getmCurrentInfo().getGwIP();
			}
			String domainIp = getIpsFromDomain(ip);
			obj.put("gwConnectServerIP",domainIp);
			obj.put("isHttp","false");
			String result = HttpUtil.postWulianCloud(cloudURL,obj);
			if(!StringUtil.isNullOrEmpty(result)){
				JSONObject resultObject = JSONObject.parseObject(result);
				if(resultObject.containsKey("retData")){
					resultObject = resultObject.getJSONObject("retData");
					if(resultObject != null){
						String address = resultObject.getString("interfaceAddress");
						if(!StringUtil.isNullOrEmpty(address)){
							baseURL = address;
							preference.putString(IPreferenceKey.p_KEY_WULIAN_CLOUD_BASE_URL, address);
						}
						Logger.debug("domainIp:"+domainIp+";"+"address:"+address+";"+"baseURL : "+baseURL);
					}
				}
			}
		}catch(Exception e){
			Logger.error("baseURL exeception :"+e.getMessage());
		}
	}
	public static WulianCloudURLManager getInstance(){
		return instance;
	}
	public static String getBasicDataURL(){
		return URLConstants.DEFAULT_ACS_BASEURL+"/acs/notice/query";
	}

	public static String getGatewayCloneURL(){
		return URLConstants.DEFAULT_ACS_BASEURL+"/acs/gateway/getGwCloneToken";
	}

	public static String getDeviceInfoURL(){
		return "https://"+getBaseUrl()+"/acs/gateway/queryDeviceData";
	}
	public static String getTimeZeroURL(){
		return "https://acs.wuliancloud.com:33443/acs/timeZone/search";
//		return "http://"+getBaseUrl()+"/acs/timeZone/search";
	}
	public static String getSocialInfoURL(){
		return "https://"+getBaseUrl()+"/acs/chat/queryGroupChat";
	}
	public static String getAdvertisementURL(){
		String adevertisementUrl = URLConstants.DEFAULT_ACS_BASEURL+"/acs/notice/queryAdvInfo";
		return adevertisementUrl;
	}
	public static String getAnnouncementURL(){
		String adevertisementUrl = URLConstants.DEFAULT_ACS_BASEURL+"/acs/notice/queryNotice";
		return adevertisementUrl;
	}
	
	public static String getPermissionInfoURL(){
		return URLConstants.DEFAULT_ACS_BASEURL+"/acs/gateway/queryGwOauthUser";
	}
	public static String getResponsePermissionInfoURL(){
		return URLConstants.DEFAULT_ACS_BASEURL+"/acs/gateway/oauthGwUser";
	}
	public static String getCommentsSaveURL(){
		String commentsUrl = URLConstants.DEFAULT_ACS_BASEURL+"/acs/comments/save";
		return commentsUrl;
	}
	public static String getCommentsQueryURL(){
		String commentsUrl = URLConstants.DEFAULT_ACS_BASEURL+"/acs/comments/query";
		return commentsUrl;
	}
	public static String getLoginOrExitURL(){
		return URLConstants.DEFAULT_ACS_BASEURL+"/acs/notice/saveAppLog";
	}
	public static String getBaseUrl() {
		String url = baseURL;
		if(StringUtil.isNullOrEmpty(url)){
			url = URLConstants.DEFAULT_QUERY_DATA_URL;
			instance.checkWulianCloudURL();
		}
		return url;
	}
	public static String getHumanTrafficInfoURL(){
		return "https://"+getBaseUrl()+"/acs/device/countPeopleFlowRate";
	}
}

