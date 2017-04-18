package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;

public class VibrateItem extends AbstractSettingItem{
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private boolean isVibrateOpen = true;
	
	private static final String VIBRATE_CMD_ON = "D1";
	private static final String VIBRATE_CMD_OFF= "D0";
	
	private static final String VIBRATE_OFF= "00";
	private static final String VIBRATE_ON= "01";
	
	private static final int DRAWABLE_VIBRATE_ON = R.drawable.thermost_setting_icon_on;
	private static final int DRAWABLE_VIBRATE_OFF = R.drawable.thermost_setting_icon_off;

	public VibrateItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Vibrate");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setVibrate();
	}
	
	public void setVibrateData(String gwId,String devId,String ep,String epType){
		mGwId = gwId;
		mDevId = devId;
		mEp = ep;
		mEpType = epType;
	}
	
	//判断  是否打开 isVibrateOpen
	public void setVibrateOpen(String vibrateData){
		if(StringUtil.equals(vibrateData, VIBRATE_ON)){
			isVibrateOpen = true;
		}else if(StringUtil.equals(vibrateData, VIBRATE_OFF)){
			isVibrateOpen = false;
		}
		setVibrateImage(isVibrateOpen);
	}

	private void setVibrateImage(boolean isOpen){
		if(isOpen){
			infoImageView.setBackgroundResource(DRAWABLE_VIBRATE_ON);
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_VIBRATE_OFF);
		}
	}

	public void setVibrate() {
		nameTextView.setTextColor(Color.parseColor("#3e3e3e"));
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		nameTextView.setLayoutParams(params);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		setVibrateImage(isVibrateOpen);
		
		infoImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(isVibrateOpen){
					SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, VIBRATE_CMD_OFF);
				}else{
					SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, VIBRATE_CMD_ON);
				}
				
			}
		});;
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
