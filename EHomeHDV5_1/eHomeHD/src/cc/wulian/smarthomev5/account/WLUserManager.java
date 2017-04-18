package cc.wulian.smarthomev5.account;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import cc.wulian.ihome.wan.sdk.user.AMServiceStub;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.callback.WLUserCallback;
import cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.configure.UserFileConfig;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.URLConstants;
import cc.wulian.smarthomev5.utils.VersionUtil;

import static cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5.ANDROID_LOGIN_APP_TOKEN;
import static cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5.ANDROID_LOGIN_OS_TYPE_VALUE;
import static cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5.ANDROID_LOGIN_PUSH_FCM;
import static cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5.ANDROID_LOGIN_PUSH_MQTT;

/**
 * Created by yanzy on 2016-7-8
 * Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
 **/
public class WLUserManager {
	private static final String TAG = "WLUserManager";
	
	private AMServiceStub stub = null;
	
	private volatile static WLUserManager _instance;
	private static Object singletonLock = new Object();

	public static WLUserManager getInstance() {
		if (_instance == null) {
			synchronized (singletonLock) {
				if (_instance == null) {
					_instance = new WLUserManager();
				}
			}
		}
		return _instance;
	}

	private WLUserManager() {
		// preventing WLUserManager object instantiation from outside
	}
	
	public AMServiceStub getStub() {
		if(stub == null) {
			throw new RuntimeException("WLUserManager has not initialized, please call init before getStub.");
		}
		return stub;
	}

	public void init(String account, String md5password, String token) {
		Log.i(TAG, "initing ,account is" + account);
		stub = new AMServiceStub();
		stub.BASEURL = URLConstants.AMS_URLBASE_VALUE + URLConstants.AMS_PATH;
		stub.init(WLUserCallback.getInstance());
		stub.setUserMd5PasswdAndToken(account, md5password, token);
		String appLang= LanguageUtil.getWulianCloudLanguage();
		String appToken=getAppToken();
		String appType= MainApplication.getApplication().getResources().getString(R.string.app_type);
		String osType=ANDROID_LOGIN_OS_TYPE_VALUE;
		String pushType=getPushType();
		stub.setLoginConfig(appLang,appToken,appType,osType,pushType);
		TelephonyManager manager= (TelephonyManager) MainApplication.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
		stub.getUser().setIMEI(getImei());
		stub.getUser().setOSVer("V"+VersionUtil.getVersionName(MainApplication.getApplication()));
		UserFileConfig.getInstance().setUserID(account);
	}
	public String getImei(){
		TelephonyManager manager= (TelephonyManager) MainApplication.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}

	private String getAppToken(){
		String appToken= SmarthomeFeatureImpl.getData(ANDROID_LOGIN_APP_TOKEN);
		if(StringUtil.isNullOrEmpty(appToken)){
			appToken = FirebaseInstanceId.getInstance().getToken();
			SmarthomeFeatureImpl.setData(ANDROID_LOGIN_APP_TOKEN,appToken);
		}
		return appToken;
	}

	private String getPushType(){
		if(LanguageUtil.isChina()){
			return ANDROID_LOGIN_PUSH_MQTT;
		}
		boolean googleserviceFlag;
		GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(MainApplication.getApplication());
		if(resultCode == ConnectionResult.SUCCESS) {
			googleserviceFlag=true;
		}else{
			googleserviceFlag=false;
		}
		if(googleserviceFlag&&(!StringUtil.isNullOrEmpty(getAppToken()))){
			return ANDROID_LOGIN_PUSH_FCM;
		}else{
			return ANDROID_LOGIN_PUSH_MQTT;
		}
	}
}
