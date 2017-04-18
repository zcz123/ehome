package com.yuantuo.customview.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yuantuo.customview.R;

public class KeyboardView extends LinearLayout
{
	public interface OnKeyboardActionListener
	{
		public static final int ACTION_ID_DIGIT = 0;
		public static final int ACTION_ID_DONE = 1;
		public static final int ACTION_ID_DELETE = 2;

		/**
		 * @param actionId
		 *          ACTION_ID_DIGIT = 0; ACTION_ID_DONE = 1; ACTION_ID_DELETE = 2;
		 * @param inputValue
		 */
		public void onPressed( int actionId, CharSequence inputValue );

	}

	private Resources mResources;
	private EventListener mListener = new EventListener();
	private OnKeyboardActionListener mActionListener;

	public KeyboardView( Context context )
	{
		this(context, null);
	}

	public KeyboardView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
		mResources = context.getResources();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.for_keyboardview, this);
		initUi();
	}

	private void initUi(){
		TypedArray buttons = mResources.obtainTypedArray(R.array.keyboard_buttons);
		for (int i = 0; i < buttons.length(); i++){
			setOnClickListener(buttons.getResourceId(i, 0));
		}
		buttons.recycle();
	}

	private void setOnClickListener( int resId ){
		View target = findViewById(resId);
		target.setOnClickListener(mListener);
	}

	public void setOnKeyboardActionListener( OnKeyboardActionListener mActionListener ){
		this.mActionListener = mActionListener;
	}

	class EventListener implements View.OnClickListener
	{
		@Override
		public void onClick( View v ){
			int id = v.getId();
			Button button = (Button) v;
			int actionId;
			if (id == R.id.done){
				actionId = OnKeyboardActionListener.ACTION_ID_DONE;
			}
			else if (id == R.id.delete){
				actionId = OnKeyboardActionListener.ACTION_ID_DELETE;
			}
			else{
				actionId = OnKeyboardActionListener.ACTION_ID_DIGIT;
			}
			mActionListener.onPressed(actionId, button.getText());
		}
	}
}