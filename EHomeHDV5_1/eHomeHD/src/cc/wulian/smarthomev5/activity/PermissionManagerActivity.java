package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.setting.permission.PermissionManagerFragment;

public class PermissionManagerActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PermissionManagerFragment fragment = new PermissionManagerFragment();
		fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, fragment)
			.commit();
	}
	
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
}
