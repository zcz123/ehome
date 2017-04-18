/**
 * Project Name:  iCam
 * File Name:     FunctionActivity.java
 * Package Name:  com.wulian.icam.view.help
 * @Date:         2014年12月19日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.help;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.ProgressBar;

import com.wulian.icam.R;
import com.wulian.icam.view.base.BaseHelpActivity;
import com.wulian.icam.view.widget.PBWebView;

/**
 * @ClassName: CommonPBActivity
 * @Function: 通用的 带进度条的 webview 浏览页面
 * @Date: 2014年12月23日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class CommonPBActivity extends BaseHelpActivity {

	private PBWebView wv_info;
	String title, url;

	protected void onCreate(Bundle savedInstanceState) {
		title = getIntent().getStringExtra("title");
		url = getIntent().getStringExtra("url");
		super.onCreate(savedInstanceState);
		initViews();

	}

	private void initViews() {
		wv_info = (PBWebView) this.findViewById(R.id.wv_info);
		wv_info.setProgerssBar((ProgressBar) findViewById(R.id.pb_loading));
		wv_info.setBackgroundColor(getResources().getColor(R.color.transparent));
		wv_info.getSettings().setJavaScriptEnabled(true);
		if (!TextUtils.isEmpty(url))
			wv_info.loadUrl(url);
		wv_info.setBackgroundColor(getResources().getColor(R.color.white));
	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_webview);

	}

	@Override
	protected void initTitle() {
		if (!TextUtils.isEmpty(title))
			tv_titlebar_title.setText(title);
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_info.canGoBack()) {
			wv_info.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (wv_info != null) {
			wv_info.removeAllViews();
			wv_info.destroy();
		}
	}
}
