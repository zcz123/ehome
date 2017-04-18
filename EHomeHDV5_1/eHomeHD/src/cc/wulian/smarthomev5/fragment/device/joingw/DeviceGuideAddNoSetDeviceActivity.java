package cc.wulian.smarthomev5.fragment.device.joingw;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;

/**
 * 添加无Set键设备Activity
 * 
 * @author Administrator
 * 
 */
public class DeviceGuideAddNoSetDeviceActivity extends EventBusActivity
		implements OnClickListener {

	private TextView nextStepTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_guide_device_noset);

		nextStepTextView = (TextView) findViewById(R.id.guide_join_gw_config_wifi_next_step_text);
		nextStepTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		
	}

	private void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.nav_device_title));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.device_common_new_hint));
	}

	public boolean fingerRightFromCenter() {
		return false;
	}

	public boolean fingerLeft() {
		return false;
	}

}
