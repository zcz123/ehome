package cc.wulian.smarthomev5.activity.iotc.config;
import com.wulian.icam.utils.WifiAdmin;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.IOTCDevConfigWifiPojo;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.widght.DialogRealize;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.netsdk.TKCamHelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.iotc.res.IOTCDevConfigFailActivity;
import cc.wulian.smarthomev5.fragment.device.DeviceDetailsFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 搜索猫眼 热点 界面
 * @author syf
 *
 */
public class IOTCDevCKWifiActivity extends SimpleFragmentActivity implements Callback,OnClickListener{
	private Button setConn;//去连接
	private TextView wifiName;
	private final Handler mHandler = new Handler(this);
	private ImageView titleBack;
	private WifiAdmin wifiAdmin = null;
	private IOTCDevCKWifiActivity instance = null;
	private IOTCDevConfigWifiPojo iotcDevConfigWifiPojo = null;
	private String tutkUid,aimSSid;
	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
			case HandlerConstant.SUCCESS:
				Log.e(TAG, "find appoint wifi");
				DialogRealize.unInit().dismissDialog();
				jumpSettingWifi();
				break;
			case HandlerConstant.ERROR:
				DialogRealize.unInit().dismissDialog();
				jumpFailactivity();
				break;
		}
		return false;
	}
	@Override
	public void root() {
		this.setContentView(R.layout.activity_cateye_connect_cramera);
		instance = this;
	}
	@Override
	public void initView() {
		titleBack = (ImageView)findViewById(R.id.iv_cateye_titlebar_back);
		setConn=(Button) findViewById(R.id.btn_set_wifi_conn);
		wifiName=(TextView) findViewById(R.id.tv_show_wifi_name);
	}
	@Override
	public void initEvents() {
		titleBack.setOnClickListener(this);
		setConn.setOnClickListener(this);
	}
	private void jumpSettingWifi(){
		Intent mIntent = new Intent(this, IOTCDevSettingWifiActivity.class);
		iotcDevConfigWifiPojo.setTutkUid(tutkUid);
		iotcDevConfigWifiPojo.setAimSSid(aimSSid);
		mIntent.putExtra(IOTCDevConfigActivity.DEVICE_CONFIG_WIFI_POJO,iotcDevConfigWifiPojo);
		startActivity(mIntent);
		if (iotcDevConfigWifiPojo.getConfigWifiType() == IOTCDevConfigActivity.DOOR_DIS_NETWORK){
			DeviceDetailsFragment.isJionNetwork=true;
		}
		finish();
	}
	private void jumpFailactivity(){
		Intent mIntent = new Intent(this, IOTCDevConfigFailActivity.class);
		startActivity(mIntent);
		finish();
	}
	private void initWifiAdamin(){
		wifiAdmin = new WifiAdmin(instance);
		if(wifiAdmin!=null){
			aimSSid = wifiAdmin.getSSID();
		}
	}
	private void checkDisNetType(){
		iotcDevConfigWifiPojo = (IOTCDevConfigWifiPojo) getIntent().getSerializableExtra(IOTCDevConfigActivity.DEVICE_CONFIG_WIFI_POJO);
		if(iotcDevConfigWifiPojo==null){
			mHandler.sendEmptyMessage(HandlerConstant.ERROR);
			return;
		}
		Log.i(TAG,iotcDevConfigWifiPojo.getConfigWifiType()==IOTCDevConfigActivity.DOOR_DIS_NETWORK?"DOOR_DIS_NETWORK":"EAGLE_DIS_NETWORK");
	}
	@Override
	public void initData() {
		Log.i(TAG, "initData");

		TaskExecutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						checkDisNetType();
						initWifiAdamin();
						return null;
					}
				}.execute();
			}
		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopPlaySurfaceView();
		instance = null;
	}
	public void startPlaySurfaceView() {
		DialogRealize.init(instance).showDiglog();
		startObtainThread();
	}
	public void stopPlaySurfaceView() {
		if(obtainTutkUidThread!=null){
			obtainTutkUidThread.stopThread();
			obtainTutkUidThread = null;
		}
	}
	private void startObtainThread(){
		obtainTutkUidThread = new ObtainTutkUidThread();
		obtainTutkUidThread.start();
	}
	private ObtainTutkUidThread obtainTutkUidThread = null;
	private class ObtainTutkUidThread extends Thread{
		private boolean isRunning = false;
		int i=0;
		public void stopThread(){
			isRunning = false;
		}
		@Override
		public void run() {
			isRunning = true;
			while (isRunning){
					if ((tutkUid = IotSendOrder.findTutkUidByWifi()) != null) {
						if (tutkUid.length() == 20) {
							Log.i(TAG, "===tutkUid(" + tutkUid + ")===");
							mHandler.sendEmptyMessage(HandlerConstant.SUCCESS);
							stopThread();
						}
					}
					i++;
				if (i>=20){
					isRunning=false;
					mHandler.sendEmptyMessage(HandlerConstant.ERROR);
					stopThread();
				}

			}
		}
	}
	/**检查wifi*/
	private void checkWifi() {
		if(wifiAdmin!=null) {
			if (wifiAdmin.getSSID().contains("CamAp_")) {
				startPlaySurfaceView();
				return;
			}
			showDialogForNoWifi();
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.iv_cateye_titlebar_back:
				Config.isEagleNetWork = true;
				finish();
				break;
			case R.id.btn_set_wifi_conn:
				checkWifi();
				break;
		}
	}
	/**没有连接到目标wifi*/
	private void showDialogForNoWifi(){
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle("");
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(
				R.layout.device_door_lock_setting_account_dynamic, null);
		TextView textView = (TextView) view
				.findViewById(R.id.device_new_door_lock_account_dynamic_textview);
		textView.setText(getResources().getString(com.wulian.icam.R.string.smartLock_switch_to_CamAp));
		builder.setContentView(view);
		builder.setPositiveButton(getResources().getString(R.string.more_i_see));
		builder.setNegativeButton(null);
		final WLDialog mMessageDialog = builder.create();
		mMessageDialog.show();

		builder.setListener(new WLDialog.MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
					}
					@Override
					public void onClickNegative(View contentViewLayout) {
					}
				});
	}
}
