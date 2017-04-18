package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperCustomMessageAddDeviceFragment;

/**
 * 准备添加发送消息的设备 页面
 * @author Administrator
 *
 */
public class HouseKeeperCustomMessageActivity extends EventBusActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, new HouseKeeperCustomMessageAddDeviceFragment())
			.commit();
		}
	}

	@Override
	public boolean fingerRightFromLeft() {
		return false;
	}

}
