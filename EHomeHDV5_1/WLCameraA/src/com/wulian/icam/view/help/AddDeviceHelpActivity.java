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
 * @ClassName: AddDeviceHelpActivity
 * @Function: 添加设备帮助页
 * @Date: 2014年12月20日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class AddDeviceHelpActivity extends BaseHelpActivity {

	private PBWebView wv_info;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
	}

	private void initViews() {
		wv_info = (PBWebView) this.findViewById(R.id.wv_info);
		wv_info.setProgerssBar((ProgressBar) findViewById(R.id.pb_loading));
		wv_info.setBackgroundColor(getResources().getColor(R.color.transparent));
		// 加载本地html文档 透明要求:页面透明&控件透明
		String language = Locale.getDefault().getLanguage();
		String country = Locale.getDefault().getCountry();
		String temp = (language + "_" + country).toLowerCase(Locale
				.getDefault());
		String name="add_device";
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
		tv_titlebar_title.setText(R.string.help_add_device);
	};

}
