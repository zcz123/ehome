package com.wulian.iot.utils;

import android.content.Context;

import com.tutk.IOTC.Camera;
import com.wulian.icam.R;

/**
 * Created by syf on 2016/10/18.
 */

public class IOTResCodeUtil {
    public static String transformStr(int resCode, Context mContext) {
        switch (resCode) {
            case Camera.CONNECTION_NOT_INITIALIZED:
                return mContext.getResources().getString(R.string.iot_not_initialize);
            case Camera.CONNECTION_STATE_IOTC_WAKE_UP://唤醒
                return mContext.getResources().getString(R.string.eagle_wake_up);
            case Camera.CONNECTION_ER_DEVICE_OFFLINE://设备不在线
                return mContext.getResources().getString(R.string.html_map_2107_error);
            case Camera.CONNECTION_STATE_CONNECTED://连接成功
                return mContext.getResources().getString(R.string.eagle_link_channel);
            case Camera.CONNECTION_EXCEED_MAX_SESSION://会话超出限制
                return mContext.getResources().getString(R.string.eagle_deivce_max_session);
            case Camera.CONNECTION_DEVICE_NOT_LISTENING://设备连接异常
                return mContext.getResources().getString(R.string.eagle_link_error);
            case Camera.CONNECTION_STATE_TIMEOUT:
            case Camera.CONNECTION_STATE_IOTC_INVALID_SID:
            case Camera.CONNECTION_STATE_IOTC_SESSION_CLOSE_BY_REMOTE:
            case Camera.CONNECTION_STATE_CONNECT_FAILED:
                return mContext.getResources().getString(R.string.ioc_session_null);
            case Camera.CONNECTION_STATE_IOTC_CLIENT_MAX:
                return mContext.getResources().getString(R.string.connection_number_exceeds_the_limit);
            case Camera.CONNECTION_STATE_IOTC_NETWORK_IS_POOR:
                return mContext.getResources().getString(R.string.current_network_is_poor);
            default:
                return "";
        }
    }
}
