package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;

public class HVACTypeDownItem extends AbstractSettingItem implements OnTouchListener{

	private boolean isTypeSelect = false;
	private String systemType;
	private String fuel;
	private String mHVACTypeData;
	private String mHVACTypeText;
	private HVACTypeDataChangedListener hvacTypeDataChangedListener;
	private HVACTypeTextChangedListener hvacTypeTextChangedListener;
	
	@ViewInject(R.id.thermost_setting_equipment_tv1)
	private TextView typeTextView1;
	@ViewInject(R.id.thermost_setting_equipment_tv2)
	private TextView typeTextView2;
	@ViewInject(R.id.thermost_setting_equipment_tv3)
	private TextView typeTextView3;
	@ViewInject(R.id.thermost_setting_equipment_tv4)
	private TextView typeTextView4;
	@ViewInject(R.id.thermost_setting_equipment_tv5)
	private TextView typeTextView5;
	@ViewInject(R.id.thermost_setting_equipment_tv6)
	private TextView typeTextView6;
	@ViewInject(R.id.thermost_setting_line_tv3)
	private TextView lineTv1;
	@ViewInject(R.id.thermost_setting_line_tv4)
	private TextView lineTv2;
	@ViewInject(R.id.thermost_setting_line_tv5)
	private TextView lineTv3;
	@ViewInject(R.id.thermost_setting_line_tv6)
	private TextView lineTv4;

	public static final String HVAC_CONVEN_TEXT_01 = "heat only";
	public static final String HVAC_CONVEN_TEXT_02 = "cool only";
	public static final String HVAC_CONVEN_TEXT_03 = "stage I heat,stage I cool";
	public static final String HVAC_CONVEN_TEXT_04 = "stage I heat,stage II cool";
	public static final String HVAC_CONVEN_TEXT_05 = "stage II heat,stage I cool";
	public static final String HVAC_CONVEN_TEXT_06 = "stage II heat,stage II cool";
	
	public static final String HVAC_PUMP_TEXT_01 = "stage I heat pump";
	public static final String HVAC_PUMP_TEXT_02 = "stage II heat pump";
	public static final String HVAC_PUMP_TEXT_03 = "stage I heat pump , stage I auxiliary heating";
	public static final String HVAC_PUMP_TEXT_04 = "stage II heat pump , stage I auxiliary heating";
	
	public static final String HVAC_TYPE_DATA_01 = "1";
	public static final String HVAC_TYPE_DATA_02 = "2";
	public static final String HVAC_TYPE_DATA_03= "3";
	public static final String HVAC_TYPE_DATA_04 = "4";
	public static final String HVAC_TYPE_DATA_05 = "5";
	public static final String HVAC_TYPE_DATA_06 = "6";
	
