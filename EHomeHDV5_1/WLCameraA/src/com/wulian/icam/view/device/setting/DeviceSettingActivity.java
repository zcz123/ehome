/**
 * Project Name:  iCam
 * File Name:     PersonalInfoActivity.java
 * Package Name:  com.wulian.icam.view.setting
 *
 * @Date: 2014年10月16日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.setting;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.datasource.DataSource;
import com.wulian.icam.model.AlarmMessage;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.VersionInfo;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.StringUtil;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.utils.WifiAdmin;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.device.config.DeviceIdQueryActivity;
import com.wulian.icam.view.protect.SafeProtectActivity;
import com.wulian.icam.view.replay.HistoryVideoSettingActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipProfile;
import com.wulian.siplibrary.model.linkagedetection.LinkageDetectionModel;
import com.yuantuo.customview.ui.WLToast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wangjj
 * @ClassName: DeviceSettingActivity
 * @Function: 设备设置
 * @Date: 2014年10月16日
 * @email wangjj@wuliangroup.cn
 */
public class DeviceSettingActivity extends BaseFragmentActivity implements
        OnClickListener, OnCheckedChangeListener {
    private static final String SEND_SIP_REQUEST = "send_sip_request";
    private static final int REQUESTCODE = 1;
    private LinearLayout ll_wifi_setting, ll_delete_device, ll_firmware_update,
            ll_history_video_setting, ll_device_desc, ll_device_description,
            ll_device_protect, ll_device_for_v5;
    private LinearLayout ll_doorlock_bind;
    private LinearLayout ll_video_invert;
    private LinearLayout ll_led_invert;
    private LinearLayout ll_voice_invert;
    private TextView tv_device_name, tv_delete_info, tv_sdcard_status,
            tv_device_desc, tv_device_version, tv_protect_status;
    private TextView tv_bind_status;
    private ImageView titlebar_back;
    private CheckBox cb_video_invert, cb_led_invert, cb_voice_invert;
    private Dialog mRenameDialog;
    private Dialog mDeleteDialog;
    private Dialog mNotifyUpdateDialog;
    private LinearLayout ll_device_function;
    private EditText et_focus;
    private String binded_doorLock_name;
    private String cameraId;
    private SharedPreferences preferences;

    private WifiAdmin wifiAdmin;
    private boolean hasSDCard;
    private boolean isClickToUpdateVersion;
    private boolean isQueryLedAndVoicePromptInfo = false;
    private Device device;
    private int callback_flag;
    private int seq = 1;
    private String deviceId;
    private String sipCallWithDomain;// xxx@wuliangruop.cn
    private String deviceSipAccount;// 设备sip账号
    private String deviceControlUrl;// 设备控制sip地址
    private String deviceCallUrl;// 设备呼叫sip地址
    private SipProfile account;
    private VersionInfo cmicWebVersionInfo = null;
    private int deviceVersionCode;
    private static final int FLAG_UNBIND_DEVICE = 0;
    private static final int FLAG_EDITMETA_DEVICE = 1;
    private SharedPreferences sp;
    private String device_name;
    private String spGwId;
    private static final int MSG_FINISH = 1;
    private static final int MSG_EDIT_META = 2;
    private static final int MSG_HIDE_IME = 3;
    private String led_on = "1", audio_online = "1", angle = "0";
    PackageManager packageManager;

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:// 结束页面
                    DeviceSettingActivity.this.finish();
                    break;
                case MSG_EDIT_META:// 编辑设备信息
                    sendEditMeta();
                    break;
                case MSG_HIDE_IME:// 隐藏键盘
                    Utils.hideIme(DeviceSettingActivity.this);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        initViews();
        initListeners();
        initData();
        onSendSipRemoteAccess();
        if (device.getIs_online() == 1) {
            initWebData();
        }
    }


    private void initWebData() {
        // 122 查询存储状态
        SipController.getInstance().sendMessage(
                sipCallWithDomain,
                SipHandler
                        .QueryStorageStatus("sip:" + sipCallWithDomain, seq++),
                app.registerAccount());
        //查询led和音量
        queryLedAndVoicePromptInfo();
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
        showDoorlockInfo();
        showProtectInfo();
        if (device == null) {// 必须的数据为空了，直接结束
            this.finish();
        }
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

    private void initViews() {
        ((TextView) findViewById(R.id.titlebar_title))
                .setText(R.string.setting_device_setting);
        ll_wifi_setting = (LinearLayout) findViewById(R.id.ll_wifi_setting);
        ll_delete_device = (LinearLayout) findViewById(R.id.ll_delete_device);
        titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
        et_focus = (EditText) findViewById(R.id.et_focus);
        tv_device_name = (TextView) findViewById(R.id.tv_device_name);
        ll_device_function = (LinearLayout) findViewById(R.id.ll_device_function);
        ll_firmware_update = (LinearLayout) findViewById(R.id.ll_firmware_update);
        tv_delete_info = (TextView) findViewById(R.id.tv_delete_info);
        tv_sdcard_status = (TextView) findViewById(R.id.tv_sdcard_status);
        tv_device_desc = (TextView) findViewById(R.id.tv_device_desc);
        tv_device_version = (TextView) findViewById(R.id.tv_device_version);
        ll_history_video_setting = (LinearLayout) findViewById(R.id.ll_history_video_setting);
        ll_video_invert = (LinearLayout) findViewById(R.id.ll_video_invert);
        ll_led_invert = (LinearLayout) findViewById(R.id.ll_led_invert);
        ll_voice_invert = (LinearLayout) findViewById(R.id.ll_voice_invert);
        ll_device_desc = (LinearLayout) findViewById(R.id.ll_device_desc);
        ll_device_description = (LinearLayout) findViewById(R.id.ll_device_description);
        ll_device_for_v5 = (LinearLayout) findViewById(R.id.ll_device_for_v5);
        ll_doorlock_bind = (LinearLayout) findViewById(R.id.ll_doorlock_bind);
        ll_device_protect = (LinearLayout) findViewById(R.id.ll_device_protect);
        cb_video_invert = (CheckBox) findViewById(R.id.cb_video_invert);
        cb_led_invert = (CheckBox) findViewById(R.id.cb_led_invert);
        cb_voice_invert = (CheckBox) findViewById(R.id.cb_voice_invert);
        tv_bind_status = (TextView) findViewById(R.id.tv_bind_status);
        tv_protect_status = (TextView) findViewById(R.id.tv_protect_status);

        if (ICamGlobal.forV5) {
            ll_device_for_v5.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        titlebar_back.setOnClickListener(this);
        ll_wifi_setting.setOnClickListener(this);
        ll_delete_device.setOnClickListener(this);
//        ll_firmware_update.setOnClickListener(this);
        ll_history_video_setting.setOnClickListener(this);
        ll_video_invert.setOnClickListener(this);
        ll_led_invert.setOnClickListener(this);
        ll_voice_invert.setOnClickListener(this);
        ll_device_desc.setOnClickListener(this);
        tv_device_desc.setOnClickListener(this);
        ll_device_description.setOnClickListener(this);
        ll_device_protect.setOnClickListener(this);
        ll_doorlock_bind.setOnClickListener(this);
        cb_video_invert.setOnCheckedChangeListener(this);
        cb_voice_invert.setOnCheckedChangeListener(this);
        cb_led_invert.setOnCheckedChangeListener(this);
        cb_video_invert
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        sp.edit()
                                .putBoolean(deviceId + APPConfig.VIDEO_INVERT,
                                        isChecked).commit();
                    }
                });

    }

    private void initData() {
        sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
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
        cb_video_invert.setChecked(sp.getBoolean(deviceId
                + APPConfig.VIDEO_INVERT, false));
        tv_device_desc.setText(device.getDevice_nick());
        ll_device_function
                .setVisibility(device.getIs_BindDevice() ? View.VISIBLE
                        : View.GONE);
        sipCallWithDomain = device.getDevice_id() + "@"
                + device.getSip_domain();
        if (device.getIs_online() == 1) {
            getLastDeviceVersion();
        }
    }

    private void showDoorlockInfo() {//显示保存在本地的门锁绑定信息
        String loginGwId;
        SharedPreferences gwIdSp = this.getSharedPreferences("doorLockGwId", MODE_PRIVATE);
        loginGwId = gwIdSp.getString("gwId", "");
        SharedPreferences bindSp = this.getSharedPreferences(loginGwId + device.getDevice_id().substring(8), MODE_PRIVATE);
        Log.i("------3",loginGwId+"--"+device.getDevice_id().substring(8));
        binded_doorLock_name = bindSp.getString("devName", "error");
        cameraId = bindSp.getString("cameraId", "");
        spGwId = bindSp.getString("gwID", "");

        if (!StringUtil.isNullOrEmpty(binded_doorLock_name) && !StringUtil.equals("error", binded_doorLock_name)) {
            tv_bind_status.setText(binded_doorLock_name);
        } else {
            tv_bind_status.setText("");
        }
    }

    private void showProtectInfo() {
        sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
        boolean isMoveEnable = sp.getBoolean(device.getDevice_id()
                + APPConfig.IS_MOVE_DETECTION, false);
        if (isMoveEnable) {
            tv_protect_status.setText(getString(R.string.protect_start_set));
        } else {
            tv_protect_status.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    private void renameDeviceDialog() {
        Resources rs = getResources();
        mRenameDialog = DialogUtils.showCommonEditDialog(this, false,
                rs.getString(R.string.setting_enter_device_name), null, null,
                rs.getString(R.string.setting_enter_device_name),
                device.getDevice_nick(), new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.et_input) {
                            EditText infoEt = (EditText) v;
                            device_name = infoEt.getText().toString().trim();
                            if (!TextUtils.isEmpty(device_name)) {
                                if (!device_name.equals(device.getDevice_nick())) {
                                    myHandler.sendEmptyMessageDelayed(
                                            MSG_EDIT_META, 100);
                                    myHandler.sendEmptyMessageDelayed(
                                            MSG_HIDE_IME, 50);
                                    mRenameDialog.dismiss();
                                } else {
                                    mRenameDialog.dismiss();
                                }
                            } else {
                                Utils.shake(DeviceSettingActivity.this, infoEt);
                            }
                        } else if (id == R.id.btn_negative) {
                            mRenameDialog.dismiss();
                        }
                    }
                });
    }

    private void stopProtect() {
        showBaseDialog();
        String moveArea = sp.getString(device.getDevice_id()
                + APPConfig.MOVE_AREA, ";");

        SipController.getInstance().sendMessage(
                deviceControlUrl,
                SipHandler.ConfigMovementDetection(deviceControlUrl, seq++,
                        false, 50, moveArea.split(";")), account);

        LinkageDetectionModel model = new LinkageDetectionModel();
        model.setUse(false);

        SipController.getInstance().sendMessage(
                deviceControlUrl,
                SipHandler.ConfigLinkageArming(deviceControlUrl, seq++, 1, 1,
                        model), account);

        ICamGlobal.isNeedRefreshDeviceList = true;
    }

    private void deleteDeviceDialog() {
        String tip;
        Resources rs = getResources();
        if (device.getIs_BindDevice()) {
            tip = rs.getString(R.string.setting_unbind_device);
        } else {
            tip = rs.getString(R.string.setting_unbind_auth_device);
        }

        mDeleteDialog = DialogUtils.showCommonDialog(this, true,
                rs.getString(R.string.setting_delete_device), tip, null, null,
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.btn_positive) {
                            stopProtect();//在摄像机解绑之前先关闭安全防护,避免下次摄像机配网成功之后还能查询到上次设置的安全防护数据.
                            unBindDevice();
//                            mDeleteDialog.dismiss();
//                            DeviceSettingActivity.this.finish();

                        } else if (id == R.id.btn_negative) {
                            mDeleteDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_wifi_setting) {
            Intent mIntent = new Intent();
            mIntent.setClass(DeviceSettingActivity.this,
                    DeviceIdQueryActivity.class);
            mIntent.putExtra("msgData", deviceId);
            mIntent.putExtra("isAddDevice", false);
            startActivity(mIntent);
        } else if (id == R.id.ll_history_video_setting) {
            if (device.getIs_online() == 0) {
                CustomToast.show(this, R.string.setting_device_offline);
                return;
            }
            if (!hasSDCard) {
                CustomToast.show(this, R.string.setting_please_insert_sdcard);
                return;
            }
            startActivity(new Intent(DeviceSettingActivity.this,
                    HistoryVideoSettingActivity.class).putExtra("device",
                    device));
        } else if (id == R.id.ll_device_description) {
            // 进入设备详细界面
            startActivity(new Intent(DeviceSettingActivity.this,
                    DeviceDetailActivity.class).putExtra("device", device));
        } else if (id == R.id.tv_device_desc || id == R.id.ll_device_desc) {
            if (device.getIs_BindDevice()) {
                renameDeviceDialog();
            }
        } else if (id == R.id.ll_firmware_update) {
            if (device.getIs_online() == 0) {
                CustomToast.show(this, R.string.setting_device_offline);
                return;
            }
            isClickToUpdateVersion = true;
            // 设备versioncode
            // 远程versioncode
            ICamGlobal.isSilentUpdate = true;// 需要显示指定一下，避免受版本升级影响;不关闭进度条。
            showBaseDialog();
            getLastDeviceVersion();
        } else if (id == R.id.ll_delete_device) {
            deleteDeviceDialog();
        } else if (id == R.id.titlebar_back) {
            this.finish();
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_right_out);
            return;
//        } else if (id == R.id.ll_video_invert) {
////            showBaseDialog();
//            mDialogManager.showDialog(SEND_CAMERA_VIDEO_SIP_REQUEST,this,null,null);
//            isQueryLedAndVoicePromptInfo = false;
//            // TODO 图像翻转角度设置
//            cb_video_invert.toggle();
//        } else if (id == R.id.ll_led_invert) {
////            showBaseDialog();
//            isQueryLedAndVoicePromptInfo = false;
//            // TODO led设置
//            cb_led_invert.toggle();
//        } else if (id == R.id.ll_voice_invert) {
////            showBaseDialog();
//            isQueryLedAndVoicePromptInfo = false;
//            // TODO 提示音设置
//            cb_voice_invert.toggle();
        } else if (id == R.id.ll_doorlock_bind) {
            startActivity(new Intent("devdiv.intent.action.BindDoorlockActivity").putExtra("device", device));
        } else if (id == R.id.ll_device_protect) {
            // guideToIcam();
            if (device.getIs_online() == 0) {
                CustomToast.show(this, R.string.setting_device_offline);
                return;
            }
            startActivity(new Intent(DeviceSettingActivity.this,
                    SafeProtectActivity.class).putExtra("device", device));
        }
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }


    private boolean checkDeviceVersion = true;

    private void getLastDeviceVersion() {
        checkDeviceVersion = true;
//        sendRequest(RouteApiType.v3,
//                RouteLibraryParams.VersionCheck(deviceId.substring(0, 6)
//                        .toLowerCase(), ""
//                        + Utils.getPackageInfo(this).versionCode), false);// 打开进度条
    }

    private void  clearCameraData(){
        preferences = this.getSharedPreferences(
                "preference", Context.MODE_PRIVATE);
        preferences.edit().remove("p_key_monitor_list").commit();
    }

    /**
     * @Function 修改设备信息
     * @author Wangjj
     * @date 2014年11月27日
     */
    private void sendEditMeta() {
        callback_flag = FLAG_EDITMETA_DEVICE;
        // 检查是否超时
        if (System.currentTimeMillis() < userInfo.getExpires() * 1000L) {
            if (device_name.length() > 15) {
                WLToast.showToast(DeviceSettingActivity.this,
                        "摄像机名称最大支持15个字符。", 1000);
            } else {
                RouteLibraryController.getInstance().doRequest(
                        DeviceSettingActivity.this,
                        RouteApiType.V3_USER_DEVICE,
                        RouteLibraryParams.V3UserDevice(userInfo.getAuth(),
                                device.getDevice_id(), device_name, ""),
                        DeviceSettingActivity.this);
            }

        } else {// 已经超时,重新登录
            reLogin();
        }

    }

    /**
     * @Function 解除设备的绑定
     * @author Wangjj
     * @date 2014年11月28日
     */
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

    @Override
    protected void DataReturn(boolean success, RouteApiType apiType, String json) {
        super.DataReturn(success, apiType, json);
        if (success) {
            switch (apiType) {
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
                case V3_USER_DEVICE:
                    CustomToast.show(this,
                            R.string.setting_device_edit_meta_success);
                    ICamGlobal.isNeedRefreshDeviceList = true;
                    // 修改本地对象为最新的值
                    device.setDevice_nick(device_name);
                    tv_device_desc.setText(device_name);
                    break;
                case V3_BIND_UNBIND:
                    clearCameraData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDeleteDialog.dismiss();
                            DeviceSettingActivity.this.finish();
                        }
                    });
                    mTask = new DeleteBundingMessageTask();
                    mTask.execute();// 删除主绑定相关的报警消息

                    // 不用break
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
                case QUERY_STORAGE_STATUS:// 122 查询存储状态
                    Utils.sysoInfo("查询存储状态:" + xmlData);
                    // <storage num="1" type="SD" status="1", attr="rw"
                    // totalsize="1000K" freesize="0K"></storage>
                    Pattern pstatus = Pattern
                            .compile("<storage.*status=\"(\\d)\"\\s+.*/?>(</storage>)?");
                    Matcher matchers = pstatus.matcher(xmlData);
                    if (matchers.find()) {
                        String status = matchers.group(1).trim();
                        if ("1".equals(status)) {
                            hasSDCard = false;
                            tv_sdcard_status.setText(getResources().getString(
                                    R.string.common_no_sdcard));
                        } else if ("2".equals(status)) {
                            hasSDCard = true;
                            tv_sdcard_status.setText(getResources().getString(
                                    R.string.setting_has_sdcard));
                        }
                    }
                    break;
                case QUERY_FIREWARE_VERSION:// 查询固件版本
                    Utils.sysoInfo("===" + "QUERY_FIREWARE_VERSION");
                    try {
                        deviceVersionCode = Integer.parseInt(Utils.getParamFromXml(
                                xmlData, "version_id"));
                        String deviceVersion = Utils.getParamFromXml(xmlData,
                                "version");
                        Utils.sysoInfo("###########" + deviceVersion);
                    } catch (NumberFormatException e) {

                        Utils.sysoInfo("服务器返回的固件版本号错误!");
                        deviceVersionCode = 0;
                    }
                    if (cmicWebVersionInfo == null) {
                        return;
                    }
                    // 比较版本
                    if (cmicWebVersionInfo.getVersion_code() < deviceVersionCode) {
                        // 需要更新
                        if (isClickToUpdateVersion) {
                            showNoticeDialog();
                        }
                        tv_device_version.setText(getResources().getString(
                                R.string.setting_device_version_past));
                    } else {
                        // 已经是最新版本
                        if (isClickToUpdateVersion) {
                            CustomToast
                                    .show(this, R.string.setting_latest_fireware);
                            // showLatestOrFailDialog(DIALOG_TYPE_LATEST);
                        }
                        tv_device_version.setText(getResources().getString(
                                R.string.setting_device_version_newest));
                    }
                    break;
                case NOTIFY_FIREWARE_UPDATE:// 通知版本更新
                    Utils.sysoInfo("===" + "NOTIFY_FIREWARE_UPDATE");
                    CustomToast.show(this,
                            R.string.setting_already_notice_fireware_update);
                    break;
                case QUERY_LED_AND_VOICE_PROMPT_INFO:// 查询LED及语音提示设置
                    // TODO 解析xml
                    led_on = Utils.getParamFromXml(xmlData, "led_on").trim();
                    audio_online = Utils.getParamFromXml(xmlData, "audio_online")
                            .trim();
                    angle = Utils.getParamFromXml(xmlData, "angle").trim();
                    // System.out.println("查询LED请求");
                    if (!TextUtils.isEmpty(led_on)) {
                        if (led_on.equals("0")) {
                            cb_led_invert.setChecked(false);
                        } else {// 1
                            cb_led_invert.setChecked(true);
                        }
                    }
                    if (!TextUtils.isEmpty(audio_online)) {
                        if (audio_online.equals("0")) {
                            cb_voice_invert.setChecked(false);
                        } else {// 1
                            cb_voice_invert.setChecked(true);
                        }
                    }
                    //该功能V5.3.4版本开放（画面倒置同步问题，需要在playvideo中改isvideovert参数）
