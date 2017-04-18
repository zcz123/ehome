package cc.wulian.smarthomev5.activity.sxgateway;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.fragment.setting.sxgateway.SXHeartFragment;

public class SXGatewayHeartSettingActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SXHeartFragment fragment = new SXHeartFragment();
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, fragment).commit();
	}

	@Override
	protected boolean finshSelf() {
		return false;
	}
}