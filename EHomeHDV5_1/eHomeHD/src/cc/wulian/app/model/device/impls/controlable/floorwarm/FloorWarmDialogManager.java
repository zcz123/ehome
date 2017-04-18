package cc.wulian.app.model.device.impls.controlable.floorwarm;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;

public class FloorWarmDialogManager {

	private static FloorWarmDialogManager instance;
	private Dialog resetDialog;
	private Dialog successDialog;
	private Dialog failedDialog;
	private ProgressDialogManager mDialogManager = ProgressDialogManager
			.getDialogManager();

	private static final int DRAWBLE_RESET_LOAD = R.drawable.thermost_program_loading;
	private static final int DRAWBLE_RESET_SUCCESS = R.drawable.thermost_program_correct;
	private static final int DRAWBLE_RESET_FAILED= R.drawable.thermost_program_error;
	
	public static FloorWarmDialogManager getDialogManager(){
		if (instance == null) instance = new FloorWarmDialogManager();
		return instance;
	}
	
	public void showResetDialog(Context context){
		resetDialog = mDialogManager.createLoadingDialog(context, "");
		LayoutInflater inflater = resetDialog.getLayoutInflater();
		
		View v = inflater.inflate(R.layout.device_thermostat82_setting_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.thermostat_dialog_view);// 加载布局
		// main.xml中的ImageView
		ImageView dialogImage = (ImageView) v.findViewById(R.id.thermostat_dialog_img);
		TextView dialogTextView = (TextView) v.findViewById(R.id.thermostat_dialog_tv);// 提示文字
		dialogImage.setBackgroundResource(DRAWBLE_RESET_LOAD);
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.load_animation);
		// 使用ImageView显示动画
		dialogImage.startAnimation(hyperspaceJumpAnimation);
		dialogTextView.setText("");// 设置加载信息
		resetDialog.setCancelable(false);// 不可以用“返回键”取消
		resetDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		
		resetDialog.show();
	}
	
	public void dismissResetDialog(){
		if(resetDialog.isShowing()){
			resetDialog.dismiss();
		}
	}
	
	public void createSuccessDialog(Context context){
		successDialog = mDialogManager.createLoadingDialog(context, "");
		LayoutInflater inflater = successDialog.getLayoutInflater();
		View v = inflater.inflate(R.layout.device_thermostat82_setting_dialog, null);
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.thermostat_dialog_view);
		ImageView dialogImage = (ImageView) v.findViewById(R.id.thermostat_dialog_img);
		TextView dialogTextView = (TextView) v.findViewById(R.id.thermostat_dialog_tv);
		dialogImage.setBackgroundResource(DRAWBLE_RESET_SUCCESS);
		dialogTextView.setText(context.getResources().getString(R.string.common_setting_success));
		successDialog.setCancelable(false);
		successDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		
	}
	
	public boolean isResetShowing(){
		if(resetDialog.isShowing()){
			return true;
		}else{
			return false;
		}
	}
	
	public void showSuccessDialog(Context context){
		if(resetDialog.isShowing()){
			resetDialog.dismiss();
			createSuccessDialog(context);
			if(!successDialog.isShowing()){
				successDialog.show();
			}
		}
	}
	
	public void dismissSuccessDialog(){
		if(successDialog.isShowing()){
			successDialog.dismiss();
		}
	}
	
	public void createFailedDialog(Context context){
		failedDialog = mDialogManager.createLoadingDialog(context, "");
		LayoutInflater inflater = failedDialog.getLayoutInflater();
		View v = inflater.inflate(R.layout.device_thermostat82_setting_dialog, null);
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.thermostat_dialog_view);
		ImageView dialogImage = (ImageView) v.findViewById(R.id.thermostat_dialog_img);
		TextView dialogTextView = (TextView) v.findViewById(R.id.thermostat_dialog_tv);
		dialogImage.setBackgroundResource(DRAWBLE_RESET_FAILED);
		dialogTextView.setText(context.getResources().getString(R.string.common_setting_fail));
		failedDialog.setCancelable(false);
		failedDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		
	}
	
	public void showFailedDialog(Context context){
		if(resetDialog.isShowing()){
			resetDialog.dismiss();
			createFailedDialog(context);
			if(!failedDialog.isShowing()){
				failedDialog.show();
			}
		}
	}
	
	public void diamissFialedDialog(){
		if(failedDialog != null){
			
			if(failedDialog.isShowing()){
				failedDialog.dismiss();
			}
		}
	}
	
	
	
}
