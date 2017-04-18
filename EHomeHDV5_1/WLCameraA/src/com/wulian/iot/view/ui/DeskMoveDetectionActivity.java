package com.wulian.iot.view.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.Packet;
import com.wulian.icam.R;
import com.wulian.icam.utils.StringUtil;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.protect.SafeDurationActivity;
import com.wulian.iot.Config;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.view.device.setting.*;
import com.yuantuo.customview.ui.WLToast;

import static com.tutk.IOTC.AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP;

/**
 * Function：桌面摄像机活动检测设置页
 * Created by hxc on 2016/11/18.
 */

public class DeskMoveDetectionActivity extends SimpleFragmentActivity implements View.OnClickListener, Handler.Callback, CameraHelper.Observer {
    public static final int REQUESTCODE_PROTECT_TIME = 1;
    public static final int REQUESTCODE_MOVE_AREA = 2;
    private LinearLayout ll_protect_time_move;
    private LinearLayout ll_protect_area_move;
    private TextView tv_move_time_show;
    private TextView tv_show_sensitivity;
    private TextView isSet;
    private LinearLayout ll_sensitivity_move;
    private ImageView titlebar_back;
    private final int SENSETIVITY_TYPE = 1;
    private IOTCameraBean cInfo = null;
    private String spMoveArea;
    private String[] fences = null;
    private int switching;//活动检测开关 0-close,1-open


    @Override
    public void root() {
        super.root();
        setContentView(R.layout.activity_move_detection_setting);
        initViews();
        setListener();
        initData();
    }

    @Override
    public void initData() {
        super.initData();
        if ((cInfo = (IOTCameraBean) getIntent().getSerializableExtra(Config.deskBean)) == null) {
            return;
        }
        if (cameaHelper != null) {
            cameaHelper.attach(this);
        }
        switching = getIntent().getIntExtra("switching", -1);
        fences = getIntent().getStringArrayExtra("fences");
        tv_move_time_show.setText(convertWeekday(fences[1]) + " " + convertTime(fences[2]));
        upMoveAreaButtonUi();
    }

    private void upMoveAreaButtonUi() {
        if (fences.length > 0) {
            if ((spMoveArea = fences[3]).equals("")) {
                spMoveArea = ";";
            }
            if (spMoveArea.split(";").length <= 0) {
                isSet.setText(getResources().getString(R.string.protect_not_set));
            } else {
                isSet.setText(spMoveArea.split(";").length + getResources().getString(R.string.protect_areas));
            }
            return;
        }
    }


    private void initViews() {
        ((TextView) findViewById(R.id.titlebar_title)).setText(getResources().getString(R.string.dt_activity_check_set));
        ll_protect_time_move = (LinearLayout) findViewById(R.id.ll_protect_time_move);
        ll_protect_area_move = (LinearLayout) findViewById(R.id.ll_protect_area_move);
        tv_move_time_show = (TextView) findViewById(R.id.tv_move_time_show);
        tv_show_sensitivity = (TextView) findViewById(R.id.tv_show_sensitivity);
        ll_sensitivity_move = (LinearLayout) findViewById(R.id.ll_sensitivity_move);
        titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
        isSet = (TextView) findViewById(R.id.tv_move_area_show);
    }

