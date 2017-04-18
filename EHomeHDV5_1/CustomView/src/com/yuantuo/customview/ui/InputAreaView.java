package com.yuantuo.customview.ui;

import java.util.Iterator;
import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yuantuo.customview.R;

public class InputAreaView extends LinearLayout
{
	public interface OnInputAreaActionListener
	{
		public void onActionEnd( Vector<CharSequence> allData );
	}

	private Resources mResources;
	private Vector<Button> mButtonVector;
	private Vector<Button> mHasSetButtons;
	private Vector<CharSequence> mTextVector;
	private OnInputAreaActionListener mActionListener;

	public InputAreaView( Context context )
	{
		this(context, null);
	}

	public InputAreaView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
		mResources = context.getResources();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.for_inputarea, this);
		initUi();
	}

	void initUi(){
		mButtonVector = new Vector<Button>();
		mHasSetButtons = new Vector<Button>();
		mTextVector = new Vector<CharSequence>();
		TypedArray buttons = mResources.obtainTypedArray(R.array.inputarea_buttons);
		for (int i = 0; i < buttons.length(); i++){
			mButtonVector.add((Button) findViewById(buttons.getResourceId(i, 0)));
		}
		buttons.recycle();
	}

	public void setInputAreaValue( CharSequence value ){
		int N = mTextVector.size();
		int M = mButtonVector.size();
		if (value == null || value.equals("")){
			if (N == 0) return;
			mTextVector.remove(mTextVector.lastElement());
		}
		else{
			if (N == M) return;
			mTextVector.add(value);
		}
		updateButtonText();
	}

	public Vector<CharSequence> getAllInputValues(){
		return mTextVector;
	}

	public void setOnInputAreaActionListener( OnInputAreaActionListener listener ){
		mActionListener = listener;
	}

	void updateButtonText(){
		int M = mButtonVector.size();
		int N = mTextVector.size();

		Iterator<Button> iterator = mHasSetButtons.iterator();
		while (iterator.hasNext()){
			Button button = (Button) iterator.next();
			button.setText(null);
			button.setSelected(false);
			iterator.remove();
		}

		for (int i = 0; i < N; i++){
			if (i > M - 1) break;

			Button button = mButtonVector.elementAt(i);
			CharSequence newValue = mTextVector.get(i);
			button.setText(newValue);
			button.setSelected(true);
			mHasSetButtons.add(button);
			if (i == M - 1) mActionListener.onActionEnd(mTextVector);
		}
	}
}
