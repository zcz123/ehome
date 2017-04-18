/**
 * Project Name:  iCam
 * File Name:     AccessDeviceActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2014年10月23日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.info;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseHelpActivity;
import com.wulian.icam.view.help.CommonPBActivity;
import com.wulian.icam.view.help.DisclaimerActivity;

/**
 * @ClassName: AboutActivity
 * @Function: 关于我们
 * @Date: 2014年10月23日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class AboutUsActivity extends BaseHelpActivity implements
		OnClickListener {
	TextView tv_version;
	ImageView iv_about_us;
	LinearLayout ll_offical, ll_sina, ll_weixin, ll_customer_service,
			ll_disclaimer;
	private Dialog noticeDialog;
	private View noticeDialogView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initListeners();
		initData();
	}

	private void initData() {
		PackageInfo info = Utils.getPackageInfo(this);
		// Date now = new Date();
		if (info == null)
			return;
		// now.setTime(info.lastUpdateTime);
		// SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd",
		// Locale.getDefault());
		// String versionTime = df.format(now);
		tv_version.setText("V " + info.versionName);
		// + "(" + versionTime + ")");

	}

	private void initViews() {
		tv_version = (TextView) findViewById(R.id.tv_version);
		ll_offical = (LinearLayout) this.findViewById(R.id.ll_offical);
		ll_sina = (LinearLayout) this.findViewById(R.id.ll_sina);
		ll_weixin = (LinearLayout) this.findViewById(R.id.ll_weixin);
		ll_customer_service = (LinearLayout) this
				.findViewById(R.id.ll_customer_service);
		ll_disclaimer = (LinearLayout) this.findViewById(R.id.ll_disclaimer);

		iv_about_us = (ImageView) findViewById(R.id.iv_about_us);

	}

	private void initListeners() {
		ll_offical.setOnClickListener(this);// http://www.wulian.cc/
		ll_sina.setOnClickListener(this);//
		ll_weixin.setOnClickListener(this);// http://weixin.qq.com/r/pUMCGpzEX8Egrdh59xa3
		ll_customer_service.setOnClickListener(this);
		ll_disclaimer.setOnClickListener(this);
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_about_us);

	}

	@Override
	protected void initTitle() {
		super.tv_titlebar_title.setText(getResources().getString(
				R.string.info_about_us));

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_offical) {
			startActivity(new Intent(this, CommonPBActivity.class).putExtra(
					"title",
					getResources().getText(R.string.info_official_website))
					.putExtra(
							"url",
							getResources().getText(
									R.string.info_official_website_url)));
		} else if (id == R.id.ll_sina) {
			startActivity(new Intent(this, CommonPBActivity.class).putExtra(
					"title", getResources().getText(R.string.info_sina))
					.putExtra("url",
							getResources().getText(R.string.info_sina_url)));
		} else if (id == R.id.ll_weixin) {
			startActivity(new Intent(this, CommonPBActivity.class).putExtra(
					"title", getResources().getText(R.string.info_sina))
					.putExtra("url",
							"file:///android_asset/help/weixin_public.html"));
		} else if (id == R.id.ll_customer_service) {
			showNoticeDialog();
		} else if (id == R.id.ll_disclaimer) {
			startActivity(new Intent(this, DisclaimerActivity.class));
		}
	}

	private void showNoticeDialog() {
		Resources rs = getResources();
		if (noticeDialog == null)
			noticeDialog = DialogUtils.showCommonDialog(
					this,
					false,
					rs.getString(R.string.common_prompt),
					rs.getString(R.string.info_prompt_callnum)
							+ rs.getString(R.string.info_customer_phonenum),
					null, null, new OnClickListener() {

						@Override
						public void onClick(View v) {
							int id = v.getId();
							if (id == R.id.btn_positive) {
								noticeDialog.dismiss();
								Intent intent = new Intent(
										Intent.ACTION_CALL,
										Uri.parse("tel:"
												+ getString(R.string.info_customer_phonenum)));
								startActivity(intent);
							} else if (id == R.id.btn_negative) {
								noticeDialog.dismiss();
							} else {
							}
						}
					});
		if (!noticeDialog.isShowing()) {
			noticeDialog.show();
		}

	}
}
