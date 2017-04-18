package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.scene.SceneEditLinkTaskFragment;

public class SceneEditLinkTaskActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SceneEditLinkTaskFragment fragment = new SceneEditLinkTaskFragment();
		fragment.setArguments(getIntent().getExtras());
		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content,fragment )
			.commit();
		}
	}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}

}
