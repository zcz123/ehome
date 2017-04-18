package com.wulian.iot.view.device.play;
import java.io.File;
import java.text.BreakIterator;

import android.content.Intent;
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
import com.tutk.IOTC.MediaCodecMonitor;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.GalleryAlarmInfo;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.view.manage.PlayLocalVideoManage;
import com.wulian.iot.view.manage.PlayServerVideoManage;
import com.wulian.iot.widght.DialogRealize;

public class PlayEagleVideoAvtivity extends SimpleFragmentActivity implements OnClickListener,Handler.Callback{
	private MediaCodecMonitor mediaCodecVideoMonitor;
	private ImageView mback,startPlay,startPauseButton,deleteVideo;// 返回, 开始播放（中间的） ,播放（播放控制上的）,删除
	private TextView titleText;
	private SeekBar progress = null; //播放进度条
	private MediaPlayer mediaPlayer = null;
	private LinearLayout layoutTitle,videoControl;
	private Handler mHandler = new Handler(this);
	private String localPath = null;
	private GalleryAlarmInfo galleryAlarmInfo = null;
	private boolean isLocalOrServer;
	private int mSelectedHistoryChannel = -1;//通道
	private int currentPosition = 0;
	private PlayLocalVideoManage playLocalVideoManage = null;
	private PlayServerVideoManage playServerVideoManage = null;
	int a=0;
	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
			case HandlerConstant.ERROR:
				Log.e(TAG, "录像播放失败");
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
			playLocalVideoManage = new PlayLocalVideoManage(progress,mediaCodecVideoMonitor);
			Log.e(TAG, "本地视频");
			Log.e(TAG, localPath);
		} else if(playVideo.equals("server")) {//服务端视频
			isLocalOrServer = false;
			galleryAlarmInfo= (GalleryAlarmInfo)dataIntent.getSerializableExtra("videoPath");
			if(galleryAlarmInfo!=null&&cameaHelper!=null){
				 playServerVideoManage = new PlayServerVideoManage(cameaHelper,mediaCodecVideoMonitor);
			}
			Log.e(TAG, "服务端视频");
		}
	}
	@Override
	public void initEvents() {
		mback.setOnClickListener(this);
		startPlay.setOnClickListener(this);
		startPauseButton.setOnClickListener(this);
		titleText.setText(galleryAlarmInfo.getTitle());
		mediaCodecVideoMonitor.setOnClickListener(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(!isLocalOrServer){
			Log.e(TAG, "===is server video===");
			if (cameaHelper!=null) {
				Log.e(TAG, "===CameraHelp is not null===");
				cameaHelper.registerstIOTCLiener();
			}
		}
		startTheVideo();
		DialogRealize.init(this).showDiglog();
	}
	@Override
	public void onClick(View v) {
		if (v==mback) {
			finish();
		}else if (v==startPlay) {
//			hideControls();
//			startTheVideo();
		}else if (v==mediaCodecVideoMonitor) {
		}
	}
	private final void  startTheVideo(){
		if(isLocalOrServer){
			playLocalVideoManage.playLocalVideo(localPath, mHandler);
		} else {
			playServerVideoManage.playServerVideo(galleryAlarmInfo);
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
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(playServerVideoManage!=null){
			playServerVideoManage.destroy();
		}
	}
}
