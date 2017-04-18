package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;


import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.yuantuo.customview.ui.ToastProxy;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmDialogManager;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.SendMessage;

public class FactoryResetItem extends AbstractSettingItem {
	
	private String gwID;
	private String devID;
	private String ep;
	private String epType;
	private WLDialog factoryResetDialog;
	private boolean isReset;
	private FloorWarmDialogManager dialogManager = FloorWarmDialogManager.getDialogManager();

	
	public FactoryResetItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.AP_reset_device));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFactoryReset();
	}

	private void setFactoryReset() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
	}
	
	public void setFactoryResetData(String gwID,String devID,String ep,String epType){
		this.gwID = gwID;
		this.devID = devID;
		this.ep = ep;
		this.epType = epType;
	}

	public void showResetResult(String result){
		if(!StringUtil.isNullOrEmpty(result)){
			if(StringUtil.equals(result, FloorWarmUtil.RESET_SUCCESS_CMD)){
				WLToast.showToast(mContext,mContext.getResources().getString(R.string.AP_reset_success), WLToast.TOAST_SHORT);
			}
			else if(StringUtil.equals(result, FloorWarmUtil.RESET_FAILED_CMD)){
				WLToast.showToast(mContext,mContext.getResources().getString(R.string.AP_reset_faild), WLToast.TOAST_SHORT);
			}
		}
	}

	@Override
	public void doSomethingAboutSystem() {
		
		showFactoryResetDialog();
	}
	
	private void showFactoryResetDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(mContext.getResources().getString(R.string.operation_title))
				.setSubTitleText(null)
				.setMessage(mContext.getResources().getString(R.string.AP_reset_hint))
				.setNegativeButton(mContext.getResources().getString(R.string.common_cancel))
				.setPositiveButton(mContext.getResources().getString(R.string.common_ok))
				.setListener(new WLDialog.MessageListener() {
					@Override
					public void onClickPositive(View view) {

						SendMessage.sendControlDevMsg(gwID, devID, ep, epType, FloorWarmUtil.RESET_CMD);
						isReset = true;
					}

					@Override
					public void onClickNegative(View view) {
						isReset = false;
					}
				});
		factoryResetDialog = builder.create();
		factoryResetDialog.show();
	}
	 
}
