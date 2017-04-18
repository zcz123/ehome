package cc.wulian.smarthomev5.tools;

import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
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
import cc.wulian.smarthomev5.collect.Maps;

import com.yuantuo.customview.ui.CustomProgressDialog;
import com.yuantuo.customview.ui.CustomProgressDialog.OnDialogDismissListener;

public class ProgressDialogManager
{
	private static ProgressDialogManager instance;

	private Map<String, CustomProgressDialog> dialogMap = Maps.newHashMap();

	private ProgressDialogManager()
	{
	}
	/**
	 * 得到自定义的progressDialog
	 * @param context
	 * @param msg
	 * @return
	 */
	public Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.load_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息

		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		loadingDialog.setCancelable(true);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;
	}
	public static ProgressDialogManager getDialogManager(){
		if (instance == null) instance = new ProgressDialogManager();
		return instance;
	}

	public Dialog showDialog( String key, Context context, String msg,
			OnDialogDismissListener listener, int timeout ){
		return showDialog(key, context, msg, null, listener, timeout);
	}

	public Dialog showDialog( String key, Context context, String msg,String cancle,
			OnDialogDismissListener listener ){
		return showDialog(key, context, msg,cancle,listener, CustomProgressDialog.DELAYMILLIS_15);
	}
	public Dialog showDialog( String key, Context context, String msg,
			OnDialogDismissListener listener ){
		return showDialog(key, context, msg,null,listener, CustomProgressDialog.DELAYMILLIS_20);
	}
	
	public Dialog showDialog( String key, Context context, String msg,String cancle,
			OnDialogDismissListener listener,int timeout ){
		CustomProgressDialog dialog = dialogMap.get(key);
		if(dialog != null){
			try{
				dialog.dismissProgressDialog();
			}catch(Exception e){
				
			}
		}
		dialogMap.remove(key);
		dialog = new CustomProgressDialog((Activity) context);
		dialog.setProgressDrawable(R.drawable.common_loading_icon);			
		if(cancle!=null){
			 dialog.setCancleDialog(cancle);
		}
		dialogMap.put(key, dialog);
		dialog.showDialog(key, msg,timeout);
		if (listener != null) 
			dialog.setOnDialogDismissListener(listener);
		return dialog;
		
	}

	public boolean containsDialog( String key ){
		return dialogMap.get(key) != null;
	}
	
	public void dimissDialog( String key, int keycode ){
		CustomProgressDialog dialog = dialogMap.get(key);
		dialogMap.remove(key);
		if(dialog != null){
			dialog.dismissProgressDialog(keycode);
		}
	}
}