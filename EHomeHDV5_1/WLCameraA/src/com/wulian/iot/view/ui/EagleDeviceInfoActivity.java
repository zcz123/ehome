package com.wulian.iot.view.ui;


import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.view.base.SimpleFragmentActivity;

public class EagleDeviceInfoActivity extends SimpleFragmentActivity implements OnClickListener{

	private ImageView mIvBack;
	private TextView mTvTitle,mDeviceId,mDeviceName;
	private String deviceId,deviceName;
	@Override
	public void root() {
		super.root();
		setContentView(R.layout.activity_eagle_deviec_info);
	}
	@Override
	public void initView() {
		mIvBack=(ImageView) findViewById(R.id.iv_cateye_titlebar_back);
		mTvTitle=(TextView) findViewById(R.id.tv_cateye_titlebar_title);
		mDeviceId = (TextView)findViewById(R.id.device_id);
		mDeviceName= (TextView) findViewById(R.id.tv_show_device_name);
	}

	@Override
	public void initData() {
		super.initData();
		Intent dataIntent = getIntent();
		deviceId = dataIntent.getStringExtra(Config.tutkUid);
		deviceName=dataIntent.getStringExtra(Config.eagleName);
		if(deviceId!=null){
			mDeviceId.setText(deviceId);
			mDeviceName.setText(deviceName);
		}

	}

	@Override
	public void onClick(View v) {
		if (v==mIvBack) {
			finish();
		}
	}
	@Override
	public void initEvents() {
		mTvTitle.setText(getResources().getString(R.string.setting_device_desc));
		mIvBack.setOnClickListener(this);
	}
	
	
}
