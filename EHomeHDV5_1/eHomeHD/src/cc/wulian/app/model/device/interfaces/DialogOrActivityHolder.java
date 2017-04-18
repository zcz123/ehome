package cc.wulian.app.model.device.interfaces;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cc.wulian.app.model.device.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class DialogOrActivityHolder {

	private boolean isShowDialog = true;
	public String dialogTitle;
	private String fragementTitle;
	private View contentView;
	
	public View getContentView() {
		return contentView;
	}

	public void setContentView(View contentView) {
		this.contentView = contentView;
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}
	public boolean isShowDialog() {
		return isShowDialog;
	}

	public void setShowDialog(boolean isShowDialog) {
		this.isShowDialog = isShowDialog;
	}

	public String getFragementTitle() {
		return fragementTitle;
	}

	public void setFragementTitle(String fragementTitle) {
		this.fragementTitle = fragementTitle;
	}
	public Dialog createSelectControlDataDialog(Context context, View contentView,MessageListener messageListener) {
		WLDialog.Builder builder = createDefaultDialogBuilder(context,
				contentView);
		builder.setListener(messageListener);
		WLDialog dialog = builder.create();
		return dialog;
	}
	private WLDialog.Builder createDefaultDialogBuilder(Context context,
			View contentView) {
		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setTitle(this.dialogTitle);
		builder.setContentView(contentView);
		builder.setPositiveButton(context.getString(R.string.device_ok));
		builder.setNegativeButton(context.getString(R.string.device_cancel));
		return builder;
	}
	public void startActivity(Context context,Bundle bundle){
		Intent intent = new Intent(context,DeviceSettingActivity.class);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		context.startActivity(intent);
	}
}
