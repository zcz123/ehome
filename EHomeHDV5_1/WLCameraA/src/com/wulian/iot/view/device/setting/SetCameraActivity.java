package com.wulian.iot.view.device.setting;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.Packet;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.IOTCDevChPojo;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.server.queue.MessageQueue;
import com.wulian.iot.utils.DateUtil;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.utils.RemindDialog;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.view.manage.FirmwareUpManage;
import com.wulian.iot.view.ui.DeskMoveDetectionActivity;
import com.wulian.iot.view.ui.ElectivetActivity;
import com.wulian.iot.widght.CameraUpdateProcessDialog;
import com.wulian.iot.widght.DialogManager;
import com.wulian.iot.widght.DialogRealize;
import com.wulian.iot.view.ui.DeskSdStorageActivity;
import com.wulian.icam.R;
import com.yuantuo.customview.ui.WLToast;

public class SetCameraActivity extends SimpleFragmentActivity implements OnClickListener, CompoundButton.OnCheckedChangeListener, Handler.Callback, CameraHelper.Observer {
    private IOTCameraBean cInfo = null;
    private final int RESOLVING_POWER_TYPE = 1;
    private final int DESK_LANUAGE_SETTING = 2;
    private final int DESK_IRSERIES_SETTING = 3;
    private final int DESK_VOLUME_SETTING = 4;
    private Handler runUiThread = new Handler(this);
    private float total, free;//SD卡的 总大小 和剩余大小
    private FirmwareUpManage firmwareUpManage = null;//add syf 设备升级管理
    private ViewHolder viewHolder = null;
    private int currentlySelectedLanguage = -1;//设备中保存的语言信息
    private int currentlySelectedIRSeries = -1;//设备中保存的红外夜视信息
    private int currentlySelectedVolume = -1;//设备中保存的摄像机音量信息
    private List<ElectivetActivity.ElectivetPojo> electivetLanguages = null;//语言
    private List<ElectivetActivity.ElectivetPojo> electivetRes = null;//分辨率
    private List<ElectivetActivity.ElectivetPojo> electivetVolume = null;//音量
    private List<ElectivetActivity.ElectivetPojo> electivetIRSeries = null;//红外夜视
    private String[] fences = null;//活动检测防护数据,0-3分别为moveSensitivity，moveWeekday，moveTime,moveArea;
    public final static int MOTION_CLOSE = 0;//活动检测关
    public final static int MOTION_OPEN = 1;//活动检测开
    private String srtSwitch = null;
    private MessageQueue messageQueue = null;
    private int switching;
    private String spMoveArea;

    @Override
    public void root() {
        setContentView(R.layout.activity_set_camera);
    }


    public void setCurrentlySelectedLanguage(int currentlySelectedLanguage) {
        this.currentlySelectedLanguage = currentlySelectedLanguage;
    }

    public void setCurrentlySelectedVolume(int currentlySelectedVolume) {
        this.currentlySelectedVolume = currentlySelectedVolume;
    }

    public void setCurrentlySelectedIRSeries(int currentlySelectedIRSeries) {
        this.currentlySelectedIRSeries = currentlySelectedIRSeries;
    }

    public int getCurrentlySelectedLanguage() {
        return currentlySelectedLanguage;
    }

    public int getCurrentlySelectedIRSeries() {
        return currentlySelectedIRSeries;
    }

    public int getCurrentlySelectedVolume() {
        return currentlySelectedVolume;
    }

    public void setSrtSwitch(String srtSwitch) {
        this.srtSwitch = srtSwitch;
    }

    public String getSrtSwitch() {
        return srtSwitch;
    }

    public void setFences(String[] fences) {
        this.fences = fences;
    }

    public String[] getFences() {
        return fences;
    }

    private class ViewHolder {
        private final static String TAG = "ViewHolder";
        private ViewHolder instance = null;

        public ViewHolder() {
            Log.i(TAG, "init view holder");
            instance = this;
        }

        private LinearLayout linSafe, linResolvingPower, lindeskSettingLanuage, linSdCard, linCamVersion;
        private LinearLayout ll_camera_volume_setting;//摄像机播报音量
        private LinearLayout ll_IR_series_seting;//红外夜视
        private LinearLayout ll_move_detection_setting;//移动侦测报警
        private LinearLayout llEnvironmentDetection;
        private TextView tvResolvingPower, tvCamName, tvCamNumber, tvSafetySet, tvSettinglanuageType, tvSdCardStatus, tvBar, tvCamVersion;
        private TextView tv_move_detection;
        private TextView tv_IR_series;
        private TextView tv_camera_volume;
        private ImageView imgBack;
        private ToggleButton tb_move_detection;
        private ToggleButton tbEnvironmentDetection;

