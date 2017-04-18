package com.wulian.iot.view.device.play;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.MediaSoftCodecMonitor;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.IOTCDevChPojo;
import com.wulian.iot.cdm.action.EagleAction;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.server.queue.MessageQueue;
import com.wulian.iot.server.receiver.DoorLockReceiver;
import com.wulian.iot.utils.AnimationUtils;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.adapter.KeyboardAdapter;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.view.ui.GalleryActivity;
import com.wulian.iot.widght.DialogManager;
import com.wulian.iot.widght.DialogRealize;
import com.yuantuo.customview.ui.WLToast;
public class PlayDoorLock_89 extends SimpleFragmentActivity implements Handler.Callback,View.OnClickListener{
	private MediaSoftCodecMonitor mediaCodecMonitor = null;
	private Context mContext = PlayDoorLock_89.this;
	private IOTCDevChPojo iotcDevChPojo;//iot通道连接参数
	private Handler mHandler = new Handler(PlayDoorLock_89.this);
	private LinearLayout  keyboardLinearLayout = null;
	private GridView  keyboardGridView,kekPwdGridview = null;
	private KeyboardAdapter keyboardAdapter = null;
	private  List<String> key = null;
	private String openDoorLockPwd = null;
	private EagleAction eagleAction = null;
	private Button pointOne;
	private Button pointTwo;
	private Button pointThree;
	private Button pointFour;
	private Button pointFive;
	private Button pointSix;
	private boolean videoFlag=false;
	private ImageView doorLookRecord;
	private ImageView speakDoorLook;
	private LinearLayout lowVoltageAlarm;
	private int [] speakImages = new int[]{R.drawable.btn_tackback_pressed_1,R.drawable.btn_tackback_pressed_2,R.drawable.btn_tackback_pressed_3,R.drawable.desk_btn_tackback_noraml};
	private int UIIndex;//切换图片下标
	private TimingUpdateUi timingUpdateUi = null;
	private String tutkUid,tutkPwd,deviceID;
	private MessageQueue messageQueue = null;
	private CameraHelper.Observer observer = new CameraHelper.Observer() {
		@Override
		public void avIOCtrlOnLine() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					DialogRealize.unInit().dismissDialog();
				}
			});
		}
		@Override
		public void avIOCtrlDataSource(byte[] data, int avIOCtrlMsgType) {
		}
		@Override
		public void avIOCtrlMsg(int resCode, String method) {
//			final String msg = messageQueue.filter(resCode,method).sendMsg();
//			if(msg!=null&&!msg.equals("")&&resCode==1){
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						Toast.makeText(PlayDoorLock_89.this, msg, Toast.LENGTH_SHORT).show();
//					}
//				});
//			}
		}
	};
	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
		case HandlerConstant.DEVICE_ONLINE:
			DialogRealize.unInit().dismissDialog();
		break;
		case HandlerConstant.UPDATE_UI:
		    updateUI(UIIndex);
		break;
		case HandlerConstant.TIEM_IS_UP:
			//弹出dialog 提示视频已断开连接
			initDiglog();
			break;
		}
		return false;
	}
	private OnItemClickListener  keyBoardItemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(keyboardAdapter.getItem(position).equals("C")){
				clearDoorLockPwd();
				addPoint(0);
				return;
			}
			if (keyboardAdapter.getItem(position).equals("×")) {
				if (openDoorLockPwd.length() > 0) {
					openDoorLockPwd = openDoorLockPwd.substring(0,
							openDoorLockPwd.length() - 1);
					addPoint(openDoorLockPwd.length());
				}
				return;
			}
			if (openDoorLockPwd==null) {
				 openDoorLockPwd =keyboardAdapter.getItem(position);
			}else {
				 openDoorLockPwd +=keyboardAdapter.getItem(position);
			}
			addPoint(openDoorLockPwd.length());
			if(openDoorLockPwd.length() == 6){
				checkOpenDoorLockPwd();
				addPoint(0);
				visibility(keyboardLinearLayout,View.GONE);
				return;
			}
		}
	};
	private CameraHelper.IOTCDevConnCallback iotcDevConnCallback = new  CameraHelper.IOTCDevConnCallback() {
		@Override
		public void session() {
			createSessionWaitThread = new CreateSessionWaitThread();
			createSessionWaitThread.start();
		}
		@Override
		public void success() {
			Log.i(TAG, "===createSessionSuccessfully   Decoding===");
			IotSendOrder.connect(cameaHelper.getmCamera());
			cameaHelper.createVideoCarrier(mediaCodecMonitor);
			cameaHelper.createVideoStream(Camera.SOFT_DECODE);//解码类型
		}
		@Override
		public void avChannel() {
			Log.i(TAG, "===createAvIndexFailed===");
			createAvChannelWaitThread = new CreateAvChannelWaitThread();
			createAvChannelWaitThread.start();
		}
	};
	@Override
	public void root() {
		setContentView(R.layout.device_doorlock_89_camera);
		handlerTime();
	}
    @Override
    public void initView() {
		mediaCodecMonitor = (MediaSoftCodecMonitor) findViewById(R.id.monitor_doorlock_89);
    	keyboardLinearLayout = (LinearLayout)findViewById(R.id.hawkeye_lockon_line);
    	keyboardGridView = (GridView)findViewById(R.id.keyboard_gridview);
//    	kekPwdGridview = (GridView)findViewById(R.id.key_pwd_gridview);
    	pointOne=(Button) findViewById(R.id.btn_password1);
    	pointTwo=(Button) findViewById(R.id.btn_password2);
    	pointThree=(Button) findViewById(R.id.btn_password3);
    	pointFour=(Button) findViewById(R.id.btn_password4);
    	pointFive=(Button) findViewById(R.id.btn_password5);
    	pointSix=(Button) findViewById(R.id.btn_password6);
    	doorLookRecord=(ImageView) findViewById(R.id.iot_door_look_record);
    	doorLookRecord.setOnClickListener(this);
		lowVoltageAlarm= (LinearLayout) findViewById(R.id.ll_low_voltage_alarm);
    }
	@Override
	public void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
