package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.more.gps.GPSFragment;

public class GPSActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, new GPSFragment()).commit();

	}
	
	/**
	 * 防止左划删除的冲突
	 */
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}

}
