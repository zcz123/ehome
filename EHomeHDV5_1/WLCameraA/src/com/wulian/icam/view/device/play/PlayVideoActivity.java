/**
 * * Project Name:  iCam
 * File Name:     AddDeviceActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2014年10月21日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.play;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.AlbumEntity;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.Scene;
import com.wulian.icam.model.Scene.OnDataChangedLisenter;
import com.wulian.icam.model.Scene.OnResultLisenter;
import com.wulian.icam.model.Scene.SData;
import com.wulian.icam.utils.AlbumUtils;
import com.wulian.icam.utils.CameraSendCmdManager;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.DialogUtils.OperatorForV5Lisener;
import com.wulian.icam.utils.DialogUtils.SceneAdapter;
import com.wulian.icam.utils.MD5Utils;
import com.wulian.icam.utils.ProgressDialogManager;
import com.wulian.icam.utils.StringUtil;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.album.AlbumGridActivity;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.AngleMeter;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.view.widget.MyHorizontalScrollView;
import com.wulian.icam.view.widget.MyHorizontalScrollView.OnScrollChangedListener;
import com.wulian.icam.view.widget.YuntaiButton;
import com.wulian.routelibrary.utils.MD5;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.manage.SipCallSession;
import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.manage.SipProfile;
import com.wulian.siplibrary.utils.WulianLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.videoengine.ViERenderer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Wangjj
 * @ClassName: PlayVideoActivity
 * @Function: 视频播放页
 * @Date: 2014年10月21日
 * @email wangjj@wuliangroup.cn
 */
