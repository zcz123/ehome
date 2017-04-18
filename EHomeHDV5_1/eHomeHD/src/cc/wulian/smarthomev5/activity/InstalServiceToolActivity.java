package cc.wulian.smarthomev5.activity;

import android.os.Bundle;

import cc.wulian.smarthomev5.fragment.setting.tools.InstalServiceToolFragment;

public class InstalServiceToolActivity extends EventBusActivity
{
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		InstalServiceToolFragment fragment = new InstalServiceToolFragment();
		fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, fragment)
			.commit();
	}

	@Override
	protected boolean finshSelf() {
		return false;
	}
	
}
