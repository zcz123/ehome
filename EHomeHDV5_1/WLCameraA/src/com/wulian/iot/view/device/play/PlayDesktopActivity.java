package com.wulian.iot.view.device.play;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.MediaCodecMonitor;
import com.wulian.icam.R;
import com.wulian.icam.model.Scene;
import com.wulian.icam.model.Scene.SData;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.DialogUtils.OperatorForV5Lisener;
import com.wulian.icam.utils.DialogUtils.SceneAdapter;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.utils.StringUtil;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.IOTCDevChPojo;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.bean.PresettingModel;
import com.wulian.iot.cdm.action.CameraAction;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.controller.CamPresetting;
import com.wulian.iot.server.controller.logic.CamPresettingLogicImpl;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.server.queue.MessageQueue;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.utils.Rotate;
import com.wulian.iot.utils.TimeUtil;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.view.manage.PresettingManager;
import com.wulian.iot.view.manage.PresettingManager.OnItemMenuClickListener;
import com.wulian.iot.widght.AdjustmentDirectionButton;
import com.wulian.iot.widght.AdjustmentDirectionButton.OnChangeListener;
import com.wulian.iot.widght.DialogRealize;
import com.yuantuo.customview.ui.WLToast;

import java.util.Iterator;

/**
 * 桌面摄像机播放ui
 *
 * @author sunyf
 */
