package cc.wulian.smarthomev5.activity.monitor;

import android.content.Intent;
import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.monitor.DesktopCameraSetFragment;
import cc.wulian.smarthomev5.fragment.uei.ACRemoteControlFragment;

public class DesktopCameraSetActivity extends EventBusActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			DesktopCameraSetFragment desktopCameraSetFragment = new DesktopCameraSetFragment();
			Bundle bundle = getIntent().getExtras();
			if(bundle != null ){
				desktopCameraSetFragment.setArguments(bundle);
			}
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, desktopCameraSetFragment).commit();
		}
	}
}
