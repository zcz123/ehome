package cc.wulian.smarthomev5.fragment.setting.permission;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.PermissionManagerActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class PermissionManagerItem extends AbstractSettingItem{

	public PermissionManagerItem(Context context) {
		super(context, R.drawable.setting_control_permission_item, context.getResources()
				.getString(R.string.set_account_manager_permission));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.system_intent_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity();
			}
		});
	}

	@Override
	public void doSomethingAboutSystem() {
		// add by yanzy:不允许被授权用户使用
		if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.GATEWAY_TIMEZONE)) {
			return;
		}

		startActivity();
	}

	private void startActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, PermissionManagerActivity.class);
		mContext.startActivity(intent);
	}

}
