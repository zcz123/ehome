package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import cc.wulian.smarthomev5.activity.EventBusActivity;

public class LocationCityActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		LocationCityFragment fragment = new LocationCityFragment();
		fragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
			
	}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
}
