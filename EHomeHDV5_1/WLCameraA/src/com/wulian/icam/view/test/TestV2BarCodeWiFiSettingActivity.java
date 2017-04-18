/**
 * Project Name:  iCam
 * File Name:     BarCodeWiFiSettingActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年6月30日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.test;

import java.util.Hashtable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wulian.icam.R;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * @ClassName: BarCodeWiFiSettingActivity
 * @Function: 二维码配置Wi-Fi
 * @Date: 2015年6月30日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class TestV2BarCodeWiFiSettingActivity extends BaseFragmentActivity
		implements OnClickListener {
	private Button btn_hear_scan_voice;
	private ImageView iv_barcode;
	private LinearLayout ll_barcode;

	private String deviceId;
	private String wifi_info;
	private ConfigWiFiInfoModel mData;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initView();
		initData();
		setListener();
	};

	private void initView() {
		ll_barcode = (LinearLayout) findViewById(R.id.ll_barcode);
		btn_hear_scan_voice = (Button) findViewById(R.id.btn_next_step);
		iv_barcode = (ImageView) findViewById(R.id.iv_barcode);
	}

	private void initData() {
		mData = new ConfigWiFiInfoModel();
		mData.setDeviceId("CMIC03XXXXXXXXXXXXXXXXXX");
		mData.setSecurity("psk");
		mData.setWifiName("HEIHEI");
		mData.setWifiPwd("12345678");
//		DialogUtils.showBarcodeConfigTipDialog(this,);
		handlePicture();
	}

	private void handlePicture() {
		int width = Utils.getDeviceSize(this).widthPixels;
		ViewGroup.LayoutParams lp = ll_barcode.getLayoutParams();
		float left_right_width = getResources().getDimension(
				R.dimen.v2_config_wifi_prog_left_right_margin);
		float linearWidth = width - Utils.px2dip(this, left_right_width * 2);
		lp.height = (int) linearWidth;
		lp.width = (int) linearWidth;
		ll_barcode.setLayoutParams(lp);
		left_right_width = getResources().getDimension(R.dimen.margin_normal)
				+ getResources().getDimension(
						R.dimen.v2_config_wifi_prog_left_right_margin);
		width -= Utils.px2dip(this, left_right_width * 2);
		String originSSid = mData.getWifiName();
		String originSecurity = mData.getSecurity();
		String pwd = mData.getWifiPwd();
		StringBuilder sb = new StringBuilder();
		sb.append("01\n");
		sb.append(originSSid + "\n");
		sb.append(originSecurity + "\n");
		sb.append(pwd + "\n");
		wifi_info = sb.toString();
		createQRImage(wifi_info, width, width);// 暂时
	}

	//
	private Bitmap createQRImage(String qrdata, int qrwidth, int qrheight) {
		Bitmap bitmap = null;
		try {
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(qrdata,
					BarcodeFormat.QR_CODE, qrwidth, qrheight, hints);
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
			bitmap = Bitmap.createBitmap(qrwidth, qrheight,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, qrwidth, 0, 0, qrwidth, qrheight);
			iv_barcode.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_next_step) {
		} else if (id == R.id.titlebar_back) {
			TestV2BarCodeWiFiSettingActivity.this.finish();
		}
	}

	private void setListener() {
		btn_hear_scan_voice.setOnClickListener(this);
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
}
