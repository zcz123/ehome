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

public class ForstProtectItem extends AbstractSettingItem {
	
	private String gwID;
	private String devID;
	private String ep;
	private String epType;
	private String forstProtectState;
	private String forstProtectTemp;
	private String forstProtectText;


	public ForstProtectItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.AP_anit_protect));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setForstProtectView();
	}

	public void setForstProtectData(String gwID,String devID,String ep,String epType){
		this.gwID = gwID;
		this.devID = devID;
		this.ep = ep;
		this.epType = epType;
	}
	
	public void setForstProtect(String state,String temp) {
		if(!StringUtil.isNullOrEmpty(state)){
			this.forstProtectState = state;
		}
		if(!StringUtil.isNullOrEmpty(temp)){
			this.forstProtectTemp = temp;
			String tempText = FloorWarmUtil.hexStr2Str100(forstProtectTemp);
			this.forstProtectText = FloorWarmUtil.getTempFormat(tempText) + FloorWarmUtil.TEMP_UNIT_C_TEXT;
		}
		infoTextView.setText(forstProtectText);
	}
	
	private void setForstProtectView() {
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
		infoImageView.setBackgroundResource(FloorWarmUtil.DRAWABLE_ARROW_RIGHT);
		infoTextView.setText(forstProtectText);
	}

	@Override
	public void doSomethingAboutSystem() {
		jumpToForstProtectActivity();
	}
	
	private void jumpToForstProtectActivity(){
		Intent intent = new Intent(mContext, ForstProtectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FloorWarmUtil.GWID, gwID);
		bundle.putString(FloorWarmUtil.DEVID, devID);
		bundle.putString(FloorWarmUtil.EP, ep);
		bundle.putString(FloorWarmUtil.EPTYPE, epType);
		bundle.putString("forstProtectState",forstProtectState);
		bundle.putString("forstProtectTemp",forstProtectTemp);
		intent.putExtra("ForstProtectFragmentInfo", bundle);
		mContext.startActivity(intent);
	}
	
	
}
