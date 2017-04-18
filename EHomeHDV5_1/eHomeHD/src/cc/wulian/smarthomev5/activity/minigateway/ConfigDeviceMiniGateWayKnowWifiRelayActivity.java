package cc.wulian.smarthomev5.activity.minigateway;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cc.wulian.smarthomev5.R;

public class ConfigDeviceMiniGateWayKnowWifiRelayActivity extends Activity {
	private ImageView bt_title_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setViewContent();
		initView();
		initData();

	}

	protected void setViewContent() {
		setContentView(R.layout.device_mini_wifi_relay);
	}

	private void initView() {
		bt_title_back = (ImageView) findViewById(R.id.titlebar_back);
	}

	private void initData() {
		if (bt_title_back != null) {
			bt_title_back.setOnClickListener(getLeftClick());
		}
	}

	protected OnClickListener getLeftClick() {
		return new OnClickListener() {
			public void onClick(View v) {
				ConfigDeviceMiniGateWayKnowWifiRelayActivity.this.finish();
			}
		};
	}

	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			finish();
		}

	}
}
