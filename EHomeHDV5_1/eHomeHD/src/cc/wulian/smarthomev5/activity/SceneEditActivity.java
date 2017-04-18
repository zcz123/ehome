package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.scene.SceneEditFragment;

public class SceneEditActivity extends EventBusActivity
{
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		SceneEditFragment fragment = new SceneEditFragment();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content,fragment )
		.commit();
	}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
}
