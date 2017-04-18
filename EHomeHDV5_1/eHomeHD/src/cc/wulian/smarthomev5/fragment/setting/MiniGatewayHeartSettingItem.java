package cc.wulian.smarthomev5.fragment.setting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayHeartSettingActivity;

public class MiniGatewayHeartSettingItem extends AbstractSettingItem {
	private boolean isReadyToSetting = false;// searching finished
	private boolean needSettingWifi = true;// need configure wifi

	public MiniGatewayHeartSettingItem(Context context) {
		super(context, R.drawable.account_information_mini_heart_icon,context.getResources()
				.getString(R.string.controlCenter_Intimate_tool));
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
		startActivity();

	}

	private void startActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, MiniGatewayHeartSettingActivity.class);
		mContext.startActivity(intent);
	}

}
