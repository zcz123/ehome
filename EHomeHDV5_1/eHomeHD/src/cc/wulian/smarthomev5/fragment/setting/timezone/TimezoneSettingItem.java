package cc.wulian.smarthomev5.fragment.setting.timezone;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.TimezoneSettingActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class TimezoneSettingItem extends AbstractSettingItem{
	
	public static final int requestCode=10001;

	public TimezoneSettingItem(Context context) {
		super(context, R.drawable.account_information__timezone_icon, context.getResources().getString(R.string.gateway_timezone_setting));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.system_intent_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jumpToTimezoneSettingActivity();	
			}
		});
	}

	@Override
	public void doSomethingAboutSystem() {
		// add by yanzy:不允许被授权用户使用
		if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.GATEWAY_TIMEZONE)) {
			return;
		}
		
		jumpToTimezoneSettingActivity();		
	}
	
	/**
	 * 跳转至TimezoneSettingActivity
	 */
	private void jumpToTimezoneSettingActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, TimezoneSettingActivity.class);
		mContext.startActivity(intent);
	}
}
