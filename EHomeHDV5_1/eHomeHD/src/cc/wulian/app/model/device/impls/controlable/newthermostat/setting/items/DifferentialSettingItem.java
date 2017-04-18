package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential.DifferentialSettingActivity;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.filterreminder.FilterReminderActivity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class DifferentialSettingItem extends AbstractSettingItem{
	
	private boolean isDefferentialOpen = false;
	private ShowDifferentialDownViewListener differentialDownViewListener;
	
	private static final int DRAWABLE_DEFF_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_DEFF_DOWN = R.drawable.thermost_setting_arrow_down;
	

	public DifferentialSettingItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Differential Setting");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setDefferentialSetting();
	}

	public void setDifferentialDownViewListener(ShowDifferentialDownViewListener differentialDownViewListener) {
		this.differentialDownViewListener = differentialDownViewListener;
	}

	public void setDefferentialSetting() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,3, 0);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_DEFF_DOWN);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		if(isDefferentialOpen){
			infoImageView.setBackgroundResource(DRAWABLE_DEFF_DOWN);
			isDefferentialOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_DEFF_UP);
			isDefferentialOpen = true;
		}
			differentialDownViewListener.onViewOpenChangeed(isDefferentialOpen);
		
	}
	
	public interface ShowDifferentialDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	
}