	public HVACTypeDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "HVAC Type");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setHVACTypeDown();
	}

	public void setmHVACTypeDownData(String systemMode ){
		if(!StringUtil.isNullOrEmpty(systemMode) && !isTypeSelect) {
			String sysMode = systemMode.substring(1, 2);
			if (StringUtil.equals(sysMode, EquipmentSettingFragment.SYSTEM_MODE_01)
					|| StringUtil.equals(sysMode, EquipmentSettingFragment.SYSTEM_MODE_02)) {
				this.systemType = SystemTypeDownItem.SYSTEM_TYPE_DATA_01;
			} else if (StringUtil.equals(sysMode, EquipmentSettingFragment.SYSTEM_MODE_00)) {
				this.systemType = "00";
			} else {
				this.systemType = SystemTypeDownItem.SYSTEM_TYPE_DATA_02;
			}

			if(StringUtil.equals(sysMode, EquipmentSettingFragment.SYSTEM_MODE_01)
					|| StringUtil.equals(sysMode, EquipmentSettingFragment.SYSTEM_MODE_03)){
				this.fuel = FuelDownItem.FUEL_DATA_01;
			}else if(StringUtil.equals(sysMode,EquipmentSettingFragment.SYSTEM_MODE_00)){
				this.fuel = "00";
			}else{
				this.fuel = FuelDownItem.FUEL_DATA_02;
			}
			setHVACTypeText();
		}
	}
	
	public void setSystemType(String type){
		this.systemType = type;
		setHVACTypeText();
		isTypeSelect = true;
	}
	
	public void setFuel(String fuel){
		this.fuel = fuel;
		setHVACTypeText();
		isTypeSelect = true;
	}

	public void setHvacTypeDataChangedListener(HVACTypeDataChangedListener hvacTypeDataChangedListener) {
		this.hvacTypeDataChangedListener = hvacTypeDataChangedListener;
	}
	
	public void setHvacTypeTextChangedListener(HVACTypeTextChangedListener hvacTypeTextChangedListener) {
		this.hvacTypeTextChangedListener = hvacTypeTextChangedListener;
	}
	
	private void setHVACTypeText(){
		if(!StringUtil.isNullOrEmpty(systemType)){
			if(StringUtil.equals(systemType, SystemTypeDownItem.SYSTEM_TYPE_DATA_01)){
				typeTextView1.setText(HVAC_CONVEN_TEXT_01);
				typeTextView2.setText(HVAC_CONVEN_TEXT_02);
				typeTextView3.setText(HVAC_CONVEN_TEXT_03);
				typeTextView4.setText(HVAC_CONVEN_TEXT_04);
				typeTextView5.setText(HVAC_CONVEN_TEXT_05);
				typeTextView6.setText(HVAC_CONVEN_TEXT_06);
				typeTextView3.setVisibility(View.VISIBLE);
				typeTextView4.setVisibility(View.VISIBLE);
				typeTextView5.setVisibility(View.VISIBLE);
				typeTextView6.setVisibility(View.VISIBLE);
				lineTv1.setVisibility(View.VISIBLE);
				lineTv2.setVisibility(View.VISIBLE);
				lineTv3.setVisibility(View.VISIBLE);
				lineTv4.setVisibility(View.VISIBLE);

			}else if(StringUtil.equals(systemType, SystemTypeDownItem.SYSTEM_TYPE_DATA_02)){
				typeTextView1.setText(HVAC_PUMP_TEXT_01);
				typeTextView2.setText(HVAC_PUMP_TEXT_02);
				typeTextView3.setText(HVAC_PUMP_TEXT_03);
				typeTextView4.setText(HVAC_PUMP_TEXT_04);
				typeTextView3.setVisibility(View.VISIBLE);
				typeTextView4.setVisibility(View.VISIBLE);
				typeTextView5.setVisibility(View.GONE);
				typeTextView6.setVisibility(View.GONE);
				lineTv1.setVisibility(View.VISIBLE);
				lineTv2.setVisibility(View.VISIBLE);
				lineTv3.setVisibility(View.GONE);
				lineTv4.setVisibility(View.GONE);
			}
		}
	}

	public void setHVACTypeDown() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_equipment, null);
		ViewUtils.inject(this,view);
		typeTextView1.setOnTouchListener(this);
		typeTextView2.setOnTouchListener(this);
		typeTextView3.setOnTouchListener(this);
		typeTextView4.setOnTouchListener(this);
		typeTextView5.setOnTouchListener(this);
		typeTextView6.setOnTouchListener(this);
		typeTextView1.setText(HVAC_CONVEN_TEXT_01);
		typeTextView2.setText(HVAC_CONVEN_TEXT_02);
		typeTextView3.setText(HVAC_CONVEN_TEXT_03);
		typeTextView4.setText(HVAC_CONVEN_TEXT_04);
		typeTextView5.setText(HVAC_CONVEN_TEXT_05);
		typeTextView6.setText(HVAC_CONVEN_TEXT_06);
		typeTextView3.setVisibility(View.VISIBLE);
		typeTextView4.setVisibility(View.VISIBLE);
		typeTextView5.setVisibility(View.VISIBLE);
		typeTextView6.setVisibility(View.VISIBLE);
		lineTv1.setVisibility(View.VISIBLE);
		lineTv2.setVisibility(View.VISIBLE);
		lineTv3.setVisibility(View.VISIBLE);
		lineTv4.setVisibility(View.VISIBLE);
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
	
	public interface HVACTypeDataChangedListener{
		public void onDataChanged(String data);
	}
	
	public interface HVACTypeTextChangedListener{
		public void onTextChanged(String data);
	}

	@Override
	public boolean onTouch(View view, MotionEvent arg1) {
		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(view.getId() == R.id.thermost_setting_equipment_tv1){
				mHVACTypeData = HVAC_TYPE_DATA_01;
				mHVACTypeText = (String) typeTextView1.getText();
			}
			if(view.getId() == R.id.thermost_setting_equipment_tv2){
				mHVACTypeData = HVAC_TYPE_DATA_02;
				mHVACTypeText = (String) typeTextView2.getText();			
			}
			if(view.getId() == R.id.thermost_setting_equipment_tv3){
				mHVACTypeData = HVAC_TYPE_DATA_03;
				mHVACTypeText = (String) typeTextView3.getText();
			}
			if(view.getId() == R.id.thermost_setting_equipment_tv4){
				mHVACTypeData = HVAC_TYPE_DATA_04;
				mHVACTypeText = (String) typeTextView4.getText();
			}
			if(view.getId() == R.id.thermost_setting_equipment_tv5){
				mHVACTypeData = HVAC_TYPE_DATA_05;
				mHVACTypeText = (String) typeTextView5.getText();
			}
			if(view.getId() == R.id.thermost_setting_equipment_tv6){
				mHVACTypeData = HVAC_TYPE_DATA_06;
				mHVACTypeText = (String) typeTextView6.getText();
			}
			return true;
		case MotionEvent.ACTION_MOVE:
			return false;
		case MotionEvent.ACTION_UP:
			hvacTypeDataChangedListener.onDataChanged(mHVACTypeData);
			hvacTypeTextChangedListener.onTextChanged(mHVACTypeText);
			return true;
		}
		return false;
	}
	
	
}
