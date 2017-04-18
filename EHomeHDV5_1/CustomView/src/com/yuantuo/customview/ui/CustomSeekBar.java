package com.yuantuo.customview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yuantuo.customview.R;

public class CustomSeekBar extends LinearLayout
{
	private final int MESSAGE_SET_TEXT = 0;
	private final static String TEXT_PERCENT = "%";
	private final static String TEXT_MILLSTON = "s";
	private final static String TEXT_EMPTY = "";
	private SeekBar mSeekBar;
	private ImageView mLeftImageView, mRightImageView;
	private TextView mProgressTextView;
	private LinearLayout mSeekBarLayout;
	private LinearLayout mProgressTextLayout;
	private OnChangeListener mListener;
	private CompanyStyle mCompanyStyle = CompanyStyle.EMPTY;

	public CustomSeekBar( Context context, AttributeSet attrs )
	{
		super(context, attrs);
		if (isInEditMode()) return;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.for_seekbar, this);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar);

		initUi(array);
		registLintener();
	}

	public CustomSeekBar( Context context )
	{
		this(context, null);
	}

	private void initUi( TypedArray array ){
		mSeekBar = (SeekBar) findViewById(R.id.seekBar_custom);
		mLeftImageView = (ImageView) findViewById(R.id.imageView_left);
		mRightImageView = (ImageView) findViewById(R.id.imageView_right);
		mProgressTextView = (TextView) findViewById(R.id.progress);
		mSeekBarLayout = (LinearLayout) findViewById(R.id.linearLayout_seekbar_bg);
		mProgressTextLayout = (LinearLayout) findViewById(R.id.linearLayout_progress);

		CharSequence text = array.getText(R.styleable.CustomSeekBar_android_text);
		if (text != null) mProgressTextView.setText(text);

		int progress = array.getInt(R.styleable.CustomSeekBar_android_progress, 0);
		mSeekBar.setProgress(progress);
		int max = array.getInt(R.styleable.CustomSeekBar_android_max, 100);
		mSeekBar.setMax(max);

		array.recycle();
	}

	private void registLintener(){
		mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
	}

	private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
	{

		@Override
		public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ){
			if (mListener != null){
				Message.obtain(mHandler, MESSAGE_SET_TEXT, progress).sendToTarget();
				mListener.onChanged(CustomSeekBar.this, progress, fromUser);
			}
		}

		@Override
		public void onStartTrackingTouch( SeekBar seekBar ){
			if (mListener != null) mListener.onStart(CustomSeekBar.this);
		}

		@Override
		public void onStopTrackingTouch( SeekBar seekBar ){
			if (mListener != null){
				Message.obtain(mHandler, MESSAGE_SET_TEXT, seekBar.getProgress()).sendToTarget();
				mListener.onStop(CustomSeekBar.this);
			}
		}
	};

	public void setLinearLayoutBackgroundResource( int res ){
		mSeekBarLayout.setBackgroundResource(res);
	}

	public void setImageResource( int leftResId, int rightResId ){
		mLeftImageView.setImageResource(leftResId);
		mRightImageView.setImageResource(rightResId);
	}

	public void setOnChangedListener( OnChangeListener listener, CompanyStyle style ){
		this.mListener = listener;
		this.mCompanyStyle = style == null ? mCompanyStyle : style;
	}

	public void setVisible( int visible ){
		mLeftImageView.setVisibility(visible);
		mRightImageView.setVisibility(visible);
	}

	public void setMax( int max ){
		mSeekBar.setMax(max);
	}

	public void setProgress( int progress ){
		mSeekBar.setProgress(progress);
	}

	public void setEnabled( boolean enabled ){
		mSeekBar.setEnabled(enabled);
	}

	public boolean isEnabled(){
		return mSeekBar.isEnabled();
	}

	public int getProgress(){
		return mSeekBar.getProgress();
	}

	public int getMax(){
		return mSeekBar.getMax();
	}

	public void setText( Object text ){
		String value = mCompanyStyle.getValue();
		boolean isEmpty = TEXT_EMPTY.equals(value);
		mProgressTextLayout.setVisibility(isEmpty ? INVISIBLE : VISIBLE);
		if (!isEmpty) mProgressTextView.setText(text + value);
	}

	public interface OnChangeListener
	{
		void onChanged( CustomSeekBar customSeekBar, int progress, boolean fromUser );

		void onStart( CustomSeekBar customSeekBar );

		void onStop( CustomSeekBar customSeekBar );
	}

	public enum CompanyStyle
	{
		PERCENT( 0 ), MILLSTON( 1 ), EMPTY( 2 );

		final int mInt;

		CompanyStyle( int num )
		{
			mInt = num;
		}

		public String getValue(){
			if (mInt == 0){
				return TEXT_PERCENT;
			}
			else if (mInt == 1){
				return TEXT_MILLSTON;
			}
			else{
				return TEXT_EMPTY;
			}
		}
	}

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage( Message msg ){
			if (MESSAGE_SET_TEXT == msg.what){
				setText(msg.obj);
			}
		}
	};
}
