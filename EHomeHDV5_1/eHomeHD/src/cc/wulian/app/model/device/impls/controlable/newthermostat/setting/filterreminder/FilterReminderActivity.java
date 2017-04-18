package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.filterreminder;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;

public class FilterReminderActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FilterReminderFragment fragment =  new FilterReminderFragment();
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
