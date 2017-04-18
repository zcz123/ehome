package cc.wulian.smarthomev5.fragment.setting.router;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniRouterSettingActivity;

public class SXRouterSettingItem extends AbstractSettingItem{

	public SXRouterSettingItem(Context context) {
		super(context, R.drawable.account_information_mini_router_icon, context.getResources()
				.getString(R.string.gateway_router_setting));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.system_intent_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			StartToMiniRouterSettingActivity();	
			}
		});
	}
	
	@Override
	public void doSomethingAboutSystem() {
		// TODO Auto-generated method stub
		StartToMiniRouterSettingActivity();
	}

private void StartToMiniRouterSettingActivity(){
	Intent intent = new Intent();
	intent.setClass(mContext, MiniRouterSettingActivity.class);
	mContext.startActivity(intent);
		
	}
}
