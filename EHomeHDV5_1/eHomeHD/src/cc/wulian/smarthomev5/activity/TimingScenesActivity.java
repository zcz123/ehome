package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.scene.TimingScenesFragment;


public class TimingScenesActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TimingScenesFragment fragment = new TimingScenesFragment();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content,fragment)
		.commit();
	}
	
}
