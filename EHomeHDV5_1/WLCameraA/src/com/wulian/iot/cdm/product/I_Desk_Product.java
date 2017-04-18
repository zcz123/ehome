package com.wulian.iot.cdm.product;

import android.content.Context;
import android.graphics.Bitmap;

import com.tutk.IOTC.MediaCodecMonitor;
import com.wulian.iot.bean.tutk.AVFrameQueueRecord;
import com.yuantuo.netsdk.TKCamHelper;

/**
 * 桌面摄像机产品
 * @author syf
 *
 */
public interface I_Desk_Product {
	/**
	 * 快照
	 * @param bitmap
	 * @param folder
	 * @param name
	 * @throws Exception
	 */
	 public void snapshot(MediaCodecMonitor monitor,String folder,String name)throws Exception;
	/**
	 *  快照
	 * @throws Exception
	 */
      public void snapshot(TKCamHelper mCamera,MediaCodecMonitor monitor,Context context)throws Exception;
      /**
       * 收听
       * @param flg
       * @throws Exception
       */
      public void listenin(TKCamHelper mCamera,boolean flg)throws Exception;
      /**
       * 语音通话
       * @param mCamera
       * @param flg
       * @throws Exception
       */
      public void speakout(TKCamHelper mCamera,boolean flg)throws Exception;
      /**
       * 設置清晰度
       * @param mCamera
       * @param type
       * @throws Exception
       */
      public void handPreviewMode(TKCamHelper mCamera,int var)throws Exception;
      /**
       * 摄像机旋转
       * @param mCamera
       * @param mAVChannel
       * @param direction
       * @throws Exception
       */
      public void rotate(TKCamHelper mCamera,int mAVChannel,int direction)throws Exception;
      /**
       * 预置位 设置
       * @param mCamera
       * @param OperTyep
       * @param position
       */
      public void settingsBit(TKCamHelper mCamera,int OperTyep,int position)throws Exception;
      /***
       * 录像
       * @param mCamera
       * @param fileName
       * @throws Exception
       */
      public void startRecording(TKCamHelper mCamera,String fileName)throws Exception;
      /***
       * 停止录像
       * @param mCamera
       * @throws Exception
       */
      public void stopRecording(TKCamHelper mCamera)throws Exception;
}
