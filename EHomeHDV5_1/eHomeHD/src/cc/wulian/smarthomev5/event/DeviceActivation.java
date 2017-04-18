package cc.wulian.smarthomev5.event;

import cc.wulian.ihome.wan.entity.DeviceInfo;

/**
 * Created by Administrator on 2017/1/12 0012.
 */

public class DeviceActivation {
    public DeviceInfo deviceInfo;
    public String deviceID;
    public DeviceActivation(String deviceID){
        this.deviceID=deviceID;
    }
    public DeviceActivation(String deviceID,DeviceInfo deviceInfo){
        this.deviceInfo=deviceInfo;
        this.deviceID=deviceID;
    }
}
