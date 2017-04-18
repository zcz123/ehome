package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.app.model.device.impls.controlable.aircondtion.DaiKinAirConditionSetFragment;

public class DaiKinAirConditionSetActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			DaiKinAirConditionSetFragment daiKinAirConditionSetFragment = new DaiKinAirConditionSetFragment();
			daiKinAirConditionSetFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, daiKinAirConditionSetFragment).commit();
		}
	}
}
