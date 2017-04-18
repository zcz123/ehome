/**
 * Project Name:  iCam
 * File Name:     V2AddDeviceActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年7月24日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.icam.view.device.config;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.barcode.decode.CaptureActivity;
import com.wulian.icam.R;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * @ClassName: AddDeviceFirstPageActivity
 * @Function:  V2添加摄像机
 * @Date:      2015年7月24日
 * @author     Puml
 * @email      puml@wuliangroup.cn
 */
public class AddDeviceFirstPageActivity extends BaseFragmentActivity implements OnClickListener{
	private Button btn_start;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initView();
		initData();
		setListener();
	}
	
	private void initView() {
		btn_start=(Button) findViewById(R.id.btn_start);
	}
	
	private void initData() {
		
	}
	
	private void setListener() {
		btn_start.setOnClickListener(this);
	}
	
	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_add_device_first_page);
	}
	
	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.config_add_camera);
	}
	
	@Override
	protected OnClickListener getLeftClick() {
		return this;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			finish();
		} else if (id == R.id.btn_start) {
			Intent it=new Intent(this, CaptureActivity.class);
			it.putExtra("isV2BarcodeScan", true);
			startActivity(it);
			finish();
		} else {
		}
	}

}

