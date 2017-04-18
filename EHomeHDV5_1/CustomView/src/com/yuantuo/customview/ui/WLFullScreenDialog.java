package com.yuantuo.customview.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;

import com.yuantuo.customview.R;

public class WLFullScreenDialog extends Dialog {

	public static final String TAG = WLFullScreenDialog.class.getSimpleName();

	public WLFullScreenDialog(Context context) {
		super(context);
	}

	public WLFullScreenDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {

		private Context mContext;
		private View mContentView;
		public Builder(Context context) {
			this.mContext = context;
		}

		public Builder setContentView(View view) {
			this.mContentView = view;
			return this;
		}
		
		public WLFullScreenDialog createFullScreenDialog(){
			WLFullScreenDialog dialog = new WLFullScreenDialog(mContext, R.style.dialog_style_fullscreen);
			dialog.setContentView(mContentView);
			dialog.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(KeyEvent.KEYCODE_BACK == keyCode){
						dialog.dismiss();
						return true;
					}
					return false;
				}
			});
			return dialog;
		}
	}
}
