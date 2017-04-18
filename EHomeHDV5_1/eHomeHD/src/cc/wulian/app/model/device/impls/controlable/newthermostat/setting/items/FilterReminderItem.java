package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.filterreminder.FilterReminderActivity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class FilterReminderItem extends AbstractSettingItem{
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private boolean isFilterOpen=false;;
	
	private ShowFliterDownViewListener fliterDownViewListener;

	private static final String P_KEY_FILTER_OPEN = "P_KEY_FILTER_OPEN";
	private static final int DRAWABLE_FILTER_ON = R.drawable.thermost_setting_icon_on;
	private static final int DRAWABLE_FILTER_OFF = R.drawable.thermost_setting_icon_off;
	
	public FilterReminderItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Filter Reminder");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFilterRemineder();
	}
	
	public void setFilterData(String gwId,String devId,String ep,String epType){
		mGwId = gwId;
		mDevId = devId;
		mEp = ep;
		mEpType = epType;
	}

	public void setFliterDownViewListener(ShowFliterDownViewListener fliterDownViewListener) {
		this.fliterDownViewListener = fliterDownViewListener;
	}
	
	private void initFilterOpen(){
		isFilterOpen = getBoolean(P_KEY_FILTER_OPEN, false);
		if(isFilterOpen){
			infoImageView.setBackgroundResource(DRAWABLE_FILTER_ON);
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_FILTER_OFF);
		}
	}

	public void setFilterRemineder() {
		nameTextView.setTextColor(Color.parseColor("#3e3e3e"));
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		nameTextView.setLayoutParams(params);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		initFilterOpen();
		
		infoImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(isFilterOpen){
					infoImageView.setBackgroundResource(DRAWABLE_FILTER_OFF);
					isFilterOpen = false;
					
				}else{
					infoImageView.setBackgroundResource(DRAWABLE_FILTER_ON);
					isFilterOpen = true;
				}
				
				putBoolean(P_KEY_FILTER_OPEN, isFilterOpen);
			
			}
		});;
		
	}

	@Override
	public void doSomethingAboutSystem() {
		fliterDownViewListener.onViewOpenChangeed(isFilterOpen);
		
	}
	
	public interface ShowFliterDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
}