        private void fillBar(String data) {
            instance.tvBar.setText(data);
        }

        private void fillCamName(String data) {
            instance.tvCamName.setText(data);
        }

        private void fillGwId(String data) {
            instance.tvCamNumber.setText(data);
        }

        private void fillLanguage(int settingType) {
            switch (settingType) {
                case Config.Language.english:
                    setCurrentlySelectedLanguage(settingType);
                    instance.tvSettinglanuageType.setText(R.string.desk_language_english);
                    return;
                case Config.Language.chinese:
                    setCurrentlySelectedLanguage(settingType);
                    instance.tvSettinglanuageType.setText(R.string.desk_language_chinese);
                    return;
            }
        }

        private void fillIRSeries(int settingType) {
            switch (settingType) {
                case Config.IRSeries.OPEN:
                    setCurrentlySelectedIRSeries(settingType);
                    instance.tv_IR_series.setText(cc.wulian.app.model.device.R.string.device_state_open);
                    return;
                case Config.IRSeries.CLOSE:
                    setCurrentlySelectedIRSeries(settingType);
                    instance.tv_IR_series.setText(cc.wulian.app.model.device.R.string.device_state_close);
                    return;
                case Config.IRSeries.AUTO:
                    setCurrentlySelectedIRSeries(settingType);
                    instance.tv_IR_series.setText(cc.wulian.app.model.device.R.string.device_ac_cmd_auto);
                    return;
            }
        }

        private void fillVolume(int settingType) {
            switch (settingType) {
                case Config.Volume.MUTE:
                    instance.tv_camera_volume.setText(getResources().getString(R.string.air_conditioner_mute));
                    break;
                case Config.Volume.LOW:
                    instance.tv_camera_volume.setText(getResources().getString(R.string.cateye_sensitivity_setting_low));
                    break;
                case Config.Volume.MID:
                    instance.tv_camera_volume.setText(getResources().getString(R.string.cateye_sensitivity_setting_mid));
                    break;
                case Config.Volume.HIGH:
                    instance.tv_camera_volume.setText(getResources().getString(R.string.cateye_sensitivity_setting_high));
                    break;
                case Config.Volume.VERYHIGH:
                    instance.tv_camera_volume.setText(getResources().getString(R.string.dt_super_higher));
                    break;
            }
            setCurrentlySelectedVolume(settingType);
        }

        private void fillCamVersion(String deskVersion) {
            tvCamVersion.setText(deskVersion);
        }

        private void fillCamVersion(int method) {
            tvCamVersion.setText(method);
        }

        private void setMoveToggle(int method) {
            switch (method) {
                case MOTION_CLOSE:
                    viewHolder.tb_move_detection.setChecked(false);
                    break;
                case MOTION_OPEN:
                    viewHolder.tb_move_detection.setChecked(true);
                    break;
            }
        }

        private void fillSdCardStatus(String method) {
            tvSdCardStatus.setText(method);
        }

        private void fillSdCardTag(Object obj) {
            tvSdCardStatus.setTag(obj);
        }

        public int getSdCardTag() {
            return (int) tvSdCardStatus.getTag();
        }

        private void fillRes(int resolvingPowerType) {
            switch (resolvingPowerType) {
                case Config.Resolution._1080P:
                    Log.i(TAG, "设置为超清");
                    viewHolder.tvResolvingPower.setText(R.string.Superclear);
                    break;
                case Config.Resolution._720P:
                    Log.i(TAG, "设置为标清");
                    viewHolder.tvResolvingPower.setText(R.string.SD);
                    break;
                default:
                    viewHolder.tvResolvingPower.setText(R.string.Superclear);
                    break;
            }
            commitResolution(resolvingPowerType);
        }

