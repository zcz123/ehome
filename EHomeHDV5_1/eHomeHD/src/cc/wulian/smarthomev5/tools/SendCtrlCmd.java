package cc.wulian.smarthomev5.tools;

import com.wulian.icam.view.device.play.SendDoorlockCmd;

import cc.wulian.ihome.wan.NetSDK;

/**
 * Created by Administrator on 2017/1/20.
 * func:摄像机直播界面开锁或者查询命令的发送
 */

public class SendCtrlCmd implements SendDoorlockCmd {

        @Override
    public void sendControlDevMsg(String gwID, String devID,String epType,String epData) {
        NetSDK.sendControlDevMsg(gwID, devID, "14", epType, epData);
    }

    @Override
    public void sendOpen70DoorCmd(String gwID, String devID) {
        NetSDK.sendControlDevMsg(gwID, devID, "14", "70", "11");
    }
    @Override
    public void query69Password(String gwID, String devID){
        NetSDK.sendCommonDeviceConfigMsg(gwID, devID, "3", null, "lock_pass", null);
    }

    @Override
    public void sendOpen69DoorCmd(String gwID, String devID) {
        NetSDK.sendControlDevMsg(gwID, devID, "14", "70", "1");
    }
}
