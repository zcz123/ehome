package cc.wulian.smarthomev5.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLToast;

import org.eclipse.paho.client.mqttv3.MqttException;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.core.mqpush.MQPushConnection;
import cc.wulian.ihome.wan.core.mqpush.MQPushConnection.IPushConnectCallBack;
import cc.wulian.ihome.wan.entity.MqttConnectionInfo;
import cc.wulian.ihome.wan.entity.RegisterInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.event.AlarmEvent;
import cc.wulian.smarthomev5.fragment.more.nfc.NFCManager;
import cc.wulian.smarthomev5.fragment.more.nfc.NFCManager.AbstractNFCListener;
import cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.TTSManager;
import cc.wulian.smarthomev5.utils.NetworkUtil;
import cc.wulian.smarthomev5.utils.URLConstants;
import de.greenrobot.event.EventBus;

public class MainService extends Service {
	private static final String TAG = "MainService";
	private static String  BASENAME;
	public static long CHECK_MILLION_SECONDS = 60000 * 2;
	private MainApplication app = MainApplication.getApplication();
	public NotificationReceiver notificationReceiver;
	private final AccountManager mAccountManger = AccountManager
			.getAccountManger();
	private TTSManager ttsManager = TTSManager.getInstance();
	private TaskExecutor executor = TaskExecutor.getInstance();
	private Preference preference = Preference.getPreferences();
	private NFCManager nfcManager = NFCManager.getInstance();
	private volatile boolean isConversation = false;
	private NotificationCompat.Builder mBuilder;
	private static MQPushConnection pushConnection = null;
	private String topic = null;

	private volatile static MainService mainService = null;
	private Handler mainHandler = new Handler(Looper.getMainLooper());
	private RegisterInfo registerInfo;
	
	//循环监听当前的推送状态
	private Handler handler;    
    private Runnable task =new Runnable() {    
       public void run() {
    	   if(pushConnection!=null && (!pushConnection.isConnected())){
    		   Log.e("mainservice", "---false--"+pushConnection.getClientInfo());
    		   pushConnection.reconnect();
    	   }else{
    		   Log.e("mainservice", "---true--"+pushConnection.getClientInfo());
    	   }
    	   if(handler!=null){
    		   handler.postDelayed(this,60000);
    	   }
       }     
    };  

	// 控制是否显示推送信息
	private boolean isShowAlarmMessage;
	private int currentAlarmMessageNumber;

	public static final MainService getMainService() {
		return mainService;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Logger.debug("obBind Service");
		return null;
	}

	@Override
	public void onDestroy() {
		NetSDK.disconnectAll();
		if (app.mBackNotification != null)
			app.mBackNotification.cancelNotification(R.drawable.app_icon_on);
		unregisterReceiver(notificationReceiver);
		EventBus.getDefault().unregister(this);
		closePushConnection();
		super.onDestroy();
		handler=null;
		mainService = null;
	}

