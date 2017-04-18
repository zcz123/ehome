package cc.wulian.smarthomev5.fragment.setting.flower;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.flower.FlowerSettingManagerActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class DreamFlowerSettingItem extends AbstractSettingItem{

	public DreamFlowerSettingItem(Context context) {
		super(context, R.drawable.account_information_dream_flower_icon, context.getResources().getString(R.string.gateway_dream_flower_setting));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.system_intent_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jumpToDreamFlowerSettingActivity();	
			}
		});
	}

	@Override
	public void doSomethingAboutSystem() {
		// add by yanzy:不允许被授权用户使用
		if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.GATEWAY_DREAMFLOWER)) {
			return;
		}

		jumpToDreamFlowerSettingActivity();		
	}
	
	/**
	 * 跳转至DreamFlowerSettingActivity
	 */
	private void jumpToDreamFlowerSettingActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, FlowerSettingManagerActivity.class);
		mContext.startActivity(intent);
	}
}
