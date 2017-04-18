package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import cc.wulian.smarthomev5.activity.EventBusActivity;

public class LocationCountryActivity extends EventBusActivity{

	public static LocationCountryActivity instance = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		Bundle bundle = getIntent().getExtras();
		LocationCountryFragment fragment = new LocationCountryFragment();
		fragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
			
	}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
}
