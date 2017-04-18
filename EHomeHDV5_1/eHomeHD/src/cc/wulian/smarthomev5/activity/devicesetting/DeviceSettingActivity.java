package cc.wulian.smarthomev5.activity.devicesetting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectControlDeviceDataFragment;

public class DeviceSettingActivity extends EventBusActivity{
	
	public static Activity instance;
	public static final String SETTING_FRAGMENT_CLASSNAME = "SETTING_FRAGMENT_CLASSNAME";
	private Fragment fragment=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance=this;
		Bundle bundle = getIntent().getExtras();
		if(bundle.containsKey(SETTING_FRAGMENT_CLASSNAME)){
			try {
				String className = bundle.getString(SETTING_FRAGMENT_CLASSNAME);
				fragment = (Fragment)Class.forName(className).newInstance();
				fragment.setArguments(bundle);
				getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}catch(Exception e){
				
			}
		}
	}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(this.fragment!=null&&(this.fragment instanceof HouseKeeperSelectControlDeviceDataFragment)){
			HouseKeeperSelectControlDeviceDataFragment houseKeeperFragment=(HouseKeeperSelectControlDeviceDataFragment) this.fragment;
			if(houseKeeperFragment.getCurDevice()!=null){
				if(data==null){
					data=new Intent();
				}
				data.putExtra("requestCode", 1);
				houseKeeperFragment.getCurDevice().OnRefreshResultData(data);
			}
		}
	}
}
