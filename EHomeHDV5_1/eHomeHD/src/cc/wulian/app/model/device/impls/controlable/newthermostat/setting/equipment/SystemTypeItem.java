package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class SystemTypeItem extends AbstractSettingItem{
	
	
	private boolean isSystemTypeOpen = false;
	private ShowSystemTypeDownViewListener systemTypeDownViewListener;
	private String mSystemTypeData;
	private String mSystemTypeText;
	//是否选择 设置类型
	private boolean isTypeSelect = false;
	
	private static final int DRAWABLE_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_DOWN = R.drawable.thermost_setting_arrow_down;
	
	public SystemTypeItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "System Type");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setSystemType();
	}
	
	public void setSystemTypeDownViewListener(ShowSystemTypeDownViewListener systemTypeDownViewListener) {
		this.systemTypeDownViewListener = systemTypeDownViewListener;
	}

	public String getmSystemTypeData() {
		return mSystemTypeData;
	}


	//根据系统类型数据  设置item显示类型
	public void setmSystemTypeData(String systemMode,String data) {
		// 获取数据，初始类型
		if(!StringUtil.isNullOrEmpty(systemMode) && !isTypeSelect){
			String sysMode = systemMode.substring(1, 2);
			if(StringUtil.equals(sysMode, EquipmentSettingFragment.SYSTEM_MODE_01)
				|| StringUtil.equals(sysMode, EquipmentSettingFragment.SYSTEM_MODE_02))
			{
				this.mSystemTypeData = SystemTypeDownItem.SYSTEM_TYPE_DATA_01;
			}else if(StringUtil.equals(sysMode,EquipmentSettingFragment.SYSTEM_MODE_00)){
				this.mSystemTypeData = "00";
			}else{
				this.mSystemTypeData = SystemTypeDownItem.SYSTEM_TYPE_DATA_02;
			}
			setSystemTypeText(mSystemTypeData);
		}
		// 设置的类型
		if(!StringUtil.isNullOrEmpty(data)){
			this.mSystemTypeData = data;
			isTypeSelect = true;
			setSystemTypeText(mSystemTypeData);
		}

	}
	// 根据类型数据 设置TextView显示
	public void setSystemTypeText(String mSystemTypeData){
		if(StringUtil.equals(mSystemTypeData, SystemTypeDownItem.SYSTEM_TYPE_DATA_01)){
			mSystemTypeText = SystemTypeDownItem.SYSTEM_TYPE_TEXT_CONVENTIONAL;
		}else if(StringUtil.equals(mSystemTypeData, SystemTypeDownItem.SYSTEM_TYPE_DATA_02)){
			mSystemTypeText = SystemTypeDownItem.SYSTEM_TYPE_TEXT_HEATPUMP;
		}else{
			mSystemTypeText = "";
		}
		
		infoTextView.setText(mSystemTypeText);
	}
	
	public String getmSystemTypeText() {
		return mSystemTypeText;
	}

	public void setIsSystemTypeOpen(boolean isOpen){
		isSystemTypeOpen = isOpen;
		infoImageView.setBackgroundResource(DRAWABLE_DOWN);
	}

	public void setSystemType() {
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
		
		if(isSystemTypeOpen){
			infoImageView.setBackgroundResource(DRAWABLE_DOWN);
			isSystemTypeOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_UP);
			isSystemTypeOpen = true;
		}
		
		systemTypeDownViewListener.onViewOpenChangeed(isSystemTypeOpen);
	}
	
	public interface ShowSystemTypeDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	
	
	
}
