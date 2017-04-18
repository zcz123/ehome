package com.wulian.iot.view.device.setting;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.yuantuo.customview.ui.WLToast;
import android.os.Handler;
import android.os.Message;
public class SetProtectActivity extends SimpleFragmentActivity implements OnClickListener,Handler.Callback,CameraHelper.Observer {
	private LinearLayout linSetAera;
	private TextView btStartProtect;
	private TextView isSet;
	private ImageView titlebar_back;
	public static final int REQUESTCODE_MOVE_AREA = 2;
	private String spMoveArea;
	private IOTCameraBean cInfo = null;
	private int switching = -1;
	private String[] fences = null;

	public void setSwitching(int switching) {
		this.switching = switching;
	}

	public int getSwitching() {
		return switching;
	}

	@Override
	public void avIOCtrlOnLine() {

	}
	@Override
	public void avIOCtrlDataSource(byte[] data, int avIOCtrlMsgType) {
		switch (avIOCtrlMsgType){
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP:
				dismissBaseDialog();
				animationExit();
				break;
		}
	}
	@Override
	public void avIOCtrlMsg(int resCode,String method) {

	}
	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
	
	
	@Override
	public void root() {
		setContentView(R.layout.activity_protection_setting);
	}
	@Override
	public void initView(){
		((TextView) findViewById(R.id.titlebar_title)).setText(R.string.protection_setting);
		titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
		linSetAera = (LinearLayout) findViewById(R.id.lin_area_protect);
		btStartProtect = (TextView) findViewById(R.id.bt_start_protect);
		isSet = (TextView) findViewById(R.id.tv_is_set);
	}
	@Override
	public void initEvents() {
		titlebar_back.setOnClickListener(this);
		linSetAera.setOnClickListener(this);
		btStartProtect.setOnClickListener(this);
	}

	@Override
	public void initData() {
		if ((cInfo = (IOTCameraBean) getIntent().getSerializableExtra(Config.deskBean)) == null) {
			return;
		}
		if (cameaHelper != null) {
			cameaHelper.attach(this);
		}
		upMoveAreaButtonUi();
	}

	private void upMoveAreaButtonUi() {
		if ((fences = getIntent().getStringArrayExtra("fences")).length > 0) {
			if ((spMoveArea = fences[3]).equals("")) {
				spMoveArea = ";";
			}
			if (spMoveArea.split(";").length <= 0) {
				isSet.setText(getResources().getString(R.string.protect_not_set));
				btStartProtect.setText(R.string.protect_start);
				setSwitching(SetCameraActivity.MOTION_OPEN);
			} else {
				isSet.setText(spMoveArea.split(";").length + getResources().getString(R.string.protect_areas));
				String strSwitch = getIntent().getStringExtra("strSwitch");
				if (strSwitch.equals("open")) {
					stopProtect();
				} else if (strSwitch.equals("close")) {
					startProtect();
				}
			}
			return;
		}
	}

	private void startProtect() {
		btStartProtect.setText(R.string.protect_start);
		setSwitching(SetCameraActivity.MOTION_OPEN);
	}
	private void stopProtect() {
		btStartProtect.setText(R.string.protect_stop);
		setSwitching(SetCameraActivity.MOTION_CLOSE);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG,"onActivityResult");
		switch (requestCode) {
			case REQUESTCODE_MOVE_AREA:
				if (resultCode == RESULT_OK) {
					spMoveArea = data.getStringExtra("area");
					Log.i(TAG, "spMoveArea(" + spMoveArea + ")");
					if (spMoveArea != null && !spMoveArea.equals("")) {
						isSet.setText(spMoveArea.split(";").length+ getResources().getString(R.string.protect_areas));
						btStartProtect.setText(R.string.protect_start);
						setSwitching(SetCameraActivity.MOTION_OPEN);
					}
					break;
				}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(cameaHelper!=null){
			cameaHelper.detach(this);
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG,"onSaveInstanceState");
		outState.putString("area", spMoveArea);
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG,"onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
		spMoveArea = savedInstanceState.getString("area");
	}
	private void jumpProtectAreaActivity(){
		startActivityForResult((new Intent(SetProtectActivity.this, ProtectAreaActivity.class)).putExtra("type", REQUESTCODE_MOVE_AREA).putExtra("area", spMoveArea).putExtra("gwid", cInfo.getGwId()),REQUESTCODE_MOVE_AREA);
	}
	@Override
	public void onClick(View v) {
		if(v == titlebar_back){
			animationExit(); 
		} else if(v == linSetAera){
			jumpProtectAreaActivity();
		} else if(v == btStartProtect){
			senFence();
		}
	}
	private void senFence(){
		if (spMoveArea.split(";").length > 0) {
			showBaseDialog();
			IotSendOrder.sendMotionDetection(cameaHelper.getmCamera(), IotUtil.assemblyMotion(fences, spMoveArea, getSwitching()));
			return;
		}
		WLToast.showToast(this, getResources().getString(R.string.protect_not_set), Toast.LENGTH_SHORT);
	}
	public static class MotionDetectionPojo{
		public MotionDetectionPojo(int switching, int sensitivity, int[] area, int defenceused, int week, byte[] moveTime) {
			this.switching = switching;
			this.sensitivity = sensitivity;
			this.area = area;
			this.defenceused = defenceused;
			this.week = week;
			this.moveTime = moveTime;
		}

		private int switching;
		private int sensitivity;
		private int[]area;
		private int defenceused;
		private int week;
		private byte[] moveTime;

		public int getSwitching() {
			return switching;
		}

		public void setSwitching(int switching) {
			this.switching = switching;
		}

		public int getSensitivity() {
			return sensitivity;
		}

		public void setSensitivity(int sensitivity) {
			this.sensitivity = sensitivity;
		}

		public int[] getArea() {
			return area;
		}

		public void setArea(int[] area) {
			this.area = area;
		}

		public int getDefenceused() {
			return defenceused;
		}

		public void setDefenceused(int defenceused) {
			this.defenceused = defenceused;
		}

		public int getWeek() {
			return week;
		}

		public void setWeek(int week) {
			this.week = week;
		}

		public byte[] getMoveTime() {
			return moveTime;
		}

		public void setMoveTime(byte[] moveTime) {
			this.moveTime = moveTime;
		}
	}
}
