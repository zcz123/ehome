package cc.wulian.smarthomev5.activity;

import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.device.NoClockChoosePwFragment;
import android.os.Bundle;

public class NoClockChoosePwActivity extends EventBusActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, new NoClockChoosePwFragment())
					.commit();
		}
	}
}