    private void setListener() {
        titlebar_back.setOnClickListener(this);
        ll_protect_time_move.setOnClickListener(this);
        ll_protect_area_move.setOnClickListener(this);
        tv_move_time_show.setOnClickListener(this);
        tv_show_sensitivity.setOnClickListener(this);
        ll_sensitivity_move.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_protect_time_move) {//设置防护时间
            startActivityForResult(new Intent(this, DeskProtectPeriodActivity.class).putExtra("fences", fences), REQUESTCODE_PROTECT_TIME);
        } else if (id == R.id.titlebar_back) {
            this.finish();
        } else if (id == R.id.ll_sensitivity_move) {//设置灵敏度
            startActivityForResult(new Intent(this, DeskSensitivityActivity.class).putExtra("type", tv_show_sensitivity.getText()), SENSETIVITY_TYPE);
        } else if (id == R.id.ll_protect_area_move) {//设置防护区域
            startActivityForResult((new Intent(DeskMoveDetectionActivity.this, com.wulian.iot.view.device.setting.ProtectAreaActivity.class)).putExtra("type", REQUESTCODE_MOVE_AREA).putExtra("area", spMoveArea).putExtra("gwid", cInfo.getGwId()), REQUESTCODE_MOVE_AREA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_MOVE_AREA:
                    spMoveArea = data.getStringExtra("area");
                    Log.i(TAG, "spMoveArea(" + spMoveArea + ")");
                    if (spMoveArea != null && !spMoveArea.equals("")) {
                        isSet.setText(spMoveArea.split(";").length + getResources().getString(R.string.protect_areas));
                    }
                    break;
                //// TODO: 2016/11/30 以下为防护时间，这个版本暂时屏蔽，下个版本发
//            case REQUESTCODE_PROTECT_TIME:
//
//                fences[1] = data.getStringExtra("weekday");
//                fences[2] = data.getStringExtra("time");
//                if (!StringUtil.isNullOrEmpty(fences[1]) && !StringUtil.isNullOrEmpty(fences[2])) {
//                    String weekday_move = convertWeekday(fences[1]);
//                    String time_move = convertTime(fences[2]);
//                    tv_move_time_show.setText(weekday_move + " " + time_move);
//                }
//                break;
                default:
                    break;
            }
        }
    }

    //查询设置的灵敏度并显示
    private void showSensitivityType() {
        String sensitivity = sharedPreferences.getString(Config.DESK_CAMERA_SENSITIVITY_SP, "-1");
        if (!sensitivity.equals("-1")) {
            tv_show_sensitivity.setText(sensitivity);
        }
    }

    //转化为灵敏度
    private void upDataIotSensitivity() {
        String sensitivity = sharedPreferences.getString("DESK_CAMERA_SENSITIVITY_SP", "-1");
        if (sensitivity.equals(getResources().getString(R.string.dt_super_lower))) {
            fences[0] = 0 + "";
        } else if (sensitivity.equals(getResources().getString(R.string.cateye_sensitivity_setting_low))) {
            fences[0] = "1";
        } else if (sensitivity.equals(getResources().getString(R.string.cateye_sensitivity_setting_mid))) {
            fences[0] = "2";
        } else if (sensitivity.equals(getResources().getString(R.string.cateye_sensitivity_setting_high))) {
            fences[0] = "3";
        } else if (sensitivity.equals(getResources().getString(R.string.dt_super_higher))) {
            fences[0] = "4";
        }
    }

    //转化为星期
    private String convertWeekday(String numbers) {
        // 7,1,2,3,4,5,6, ->每天 everyday
        // 1,2,3,4,5, ->工作日 workday
        // 2,4, -> 周二,周四 Tue,Thus

        if (DeskProtectPeriodActivity.DAY_EVERY.equals(numbers)) {
            return getResources().getString(R.string.common_everyday);
        }
        if (DeskProtectPeriodActivity.DAY_WORKDAY.equals(numbers)) {

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

    //转化为时间
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

    @Override
    protected void onResume() {
        super.onResume();
        showSensitivityType();
        upDataIotSensitivity();
        showBaseDialog();
        IotSendOrder.sendMotionDetection(cameaHelper.getmCamera(), IotUtil.assemblyMotion(fences, spMoveArea, switching));
        Log.i(TAG, "灵敏度为" + fences[0] + "防护星期为" + fences[1] + ",防护时间为" + fences[2] + ",使能开关为" + switching + ",设防区域为" + spMoveArea);

    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void avIOCtrlOnLine() {

    }

    @Override
    public void avIOCtrlDataSource(byte[] data, int avIOCtrlMsgType) {
        switch (avIOCtrlMsgType) {
            case IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP:
                dismissBaseDialog();
        }

    }

    @Override
    public void avIOCtrlMsg(int resCode, String method) {

    }

    public static class MotionDetectionPojo {
        public MotionDetectionPojo(int switching, int sensitivity, int[] area, int defenceused, int week, byte[] moveTime) {
            this.switching = switching;
            this.sensitivity = sensitivity;
            this.area = area;
            this.defenceused = defenceused;
            this.week = week;
            this.moveTime = moveTime;
        }

        private int switching;
        private int sensitivity;
        private int[] area;
        private int defenceused;
        private int week;
        private byte[] moveTime;

        public int getSwitching() {
            return switching;
        }

        public void setSwitching(int switching) {
            this.switching = switching;
        }

        public int getSensitivity() {
            return sensitivity;
        }

        public void setSensitivity(int sensitivity) {
            this.sensitivity = sensitivity;
        }

        public int[] getArea() {
            return area;
        }

        public void setArea(int[] area) {
            this.area = area;
        }

        public int getDefenceused() {
            return defenceused;
        }

        public void setDefenceused(int defenceused) {
            this.defenceused = defenceused;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public byte[] getMoveTime() {
            return moveTime;
        }

        public void setMoveTime(byte[] moveTime) {
            this.moveTime = moveTime;
        }
    }
}
