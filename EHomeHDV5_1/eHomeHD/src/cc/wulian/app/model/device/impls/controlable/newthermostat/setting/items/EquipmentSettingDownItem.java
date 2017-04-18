package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.ThermostatSettingFragment;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.EquipmentSettingActivity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class EquipmentSettingDownItem extends AbstractSettingItem{
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private TextView equipmentTv;
	private Button equipmentBtn;
	//工作模式 ：设备选择+供热方式
	private String mSystemMode;
	 // 系统类型(如 一级制冷)
	private String mSystemType;
	
	private static final String EQUIPMENT_TEXT = "Incrrect equipment setting will cause unexpected HVAC activity or equipment damage.";
	private static final String EQUIPMENT_BTN_TEXT = "continue"; 
	
	public EquipmentSettingDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Equipment Setting");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setEquipmentSettingDown();
	}


	public void setEquipmentSettingData(String gwId,String devId,String ep,String epType){
		this.mGwId = gwId;
		this.mDevId = devId;
		this.mEp = ep;
		this.mEpType = epType;
	}

	public void setEquipmentType(String systempMode,String systemType){
		this.mSystemMode = systempMode;
		this.mSystemType = systemType;
	}

	public void setEquipmentSettingDown() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_other, null);
		equipmentTv = (TextView) view.findViewById(R.id.thermost_setting_other_tv);
		equipmentBtn = (Button) view.findViewById(R.id.thermost_setting_other_btn);
		equipmentTv.setText(EQUIPMENT_TEXT);
		equipmentBtn.setText(EQUIPMENT_BTN_TEXT);
		
		equipmentBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				jumpToEquipmentSettingActivity();
			}
		});
	}
	
	private void jumpToEquipmentSettingActivity(){
		Intent intent = new Intent(mContext, EquipmentSettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ThermostatSettingFragment.GWID, mGwId);
		bundle.putString(ThermostatSettingFragment.DEVID, mDevId);
		bundle.putString(ThermostatSettingFragment.EP, mEp);
		bundle.putString(ThermostatSettingFragment.EPTYPE, mEpType);
		bundle.putString("systemMode",mSystemMode);
		bundle.putString("systemType",mSystemType);
		intent.putExtra("EquipmentSettingInfo", bundle);
		mContext.startActivity(intent);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
