package com.yuantuo.customview.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuantuo.bean.SingleErrorDialog;

import com.yuantuo.customview.R;
import com.yuantuo.customview.ui.WLDialog.Builder;

public class CustomProgressDialog extends Dialog
{
	public static final int DELAYMILLIS_5 = 5 * 1000;
	public static final int DELAYMILLIS_10 = 10 * 1000;
	public static final int DELAYMILLIS_15 = 15 * 1000;
	public static final int DELAYMILLIS_20 = 20 * 1000;
	public static final int DELAYMILLIS_25 = 25 * 1000;
	public static final int DELAYMILLIS_30 = 30 * 1000;
	public static final int DELAYMILLIS_40 = 40 * 1000;
	public static final int DELAYMILLIS_120 = 120 * 1000;
	public static final int MSG_DISMISS_DELAY = 1221;
    
	public static boolean isCancle = true;
	public static final int resultOk = 0;
	public static final int resultFail = -1;
	public static final int resultCancel = -2;
	
	private final Activity activity;
	private ImageView mImageView;
	private TextView mTextView;
	private Button mButton;
	private Animation animation;
	private Runnable dialogRunnable = new Runnable()
	{
		@Override
		public void run(){
			isCancle = false;
			dismissProgressDialog(resultFail);
		}
	};
	private final Handler childHandler = new Handler(Looper.getMainLooper());


	private OnDialogDismissListener newDialogDismissListener = new OnDialogDismissListener()
	{
		@Override
		public void onDismiss( CustomProgressDialog progressDialog, int result ){
			
			if (resultFail == result){
				try{
					showErrorDialog();
				}
				catch(Exception e){
					
				}
				isCancle = true;
			}
		}

	};
	private void showErrorDialog() {
//		Builder builder = new Builder(getContext());
//		builder.setNegativeButton(null);
//		builder.setPositiveButton(R.string.switch_off);
//		builder.setContentView(R.layout.dialog_error_content);
//		WLDialog  dialog = builder.create();
//		if (!dialog.isShowing()){//add mabo
//			dialog.show();
//		}
		//修改 2016年12月13日14:23:10 mabo 这个提醒超时的dialog需要单例来管理，解决出现多个dialog的问题
		SingleErrorDialog.getInstant().showErrorDialog(getContext());
	}

	public CustomProgressDialog( Activity activity )
	{
		super(activity, R.style.customProgressDialog);
		this.activity = activity;
		initDialog();
	}

	private void initDialog(){
	   View contentView = initContentView();
		setContentView(contentView);
		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lp.dimAmount = 0.6f;
		lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		setCancelable(false);
		setOnCancelListener(onCancelListener);
	}

	private View initContentView(){
		View contentView = View.inflate(activity, R.layout.general_progress_dialog, null);
		mImageView = (ImageView) contentView.findViewById(R.id.loadingProgress);
		mTextView = (TextView) contentView.findViewById(R.id.textView_loadingmsg);
		mButton = (Button)contentView.findViewById(R.id.button_cancel);
				mButton.setOnClickListener(
				new Button.OnClickListener()
				{				
					@Override
					public void onClick( View arg0 )
					{
						cancel();
					}
				});
		animation = AnimationUtils.loadAnimation(activity, R.anim.progressdialog_anim);
		LinearInterpolator linearInterpolator = new LinearInterpolator();
		animation.setInterpolator(linearInterpolator);
//		mImageView.startAnimation(animation);
		return contentView;
	}
	
	public void setProgressDrawable( int res ){
		setProgressDrawable(getActivity().getResources().getDrawable(res));
	}

	public void setProgressDrawable( Drawable drawable ){
		mImageView.setImageDrawable(drawable);
	}
	public void setCancleDialog( String string ){
		mButton.setText(string);
		mButton.setVisibility(View.VISIBLE);
	}

	public void setCancleButtonBackground( int res ){
		mButton.setBackgroundResource(res);
	}

	public void setCancleButtonTextSize( float size ){
		mButton.setTextSize(size);
	}

	public void setCancleButtonTextColor( int color ){
		mButton.setTextColor(color);
	}

	public Activity getActivity(){
		return activity;
	}
  
	@Override
	public void onWindowFocusChanged( boolean hasFocus ){
		if (hasFocus){
			mImageView.startAnimation(animation);
		}
		else{
			super.onWindowFocusChanged(hasFocus);
		}
	}
	public void setOnDialogDismissListener( OnDialogDismissListener newDialogDismissListener ){
		this.newDialogDismissListener = newDialogDismissListener;
	}
	private final DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener()
	{
		@Override
		public void onCancel( DialogInterface dialog ){
			dismissProgressDialog(resultCancel);
		}
	};


	private void postDialogDelayRunnableMsg( int timeOut ){
		childHandler.postDelayed(dialogRunnable, timeOut);
	}

	public void showDialog( String key ){
		showDialog(key, null, DELAYMILLIS_15);
	}

	public void showDialog( String key, String message ){
		showDialog(key, message, DELAYMILLIS_15);
	}

	public void showDialog( String key, int timeOut ){
		showDialog(key, null,timeOut);
	}

	public void showDialog( String key, String message,int timeOut ){
		try{
			if (!isShowing()){
				postDialogDelayRunnableMsg(timeOut);
				show();
				mTextView.setText(message);
				mTextView.setGravity(Gravity.CENTER);
			}
			else{
				if (!TextUtils.equals(message, mTextView.getText().toString())){
					mTextView.setText(message);
					
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public void dismissProgressDialog(){
		dismissProgressDialog(resultOk);
	}

	/**
	 * success=0,failed=-1
	 */
	public void dismissProgressDialog( int keyCode ){
		if (isShowing()){
			try{
				childHandler.removeCallbacks(dialogRunnable);
				newDialogDismissListener.onDismiss(this, keyCode);
				dismiss();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	public interface OnDialogDismissListener
	{
		public void onDismiss( CustomProgressDialog progressDialog, int result );
	}
}
