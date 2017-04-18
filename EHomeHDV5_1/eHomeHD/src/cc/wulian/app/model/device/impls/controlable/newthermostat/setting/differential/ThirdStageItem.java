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

public class ThirdStageItem extends AbstractSettingItem{
	
	private final String TAG = getClass().getSimpleName();
	private boolean isThirdStageOpen = false;
	private ShowThirdStageDownViewListener thirdStageDownViewListener;
	
	private static final int DRAWABLE_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_DOWN = R.drawable.thermost_setting_arrow_down;

	public ThirdStageItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Third Stage Option");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setThirdStage();
	}
	
	public void setThirdStageDownViewListener(ShowThirdStageDownViewListener thirdStageDownViewListener) {
		this.thirdStageDownViewListener = thirdStageDownViewListener;
	}

	public void setThirdStage() {
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
		if(isThirdStageOpen){
			infoImageView.setBackgroundResource(DRAWABLE_DOWN);
			isThirdStageOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_UP);
			isThirdStageOpen = true;
		}
		
		thirdStageDownViewListener.onViewOpenChangeed(isThirdStageOpen);
	}
	
	public interface ShowThirdStageDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	
	
	
}
