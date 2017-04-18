package com.wulian.iot.cdm.product;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tutk.IOTC.MediaCodecMonitor;
import com.wulian.iot.utils.EagleUtil;
import com.wulian.iot.utils.IotUtil;
import com.yuantuo.netsdk.TKCamHelper;

public class ConcreateEagleProduct implements I_Eagle_Product{
	private final static String TAG = "ConcreateEagleProduct";
	@Override
	public void speakout(TKCamHelper mCamera,boolean flg) throws Exception {
		if(mCamera!=null){
			if(flg){
				mCamera.startSpeaking(0);
			} else {
				mCamera.stopSpeaking(0);
			}
		}
	}
	@Override
	public void snapshot(TKCamHelper mCamera, Context context,
						 String folder) throws Exception {
		if(mCamera!=null){
			Bitmap mBmp = mCamera.Snapshot(0);
			IotUtil.saveSnapshot(context,EagleUtil.rotateBitmap(mBmp,-90),folder);
			return;
		}
		throw new Exception();
	}
	@Override
	public void listenin(TKCamHelper mCamera, boolean flg) throws Exception {
		if(mCamera!=null){
			if(flg){
				mCamera.startListening(0, false);
			} else {
				mCamera.stopListening(0);
			}
		} else{
			throw new Exception();
		}
	}

	@Override
	public void startRecording(TKCamHelper mCamera, String fileName,boolean isHighFix)
			throws Exception {
		if(mCamera!=null){
			mCamera.startRecording(0, fileName,isHighFix);
			return;
		}
		throw new Exception();
	}

	@Override
	public void stopRecording(TKCamHelper mCamera) throws Exception {
		if(mCamera!=null){
			mCamera.stopRecording(0);
			return;
		}
		throw new Exception();
	}
}
