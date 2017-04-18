/**
 * Project Name:  iCam
 * File Name:     AccessDeviceActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2014年10月23日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.R;

/**
 * @ClassName: BaseHelpActivity
 * @Function: 带返回键和标题的基类
 * @Date: 2014年10月23日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public abstract class BaseHelpActivity extends Activity {
	private ImageView btn_titlebar_back;
	protected TextView tv_titlebar_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setViewContent();
		initBaseViews();
		initBaseListeners();
		initTitle();
	}

	/**
	 * @Function 设备布局界面
	 * @author Wangjj
	 * @date 2014年12月19日
	 */
	protected abstract void setViewContent();

	/**
	 * @Function 设置标题
	 * @author Wangjj
	 * @date 2014年10月23日
	 */
	protected abstract void initTitle();

	private void initBaseViews() {
		btn_titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.titlebar_title);
	}

	private void initBaseListeners() {
		btn_titlebar_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int id = v.getId();
				if (id == R.id.titlebar_back) {
					finish();
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_right_out);
				} else {
				}
			}
		});
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
