package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.view.View;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;

public class GateWayIDItem extends AbstractSettingItem{

	public GateWayIDItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setGateWayID();
	}

	public void setLongClickListener(View.OnLongClickListener onLongClickListener){
		view.setOnLongClickListener(onLongClickListener);
	}


	public void setGateWayID() {
		infoTextView.setVisibility(View.VISIBLE);
		infoTextView.setText(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		setInfoTextViewColor(mContext.getResources().getColor(R.color.black));
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
