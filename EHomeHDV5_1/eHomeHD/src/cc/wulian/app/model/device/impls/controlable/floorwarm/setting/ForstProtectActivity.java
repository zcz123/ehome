package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;

import android.os.Bundle;

import cc.wulian.smarthomev5.activity.EventBusActivity;

public class ForstProtectActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		ForstProtectFragment fragment = new ForstProtectFragment();
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
