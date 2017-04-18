package cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;

import com.wulian.icam.R;
import com.wulian.iot.view.base.SimpleFragmentActivity;

public class HawkeyeWifiConnFailActivity extends SimpleFragmentActivity{

	private TextView   textView;      //  back_textview      恢复出厂设置和跳转到配锁界面。
	private Button   sureButton;       //lan_restore_factory
	
	
	private String gwID;
	private String deviceID;
	 
	@Override
	public void root() {
		super.root();
		setContentView(R.layout.activity_eagle_setting_wifi_fail);
		
		gwID =getIntent().getStringExtra("gwID");
		deviceID =getIntent().getStringExtra("devID");
		
		initViewWifi();	
		 
	}
	
	private void initViewWifi() {
		// TODO Auto-generated method stub
		textView = (TextView)findViewById(R.id.back_textview);	
		Log.i("IOTCamera", "------------修改为eye_setwifi_fail_lock");
		textView.setText(getResources().getString(R.string.eye_setwifi_fail_lock));			
		textView.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("IOTCamera", "------------修改为beidiji");				
					Intent it=new Intent(HawkeyeWifiConnFailActivity.this, DeviceDetailsActivity.class);
					Log.i("IOTCamera", "------------修改为beidiji"+gwID+"---"+deviceID);	
					it.putExtra("gwID", gwID);
					it.putExtra("devID", deviceID);				
					startActivity(it);
					finish();			
			}
		});
		
		sureButton = (Button)findViewById(R.id.eye_retry_button); 
		sureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("IOTCamera", "------------重新调回到配置界面");				
				Intent it=new Intent(HawkeyeWifiConnFailActivity.this, HawkeyeReadyConnectionActivity.class);
				startActivity(it);
				finish();								
			}			
		});
	}

	
	
	
	
	
}