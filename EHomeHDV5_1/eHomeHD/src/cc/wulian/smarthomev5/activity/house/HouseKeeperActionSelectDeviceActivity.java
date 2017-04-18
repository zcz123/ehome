package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionSelectDeviceFragment;

public class HouseKeeperActionSelectDeviceActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			HouseKeeperActionSelectDeviceFragment fragment = new HouseKeeperActionSelectDeviceFragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, fragment)
			.commit();
		}
	}
	
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
}
