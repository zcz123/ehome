package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;

public class EmergencyHeatItem extends AbstractSettingItem{
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private boolean isHeatOpen;
	private ShowEmergencyDownViewListener emergencyDownViewListener;
	
	private static final String EHEAT_CMD_ON = "A1";
	private static final String EHEAT_CMD_OFF= "A0";
	
	private static final String EHEAT_OFF= "00";
	private static final String EHEAT_ON= "01";
	
	private static final int DRAWABLE_EHEAT_ON = R.drawable.thermost_setting_icon_on;
	private static final int DRAWABLE_EHEAT_OFF = R.drawable.thermost_setting_icon_off;
	
	public EmergencyHeatItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Emergency Heat");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setEmergencyHeat();
	}
	
	public void setEmergencyHeatData(String gwId,String devId,String ep,String epType){
		mGwId = gwId;
		mDevId = devId;
		mEp = ep;
		mEpType = epType;
	}
	
	public void setEmergencyDownViewListener(ShowEmergencyDownViewListener emergencyDownViewListener) {
		this.emergencyDownViewListener = emergencyDownViewListener;
	}

	//判断  是否打开 isHeatOpen
	public void setHeatOpen(String emergencyHeatData){
		if(StringUtil.equals(emergencyHeatData, EHEAT_ON)){
			isHeatOpen = true;
		}else if(StringUtil.equals(emergencyHeatData, EHEAT_OFF)){
			isHeatOpen = false;
		}
		setEmergencyHeatImage(isHeatOpen);
		emergencyDownViewListener.onViewOpenChangeed(isHeatOpen);
	}

	private void setEmergencyHeatImage(boolean isOpen){
		if(isOpen){
			infoImageView.setBackgroundResource(DRAWABLE_EHEAT_ON);
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_EHEAT_OFF);
		}
	}

	public void setEmergencyHeat() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		nameTextView.setLayoutParams(params);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		descriptionTextView.setText("It is more expensive to use Emergency Heating");
		descriptionTextView.setTextColor(Color.parseColor("#eb8f4c"));
		descriptionTextView.setTextSize(15);
		descriptionTextView.setGravity(Gravity.CENTER);
		downLineLayout.setLayoutParams(params);
		setEmergencyHeatImage(isHeatOpen);
		
		infoImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(isHeatOpen){
					SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, EHEAT_CMD_OFF);
				}else{
					SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, EHEAT_CMD_ON);
				}
				
			}
		});
	}

	public interface ShowEmergencyDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	
	@Override
	public void doSomethingAboutSystem() {
		
	}
}
