package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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

public class FirstStageDownItem extends AbstractSettingItem{
	
	private final String TAG = getClass().getSimpleName();
	private TextView mFirstStageTv;
	private SeekBar mFirstStageSeekBar;
	private String mFirstStage;
	private String mFirstStageValue;
	private String mTempUnit = "00";
	private HashMap<String, String> firstStageMap;
	//拖动条是否拖动
	private boolean isProgressMoved = false;
	
	private Drawable SEEKBAR_THUMB_C = mContext.getResources().getDrawable(R.drawable.thermost_setting_icon_c);
	private Drawable SEEKBAR_THUMB_F = mContext.getResources().getDrawable(R.drawable.thermost_setting_icon_f);
	
	private static final String FIRST_SATGE_TEXT = "This option is used to adjust the heating and cooling temperature"
			+ "differential.This represent the temperature above the cooding setpoint or below the heating setpoint"
			+ "when the equipment is power on.";

	public FirstStageDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "First Stage Option");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFirstStageDown();
	}
	
	public String getmFirstStage() {
		return mFirstStage;
	}
	
	public String getmFirstStageValue() {
		return mFirstStageValue;
	}
	
	public void setFirstStageData(String tempUnit,String firstStage){
		if(!StringUtil.isNullOrEmpty(tempUnit)){
			this.mTempUnit = tempUnit;
			Log.i(TAG+":mTempUnit11", mTempUnit);
		}
		if(!StringUtil.isNullOrEmpty(firstStage)){
			this.mFirstStage = firstStage;
		}
		if(!isProgressMoved){
			setDownView(mTempUnit);
		}
	}

	private void setDownView(String tempUnit){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		Log.i(TAG+":mTempUnit22", mTempUnit);
		if(StringUtil.equals(tempUnit, DifferentialSettingFragment.DIFF_TEMP_C)){
			view = inflater.inflate(R.layout.device_thermostat82_setting_differential_first_c, null);
			mFirstStageTv = (TextView) view.findViewById(R.id.thermost_setting_diff_first_tv_c);
			mFirstStageSeekBar = (SeekBar) view.findViewById(R.id.thermost_setting_diff_first_seekbar_c);
			mFirstStageSeekBar.setThumb(SEEKBAR_THUMB_C);
			initFirstStageMapC();
		}else{
			view = inflater.inflate(R.layout.device_thermostat82_setting_differential_first_f, null);
			mFirstStageTv = (TextView) view.findViewById(R.id.thermost_setting_diff_first_tv_f);
			mFirstStageSeekBar = (SeekBar) view.findViewById(R.id.thermost_setting_diff_first_seekbar_f);
			mFirstStageSeekBar.setThumb(SEEKBAR_THUMB_F);
			initFirstStageMapF();
		}
		mFirstStageSeekBar.setThumbOffset(0);
		if(!StringUtil.isNullOrEmpty(mFirstStage) && !StringUtil.equals(mFirstStage,"00")){

			mFirstStageSeekBar.setProgress(Integer.parseInt(mFirstStage.substring(1,2))-1);
			mFirstStageValue = firstStageMap.get(mFirstStage);

		}else{
			mFirstStageSeekBar.setProgress(0);
			mFirstStage = "01";
			mFirstStageValue = firstStageMap.get(mFirstStage);
		}
		mFirstStageTv.setText(FIRST_SATGE_TEXT);
		mFirstStageSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekbar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				isProgressMoved = true;
				mFirstStage = "0"+(progress+1);
				mFirstStageValue = firstStageMap.get(mFirstStage);
				Log.i(TAG+":SeekBar", mFirstStage+"-"+mFirstStageValue);
			}
		});
	}
	
	private void initFirstStageMapC(){
		firstStageMap = new HashMap<>();
		firstStageMap.put("01", "0.2");
		firstStageMap.put("02", "0.5");
		firstStageMap.put("03", "0.8");
		firstStageMap.put("04", "1");
		firstStageMap.put("05", "1.2");
		firstStageMap.put("06", "1.5");
		firstStageMap.put("07", "1.8");
		firstStageMap.put("08", "2");
	}
	private void initFirstStageMapF(){
		firstStageMap = new HashMap<>();
		firstStageMap.put("01", "0.5");
		firstStageMap.put("02", "1");
		firstStageMap.put("03", "1.5");
		firstStageMap.put("04", "2");
		firstStageMap.put("05", "2.5");
		firstStageMap.put("06", "3");
		firstStageMap.put("07", "3.5");
		firstStageMap.put("08", "4");
	}
	
	public void setFirstStageDown() {
		setDownView(DifferentialSettingFragment.DIFF_TEMP_C);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
