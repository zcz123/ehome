package cc.wulian.smarthomev5.activity.minigateway;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniGateWayRelaySettingFragment;

public class MiniGatewayRelaySettingActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MiniGateWayRelaySettingFragment fragment = new MiniGateWayRelaySettingFragment();
		Bundle args =getIntent().getBundleExtra("Wifiname_key");
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, fragment).commit();
	}

	@Override
	protected boolean finshSelf() {
		return false;

	}

}
