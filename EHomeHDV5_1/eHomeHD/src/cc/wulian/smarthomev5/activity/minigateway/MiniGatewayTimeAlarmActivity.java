package cc.wulian.smarthomev5.activity.minigateway;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.fragment.setting.minigateway.SetNowTimeFragment;

public class MiniGatewayTimeAlarmActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SetNowTimeFragment setNowTimeFragment = new SetNowTimeFragment();

		setNowTimeFragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, setNowTimeFragment).commit();
	}

	@Override
	protected boolean finshSelf() {
		return false;
	}
}