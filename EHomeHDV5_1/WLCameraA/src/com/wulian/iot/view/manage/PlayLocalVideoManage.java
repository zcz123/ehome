package com.wulian.iot.view.manage;
import java.io.File;
import com.tutk.IOTC.MediaCodecMonitor;
import com.wulian.iot.HandlerConstant;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PlayLocalVideoManage implements OnSeekBarChangeListener,Callback{
	private final static String TAG = "PlayLocalVideoManage";
	private MediaPlayer mediaPlayer = null;
	private SeekBarThread seekBarThread = null;
	private MediaCodecMonitor mediaCodecVideoMonitor = null;
	private int currentPosition = 0;
	private SeekBar mSeekBar;
	public PlayLocalVideoManage(){
		
	}
	public PlayLocalVideoManage(SeekBar seekBar,MediaCodecMonitor mediaCodecVideoMonitor){
		this.mSeekBar = seekBar;
		this.mediaCodecVideoMonitor = mediaCodecVideoMonitor;
		initView();
	}
	private void initView(){
		addSurfaseCallBack();
	}
	private void addSurfaseCallBack() {
		mediaCodecVideoMonitor.getHolder().addCallback(this);
	}
	public void playLocalVideo(String fileName,Handler dataHand){
		if(fileName == null){
			dataHand.sendEmptyMessage(HandlerConstant.ERROR);
			return;
		}
		File file = new File(fileName);
		if (!file.exists()) {
			dataHand.sendEmptyMessage(HandlerConstant.ERROR);
			return;
		}
		try{
			if(mediaPlayer == null){
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDisplay(mediaCodecVideoMonitor.getHolder());
				mediaPlayer.setDataSource(file.getAbsolutePath());
				mediaPlayer.prepareAsync();
			}
			if(seekBarThread == null){
				seekBarThread = new SeekBarThread();
			}
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
					mediaPlayer.seekTo(0);
					mSeekBar.setMax(mediaPlayer.getDuration());
					seekBarThread.start();
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					Log.i(TAG, "setOnCompletionListener");
					mSeekBar.setProgress(mediaPlayer.getDuration());
					release();
				}
			});
		}catch(Exception ex){
			
		}
	}
	private void release(){
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		mediaPlayer = null;
		if(seekBarThread!=null){
			seekBarThread.stopThread();
		}
		seekBarThread = null;
	}
	private class SeekBarThread extends Thread{
		private boolean mIsRunning = false;
		public void stopThread(){
			setmIsRunning(false);
		}
		public void setmIsRunning(boolean mIsRunning) {
			this.mIsRunning = mIsRunning;
		}
		public boolean ismIsRunning() {
			return mIsRunning;
		}
		public void sleepThread(){
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				return;
			}
		}
		@Override
		public void run() {
			setmIsRunning(true);
			while(ismIsRunning()){
				int current = mediaPlayer.getCurrentPosition();
				mSeekBar.setProgress(current>mediaPlayer.getDuration()?mediaPlayer.getDuration():current);
				sleepThread();
			}
		}
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.i(TAG, "onProgressChanged");
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.i(TAG, "onStopTrackingTouch");
		int progress = seekBar.getProgress();
		if(mediaPlayer!=null){
			mediaPlayer.seekTo(progress);
		}
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated");
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			currentPosition = mediaPlayer.getCurrentPosition();
			mediaPlayer.stop();
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i(TAG, "surfaceChanged");
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");
		if (mediaPlayer != null) {
			currentPosition = mediaPlayer.getCurrentPosition();
			mediaPlayer.stop();
		}
	} 
}
