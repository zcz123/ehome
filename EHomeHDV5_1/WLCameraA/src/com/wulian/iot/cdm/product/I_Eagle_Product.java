package com.wulian.iot.cdm.product;

import android.content.Context;

import com.tutk.IOTC.MediaCodecMonitor;
import com.yuantuo.netsdk.TKCamHelper;

/**
 * 鹰眼操作接口
 * @author syf
 *
 */
public interface I_Eagle_Product {
    /**
     * 语音
     * @throws Exception
     */
    public void speakout(TKCamHelper mCamera,boolean flg)throws Exception;
    /**
     *  快照
     * @throws Exception
     */
    public void snapshot(TKCamHelper mCamera,Context context,String folder)throws Exception;

    /***
     * 双向语音
     */
    public void listenin(TKCamHelper mCamera, boolean flg)throws Exception;

    public void startRecording(TKCamHelper mCamera,String fileName,boolean isHighFix)throws Exception;
    public void stopRecording(TKCamHelper mCamera)throws Exception;
}
