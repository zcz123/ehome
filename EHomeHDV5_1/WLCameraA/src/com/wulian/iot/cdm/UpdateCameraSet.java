package com.wulian.iot.cdm;

import android.app.Activity;
import android.os.Handler;

/**
 * Created by Administrator on 2016/11/11 0011.
 */

public interface UpdateCameraSet {
    public  void deviceUpdate(String deviceId, String common, Handler mHandler) ;

    public void deleteEageleCamera(String deviceId,boolean isAdmin,Handler mHandler);

    public void startShareActivity(String deviceID, Activity activity);

}
