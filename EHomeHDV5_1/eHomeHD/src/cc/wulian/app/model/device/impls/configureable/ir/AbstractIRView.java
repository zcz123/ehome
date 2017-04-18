package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;

public abstract class AbstractIRView 
{
	protected Context mContext;
	protected LayoutInflater inflater;
	protected MainApplication mApp = MainApplication.getApplication();
	private Preference preference = Preference.getPreferences();
	protected DeviceInfo deviceInfo;
	protected Map<String,AbstractIRView> viewMap;
	protected TextView headTextView;
	protected Resources resources;
	protected LinearLayout contentLineLayout;
	public static String TYPE_GENERAL = "00";
	public static String TYPE_AIR_CONDITION = "01";
	public static String TYPE_STB = "02";
	public AbstractIRView( Context context ,DeviceInfo info)
	{
		this.mContext = context;
		this.deviceInfo = info;
		inflater = LayoutInflater.from(this.mContext);
		resources = this.mContext.getResources();
	}
	public void attachContext(Context context){
		this.mContext = context;
	}
	public abstract View onCreateView();
	public abstract void onViewCreated(View view);
	public void reloadData(){
		
	}
	public String getType(){
		return TYPE_GENERAL;
	}
	public Intent getSettingIntent(){
		return null;
	}
	public void headClick(TextView view){
		if(view == headTextView){
			preference.putString(IPreferenceKey.P_KEY_IR_CURRENT_PAGE, getType());
			headTextView.setTextColor(resources.getColor(R.color.v5_green_light));
			contentLineLayout.removeAllViews();
			View v = onCreateView();
			onViewCreated(v);
			contentLineLayout.addView(v);
			reloadData();
		}else{
			headTextView.setTextColor(resources.getColor(R.color.white));
		}
	}
	public void setHeadView(TextView view){
		headTextView = view;
		headTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for(AbstractIRView irView : viewMap.values()){
					irView.headClick(headTextView);
				}
			}
		});
	}
	public void setViewMap(Map<String,AbstractIRView> viewMap){
		this.viewMap = viewMap;
		this.viewMap.put(getType(),this);
	}
	public void setContentView(LinearLayout contentView){
		contentLineLayout = contentView;
	}
	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
}
