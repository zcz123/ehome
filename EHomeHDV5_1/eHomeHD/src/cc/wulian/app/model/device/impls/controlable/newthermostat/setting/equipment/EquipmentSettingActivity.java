package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment;

import android.os.Bundle;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.EquipmentSettingFragment;
import cc.wulian.smarthomev5.activity.EventBusActivity;

public class EquipmentSettingActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		EquipmentSettingFragment fragment = new EquipmentSettingFragment();
		fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
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
