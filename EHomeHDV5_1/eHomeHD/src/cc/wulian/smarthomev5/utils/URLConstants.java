package cc.wulian.smarthomev5.utils;

import android.os.Environment;

import cc.wulian.smarthomev5.activity.MainApplication;

/**
 * Created by yanzy on 2016-6-8 Copyright wulian group 2008-2016 All rights
 * reserved. http://www.wuliangroup.com
 **/
public class URLConstants {
	private static String LOCAL_BASEURL_D = "file:///android_asset/apps/smarthome/";

	private static String PLUGIN_SERVER_URL_D = "http://7xntes.com1.z0.glb.clouddn.com/";

	private static String MQTT_PUSH_SERVER_ADDR_D = "v2.wuliancloud.com";

	private static int MQTT_PUSH_SERVER_PORT_D = 52180;

	private static String AMS_URLBASE_VALUE_D = "https://v2.wuliancloud.com:52182";

	private static String AMS_DIGEST_URLBASE_VALUE_D = "https://v2.wuliancloud.com:52182/AMS";

	private static String FILE_ADDRESS_VALUE_D = "http://res.wulian.cc:53010/file";

	private static String PLUGIN_FOLD_D = null;
	public static String PLUGIN_FOLD_D_TEMP=null;//下载插件临时存储的目录
	private static String DEFAULT_ACS_BASEURL_D = "https://acs.wuliancloud.com:33443";
	
	static {
		if (TargetConfigure.forTest) {
//			LOCAL_BASEURL_D = "file://" + Environment.getExternalStorageDirectory().getPath() + "/wulian/smarthome/";
			PLUGIN_SERVER_URL_D = "http://7xrlkz.com1.z0.glb.clouddn.com/";
			PLUGIN_FOLD_D = Environment.getExternalStorageDirectory().getPath() + "/wulian/plugins/";
			PLUGIN_FOLD_D_TEMP = Environment.getExternalStorageDirectory().getPath() + "/wulian/plugins_temp/";
			DEFAULT_ACS_BASEURL_D = "https://testdemo.wulian.cc:6009";
			AMS_URLBASE_VALUE_D = "https://testv2.wulian.cc:52182";
			AMS_DIGEST_URLBASE_VALUE_D = "https://testv2.wulian.cc:52182/AMS";
			FILE_ADDRESS_VALUE_D = "https://testv2.wulian.cc:52182/file";
			MQTT_PUSH_SERVER_ADDR_D = "testv2.wulian.cc";
			MQTT_PUSH_SERVER_PORT_D = 52180;
		}
	}

	public static final String LOCAL_BASEURL = LOCAL_BASEURL_D;

	public static final String PLUGIN_SERVER_URL = PLUGIN_SERVER_URL_D;

	public static final String MQTT_PUSH_SERVER_ADDR = MQTT_PUSH_SERVER_ADDR_D;

	public static final int MQTT_PUSH_SERVER_PORT = MQTT_PUSH_SERVER_PORT_D;

	public static final String AMS_URLBASE_VALUE = AMS_URLBASE_VALUE_D;

	public static final String AMS_DIGEST_URLBASE_VALUE = AMS_DIGEST_URLBASE_VALUE_D;

	public static final String AMS_PATH = "/AMS";

	public static final String FILE_ADDRESS_VALUE = FILE_ADDRESS_VALUE_D;

	public static final String ASSERTS_PREFIX = "file:///android_asset/";
	public static final String SDCARD_PREFIX = "file://";

	public static final String getPluginRootFold() {
		if (PLUGIN_FOLD_D == null) {
			try {
				PLUGIN_FOLD_D = MainApplication.getApplication().getFilesDir().getAbsolutePath() + "/plugins/";
			} catch (Throwable t) {
				PLUGIN_FOLD_D = null;
			}
		}
		return PLUGIN_FOLD_D;
	}

	public static final String getPluginRootFold_Temp(){
		if (PLUGIN_FOLD_D_TEMP == null) {
			try {
				PLUGIN_FOLD_D_TEMP = MainApplication.getApplication().getFilesDir().getAbsolutePath() + "/plugins_temp/";
			} catch (Throwable t) {
				PLUGIN_FOLD_D_TEMP = null;
			}
		}
		return PLUGIN_FOLD_D_TEMP;
	}
	
	public static final String DEFAULT_ACS_BASEURL = DEFAULT_ACS_BASEURL_D;
	
	public static final String DEFAULT_QUERY_DATA_URL = "acs.wuliancloud.com:33443";

	public static final String URL_WULIAN = "http://www.wuliangroup.com";

}
