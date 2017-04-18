package cc.wulian.smarthomev5.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.RemoteViews;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.databases.entitys.Messages;

public class CustomBackNotification
{
	/**
	 * 2
	 */
	public static final int DEFAULT_VIBRATE = 2;
	/**
	 * 900
	 */
	private static final long DEFAULT_VIBRATE_TIME = 900;
	/**
	 * -1
	 */
	private static final int DEFAULT_VIBRATE_REPEAT = -1;

	private static String  BASENAME = "A";
	
	public NotificationManager notificationManager;
	public Notification notification;
	private Intent intent;
	private MainApplication application =MainApplication.getApplication();;
	private static int notificationNum = 0;
	private static CustomBackNotification instance = new CustomBackNotification();
	private Preference preference = Preference.getPreferences();
	private Vibrator vibrator;
	private static boolean isRunning = false;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private long[] vibrate;
	private CustomBackNotification(){
		notificationManager = (NotificationManager) this.application.getSystemService(Context.NOTIFICATION_SERVICE);
		vibrator = (Vibrator) this.application.getSystemService(Context.VIBRATOR_SERVICE);
		this.intent = resetIntent();
	}
	public static CustomBackNotification getInstace(){
		return instance;
	}
	private Intent resetIntent(){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(application, MainHomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return intent;
	}
	public void initIntent(Intent intent){
		this.intent = intent;
	}
	public static void resetNotificationNum(){
		notificationNum = 0;
	}

	public void showMessageNotification( int icon, String ticker, String title, String content,
			String notifyType , String notifyFrom){
		BASENAME = notifyFrom;
		if (0 == icon){
			icon = R.drawable.app_icon_40;
		}
		if (StringUtil.isNullOrEmpty(ticker)){
			ticker = application.getString(R.string.home_notification_ticker);
		}
		if (StringUtil.isNullOrEmpty(title)){
			title = application.getString(R.string.app_name);
		}
		if (StringUtil.isNullOrEmpty(content)){
			content = application.getString(R.string.home_notification_content);
		}
		Intent intent = resetIntent();
		PendingIntent contentIntent = PendingIntent.getActivity(application, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new Notification(icon, ticker, System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		String soundPath = messageAlarmNotifyEnbalePath(notifyType);
		boolean sounded = !StringUtil.isNullOrEmpty(soundPath);
		boolean vibrated = preference.getBoolean(BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_VIBRATE_ENABLE, true);
		notification.defaults = Notification.DEFAULT_LIGHTS;
		if (sounded){
			notification.sound = Uri.parse(soundPath);
		}
		if (vibrated){
			notification.defaults = Notification.DEFAULT_VIBRATE;
		}
		if (!sounded && !vibrated){
			notification.defaults = Notification.DEFAULT_LIGHTS;
		}
		notification.number = notificationNum++;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(application, title, content, contentIntent);
		notificationManager.notify(R.drawable.app_icon_on, notification);
	}

	public void showNormalNotification(int notifyID, int icon, String ticker, String title, String content ){
		if (0 == icon){
			icon = R.drawable.app_icon_40;
		}
		if (StringUtil.isNullOrEmpty(ticker)){
			ticker = application.getString(R.string.home_notification_ticker);
		}
		if (StringUtil.isNullOrEmpty(title)){
			title = application.getString(R.string.app_name);
		}
		if (StringUtil.isNullOrEmpty(content)){
			content = application.getString(R.string.home_notification_content);
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(application, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new Notification(icon, ticker, System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		
		boolean vibrated = preference.getBoolean(BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_VIBRATE_ENABLE, true);
		if (!vibrated){
			notification.defaults = Notification.DEFAULT_LIGHTS;
		}
		else if (vibrated){
			notification.defaults = Notification.DEFAULT_VIBRATE;
		}
		notification.number = notificationNum++;
		
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(application, title, content, contentIntent);
		notificationManager.notify(notifyID, notification);
	}
	public void showAppErrNotification( Intent intent, int icon, String ticker, String title, String content ){
		if (0 == icon){
			icon = R.drawable.app_icon_40;
		}
		if (StringUtil.isNullOrEmpty(ticker)){
			ticker = application.getString(R.string.home_notification_ticker);
		}
		if (StringUtil.isNullOrEmpty(title)){
			title = application.getString(R.string.app_name);
		}
		if (StringUtil.isNullOrEmpty(content)){
			content = application.getString(R.string.home_notification_content);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(application, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification = new Notification(icon, ticker, System.currentTimeMillis());

		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(application, title, content, contentIntent);

		notificationManager.notify(R.string.home_connect_fail_hint, notification);
	}
	public void showDownloadNotification(int process){
		notificationManager.cancel(R.string.home_notification_content_download_progress);

		Intent intent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(application, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		RemoteViews contentViews = new RemoteViews(application.getPackageName(), R.layout.home_update_download_layout);
		contentViews.setImageViewResource(R.id.imageView_logo, android.R.drawable.stat_sys_download);
		if(process <0)
			contentViews.setTextViewText(R.id.process_tv, application.getString(R.string.set_version_update_erro));
		else
			contentViews.setTextViewText(R.id.process_tv, process+"%");
		notification = new Notification();
		notification.icon = android.R.drawable.stat_sys_download;
		notification.when = System.currentTimeMillis();
		notification.contentView = contentViews;
		notification.flags = Notification.FLAG_NO_CLEAR;
		notification.contentIntent = contentIntent;
		notificationManager.notify(R.string.home_notification_content_download_progress, notification);
	}
	public void cancelNotification( int notifyId ){
		notificationManager.cancel(notifyId);
	}

	public void cancelAllNotification(){
		notificationManager.cancelAll();
	}
	public void notifyAlarm(String notifyType , String notifyFrom){
		BASENAME = notifyFrom;
		try{
			if (isRunning) return;
			isRunning = true;
			soundAlarm( notifyType);
			vibrate(DEFAULT_VIBRATE_TIME, DEFAULT_VIBRATE_REPEAT);
			isRunning = false;
		}
		catch (Exception e){
			e.printStackTrace();
			isRunning = false;
		}
	}

	private void vibrate( long time, int repeat ) throws InterruptedException{
		if (preference.getBoolean(BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_VIBRATE_ENABLE, true)){
			if (vibrate == null){
				vibrate = new long[]{0, time / 6, time / 3, time / 2};
			}
			vibrator.vibrate(vibrate, repeat);
			Thread.sleep(time);
		}
	}

	private void soundAlarm(final String notifyType ){
		String path = messageAlarmNotifyEnbalePath( notifyType);
		if (!StringUtil.isNullOrEmpty(path)){
			try{
				if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.reset();
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepare();
				mediaPlayer.start();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取报警声音
	 * @paramnotifyType
	 * @return
	 */
	private String getDefaultPath(){
		return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).getPath();
	}
	public String messageAlarmNotifyEnbalePath(String notifyType){
		String filePath = "";
		if (TextUtils.equals(Messages.TYPE_DEV_ALARM, notifyType)){
			boolean isEnable = preference.getBoolean(BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO_ENABLE, true);
			if(isEnable){
				filePath =  preference.getString(BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO, getDefaultPath());
			}
		}
		else if (TextUtils.equals(Messages.TYPE_DEV_OFFLINE, notifyType)){
			boolean isEnable = preference.getBoolean(BASENAME + "_" + IPreferenceKey.P_KEY_OFFLINE_ALARM_NOTE_TYPE_AUDIO_ENABLE ,true);
			if(isEnable){
				filePath =  preference.getString(BASENAME + "_" + IPreferenceKey.P_KEY_OFFLINE_ALARM_NOTE_TYPE_AUDIO, getDefaultPath());
			}
		}
		else if (TextUtils.equals(Messages.TYPE_DEV_LOW_POWER, notifyType)){
			boolean isEnable = preference.getBoolean(BASENAME + "_" + IPreferenceKey.P_KEY_LOW_POWER_ALARM_NOTE_TYPE_AUDIO_ENABLE, true);
			if(isEnable){
				filePath = preference.getString(BASENAME + "_" + IPreferenceKey.P_KEY_LOW_POWER_ALARM_NOTE_TYPE_AUDIO, getDefaultPath());
			}
		}
		else if (TextUtils.equals(Messages.TYPE_DEV_DESTORY, notifyType)){
			boolean isEnable = preference.getBoolean(BASENAME + "_" + IPreferenceKey.P_KEY_DESTORY_ALARM_NOTE_TYPE_AUDIO_ENABLE, true);
			if(isEnable){
				filePath =  preference.getString(BASENAME + "_" + IPreferenceKey.P_KEY_DESTORY_ALARM_NOTE_TYPE_AUDIO, getDefaultPath());
			}
		}
		return filePath;
	}
}
