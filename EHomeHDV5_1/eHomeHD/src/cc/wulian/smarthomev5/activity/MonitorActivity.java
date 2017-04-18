package cc.wulian.smarthomev5.activity;

import android.content.Intent;
import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.monitor.MonitorFragment;

public class MonitorActivity extends EventBusActivity
{
	private MonitorFragment monitorFragment;
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null){
			monitorFragment=new MonitorFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, monitorFragment)
			.commit();
		}
	}

	@Override
	protected boolean finshSelf() {
		return false;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
}
