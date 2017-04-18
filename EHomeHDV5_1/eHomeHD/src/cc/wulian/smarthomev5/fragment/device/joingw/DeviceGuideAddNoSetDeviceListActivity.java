package cc.wulian.smarthomev5.fragment.device.joingw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;

/**
 * 显示无Set键设备列表Activity
 * 
 * @author Administrator
 * 
 */
public class DeviceGuideAddNoSetDeviceListActivity extends EventBusActivity implements OnClickListener {

	private LinearLayout mDeviceThermost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_guide_device_noset_list);
		initBar();
		initContentView();
		initItemView();
		View findViewById = findViewById(R.id.device_config_wifi_thermost);
	}

	private void initItemView() {
		mDeviceThermost = (LinearLayout) findViewById(R.id.device_config_wifi_thermost);
		mDeviceThermost.setOnClickListener(this);
	}

	private void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.nav_device_title));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.device_common_new_hint));
	}

	private void initContentView() {

	}

	public boolean fingerRightFromCenter() {
		return false;
	}

	public boolean fingerLeft() {
		return false;
	}

	@Override
	public void onClick(View v) {
		if(v == mDeviceThermost){
			
		}
	}

}
