package cc.wulian.smarthomev5.activity.minigateway;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniHeartFragment;

public class MiniGatewayHeartSettingActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MiniHeartFragment fragment = new MiniHeartFragment();
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, fragment).commit();
	}

	@Override
	protected boolean finshSelf() {
		return false;
	}
}