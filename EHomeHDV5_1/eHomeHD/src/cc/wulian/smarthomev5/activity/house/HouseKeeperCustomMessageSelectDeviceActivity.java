package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperCustomMessageSelectDeviceFragment;


public class HouseKeeperCustomMessageSelectDeviceActivity extends
		EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content,
							new HouseKeeperCustomMessageSelectDeviceFragment())
					.commit();
		}
	}

	@Override
	public boolean fingerRightFromLeft() {
		return false;
	}

}
