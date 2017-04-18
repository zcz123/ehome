package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.ThermostatSettingFragment;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential.DifferentialSettingActivity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class DifferentialSettingDownItem extends AbstractSettingItem{
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private TextView differentialTv;
	private Button differentialBtn;
	/**
	 * 温度校正数据
	 */
	private String mTempratureFormat;

	private static final String DIFF_TEXT = "Incrrect equipment setting will cause unexpected HVAC activity or equipment damage.";
	private static final String DIFF_BTN_TEXT = "continue";

	public DifferentialSettingDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Differential Setting");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setDefferentialSettingDown();
	}
	
	public void setDefferentialSettingData(String gwId,String devId,String ep,String epType){
		this.mGwId = gwId;
		this.mDevId = devId;
		this.mEp = ep;
		this.mEpType = epType;
	}
	
	public void setmTempratureFormat(String mTempratureFormat) {
		this.mTempratureFormat = mTempratureFormat;
	}

	public void setDefferentialSettingDown() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_other, null);
		differentialTv = (TextView) view.findViewById(R.id.thermost_setting_other_tv);
		differentialBtn = (Button) view.findViewById(R.id.thermost_setting_other_btn);
		differentialTv.setText(DIFF_TEXT);
		differentialBtn.setText(DIFF_BTN_TEXT);
		
		differentialBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				jumpToDifferentialSettingActivity();
			}
		});
		
	}
	
	private void jumpToDifferentialSettingActivity(){
		Intent intent = new Intent(mContext, DifferentialSettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ThermostatSettingFragment.GWID, mGwId);
		bundle.putString(ThermostatSettingFragment.DEVID, mDevId);
		bundle.putString(ThermostatSettingFragment.EP, mEp);
		bundle.putString(ThermostatSettingFragment.EPTYPE, mEpType);
		bundle.putString("tempUnit", mTempratureFormat);
		intent.putExtra("DifferentialSettingInfo", bundle);
		mContext.startActivity(intent);
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
