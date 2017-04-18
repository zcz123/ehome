package cc.wulian.smarthomev5.activity.devicesetting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cc.wulian.smarthomev5.activity.EventBusActivity;

public class DeviceSettingshowActivity extends EventBusActivity{

	public static final String SETTING_ITEM_SHOW_FRAGMENT_CLASSNAME = "SETTING_ITEM_SHOW_FRAGMENT_CLASSNAME";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if(bundle.containsKey(SETTING_ITEM_SHOW_FRAGMENT_CLASSNAME)){
			try {
				String className = bundle.getString(SETTING_ITEM_SHOW_FRAGMENT_CLASSNAME);
				Fragment fragment = (Fragment)Class.forName(className).newInstance();
				fragment.setArguments(bundle);
				getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch(Exception e){
				
			}
		}
	}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
	
}
