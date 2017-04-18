package cc.wulian.smarthomev5.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import cc.wulian.ihome.wan.core.mqpush.MQPushConnection;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.smarthomev5.tools.Preference;

public class StartServiceBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.debug("start service:");
        startMainService(context);
        checkPushConnection(context);
    }

    private void checkPushConnection(Context context) {
        MQPushConnection pushConnection = MainService.getPushConnection();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) {
            return;
        }
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String enterType = Preference.getPreferences().getUserEnterType();
        if (enterType.equals("account")) {
            if (wifiNetInfo != null && wifiNetInfo.getExtraInfo() != null) {
                if (!wifiNetInfo.getExtraInfo().contains("CamAp")) {//modifi syf
                    if (wifiNetInfo.isConnected() || mobNetInfo.isConnected()) {
                        if (pushConnection != null && (!pushConnection.isConnected())) {
                            Log.e("mainservice", "---BroadcastReceiver--");
                            pushConnection.reconnect();
                        }
                    }
                }
            }
        }
    }

    private void startMainService(Context context) {
        Intent serviceIntent = new Intent(context, MainService.class);
        context.startService(serviceIntent);
    }

}
