package cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_89_DoorLock_6;
import cc.wulian.smarthomev5.R;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.MediaCodecMonitor;
import com.wulian.iot.Config;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.utils.TimeUtil;
import com.wulian.iot.view.base.SimpleFragmentActivity;

public class PlayHawkeyeActivity extends SimpleFragmentActivity implements IRegisterIOTCListener, OnClickListener {

	/*
	 * 要注意拿这个 的设置选项作为参考，完成相关任务。 <activity
	 * android:name="cc.wulian.monitor.activity.MonitorTKActivity"
	 * android:alwaysRetainTaskState="true"
	 * android:configChanges="orientation|keyboardHidden|screenSize"
	 * android:screenOrientation="portrait" android:hardwareAccelerated="true"
	 * android:theme="@android:style/Theme.NoTitleBar.Fullscreen" >
	 * <intent-filter> <action
	 * android:name="cc.wulian.smarthomemonitor.MonitorTKActivity" />
	 * 
	 * <category android:name="android.intent.category.DEFAULT" />
	 * </intent-filter> </activity>
	 */

	private static final String TAG = "IOTCamera";
	private ImageView ivSpeak, iotSnapsHoot, iotMapDepot, iotRecording, iotSet, lockbackimage;
	private MediaCodecMonitor monitor = null;

	private RelativeLayout hawkeyerelative;
	private LinearLayout lockvisibleLine;

	private LinearLayout linTime; // 时间配置函数、显示时间的函数
	private TextView tvTime;
	private ImageView ivTime;
	private TimeUtil timeUtil = new TimeUtil();

	private String tutkUid = "XGW17V7DGVW2FAV4111A";

	private boolean mIsListening = true;
	private Context mContext;


	public void root() {
		setContentView(R.layout.device_hawkeye_camera);
	}
	
	
	@Override
	public void initView() {
		
		if (monitor != null) {
			monitor.deattachCamera();
			monitor = null;
		}
		monitor = (MediaCodecMonitor)findViewById(R.id.monitor_eagle);

		// 这个地方修改用于屏幕增益
		monitor.setMaxZoom(2.0f);
		// call in onResume
		

		ivSpeak = (ImageView) findViewById(R.id.iot_eagle_speak);// 语音
		ivSpeak.setTag("Speak");

		iotSnapsHoot = (ImageView) findViewById(R.id.iot_eagle_snapshot);// 屏幕快照
		iotMapDepot = (ImageView) findViewById(R.id.iot_mapdepot);// 上锁界面显示
		iotRecording = (ImageView) findViewById(R.id.iot_eagle_record);// 录制视频
		iotRecording.setTag("recording");
		iotSet = (ImageView)findViewById(R.id.iot_eagle_set); // 图库界面显示。

		hawkeyerelative = (RelativeLayout) findViewById(R.id.hawkeye_videoView_relative);
		lockvisibleLine = (LinearLayout) findViewById(R.id.hawkeye_lockon_line);
		lockbackimage = (ImageView) findViewById(R.id.lock_back_image);

		linTime = (LinearLayout) findViewById(R.id.lin_videotape_time_hawk);
		tvTime = (TextView) findViewById(R.id.tv_videotape_time_hawk);
		ivTime = (ImageView) findViewById(R.id.iv_videotape_time_hawk);
		
		
	}
	
	
	
	@Override
	public void initData() {

		mContext = this;
		  
	}

	public void initEvents() {

		ivSpeak.setOnClickListener(this);
		iotSnapsHoot.setOnClickListener(this);
		iotMapDepot.setOnClickListener(this);
		iotRecording.setOnClickListener(this);
		iotSet.setOnClickListener(this);
		lockbackimage.setOnClickListener(this);	
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startPlayVideo();
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (monitor != null) {
			
			monitor = null;
			Log.i(TAG, "--------销毁monitor");
			
		}
		
	}
	public void stopPlaySurfaceView() {
		if (WL_89_DoorLock_6.cHelperHawkeye == null)
			return;

		
		
		if (monitor != null) {
			monitor.deattachCamera();	
		}

		WL_89_DoorLock_6.cHelperHawkeye.unregisterIOTCListener(this);

		WL_89_DoorLock_6.cHelperHawkeye.stopSpeaking(WL_89_DoorLock_6.mSelectedChannel);
		WL_89_DoorLock_6.cHelperHawkeye.stopListening(WL_89_DoorLock_6.mSelectedChannel);

		// stopShow
		WL_89_DoorLock_6.cHelperHawkeye.stopShow(WL_89_DoorLock_6.mSelectedChannel);

		// disconnect camera made by guofeng 这两项本来是打开的，我给他关闭了
		// mCamera.stop(Camera.DEFAULT_AV_CHANNEL);
		// mCamera.disconnect();
	}

