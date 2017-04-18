package cc.wulian.smarthomev5.activity;

import cc.wulian.smarthomev5.fragment.setting.timezone.SettingTimeZoneFragment;
import android.os.Bundle;

public class TimezoneSettingActivity extends EventBusActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingTimeZoneFragment()).commit();
	}
	
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}

}
