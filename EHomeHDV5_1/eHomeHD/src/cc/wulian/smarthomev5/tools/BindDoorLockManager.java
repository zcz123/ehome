package cc.wulian.smarthomev5.tools;

import android.content.Context;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.smarthomev5.R;

/**
 * Created by hxc on 2017/1/17.
 */

public class BindDoorLockManager {
    private static BindDoorLockManager instance = null;
    private Context mContext;
    private String deviceName;
    private boolean isSuccess = false;

    private BindDoorLockManager(Context context) {
        this.mContext = context;
    }

    public static synchronized BindDoorLockManager getInstance(Context context) {
        if (instance == null)
            instance = new BindDoorLockManager(context);
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

    //名字为空时显示默认的门锁名称
    public String getDefaultDevName(String epType) {
        switch (epType) {
            case "69":
                deviceName = mContext.getResources().getString(R.string.device_type_69);
                break;
            case "70":
                deviceName = mContext.getResources().getString(R.string.device_type_70);
                break;
            case "OW":
                deviceName = mContext.getResources().getString(R.string.device_type_69);
                break;
            default:
                break;
        }
        return deviceName;
    }

    //验证密码并且绑定门锁
    public void verifyAdminPassword(String gwId, String devId, String epType, String password) {
        switch (epType) {
            case "69":
                NetSDK.sendCommonDeviceConfigMsg(gwId, devId, "3", null, "lock_pass", null);
                break;
            case "70":
                SendMessage.sendControlDevMsg(gwId, devId, "14",
                        "70", 9 + "" + password.length() + password);
                break;
            case "OW":
                SendMessage.sendControlDevMsg(gwId, devId, "14",
                        "OW", 2 + "" + password.length() + password);
                break;
            default:
                break;
        }

    }

    public boolean checkBindResult(String epData, String epType) {
        switch (epType) {
            case "70":
                if (epData.equals("144")) {
                    isSuccess = true;
                } else if (epData.equals("145")) {
                    isSuccess = false;
                }
                break;
            case "OW":
                if (epData.equals("0220")) {
                    isSuccess = true;
                } else if (epData.equals("0810")) {
                    isSuccess = false;
                }
                break;
            default:break;
        }
        return isSuccess;
    }

}
