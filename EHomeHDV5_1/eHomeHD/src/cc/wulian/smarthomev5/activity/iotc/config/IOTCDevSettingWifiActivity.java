package cc.wulian.smarthomev5.activity.iotc.config;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import cc.wulian.smarthomev5.adapter.SsidListViewPupopAdapter;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.wulian.icam.R;
import com.wulian.icam.utils.WifiAdmin;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.EagleWifiListEntiy;
import com.wulian.iot.bean.IOTCDevChPojo;
import com.wulian.iot.bean.IOTCDevConfigWifiPojo;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.yuantuo.customview.ui.WLToast;

import java.util.List;

public class IOTCDevSettingWifiActivity extends SimpleFragmentActivity implements Handler.Callback,OnClickListener{
	private Button btnconn;
	private TextView tvssid,settingWifHitTxt;
	private TextView tvLine;
	private LinearLayout inputPassword,showSsid;
	private EditText etkey;
	private ImageView setkey,imgBack,chooseWifi;
	private CheckBox noKey;
	private boolean flag =false;
	private Handler mHandler = new Handler(this);
	private IOTCDevSettingWifiActivity instance = null;
	byte mode = 1;
	byte enctype = 9;
//	private final static String TAG = "nu";
	private WifiAdmin wifiAdmin = null;
	private IOTCDevConfigWifiPojo iotcDevConfigWifiPojo = null;
	private IOTCDevChPojo iotcDevChPojo = null;
	private List<EagleWifiListEntiy>  eagleWifiList=null;
	private CameraHelper.Observer observer = new CameraHelper.Observer() {
		@Override
		public void avIOCtrlOnLine() {
		}
		@Override
		public void avIOCtrlDataSource(byte[] data, int avIOCtrlMsgType) {
			switch (avIOCtrlMsgType){
				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_RESP://wifi 列表
					Log.i(TAG,"IOTYPE_USER_IPCAM_LISTWIFIAP_RESP");
//					for(EagleWifiListEntiy obj:IotUtil.parseWifiList(data)){
//					    Log.i(TAG,obj.getWifiname());
//					}
					if (data!=null)
					eagleWifiList=IotUtil.parseWifiList(data);
					break;
				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_RESP:
					break;
			}
		}
		@Override
		public void avIOCtrlMsg(int resCode, String method) {
		}
	};
	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
	public void root() {
		setContentView(R.layout.activity_cateye_wifi_configuration);
		instance = this;
	}
	private void checkDisNetType(){
		iotcDevConfigWifiPojo = (IOTCDevConfigWifiPojo) getIntent().getSerializableExtra(IOTCDevConfigActivity.DEVICE_CONFIG_WIFI_POJO);
		if(iotcDevConfigWifiPojo==null){
			mHandler.sendEmptyMessage(HandlerConstant.ERROR);
			return;
		}
		Log.i(TAG,iotcDevConfigWifiPojo.getConfigWifiType()==IOTCDevConfigActivity.DOOR_DIS_NETWORK?"DOOR_DIS_NETWORK":"EAGLE_DIS_NETWORK");
	}
	private void obtainData(){
		if(iotcDevConfigWifiPojo.getAimSSid()==null){
			mHandler.sendEmptyMessage(HandlerConstant.ERROR);
			return;
		}
			iotcDevConfigWifiPojo.setAimSSid(iotcDevConfigWifiPojo.getAimSSid().substring(1,iotcDevConfigWifiPojo.getAimSSid().length()-1).trim());
			Log.i(TAG,"AimSSid("+iotcDevConfigWifiPojo.getAimSSid()+")");
		if(iotcDevConfigWifiPojo.getTutkUid()==null){
			mHandler.sendEmptyMessage(HandlerConstant.ERROR);
			return;
		}
		iotcDevConfigWifiPojo.setTutkPwd("admin");
		iotcDevChPojo = new IOTCDevChPojo(iotcDevConfigWifiPojo.getTutkUid(), iotcDevConfigWifiPojo.getTutkPwd(), Camera.IOTC_Connect_ByUID, Config.CAMERA);
	}
	private void updateUi(){
		tvssid.setText(iotcDevConfigWifiPojo.getAimSSid());
		etkey.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		settingWifHitTxt.setText(iotcDevConfigWifiPojo.getConfigWifiType()==IOTCDevConfigActivity.EAGLE_DIS_NETWORK?instance.getResources().getString(R.string.tv_wifi_cateye_configuration_note):null);
	}
	@Override
	public void initData() {
		checkDisNetType();
		obtainData();
		updateUi();
	}
	@Override
	protected void onResume() {
		super.onResume();
		startPlaySurfaceView();
	}
	public void startPlaySurfaceView() {
		startCheckWifi();
	}
	private void startCheckWifi(){
		checkWifiThread = new CheckWifiThread();
		checkWifiThread.start();
	}
	private CheckWifiThread checkWifiThread = null;
	private class CheckWifiThread extends Thread{
		public void stopThread(){
			mIsRunning = false;
		}
		private boolean mIsRunning = true;
		@Override
		public void run() {
			wifiAdmin = new WifiAdmin(instance);
			while (mIsRunning){
				if(wifiAdmin!=null){
					if(wifiAdmin.getSSID().contains("CamAp")){
						Log.e(TAG, "==="+wifiAdmin.getSSID()+"===");
						openApModel();
						stopThread();
					}
				}
			}
		}
	}
	private CameraHelper.IOTCDevConnCallback iotcDevConnCallback = new CameraHelper.IOTCDevConnCallback() {
		@Override
		public void success() {
			cameaHelper.registerstIOTCLiener();
			IotSendOrder.USER_FINDWIFILIST(cameaHelper.getmCamera());
			Log.e(TAG, "===createSessionSuccessfully===");
		}
		@Override
		public void session() {
			Log.i(TAG, "===session===");
			createSessionWaitThread = new CreateSessionWaitThread();
			createSessionWaitThread.start();
		}
		@Override
		public void avChannel() {
			Log.i(TAG, "===createAvIndexFailed===");
			createAvChannelWaitThread = new CreateAvChannelWaitThread();
			createAvChannelWaitThread.start();
		}
	};
	private final void openApModel(){
		Log.e(TAG, "openApModel");
		if(cameaHelper == null){
			cameaHelper = CameraHelper.getInstance(iotcDevChPojo);
			cameaHelper.attach(iotcDevConnCallback);
			cameaHelper.registerstIOTCLiener();
			cameaHelper.attach(observer);
		}
		cameaHelper.register();
	}
	private final void closeApModel(){
		Log.e(TAG, "closeApModel");
		destroyWailThread();
		if(cameaHelper != null){
			cameaHelper.detach(observer);
			cameaHelper.detach(iotcDevConnCallback);
//			cameaHelper.unregisterIOTCLiener();
//			cameaHelper.destroyCameraHelper();
		}
//		cameaHelper = null;
	}
	@Override
	public void initView() {
		btnconn=(Button) findViewById(R.id.btn_cateye_wifi_configuration_sure);
		tvssid=(TextView) findViewById(R.id.tv_eagle_wifi_ssid);
		etkey=(EditText) findViewById(R.id.et_eagle_wifi_key);
		setkey=(ImageView) findViewById(R.id.iv_wifi_key_set_visiblely);
		imgBack = (ImageView)findViewById(R.id.iv_cateye_titlebar_back);
		settingWifHitTxt = (TextView)findViewById(R.id.iot_setting_wifi_hint);
		inputPassword=(LinearLayout) findViewById(R.id.ll_wifi_set_input_password);
		tvLine=(TextView) findViewById(R.id.tv_line_no_password);
		noKey= (CheckBox) findViewById(R.id.cb_no_password);
		chooseWifi= (ImageView) findViewById(R.id.iv_wifi_choose);
		showSsid= (LinearLayout) findViewById(R.id.ll_eagle_wifi_ssid);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.ll_eagle_wifi_ssid:
				if (eagleWifiList==null){
					WLToast.showToast(getApplicationContext(),getResources().getString(R.string.cateye_no_wifi_found_hint),0);
				}else {
					ssidPupopWindows(v);
				}
				break;
			case R.id.btn_cateye_wifi_configuration_sure:
				checkPassWord();
				break;
			case R.id.iv_wifi_key_set_visiblely:
				setFlag();
				break;
			case R.id.iv_cateye_titlebar_back:
				Config.isEagleNetWork = true;
				this.finish();
				break;
		}
	}
	@Override
	public void initEvents() {
		btnconn.setOnClickListener(this);
		tvssid.setOnClickListener(this);
		imgBack.setOnClickListener(this);
		setkey.setOnClickListener(this);
		showSsid.setOnClickListener(this);
		setCheckNoKey();//是否隐藏密码框
	}
	/**判断一下密码的情况在发送命令 */
	public void checkPassWord(){
		String aimPwd = null;
		aimPwd = etkey.getText().toString();
		if (inputPassword.isShown()){
			if(aimPwd.isEmpty()){
				WLToast.showToast(getApplicationContext(), getResources().getString(cc.wulian.smarthomev5.R.string.set_password_not_null_hint), 0);
				return;
			}
			if(aimPwd.length()<8){
				WLToast.showToast(getApplicationContext(), getResources().getString(cc.wulian.smarthomev5.R.string.cateye_wifi_set_password_less), 0);
				return;
			}
		}else {
			aimPwd="";
		}
		if(cameaHelper.checkAvChannel()){
			createConfigPojo(aimPwd);
			jumpEagleCheckConnStatus();
//			IotSendOrder.sendDevConfig(cameaHelper.getmCamera(),iotcDevConfigWifiPojo.getAimSSid(),iotcDevConfigWifiPojo.getAimPwd(), (byte) iotcDevConfigWifiPojo.getEntryMode(), (byte)iotcDevConfigWifiPojo.getConfigDeviceMode());//配置wifi信息

		}

	}
	private void createConfigPojo(String aimPwd){
		iotcDevConfigWifiPojo.setAimPwd(aimPwd);
		iotcDevConfigWifiPojo.setEntryMode(1);
		iotcDevConfigWifiPojo.setConfigDeviceMode(9);
	}
	private void jumpEagleCheckConnStatus(){
		instance.startActivity(new Intent(this,IOTCDevCKConnStatusActivity.class).putExtra(IOTCDevConfigActivity.DEVICE_CONFIG_WIFI_POJO,iotcDevConfigWifiPojo));
		instance.finish();
	}
	/**设置密码*/
	private void setFlag(){
		if (flag) {
			etkey.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			flag=false;
		}else {
			etkey.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			flag=true;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeApModel();//关闭连接
		instance = null;
	}


	public void setCheckNoKey(){
		noKey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					tvLine.setVisibility(View.INVISIBLE);
					inputPassword.setVisibility(View.INVISIBLE);
				}else {
					tvLine.setVisibility(View.VISIBLE);
					inputPassword.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	public void ssidPupopWindows(View v){
		SsidListViewPupopAdapter mAdapter=new SsidListViewPupopAdapter(instance, null);
		View mView=LayoutInflater.from(instance).inflate(R.layout.item_list_view_popupwindow, null);
		ListView mListView=(ListView) mView.findViewById(R.id.lv_pupop_window);
		mAdapter.add(eagleWifiList);
		mListView.setAdapter(mAdapter);
		final PopupWindow popupWindow = new PopupWindow(mView,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				EagleWifiListEntiy mEntiy =(EagleWifiListEntiy)parent.getItemAtPosition(position);
				mode=mEntiy.getMode();
				enctype=mEntiy.getEnctype();
				//每一项的点击事件
				TextView mTextView=	(TextView) view.findViewById(R.id.tv_list_pupop_show_ssid);
				tvssid.setText(mTextView.getText().toString());
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		popupWindow.setTouchable(true);
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});
		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
		// 设置好参数之后再show
		popupWindow.showAsDropDown(v);
	}
	private CreateSessionWaitThread createSessionWaitThread = null;
	private class CreateSessionWaitThread extends Thread{
		private boolean mIsRunning = true;
		public void stopThread(){
			mIsRunning = false;
		}
		@Override
		public void run() {
			mIsRunning = true;
			while (mIsRunning){
				if (cameaHelper.checkSession()) {
					cameaHelper.register();
					mIsRunning = false;
				}
			}
		}
	}
	private CreateAvChannelWaitThread createAvChannelWaitThread = null;
	private class CreateAvChannelWaitThread extends Thread{
		private boolean mIsRunning = true;
		public void stopThread(){
			mIsRunning = false;
		}
		@Override
		public void run() {
			mIsRunning = true;
			while (mIsRunning){
				if (cameaHelper.checkAvChannel()) {
					cameaHelper.register();
					mIsRunning = false;
				}
			}
		}
	}
	private void destroyWailThread(){
		if(checkWifiThread!=null){
			checkWifiThread.stopThread();
			checkWifiThread = null;
		}
		if(createSessionWaitThread!=null){
			createSessionWaitThread.stopThread();
			createSessionWaitThread = null;
		}
		if(createAvChannelWaitThread!=null){
			createAvChannelWaitThread.stopThread();
			createAvChannelWaitThread = null;
		}
	}
}
