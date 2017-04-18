package cc.wulian.smarthomev5.fragment.welcome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.activity.SigninActivityV5;
import cc.wulian.smarthomev5.entity.AdvertisementEntity;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

/**
 * Created by Administrator on 2016/11/16 0016.
 */

public class AdvActivityV5 extends Activity {
    private static final String TAG = "AdvActivityV5";
    private RelativeLayout advertisementView;
    private Button advSkip;
    private Handler handler;
    private Preference preference=Preference.getPreferences();
    private AccountManager accountManager = AccountManager.getAccountManger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_adv_v5);
        advertisementView = (RelativeLayout) findViewById(R.id.advertisement);
        advSkip = (Button) findViewById(R.id.adv_skip);

        setAdvertisementView();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                jumpToActivity();
            }
        };
        handler.sendEmptyMessageDelayed(0,5000);
        advSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                jumpToActivity();
            }
        });
    }

    private void setAdvertisementView() {
        String fileName = FileUtil.getAdvertisementPath() + "/"
                +"welAdvertisement.png";
        AdvertisementEntity entity = null;
        boolean isExit = FileUtil.checkFileExistedAndAvailable(fileName);
        try {
            if (isExit) {
                Uri.Builder builder = new Uri.Builder();
                builder.path(fileName);
                makeAdvertisementImageView(entity,
                        builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jumpToActivity() {
        String defaultGwID = preference.getLastSigninID();
        boolean isRemberPassword = preference.isAutoLoginChecked(defaultGwID) && preference.isRememberChecked(defaultGwID);
        if (isRemberPassword) {
            System.out.println("------>" + "isRemberPassword");
            myStartActivity(new Intent(AdvActivityV5.this, MainHomeActivity.class));
        } else if (Preference.getPreferences().isUseAccount()) {
            System.out.println("------>" + "isUseAccount");
            try {
                tryLoginLastGateway();
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }else {
            System.out.println("------>" + "else");
            myStartActivity(new Intent(AdvActivityV5.this, SigninActivityV5.class));
        }
    }

    private void myStartActivity(Intent i) {
        startActivity(i);
        this.finish();
    }

    private void tryLoginLastGateway() {
        accountManager.setConnectGatewayCallbackAndActivity(connectGatewayCallback, this);
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                accountManager.loginLastGatewayByAccount();
            }
        });

    }

    private AccountManager.ConnectGatewayCallback connectGatewayCallback = new AccountManager.ConnectGatewayCallback() {
        @Override
        public void connectSucceed() {
            accountManager.clearConnectGatewayCallbackAndActivity(this);
            AdvActivityV5.this.jumpToMainActivity();
        }

        @Override
        public void connectFailed(int reason) {
            AdvActivityV5.this.jumpToAccountLogin();
        }
    };

    private void jumpToMainActivity() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AdvActivityV5.this, MainHomeActivity.class);
                AdvActivityV5.this.myStartActivity(intent);
            }
        });
    }

    private void jumpToAccountLogin() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AdvActivityV5.this, Html5PlusWebViewActvity.class);
                String uri = URLConstants.LOCAL_BASEURL + "login.html";
                intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
                AdvActivityV5.this.myStartActivity(intent);
            }
        });
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void makeAdvertisementImageView(
            final AdvertisementEntity entity, Uri uri) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
        imageView.setImageURI(uri);
        imageView.setAdjustViewBounds(true);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView
                .getDrawable();
        if (Build.VERSION.SDK_INT < 16) {
            advertisementView.setBackgroundDrawable(bitmapDrawable);
        } else {
            advertisementView.setBackground(bitmapDrawable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
    }
}
