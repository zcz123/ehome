package cc.wulian.smarthomev5.activity;

import android.os.Bundle;

import cc.wulian.smarthomev5.fragment.setting.gateway.ManagerGatewayFragment;
import cc.wulian.smarthomev5.fragment.setting.gateway.SwitchAccountFragment;

public class ManagerAccountActivity extends EventBusActivity
{
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		ManagerGatewayFragment fragment = new ManagerGatewayFragment();
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
