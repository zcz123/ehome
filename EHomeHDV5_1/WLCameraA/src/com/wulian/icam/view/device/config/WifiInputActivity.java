/**
 * Project Name:  iCam
 * File Name:     SingleWiFiSettingActivity.java
 * Package Name:  com.wulian.icam.view.setting
 *
 * @Date: 2015年6月29日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebStorage.Origin;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.wifidirect.utils.DirectUtils;
import com.wulian.icam.wifidirect.utils.WiFiLinker;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;

import static com.wulian.icam.R.id.btn_next_step;

/**
 * @author Puml
 * @ClassName: WifiInputActivity
 * @Function: 设置当前的Wi-Fi的密码
 * @Date: 2015年6月29日
 * @email puml@wuliangroup.cn
 */
public class WifiInputActivity extends BaseFragmentActivity implements
        OnClickListener {
    private EditText et_wifi_name;
    private EditText et_wifi_pwd;
    private Button btn_start_linkwifi;
    private TextView tv_config_wifi_tips;
    private CheckBox cb_wifi_pwd_show;
    private CheckBox cb_no_wifi_pwd;
    private RelativeLayout rl_wifi_pwd_input;
    private TextView tv_change_wifi;

    // 参数
    private String deviceId;// 设备ID
    private int configWiFiType;// 配置Wi-Fi方式
    private WiFiLinker mWiFiLinker;
    private String originSSid;
    private String originSecurity;
    private String originPwd;
    private ConfigWiFiInfoModel mData;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        et_wifi_name = (EditText) findViewById(R.id.et_wifi_name);
        et_wifi_pwd = (EditText) findViewById(R.id.et_wifi_pwd);
        btn_start_linkwifi = (Button) findViewById(R.id.btn_start_linkwifi);
        tv_config_wifi_tips = (TextView) findViewById(R.id.tv_config_wifi_tips);
        cb_wifi_pwd_show = (CheckBox) findViewById(R.id.cb_wifi_pwd_show);
        cb_no_wifi_pwd = (CheckBox) findViewById(R.id.no_wifi_pwd_checkbox);
        rl_wifi_pwd_input = (RelativeLayout) findViewById(R.id.rl_wifi_pwd_input);
        tv_change_wifi = (TextView) findViewById(R.id.tv_change_wifi);
        tv_change_wifi.setOnClickListener(this);
        tv_config_wifi_tips.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

    }

    private void initData() {
        Bundle bd = getIntent().getExtras();
        if (bd == null) {
            this.finish();
            return;
        }
        mData = bd.getParcelable("configInfo");
        if (mData == null || TextUtils.isEmpty(mData.getDeviceId())) {
            this.finish();
            return;
        }
        deviceId = mData.getDeviceId();
        originPwd = mData.getWifiPwd();
        configWiFiType = mData.getConfigWiFiType();

        /** 网络操作初始化 */
        getCurrentWifiInfo();
    }

    private void getCurrentWifiInfo() {
        mWiFiLinker = new WiFiLinker();
        mWiFiLinker.WifiInit(this);

        if (mWiFiLinker.isWiFiEnable()) {
            WifiInfo info = mWiFiLinker.getWifiInfo();
            String currentSsid = "";
            if (info != null) {
                String ssid = info.getSSID();
                if (!TextUtils.isEmpty(ssid) && !info.getHiddenSSID()
                        && !"<unknown ssid>".equals(ssid)) {
                    currentSsid = ssid.replace("\"", "");
                } else {
                    showMsg(R.string.config_confirm_wifi_hidden);
                    this.finish();
                    return;
                }
            } else {
                showMsg(R.string.config_open_wifi);
                this.finish();
                return;
            }

            ScanResult result = null;
            List<ScanResult> scanList = mWiFiLinker.WifiGetScanResults();
            if (scanList == null || scanList.size() == 0) {
                showMsg(R.string.config_no_wifi_scan_result);
                this.finish();
                return;
            }
            for (ScanResult item : scanList) {
                if (item.SSID.equalsIgnoreCase(currentSsid)) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                showMsg(R.string.config_open_wifi);
                this.finish();
                return;
            }
            if (DirectUtils.isAdHoc(result.capabilities)) {
                showMsg(R.string.config_adhoc_is_not_suppored);
                this.finish();
                return;
            }
            if (DirectUtils.isOpenNetwork(result.capabilities)) {
                et_wifi_pwd.setVisibility(View.GONE);
            }
            originSecurity = DirectUtils
                    .getStringSecurityByCap(result.capabilities);
            String localMac = info.getMacAddress();
            if (TextUtils.isEmpty(localMac)) {
                showMsg(R.string.config_wifi_not_allocate_ip);
                this.finish();
                return;
            }

            originSSid = currentSsid;
            et_wifi_name.setText(currentSsid);
            if (!TextUtils.isEmpty(originPwd)
                    && originSSid.equals(mData.getWifiName())) {
                et_wifi_pwd.setText(originPwd);
            }
        } else {
            showMsg(R.string.config_open_wifi);
            this.finish();
        }
    }

    private void setListener() {
        cb_no_wifi_pwd.setOnClickListener(this);
        btn_start_linkwifi.setOnClickListener(this);
        cb_wifi_pwd_show.setOnClickListener(this);
        tv_config_wifi_tips.setOnClickListener(this);
        et_wifi_pwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (et_wifi_pwd.getText().length() > 7 || cb_no_wifi_pwd.isChecked()) {
                    btn_start_linkwifi.setBackgroundResource(R.color.action_bar_bg);
                    btn_start_linkwifi.setClickable(true);
                } else {
                    btn_start_linkwifi.setBackgroundResource(R.color.gray);
                    btn_start_linkwifi.setClickable(false);
                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        cb_wifi_pwd_show
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            et_wifi_pwd
                                    .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            et_wifi_pwd.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                    }
                });
        cb_no_wifi_pwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rl_wifi_pwd_input.setVisibility(View.GONE);
                    btn_start_linkwifi.setClickable(true);
                    btn_start_linkwifi.setBackgroundResource(R.color.action_bar_bg);
                } else {
                    rl_wifi_pwd_input.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showWifiView() {
        String wifiSSID = mWiFiLinker.getConnectedWifiSSID().replace("\"", "");
        WifiInfo info = mWiFiLinker.getWifiInfo();
        ScanResult result = null;
        if (!TextUtils.isEmpty(wifiSSID)) {
            List<ScanResult> scanList = mWiFiLinker.WifiGetScanResults();
            for (ScanResult item : scanList) {
                if (item.SSID.equalsIgnoreCase(wifiSSID)) {
                    result = item;
                    break;
                }
            }
            if (DirectUtils.isOpenNetwork(result.capabilities)) {
                et_wifi_pwd.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_start_linkwifi) {
            // configWiFiType = iCamConstants.CONFIG_DIRECT_WIFI_SETTING;
            String pwd = "";
            if (et_wifi_pwd.getVisibility() == View.VISIBLE) {
                pwd = et_wifi_pwd.getText().toString();
                if (TextUtils.isEmpty(pwd) && rl_wifi_pwd_input.getVisibility() == View.VISIBLE) {
                    showMsg(R.string.device_door_psw);
                    return;
                }
            }
            mData.setWifiName(originSSid);
            mData.setWifiPwd(pwd);
            mData.setSecurity(originSecurity);
            Intent it;
            if (mData.getConfigWiFiType() == iCamConstants.CONFIG_SOFT_AP_SETTING) {
                Log.e("getConfigWiFiType", "ap mode");
                it = new Intent(this, SoftApSettingActivity.class);
            } else {
                it = new Intent(this, DeviceGetReadyGuideActivity.class);
                Log.e("getConfigWiFiType", "ry  mode");
            }
            it.putExtra("configInfo", mData);
            startActivity(it);
            finish();
        } else if (id == R.id.titlebar_back) {
            this.finish();
        } else if (id == R.id.tv_config_wifi_tips) {
            DialogUtils.showCommonInstructionsWebViewTipDialog(
                    this,
                    getResources().getString(
                            R.string.config_how_to_speed_up_config_wifi),
                    "fastconn");
        } else if (id == R.id.tv_change_wifi) {
            this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_wifi_input);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.config_wifi_input);
    }

    @Override
    protected void onResume() {//切换wifi之后刷新ssid
        super.onResume();
        getCurrentWifiInfo();
    }

    @Override
    protected OnClickListener getRightClick() {
        return this;
    }

}
