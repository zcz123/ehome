package cc.wulian.smarthomev5.fragment.more.shake;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.ShakeActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.IPreferenceKey;

public class ShakeItem extends AbstractSettingItem {

	public ShakeItem(Context context) {
		super(context, R.drawable.icon_more_shake, context.getResources()
				.getString(R.string.more_shake_off_function));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		chooseToggleButton.setVisibility(View.VISIBLE);
		chooseToggleButton.setChecked(getBoolean(
				IPreferenceKey.P_KEY_OPEN_SHAKE, false));
		chooseToggleButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						putBoolean(IPreferenceKey.P_KEY_OPEN_SHAKE, isChecked);
					}
				});
	}

	@Override
	public void doSomethingAboutSystem() {
		Intent intent = new Intent();
		intent.setClass(mContext, ShakeActivity.class);
		mContext.startActivity(intent);
	}

}
