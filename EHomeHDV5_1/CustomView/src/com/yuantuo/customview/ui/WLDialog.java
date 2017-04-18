package com.yuantuo.customview.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuantuo.customview.R;

public class WLDialog extends Dialog {

	public static final String TAG = WLDialog.class.getSimpleName();

	public WLDialog(Context context) {
		super(context);
	}

	public WLDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {

		private Context mContext;
		private String mTitleText;
		private String subTitleText;
		private String mMessageText;
		private String mPositiveButtonText;
		private String mNegativeButtonText;
		private View mContentView;
		private MessageListener mListener;
		private boolean cancelOnTouchOutSide = true;
		private int mWidth = -1;
		private int mHeight = -1;
		private float mWidthPercent = 0.8f;
		private float mHeightPercent = -1f;
		private boolean isDismission = true;
		public Builder(Context context) {
			this.mContext = context;
		}

		public Builder setSubTitleText(String mBottomTitleText) {
			this.subTitleText = mBottomTitleText;
			return this;
		}

		public Builder setSubTitleText(int mBottomTitleText) {
			this.subTitleText = mContext.getString(mBottomTitleText);
			return this;
		}
		
		public Builder setTitle(int title) {
			this.mTitleText = mContext.getString(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.mTitleText = title;
			return this;
		}
		public Builder setDismissAfterDone(boolean isDismiss) {
			this.isDismission = isDismiss;
			return this;
		}
		public Builder setMessage(String message) {
			this.mMessageText = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.mMessageText = mContext.getString(message);
			return this;
		}

		public boolean isCancelOnTouchOutSide() {
			return cancelOnTouchOutSide;
		}

		public Builder setCancelOnTouchOutSide(boolean cancelOnTouchOutSide) {
			this.cancelOnTouchOutSide = cancelOnTouchOutSide;
			return this;
		}

		public Builder setContentView(int resLayout) {
			View view = LayoutInflater.from(this.mContext).inflate(resLayout,
					null);
			this.mContentView = view;
			return this;
		}

		public Builder setContentView(View v) {
			this.mContentView = v;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText) {
			this.mPositiveButtonText = mContext.getString(positiveButtonText);
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText) {
			this.mPositiveButtonText = positiveButtonText;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText) {
			this.mNegativeButtonText = mContext.getString(negativeButtonText);
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText) {
			this.mNegativeButtonText = negativeButtonText;
			return this;
		}

		public MessageListener getListener() {
			return this.mListener;
		}

		public Builder setListener(MessageListener listener) {
			this.mListener = listener;
			return this;
		}

		public int getWidth() {
			return mWidth;
		}

		public Builder setWidth(int width) {
			mWidth = width;
			return this;
		}

		public int getHeight() {
			return mHeight;
		}

		public Builder setHeight(int height) {
			mHeight = height;
			return this;
		}

		public float getWidthPercent() {
			return mWidthPercent;
		}

		public Builder setWidthPercent(float widthPercent) {
			mWidthPercent = widthPercent;
			return this;
		}

		public float getHeightPercent() {
			return mHeightPercent;
		}

		public Builder setHeightPercent(float heightPercent) {
			mHeightPercent = heightPercent;
			return this;
		}
		
		/**
		 * 生成并返回自定义的Dialog
		 * 
		 * @return
		 */
		public WLDialog create() {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			WLDialog dialog = new WLDialog(mContext, R.style.dialog_style_v5);
			/*Window window = dialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			lp.dimAmount = 0.2f;*/

			View view = inflater.inflate(R.layout.layout_dialog_v5, null);
			dialog.setContentView(view, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			initTitle(dialog, view);
			initSubTitle(dialog, view);
			initContent(dialog, view);
			initButton(dialog, view);

			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);

			initSize(dialog, dm.widthPixels, dm.heightPixels);
			dialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					hideInput(mContext);
				}
			});
			dialog.setContentView(view);
			dialog.setCanceledOnTouchOutside(cancelOnTouchOutSide);
			return dialog;
		}
		
		/**
		 * 初始化Dialog标题下面小标题部分
		 * 
		 * @param dialog
		 * @param view
		 */
		private void initSubTitle(WLDialog dialog, View view) {

			TextView titleBottomTextView = (TextView) view
					.findViewById(R.id.dialog_tv_title_bottom);

			if (TextUtils.isEmpty(this.subTitleText)) {
				titleBottomTextView.setVisibility(View.GONE);
			} else {
				titleBottomTextView.setVisibility(View.VISIBLE);
				titleBottomTextView.setText(this.subTitleText);
			}
		}
		/**
		 * 初始化Dialog标题部分
		 * 
		 * @param dialog
		 * @param view
		 */
		private void initTitle(WLDialog dialog, View view) {

			TextView titleTextView = (TextView) view
					.findViewById(R.id.dialog_tv_title);

			if (TextUtils.isEmpty(this.mTitleText)) {
				titleTextView.setVisibility(View.GONE);
			} else {
				titleTextView.setText(this.mTitleText);
			}
		}

		/**
		 * 初始化Dialog内容区域
		 * 
		 * @param dialog
		 * @param view
		 */
		private void initContent(WLDialog dialog, View view) {

			if (!TextUtils.isEmpty(this.mMessageText)) {

				TextView messageTextView = (TextView) view
						.findViewById(R.id.dialog_tv_message);
				messageTextView.setText(mMessageText);
			} else if (mContentView != null) {
				LinearLayout lcontentLayout = (LinearLayout) view
						.findViewById(R.id.dialog_layout_content);
				lcontentLayout.removeAllViews();
				lcontentLayout.addView(mContentView, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
		}

		/**
		 * 初始化Dialog按钮区域
		 * 
		 * @param dialog
		 * @param view
		 */
		private void initButton(final WLDialog dialog, final View view) {

			boolean noButton = true;
			TextView positiveButton = (TextView) view
					.findViewById(R.id.dialog_btn_positive);
			if (mPositiveButtonText != null) {
				noButton = false;
				positiveButton.setText(mPositiveButtonText);
				positiveButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mListener != null) {
							mListener.onClickPositive(view);
						}
						if(isDismission){
							dialog.dismiss();
						}
					}
				});
			} else {
				positiveButton.setVisibility(View.GONE);
			}

			TextView negativeButton = (TextView) view
					.findViewById(R.id.dialog_btn_negative);
			if (mNegativeButtonText != null) {
				noButton = false;
				negativeButton.setText(mNegativeButtonText);
				negativeButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mListener != null) {
							mListener.onClickNegative(view);
						}
						dialog.dismiss();
					}
				});
			} else {
				negativeButton.setVisibility(View.GONE);
			}

			View divider = view.findViewById(R.id.dialog_divider);

			if (!TextUtils.isEmpty(mNegativeButtonText)
					&& !TextUtils.isEmpty(mPositiveButtonText)) {
				divider.setVisibility(View.VISIBLE);
			} else if (TextUtils.isEmpty(mNegativeButtonText)
					&& TextUtils.isEmpty(mPositiveButtonText)) {
				divider.setVisibility(View.GONE);
			} else {
				divider.setVisibility(View.GONE);

				if (!TextUtils.isEmpty(mNegativeButtonText)) {
					negativeButton.setBackgroundResource(R.drawable.dialog_btn);
				} else {
					positiveButton.setBackgroundResource(R.drawable.dialog_btn);
				}
			}

			if (noButton)
				view.findViewById(R.id.dialog_layout_btn).setVisibility(
						View.GONE);
		}
		public void hideInput(Context context){
			try{
				InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);  
				if (imm != null && imm.isActive()) { 
					Activity activity = (Activity)context; 
					imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(),0);
				} 
			}catch(Exception e){
				
			}
		}
		/**
		 * 初始化Dialog大小
		 * 
		 * @param dialog
		 * @param view
		 */
		private void initSize(final WLDialog dialog, int screenWidth,
				int screenHeight) {
			WindowManager.LayoutParams winParams = dialog.getWindow()
					.getAttributes();
			if (this.mHeight > 0) {
				winParams.height = this.mHeight;
			} else if (this.mHeightPercent > 0) {
				winParams.height = (int) (this.mHeightPercent * screenHeight);
			}

			if (this.mWidth > 0) {
				winParams.width = this.mWidth;
			} else if (this.mWidthPercent > 0) {
				winParams.width = (int) (this.mWidthPercent * screenWidth);
			}
			dialog.getWindow().setAttributes(winParams);
		}
	}

	/**
	 * Dialog消息监听器
	 * 
	 * @author gaoxy
	 * 
	 */
	public interface MessageListener {

		public void onClickPositive(View contentViewLayout);

		public void onClickNegative(View contentViewLayout);
	}

}
