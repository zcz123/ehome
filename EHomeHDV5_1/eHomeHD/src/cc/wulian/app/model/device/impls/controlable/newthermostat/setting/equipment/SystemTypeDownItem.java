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

public class SystemTypeDownItem extends AbstractSettingItem{
	
	
	private TextView conventionalTv;
	private TextView heatPumpTv;
	private String mSystemTypeData;
	private SystemTypeDataChangedListener systemTypeDataChangedListener;
	
	public static final String SYSTEM_TYPE_TEXT_CONVENTIONAL = "Conventional";
	public static final String SYSTEM_TYPE_TEXT_HEATPUMP = "Heat Pump";
	
	public static final String SYSTEM_TYPE_DATA_01 = "01";
	public static final String SYSTEM_TYPE_DATA_02 = "02";

	public SystemTypeDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "System Type");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setSystemTypeDown();
	}
	
	public void setSystemTypeDataChangedListener(SystemTypeDataChangedListener systemTypeDataChangedListener) {
		this.systemTypeDataChangedListener = systemTypeDataChangedListener;
	}

	public void setSystemTypeDown() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_equipment, null);
		conventionalTv = (TextView) view.findViewById(R.id.thermost_setting_equipment_tv1);
		heatPumpTv = (TextView) view.findViewById(R.id.thermost_setting_equipment_tv2);
		conventionalTv.setText(SYSTEM_TYPE_TEXT_CONVENTIONAL);
		heatPumpTv.setText(SYSTEM_TYPE_TEXT_HEATPUMP);
		
		
		conventionalTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mSystemTypeData = SYSTEM_TYPE_DATA_01;
				systemTypeDataChangedListener.onDataChanged(mSystemTypeData);
			}
		});
		
		heatPumpTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mSystemTypeData = SYSTEM_TYPE_DATA_02;
				systemTypeDataChangedListener.onDataChanged(mSystemTypeData);
			}
		});
		
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
	
	public interface SystemTypeDataChangedListener{
		public void onDataChanged(String data);
	}
	
	
}
