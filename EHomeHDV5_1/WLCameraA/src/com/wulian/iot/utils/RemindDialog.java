package com.wulian.iot.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.wulian.icam.R;

/**
 * 带标题 内容 取消 和 确定的 dialog 此dialog多处用到，因此提供可以修改 标题，内容和点击事件的类 add mabo 2016/8/3
 * */
public class RemindDialog {
	@SuppressWarnings("unused")
	private String title, message;
	private TextView mCancle, mSure, mTitle, mMessage;
	private Dialog mDialog = null;

	public RemindDialog(Context mContext) {
		View mView = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_desk_note_updata, null);
		mCancle = (TextView) mView.findViewById(R.id.tv_dialog_cancle);
		mSure = (TextView) mView.findViewById(R.id.tv_dialog_sure);
		mTitle = (TextView) mView.findViewById(R.id.tv_dialog_title);
		mMessage = (TextView) mView.findViewById(R.id.tv_dialog_message);
		mDialog = new Dialog(mContext);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(mView);
		mCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

	}

	public void showDialog() {
		if (mDialog != null) {
			mDialog.show();
		}
	}

	/**
	 * 设置dialog的宽度，
	 * */
	@SuppressWarnings("deprecation")
	public void setDialogWidth(WindowManager widManager) {
		Display display = widManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
		lp.width = (int) (display.getWidth() * 0.8); // 设置宽度
		mDialog.getWindow().setAttributes(lp);
	}

	/** 获取dialog对象 */
	public Dialog getDialog() {
		if (mDialog != null) {
			return mDialog;
		}
		return null;
	}

	/** 设置标题 */
	public void setTitle(String title) {
		this.title = title;
		mTitle.setText(title);
	}

	/** 设置内容 */
	public void setMessage(String message) {
		this.message = message;
		mMessage.setText(message);
	}

	/** 得到确定按钮 */
	public TextView getTvSuer() {
		if (mSure != null) {
			return mSure;
		}
		return null;
	}

	/** 得到取消按钮， 使对话框消失的事件已完成 */
	public TextView getTTvCancle() {
		if (mCancle != null) {
			return mCancle;
		}
		return null;
	}
}
