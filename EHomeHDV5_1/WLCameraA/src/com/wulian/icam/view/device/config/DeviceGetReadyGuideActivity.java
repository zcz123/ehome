/**
 * Project Name:  iCam
 * File Name:     V2DeviceGetReadyActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015楠烇拷6閺堬拷29閺冿拷
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * @author Puml
 * @ClassName: DeviceGetReadyGuideActivity
 * @Function: 摄像头准备界面
 * @Date: 2015年6月29日
 * @email puml@wuliangroup.cn
 */
public class DeviceGetReadyGuideActivity extends BaseFragmentActivity implements OnClickListener {
    private TextView tv_tips;
    private ImageView iv_device_type;
    private Button btn_next_step;// 听到提示音按钮
    private LinearLayout ll_choose_config_network_way;
    private TextView tv_help;// 没有听到提示音

    private ConfigWiFiInfoModel infoData;
    private String deviceId;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        btn_next_step = (Button) findViewById(R.id.btn_next_step);
        iv_device_type = (ImageView) findViewById(R.id.iv_device_type);
        tv_help = (TextView) findViewById(R.id.tv_help);
        ll_choose_config_network_way = (LinearLayout) findViewById(R.id.ll_choose_config_network_way);
        tv_help.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_help.setVisibility(View.VISIBLE);
        ll_choose_config_network_way.setVisibility(View.GONE);
        btn_next_step.setVisibility(View.VISIBLE);
    }

    private void initData() {
        infoData = getIntent().getParcelableExtra("configInfo");
        deviceId = infoData.getDeviceId();
        if (TextUtils.isEmpty(deviceId) || infoData == null) {
            this.finish();
        }
        handleDevice();
    }

    private void setListener() {
        btn_next_step.setOnClickListener(this);
        tv_help.setOnClickListener(this);
    }

    private void handleDevice() {
        btn_next_step.setText(getResources().getString(R.string.config_already_hear_tip_voice));
        tv_help.setText(getResources().getString(R.string.config_not_hear_tip_voice));

        DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
        switch (type) {
            case INDOOR:
                iv_device_type.setImageResource(R.drawable.icon_04_device_sys);
                tv_tips.setText(Html.fromHtml(getResources().getString(R.string.sb_if_motion_sys_01)));
                btn_next_step.setText(getResources().getString(R.string.sb_look_upordown_motion));
                break;
            case OUTDOOR:
                iv_device_type.setImageResource(R.drawable.icon_02_device_set);
                tv_tips.setText(Html.fromHtml(getResources().getString(R.string.config_click_device_sys_button)));
                break;
            case SIMPLE:
            case SIMPLE_N:
                iv_device_type.setImageResource(R.drawable.icon_03_device_set);
                tv_tips.setText(Html.fromHtml(getResources().getString(R.string.config_click_device_set_button)));
                break;
            case INDOOR2:
                iv_device_type.setImageResource(R.drawable.icon_04_device_sys);
                tv_tips.setText(Html.fromHtml(getResources().getString(R.string.config_click_device_sys_button)));
                break;
            case DESKTOP_C:
                iv_device_type.setImageResource(R.drawable.d002_desktop);
                tv_tips.setText(Html.fromHtml(getResources().getString(R.string.config_click_device_sys_button_for_desk)));
                break;
            case NewEagle:
                iv_device_type.setImageResource(R.drawable.icon_08_device_set);
                tv_tips.setText(Html.fromHtml(getResources().getString(R.string.config_click_device_sys_button_for_desk)));
                break;
            default:
                showMsg(R.string.config_not_support_device);
                this.finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_next_step) {
            switch (infoData.getConfigWiFiType()) {
                case iCamConstants.CONFIG_DIRECT_WIFI_SETTING: {
                    Intent intent = new Intent(this, WifiDirectSettingActivity.class);
                    intent.putExtra("configInfo", infoData);
                    startActivity(intent);
                    finish();
                }
                break;
                case iCamConstants.CONFIG_WIRED_SETTING:
                case iCamConstants.CONFIG_BARCODE_WIFI_SETTING: {
                    Intent intent = new Intent(this, BarCodeSettingActivity.class);
                    intent.putExtra("configInfo", infoData);
                    startActivity(intent);
                    finish();
                }
                break;
                default:
                    break;
            }
        } else if (id == R.id.tv_help) {
            showTipNotHearTipVoice();
        }
    }

    private void showTipNotHearTipVoice() {
        DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
        String html = "no_voice";
        if (DeviceType.DESKTOP_C.equals(type)) {
            html = "deskcamera_no_voice";
        } else if (DeviceType.INDOOR.equals(type)) {
            html = "penguin_camera_no_voice";//一代企鹅机提示 add by hxc
        }
        DialogUtils.showCommonInstructionsWebViewTipDialog(this,
                getResources().getString(R.string.config_not_hear_tip_voice), html);
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_device_reset_guide);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.config_device_getready);
    }

}
