/**
 * Project Name:  iCam
 * File Name:     SafeProtectSettingActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015年9月24日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.protect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.Device;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipProfile;
import com.wulian.siplibrary.model.linkagedetection.DetectionAction;
import com.wulian.siplibrary.model.linkagedetection.LinkageDetectionModel;
import com.wulian.siplibrary.model.linkagedetection.TimePeriod;
import com.wulian.siplibrary.model.linkagedetection.WeekModel;

/**
 * @ClassName: SafeProtectSettingActivity
 * @Function: TODO
 * @Date: 2015年9月24日
 * @author Yanmin
 * @email min.yan@wuliangroup.cn
 */
public class SafeProtectSettingActivity extends BaseFragmentActivity implements
        OnClickListener, OnSeekBarChangeListener {
    private static final String SPLIT = ";";

    private Button btn_start_protection;
    private SharedPreferences sp;
    private LinearLayout ll_protect_area_move, ll_protect_time_move,
            ll_container_move;
    private SeekBar sb_move;
    private TextView tv_move_time_show, tv_move_area_show;
    public static final int REQUESTCODE_MOVE_TIME = 1;
    public static final int REQUESTCODE_MOVE_AREA = 2;
    public static final int REQUESTCODE_COVER_TIME = 3;
    public static final int REQUESTCODE_COVER_AREA = 4;

    private int sipokcount = 0;
    Device device;
    String deviceSipAccount;// 设备sip账号
    String deviceControlUrl;// 设备控制sip地址
    String deviceCallUrl;// 设备呼叫sip地址
    private SipProfile account;
    int seq = 1;

    private String spMoveArea;
    private int spMoveSensitivity;
    private String spMoveWeekday;
    private String spMoveTime;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        onSendSipRemoteAccess();
        initViews();
        initListeners();
        initData();
        if (arg0 == null) {
            initMoveData();
            updateView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("area", spMoveArea);
        outState.putString("weekday", spMoveWeekday);
        outState.putString("time", spMoveTime);
        outState.putInt("sens", spMoveSensitivity);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        spMoveArea = savedInstanceState.getString("area");
        spMoveWeekday = savedInstanceState.getString("weekday");
        spMoveTime = savedInstanceState.getString("time");
        spMoveSensitivity = savedInstanceState.getInt("sens");
        updateView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_safe_protect_setting);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.protect_setting);
    }

    private void initViews() {
        btn_start_protection = ((Button) findViewById(R.id.btn_start_protect));
        ll_container_move = (LinearLayout) findViewById(R.id.ll_container_move);
        ll_protect_area_move = (LinearLayout) findViewById(R.id.ll_protect_area_move);
        ll_protect_time_move = (LinearLayout) findViewById(R.id.ll_protect_time_move);
        sb_move = (SeekBar) findViewById(R.id.sb_move);
        tv_move_time_show = (TextView) findViewById(R.id.tv_move_time_show);
        tv_move_area_show = (TextView) findViewById(R.id.tv_move_area_show);
    }

    private void initListeners() {
        btn_start_protection.setOnClickListener(this);
        ll_protect_area_move.setOnClickListener(this);
        ll_protect_time_move.setOnClickListener(this);
        sb_move.setOnSeekBarChangeListener(this);
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
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        if (id == R.id.sb_move) {
            spMoveSensitivity = seekBar.getProgress();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            this.finish();
        } else if (id == R.id.btn_start_protect) {
            String weekdayMove[] = spMoveWeekday.split(",");
            String timeMove[] = spMoveTime.split(",");
            // 时间跨夜，进行分割
            if ((Integer.parseInt(timeMove[0]) > Integer.parseInt(timeMove[2]))
                    || ((Integer.parseInt(timeMove[0]) == Integer
                    .parseInt(timeMove[2])) && Integer
                    .parseInt(timeMove[1]) > Integer
                    .parseInt(timeMove[3]))) {
                timeMove = new String[]{timeMove[0], timeMove[1], "23", "59",
                        "0", "0", timeMove[2], timeMove[3]};
            }
            String areaMove[] = spMoveArea.split(";");
            // 时间、星期，区域
            if (timeMove.length <= 0) {
                CustomToast.show(this,
                        R.string.protect_set_move_protection_time);
                return;

            }
            if (weekdayMove.length <= 0) {
                CustomToast.show(this,
                        R.string.protect_set_move_protection_weekday);
                return;
            }
            if (areaMove.length <= 0) {
                CustomToast.show(this,
                        R.string.protect_set_move_protection_area);
                return;
            }
            showBaseDialog();
            // 运动侦测 多个区域
            SipController.getInstance().sendMessage(
                    deviceControlUrl,
                    SipHandler.ConfigMovementDetection(deviceControlUrl, seq++,
                            true, sb_move.getProgress(), areaMove), account);
            // 运动侦测联动
            LinkageDetectionModel moveDetections = new LinkageDetectionModel(
                    true);// cb_move.isChecked()
            for (int i = 0; i < weekdayMove.length; i++) {
                DetectionAction da = getDetectionAction(weekdayMove[i]);// 某天
                if (da == null) {
                    break;
                }
                if (timeMove.length == 4) {
                    TimePeriod tp = new TimePeriod();
                    tp.setId(i);
                    Utils.formatSingleNum(timeMove);// 补零
                    tp.setStartTime(timeMove[0] + ":" + timeMove[1]);
                    tp.setEndTime(timeMove[2] + ":" + timeMove[3]);
                    da.addLinkageDetection(tp);// 某天的时间段，目前只有一个
                } else if (timeMove.length == 8) {
                    // TODO 可以增加第二个时间段
                    TimePeriod tp2 = new TimePeriod();
                    tp2.setId(i);
                    Utils.formatSingleNum(timeMove);// 补零
                    tp2.setStartTime(timeMove[0] + ":" + timeMove[1]);
                    tp2.setEndTime(timeMove[2] + ":" + timeMove[3]);
                    TimePeriod tp3 = new TimePeriod();
                    tp3.setId(i + weekdayMove.length);// 唯一标识，不能相同
                    tp3.setStartTime(timeMove[4] + ":" + timeMove[5]);
                    tp3.setEndTime(timeMove[6] + ":" + timeMove[7]);
                    da.addLinkageDetection(tp2);
                    da.addLinkageDetection(tp3);
                }

                moveDetections.addDetectionAction(i, da);
            }
            SipController.getInstance().sendMessage(
                    deviceControlUrl,
                    SipHandler.ConfigLinkageArming(deviceControlUrl, seq++, 1,
                            1, moveDetections), account);
            ICamGlobal.isNeedRefreshDeviceList = true;
        } else if (id == R.id.ll_protect_area_move) {
            startActivityForResult(
                    (new Intent(this, DetectionAreaActivity.class))
                            .putExtra("type", REQUESTCODE_MOVE_AREA)
                            .putExtra("area", spMoveArea)
                            .putExtra("device", device), REQUESTCODE_MOVE_AREA);
        } else if (id == R.id.ll_protect_time_move) {
            startActivityForResult(
                    (new Intent(this, SafeDurationActivity.class)).putExtra(
                            "time", spMoveTime).putExtra("weekday",
                            spMoveWeekday), REQUESTCODE_MOVE_TIME);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUESTCODE_MOVE_TIME:
                if (resultCode == RESULT_OK) {
                    spMoveWeekday = data.getStringExtra("weekday");
                    spMoveTime = data.getStringExtra("time");
                    updateView();
                }
                break;
            case REQUESTCODE_MOVE_AREA:
                if (resultCode == RESULT_OK) {
                    spMoveArea = data.getStringExtra("area");
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
                // case QUERY_MOVEMENT_DETECTION_INFO:
                // Utils.sysoInfo("移动布防:" + xmlData);
                // String[] results1 = Utils.getMotionArea(xmlData,
                // APPConfig.MAX_MONITOR_AREA);
                // StringBuilder sb = new StringBuilder();
                // for (String s : results1) {
                // Utils.sysoInfo("还原监测区:" + s);
                // sb.append(s).append(";");
                // }
                // boolean isMoveEnable = "true".equals(Utils.getParamFromXml(
                // xmlData, "enable")) ? true : false;
                // int sen = 0;
                // try {
                // sen = Integer.parseInt(Utils.getParamFromXml(xmlData,
                // "sensitivity"));
                // } catch (NumberFormatException e) {
                // sen = 50;
                // }
                // Editor editor = sp.edit();
                // editor.putInt(device.getDevice_id()
                // + APPConfig.MOVE_SENSITIVITY, sen);// 灵敏度
                // editor.putBoolean(device.getDevice_id()
                // + APPConfig.IS_MOVE_DETECTION, isMoveEnable);// 启用
                // editor.putString(device.getDevice_id() + APPConfig.MOVE_AREA,
                // sb.toString().equals("") ? ";" : sb.toString());// 区域
                // editor.commit();
                // updateMoveAndBlock();
                // break;

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
                // updateMoveAndBlock();
                // break;

                // case QUERY_LINKAGE_ARMING_INFO:
                // Utils.sysoInfo("联动信息:" + xmlData);
                // int eventType = 0;
                // try {
                // eventType = Integer.parseInt(Utils.getParamFromXml(xmlData,
                // "eventType"));
                // } catch (NumberFormatException e) {
                // e.printStackTrace();
                // }
                // if (eventType == 1 || eventType == 2) {// 1 移动布防 2 遮挡布防
                // String config_weekday, config_time;
                // if (eventType == 1) {
                // config_weekday = device.getDevice_id()
                // + APPConfig.MOVE_WEEKDAY;
                // config_time = device.getDevice_id()
                // + APPConfig.MOVE_TIME;
                // } else {
                // config_weekday = device.getDevice_id()
                // + APPConfig.COVER_WEEKDAY;
                // config_time = device.getDevice_id()
                // + APPConfig.COVER_TIME;
                // }
                //
                // String weekTime = getWeekFromXmlData(xmlData);
                // Editor weekAndTimeEditor = sp.edit();
                // if (!TextUtils.isEmpty(weekTime)) {
                // weekAndTimeEditor.putString(config_weekday, weekTime);
                // }
                // // <time start="00:48" end="21:00">0</time>
                // Pattern p = Pattern
                // .compile("<time start=\"(.+)\" end=\"(.+)\">0</time>");
                // Matcher matcher = p.matcher(xmlData);
                // if (matcher.find()) {
                // String startTime[] = matcher.group(1).trim().split(":");
                // String endTime[] = matcher.group(2).trim().split(":");
                // if (startTime.length == 2 && endTime.length == 2) {
                // weekAndTimeEditor.putString(config_time,
                // startTime[0] + "," + startTime[1] + ","
                // + endTime[0] + "," + endTime[1]);
                // }
                // }
                // weekAndTimeEditor.commit();
                // updateWeekdayAndTime();
                // }
                // break;
                default:
                    break;
            }
            if (sipokcount == 2) {
                commitSP();
                CustomToast.show(this, R.string.common_setting_success);
                sipokcount = 0;
                dismissBaseDialog();
                setResult(RESULT_OK);
                finish();
            }

        } else {
            dismissBaseDialog();
            CustomToast.show(this, R.string.common_setting_fail);
        }
    }

    private void commitSP() {
        Editor editor = sp.edit();
        editor.putInt(device.getDevice_id() + APPConfig.MOVE_SENSITIVITY,
                sb_move.getProgress());// 灵敏度
        editor.putBoolean(device.getDevice_id() + APPConfig.IS_MOVE_DETECTION,
                true);// 启用
        editor.putString(device.getDevice_id() + APPConfig.MOVE_AREA,
                spMoveArea);// 区域
        editor.putString(device.getDevice_id() + APPConfig.MOVE_WEEKDAY,
                spMoveWeekday);
        editor.putString(device.getDevice_id() + APPConfig.MOVE_TIME,
                spMoveTime);
        editor.commit();
    }

    private DetectionAction getDetectionAction(String num) {
        if (TextUtils.isEmpty(num)) {
            return null;
        }
        switch (Integer.parseInt(num)) {
            case 1:
                return new DetectionAction(WeekModel.MONDAY);
            case 2:
                return new DetectionAction(WeekModel.TUESDAY);
            case 3:
                return new DetectionAction(WeekModel.WEDNESDAY);
            case 4:
                return new DetectionAction(WeekModel.THURSDAY);
            case 5:
                return new DetectionAction(WeekModel.FRIDAY);
            case 6:
                return new DetectionAction(WeekModel.SATURDAY);
            case 7:
                return new DetectionAction(WeekModel.SUNDAY);
        }
        return null;
    }

    private void initMoveData() {
        spMoveArea = sp.getString(device.getDevice_id() + APPConfig.MOVE_AREA,
                ";");
        spMoveSensitivity = sp.getInt(device.getDevice_id()
                + APPConfig.MOVE_SENSITIVITY, 50);
        spMoveWeekday = sp.getString(device.getDevice_id()
                + APPConfig.MOVE_WEEKDAY, "");
        spMoveTime = sp.getString(device.getDevice_id() + APPConfig.MOVE_TIME,
                ",");
        // TODO 过滤，如果跨夜，则截取,维持成四段
        String[] time = spMoveTime.split(",");
        if (time.length > 4 && time.length == 8) {
            spMoveTime = time[0] + "," + time[1] + "," + time[6] + ","
                    + time[7];
        }
        if (TextUtils.isEmpty(spMoveWeekday))
            spMoveWeekday = SafeDurationActivity.DAY_EVERY;

        if (TextUtils.isEmpty(spMoveTime))
            spMoveTime = SafeDurationActivity.TIME_ALL_DAY;
    }

    private void updateView() {
        int length = spMoveArea.split(";").length;
        if (length <= 0) {
            tv_move_area_show.setText(getResources().getString(
                    R.string.protect_not_set));
        } else {
            tv_move_area_show.setText(length
                    + getResources().getString(R.string.protect_areas));
        }
        sb_move.setProgress(spMoveSensitivity);
        String weekday_move = convertWeekday(spMoveWeekday);
        String time_move = convertTime(spMoveTime);
        tv_move_time_show.setText(weekday_move + " " + time_move);
    }

    private String convertWeekday(String numbers) {
        // 7,1,2,3,4,5,6, ->每天 everyday
        // 1,2,3,4,5, ->工作日 workday
        // 2,4, -> 周二,周四 Tue,Thus

        if (SafeDurationActivity.DAY_EVERY.equals(numbers)) {
            return getResources().getString(R.string.common_everyday);
        }
        if (SafeDurationActivity.DAY_WORKDAY.equals(numbers)) {

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

}
