package cc.wulian.smarthomev5.fragment.setting.router;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class RouterSettingItem extends AbstractSettingItem{

	public RouterSettingItem(Context context) {
		super(context, R.drawable.account_information_router_icon, context.getResources().getString(R.string.gateway_router_setting));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.voice_remind_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jumpToRouterSettingActivity();	
			}
		});
	}

	@Override
	public void doSomethingAboutSystem() {
		// add by yanzy:不允许被授权用户使用
		if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.GATEWAY_ROUTING)) {
			return;
		}

		jumpToRouterSettingActivity();		
	}
	/**
	 * 跳转至RouterSettingActivity
	 */
	private void jumpToRouterSettingActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, RouterSettingActivity.class);
		mContext.startActivity(intent);
	}
}
