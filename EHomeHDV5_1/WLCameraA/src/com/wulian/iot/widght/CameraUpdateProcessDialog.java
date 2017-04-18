package com.wulian.iot.widght;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wulian.icam.R;
import com.yuantuo.customview.ui.WLFullScreenDialog;

public class CameraUpdateProcessDialog {

	private View rootView;
	private ProgressBar percentProgessBar ;
	private TextView percentTextView;
	private WLFullScreenDialog fullScreenDialog;
	public CameraUpdateProcessDialog(Context context){
		rootView = LayoutInflater.from(context).inflate(R.layout.camera_setting_update_progress, null);
		this.percentProgessBar = (ProgressBar)rootView.findViewById(R.id.pb_camera_update);
		this.percentTextView = (TextView)rootView.findViewById(R.id.tv_camera_update_percent);
		WLFullScreenDialog.Builder builder = new WLFullScreenDialog.Builder(context);
		builder.setContentView(rootView);
		this.fullScreenDialog =builder.createFullScreenDialog();
	}
	public void show(){
		if(this.fullScreenDialog != null){
			this.fullScreenDialog.show();
		}
	}
	public void setProgess(int progress){
		if(this.percentProgessBar != null){
			this.percentProgessBar.setProgress(progress);
			percentTextView.setText(progress+"%");
		}
	}
	public void dismiss(){
		if(this.fullScreenDialog != null){
			this.fullScreenDialog.dismiss();
		}
	}
	
}
