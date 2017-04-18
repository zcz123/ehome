package cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye;




import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_89_DoorLock_6;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.fragment.more.wifi.WifiDataManager;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.yuantuo.customview.ui.WLToast;
public class HawkeyeWifiConnSuccessActivity extends SimpleFragmentActivity implements OnClickListener,Handler.Callback{
	
	private String deviceId = null,devicePasswd ="admin",aimSsid = null;
	private Button okBtn;
	private Context mContext = HawkeyeWifiConnSuccessActivity.this;

	private String gwID;
	 private String deviceID;
	 private String TAG = "IOTCamera";
	@Override
	public void root() {
		setContentView(R.layout.activity_eagle_setting_wifi_success);
	}
	@Override
	public void initData() {
		//获取设备Uid和设备密码
		//deviceId = getIntent().getStringExtra(Config.tutkUid);//用户名
		//devicePasswd = getIntent().getStringExtra(Config.tutkPwd);//密码		
		deviceId = sharedPreferences.getString(Config.IS_HAWKEYE_UID, "false");
		if (deviceId.equals("false")) {
			Log.i(TAG, "-----------UID错误");
			return;
		}
		aimSsid = getIntent().getStringExtra(Config.aimSSID);	
		gwID =getIntent().getStringExtra("gwID");
		deviceID =getIntent().getStringExtra("devID");
		/**绑定设备*/
		if(deviceId!=null&&devicePasswd!=null&&aimSsid!=null){
			Log.e(TAG, aimSsid);
			Log.e(TAG, deviceId);
			editor.putString(Config.IS_SET_APWIFI, "true").commit();
			 if(checkWIfi()){
				Log.i(TAG, "--------------已经是目标wifi不需要切换");
				
			 }else {
				 WLToast.showToast(mContext,getResources().getString(R.string.eagle_cut_ssid),Toast.LENGTH_SHORT);
				 conAimSsid();
				 Log.e(TAG, "切换WIFI中请稍后");
			}	
		}
		
		//程序睡眠30秒
		for(int i = 0; i < 30; i++){
            try {
                Thread.sleep(1000);//睡眠1秒。循环300次就是300秒也就是五分钟
                Log.i(TAG, "-------------正在转换wifi成功"+i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		
		okBtn.setClickable(true);
		
		
	}
	
	@Override
	public void initView() {
		okBtn = (Button)findViewById(R.id.okBtn);
		okBtn.setClickable(false);
	}
	@Override
	public void initEvents() {
		okBtn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.okBtn: 
				 Intent intent = new Intent(mContext, DeviceDetailsActivity.class);				 
				 intent.putExtra("gwID", gwID);
				 intent.putExtra("devID", deviceID);
				 startActivity(intent);
				 this.finish();							
			break;
		}
	}
	private final boolean  checkWIfi(){
	     	Log.e(TAG, WifiDataManager.getInstance().getSSID(mContext).replace("\"",""));
		    return WifiDataManager.getInstance().getSSID(mContext).replace("\"","").trim().equals(aimSsid);
	}
	private final void conAimSsid(){
		WifiDataManager.getInstance().connectWifi(aimSsid, mContext);
	}
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
}
