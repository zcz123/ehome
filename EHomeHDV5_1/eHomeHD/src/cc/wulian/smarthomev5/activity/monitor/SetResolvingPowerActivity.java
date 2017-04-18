package cc.wulian.smarthomev5.activity.monitor;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.monitor.DesktopCameraSetFragment;
import cc.wulian.smarthomev5.fragment.monitor.SetResolvingPowerFragment;

public class SetResolvingPowerActivity  extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, new SetResolvingPowerFragment()).commit();
		}
	}
}
