package cc.wulian.smarthomev5.activity;

import android.os.Bundle;

import cc.wulian.smarthomev5.fragment.setting.gateway.LocationSettingFragment;

public class LocationSettingActivity extends EventBusActivity
{
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		LocationSettingFragment fragment = new LocationSettingFragment();
		fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, fragment)
			.commit();
	}

	@Override
	protected boolean finshSelf() {
		return false;
	}
	
}
