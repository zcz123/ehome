package cc.wulian.smarthomev5.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import cc.wulian.smarthomev5.R;

import com.yuantuo.customview.ui.CustomDialog;
//import android.widget.TextView;

public class InputDialog
{
	public interface OnInputDialogSubmitListener
	{
		/**
		 * @param dialogInterface
		 * @param input
		 *          has trimed and difference from the defaultValue
		 * @param actionID
		 *          who call this function
		 */
		public void onInputSubmit( DialogInterface dialogInterface, String input, int actionID );
	}

	private final Context mContext;
	private OnInputDialogSubmitListener mListener;
	private int mInputType = EditorInfo.TYPE_CLASS_TEXT;
	private Dialog mDialog;

	public InputDialog( Context mContext )
	{
		this.mContext = mContext;
	}

	public void setInputType( int type ){
		mInputType = type;
	}

	public void setOnInputDialogSubmitListener( OnInputDialogSubmitListener mListener ){
		this.mListener = mListener;
	}

	public void showInputDialog( int actionID, String defaultValue ){
		showInputDialog(0, android.R.string.dialog_alert_title, true, actionID, defaultValue);
	}

	public void showInputDialog( int icon, int title, boolean click2Dimiss, final int actionID,
			final String defaultValue ){
		View view = View.inflate(mContext, R.layout.common_content_edittext, null);
		//TextView hintTextView = (TextView) view.findViewById(R.id.textView_hint);
		final EditText inputEt = (EditText) view.findViewById(R.id.editText_phonenum);
		inputEt.setFilters(new InputFilter[]{
				new InputFilter.LengthFilter(16)
		});
		if (!TextUtils.isEmpty(defaultValue)) inputEt.setText(defaultValue);
		inputEt.setInputType(mInputType);

		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick( DialogInterface arg0, int arg1 ){
				String numStr = inputEt.getText().toString().trim();
				if (mListener != null && !TextUtils.isEmpty(numStr) && !TextUtils.equals(numStr, defaultValue)){
					mListener.onInputSubmit(arg0, numStr, actionID);
				}
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		if (icon != 0) builder.setIcon(icon);
		if (title != 0) builder.setTitle(title);
		builder.setContentView(view);
		builder.setAutoDismiss(click2Dimiss);
		mDialog = builder.create(false, false);
		mDialog.show();
	}

	public void dimiss(){
		if (mDialog != null){
			mDialog.dismiss();
		}
	}
}