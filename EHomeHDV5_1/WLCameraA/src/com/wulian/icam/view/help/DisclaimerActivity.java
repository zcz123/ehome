/**
 * Project Name:  iCam
 * File Name:     FaqActivity.java
 * Package Name:  com.wulian.icam.view.help
 * @Date:         2014年12月19日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.help;

import java.util.Locale;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.wulian.icam.R;
import com.wulian.icam.view.base.BaseHelpActivity;
import com.wulian.icam.view.widget.PBWebView;

/**
 * @ClassName: DisclaimerActivity
 * @Function: 免责申明
 * @Date: 2015年4月18日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class DisclaimerActivity extends BaseHelpActivity {

	private PBWebView wv_info;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
	}
	


	private void initViews() {
		wv_info = (PBWebView) this.findViewById(R.id.wv_info);
		wv_info.setProgerssBar((ProgressBar) findViewById(R.id.pb_loading));
		wv_info.setBackgroundColor(getResources().getColor(R.color.transparent));	
		String language = Locale.getDefault().getLanguage();
		String country = Locale.getDefault().getCountry();
		String temp = (language + "_" + country).toLowerCase(Locale
				.getDefault());
		String name="disclaimer";
		if (temp.equalsIgnoreCase("zh_cn")
				|| temp.equalsIgnoreCase("pt_br")) {
			wv_info.loadUrl("file:///android_asset/help/" + temp + "/"
					+ name + ".html");
		} else {
			wv_info.loadUrl("file:///android_asset/help/" +"en/"+ name
					+ ".html");
		}
	}
	
	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_webview);

	}

	@Override
	protected void initTitle() {
		tv_titlebar_title.setText(R.string.help_disclaimer);
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (wv_info != null) {
			wv_info.removeAllViews();
			wv_info.destroy();
		}
	}
}
