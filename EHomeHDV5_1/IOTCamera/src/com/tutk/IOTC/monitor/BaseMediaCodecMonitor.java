package com.tutk.IOTC.monitor;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.yuantuo.netsdk.TKCamHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BaseMediaCodecMonitor extends SurfaceView implements  SurfaceHolder.Callback, IRegisterIOTCListener {
    protected final String TAG = getClass().getName().toString();
    protected Object mWaitObjectForStopThread = new Object();
    protected SurfaceHolder mSurHolder = null;
    protected  TKCamHelper mCamera = null;
	public BaseMediaCodecMonitor(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSurHolder = getHolder();
		mSurHolder.addCallback(this);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceCreated");
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceChanged");
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.e(TAG, "surfaceDestroyed");
	}
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
