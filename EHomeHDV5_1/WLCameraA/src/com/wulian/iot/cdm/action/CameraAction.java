package com.wulian.iot.cdm.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import com.tutk.IOTC.MediaCodecMonitor;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.netsdk.TKCamHelper;

public class CameraAction extends BaseAction{
	
	public CameraAction(Context context){
		super(context);
	}
	
	public void listenin(TKCamHelper mCamera, boolean flg){
		try {
			fty.deskFactory().listenin(mCamera, flg);
		} catch (Exception e) {
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
	public void snapshot(TKCamHelper mCamera,MediaCodecMonitor monitor){
		try {
			fty.deskFactory().snapshot(mCamera, monitor, context);
		} catch (Exception e) {
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
	public void snapshot(MediaCodecMonitor monitor,String folder,String name){
		try {
			fty.deskFactory().snapshot(monitor, folder, name);
		} catch (Exception e) {
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
	public void speakout(TKCamHelper mCamera,boolean flg){
	       try {
			fty.deskFactory().speakout(mCamera, flg);
		} catch (Exception e) {
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
	public void handPreviewMode(TKCamHelper mCamera,int var){
		try {
			fty.deskFactory().handPreviewMode(mCamera, var);
		} catch (Exception e) {
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
	public void rotate(TKCamHelper mCamera, int mAVChannel, int direction){
		try {
			fty.deskFactory().rotate(mCamera, mAVChannel, direction);
		} catch (Exception e) {
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
	public void settingsBit(TKCamHelper mCamera,int OperTyep, int position){
		try{
			fty.deskFactory().settingsBit(mCamera, OperTyep, position);
		}catch(Exception e){
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
	public void startRecording(TKCamHelper mCamera,String fileName){
		try {
			fty.deskFactory().startRecording(mCamera, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
	public void stopRecording(TKCamHelper mCamera){
		try {
			fty.deskFactory().stopRecording(mCamera);
		} catch (Exception e) {
			e.printStackTrace();
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
}
