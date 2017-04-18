package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;

public class EmergencyHeatDownItem extends AbstractSettingItem{
	
	private TextView heatTextView; 
	private static final String HEAT_TEXT = "It is more expensive to use Emergency Heating";
	
	public EmergencyHeatDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Emergency Heat");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setEmergencyHeatDown();
	}
	

	public void setEmergencyHeatDown() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_emergencyheat, null);
		heatTextView = (TextView) view.findViewById(R.id.thermost_setting_emergency_tv);
		heatTextView.setText(HEAT_TEXT);
		heatTextView.setTextColor(Color.parseColor("#ec7017"));
	}
	

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
