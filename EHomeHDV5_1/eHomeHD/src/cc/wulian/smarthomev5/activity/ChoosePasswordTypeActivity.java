package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.device.ChoosePasswordTypeFragment;

public class ChoosePasswordTypeActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, new ChoosePasswordTypeFragment())
					.commit();
		}

	}

}
