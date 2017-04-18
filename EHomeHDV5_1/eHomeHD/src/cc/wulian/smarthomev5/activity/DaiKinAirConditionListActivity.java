package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.app.model.device.impls.controlable.aircondtion.DaikinAirConditionListFragment;
import cc.wulian.smarthomev5.fragment.uei.ACRemoteControlFragment;

public class DaiKinAirConditionListActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			DaikinAirConditionListFragment daikinAirConditionListFragment = new DaikinAirConditionListFragment();
			daikinAirConditionListFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, daikinAirConditionListFragment).commit();
		}
	}
}
