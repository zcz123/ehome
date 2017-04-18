package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.more.MoreManagerFragment;

public class MoreManagerActivity extends EventBusActivity
{
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, new MoreManagerFragment())
			.commit();
		}
	}
	@Override
	protected boolean finshSelf() {
		return false;
	}
}
