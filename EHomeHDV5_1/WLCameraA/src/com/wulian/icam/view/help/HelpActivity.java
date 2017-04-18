/**
 * Project Name:  iCam
 * File Name:     AccessDeviceActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2014年10月23日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.help;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.wulian.icam.R;
import com.wulian.icam.view.base.BaseHelpActivity;

/**
 * @Function: 帮助指南
 * @date: 2014年12月18日
 * @author Wangjj
 */
public class HelpActivity extends BaseHelpActivity implements OnClickListener {

	private LinearLayout ll_function_intro;
	private LinearLayout ll_faq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initListeners();
	}

	private void initListeners() {
		ll_function_intro.setOnClickListener(this);
		ll_faq.setOnClickListener(this);

	}

	private void initViews() {
		ll_function_intro = (LinearLayout) this
				.findViewById(R.id.ll_function_intro);
		ll_faq = (LinearLayout) this.findViewById(R.id.ll_faq);

	}

	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_help);

	}

	@Override
	protected void initTitle() {
		tv_titlebar_title
				.setText(getResources().getString(R.string.help_guide));

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_function_intro) {
			startActivity(new Intent(this, FunctionIntroActivity.class));
		} else if (id == R.id.ll_faq) {
			startActivity(new Intent(this, FaqActivity.class));
		}
	}

}
