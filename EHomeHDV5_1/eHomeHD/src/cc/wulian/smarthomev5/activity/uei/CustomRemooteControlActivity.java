package cc.wulian.smarthomev5.activity.uei;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.uei.CustomRemoteControlFragment;

public class CustomRemooteControlActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, new CustomRemoteControlFragment()).commit();
		}
	}
	

}
