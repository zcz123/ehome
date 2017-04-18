/**
 * Project Name:  iCam
 * File Name:     BarCodeWiFiSettingActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015楠烇拷6閺堬拷30閺冿拷
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.device.config;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wulian.icam.R;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.wifidirect.utils.DirectUtils;
import com.wulian.lanlibrary.LanController;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
/**
 * @ClassName: BarCodeSettingActivity
 * @Function: 二维码配置Wi-Fi
 * @Date: 2015年6月30日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class BarCodeSettingActivity extends BaseFragmentActivity implements OnClickListener {
    private Button btn_hear_scan_voice;
    private ImageView iv_barcode;
    private RelativeLayout rl_barcode_layout;
    private LinearLayout ll_barcode;
    private String deviceId;
    private int QrWidth;
    private Dialog mTipDialog;
    private ConfigWiFiInfoModel mData;
    private static final int RETRY_QR_SEED = 1;
    private TextView tv_help;
    private TextView tv_barcode_link_tips;

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        initData();
        setListener();
        setBrightestScreen();
    }

    ;

    private void initView() {
        rl_barcode_layout = (RelativeLayout) findViewById(R.id.rl_barcode_layout);
        ll_barcode = (LinearLayout) findViewById(R.id.ll_barcode);
        btn_hear_scan_voice = (Button) findViewById(R.id.btn_next_step);
        iv_barcode = (ImageView) findViewById(R.id.iv_barcode);
        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_help.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_barcode_link_tips = (TextView) findViewById(R.id.tv_barcode_link_tips);
    }

    private void initData() {
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            mData = getIntent().getParcelableExtra("configInfo");
            if (mData == null) {
                this.finish();
                return;
            } else {
                deviceId = mData.getDeviceId();
                Log.d("BarCodeSettingActivity", deviceId);
                if (TextUtils.isEmpty(deviceId)) {
                    this.finish();
                    return;
                }
            }
        } else {
            this.finish();
            return;
        }
        QrWidth = Utils.getDeviceSize(this).widthPixels;
        ViewGroup.LayoutParams lp = ll_barcode.getLayoutParams();
        float left_right_width = getResources().getDimension(R.dimen.margin_little);
        float linearWidth = QrWidth - Utils.px2dip(this, left_right_width * 2);
        lp.height = (int) linearWidth;
        lp.width = (int) linearWidth;
        ll_barcode.setLayoutParams(lp);
        left_right_width = getResources().getDimension(R.dimen.margin_little)
                + getResources().getDimension(R.dimen.margin_little);
        QrWidth -= Utils.px2dip(this, left_right_width * 2);

        DialogUtils.showBarcodeConfigTipDialog(this, deviceId);

        handleDevice();

        rl_barcode_layout.setVisibility(View.GONE);
        if (mData.isAddDevice()) {
            Log.d("BarCodeSettingActivity", "isAddDevice");
            BindCheck(false);
        } else {
            Log.d("BarCodeSettingActivity", "NOAddDevice");
            handlePicture(null);
        }
    }

    private void handleDevice() {

        DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
        switch (type) {
            case INDOOR:
                break;
            case OUTDOOR:
                break;
            case SIMPLE:
            case SIMPLE_N:
                break;
            case INDOOR2:
                break;
            case DESKTOP_C:
                tv_barcode_link_tips.setText(Html.fromHtml(getResources().getString(R.string.config_barcode_tips_hear_voice_for_06)));
                break;
            case NewEagle:
                break;
            default:
                showMsg(R.string.config_not_support_device);
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void BindCheck(boolean showDialog) {
        myHandler.removeMessages(RETRY_QR_SEED);
        sendRequest(RouteApiType.V3_BIND_CHECK, RouteLibraryParams.V3BindCheck(userInfo.getAuth(), deviceId),
                showDialog);
    }

    private void handlePicture(String seed) {
        rl_barcode_layout.setVisibility(View.VISIBLE);
        String wifi_info = generateWifiCode(seed);
        createQRImage(wifi_info, QrWidth, QrWidth);
    }

    private String generateWifiCode(String seed) {
        String originSSid = mData.getWifiName();
        String originSecurity = mData.getSecurity();
        String pwd = mData.getWifiPwd();
        DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
        switch (type) {
            case DESKTOP_C:
                return getWifiCodeForDesk(seed, originSSid, originSecurity, pwd);
            default:
                return getWifiCodeCommon(seed, originSSid, originSecurity, pwd);
        }
    }

    private String getWifiCodeCommon(String seed, String originSSid, String originSecurity, String pwd) {
        StringBuilder sb = new StringBuilder();
        if (mData.getConfigWiFiType() == iCamConstants.CONFIG_WIRED_SETTING) {
            sb.append("9");
        } else {
            int secType = DirectUtils.getTypeSecurityByCap(originSecurity);
            if (secType == 4) {//开放网络 add by hxc
                sb.append(secType+"\n");
                sb.append(originSSid);
            } else {
                sb.append(DirectUtils.getTypeSecurityByCap(originSecurity) + "\n");
                sb.append(originSSid + "\n");
                sb.append(LanController.EncodeMappingString(pwd));
            }
        }

        if (mData.isAddDevice()) {
            sb.append("\n");
            sb.append(LanController.EncodeMappingString(seed));
        }
        return sb.toString();
    }

    private String getWifiCodeForDesk(String seed, String originSSid, String originSecurity, String pwd) {
        StringBuilder sb = new StringBuilder();
        sb.append("01\n");
        sb.append(originSSid + "\n");
        sb.append("psk" + "\n");
        sb.append(pwd + "\n");
        return sb.toString();
    }

    Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RETRY_QR_SEED:
                    mTipDialog = DialogUtils.showCommonTipDialog(BarCodeSettingActivity.this, false, "", getResources()
                                    .getString(R.string.config_barcode_expire),
                            getResources().getString(R.string.config_barcode_get_retry), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mTipDialog.dismiss();
                                    BarCodeSettingActivity.this.reLogin();
                                    BindCheck(true);
                                }
                            });
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private Bitmap createQRImage(String qrdata, int qrwidth, int qrheight) {
        Bitmap bitmap = null;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(qrdata, BarcodeFormat.QR_CODE, qrwidth, qrheight, hints);
            int[] pixels = new int[qrwidth * qrheight];
            for (int y = 0; y < qrheight; y++) {
                for (int x = 0; x < qrwidth; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * qrwidth + x] = 0xff000000;
                    } else {
                        pixels[y * qrwidth + x] = 0xffffffff;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(qrwidth, qrheight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, qrwidth, 0, 0, qrwidth, qrheight);
            iv_barcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    @Override
    protected void DataReturn(boolean success, RouteApiType apiType, String json) {
        super.DataReturn(success, apiType, json);
        Log.e("BarCodeSettingActivity", json);
        Log.e("BarCodeSettingActivity", apiType.toString());
        if (success) {
            switch (apiType) {
                case V3_BIND_CHECK:
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String seed = jsonObject.isNull("seed") ? "" : jsonObject.getString("seed");
                        if (!TextUtils.isEmpty(seed)) {
                            String timestamp = jsonObject.isNull("timestamp") ? "" : jsonObject.getString("timestamp");

                            seed = RouteLibraryParams.getDecodeString(seed, timestamp);
                            handlePicture(seed);
                            myHandler.sendEmptyMessageDelayed(RETRY_QR_SEED, 300000);
                        } else {
                            showMsg(R.string.config_device_bind_others);
                            finish();
                        }
                    } catch (JSONException e) {
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (apiType) {
                case V3_BIND_CHECK:
                    showMsg(R.string.config_query_device_fail);
                    myHandler.sendEmptyMessage(RETRY_QR_SEED);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_next_step) {
            if (mData.isAddDevice()) {
                Intent it = new Intent(BarCodeSettingActivity.this, CheckBindingStateActivity.class);
                it.putExtra("configInfo", mData);
                startActivity(it);
            } else if (DeviceType.getDevivceTypeByDeviceID(deviceId).equals(DeviceType.DESKTOP_C)) {
                Intent it = new Intent(BarCodeSettingActivity.this, CheckConfigResultForDeskCamActivity.class);
                it.putExtra("configInfo", mData);
                startActivity(it);
            } else {
                showMsg(R.string.setting_wifi_success);
            }
            finish();
        } else if (id == R.id.titlebar_back) {
            BarCodeSettingActivity.this.finish();
        } else if (id == R.id.tv_help) {
            DialogUtils.showCommonInstructionsWebViewTipDialog(this,
                    getResources().getString(R.string.config_not_hear_tip_voice), "scan_no_voice");
        }
    }

    private void setListener() {
        btn_hear_scan_voice.setOnClickListener(this);
        tv_help.setOnClickListener(this);
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_barcode_setting);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.config_barcode_config);
    }

    @Override
    protected OnClickListener getRightClick() {
        return this;
    }

    /**
     * 项目名称：iCam 类描述： 创建人：huihui 创建时间：2016年5月9日 上午10:36:37 修改人：Administrator
     * 修改时间：2016年5月9日 上午10:36:37 修改备注：
     *
     * @version
     *
     */
    private void setBrightestScreen() {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.screenBrightness = 1;
        getWindow().setAttributes(wl);
    }
}
