package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;

/**
 * @author Administrator
 *
 */
public class FuelDownItem extends AbstractSettingItem{
	
	
	private TextView singleFuellTv;
	private TextView dualFuelTv;
	private String mFuelData;
	private FuelDataChangedListener fuelDataChangedListener;
	
	public static final String FUEL_TEXT_SINGLE = "Single Fuel";
	public static final String FUEL_TEXT_DUAL = "Dual Fuel";
	
	public static final String FUEL_DATA_01 = "01";
	public static final String FUEL_DATA_02 = "02";
	
	public FuelDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Fuel");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFuelDown();
	}
	
	public void setFuelDataChangedListener(FuelDataChangedListener fuelDataChangedListener) {
		this.fuelDataChangedListener = fuelDataChangedListener;
	}

	public void setFuelDown() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_equipment, null);
		singleFuellTv = (TextView) view.findViewById(R.id.thermost_setting_equipment_tv1);
		dualFuelTv = (TextView) view.findViewById(R.id.thermost_setting_equipment_tv2);
		singleFuellTv.setText(FUEL_TEXT_SINGLE);
		dualFuelTv.setText(FUEL_TEXT_DUAL);
		
		singleFuellTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mFuelData = FUEL_DATA_01;
				fuelDataChangedListener.onDataChanged(mFuelData);
			}
		});
		
		dualFuelTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mFuelData = FUEL_DATA_02;
				fuelDataChangedListener.onDataChanged(mFuelData);
			}
		});
	}
	
	@Override
	public void doSomethingAboutSystem() {
		
	}
	
	public interface FuelDataChangedListener{
		public void onDataChanged(String data);
	}
	
	
}
