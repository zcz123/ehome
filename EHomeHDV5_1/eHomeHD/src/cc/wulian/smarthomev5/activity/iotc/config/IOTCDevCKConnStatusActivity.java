package cc.wulian.smarthomev5.activity.iotc.config;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.Packet;
import com.wulian.icam.utils.WifiAdmin;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.IOTCDevChPojo;
import com.wulian.iot.bean.IOTCDevConfigWifiPojo;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.IOTCTimer;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.yuantuo.customview.ui.WLToast;

import java.util.HashMap;
import java.util.Map;

import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.sdk.user.AMSConstants;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.iotc.res.IOTCDevConfigFailActivity;
import cc.wulian.smarthomev5.activity.iotc.res.IOTCDevConfigWinActivity;
import cc.wulian.smarthomev5.event.DeviceUeiItemEvent;
import cc.wulian.smarthomev5.event.SigninEvent;
import cc.wulian.smarthomev5.fragment.device.DeviceDetailsFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DevicesUserManage;
import de.greenrobot.event.EventBus;


public class IOTCDevCKConnStatusActivity extends SimpleFragmentActivity {
    private final static int DEVICE_ACCEPT_WIFI = -2;
    private final static int PHONE_ACCEPT_WIFI = 1;
    private final static int DEVICE_CONFIG_WIFI = 2;
    private final static int DEVICE_BIND_ACCOUNT = 3;
    private IOTCDevCKConnStatusActivity instance = null;
    private ImageView deviceAcceptWifiImg, phoneAcceptWifiImg, deviceConfigWifiImg, deviceBindAccountImg, deviceConfigImg;
    private ImageView verticalPoint;
    private WifiAdmin wifiAdmin = null;
    private IOTCDevConfigWifiPojo iotcDevConfigWifiPojo = null;
    private Map<Integer, Object> bindFaultMap = new HashMap<Integer, Object>();
    private LinearLayout eagleBindAccountLinear = null;
    private static H5PlusWebView pWebview;
    private static String callbackID;
    private IOTCTimer iotcTimer = null;
    private IOTCTimer.TimerCallback timerCallback = new IOTCTimer.TimerCallback() {
        @Override
        public void callback() {
            Log.i(TAG, "配网超时");
            runUiThread.sendEmptyMessage(HandlerConstant.ERROR);
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };
    private CameraHelper.IOTCDevConnCallback iotcDevConnCallback = new CameraHelper.IOTCDevConnCallback() {
        @Override
        public void session() {
            Log.i(TAG, "session");
            createSessionWaitThread = new CreateSessionWaitThread();
            createSessionWaitThread.start();
        }

        @Override
        public void avChannel() {
            Log.i(TAG, "avChannel");
        }

        @Override
        public void success() {
            Log.i(TAG, "success");
        }
    };
    private CameraHelper.Observer observer = new CameraHelper.Observer() {
        @Override
        public void avIOCtrlOnLine() {
        }

        @Override
        public void avIOCtrlDataSource(byte[] data, int avIOCtrlMsgType) {
            switch (avIOCtrlMsgType) {
                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_RESP:
//                    int a=Packet.byteArrayToInt_Little(data,0);
                    updateUi(DEVICE_ACCEPT_WIFI);
                    break;
            }
        }

        @Override
        public void avIOCtrlMsg(int resCode, String method) {
        }
    };

    private void startCheckWifi() {
        deviceAcceptWifiImg.setBackground(getResources().getDrawable(R.drawable.eagle_config_success));
        checkWifThread = new CheckWifThread();
        checkWifThread.start();
    }

    private void checkIOTChannel() {
        Log.i(TAG, "checkIOTChannel");
        cameaHelper = CameraHelper.getInstance(new IOTCDevChPojo(iotcDevConfigWifiPojo.getTutkUid(), iotcDevConfigWifiPojo.getTutkPwd(), Camera.IOTC_Connect_ByUID, "eagle"));
        cameaHelper.attach(iotcDevConnCallback);
//        cameaHelper.attach(observer);
        cameaHelper.register();
        startMoreTime();
        phoneAcceptWifiImg.setBackground(getResources().getDrawable(R.drawable.eagle_config_success));
    }
    String tutkUId=null;

    private boolean isSendSuccess=true;
    private void bindDevice() {
        if (iotcDevConfigWifiPojo.getConfigWifiType() == IOTCDevConfigActivity.DOOR_DIS_NETWORK) {//门锁
           tutkUId= iotcDevConfigWifiPojo.getTutkUid();
            Log.i(TAG,"============-----==========="+tutkUId);
            TaskExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    while (isSendSuccess){
                        if (isSendSuccess){
                            DevicesUserManage.bindDevice(iotcDevConfigWifiPojo.getDoor_89_deviceId(),tutkUId+"admin");
                        }
                        try {
                            Thread.sleep(5*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else if (iotcDevConfigWifiPojo.getConfigWifiType() == IOTCDevConfigActivity.EAGLE_DIS_NETWORK) {
            DevicesUserManage.bindDevice(iotcDevConfigWifiPojo.getTutkUid(), iotcDevConfigWifiPojo.getTutkPwd(), AMSConstants.DEVICE_TYPE_CAMERA, "CMMY01", runUiThread);
    }
        deviceConfigWifiImg.setBackground(getResources().getDrawable(R.drawable.eagle_config_success));
    }

    private void jumpConfigWin() {
        deviceBindAccountImg.setBackground(getResources().getDrawable(R.drawable.eagle_config_success));
        startActivity(new Intent(instance, IOTCDevConfigWinActivity.class));
        finish();
    }

    private void jumpConfigFail() {
        startActivity(new Intent(instance, IOTCDevConfigFailActivity.class));
        finish();
    }

    private Handler.Callback updateUiCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DEVICE_ACCEPT_WIFI:
                    startCheckWifi();//检查wifi状态
                    stopPlaySurfaceView();
                    break;
                case PHONE_ACCEPT_WIFI:
                    checkIOTChannel();
                    break;
                case DEVICE_CONFIG_WIFI:
                    bindDevice();
                    break;
                case DEVICE_BIND_ACCOUNT:
                case HandlerConstant.AMS_DEVICE_HAVE_BINDED://重复绑定，也要跳到成功界面
                    Log.i(TAG, "eagle bind account success");
                    jumpConfigWin();
                    break;
                case HandlerConstant.ERROR:
                    jumpConfigFail();
                    break;
                case HandlerConstant.AMS_COMMON_SUCCESSFUL:
                    updateUi(DEVICE_BIND_ACCOUNT);
//                    WLToast.showToast(instance, bindFaultMap.get(msg.arg1).toString(),
//                            Toast.LENGTH_LONG);
                    break;
                case HandlerConstant.AMS_DEVICE_INFO_LOST:
//                case HandlerConstant.AMS_DEVICE_HAVE_BINDED:
                case HandlerConstant.AMS_ACCOUNT_TOKEN_FAILURE:
                case HandlerConstant.AMS_COMMON_ERRORFUL:
//                    WLToast.showToast(instance, bindFaultMap.get(msg.arg1).toString(),
//                            Toast.LENGTH_LONG);
                    jumpConfigFail();
                    break;

            }
            return false;
        }
    };
    private Handler runUiThread = new Handler(updateUiCallback);

    @Override
    public void root() {
        this.setContentView(R.layout.activity_eagle_send_wifi);
        instance = this;
    }

    @Override
    public void initView() {
        deviceAcceptWifiImg = (ImageView) findViewById(R.id.device_accept_wifi);
        phoneAcceptWifiImg = (ImageView) findViewById(R.id.phone_accept_wifi);
        deviceConfigWifiImg = (ImageView) findViewById(R.id.device_config_wifi);
        deviceBindAccountImg = (ImageView) findViewById(R.id.device_bind_account);
        eagleBindAccountLinear = (LinearLayout) findViewById(R.id.eagle_bind_account_linear);
        deviceConfigImg = (ImageView) findViewById(R.id.iotc_dev_config_type);
        verticalPoint= (ImageView) findViewById(R.id.iv_vertical_point_bg);
    }

    @Override
    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                obtainConfigData();
            }
        }).start();
        iotcDevConfigWifiPojo = (IOTCDevConfigWifiPojo) getIntent().getSerializableExtra(IOTCDevConfigActivity.DEVICE_CONFIG_WIFI_POJO);
        iotcTimer = new IOTCTimer();
        iotcTimer.setTimerCallback(timerCallback);
        instance.startPlaySurfaceView();
    }

    private void startMoreTime() {
        if (iotcTimer != null) {
            Log.i(TAG, "启动计时器");
            iotcTimer.startMoreTime(120000);
        }
    }

    @Override
    public void initEvents() {
        EventBus.getDefault().register(instance);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance.destroyWailThread();
        instance.stopPlaySurfaceView();
        if (iotcDevConfigWifiPojo.getConfigWifiType() == IOTCDevConfigActivity.DOOR_DIS_NETWORK)
            EventBus.getDefault().unregister(instance);
        instance = null;
    }

    private void startPlaySurfaceView() {
        if (cameaHelper != null) {
            cameaHelper.attach(observer);
            cameaHelper.attach(iotcDevConnCallback);
            IotSendOrder.sendDevConfig(cameaHelper.getmCamera(), iotcDevConfigWifiPojo.getAimSSid(), iotcDevConfigWifiPojo.getAimPwd(), (byte) iotcDevConfigWifiPojo.getEntryMode(), (byte) iotcDevConfigWifiPojo.getConfigDeviceMode());//配置wifi信息
        }
        if (iotcDevConfigWifiPojo.getConfigWifiType() == IOTCDevConfigActivity.EAGLE_DIS_NETWORK) {
            eagleBindAccountLinear.setVisibility(View.VISIBLE);
            verticalPoint.setVisibility(View.VISIBLE);
            deviceConfigImg.setBackground(getResources().getDrawable(R.drawable.cateye_wifiset_pic));
            return;
        }

        deviceConfigImg.setBackground(getResources().getDrawable(R.drawable.ic_doorlock_send_wifi));
    }

    private void stopPlaySurfaceView() {
        if (cameaHelper != null) {
            cameaHelper.detach(observer);
            cameaHelper.detach(iotcDevConnCallback);
            cameaHelper.destroyCameraHelper();
        }
        cameaHelper = null;
    }

    private void updateUi(int what) {
        runUiThread.sendEmptyMessage(what);
    }

    private void obtainConfigData() {
        wifiAdmin = new WifiAdmin(instance);
        bindFaultMap.put(HandlerConstant.AMS_ACCOUNT_TOKEN_FAILURE, getResources().getString(com.wulian.icam.R.string.eagle_bind_token_failure));
        bindFaultMap.put(HandlerConstant.AMS_DEVICE_HAVE_BINDED, getResources()
                .getString(com.wulian.icam.R.string.eagle_bind_repeat));
        bindFaultMap.put(HandlerConstant.AMS_COMMON_ERRORFUL, getResources()
                .getString(com.wulian.icam.R.string.eagle_bind_error));
        bindFaultMap.put(HandlerConstant.AMS_DEVICE_INFO_LOST, getResources()
                .getString(com.wulian.icam.R.string.eagle_bind_devicinof_bug));
        bindFaultMap.put(HandlerConstant.AMS_COMMON_SUCCESSFUL, getResources()
                .getString(com.wulian.icam.R.string.config_device_bind_success));
    }

    private CheckWifThread checkWifThread = null;

    private class CheckWifThread extends Thread {
        private boolean isRunning = true;
        private boolean isConnect = false;

        public void stopThread() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (wifiAdmin != null) {
                    if (!wifiAdmin.getSSID().contains("CamAp")) {
                        if (!isConnect) {
                            if (iotcDevConfigWifiPojo.getAimSSid()!=null&&iotcDevConfigWifiPojo.getAimPwd()!=null){
                                isConnect = wifiAdmin.connectWifi(iotcDevConfigWifiPojo.getAimSSid(), iotcDevConfigWifiPojo.getAimPwd(), returnEncryptCode(wifiAdmin.getEncryption(iotcDevConfigWifiPojo.getAimSSid())));
                            }
                        }
                        if (wifiAdmin.getSSID().contains(iotcDevConfigWifiPojo.getAimSSid())) {
                            Log.i(TAG, "IS WIFI (" + wifiAdmin.getSSID() + ")");
                            instance.updateUi(PHONE_ACCEPT_WIFI);
                            stopThread();
                        }
                    }
                }
            }
        }

        private int returnEncryptCode(String encryption) {
            switch (encryption) {
                case "wep":
                    Log.i(TAG, "wep");
                    return WifiAdmin.TYPE_WEP;
                case "psk":
                case "psk2":
                    Log.i(TAG, "psk");
                    return WifiAdmin.TYPE_WPA;
            }
            Log.i(TAG, "NONE");
            return WifiAdmin.TYPE_NONE;
        }
    }

    private CreateSessionWaitThread createSessionWaitThread = null;

    private class CreateSessionWaitThread extends Thread {
        private boolean mIsRunning = true;

        public void stopThread() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            mIsRunning = true;
            while (mIsRunning) {
                if (cameaHelper.checkSession()) {
                    if (flag||iotcDevConfigWifiPojo.getConfigWifiType() == IOTCDevConfigActivity.EAGLE_DIS_NETWORK){
                        instance.updateUi(DEVICE_CONFIG_WIFI);
                    }
                    flag=true;
                    iotcTimer.setTimerCallback(null);
                    stopPlaySurfaceView();
                    stopThread();
                }
            }
        }
    }

    private void destroyWailThread() {
        if (checkWifThread != null) {
            checkWifThread.stopThread();
            checkWifThread = null;
        }
        if (createSessionWaitThread != null) {
            createSessionWaitThread.stopThread();
            createSessionWaitThread = null;
        }
    }

    public void onEventMainThread(DeviceUeiItemEvent event) {
        Log.i(TAG, "=====++++===:onEventMainThread:==");
        isSendSuccess=false;//标志发送成功
        if (event != null) {
            if (event.data != null && !event.data.trim().equals("")) {
                if (event.data.substring(0, 20).equals(iotcDevConfigWifiPojo.getTutkUid())) {
                    jumpConfigWin();
                    return;
                }
                jumpConfigWin();
                return;
            }
            jumpConfigFail();
        }
    }
    private boolean flag=false;//标示 是否绑定
    private int time=0; //因为此方法多次执行，
    public void onEventMainThread(SigninEvent event) {
        Log.i(TAG,"==================---==========onEventMainThread");
        if (iotcDevConfigWifiPojo.getConfigWifiType() == IOTCDevConfigActivity.EAGLE_DIS_NETWORK){
            return;
        }
        if (time>=1){
            return;
        }
        if (event!=null){
            if(flag&&event.result==0){
                instance.updateUi(DEVICE_CONFIG_WIFI);
            }
            else if (event.result==0){
                flag=true;
            }else {
                time--;
            }
        }
        time++;
    }
}
