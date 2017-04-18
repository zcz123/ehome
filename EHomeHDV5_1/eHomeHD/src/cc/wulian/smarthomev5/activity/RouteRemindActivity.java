package cc.wulian.smarthomev5.activity;

import cc.wulian.smarthomev5.fragment.more.route.DeviceStatusFragment;
import android.os.Bundle;

public class RouteRemindActivity extends EventBusActivity
{
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, new DeviceStatusFragment())
			.commit();
		}
	}

}
