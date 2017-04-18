package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.Preference;

public class AbstractDoorLockAlarmItem {
	protected View view;
	protected Drawable icon;
	protected String name;
	protected String alarmKey;
	protected Context mContext;
	protected LayoutInflater inflater;
	private Preference preference = Preference.getPreferences();
	
	public AbstractDoorLockAlarmItem( Context context, Drawable icon, String name ,String alarmKey)
	{
		this.mContext = context;
		inflater = LayoutInflater.from(this.mContext);
		this.icon =icon;
		this.name = name;
		this.alarmKey = alarmKey;
	}
	
	public AbstractDoorLockAlarmItem( Context context, int icon, String name, String alarmKey)
	{
		this(context,context.getResources().getDrawable(icon),name,alarmKey);
	}
	
	public void initSystemState(){
		view = inflater.inflate(R.layout.device_door_lock_setting_alarm_remind, null);
		
		ImageView iconImageView = (ImageView)view.findViewById(R.id.device_door_item_name_iv);
		iconImageView.setImageDrawable(this.icon);
		TextView nameTextView = (TextView)view.findViewById(R.id.device_door_item_name_tv);
		nameTextView.setText(this.name);
		ToggleButton chooseToggleButton = (ToggleButton)view.findViewById(R.id.device_door_item_switch);
		iconImageView.setVisibility(View.GONE);
		chooseToggleButton.setChecked(preference.getBoolean(
				alarmKey, true));
		chooseToggleButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						preference.putBoolean(alarmKey,isChecked);
					}
				});
	}
	
	
	public  View getShowView(){
		initSystemState();
		return this.view;
	}
}
