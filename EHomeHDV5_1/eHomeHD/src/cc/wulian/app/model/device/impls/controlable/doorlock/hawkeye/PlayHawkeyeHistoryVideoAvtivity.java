package cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye;

import com.wulian.iot.view.base.SimpleFragmentActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_89_DoorLock_6;

import java.io.File;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.MediaCodecMonitor;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.GalleryAlarmInfo;
import com.wulian.iot.utils.DateUtil;
import com.yuantuo.netsdk.TKCamHelper;
public class  PlayHawkeyeHistoryVideoAvtivity extends SimpleFragmentActivity implements OnClickListener,Handler.Callback, IRegisterIOTCListener{
	private MediaCodecMonitor mediaCodecVideoMonitor;
	private ImageView mback,startPlay,startPauseButton,deleteVideo;// 返回, 开始播放（中间的） ,播放（播放控制上的）,删除
	private TextView titleText;
	private SeekBar progress = null; //播放进度条
	private MediaPlayer mediaPlayer = null;
	private LinearLayout layoutTitle,videoControl;
	//private TKCamHelper mCamera;
	private Handler mHandler = new Handler(this);
	private String localPath = null;
	private GalleryAlarmInfo galleryAlarmInfo = null;
	private boolean isLocalOrServer;
	protected String TAG = "IOTCamera";
	
	private int mSelectedHistoryChannel = -1;
	private int command = -1; //TUTK命令返回的参数判断
	//不能在这里讲值写死，应该通过首选项动态的获取。
	