//                    if (!TextUtils.isEmpty(angle)) {
//                        if (angle.equals("0")) {
//                            cb_video_invert.setChecked(false);
//                        } else {
//                            //180
//                            cb_video_invert.setChecked(true);
//                        }
//                    }
                    break;
                case CONFIG_LED_AND_VOICE_PROMPT://
                    CustomToast.show(this, R.string.common_setting_success);
                    break;
            }
        } else {
            Utils.sysoInfo("sip fail:" + xmlData);
            switch (apiType) {
                case QUERY_FIREWARE_VERSION:// 查询固件版本
                    if (isClickToUpdateVersion) {
                        CustomToast
                                .show(this, R.string.setting_query_fireware_fail);
                    }
                    break;
                case NOTIFY_FIREWARE_UPDATE:// 通知版本更新
                    CustomToast.show(this, R.string.setting_notice_fireware_fail);
                    break;
                case QUERY_LED_AND_VOICE_PROMPT_INFO:// 查询LED及语音提示设置
                    CustomToast.show(this, R.string.config_query_device_fail);
                    break;
                case CONFIG_LED_AND_VOICE_PROMPT://
                    CustomToast.show(this, R.string.common_setting_fail);
                    break;
            }
        }
    }

    // 固件升级
    private void showNoticeDialog() {
        Resources rs = getResources();
        mNotifyUpdateDialog = DialogUtils.showCommonDialog(this, true,
                rs.getString(R.string.setting_version_update) + " "
                        + cmicWebVersionInfo.getVersion_name(),
                Html.fromHtml(cmicWebVersionInfo.getDesc()),
                rs.getString(R.string.setting_update_now),
                rs.getString(R.string.setting_update_later),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.btn_positive) {
                            mNotifyUpdateDialog.dismiss();
                            // 发送远程更新命令
                            showBaseDialog();
                            SipController.getInstance().sendMessage(
                                    device.getSip_username() + "@"
                                            + device.getSip_domain(),
                                    SipHandler.NotifyFirewareUpdate("sip:"
                                                    + device.getSip_username() + "@"
                                                    + device.getSip_domain(), seq++,
                                            cmicWebVersionInfo
                                                    .getVersion_name(),
                                            cmicWebVersionInfo
                                                    .getVersion_code()),
                                    app.registerAccount());
                        } else if (id == R.id.btn_negative) {
                            mNotifyUpdateDialog.dismiss();
                        }
                    }
                });
    }

    private String uuid;
    private DataSource mDataSource;
    private List<AlarmMessage> msgList = null;
    private DeleteBundingMessageTask mTask;

    private class DeleteBundingMessageTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            uuid = ICamGlobal.getInstance().getUserinfo().getUuid();
            if (!TextUtils.isEmpty(uuid)) {
                mDataSource = new DataSource(DeviceSettingActivity.this);
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
     * @author: yuanjs
     * @date: 2015年10月22日
     * @email: jiansheng.yuan@wuliangroup.com
     */
    // 查询LED及语音提示设置
    private void queryLedAndVoicePromptInfo() {
        SipController.getInstance().sendMessage(
                device.getSip_username() + "@" + device.getSip_domain(),
                SipHandler.QueryLedAndVoicePromptInfo(
                        "sip:" + device.getSip_username() + "@"
                                + device.getSip_domain(), seq++),
                app.registerAccount());
        mDialogManager.showDialog(SEND_SIP_REQUEST, this, null, null);
    }

    private void configLedAndVoicePrompt(String led_on, String audio_online, String angle) {
        SipController.getInstance().sendMessage(
                device.getSip_username() + "@" + device.getSip_domain(),
                SipHandler.ConfigLedAndVoicePrompt(
                        "sip:" + device.getSip_username() + "@"
                                + device.getSip_domain(), seq++, led_on,
                        audio_online, angle), app.registerAccount());
        mDialogManager.showDialog(SEND_SIP_REQUEST, this, null, null);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (!buttonView.isPressed()) return;
        if (id == R.id.cb_video_invert) {
//            sp.edit().putBoolean(deviceId + APPConfig.VIDEO_INVERT, isChecked)
//                    .commit();
//            if (isChecked) {
//                angle = "180";
//            } else {
//                angle = "0";
//            }
//            if (isQueryLedAndVoicePromptInfo) {
//                isQueryLedAndVoicePromptInfo = false;
//            } else {
//                // System.out.println("执行LED设置");
//                configLedAndVoicePrompt(led_on, audio_online, angle);
////                mDialogManager.showDialog(SEND_CAMERA_VIDEO_SIP_REQUEST,this,null,null);
//            }
        } else if (id == R.id.cb_led_invert) {
            // TODO
            if (isChecked) {
                led_on = "1";
                // sp.edit().putString(CONFIG_LED + "_" + device.getDid(),
                // "1").commit();
            } else {
                led_on = "0";
                // sp.edit().putString(CONFIG_LED + "_" + device.getDid(),
                // "0").commit();
            }
            if (isQueryLedAndVoicePromptInfo) {
                isQueryLedAndVoicePromptInfo = false;
            } else {
                // System.out.println("执行LED设置");
                configLedAndVoicePrompt(led_on, audio_online, angle);
            }
        } else if (id == R.id.cb_voice_invert) {
            // TODO
            if (isChecked) {
                audio_online = "1";
                // sp.edit().putString(CONFIG_VOICE + "_" +
                // device.getDid(), "1").commit();
            } else {
                audio_online = "0";
                // sp.edit().putString(CONFIG_VOICE + "_" +
                // device.getDid(), "0").commit();
            }
            if (isQueryLedAndVoicePromptInfo) {
                isQueryLedAndVoicePromptInfo = false;
            } else {
                // System.out.println("执行语音提示设置");
                configLedAndVoicePrompt(led_on, audio_online, angle);
            }
        }
    }
}