package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.more.wifi.WifiFragment;

public class WifiActivity extends EventBusActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, new WifiFragment()).commit();
	}

}
