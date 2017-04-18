package cc.wulian.app.model.device.impls.controlable.floorwarm.program;


import android.content.pm.ActivityInfo;
import android.os.Bundle;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;

public class FloorWarmProgramActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		setContentView(R.layout.device_thermostat82_program_activity);
		Bundle bundle = getIntent().getExtras();
		FloorWarmProgramFragment fragment = new FloorWarmProgramFragment();
		fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.program_content_layout,fragment).commit();
        
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
