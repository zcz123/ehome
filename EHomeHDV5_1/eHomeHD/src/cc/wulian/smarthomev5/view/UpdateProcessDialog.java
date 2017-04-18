package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;

import com.yuantuo.customview.ui.WLFullScreenDialog;

public class UpdateProcessDialog {

	private View rootView;
	private ProgressBar percentProgessBar ;
	private TextView percentTextView;
	private WLFullScreenDialog fullScreenDialog;
	public UpdateProcessDialog(Context context){
		rootView = LayoutInflater.from(context).inflate(R.layout.setting_update_progress, null);
		this.percentProgessBar = (ProgressBar)rootView.findViewById(R.id.update_progress_bar);
		this.percentTextView = (TextView)rootView.findViewById(R.id.update_percent_tv);
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
