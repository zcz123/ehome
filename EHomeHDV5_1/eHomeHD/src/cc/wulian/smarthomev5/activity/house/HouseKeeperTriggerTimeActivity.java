package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerTimeFragment;

public class HouseKeeperTriggerTimeActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HouseKeeperTriggerTimeFragment fragment = new HouseKeeperTriggerTimeFragment();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content,fragment)
		.commit();
	}

}
