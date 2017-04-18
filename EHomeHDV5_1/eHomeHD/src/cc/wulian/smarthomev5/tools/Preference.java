package cc.wulian.smarthomev5.tools;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Locale;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.UserLocation;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.VersionUtil;

/**
 * 系统参数
 */
public class Preference {
    public static final String ENTER_TYPE_ACCOUNT = "account";
    public static final String ENTER_TYPE_GW = "gateway";
    public static final String ISADMIN_TRUE = "1";
    public static final String ISADMIN_FALSE = "0";

    private final Context mContext;
    private final SharedPreferences mPreferences;
    private final Editor mEditor;

    private SharedPreferences mGwIDPreferences;
    private Editor mGwIDEditor;

    String currentVersion;

    private static Preference mInstance;

    public static Preference getPreferences() {
        if (mInstance == null)
            mInstance = new Preference();
        return mInstance;
    }

    private Preference() {
        mContext = MainApplication.getApplication();
        mPreferences = mContext.getSharedPreferences(
                IPreferenceKey.P_KEY_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        currentVersion=VersionUtil.getVersionName(mContext);
    }

    // //////////////////////////////////////////////////////////////////////
    // Custom SharedPreference //
    // /////////////////////////////////////////////////////////////////////
    public boolean isWelcomeReadGuide(String version) {
        if(currentVersion.equals(version)){
            return mPreferences.getBoolean(IPreferenceKey.P_KEY_GUIDE + currentVersion, true);
        }else{
            return false;
        }
    }

    public void readWelcomeGuide() {
        mEditor.putBoolean(IPreferenceKey.P_KEY_GUIDE + currentVersion, false).commit();
    }

    public boolean isCurrentVersionFirst() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_CURRENT_VERSION_FIRST + currentVersion, true);
    }

    public void currentVersionFirst() {
        mEditor.putBoolean(IPreferenceKey.P_KEY_CURRENT_VERSION_FIRST + currentVersion, false).commit();
    }

    public void saveRememberChecked(boolean isRememberChecked, String gwID) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_REMEMBER + "_" + gwID,
                isRememberChecked).commit();
    }

    public boolean isRememberChecked(String gwID) {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_REMEMBER + "_"
                + gwID, true);
    }

    public void saveAutoLoginChecked(boolean isAutoLoginChecked, String gwID) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_AUTOLOGIN + "_" + gwID,
                isAutoLoginChecked).commit();
    }

    public boolean isAutoLoginChecked(String gwID) {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_AUTOLOGIN + "_"
                + gwID, false);
    }

    public void saveLastSigninID(String username) {
        mEditor.putString(IPreferenceKey.P_KEY_USERNAME, username).commit();
    }

    public String getLastSigninID() {
        return mPreferences.getString(IPreferenceKey.P_KEY_USERNAME, "");
    }

    public void saveGateWayVersion(String gwId, String version) {
        mEditor.putString(IPreferenceKey.P_KEY_GATEWYVERSION + "_" + gwId, version)
                .commit();
    }

    public String getGateWayVersion(String gwId) {
        return mPreferences.getString(IPreferenceKey.P_KEY_GATEWYVERSION + "_"
                + gwId, "");
    }

    public void saveGateWayName(String gwId, String name) {
        mEditor.putString(IPreferenceKey.P_KEY_GATEWYNAME + "_" + gwId, name)
                .commit();
    }

    public String getGateWayName(String gwId) {
        return mPreferences.getString(IPreferenceKey.P_KEY_GATEWYNAME + "_"
                + gwId, "");
    }

    public void saveLastSaveImageVersion(int sendData) {
        mEditor.putInt(IPreferenceKey.P_KEY_LAST_SAVE_IMAGE_VERSION, sendData)
                .commit();
    }

    public int getLastSaveImageVersion() {
        return mPreferences.getInt(
                IPreferenceKey.P_KEY_LAST_SAVE_IMAGE_VERSION, 0);
    }

    public void saveThemeID(String themeID) {
        mEditor.putString(IPreferenceKey.P_KEY_THEME_ID, themeID).commit();
    }

    public String getThemeID() {
        return mPreferences.getString(IPreferenceKey.P_KEY_THEME_ID, null);
    }

    public void saveIsNormalQuit(boolean normalQuit) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_NORMAL_QUIT, normalQuit)
                .commit();
    }

    public boolean isNormalQuit() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_NORMAL_QUIT, true);
    }

    public void saveIsFirstIn(boolean isFirstIn) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_FIRST_IN, isFirstIn).commit();
    }

    public boolean isFirstIn() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_FIRST_IN, true);
    }

    public void saveAppVersion(int versionCode) {
        mEditor.putInt(IPreferenceKey.P_KEY_VERSION, versionCode).commit();
    }

    public int getAppVersion() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_VERSION, 0);
    }

    public void saveAppVersionRemindTimes(int versionCode, int time) {
        mEditor.putInt(IPreferenceKey.P_KEY_UPDATE_REMIND + "_" + versionCode, time).commit();
    }

    public int getAppVersionRemindTimes(int versionCode) {
        return mPreferences.getInt(IPreferenceKey.P_KEY_UPDATE_REMIND + "_" + versionCode, -1);
    }

    public void saveAdvertisementLocation(UserLocation location) {
        mEditor.putString(IPreferenceKey.P_KEY_ADV_LOCATION_COUNTRY,
                location.getCountryCode())
                .putString(IPreferenceKey.P_KEY_ADV_LOCATION_PROVINCE,
                        location.getProvince())
                .putString(IPreferenceKey.P_KEY_ADV_LOCATION_CITY,
                        location.getCity())
                .putString(IPreferenceKey.P_KEY_ADV_LOCATION_CITY_CODE,
                        location.getCityCode())
                .putString(IPreferenceKey.P_KEY_LOCATION_LAT, String.valueOf(location.getLatitude()))
                .putString(IPreferenceKey.P_KEY_LOCATION_LNG, String.valueOf(location.getLongitude()))
                .commit();
    }

    public void saveAdvertisementContent(String content) {
        mEditor.putString(IPreferenceKey.P_KEY_ADV_CONTENT, content);
    }

    public String getAdvertisementContent() {
        return mPreferences.getString(IPreferenceKey.P_KEY_ADV_CONTENT, "");
    }

    public UserLocation getAdvertisementLocation() {
        UserLocation location = new UserLocation();
        location.setCountryCode(mPreferences.getString(
                IPreferenceKey.P_KEY_ADV_LOCATION_COUNTRY, Locale.getDefault()
                        .getCountry()));
        location.setProvince(mPreferences.getString(
                IPreferenceKey.P_KEY_ADV_LOCATION_PROVINCE, null));
        location.setCity(mPreferences.getString(
                IPreferenceKey.P_KEY_ADV_LOCATION_CITY, null));
        location.setCityCode(mPreferences.getString(
                IPreferenceKey.P_KEY_ADV_LOCATION_CITY_CODE, null));
        Double lat = 0.0;
        if (!StringUtil.isNullOrEmpty(mPreferences.getString(IPreferenceKey.P_KEY_LOCATION_LAT, null))) {
            lat = Double.valueOf(mPreferences.getString(IPreferenceKey.P_KEY_LOCATION_LAT, null));
        }
        Double lng = 0.0;
        if (!StringUtil.isNullOrEmpty(mPreferences.getString(IPreferenceKey.P_KEY_LOCATION_LNG, null))) {
            lng = Double.valueOf(mPreferences.getString(IPreferenceKey.P_KEY_LOCATION_LNG, null));
        }
        location.setLatitude(lat);
        location.setLongitude(lng);
        return location;
    }

    public void saveSTBLocation(UserLocation location) {
        mEditor.putString(IPreferenceKey.P_KEY_LOCATION_ID,
                location.getProvinceCode())
                .putString(IPreferenceKey.P_KEY_LOCATION_NAME,
                        location.getProvince())
                .putLong(IPreferenceKey.P_KEY_LOCATION_TIME, location.getTime())
                .commit();
    }

    public boolean isHasSTBLocation() {
        boolean result = false;
        result = mPreferences.contains(IPreferenceKey.P_KEY_LOCATION_ID);
        if (result) {
            long lastTime = mPreferences.getLong(
                    IPreferenceKey.P_KEY_LOCATION_TIME, 0);
            result = (System.currentTimeMillis() - lastTime) < AlarmManager.INTERVAL_HALF_DAY;
        }
        return result;
    }

    public UserLocation getSTBLocation() {
        UserLocation location = new UserLocation();
        location.setProvinceCode(mPreferences.getString(
                IPreferenceKey.P_KEY_LOCATION_ID, null));
        location.setProvince(mPreferences.getString(
                IPreferenceKey.P_KEY_LOCATION_NAME, null));
        location.setTime(mPreferences.getLong(
                IPreferenceKey.P_KEY_LOCATION_TIME, 0));
        return location;
    }

    public void clearAllData() {
        mEditor.clear().commit();
    }

    // //////////////////////////////////////////////////////////////////////
    // gwID SharedPreference //
    // because this is single user's setting, need add current gwID for
    // distinguish
    // /////////////////////////////////////////////////////////////////////

    public String getString(String key, String defaule) {
        setCustomSharedPreference();
        return mGwIDPreferences.getString(key, defaule);
    }

    public void putString(String key, String value) {
        setCustomSharedPreference();
        mGwIDEditor.putString(key, value).commit();
    }

    public int getInt(String key, int defaule) {
        setCustomSharedPreference();
        return mGwIDPreferences.getInt(key, defaule);
    }

    public void putInt(String key, int value) {
        setCustomSharedPreference();
        mGwIDEditor.putInt(key, value).commit();
    }

    public void putLong(String key, long value) {
        setCustomSharedPreference();
        mGwIDEditor.putLong(key, value).commit();
    }

    public long getLong(String key, long defaule) {
        setCustomSharedPreference();
        return mGwIDPreferences.getLong(key, defaule);
    }

    public void putFloat(String key, float value) {
        setCustomSharedPreference();
        mGwIDEditor.putFloat(key, value).commit();
    }

    public float getFloat(String key, float defaule) {
        setCustomSharedPreference();
        return mGwIDPreferences.getFloat(key, defaule);
    }

    public boolean getBoolean(String key, boolean defaule) {
        setCustomSharedPreference();
        return mGwIDPreferences.getBoolean(key, defaule);
    }

    public void putBoolean(String key, boolean value) {
        setCustomSharedPreference();
        mGwIDEditor.putBoolean(key, value).commit();
    }

    public void clearAllCustomGwData() {
        setCustomSharedPreference();
        mGwIDEditor.clear().commit();
    }

    public boolean clearCustomKeyData(String key) {
        MainApplication app = (MainApplication) mContext
                .getApplicationContext();
        SharedPreferences pref = app.getSharedPreferences(key,
                Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        return editor.clear().commit();
    }

    private void setCustomSharedPreference() {
        AccountManager accountManger = AccountManager.getAccountManger();
        MainApplication app = (MainApplication) mContext
                .getApplicationContext();
        mGwIDPreferences = app.getSharedPreferences(
                accountManger.getmCurrentInfo() == null ? CmdUtil.GW_DEFAULT_DEMO_ID
                        : accountManger.getmCurrentInfo().getGwID(),
                Context.MODE_PRIVATE);
        mGwIDEditor = mGwIDPreferences.edit();
    }

    public void saveTwoOutputSettingData(String epData) {
        mEditor.putString(IPreferenceKey.P_KEY_TWO_OUTPUT_DEFAULT_DATA, epData)
                .commit();
    }

    public void saveDoorLockBindedInfo(String gwId, String decvId, String status) {
        mEditor.putString("gwId", gwId);
        mEditor.putString("decvId", decvId);
        mEditor.putString("status", status).commit();
    }

    public String getTwoOutputSettingData() {
        return mPreferences.getString(
                IPreferenceKey.P_KEY_TWO_OUTPUT_DEFAULT_DATA, "");
    }

    public void saveAreaGroupHomeSetting(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_AREA_GROUP_HOME_SETTING, str)
                .commit();
    }

    public String getAreaGroupHomeSetting() {
        return mPreferences.getString(
                IPreferenceKey.P_KEY_AREA_GROUP_HOME_SETTING, "");
    }

    public void saveDefaultSceneSetting(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEFAULT_SCENE_SETTING, str)
                .commit();
    }

    public String getDefaultSceneSetting() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEFAULT_SCENE_SETTING, "");
    }

    public void saveDefaultSceneSelect(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEFAULT_SCENE_SELECT, str)
                .commit();
    }

    public String getDefaultSceneSelect() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEFAULT_SCENE_SELECT, "");
    }

    public void saveUserEnterType(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_CHECK_ACCOUNT_ENTER_TYPE, str)
                .commit();
    }

    public String getUserEnterType() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CHECK_ACCOUNT_ENTER_TYPE, "account");
    }

    public boolean isUseAccount() {
        return mContext.getResources().getBoolean(R.bool.use_account)
                && Preference.ENTER_TYPE_ACCOUNT.equals(Preference.getPreferences().getUserEnterType());
    }

    public void saveGatewayIsAdmin(boolean isAdmin) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_CHECK_IS_ADMIN, isAdmin)
                .commit();
    }

    public boolean getGatewayIsAdmin() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_CHECK_IS_ADMIN, false);
    }

    public void saveVocHtmlUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_VOC_HTML_URI, str)
                .commit();
    }

    public String getVocHtmlUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_VOC_HTML_URI, "noUri");
    }

    public void saveTemhumHtmlUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_TEMHUM_HTML_URI, str)
                .commit();
    }

    public String getTemhumHtmlUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_TEMHUM_HTML_URI, "noUri");
    }

    public void saveNoiseHtmlUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_NOISE_HTML_URI, str)
                .commit();
    }

    public String getNoiseHtmlUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_NOISE_HTML_URI, "noUri");
    }

    public void savePMHtmlUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_PM_HTML_URI, str)
                .commit();
    }

    public String getPMHtmlUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_PM_HTML_URI, "noUri");
    }

    public void saveAirHtmlUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_AIR_HTML_URI, str)
                .commit();
    }

    public String getAirHtmlUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_AIR_HTML_URI, "noUri");
    }

    public void saveBackgroundMusicHtmlUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_BGMUSIC_HTML_URI, str)
                .commit();
    }

    public String getBackgroundMusicHtmlUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_BGMUSIC_HTML_URI, "noUri");
    }

    public void saveVoiceChooseNum(int number) {
        mEditor.putInt(IPreferenceKey.P_KEY_MINI_VOICE_CHOOSE_NUM, number)
                .commit();
    }

    public int getVoiceChooseNum() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_MINI_VOICE_CHOOSE_NUM, 0);
    }

    public void saveVoiceChooseSize(int number) {
        mEditor.putInt(IPreferenceKey.P_KEY_MINI_VOICE_CHOOSE_SIZE, number)
                .commit();
    }

    public int getVoiceChooseSize() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_MINI_VOICE_CHOOSE_SIZE, 0);
    }

    public void saveVoiceChooseName(String name) {
        mEditor.putString(IPreferenceKey.P_KEY_MINI_VOICE_CHOOSE_NAME, name)
                .commit();
    }

    public String getVoiceChooseName() {
        return mPreferences.getString(IPreferenceKey.P_KEY_MINI_VOICE_CHOOSE_NAME, "");
    }

    public void saveNowtimeWifiName(String name) {
        mEditor.putString(IPreferenceKey.P_KEY_NOWTIME_WIFI_NAME, name)
                .commit();
    }

    public String getNowtimeWifiName() {
        return mPreferences.getString(IPreferenceKey.P_KEY_NOWTIME_WIFI_NAME, "");
    }

    public void saveManagerGatewayUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_MANAGER_GATEWAY_HTML_URI, str)
                .commit();
    }

    public String getManagerGatewayUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_MANAGER_GATEWAY_HTML_URI, "noUri");
    }

    public String get30ASwichUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_30ASWICTH, "noUri");
    }

    public void save30ASwichUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_30ASWICTH, str)
                .commit();
    }
    public String get10ASwichUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_10ASWICTH, "noUri");
    }

    public void save10ASwichUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_10ASWICTH, str)
                .commit();
    }

    public String getAjSwichUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_AJSWICTH, "noUri");
    }

    public void saveAjSwichUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_AJSWICTH, str)
                .commit();
    }

    public String getAjSwichSettingUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_AJSWICTHSETTING, "noUri");
    }

    public void saveAjSwichSettingUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_AJSWICTHSETTING, str)
                .commit();
    }

    public String getAqSwichUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_AQSWICTH, "noUri");
    }

    public void saveAqSwichUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_AQSWICTH, str)
                .commit();
    }

    public String getAqSwichSettingUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_AQSWICTHSETTING, "noUri");
    }

    public void saveAqSwichSettingUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_AQSWICTHSETTING, str)
                .commit();
    }

    public String getAtSwichUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_ATSWICTH, "noUri");
    }

    public void saveAtSwichUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_ATSWICTH, str)
                .commit();
    }

    public String getAtSwichSettingUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_ATSWICTHSETTING, "noUri");
    }

    public void saveAtSwichSettingUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_ATSWICTHSETTING, str)
                .commit();
    }

    public String getInstalServiceToolUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_INSTAL_SERVICE_TOOL, "noUri");
    }

    public void saveInstalServiceToolUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_INSTAL_SERVICE_TOOL, str)
                .commit();
    }

    public void saveLittlewhiteGwpwd(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_LITTLE_WHITE_GWPWD, str)
                .commit();
    }

    public String getLittlewhiteGwpwd() {
        return mPreferences.getString(IPreferenceKey.P_KEY_LITTLE_WHITE_GWPWD, "");
    }

    public void saveChannelUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_CHANNEL, str)
                .commit();
    }

    public String getChannelUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CHANNEL, "noUri");
    }

    public String getUeiUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_UEI23, "noUri");
    }

    public void saveUeiUrl(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_UEI23, str)
                .commit();
    }

    public void saveUeiTopBox_Program(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_UEI23_PROGRAM, str)
                .commit();
    }

    public String getUeiTopBox_Program() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_UEI23_PROGRAM, "noUri");
    }

    public void saveUeiTopBox_Channel(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_UEI23_CHANNEL, str)
                .commit();
    }

    public String getUeiTopBox_Channel() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_UEI23_CHANNEL, "noUri");
    }

    public void saveUeiTopBox_Collection(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_UEI23_COLLECTION, str)
                .commit();
    }

    public String getUeiTopBox_Collection() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_UEI23_COLLECTION, "noUri");
    }

    public String get30ASwichSettingUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_30ASWICTHSETTING, "noUri");
    }

    public void save30ASwichSettingUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_30ASWICTHSETTING, str)
                .commit();
    }
    public String get10ASwichSettingUri() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_10ASWICTHSETTING, "noUri");
    }

    public void save10ASwichSettingUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_10ASWICTHSETTING, str)
                .commit();
    }

    public String getAdvertisement_version() {
        return mPreferences.getString(IPreferenceKey.P_KEY_ADVERTISEMENT_VERSION, "0");
    }

    public void saveAdvertisement_version(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_ADVERTISEMENT_VERSION, str)
                .commit();
    }

    public String getAdvertisement_s_time() {
        return mPreferences.getString(IPreferenceKey.P_KEY_ADVERTISEMENT_S_TIME, "0");
    }

    public void saveAdvertisement_s_time(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_ADVERTISEMENT_S_TIME, str)
                .commit();
    }

    public String getAdvertisement_e_time() {
        return mPreferences.getString(IPreferenceKey.P_KEY_ADVERTISEMENT_E_TIME, "0");
    }

    public void saveAdvertisement_e_time(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_ADVERTISEMENT_E_TIME, str)
                .commit();
    }

    public void saveMonitorList(String str, String gwId) {
        mEditor.putString(IPreferenceKey.P_KEY_MONITOR_LIST, str);
        mEditor.putString(IPreferenceKey.P_KEY_MONITOR_LIST_GWID, gwId).commit();
    }

    public String getMonitorList() {
        return mPreferences.getString(IPreferenceKey.P_KEY_MONITOR_LIST, "0");
    }
    public String getMonitorListgwID() {
        return mPreferences.getString(IPreferenceKey.P_KEY_MONITOR_LIST_GWID, "0");
    }

    public void saveUeiTopBox_Operators(String str) {
        mEditor.putString(IPreferenceKey.P_key_device_UEI23_OPERATORS, str)
                .commit();
    }

    public String getUeiTopBox_Operators() {
        return mPreferences.getString(IPreferenceKey.P_key_device_UEI23_OPERATORS, "noUri");
    }

	public String getAuCurtainUri(){
		return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_AUCURTAIN, "noUri");
	}
	public void saveAuCurtainUri(String str){
		mEditor.putString(IPreferenceKey.P_KEY_DEVICE_AUCURTAIN, str)
				.commit();
	}
	public String getAuCurtainSettingUri(){
		return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_AUCURTAINSETTING, "noUri");
	}
	public void saveAuCurtainSettingUri(String str){
		mEditor.putString(IPreferenceKey.P_KEY_DEVICE_AUCURTAINSETTING, str)
				.commit();
	}

    public void setContinuouInputErrorsLockTime(long currentTime){
        mEditor.putLong(IPreferenceKey.P_KEY_CONTINUOU_INPUT_ERRORS_LOCKTIME,currentTime).commit();
    }

    public long getContinuouInputErrorsLockTime(){
        return mPreferences.getLong(IPreferenceKey.P_KEY_CONTINUOU_INPUT_ERRORS_LOCKTIME,0);
    }

    public String getChatCurrentUserName(String gwID){
        return mPreferences.getString(IPreferenceKey.P_KEY_CHAT_CURRENT_USER_NAME+gwID, "");
    }

    public void saveChatCurrentUserName(String str,String gwID) {
        mEditor.putString(IPreferenceKey.P_KEY_CHAT_CURRENT_USER_NAME+gwID, str)
                .commit();
    }

    public String getChatPassword(){
        return mPreferences.getString(IPreferenceKey.P_KEY_CHAT_PASSWORD, "");
    }

    public void saveChatPassword(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_CHAT_PASSWORD, str)
                .commit();
    }

    public void saveUserNickName(String str,String ID) {
        mEditor.putString(IPreferenceKey.P_KEY_CHAT_USER_NICK_NAME+ID, str)
                .commit();
    }

    public String getUserNickName(String ID){
        return mPreferences.getString(IPreferenceKey.P_KEY_CHAT_USER_NICK_NAME+ID, "");
    }

    public void saveUserNickNameTime(String str,String ID) {
        mEditor.putString(IPreferenceKey.P_KEY_CHAT_USER_NICK_NAME_TIME+ID, str)
                .commit();
    }

    public String getUserNickNameTime(String ID) {
        return mPreferences.getString(IPreferenceKey.P_KEY_CHAT_USER_NICK_NAME_TIME + ID, "");
    }

    public void setClickOnTheNumberOfTimes(int clickOnTheNumberOfTimes){
        mEditor.putInt(IPreferenceKey.P_KEY_CLICK_ON_THE_NUMBER_OF_TIMES,clickOnTheNumberOfTimes).commit();
    }

    public int getClickOnTheNumberOfTimes(){
        return mPreferences.getInt(IPreferenceKey.P_KEY_CLICK_ON_THE_NUMBER_OF_TIMES,0);
    }


    public String getOWRtcResult(String devID){
        return mPreferences.getString(IPreferenceKey.P_KEY_OW_HAVE_RTC_RESULT+devID, "");
    }

    public void saveOWRtcResult(String result,String devID) {
        mEditor.putString(IPreferenceKey.P_KEY_OW_HAVE_RTC_RESULT+devID,result)
                .commit();
    }

    public boolean getInstalServiceToolActivity(){
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_INSTALL_SERVICE_TOOl, false);
    }

    public void setInstalServiceToolActivity() {
        mEditor.putBoolean(IPreferenceKey.P_KEY_INSTALL_SERVICE_TOOl,true)
                .commit();
    }

    public void saveOzCentralAirUri(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_OZCENTRALAIR, str)
                .commit();
    }
    public String getOzCentralAir() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_OZCENTRALAIR, "noUri");
    }

    public String getHxWifiAndDeviceId(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_WIFIANDDEVICEID, "");
    }

    public void saveHxWifiAndDeviceId(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_WIFIANDDEVICEID, str)
                .commit();
    }
    public String getHxAppCode(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_APPCODE, "");
    }

    public void saveHxAppCode(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_APPCODE, str)
                .commit();
    }
    public String getHxToken(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_APPTOKEN, "");
    }

    public void saveHxToken(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_APPTOKEN, str)
                .commit();
    }
    public String getHxOriginStatus(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_ORIGINSTATUS, "");
    }

    public void saveHxOriginStatus(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_ORIGINSTATUS, str)
                .commit();
    }
    public Long getHxCustomerID(){
        return mPreferences.getLong(IPreferenceKey.P_KEY_HX_CUSTOMERID, 0L);
    }

    public void saveHxCustomerID(long customerID){
        mEditor.putLong(IPreferenceKey.P_KEY_HX_CUSTOMERID, customerID)
                .commit();
    }

    public void saveHxOaSetting(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_OaRangehood_Setting, str)
                .commit();
    }
    public String getHxOaSetting(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_OaRangehood_Setting,"");
    }

    public void saveHxOaDeviceInfo(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_OaRangehood_DeviceInfo, str)
                .commit();
    }
    public String getHxOaDeviceInfo(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_OaRangehood_DeviceInfo,"");
    }
    public void saveHxOcDeviceInfo(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_OcWahsingMachine, str)
                .commit();
    }
    public String getHxOcDeviceInfo(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_OcWahsingMachine,"");
    }
    public void saveHxOeDeviceInfo(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_OeFridge, str)
                .commit();
    }
    public String getHxOeDeivceInfo(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_OeFridge,"");
    }
    public void saveHxObDeviceInfo(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_ObHouseholdAir, str)
                .commit();
    }
    public String getHxObDeivceInfo(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_ObHouseholdAir,"");
    }
    public void saveHxOdDeviceInfo(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_OdGasStoves, str)
                .commit();
    }
    public String getHxOdDeivceInfo(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_OdGasStoves,"");
    }
    public void saveHxOZDeviceInfo(String str){
        mEditor.putString(IPreferenceKey.P_KEY_HX_OZCentralAir, str)
                .commit();
    }
    public String getHxOZDeivceInfo(){
        return mPreferences.getString(IPreferenceKey.P_KEY_HX_OZCentralAir,"");
    }
}