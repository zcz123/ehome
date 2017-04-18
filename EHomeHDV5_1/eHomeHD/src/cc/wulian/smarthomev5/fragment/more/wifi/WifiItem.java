package cc.wulian.smarthomev5.fragment.more.wifi;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.WifiActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.IPreferenceKey;

public class WifiItem extends AbstractSettingItem {

	public WifiItem(Context context) {
		super(context, R.drawable.wifi_scene_small, context.getResources()
				.getString(R.string.more_wifi));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		chooseToggleButton.setVisibility(View.VISIBLE);
		chooseToggleButton.setChecked(getBoolean(
				IPreferenceKey.P_KEY_OPEN_WIFI, false));
		chooseToggleButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						putBoolean(IPreferenceKey.P_KEY_OPEN_WIFI, isChecked);
					}
				});
	}

	@Override
	public void doSomethingAboutSystem() {
		Intent intent = new Intent();
		intent.setClass(mContext, WifiActivity.class);
		mContext.startActivity(intent);
	}

}
