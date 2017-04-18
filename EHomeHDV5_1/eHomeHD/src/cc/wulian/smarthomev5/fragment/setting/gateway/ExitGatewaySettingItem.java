package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.view.Gravity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;

public class ExitGatewaySettingItem extends AbstractSettingItem{

	AccountManager accountManager = AccountManager.getAccountManger();
	public ExitGatewaySettingItem(Context context) {
		super(context, null, context.getResources().getString(R.string.set_account_manager_change_gw_exit_current));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		middleLinearLayout.setGravity(Gravity.CENTER);
		nameTextView.setTextColor(android.graphics.Color.RED);
	}

	@Override
	public void doSomethingAboutSystem() {
		accountManager.exitCurrentGateway(mContext);		
	}

}
