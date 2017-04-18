package cc.wulian.smarthomev5.activity;


import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.setting.SettingManagerFragment;

public class SettingManagerActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, new SettingManagerFragment())
			.commit();
		}
	}
	@Override
	protected boolean finshSelf() {
		return false;
	}
}
