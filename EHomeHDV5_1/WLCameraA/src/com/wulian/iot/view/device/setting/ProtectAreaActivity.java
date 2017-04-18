package com.wulian.iot.view.device.setting;
import com.wulian.iot.Config;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.utils.DateUtil;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.MonitorArea;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.protect.SafeProtectSettingActivity;
import com.wulian.icam.view.widget.CustomOverlayView;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipProfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ProtectAreaActivity extends SimpleFragmentActivity implements OnClickListener{
	private SipProfile account;
	String deviceSipAccount;// 设备sip账号
	String deviceControlUrl;// 设备控制sip地址
	String deviceCallUrl;// 设备呼叫sip地址
	CustomOverlayView cov;
	RelativeLayout rl_bg;
	int seq = 1;
	Button btn_sure, btn_reset;
	int type;
	String area;
	String gwidString;
	Handler myHandler;
	
	private  IOTCameraBean cInfo = null;
	
    @Override
    public void root() {
	   super.root();
	   setContentView(R.layout.activity_detection_area);
    }
    @SuppressLint("NewApi")
	public void initView() {
		super.initView();
		cov = (CustomOverlayView) findViewById(R.id.cov);
		btn_sure = (Button) findViewById(R.id.btn_sure);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		btn_reset.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
		rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
		
		//初始化获取data要放在前面 因为后面setImage要用到， 所以提前勿动。
        cInfo = (IOTCameraBean) getIntent().getSerializableExtra(Config.deskBean);	
		type = getIntent().getIntExtra("type", 1);
		area = getIntent().getStringExtra("area");
		gwidString = getIntent().getStringExtra("gwid");
		
		setImage();
	}
    private Drawable getImage(String base64){
    	Bitmap bitmap = IotUtil.getBitmap(base64);
    	if(bitmap!=null){
    		return IotUtil.bitmapToDrawble(bitmap,this);
    	}
    	return null;
    }
	@SuppressLint("NewApi")
	private void setImage(){
		String base64 = null;
		//cInfo.getGwId() 
		Log.i("IOTCamera", "-------------setImage"+gwidString);
    	if((base64=sharedPreferences.getString(gwidString + Config.SNAPSHOT,null)) != null){
    		rl_bg.setBackground(getImage(base64));
    	}
    }
	public void initData() {
		super.initData();
		//sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
		//add by guofeng 无论有无设置区域，只要进入这个设置页面都将标志为置为false.
		editor.putString(Config.IS_SET_PROTECT, "flase").commit();
		
		Log.i("IOTCamera", "-------------initData"+gwidString);
		
		myHandler = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				switch (msg.what) {
				case 1:
					cov.restoreMonitorArea(area.split(";"));
					break;
				case 2:
					cov.restoreMonitorArea(area.split(";"));
					break;
				}
			}
		};
		switch (type) {
		case SafeProtectSettingActivity.REQUESTCODE_MOVE_AREA:
			myHandler.sendEmptyMessageDelayed(1, 500);
			break;
		case SafeProtectSettingActivity.REQUESTCODE_COVER_AREA:
			MonitorArea first = cov.mas.getFirst();
			cov.mas.clear();
			cov.mas.add(first);
			myHandler.sendEmptyMessageDelayed(2, 500);
			break;
		}

	}

	public void sure() {
		if (cov.getPointResult().length >= 0) { 
			for (String i : cov.getPointResult()) {
				Utils.sysoInfo(i);
			}
			Intent it = new Intent();
			it.putExtra("area", cov.getPointResultString());
			setResult(RESULT_OK, it);
			this.finish();
		} else {
			CustomToast.show(this, R.string.protect_detection_no_field);
		}
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_reset) {
			cov.reset();
		} else if (id == R.id.btn_sure) {
			sure();
		} else {
		}

	}
}
