package cc.wulian.app.model.device.impls.controlable.fancoil.setting;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import cc.wulian.smarthomev5.activity.EventBusActivity;

public class ProgramSettingActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		ProgramSettingFragment fragment = new ProgramSettingFragment();
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
