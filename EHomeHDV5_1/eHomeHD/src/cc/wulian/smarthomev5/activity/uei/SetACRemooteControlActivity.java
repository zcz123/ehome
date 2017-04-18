package cc.wulian.smarthomev5.activity.uei;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.uei.ACRemoteControlFragment;
import cc.wulian.smarthomev5.fragment.uei.SetACRemoteControlFragment;

public class SetACRemooteControlActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			SetACRemoteControlFragment acRemoteFragment=new SetACRemoteControlFragment();
			Bundle args=getIntent().getBundleExtra("args");
			acRemoteFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, acRemoteFragment).commit();
		}
	}
}
