package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.SwitchAccountActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.URLConstants;

public class SwitchGateWayItem extends AbstractSettingItem {

	public SwitchGateWayItem(Context context) {
		super(context, R.drawable.icon_gateway_switch, context.getResources()
				.getString(R.string.set_account_manager_change_gw));
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
		//如果是从html登陆的
		if(Preference.getPreferences().getUserEnterType().equals("account")){
			intent.setClass(mContext, Html5PlusWebViewActvity.class);
			String uri=URLConstants.LOCAL_BASEURL+"gatewayList.html?action=controlCenter";
			intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
		}else{
			intent.setClass(mContext, SwitchAccountActivity.class);
		}
		mContext.startActivity(intent);
	}

}
