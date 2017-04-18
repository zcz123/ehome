package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;
import android.view.KeyEvent;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionDelayFragment;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionTaskFragment;

public class HouseKeeperActionDelayActivity extends EventBusActivity{

	private HouseKeeperActionDelayFragment fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			fragment = new HouseKeeperActionDelayFragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, fragment)
			.commit();
		}
	}
	
	@Override
	public boolean fingerRightFromLeft() {
		return false;
	}
	
}
