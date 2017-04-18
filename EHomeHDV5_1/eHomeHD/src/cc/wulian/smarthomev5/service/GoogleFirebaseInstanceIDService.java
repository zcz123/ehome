package cc.wulian.smarthomev5.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;


public class GoogleFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "=GoogleFirebase=";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }
    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        SmarthomeFeatureImpl.setData(WelcomeActivityV5.ANDROID_LOGIN_APP_TOKEN,token);
    }
}