        public void onDestroy() {
            instance = null;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HandlerConstant.DOWNLOAD_FINISH:// 固件升级
                showUpdataNoteDialog();
                break;
            case HandlerConstant.UPDATE_UI:// 更新ui
                viewHolder.fillCamVersion(R.string.already_latest);
                break;
            case HandlerConstant.DOWNLOAD_FIRMWARE:// 下载固件
                initCameraUpProcessDialog();
                showCameraUpDialog();
                camServerDowRegisterCallback();
                break;
            case HandlerConstant.SUCCESS:
                break;
            case HandlerConstant.ERROR:
                WLToast.showToast(this, getResources()
                        .getString(R.string.error_app), Toast.LENGTH_SHORT);
                break;
            case HandlerConstant.INSTALL_SUCCESS:
                WLToast.showToast(this,
                        getResources().getString(R.string.install_success),
                        Toast.LENGTH_SHORT);
                break;
            case HandlerConstant.INSTALL_ERROR:
                WLToast.showToast(this,
                        getResources().getString(R.string.install_error),
                        Toast.LENGTH_SHORT);
                break;
        }
        return false;
    }

    @Override
    public void avIOCtrlOnLine() {
        Log.i(TAG, "avIOCtrlOnLine");
    }

    @Override
    public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (avIOCtrlMsgType) {
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FW_UPDATA_RESP://版本信息
                        Log.i(TAG, "摄像机版本信息查询返回");
                        DialogRealize.unInit().dismissDialog();
                        if (firmwareUpManage.parseCameConfigInfo(data) != null)
                            viewHolder.fillCamVersion(firmwareUpManage.getCameraUpPojoFromIOT().getDeviceVersion());
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP:
                        Log.i(TAG, "活动检测查询返回");
                        setFences(IotUtil.getDeviceSafeProtectSetting(data));
                        String spMoveSensitivity = Integer.toString(DateUtil.bytesToInt(data, 4));
                        String[] a = IotUtil.getDeviceSafeProtectSetting(data);
                        spMoveArea = a[3];
                        viewHolder.setMoveToggle(DateUtil.bytesToInt(data, 0));//该方法必须得在给spmoveArea赋值之后调用
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VOLUME_RESP:
                        Log.i(TAG, "播报音量查询返回");
                        viewHolder.fillVolume(data[0]);
                        obtainElectivetVolumePojos();
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_LANGUAGE_RESP:
                        Log.i(TAG, "语言查询返回");
                        viewHolder.fillLanguage(data[0]);
                        obtainElectivetLanguagePojos();//获取语言实体
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_IRCARD_RESP:
                        Log.i(TAG, "红外夜视查询返回");
                        viewHolder.fillIRSeries(data[0]);
                        obtainElectivetIRSeriesPojos();
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_SDCARD_STATUS_RESP://sd卡状态
                        Log.i(TAG, "查询摄像机SD卡状态返回");
                        checkSdUpdateUi(IotUtil.byteArrayToMap(data));
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SDCARD_FORMAT_RESP://格式化
                        Log.i(TAG, "格式化成功");
                        dismissDialog();//等待 格式化对话框消失
                        if (Packet.byteArrayToInt_Little(data, 0) == 0) {
                            IotSendOrder.findSdCodeStatus(cameaHelper.getmCamera());//格式化成功  然后去查询一下sd卡的状态
                            return;
                        }
                        WLToast.showToast(SetCameraActivity.this, getResources().getString(R.string.desk_format_fail), Toast.LENGTH_SHORT);
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VOLUME_RESP:
                        Log.i(TAG, "音量播报设置成功");
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_IRCARD_RESP:
                        Log.i(TAG, "红外夜视设置成功");
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_LANGUAGE_RESP:
                        Log.i(TAG, "语言设置返回");
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP:
                        Log.i(TAG, "活动检测设置成功");
                }
            }
        });
    }

    @Override
    public void avIOCtrlMsg(int resCode, String method) {
        Log.i(TAG, "avIOCtrlMsg");
        final String msg = messageQueue.filter(resCode, method).sendMsg();
        if (msg != null && !msg.equals("")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SetCameraActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //add syf 固件升级
    private CameraUpdateProcessDialog cameraUpdateProcessDialog = null;

    private void initCameraUpProcessDialog() {
        if (cameraUpdateProcessDialog == null) {
            cameraUpdateProcessDialog = new CameraUpdateProcessDialog(this);
        }
    }

    private void showCameraUpDialog() {
        if (cameraUpdateProcessDialog != null) {
            cameraUpdateProcessDialog.show();
        }
    }

    private void dismissCameraUpDialog() {
        if (cameraUpdateProcessDialog != null) {
            cameraUpdateProcessDialog.dismiss();
        }
        cameraUpdateProcessDialog = null;
    }

    private FirmwareUpManage.DownloadCameraUpFileCallback downloadCameraUpFileCallback = new FirmwareUpManage.DownloadCameraUpFileCallback() {
        @Override
        public void downFinish() {
            dismissCameraUpDialog();
            runUiThread.sendEmptyMessage(HandlerConstant.DOWNLOAD_FINISH);
        }

        @Override
        public void updateUi(int present) {
            if (cameraUpdateProcessDialog != null) {
                cameraUpdateProcessDialog.setProgess(present);
            }
        }

        @Override
        public void dismissUi() {
            dismissCameraUpDialog();
        }
    };
    private FirmwareUpManage.CameraUpFromServerCallback cameraUpFromServerCallback = new FirmwareUpManage.CameraUpFromServerCallback() {
        @Override
        public void success() {
            switch (firmwareUpManage.checkoutVersion()) {
                case FirmwareUpManage.CHECKOUT_VERSION_STATE_UP:
                    Log.i(TAG, "CHECKOUT_VERSION_STATE_UP");
                    runUiThread.sendEmptyMessage(HandlerConstant.DOWNLOAD_FIRMWARE);
                    break;
                case FirmwareUpManage.CHECKOUT_VERSION_STATE_SAME:
                    Log.i(TAG, "CHECKOUT_VERSION_STATE_SAME");
                    runUiThread.sendEmptyMessage(HandlerConstant.UPDATE_UI);
                    break;
                case FirmwareUpManage.CHECKOUT_VERSION_STATE_FORCE:
                    Log.i(TAG, "CHECKOUT_VERSION_STATE_FORCE");
                    break;
            }
        }

        @Override
        public void error(String msg) {
            Log.e(TAG, "CameraUpFromServerCallback(" + msg + ")");
        }
    };

    private final void camServerConfigRegisterCallback() {
        if (firmwareUpManage != null) {
            firmwareUpManage.setCameraUpFromServerCallback(cameraUpFromServerCallback);
            firmwareUpManage.connectServerObtainCamVersion();
        }
    }

    private final void camServerDowRegisterCallback() {
        if (firmwareUpManage != null) {
            firmwareUpManage.setDownloadCameraUpFileCallback(downloadCameraUpFileCallback);
            firmwareUpManage.downloadCameraUpFile();
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

    private CameraHelper.IOTCDevConnCallback iotcDevConnCallback = new CameraHelper.IOTCDevConnCallback() {
        @Override
        public void success() {
            Log.i(TAG, "===createSessionSuccessfully===");
            if (firmwareUpManage == null) { //add syf 固件升级
                firmwareUpManage = new FirmwareUpManage(SetCameraActivity.this);
            }
            IotSendOrder.findDeskCameraVerByIoc(cameaHelper.getmCamera());
            IotSendOrder.findSdCodeStatus(cameaHelper.getmCamera());//成功后 去查询sd卡的状态
            IotSendOrder.findhLanguageByIoc(cameaHelper.getmCamera());//查询语言
            IotSendOrder.findIRByIoc(cameaHelper.getmCamera());//查询红外夜视
            IotSendOrder.findMoveDataByIoc(cameaHelper.getmCamera());//活动检测查询
            IotSendOrder.findVolumeByIoc(cameaHelper.getmCamera());
        }

        @Override
        public void session() {
            Log.i(TAG, "===createSessionFailed===");
            createSessionWaitThread = new CreateSessionWaitThread();
            createSessionWaitThread.start();
        }

        @Override
        public void avChannel() {
            Log.i(TAG, "===createAvIndexFailed===");
            createAvChannelWaitThread = new CreateAvChannelWaitThread();
            createAvChannelWaitThread.start();
        }
    };

    public void startPlaySurfaceView() {
        if (messageQueue == null)
            messageQueue = new MessageQueue(this);
        if (cameaHelper == null) {
            cameaHelper = CameraHelper.getInstance(getIotcDevChPojo());
            cameaHelper.attach(iotcDevConnCallback);
            cameaHelper.registerstIOTCLiener();
            cameaHelper.attach(this);
        }
        cameaHelper.register();
    }

    private String tutkUid, tutkPwd;

    private IOTCDevChPojo getIotcDevChPojo() {
        return new IOTCDevChPojo(tutkUid, tutkPwd, Camera.IOTC_Connect_ByUID, Config.CAMERA);
    }

    @Override
    public void initView() {
        viewHolder = new ViewHolder();
        viewHolder.linSafe = (LinearLayout) findViewById(R.id.lin_safety_protection);
        viewHolder.linResolvingPower = (LinearLayout) findViewById(R.id.lin_resolving_power);
        viewHolder.lindeskSettingLanuage = (LinearLayout) findViewById(R.id.lindesk_setting_lanuage);
        viewHolder.linCamVersion = (LinearLayout) findViewById(R.id.lin_firmware_update);
        viewHolder.linSdCard = (LinearLayout) findViewById(R.id.lin_setting_format_sdcard);
        viewHolder.ll_move_detection_setting = (LinearLayout) findViewById(R.id.ll_move_detection_setting);
        viewHolder.ll_camera_volume_setting = (LinearLayout) findViewById(R.id.ll_camera_volume_setting);
        viewHolder.tvResolvingPower = (TextView) findViewById(R.id.tv_resolvingower_type);
        viewHolder.tvCamName = (TextView) findViewById(R.id.tv_camera_name);
        viewHolder.tvCamNumber = (TextView) findViewById(R.id.tv_camera_number);
        viewHolder.imgBack = (ImageView) findViewById(R.id.titlebar_back);
        viewHolder.tvCamVersion = (TextView) findViewById(R.id.tv_desk_version);
        viewHolder.tvSafetySet = (TextView) findViewById(R.id.tv_safety_isset);
        viewHolder.tvSettinglanuageType = (TextView) findViewById(R.id.tv_settinglanuage_type);
        viewHolder.tvSdCardStatus = (TextView) findViewById(R.id.tv_format_sdcard);
        viewHolder.tvBar = (TextView) findViewById(R.id.titlebar_title);
        viewHolder.ll_IR_series_seting = (LinearLayout) findViewById(R.id.ll_IR_series_setting);
        viewHolder.tv_IR_series = (TextView) findViewById(R.id.tv_IR_series);
        viewHolder.tb_move_detection = (ToggleButton) findViewById(R.id.tb_move_detection);
        viewHolder.tv_camera_volume = (TextView) findViewById(R.id.tv_camera_volume);
        viewHolder.tbEnvironmentDetection = (ToggleButton) findViewById(R.id.tb_environment_data);
    }

    @Override
    public void initData() {
        cInfo = (IOTCameraBean) getIntent().getSerializableExtra(Config.deskBean);
        if (cInfo != null) {
            tutkUid = cInfo.getUid();
            tutkPwd = cInfo.getPassword();
            if (cInfo.getCamName() == null || cInfo.getCamName().equals("")) {
                viewHolder.fillCamName(getResources().getString(R.string.desktop_setting_not_name));
            } else {
                viewHolder.fillCamName(cInfo.getCamName());
            }
            if (cInfo.getGwId() != null && !cInfo.getGwId().equals("")) {
                viewHolder.fillGwId(cInfo.getGwId());
            }
            viewHolder.fillBar(getResources().getString(R.string.setting_device_setting));
            return;
        }
        finish();
    }

    @Override
    public void initEvents() {
        viewHolder.ll_move_detection_setting.setOnClickListener(this);
        viewHolder.linResolvingPower.setOnClickListener(this);
        viewHolder.imgBack.setOnClickListener(this);
        viewHolder.linCamVersion.setOnClickListener(this);
        viewHolder.tvCamName.setOnClickListener(this);
        viewHolder.lindeskSettingLanuage.setOnClickListener(this);
        viewHolder.linSdCard.setOnClickListener(this);
        viewHolder.ll_IR_series_seting.setOnClickListener(this);
        viewHolder.ll_camera_volume_setting.setOnClickListener(this);
        viewHolder.tb_move_detection.setOnCheckedChangeListener(this);
        viewHolder.tbEnvironmentDetection.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.tb_move_detection) {
            if (isChecked) {
                switching = 1;
            } else {
                switching = 0;
            }
            if (cameaHelper.getmCamera() != null) {
                IotSendOrder.sendMotionDetection(cameaHelper.getmCamera(), IotUtil.assemblyMotion(fences, spMoveArea, switching));
                Log.i(TAG, "灵敏度为" + fences[0] + "防护星期为" + fences[1] + ",防护时间为" + fences[2] + ",使能开关为" + switching + ",设防区域为" + spMoveArea);
            }
        } else if (id == R.id.tb_environment_data) {
            if (isChecked) {
                editor.putBoolean("status", true).commit();
            } else {
                editor.putBoolean("status", false).commit();
            }
        }
    }


    private void onAtyRes(Intent intent) {
        viewHolder.fillRes(intent.getIntExtra("code", 0));
    }

    private void onAtyLan(Intent intent) {
        int resolvingPowerType = intent.getIntExtra("code", -1);
        Log.i(TAG, "onAtyLan(" + resolvingPowerType + ")");
        IotSendOrder.setLanguage(cameaHelper.getmCamera(), resolvingPowerType);
    }

    private void onAtyVolume(Intent intent) {
        int VolumeType = intent.getIntExtra("code", -1);
        Log.i("===VolumeType===", VolumeType + "");
        IotSendOrder.setCameraVolume(cameaHelper.getmCamera(), VolumeType);
    }

    private void onAtyIRSeries(Intent intent) {
        int IRSeriesType = intent.getIntExtra("code", -1);
        Log.i("===IRSeries===", IRSeriesType + "");
        IotSendOrder.setIRSeries(cameaHelper.getmCamera(), IRSeriesType);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESOLVING_POWER_TYPE:
                if (resultCode == RESULT_OK) {
                    onAtyRes(data);
                }
                break;
            case DESK_LANUAGE_SETTING:
                if (resultCode == RESULT_OK) {
                    onAtyLan(data);
                }
                break;
            case DESK_IRSERIES_SETTING:
                if (resultCode == RESULT_OK) {
                    onAtyIRSeries(data);
                }
                break;
            case DESK_VOLUME_SETTING: {
                if (resultCode == RESULT_OK) {
                    onAtyVolume(data);
                }
                break;
            }
            default:
                break;
        }
    }


    private void IotSendQuery() {
        IotSendOrder.findVolumeByIoc(cameaHelper.getmCamera());//查询音量大小
        IotSendOrder.findhLanguageByIoc(cameaHelper.getmCamera());//查询语言
        IotSendOrder.findIRByIoc(cameaHelper.getmCamera());//查询红外夜视
    }

    @Override
    protected void onResume() {
        super.onResume();
        DialogRealize.init(this).showDiglog();
        startPlaySurfaceView();
        findDataBySharedPreferences();//查询本地记录的清晰度
        showSpEnvironmentCheck();
        obtainElectivetResPojos();
        IotSendQuery();
    }

    @Override
    protected void onPause() {
        super.onPause();
        destroyWailThread();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firmwareUpManage != null)
            firmwareUpManage.destroy();
        if (viewHolder != null)
            viewHolder.onDestroy();
        cameaHelper.detach(this);
        cameaHelper.attach(iotcDevConnCallback);
        cameaHelper.destroyCameraHelper();
        cameaHelper = null;
    }

    public void stopPlaySurfaceView() {
    }

    @Override
    public void onClick(View view) {
        if (view == viewHolder.linSafe) {
//            jumpSetProtective();//设置防护区域
        } else if (view == viewHolder.linResolvingPower) {
            jumpSetResolution();//设置分辨率
        } else if (view == viewHolder.linCamVersion) {
            camServerConfigRegisterCallback();//通过服务器获取最新固件版本号
        } else if (view == viewHolder.imgBack) {
            animationExit();
        } else if (view == viewHolder.tvCamName) {
            Toast.makeText(SetCameraActivity.this, getString(R.string.tv_nameChange), Toast.LENGTH_SHORT).show();//add by hxc
        } else if (view == viewHolder.lindeskSettingLanuage) {
            jumpSetLanguage();
        } else if (view == viewHolder.linSdCard) {
            jumpSetSdCard();
        } else if (view == viewHolder.ll_move_detection_setting) {//活动检测
            jumpSetMoveDetetion();
        } else if (view == viewHolder.ll_IR_series_seting) {//红外夜视
            jumpSetIRSeries();
        } else if (view == viewHolder.ll_camera_volume_setting) {//音量设置
            jumpSetVolume();
        }
    }

    private void showSpEnvironmentCheck() {
        boolean isChecked = sharedPreferences.getBoolean("status", false);
        viewHolder.tbEnvironmentDetection.setChecked(isChecked);
    }

    private void findDataBySharedPreferences() {
        viewHolder.fillRes(sharedPreferences.getInt(Config.DESK_CAMERA_DEFINITION_SP, -1));//分辨率
    }

    private void jumpSetResolution() {
        startActivityForResult(new Intent(this, ElectivetActivity.class).putExtra("check", 1).putExtra("datas", (Serializable) electivetRes), RESOLVING_POWER_TYPE);
    }

    private void jumpSetLanguage() {
        startActivityForResult(new Intent(this, ElectivetActivity.class).putExtra("datas", (Serializable) electivetLanguages).putExtra("check", 0), DESK_LANUAGE_SETTING);
    }

    private void jumpSetVolume() {
        startActivityForResult(new Intent(this, ElectivetActivity.class).putExtra("datas", (Serializable) electivetVolume).putExtra("check", 3), DESK_VOLUME_SETTING);
    }

    private void jumpSetIRSeries() {
        startActivityForResult(new Intent(this, ElectivetActivity.class).putExtra("datas", (Serializable) electivetIRSeries).putExtra("check", 2), DESK_IRSERIES_SETTING);
    }

    private void jumpSetSdCard() {
        if (checkSdCardState() == 0) {
            startActivity(new Intent(this, DeskSdStorageActivity.class).putExtra("total", total).putExtra("free", free));
        }
    }

    private void jumpSetMoveDetetion() {
        if (!cameaHelper.getmCamera().isSessionConnected()) {
            WLToast.showToast(this, getResources().getString(R.string.ioc_error), Toast.LENGTH_SHORT);
            return;
        }
        startActivity(new Intent(SetCameraActivity.this, DeskMoveDetectionActivity.class).putExtra(Config.deskBean, cInfo).putExtra("fences", getFences()).putExtra("switching", switching));
    }

    private void obtainElectivetLanguagePojos() {
        electivetLanguages = new ArrayList<ElectivetActivity.ElectivetPojo>();
        for (Map.Entry<String, Object> entry : Config.Language.camLanguage(this).entrySet()) {
            ElectivetActivity.ElectivetPojo electivetPojo = new ElectivetActivity.ElectivetPojo();
            electivetPojo.setKey(entry.getKey());
            electivetPojo.setValue((int) entry.getValue());
            if ((int) entry.getValue() == getCurrentlySelectedLanguage()) {
                electivetPojo.setCheck(true);
            } else {
                electivetPojo.setCheck(false);
            }
            electivetLanguages.add(electivetPojo);
        }
    }

    private void obtainElectivetResPojos() {
        electivetRes = new ArrayList<ElectivetActivity.ElectivetPojo>();
        for (Map.Entry<String, Object> entry : Config.Resolution.camRes(this).entrySet()) {
            ElectivetActivity.ElectivetPojo electivetPojo = new ElectivetActivity.ElectivetPojo();
            electivetPojo.setKey(entry.getKey());
            electivetPojo.setValue((int) entry.getValue());
            int articulation = sharedPreferences.getInt(Config.DESK_CAMERA_DEFINITION_SP, 0);
            if ((int) entry.getValue() == articulation) {
                electivetPojo.setCheck(true);
            } else {
                electivetPojo.setCheck(false);
            }
            electivetRes.add(electivetPojo);
        }
    }

    private void obtainElectivetIRSeriesPojos() {
        electivetIRSeries = new ArrayList<ElectivetActivity.ElectivetPojo>();
        for (Map.Entry<String, Object> entry : Config.IRSeries.camIRSeries(this).entrySet()) {
            ElectivetActivity.ElectivetPojo electivetPojo = new ElectivetActivity.ElectivetPojo();
            electivetPojo.setKey(entry.getKey());
            electivetPojo.setValue((int) entry.getValue());
            if ((int) entry.getValue() == getCurrentlySelectedIRSeries()) {
                electivetPojo.setCheck(true);
            } else {
                electivetPojo.setCheck(false);
            }
            electivetIRSeries.add(electivetPojo);
        }
    }

    private void obtainElectivetVolumePojos() {
        electivetVolume = new ArrayList<ElectivetActivity.ElectivetPojo>();
        for (LinkedHashMap.Entry<String, Object> entry : Config.Volume.camVolume(this).entrySet()) {
            ElectivetActivity.ElectivetPojo electivetPojo = new ElectivetActivity.ElectivetPojo();
            electivetPojo.setKey(entry.getKey());
            electivetPojo.setValue((int) entry.getValue());
            if ((int) entry.getValue() == getCurrentlySelectedVolume()) {
                electivetPojo.setCheck(true);
            } else {
                electivetPojo.setCheck(false);
            }
            electivetVolume.add(electivetPojo);
        }
    }

    private void commitResolution(int articulation) {//默认清晰度设置为720p
        if (articulation == -1)
            articulation = 0;
        editor.putInt(Config.DESK_CAMERA_DEFINITION_SP, articulation);
        editor.commit();
    }

    // TODO 安全防护内部没有重构  格式sdcard 没有重构 其他都好了
    private int checkSdCardState() {
        int state = viewHolder.getSdCardTag();
        switch (state) {
            case 0:
                return state;
            case -10:
                showDialogFormatWarm();

//                Toast.makeText(this, getResources().getString(R.string.set_desk_format_unable), Toast.LENGTH_SHORT).show();
                return state;
            case -11:
                Toast.makeText(this, getResources().getString(R.string.set_desk_format_no_sdcard), Toast.LENGTH_SHORT).show();
                return state;
            default:
                return state;
        }
    }
    /**
     * 格式化 等待的dialog
     */
    public void showWaitDialogFormat() {
        DialogManager manager = new DialogManager(this);
        //自定义实现部分
        View view = manager.getView(DialogManager.iot_camera);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);// 加载布局
        ImageView spaceshipImage = (ImageView) view.findViewById(R.id.img);
        TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);// 提示文字
        spaceshipImage.setAnimation(manager.getAnimation(DialogManager.animation));
        tipTextView.setText(R.string.desk_format_wait_dialog);
        //自定义实现部分
        if (layout != null) {
            mDiglog = manager.getDialog(DialogManager.iot_dialog_style, layout);
            showDialog();
        }
    }
    /**
     * sd卡存在 但需要格式化  提示格式的dialog
     */
    public void showDialogFormatWarm() {
        final RemindDialog rd = new RemindDialog(this);
        rd.setDialogWidth(getWindowManager());
        rd.setTitle(this.getResources().getString(R.string.set_desk_format_dialog_title));
        //格式化将使SD卡变为FAT32格式，并将删除卡内所有数据
        rd.setMessage(this.getResources().getString(R.string.set_desk_format_dialog_message1));
        rd.getTvSuer().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //发送格式化命令
                IotSendOrder.sendSdFormat(cameaHelper.getmCamera());
                showWaitDialogFormat();
                rd.getDialog().dismiss();
            }
        });
        rd.showDialog();
    }

    /**
     * 提示用户升级的dialog
     */
    private void showUpdataNoteDialog() {
        final RemindDialog rd = new RemindDialog(this);
        rd.getTvSuer().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                firmwareUpManage.updateCamera(SetCameraActivity.this);
                rd.getDialog().dismiss();
            }
        });
        rd.setDialogWidth(this.getWindowManager());
        rd.showDialog();
    }

    /**
     * 根据返回的信息 更新UI
     */
    private void checkSdUpdateUi(Map<String, Integer> sdkMap) {
        int status = sdkMap.get(Config.status);
        int sdexist = sdkMap.get(Config.sdexist);
        if (status == 0 && sdexist == 0) {
            //sd卡 存在， 可读可写   这时需要把free total 显示出来
            total = new BigDecimal(sdkMap.get(Config.totalMB) / 1024f).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            free = new BigDecimal(sdkMap.get(Config.freeMB) / 1024f).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            viewHolder.fillSdCardStatus(free + "G/" + total + "G");
            viewHolder.fillSdCardTag(0);
        } else if (status == -1 && sdexist == 0) {//sd卡 存在 ，但不可用，因此 需要格式化
            viewHolder.fillSdCardStatus(getResources().getString(R.string.set_desk_format_unable));
            viewHolder.fillSdCardTag(-10);
        } else if (sdexist == -1) {//sd卡 不存在
            viewHolder.fillSdCardStatus(getResources().getString(R.string.set_desk_format_no_sdcard));
            viewHolder.fillSdCardTag(-11);
        }
    }
}