public class PlayVideoActivity extends BaseFragmentActivity implements
        OnClickListener, OnTouchListener {

    private View view;
    private View view_control_background;

    public class info {
        public String title;
        public int resId;
        public String action;
    }

    public interface SceneSelete {
        void onSeleted(int position);
    }

    private Scene scene;
    private String gwID, devID, spPwd, spEpType;
    private YuntaiButton yuntaiBtn, yuntai_btn_nospeak,
            yuntai_btn_nospeak_landscape, yuntai_btn_new;
    private PopupWindow popDefinitionWindow;
    private ImageView iv_control_csc_bar, btn_titlebar_back;
    private ImageView iv_control_fullscreen_bar;
    private View popDefinitionView, divider_silence;
    private TextView tv_control_definition_bar, tv_control_definition1,
            tv_control_definition2, tv_control_definition3, tv_speed,
            tv_speed_landscape, tv_video_play_timeorname;

    private AlertDialog cscDialog;
    private View cscDialogView;
    private Button btn_csc_dismiss, btn_csc_restore_default, progress_refresh,
            btn_control_landscape_to_portrait,
            btn_control_definition_bar_landscape,
            btn_control_snapshot_landscape, btn_control_talkback_landscape,
            btn_control_silence_landscape, btn_album_new, btn_scene_new, btn_lock;
    private SeekBar sb_csc_luminance, sb_csc_contrast, sb_csc_saturability,
            sb_csc_definition;

    private FrameLayout flPortrait;
    private SurfaceView cameraPreview;
    private WakeLock wakeLock;
    private WakeLock videoWakeLock;
    private PowerManager powerManager;
    private Dialog mTipDialog;
    private final static String TAG = "PlayVideoActivity";
    private Device device;
    private SipProfile account;
    private AngleMeter anglemeter;
    SipCallSession sipCallSession;
    int callId = -1;
    int seq = 0;
    String savePath = "", snapSavePath = "";
    private boolean is_portrait = true, isMuteOpen/* 静音是否打开 */ = true;
    private int widthRatio = 16;
    private int heightRatio = 9;
    private int minWidth, maxWidth, beginWidth;
    GestureDetector mGestureDetector;
    ScaleGestureDetector mScaleGestureDetector;
    private boolean is_portrait_fullSize = true;
    private MyHorizontalScrollView horizontal_sv;
    private static int SCROLLBY = 18;// 每次偏移的距离
    private RelativeLayout rl_video;// 竖屏宿主

    private Button btn_replay;
    private String status;
    private String cameraId;
    boolean temp = false;
    private String password;
    private List<String> elist = new ArrayList<>();
    private CameraSendCmdManager cameraSendCmdManager = CameraSendCmdManager.getInstance(this);
    private SendDoorlockCmd sendDoorlockCmd;
    private static final String SEND_CTR_CMD = "send_ctr_cmd";

    //  新猫眼控件
    private ImageView ivEagleSnapshot;
    private ImageView ivEagleAlbum;
    private ImageView ivEagleTalk;
    private ImageView ivEagleClose;
    private ImageView ivEagleSilence;
    private LinearLayout llNewEagleLayout;
    private boolean isNewEagle = false;

    private boolean isControling = false, hasYuntai = false, hasSpeak = false;// 使得控制和断开成对匹配，同时避免只能长按取消
    private boolean isIgnoreSingleTapConfirmed = false;// 如果按下时，当前正在控制中，则只要停止控制即可，忽略控制条的显隐。

    String deviceSipAccount;// 设备sip账号
    String deviceControlUrl;// 设备控制sip地址
    String deviceCallUrl;// 设备呼叫sip地址

    private RelativeLayout rl_video_control_panel,
            rl_video_control_panel_nospeak;
    LinearLayout include_control_bar;
    private LinearLayout ll_play_container, ll_contain_fullscreen_btn,
            ll_control_panel_new, ll_video_control_panel_new,
            ll_control_yuntai, ll_control_forv5;
    private RelativeLayout rl_control_panel, rl_control_panel_nospeak,
            rl_control_landscape;
    private SharedPreferences sp;
    private SharedPreferences spLan;
    private boolean isRunOnUI = false, isConncted = false,
            isShowVideo = true/* 视频是否显示,默认显示，便于判断 */, isMediaPlaying = false,
            isVideoInvert = false, isSnapshot = false/* 退出截屏 */;

    private boolean isStop = false;
    private int disconnectCount = 0;
    private int retryFor486 = 0;
    private long startTime = System.currentTimeMillis();// 启动时间
    private int isNegotiationState = NEGOTIATION_UNKNOWN;
    private String lastSpeed = "0", devicePwd = "", deviceCallIp = "";
    private ImageView iv_silence_new;
    private Button btn_snapshot, btn_talkback, btn_mute,
            btn_snapshot_nospeak, btn_snapshot_new, btn_speak_new,
            btn_speak_no_yuntai_new;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    protected ProgressDialogManager mDialogManager = ProgressDialogManager
            .getDialogManager();


    // 传感器相关
    private SensorManager sensorManager;
    private Sensor acceleSensor, magneticSensor, origintationSensor;
    private boolean isSensorRegister = false;
    private SensorEventListener sensorEventListener;
    private float[] gravity, geomagnetic, matrixR = new float[9],
            results = new float[3];
    private float lastRotation = -1, detlaRotation = 3/* 越小越灵敏 */;

    // nat状态
    private static final int NEGOTIATION_UNKNOWN = 0;
    private static final int NEGOTIATION_INPROGRESS = 1;
    private static final int NEGOTIATION_SUCCESS = 2;
    private static final int NEGOTIATION_FAIL = 3;

    private static final int SHOWSPEED = 3;
    private static final int KEYFRAME = 4;
    private static final int INENABLE = 10;
    private static final int ENABLE = 11;
    private static final int SPEED_RETRY = 5;
    private static final int SPEED_RETRY_FORCE = 6;
    private static final int SPEED_RETRY_TIME = 15000;// 15秒0kb监控
    private static final int AUTO_SILENCE = 12;// 自动切换为静音状态
    private static final int VIDEO_CENTER = 13;// 视频画面居中
    private static final int AUTO_SILENCE_TIME = 20;// 20秒
    private static final int NET_CHECK = 7;
    private static final int NET_CHECK_RANGE = 10;// 在前10秒检测
    private static final int SHOWSPEED_INTERVAL = 5;// 速度间隔为5秒
    private static final int SEND_RTP = 100;
    private static final int SCENE_RESULT = 110;
    private static final int SCENE_OVERTIME = 111;

    private static final int YUNTAI_CONTROL = 1;
    private LinearLayout ll_linking_video, ll_linking_video_refresh;
    UpdateUIFromCallRunnable uiUpdate = new UpdateUIFromCallRunnable();
    private com.wulian.icam.view.widget.YuntaiButton.Direction lastDirection,
            curDirection;
    private List<SData> mSDataList;
    private boolean isInPlayUI;
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCENE_OVERTIME:
                    dismissBaseDialog();
                    if (isInPlayUI)
                        CustomToast.show(PlayVideoActivity.this, getResources()
                                .getString(R.string.scene_timeout));
                    break;
                case SCENE_RESULT:
                    myHandler.removeMessages(SCENE_OVERTIME);
                    if (msg.arg1 == 1) {
                        scene = Scene.getInstance();
                        int idx = scene.getSelectdIdx();
                        mSDataList = scene.getDataList();
                        dismissBaseDialog();
                        if (isInPlayUI)
                            CustomToast.show(PlayVideoActivity.this, getResources()
                                            .getString(R.string.scene_success)
                        /* + scene.getDataList().get(idx).title */);
                    } else {
                        dismissBaseDialog();
                        if (isInPlayUI)
                            CustomToast.show(PlayVideoActivity.this, getResources()
                                    .getString(R.string.scene_failure));
                    }
                    break;
                case SPEED_RETRY:
                    if (callId != -1) {
                        // CustomToast.show(PlayVideoActivity.this, "没有数据流,重连...");
                        Utils.sysoInfo("没有数据流,重连callId=" + callId);
                        myHandler.removeMessages(SPEED_RETRY);
                        hangUpVideo();
                        reCallVideo();
                    }
                    break;
                case SPEED_RETRY_FORCE:
                    hangUpVideo();
                    reCallVideo();
                    break;
                case SHOWSPEED:
                    if (callId != -1) {
                        String speedInfo = SipController.getInstance()
                                .getCallSpeedInfos(callId);// 越来越大，用long
                        if (!TextUtils.isEmpty(speedInfo)) {
                            long speed = 0;
                            long delatSpeed = 0;
                            try {
                                speed = Long.parseLong(speedInfo);
                                delatSpeed = speed - Long.parseLong(lastSpeed);
                                delatSpeed = (delatSpeed > 0 ? delatSpeed : 0)
                                        / SHOWSPEED_INTERVAL;// 除以SHOWSPEED_INTERVAL因为有SHOWSPEED_INTERVAL秒间隔
                                lastSpeed = speedInfo;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            if (delatSpeed == 0) {// 连续10秒，则重试
                                Utils.sysoInfo("检测到速度为0KB");
                                if (!myHandler.hasMessages(SPEED_RETRY)) {
                                    Utils.sysoInfo("发送延迟10秒重呼");
                                    myHandler.sendEmptyMessageDelayed(SPEED_RETRY,
                                            SPEED_RETRY_TIME);
                                } else {
                                    Utils.sysoInfo("10秒重呼已经存在,不再发送");
                                }
                            } else {

                                if (myHandler.hasMessages(SPEED_RETRY)) {
                                    Utils.sysoInfo("检测到速度>0,移除重呼消息");
                                    myHandler.removeMessages(SPEED_RETRY);
                                }
                            }
                            if (cameraPreview != null) {
                                // Utils.sysoInfo(cameraPreview.getTop() + "-"
                                // + cameraPreview.getLeft() + "-"
                                // + cameraPreview.getHeight() + "-"
                                // + rl_video.getHeight());

                                // Utils.sysoInfo(tv_speed.getTop() + "-"
                                // + tv_speed.getLeft());

                                // tv_speed.setTop(cameraPreview.getTop());

                                // tv_speed.setX(10);
                                // tv_speed.setY(cameraPreview.getTop() + 10);
                            }
                            long perSpeed = delatSpeed / 8 / 1000;
                            if (is_portrait) {
                                tv_speed.setVisibility(View.VISIBLE);
                            }
                            tv_speed.setText("" + (perSpeed > 0 ? perSpeed : 1)
                                    + "KB/s");
                            tv_speed_landscape.setText(""
                                    + (perSpeed > 0 ? perSpeed : 1) + "KB/s");
                            if (tv_speed.getVisibility() == View.GONE
                                    && is_portrait) {
                                tv_speed.setVisibility(View.VISIBLE);
                            }
                            if (tv_speed_landscape.getVisibility() == View.GONE
                                    && !is_portrait) {
                                tv_speed_landscape.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    myHandler.removeMessages(SHOWSPEED);// 清空历史消息，避免频繁
                    myHandler.sendEmptyMessageDelayed(SHOWSPEED,
                            SHOWSPEED_INTERVAL * 1000);
                    break;
                case KEYFRAME:
                    if (!isRunOnUI) {
                        if (callId != -1 && isConncted) {
                            Utils.sysoInfo("10秒钟,已经呼通，关键帧还没来,直接runOnUI");
                            isRunOnUI = true;
                            isMediaPlaying = true;
                            runOnUiThread(uiUpdate);
                        } else {
                            myHandler.sendEmptyMessageDelayed(KEYFRAME, 5000);
                            Utils.sysoInfo("10秒钟,还没有视频流,延迟5秒判断关键帧");
                        }
                    } else {
                        Utils.sysoInfo("关键帧判断handler消息KEYFRAME来了，但是已经播放中");
                        isMediaPlaying = true;
                    }
                    break;
                case NET_CHECK:
                    if (ICamGlobal.getInstance().getNatNum() == 0
                            && System.currentTimeMillis() < startTime
                            + NET_CHECK_RANGE * 1000) {
                        myHandler.sendEmptyMessageDelayed(NET_CHECK, 1000);
                    } else {
                        myHandler.removeMessages(NET_CHECK);
                        ViERenderer.setIsReturnPictureState();
                        makeCallDevice(device);
                    }
                    break;
                case AUTO_SILENCE:
                    isMuteOpen = true;
                    btn_mute.setBackgroundResource(R.drawable.selector_function_silence_off);
                    btn_control_silence_landscape
                            .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_off);
                    iv_silence_new
                            .setBackgroundResource(R.drawable.desk_cb_silence_off);
                    ivEagleSilence.setBackgroundResource(R.drawable.desk_cb_silence_off);
                    if (callId != -1)
                        SipController.getInstance().closeAudioTransport(callId);
                    break;
                case VIDEO_CENTER:
                    videoCenter();
                    break;
                default:
                    break;
            }
        }
    };
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ViERenderer.FILE_OK:// 可能是会被上一个消息接受到，导致其他设备也获取该截图作为背景
                    if (isSnapshot) {// 退出截屏,不要重置isSnapshot =
                        // false，否则导致下面面的!isSnapshot成立
                        Bundle bundle = msg.getData();
                        final Bitmap bitmap = bundle
                                .getParcelable(ViERenderer.GET_PICTURE);
                        if (bitmap != null) {
                            Utils.sysoInfo(this + "接受到handler图片"
                                    + device.getDevice_id());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {// 800毫秒的卡顿
                                    Utils.saveBitmap(device.getDevice_id(), bitmap,
                                            PlayVideoActivity.this);
                                }
                            }).start();
                            mHandler.removeMessages(ViERenderer.FILE_OK);
                        }
                        hangUpVideo();// final,挂断
                    } else {
                        Bundle bundle = msg.getData();
                        Bitmap bitmap = bundle
                                .getParcelable(ViERenderer.GET_PICTURE);
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                            bitmap = null;
                            showMsg(R.string.play_take_picture_ok);
                        }
                        if (PlayVideoActivity.this != null) {
                            MediaScannerConnection.scanFile(PlayVideoActivity.this,
                                    new String[]{snapSavePath},
                                    new String[]{"image/png"}, null);
                            Utils.sysoInfo("请求扫描" + snapSavePath);
                        }
                    }
                    break;
                case ViERenderer.TAKE_PICTURE_FAIL:
                    if (!isSnapshot) {
                        showMsg(R.string.play_take_picture_exception);
                    }
                    break;
                case ViERenderer.FILE_MOUNT_EXCEPTION:
                    if (!isSnapshot) {
                        showMsg(R.string.play_take_picture_mount_exception);
                    }
                    break;
                case ViERenderer.FILE_PICTURE_CREATE_EXCEPTION:
                    if (!isSnapshot) {
                        showMsg(R.string.play_take_picture_create_exception);
                    }
                    break;
                case ViERenderer.FILE_PICTURE_EXCEPTION:
                    if (!isSnapshot) {
                        showMsg(R.string.play_take_picture_exception);
                    }
                    break;
                case SEND_RTP:
                    WulianLog.d(TAG, "SendRTP");
                    SipController.getInstance().sendRtp(callId);
                    mHandler.sendEmptyMessageDelayed(SEND_RTP, 5000);
                    break;
                case ViERenderer.PICTURE_HAS_COMING:
                    WulianLog.d(TAG, "PICTURE_HAS_COMING");
                    ICamGlobal.isNeedRefreshSnap = true;
                    ll_linking_video.setVisibility(View.GONE);
                    myHandler.sendEmptyMessage(SHOWSPEED);
                    break;
                default:
                    break;
            }
        }
    };

    public void videoCenter() {
        if (horizontal_sv != null) {
            horizontal_sv.scrollTo((maxWidth - minWidth) / 2, 0);
        }
    }

    Handler InputHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENABLE:
                    WulianLog.d("PlayVideo", "ENABLE");
                    SipController.getInstance().setMicrophoneMute(false, callId);
                    InputHandler.sendEmptyMessageDelayed(INENABLE, 2000);
                    break;
                case INENABLE:
                    WulianLog.d("PlayVideo", "INENABLE");
                    SipController.getInstance().setMicrophoneMute(true, callId);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    Handler ytHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case YUNTAI_CONTROL:
                    curDirection = (com.wulian.icam.view.widget.YuntaiButton.Direction) msg.obj;
                    switch (curDirection) {
                        case left:
                            yuntai_left();
                            break;
                        case up:
                            yuntai_up();
                            break;
                        case right:
                            yuntai_right();
                            break;
                        case down:
                            yuntai_down();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_play_video,null);
        setContentView(view);
        initViews();
        initListeners();
        initData();
        attachVideoPreview();
        showVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.sysoInfo("onResume");
        reCallVideo();// 弹窗也会照成onResume
        disconnectCount = 0;
        isStop = false;
        int callStream = SipController.getInstance().getCallStream();
        if (callStream != -1)
            this.setVolumeControlStream(callStream);
        registerSensorListener();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterSeneorListener();
        unregisterReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.sysoInfo("onStop");
        isStop = true;
        hangUpVideo();// 如果不关闭，电话岂不是被窃听了
        // v5提示
        if (ICamGlobal.forV5) {
            Scene.getInstance().setInPlayVideoUI(false);
            isInPlayUI = false;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        unregisterReceiver(headsetPlugReceiver);
        Utils.sysoInfo("onDestory");
        // 健壮性:恢复item的点击
        ICamGlobal.isItemClickProcessing = false;
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        if (popDefinitionWindow != null && popDefinitionWindow.isShowing()) {
            popDefinitionWindow.dismiss();
        }

        myHandler.removeMessages(SPEED_RETRY);
        myHandler.removeMessages(SHOWSPEED);
        myHandler.removeMessages(KEYFRAME);
        myHandler.removeMessages(AUTO_SILENCE);

        InputHandler.removeMessages(ENABLE);
        InputHandler.removeMessages(INENABLE);

        isSnapshot = true;
        mHandler.removeMessages(ViERenderer.FILE_OK);
        mHandler.removeMessages(ViERenderer.TAKE_PICTURE_FAIL);
        mHandler.removeMessages(ViERenderer.FILE_MOUNT_EXCEPTION);
        mHandler.removeMessages(ViERenderer.FILE_PICTURE_CREATE_EXCEPTION);
        mHandler.removeMessages(ViERenderer.FILE_PICTURE_EXCEPTION);
        mHandler.removeMessages(SEND_RTP);
        ViERenderer.setTakePicNotSave();
        unregisterReceiver(callStateReceiver);
        detachVideoPreview();
        if (callId != -1) {// 已经建立连接
            // SipController.getInstance().hangupCall(callId);
        }
        SipController.getInstance().setVideoAndroidRenderer(
                SipCallSession.INVALID_CALL_ID, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    @SuppressLint("NewApi")
    private void initViews() {
        flPortrait = (FrameLayout) findViewById(R.id.fl_portrait);
        btn_lock = (Button) findViewById(R.id.btn_lock);
        btn_titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
        iv_control_csc_bar = (ImageView) findViewById(R.id.iv_control_csc_bar);
        yuntaiBtn = (YuntaiButton) findViewById(R.id.yuntai_btn);
        yuntai_btn_nospeak = (YuntaiButton) findViewById(R.id.yuntai_btn_nospeak);
        yuntai_btn_nospeak_landscape = (YuntaiButton) findViewById(R.id.yuntai_btn_nospeak_landscape);
        yuntai_btn_new = (YuntaiButton) findViewById(R.id.yuntai_btn_new);
        anglemeter = (AngleMeter) findViewById(R.id.anglemeter);
        btn_snapshot = (Button) findViewById(R.id.btn_snapshot);
        btn_snapshot_nospeak = (Button) findViewById(R.id.btn_snapshot_nospeak);
        btn_snapshot_new = (Button) findViewById(R.id.btn_snapshot_new);
        btn_control_snapshot_landscape = (Button) findViewById(R.id.btn_control_snapshot_landscape);
        btn_talkback = (Button) findViewById(R.id.btn_talkback);
        btn_speak_new = (Button) findViewById(R.id.btn_speak_new);
        btn_speak_no_yuntai_new = (Button) findViewById(R.id.btn_speak_no_yuntai_new);
        btn_control_talkback_landscape = (Button) findViewById(R.id.btn_control_talkback_landscape);
        btn_mute = (Button) findViewById(R.id.btn_silence);
        iv_silence_new = (ImageView) findViewById(R.id.cb_silence);
        btn_control_silence_landscape = (Button) findViewById(R.id.btn_control_silence_landscape);
        btn_album_new = (Button) findViewById(R.id.btn_album_new);
        btn_scene_new = (Button) findViewById(R.id.btn_scene_new);

        ll_linking_video = (LinearLayout) findViewById(R.id.ll_linking_video);
        ll_linking_video_refresh = (LinearLayout) findViewById(R.id.ll_linking_video_refresh);

        rl_video_control_panel = (RelativeLayout) findViewById(R.id.rl_video_control_panel);
        rl_video_control_panel_nospeak = (RelativeLayout) findViewById(R.id.rl_video_control_panel_nospeak);
        rl_control_panel = (RelativeLayout) findViewById(R.id.rl_control_panel);
        ll_video_control_panel_new = (LinearLayout) findViewById(R.id.ll_video_control_panel_new);
        ll_control_panel_new = (LinearLayout) findViewById(R.id.ll_control_panel_new);
        ll_control_yuntai = (LinearLayout) findViewById(R.id.ll_control_yuntai);
//        ll_control_forv5 = (LinearLayout) findViewById(R.id.ll_control_forv5);
        rl_control_panel_nospeak = (RelativeLayout) findViewById(R.id.rl_control_panel_nospeak);
        rl_control_landscape = (RelativeLayout) findViewById(R.id.rl_control_landscape);

        include_control_bar = (LinearLayout) findViewById(R.id.include_control_bar);
        ll_play_container = (LinearLayout) findViewById(R.id.ll_play_container);
        ll_contain_fullscreen_btn = (LinearLayout) findViewById(R.id.ll_contain_fullscreen_btn);
        iv_control_fullscreen_bar = (ImageView) findViewById(R.id.iv_control_fullscreen_bar);
        btn_control_landscape_to_portrait = (Button) findViewById(R.id.btn_control_landscape_to_portrait);
        tv_video_play_timeorname = (TextView) findViewById(R.id.tv_video_play_timeorname);
        horizontal_sv = (MyHorizontalScrollView) findViewById(R.id.horizontal_sv);
        btn_replay = (Button) findViewById(R.id.btn_replay);

        rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        tv_speed_landscape = (TextView) findViewById(R.id.tv_speed_landscape);
        ll_linking_video_refresh.setVisibility(View.GONE);
        progress_refresh = (Button) findViewById(R.id.progress_refresh);
        tv_control_definition_bar = (TextView) findViewById(R.id.tv_control_definition_bar);
        btn_control_definition_bar_landscape = (Button) findViewById(R.id.btn_control_definition_bar_landscape);
        divider_silence = findViewById(R.id.divider_silence);
        popDefinitionView = getLayoutInflater().inflate(
                R.layout.control_definition, null);
        tv_control_definition1 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition1);
        tv_control_definition2 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition2);
        tv_control_definition3 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition3);
        popDefinitionWindow = new PopupWindow(popDefinitionView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        popDefinitionWindow.setAnimationStyle(R.style.bottom_menu_scale);

        // csc相关设置初始化
        cscDialog = new AlertDialog.Builder(this, R.style.alertDialog).create();

        cscDialogView = LinearLayout.inflate(this, R.layout.control_csc,
                (ViewGroup) findViewById(R.id.rl_csc));

        btn_csc_dismiss = (Button) cscDialogView
                .findViewById(R.id.btn_csc_dissmis);
        btn_csc_restore_default = (Button) cscDialogView
                .findViewById(R.id.btn_csc_restore_default);

        sb_csc_luminance = (SeekBar) cscDialogView
                .findViewById(R.id.sb_luminance);
        sb_csc_contrast = (SeekBar) cscDialogView
                .findViewById(R.id.sb_contrast);
        sb_csc_saturability = (SeekBar) cscDialogView
                .findViewById(R.id.sb_saturability);
        sb_csc_definition = (SeekBar) cscDialogView
                .findViewById(R.id.sb_definition);

        ivEagleAlbum = (ImageView) findViewById(R.id.iv_eagle_album);
        ivEagleSnapshot = (ImageView) findViewById(R.id.iv_eagle_snapshot);
        ivEagleTalk = (ImageView) findViewById(R.id.iv_eagle_talk);
        ivEagleClose = (ImageView) findViewById(R.id.iv_eagle_close);
        ivEagleSilence = (ImageView) findViewById(R.id.iv_eagle_silence);
        llNewEagleLayout = (LinearLayout) findViewById(R.id.ll_neweagle_layout);
        view_control_background = findViewById(R.id.view_control_background);

    }

    private void initData() {
        isInPlayUI = true;
        mGestureDetector = new GestureDetector(this, new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Utils.sysoInfo("onSingleTapUp");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                Utils.sysoInfo("onShowPress");
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                // Utils.sysoInfo("onScroll");
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Utils.sysoInfo("onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {

                Utils.sysoInfo("onFling");
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Utils.sysoInfo("mGestureDetector onDown" + e.getPointerCount());// 个数始终是1

                return true;// false会忽略掉手势
            }
        });
        mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Utils.sysoInfo("onSingleTapConfirmed");
                if (popDefinitionWindow.isShowing()) {
                    popDefinitionWindow.dismiss();
                    return false;
                }

                if (isIgnoreSingleTapConfirmed) {
                    isIgnoreSingleTapConfirmed = false;
                    Utils.sysoInfo("由于云台控制中，这里的单击显隐控制条方法被忽略！");
                    return false;
                }
                // TODO　显示横屏下控件
                if (!is_portrait) {
                    if (rl_control_landscape.getVisibility() == View.GONE) {
                        rl_control_landscape.setVisibility(View.VISIBLE);
                    } else {
                        rl_control_landscape.setVisibility(View.GONE);
                    }
                }
                // }// 控制云台时，不是有效的单击
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                Utils.sysoInfo("onDoubleTapEvent");
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Utils.sysoInfo("onDoubleTap");
                // 要求双击不填冲,改为全屏
                // fullSize();
                if (is_portrait) {
                    goLandscape();
                } else {
                    goPortrait();
                }
                return false;
            }
        });

        mScaleGestureDetector = new ScaleGestureDetector(this,
                new OnScaleGestureListener() {

                    @Override
                    public void onScaleEnd(ScaleGestureDetector detector) {
                        Utils.sysoInfo("onScaleEnd");
                        updateAngleMeter(horizontal_sv);
                    }

                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        Utils.sysoInfo("onScaleBegin");
                        RelativeLayout.LayoutParams lp = (LayoutParams) cameraPreview
                                .getLayoutParams();
                        beginWidth = lp.width;
                        // 缩放手势开始，忽略HorizontalScrollView的水平滚动手势
                        return true;
                    }

                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        Utils.sysoInfo("onScale" + detector.getScaleFactor());
                        if (isPortrait()) {
                            float finalWidth = beginWidth
                                    * detector.getScaleFactor();
                            if (minWidth < finalWidth && finalWidth < maxWidth) {
                                RelativeLayout.LayoutParams lpf = (LayoutParams) cameraPreview
                                        .getLayoutParams();
                                lpf.width = (int) finalWidth;
                                lpf.height = (int) (finalWidth * heightRatio / widthRatio);
                                cameraPreview.setLayoutParams(lpf);
                            }
                        }
                        return false;
                    }
                });
        sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
        spLan = getSharedPreferences(APPConfig.SP_LAN_CONFIG, MODE_PRIVATE);
        device = (Device) getIntent().getSerializableExtra("device");
        tv_video_play_timeorname.setText(device.getDevice_nick());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acceleSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        origintationSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event.sensor == null)// 没有传感器
                    return;

                // 简单方式
                if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                    if (lastRotation != -1) {
                        if (event.values[0] - lastRotation > detlaRotation) {
                            // scrollViewRight();//暂时不需要传感器功能
                        } else if (event.values[0] - lastRotation < -detlaRotation) {
                            // scrollViewLeft();
                        }
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mediaPlayer = MediaPlayer.create(this, R.raw.snapshot);

        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // mp.release();
            }
        });

        mediaPlayer.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.release();
                return false;
            }
        });
        disconnectCount = 0;

        deviceSipAccount = device.getSip_username();// 1044
        deviceCallUrl = deviceSipAccount + "@" + device.getSip_domain();
        deviceControlUrl = deviceCallUrl;// sip:cmicxxx@wuliangroup.cn
        isVideoInvert = sp.getBoolean(device.getDevice_id()
                + APPConfig.VIDEO_INVERT, false);
        // 企鹅、随便看、新猫眼的不同控件显示。
        if (device.getDevice_id().toLowerCase(Locale.US).startsWith("cmic01")) {
            hasYuntai = true;
            hasSpeak = false;
        } else {
            hasYuntai = false;
            hasSpeak = true;
        }

        if (device.getDevice_id().toLowerCase(Locale.US).startsWith("cmic04")) {
            hasYuntai = true;
            hasSpeak = true;
        }
        if (device.getDevice_id().toLowerCase(Locale.US).startsWith("cmic08")) {
            isNewEagle = true;
            include_control_bar.setVisibility(View.GONE);
            ll_control_panel_new.setVisibility(View.GONE);
            anglemeter.setVisibility(View.GONE);
            llNewEagleLayout.setVisibility(View.VISIBLE);
        }

        initDoorlockData(PlayVideoActivity.this);//读取绑定时的gwID和devID,cameraId
        if (hasYuntai) {
            yuntai_btn_new.setVisibility(View.VISIBLE);
            yuntai_btn_nospeak_landscape.setVisibility(View.VISIBLE);
            yuntai_btn_nospeak_landscape.setBackground(this,
                    R.drawable.video_control_panel);
            if (hasSpeak) {
                btn_speak_new.setVisibility(View.VISIBLE);
                btn_speak_no_yuntai_new.setVisibility(View.GONE);
                iv_silence_new.setVisibility(View.VISIBLE);
                divider_silence.setVisibility(View.VISIBLE);
                btn_control_talkback_landscape.setVisibility(View.VISIBLE);
                btn_control_silence_landscape.setVisibility(View.VISIBLE);
            } else {
                btn_speak_new.setVisibility(View.GONE);
                btn_speak_no_yuntai_new.setVisibility(View.GONE);
                iv_silence_new.setVisibility(View.GONE);
                divider_silence.setVisibility(View.GONE);
                btn_control_talkback_landscape.setVisibility(View.GONE);
                btn_control_silence_landscape.setVisibility(View.GONE);
            }
        } else {
            yuntaiBtn.setVisibility(View.GONE);
            yuntai_btn_nospeak_landscape.setVisibility(View.GONE);
            if (hasSpeak) {
                btn_speak_new.setVisibility(View.GONE);
                btn_speak_no_yuntai_new.setVisibility(View.VISIBLE);
                iv_silence_new.setVisibility(View.VISIBLE);
                divider_silence.setVisibility(View.VISIBLE);
                btn_control_talkback_landscape.setVisibility(View.VISIBLE);
                btn_control_silence_landscape.setVisibility(View.VISIBLE);
            }
        }
        if (!device.getIs_BindDevice() || !hasYuntai) {
            rl_video_control_panel.setVisibility(View.GONE);
            rl_video_control_panel_nospeak.setVisibility(View.GONE);
            ll_control_yuntai.setVisibility(View.GONE);
        }
        if (ICamGlobal.forV5) {
            scene = Scene.getInstance();
            scene.setOnResultLisenter(new OnResultLisenter() {
                @Override
                public void onResultChanged(boolean success) {
                    int arg = success ? 1 : 0;
                    myHandler.sendMessage(Message.obtain(myHandler,
                            SCENE_RESULT, arg, 0));
                }
            });
            scene.setOnDataResultLisenter(new OnDataChangedLisenter() {
                @Override
                public void OnDateChanged(List<SData> sDataList) {
                    if (mSceneAdapter != null)
                        mSceneAdapter.refreshAdapter(sDataList);
                }
            });
            scene.setInPlayVideoUI(true);
        }
