package cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_89_DoorLock_6;
import cc.wulian.smarthomev5.adapter.SsidListViewPupopAdapter;
import cc.wulian.smarthomev5.fragment.more.wifi.WifiDataManager;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.st_SearchDeviceInfo;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.EagleWifiListEntiy;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.yuantuo.netsdk.TKCamHelper;

public class HawkeyeSettingWifiActivity  extends SimpleFragmentActivity implements Callback,OnClickListener, IRegisterIOTCListener {
	private Button btnconn;
	private TextView tvssid;
	private EditText etkey;
	private String devicesID, goalWifi; //
	private String ssid = null, sspwd = null; // 
	
	private ImageView setkey, imgBack;
	private List<String> wifis = null;// 手机端获取到的wifi
	private List<String> wifiName = null;
	private List<Object> wifi = null;// 用于存放 设备端获取到的wifi
	private boolean mmssid, flag = false;
	private Handler mHandler = new Handler(this);
	private Intent mIntent = null;
	// add by guofeng
	private String gwID;
	 private String deviceID;

	private Context mContext = null;
	byte mode = 1;
	byte enctype = 9;
	private final static String TAG = "IOTCamera";
	private static final String CameraUserName = "admin";
	private static final String CameraPassword = "admin";
	private Context context = HawkeyeSettingWifiActivity.this;

	
	// add by guofeng 这是门锁中鹰眼配网所单独拥有，所以写在这个地方。

	private int getUidNumber = 0;  //获取Wifi设备的次数。
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_RESP:
			Log.e(TAG, "---IOTYPE_USER_IPCAM_SETWIFI_RESP");
			//配置成功，跳转到其他页面等。
			mIntent = new Intent(this, HawkeyeWifiConnSuccessActivity.class);
			mIntent.putExtra("gwID", gwID);
			mIntent.putExtra("devID", deviceID);
			mIntent.putExtra(Config.aimSSID, ssid);
			startActivity(mIntent);
			finish();
			break;
		case HandlerConstant.ERROR:
			mIntent = new Intent(this, HawkeyeWifiConnFailActivity.class);
			startActivity(mIntent);
			finish();
			break;
		
		}
		return false;
	}

	public List<String> getWifiName(List<Map<String, Object>> wifiMap) {
		List<String> wifiName = new ArrayList<String>();
		for (Map<String, Object> map : wifiMap) {
			wifiName.add((String) map.get("wifiname"));
			mode = (byte) map.get("mode");
			enctype = (byte) map.get("enctype");
			System.out.println("aaaaa:" + (String) map.get("wifiname") + mode
					+ enctype);
		}
		return wifiName;
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		stopPlaySurfaceView();
		uninitSDK();
		closeApModel();// 关闭连接
	}

	public void root() {
		setContentView(R.layout.activity_cateye_wifi_configuration);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.post(OpenApModel);
	}

	private final Runnable OpenApModel = new Runnable() {
		@Override
		public void run() {
			startPlaySurfaceView();
		}
	};

	
	public void startPlaySurfaceView() {
		
		ssid = goalWifi.substring(1, goalWifi.length() - 1).trim();
		sspwd = etkey.getText().toString().trim();// wifi 密码
		//一定注意这个地方对设备的处理。deviceID的处理。
	    //tutkUid = devicesID.trim();
	     initSDK();
		 checkWifi();
	}

	private final void checkWifi() {
		while (WifiDataManager.getInstance().getSSID(context).contains("CamAp")) {
			Log.e(TAG, "===当前wifiCamAp===");
			devicesID = mLanSearch();
			if (devicesID == null) {
				getUidNumber++;
				if (getUidNumber == 10) {
					getUidNumber = 0;
					Log.i(TAG, "****************************搜索不到UID的次数超过预定指标");
					Message message = mHandler.obtainMessage();
					message.what  = HandlerConstant.ERROR;
					mHandler.sendMessage(message);
					break;
				}
				
				Log.i(TAG, "****************************搜索不到UID");
				Toast.makeText(HawkeyeSettingWifiActivity.this, "当前wifi搜索不到UID", Toast.LENGTH_LONG).show();
			}else {
				Log.i(TAG, "****************************搜索的UID"+devicesID);
				editor.putString(Config.IS_HAWKEYE_UID, devicesID).commit();
				if (WL_89_DoorLock_6.cHelperHawkeye != null) {
					WL_89_DoorLock_6.cHelperHawkeye.setmUID(devicesID);
				}
				openApModel();
				break;
			}
			
		}
		if (!WifiDataManager.getInstance().getSSID(context).contains("CamAp")) {
			Log.i(TAG, "****************************当前wifi连接的不对");
			Toast.makeText(HawkeyeSettingWifiActivity.this, "当前wifi连接的不对", Toast.LENGTH_LONG).show();
			Message message = mHandler.obtainMessage();
			message.what  = HandlerConstant.ERROR;
			mHandler.sendMessage(message);
		}
		
	}

	private  void openApModel() {
		
		if (WL_89_DoorLock_6.cHelperHawkeye == null) {
			Log.i(TAG, "****************************Camera对象为空");
			return;
		}
		
		WL_89_DoorLock_6.cHelperHawkeye.registerIOTCListener(this);
		Log.e("IOTCamera", "---------------开始连接2");
		Log.e("IOTCamera", "---------------startPlaySurfaceView"+WL_89_DoorLock_6.cHelperHawkeye.isSessionConnected());
		if (!WL_89_DoorLock_6.cHelperHawkeye.isSessionConnected()) {
			Log.e("IOTCamera", "---------------开始连接3");
			// connect camera
			//getMessageAction().sendEmptyMessage(CmdUtil.MESSAGE_PROCESS_SHOW);
			WL_89_DoorLock_6.cHelperHawkeye.connect(devicesID,"admin");
			WL_89_DoorLock_6.cHelperHawkeye.start(Camera.DEFAULT_AV_CHANNEL, CameraUserName, CameraPassword);
		
		
			//获取wifi列表
			WL_89_DoorLock_6.cHelperHawkeye.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_REQ,new byte[]{0});
		
			
			//固件升级的相关信息
			byte[] reserved = new byte[4];
			WL_89_DoorLock_6.cHelperHawkeye.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, reserved);
			
			WL_89_DoorLock_6.cHelperHawkeye.LastAudioMode = 0;// 0:mute,1:listening,2:speaking
			
		}
		
