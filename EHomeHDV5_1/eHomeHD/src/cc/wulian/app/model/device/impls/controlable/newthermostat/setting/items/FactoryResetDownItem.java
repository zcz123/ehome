package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class FactoryResetDownItem extends AbstractSettingItem{
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private TextView resetTv;
	private Button resetBtn;
	private ResetBtnListener resetBtnListener;
	
	private static final String RESET_TEXT = "This operation will delete all information on the thermostat.";
	private static final String RESET_BTN_TEXT  = "continue";

	public FactoryResetDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Factory Reset");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFactoryReset();
	}
	
	public void setResetBtnListener(ResetBtnListener resetBtnListener) {
		this.resetBtnListener = resetBtnListener;
	}
	
	public void setResetData(String gwId,String devId,String ep,String epType){
		mGwId = gwId;
		mDevId = devId;
		mEp = ep;
		mEpType = epType;
	}

	public void setFactoryReset() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_other, null);
		resetTv = (TextView) view.findViewById(R.id.thermost_setting_other_tv);
		resetBtn = (Button) view.findViewById(R.id.thermost_setting_other_btn);
		resetTv.setText(RESET_TEXT);
		resetBtn.setText(RESET_BTN_TEXT);
		
		resetBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				resetBtnListener.onResetClick();
			}
		});
	}
	
	public interface ResetBtnListener{
		public void onResetClick();
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