	private void closePushConnection() {
		if (pushConnection != null) {
			try {
				this.pushConnection.close();
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mainService = this;
		Logger.debug("onCreate service");
		EventBus.getDefault().register(this);
		initAllReceiver();
		initNfc();
		startPush();
	}

	private void startPush() {
		String userID = SmarthomeFeatureImpl.getData("userID");
		String gwID = mAccountManger.getmCurrentInfo().getGwID();
		String enterType = Preference.getPreferences().getUserEnterType();
		String pushType=SmarthomeFeatureImpl.getData(WelcomeActivityV5.ANDROID_LOGIN_PUSH_TYPE);
		if(enterType.equals("account")&&WelcomeActivityV5.ANDROID_LOGIN_PUSH_MQTT.equals(pushType)){
			TelephonyManager tm = (TelephonyManager) app
					.getSystemService(Context.TELEPHONY_SERVICE);
			// 设备的唯一标识
			String deviceId = tm.getDeviceId();
			// 设置终端信息
			if (StringUtil.isNullOrEmpty(deviceId)) {
				deviceId = Secure.getString(app.getContentResolver(),
						Secure.ANDROID_ID);
			}
			registerInfo = new RegisterInfo(deviceId);
			Log.e("---mainservice---","---"+registerInfo.getAppID());
			registerPushTopic(userID, enterType);
		}
	}

	/*
	 * 推送的实现
	 */
	public void registerPushTopic(String userID, String enterType) {
		String newtopic = generateTopic(userID, enterType);
		Log.e("---mainservice---","---newtopic"+newtopic);
		if (StringUtil.isNullOrEmpty(newtopic)) {
			// 参数格式不正确，无法生成topic
			return;
		}

		if (newtopic.equals(topic)) {
			// 主题没有变，不作处理
			return;
		}

		closePushConnection();

		if (connectPushServer(newtopic) == false) {
			return;
		}

		topic = newtopic;
		pushConnection.subscribe(newtopic);
		Log.e("---mainservice---","---subscribe-newtopic"+newtopic);

		// 订阅一个主题后，对推送信息进行管理
		currentAlarmMessageNumber = 0;
		isShowAlarmMessage = false;
		handler=null;
		handler=new Handler();
		handler.post(task);
		mainHandler.postDelayed(new Runnable() {

			public void run() {
				isShowAlarmMessage = true;
				if (app.mBackNotification != null
						&& currentAlarmMessageNumber > 0) {
					app.mBackNotification.showMessageNotification(
							R.drawable.app_icon_40,
							getString(R.string.main_service_wulian_push_alarm),
							getString(R.string.main_service_wulian_push_alarm),
							getResources().getString(R.string.main_service_push_message_number,
									currentAlarmMessageNumber), "0" , BASENAME);
				}
			}

		}, 3000);
	}

	public void unregisterCurrentTopic() {
		if (pushConnection != null && topic != null) {
			try {
				pushConnection.unsubscribe(topic);
			} catch (Exception e) {
				Log.e("pushsevice", "unsubscribe error", e);
			}
		}
	}

	private String generateTopic(String userID,String enterType) {
		String newtopic = null;
		switch (enterType) {
		case "account":
			if (!(userID == null || userID.equals(""))) {
				// 根据账号进行订阅
				newtopic = ("WLPush/" + "User/" + userID);
				Logger.info("------------>" + "WLPush/" + "User/" + userID);
			}
			break;
		case "gateway":
			String appID=registerInfo.getAppID();
			if (!(appID == null || appID.equals(""))) {
				// 根据网关进行推送
				newtopic = ("WLPush/" + "AppID/" + appID);
				Logger.info("------------>" + "WLPush/" + "AppID/" + appID);
			}
			break;
		}
		return newtopic;
	}

	/**
	 * 连接推送服务器 2016-6-15
	 * 
	 * @author yanzhy
	 * @return 连接成功返回true 连接失败返回false
	 */
	private boolean connectPushServer(String clientInfo) {
		pushConnection = new MQPushConnection();
		MqttConnectionInfo connectionInfo = new MqttConnectionInfo();
		connectionInfo.user = "wulian_mq";
		connectionInfo.passwd = "MQ168@wulian";
		connectionInfo.qos = 1;
		pushConnection.setConnectionInfo(connectionInfo);
		pushConnection.setRegisterInfo(mAccountManger.getRegisterInfo(),
				clientInfo);
		int result = pushConnection.connect(URLConstants.MQTT_PUSH_SERVER_ADDR,
				URLConstants.MQTT_PUSH_SERVER_PORT);
		if (result != ResultUtil.RESULT_SUCCESS) {
			Log.e("pushservice", "Can't connect push server:"
					+ URLConstants.MQTT_PUSH_SERVER_ADDR
					+ ". Can't receive push message.");
			pushConnection = null;
			return false;
		}
		pushConnection.setIPushConnectCallBack(new IPushConnectCallBack() {

			@Override
			public void postExec(String str) {
				getmessage(str);
			}
		});
		pushConnection.subscribe("WLPush/Public");
		return true;
	}

	private void showTips() {
		WLDialog.Builder builder = new Builder(app);
		View view = LayoutInflater.from(app).inflate(
				R.layout.account_permission_no_enter_dialog, null);
		TextView textView = (TextView) view
				.findViewById(R.id.account_permission_dialog_tv);
		textView.setText(app.getResources().getString(
				R.string.push_no_get_dialog));
		builder.setContentView(view)
				.setTitle(R.string.device_select_device_hint)
				.setPositiveButton(R.string.common_ok).setNegativeButton(null);
		WLDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startPush();
		return START_REDELIVER_INTENT;
	}

	private void initNfc() {
		NFCManager.getInstance().addNFCListener(new AbstractNFCListener() {

			@Override
			public void onExecute(final Intent intent) {
				executor.execute(new Runnable() {

					@Override
					public void run() {
						Logger.debug("excecute nfc");
						nfcManager.dispatchComingMessage(intent);
						nfcManager.parse();
						nfcManager.clear();
						mainHandler.post(new Runnable() {

							@Override
							public void run() {
								WLToast.showToast(
										MainService.this,
										MainService.this
												.getResources()
												.getString(
														R.string.more_nfc_function_read_success),
										WLToast.TOAST_SHORT);
							}
						});
					}
				});
			}

		});
	}

	private void initAllReceiver() {
		// for notification
		notificationReceiver = new NotificationReceiver();
		IntentFilter notificationFilter = new IntentFilter();
		notificationFilter
				.addAction(NotificationReceiver.ACTION_CONNECT_CHANGE);
		notificationFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
		notificationFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(notificationReceiver, notificationFilter);
		TelephonyManager manager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		manager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				super.onCallStateChanged(state, incomingNumber);
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE: {
					isConversation = false;
					break;
				}
				case TelephonyManager.CALL_STATE_OFFHOOK: {
					isConversation = true;
					break;
				}
				case TelephonyManager.CALL_STATE_RINGING: {
					isConversation = true;
					break;
				}
				default:
					break;
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

	public class NotificationReceiver extends BroadcastReceiver {

		public static final String ACTION_CONNECT_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Logger.debug("receiver:" + action);
			if (ACTION_CONNECT_CHANGE.equals(action)
					|| Intent.ACTION_SCREEN_ON.equals(action)) {
				if (!mAccountManger.isConnectedGW()) {
					Logger.debug("newwork up,screen up check network");
					if (NetworkUtil.isNetworkAvailable(MainService.this))
						mAccountManger.signinDefaultAccount();
				}
			}
		}
	}

	// 解析转成json后的string
	void getmessage(String message) {
		if (isShowAlarmMessage) {
			String msgType = "";
			String msgContent = "";
			System.out.println("-------------------" + message);
			try {
				JSONObject jsonObject = (JSONObject) JSON.parse(message);
				msgType = jsonObject.getString("msgType");
				JSONObject msgPayload = jsonObject.getJSONObject("msgPayload");
				msgContent = msgPayload.getString("msgContent");
				String gwId = msgPayload.getString("gwId");
				String devId = msgPayload.getString("devId");
				String alarmTime = msgPayload.getString("alarmTime");
				resolvePushMessage(msgContent, "0");
				app.isAlarming = true;
			} catch (Exception e) {
				Log.e("TAG", "", e);
				resolvePushMessage(msgContent, "0");
			}
		} else {
			currentAlarmMessageNumber++;
		}
	}

	public void resolvePushMessage(final String message,
			String action) {
		app.isAlarming = true;
		if (StringUtil.isNullOrEmpty(message)) {
			return;
		}
		final String readString = (message + " " + message)
				.replaceAll("\\+", "").replaceAll("\t", "")
				.replaceAll("\n", "").replaceAll("_", "");

		if (app.isBackground()) {
			if (app.mBackNotification != null){
                BASENAME = "P";
				app.mBackNotification.showMessageNotification(
						R.drawable.app_icon_40, message,
						getString(R.string.main_service_wulian_push_alarm),
						message, action , BASENAME);
            }
		} else {
            BASENAME = "A";
			// 开启线程池并且在当前线程打开Looper从而使用Handle将事务进入到UI线程内执行
			executor.execute(new Runnable() {

				@Override
				public void run() {
					mainHandler.post(new Runnable() {

						@Override
						public void run() {
							WLToast.showAlarmMessageToast(app, message,
									WLToast.TOAST_SHORT, null);
						}
					});
				}
			});
		}
        startVoiceAndVibrateTips(readString,action);
	}
	
	private void startVoiceAndVibrateTips(final String readString, final String action) {
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				if (app.mBackNotification != null)
					app.mBackNotification.notifyAlarm(action , BASENAME);
			}
		});
		if (preference.getBoolean(BASENAME + "_" +
				IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_TTS_ENABLE, true)) {
			if (!StringUtil.isNullOrEmpty(readString)) {
				if (isConversation)
					return;
				executor.execute(new Runnable() {
					@Override
					public void run() {
						ttsManager.readTts(readString , BASENAME);
					}
				});

			}
		}
	}

	public void onEventMainThread(AlarmEvent event) {
		String message=event.getAlarmStr();
		String action=event.getAction();
		String enterType = Preference.getPreferences().getUserEnterType();
		if(pushConnection==null){
			pushConnection=new MQPushConnection();
		}
		String pushType=SmarthomeFeatureImpl.getData(WelcomeActivityV5.ANDROID_LOGIN_PUSH_TYPE);
		String appToken=SmarthomeFeatureImpl.getData(WelcomeActivityV5.ANDROID_LOGIN_APP_TOKEN);
		boolean isMQTTConnected=enterType.equals("account")&&(pushConnection.isConnected()&&pushType.equals(WelcomeActivityV5.ANDROID_LOGIN_PUSH_MQTT));
		boolean isFCMConnected=enterType.equals("account")&&(!StringUtil.isNullOrEmpty(appToken))&&pushType.equals(WelcomeActivityV5.ANDROID_LOGIN_PUSH_FCM);
		if(isMQTTConnected||isFCMConnected){
			return;
		}
		resolvePushMessage(message,action);
		app.isAlarming = true;
	}

	public static MQPushConnection getPushConnection(){
		return pushConnection;
	}

}