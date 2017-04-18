package cc.wulian.smarthomev5.activity.iotc.config;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.bean.IOTCDevConfigWifiPojo;
import com.wulian.iot.view.base.SimpleFragmentActivity;

import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;

/***
 * 猫眼配网的准备界面
 * @author Administrator
 */
public class IOTCDevConfigActivity extends SimpleFragmentActivity implements OnClickListener{
	private IOTCDevConfigWifiPojo iotcDevConfigWifiPojo = null;
	private Button btnok;
	private String door_89_deviceId;
	private ImageView titlebarBack;
	private ImageView reaydImgView;
	private TextView readyTxtView;
	private int entryMode = -1;//进入模式 0鹰眼 1门锁
	public static String WIFI_CONFIG_TYPE = "entryMode";
	public static String DOOR_89_DEVICEID = "device";
	private Context mContext = IOTCDevConfigActivity.this;
	public static final int DOOR_DIS_NETWORK = 1;
	public static final int EAGLE_DIS_NETWORK = 0;
	public static final String DEVICE_CONFIG_WIFI_POJO = "DEVICE_CONFIG_WIFI_POJO";
	@Override
	public void root() {
		setContentView(R.layout.activity_wifi_caieye_connection_ready);
	}
	@Override
	public void initView() {
		btnok=(Button) findViewById(R.id.btn_cateye_connection_sure);
		titlebarBack = (ImageView)findViewById(R.id.iv_cateye_titlebar_back);
		reaydImgView	 = (ImageView)findViewById(R.id.iot_ready_img);
		readyTxtView = (TextView)findViewById(R.id.iot_ready_title);
	}
	@Override
	public void initData() {
		setEntryMode(getIntent().getIntExtra(WIFI_CONFIG_TYPE, -1));
		updateUi(getEntryMode());
	}
	private void updateUi(int what){
		switch (what){
			case EAGLE_DIS_NETWORK:
				Log.i(TAG,"EAGLE_DIS_NETWORK");
				eagleUi();
				break;
			case DOOR_DIS_NETWORK:
				Log.i(TAG,"DOOR_DIS_NETWORK");
				door89Ui();
				break;
			default:// 异常
				Log.i(TAG,"NOT_DIS_TYPE");
				break;
		}
	}
	private void eagleUi(){
		reaydImgView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cat_eyea_camera));
		readyTxtView.setText(mContext.getResources().getString(R.string.cateye_wifiSetting_hint));
	}
	private void door89Ui(){
		reaydImgView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.door_look_camera));
		readyTxtView.setText(mContext.getResources().getString(R.string.smartLock_wifiSetting_hint));
		door_89_deviceId =getIntent().getStringExtra(IOTCDevConfigActivity.DOOR_89_DEVICEID);
	}
	@Override
	public void initEvents() {
		btnok.setOnClickListener(this);
		titlebarBack.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_cateye_connection_sure:
				Config.isEagleNetWork = false;
				Intent it=new Intent(mContext, IOTCDevCKWifiActivity.class);
				iotcDevConfigWifiPojo  = new IOTCDevConfigWifiPojo();
				iotcDevConfigWifiPojo.setConfigWifiType(entryMode);
				if (iotcDevConfigWifiPojo.getConfigWifiType()==DOOR_DIS_NETWORK){
					iotcDevConfigWifiPojo.setDoor_89_deviceId(door_89_deviceId);
				}
				it.putExtra(DEVICE_CONFIG_WIFI_POJO,iotcDevConfigWifiPojo);
				startActivity(it);
				finish();
				break;
			case R.id.iv_cateye_titlebar_back:
				JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.pWebview, SmarthomeFeatureImpl.callbackid,
						"-2", JsUtil.OK,false);
				finish();
				break;
		}
	}
	public void setEntryMode(int entryMode) {
		this.entryMode = entryMode;
	}

	public int getEntryMode() {
		return entryMode;
	}
}
