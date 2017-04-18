package com.wulian.icam.view.device.setting;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.EagleConfig;
import com.wulian.icam.datasource.DataSource;
import com.wulian.icam.model.AlarmMessage;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.NewEagleInfo;
import com.wulian.icam.model.VersionInfo;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.ProgressDialogManager;
import com.wulian.icam.utils.StringUtil;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.device.config.DeviceIdQueryActivity;
import com.wulian.icam.view.replay.HistoryVideoSettingActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipProfile;
import com.yuantuo.customview.ui.WLToast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hxc on 2016/12/22.
 * function：新猫眼设置页
 */

public class NewEagleSettingActivity extends BaseFragmentActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private ImageView titlebarBack;
    private TextView titlebarTitle;
    private TextView tvEagleName;
    private RelativeLayout rlEagleName;
    private TextView tvEagleInfo;
    private RelativeLayout rlEagleInfo;
    private ToggleButton tbDoorbellLightSwitch;
    private RelativeLayout rlDoorbellLightSwitch;
    private ToggleButton tbDoorbellPir;
    private RelativeLayout rlDoorbellPir;
    private ToggleButton tbMoveDetection;
    private RelativeLayout rlMoveDetection;
    private RelativeLayout rlMoveDetectionSet;
    private RelativeLayout rlIrSeries;
    private RelativeLayout rlWifiConfig;
    private RelativeLayout rlBroadcastLanguage;
    private Button btnUnbindEagle;
    private TextView tvIrShow;
    private TextView tvLanguageShow;
    private TextView tvVideoSize;
    private ImageView ivVideo;
    private RelativeLayout rlHistoryVideo;


    private final static String TAG = "NewEagleSettingActivity";
    private static final String SEND_SIP_REQUEST = "send_sip_request";
    private static final int FLAG_UNBIND_DEVICE = 0;
    private static final int FLAG_EDITMETA_DEVICE = 1;
    private static final int MSG_FINISH = 1;
    private static final int MSG_EDIT_META = 2;
    private int seq = 1;
    private boolean isClickToUpdateVersion;
    private String ledOn = "1";

    private Dialog renameDialog;
    private Dialog unbindDialog;

    private String language;
    private Device device;
    private NewEagleInfo newEagleInfo = new NewEagleInfo();
    private String deviceId;
    private int callback_flag;
    private String sipCallWithDomain;// xxx@wuliangruop.cn
    private String deviceSipAccount;// 设备sip账号
    private String deviceControlUrl;// 设备控制sip地址
    private String deviceCallUrl;// 设备呼叫sip地址
    private SipProfile account;
    private VersionInfo cmicWebVersionInfo = null;
    private String deviceName;
    private String uuid;
    private boolean hasSDCard = false;
    private DataSource mDataSource;
    private List<AlarmMessage> msgList = null;
    private DeleteBundingMessageTask mTask;
    protected ProgressDialogManager mDialogManager = ProgressDialogManager
            .getDialogManager();


    private Handler myHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:// 结束页面
                    NewEagleSettingActivity.this.finish();
                    break;
                case MSG_EDIT_META:// 编辑设备信息
                    sendEditMeta();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_neweagle_setting);
        initView();
        initListener();
        initData();
        onSendSipRemoteAccess();
        if (device.getIs_online() == 1) {
            initWebData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.sysoInfo("onStart registerReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (device == null) {// 必须的数据为空了，直接结束
            this.finish();
        }

        queryDeviceInfo();

    }

    @Override
    protected void onStop() {
        super.onStop();
        isClickToUpdateVersion = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        ((TextView) findViewById(R.id.titlebar_title))
                .setText(R.string.device_ir_setting);
        titlebarBack = (ImageView) findViewById(R.id.titlebar_back);
        titlebarTitle = (TextView) findViewById(R.id.titlebar_title);
        tvEagleName = (TextView) findViewById(R.id.tv_eagle_name);
        rlEagleName = (RelativeLayout) findViewById(R.id.rl_eagle_name);
        tvEagleInfo = (TextView) findViewById(R.id.tv_eagle_info);
        rlEagleInfo = (RelativeLayout) findViewById(R.id.rl_eagle_info);
        tbDoorbellLightSwitch = (ToggleButton) findViewById(R.id.tb_doorbell_light_switch);
        rlDoorbellLightSwitch = (RelativeLayout) findViewById(R.id.rl_doorbell_light_switch);
        tbDoorbellPir = (ToggleButton) findViewById(R.id.tb_doorbell_pir);
        rlDoorbellPir = (RelativeLayout) findViewById(R.id.rl_doorbell_pir);
        tbMoveDetection = (ToggleButton) findViewById(R.id.tb_move_detection);
        rlMoveDetection = (RelativeLayout) findViewById(R.id.rl_move_detection);
        rlMoveDetectionSet = (RelativeLayout) findViewById(R.id.rl_move_detection_set);
        rlIrSeries = (RelativeLayout) findViewById(R.id.rl_ir_series);
        rlWifiConfig = (RelativeLayout) findViewById(R.id.rl_wifi_config);
        rlBroadcastLanguage = (RelativeLayout) findViewById(R.id.rl_broadcast_language);
        btnUnbindEagle = (Button) findViewById(R.id.btn_unbind_eagle);
        tvIrShow = (TextView) findViewById(R.id.tv_ir_show);
        tvLanguageShow = (TextView) findViewById(R.id.tv_language_show);
        tvVideoSize = (TextView) findViewById(R.id.tv_video_size);
        ivVideo = (ImageView) findViewById(R.id.iv_video);
        rlHistoryVideo = (RelativeLayout) findViewById(R.id.rl_history_video);
    }

    private void initListener() {
        titlebarBack.setOnClickListener(this);
        btnUnbindEagle.setOnClickListener(this);
        rlEagleName.setOnClickListener(this);
        rlEagleInfo.setOnClickListener(this);
        rlMoveDetectionSet.setOnClickListener(this);
        rlIrSeries.setOnClickListener(this);
        rlWifiConfig.setOnClickListener(this);
        rlBroadcastLanguage.setOnClickListener(this);
        tbDoorbellLightSwitch.setOnCheckedChangeListener(this);
        tbDoorbellPir.setOnCheckedChangeListener(this);
        tbMoveDetection.setOnCheckedChangeListener(this);
        ivVideo.setOnClickListener(this);
        tvVideoSize.setOnClickListener(this);
        rlHistoryVideo.setOnClickListener(this);
    }

    private void initData() {
        device = (Device) getIntent().getSerializableExtra("device");
        deviceSipAccount = device.getDevice_id();
        deviceCallUrl = deviceSipAccount + "@" + device.getSip_domain();
        deviceControlUrl = deviceCallUrl;
        if (device == null) {
            finish();
            return;
        }
        account = app.registerAccount();
        if (account == null) {
            CustomToast.show(this, R.string.login_user_account_register_fail);
            this.finish();
        }
        deviceId = device.getDevice_id();
        tvEagleName.setText(device.getDevice_nick());
        sipCallWithDomain = device.getDevice_id() + "@"
                + device.getSip_domain();
    }

    private void initWebData() {
        // 122 查询存储状态
        SipController.getInstance().sendMessage(
                sipCallWithDomain,
                SipHandler
                        .QueryStorageStatus("sip:" + sipCallWithDomain, seq++),
                app.registerAccount());

        queryLedAndVoicePromptInfo();
    }

    private void queryDeviceInfo() {
        String sip_ok = "sip:" + device.getSip_username() + "@"
                + device.getSip_domain();
        SipController.getInstance().sendMessage(sip_ok.replace("sip:", ""), SipHandler.QueryDeviceInformation(sip_ok, seq++), account);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            NewEagleSettingActivity.this.finish();
        } else if (id == R.id.rl_eagle_name) {//设备名称
            renameDeviceDialog();
        } else if (id == R.id.rl_eagle_info) {//设备信息
            startActivity(new Intent(NewEagleSettingActivity.this,
                    DeviceDetailActivity.class).putExtra("device", device));
        } else if (id == R.id.rl_wifi_config) {//wifi配置
            startActivity(new Intent(NewEagleSettingActivity.this, DeviceIdQueryActivity.class)
                    .putExtra("msgData", deviceId).putExtra("isAddDevice", false));
        } else if (id == R.id.rl_broadcast_language) {//播报语言
            startActivityForResult(new Intent(NewEagleSettingActivity.this, NewEagleLanguageActivity.class).putExtra("language", newEagleInfo.getLanguage()), EagleConfig.REQUEST_FOR_LANGUAGE);
        } else if (id == R.id.rl_move_detection_set) {//人体侦测设置
            startActivityForResult(new Intent(NewEagleSettingActivity.this, NewEagleDecetionActivity.class).putExtra("newEagleInfo", newEagleInfo), EagleConfig.REQUEST_FOR_DETECTION);
        } else if (id == R.id.rl_history_video) {//录像存储
            jumpToHistoryVideo();
        } else if (id == R.id.btn_unbind_eagle) {//解绑猫眼
            unbindDeviceDialog();
        }

    }

    @Override
    protected void DataReturn(boolean success, RouteApiType apiType, String json) {
        super.DataReturn(success, apiType, json);
        if (success) {
            switch (apiType) {
                case V3_USER_DEVICE:
                    CustomToast.show(this,
                            R.string.setting_device_edit_meta_success);
                    ICamGlobal.isNeedRefreshDeviceList = true;
                    // 修改本地对象为最新的值
                    device.setDevice_nick(deviceName);
                    tvEagleName.setText(deviceName);
                    break;
                case V3_LOGIN:// 重新登录成功，继续刚才的请求
                    switch (callback_flag) {// 多个回调需要判断
                        case FLAG_EDITMETA_DEVICE:
                            sendEditMeta();
                            break;
                        case FLAG_UNBIND_DEVICE:
                            unBindDevice();
                            break;
                        default:
                            break;
                    }
                    break;
                case V3_BIND_UNBIND:
                    mTask = new DeleteBundingMessageTask();
                    mTask.execute();// 删除主绑定相关的报警消息

                default:
                    break;
            }
        } else {
            switch (apiType) {
                default:
                    break;
            }
        }
    }

    @Override
    protected void SipDataReturn(boolean isSuccess, SipMsgApiType apiType,
                                 String xmlData, String from, String to) {
        super.SipDataReturn(isSuccess, apiType, xmlData, from, to);
        mDialogManager.dimissDialog(SEND_SIP_REQUEST, 0);
        if (isSuccess) {
            Utils.sysoInfo("===" + xmlData);
            switch (apiType) {
                case QUERY_DEVICE_INFORMATION:
                    Utils.sysoInfo("查询设置信息:" + xmlData);
                    getPojoByXmlData(xmlData);
                    showQuerySipData();
                    break;
                case QUERY_STORAGE_STATUS:// 122 查询存储状态
                    Utils.sysoInfo("查询存储状态:" + xmlData);
                    Pattern pstatus = Pattern
                            .compile("<storage.*status=\"(\\d)\"\\s+.*/?>(</storage>)?");
                    Matcher matchers = pstatus.matcher(xmlData);
                    if (matchers.find()) {
                        String status = matchers.group(1).trim();
                        if ("1".equals(status)) {
                            hasSDCard = false;
                            tvVideoSize.setText(getResources().getString(
                                    R.string.common_no_sdcard));
                        } else if ("2".equals(status)) {
                            hasSDCard = true;
                            tvVideoSize.setText(getResources().getString(
                                    R.string.setting_has_sdcard));
                        }
                    }
                    break;
                case QUERY_LED_AND_VOICE_PROMPT_INFO:// 查询LED及语音提示设置
                    ledOn = Utils.getParamFromXml(xmlData, "led_on").trim();
                    if (!TextUtils.isEmpty(ledOn)) {
                        if (ledOn.equals("0")) {
                            tbDoorbellLightSwitch.setChecked(false);
                        } else {// 1
                            tbDoorbellLightSwitch.setChecked(true);
                        }
                    }
                    Log.e(TAG, "查询门铃灯数据返回，状态为：" + ledOn);
                    break;
                case CONFIG_LED_AND_VOICE_PROMPT:
                    Log.e(TAG,"门铃灯配置数据返回");
                    CustomToast.show(this, R.string.common_setting_success);
                    break;
                case QUERY_DEVICE_LANGUAGE:
                    language = Utils.getParamFromXml(xmlData, "language");
                    if (!StringUtil.isNullOrEmpty(language)) {
                        if (language.equals("1")) {
                            tvLanguageShow.setText(getResources().getString(R.string.desk_language_chinese));
                        } else if (language.equals("2")) {
                            tvLanguageShow.setText(getResources().getString(R.string.desk_language_english));
                        }
                    }
                    break;

                case QUERY_PIR_PARAM:
                    Utils.sysoInfo("配置PIR返回:" + xmlData);
                    break;
            }
        } else {
            Utils.sysoInfo("sip fail:" + xmlData);
            switch (apiType) {
                case QUERY_STORAGE_STATUS:
                    CustomToast.show(this, R.string.common_setting_fail);
                    break;
                case QUERY_DEVICE_INFORMATION:
                    CustomToast.show(this, R.string.common_setting_fail);
                    break;
                case QUERY_LED_AND_VOICE_PROMPT_INFO:// 查询LED及语音提示设置
                    CustomToast.show(this, R.string.config_query_device_fail);
                    break;
                case CONFIG_LED_AND_VOICE_PROMPT://
                    CustomToast.show(this, R.string.common_setting_fail);
                    break;
                case QUERY_DEVICE_LANGUAGE:
                    CustomToast.show(this, R.string.common_setting_fail);
                    break;
                case QUERY_PIR_PARAM:
                    CustomToast.show(this, R.string.common_setting_fail);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EagleConfig.REQUEST_FOR_LANGUAGE:
                if (resultCode == RESULT_OK) {
                    language = data.getStringExtra("language");
                    if (!StringUtil.isNullOrEmpty(language)) {
                        newEagleInfo.setLanguage(language);
                        configSelectedLanguage();
                    }
                }
                break;
            case EagleConfig.REQUEST_FOR_DETECTION:
                if (resultCode == RESULT_OK) {
                    newEagleInfo = (NewEagleInfo) data.getSerializableExtra("newEagleInfo");
                    configPIR();
                }
                break;

            default:
                break;
        }
    }

    private NewEagleInfo getPojoByXmlData(String xmlData) {

        newEagleInfo.setLanguage(Utils.getParamFromXml(xmlData, "language"));
        newEagleInfo.setPIRSwitch(Utils.getParamFromXml(xmlData, "PIRSwitch"));
        newEagleInfo.setHoverDetectTime(Utils.getParamFromXml(xmlData, "HoverDetectTime"));
        newEagleInfo.setPIRDetectLevel(Utils.getParamFromXml(xmlData, "PIRDetectLevel"));
        newEagleInfo.setHoverProcMode(Utils.getParamFromXml(xmlData, "HoverProcMode"));
        newEagleInfo.setHoverRecTime(Utils.getParamFromXml(xmlData, "HoverRecTime"));
        newEagleInfo.setHoverSnapshotCount(Utils.getParamFromXml(xmlData, "HoverSnapshotCount"));
        newEagleInfo.setHoverSnapshotInterval(Utils.getParamFromXml(xmlData, "HoverSnapshotInterval"));
        newEagleInfo.setContrast(Utils.getParamFromXml(xmlData, "contrast"));
        newEagleInfo.setBrightness(Utils.getParamFromXml(xmlData, "brightness"));
        newEagleInfo.setDayNightMode(Utils.getParamFromXml(xmlData, "DayNightMode"));

        return newEagleInfo;
    }

    private void showQuerySipData() {
        if (newEagleInfo.getLanguage().equals("1")) {
            tvLanguageShow.setText(getResources().getString(R.string.desk_language_chinese));
        } else if (newEagleInfo.getLanguage().equals("2")) {
            tvLanguageShow.setText(getResources().getString(R.string.desk_language_english));
        }
        if (newEagleInfo.getPIRSwitch().equals("0")) {
            tbDoorbellPir.setChecked(false);
        } else if (newEagleInfo.getPIRSwitch().equals("1")) {
            tbDoorbellPir.setChecked(true);
        }

    }

    private void renameDeviceDialog() {
        Resources rs = getResources();
        renameDialog = DialogUtils.showCommonEditDialog(this, false,
                rs.getString(R.string.setting_enter_device_name), null, null,
                rs.getString(R.string.setting_enter_device_name),
                device.getDevice_nick(), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.et_input) {
                            EditText infoEt = (EditText) v;
                            deviceName = infoEt.getText().toString().trim();
                            if (!StringUtil.isNullOrEmpty(deviceName)) {
                                if (!deviceName.equals(device.getDevice_nick())) {
                                    myHandler.sendEmptyMessageDelayed(
                                            MSG_EDIT_META, 100);
                                    renameDialog.dismiss();
                                } else {
                                    renameDialog.dismiss();
                                }
                            } else {
                                Utils.shake(NewEagleSettingActivity.this, infoEt);
                            }
                        } else if (id == R.id.btn_negative) {
                            renameDialog.dismiss();
                        }
                    }
                });
    }

    private void sendEditMeta() {
        callback_flag = FLAG_EDITMETA_DEVICE;
        // 检查是否超时
        if (System.currentTimeMillis() < userInfo.getExpires() * 1000L) {
            if (deviceName.length() > 15) {
                WLToast.showToast(NewEagleSettingActivity.this,
                        "摄像机名称最大支持15个字符。", 1000);
            } else {
                RouteLibraryController.getInstance().doRequest(
                        NewEagleSettingActivity.this,
                        RouteApiType.V3_USER_DEVICE,
                        RouteLibraryParams.V3UserDevice(userInfo.getAuth(),
                                device.getDevice_id(), deviceName, ""),
                        NewEagleSettingActivity.this);
            }

        } else {// 已经超时,重新登录
            reLogin();
        }
    }

    private void configSelectedLanguage() {
        String sip_ok = "sip:" + device.getSip_username() + "@"
                + device.getSip_domain();
        SipController.getInstance().sendMessage(
                sip_ok.replace("sip:", ""), SipHandler.QueryDeviceLanguage(sip_ok, seq++, newEagleInfo.getLanguage()), account);
        Log.e(TAG, "设置的language====" + language);
    }

    private void configPIR() {
        String sip_ok = "sip:" + device.getSip_username() + "@"
                + device.getSip_domain();
        SipController.getInstance().sendMessage(
                sip_ok.replace("sip:", ""), SipHandler.QueryPIRParam(sip_ok, seq++, newEagleInfo.getPIRSwitch(), newEagleInfo.getHoverDetectTime(), newEagleInfo.getPIRDetectLevel(), newEagleInfo.getHoverProcMode()), account);
        Utils.sysoInfo("配置pir");
    }

    private void unbindDeviceDialog() {
        String tip;
        Resources rs = getResources();
        if (device.getIs_BindDevice()) {
            tip = rs.getString(R.string.setting_unbind_device);
        } else {
            tip = rs.getString(R.string.setting_unbind_auth_device);
        }

        unbindDialog = DialogUtils.showCommonDialog(this, true,
                rs.getString(R.string.setting_delete_device), tip, null, null,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.btn_positive) {
//                            stopProtect();//在摄像机解绑之前先关闭安全防护,避免下次摄像机配网成功之后还能查询到上次设置的安全防护数据.
                            unBindDevice();
                            unbindDialog.dismiss();
                            NewEagleSettingActivity.this.finish();
                        } else if (id == R.id.btn_negative) {
                            unbindDialog.dismiss();
                        }
                    }
                });
    }

    private void unBindDevice() {
        callback_flag = FLAG_UNBIND_DEVICE;
        // 检查是否超时
        if (System.currentTimeMillis() < userInfo.getExpires() * 1000L) {
            if (device.getIs_BindDevice()) {// 解除绑定设备
                sendRequest(RouteApiType.V3_BIND_UNBIND,
                        RouteLibraryParams.V3BindUnbind(userInfo.getAuth(),
                                device.getDevice_id()), true);
            } else {
            }
        } else {// 已经超时,重新登录
            reLogin();
        }
    }

    private class DeleteBundingMessageTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            uuid = ICamGlobal.getInstance().getUserinfo().getUuid();
            if (!TextUtils.isEmpty(uuid)) {
                mDataSource = new DataSource(NewEagleSettingActivity.this);
                msgList = mDataSource.queryAlarmMessages(uuid,
                        device.getDevice_id());
                mDataSource.deleteAlarmMessage(msgList, uuid);
            }
            return null;
        }
    }

    /**
     * //     * @param led_on       0 : 关闭LED 1 ：开启LED
     * //     * @param audio_online 上线提醒 0 ：关闭 1 : 开启
     *
     * @MethodName: configLedAndVoicePrompt
     * @Function: 设置LED及语音提示设置
     */
    // 查询LED及语音提示设置
    private void queryLedAndVoicePromptInfo() {
        SipController.getInstance().sendMessage(
                device.getSip_username() + "@" + device.getSip_domain(),
                SipHandler.QueryLedAndVoicePromptInfo(
                        "sip:" + device.getSip_username() + "@"
                                + device.getSip_domain(), seq++),
                app.registerAccount());
        Log.e(TAG, "查询门铃灯");
        mDialogManager.showDialog(SEND_SIP_REQUEST, this, null, null);
    }

    private void configLedAndVoicePrompt(String led_on, String audio_online, String angle) {
        SipController.getInstance().sendMessage(
                device.getSip_username() + "@" + device.getSip_domain(),
                SipHandler.ConfigLedAndVoicePrompt(
                        "sip:" + device.getSip_username() + "@"
                                + device.getSip_domain(), seq++, led_on,
                        audio_online, angle), app.registerAccount());
        Log.e(TAG, "配置门铃灯，状态为：" + led_on);
        mDialogManager.showDialog(SEND_SIP_REQUEST, this, null, null);
    }


    private void jumpToHistoryVideo() {
        if (device.getIs_online() == 0) {
            CustomToast.show(this, R.string.setting_device_offline);
            return;
        }
        if (!hasSDCard) {
            CustomToast.show(this, R.string.setting_please_insert_sdcard);
            return;
        }
        startActivity(new Intent(NewEagleSettingActivity.this,
                HistoryVideoSettingActivity.class).putExtra("device",
                device));
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.tb_doorbell_light_switch) {
            if (isChecked) {
                configLedAndVoicePrompt("1", "1", "1");
            } else {
                configLedAndVoicePrompt("0", "1", "1");
            }
        }
    }
}
