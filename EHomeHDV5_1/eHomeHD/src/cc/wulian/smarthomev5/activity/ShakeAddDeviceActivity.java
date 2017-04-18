package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.more.shake.ShakeAddDeviceFragment;

public class ShakeAddDeviceActivity extends EventBusActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content,new ShakeAddDeviceFragment())
			.commit();
		}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
	
}
