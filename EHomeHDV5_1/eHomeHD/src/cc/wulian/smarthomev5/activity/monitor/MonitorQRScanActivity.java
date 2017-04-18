package cc.wulian.smarthomev5.activity.monitor;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.monitor.MonitorQRScanFragment;

public class MonitorQRScanActivity extends SherlockFragmentActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getCompatActionBar().hide();
		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, new MonitorQRScanFragment())
			.commit();
		}
	}
}
