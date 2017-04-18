package cc.wulian.smarthomev5.tools;

import android.content.Context;

import cc.wulian.smarthomev5.R;

/**
 * Created by Administrator on 2016/11/8.
 */

public class MotionDetectionManger {

    private static MotionDetectionManger instance = null;
    private Context mContext;

    private MotionDetectionManger(Context context) {
        this.mContext = context;
    }

    public static synchronized MotionDetectionManger getInstance(Context context) {
        if (instance == null)
            instance = new MotionDetectionManger(context);
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

    public String chooseCameraAlarmMsg(String epData) {
        String arr[] = epData.split(" ");
        String alarmStr = null;
        if (arr[0].equals("X01")) {
            switch (arr[4]) {
                case "01":
                    alarmStr = mContext.getResources().getString(com.wulian.icam.R.string.setting_detail_device_03) +
                            mContext.getResources().getString(com.wulian.icam.R.string.home_device_alarm_default_voice_detect) +
                            mContext.getResources().getString(R.string.home_device_alarm_type_05_voice);
                    break;
                case "02":
                    alarmStr = mContext.getResources().getString(com.wulian.icam.R.string.setting_detail_device_04) +
                            mContext.getResources().getString(com.wulian.icam.R.string.home_device_alarm_default_voice_detect) +
                            mContext.getResources().getString(R.string.home_device_alarm_type_05_voice);
                    break;
                case "03":
                    alarmStr = mContext.getResources().getString(com.wulian.icam.R.string.setting_detail_device_03) +
                            mContext.getResources().getString(com.wulian.icam.R.string.home_device_alarm_default_voice_detect) +
                            mContext.getResources().getString(R.string.home_device_alarm_type_05_voice);
                    break;
                case "09":
                    alarmStr = mContext.getResources().getString(com.wulian.icam.R.string.setting_detail_device_06) +
                            mContext.getResources().getString(com.wulian.icam.R.string.home_device_alarm_default_voice_detect) +
                            mContext.getResources().getString(R.string.home_device_alarm_type_05_voice);
                    break;
                default:
                    break;
            }
        }
        return alarmStr;
    }


    public String getDeviceByEpdata(String epData) {
        String arr[] = epData.split(" ");
        String name = null;
        if (arr[0].equals("X01")) {
            switch (arr[4]) {
                case "01":
                    name = mContext.getResources().getString(com.wulian.icam.R.string.setting_detail_device_03);
                    break;
                case "02":
                    name = mContext.getResources().getString(com.wulian.icam.R.string.setting_detail_device_04);
                    break;
                case "03":
                    name = mContext.getResources().getString(com.wulian.icam.R.string.setting_detail_device_03);
                    break;
                case "09":
                    name = mContext.getResources().getString(com.wulian.icam.R.string.setting_detail_device_06);
                    break;
                default:
                    break;
            }
        }
        return name;
    }
}

