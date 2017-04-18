package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class TimeSyncItem extends AbstractSettingItem{
	
	private boolean isSyncOpen = false;
	private ShowTimeDownViewListener timeDownViewListener;
	
	private static final int DRAWABLE_TIME_SYNC_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_TIME_SYNC_DOWN = R.drawable.thermost_setting_arrow_down;
	
	public TimeSyncItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Time Setting");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setTimeSync();
	}
	
	public void setShowTimeDownViewListener(ShowTimeDownViewListener timeDownViewListener) {
		this.timeDownViewListener = timeDownViewListener;
	}
	
	public void setTimeSync() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 3, 0);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_TIME_SYNC_DOWN);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		if(isSyncOpen){
			infoImageView.setBackgroundResource(DRAWABLE_TIME_SYNC_DOWN);
			isSyncOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_TIME_SYNC_UP);
			isSyncOpen = true;
		}
		
		timeDownViewListener.onViewOpenChangeed(isSyncOpen);
	}
	
	public interface ShowTimeDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	 
}
