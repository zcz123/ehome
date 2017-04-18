package com.wulian.icam.utils;

import android.content.Context;

import com.wulian.icam.view.device.play.PlayVideoActivity;
import com.wulian.icam.view.device.play.SendDoorlockCmd;
import com.wulian.iot.cdm.UpdateCameraSet;

/**
 * Created by hxc on 2017/1/18.
 * Func:针对69、70、ow三种门锁在此统一处理
 */

public class CameraSendCmdManager {
    private static CameraSendCmdManager instance = null;
    private Context mContext;
    private String epData;
    private static UpdateCameraSet updateCameraName = null;
    private static SendDoorlockCmd sendDoorlockCmd = null;

    private CameraSendCmdManager(Context context) {
        this.mContext = context;
    }

    public static synchronized CameraSendCmdManager getInstance(Context context) {
        if (instance == null)
            instance = new CameraSendCmdManager(context);
        else {
            if (context != instance.getContext()) {
                instance.setContext(context);
            }

        }
        return instance;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public static void setSendCmd(SendDoorlockCmd sendDoorlockCmd) {
        CameraSendCmdManager.sendDoorlockCmd = sendDoorlockCmd;
    }

    public void sendDoorLockCmd(String gwId,String devId,String epType, String password) {
        if (sendDoorlockCmd != null) {
            switch (epType) {
                case "70":
                    epData =  "9" + password.length() + password;
                    sendDoorlockCmd.sendControlDevMsg(gwId,devId,epType,epData);
                    break;
                case "OW":
                    epData =  "5" + password.length() + password;
                    sendDoorlockCmd.sendControlDevMsg(gwId,devId,epType,epData);
                    break;
                case "69":
                    sendDoorlockCmd.query69Password(gwId, devId);
                    break;
                default:
                    break;
            }
        }
    }

    public void sendOpen70Cmd(String gwId,String devId){
        sendDoorlockCmd.sendOpen70DoorCmd(gwId,devId);
    }


    public void sendOpen69Cmd(String gwId,String devId){
        sendDoorlockCmd.sendOpen69DoorCmd(gwId, devId);
    }


}
