package cc.wulian.smarthomev5.fragment.more.gps;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.GPSActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.IPreferenceKey;

/**
 * GPS Item
 * @author Administrator
 *
 */
public class GPSItem extends AbstractSettingItem {

	public GPSItem(Context context) {
		super(context, R.drawable.icon_more_shake, context.getResources()
				.getString(R.string.more_gps_scene));
	}
	
	@Override
	public void initSystemState() {
		super.initSystemState();
		chooseToggleButton.setVisibility(View.VISIBLE);
		chooseToggleButton.setChecked(getBoolean(
				IPreferenceKey.P_KEY_OPEN_GPS, false));
		chooseToggleButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						putBoolean(IPreferenceKey.P_KEY_OPEN_GPS, isChecked);
					}
				});
	}

	@Override
	public void doSomethingAboutSystem() {
		Intent intent = new Intent();
		intent.setClass(mContext, GPSActivity.class);
		mContext.startActivity(intent);
	}

}
