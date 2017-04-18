package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;

public class DifferentialSettingActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		DifferentialSettingFragment fragment =  new DifferentialSettingFragment();
		fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content,fragment).commit();
	}
	@Override
	protected boolean finshSelf() {
		return false;
	}
	
	@Override
	public boolean fingerRightFromLeft() {
		return false;
	}
	
}
