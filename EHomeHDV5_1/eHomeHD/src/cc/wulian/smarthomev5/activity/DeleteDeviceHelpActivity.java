package cc.wulian.smarthomev5.activity;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class DeleteDeviceHelpActivity extends EventBusActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_device_help_layout);
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setDisplayShowMenuEnabled(false);
		getCompatActionBar().setDisplayShowMenuTextEnabled(false);
		getCompatActionBar().setTitle(
				getString(R.string.device_config_edit_dev_help));
		getCompatActionBar().setIconText(
				mApplication.getResources().getString(R.string.about_back));
		getCompatActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
	}
}
