package cc.wulian.smarthomev5.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.Preference;

public class GoogleFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "=MyFirebaseMsgService=";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String body=remoteMessage.getNotification().getBody();
            if(isisFCMConnected()){
                MainService.getMainService().resolvePushMessage(body,"0");
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_action_tick_on)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private boolean isisFCMConnected(){
        String enterType = Preference.getPreferences().getUserEnterType();
        String pushType= SmarthomeFeatureImpl.getData(WelcomeActivityV5.ANDROID_LOGIN_PUSH_TYPE);
        String appToken=SmarthomeFeatureImpl.getData(WelcomeActivityV5.ANDROID_LOGIN_APP_TOKEN);
        boolean isFCMConnected=enterType.equals("account")&&(!StringUtil.isNullOrEmpty(appToken))&&pushType.equals(WelcomeActivityV5.ANDROID_LOGIN_PUSH_FCM);
        return isFCMConnected;
    }
}
