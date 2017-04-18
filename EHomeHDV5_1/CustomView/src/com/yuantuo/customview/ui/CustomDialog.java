package com.yuantuo.customview.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yuantuo.customview.R;

public class CustomDialog extends Dialog
{
	public static Set<CustomDialog> mDialogManagerSet = new HashSet<CustomDialog>();

	public interface MessageListener
	{
		public void onClickPositive( int requestCode, DialogInterface dialog );

		public void onClickNeutral( int requestCode, DialogInterface dialog );

		public void onClickNegative( int requestCode, DialogInterface dialog );
	}

	public CustomDialog( Context context )
	{
		super(context);
	}

	public CustomDialog( Context context, int theme )
	{
		super(context, theme);
	}

	public static class Builder
	{
		private final Context mContext;
		private String mTitleText;
		private Drawable mIconDrawable;
		private String mMessageText;
		private String mPositiveButtonText;
		private String mNegativeButtonText;
		private String mMiddleButtonText;
		private CharSequence[] mItemsArr;
		private ListAdapter mAdapter;
		private AdapterView.OnItemClickListener mItemClickListener;
		private View mContentView;
		private DialogInterface.OnClickListener mPositiveButtonClickListener,
				mNegativeButtonClickListener, mMiddleButtonClickListener, mOnClickListListener;
		private ListView mListView;
		private boolean mAutoDismiss;
		private boolean mSingleton;

		public Builder( Context context )
		{
			this.mContext = context;
		}

		public Builder setMessage( String message ){
			this.mMessageText = message;
			return this;
		}

		public Builder setMessage( int message ){
			this.mMessageText = mContext.getString(message);
			return this;
		}

		public Builder setTitle( int title ){
			try{
				this.mTitleText = mContext.getString(title);
			}
			catch (Exception e){
			}
			return this;
		}

		public Builder setTitle( String title ){
			this.mTitleText = title;
			return this;
		}

		public Builder setIcon( int icon ){
			try{
				this.mIconDrawable = mContext.getResources().getDrawable(icon);
			}
			catch (Exception e){
			}
			return this;
		}

		public Builder setIcon( Drawable icon ){
			this.mIconDrawable = icon;
			return this;
		}

		public Builder setContentView( View v ){
			this.mContentView = v;
			return this;
		}