	@Override
	public boolean handleMessage(Message msg) {			
		Bundle bundle = msg.getData();
		//int avChannel = bundle.getInt("avChannel");
		byte[] data = bundle.getByteArray("data");

		// throw exception
		
		switch (msg.what) {
		// TODO	
		case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP:					  
            //开始播放视频了。
			// 
			// 开始接收视频数据了。		
			 //可以先不要设置这个函数。
			 // mCamera.setAEC(true);			 
			 //开始回放流。
			mSelectedHistoryChannel =  DateUtil.bytesToInt(data, 4);
			Log.i("IOTCamera", "------------mSelectedHistoryChannel"+mSelectedHistoryChannel);
			command = DateUtil.bytesToInt(data, 0); 
			Log.e("IOTCamera", "------------command"+command); 
			if (command == AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START) {
				if (data.length > 6 ) 
				  {	
					  
					  if (mSelectedHistoryChannel < 0) 
					  {					
						  Log.e("IOTCamera", "---------mSelectedHistoryChannel=data[0]------mSelectedHistoryChannel=data[0]"+mSelectedHistoryChannel);				  
						  break;
					   } 
					  
						  
					  WL_89_DoorLock_6.cHelperHawkeye.startPlayBack(mSelectedHistoryChannel, "admin", "admin");       
					  WL_89_DoorLock_6.cHelperHawkeye.startPlayBackShow(mSelectedHistoryChannel, true, false);
						  
						 Log.e("IOTCamera", "---------mCamera.startPlayBack---startPlayBackShow");
						 mediaCodecVideoMonitor.cleanFrameQueue();
					  
			     }
			}
			  
			break;
		case HandlerConstant.ERROR:
			Log.e(TAG, "录像播放失败");
			break;
		default:
			break;
		}
		
		
		
		
		
		
		
		
		
		
		return false;
	}
	@Override
	public void root() {
		setContentView(R.layout.activity_play_human_video);
	}
	@Override
	public void initView() {
		
		
		if (mediaCodecVideoMonitor != null) {
			mediaCodecVideoMonitor.deattachCamera();
			mediaCodecVideoMonitor = null;
		}
		
		mediaCodecVideoMonitor=(MediaCodecMonitor) findViewById(R.id.media_human_video);
		mback=(ImageView) findViewById(R.id.iv_cateye_titlebar_back);
		titleText=(TextView) findViewById(R.id.tv_cateye_titlebar_title);
		startPlay=(ImageView) findViewById(R.id.iv_start_video_play);
		startPauseButton=(ImageView) findViewById(R.id.iv_startPauseButton);
		progress=(SeekBar) findViewById(R.id.sb_seekBar);
		videoControl=(LinearLayout) findViewById(R.id.ll_video_control);
		layoutTitle=(LinearLayout) findViewById(R.id.ll_video_title);
		deleteVideo=(ImageView) findViewById(R.id.iv_delete_human_video);//删除video 需要在播放完毕后显示
	}
	@Override
	public void initData() {
		Intent dataIntent = getIntent();
		String playVideo = dataIntent.getStringExtra(Config.playEagleVideoTyep);
		if(playVideo.equals("localhost")){//本地视频
			localPath = dataIntent.getStringExtra("videoPath");
			isLocalOrServer = true;
			Log.e(TAG, "本地视频");
			Log.e(TAG, localPath);
		} else if(playVideo.equals("server")) {//服务端视频
			isLocalOrServer = false;
			galleryAlarmInfo= (GalleryAlarmInfo)dataIntent.getSerializableExtra("videoPath");
			Log.e(TAG, "服务端视频");
		}
		
		
		if (WL_89_DoorLock_6.cHelperHawkeye != null) {
			
			
		
			
		}else{
			
			Log.e(TAG, "----回放播放页面获取不到camera对象");
			return;
		}
		

		
		if (mediaCodecVideoMonitor != null) {

			mediaCodecVideoMonitor.attachCamera(WL_89_DoorLock_6.cHelperHawkeye, mSelectedHistoryChannel);

		}
		
	}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
	@Override
	public void initEvents() {
		mback.setOnClickListener(this);
		startPlay.setOnClickListener(this);
		startPauseButton.setOnClickListener(this);
		titleText.setText("录像编号");
		mediaCodecVideoMonitor.setOnClickListener(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		startPlayVideo();
		
	}
	@Override
	public void onClick(View v) {
		if (v==mback) {
			finish();
		}else if (v==startPlay) {
			hideControls();
			startTheVideo();
		}else if (v==mediaCodecVideoMonitor) {
		}
	}
	private final void  startTheVideo(){
		if(isLocalOrServer){
			playVideo(localPath);//本地视频
		} else {
			playVideo(galleryAlarmInfo);//服务端视频
		}
	}
	/*** 隐藏控件 */
	private void hideControls(){
		if (startPlay.getVisibility()==View.VISIBLE) {
			startPlay.setVisibility(View.GONE);
			startPauseButton.setImageResource(R.drawable.pause_videotape2);
		}
	}
	/*** 显示控件*/
	private void showControls(){
		startPlay.setVisibility(View.VISIBLE);
		layoutTitle.setVisibility(View.VISIBLE);
		videoControl.setVisibility(View.VISIBLE);
	}
	private void playVideo(String path){
		if (path == null) {
			return;
		}
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		try {
			if (mediaPlayer == null) {
				Log.e(TAG, "mediaPlayer");
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDisplay(mediaCodecVideoMonitor.getHolder());
				mediaPlayer.setDataSource(file.getAbsolutePath());
			}
			mediaPlayer.prepareAsync();
			if(progressThread==null){
				Log.e(TAG, "ProgressThread");
				progressThread = new ProgressThread();
				progressThread.start();
			}
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					Log.e(TAG, "onPrepared");
					mediaPlayer.start();
					progress.setMax(mediaPlayer.getDuration());
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					progress.setProgress(mediaPlayer.getDuration());
					Log.e("IOTCamera", "-------------onCompletion");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(HandlerConstant.ERROR);
		}
	}
	private void playVideo(GalleryAlarmInfo galleryAlarmInfo){
		if(galleryAlarmInfo !=null){
			
			Log.e("IOTCamera", "-------------year"+galleryAlarmInfo.getYear());
			//byte[] temp =  galleryAlarmInfo.getTimeAck();
//			for (int i = 0; i <temp.length; i++) {
//				Log.e("IOTCamera", "-------------shifenmiao"+temp[i]);
//			}
			
//			byte[] startTimeAck =new byte[]{0x07,0xc,0x00,0xc,0xe,0x0a};            
//			short year = 2016;
			WL_89_DoorLock_6.cHelperHawkeye.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL, SMsgAVIoctrlPlayRecord.parseContent(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START,12,galleryAlarmInfo.getYear(), galleryAlarmInfo.getTimeAck()));						
		
		}
	}
	private void stopVideo(GalleryAlarmInfo galleryAlarmInfo){
		if(galleryAlarmInfo !=null){
			Log.i(TAG, "---------停止播放");
			
			byte[] startTimeAck =new byte[]{0x07,0xc,0x00,0xc,0xe,0x0a};            
			short year = 2016;
			WL_89_DoorLock_6.cHelperHawkeye.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL, SMsgAVIoctrlPlayRecord.parseContent(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_PAUSE,year, startTimeAck));						
			
			WL_89_DoorLock_6.cHelperHawkeye.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL, SMsgAVIoctrlPlayRecord.parseContent(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP,year, startTimeAck));						
		
		}
	}
	private ProgressThread progressThread = null;
	private final class ProgressThread extends Thread{
		private boolean isPlaying = false;
		public void stopThread(){
			isPlaying = false;
		}
		@Override
		public void run() {
			isPlaying = true;
			while(isPlaying){
				int current = mediaPlayer.getCurrentPosition();
				progress.setProgress(current>mediaPlayer.getDuration()?mediaPlayer.getDuration():current);
				try{
					Thread.sleep(300);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopVideo(galleryAlarmInfo);
		stopPlaySurfaceView();
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		undefined();
	}

	private void startPlayVideo() {

		if (WL_89_DoorLock_6.cHelperHawkeye == null)
			return;
		
		WL_89_DoorLock_6.cHelperHawkeye.registerIOTCListener(this);

		
		

	}
	private void stopPlaySurfaceView() {
		if (WL_89_DoorLock_6.cHelperHawkeye == null)
			return;

		
		
		if (mediaCodecVideoMonitor != null) {
			mediaCodecVideoMonitor.deattachCamera();	
		}

		
		
		WL_89_DoorLock_6.cHelperHawkeye.unregisterIOTCListener(this);

		WL_89_DoorLock_6.cHelperHawkeye.stopSpeaking(mSelectedHistoryChannel);
		WL_89_DoorLock_6.cHelperHawkeye.stopListening(mSelectedHistoryChannel);

		// stopShow
		WL_89_DoorLock_6.cHelperHawkeye.stopPlayBackShow(mSelectedHistoryChannel);

		// disconnect camera made by guofeng 这两项本来是打开的，我给他关闭了
		// mCamera.stop(Camera.DEFAULT_AV_CHANNEL);
		// mCamera.disconnect();
	}
	
	
	private final void undefined(){
		if(mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if(progressThread != null){
			progressThread.stopThread();
			progressThread = null;
		}
		
	}
	
	
	
	
	


	
	
	
	
	
	
	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate, int frameRate, int onlineNm,
			int frameCount, int incompleteFrameCount) {
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
	public void receiveIOCtrlData(Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub
		
		if (WL_89_DoorLock_6.cHelperHawkeye == null)
			return;
		if (WL_89_DoorLock_6.cHelperHawkeye == camera) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);
			bundle.putByteArray("data", data);

			Message msg = mHandler.obtainMessage();
			msg.what = avIOCtrlMsgType;
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
		
	}
	@Override
	public void receiveFrameDataForMediaCodec(Camera camera, int i, byte[] abyte0, int j, int k, byte[] abyte1,
			boolean flag, int l) {
		// TODO Auto-generated method stub
		
	}
}