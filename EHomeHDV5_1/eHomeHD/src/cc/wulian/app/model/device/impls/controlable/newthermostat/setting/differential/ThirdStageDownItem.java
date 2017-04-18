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

public class ThirdStageDownItem extends AbstractSettingItem{
	
	private final String TAG = getClass().getSimpleName();
	private TextView mThirdStageTv;
	private SeekBar mThirdStageSeekBar;
	private String mTempUnit = "00";
	private String mThirdStage;
	private String mThirdStageValue;
	private HashMap<String, String> thirdStageMap;
	
	private Drawable SEEKBAR_THUMB_C = mContext.getResources().getDrawable(R.drawable.thermost_setting_icon_c);
	private Drawable SEEKBAR_THUMB_F = mContext.getResources().getDrawable(R.drawable.thermost_setting_icon_f);
	
	private static final String THIRD_SATGE_TEXT = "This option is used to adjust the heating and cooling temperature"
			+ "differential.This represent the temperature above the cooding setpoint or below the heating setpoint"
			+ "when the equipment is power on.";

	public ThirdStageDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Third Stage Option");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setThirdStageDown();
	}
	
	public String getmThirdStage() {
		return mThirdStage;
	}

	public String getmThirdStageValue() {
		return mThirdStageValue;
	}
	
	public void setThirdStageData(String tempUnit,String thirdStage){
		if(!StringUtil.isNullOrEmpty(tempUnit)){
			this.mTempUnit = tempUnit;
		}
		if(!StringUtil.isNullOrEmpty(thirdStage)){
			this.mThirdStage = thirdStage;
		}
		setDownView(mTempUnit);
	}
	
	private void setThirdSeekBarThumb(String tempUnit){
		if(StringUtil.equals(tempUnit, DifferentialSettingFragment.DIFF_TEMP_C)){
			mThirdStageSeekBar.setThumb(SEEKBAR_THUMB_C);
		}else{
			mThirdStageSeekBar.setThumb(SEEKBAR_THUMB_F);
		}
		mThirdStageSeekBar.setThumbOffset(0);
	}
	
	private void setDownView(String tempUnit){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		
		if(StringUtil.equals(tempUnit, DifferentialSettingFragment.DIFF_TEMP_C)){
			view = inflater.inflate(R.layout.device_thermostat82_setting_differential_third_c, null);
			mThirdStageTv = (TextView) view.findViewById(R.id.thermost_setting_diff_third_tv_c);
			mThirdStageSeekBar = (SeekBar) view.findViewById(R.id.thermost_setting_diff_third_seekbar_c);
			mThirdStageSeekBar.setThumb(SEEKBAR_THUMB_C);
			initThirdStageMapC();
		}else{
			view = inflater.inflate(R.layout.device_thermostat82_setting_differential_third_f, null);
			mThirdStageTv = (TextView) view.findViewById(R.id.thermost_setting_diff_third_tv_f);
			mThirdStageSeekBar = (SeekBar) view.findViewById(R.id.thermost_setting_diff_third_seekbar_f);
			mThirdStageSeekBar.setThumb(SEEKBAR_THUMB_F);
			initThirdStageMapF();
		}
		mThirdStageTv.setText(THIRD_SATGE_TEXT);
		mThirdStageSeekBar.setThumbOffset(0);
		if(!StringUtil.isNullOrEmpty(mThirdStage) && !StringUtil.equals(mThirdStage,"00")){
			mThirdStageSeekBar.setProgress(Integer.parseInt(mThirdStage.substring(1,2))-1);
			mThirdStageValue = thirdStageMap.get(mThirdStage);
		}else{
			mThirdStageSeekBar.setProgress(0);
			mThirdStage = "01";
			mThirdStageValue = thirdStageMap.get(mThirdStage);
		}
		mThirdStageSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekbar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				mThirdStage = "0"+(progress+1);
				mThirdStageValue = thirdStageMap.get(mThirdStage);
				Log.i(TAG+":ThirdStageSeekBar", mThirdStage+"-"+mThirdStageValue);
				
			}
		});
	}
	
	private void initThirdStageMapC(){
		thirdStageMap = new HashMap<>();
		thirdStageMap.put("01", "2.5");
		thirdStageMap.put("02", "3");
		thirdStageMap.put("03", "3.5");
		thirdStageMap.put("04", "4");
		thirdStageMap.put("05", "4.5");
		thirdStageMap.put("06", "5");
	}
	private void initThirdStageMapF(){
		thirdStageMap = new HashMap<>();
		thirdStageMap.put("01", "5");
		thirdStageMap.put("02", "6");
		thirdStageMap.put("03", "7");
		thirdStageMap.put("04", "8");
		thirdStageMap.put("05", "9");
		thirdStageMap.put("06", "10");
	}
	
	public void setThirdStageDown() {
		setDownView(DifferentialSettingFragment.DIFF_TEMP_C);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
