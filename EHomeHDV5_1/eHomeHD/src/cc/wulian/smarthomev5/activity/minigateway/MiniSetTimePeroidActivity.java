package cc.wulian.smarthomev5.activity.minigateway;

import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniSetTimePeroidFragment;
import android.os.Bundle;

public class MiniSetTimePeroidActivity extends BaseActivity {
	

	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		MiniSetTimePeroidFragment miniSetTimePeroidFragment = new MiniSetTimePeroidFragment();
		miniSetTimePeroidFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, miniSetTimePeroidFragment)
			.commit();
	}
 
	@Override
	protected boolean finshSelf() {
		return false;
	}
}
