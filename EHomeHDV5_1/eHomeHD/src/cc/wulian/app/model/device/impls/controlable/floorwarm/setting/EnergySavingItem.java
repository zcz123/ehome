package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.yuantuo.customview.ui.ToastProxy;
import com.yuantuo.customview.ui.WLDialog;

import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.SendMessage;

public class EnergySavingItem extends AbstractSettingItem {
	
	private String gwID;
	private String devID;
	private String ep;
	private String epType;

	private String energySavingState;
	private String energySavingTemp;
	private String energySavingText = "";
	private WLDialog energySavingDialog;
	private EnergySavingView energySavingView;
	
	private String[] energyTempCValueArray = { "10", "10.5","11", "11.5", "12", "12.5", "13", "13.5", "14", "14.5", 
			"15", "15.5", "16", "16.5", "17", "17.5", "18", "18.5", "19","19.5","20","20.5","21"};

	public EnergySavingItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.AP_enegry_mode));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setEnergySaving();
	}
	
	public void setEnergySavingData(String gwID,String devID,String ep,String epType){
		this.gwID = gwID;
		this.devID = devID;
		this.ep = ep;
		this.epType = epType;
	}
	
	public void setEnergySavingTemp(String state ,String temp) {
		if(!StringUtil.isNullOrEmpty(state)){
			this.energySavingState = state.substring(1,2);
		}
		if(!StringUtil.isNullOrEmpty(temp)){
			this.energySavingTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(temp));
			//温度 小数点后为0，则 显示整数
			this.energySavingText = FloorWarmUtil.getTempFormat(energySavingTemp)+ FloorWarmUtil.TEMP_UNIT_C_TEXT;
		}
		infoTextView.setText(energySavingText);
	}
	
	public void setEnergySaving() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,3, 0);
		infoTextView.setVisibility(View.VISIBLE);
		infoTextView.setLayoutParams(params);
		infoTextView.setSingleLine(false);
		infoTextView.setTextColor(Color.parseColor("#737373"));
		infoTextView.setText(energySavingText);
	}

	@Override
	public void doSomethingAboutSystem() {
		showEnergySavingDialog();
	}
	
	private void showEnergySavingDialog(){

			WLDialog.Builder builder = new WLDialog.Builder(mContext);
			builder.setTitle(mContext.getResources().getString(R.string.AP_enegry_mode));
			builder.setContentView(creatDialogView());
			builder.setPositiveButton(mContext.getResources().getString(R.string.common_ok));
			builder.setNegativeButton(mContext.getResources().getString(R.string.common_cancel));
			builder.setListener(new WLDialog.MessageListener() {
				@Override
				public void onClickPositive(View view) {
					String temp = energySavingView.getSettingTempValue();
					double tempdou = Double.parseDouble(temp) * 100;
					String tempstr = (int) tempdou + "";
					String tempCmd = StringUtil.appendLeft(tempstr, 4, '0');
					if(StringUtil.isNullOrEmpty(energySavingState)){
						ToastProxy.makeText(mContext,"设置失败", ToastProxy.LENGTH_SHORT).show();
						return;
					}
					String sendCmd = FloorWarmUtil.ENERGY_SAVING_CMD + energySavingState + tempCmd ;
					Log.i("energysavingitem","temp:"+temp+",tempCmd:"+tempCmd+",sendCmd:"+sendCmd);
					SendMessage.sendControlDevMsg(gwID, devID, ep, epType, sendCmd);
				}

				@Override
				public void onClickNegative(View view) {

				}
			});
		energySavingDialog = builder.create();
		energySavingDialog.show();
		}
	private View creatDialogView(){
		energySavingView = new EnergySavingView(mContext);
		energySavingView.setSettingTempValue( FloorWarmUtil.getTempFormat(energySavingTemp));
		return energySavingView;
	}
	
	
}
