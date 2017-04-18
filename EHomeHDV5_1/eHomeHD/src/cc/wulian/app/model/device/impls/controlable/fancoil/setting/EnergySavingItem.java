package cc.wulian.app.model.device.impls.controlable.fancoil.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilUtil;
import cc.wulian.app.model.device.impls.controlable.fancoil.program.FanCoilProgramActivity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class EnergySavingItem extends AbstractSettingItem{
	
	private String gwID;
	private String devID;
	private String ep;
	private String epType;
	
	public EnergySavingItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.AP_enegry_mode));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setEnergySaving();
	}
	
	public void setEnergySaving() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,3, 0);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(FanCoilUtil.DRAWABLE_ARROW_RIGHT);
	}
	
	public void setEnergySavingData(String gwID,String devID,String ep,String epType){
		this.gwID = gwID;
		this.devID = devID;
		this.ep = ep;
		this.epType = epType;
	}

	@Override
	public void doSomethingAboutSystem() {
		jumpToEnergySavingActivity();
	}
	
	private void jumpToEnergySavingActivity(){
		Intent intent = new Intent(mContext, EnergySavingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FanCoilUtil.GWID, gwID);
		bundle.putString(FanCoilUtil.DEVID, devID);
		bundle.putString(FanCoilUtil.EP, ep);
		bundle.putString(FanCoilUtil.EPTYPE, epType);
		intent.putExtra("EnergySavingFragmentInfo", bundle);
		mContext.startActivity(intent);
	}
	
	
	
	
}
