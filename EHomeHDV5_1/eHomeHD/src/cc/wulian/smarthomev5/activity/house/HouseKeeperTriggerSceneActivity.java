package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerSceneFragment;

public class HouseKeeperTriggerSceneActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			HouseKeeperTriggerSceneFragment fragment = new HouseKeeperTriggerSceneFragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, fragment)
			.commit();
		}
	}

}
