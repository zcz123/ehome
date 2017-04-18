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

public class FilterReminderDownItem extends AbstractSettingItem{
	
	private TextView tvInstruct;
	private TextView tvContent;
	private ImageButton goButton;
	private GoButtonListener goButtonListener;
	private static final String FLITER_INS_TEXT = "Changing air filter is a simple way to save energy. The reminder will appear after the settled time is up";
	private static final String FLITER_CONTENT_TEXT = "Reminder after 10 days of running."+"\n"+"(About 1 month)";
	private static final String FLITER_CONTENT_DATA = "filterContent_data";
	
	public FilterReminderDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Filter Reminder");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFilterReminederDown();
	}

	public void setGoButtonListener(GoButtonListener goButtonListener) {
		this.goButtonListener = goButtonListener;
	}
	
	public void setContentData(String content){
		putString(FLITER_CONTENT_DATA, content);
	}

	public void setFilterReminederDown() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_fliter, null);
		tvInstruct = (TextView) view.findViewById(R.id.thermost_setting_fliter_tv1);
		tvContent = (TextView) view.findViewById(R.id.thermost_setting_fliter_tv2);
		goButton = (ImageButton) view.findViewById(R.id.thermost_setting_fliter_btn);
		tvInstruct.setText(FLITER_INS_TEXT);
		tvContent.setText(getString(FLITER_CONTENT_DATA, FLITER_CONTENT_TEXT));
		
		goButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				goButtonListener.onGoBtnClick();
			}
		});
	}
	
	public interface GoButtonListener{
		public void onGoBtnClick();
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
	
	
}
