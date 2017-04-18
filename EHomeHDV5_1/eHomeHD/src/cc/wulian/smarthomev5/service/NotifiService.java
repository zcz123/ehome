package cc.wulian.smarthomev5.service;

import com.wulian.iot.Config;
import com.wulian.iot.view.device.play.PlayEagleActivity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.BaiduPushEntity;
import cc.wulian.smarthomev5.receiver.OperBaiduReceiver;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotifiService extends Service{
	private final static String TAG = "NotifiService";
	/** Notification管理 */
	public NotificationManager mNotificationManager;
	/** Notification构造器 */
	NotificationCompat.Builder mBuilder;
	/** Notification的ID */
	int notifyId = 100;
	private OperBaiduReceiver rpReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		initService();
		initNotify();
		registerBaiduPush();
	}
	private void initService(){
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	@SuppressLint("InlinedApi")
	private void initNotify(){
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder
				.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
				.setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
				.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				.setSmallIcon(R.drawable.logo_user);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterBaiduPush();
	}
	/*******************************百度推送接*************************************/
	private final void registerBaiduPush(){
		if(rpReceiver == null){
			Log.d(TAG, "===registerBaiduPush===");
			rpReceiver = new OperBaiduReceiver(){
				@Override
				public void doPush(BaiduPushEntity baiduPushEntity) {
					Log.d(TAG, baiduPushEntity.getUid());
					Log.d(TAG, baiduPushEntity.getPwd());
					showIntentActivityNotify(baiduPushEntity);
				}
			};
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(OperBaiduReceiver.ACTION);
			registerReceiver(rpReceiver, intentFilter);
		}
	}
	private final void unregisterBaiduPush(){
		if(rpReceiver!=null){
			unregisterReceiver(rpReceiver);
		}
	}
	/*******************************百度推送接*************************************/
	/**跳转通知*/
	private final void showIntentActivityNotify(BaiduPushEntity baiduPushEntity){
		mBuilder.setAutoCancel(true)// 点击后让通知将消失
				.setContentTitle(baiduPushEntity.getTitle())
				.setContentText(baiduPushEntity.getEventType())
				.setTicker("点我")
				.setWhen(System.currentTimeMillis());//通知产生的时间，会在通知信息里显示;
		// 点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(this, PlayEagleActivity.class);
		resultIntent.putExtra(Config.tutkPwd, baiduPushEntity.getPwd());
		resultIntent.putExtra(Config.tutkUid, baiduPushEntity.getUid());
		resultIntent.putExtra("without", 1);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		mNotificationManager.notify(notifyId, mBuilder.build());
	}
	public PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
		return pendingIntent;
	}
}
