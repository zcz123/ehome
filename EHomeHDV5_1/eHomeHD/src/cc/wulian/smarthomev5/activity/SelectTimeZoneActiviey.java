package cc.wulian.smarthomev5.activity;

import cc.wulian.smarthomev5.fragment.setting.timezone.SelectTimeZomeFragment;
import android.os.Bundle;

public class SelectTimeZoneActiviey extends EventBusActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SelectTimeZomeFragment()).commit();
	}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
}
