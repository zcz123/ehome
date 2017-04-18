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

public class FuelItem extends AbstractSettingItem{
	
	
	private String mFuelData;
	private String mFuelText;
	private boolean isFuslOpen = false;
	private ShowFuelDownViewListener fuelDownViewListener;
	//是否选择 设置类型
	private boolean isTypeSelect = false;
	
	private static final int DRAWABLE_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_DOWN = R.drawable.thermost_setting_arrow_down;

	public FuelItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Fuel");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFuel();
	}
	
	public void setFuelDownViewListener(ShowFuelDownViewListener fuelDownViewListener) {
		this.fuelDownViewListener = fuelDownViewListener;
	}

	public String getmFuelData() {
		return mFuelData;
	}

	public void setmFuelData(String systemMode,String data) {
		if(!StringUtil.isNullOrEmpty(systemMode) && !isTypeSelect){
			
			String sysMode = systemMode.substring(1, 2);
			if(StringUtil.equals(sysMode, EquipmentSettingFragment.SYSTEM_MODE_01)
					|| StringUtil.equals(sysMode, EquipmentSettingFragment.SYSTEM_MODE_03)){
				this.mFuelData = FuelDownItem.FUEL_DATA_01;
			}else if(StringUtil.equals(sysMode,EquipmentSettingFragment.SYSTEM_MODE_00)){
				this.mFuelData = "00";
			}else{
				this.mFuelData = FuelDownItem.FUEL_DATA_02;
			}
			setFuelText(mFuelData);
		}
		if(!StringUtil.isNullOrEmpty(data)){
			this.mFuelData = data;
			isTypeSelect = true;
			setFuelText(mFuelData);
		}
	}
	
	public void setFuelText(String mFuelData){
		if(StringUtil.equals(mFuelData, FuelDownItem.FUEL_DATA_01)){
			mFuelText = FuelDownItem.FUEL_TEXT_SINGLE;
		}else if(StringUtil.equals(mFuelData, FuelDownItem.FUEL_DATA_02)){
			mFuelText = FuelDownItem.FUEL_TEXT_DUAL;
		}else{
			mFuelText = "";
		}
		infoTextView.setText(mFuelText);
	}
	
	public String getmFuelText() {
		return mFuelText;
	}
	
	public void setIsFuelOpen(boolean isOpen){
		isFuslOpen = isOpen;
		infoImageView.setBackgroundResource(DRAWABLE_DOWN);
	}

	public void setFuel() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,3, 0);
		infoTextView.setVisibility(View.VISIBLE);
		infoTextView.setLayoutParams(params);
		infoTextView.setTextColor(Color.parseColor("#737373"));
		infoTextView.setText("");
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(nameParams);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_DOWN);
	}
	

	@Override
	public void doSomethingAboutSystem() {
		if(isFuslOpen){
			infoImageView.setBackgroundResource(DRAWABLE_DOWN);
			isFuslOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_UP);
			isFuslOpen = true;
		}
		
		fuelDownViewListener.onViewOpenChangeed(isFuslOpen);
	}
	
	public interface ShowFuelDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	
	
	
}
