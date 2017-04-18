package cc.wulian.smarthomev5.activity.minigateway;

import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniTimeVoiceFragment;
import android.os.Bundle;

public class MiniTimeVoiceActivity extends BaseActivity {
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		MiniTimeVoiceFragment miniTimeVoiceFragment = new MiniTimeVoiceFragment();
		miniTimeVoiceFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, miniTimeVoiceFragment)
			.commit();
	}
 
	@Override
	protected boolean finshSelf() {
		return false;
	}

	
}
