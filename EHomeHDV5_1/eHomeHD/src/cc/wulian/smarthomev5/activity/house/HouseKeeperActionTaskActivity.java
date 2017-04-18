package cc.wulian.smarthomev5.activity.house;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionTaskFragment;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class HouseKeeperActionTaskActivity extends EventBusActivity{

	public static HouseKeeperActionTaskActivity instance=null;
	private HouseKeeperActionTaskFragment fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance=this;
		if (savedInstanceState == null){
			fragment = new HouseKeeperActionTaskFragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, fragment)
			.commit();
		}
	}
	
	@Override
	public boolean fingerRightFromLeft() {
		return false;
	}

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
        	if(HouseKeeperActionTaskFragment.isSaveTask){
        		fragment.backSaveSceneTask();
			}else{
				finish();
			}
         }
         return false;
     }
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if(this.fragment!=null){
//			if(this.fragment.getCurDevice()!=null){
//				this.fragment.getCurDevice().OnRefreshResultData(data);
//			}
//		}
//	}
}
