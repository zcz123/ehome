package com.wulian.iot.view.device.play;
import java.util.Timer;
import java.util.TimerTask;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.MediaSoftCodecMonitor;
import com.tutk.IOTC.Packet;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.IOTCDevChPojo;
import com.wulian.iot.cdm.action.EagleAction;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.server.queue.MessageQueue;
import com.wulian.iot.utils.AnimationUtils;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.view.device.setting.SetEagleCameraActivity;
import com.wulian.iot.view.ui.GalleryActivity;
import com.wulian.iot.widght.DialogRealize;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.netsdk.TKCamHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 鹰眼ui
 *
 * @author syf
 */
public class PlayEagleActivity extends SimpleFragmentActivity implements OnClickListener, Handler.Callback {
    private Handler mHandler = new Handler(this);
    private EagleAction eagleAction = null;
    private Context mContext = PlayEagleActivity.this;
    private IOTCDevChPojo iotcDevChPojo;//iot通道连接参数
    private MediaSoftCodecMonitor mediaCodecMonitor = null;
    private ImageView speakImage = null;
    private LinearLayout lowVoltageAlarm;
    private TextView lowVoltageNote;
    private int[] speakImages = new int[]{R.drawable.btn_tackback_pressed_1, R.drawable.btn_tackback_pressed_2, R.drawable.btn_tackback_pressed_3, R.drawable.desk_btn_tackback_noraml};
    private int UIIndex;//切换图片下标
    private TimingUpdateUi timingUpdateUi = null;
    private int entranceMode = -1;
    private String tutkPwd, tutkUid;
    private boolean isAdmin=false;
    private MessageQueue messageQueue = null;
    private CameraHelper.Observer observer = new CameraHelper.Observer() {
        @Override
        public void avIOCtrlOnLine() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogRealize.unInit().dismissDialog();
                }
            });
        }
        @Override
        public void avIOCtrlDataSource(byte[] data, int avIOCtrlMsgType) {
            switch (avIOCtrlMsgType){
                case  AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_BATTERY_RESP:
                    if (data!=null){
                        int a= Packet.byteArrayToInt_Little(data,4);
                        Log.i(TAG,"IOTYPE_USER_IPCAM_GET_BATTERY_RESP------:"+a);
                        if (a<25){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lowVoltageAlarm.setVisibility(View.VISIBLE);
                                    AnimationUtils.startFlick(lowVoltageAlarm);
                                }
                            });
                        }
                    }
                    break;
            }

        }
        @Override
        public void avIOCtrlMsg(int resCode,String method) {
            final String msg = messageQueue.filter(resCode,method).sendMsg();
            if (resCode==Camera.CONNECTION_ER_DEVICE_OFFLINE){
                runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          Toast.makeText(PlayEagleActivity.this, msg, Toast.LENGTH_SHORT).show();
                          DialogRealize.unInit().dismissDialog();
                      }
                  });
            }

