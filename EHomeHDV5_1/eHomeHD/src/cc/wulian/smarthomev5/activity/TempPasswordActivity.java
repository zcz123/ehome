package cc.wulian.smarthomev5.activity;

import cc.wulian.smarthomev5.fragment.device.TempPasswordFragment;

import android.os.Bundle;

public class TempPasswordActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, new TempPasswordFragment())
					.commit();
		}
	}

}
