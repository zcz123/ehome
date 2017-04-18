package cc.wulian.smarthomev5.activity.uei;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.uei.SettingFragment;

public class SettingActivity extends EventBusActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args=null;
		WulianFragment wulianFragment=null;
		if(getIntent()!=null){
			wulianFragment=new SettingFragment();
			args=getIntent().getBundleExtra("args");
		}
		if(wulianFragment!=null){
			wulianFragment.setArguments(args);
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, wulianFragment)
			.commit();
		}
	}
}
