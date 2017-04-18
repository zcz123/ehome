/**
 * Project Name:  iCam
 * File Name:     FaqActivity.java
 * Package Name:  com.wulian.icam.view.help
 * @Date:         2014年12月19日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.help;

import java.util.Locale;
import java.util.TimeZone;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.wulian.icam.R;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseHelpActivity;
import com.wulian.icam.view.widget.PBWebView;

/**
 * @ClassName: FaqActivity
 * @Function: 常见问题
 * @Date: 2014年12月19日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class FaqActivity extends BaseHelpActivity {

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
		Utils.sysoInfo(wv_info.getSettings().getUserAgentString());
		// m1 note: Mozilla/5.0 (Linux; Android 4.4.4; m1 note Build/KTU84P)
		// AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0
		// Mobile Safari/537.36

		// mx4: Mozilla/5.0 (Linux; Android 4.4.2; zh-cn; MX4 Build/KOT49H)
		// AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0
		// Mobile Safari/537.36
		// 三星、红米note

		Utils.sysoInfo(TimeZone.getDefault().getDisplayName(false,
				TimeZone.LONG));// 中国标准时间

		Utils.sysoInfo(TimeZone.getDefault().getDisplayName(false,
				TimeZone.SHORT));// GMT+08:00

		Utils.sysoInfo(TimeZone.getDefault().getID());// Asia/Shanghai

		Utils.sysoInfo(Locale.getDefault().toString());// zh_CN
		String language = Locale.getDefault().getLanguage();
		String country = Locale.getDefault().getCountry();
		String temp = (language + "_" + country).toLowerCase(Locale
				.getDefault());
		String name="faq";
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
		tv_titlebar_title.setText(R.string.help_faq);
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