//            if(msg!=null&&!msg.equals("")){
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(PlayEagleActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
        }
    };
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HandlerConstant.DEVICE_ONLINE:
                DialogRealize.unInit().dismissDialog();
                break;
            case HandlerConstant.UPDATE_UI:
                updateUI(UIIndex);
                break;
        }
        return false;
    }

    @Override
    public void root() {
        setContentView(R.layout.device_eagle_camera);
    }

    @Override
    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tutkPwd = getIntent().getStringExtra(Config.tutkPwd);
                tutkUid = getIntent().getStringExtra(Config.tutkUid);
                isAdmin = getIntent().getBooleanExtra(Config.isAdmin,false);
                entranceMode = getIntent().getIntExtra("without", -1);
                editor.putString(Config.CAMERANAME_EAGLE,getIntent().getStringExtra(Config.eagleName)).commit();
                eagleAction = new EagleAction(mContext);
                if (entranceMode == 1) {
                    Log.i(TAG, "===推送进入===");
                    TKCamHelper.init();
                    return;
                }
                Log.i(TAG, "===正常进入===");
            }
        }).start();
    }

    private IOTCDevChPojo getIotcDevChPojo() {
        iotcDevChPojo = new IOTCDevChPojo();
        iotcDevChPojo.setDevTag(Config.EAGLE);
        iotcDevChPojo.setTutkPwd(tutkPwd);
        iotcDevChPojo.setTutkUid(tutkUid);
        iotcDevChPojo.setDevConnMode(Config.EagleConnMode);
        return iotcDevChPojo;
    }

    @Override
    public void initView() {
        mediaCodecMonitor = (MediaSoftCodecMonitor) this.findViewById(R.id.monitor_eagle);
        lowVoltageAlarm= (LinearLayout) findViewById(R.id.ll_low_voltage_alarm);//显示低电压的图片
    }

    @Override
    public void initEvents() {
    }

    @Override
    public void onClick(View v) {
    }

    public void iotEagleRecord(View view) {
        Log.i(TAG, "=====iotEagleRecord=====");
        if (cameaHelper != null) {
//            recording(view); //本地录像暂时不支持
            WLToast.showToast(getApplicationContext(),getResources().getString(R.string.config_not_support_device),0);
            return;
        }
        WLToast.showToast(mContext, getResources().getString(R.string.device_connecting), Toast.LENGTH_LONG);
    }

    public void iotEagleSpeak(View view) {
        Log.i(TAG, "=====iotEagleSpeak=====");
        if (cameaHelper != null) {
            speakImageAssignment(view);
            initTimingUpdateUi();
            if (view.getTag().toString().equals("open")) {
                Log.i(TAG, "=====iotEagleSpeak open =====");
                timingUpdateUi.start();
                eagleAction.listenin(cameaHelper.getmCamera(), true);
                eagleAction.speakout(cameaHelper.getmCamera(), true);
                view.setTag("close");
                return;
            }
            Log.i(TAG, "=====iotEagleSpeak close =====");
            timingUpdateUi.stop();
            eagleAction.listenin(cameaHelper.getmCamera(), false);
            eagleAction.speakout(cameaHelper.getmCamera(), false);
            view.setTag("open");
            return;
        }
        WLToast.showToast(mContext, getResources().getString(R.string.device_connecting), Toast.LENGTH_LONG);
    }

    public void iotMapDepot(View view) {
        Log.d(TAG, "===iotMapDepot===");
        new Thread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mContext, GalleryActivity.class). putExtra(Config.tutkUid, tutkUid));
            }
        }).start();
    }

    public void iotSnapShoot(View view) {
        Log.d(TAG, "===iotSnapShoot===");
        if (cameaHelper != null) {
            eagleAction.snapshot(cameaHelper.getmCamera(), mContext, IotUtil.getSnapshotEaglePath(tutkUid));
            return;
        }
    }

    public void iotEagleSetting(View view) {
        Log.d(TAG, "===iotEagleSetting===");
        if (cameaHelper != null) {
            Intent mIntent = new Intent(this, SetEagleCameraActivity.class);
            mIntent.putExtra(Config.eagleSettingEnter, SetEagleCameraActivity.INTERIOR_CAMERA_SETTING);
            mIntent.putExtra(Config.tutkUid, tutkUid);
            mIntent.putExtra(Config.tutkPwd, tutkPwd);
            mIntent.putExtra(Config.isAdmin,isAdmin);
            startActivity(mIntent);
            return;
        }
        WLToast.showToast(mContext, getResources().getString(R.string.device_connecting), Toast.LENGTH_LONG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DialogRealize.init(mContext).showDiglog();
        mHandler.post(startAhannel);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.stopPlaySurfaceView();
    }

    private Runnable startAhannel = new Runnable() {
        @Override
        public void run() {
            startPlaySurfaceView();
        }
    };

    public void startPlaySurfaceView() {
        if(messageQueue==null){
            messageQueue = new MessageQueue(this);
        }
        if (cameaHelper == null) {
            Log.i(TAG, "===首次进入猫眼===");
            cameaHelper = CameraHelper.getInstance(getIotcDevChPojo());
            cameaHelper.attach(iotcDevConnCallback);
            cameaHelper.registerstIOTCLiener();
            cameaHelper.attach(observer);
        }
        cameaHelper.register();
    }

    public void stopPlaySurfaceView() {
        destroyWailThread();
        if (cameaHelper != null) {
            Log.e(TAG, "stopPlaySurfaceView");
            cameaHelper.destroyVideoCarrier(mediaCodecMonitor);
        }
        if (timingUpdateUi != null) {
            timingUpdateUi.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(disPlayRunnable).start();
        if (lowVoltageAlarm.isShown()){//取消动画
            AnimationUtils.stopFlick(lowVoltageAlarm);
        }
    }

    private Runnable disPlayRunnable = new Runnable() {
        @Override
        public void run() {
            if(messageQueue!=null){
                messageQueue.ondestroy();
                messageQueue = null;
            }
            if (cameaHelper != null) {
                cameaHelper.detach(iotcDevConnCallback);
                cameaHelper.detach(observer);
                cameaHelper.destroyVideoStream();//停止
                cameaHelper.destroyCameraHelper();
            }
            if (createLoopVolageThread!=null){
                createLoopVolageThread.stopThread();
                createLoopVolageThread=null;
            }
            mediaCodecMonitor = null;
            cameaHelper = null;
        }
    };

    private void recording(View view) {
        switch (view.getTag().toString()) {
            case "recording":
                Log.i(TAG, view.getTag().toString());
                view.setTag("unrecording");
                view.setBackgroundResource(R.drawable.eagle_icon_videotape_stop);
                startRecording();
                break;
            case "unrecording":
                Log.i(TAG, view.getTag().toString());
                view.setTag("recording");
                view.setBackgroundResource(R.drawable.eagle_icon_videotape);
                stopRecording();
                break;
        }
    }

    private void startRecording() {
        eagleAction.startRecording(cameaHelper.getmCamera(), IotUtil.getFileName(tutkUid, Config.eagleVideoType), false);
    }

    private void stopRecording() {
        eagleAction.stopRecording(cameaHelper.getmCamera());
    }

    @Override
    protected void removeMessages() {
        mHandler.removeMessages(HandlerConstant.DEVICE_ONLINE);
    }

    private  CameraHelper.IOTCDevConnCallback iotcDevConnCallback = new CameraHelper.IOTCDevConnCallback() {
        @Override
        public void session() {
            Log.i(TAG, "===session===");
            createSessionWaitThread = new CreateSessionWaitThread();
            createSessionWaitThread.start();
        }

        @Override
        public void success() {
            Log.i(TAG, "===success===");
            IotSendOrder.connect(cameaHelper.getmCamera());
            cameaHelper.createVideoCarrier(mediaCodecMonitor);
            cameaHelper.createVideoStream(Camera.SOFT_DECODE);//解码类型
            if (createLoopVolageThread==null){
                createLoopVolageThread=new CreateLoopVolageThread();
                createLoopVolageThread.start();
            }
        }
        @Override
        public void avChannel() {
            Log.i(TAG, "===avChannel===");
            createAvChannelWaitThread = new CreateAvChannelWaitThread();
            createAvChannelWaitThread.start();
        }
    };

    private class TimingUpdateUi {
        public Timer timer = null;

        public TimingUpdateUi() {
            timer = new Timer();
            UIIndex = -1;
        }

        public void start() {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (UIIndex == 2) {
                        UIIndex = -1;
                    }
                    UIIndex++;
                    mHandler.sendEmptyMessage(HandlerConstant.UPDATE_UI);
                }
            };
            timer.schedule(task, 0, 1000);
        }

        public void stop() {
            if (timer != null) {
                timer.cancel();
                UIIndex = 3;
                mHandler.sendEmptyMessage(HandlerConstant.UPDATE_UI);
                timingUpdateUi = null;
            }
        }
    }

    private void updateUI(int index) {
        if (speakImage != null) {
            speakImage.setImageResource(speakImages[index]);
        }
    }

    private void speakImageAssignment(View view) {
        if (speakImage == null) {
            speakImage = (ImageView) view;
        }
    }

    private void initTimingUpdateUi() {
        if (timingUpdateUi == null) {
            timingUpdateUi = new TimingUpdateUi();
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
                    cameaHelper.register();
                    mIsRunning = false;
                }
            }
        }
    }

    private CreateAvChannelWaitThread createAvChannelWaitThread = null;

    private class CreateAvChannelWaitThread extends Thread {
        private boolean mIsRunning = true;

        public void stopThread() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            mIsRunning = true;
            while (mIsRunning) {
                if (cameaHelper.checkAvChannel()) {
                    cameaHelper.register();
                    mIsRunning = false;
                }
            }
        }
    }
    private CreateLoopVolageThread createLoopVolageThread=null;
    private class CreateLoopVolageThread extends Thread{
        private volatile boolean mIsRunning=true;
        public void stopThread(){
            mIsRunning=false;
        }

        @Override
        public void run() {
            while (mIsRunning){
                if (mIsRunning){
                    Log.e(TAG,"==============================发送了");
                    IotSendOrder.sendGetVoltageInfo(cameaHelper.getmCamera());
                }
                try {
                    Thread.sleep(5*60*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
    private void destroyWailThread() {
        if (createSessionWaitThread != null) {
            createSessionWaitThread.stopThread();
            createSessionWaitThread = null;
        }
        if (createAvChannelWaitThread != null) {
            createAvChannelWaitThread.stopThread();
            createAvChannelWaitThread = null;
        }
    }
}
