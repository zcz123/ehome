package cc.wulian.app.model.device.impls.controlable.fancoil.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;

public class FilterReminderItem extends AbstractSettingItem{

	public FilterReminderItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.thermostat_fans_filter_hint));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFliterReminder();
	}
	
	public void setFliterReminder() {
		nameTextView.setTextColor(Color.parseColor("#3e3e3e"));
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		nameTextView.setLayoutParams(params);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params2.setMargins(0, 0,20, 0);
		infoImageView.setVisibility(View.VISIBLE);
		infoTextView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoTextView.setLayoutParams(params2);
		infoTextView.setTextColor(Color.parseColor("#737373"));
		infoImageView.setBackgroundResource(FanCoilUtil.DRAWABLE_ARROW_RIGHT);
		initFilterReminder();
	}

	private void initFilterReminder(){
		String filterText = getString(FanCoilUtil.P_KEY_FANCOIL_FILTER_REMINDER,
				mContext.getResources().getString(R.string.house_rule_add_new_condition_door_window_close));
		if(!StringUtil.isNullOrEmpty(filterText)) {
			if (StringUtil.equals(filterText, FanCoilUtil.FILTER_REMINDER_CLOSE)) {
				infoTextView.setText(mContext.getResources().getString(R.string.house_rule_add_new_condition_door_window_close));
			} else if (StringUtil.equals(filterText, FanCoilUtil.FILTER_REMINDER_THREE)) {
				infoTextView.setText(mContext.getResources().getString(R.string.thermostat_fans_month_3));
			} else if (StringUtil.equals(filterText, FanCoilUtil.FILTER_REMINDER_SIX)) {
				infoTextView.setText(mContext.getResources().getString(R.string.thermostat_fans_month_6));
			} else if (StringUtil.equals(filterText, FanCoilUtil.FILTER_REMINDER_NINE)) {
				infoTextView.setText(mContext.getResources().getString(R.string.thermostat_fans_month_9));
			} else if (StringUtil.equals(filterText, FanCoilUtil.FILTER_REMINDER_TWELVE)) {
				infoTextView.setText(mContext.getResources().getString(R.string.thermostat_fans_month_12));
			} else {
				infoTextView.setText(mContext.getResources().getString(R.string.house_rule_add_new_condition_door_window_close));
			}
		}

	}

	@Override
	public void doSomethingAboutSystem() {
		jumpToFilterReminderActivity();
	}

	private void jumpToFilterReminderActivity(){
		Intent intent = new Intent(mContext,FilterReminderActivity.class);
		mContext.startActivity(intent);
	}
}
