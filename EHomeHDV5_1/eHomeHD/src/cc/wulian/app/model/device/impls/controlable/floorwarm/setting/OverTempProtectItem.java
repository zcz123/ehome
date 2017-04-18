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

public class OverTempProtectItem extends AbstractSettingItem {
	
	private String gwID;
	private String devID;
	private String ep;
	private String epType;
	private String overTempProtectState;
	private String overTempProtectTemp;
	private String overTempProtectText = "";

	private static final int DRAWABLE_ARROW_RIGHT = R.drawable.thermost_setting_arrow_right;

	public OverTempProtectItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.AP_over_protect));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setOverTempProtectView();
	}

	public void setOverTempData(String gwID,String devID,String ep,String epType){
		this.gwID = gwID;
		this.devID = devID;
		this.ep = ep;
		this.epType = epType;
	}
	
	public void setOverTempProtect(String state,String temp) {
		if(!StringUtil.isNullOrEmpty(state)){
			this.overTempProtectState = state;
		}
		if(!StringUtil.isNullOrEmpty(temp)){
			this.overTempProtectTemp = temp;
			String tempText = FloorWarmUtil.hexStr2Str100(overTempProtectTemp);
			//温度 小数点后为0，则 显示整数
			this.overTempProtectText = FloorWarmUtil.getTempFormat(tempText) + FloorWarmUtil.TEMP_UNIT_C_TEXT;
		}
		infoTextView.setText(overTempProtectText);
	}
	
	private void setOverTempProtectView() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,20, 0);
		infoTextView.setVisibility(View.VISIBLE);
		infoImageView.setVisibility(View.VISIBLE);
		infoTextView.setLayoutParams(params);
		infoTextView.setTextColor(Color.parseColor("#737373"));
		infoImageView.setLayoutParams(nameParams);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_ARROW_RIGHT);
		infoTextView.setText(overTempProtectText);
	}

	@Override
	public void doSomethingAboutSystem() {
		jumpToOverTempActivity();
	}
	
	private void jumpToOverTempActivity(){
		Intent intent = new Intent(mContext, OverTempProtectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FloorWarmUtil.GWID, gwID);
		bundle.putString(FloorWarmUtil.DEVID, devID);
		bundle.putString(FloorWarmUtil.EP, ep);
		bundle.putString(FloorWarmUtil.EPTYPE, epType);
		bundle.putString("overTempProtectState",overTempProtectState);
		bundle.putString("overTempProtectTemp",overTempProtectTemp);
		intent.putExtra("OverTempProtectFragmentInfo", bundle);
		mContext.startActivity(intent);
	}
	
}
