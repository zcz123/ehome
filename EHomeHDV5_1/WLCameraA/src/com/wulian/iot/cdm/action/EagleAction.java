package com.wulian.iot.cdm.action;
import com.tutk.IOTC.MediaCodecMonitor;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.netsdk.TKCamHelper;

import android.content.Context;
import android.widget.Toast;
public class EagleAction extends BaseAction {
	public EagleAction(Context context){
		super(context);
	}
	/**
	 * 语音功能
	 * @param mCamera
	 * @param flg
	 */
	public void speakout(TKCamHelper mCamera,boolean flg){
		try {
			fty.eagleFactory().speakout(mCamera, flg);
		} catch (Exception e) {
			WLToast.showToast(context, e.getMessage(),Toast.LENGTH_SHORT);
		}
	}
	public void snapshot(TKCamHelper mCamera, Context context,String folder){
		try {
			fty.eagleFactory().snapshot(mCamera, context, folder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void 	 listenin(TKCamHelper mCamera, boolean flg) {
		try {
			fty.eagleFactory().listenin(mCamera, flg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void startRecording(TKCamHelper mCamera,String fileName,boolean isHighFix){
		try {
			fty.eagleFactory().startRecording(mCamera, fileName,isHighFix);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void stopRecording(TKCamHelper mCamera){
		try {
			fty.eagleFactory().stopRecording(mCamera);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
