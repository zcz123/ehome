package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.scene.SceneTimingActionTaskFragment;

public class SceneTimingActionTaskActivity extends EventBusActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SceneTimingActionTaskFragment fragment = new SceneTimingActionTaskFragment();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content,fragment)
		.commit();
	}
	
	@Override
	public boolean fingerRightFromLeft() {
		return false;
	}
}
