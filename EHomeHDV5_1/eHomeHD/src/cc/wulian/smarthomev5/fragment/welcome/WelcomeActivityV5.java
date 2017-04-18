package cc.wulian.smarthomev5.fragment.welcome;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.util.Date;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.activity.SigninActivityV5;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.UpdateManger;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.URLConstants;
import cc.wulian.smarthomev5.utils.VersionUtil;

public class WelcomeActivityV5 extends Activity implements Handler.Callback{
	private static final String TAG = "WelComeActivity";
	public final static String ANDROID_LOGIN_APP_VERSION="loginAppVer";
	public final static String ANDROID_LOGIN_APP_TOKEN="loginAppToken";
	public final static String ANDROID_LOGIN_APP_TYPE="loginAppType";
	public final static String ANDROID_LOGIN_OS_TYPE="loginOsType";
	public final static String ANDROID_LOGIN_OS_TYPE_VALUE="0";
	public final static String ANDROID_LOGIN_PUSH_TYPE="loginPushType";
	public final static String ANDROID_LOGIN_PUSH_FCM="3";
	public final static String ANDROID_LOGIN_PUSH_MQTT="1";
	private Preference preference = Preference.getPreferences();
    private MainApplication application = MainApplication.getApplication();
	private Handler handler = new Handler(this);
	private Date date = new Date();
	private AccountManager accountManager = null;
	private LinearLayout splashLineLayout = null;
	private static boolean isadvexist = false;
	private static String gatewayId = null;
	private static String gatewayPwd = null;
	public static String gateWayIdTag = "WGWTag";
	public static String gatePwdTag = "WGPTag";
	private Bundle mBundle = null;
	public static final String VERSION_RUN_ONCE = "5.3.3";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_welcome_v5);
		splashLineLayout = (LinearLayout) this.findViewById(R.id.welcome_splash_ll);
		setSplash();
		TaskExecutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				try {
					checkFirstLogin();
					accountManager = AccountManager.getAccountManger();
                    checkAdvertisements();
					setFCMConfig();
					moMedia();
                    WelcomeActivityV5.this.startAty();
				} catch (Exception e) {
					Log.e(TAG, "", e);
				}
			}
		});
	}

	private void checkFirstLogin() {
		boolean isFirst = Preference.getPreferences().isCurrentVersionFirst();
		if(isFirst){
			SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.TOKEN,"");
			Preference.getPreferences().currentVersionFirst();
		}
	}

	/**
	 * 避免文件泄露在系统图库和系统铃声中
	 */
	private void moMedia(){
		File nomedia = new File(Environment.getExternalStorageDirectory() + "/wulian/.nomedia/");
		if (! nomedia.exists())
			try {
				nomedia.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private void setFCMConfig() {
		SmarthomeFeatureImpl.setData(ANDROID_LOGIN_OS_TYPE,ANDROID_LOGIN_OS_TYPE_VALUE);
		setAppVer();
		setAppType();
		setAppToken();
		setPushType();
	}

	private void setAppVer() {
		String sysVersion = "V" + VersionUtil.getVersionName(this);
		SmarthomeFeatureImpl.setData(ANDROID_LOGIN_APP_VERSION,sysVersion);
	}

	private void setAppToken() {
		String token = FirebaseInstanceId.getInstance().getToken();
		if(!StringUtil.isNullOrEmpty(token)){
			SmarthomeFeatureImpl.setData(ANDROID_LOGIN_APP_TOKEN,token);
		}
	}

	private void setAppType() {
		String appType=accountManager.getRegisterInfo().getAppType();
		SmarthomeFeatureImpl.setData(ANDROID_LOGIN_APP_TYPE,appType);
	}

	private void setPushType() {
		//推送类型(1:mqtt推送,2:ios推送,3: FCM推送)
		//谷歌服务有+token不为零=3,其余都为1
		if(LanguageUtil.isChina()){
			SmarthomeFeatureImpl.setData(ANDROID_LOGIN_PUSH_TYPE,ANDROID_LOGIN_PUSH_MQTT);
			return;
		}
		boolean googleserviceFlag;
		GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
		if(resultCode == ConnectionResult.SUCCESS) {
			googleserviceFlag=true;
		}else{
			googleserviceFlag=false;
		}
		if(googleserviceFlag&&(!StringUtil.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken()))){
			SmarthomeFeatureImpl.setData(ANDROID_LOGIN_PUSH_TYPE,ANDROID_LOGIN_PUSH_FCM);
		}else{
			SmarthomeFeatureImpl.setData(ANDROID_LOGIN_PUSH_TYPE,ANDROID_LOGIN_PUSH_MQTT);
		}

	}

	@SuppressWarnings("deprecation")
	private void setSplash() {
		if (!LanguageUtil.isChina()) {
			splashLineLayout.setBackgroundResource(R.drawable.welcome_splash_english);
		}
		try {
			String splashPath = FileUtil.getSplashPath() + "/" + UpdateManger.SPLASH_NAME;
			if (FileUtil.checkFileExistedAndAvailable(splashPath)) {
				Drawable drawable = BitmapDrawable.createFromPath(splashPath);
				if (drawable != null) {
					if (android.os.Build.VERSION.SDK_INT >= 16)
						// splashLineLayout.setBackground(drawable);
						splashLineLayout.setBackgroundDrawable(drawable);
					else
						splashLineLayout.setBackgroundDrawable(drawable);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final void startAty() {
		mBundle = getIntent().getExtras();
		// mBundle = null;
		if (mBundle != null) {
			gatewayId = mBundle.getString(gateWayIdTag);
			gatewayPwd = mBundle.getString(gatePwdTag);
		}
        if (gatewayId != null && gatewayPwd != null) {
            System.out.println("------>" + "外部登陆");
            handler.sendEmptyMessage(2);
        } else {
            System.out.println("------>" + "内部登陆");
            handler.sendEmptyMessageDelayed(1, 2000);
        }
	}

	private final void startAtyFromWithout() {
		if ((gatewayId != null && !gatewayId.trim().equals(""))
				&& (gatewayPwd != null && !gatewayPwd.trim().equals(""))) {
			SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.IS_LOGIN, "false");
			Intent mIntent = new Intent(this, SigninActivityV5.class);
			mIntent.putExtra(gateWayIdTag, gatewayId);
			mIntent.putExtra(gatePwdTag, gatewayPwd);
			myStartActivity(mIntent);
		}
}
	
	private final void startAtyFromInside() {
		//暂时屏蔽
		boolean isGuide = Preference.getPreferences().isWelcomeReadGuide(VERSION_RUN_ONCE)
				&& this.getResources().getBoolean(R.bool.use_account);

		String defaultGwID = preference.getLastSigninID();
		boolean isRemberPassword = preference.isAutoLoginChecked(defaultGwID) && preference.isRememberChecked(defaultGwID);
		System.out.println("------>" + "内部登陆");
		if (isGuide) {
			System.out.println("------>" + "isGuide");
			myStartActivity(new Intent(WelcomeActivityV5.this, GuideActivityV5.class));
		} else if (isadvexist){
			System.out.println("------>" + "isadvexist");
			myStartActivity(new Intent(WelcomeActivityV5.this, AdvActivityV5.class));
		}else if (isRemberPassword) {
                System.out.println("------>" + "isRemberPassword");
                myStartActivity(new Intent(WelcomeActivityV5.this, MainHomeActivity.class));
        } else if (Preference.getPreferences().isUseAccount()) {
                System.out.println("------>" + "isUseAccount");
                try {
                    tryLoginLastGateway();
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
        }else {
            System.out.println("------>" + "else");
            myStartActivity(new Intent(WelcomeActivityV5.this, SigninActivityV5.class));
        }
	}

	/**
	 * 尝试登录上次退出的网关 2016-6-8
	 * 
	 * @author yanzhy
	 */
	private void tryLoginLastGateway() {
		accountManager.setConnectGatewayCallbackAndActivity(connectGatewayCallback, this);
		TaskExecutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				accountManager.loginLastGatewayByAccount();
			}
		});
	}

	private AccountManager.ConnectGatewayCallback connectGatewayCallback = new AccountManager.ConnectGatewayCallback() {
		@Override
		public void connectSucceed() {
			accountManager.clearConnectGatewayCallbackAndActivity(this);
			WelcomeActivityV5.this.jumpToMainActivity();
		}

		@Override
		public void connectFailed(int reason) {
			WelcomeActivityV5.this.jumpToAccountLogin();
		}
	};

	private void jumpToMainActivity() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(WelcomeActivityV5.this, MainHomeActivity.class);
				myStartActivity(intent);
			}
		});
	}

	private void jumpToAccountLogin() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(WelcomeActivityV5.this, Html5PlusWebViewActvity.class);
				String uri = URLConstants.LOCAL_BASEURL + "login.html";
				intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
				WelcomeActivityV5.this.myStartActivity(intent);
			}
		});
	}

	private void myStartActivity(Intent i) {
		super.startActivity(i);
        this.finish();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 1:
			this.startAtyFromInside();
			break;
		case 2:
			this.startAtyFromWithout();
			break;
		}
		return false;
	}

    //检查本地是否存在图片
    private void checkAdvertisements() {
        String fileName = "welAdvertisement.png";
        String floder = FileUtil.getAdvertisementPath();
        String startTime = preference.getAdvertisement_s_time();
        String endTime = preference.getAdvertisement_e_time();
        try {
            if (!FileUtil.checkFileExistedAndAvailable(floder + "/"
                    + fileName) ) {
                isadvexist = false;
            }else if(Long.valueOf(startTime) <= date.getTime() &&
					Long.valueOf(endTime) >= date.getTime()){
                isadvexist = true;
            }else {
                isadvexist = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
