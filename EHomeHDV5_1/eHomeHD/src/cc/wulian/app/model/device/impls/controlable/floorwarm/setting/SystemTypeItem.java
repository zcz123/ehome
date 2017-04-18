package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class SystemTypeItem extends AbstractSettingItem {
	
	private String gwID;
	private String devID;
	private String ep;
	private String epType;
	private String mSystemType;
	private String systemTypeText = "";
	
	private static final int DRAWABLE_ARROW_RIGHT = R.drawable.thermost_setting_arrow_right;
	
	public SystemTypeItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.AP_select_program));

	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setSystemTypeItem();
	}
	
	public void setSystemTypeData(String gwID,String devID,String ep,String epType){
		this.gwID = gwID;
		this.devID = devID;
		this.ep = ep;
		this.epType = epType;
	}
	
	public void setSystemType(String systemType) {
		this.mSystemType = systemType;
		setSystemTypeText(mSystemType);
	}
	
	public void setSystemTypeText(String systemType) {
		if(!StringUtil.isNullOrEmpty(systemType)){
			if(StringUtil.equals(systemType, FloorWarmUtil.SYSTEM_TYPE_ELECT_TAG)){
				this.systemTypeText = mContext.getResources().getString(R.string.AP_warm_system);
			}else{
				this.systemTypeText = mContext.getResources().getString(R.string.AP_water_system);
			}
		}

		infoTextView.setText(systemTypeText);
	}

	public void setSystemTypeItem() {
		nameTextView.setTextColor(Color.parseColor("#3e3e3e"));
		iconImageView.setVisibility(View.GONE);
//		chooseToggleButton.setVisibility(View.INVISIBLE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		nameTextView.setLayoutParams(params);
		
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params2.setMargins(0, 0,20, 0);
		infoTextView.setVisibility(View.VISIBLE);
		infoImageView.setVisibility(View.VISIBLE);
		infoTextView.setLayoutParams(params2);
		infoTextView.setTextColor(Color.parseColor("#737373"));
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_ARROW_RIGHT);
		setSystemTypeText(mSystemType);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		jumpToSystemTypeSettingActivity();
	}
	
	private void jumpToSystemTypeSettingActivity(){
		Intent intent = new Intent(mContext, SystemTypeSettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FloorWarmUtil.GWID, gwID);
		bundle.putString(FloorWarmUtil.DEVID, devID);
		bundle.putString(FloorWarmUtil.EP, ep);
		bundle.putString(FloorWarmUtil.EPTYPE, epType);
		bundle.putString("systemType",mSystemType);
		intent.putExtra("SystemTypeSettingInfo", bundle);
		mContext.startActivity(intent);
	}
	
}
