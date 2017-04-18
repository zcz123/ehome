package cc.wulian.smarthomev5.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Process;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.collect.Maps;
import cc.wulian.smarthomev5.databases.CustomDataBaseHelper;
import cc.wulian.smarthomev5.entity.AdvertisementEntity;
import cc.wulian.smarthomev5.service.LocationService;
import cc.wulian.smarthomev5.service.MainService;
import cc.wulian.smarthomev5.service.NotifiService;
import cc.wulian.smarthomev5.service.html5plus.plugins.CmdControlFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.CustomBackNotification;
import cc.wulian.smarthomev5.tools.HyphenateManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.UEHandler;
import cc.wulian.smarthomev5.tools.WLCameraOperationManager;
import cc.wulian.smarthomev5.utils.TargetConfigure;
import cc.wulian.smarthomev5.utils.VersionUtil;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends MultiDexApplication {
	public static final String USERNAME = "wulian_123";

	//	public static final String DISTRIBUTE_URL_TEST = "thirdpartytest.ecamzone.cc:8444";
	public static final String TAG = "MyApplication";


	public static final int HANDLER_WAHT_MSGRESP = 0;
	public static final int HANDLER_WAHT_ONDISCONNECT = 1;
	public static final int HANDLER_WAHT_QRCODE = 3;
	public static final int ACTIVITY_REQUEST_CODE = 10001;
	public static final String ACTIVITY_RESULT_EXTRA = "activity_result_extra";

	public static final int REQUEST_CODE_ASK_RECORD_AUDIO = 2;
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	private static final Comparator<String> DEFAULT_TREE_MAP_COMPARABLE = new Comparator<String>() {
		@Override
		public int compare(String string1, String string2) {
			if (string1.length() < 13 || string2.length() < 13)
				return 0;

			int length = 12;
			int k1 = StringUtil.toInteger(string1.substring(length));
			int k2 = StringUtil.toInteger(string2.substring(length));
			return k1 - k2;
		}
	};
	private static MainApplication mInstance = null;
	/**
	 * key=gwId + roomId
	 */
	/*
	 * public TreeMap<String, RoomInfo> roomInfoMap =
	 * Maps.newTreeMap(ROOM_COMPAREABLE);
	 */
	/**
	 * key=gwId + sceneId
	 */
	public TreeMap<String, SceneInfo> sceneInfoMap = Maps.newTreeMap(DEFAULT_TREE_MAP_COMPARABLE);
	/**
	 * key=gwId + devId
	 */
	public HashMap<String, Map<String, SceneInfo>> bindSceneInfoMap = Maps.newHashMap();

	/**
	 * key=gwId + devId
	 */
	public HashMap<String, Map<String, DeviceInfo>> bindDeviceInfoMap = Maps.newHashMap();

	/**
	 * key=gwId + devId <br/>
	 * rss >= 0 && rss <= 15, red<br/>
	 * rss > 15 && rss <= 30, yellow<br/>
	 * rss > 30 && rss <= 40, orange <br/>
	 * rss > 40 && rss <= 100, green <br/>
	 */
	public AdvertisementEntity welAdveEntity = new AdvertisementEntity();

	public HashMap<String, Integer> queryRssiInfoMap = Maps.newHashMap();

	private final List<BaseActivity> mActivitys = new ArrayList<BaseActivity>();

	public boolean isDemo = false;
	public boolean isDownloading = false;

	public boolean isNetWorkCanUse = false;
	public boolean isNetWorkWiFi = false;

	public boolean isTaskRunBack = true;

	/**
	 * is device report alarm event and is show user alarming
	 */
	public boolean isAlarming = false;
	public boolean isHideOfflineDevice = false;

	public CustomBackNotification mBackNotification;
	public FragmentActivity mCurrentActivity;
	public CustomDataBaseHelper mDataBaseHelper;

	public int mCurrentThemeChooseID;

	private Activity mTopActivity = null;
	private final static int PHONE_STATUS_BACKGROUND=0;
	private int foregroundActivities=0;



	public synchronized static MainApplication getApplication() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		initConfig();
		initDB();
		HyphenateManager.getHyphenateManager().initConfig(this);
		if (!this.getResources().getBoolean(R.bool.use_fabric) || TargetConfigure.forTest == true) {
			initUeHandler();
		} else {
			Fabric.with(this, new Crashlytics());
			Fabric.with(this, new Answers());
		}
		if(TargetConfigure.ENALBLE_WEBVIEW_DEBUG) {
			H5PlusWebView.enableDebug();
		}  else {
			H5PlusWebView.disableDebug();
		}
		Engine.startup();
		Engine.addFeature("ehomev5", new SmarthomeFeatureImpl());
		initGlobeActivity();
		Engine.addFeature("cmdControl", new CmdControlFeatureImpl());
	}

	// must init when activity onCreate
	public void initNotification(Intent intent) {
		mBackNotification = CustomBackNotification.getInstace();
		mBackNotification.initIntent(intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	private void initUeHandler() {
		UEHandler ueHandler = new UEHandler(this);
		Thread.setDefaultUncaughtExceptionHandler(ueHandler);
	}

	public void startService() {
		startService(new Intent(this, MainService.class));
		startService(new Intent(this, LocationService.class));
		startService(new Intent(this, NotifiService.class));// 启动监听 百度推送消息 add syf
	}

	@SuppressLint("NewApi")
	private void initConfig() {
		int sdkInt = VersionUtil.getSystemSdkInt();
		if (sdkInt > Build.VERSION_CODES.FROYO)
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
	}

	private void initDB() {
		mDataBaseHelper = CustomDataBaseHelper.getInstance(this);

	}

	public void stopApplication() {
		Preference mPreference = Preference.getPreferences();
		mPreference.saveIsNormalQuit(true);
		AccountManager am = AccountManager.getAccountManger();
		WLCameraOperationManager.destoryInstance();
		am.setConnectedGW(false);
		isTaskRunBack = true;
		finshActivitys();
		stopServcie();
	}

	private void stopServcie() {
		stopService(new Intent(this, MainService.class));
		stopService(new Intent(this, LocationService.class));
		stopService(new Intent(this, NotifiService.class));
	}

	private void finshActivitys() {
		for (Activity a : mActivitys) {
			a.finish();
		}
	}

	public long appStartedReceData;
	public long appStartedSendData;

	public void getSatrtTrafficStus() {
		appStartedReceData = getReceData();
		appStartedSendData = getSendData();
	}

	public long getReceData() {
		return TrafficStats.getUidRxBytes(Process.myUid());
	}

	public long getSendData() {
		return TrafficStats.getUidTxBytes(Process.myUid());
	}

	public void pushActivity(BaseActivity activity) {
		mActivitys.add(activity);
	}

	public void removeActivity(BaseActivity activity) {
		mActivitys.remove(activity);
	}

	public List<BaseActivity> getActivities() {
		return mActivitys;
	}

	public boolean isBackground() {
		PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		if(!isScreenOn) {
			//当屏幕熄屏时，程序应该当作在后台。
			return true;
		}
		if(foregroundActivities==PHONE_STATUS_BACKGROUND){
			return true;
		}
		return false;
	}

	private void initGlobeActivity(){
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			}

			@Override
			public void onActivityDestroyed(Activity activity) {
			}

			@Override
			public void onActivityStarted(Activity activity) {
				foregroundActivities++;
			}

			@Override
			public void onActivityResumed(Activity activity) {
				// 此处记录最后的activity
				mTopActivity = activity;
			}

			@Override
			public void onActivityPaused(Activity activity) {
			}

			@Override
			public void onActivityStopped(Activity activity) {
				foregroundActivities--;
			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
		});
	}

	public Activity getTopActiviy() {
		return mTopActivity;
	}
}
