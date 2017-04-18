package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperAddRulesFragment;

public class HouseKeeperAddRulesActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, new HouseKeeperAddRulesFragment())
			.commit();
		}
	}

	@Override
	public boolean fingerRightFromLeft() {
		return false;
	}

	
}
