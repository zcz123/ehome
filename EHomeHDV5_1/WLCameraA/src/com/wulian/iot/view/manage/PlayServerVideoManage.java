package com.wulian.iot.view.manage;
import java.text.BreakIterator;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord;
import com.tutk.IOTC.MediaCodecMonitor;
import com.wulian.iot.bean.GalleryAlarmInfo;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.DateUtil;
import com.wulian.iot.widght.DialogRealize;
import com.yuantuo.netsdk.TKCamHelper;
public class PlayServerVideoManage {
	private static final String TAG = "PlayServerVideoManage";
	private CameraHelper  mCameraHelper = null;
	private TKCamHelper mCamera = null;
	private int mSelectedHistoryChannel = -1;//通道
	private MediaCodecMonitor mediaCodecVideoMonitor =null;
	private GalleryAlarmInfo  galleryAlarmInfo;
	public PlayServerVideoManage(CameraHelper camHelper,MediaCodecMonitor mediaCodecVideoMonitor){
		this.mCameraHelper = camHelper;
		this.mediaCodecVideoMonitor = mediaCodecVideoMonitor;
	}
	public void playServerVideo(GalleryAlarmInfo  galleryAlarmInfo){
		this.galleryAlarmInfo=galleryAlarmInfo;
		if(galleryAlarmInfo!=null&&mCameraHelper!=null&&mediaCodecVideoMonitor!=null){
			mCamera = mCameraHelper.getmCamera();
			mCameraHelper.registerstIOTCLiener();
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,SMsgAVIoctrlPlayRecord.parseContent(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START,0,galleryAlarmInfo.getTimeReceive()));//
		}
		mCameraHelper.attach(observer);
	}
	private  CameraHelper.Observer observer=new CameraHelper.Observer() {
		@Override
		public void avIOCtrlOnLine() {
			DialogRealize.unInit().dismissDialog();
		}

		@Override
		public void avIOCtrlDataSource(byte[] data, int avIOCtrlMsgType) {
			Message ms=mHandler.obtainMessage();
			ms.what=avIOCtrlMsgType;
			Bundle bundle=new Bundle();
			bundle.putByteArray("data",data);
			ms.setData(bundle);
			mHandler.sendMessage(ms);
		}

		@Override
		public void avIOCtrlMsg(int resCode, String method) {

		}
	};

	private Callback dataCallback = new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			byte[] data = bundle.getByteArray("data");
			switch(msg.what){
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP://获取历史录像通道号
				if(data.length>0){
					setmSelectedHistoryChannel(DateUtil.bytesToInt(data, 4));
					if(getmSelectedHistoryChannel()>0){
						new Thread(StartVideo).start();
//						mediaCodecVideoMonitor.cleanFrameQueue();
					}
				}
				Log.e("IOTCamera","=== 没有回来===Channel is build ("+getmSelectedHistoryChannel()+")===");
				break;
			}
			return false;
		}
	};
	private Handler mHandler = new Handler(Looper.getMainLooper(),dataCallback);
    private Runnable StartVideo = new Runnable() {
		@Override
		public void run() {
			if (mCamera != null) {
				mCamera.startPlayBack(getmSelectedHistoryChannel(), "admin","admin");
				mCamera.startPlayBackShow(getmSelectedHistoryChannel(), true, false);
			}
			if (mediaCodecVideoMonitor != null) {
				mediaCodecVideoMonitor.attachCamera(mCamera, getmSelectedHistoryChannel());
				Log.i("IOTCamera", "----mediaCodecVideoMonitor----");
			}
		}


	};
	private Runnable StopVideo = new Runnable() {
		@Override
		public void run() {
			if(getmSelectedHistoryChannel()>=0){
				if (mCamera != null) {
					mCamera.stopPlayBack(mSelectedHistoryChannel);
					mCamera.stopPlayBackShow(getmSelectedHistoryChannel());
					mCameraHelper.unregisterIOTCLiener();
					mediaCodecVideoMonitor.deattachCamera();
				}
			}
			setmSelectedHistoryChannel(-1);
			if (galleryAlarmInfo!=null)
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,SMsgAVIoctrlPlayRecord.parseContent(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP,0,galleryAlarmInfo.getTimeReceive()));
		}
	};

	public void setmSelectedHistoryChannel(int mSelectedHistoryChannel) {
		this.mSelectedHistoryChannel = mSelectedHistoryChannel;
	}

	public int getmSelectedHistoryChannel() {
		return mSelectedHistoryChannel;
	}
	public void destroy(){
		new Thread(StopVideo).start();
	}
}
