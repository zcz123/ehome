package com.wulian.iot.cdm.product;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.MediaCodecMonitor;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetPreviewModeReq;
import com.wulian.iot.utils.IotUtil;
import com.yuantuo.netsdk.TKCamHelper;


public class ConcreateDeskProduct implements I_Desk_Product {

	@Override
	public void snapshot(MediaCodecMonitor monitor, String folder, String name)throws Exception {
		if(monitor!=null){
			IotUtil.saveBitmapToJpeg(monitor.getBitmapSnap(), folder, name+ ".jpg");
			return;
			}
		     throw new Exception();
	}
	@Override
	public void snapshot(TKCamHelper mCamera,MediaCodecMonitor monitor,Context context) throws Exception {
        if(mCamera != null){
        	 Bitmap mBmp = monitor.getBitmapSnap();
        	 if(mBmp != null){
        		 IotUtil.saveSnapshot(context, mBmp);
        		 return;
        	 }
        } else {
        	throw new Exception();
        }		
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
	public void speakout(TKCamHelper mCamera, boolean flg) throws Exception {
		if(mCamera != null){
           if(flg){
        	   mCamera.startSpeaking(0);
           } else{
        	   mCamera.stopSpeaking(0);
           }		
		} else {
	     	throw new Exception();	
		}
	}

	@Override
	public void handPreviewMode(TKCamHelper mCamera, int var) throws Exception {
		if(mCamera!=null){
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_PREVIEW_MODE_REQ,
					SMsgAVIoctrlSetPreviewModeReq.parseContent(var));
		} else {
			throw new Exception();		
		}
	}

	@Override
	public void rotate(TKCamHelper mCamera, int mAVChannel, int direction)throws Exception {
		byte location = -1;
		if(mCamera!=null&&mAVChannel >= 0){
			switch(direction){
			case 0://停止
				location = (byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_STOP;
				break;
			case 1://右
				location = (byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_RIGHT;
				break;
			case 2://左
				location = (byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_LEFT;
				break;
			}
			mCamera.sendIOCtrl(mAVChannel,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
					SMsgAVIoctrlPtzCmd.parseContent(
							location,
							(byte) 0x8, (byte) 0, (byte) 0, (byte) 0,
							(byte) 0));
		}
	}

	@Override
	public void settingsBit(TKCamHelper mCamera, int OperTyep, int position)
			throws Exception {
		byte OperTyepFinal,positionFinal = -1;
		if(mCamera!=null){
			OperTyepFinal = (byte)OperTyep;
			positionFinal = (byte)position;
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
					SMsgAVIoctrlPtzCmd.parseContent(OperTyepFinal,(byte) 0x8, positionFinal, (byte) 0, (byte) 0,(byte) 0));
			}
	}
	@Override
	public void startRecording(TKCamHelper mCamera, String fileName)
			throws Exception {
		if(mCamera!=null){
    		mCamera.startRecording(0, fileName,true);
		}
	}
	@Override
	public void stopRecording(TKCamHelper mCamera) throws Exception {
		if(mCamera!=null){
    		mCamera.stopRecording(0);
		}
	}
}