@SuppressLint("NewApi")
public class PlayDesktopActivity extends SimpleFragmentActivity implements
        OnClickListener, OnChangeListener {
    // 初始化摄像机工厂方法
    private CameraAction cameraAction = null;
    private final int mAVChannel = 0;
    private ImageView callBack;
    private Button snapshotBtn, speakBtn, btnPresetPosition;
    // 横屏幕时使用 隐藏布局
    private FrameLayout content;
    private LinearLayout conBar;
    private AdjustmentDirectionButton adbtn;
    // 场景使用
    private Button selectorFunctionShowScene, silenceBtn;
    private static final int SCENE_OVERTIME = 111;
    private boolean mIsListening = true;
    private SceneAdapter mSceneAdapter;
    private Dialog mSceneDialog;
    // 横屏
    private TextView rotatorTxt;
    //环境检测
    private TextView tvEnvironment;
    private boolean isEnvironmentChecked;
    private String D4Msg = "";
    private String D5Msg = "";
    private String D6Msg = "";
    private String A0Msg = "";
    private String T17Msg1 = "";
    private String T17Msg2 = "";
    private String showMsg = "";
    // 历史记录
    private CheckBox history;
    private LinearLayout tvHistory;
    private Button recordingBtn;
    private TextView tvHistText;
    // 预置位
    private LinearLayout linFoot;
    // 分辨率
    protected TextView tvDefinition, tv_control_definition1,
            tv_control_definition2, tv_control_definition3,
            tv_control_definition4;
    private TextView tvCameraName;
    private PopupWindow popDefinitionWindow;
    private View popDefinitionView;
    private Rotate mDetector = null;
    private boolean accord = true;
    private int touchSpeakCount = 0;
    private int changeCounts = 0;
    private boolean isStart = false;
    private TextView tvTime;
    private ImageView ivTime;
    private LinearLayout linTime;
    private TimeUtil timeUtil = new TimeUtil();
    //横屏下控制布局
    private RelativeLayout rlLandscape;
    private ImageView ivLandscapeSnapshot;
    private ImageView ivLandscapeSilence;
    private ImageView ivLandscapeTalk;
    private ImageView ivLandscapeVideo;

    //横屏转动常量
    private static final int CAMERA_ROTATE_STOP = 0;
    private static final int CAMERA_ROTATE_LEFT = 1;
    private static final int CAMERA_ROTATE_RIGHT = 2;
    private boolean isLandScape = false;
    private static IOTCameraBean cInfo = null;
    GestureDetector mGestureDetector;
    private PopupWindow rotatePopupWindow;
    private View popRotateView;
    // add syf 移动侦测 默认每次打开设备给用户自动截取默认内容
    // add by hu
    private int avIOCtrlMsgTypeRotate;
    private PlayDesktopActivity instance = null;//add syf
    private Handler mHandler = null;//add syf 统一handler
    private CamPresetting camPresettingImpl = null;// 预置位 add syf
    private String presettingPath = null;//add syf 预置位路径
    private PresettingManager presettingManager = null;
    private String tutkUid, tutkPwd;
    private String epData;//D4D5D6设备的epdata(环境检测功能)
    private String epType;//判断何种设备类型
    private String environmentText = "";
    private MediaCodecMonitor monitor = null;
    private MessageQueue messageQueue = null;
    private int decodingWay = -1;
    private Callback deskCamerHandler = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SCENE_OVERTIME:
                    dismissBaseDialog();
                    CustomToast.show(PlayDesktopActivity.this, getResources().getString(R.string.scene_timeout));
                    break;
                case HandlerConstant.DEVICE_ONLINE:
                    DialogRealize.unInit().dismissDialog();
                    break;
                case HandlerConstant.ERROR:
                    WLToast.showToast(instance, getResources().getString(R.string.iot_link_data_error), Toast.LENGTH_SHORT);
                    break;
            }
            return false;
        }
    };
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
        }

        @Override
        public void avIOCtrlMsg(int resCode, String method) {
            final String msg = messageQueue.filter(resCode, method).sendMsg();
            if (msg != null && !msg.equals("")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlayDesktopActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    private BroadcastReceiver environmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("sendepData")) {
                epData = intent.getStringExtra("epData");
                epType = intent.getStringExtra("epType");
                String environmentText = handleDataByEpInfo(epData, epType);
                tvEnvironment.setText(environmentText);
            }
        }
    };
    private BroadcastReceiver myNetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("playDeskScene")) {
                dismissBaseDialog();
            }
        }
    };

    private void registerBroadcast() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("playDeskScene");
        mFilter.addAction("sendepData");
        registerReceiver(myNetReceiver, mFilter);
        registerReceiver(environmentReceiver, mFilter);
    }


    private String handleDataByEpInfo(String epData, String epType) {
        try {
            int deviceValue = Integer.parseInt(epData.substring(epData.length() - 4, epData.length()).trim(), 16);
            Log.i(TAG, "epData=" + epData.substring(epData.length() - 4, epData.length()));

            switch (epType) {
                case "D4":
                    D4Msg = deviceValue + "";
                    break;
                case "D5":
                    D5Msg = deviceValue + "";
                    break;
                case "D6":
                    D6Msg = deviceValue + "";
                    break;
                case "A0":
                    A0Msg = deviceValue + "";
                    break;
                case "17":
                    String data[] = epData.split(",");
                    T17Msg1 = data[0];
                    T17Msg2 = data[1];
                    break;
                default:
                    break;
            }

            showMsg = getResources().getString(R.string.device_noise_unit_name) + ":" + D4Msg + "dB" + ";"
                    + getResources().getString(R.string.device_pm2p5) + ":" + D5Msg + "ug/m³" + ";"
                    + getResources().getString(R.string.device_type_20) + ":" + D6Msg + "ppb" + ";"
                    + getResources().getString(R.string.device_quality)+":" + A0Msg + "PPM" + ";"
                    + getResources().getString(R.string.device_temphum) + ":" + T17Msg1 + "℃" + T17Msg2 + "%RH" + ";";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return showMsg;
    }


    @Override
    public void root() {
        instance = this;
        setContentView(R.layout.device_desktop_camera);
        PlayDesktopActivity.this.registerBroadcast();//注册广播
        PlayDesktopActivity.this.initPopWindow();
    }

    @Override
    public void initData() {
        mHandler = new Handler(deskCamerHandler);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        cInfo = (IOTCameraBean) getIntent().getSerializableExtra(Config.deskBean);
        isEnvironmentChecked = sharedPreferences.getBoolean("status", false);
        if (isEnvironmentChecked) {
            tvEnvironment.setVisibility(View.VISIBLE);
        }
        if (cInfo != null) {
            if (cInfo.getUid() == null) {
                mHandler.sendEmptyMessage(HandlerConstant.ERROR);
                return;
            }
            tutkUid = cInfo.getUid();
            if (cInfo.getPassword() == null) {
                mHandler.sendEmptyMessage(HandlerConstant.ERROR);
                return;
            }
            tutkPwd = cInfo.getPassword();
            presettingPath = IotUtil.getPresetInfoPath(cInfo.getGwId());
            PlayDesktopActivity.this.setDecodingWay(IotUtil.selectDecode());
            if (StringUtil.isNullOrEmpty(cInfo.camName)) {
                tvCameraName.setText(getResources().getString(R.string.setting_detail_device_06));
            } else {

                tvCameraName.setText(cInfo.camName);
            }
            return;
        }
        mHandler.sendEmptyMessage(HandlerConstant.ERROR);
    }

    private void setDecodingWay(int decodingWay) {
        this.decodingWay = decodingWay;
    }

    private int getDecodingWay() {
        return decodingWay;
    }

    @Override
    public void onResume() {
        super.onResume();
        DialogRealize.init(instance).showDiglog();
        if (mDetector == null) {
            registerScreenListener();
        }
        if (cameraAction == null) {
            cameraAction = new CameraAction(instance);
        }
        if (camPresettingImpl == null) {
            camPresettingImpl = new CamPresettingLogicImpl(instance);
        }
        PlayDesktopActivity.this.startPlaySurfaceView();

    }

    public void registerScreenListener() {
        mDetector = new Rotate(this) {
            @Override
            public void callBack(boolean mScreenOrientation) {
                screen(mScreenOrientation);
            }
        };
        mDetector.enable();
    }

    public void screen(boolean mScreenOrientation) {
        if (accord) {
            if (mScreenOrientation) {
                goPortrait();
                if (!mDetector.isScreenChange()) {
                    content.setVisibility(View.VISIBLE);
                    conBar.setVisibility(View.VISIBLE);
                }
            } else {
                goLandscape();
                if (!mDetector.isScreenChange()) {
                    content.setVisibility(View.GONE);
                    conBar.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void initView() {
        monitor = (MediaCodecMonitor) this.findViewById(R.id.monitor);
        callBack = (ImageView) this.findViewById(R.id.titlebar_back);
        snapshotBtn = (Button) this.findViewById(R.id.btn_snapshot);

        tvTime = (TextView) this.findViewById(R.id.tv_videotape_time);
        ivTime = (ImageView) this.findViewById(R.id.iv_videotape_time);
        linTime = (LinearLayout) this.findViewById(R.id.lin_videotape_time);

        adbtn = (AdjustmentDirectionButton) findViewById(R.id.button_adjust_direction);
        selectorFunctionShowScene = (Button) findViewById(R.id.selector_function_show_scene);
        speakBtn = (Button) findViewById(R.id.btn_speak_no_yuntai_new);
        history = (CheckBox) findViewById(R.id.cb_history);
        recordingBtn = (Button) findViewById(R.id.btn_scene_new);
        recordingBtn.setTag("start");
        btnPresetPosition = (Button) findViewById(R.id.btn_preset_position);
        linFoot = (LinearLayout) findViewById(R.id.lin_foot_play);
        silenceBtn = (Button) findViewById(R.id.cb_silence);
        rotatorTxt = (TextView) findViewById(R.id.tv_control_fullscreen_bar);
        content = (FrameLayout) findViewById(R.id.content_frame_layout);
        conBar = (LinearLayout) findViewById(R.id.include_control_bar);
        // add syf 分辨率
        tvDefinition = (TextView) findViewById(R.id.tv_control_definition_bar);
        tvHistory = (LinearLayout) findViewById(R.id.tv_videotape_history);
        tvHistText = (TextView) findViewById(R.id.tv_videotape_histroy_text);
        tvCameraName = (TextView) findViewById(R.id.tv_video_play_timeorname);

        popDefinitionView = getLayoutInflater().inflate(
                R.layout.control_definition_desk, null);
        tv_control_definition1 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition1);
        tv_control_definition2 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition2);
        tv_control_definition3 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition3);
        tv_control_definition4 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition4);
        tv_control_definition4.setVisibility(View.VISIBLE);
        popDefinitionWindow = new PopupWindow(popDefinitionView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        popDefinitionWindow.setAnimationStyle(R.style.bottom_menu_scale);
        tvEnvironment = (TextView) findViewById(R.id.tv_environment);
        rlLandscape = (RelativeLayout) findViewById(R.id.rl_control_landscape);
        ivLandscapeSilence = (ImageView) findViewById(R.id.desk_landscape_silence);
        ivLandscapeSnapshot = (ImageView) findViewById(R.id.desk_landscape_snapshot);
        ivLandscapeTalk = (ImageView) findViewById(R.id.desk_landscape_talk);
        ivLandscapeVideo = (ImageView) findViewById(R.id.desk_landscape_videotape);
        popRotateView = getLayoutInflater().inflate(R.layout.device_desktop_landscape_introduction, null);
        if (IotUtil.selectDecode() == Camera.SOFT_DECODE) {
            recordingBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void initEvents() {
        callBack.setOnClickListener(this);
        snapshotBtn.setOnClickListener(this);
        selectorFunctionShowScene.setOnClickListener(this);
        speakBtn.setOnClickListener(this);
        recordingBtn.setOnClickListener(this);
        tvHistory.setOnClickListener(this);
        btnPresetPosition.setOnClickListener(this);
        silenceBtn.setOnClickListener(this);
        adbtn.setOnChangeListener(this);
        rotatorTxt.setOnClickListener(this);
        tvDefinition.setOnClickListener(this);
        tv_control_definition1.setOnClickListener(this);// 标清
        tv_control_definition2.setOnClickListener(this);// 高清
        tv_control_definition3.setOnClickListener(this);// 流畅
        tv_control_definition4.setOnClickListener(this);// 自适应
        ivLandscapeVideo.setOnClickListener(this);
        ivLandscapeTalk.setOnClickListener(this);
        ivLandscapeSnapshot.setOnClickListener(this);
        ivLandscapeSilence.setOnClickListener(this);
        monitor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);// 手势双击
                // mScaleGestureDetector.onTouchEvent(event);// 双指缩放功能暂时关闭
                return true;// 自定义方向判断
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDetector.disable();
        selectedListenView(false);
        PlayDesktopActivity.this.obtainSenseImg();//TODO 软解码可能出现耗时
        Log.i(TAG, "obtainSenseImg");
        PlayDesktopActivity.this.stopPlaySurfaceView();
        Log.i(TAG, "stopPlaySurfaceView");
    }

    private void obtainSenseImg() {
        IotUtil.saveBitmap(monitor.getBitmapSnap(), editor, cInfo.gwId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "===onDestroy===");
        Log.i(TAG, getDecodingWay() == Camera.HARD_DECODE ? "硬解码" : "软解码");
        if (messageQueue != null) {
            messageQueue.ondestroy();
            messageQueue = null;
        }
        if (cameaHelper != null) {
            cameaHelper.detach(iotcDevConnCallback);
            cameaHelper.detach(observer);
            cameaHelper.destroyVideoStream();
            cameaHelper.destroyCameraHelper();
        }
        cameaHelper = null;
        mDetector = null;
        cameraAction = null;
        if (myNetReceiver != null) {
            unregisterReceiver(myNetReceiver);
        }
        if (environmentReceiver != null) {
            unregisterReceiver(environmentReceiver);
        }
        if (presettingManager != null) {
            presettingManager.destroy();
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            Log.i(TAG, "onDown");
            gestureRotate(CAMERA_ROTATE_STOP);
            return true;//false 会忽略掉手势
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i(TAG, "onSingleTapUp");
            gestureRotate(CAMERA_ROTATE_STOP);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, "onScroll" + "------>" + distanceX);
            if (distanceX > 0) {
                gestureRotate(CAMERA_ROTATE_LEFT);
            } else if (distanceX < 0) {
                gestureRotate(CAMERA_ROTATE_RIGHT);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i(TAG, "onFling" + "------>" + velocityX);
            if (velocityX > 0) {
                gestureRotate(CAMERA_ROTATE_LEFT);
            } else if (velocityX < 0) {
                gestureRotate(CAMERA_ROTATE_RIGHT);
            }
            return false;
        }
    }

    /**
     * 横屏滑动事件 add by hxc
     * direction: 0.停止 1.左转 2.右转
     */
    private void gestureRotate(int direction) {
        if (isLandScape) {
            cameraAction.rotate(cameaHelper.getmCamera(), mAVChannel, direction);
        }
    }


    /**
     * 退出
     */
    private void sendBack() {
        if (!accord) {
            goPortrait();
//          rlLandscape.setVisibility(View.GONE);
            accord = true;
        } else {
            finish();
        }
    }

    /**
     * 设置图片
     */
    private void selectedListenView(boolean flg) {
        int drawable = flg ? R.drawable.desk_cb_silence_on
                : R.drawable.desk_cb_silence_off;
        silenceBtn.setBackground(getResources().getDrawable(drawable));
    }

    /**
     * 设置图片
     */
    private final void goLandscape() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isLandScape = true;
                rlLandscape.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
                conBar.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
    }

    /**
     * 竖屏
     */
    private final void goPortrait() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isLandScape = false;
                rlLandscape.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                conBar.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }
    /************************************************************** 预置位 开始 **************************************************************************************/
    /**
     * 初始化popwindow
     */
    private void initPopWindow() {
        presettingManager = PresettingManager.getInstance(this);
        presettingManager.setOnItemMenuClickListener(onItemMenuClickListener);
    }

    /**
     * 初始化popwindow
     */
    private OnItemMenuClickListener<PresettingModel> onItemMenuClickListener = new OnItemMenuClickListener<PresettingModel>() {
        @Override
        public void onMenuItemClick(int position) {
            presettingManager.showDialog(PlayDesktopActivity.this, IotUtil.rotateIndex(position));
        }

        @Override
        public void rotateDevicePresetting(int position) {
            Log.i(TAG, "===旋转设备位置(" + position + ")===");
            cameraAction.settingsBit(cameaHelper.getmCamera(), AVIOCTRLDEFs.AVIOCTRL_PTZ_GOTO_POINT, position);
        }

        @Override
        public void createDevicePresettingImage(String title) {
            Log.i(TAG, "===createDevicePresettingImage(" + title + ")===");
            cameraAction.snapshot(monitor, presettingPath, title);
        }

        @Override
        public void saveDevicePresetting(int position) {
            Log.i(TAG, "===设置设备预置位(" + position + ")===");
            cameraAction.settingsBit(cameaHelper.getmCamera(), AVIOCTRLDEFs.AVIOCTRL_PTZ_SET_POINT, position);
        }

        @Override
        public void clearDevicePresetting(int position) {
            Log.i(TAG, "===清除设备预置位(" + position + ")===");
            cameraAction.settingsBit(cameaHelper.getmCamera(), AVIOCTRLDEFs.AVIOCTRL_PTZ_CLEAR_POINT, position);
        }
    };

    /**************************************************************
     * 预置位 结束
     **************************************************************************************/
    @Override
    public void onClick(View arg0) {
        if (arg0 == callBack) {
            sendBack();
        } else if (arg0 == snapshotBtn || arg0 == ivLandscapeSnapshot) { // 快照
            cameraAction.snapshot(cameaHelper.getmCamera(), monitor);
        } else if (arg0 == selectorFunctionShowScene) {
            showSceneDialog();
        } else if (arg0 == tvHistory) {// 歷史記錄
            jumpVideoAty();
        } else if (arg0 == recordingBtn || arg0 == ivLandscapeVideo) {// 录像功能
            this.video();
        } else if (arg0 == silenceBtn || arg0 == ivLandscapeSilence) {// 开启外音
            cameraAction.listenin(cameaHelper.getmCamera(), mIsListening);
            selectedListenView(mIsListening);
            mIsListening = !mIsListening;
        } else if (arg0 == rotatorTxt) {
            accord = false;
            goLandscape();
            showPopupWindow(arg0);
//            rlLandscape.setVisibility(View.VISIBLE);
        } else if (arg0 == btnPresetPosition) {// 预置位
            presettingManager.showPopWindow(arg0, linFoot, presettingPath);
        } else if (arg0 == tvDefinition) {
            showPopWindow();
        } else if (arg0 == tv_control_definition1) {
            showSelectPopWindow(Config.S_Definition);
        } else if (arg0 == tv_control_definition2) {
            showSelectPopWindow(Config.H_Definition);
        } else if (arg0 == tv_control_definition3) {
            showSelectPopWindow(Config.Fluency);
        } else if (arg0 == tv_control_definition4) {
            showSelectPopWindow(Config.Adaptive);
        } else if (arg0 == speakBtn || arg0 == ivLandscapeTalk) {
            speakSet();
        }
    }

    private boolean dismiss() {
        if (popDefinitionWindow != null) {
            if (popDefinitionWindow.isShowing()) {
                popDefinitionWindow.dismiss();
                return true;
            }
            return false;
        }
        return false;
    }

    // TODO 添加 action
    private void showSelectPopWindow(int var) {
        dismiss();
        if (doWhat(var)) {
            cameraAction.handPreviewMode(cameaHelper.getmCamera(), var);
        }
    }

    private void showPopWindow() {
        if (!dismiss()) {
            popDefinitionWindow.showAsDropDown(tvDefinition, 0,
                    -tvDefinition.getHeight() * 5 - 4);
        }
    }

    private boolean doWhat(int var) {
        String checked = null;
        String exist = tvDefinition.getText().toString();
        if ((checked = returnTxt(var)) != null) {
            if (!checked.equals(exist)) {
                tvDefinition.setText(checked);
                return true;
            }
        }
        return false;
    }

    private String returnTxt(int var) {
        switch (var) {
            case Config.Adaptive:
                return tv_control_definition4.getText().toString();
            case Config.Fluency:
                return tv_control_definition3.getText().toString();
            case Config.H_Definition:
                return tv_control_definition2.getText().toString();
            case Config.S_Definition:
                return tv_control_definition1.getText().toString();
        }
        return null;
    }

    private void showSceneDialog() {
        Scene scene = Scene.getInstance();
        // 遍历更改status
        if (scene.getDataList() != null) {
            Iterator<SData> iterator = scene.getDataList().iterator();
            while (iterator.hasNext()) {
                SData data = (SData) iterator.next();
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
                scene.setInPlayVideoUI(true);
            } else {
                CustomToast.show(this,
                        getResources().getString(R.string.scene_unset));
            }
        } else {
            CustomToast.show(this,
                    getResources().getString(R.string.scene_unset));
        }
    }

    private final class MyOperatorForV5Lisener implements OperatorForV5Lisener {
        @Override
        public void showProgressDialog() {
            showBaseDialog();
        }

        @Override
        public void requestOverTime() {
            mHandler.sendEmptyMessageDelayed(SCENE_OVERTIME, 22 * 1000);
        }
    }

    @Override
    public void onChange(AdjustmentDirectionButton mtb, int btnState) {
        cameraAction.rotate(cameaHelper.getmCamera(), mAVChannel, btnState);
    }

    private void jumpVideoAty() {
        startActivity(new Intent(PlayDesktopActivity.this, PlayHistoryVideoActivity.class).putExtra(Config.deskBean, cInfo));
    }

    // add syf
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                switch (changeCounts % 3) {
                    case 0:
                        speakBtn.setBackgroundResource(R.drawable.btn_tackback_pressed_1);
                        break;
                    case 1:
                        speakBtn.setBackgroundResource(R.drawable.btn_tackback_pressed_2);
                        break;
                    case 2:
                        speakBtn.setBackgroundResource(R.drawable.btn_tackback_pressed_3);
                        break;
                }
                changeCounts++;
            } else if (msg.what == 2) {
                tvTime.setText(timeUtil.lastTime);
                AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
                animation.setDuration(1000);
                ivTime.startAnimation(animation);
            } else if (msg.what == 100) {
                Toast.makeText(getApplicationContext(),
                        R.string.desktop_Loading_promp1, Toast.LENGTH_SHORT)
                        .show();
            } else if (msg.what == 101) {
                Toast.makeText(getApplicationContext(),
                        R.string.desktop_Loading_promp2, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    /**
     * 对讲功能 addby 李凯
     */
    public void speakSet() {
        touchSpeakCount++;
        if (touchSpeakCount % 2 == 1) {
            isStart = true;
            changeSpeakBackGround();
            cameraAction.speakout(cameaHelper.getmCamera(), true);
        } else {
            isStart = false;
            changeCounts = 0;
            speakBtn.setBackgroundResource(R.drawable.desk_btn_tackback_noraml);
            cameraAction.speakout(cameaHelper.getmCamera(), false);
        }
    }

    public void changeSpeakBackGround() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isStart) {
                    try {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private IOTCDevChPojo getIotcDevChPojo() {
        return new IOTCDevChPojo(tutkUid, tutkPwd, Camera.IOTC_Connect_ByUID, Config.CAMERA);
    }

    private int getResolutionBySP() {
        return sharedPreferences.getInt(Config.DESK_CAMERA_DEFINITION_SP, 0);
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

    private CameraHelper.IOTCDevConnCallback iotcDevConnCallback = new CameraHelper.IOTCDevConnCallback() {
        @Override
        public void success() {
            Log.i(TAG, "===success===");
            IotSendOrder.settingsResolution(cameaHelper.getmCamera(), getResolutionBySP());
            IotSendOrder.connect(cameaHelper.getmCamera());
            cameaHelper.createVideoStream(getDecodingWay());//硬解码
            cameaHelper.createVideoCarrier(monitor);
        }

        @Override
        public void session() {
            Log.i(TAG, "===session===");
            createSessionWaitThread = new CreateSessionWaitThread();
            createSessionWaitThread.start();
        }

        @Override
        public void avChannel() {
            Log.i(TAG, "===avChannel===");
            createAvChannelWaitThread = new CreateAvChannelWaitThread();
            createAvChannelWaitThread.start();
        }
    };

    public void stopPlaySurfaceView() {
        destroyWailThread();
        if (cameraAction != null) {
            cameraAction.listenin(cameaHelper.getmCamera(), false);
        }
        if (cameaHelper != null) {
            if (cameaHelper.getmCamera() != null) {
                cameaHelper.destroyVideoCarrier(monitor);
            }
        }
    }

    private void startPlaySurfaceView() {
        if (messageQueue == null) {
            messageQueue = new MessageQueue(this);
        }
        if (cameaHelper == null) {
            cameaHelper = CameraHelper.getInstance(getIotcDevChPojo());
            cameaHelper.attach(iotcDevConnCallback);
            cameaHelper.attach(observer);
            cameaHelper.registerstIOTCLiener();
        }
        cameaHelper.register();
    }

    @Override
    protected void removeMessages() {
        mHandler.removeMessages(SCENE_OVERTIME);
    }

    /**
     * 录像方法
     */
    private void video() {
        if (getDecodingWay() == Camera.HARD_DECODE) {
            switch (recordingBtn.getTag().toString()) {
                case "start":
                    this.rec();
                    break;
                case "end":
                    this.stop();
                    break;
            }
            return;
        }
    }

    /**
     * 录制视频
     */
    private final void rec() {
        recordingBtn
                .setBackgroundResource(R.drawable.desk_btn_videotape_pressed_start);
        linTime.setVisibility(View.VISIBLE);
        timeUtil.addTime(handler);
        recordingBtn.setTag("end");
        cameraAction.startRecording(cameaHelper.getmCamera(), IotUtil.getFileName(cInfo.getGwId(), Config.cameraVideoType));
    }

    /**
     * 结束录制视频
     */
    private final void stop() {
        recordingBtn
                .setBackgroundResource(R.drawable.desk_btn_videotape_normal);
        linTime.setVisibility(View.GONE);
        timeUtil.stopTime();
        recordingBtn.setTag("start");
        cameraAction.stopRecording(cameaHelper.getmCamera());
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

    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.device_desktop_landscape_introduction, null);
        // 设置按钮的点击事件
//        Button button = (Button) contentView.findViewById(R.id.button1);
        RelativeLayout layout = (RelativeLayout) contentView.findViewById(R.id.rl_landscape_tip);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popRotateView.postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        }, 3000);

    }
}
