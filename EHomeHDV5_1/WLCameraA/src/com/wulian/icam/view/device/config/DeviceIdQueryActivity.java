/**
 * Project Name:  iCam
 * File Name:     V2DeviceQueryActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015楠烇拷7閺堬拷25閺冿拷
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.CheckBind;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.utils.StringUtil;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.controller.RouteLibraryController;

/**
 * @ClassName: DeviceIdQueryActivity
 * @Function: 查询摄像机是否被绑定
 */
public class DeviceIdQueryActivity extends BaseFragmentActivity implements
        OnClickListener {
    private RelativeLayout rl_query_device;
    private RelativeLayout rl_query_device_fail;
    private Button btn_retry_query_device;

    private String deviceId;
    private boolean isAddDevice;
    private String seed;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        rl_query_device = (RelativeLayout) findViewById(R.id.rl_query_device);
        rl_query_device_fail = (RelativeLayout) findViewById(R.id.rl_query_device_fail);
        btn_retry_query_device = (Button) findViewById(R.id.btn_retry_query_device);
    }

    private void initData() {
        String msgData = getIntent().getStringExtra("msgData");
        isAddDevice = getIntent().getBooleanExtra("isAddDevice", true);
        if (TextUtils.isEmpty(msgData)) {
            showMsg(R.string.config_error_deviceid);
            finish();
            return;
        }

        if (msgData.contains("device_id=")) {
            deviceId = Utils.getRequestParams(msgData).get("device_id");
        } else {
            deviceId = msgData;
        }
        deviceId = deviceId.toLowerCase(Locale.getDefault());
        if (deviceId != null && deviceId.length() == 20) {// 6+2+12
            showDeviceQuery();
            if (isAddDevice) {
                startBindingCheck();
            } else {
                getDeviceFlag();
            }
        } else {
            showMsg(R.string.config_error_deviceid);
            finish();
        }
    }

    private void setListener() {
        btn_retry_query_device.setOnClickListener(this);
    }

    private void showDeviceQuery() {
        setActivityTitle(getResources()
                .getString(R.string.config_device_search));
        rl_query_device.setVisibility(View.VISIBLE);
        rl_query_device_fail.setVisibility(View.GONE);
    }

    private void showDeviceQueryFail() {
        setActivityTitle(getResources().getString(R.string.common_result));
        rl_query_device.setVisibility(View.GONE);
        rl_query_device_fail.setVisibility(View.VISIBLE);
    }

    private void getDeviceFlag() {
        sendRequest(RouteApiType.V3_APP_FLAG,
                RouteLibraryParams.V3AppFlag(deviceId, "donotneedauth"), false);
    }

    private void startBindingCheck() {
        userInfo = ICamGlobal.getInstance().getUserinfo();
        RouteLibraryController.getInstance().doRequest(this,
                RouteApiType.V3_BIND_CHECK,
                RouteLibraryParams.V3BindCheck(userInfo.getAuth(), deviceId),
                this);
        Log.i(this.getClass().getName(),userInfo.getAuth());
        Log.i(this.getClass().getName(),deviceId);
    }

    private void saveDeviceIdToSp() {
        if (!StringUtil.isNullOrEmpty(deviceId) && deviceId.length() >= 4) {
            String id = deviceId.substring(4);
            Utils.saveHandInput2Sp(app.getUserinfo().getUuid(), id, this);
        }
    }

    private void handleData(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                // JSONObject flagObj = jsonObj.getJSONObject("flag");
                boolean qrConnect = false;
                boolean smartConnect = false;
                boolean wiredConnect = false;
                boolean apConnect = false;
                int currentConfigType = iCamConstants.CONFIG_UNKNOWN;
                if (!jsonObj.isNull("qr") && jsonObj.getInt("qr") == 1) {
                    qrConnect = true;
                }
                if (!jsonObj.isNull("sc") && jsonObj.getInt("sc") == 1) {
                    smartConnect = true;
                }
                DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
                switch (type) {
                    case INDOOR:
                        apConnect = true;
                        smartConnect = false;
                        wiredConnect = false;
                        break;
                    case OUTDOOR:
                        qrConnect = true;
                        smartConnect = true;
                        apConnect = false;
                        wiredConnect = true;
                        break;
                    case SIMPLE:
                        if (smartConnect) {
                            apConnect = false;
                        } else {
                            apConnect = true;
                        }
                        wiredConnect = false;
                        break;
                    case INDOOR2:
                    case SIMPLE_N:
                        apConnect = false;
                        wiredConnect = false;
                        smartConnect = true;
                        break;
                    case DESKTOP_C:
                        qrConnect = true;
                        apConnect = false;
                        smartConnect = true;
                        wiredConnect = false;
                        break;
                    case NewEagle:
                        qrConnect = true;
                        break;
                    default:
                        showMsg(R.string.config_error_deviceid);
                        finish();
                        break;
                }
                // qrConnect = false;
                if (!wiredConnect) {
                    if (qrConnect) {
                        currentConfigType = iCamConstants.CONFIG_BARCODE_WIFI_SETTING;
                    } else if (smartConnect) {
                        currentConfigType = iCamConstants.CONFIG_DIRECT_WIFI_SETTING;
                    } else if (apConnect) {
                        currentConfigType = iCamConstants.CONFIG_SOFT_AP_SETTING;
                    }
                }
                ConfigWiFiInfoModel data = new ConfigWiFiInfoModel();
                data.setDeviceId(deviceId);
                data.setAddDevice(isAddDevice);
                if (isAddDevice) {
                    data.setSeed(seed);
                }
                data.setApConnect(apConnect);
                data.setQrConnect(qrConnect);
                data.setSmartConnect(smartConnect);
                data.setWiredConnect(wiredConnect);
                data.setConfigWiFiType(currentConfigType);
                Intent it = new Intent(this, V2EmptyActivity.class);
                it.putExtra("configInfo", data);
                startActivity(it);
                finish();
            } catch (JSONException e) {
                showMsg(R.string.config_error_deviceid);
                finish();
            }
        }
    }

    @Override
    protected void DataReturn(boolean success, RouteApiType apiType, String json) {
        super.DataReturn(success, apiType, json);
        if (success) {
            switch (apiType) {
                case V3_APP_FLAG:
                    handleData(json);
                    break;
                case V3_BIND_CHECK:
                    CheckBind cb = Utils.parseBean(CheckBind.class, json);
                    if (cb == null) {
                        showDeviceQueryFail();
                    } else {
                        if (!TextUtils.isEmpty(cb.getUuid())) {
                            saveDeviceIdToSp();
                            if (cb.getUuid().equals(userInfo.getUuid())) {
                                showMsg(R.string.config_device_already_in_list);
                                finish();
                            } else {
                                Intent it = new Intent(this,
                                        DeviceAlreadyBindedResultActivity.class);
                                it.putExtra("deviceId", deviceId);
                                startActivity(it);
                                finish();
                            }
                        } else if (!TextUtils.isEmpty(cb.getSeed())) {
                            saveDeviceIdToSp();
                            seed = cb.getSeed();
                            getDeviceFlag();
                        } else {
                            showDeviceQueryFail();
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (apiType) {
                case V3_APP_FLAG:
                    // handleData(null);
                    showDeviceQueryFail();
                    break;
                case V3_BIND_CHECK:
                    showDeviceQueryFail();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_deviceid_query);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.config_device_search);
    }

    @Override
    protected OnClickListener getLeftClick() {
        return this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_retry_query_device) {
            startBindingCheck();
        } else if (id == R.id.titlebar_back) {
            this.finish();
        }
    }
}
