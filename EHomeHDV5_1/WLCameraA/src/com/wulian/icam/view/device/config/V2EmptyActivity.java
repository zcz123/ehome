/**
 * Project Name:  iCam
 * File Name:     V2EmptyActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015年7月27日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.ConfigWiFiInfoModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @ClassName: V2EmptyActivity
 * @Function: TODO
 * @Date: 2015年7月27日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class V2EmptyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        ConfigWiFiInfoModel data = getIntent().getParcelableExtra("configInfo");
        //boolean isFirstAdd = getIntent().getBooleanExtra("isFirstAdd", true);
        Intent itIntent = new Intent();
        //if (data.getConfigWiFiType() == iCamConstants.CONFIG_SOFT_AP_SETTING) {
        //	itIntent.setClass(this, V2DeviceResetActivity.class);
        //} else {
        itIntent.setClass(this, DeviceLaunchGuideActivity.class);
        //}
        itIntent.putExtra("configInfo", data);
        startActivity(itIntent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }
}
