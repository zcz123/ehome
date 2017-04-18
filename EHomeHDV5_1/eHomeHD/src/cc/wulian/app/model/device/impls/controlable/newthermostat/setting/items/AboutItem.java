package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;

public class AboutItem extends AbstractSettingItem{
	

	public AboutItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "About");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setAbout();
	}

	public void setAbout() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
