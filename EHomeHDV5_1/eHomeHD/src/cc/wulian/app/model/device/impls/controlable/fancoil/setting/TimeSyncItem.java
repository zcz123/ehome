package cc.wulian.app.model.device.impls.controlable.fancoil.setting;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.yuantuo.customview.ui.ToastProxy;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import java.util.Date;

import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.SendMessage;

public class TimeSyncItem extends AbstractSettingItem {

	private String gwID;
	private String devID;
	private String ep;
	private String epType;
	private WLDialog syncDialog;
	private boolean isTimeSynced = false;
	private static final String SYNC_MESSAGE = "是否将手机时间同步到设备端?";
	private static final String SYNC_SUCCESS = "设备时间已同步";

	public TimeSyncItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.AP_time_synsize));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setTimeSync();
	}

	public void setTimeSyncData(String gwID,String devID,String ep,String epType){
		this.gwID = gwID;
		this.devID = devID;
		this.ep = ep;
		this.epType = epType;
	}

	public void showSyncResult(String returnId){
		if(!StringUtil.isNullOrEmpty(returnId)){
			if(StringUtil.equals(returnId, FanCoilUtil.TIME_SYNC_TAG) && isTimeSynced){
				WLToast.showToast(mContext,mContext.getResources().getString(R.string.AP_setTime_equal),WLToast.TOAST_SHORT);
			}
		}
	}

	private void setTimeSync() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
	}

	@Override
	public void doSomethingAboutSystem() {
		showTimeSyncDialog();
	}

	private void showTimeSyncDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(mContext.getResources().getString(R.string.operation_title))
				.setSubTitleText(null)
				.setMessage(mContext.getResources().getString(R.string.AP_phoneTime_equal))
				.setNegativeButton(mContext.getResources().getString(R.string.common_cancel))
				.setPositiveButton(mContext.getResources().getString(R.string.common_ok))
				.setListener(new WLDialog.MessageListener() {
					@Override
					public void onClickPositive(View view) {
						String currentTime = FanCoilUtil.TIME_SYNC_FORMAT.format(new Date());
						String timeCmd = getTimeCmd(currentTime);
						SendMessage.sendControlDevMsg(gwID, devID, ep, epType, FanCoilUtil.TIME_SYNC_CMD+timeCmd);
						isTimeSynced = true;
					}

					@Override
					public void onClickNegative(View view) {
						isTimeSynced = false;
					}
				});

		syncDialog = builder.create();
		syncDialog.show();
	}

	private String getTimeCmd(String time){
		String sendTime = time.substring(2,time.length());
		return sendTime;
	}


}
