package cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.view.base.SimpleFragmentActivity;
/***
 * 猫眼配网的准备界面
 * @author Administrator
 */
public class HawkeyeReadyConnectionActivity extends SimpleFragmentActivity implements OnClickListener{
	private Button btnok;
	
	private ImageView titlebarBack;
	 private Context mContext = HawkeyeReadyConnectionActivity.this;
	 
	 private String gwID;
	 private String deviceID;
	 
	@Override
	public void root() {
		setContentView(R.layout.activity_wifi_caieye_connection_ready);
	}
	
	@Override
	public void initView() {
		btnok=(Button) findViewById(R.id.btn_cateye_connection_sure);
		titlebarBack = (ImageView)findViewById(R.id.iv_cateye_titlebar_back);
	}
	@Override
	public void initData() {		
		//开始配置就必须将相关参数设置为false，然后再做其他操作。
		gwID =getIntent().getStringExtra("gwID");
		deviceID =getIntent().getStringExtra("devID");
		editor.putString(Config.IS_SET_APWIFI, "false").commit();
		
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
			Intent it=new Intent(mContext, null);
			
			it.putExtra("gwID", gwID);
			it.putExtra("devID", deviceID);
			startActivity(it);
			finish();
			break;
		case R.id.iv_cateye_titlebar_back:
			finish();
			break;
		}
	}
}