	private void startPlayVideo() {

		if (WL_89_DoorLock_6.cHelperHawkeye == null)
			return;
		
		WL_89_DoorLock_6.cHelperHawkeye.registerIOTCListener(this);

		WL_89_DoorLock_6.cHelperHawkeye.startShow(WL_89_DoorLock_6.mSelectedChannel, true, false,Camera.SOFT_DECODE);

		if (monitor != null) {

			monitor.attachCamera(WL_89_DoorLock_6.cHelperHawkeye, WL_89_DoorLock_6.mSelectedChannel);

		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
        super.onPause();
		stopPlaySurfaceView();

	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cateye_connection_sure:
			// Config.isEagleNetWork = false;
			// Intent it=new Intent(mContext , HawkeyeFindHotActivity.class);
			// startActivity(it);
			// finish();
			break;
		case R.id.iv_cateye_titlebar_back:
			// finish();
			break;
		case R.id.iot_mapdepot: // 开锁按钮
			lockvisibleLine.setVisibility(View.VISIBLE);
			break;
		case R.id.lock_back_image: // 开锁返回按钮
			lockvisibleLine.setVisibility(View.GONE);
			break;
		case R.id.iot_eagle_speak: // 听音按钮

			Log.i(TAG, "=====ivSpeak=====");
			listenspeak();
			break;

		case R.id.iot_eagle_record: // 录像按钮
			recording();
			break;
		case R.id.iot_eagle_snapshot: // 抓拍
			Bitmap mBmp = monitor.getBitmapSnap();
			IotUtil.saveSnapshot(mContext, mBmp, IotUtil.getSnapshotEaglePath(tutkUid));
			break;
		case R.id.iot_eagle_set: // 图库
			Log.i(TAG, "=====ivSpeak=====图库");
//			GalleryHawkeyeActivity
			Intent intent = new Intent(mContext, GalleryHawkeyeActivity.class);
			startActivity(intent);
			break;
		}
	}

	private void listenspeak() {
		switch (ivSpeak.getTag().toString()) {
		case "Speak":
			WL_89_DoorLock_6.cHelperHawkeye.startListening(WL_89_DoorLock_6.mSelectedChannel, false);
			WL_89_DoorLock_6.cHelperHawkeye.startSpeaking(WL_89_DoorLock_6.mSelectedChannel);
			ivSpeak.setTag("NoSpeak");
			ivSpeak.setImageResource(R.drawable.btn_tackback_pressed_1);
			break;
		case "NoSpeak":
			ivSpeak.setTag("Speak");
			WL_89_DoorLock_6.cHelperHawkeye.stopSpeaking(WL_89_DoorLock_6.mSelectedChannel);
			WL_89_DoorLock_6.cHelperHawkeye.stopListening(WL_89_DoorLock_6.mSelectedChannel);
			ivSpeak.setImageResource(R.drawable.eagle_icon_speak);
			break;
		}
	}

	private void recording() {
		switch (iotRecording.getTag().toString()) {
		case "recording":
			Log.e(TAG, iotRecording.getTag().toString());
			iotRecording.setTag("unrecording");
			linTime.setVisibility(View.VISIBLE);
			timeUtil.addTime(handler);
			WL_89_DoorLock_6.cHelperHawkeye.startRecording(WL_89_DoorLock_6.mSelectedChannel,
					IotUtil.getFileName(tutkUid, Config.eagleVideoType),
					WL_89_DoorLock_6.cHelperHawkeye.getCamIndex() == 0 ? true : false);
			break;
		case "unrecording":
			Log.e(TAG, iotRecording.getTag().toString());
			linTime.setVisibility(View.GONE);
			timeUtil.stopTime();
			iotRecording.setTag("recording");
			WL_89_DoorLock_6.cHelperHawkeye.stopRecording(WL_89_DoorLock_6.mSelectedChannel);
			break;
		}
	}

	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case 2:	   
				
				tvTime.setText(timeUtil.lastTime);
				AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
				animation.setDuration(1000);
				ivTime.startAnimation(animation);  
				break;
			
		       }

		}
	};

	

	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveIOCtrlData(Camera camera, int avChannel,
			int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera camera, int i,
			byte[] abyte0, int j, int k, byte[] abyte1, boolean flag, int l) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
