package cc.wulian.smarthomev5.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.yuantuo.customview.ui.CustomDialog;

public class SubmitDialog
{
	public interface OnSubmitDialogSubmitListener
	{
		public void onDialogSubmit( DialogInterface dialogInterface, int actionID );
	}

	private Context mContext;
	private OnSubmitDialogSubmitListener mListener;
	private Dialog mDialog;

	public SubmitDialog( Context mContext )
	{
		this.mContext = mContext;
	}

	public void setOnSubmitDialogSubmitListener( OnSubmitDialogSubmitListener mListener ){
		this.mListener = mListener;
	}

	public void showSubmitDialog( int message, int actionID ){
		showSubmitDialog(0, 0, message, true, actionID);
	}

	public void showSubmitDialog( int icon, int title, int msg, boolean click2Dimiss,
			final int actionID ){
		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick( DialogInterface dialog, int arg1 ){
				if (mListener != null){
					mListener.onDialogSubmit(dialog, actionID);
				}
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		if (msg != 0) builder.setMessage(msg);
		if (icon != 0) builder.setIcon(icon);
		if (title != 0) builder.setTitle(title);
		builder.setAutoDismiss(click2Dimiss);
		mDialog = builder.create(false, true);
		mDialog.show();
	}

	public void dimiss(){
		if (mDialog != null){
			mDialog.dismiss();
		}
	}
}