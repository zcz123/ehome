package cc.wulian.smarthomev5.activity.sxgateway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniRouterSettingActivity;

public class SxGatewayRelaySucceedActivity extends Activity implements OnClickListener{
	private ImageView bt_title_back;
	private Button btn_succeed;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_sxgateway_connect_succeed);
		initView();
		initData();

	}

	private void initView() {
		bt_title_back = (ImageView) findViewById(R.id.titlebar_back);
		btn_succeed = (Button) findViewById(R.id.device_sxgateway_relay_connect_succeed_bt);
	}

	private void initData() {
		if (bt_title_back != null) {
			bt_title_back.setOnClickListener(getLeftClick());
		}
		btn_succeed.setOnClickListener(this);
	}

	protected OnClickListener getLeftClick() {
		return new OnClickListener() {
			public void onClick(View v) {
				SxGatewayRelaySucceedActivity.this.finish();
			}
		};
	}
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		if(id == R.id.device_sxgateway_relay_connect_succeed_bt){
			Intent intent = new Intent(SxGatewayRelaySucceedActivity.this,MiniRouterSettingActivity.class);
			startActivity(intent);
			finish();
		}
		
	}
}