//			doorLockAction = new DoorLockAction(mContext);
		    tutkPwd = getIntent().getStringExtra(Config.tutkPwd);
			tutkUid = getIntent().getStringExtra(Config.tutkUid);
			deviceID=getIntent().getStringExtra("deviceID");
			initKeyBoardView();
			initKeyPwdView();
			}
		}).start();
		eagleAction = new EagleAction(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		DialogRealize.init(mContext).showDiglog();
//		this.startPlaySurfaceView();
		mHandler.post(startAhannel);
		registerReceiver();//注册广播
	}
	@Override
	protected void onPause() {
		super.onPause();
		destroyWailThread();
		if(cameaHelper!=null){
			cameaHelper.destroyVideoStream();
			cameaHelper.destroyVideoCarrier(mediaCodecMonitor);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.sendDoorLockCloseOv788();
		if(cameaHelper!=null){
			cameaHelper.detach(observer);
			cameaHelper.destroyVideoCarrier(mediaCodecMonitor);
			cameaHelper.destroyCameraHelper();
		}
		cameaHelper = null;
		new Thread(new Runnable() {
			@Override
			public void run() {
				unregisterReceiver();
				mHandler.removeCallbacks(mRunnable);
				if (lowVoltageAlarm.isShown()){
					AnimationUtils.stopFlick(lowVoltageAlarm);
				}
			}
		}).start();

	}
	@Override
	protected void removeMessages() {
		mHandler.removeMessages(HandlerConstant.DEVICE_ONLINE);
	}
	public void  iotSnapShoot(View view){
		Log.d(TAG, "===iotSnapShoot===");
		eagleAction.snapshot(cameaHelper.getmCamera(), mContext,IotUtil.getSnapshotEaglePath(tutkUid));
	}
	public void iotMapDepot(View view){
		Log.d(TAG, "===iotMapDepot===");
		new Thread(jumpToGallery).start();
	}
	private Runnable jumpToGallery = new Runnable() {
		@Override
		public void run() {
			Intent mIntent = new Intent(mContext,GalleryActivity.class);
			mIntent.putExtra(Config.tutkUid, tutkUid);
			startActivity(mIntent);
		}
	};

	private Runnable startAhannel=new Runnable() {
		@Override
		public void run() {
			startPlaySurfaceView();
		}
	};
	public void startPlaySurfaceView() {
		if(messageQueue==null){
			messageQueue = new MessageQueue(this);
		}
		if (cameaHelper == null) {
			Log.i(TAG, "===首次进入猫眼===");
			cameaHelper = CameraHelper.getInstance(getIotcDevChPojo());
			cameaHelper.attach(iotcDevConnCallback);
			cameaHelper.registerstIOTCLiener();
			cameaHelper.attach(observer);
		}
		cameaHelper.register();
	}
	private IOTCDevChPojo getIotcDevChPojo(){
		iotcDevChPojo = new IOTCDevChPojo();
		iotcDevChPojo.setDevTag(Config.EAGLE);
		iotcDevChPojo.setTutkPwd(tutkPwd);
		iotcDevChPojo.setTutkUid(tutkUid);
		iotcDevChPojo.setDevConnMode(Config.EagleConnMode);
		return iotcDevChPojo;
	}
	public void iotSpeaking(View view) {
		if(cameaHelper!=null){
			speakImageAssignment(view);
			initTimingUpdateUi();
			if(view.getTag().toString().equals("open")){
				Log.i(TAG, "=====iotEagleSpeak open ====="); 
				timingUpdateUi.start();
				eagleAction.listenin(cameaHelper.getmCamera(), true);
				eagleAction.speakout(cameaHelper.getmCamera(), true);
				view.setTag("close");
				return;
			} 
				Log.i(TAG, "=====iotEagleSpeak close =====");
				timingUpdateUi.stop();
				eagleAction.listenin(cameaHelper.getmCamera(), false);
				eagleAction.speakout(cameaHelper.getmCamera(), false);
				view.setTag("open");
				return;
			}
			WLToast.showToast(mContext, getResources().getString(R.string.device_connecting), Toast.LENGTH_LONG);
	}
	public void operLockByZgbee(View view){
		Log.i(TAG, "===operLockByZgbee===");
		visibility(keyboardLinearLayout,View.VISIBLE);
	}
	public void closeKeyBoard(View view){
		Log.i(TAG, "===closeKeyBoard===");
		visibility(keyboardLinearLayout,View.GONE);
		this.clearDoorLockPwd();
	}
	private void sendDoorLockCloseOv788(){
		Intent broadcast = new Intent();
		broadcast.setAction(DoorLockReceiver.ACTION);
		broadcast.putExtra(DoorLockReceiver.DoorLockOperMode, DoorLockReceiver.CLOSEOV788);
		mContext.sendBroadcast(broadcast);
	}
	public void sendDoorLockOpen(){
		Intent broadcast = new Intent();
		broadcast.setAction(DoorLockReceiver.ACTION);
		broadcast.putExtra(DoorLockReceiver.DoorLockOperMode, DoorLockReceiver.OPENDOORFROMIOT);
		broadcast.putExtra(DoorLockReceiver.OPENDOORPWD, openDoorLockPwd);
		mContext.sendBroadcast(broadcast);
	}
	private List<String> getKeyList(){
		key = new ArrayList<>();
		for(String obj:getResources().getStringArray(R.array.keyboard)){
			key.add(obj);
		}
		return key;
	}
	private void clearDoorLockPwd(){
		openDoorLockPwd = null;
	}
	private void initKeyBoardView(){
		keyboardAdapter = new KeyboardAdapter(mContext, getKeyList());
		keyboardGridView.setAdapter(keyboardAdapter);
		keyboardGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		keyboardGridView.setOnItemClickListener(keyBoardItemListener);
	}
	private void initKeyPwdView(){
	}
	private void visibility(View view,int visibility){
		view.setVisibility(visibility);
	}
	private void checkOpenDoorLockPwd(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					if(openDoorLockPwd.length() == 6){
						sendDoorLockOpen();
						clearDoorLockPwd();
						Log.i(TAG, "= ==send open doorlock order===");
						break;
					}
				}
			}
		}).start();
	}

	/**切换录像的背景图片 add  mabo*/
	public void changeBackgroundVideo(){
		if (videoFlag) {
			doorLookRecord.setBackgroundResource(R.drawable.eagle_icon_videotape);
			videoFlag=false;
		}else {
			doorLookRecord.setBackgroundResource(R.drawable.eagle_icon_videotape_stop);
			videoFlag=true;
		}
	}