//		if (monitor != null){
//			// 探测Camera是否存在，并渲染视频流
//				monitor.attachCamera(mCamera, mSelectedChannel);
//			}
		
		 //cHelperHawkeye.startShow(mSelectedChannel,true,false);
		
	}

	private final void closeApModel() {
		
		
	}

	@Override
	public void initView() {
		btnconn = (Button) findViewById(R.id.btn_cateye_wifi_configuration_sure);
		tvssid = (TextView) findViewById(R.id.tv_eagle_wifi_ssid);
		etkey = (EditText) findViewById(R.id.et_eagle_wifi_key);
		setkey = (ImageView) findViewById(R.id.iv_wifi_key_set_visiblely);
		imgBack = (ImageView) findViewById(R.id.iv_cateye_titlebar_back);
	}

	@Override
	public void initData() {

		mContext = this;
		goalWifi = getIntent().getStringExtra("ssid");// 目标wifi
		
		gwID =getIntent().getStringExtra("gwID");
		deviceID =getIntent().getStringExtra("devID");
		
		wifis = WifiDataManager.getInstance().getWifiScanResultList();
		etkey.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
     
	}

	
	
	
	
	
	private void initSDK() {

		Log.e("IOTCamera", "---------------初始化TUTK通道");
		TKCamHelper.init();	
		return;
		
	}
	
	
	private void uninitSDK() {
		// call in MainHomeActivity
		Log.i(TAG, "------销毁TUTK通道");
		TKCamHelper.uninit();
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}
	
	
	private void stopPlaySurfaceView() {
		if (WL_89_DoorLock_6.cHelperHawkeye == null) return;
		Log.e("IOTCamera", "---------------暂停断开连接");
		WL_89_DoorLock_6.cHelperHawkeye.unregisterIOTCListener(this);

		if (WL_89_DoorLock_6.cHelperHawkeye.isSessionConnected()) {
			
			WL_89_DoorLock_6.cHelperHawkeye.stop(Camera.DEFAULT_AV_CHANNEL);
			WL_89_DoorLock_6.cHelperHawkeye.disconnect();
			
		}
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_eagle_wifi_ssid:
			ssidPupopWindows(v);
			break;
		case R.id.btn_cateye_wifi_configuration_sure:
			sspwd = etkey.getText().toString().trim();
			if (mmssid) {
				//checkWifi();      //不该再执行这个函数 去获得SSID			
				//配置wifi的。
				Log.i(TAG, "-----------ssid="+ssid+"sspwd"+sspwd);
				WL_89_DoorLock_6.cHelperHawkeye.sendIOCtrl(
						Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(
									ssid.getBytes(), sspwd.getBytes(), mode, enctype));				
				
			}
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
		setText();
	}

	public void setText() {
		tvssid.setText(goalWifi.substring(1, goalWifi.length() - 1));
		String msssid = WifiDataManager.getInstance().getSSID(this);
		if (msssid.contains("CamAp")) {
			mmssid = true;
		} else {
			Toast.makeText(getApplicationContext(), "wifi不对", Toast.LENGTH_LONG).show();
		}
	}

	public void setFlag() {
		if (flag) {
			etkey.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			flag = false;
		} else {
			etkey.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			flag = true;
		}
	}

	public void ssidPupopWindows(View v) {
		SsidListViewPupopAdapter mAdapter = new SsidListViewPupopAdapter(
				mContext, null);
		View mView = LayoutInflater.from(mContext).inflate(
				R.layout.item_list_view_popupwindow, null);
		ListView mListView = (ListView) mView
				.findViewById(R.id.lv_pupop_window);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EagleWifiListEntiy mEntiy = (EagleWifiListEntiy) parent
						.getItemAtPosition(position);
				mode = mEntiy.getMode();
				enctype = mEntiy.getEnctype();
				// 每一项的点击事件
				TextView mTextView = (TextView) view
						.findViewById(R.id.tv_list_pupop_show_ssid);
				tvssid.setText(mTextView.getText().toString());
			}
		});
		final PopupWindow popupWindow = new PopupWindow(mView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
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
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));
		// 设置好参数之后再show
		popupWindow.showAsDropDown(v);
	}	
	
	
	
	
	/*
	 *  这个函数 返回的是最后一个 UID，假如当前网络中 有多个UID则会确定不好使那个设备。
	 * 
	 * 
	 * */
	private String mLanSearch() 
	{
		int[] nArray = new int[1];
		
		IOTCAPIs.IOTC_Search_Device_Start(3000,100);
		String resultString = null;
		while(true)
		{
			st_SearchDeviceInfo[] ab_LanSearchInfo = IOTCAPIs.IOTC_Search_Device_Result(nArray,0);	
			if(nArray[0] < 0)
			{
				Log.i(TAG, "***************************跳出搜索UID--mLanSearch");
				break;
			}	
			for(int i = 0; i < nArray[0];i++){
				
				try 
				{
					Log.i(TAG, "UID = " + i +  " = " + new String(ab_LanSearchInfo[i].UID,0, ab_LanSearchInfo[i].UID.length,"utf-8"));
					resultString = new String(ab_LanSearchInfo[i].UID,0, ab_LanSearchInfo[i].UID.length,"utf-8");
					Log.i(TAG, "****************************将搜索到设备赋值给deviceID");
					Log.i(TAG, "IP " + i +  " = " + new String(ab_LanSearchInfo[i].IP,0, ab_LanSearchInfo[i].IP.length,"utf-8"));
				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				}
				Log.i(TAG, "Port " + i +  " = " + String.valueOf(ab_LanSearchInfo[i].port));
				Log.i(TAG, "****************************");			
			}		
			try {
					Thread.sleep(1000);
			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
		
		return resultString;
	}

	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveIOCtrlData(Camera camera, int avChannel,
			int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub
		if (avIOCtrlMsgType == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_RESP) {
			Log.e(TAG,"--------接收到wifi列表");
        	Log.e(TAG,String.valueOf(data.length));
        	if (data.length > 0) {
//        		wifi = IotUtil.parseWifiList(data);
			}		
		}else if (avIOCtrlMsgType == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP) {
			Log.e(TAG,"--------接收到wifi列表");
        	Log.e(TAG,String.valueOf(data.length));
        	if (data.length > 0) {
        		IotUtil.parseEagleInfo(data);//解析固件版本数据
			}		
		}else if((avIOCtrlMsgType == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_RESP)){
			
			
			Message message = mHandler.obtainMessage();
			message.what  = AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_RESP;
			mHandler.sendMessage(message);
		}
			
	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera camera, int i,
			byte[] abyte0, int j, int k, byte[] abyte1, boolean flag, int l) {
		// TODO Auto-generated method stub
		
	}
	
	
}