		public Builder setPositiveButton( int positiveButtonText,
				DialogInterface.OnClickListener listener ){
			this.mPositiveButtonText = mContext.getString(positiveButtonText);
			this.mPositiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton( String positiveButtonText,
				DialogInterface.OnClickListener listener ){
			this.mPositiveButtonText = positiveButtonText;
			this.mPositiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton( int negativeButtonText,
				DialogInterface.OnClickListener listener ){
			this.mNegativeButtonText = mContext.getString(negativeButtonText);
			this.mNegativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton( String negativeButtonText,
				DialogInterface.OnClickListener listener ){
			this.mNegativeButtonText = negativeButtonText;
			this.mNegativeButtonClickListener = listener;
			return this;
		}

		public Builder setMiddleButton( int middleButtonText, DialogInterface.OnClickListener listener ){
			this.mMiddleButtonText = mContext.getString(middleButtonText);
			this.mMiddleButtonClickListener = listener;
			return this;
		}

		public Builder setMiddleButton( String middleButtonText,
				DialogInterface.OnClickListener listener ){
			this.mMiddleButtonText = middleButtonText;
			this.mMiddleButtonClickListener = listener;
			return this;
		}

		public Builder setItems( CharSequence[] items, final DialogInterface.OnClickListener listener ){
			this.mItemsArr = items;
			this.mOnClickListListener = listener;
			return this;
		}

		public Builder setAdapter( ListAdapter adapter, AdapterView.OnItemClickListener listener ){
			this.mAdapter = adapter;
			this.mItemClickListener = listener;
			return this;
		}

		public Builder setAutoDismiss( boolean autoDismiss ){
			mAutoDismiss = autoDismiss;
			return this;
		}

		public Builder setSingleton( boolean mSingleton ){
			this.mSingleton = mSingleton;
			return this;
		}

		public CustomDialog create( boolean cancelOnTouchOutSide, boolean hasFramework ){
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			int theme = hasFramework ? R.style.dialogStyle_Alarm : R.style.dialogStyle;
			CustomDialog dialog = new CustomDialog(mContext, theme);
			Window window = dialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			lp.dimAmount = 0.6f;
			dialog.setCanceledOnTouchOutside(cancelOnTouchOutSide);

			TypedArray array = mContext.obtainStyledAttributes(null, R.styleable.DialogTheme,
					R.attr.dialogStyle, 0);
			int internalLayout = array.getResourceId(R.styleable.DialogTheme_internalLayout,
					R.layout.for_dialog);
			array.recycle();

			View layout = inflater.inflate(internalLayout, null);
			dialog.setContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));

			initTitle(layout, hasFramework);
			initDialogButton(dialog, layout, hasFramework);
			initContentView(dialog, layout, hasFramework);

			dialog.setContentView(layout);
			removeAllDialog(dialog);

			return dialog;
		}

		private void removeAllDialog( CustomDialog dialog ){
			if (mSingleton){
				final Set<CustomDialog> dialogSet = mDialogManagerSet;
				Iterator<CustomDialog> iterator = dialogSet.iterator();
				while (iterator.hasNext()){
					CustomDialog customDialog = iterator.next();
					try{
						customDialog.dismiss();
					}
					catch (Exception e){
					}
				}
				dialogSet.clear();
				dialogSet.add(dialog);
			}
		}

		private void initTitle( View layout, boolean hasFramework ){
			TextView titleTextView = (TextView) layout.findViewById(R.id.alertTitle);
			View titleDivider = layout.findViewById(R.id.titleDivider);
			ImageView icon = (ImageView) layout.findViewById(R.id.icon);
			if (mTitleText == null){
				titleTextView.setVisibility(View.GONE);
				titleDivider.setVisibility(View.GONE);
				icon.setVisibility(View.GONE);
			}
			else{
				titleTextView.setText(mTitleText);
				if (hasFramework){
					titleTextView.setTextColor(Color.RED);
					titleDivider.setBackgroundColor(Color.RED);
				}
				if (mIconDrawable != null){
					icon.setVisibility(View.VISIBLE);
					icon.setImageDrawable(mIconDrawable);
				}
			}
		}

		private void initDialogButton( final CustomDialog dialog, View layout, boolean hasFramework ){
			boolean allButtonGone = true;
			Button positiveButton = (Button) layout.findViewById(R.id.positiveButton);
			if (mPositiveButtonText != null){
				allButtonGone = false;
				positiveButton.setText(mPositiveButtonText);
				positiveButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick( View v ){
						if (mPositiveButtonClickListener != null)
							mPositiveButtonClickListener.onClick(dialog, BUTTON_POSITIVE);
						if (mAutoDismiss) dialog.dismiss();
					}
				});
			}
			else{
				positiveButton.setVisibility(View.GONE);
			}

