/**
 * Project Name:  iCam
 * File Name:     CheckBindingStateActivity.java
 * Package Name:  com.wulian.icam.view.device.config
 *
 * @Date: 2016年4月26日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.JsonHandler;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;

/**
 * @ClassName: CheckBindingStateActivity
 * @Function: 检查绑定状态
 * @Date: 2016年4月26日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class CheckBindingStateActivity extends BaseFragmentActivity implements OnClickListener {
    private ImageView iv_oval_left_device;
    private ImageView iv_config_wifi_step_state;
    private AnimationDrawable mAnimation;
    private Dialog mExitDialog;

    private ConfigWiFiInfoModel mInfoData;
    private String originDeviceId;
    private static final long START_DELAY = 1000;
    private static final int BIND_RESULT_MSG = 1;
    private int mCurrentNum = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        initData();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawHandler.postDelayed(mRunnable, START_DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAnimation(mAnimation);
    }

    @Override
    protected void onDestroy() {
        mDrawHandler.removeCallbacksAndMessages(null);
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void initView() {
        iv_oval_left_device = (ImageView) findViewById(R.id.iv_oval_left_device);
        iv_config_wifi_step_state = (ImageView) findViewById(R.id.iv_config_wifi_step_state);
    }

    private void initData() {
        Bundle bd = getIntent().getExtras();
        mInfoData = bd.getParcelable("configInfo");
        originDeviceId = mInfoData.getDeviceId();
        if (mInfoData == null) {
            this.finish();
            return;
        }
        mAnimation = (AnimationDrawable) iv_config_wifi_step_state.getDrawable();
        handleDevice();
        myHandler.sendEmptyMessage(BIND_RESULT_MSG);
    }

    private void setListener() {
    }

    private void handleDevice() {
        DeviceType type = DeviceType.getDevivceTypeByDeviceID(originDeviceId);
        switch (type) {
            case INDOOR:
                iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_4);
                break;
            case OUTDOOR:
                iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_2);
                break;
            case SIMPLE:
            case SIMPLE_N:
                iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_3);
                break;
            case INDOOR2:
                iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_4);
                break;
            case DESKTOP_C:
                iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_3);
                break;
            case NewEagle:
                iv_oval_left_device.setImageResource(R.drawable.monitor_cat_eye_online);
                break;

            default:
                showMsg(R.string.config_not_support_device);
                this.finish();
                break;
        }
    }

    private Handler mDrawHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };

    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case BIND_RESULT_MSG:
                    if (mCurrentNum <= 15) {
                        checkBindingState();
                        mCurrentNum++;
                        myHandler.sendEmptyMessageDelayed(BIND_RESULT_MSG, 6000);
                    } else {
                        jumpToResult(false);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void checkBindingState() {
        sendRequest(RouteApiType.V3_BIND_RESULT, RouteLibraryParams.V3BindResult(userInfo.getAuth(), originDeviceId),
                false);
    }

    private void jumpToResult(boolean isSuccess) {
        myHandler.removeMessages(BIND_RESULT_MSG);
        Intent it = new Intent();
        it.putExtra("configInfo", mInfoData);
        if (!isSuccess) {
            it.setClass(this, DeviceConfigFailResultActivity.class);
        } else {
            it.setClass(this, DeviceConfigSuccessActivity.class);
        }
        startActivity(it);
        this.finish();
    }

    protected void startAnimation(final AnimationDrawable animation) {
        if (animation != null && !animation.isRunning()) {
            animation.run();
        }
    }

    protected void stopAnimation(final AnimationDrawable animation) {
        if (animation != null && animation.isRunning())
            animation.stop();
    }

    @Override
    protected void DataReturn(boolean success, RouteApiType apiType, String json) {
        super.DataReturn(success, apiType, json);
        if (success) {
            switch (apiType) {
                case V3_BIND_RESULT:
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int result = jsonObject.isNull("result") ? 0 : jsonObject.getInt("result");
                        if (result == 1) {
                            jumpToResult(true);
                        }
                    } catch (JSONException e) {

                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (apiType) {
                case V3_BIND_RESULT:
                    showMsg(R.string.config_query_device_fail);
                    jumpToResult(false);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        Resources rs = getResources();
        mExitDialog = DialogUtils.showCommonDialog(this, true, rs.getString(R.string.common_tip),
                rs.getString(R.string.config_is_exit_current_config), rs.getString(R.string.config_exit),
                rs.getString(R.string.common_cancel), new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.btn_positive) {
                            mExitDialog.dismiss();
                            finish();
                        } else if (id == R.id.btn_negative) {
                            mExitDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_check_binding_state);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.config_link_camera);
    }
}
