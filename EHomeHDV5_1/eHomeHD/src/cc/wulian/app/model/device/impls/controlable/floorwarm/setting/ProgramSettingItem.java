package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.app.model.device.impls.controlable.floorwarm.program.FloorWarmProgramActivity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class ProgramSettingItem extends AbstractSettingItem {
	
	private String gwID;
	private String devID;
	private String ep;
	private String epType;
	private static final int DRAWABLE_ARROW_RIGHT = R.drawable.thermost_setting_arrow_right;
	
	public ProgramSettingItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.AP_program_mode));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setProgramSetting();
	}
	
	public void setProgramSetting() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,3, 0);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_ARROW_RIGHT);
	}
	
	public void setProgramData(String gwID,String devID,String ep,String epType){
		this.gwID = gwID;
		this.devID = devID;
		this.ep = ep;
		this.epType = epType;
	}

	@Override
	public void doSomethingAboutSystem() {
		jumpToProgramActivity();
	}
	
	private void jumpToProgramActivity(){
		Intent intent = new Intent(mContext, FloorWarmProgramActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FloorWarmUtil.GWID, gwID);
		bundle.putString(FloorWarmUtil.DEVID, devID);
		bundle.putString(FloorWarmUtil.EP, ep);
		bundle.putString(FloorWarmUtil.EPTYPE, epType);
		intent.putExtra("FloorWarmProgramFragmentInfo", bundle);
		mContext.startActivity(intent);
	}
	
	
	
	
}
