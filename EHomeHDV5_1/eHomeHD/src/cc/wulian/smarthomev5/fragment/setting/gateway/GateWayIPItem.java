package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.view.View;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;

public class GateWayIPItem extends AbstractSettingItem{

	public GateWayIPItem(Context context) {
		super(context, R.drawable.gateway_ip, context.getResources().getString(R.string.set_account_manager_gw_IP));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoTextView.setVisibility(View.VISIBLE);
		infoTextView.setText(AccountManager.getAccountManger().getmCurrentInfo().getGwSerIP());
	}
	@Override
	public void doSomethingAboutSystem() {
		
	}
}
