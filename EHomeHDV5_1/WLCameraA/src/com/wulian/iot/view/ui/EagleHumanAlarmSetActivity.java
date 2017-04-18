package com.wulian.iot.view.ui;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Packet;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.yuantuo.customview.ui.WLToast;

public class EagleHumanAlarmSetActivity extends SimpleFragmentActivity implements OnClickListener{
	
	private ImageView back;
	private TextView title;
	private LinearLayout linHuman0,linHuman1,linHuman2,linHuman3;
	private CheckBox cb0,cb1,cb2,cb3;
	private LinearLayout lin []={linHuman0,linHuman1,linHuman2,linHuman3};
	private CheckBox cb []={cb0,cb1,cb2,cb3}; 
	
	private int sensity=-1;
	
	private byte [] sensitivity={15,5,10,14};
	private int cbId[]={R.id.cb_human_alarm_0,R.id.cb_human_alarm_1,R.id.cb_human_alarm_2,R.id.cb_human_alarm_3};
	private int linId[] ={R.id.lin_human_alarm_0,R.id.lin_human_alarm_1,R.id.lin_human_alarm_2,R.id.lin_human_alarm_3};
	
	private CameraHelper.Observer observer = new CameraHelper.Observer() {

		@Override
		public void avIOCtrlOnLine() {

		}

		@Override
		public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					switch (avIOCtrlMsgType) {
						case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP:
							Log.i("EagleHumanAlarmSetActivity", "data.length=:"+data.length);
							int sensitivity=Packet.byteArrayToInt_Little(data, 4);
							Log.i("EagleHumanAlarmSetActivity", "EagleHumanAlarmSetActivity:==="+sensitivity);
							setCheckboxStatus(sensitivity);
							break;

						case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP:
							int a=Packet.byteArrayToInt_Little(data, 0);
							if (a==0) {
								//success
								Log.i("EagleHumanAlarmSetActivity", "success");
							}else {
								//fail
							}
						default:
							break;
					}
				}
			});
		}

		@Override
		public void avIOCtrlMsg(int resCode, String method) {

		}
	};

	@Override
	public void root() {
		setContentView(R.layout.activity_eagle_human_alarm);
	}
	@Override
	public void initData() {
		cameaHelper.attach(observer);
		IotSendOrder.sendGetEagleSensitivity(cameaHelper.getmCamera());
		getSp();
	}
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void initView() {
		for (int i = 0; i < cb.length; i++) {
			cb[i]=(CheckBox) findViewById(cbId[i]);
			lin[i]=(LinearLayout) findViewById(linId[i]);
		}
		back=(ImageView) findViewById(R.id.iv_cateye_titlebar_back);
		title=(TextView) findViewById(R.id.tv_cateye_titlebar_title);
		title.setText(R.string.cateye_human_detection_alarm);
	}
	@Override
	public void initEvents() {
		for (int i = 0; i < lin.length; i++) {
			lin[i].setOnClickListener(this);
//			if (i==sensity) {
//				cb[i].setChecked(true);
//			}
			cb[i].setClickable(false);
		}
		back.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		if (v==back) {
			finish();
			return;
		}
		for (int i = 0; i < lin.length; i++) {
			if (v==lin[i]) {
				cb[i].setChecked(true);
				IotSendOrder.sendSetEagleSensitivity(cameaHelper.getmCamera(), sensitivity[i]);
				setSp(i);
			}else {
				cb[i].setChecked(false);
			}
		}
	}
	private void setSp(int a){
		editor.putInt(Config.SENSITIVITY_EAGLE, a).commit();
	}
	
	private void getSp(){
		sensity=sharedPreferences.getInt(Config.SENSITIVITY_EAGLE, -1);
		Log.i("EagleHumanAlarmSetActivity", "取到的值："+sensity);
//		if (sensity!=-1) {
//			cb[sensity].setChecked(true);
//		}
	}
	private  void setCheckboxStatus(int sensity){
		for (int i = 0; i < sensitivity.length; i++) {
			if (sensitivity[i]==sensity) {
				cb[i].setChecked(true);
				setSp(i);
				return;
			}
		}
		WLToast.showToast(this, "没有设置", 0);
	}
}
