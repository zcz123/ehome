package cc.wulian.smarthomev5.event;

/**
 * Created by yuxiaoxuan on 17/1/11.
 */

public class WifiJionNetworkEvent {
    String gwID;
    String appID;
    String devType;
    String typeID;
    String opt;
    String mode;
    String data;
    public WifiJionNetworkEvent(String gwID, String appID,String devType,String typeID, String opt, String mode,String data){
        this.gwID=gwID;
        this.appID=appID;
        this.devType=devType;
        this.typeID=typeID;
        this.opt=opt;
        this.mode=mode;
        this.data=data;
    }

    public String getData(){
        return data;
    }
}
