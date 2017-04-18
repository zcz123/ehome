package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential;

import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class SecondStageDownItem extends AbstractSettingItem{
	
	private final String TAG = getClass().getSimpleName();
	private TextView mSecondStageTv;
	private SeekBar mSecondStageSeekBar;
	private String mTempUnit = "00";
	private String mSecondStage;
	private String mSecondStageData;
	private String mSecondStageValue;
	private HashMap<String, String> secondStageMap;
	
	private Drawable SEEKBAR_THUMB_C = mContext.getResources().getDrawable(R.drawable.thermost_setting_icon_c);
	private Drawable SEEKBAR_THUMB_F = mContext.getResources().getDrawable(R.drawable.thermost_setting_icon_f);
	
	private static final String SECOND_SATGE_TEXT = "This option is used to adjust the heating and cooling temperature"
			+ "differential.This represent the temperature above the cooding setpoint or below the heating setpoint"
			+ "when the equipment is power on.";

	public SecondStageDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Second Stage Option");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setSecondStageDown();
	}
	
	public String getmSecondStage() {
		return mSecondStage;
	}
	
	public String getmSecondStageValue() {
		return mSecondStageValue;
	}
	
	public void setSecondStageData(String tempUnit,String secondStage){
		if(!StringUtil.isNullOrEmpty(tempUnit)){
			this.mTempUnit = tempUnit;
		}
		if(!StringUtil.isNullOrEmpty(secondStage)){
			this.mSecondStage = secondStage;
		}
		setDownView(mTempUnit);
	}
	
	private void setDownView(String tempUnit){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		
		if(StringUtil.equals(tempUnit, DifferentialSettingFragment.DIFF_TEMP_C)){
			view = inflater.inflate(R.layout.device_thermostat82_setting_differential_second_c, null);
			mSecondStageTv = (TextView) view.findViewById(R.id.thermost_setting_diff_second_tv_c);
			mSecondStageSeekBar = (SeekBar) view.findViewById(R.id.thermost_setting_diff_second_seekbar_c);
			mSecondStageSeekBar.setThumb(SEEKBAR_THUMB_C);
			initSecondStageMapC();
		}else{
			view = inflater.inflate(R.layout.device_thermostat82_setting_differential_second_f, null);
			mSecondStageTv = (TextView) view.findViewById(R.id.thermost_setting_diff_second_tv_f);
			mSecondStageSeekBar = (SeekBar) view.findViewById(R.id.thermost_setting_diff_second_seekbar_f);
			mSecondStageSeekBar.setThumb(SEEKBAR_THUMB_F);
			initSecondStageMapF();
		}
		mSecondStageTv.setText(SECOND_SATGE_TEXT);
		mSecondStageSeekBar.setThumbOffset(0);
		if(!StringUtil.isNullOrEmpty(mSecondStage) && !StringUtil.equals(mSecondStage,"00")){
			mSecondStageSeekBar.setProgress(Integer.parseInt(mSecondStage.substring(1,2))-1);
			mSecondStageValue = secondStageMap.get(mSecondStage);
		}else{
			mSecondStageSeekBar.setProgress(0);
			mSecondStage = "01";
			mSecondStageValue = secondStageMap.get(mSecondStage);
		}
		mSecondStageSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekbar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				mSecondStage = "0"+(progress+1);
				mSecondStageValue = secondStageMap.get(mSecondStage);
				Log.i(TAG+":SecondStageSeekBar", mSecondStage+"-"+mSecondStageValue);
			}
		});
	}
	
	private void initSecondStageMapC(){
		secondStageMap = new HashMap<>();
		secondStageMap.put("01", "1");
		secondStageMap.put("02", "1.5");
		secondStageMap.put("03", "2");
		secondStageMap.put("04", "2.5");
		secondStageMap.put("05", "3");
	}
	private void initSecondStageMapF(){
		secondStageMap = new HashMap<>();
		secondStageMap.put("01", "2");
		secondStageMap.put("02", "3");
		secondStageMap.put("03", "4");
		secondStageMap.put("04", "5");
		secondStageMap.put("05", "6");
	}

	public void setSecondStageDown() {
		setDownView(DifferentialSettingFragment.DIFF_TEMP_C);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