//        if (ICamGlobal.forV5) {
//            ll_control_forv5.setVisibility(View.VISIBLE);
//        } else {
//            if (hasSpeak && hasYuntai) {
//                ll_control_forv5.setVisibility(View.INVISIBLE);
//            } else {
//                ll_control_forv5.setVisibility(View.GONE);
//            }
//        }
        ViERenderer.setTakePicHandler(mHandler);
        isNegotiationState = NEGOTIATION_UNKNOWN;
        registerHeadsetPlugReceiver();
        btn_mute.setBackgroundResource(R.drawable.selector_function_silence_off);
        btn_control_silence_landscape
                .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_off);
        iv_silence_new.setBackgroundResource(R.drawable.cb_silence_off);
        ivEagleSilence.setBackgroundResource(R.drawable.btn_neweagle_silence_off);
        yuntaiBtn.setOnDirectionLisenter(new MyDirection());
        yuntai_btn_nospeak.setOnDirectionLisenter(new MyDirection());
        yuntai_btn_nospeak_landscape.setOnDirectionLisenter(new MyDirection());
        yuntai_btn_new.setOnDirectionLisenter(new MyDirection());
    }

    private void initListeners() {
        btn_lock.setOnClickListener(this);
        btn_titlebar_back.setOnClickListener(this);
        iv_control_csc_bar.setOnClickListener(this);
        progress_refresh.setOnClickListener(this);
        btn_snapshot.setOnClickListener(this);
        btn_snapshot_nospeak.setOnClickListener(this);
        btn_snapshot_new.setOnClickListener(this);
        btn_control_snapshot_landscape.setOnClickListener(this);
        btn_talkback.setOnClickListener(this);// 必须有,配合触摸事件一起工作
        btn_speak_new.setOnClickListener(this);// 必须有,配合触摸事件一起工作
        btn_speak_no_yuntai_new.setOnClickListener(this);// 必须有,配合触摸事件一起工作
        btn_control_talkback_landscape.setOnClickListener(this);// 必须有,配合触摸事件一起工作
        iv_control_fullscreen_bar.setOnClickListener(this);
        btn_control_landscape_to_portrait.setOnClickListener(this);
        ll_contain_fullscreen_btn.setOnClickListener(this);
        btn_control_talkback_landscape.setOnTouchListener(this);
        btn_talkback.setOnTouchListener(this);
        btn_speak_new.setOnTouchListener(this);
        btn_speak_no_yuntai_new.setOnTouchListener(this);
        btn_mute.setOnClickListener(this);
        btn_control_silence_landscape.setOnClickListener(this);
        iv_silence_new.setOnClickListener(this);
        tv_control_definition_bar.setOnClickListener(this);
        btn_control_definition_bar_landscape.setOnClickListener(this);
        tv_control_definition1.setOnClickListener(this);
        tv_control_definition2.setOnClickListener(this);
        tv_control_definition3.setOnClickListener(this);

        btn_replay.setOnClickListener(this);

        btn_csc_dismiss.setOnClickListener(this);
        btn_csc_restore_default.setOnClickListener(this);

        btn_album_new.setOnClickListener(this);
        btn_scene_new.setOnClickListener(this);

        ivEagleAlbum.setOnClickListener(this);
        ivEagleSilence.setOnClickListener(this);
        ivEagleSnapshot.setOnClickListener(this);
        ivEagleTalk.setOnClickListener(this);
        ivEagleTalk.setOnTouchListener(this);
        ivEagleClose.setOnClickListener(this);

        OnSeekBarChangeListener sbcListener = new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // Utils.sysoInfo("onProgressChanged");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Utils.sysoInfo("onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Utils.sysoInfo("onStopTrackingTouch");
                setCsc();
            }

        };
        sb_csc_contrast.setOnSeekBarChangeListener(sbcListener);
        sb_csc_definition.setOnSeekBarChangeListener(sbcListener);
        sb_csc_luminance.setOnSeekBarChangeListener(sbcListener);
        sb_csc_saturability.setOnSeekBarChangeListener(sbcListener);

        yuntaiBtn.setOnTouchListener(this);
        yuntai_btn_nospeak.setOnTouchListener(this);
        yuntai_btn_nospeak_landscape.setOnTouchListener(this);

        horizontal_sv.setOnScrollChangedListener(new OnScrollChangedListener() {

            @Override
            public void onScrollChanged(HorizontalScrollView sv, int l, int t,
                                        int oldl, int oldt) {
                updateAngleMeter(sv);
            }

        });
    }

    private void attachVideoPreview() {

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE,
                "com.sip.sipdemo.onIncomingCall");
        wakeLock.setReferenceCounted(false);

        if (cameraPreview == null) {
            cameraPreview = ViERenderer.CreateRenderer(this, true,
                    isVideoInvert);// 现在是取不到尺寸的
            int deviceHeight = Utils.getDeviceSize(this).heightPixels;
            int deviceWidth = Utils.getDeviceSize(this).widthPixels;
            int cameraPreviewHeight = deviceHeight * 4 / 9;// 根据布局中的上下比例
            int cameraPreviewWidth = (int) ((float) cameraPreviewHeight
                    / heightRatio * widthRatio);
            minWidth = Utils.getDeviceSize(this).widthPixels;
            maxWidth = cameraPreviewWidth;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    cameraPreviewWidth, cameraPreviewHeight);
            RelativeLayout.LayoutParams eagleLp = new RelativeLayout.LayoutParams(
                    deviceWidth, deviceHeight);

            lp.addRule(RelativeLayout.CENTER_IN_PARENT);// 全尺寸时居中显示
            if (isNewEagle) {//newEagle的视频界面大小
                rl_video.addView(cameraPreview, 0, eagleLp);
            } else {//企鹅、随便看视频界面大小
                rl_video.addView(cameraPreview, 0, lp);
            }
            is_portrait_fullSize = true;
            rl_video.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGestureDetector.onTouchEvent(event);// 手势双击
                    // mScaleGestureDetector.onTouchEvent(event);// 双指缩放功能暂时关闭
                    return true;// 自定义方向判断
                }
            });
            cameraPreview.setKeepScreenOn(true);
            if (videoWakeLock == null) {
                videoWakeLock = powerManager.newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                                | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                        "com.sip.sipdemo.videoCall");
                videoWakeLock.setReferenceCounted(false);
            }
        } else {
        }
        registerReceiver(callStateReceiver,
                new IntentFilter(SipManager.GET_ACTION_SIP_CALL_CHANGED()));// "com.wulian.siplibrary.icam.service.CALL_CHANGED"
    }

    private void showVideo() {
        // 1、初始化sip
        app.initSip();
        if (!device.getIs_lan() || !ICamGlobal.isPureLanModel) {// 非局域网设备||正常登陆
            // 2、用户注册账号
            account = app.registerAccount();
            if (account == null) {
                CustomToast.show(this,
                        R.string.login_user_account_register_fail);
                PlayVideoActivity.this.finish();
                return;
            }
        } else {// 局域网设备&&纯粹的局域网
            // account = new SipProfile();// 给个非空的实例，以兼容以前的判断逻辑
        }
        // 3、显示视频
        if (cameraPreview.getVisibility() == View.GONE) {
            cameraPreview.setVisibility(View.VISIBLE);
        }
    }

    private void reCallVideo() {
        isShowVideo = true;
        isConncted = false;// 先标记断开，等待状态通知改变
        isMediaPlaying = false;
        ll_linking_video_refresh.setVisibility(View.GONE);
        ll_linking_video.setVisibility(View.VISIBLE);
        tv_control_definition_bar.setText(getString(R.string.play_definition2));
        btn_control_definition_bar_landscape
                .setText(getString(R.string.play_definition2));
        if (deviceCallUrl != null) {
            ViERenderer.setIsReturnPictureState();
            makeCallDevice(device);
        }
    }

    private void hangUpVideo() {
        ll_linking_video.setVisibility(View.GONE);
        isShowVideo = false;
        isConncted = false;// 立即标记断开
        isMediaPlaying = false;
        if (callId != -1) {
            SipController.getInstance().setMicrophoneMute(true, callId);
            SipController.getInstance().setSpeakerphoneOn(false, callId);
        }
        ICamGlobal.getInstance().hangupAllCall();
        callId = -1;
        isRunOnUI = false;
        tv_speed.setText("0KB/s");
        tv_speed_landscape.setText("0KB/s");

        myHandler.removeMessages(SPEED_RETRY);
        myHandler.removeMessages(SHOWSPEED);
        myHandler.removeMessages(KEYFRAME);
        myHandler.removeMessages(AUTO_SILENCE);

        InputHandler.removeMessages(ENABLE);
        InputHandler.removeMessages(INENABLE);

        mHandler.removeMessages(ViERenderer.FILE_OK);
        mHandler.removeMessages(ViERenderer.TAKE_PICTURE_FAIL);
        mHandler.removeMessages(ViERenderer.FILE_MOUNT_EXCEPTION);
        mHandler.removeMessages(ViERenderer.FILE_PICTURE_CREATE_EXCEPTION);
        mHandler.removeMessages(ViERenderer.FILE_PICTURE_EXCEPTION);
        mHandler.removeMessages(SEND_RTP);
    }

    public void makeCallDevice(Device device) {
        if (!device.getIs_lan()) {// 正常登陆的非局域网设备
            if (app.registerAccount() != null) {
                Utils.sysoInfo("正常登陆的非局域网设备");
                ICamGlobal.getInstance().makeCall(deviceCallUrl,
                        app.registerAccount());
            }
        } else {// 局域网设备
            String ip = device.getIp();
            int video_port = device.getVideo_port();
            deviceCallIp = ip + ":" + video_port;
            String deviceId = device.getDevice_id().toLowerCase(Locale.ENGLISH);
            String pwd = spLan
                    .getString(deviceId + APPConfig.LAN_VIDEO_PWD, "");
            if (TextUtils.isEmpty(pwd)) {// 未设置则用默认密码:后6位小写
                pwd = deviceId.substring(deviceId.length() - 6);// 默认密码后6位小写
            }
            try {// 使用保存好的密码或默认密码
                pwd = MD5.MD52(pwd).toLowerCase(Locale.ENGLISH);
                devicePwd = pwd;
            } catch (Exception e) {
                e.printStackTrace();

            }
            if (video_port != -1) {// 局域网内设备支持局域网
                if (ICamGlobal.isPureLanModel) {// 纯局域网
                    Utils.sysoInfo("局域网内设备支持局域网-纯局域网");
                    ICamGlobal.getInstance().makeLocalCall(deviceCallIp, pwd,
                            null);
                } else {// 正常登陆
                    Utils.sysoInfo("局域网内设备支持局域网-正常登陆");
                    // 由于固件目前还不支持局域网指令，暂时使用原来的
                    ICamGlobal.getInstance().makeCall(deviceCallUrl,
                            app.registerAccount());
                }
            } else {// 局域网内设备不支持局域网
                if (!ICamGlobal.isPureLanModel) {// 非纯粹局域网模式（正常登陆），
                    // 不支持不是不看了，走原流程
                    Utils.sysoInfo("局域网内设备不支持局域网-正常登陆，走原流程");
                    if (app.registerAccount() != null) {
                        ICamGlobal.getInstance().makeCall(deviceCallUrl,
                                app.registerAccount());
                    }
                } else {// 纯粹局域网模式，不支持就提示
                    Utils.sysoInfo("局域网内设备不支持局域网-纯粹局域网模式，不支持就提示");
                    CustomToast.show(PlayVideoActivity.this,
                            R.string.lan_video_not_support);
                    hangUpVideo();
                }
            }
        }
    }

    class MyDirection implements YuntaiButton.OnDirectionLisenter {

        @Override
        public void directionLisenter(
                com.wulian.icam.view.widget.YuntaiButton.Direction direction) {
            yuntai_stop();
            ytHandler.removeMessages(YUNTAI_CONTROL);
            if (direction != com.wulian.icam.view.widget.YuntaiButton.Direction.none) {
                ytHandler.sendMessageDelayed(
                        Message.obtain(ytHandler, YUNTAI_CONTROL, direction),
                        500);
            }
        }
    }

    protected void scrollViewRight() {
        int newDeltaWidth = rl_video.getWidth() - minWidth;
        if (horizontal_sv.getScrollX() + SCROLLBY < newDeltaWidth) {
            horizontal_sv.smoothScrollBy(SCROLLBY, 0);
        }
    }

    protected void scrollViewLeft() {
        if (horizontal_sv.getScrollX() - SCROLLBY > 0) {
            horizontal_sv.smoothScrollBy(-SCROLLBY, 0);
        }

    }

    private void detachVideoPreview() {
        if (rl_video != null && cameraPreview != null) {
            rl_video.removeView(cameraPreview);
        }
        if (videoWakeLock != null && videoWakeLock.isHeld()) {
            videoWakeLock.release();
        }
        if (cameraPreview != null) {
            cameraPreview = null;
        }
    }

    //初始化绑定门锁的信息
    private void initDoorlockData(Context context) {
        String loginGwId;
        SharedPreferences gwIdSp = this.getSharedPreferences("doorLockGwId", MODE_PRIVATE);
        loginGwId = gwIdSp.getString("gwId", "");
        gwID = loginGwId;
        SharedPreferences bindSp = this.getSharedPreferences(loginGwId + device.getDevice_id().substring(8), MODE_PRIVATE);
        Log.i("------4",loginGwId+"--"+device.getDevice_id().substring(8));
        cameraId = bindSp.getString("cameraId", "-1");
        devID = bindSp.getString("devId", "-1");
        spPwd = bindSp.getString("password", "-1");
        spEpType = bindSp.getString("epType", "-1");
    }

    public void updateAngleMeter(HorizontalScrollView sv) {
        int newDeltaWidth = rl_video.getWidth() - minWidth;
        if (newDeltaWidth <= 0) {
            anglemeter.refreshAngle(0);
        } else {
            anglemeter.refreshAngle(sv.getScrollX() * 1.0 / newDeltaWidth);
        }
    }

    private BroadcastReceiver callStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Log.d("PML", "BroadcastReceiver callStateReceiver");
            if (action.equals(SipManager.GET_ACTION_SIP_CALL_CHANGED())) {// "com.wulian.siplibrary.icam.service.CALL_CHANGED"
                // 启用控制按钮。。。即使挂断也会触发changed方法。。。
                int key_found = intent.getIntExtra("key_found", -1);
                sipCallSession = (SipCallSession) intent
                        .getParcelableExtra("call_info");
                Utils.sysoInfo("key_found=" + key_found + " sp="
                        + sipCallSession);
                if (sipCallSession != null) {
                    int lastCode = sipCallSession.getLastStatusCode();
                    Utils.sysoInfo("sip状态码:" + lastCode);

                    switch (sipCallSession.getCallState()) {
                        case SipCallSession.InvState.INVALID:
                            // titlebar_title.setText(R.string.call_fail);
                            break;
                        case SipCallSession.InvState.CALLING:
                            // titlebar_title.setText(R.string.calling);
                            break;
                        case SipCallSession.InvState.INCOMING:
                        case SipCallSession.InvState.EARLY:
                        case SipCallSession.InvState.CONNECTING:
                            // titlebar_title.setText(R.string.linking);
                            break;
                        case SipCallSession.InvState.CONFIRMED:
                            // titlebar_title.setText(R.string.linked);
                            isConncted = true;
                            ll_linking_video_refresh.setVisibility(View.GONE);
                            Utils.sysoInfo("收到confirmed消息");
                            // mHandler.sendEmptyMessage(SEND_RTP);
                            break;
                        case SipCallSession.InvState.DISCONNECTED:
                            // titlebar_title.setText(R.string.breaked);
                            tv_speed.setText("0KB/s");
                            tv_speed_landscape.setText("0KB/s");
                            tv_speed.setVisibility(View.GONE);
                            tv_speed_landscape.setVisibility(View.GONE);
                            // pd_loading.dismiss();
                            ll_linking_video.setVisibility(View.GONE);
                            if (is_portrait) {
                                if (disconnectCount == 1 && !isStop) {
                                    // 第一次连接失败，有重连的机会，此时不显示刷新按钮
                                } else
                                    ll_linking_video_refresh
                                            .setVisibility(View.VISIBLE);
                            }
                            mHandler.removeMessages(SEND_RTP);
                            myHandler.removeMessages(SHOWSPEED);
                            // iv_control_play_or_pause
                            // .setBackgroundResource(R.drawable.selector_video_btn_play_new);//
                            // 后面可能无法收到播放消息，所以按钮没切换回来
                            // CustomToast.show(PlayVideoActivity.this, lastCode +
                            // "");
                            isConncted = false;
                            isShowVideo = false;
                            // if (isFirstCall) {// 有一次重呼机会
                            // isFirstCall = false;
                            // myHandler.sendEmptyMessageDelayed(
                            // SPEED_RETRY_FORCE, 1000);
                            // }
                            break;
                    }
                    switch (sipCallSession.getMediaStatus()) {
                        case SipCallSession.MediaState.NONE:
                            Utils.sysoInfo("media none");
                            // 可能遇到的设备返回的状态码提示
                            if (lastCode == 404) {
                                CustomToast.show(PlayVideoActivity.this,
                                        R.string.play_sip_server_not_found);
                            } else if (lastCode == 407) {
                                CustomToast
                                        .show(PlayVideoActivity.this,
                                                R.string.play_sip_proxy_authentication_required);
                            } else if (lastCode == 408 || lastCode == 480) {
                                CustomToast.show(PlayVideoActivity.this,
                                        R.string.play_sip_request_timeout);
                                disconnectCount++;
                                if (disconnectCount == 1 && !isStop) {// 有一次重呼机会
                                    hangUpVideo();
                                    reCallVideo();
                                }
                            } else if (lastCode == 486) {
                                if (!device.getIs_lan()) {// 正常登陆的非局域网设备
                                    try {
                                        if (retryFor486++ <= 1) {
                                            Thread.sleep(2000);
                                            reCallVideo();
                                        }
                                    } catch (InterruptedException e) {
                                        Log.e(TAG, "", e);
                                    }
                                    CustomToast.show(PlayVideoActivity.this,
                                            R.string.play_sip_device_busy);
                                } else {// 局域网设备
                                    if (ICamGlobal.isPureLanModel) {// 纯局域网内
                                        // 局域网设备
                                        String pwd = spLan.getString(
                                                device.getDevice_id()
                                                        + APPConfig.LAN_VIDEO_PWD,
                                                "");
                                        if (!TextUtils.isEmpty(pwd)) {// 如果保存过密码才弹出密码错误提示消息。未设置过的则不弹出（此时用默认密码进行尝试），提高用户首次使用的体验。
                                            CustomToast.show(
                                                    PlayVideoActivity.this,
                                                    R.string.lan_video_pwd_error);
                                        }
                                        showVideoPwd();
                                    } else {// 正常登陆 局域网设备 密码为userSipAccount
                                        CustomToast.show(PlayVideoActivity.this,
                                                R.string.play_sip_device_busy);
                                    }
                                }
                            } else if (lastCode == 487) {
                                CustomToast.show(PlayVideoActivity.this,
                                        R.string.play_sip_request_terminated);
                            } else if (lastCode > 500) {
                                CustomToast.show(PlayVideoActivity.this,
                                        R.string.play_sip_serve_error);
                            } else if (lastCode != 200 && lastCode != 0) {
                                if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
                                    CustomToast.show(PlayVideoActivity.this, "sip:"
                                            + lastCode);
                                }
                            }
                            break;
                        case SipCallSession.MediaState.LOCAL_HOLD:
                            Utils.sysoInfo("media local_hold");
                            break;

                        case SipCallSession.MediaState.REMOTE_HOLD:
                            Utils.sysoInfo("media remote_hold");
                            break;
                        case SipCallSession.MediaState.ACTIVE:// 有时候会延迟到来
                            Utils.sysoInfo("media active");
                            if (!isMediaPlaying)
                                if (sipCallSession.mediaHasVideo()) {
                                    callId = sipCallSession.getCallId();
                                    if (callId != -1) {
                                        SipController.getInstance()
                                                .closeAudioTransport(callId);// 关闭语音通道
                                    }
                                    boolean isWiredHeadsetOn = SipController
                                            .getInstance().isWiredHeadsetOn();
                                    SipController.getInstance().setSpeakerPhone(
                                            !isWiredHeadsetOn);

                                    String natStr = SipController.getInstance()
                                            .getCallNatInfos(callId);
                                    WulianLog.d("PlayVideo", TextUtils
                                            .isEmpty(natStr) ? "natStr null"
                                            : natStr);
                                    if (!TextUtils.isEmpty(natStr)) {
                                        try {
                                            JSONObject json = new JSONObject(natStr);
                                            WulianLog.d("PlayVideo",
                                                    "TextUtils.isEmpty no ");
                                            if (!json.isNull("video_ICEstate")) {
                                                WulianLog.d("PlayVideo",
                                                        "video_ICEstate no ");
                                                int layer = 0;
                                                if (json.getString("video_ICEstate")
                                                        .equals("Negotiation Success")
                                                        || json.getString(
                                                        "video_ICEstate")
                                                        .equals("Candidate Gathering")
                                                        || json.getString(
                                                        "video_ICEstate")
                                                        .equals("Negotiation Failed")) {
                                                    isNegotiationState = NEGOTIATION_SUCCESS;
                                                    isShowVideo = true;
                                                    isMediaPlaying = true;
                                                    isRunOnUI = true;
                                                    Utils.sysoInfo("sipCallSession.mediaHasVideo(),runOnUiThread");
                                                    runOnUiThread(uiUpdate);
                                                    if (myHandler
                                                            .hasMessages(SPEED_RETRY)) {
                                                        Utils.sysoInfo("检测到NEGOTIATION_SUCCESS,移除重呼消息");
                                                        myHandler
                                                                .removeMessages(SPEED_RETRY);
                                                    }
                                                    // InputHandler
                                                    // .sendEmptyMessageDelayed(
                                                    // ENABLE, 2000);
                                                    if (!json
                                                            .getString(
                                                                    "video_ICEstate")
                                                            .equals("Negotiation Failed")) {
                                                        WulianLog
                                                                .d("PlayVideo",
                                                                        "video_ICEstate Negotiation Success ");
                                                        String video_peer = json
                                                                .isNull("video_peer") ? ""
                                                                : json.getString("video_peer");
                                                        String audio_peer = json
                                                                .isNull("audio_peer") ? ""
                                                                : json.getString("audio_peer");
                                                        String video_addr_0_L = json
                                                                .isNull("video_addr_0_L") ? ""
                                                                : json.getString("video_addr_0_L");
                                                        String video_addr_0_R = json
                                                                .isNull("video_addr_0_R") ? ""
                                                                : json.getString("video_addr_0_R");
                                                        if (!TextUtils
                                                                .isEmpty(video_peer)
                                                                && !TextUtils
                                                                .isEmpty(audio_peer)) {
                                                            audio_peer = audio_peer
                                                                    .split(":")[0];
                                                            video_peer = video_peer
                                                                    .split(":")[0];
                                                            video_addr_0_L = video_addr_0_L
                                                                    .split(":")[0];
                                                            video_addr_0_R = video_addr_0_R
                                                                    .split(":")[0];
                                                            WulianLog
                                                                    .d("PlayVideo",
                                                                            "video_addr_0_L:"
                                                                                    + video_addr_0_L
                                                                                    + ";video_addr_0_R:"
                                                                                    + video_addr_0_R
                                                                                    + ";audio_peer:"
                                                                                    + audio_peer
                                                                                    + ";video_peer:"
                                                                                    + video_peer);
                                                            if (video_addr_0_L
                                                                    .equals(video_peer)) {
                                                                layer = 3333;
                                                            } else {
                                                                String oneIP = video_addr_0_L
                                                                        .split("\\.")[0];
                                                                if (oneIP
                                                                        .equals("10")
                                                                        || oneIP.equals("172")
                                                                        || oneIP.contains("192")) {
                                                                    layer = 1111;
                                                                } else {
                                                                    layer = 2222;// 包含移动网络
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else if (json.getString(
                                                        "video_ICEstate").equals(
                                                        "Negotiation In Progress")) {
                                                    isNegotiationState = NEGOTIATION_INPROGRESS;

                                                    Utils.sysoInfo("检测到 NEGOTIATION_INPROGRESS");
                                                    if (!myHandler
                                                            .hasMessages(SPEED_RETRY)) {
                                                        Utils.sysoInfo("发送延迟10秒重呼");
                                                        myHandler
                                                                .sendEmptyMessageDelayed(
                                                                        SPEED_RETRY,
                                                                        SPEED_RETRY_TIME);
                                                    } else {
                                                        Utils.sysoInfo("10秒重呼已经存在,不再发送");
                                                    }

                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (ICamGlobal.isPureLanModel
                                            && device.getIs_lan()) {
                                        isShowVideo = true;
                                        isMediaPlaying = true;
                                        isRunOnUI = true;
                                        runOnUiThread(uiUpdate);
                                    }
                                    Utils.sysoInfo("sp有视频流!callId=" + callId);
                                }

                            break;

                        case SipCallSession.MediaState.ERROR:
                            Utils.sysoInfo("media error");
                            break;

                        default:
                            break;
                    }
                } else {
                    if (key_found != -1 && !isRunOnUI) {
                    }
                }
            }
        }
    };

    /**
     * Update the user interface from calls state.
     **/
    private class UpdateUIFromCallRunnable implements Runnable {
        @Override
        public void run() {
            SipController.getInstance().setVideoAndroidRenderer(callId,
                    cameraPreview);
            SipController.getInstance().setEchoCancellation(true);
        }
    }

    class MyOperatorForV5Lisener implements OperatorForV5Lisener {
        @Override
        public void showProgressDialog() {
            showBaseDialog();
        }

        @Override
        public void requestOverTime() {
            myHandler.sendEmptyMessageDelayed(SCENE_OVERTIME, 22 * 1000);
        }

    }

    private SceneAdapter mSceneAdapter;
    private Dialog mSceneDialog;

    private void showSceneDialog() {
        Scene scene = Scene.getInstance();
        // 遍历更改status
        if (scene.getDataList() != null) {
            Iterator<SData> iterator = scene.getDataList().iterator();
            while (iterator.hasNext()) {
                Scene.SData data = (Scene.SData) iterator.next();
                if (data == null) {
                    CustomToast.show(this,
                            getResources().getString(R.string.scene_error));
                    return;
                }
            }
            if (scene.getDataList().size() > 0) {
                mSceneAdapter = new SceneAdapter(this, scene.getDataList());
                mSceneDialog = DialogUtils.showCommonGridViewDialog(this, true,
                        0, scene.getOnSelectionLisenter(),
                        new MyOperatorForV5Lisener(), mSceneAdapter);
            } else {
                CustomToast.show(this,
                        getResources().getString(R.string.scene_unset));
            }
        } else {
            CustomToast.show(this,
                    getResources().getString(R.string.scene_unset));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            if (isControling) {
                Utils.sysoInfo("左上角 退出时 还在控制云台");
                yuntai_stop();

            } else {
                // hangUpVideo();//导致退出截图失败
                this.finish();
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_right_out);
            }
        } else if (id == R.id.iv_control_fullscreen_bar
                || id == R.id.btn_control_landscape_to_portrait
                || id == R.id.ll_contain_fullscreen_btn
                || id == R.id.iv_control_fullscreen) {
            if (isPortrait()) {
                goLandscape();
            } else {
                goPortrait();
            }
        } else if (id == R.id.progress_refresh || id == R.id.iv_control_play) {
            if (is_portrait) {
                ll_linking_video_refresh.setVisibility(View.GONE);
            }
            if (isShowVideo && isConncted) {// CONFIRMED
                hangUpVideo();
                disconnectCount = 0;
            } else if (!isShowVideo && !isConncted) {// DISCONNECTED
                reCallVideo();
            }
        } else if (id == R.id.iv_control_csc_bar || id == R.id.iv_control_csc) {
            if (cscDialog.isShowing()) {
                cscDialog.dismiss();
            } else {
                cscDialog.show();
                cscDialog.setContentView(cscDialogView);
            }
        } else if (id == R.id.tv_control_definition_bar
                || id == R.id.btn_control_definition_bar_landscape) {
            // case R.id.tv_control_definition:
            if (popDefinitionWindow != null) {
                if (popDefinitionWindow.isShowing()) {
                    popDefinitionWindow.dismiss();
                } else {
                    if (cameraPreview != null) {
                        if (!is_portrait) {
                            popDefinitionWindow
                                    .showAsDropDown(
                                            btn_control_definition_bar_landscape,
                                            (btn_control_definition_bar_landscape
                                                    .getWidth() - tv_control_definition_bar
                                                    .getWidth()) / 2,
                                            -tv_control_definition_bar
                                                    .getHeight()
                                                    * 3
                                                    - btn_control_definition_bar_landscape
                                                    .getHeight() - 3);
                        } else {
                            popDefinitionWindow
                                    .showAsDropDown(tv_control_definition_bar,
                                            0, -tv_control_definition_bar
                                                    .getHeight() * 4 - 3);
                        }

                    }
                }
            }
        } else if (id == R.id.tv_control_definition1) {
            if (is_portrait) {
                if (tv_control_definition_bar.getText().equals(
                        tv_control_definition1.getText())) {
                    Utils.sysoInfo("相同清晰度1 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            } else {
                if (btn_control_definition_bar_landscape.getText().equals(
                        tv_control_definition1.getText())) {
                    Utils.sysoInfo("相同清晰度1 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            }
            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance().sendLocalMessage(
                        deviceCallIp,
                        SipHandler.ConfigEncode("sip:" + deviceCallIp, seq++,
                                "320x240", 15, 0), devicePwd, null);
            } else {
                SipController.getInstance().sendMessage(
                        deviceControlUrl,
                        SipHandler.ConfigEncode(deviceControlUrl, seq++,
                                "320x240", 15, 0), account);
            }
            tv_control_definition_bar
                    .setText(getString(R.string.play_definition1));
            btn_control_definition_bar_landscape
                    .setText(getString(R.string.play_definition1));
            popDefinitionWindow.dismiss();
        } else if (id == R.id.tv_control_definition2) {
            if (is_portrait) {
                if (tv_control_definition_bar.getText().equals(
                        tv_control_definition2.getText())) {
                    Utils.sysoInfo("相同清晰度2 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            } else {
                if (btn_control_definition_bar_landscape.getText().equals(
                        tv_control_definition2.getText())) {
                    Utils.sysoInfo("相同清晰度2 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            }
            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {

                SipController.getInstance().sendLocalMessage(
                        deviceCallIp,
                        SipHandler.ConfigEncode("sip:" + deviceCallIp, seq++,
                                "640x480", 15, 0), devicePwd, null);
            } else {
                SipController.getInstance().sendMessage(
                        deviceControlUrl,
                        SipHandler.ConfigEncode(deviceControlUrl, seq++,
                                "640x480", 15, 0), account);
            }
            tv_control_definition_bar
                    .setText(getString(R.string.play_definition2));
            btn_control_definition_bar_landscape
                    .setText(getString(R.string.play_definition2));
            popDefinitionWindow.dismiss();
        } else if (id == R.id.tv_control_definition3) {
            if (is_portrait) {
                if (tv_control_definition_bar.getText().equals(
                        tv_control_definition3.getText())) {
                    Utils.sysoInfo("相同清晰度3 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            } else {
                if (btn_control_definition_bar_landscape.getText().equals(
                        tv_control_definition3.getText())) {
                    Utils.sysoInfo("相同清晰度3 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            }
            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance().sendLocalMessage(
                        deviceCallIp,
                        SipHandler.ConfigEncode("sip:" + deviceCallIp, seq++,
                                "1280x720", 15, 0), devicePwd, null);
            } else {
                SipController.getInstance().sendMessage(
                        deviceControlUrl,
                        SipHandler.ConfigEncode(deviceControlUrl, seq++,
                                "1280x720", 15, 0), account);
            }
            tv_control_definition_bar
                    .setText(getString(R.string.play_definition3));
            btn_control_definition_bar_landscape
                    .setText(getString(R.string.play_definition3));
            popDefinitionWindow.dismiss();
        } else if (id == R.id.btn_csc_dissmis) {
            cscDialog.dismiss();
        } else if (id == R.id.btn_csc_restore_default) {
            sb_csc_luminance.setProgress(50);
            sb_csc_contrast.setProgress(50);
            sb_csc_saturability.setProgress(50);
            sb_csc_definition.setProgress(50);
            setCsc();
        } else if (id == R.id.btn_snapshot || id == R.id.btn_snapshot_nospeak
                || id == R.id.btn_control_snapshot_landscape
                || id == R.id.btn_snapshot_new || id == R.id.iv_eagle_snapshot) {

            new Thread(new Runnable() {//截图按钮不可过快点击，保存截图需要时间

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    btn_snapshot_new.setClickable(false);
                    try {
                        Thread.sleep(1500);
                        btn_snapshot_new.setClickable(true);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();

            // 判断是否挂载了SD卡
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                savePath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + APPConfig.ALBUM_DIR;
                File dir = new File(savePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            // 没有挂载SD卡，无法下载文件
            if (TextUtils.isEmpty(savePath)) {
                mHandler.sendEmptyMessage(ViERenderer.FILE_MOUNT_EXCEPTION);
                return;
            }
            snapSavePath = savePath + device.getDevice_id() + "/";
            ViERenderer.setTakePic(snapSavePath);
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer
                            .setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                    mediaPlayer.prepare();
                    mediaPlayer.setVolume(0.5f, 0.5f);
                    mediaPlayer.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.btn_talkback
                || id == R.id.btn_control_talkback_landscape
                || id == R.id.btn_speak_new
                || id == R.id.btn_speak_no_yuntai_new
                || id == R.id.iv_eagle_talk) {
            Utils.sysoInfo("对讲点击了");
        } else if (id == R.id.btn_control_silence_landscape
                || id == R.id.btn_silence || id == R.id.cb_silence || id == R.id.iv_eagle_silence) {
            if (!isConncted) {
                Utils.sysoInfo("静音 return ");
                return;
            }
            myHandler.removeMessages(AUTO_SILENCE);// 移除自动静音消息
            isMuteOpen = !isMuteOpen;// 首先就切换为新的状态值，而不是最后才切换。
            if (isMuteOpen) {
                btn_mute.setBackgroundResource(R.drawable.selector_function_silence_off);
                btn_control_silence_landscape
                        .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_off);
                iv_silence_new
                        .setBackgroundResource(R.drawable.desk_cb_silence_off);
                ivEagleSilence.setBackgroundResource(R.drawable.btn_neweagle_silence_off);
            } else {
                btn_mute.setBackgroundResource(R.drawable.selector_function_silence_on);
                btn_control_silence_landscape
                        .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_on);
                iv_silence_new
                        .setBackgroundResource(R.drawable.desk_cb_silence_on);
                ivEagleSilence.setBackgroundResource(R.drawable.btn_neweagle_silence_on);
            }
            // setMicrophoneMute这个方法内部逻辑与实际的相反！！！哎。
            SipController.getInstance().setMicrophoneMute(true, callId);
            SipController.getInstance().setSpeakerphoneOn(!isMuteOpen, callId);
            silenceControl(isMuteOpen);
            /*
             * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
			 * SipController.getInstance().sendLocalInfo( deviceCallIp,
			 * SipHandler.ConfigVoiceMute(deviceCallIp, seq, isMuteOpen ? "true"
			 * : "false"), callId, devicePwd, null); } else {
			 *
			 * SipController.getInstance().sendInfo( deviceCallUrl,
			 * SipHandler.ConfigVoiceMute(deviceCallUrl, seq, isMuteOpen ?
			 * "true" : "false"), callId, app.registerAccount());// 这里又不相反了，哎。 }
			 */
        } else if (id == R.id.btn_album_new || id == R.id.iv_eagle_album) {
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + APPConfig.ALBUM_DIR
                    + device.getDevice_id();
            AlbumEntity albumEntity = AlbumUtils.getAlbumEntityFromAFile(path);
            if (albumEntity == null) {
                CustomToast.show(this, R.string.album_empty_album_string);
            } else {
                // 进入相册
                albumEntity.setDeviceName(device.getDevice_nick());
                startActivity(new Intent(PlayVideoActivity.this,
                        AlbumGridActivity.class).putExtra("AlbumEntity",
                        albumEntity));
            }
        } else if (id == R.id.btn_scene_new) {
            showSceneDialog();
        } else if (id == R.id.btn_replay) {
            Intent it = new Intent();
            it.setClass(PlayVideoActivity.this, ReplayVideoActivity.class);
            it.putExtra("device", device);
            startActivity(it);
        } else if (id == R.id.btn_lock) {
            elist.clear();
            for (int i = 0; i < 9; i++) {
                elist.add(i + 1 + "");
            }
            if (cameraId.equals(device.getDevice_id().substring(8))) {
                showKeyboard(elist);
            } else {
                CustomToast.show(PlayVideoActivity.this, R.string.camera_please_bind);
            }
        } else if (id == R.id.iv_eagle_close) {
            finish();
        }
    }

    private void showKeyboard(List<String> eList) {
        final StringBuilder sb = new StringBuilder();
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_verify_password, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        popupWindow.setFocusable(true);
        GridView gridView = (GridView) contentView.findViewById(R.id.gv_num);
        CameraKeyBoardAdapter keyboardAdapter = new CameraKeyBoardAdapter(this, eList);
        gridView.setAdapter(keyboardAdapter);
        Button btnQuit = (Button) contentView.findViewById(R.id.btn_verify_cancel);
        Button btnSure = (Button) contentView.findViewById(R.id.btn_sure);
        Button btnDelOne = (Button) contentView.findViewById(R.id.btn_del_one);
        Button btnDelAll = (Button) contentView.findViewById(R.id.btn_del_all);
        Button btnNum0 = (Button) contentView.findViewById(R.id.btn_num_0);
        final EditText etPassword = (EditText) contentView.findViewById(R.id.et_password);
        hindKeyBoard(view);
        etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hindKeyBoard(v);
            }
        });

//
//        etPassword.setInputType(InputType.TYPE_NULL);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etPassword.setText(sb.append(position + 1 + ""));
                etPassword.setSelection(etPassword.getText().length());
            }
        });
        btnQuit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                password = etPassword.getText().toString();
                cameraSendCmdManager.sendDoorLockCmd(gwID, devID, spEpType, etPassword.getText().toString());
                mDialogManager.showDialog(SEND_CTR_CMD, PlayVideoActivity.this, null, null);
                popupWindow.dismiss();
            }
        });
        btnDelOne.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sb!=null && sb.length()>0){
                    sb.deleteCharAt(etPassword.getText().length() - 1);
                    etPassword.setText(sb);
                    etPassword.setSelection(etPassword.getText().length());
                }
            }
        });
        btnDelAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.delete(0, etPassword.getText().length());
                etPassword.setText(sb);
            }
        });
        btnNum0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword.setText(sb.append("0"));
                etPassword.setSelection(etPassword.getText().length());
            }
        });
//        popupWindow.showAsDropDown(btn_lock,0, 0,Gravity.BOTTOM);
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        if (popupWindow.isShowing()){
            view_control_background.setVisibility(View.VISIBLE);
            view_control_background.getBackground().setAlpha(100);
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_control_background.setVisibility(View.GONE);
            }
        });
    }


    /**
     * 强制隐藏键盘
     */
    private void hindKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
    }


    StringBuilder sb = new StringBuilder();

    public void setCsc() {
        sb.delete(0, sb.length());
        sb.append(sb_csc_luminance.getProgress()).append(",")
                .append(sb_csc_contrast.getProgress()).append(",")
                .append(sb_csc_saturability.getProgress()).append(",")
                .append(sb_csc_definition.getProgress());

        if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
            SipController.getInstance().sendLocalMessage(
                    deviceCallIp,
                    SipHandler.ConfigCSC("sip:" + deviceCallIp, seq++,
                            sb.toString()), devicePwd, null);
        } else {
            SipController.getInstance()
                    .sendMessage(
                            deviceControlUrl,
                            SipHandler.ConfigCSC(deviceControlUrl, seq++,
                                    sb.toString()), account);
        }
        Utils.sysoInfo("csc:" + sb.toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isLandscape()) {
                goPortrait();
            } else {
                if (isControling) {
                    Utils.sysoInfo("back键按下 退出时 还在控制云台");
                    yuntai_stop();
                } else {
                    // hangUpVideo();//导致退出截图失败
                    return super.onKeyDown(keyCode, event);
                }
            }
            return true;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                SipController.getInstance().AdjustCurrentVolume();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @Function 竖屏模式
     * @author Wangjj
     * @date 2014年12月4日
     */
    private void goPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        tv_speed.setVisibility(View.VISIBLE);
        flPortrait.setVisibility(View.VISIBLE);
        tv_speed_landscape.setVisibility(View.GONE);
        btn_titlebar_back.setVisibility(View.VISIBLE);
        is_portrait = true;
        registerSensorListener();
        anglemeter.setVisibility(View.VISIBLE);
        include_control_bar.setVisibility(View.VISIBLE);
        ll_control_panel_new.setVisibility(View.VISIBLE);
        if (popDefinitionWindow != null && popDefinitionWindow.isShowing()) {
            popDefinitionWindow.dismiss();
        }
        /*
         * if (hasSpeak) { rl_control_panel.setVisibility(View.VISIBLE); } else
		 * { rl_control_panel_nospeak.setVisibility(View.VISIBLE); }
		 */
        if (!isConncted) {
            // 横屏下，重新连接，显示ll_linking_video，切换到竖屏，不显示ll_linking_video_refresh
            if (ll_linking_video.getVisibility() == View.VISIBLE) {
                ll_linking_video_refresh.setVisibility(View.GONE);
            } else {
                ll_linking_video_refresh.setVisibility(View.VISIBLE);
            }
        }
        if (rl_control_landscape.getVisibility() == View.VISIBLE) {
            rl_control_landscape.setVisibility(View.GONE);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 以宽度 推测 高度
        RelativeLayout.LayoutParams lp = (LayoutParams) cameraPreview
                .getLayoutParams();
        lp.width = maxWidth;
        lp.height = (int) ((float) maxWidth / widthRatio * heightRatio);
        cameraPreview.setLayoutParams(lp);
        is_portrait_fullSize = true;
    }

    public void registerSensorListener() {
        if (!isSensorRegister) {
            sensorManager.registerListener(sensorEventListener,
                    origintationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isSensorRegister = true;
        }
    }

    public void unRegisterSeneorListener() {
        if (isSensorRegister) {
            sensorManager.unregisterListener(sensorEventListener);
            isSensorRegister = false;
        }
    }

    /**
     * @Function 横屏模式
     * @author Wangjj
     * @date 2014年12月4日
     */
    private void goLandscape() {
        if (!isNewEagle) {
            // 进入横屏模式 需要layout_weight
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            tv_speed.setVisibility(View.GONE);
            view_control_background.setVisibility(View.GONE);
            tv_speed_landscape.setVisibility(View.GONE);
            btn_titlebar_back.setVisibility(View.INVISIBLE);
            ll_linking_video_refresh.setVisibility(View.GONE);
            ll_control_panel_new.setVisibility(View.GONE);
            flPortrait.setVisibility(View.GONE);
            is_portrait = false;
            unRegisterSeneorListener();
            include_control_bar.setVisibility(View.GONE);
            if (hasSpeak) {
                rl_control_panel.setVisibility(View.GONE);
            } else {
                rl_control_panel_nospeak.setVisibility(View.GONE);
            }
            if (popDefinitionWindow != null && popDefinitionWindow.isShowing()) {
                popDefinitionWindow.dismiss();
            }
            anglemeter.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏

            RelativeLayout.LayoutParams lp = (LayoutParams) cameraPreview
                    .getLayoutParams();
            lp.width = Utils.getDeviceSize(this).widthPixels;
            // 以屏幕宽度 推测 高度
//            lp.height = (int) ((float) lp.width / widthRatio * heightRatio);
            lp.height = Utils.getDeviceSize(this).heightPixels;//add by hxc 上面的用屏幕宽度推测高度会导致有虚拟按键的手机横屏产生白边
            Utils.sysoInfo(lp.width + "--" + lp.height + " 布局容器:"
                    + ll_play_container.getWidth() + "--"
                    + ll_play_container.getHeight());
            cameraPreview.setLayoutParams(lp);
        }
    }

    int defalutAngle = 30;
    int defalutDistance = 10;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isConncted) {
            return false;
        }
        int id = v.getId();
        if (id == R.id.btn_control_talkback_landscape
                || id == R.id.btn_talkback || id == R.id.btn_speak_new
                || id == R.id.btn_speak_no_yuntai_new || id == R.id.iv_eagle_talk) {
            // v.performClick();//move时，导致不断调用
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    btn_talkback.setText(R.string.play_loosen_end);
                    btn_speak_new.setText(R.string.play_loosen_end);
                    btn_speak_no_yuntai_new.setText(R.string.play_loosen_end);
                    if (!isConncted) {
                        Utils.sysoInfo("语音 return ");
                        return false;
                    }
                    Utils.sysoInfo("按下对讲，可以讲话了");
                    WulianLog.d("PML", "sendInfo is:isPureLanModel"
                            + ICamGlobal.isPureLanModel);
                /*
                 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
				 * SipController.getInstance().sendLocalInfo( deviceCallIp,
				 * SipHandler.ConfigVoiceMute(deviceCallIp, seq, "true"),
				 * callId, devicePwd, null); } else {
				 * SipController.getInstance().sendInfo( deviceCallUrl,
				 * SipHandler.ConfigVoiceMute(deviceCallUrl, seq, "true"),
				 * callId, app.registerAccount());// 这里又不相反了，哎。 }
				 */
                    silenceControl(true);
                    isMuteOpen = true;
                    // ui变化 声音off
                    btn_mute.setBackgroundResource(R.drawable.selector_function_silence_off);
                    btn_control_silence_landscape
                            .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_off);
                    iv_silence_new
                            .setBackgroundResource(R.drawable.desk_cb_silence_off);
                    ivEagleSilence.setBackgroundResource(R.drawable.btn_neweagle_silence_off);
                    myHandler.removeMessages(AUTO_SILENCE);
                    SipController.getInstance().setMicrophoneMute(!isMuteOpen,
                            callId);
                    SipController.getInstance().setSpeakerphoneOn(!isMuteOpen,
                            callId);
                /*
                 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
				 * SipController.getInstance().sendLocalInfo( deviceCallIp,
				 * SipHandler.ConfigVoiceIntercom(deviceCallIp, seq, "input"),
				 * callId, devicePwd, null); } else {
				 * SipController.getInstance().sendInfo( deviceCallUrl,
				 * SipHandler.ConfigVoiceIntercom(deviceCallUrl, seq, "input"),
				 * callId, app.registerAccount()); }
				 */
                    configVoiceIntercom("input");

                    break;
                case MotionEvent.ACTION_UP:
                    btn_talkback.setText(R.string.play_press_talk);
                    btn_speak_new.setText(R.string.play_press_talk);
                    btn_speak_no_yuntai_new.setText(R.string.play_press_talk);
                    if (!isConncted) {
                        Utils.sysoInfo("语音 return ");
                        return false;
                    }
                    Utils.sysoInfo("松开对讲，结束讲话");
                    isMuteOpen = false;
                    // ui变化 声音on
                    btn_mute.setBackgroundResource(R.drawable.selector_function_silence_on);
                    btn_control_silence_landscape
                            .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_on);
                    iv_silence_new
                            .setBackgroundResource(R.drawable.desk_cb_silence_on);
                    ivEagleSilence.setBackgroundResource(R.drawable.btn_neweagle_silence_on);
                    SipController.getInstance().setMicrophoneMute(!isMuteOpen,
                            callId);
                    SipController.getInstance().setSpeakerphoneOn(!isMuteOpen,
                            callId);

				/*
                 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
				 * SipController.getInstance().sendLocalInfo( deviceCallIp,
				 * SipHandler.ConfigVoiceIntercom(deviceCallIp, seq, "output"),
				 * callId, devicePwd, null); } else {
				 * SipController.getInstance().sendInfo( deviceCallUrl,
				 * SipHandler.ConfigVoiceIntercom(deviceCallUrl, seq, "output"),
				 * callId, app.registerAccount()); }
				 */
                    configVoiceIntercom("output");

                    myHandler.sendEmptyMessageDelayed(AUTO_SILENCE,
                            AUTO_SILENCE_TIME * 1000);
                    break;
                default:
                    break;
            }
            return false;
        }
        return false;
    }

    public void yuntai_stop() {
        if (!hasYuntai) {
            return;
        }
        stopMove();
    }

    public void yuntai_left() {
        if (!hasYuntai) {
            return;
        }
        if (isControling) {
            Utils.sysoInfo("按下 span时间已过,但控制中,return");
            return;
        }
        Utils.sysoInfo("按下 control_left");
        isControling = true;

        if (!isVideoInvert) {
            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance().sendLocalInfo(
                        deviceCallIp,
                        SipHandler.ControlPTZMovement(deviceCallIp,
                                -APPConfig.MOVE_SPEED, 0), callId, devicePwd,
                        null);
            } else {
                SipController.getInstance().sendInfo(
                        deviceCallUrl,
                        SipHandler.ControlPTZMovement(deviceControlUrl,
                                -APPConfig.MOVE_SPEED, 0), callId, account);
            }

        } else {
            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance().sendLocalInfo(
                        deviceCallIp,
                        SipHandler.ControlPTZMovement(deviceCallIp,
                                APPConfig.MOVE_SPEED, 0), callId, devicePwd,
                        null);
            } else {
                SipController.getInstance().sendInfo(
                        deviceCallUrl,
                        SipHandler.ControlPTZMovement(deviceControlUrl,
                                APPConfig.MOVE_SPEED, 0), callId, account);
            }

        }
    }

    public void yuntai_down() {
        if (!hasYuntai) {
            return;
        }
        if (isControling) {
            Utils.sysoInfo("按下 span时间已过,但控制中,return");
            return;
        }
        Utils.sysoInfo("按下 control_down");
        isControling = true;
        if (!isVideoInvert) {

            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance()
                        .sendLocalInfo(
                                deviceCallIp,
                                SipHandler.ControlPTZMovement(deviceCallIp, 0,
                                        -APPConfig.MOVE_SPEED), callId,
                                devicePwd, null);
            } else {
                SipController.getInstance().sendInfo(
                        deviceCallUrl,
                        SipHandler.ControlPTZMovement(deviceControlUrl, 0,
                                -APPConfig.MOVE_SPEED), callId, account);
            }

        } else {

            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance().sendLocalInfo(
                        deviceCallIp,
                        SipHandler.ControlPTZMovement(deviceCallIp, 0,
                                APPConfig.MOVE_SPEED), callId, devicePwd, null);
            } else {
                SipController.getInstance().sendInfo(
                        deviceCallUrl,
                        SipHandler.ControlPTZMovement(deviceControlUrl, 0,
                                APPConfig.MOVE_SPEED), callId, account);
            }

        }
    }

    public void yuntai_right() {
        if (!hasYuntai) {
            return;
        }
        if (isControling) {
            Utils.sysoInfo("按下 span时间已过,但控制中,return");
            return;
        }
        Utils.sysoInfo("按下 control_right");
        isControling = true;
        if (!isVideoInvert) {

            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance().sendLocalInfo(
                        deviceCallIp,
                        SipHandler.ControlPTZMovement(deviceCallIp,
                                APPConfig.MOVE_SPEED, 0), callId, devicePwd,
                        null);
            } else {
                SipController.getInstance().sendInfo(
                        deviceCallUrl,
                        SipHandler.ControlPTZMovement(deviceControlUrl,
                                APPConfig.MOVE_SPEED, 0), callId, account);
            }

        } else {

            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance().sendLocalInfo(
                        deviceCallIp,
                        SipHandler.ControlPTZMovement(deviceCallIp,
                                -APPConfig.MOVE_SPEED, 0), callId, devicePwd,
                        null);
            } else {
                SipController.getInstance().sendInfo(
                        deviceCallUrl,
                        SipHandler.ControlPTZMovement(deviceControlUrl,
                                -APPConfig.MOVE_SPEED, 0), callId, account);
            }

        }
    }

    public void yuntai_up() {
        if (!hasYuntai) {
            return;
        }
        if (isControling) {
            Utils.sysoInfo("按下 span时间已过,但控制中,return");
            return;
        }
        Utils.sysoInfo("按下 control_up");
        isControling = true;
        if (!isVideoInvert) {

            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance().sendLocalInfo(
                        deviceCallIp,
                        SipHandler.ControlPTZMovement(deviceCallIp, 0,
                                APPConfig.MOVE_SPEED), callId, devicePwd, null);
            } else {

                SipController.getInstance().sendInfo(
                        deviceCallUrl,
                        SipHandler.ControlPTZMovement(deviceControlUrl, 0,
                                APPConfig.MOVE_SPEED), callId, account);
            }

        } else {

            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance()
                        .sendLocalInfo(
                                deviceCallIp,
                                SipHandler.ControlPTZMovement(deviceCallIp, 0,
                                        -APPConfig.MOVE_SPEED), callId,
                                devicePwd, null);
            } else {
                SipController.getInstance().sendInfo(
                        deviceCallUrl,
                        SipHandler.ControlPTZMovement(deviceControlUrl, 0,
                                -APPConfig.MOVE_SPEED), callId, account);
            }

        }
    }

    private void stopMove() {
        if (isControling) {
            Utils.sysoInfo("抬起 stopMove,stop control");
            isControling = false;
            if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
                SipController.getInstance().sendLocalInfo(deviceCallIp,
                        SipHandler.ControlPTZMovement(deviceCallIp, 0, 0),
                        callId, devicePwd, null);
            } else {
                SipController.getInstance().sendInfo(deviceCallUrl,
                        SipHandler.ControlPTZMovement(deviceControlUrl, 0, 0),
                        callId, account);
            }

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isLandscape()) {
            if (is_portrait) {
                goLandscape();
            }
        } else if (isPortrait()) {
            if (!is_portrait) {
                goPortrait();
            }
        }
    }

    public boolean isLandscape() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public boolean isPortrait() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private void unregisterReceiver() {
        this.unregisterReceiver(judgepwdRecevier);
    }

    private void registerReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("sendepData");
        mFilter.addAction("send406Data");
        registerReceiver(judgepwdRecevier, mFilter);
    }

    private BroadcastReceiver judgepwdRecevier = new BroadcastReceiver() {//此广播是用来验证开锁密码 add by hxc
        @Override
        public void onReceive(Context context, Intent intent) {//TODO 这边3中门锁数据返回进行处理，需要优化一下，写的比较乱
            String action = intent.getAction();
//            Log.i(TAG, epdata + "------" + eptype);
            mDialogManager.dimissDialog(SEND_CTR_CMD, 0);
            if (action.equals("sendepData")) {
                String epdata = intent.getStringExtra("epData");
                String eptype = intent.getStringExtra("epType");
                if (epdata.equals("144")) {
                    cameraSendCmdManager.sendOpen70Cmd(gwID, devID);
                } else if (epdata.equals("145")) {
                    CustomToast.show(PlayVideoActivity.this, getString(R.string.home_password_error), 1000);
                } else if (epdata.equals("1") && eptype.equals("70")) {
                    btn_lock.setBackgroundResource((R.drawable.btn_camera_unlock));
                    CustomToast.show(PlayVideoActivity.this, getResources().getString(R.string.smartLock_lock_open), 3000);

                } else if (epdata.equals("2") && eptype.equals("70")) {
                    btn_lock.setBackgroundResource((R.drawable.btn_camera_lock_normal));
                } else if (epdata.equals("0220") && eptype.equals("OW")) {
                    sendDoorlockCmd.sendControlDevMsg(gwID, devID, spEpType, "5" + spPwd.length() + spPwd);
                } else if (epdata.startsWith("0807") && eptype.equals("OW")) {
                    btn_lock.setBackgroundResource((R.drawable.btn_camera_unlock));
                    CustomToast.show(PlayVideoActivity.this, getResources().getString(R.string.smartLock_lock_open), 3000);
                } else if (epdata.equals("0102") && eptype.equals("OW")) {
                    btn_lock.setBackgroundResource((R.drawable.btn_camera_lock_normal));
                } else if (epdata.equals("1") && eptype.equals("69")) {
                    btn_lock.setBackgroundResource((R.drawable.btn_camera_unlock));
                    CustomToast.show(PlayVideoActivity.this, getResources().getString(R.string.smartLock_lock_open), 3000);
                } else if (epdata.equals("2") && eptype.equals("69")) {
                    btn_lock.setBackgroundResource((R.drawable.btn_camera_lock_normal));
                    btn_lock.setBackgroundResource((R.drawable.btn_camera_lock_normal));
                }
            } else if (action.equals("send406Data")) {
                String data = intent.getStringExtra("406Data");
                String md5Password = "";
                if (!StringUtil.isNullOrEmpty(password)){
                    md5Password = MD5Utils.encrypt(password);
                }
                if (StringUtil.isNullOrEmpty(md5Password)) {
                    CustomToast.show(PlayVideoActivity.this, "请先去设备详情中设置密码", 3000);
                } else if (data.equals(MD5Utils.encrypt(password))) {
                    cameraSendCmdManager.sendOpen69Cmd(gwID, devID);
                    mDialogManager.showDialog(SEND_CTR_CMD, PlayVideoActivity.this, null, null);
                } else {
                    CustomToast.show(PlayVideoActivity.this, getString(R.string.device_state_password_mistake), 3000);
                }
            }
        }
    };


    HeadsetPlugReceiver headsetPlugReceiver;

    private void registerHeadsetPlugReceiver() {
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    WulianLog.d("PML", "HeadsetPlugReceiver is 0");
                    SipController.getInstance().setSpeakerPhone(true);
                } else if (intent.getIntExtra("state", 0) == 1) {
                    WulianLog.d("PML", "HeadsetPlugReceiver is 1");
                    SipController.getInstance().setSpeakerPhone(false);
                } else {
                    WulianLog.d(
                            "PML",
                            "HeadsetPlugReceiver is XXX:"
                                    + intent.getIntExtra("state", 0));
                }
            }
        }
    }

    AlertDialog dialogPwdName;
    View dialogPwdView;

    @SuppressLint("NewApi")
    public void showVideoPwd() {
        if (dialogPwdName == null) {
            dialogPwdName = new AlertDialog.Builder(this, R.style.alertDialog)
                    .create();
        }

        if (dialogPwdView == null) {
            dialogPwdView = LinearLayout.inflate(this,
                    R.layout.custom_alertdialog_lan_pwd,
                    (ViewGroup) findViewById(R.id.ll_custom_alertdialog));
            final EditText et_input = (EditText) dialogPwdView
                    .findViewById(R.id.et_input);
            et_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                    20)});
            ((Button) dialogPwdView.findViewById(R.id.btn_positive))
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String pwd = et_input.getText().toString().trim();
                            if (!TextUtils.isEmpty(pwd)) {
                                spLan.edit()
                                        .putString(
                                                device.getDevice_id()
                                                        + APPConfig.LAN_VIDEO_PWD,
                                                pwd).commit();
                                reCallVideo();
                                dialogPwdName.dismiss();
                            } else {
                                Utils.shake(PlayVideoActivity.this, et_input);
                            }
                        }
                    });
            ((Button) dialogPwdView.findViewById(R.id.btn_negative))
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialogPwdName.dismiss();
                        }
                    });
        }
        dialogPwdName.show();
        dialogPwdName.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogPwdName.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialogPwdName.setContentView(dialogPwdView);

    }

    private void silenceControl(boolean flag) {
        if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
            SipController.getInstance().sendLocalInfo(
                    deviceCallIp,
                    SipHandler.ConfigVoiceMute(deviceCallIp, seq, flag ? "true"
                            : "false"), callId, devicePwd, null);
        } else {
            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler.ConfigVoiceMute(deviceCallUrl, seq,
                            flag ? "true" : "false"), callId,
                    app.registerAccount());// 这里又不相反了，哎。
        }
    }

    private void configVoiceIntercom(String function) {
        if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
            SipController.getInstance()
                    .sendLocalInfo(
                            deviceCallIp,
                            SipHandler.ConfigVoiceIntercom(deviceCallIp, seq,
                                    function), callId, devicePwd, null);
        } else {
            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler
                            .ConfigVoiceIntercom(deviceCallUrl, seq, function),
                    callId, app.registerAccount());
        }
    }


}
