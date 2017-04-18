package cc.wulian.smarthomev5.activity;

import android.content.Intent;
import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.monitor.EditMonitorInfoFragment;

public class EditMonitorInfoActivity extends EventBusActivity
{
	EditMonitorInfoFragment fragment;
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			fragment = new EditMonitorInfoFragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content,fragment)
			.commit();
		}
	}
	
	@Override
	protected boolean finshSelf() {
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
				fragment.onActivityResult(requestCode, resultCode, data);
	}
}
