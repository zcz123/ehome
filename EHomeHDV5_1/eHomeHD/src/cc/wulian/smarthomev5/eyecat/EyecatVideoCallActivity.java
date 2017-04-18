package cc.wulian.smarthomev5.eyecat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.ELog;
import com.eques.icvss.utils.Method;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import cc.wulian.smarthomev5.R;

public class EyecatVideoCallActivity extends Activity {
	private final String TAG = "EyecatVideoCallActivity";
	private SurfaceView surfaceView;
	private String callId;
	private int currVolume;
	private int devType = 0;
	private int current;
	private boolean isMuteFlag;
	private boolean hasVideo;
	
	private AudioManager audioManager;
	private LinearLayout linear_padding;
	
	private Button btnCapture, btnMute, btnHangupCall, btnSoundSwitch;
	
	int width = 640;
	int height = 480;
	
	private int screenWidthDip;
	private int screenHeightDip;
	private String uid ;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		setContentView(R.layout.eyecat_activity_videomain);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
		
		currVolume = current;
		uid = getIntent().getStringExtra(Method.ATTR_BUDDY_UID);
		Log.d("uid",uid);
		hasVideo = getIntent().getBooleanExtra(Method.ATTR_CALL_HASVIDEO, false);


		initUI();
		
		boolean bo = audioManager.isWiredHeadsetOn();
		if(!bo){
			openSpeaker();
		}
	}

	private void initUI() {
		surfaceView = (SurfaceView) findViewById(R.id.surface_view);
		
		btnCapture = (Button) findViewById(R.id.btn_capture);
		btnCapture.setOnClickListener(new MyOnClickListener());
		
		btnMute = (Button) findViewById(R.id.btn_mute);
		btnMute.setOnClickListener(new MyOnClickListener());
		
		btnHangupCall = (Button) findViewById(R.id.btn_hangupCall);
		btnHangupCall.setOnClickListener(new MyOnClickListener());
		
		btnSoundSwitch = (Button) findViewById(R.id.btn_soundSwitch);
		btnSoundSwitch.setOnTouchListener(new MyOnTouchListener());
		
		linear_padding = (LinearLayout) findViewById(R.id.linear_padding);
		RelativeLayout relative_videocall = (RelativeLayout) findViewById(R.id.relative_videocall);
		relative_videocall.setOnClickListener(new MyOnClickListener());
		
		surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
			
			public void surfaceChanged(SurfaceHolder holder, int arg1,
                                       int arg2, int arg3) {
			}
			
			public void surfaceCreated(SurfaceHolder holder) {
				
				if(hasVideo){ //是否显示视频
				    callId = EyecatManager.getInstance().getICVSSUserInstance().equesOpenCall(uid, holder.getSurface()); //视频 + 语音通话
				}else{
				    callId = EyecatManager.getInstance().getICVSSUserInstance().equesOpenCall(uid, surfaceView, null); //语音通话
				}
			}
			
			public void surfaceDestroyed(SurfaceHolder holder) {
			}
		});
		
		setVideoSize();
		
		LayoutParams layoutParams;
		if(devType == BuddyType.TYPE_CAMERA_C01){
			layoutParams = new LayoutParams(screenWidthDip, (screenWidthDip / 5));
		}else{
			layoutParams = new LayoutParams(screenWidthDip, (screenWidthDip / 7));
		}
		linear_padding.setLayoutParams(layoutParams);
	}
	
	private void callSpeakerSetting(boolean f) {
		if (f) {
			btnSoundSwitch.setText("松开 结束");
			if (callId != null) {
				EyecatManager.getInstance().getICVSSUserInstance().equesAudioRecordEnable(true, callId);
				EyecatManager.getInstance().getICVSSUserInstance().equesAudioPlayEnable(false, callId);
			}
			closeSpeaker();
		} else {
			btnSoundSwitch.setText("按住 说话");

			if (callId != null) {
				EyecatManager.getInstance().getICVSSUserInstance().equesAudioPlayEnable(true, callId);
				EyecatManager.getInstance().getICVSSUserInstance().equesAudioRecordEnable(false, callId);
			}
			openSpeaker();
		}
	}
	
	private void setVideoSize(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidthDip = dm.widthPixels;
		screenHeightDip = dm.heightPixels;
		
		if(screenWidthDip == 1812){
			screenWidthDip = 1920;
		}
		setAudioMute(); //设置是否静音
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			surfaceView.getHolder().setFixedSize(screenWidthDip, screenHeightDip);
		} else {
			getVerticalPixel();
		}
	}

	private void getVerticalPixel() {
		int verticalHeight;
		
		if(devType == BuddyType.TYPE_CAMERA_C01){
			verticalHeight = (screenWidthDip * 9) / 16;
		
		}else{
			verticalHeight = (screenWidthDip * 3) / 4;
		}
		surfaceView.getHolder().setFixedSize(screenWidthDip, verticalHeight);
	}

	long waitTime = 5000;  
	long touchTime = 0;
	
	public String format(int i) {
		String s = i + "";
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}
	


	
	public void onBackPressed() {
		hangUpCall();
	}
	
	
	protected void onPause() {
		super.onPause();
		hangUpCall();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);

		closeSpeaker();
	}

	private class MyOnTouchListener implements OnTouchListener {
		
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				callSpeakerSetting(true);
				break;
				
			case MotionEvent.ACTION_UP:
				callSpeakerSetting(false);	
				break;
			}
			return true;
		}
	}
	
	class MyOnClickListener implements OnClickListener {
		
		public void onClick(View view) {
			
			switch (view.getId()) {
			case R.id.btn_hangupCall:
				hangUpCall();
				break;
				
			case R.id.btn_capture:
				String path = getCamPath();
				
				boolean isCreateOk = createDirectory(path);
				if(isCreateOk){
					path = StringUtils.join(path, "test", ".jpg");
					
					if(devType == BuddyType.TYPE_CAMERA_C01){
						height = 360;
					}
					EyecatManager.getInstance().getICVSSUserInstance().equesSnapCapture(BuddyType.TYPE_WIFI_DOOR_R22, path);
					ELog.showToastLong(EyecatVideoCallActivity.this, "截图成功");
				}else{
					ELog.showToastLong(EyecatVideoCallActivity.this, "截图失败");
				}
				break;
				
			case R.id.btn_mute:
				if(callId != null){
					isMuteFlag = !isMuteFlag;
					
					setAudioMute();//设置静音
				}
				break;

			default:
				break;
			}
		}
	}
	
	
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	    case KeyEvent.KEYCODE_VOLUME_UP:
	    	audioManager.adjustStreamVolume(
	            AudioManager.STREAM_MUSIC ,
	            AudioManager.ADJUST_RAISE,
	            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
	        return true;
	    case KeyEvent.KEYCODE_VOLUME_DOWN:
	    	audioManager.adjustStreamVolume(
	            AudioManager.STREAM_MUSIC ,
	            AudioManager.ADJUST_LOWER,
	            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
	        return true;
	    default:
	        break;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	public void openSpeaker() {
		try {
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (!audioManager.isSpeakerphoneOn()) {
				audioManager.setSpeakerphoneOn(true);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVolume,
								0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeSpeaker(){
		try {
			if (audioManager != null) {
				if (audioManager.isSpeakerphoneOn()) {
					currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					audioManager.setSpeakerphoneOn(false);
					audioManager.setStreamVolume(
							AudioManager.STREAM_MUSIC, currVolume,
							0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setAudioMute(){
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, isMuteFlag);
		
		if(isMuteFlag){
			if(callId != null){
				EyecatManager.getInstance().getICVSSUserInstance().equesAudioPlayEnable(false, callId);
				EyecatManager.getInstance().getICVSSUserInstance().equesAudioRecordEnable(false, callId);
			}
			
			btnMute.setText("静音模式");
			
		}else{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
			callSpeakerSetting(false);
			
			btnMute.setText("外放模式");
		}
	}
	
	private void hangUpCall(){
		if (callId != null) {
			EyecatManager.getInstance().getICVSSUserInstance().equesCloseCall(callId);
		}
		finish();
	}
		
	public boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
	
	public String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/";
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/";
		}
	}
	
	public String getCamPath() {
		String rootPath = getRootFilePath();
		String camPicPath = rootPath + "DingDong" + File.separator;
		return camPicPath;
	}
	
	public boolean createDirectory(String filePath) {
		if (null == filePath) {
			return false;
		}
		File file = new File(filePath);
		if (file.exists()) {
			return true;
		}
		return file.mkdirs();

	}
	
	
}
