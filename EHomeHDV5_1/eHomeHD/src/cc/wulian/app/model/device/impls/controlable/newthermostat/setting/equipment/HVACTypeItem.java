package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class HVACTypeItem extends AbstractSettingItem{
	
	private String mSystemData;
	private String mHVACTypeData;
	private String mHVACTypeText;
	private boolean isHVACTypeOpen = false;
	private ShowHVACTypeDownViewListener hvacTypeDownViewListener;
	//是否选择 设置类型
	private boolean isTypeSelect = false;

	private static final int DRAWABLE_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_DOWN = R.drawable.thermost_setting_arrow_down;
	
	public static final String HVACTYPE_PUMP_TEXT_03 = "stage I heat pump,\nstage I auxiliary heating";
	public static final String HVACTYPE_PUMP_TEXT_04 = "stage II heat pump,\nstage I auxiliary heating";

	public HVACTypeItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "HVAC Type");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setHVACType();
	}

	public void setHvacTypeDownViewListener(ShowHVACTypeDownViewListener hvacTypeDownViewListener) {
		this.hvacTypeDownViewListener = hvacTypeDownViewListener;
	}
	
	public String getHVACTypeData() {
		return mHVACTypeData;
	}
	
	public void setSystemData(String systemMode){
		this.mSystemData = systemMode;
	}

	public void setHVACTypeData(String mHVACType, String data) {
		if(!StringUtil.isNullOrEmpty(mHVACType) && !isTypeSelect){
			this.mHVACTypeData = mHVACType.substring(1,2);
			setItemText(mSystemData, mHVACTypeData);
		}
		
		if(!StringUtil.isNullOrEmpty(data)){
			this.mHVACTypeData  = data;
			isTypeSelect = true;
			setItemText(mSystemData, mHVACTypeData);
		}
		
	}
	// 初始显示 HVAC类型
	private void setItemText(String systemData,String hvacTypeData){
		String text = "";
		if(StringUtil.equals(systemData, "01")){
			switch (Integer.parseInt(hvacTypeData)) {
			case 1:
				text = HVACTypeDownItem.HVAC_CONVEN_TEXT_01;
				break;
			case 2:
				text = HVACTypeDownItem.HVAC_CONVEN_TEXT_02;			
				break;
			case 3:
				text = HVACTypeDownItem.HVAC_CONVEN_TEXT_03;
				break;
			case 4:
				text = HVACTypeDownItem.HVAC_CONVEN_TEXT_04;
				break;
			case 5:
				text = HVACTypeDownItem.HVAC_CONVEN_TEXT_05;
				break;
			case 6:
				text = HVACTypeDownItem.HVAC_CONVEN_TEXT_06;
				break;
			
			}
		}
		else if(StringUtil.equals(systemData, "02")){
			switch (Integer.parseInt(hvacTypeData)) {
				case 1:
					text = HVACTypeDownItem.HVAC_CONVEN_TEXT_01;
					break;
				case 2:
					text = HVACTypeDownItem.HVAC_CONVEN_TEXT_02;
					break;
				case 3:
					text = HVACTypeDownItem.HVAC_CONVEN_TEXT_03;
					break;
				case 4:
					text = HVACTypeDownItem.HVAC_CONVEN_TEXT_04;
					break;
				case 5:
					text = HVACTypeDownItem.HVAC_CONVEN_TEXT_05;
					break;
				case 6:
					text = HVACTypeDownItem.HVAC_CONVEN_TEXT_06;
					break;
			}
		}else{
			switch (Integer.parseInt(hvacTypeData)) {
			case 1:
				text = HVACTypeDownItem.HVAC_PUMP_TEXT_01;
				break;
			case 2:
				text = HVACTypeDownItem.HVAC_PUMP_TEXT_02;			
				break;
			case 3:
				text = HVACTypeDownItem.HVAC_PUMP_TEXT_03;
				break;
			case 4:
				text = HVACTypeDownItem.HVAC_PUMP_TEXT_04;
				break;
			default:
				break;
			}
		}
		
		setHVACTypeText(text);
	}
	
	public void setHVACTypeText(String hvacTypeText){
		this.mHVACTypeText = hvacTypeText;
		if(StringUtil.equals(mHVACTypeText, HVACTypeDownItem.HVAC_PUMP_TEXT_03)){
			mHVACTypeText = HVACTYPE_PUMP_TEXT_03;
		}
		if(StringUtil.equals(mHVACTypeText, HVACTypeDownItem.HVAC_PUMP_TEXT_04)){
			mHVACTypeText = HVACTYPE_PUMP_TEXT_04;
		}
		infoTextView.setText(mHVACTypeText);
	}
	
	public String getHVACTypeText() {
		return mHVACTypeText;
	}

	public void setIsHVACTypeOpen(boolean isOpen){
		isHVACTypeOpen = isOpen;
		infoImageView.setBackgroundResource(DRAWABLE_DOWN);
	}
	
	public void setHVACType() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,3, 0);
		infoTextView.setVisibility(View.VISIBLE);
		infoTextView.setLayoutParams(params);
		infoTextView.setSingleLine(false);
		infoTextView.setTextColor(Color.parseColor("#737373"));
		infoTextView.setText("");
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(nameParams);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_DOWN);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		if(isHVACTypeOpen){
			infoImageView.setBackgroundResource(DRAWABLE_DOWN);
			isHVACTypeOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_UP);
			isHVACTypeOpen = true;
		}
		hvacTypeDownViewListener.onViewOpenChangeed(isHVACTypeOpen);
	}
	
	public interface ShowHVACTypeDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	
	
	
}
