/**
 * Project Name:  iCam
 * File Name:     V2DeviceLaunchStartActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015楠烇拷7閺堬拷25閺冿拷
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import android.content.Intent;
import android.net.wifi.WifiInfo;
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
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.wifidirect.utils.WiFiLinker;

/**
 * @ClassName: DeviceLaunchGuideActivity
 * @Function: 摄像机启动界面
 * @Date: 2015年7月25日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceLaunchGuideActivity extends BaseFragmentActivity implements
        OnClickListener {
    private TextView tv_tips;
    private ImageView iv_device_type;
    private Button btn_next_step;
    private LinearLayout ll_choose_config_network_way;
    private Button btn_wired_config_wifi;
    private Button btn_wifi_direct_config_wifi;

    private String deviceId;
    private ConfigWiFiInfoModel mData;
    private WiFiLinker mWiFiLinker;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        iv_device_type = (ImageView) findViewById(R.id.iv_device_type);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        btn_next_step = (Button) findViewById(R.id.btn_next_step);
        ll_choose_config_network_way = (LinearLayout) findViewById(R.id.ll_choose_config_network_way);
        btn_wired_config_wifi = (Button) findViewById(R.id.btn_wired_config_wifi);
        btn_wifi_direct_config_wifi = (Button) findViewById(R.id.btn_wifi_direct_config_wifi);
    }

    private void initData() {
        mData = getIntent().getParcelableExtra("configInfo");
        if (mData == null)
            finish();
        deviceId = mData.getDeviceId();
        /** 网络操作初始化 */
        mWiFiLinker = new WiFiLinker();
        mWiFiLinker.WifiInit(this);
        handleDevice();
    }

    private void setListener() {
        btn_next_step.setOnClickListener(this);
        btn_wired_config_wifi.setOnClickListener(this);
        btn_wifi_direct_config_wifi.setOnClickListener(this);
    }

    private void handleDevice() {
        DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
        switch (type) {
            case INDOOR:
                iv_device_type.setImageResource(R.drawable.type_04_device);
                tv_tips.setText(Html.fromHtml(getResources().getString(
                        R.string.config_link_device_check_led_04)));
                ll_choose_config_network_way.setVisibility(View.GONE);
                btn_next_step.setVisibility(View.VISIBLE);
                break;
            case OUTDOOR:
                iv_device_type.setImageResource(R.drawable.type_02_device);
                tv_tips.setText(Html.fromHtml(getResources().getString(
                        R.string.config_link_device_check_led_02)));
                if (mData.getConfigWiFiType() == iCamConstants.CONFIG_UNKNOWN
                        && mData.isAddDevice()) {
                    ll_choose_config_network_way.setVisibility(View.VISIBLE);
                    btn_next_step.setVisibility(View.GONE);
                } else {
                    ll_choose_config_network_way.setVisibility(View.GONE);
                    btn_next_step.setVisibility(View.VISIBLE);
                }
                break;
            case SIMPLE:
            case SIMPLE_N:
                iv_device_type.setImageResource(R.drawable.type_03_device);
                tv_tips.setText(Html.fromHtml(getResources().getString(
                        R.string.config_link_device_check_led_03)));
                ll_choose_config_network_way.setVisibility(View.GONE);
                btn_next_step.setVisibility(View.VISIBLE);
                break;
            case INDOOR2:
                iv_device_type.setImageResource(R.drawable.type_04_device);
                tv_tips.setText(Html.fromHtml(getResources().getString(
                        R.string.config_link_device_check_led_04)));
                ll_choose_config_network_way.setVisibility(View.GONE);
                btn_next_step.setVisibility(View.VISIBLE);
                break;
            case DESKTOP_C:
                iv_device_type.setImageResource(R.drawable.type06_bg);
                String tip = getResources().getString(R.string.config_wait_led_light_05);
                tv_tips.setText(Html.fromHtml(tip));
                if (mData.getConfigWiFiType() == iCamConstants.CONFIG_UNKNOWN
                        && mData.isAddDevice()) {
                    ll_choose_config_network_way.setVisibility(View.VISIBLE);
                    btn_next_step.setVisibility(View.GONE);
                } else {
                    ll_choose_config_network_way.setVisibility(View.GONE);
                    btn_next_step.setVisibility(View.VISIBLE);
                }
                break;
            case NewEagle:
                iv_device_type.setImageResource(R.drawable.type_08_device);
                tv_tips.setText(Html.fromHtml(getResources().getString(R.string.config_wait_led_light_05)));
                break;
            default:
                showMsg(R.string.config_not_support_device);
                this.finish();
                break;
        }
    }

    @Override
    protected OnClickListener getLeftClick() {
        return this;
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_device_reset_guide);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.setting_device_launch_start);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            this.finish();
        } else if (id == R.id.btn_next_step) {
            if (checkWiFiConnectState()) {
                Intent it;
                it = new Intent(this, WifiInputActivity.class);
                it.putExtra("configInfo", mData);
                startActivity(it);
                this.finish();
            }
        } else if (id == R.id.btn_wired_config_wifi) {
            if (mData.isAddDevice()) {
                Intent it;
                it = new Intent(this, BarCodeSettingActivity.class);
                mData.setConfigWiFiType(iCamConstants.CONFIG_WIRED_SETTING);
                it.putExtra("configInfo", mData);
                startActivity(it);
                this.finish();
            } else {
                showMsg(R.string.config_connect_wired_config_way_tip);
            }
        } else if (id == R.id.btn_wifi_direct_config_wifi) {
            if (checkWiFiConnectState()) {
                Intent it;
                it = new Intent(this, WifiInputActivity.class);
                it.putExtra("configInfo", mData);
                startActivity(it);
                this.finish();
            }
        }
    }

    private boolean checkWiFiConnectState() {
        if (mWiFiLinker.isWiFiEnable()) {
            WifiInfo info = mWiFiLinker.getWifiInfo();
            if (info != null) {
                String ssid = info.getSSID();
                if (TextUtils.isEmpty(ssid) || "0x".equals(ssid)) {
                    showMsg(R.string.config_open_wifi);
                    return false;
                } else {
                    if (info.getHiddenSSID() || "<unknown ssid>".equals(ssid)) {
                        showMsg(R.string.config_confirm_wifi_hidden);
                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                showMsg(R.string.config_open_wifi);
                return false;
            }
        } else {
            showMsg(R.string.config_open_wifi);
            return false;
        }
    }
}
