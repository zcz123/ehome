/**
 * Project Name:  iCam
 * File Name:     SafePortectionActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015年2月2日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.protect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.Device;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipProfile;
import com.wulian.siplibrary.model.linkagedetection.LinkageDetectionModel;

/**
 * @author Wangjj
 * @ClassName: SafeProtectActivity
 * @Function: 安全防护
 * @Date: 2015年2月2日
 * @email wangjj@wuliangroup.cn
 */
public class SafeProtectActivity extends BaseFragmentActivity implements
        OnClickListener {
    private Button btn_start_protect;

    private LinearLayout ll_protect_on, ll_protect, ll_resetting,
            ll_stop_protect;
    private TextView tv_safe_protected_tip;
    SharedPreferences sp;

    private int sipokcount = 0;
    Device device;

    String deviceSipAccount;// 设备sip账号
    String deviceControlUrl;// 设备控制sip地址
    String deviceCallUrl;// 设备呼叫sip地址
    private SipProfile account;
    int seq = 1;
    // private AlertDialog dialogUnBindDevice;
    // private View dialogUnBindDeviceView;
    private Dialog mStopDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initListeners();
        initData();
        onSendSipRemoteAccess();
        initWebData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onSendSipRemoteAccess();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStopSipRemoteAccess();
    }

    private void initViews() {
        btn_start_protect = ((Button) findViewById(R.id.btn_start_protect));
        // btn_stop_protect = ((Button) findViewById(R.id.btn_stop_protect));
        // btn_resetting = ((Button) findViewById(R.id.btn_resetting));
        ll_protect = (LinearLayout) findViewById(R.id.ll_protect);
        ll_protect_on = (LinearLayout) findViewById(R.id.ll_protect_on);
        // safe_protect_time_tip
        ll_resetting = (LinearLayout) findViewById(R.id.ll_resetting);
        ll_stop_protect = (LinearLayout) findViewById(R.id.ll_stop_protect);

        tv_safe_protected_tip = (TextView) findViewById(R.id.tv_safe_protected_tip);
    }

    private void initListeners() {
        btn_start_protect.setOnClickListener(this);
        // btn_stop_protect.setOnClickListener(this);
        // btn_resetting.setOnClickListener(this);
        ll_protect_on.setOnClickListener(this);
        ll_resetting.setOnClickListener(this);
        ll_stop_protect.setOnClickListener(this);
    }

    private void initData() {
        device = (Device) getIntent().getSerializableExtra("device");
        deviceSipAccount = device.getDevice_id();
        deviceCallUrl = deviceSipAccount + "@" + device.getSip_domain();
        deviceControlUrl = deviceCallUrl;
        sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
        // 1、初始化sip
        // app.initSip();
        // 2、用户注册账号
        account = app.registerAccount();
        if (account == null) {
            CustomToast.show(this, R.string.login_user_account_register_fail);
            this.finish();
        }
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_safe_protect);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.safety_protection);
    }

    @Override
    protected OnClickListener getLeftClick() {
        return this;
    }

    private void updateView() {
        boolean isMoveEnable = sp.getBoolean(device.getDevice_id()
                + APPConfig.IS_MOVE_DETECTION, false);
        if (isMoveEnable) {
            ll_protect.setVisibility(View.VISIBLE);
            btn_start_protect.setVisibility(View.GONE);

            String weekday = sp.getString(device.getDevice_id()
                    + APPConfig.MOVE_WEEKDAY, "");
            String time = sp.getString(device.getDevice_id()
                    + APPConfig.MOVE_TIME, "");

            String weekday_move = convertWeekday(weekday);
            String time_move = convertTime(time);
            tv_safe_protected_tip.setText(weekday_move + time_move);
        } else {
            ll_protect.setVisibility(View.GONE);
            btn_start_protect.setVisibility(View.VISIBLE);
        }
    }

    private String convertWeekday(String numbers) {
        // 7,1,2,3,4,5,6, ->每天 everyday
        // 1,2,3,4,5, ->工作日 workday
        // 2,4, -> 周二,周四 Tue,Thus

        if ("7,1,2,3,4,5,6,".equals(numbers)) {
            return getResources().getString(R.string.common_everyday);
        }
        if ("1,2,3,4,5,".equals(numbers)) {

            return getResources().getString(R.string.common_workday);
        }
        if (TextUtils.isEmpty(numbers) || ",".equals(numbers)) {
            // 默认值为 ""
            // 网络同步下来 但没有日期数据，解析后为""
            // 设置日期 但未选中时候 为","
            return "";
        } else {
            String weekdays[] = numbers.split(",");
            StringBuilder sb = new StringBuilder();
            Resources rs = getResources();
            for (String s : weekdays) {
                int sValue = Integer.parseInt(s);
                switch (sValue) {
                    case 7:
                        sb.append(rs.getString(R.string.common_sun)).append(",");
                        break;
                    case 1:
                        sb.append(rs.getString(R.string.common_mon)).append(",");
                        break;
                    case 2:
                        sb.append(rs.getString(R.string.common_tue)).append(",");
                        break;
                    case 3:
                        sb.append(rs.getString(R.string.common_wed)).append(",");
                        break;
                    case 4:
                        sb.append(rs.getString(R.string.common_thurs)).append(",");
                        break;
                    case 5:
                        sb.append(rs.getString(R.string.common_fri)).append(",");
                        break;
                    case 6:
                        sb.append(rs.getString(R.string.common_sat)).append(",");
                        break;

                    default:
                        break;
                }
            }
            return sb.toString()
                    .substring(0, sb.length() - 1 < 0 ? 0 : sb.length() - 1)
                    .replace("周", "");
        }
    }

    private String convertTime(String times) {
        if (TextUtils.isEmpty(times)) {
            return "";
        } else {
            String timeNums[] = times.split(",");
            if (timeNums.length == 4) {
                Utils.formatSingleNum(timeNums);
                return timeNums[0] + ":" + timeNums[1] + "-" + timeNums[2]
                        + ":" + timeNums[3];
            } else {
                return "";
            }

        }
    }

    public void initWebData() {
        // 查询移动布防
        SipController.getInstance().sendMessage(deviceControlUrl,
                SipHandler.QueryMovementDetectionInfo(deviceControlUrl, seq++),
                account);
        SipController.getInstance().sendMessage(deviceControlUrl,
                SipHandler.QueryLinkageArmingInfo(deviceControlUrl, seq++, 1),
                account);
        // // 查询遮挡布防
        // SipController.getInstance().sendMessage(deviceControlUrl,
        // SipHandler.QueryBlockDetectionInfo(deviceControlUrl, seq++),
        // account);
        // SipController.getInstance().sendMessage(deviceControlUrl,
        // SipHandler.QueryLinkageArmingInfo(deviceControlUrl, seq++, 2),
        // account);

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

    private void showStopConfirmDialog() {
        Resources rs = getResources();
        mStopDialog = DialogUtils.showCommonDialog(this, true,
                rs.getString(R.string.common_tip),
                rs.getString(R.string.protect_stop_confirm), null, null,
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.btn_positive) {
                            mStopDialog.dismiss();
                            sp.edit().putBoolean(device.getDevice_id()
                                    + APPConfig.IS_MOVE_DETECTION, false).commit();
                            stopProtect();
                        } else if (id == R.id.btn_negative) {
                            mStopDialog.dismiss();
                        }
                    }
                });

        // if (dialogUnBindDevice == null) {
        // dialogUnBindDevice = new AlertDialog.Builder(this,
        // R.style.alertDialogIosAlert).create();
        // }
        //
        // if (dialogUnBindDeviceView == null) {
        // dialogUnBindDeviceView = LinearLayout.inflate(this,
        // R.layout.custom_alertdialog_notice_ios,
        // (ViewGroup) findViewById(R.id.ll_custom_alertdialog));
        //
        // ((TextView) dialogUnBindDeviceView.findViewById(R.id.tv_info))
        // .setText(R.string.protect_stop_confirm);
        //
        // ((Button) dialogUnBindDeviceView.findViewById(R.id.btn_positive))
        // .setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // stopProtect();
        // dialogUnBindDevice.dismiss();
        // }
        // });
        // ((Button) dialogUnBindDeviceView.findViewById(R.id.btn_negative))
        // .setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // dialogUnBindDevice.dismiss();
        // }
        // });
        // }
        //
        // Utils.updateDialog2BottomDefault(dialogUnBindDevice);
        // dialogUnBindDevice.show();
        // Utils.updateDialogWidth2ScreenWidthDefault(this, dialogUnBindDevice);
        // dialogUnBindDevice.setContentView(dialogUnBindDeviceView);
    }

    /**
     * @param numbers 7,1,2,3,4,5,6,
     * @return
     * @Function 转换星期
     * @author Wangjj
     * @date 2015年2月5日
     */
    public String getWeekFromXmlData(String xmlData) {
        // String weekdays[] = { "Sun", "Mon", "Tues", "Wed", "Thurs", "Fri",
        // "Sat" };
        StringBuilder sbWeek = new StringBuilder();

        if (xmlData.contains("day=\"Sun\"")) {
            sbWeek.append("7,");
        }
        if (xmlData.contains("day=\"Mon\"")) {
            sbWeek.append("1,");
        }
        if (xmlData.contains("day=\"Tues\"")) {
            sbWeek.append("2,");
        }
        if (xmlData.contains("day=\"Wed\"")) {
            sbWeek.append("3,");
        }
        if (xmlData.contains("day=\"Thurs\"")) {
            sbWeek.append("4,");
        }
        if (xmlData.contains("day=\"Fri\"")) {
            sbWeek.append("5,");
        }
        if (xmlData.contains("day=\"Sat\"")) {
            sbWeek.append("6,");
        }
        Utils.sysoInfo("解析的星期为:" + sbWeek.toString());
        return sbWeek.toString();
    }

    public static final int REQUESTCODE_SETTING = 1;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            this.finish();
        } else if (id == R.id.btn_start_protect) {
            startActivityForResult((new Intent(this,
                    SafeProtectSettingActivity.class)).putExtra("device",
                    device), REQUESTCODE_SETTING);
        } else if (id == R.id.ll_stop_protect) {
            // stopProtect();
            showStopConfirmDialog();
        } else if (id == R.id.ll_resetting
                || id == R.id.ll_protect_on) {
            startActivityForResult((new Intent(this,
                    SafeProtectSettingActivity.class)).putExtra("device",
                    device), REQUESTCODE_SETTING);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUESTCODE_SETTING:
                if (resultCode == RESULT_OK) {
                    updateView();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void SipDataReturn(boolean isSuccess, SipMsgApiType apiType,
                                 String xmlData, String from, String to) {
        super.SipDataReturn(isSuccess, apiType, xmlData, from, to);
        if (isSuccess) {
            switch (apiType) {
                case CONFIG_MOVEMENT_DETECTION:
                    if (Utils.getParamFromXml(xmlData, "status").equalsIgnoreCase(
                            "ok")) {
                        // CustomToast.show(this, "1");
                        sipokcount++;
                    } else {
                        CustomToast.show(this, R.string.common_setting_fail);
                    }
                    break;

                // case CONFIG_BLOCK_DETECTION:
                // if (Utils.getParamFromXml(xmlData, "status").equalsIgnoreCase(
                // "ok")) {
                // // CustomToast.show(this, "2");
                // sipokcount++;
                //
                // } else {
                // CustomToast.show(this, R.string.move_detection_fail);
                // }
                // break;

                case CONFIG_LINKAGE_ARMING:
                    if (Utils.getParamFromXml(xmlData, "status").equalsIgnoreCase(
                            "ok")) {
                        // CustomToast.show(this, "3");
                        sipokcount++;

                    } else {
                        CustomToast.show(this, R.string.common_setting_fail);
                    }
                    break;
                case QUERY_MOVEMENT_DETECTION_INFO:
                    Utils.sysoInfo("移动布防:" + xmlData);
                    String[] results1 = Utils.getMotionArea(xmlData,
                            APPConfig.MAX_MONITOR_AREA);
                    StringBuilder sb = new StringBuilder();
                    for (String s : results1) {
                        Utils.sysoInfo("还原监测区:" + s);
                        sb.append(s).append(";");
                    }
                    boolean isMoveEnable = "true".equals(Utils.getParamFromXml(
                            xmlData, "enable")) ? true : false;
                    int sen = 0;
                    try {
                        sen = Integer.parseInt(Utils.getParamFromXml(xmlData,
                                "sensitivity"));
                    } catch (NumberFormatException e) {
                        sen = 50;
                    }
                    Editor editor = sp.edit();
                    editor.putInt(device.getDevice_id()
                            + APPConfig.MOVE_SENSITIVITY, sen);// 灵敏度
                    editor.putBoolean(device.getDevice_id()
                            + APPConfig.IS_MOVE_DETECTION, isMoveEnable);// 启用
                    editor.putString(device.getDevice_id() + APPConfig.MOVE_AREA,
                            sb.toString().equals("") ? ";" : sb.toString());// 区域
                    editor.commit();

                    updateView();
                    break;

                // case QUERY_BLOCK_DETECTION_INFO:
                // Utils.sysoInfo("遮挡布防:" + xmlData);
                // String blockArea = Utils.getParamFromXml(xmlData, "area") + ";";
                // boolean isCoverEnable = "true".equals(Utils.getParamFromXml(
                // xmlData, "enable")) ? true : false;
                // int sen2 = 0;
                // try {
                // sen2 = Integer.parseInt(Utils.getParamFromXml(xmlData,
                // "sensitivity"));
                // } catch (NumberFormatException e) {
                // sen2 = 50;
                // }
                // Editor editor2 = sp.edit();
                // editor2.putInt(device.getDevice_id()
                // + APPConfig.COVER_SENSITIVITY, sen2);// 灵敏度
                // editor2.putBoolean(device.getDevice_id()
                // + APPConfig.IS_COVER_DETECTION, isCoverEnable);// 启用
                // editor2.putString(device.getDevice_id() + APPConfig.COVER_AREA,
                // blockArea);// 区域
                // editor2.commit();
                // break;

                case QUERY_LINKAGE_ARMING_INFO:
                    // TODO 数据返回，进行存储，需判断跨夜时间段
                    Utils.sysoInfo("联动信息:" + xmlData);
                    int eventType = 0;
                    try {
                        eventType = Integer.parseInt(Utils.getParamFromXml(xmlData,
                                "eventType"));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (eventType == 1 || eventType == 2) {// 1 移动布防 2 遮挡布防
                        String config_weekday, config_time;
                        if (eventType == 1) {
                            config_weekday = device.getDevice_id()
                                    + APPConfig.MOVE_WEEKDAY;
                            config_time = device.getDevice_id()
                                    + APPConfig.MOVE_TIME;
                        } else {
                            config_weekday = device.getDevice_id()
                                    + APPConfig.COVER_WEEKDAY;
                            config_time = device.getDevice_id()
                                    + APPConfig.COVER_TIME;
                        }

                        String weekTime = getWeekFromXmlData(xmlData);
                        Editor weekAndTimeEditor = sp.edit();
                        // if (!TextUtils.isEmpty(weekTime)) {
                        weekAndTimeEditor.putString(config_weekday, weekTime);
                        // }
                        // <time start="00:48" end="21:00">0</time>
                        Pattern p = Pattern
                                .compile("<time start=\"(.+)\" end=\"(.+)\">0</time>");
                        Pattern p2 = Pattern
                                .compile("<time start=\"(.+)\" end=\"(.+)\">1</time>");
                        Matcher matcher = p.matcher(xmlData);
                        Matcher matcher2 = p2.matcher(xmlData);
                    /*
                     * if (matcher.find()) { String startTime[] =
					 * matcher.group(1).trim().split(":"); String endTime[] =
					 * matcher.group(2).trim().split(":"); if (startTime.length
					 * == 2 && endTime.length == 2) {
					 * weekAndTimeEditor.putString(config_time, startTime[0] +
					 * "," + startTime[1] + "," + endTime[0] + "," +
					 * endTime[1]); } else {
					 * weekAndTimeEditor.putString(config_time, ""); } } else {
					 * weekAndTimeEditor.putString(config_time, ""); }
					 */
                        String savaTime = "";
                        if (matcher.find()) {
                            String startTime[] = matcher.group(1).trim().split(":");
                            String endTime[] = matcher.group(2).trim().split(":");
                            savaTime = startTime[0] + "," + startTime[1] + ","
                                    + endTime[0] + "," + endTime[1];
                        }
                        if (matcher2.find()) {
                            String startTime2[] = matcher2.group(1).trim()
                                    .split(":");
                            String endTime2[] = matcher2.group(2).trim().split(":");
                            savaTime += "," + startTime2[0] + "," + startTime2[1]
                                    + "," + endTime2[0] + "," + endTime2[1];
                        }
                        if (savaTime.equals("")) {
                            weekAndTimeEditor.putString(config_time, "");
                        } else {
                            String[] time = savaTime.split(",");
                            // 始终存取四段
                            if (time.length > 4 && time.length == 8) {
                                savaTime = time[0] + "," + time[1] + "," + time[6]
                                        + "," + time[7];
                            }
                            weekAndTimeEditor.putString(config_time, savaTime);
                        }
                        weekAndTimeEditor.commit();
                        updateView();
                    }
                    break;
                default:
                    break;
            }
            // toast 1,3,2,3
            if (sipokcount == 2) {
                CustomToast.show(this, R.string.common_setting_success);
                sipokcount = 0;
                Editor editor = sp.edit();
                editor.putBoolean(device.getDevice_id()
                        + APPConfig.IS_MOVE_DETECTION, false);
                editor.commit();

                updateView();

                dismissBaseDialog();
            }

        } else {
            dismissBaseDialog();
            CustomToast.show(this, R.string.common_setting_fail);
        }

    }
}
