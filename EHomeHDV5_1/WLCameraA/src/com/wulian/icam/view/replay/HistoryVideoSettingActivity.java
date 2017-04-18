/**
 * Project Name:  iCam
 * File Name:     HistoryVideoSettingActivity.java
 * Package Name:  com.wulian.icam.view.replay
 *
 * @Date: 2015年5月27日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.replay;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.Device;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.protect.SafeDurationActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.model.linkagedetection.DetectionAction;
import com.wulian.siplibrary.model.linkagedetection.TimePeriod;
import com.wulian.siplibrary.model.linkagedetection.WeekModel;
import com.wulian.siplibrary.model.prerecordplan.PreRecordPlanModel;
import com.wulian.siplibrary.utils.WulianLog;

/**
 * @author Wangjj
 * @ClassName: HistoryVideoSettingActivity
 * @Function: 历史回放设置
 * @Date: 2015年5月27日
 * @email wangjj@wuliangroup.cn
 */
public class HistoryVideoSettingActivity extends BaseFragmentActivity implements
        OnClickListener {
    private TextView tv_save_time, tv_save_time_day;
    private LinearLayout ll_save_time, ll_save_time_day,
            ll_recoding_only_change, ll_sd_override, ll_custom_time,
            layout_custom_time;
    private CheckBox cb_record_only_change, cb_sd_override, cb_sd_savetime;
    private Button btn_disk_format;
    private Device device;
    private ImageView titlebar_right;
    SharedPreferences sp;
    AlertDialog dialog;
    View dialogContentView;
    private Dialog mTimePeriodDialog, mTimePeriodDayDialog;
    private String moveTime, moveDayTime;
    String sipCallWithDomain;// xxx@wuliangruop.cn
    int seq = 1;
    private List<String> dataSource;

    protected static final int STOP = 0x10000;
    protected static final int NEXT = 0x10001;
    private int iCount = 0;

    private ProgressBar pb_format;
    private TextView tv_info;
    private TextView tv_title;
    private TextView tv_sd_storage_free;
    private TextView tv_sd_storage_total;
    private LinearLayout layout_format_button;

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    SipController.getInstance().sendMessage(
                            sipCallWithDomain,
                            SipHandler.QueryStorageStatus("sip:"
                                    + sipCallWithDomain, seq++),
                            app.registerAccount());
                    break;
                case STOP:
                    dialog.dismiss();
                    btn_disk_format.setEnabled(false);
                    CustomToast.show(HistoryVideoSettingActivity.this, R.string.replay_disk_format_ok);
                    break;
                case NEXT:
                    if (!Thread.currentThread().isInterrupted()) {
                        pb_format.setProgress(iCount);
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
        onSendSipRemoteAccess();
        initViews();
        initListeners();
        initData();
        updateWeekdayAndTime();
        initWebData();
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_history_video_setting);
    }

    private void initViews() {
        tv_save_time = (TextView) findViewById(R.id.tv_save_time);
        tv_save_time_day = (TextView) findViewById(R.id.tv_save_time_day);
        cb_record_only_change = (CheckBox) findViewById(R.id.cb_record_only_change);
        cb_sd_override = (CheckBox) findViewById(R.id.cb_sd_override);
        btn_disk_format = (Button) findViewById(R.id.btn_disk_format);
        titlebar_right = (ImageView) findViewById(R.id.titlebar_right);
        titlebar_right.setVisibility(View.INVISIBLE);
        ll_save_time = (LinearLayout) findViewById(R.id.ll_save_time);
        ll_save_time_day = (LinearLayout) findViewById(R.id.ll_save_time_day);
        ll_recoding_only_change = (LinearLayout) findViewById(R.id.ll_recoding_only_change);
        ll_sd_override = (LinearLayout) findViewById(R.id.ll_sd_override);
        layout_custom_time = (LinearLayout) findViewById(R.id.layout_custom_time);
        ll_custom_time = (LinearLayout) findViewById(R.id.ll_custom_time);
        cb_sd_savetime = (CheckBox) findViewById(R.id.cb_sd_savetime);
        tv_sd_storage_free = (TextView) findViewById(R.id.tv_sd_storage_free);
        tv_sd_storage_total = (TextView) findViewById(R.id.tv_sd_storage_total);
    }

    private void initListeners() {
        // tv_save_time.setOnClickListener(this);
        btn_disk_format.setOnClickListener(this);
        ll_save_time.setOnClickListener(this);
        ll_save_time_day.setOnClickListener(this);
        ll_recoding_only_change.setOnClickListener(this);
        ll_custom_time.setOnClickListener(this);
        ll_sd_override.setOnClickListener(this);
    }

    private void initData() {
        sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
        device = (Device) getIntent().getSerializableExtra("device");
        sipCallWithDomain = device.getDevice_id() + "@"
                + device.getSip_domain();
        int[] sourceId = new int[]{R.string.common_sun, R.string.common_mon,
                R.string.common_tue, R.string.common_wed,
                R.string.common_thurs, R.string.common_fri, R.string.common_sat};
        dataSource = new ArrayList<String>();
        for (int i = 0; i < sourceId.length; i++) {
            dataSource.add(this.getResources().getString(sourceId[i]));
        }
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.setting_device_restore);
    }

    @Override
    protected OnClickListener getRightClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 125 设置录像计划
                // String weekdayNumsMove[] = sp.getString(
                // device.getDevice_id() + APPConfig.HISTORY_SAVE_WEEKDAY,
                // ",").split(",");
                String weekdayNumsMove[] = moveDayTime.split(",");
                PreRecordPlanModel recordPlanModel = new PreRecordPlanModel();

                for (int i = 0; i < weekdayNumsMove.length; i++) {

                    DetectionAction da = getDetectionAction(weekdayNumsMove[i]);// 某天
                    if (da == null) {
                        break;
                    }
                    String times[] = moveTime.split(",");
                    TimePeriod tp = new TimePeriod();
                    tp.setId(0);
                    if (times.length == 4) {
                        Utils.formatSingleNum(times);
                        tp.setStartTime(times[0] + ":" + times[1]);
                        tp.setEndTime(times[2] + ":" + times[3]);
                    }
                    da.addLinkageDetection(tp);// 某天的时间段，目前只有一个

                    recordPlanModel.addDetectionAction(i, da);
                }
                showBaseDialog();
                SipController.getInstance().sendMessage(
                        sipCallWithDomain,
                        SipHandler.ConfigPrerecordPlan("sip:"
                                        + sipCallWithDomain, seq++, true, 5, 5,
                                recordPlanModel), app.registerAccount());
            }
        };
    }

    private void sendMessageToSip() {
        String weekdayNumsMove[] = moveDayTime.split(",");
        PreRecordPlanModel recordPlanModel = new PreRecordPlanModel();
        // 时间跨夜，进行分割
        String times[] = moveTime.split(",");
        WulianLog.e("times", times[0] + " " + times[1] + " " + times[2] + " "
                + times[3]);
        if ((Integer.parseInt(times[0]) > Integer.parseInt(times[2]))
                || ((Integer.parseInt(times[0]) == Integer.parseInt(times[2])) && Integer
                .parseInt(times[1]) > Integer.parseInt(times[3]))) {
            times = new String[]{times[0], times[1], "23", "59", "0", "0",
                    times[2], times[3]};
        }
        for (int i = 0; i < weekdayNumsMove.length; i++) {

            DetectionAction da = getDetectionAction(weekdayNumsMove[i]);// 某天
            if (da == null) {
                break;
            }
            if (times.length == 4) {
                TimePeriod tp = new TimePeriod();
                tp.setId(0);
                Utils.formatSingleNum(times);// 补零
                tp.setStartTime(times[0] + ":" + times[1]);
                tp.setEndTime(times[2] + ":" + times[3]);
                da.addLinkageDetection(tp);// 某天的时间段，目前只有一个
            } else if (times.length == 8) {
                // TODO 可以增加第二个时间段
                TimePeriod tp2 = new TimePeriod();
                tp2.setId(2);
                Utils.formatSingleNum(times);// 补零
                tp2.setStartTime(times[0] + ":" + times[1]);
                tp2.setEndTime(times[2] + ":" + times[3]);
                TimePeriod tp3 = new TimePeriod();
                tp3.setId(i + times.length);// 唯一标识，不能相同
                tp3.setStartTime(times[4] + ":" + times[5]);
                tp3.setEndTime(times[6] + ":" + times[7]);
                da.addLinkageDetection(tp2);
                da.addLinkageDetection(tp3);
            }
            recordPlanModel.addDetectionAction(i, da);
        }
        showBaseDialog();
        SipController.getInstance().sendMessage(
                sipCallWithDomain,
                SipHandler.ConfigPrerecordPlan("sip:" + sipCallWithDomain,
                        seq++, true, 5, 5, recordPlanModel),
                app.registerAccount());
    }

    private void initWebData() {
        // 122 查询存储状态
        SipController.getInstance().sendMessage(
                sipCallWithDomain,
                SipHandler
                        .QueryStorageStatus("sip:" + sipCallWithDomain, seq++),
                app.registerAccount());
        // 126 查询录像计划覆盖本地
        SipController.getInstance().sendMessage(
                sipCallWithDomain,
                SipHandler
                        .QueryPrerecordPlan("sip:" + sipCallWithDomain, seq++),
                app.registerAccount());
    }

    public DetectionAction getDetectionAction(String num) {
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

    public void updateWeekdayAndTime() {
        String tempWeekday = sp.getString(device.getDevice_id()
                + APPConfig.HISTORY_SAVE_WEEKDAY, "");
        String weekday = convertWeekday(tempWeekday);
        String tempTime = sp.getString(device.getDevice_id()
                + APPConfig.HISTORY_SAVE_TIME, ",");
        String time = convertTime(tempTime);
        tv_save_time.setText(time);
        moveTime = tempTime;
        tv_save_time_day.setText(weekday);
        moveDayTime = tempWeekday;
    }

    /**
     * @param numbers 7,1,2,3,4,5,6,
     * @return
     * @Function 转换星期
     * @author Wangjj
     * @date 2015年2月5日
     */

    public String convertWeekday(String numbers) {
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
            return sb.toString().substring(0,
                    sb.length() - 1 < 0 ? 0 : sb.length() - 1)
            /* .replace("周", "") */;
        }
    }

    public String convertTime(String times) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            updateWeekdayAndTime();
        }
    }

    @Override
    protected void SipDataReturn(boolean isSuccess, SipMsgApiType apiType,
                                 String xmlData, String from, String to) {
        super.SipDataReturn(isSuccess, apiType, xmlData, from, to);
        if (isSuccess) {
            dismissBaseDialog();
            switch (apiType) {
                case CONFIG_LOCAL_STORAGE_DEVICE_FORMAT:// 123 sd格式化
                    Utils.sysoInfo("格式化:" + xmlData);
                    if (Utils.getParamFromXml(xmlData, "status").equalsIgnoreCase(
                            "ok")) {
                        CustomToast.show(this, R.string.replay_disk_format_success);
                        // myHandler.sendEmptyMessageDelayed(0, 2000);// 122
                        // 两秒后重新查询存储状态
                    } else {
                        CustomToast.show(this, R.string.replay_disk_format_fail);
                    }

                    break;
                case CONFIG_PRERECORD_PLAN:// 125 视频计划
                    Utils.sysoInfo("视频计划:" + xmlData);
                    if (Utils.getParamFromXml(xmlData, "status").equalsIgnoreCase(
                            "ok")) {
                        CustomToast.show(this, R.string.replay_save_time_success);
                    } else {
                        CustomToast.show(this, R.string.replay_disk_format_fail);
                    }
                    break;

                case QUERY_STORAGE_STATUS:// 122 查询存储状态
                    Utils.sysoInfo("查询存储状态:" + xmlData);
                    // <storage num="1" type="SD" status="1", attr="rw"
                    // totalsize="1000K" freesize="0K"></storage>
                    Pattern pstatus = Pattern
                            .compile("<storage.*status=\"(\\d)\"\\s+.*/?>(</storage>)?");
                    Matcher matchers = pstatus.matcher(xmlData);
                    if (matchers.find()) {
                        String status = matchers.group(1).trim();
                        if ("0".equals(status)) {
                            CustomToast.show(this, R.string.replay_disk_no_format);
                            btn_disk_format.setEnabled(true);
                        } else if ("1".equals(status)) {
                            btn_disk_format.setEnabled(false);
                            CustomToast.show(this, R.string.replay_disk_no_found);
                        } else if ("2".equals(status)) {
                            btn_disk_format.setEnabled(true);
                            Pattern p = Pattern
                                    .compile("<storage.*totalsize=\"(\\d+)\"\\s+.*freesize=\"(\\d+)\".*/?>(</storage>)?");
                            Matcher matcher = p.matcher(xmlData);
                            if (matcher.find()) {
                                try {
                                    String totalSize = matcher.group(1).trim();
                                    String freeSize = matcher.group(2).trim();
                                    // android 自带的文件大小转换方法是？

                                    long totalSizeNum = Long.parseLong(totalSize) * 1024;
                                    long freeSizeNum = Long.parseLong(freeSize) * 1024;
                                    tv_sd_storage_free.setText(Utils.convertFileSize(freeSizeNum));
                                    tv_sd_storage_total.setText(Utils.convertFileSize(totalSizeNum));
                                    if (totalSizeNum == freeSizeNum) {
                                        btn_disk_format.setEnabled(false);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    break;
                case QUERY_PRERECORD_PLAN:// 126 查询录像计划
                    Utils.sysoInfo("录像计划信息:" + xmlData);
                    String config_weekday = device.getDevice_id()
                            + APPConfig.HISTORY_SAVE_WEEKDAY;
                    String config_time = device.getDevice_id()
                            + APPConfig.HISTORY_SAVE_TIME;
                    String weekTime = getWeekFromXmlData(xmlData);
                    Editor weekAndTimeEditor = sp.edit();
                    weekAndTimeEditor.putString(config_weekday, weekTime);
                    // <time start="00:48" end="21:00">0</time> 只有一个
                    Pattern p1 = Pattern
                            .compile("<time start=\"(.+)\" end=\"(.+)\">0</time>");
                    Pattern p2 = Pattern
                            .compile("<time start=\"(.+)\" end=\"(.+)\">1</time>");
                    Matcher matcher1 = p1.matcher(xmlData);
                    Matcher matcher2 = p2.matcher(xmlData);
                    String savaTime = "";
                    if (matcher1.find()) {
                        String startTime[] = matcher1.group(1).trim().split(":");
                        String endTime[] = matcher1.group(2).trim().split(":");
                        savaTime = startTime[0] + "," + startTime[1] + ","
                                + endTime[0] + "," + endTime[1];
                    }
                    if (matcher2.find()) {
                        String startTime2[] = matcher2.group(1).trim().split(":");
                        String endTime2[] = matcher2.group(2).trim().split(":");
                        savaTime += "," + startTime2[0] + "," + startTime2[1] + ","
                                + endTime2[0] + "," + endTime2[1];
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
                /*
				 * if (matcher1.find()) { String startTime[] =
				 * matcher1.group(1).trim().split(":"); String endTime[] =
				 * matcher1.group(2).trim().split(":"); if (startTime.length ==
				 * 2 && endTime.length == 2) {
				 * weekAndTimeEditor.putString(config_time, startTime[0] + "," +
				 * startTime[1] + "," + endTime[0] + "," + endTime[1]); } }
				 */
                    weekAndTimeEditor.commit();
                    updateWeekdayAndTime();
                    break;
                default:
                    break;
            }
        }
    }

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_save_time) {
            // startActivityForResult(
            // new Intent(this, SaveTimeActivity.class).putExtra("device",
            // device), 0);
            showDurationSeletor();
        } else if (id == R.id.ll_save_time_day) {
            showDurationDaySeletor();
        } else if (id == R.id.btn_disk_format) {
            if (dialog == null) {
                dialog = new AlertDialog.Builder(this,
                        R.style.alertDialogIosAlert).create();
            }
            if (dialogContentView == null) {
                dialogContentView = LinearLayout.inflate(this,
                        R.layout.custom_alertdialog_format, // 默认就是注销警告
                        (ViewGroup) this
                                .findViewById(R.id.ll_custom_alertdialog));
                tv_title = ((TextView) dialogContentView
                        .findViewById(R.id.tv_title));
                tv_title.setText("格式化存储卡");
                tv_info = ((TextView) dialogContentView
                        .findViewById(R.id.tv_info));
                tv_info.setText(R.string.replay_disk_format_warn);

                pb_format = ((ProgressBar) dialogContentView
                        .findViewById(R.id.pb_format));
                layout_format_button = ((LinearLayout) dialogContentView
                        .findViewById(R.id.layout_format_button));

                ((Button) dialogContentView.findViewById(R.id.btn_positive))
                        .setText("继续格式化");

                ((Button) dialogContentView.findViewById(R.id.btn_positive))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                pb_format.setVisibility(View.VISIBLE);
                                tv_title.setText("正在格式化");
                                tv_info.setText("格式化过程需要一定时间，您可返回其他界面正常使用功能。");
                                layout_format_button.setVisibility(View.GONE);
                                Thread mthread = new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        for (int i = 0; i < 50; i++) {
                                            try {
                                                iCount = (i + 1) * 2;
                                                Thread.sleep(1000);
                                                if (i == 49) {
                                                    Message msg = new Message();
                                                    msg.what = STOP;
                                                    myHandler.sendMessage(msg);
                                                    btn_disk_format
                                                            .setEnabled(false);
                                                    break;
                                                } else if (i == 45) {
                                                    myHandler
                                                            .sendEmptyMessageDelayed(
                                                                    0, 500);
                                                } else {
                                                    Message msg = new Message();
                                                    msg.what = NEXT;
                                                    myHandler.sendMessage(msg);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                mthread.start();
                                // 123 格式化
                                showBaseDialog();
                                SipController
                                        .getInstance()
                                        .sendMessage(
                                                sipCallWithDomain,
                                                SipHandler
                                                        .ConfigLocalStorageDeviceFormat(
                                                                "sip:"
                                                                        + sipCallWithDomain,
                                                                seq++, 1),
                                                app.registerAccount());

                            }
                        });
                ((Button) dialogContentView.findViewById(R.id.btn_negative))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
            }
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            dialog.setContentView(dialogContentView);
        } else if (id == R.id.ll_sd_override) {
            if (cb_sd_override.isChecked()) {
                cb_sd_override.setChecked(false);
            } else {
                cb_sd_override.setChecked(true);
            }
            // TODO 调用相应接口
        } else if (id == R.id.ll_recoding_only_change) {

            if (cb_record_only_change.isChecked()) {
                cb_record_only_change.setChecked(false);
            } else {
                cb_record_only_change.setChecked(true);
            }

        } else if (id == R.id.ll_custom_time) {

            // if (cb_sd_savetime.isChecked()) {
            // cb_sd_savetime.setChecked(false);
            // layout_custom_time.setVisibility(View.GONE);
            // } else {
            // cb_sd_savetime.setChecked(true);
            // layout_custom_time.setVisibility(View.VISIBLE);
            // }

        }
    }

    private void showDurationSeletor() {
        Resources rs = getResources();
        mTimePeriodDialog = DialogUtils.showCommonTimePeriodDialog(this, true,
                rs.getString(R.string.protect_user_defined), null, null,
                moveTime, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.btn_positive) {
                            moveTime = (String) v.getTag();
                            String time[] = moveTime.split(",");
                            if (time.length == 4) {
                                // TODO 暂时不支持跨夜设置，此处加个判断
                                if (Integer.parseInt(time[0]) > Integer
                                        .parseInt(time[2])
                                        || (Integer.parseInt(time[0]) == Integer
                                        .parseInt(time[2]) && Integer
                                        .parseInt(time[1]) >= Integer
                                        .parseInt(time[3]))) {
                                    CustomToast.show(
                                            HistoryVideoSettingActivity.this,
                                            R.string.protect_time_invalid);
                                    return;
                                }
                            }
                            mTimePeriodDialog.dismiss();
                            if (time.length == 4) {
                                Utils.formatSingleNum(time);
                                tv_save_time.setText(time[0] + ":" + time[1]
                                        + "  --  " + time[2] + ":" + time[3]);
                            }
                            sendMessageToSip();
                        } else if (id == R.id.btn_negative) {
                            mTimePeriodDialog.dismiss();
                        }
                    }
                });
    }

    private void showDurationDaySeletor() {
        final Resources rs = getResources();
        mTimePeriodDayDialog = DialogUtils.showCommonTimeDayPeriodDialog(this,
                true, rs.getString(R.string.common_repeat), null, null,
                moveDayTime, dataSource, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.btn_positive) {
                            moveDayTime = (String) v.getTag();
                            if (TextUtils.isEmpty(moveDayTime)) {
                                CustomToast.show(
                                        HistoryVideoSettingActivity.this,
                                        R.string.protect_time_day_invalid);
                                return;
                            }
                            mTimePeriodDayDialog.dismiss();
                            tv_save_time_day
                                    .setText(convertWeekday(moveDayTime));
                            sendMessageToSip();
                        } else if (id == R.id.btn_negative) {
                            mTimePeriodDayDialog.dismiss();
                        }
                    }
                });
    }
}
