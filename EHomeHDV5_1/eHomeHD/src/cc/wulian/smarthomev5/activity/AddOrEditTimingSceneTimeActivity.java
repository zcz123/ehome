package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.scene.AddOrEditTimingSceneTimeFragment;


public class AddOrEditTimingSceneTimeActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AddOrEditTimingSceneTimeFragment fragment = new AddOrEditTimingSceneTimeFragment();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content,fragment)
		.commit();
	}
}