			Button negativeButton = (Button) layout.findViewById(R.id.negativeButton);
			if (mNegativeButtonText != null){
				allButtonGone = false;
				negativeButton.setText(mNegativeButtonText);
				negativeButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick( View v ){
						if (mNegativeButtonClickListener != null)
							mNegativeButtonClickListener.onClick(dialog, BUTTON_NEGATIVE);
						if (mAutoDismiss) dialog.dismiss();
					}
				});
			}
			else{
				negativeButton.setVisibility(View.GONE);
			}

			Button middleButton = (Button) layout.findViewById(R.id.middleButton);
			if (mMiddleButtonText != null){
				allButtonGone = false;
				middleButton.setText(mMiddleButtonText);
				((Button) layout.findViewById(R.id.middleButton))
						.setOnClickListener(new View.OnClickListener()
						{
							@Override
							public void onClick( View v ){
								if (mMiddleButtonClickListener != null)
									mMiddleButtonClickListener.onClick(dialog, BUTTON_NEUTRAL);
								if (mAutoDismiss) dialog.dismiss();
							}
						});
			}
			else{
				middleButton.setVisibility(View.GONE);
			}

			View pos_mid_divider = layout.findViewById(R.id.pos_mid_divider);
			View mid_nega_divider = layout.findViewById(R.id.mid_nega_divider);

			if (mMiddleButtonText != null){
				pos_mid_divider.setVisibility(View.VISIBLE);
			}
			if (mNegativeButtonText != null){
				mid_nega_divider.setVisibility(View.VISIBLE);
			}
			if(allButtonGone)layout.findViewById(R.id.buttonPanel).setVisibility(View.GONE);
		}

		private void initContentView( final CustomDialog dialog, View layout, boolean hasFramework ){
			TextView messageTextView = (TextView) layout.findViewById(R.id.message);
			if (mMessageText != null){
				messageTextView.setText(mMessageText);
			}
			else if (mContentView != null){
				LinearLayout lcontentLayout = (LinearLayout) layout.findViewById(R.id.content);
				lcontentLayout.removeAllViews();
				lcontentLayout.addView(mContentView, new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
			}
			else if (mItemsArr != null && mOnClickListListener != null){
				ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(mContext,
						R.layout.select_dialog_item, R.id.text_dialog, mItemsArr);
				View view = View.inflate(mContext, R.layout.list_pop_menu, null);

				if (mTitleText != null){
					TextView title = (TextView) view.findViewById(R.id.alertTitle);
					title.setVisibility(View.VISIBLE);
					title.setText(mTitleText);
				}

				mListView = (ListView) view.findViewById(R.id.action_pop_menu_list);
				mListView.setAdapter(adapter);
				mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{
					@Override
					public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
						mOnClickListListener.onClick(dialog, position);
						if (mAutoDismiss) dialog.dismiss();
					}
				});

				if (mPositiveButtonText == null && mMiddleButtonText == null && mNegativeButtonText == null){
					layout.findViewById(R.id.buttonPanel).setVisibility(View.GONE);
				}
				if (mTitleText == null && mIconDrawable == null){
					layout.findViewById(R.id.top_view).setVisibility(View.GONE);
				}

				LinearLayout lcontentLayout = (LinearLayout) layout.findViewById(R.id.content);
				lcontentLayout.removeAllViews();
				lcontentLayout.addView(view, new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
			}
			else if (mAdapter != null && mItemClickListener != null){
				View view = View.inflate(mContext, R.layout.list_pop_menu, null);

				if (mTitleText != null){
					TextView title = (TextView) view.findViewById(R.id.alertTitle);
					title.setVisibility(View.VISIBLE);
					title.setText(mTitleText);
				}

				mListView = (ListView) view.findViewById(R.id.action_pop_menu_list);
				mListView.setAdapter(mAdapter);
				mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{
					@Override
					public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
						mItemClickListener.onItemClick(parent, view, position, id);
						if (mAutoDismiss) dialog.dismiss();
					}
				});

				if (mPositiveButtonText == null && mMiddleButtonText == null && mNegativeButtonText == null){
					layout.findViewById(R.id.buttonPanel).setVisibility(View.GONE);
				}
				if (mTitleText == null && mIconDrawable == null){
					layout.findViewById(R.id.top_view).setVisibility(View.GONE);
				}

				LinearLayout lcontentLayout = (LinearLayout) layout.findViewById(R.id.content);
				lcontentLayout.removeAllViews();
				lcontentLayout.addView(view, new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
			}
		}
	}

	public static class onDialogClickListener implements DialogInterface.OnClickListener
	{
		private final MessageListener listener;
		private final int requestCode;

		public onDialogClickListener( int requestCode, MessageListener listener )
		{
			this.requestCode = requestCode;
			this.listener = listener;
		}

		@Override
		public void onClick( DialogInterface dialog, int which ){
			switch (which){
				case Dialog.BUTTON_POSITIVE :
					if (listener != null) listener.onClickPositive(requestCode, dialog);
					break;
				case Dialog.BUTTON_NEUTRAL :
					if (listener != null) listener.onClickNeutral(requestCode, dialog);
					break;
				case Dialog.BUTTON_NEGATIVE :
					if (listener != null) listener.onClickNegative(requestCode, dialog);
					break;
				default :
					break;
			}
		}

	}

	public static void showCustomDialog( Context context, Drawable icon, String title,
			String message, String negativeButtonText, String middleButtonText,
			String positiveButtonText, View contentView, MessageListener listener, int requestCode,
			boolean hasFramework, boolean cancelOutSide ){
		CustomDialog.Builder builder = new CustomDialog.Builder(context);
		builder.setContentView(contentView);
		builder.setTitle(title);
		builder.setIcon(icon);
		builder.setMessage(message);
		builder.setAutoDismiss(true);

		if (listener != null){
			builder.setNegativeButton(negativeButtonText,
					new onDialogClickListener(requestCode, listener));
			builder.setMiddleButton(middleButtonText, new onDialogClickListener(requestCode, listener));
			builder.setPositiveButton(positiveButtonText,
					new onDialogClickListener(requestCode, listener));
		}
		else{
			builder.setNegativeButton(negativeButtonText, null);
			builder.setMiddleButton(middleButtonText, null);
			builder.setPositiveButton(positiveButtonText, null);
		}
		CustomDialog dialog = builder.create(cancelOutSide, hasFramework);
		try{
			dialog.show();
		}
		catch (Exception e){
		}
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
		{
			@Override
			public void onDismiss( DialogInterface mDialog ){
				mDialog = null;
			}
		});
	}

	public static void showCustomDialog( Context context, int icon, int title, int message,
			int negativeButtonText, int middleButtonText, int positiveButtonText, View contentView,
			MessageListener listener, int requestCode, boolean hasFramework, boolean cancelOutSide ){
		CustomDialog.Builder builder = new CustomDialog.Builder(context);
		builder.setContentView(contentView);
		if (title != 0) builder.setTitle(title);
		if (message != 0) builder.setMessage(message);
		if (icon != 0) builder.setIcon(icon);
		builder.setAutoDismiss(true);

		if (listener != null){
			if (negativeButtonText != 0)
				builder.setNegativeButton(negativeButtonText, new onDialogClickListener(requestCode,
						listener));
			if (middleButtonText != 0)
				builder.setMiddleButton(middleButtonText, new onDialogClickListener(requestCode, listener));
			if (positiveButtonText != 0)
				builder.setPositiveButton(positiveButtonText, new onDialogClickListener(requestCode,
						listener));
		}
		else{
			if (negativeButtonText != 0) builder.setNegativeButton(negativeButtonText, null);
			if (middleButtonText != 0) builder.setMiddleButton(middleButtonText, null);
			if (positiveButtonText != 0) builder.setPositiveButton(positiveButtonText, null);
		}
		CustomDialog dialog = builder.create(cancelOutSide, hasFramework);
		try{
			dialog.show();
		}
		catch (Exception e){
		}
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
		{
			@Override
			public void onDismiss( DialogInterface mDialog ){
				mDialog = null;
			}
		});
	}

	public static void showCustomDialog( Context context, Drawable icon, String title,
			String message, String negativeButtonText, String positiveButtonText,
			MessageListener listener, int requestCode, boolean hasFramework, boolean cancelOutSide ){
		showCustomDialog(context, icon, title, message, negativeButtonText, null, positiveButtonText,
				null, listener, requestCode, hasFramework, cancelOutSide);
	}

	public static void showCustomDialog( Context context, int icon, int title, int message,
			int negativeButtonText, int positiveButtonText, MessageListener listener, int requestCode,
			boolean hasFramework, boolean cancelOutSide ){
		showCustomDialog(context, icon, title, message, negativeButtonText, 0, positiveButtonText,
				null, listener, requestCode, hasFramework, cancelOutSide);
	}

	public static void showCustomDialog( Context context, View contentView, MessageListener listener,
			int requestCode, boolean hasFramework, boolean cancelOutSide ){
		showCustomDialog(context, null, null, null, null, null, null, contentView, listener,
				requestCode, hasFramework, cancelOutSide);
	}

	public static void showCustomDialog( Context context, String message, String positiveButtonText,
			MessageListener listener, int requestCode, boolean hasFramework, boolean cancelOutSide ){
		showCustomDialog(context, null, message, null, null, null, positiveButtonText, null, listener,
				requestCode, hasFramework, cancelOutSide);
	}

	public static void showCustomDialog( Context context, int icon, int title, int message,
			int positiveButtonText, MessageListener listener, int requestCode, boolean hasFramework,
			boolean cancelOutSide ){
		showCustomDialog(context, icon, title, message, 0, 0, positiveButtonText, null, listener,
				requestCode, hasFramework, cancelOutSide);
	}

	public static void showCustomDialog( Context context, int icon, int title, int message,
			int positiveButtonText, View contentView, MessageListener listener, int requestCode,
			boolean cancelOutSide ){
		showCustomDialog(context, icon, title, message, 0, 0, positiveButtonText, contentView,
				listener, requestCode, false, cancelOutSide);
	}

	public static void showCustomDialog( Context context, CharSequence[] mItems,
			DialogInterface.OnClickListener mOnClickListener, boolean cancelOutSide ){
		CustomDialog.Builder builder = new CustomDialog.Builder(context);
		builder.setItems(mItems, mOnClickListener);
		builder.setAutoDismiss(true);
		CustomDialog dialog = builder.create(cancelOutSide, false);
		try{
			dialog.show();
		}
		catch (Exception e){
		}
	}
}