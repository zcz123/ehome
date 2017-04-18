package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HousekeeperSelectTimeZomeFragment;

public class HousekeeperSelectTimeZoneActivity extends EventBusActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new HousekeeperSelectTimeZomeFragment()).commit();
	}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
}
