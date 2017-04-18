package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class FirstStageItem extends AbstractSettingItem{
	
	private final String TAG = getClass().getSimpleName();
	private boolean isFirstStageOpen = false;
	private ShowFirstStageDownViewListener firstStageDownViewListener;
	
	private static final int DRAWABLE_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_DOWN = R.drawable.thermost_setting_arrow_down;

	public FirstStageItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "First Stage Option");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFirstStage();
	}
	
	public void setFirstStageDownViewListener(ShowFirstStageDownViewListener firstStageDownViewListener) {
		this.firstStageDownViewListener = firstStageDownViewListener;
	}
	
	public void setFirstStage() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,10, 0);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_DOWN);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		if(isFirstStageOpen){
			infoImageView.setBackgroundResource(DRAWABLE_DOWN);
			isFirstStageOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_UP);
			isFirstStageOpen = true;
		}
		
		firstStageDownViewListener.onViewOpenChangeed(isFirstStageOpen);
	}
	
	public interface ShowFirstStageDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	
	
	
}
