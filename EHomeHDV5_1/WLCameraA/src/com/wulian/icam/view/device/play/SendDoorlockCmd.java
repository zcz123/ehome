package com.wulian.icam.view.device.play;

import android.app.Activity;

/**
 * Created by Administrator on 2017/1/20.
 */

public interface SendDoorlockCmd {
    public void sendControlDevMsg(String gwID,String devID,String epType,String epData);
    public void sendOpen70DoorCmd(String gwID,String devID);
    public void query69Password(String gwID,String devID);
    public void sendOpen69DoorCmd(String gwID,String devID);
}