//	private Thread mThread;
	/**计时五分钟  add  mabo*/
	public void handlerTime(){
//		mThread=new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
				mHandler.postDelayed(mRunnable,  60000*5-2000);
//			}
//		});
//		mThread.start();
	}
	private Runnable mRunnable=new Runnable() {
		@Override
		public void run() {
			Message ms=mHandler.obtainMessage();
			ms.what=HandlerConstant.TIEM_IS_UP;
			mHandler.sendMessage(ms);
		}
	};
	@Override
	public void onClick(View v) {
		if (v==doorLookRecord) {
//			changeBackgroundVideo();  // 暂不支持录像， 门锁也没有做录像
			WLToast.showToast(getApplicationContext(),getResources().getString(R.string.config_not_support_device),0);
		}
	}
	private void updateUI(int index){
		if(speakDoorLook !=null){
			speakDoorLook.setImageResource(speakImages[index]);
		}
	}
	private void speakImageAssignment(View view){
		if(speakDoorLook == null){
			speakDoorLook = (ImageView) view;
		}
	}
	private void initTimingUpdateUi(){
		if(timingUpdateUi == null){
			timingUpdateUi = new TimingUpdateUi();
		}		
	}
	
	private class TimingUpdateUi{
		 public Timer timer   =  null;
		public TimingUpdateUi(){
			timer = new Timer();
			UIIndex = -1;
		}
		public void start(){
			   TimerTask task = new TimerTask() {
				@Override
				public void run() {
					if(UIIndex == 2){
						UIIndex = -1;
					}
					UIIndex++;
					mHandler.sendEmptyMessage(HandlerConstant.UPDATE_UI);
				}
				};
			    timer.schedule(task, 0, 1000);
		}
		public void stop(){
			if(timer!=null){
				timer.cancel();
				UIIndex = 3;
				mHandler.sendEmptyMessage(HandlerConstant.UPDATE_UI);
				timingUpdateUi = null;
			}
		}
	}
	/**视频已断开连接的dialog*/
	protected void initDiglog() {
		DialogManager manager = new DialogManager(PlayDoorLock_89.this);
		View view = manager.getView(DialogManager.iot_camera);
		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.dialog_view);// 加载布局
		ImageView spaceshipImage = (ImageView) view.findViewById(R.id.img);
		spaceshipImage.setVisibility(View.GONE);
		TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);// 提示文字
		spaceshipImage.setAnimation(manager
				.getAnimation(DialogManager.animation));
		tipTextView.setText(R.string.smartLock_disconnect_video_hint);
		if (layout != null) {
			manager.setCancelable(true);
			Dialog mDiglog = manager.getDialog(DialogManager.iot_dialog_style, layout);
			mDiglog.show();
		}
	}
	
	private BroadcastReceiver myNetReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			if (action.equals("DOORLOCK_IS_OPEN")) {
				boolean flag=intent.getBooleanExtra("flag", false);
				if (flag) {
					WLToast.showToast(mContext, getResources().getString(R.string.camera_door_lock_open), 0);
				}else {
					WLToast.showToast(mContext, getResources().getString(R.string.camera_door_lock_cloce), 0);
				}
			}
			if (action.equals("DOORLOCK_IS_LOW_POWER")){
				String devicesId=intent.getStringExtra("devID");
				if (devicesId.equals(deviceID)){
					lowVoltageAlarm.setVisibility(View.VISIBLE);
					AnimationUtils.startFlick(lowVoltageAlarm);//启动动画
				}
			}
			
		}
	};
	private void unregisterReceiver(){
		mContext.unregisterReceiver(myNetReceiver);
	}
	private void registerReceiver(){
	   IntentFilter mFilter = new IntentFilter();  
	   mFilter.addAction("DOORLOCK_IS_OPEN");  
	   registerReceiver(myNetReceiver, mFilter);
	}
	@SuppressLint("NewApi")
	private void addPoint(int length) {
		switch (length) {
		case 0:
			pointOne.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointTwo.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFour.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 1:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFour.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 2:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFour.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 3:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.point));
			pointFour.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 4:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.point));
			pointFour.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 5:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.point));
			pointFour.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointFive.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 6:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.point));
			pointFour.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointFive.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointSix.setBackground(getResources().getDrawable(R.drawable.point));
			break;
		default:
			break;
		}
	}
	private CreateSessionWaitThread createSessionWaitThread = null;
	private class CreateSessionWaitThread extends Thread{
		private boolean mIsRunning = true;
		public void stopThread(){
			mIsRunning = false;
		}
		@Override
		public void run() {
			mIsRunning = true;
			while (mIsRunning){
				if (cameaHelper.checkSession()) {
					cameaHelper.register();
					mIsRunning = false;
				}
			}
		}
	}
	private CreateAvChannelWaitThread createAvChannelWaitThread = null;
	private class CreateAvChannelWaitThread extends Thread{
		private boolean mIsRunning = true;
		public void stopThread(){
			mIsRunning = false;
		}
		@Override
		public void run() {
			mIsRunning = true;
			while (mIsRunning){
				if (cameaHelper.checkAvChannel()) {
					cameaHelper.register();
					mIsRunning = false;
				}
			}
		}
	}
	private void destroyWailThread(){
		if(createSessionWaitThread!=null){
			createSessionWaitThread.stopThread();
			createSessionWaitThread = null;
		}
		if(createAvChannelWaitThread!=null){
			createAvChannelWaitThread.stopThread();
			createAvChannelWaitThread = null;
		}
	}
}
