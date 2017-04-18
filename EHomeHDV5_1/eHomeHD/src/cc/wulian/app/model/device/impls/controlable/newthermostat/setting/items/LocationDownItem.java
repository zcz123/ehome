package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location.LocationSettingActivity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class LocationDownItem extends AbstractSettingItem{
	
	private String gwId;
	
	private TextView locationTv;
	private Button locationBtn;
	
	private static final String LOCATION_TEXT = "Knowing about your home helps the thermostat work effectively.";
	private static final String LOCATION_BTN_TEXT = "Setting your Location";

	public LocationDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Location");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setLocationDown();
	}
	
	public void setLocationDownData(String gwId){
		this.gwId = gwId;
	}

	public void setLocationDown() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_other, null);
		locationTv = (TextView) view.findViewById(R.id.thermost_setting_other_tv);
		locationBtn = (Button) view.findViewById(R.id.thermost_setting_other_btn);
		locationTv.setText(LOCATION_TEXT);
		locationBtn.setText(LOCATION_BTN_TEXT);
		
		locationBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				jumpToLocationSettingActivity();
			}
		});
		
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
	
	/**
	 * 跳转至LocationSettingActivity
	 */
	private void jumpToLocationSettingActivity() {
		Intent intent = new Intent(mContext,LocationSettingActivity.class);
		intent.putExtra("gwID",gwId);
		mContext.startActivity(intent);
	}
	
	
}
