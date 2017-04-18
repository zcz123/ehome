package com.yuantuo.customview.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuantuo.customview.R;

public class CustomCornerView extends BaseCornerView implements OnCheckedChangeListener
{
	private Drawable mItemDrawable;
	private String mCornerTitle;
	private int mCornerTitleColor;
	private String mCornerSummary;
	private int mCornerSummaryColor;
	private boolean mSwitchShow;
	private boolean mSwitchChecked;
	private Drawable mNextGuideDrawable;

	private View mItemIconLayout;
	private ImageView mItemIconIV;

	private TextView mTitleTV;
	private TextView mSummaryTV;

	private View mSwitchView;
	private Switch mSwitch;

	private View mNextGuideView;
	private ImageView mNextGuideIV;

	private OnCheckedSwitchChangeListener mOnCheckedChangeListener;

	public CustomCornerView( Context context )
	{
		super(context);
	}

	public CustomCornerView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public CustomCornerView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void initAttributeSet( Context context, AttributeSet attrs, int defStyle ){
		super.initAttributeSet(context, attrs, defStyle);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCornerView,
				defStyle, 0);

		mItemDrawable = typedArray.getDrawable(R.styleable.CustomCornerView_itemIcon);
		mCornerTitle = typedArray.getString(R.styleable.CustomCornerView_cornerTitle);
		mCornerTitleColor = typedArray.getColor(R.styleable.CustomCornerView_cornerTitleColor,
				Color.BLACK);
		mCornerSummary = typedArray.getString(R.styleable.CustomCornerView_cornerSummary);
		mCornerSummaryColor = typedArray.getColor(R.styleable.CustomCornerView_cornerSummaryColor,
				Color.BLACK);
		mSwitchShow = typedArray.getBoolean(R.styleable.CustomCornerView_switchShow, true);
		mSwitchChecked = typedArray.getBoolean(R.styleable.CustomCornerView_android_checked, false);
		mNextGuideDrawable = typedArray.getDrawable(R.styleable.CustomCornerView_nextGuideIcon);

		inflate(context, R.layout.for_cornerview, this);
		
		typedArray.recycle();
	}

	// ///////////////// inflate view ///////////////////////
	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();

		bindView();
		initUi();
	}

	private void bindView(){
		mItemIconLayout = findViewById(R.id.icon_layout);
		mItemIconIV = (ImageView) findViewById(R.id.icon_iv);
		
		
		mTitleTV = (TextView) findViewById(R.id.title_title);
		mSummaryTV = (TextView) findViewById(R.id.title_summary);

		mSwitchView = findViewById(R.id.switch_layout);
		mSwitch = (Switch) findViewById(android.R.id.checkbox);

		mNextGuideView = findViewById(R.id.nextGuide_layout);
		mNextGuideIV = (ImageView) findViewById(R.id.nextGuide_iv);
	}

	private void initUi(){
		setItemIcon(mItemDrawable);
		setCornerTitle(mCornerTitle);
		setCornerTitleColor(mCornerTitleColor);
		setCornerSummary(mCornerSummary);
		setCornerSummaryColor(mCornerSummaryColor);
		setSwitchWantShown(mSwitchShow);
		setNextGuideIcon(mNextGuideDrawable);
	}

	public void setCornerTitle( String title ){
		mCornerTitle = title;
		mTitleTV.setText(title);
	}

	public void setCornerTitle( int title ){
		setCornerTitle(getResources().getString(title));
	}

	public void setCornerTitleColor( int color ){
		mTitleTV.setTextColor(color);
	}

	public void setCornerTitle( ColorStateList color ){
		mTitleTV.setTextColor(color);
	}

	public void setCornerSummary( String summary ){
		mCornerSummary = summary;
		if (summary == null){
			mSummaryTV.setVisibility(View.GONE);
		}
		else{
			if (!mSummaryTV.isShown()) mSummaryTV.setVisibility(View.VISIBLE);
			mSummaryTV.setText(summary);
		}
	}

	public void setCornerSummary( int summary ){
		setCornerSummary(getResources().getString(summary));
	}

	public void setCornerSummaryColor( int color ){
		mSummaryTV.setTextColor(color);
	}

	public void setCornerSummaryColor( ColorStateList color ){
		mSummaryTV.setTextColor(color);
	}

	public void setItemIcon( Drawable icon ){
		mItemDrawable = icon;
		if (mItemDrawable == null){
			mItemIconLayout.setVisibility(View.GONE);
		}
		else{
			if (!mItemIconLayout.isShown()) mItemIconLayout.setVisibility(View.VISIBLE);
			mItemIconIV.setImageDrawable(icon);
		}
	}

	public void setItemIcon( int icon ){
		setItemIcon(getResources().getDrawable(icon));
	}

	public void setItemIconSize( int width, int height ){
		mItemIconIV.setLayoutParams(new LinearLayout.LayoutParams(width, height));
	}

	public void setSwitchWantShown( boolean wantShown ){
		if (wantShown){
			if (!mSwitchView.isShown()) mSwitchView.setVisibility(View.VISIBLE);
			mSwitch.setOnCheckedChangeListener(this);
			setSwicthCheckStat(mSwitchChecked);
		}
		else{
			mSwitchView.setVisibility(View.GONE);
		}
	}

	public void setNextGuideIcon( Drawable icon ){
		if (icon == null){
			mNextGuideView.setVisibility(View.INVISIBLE);
		}
		else{
			mNextGuideDrawable = icon;
			if (!mNextGuideView.isShown()) mNextGuideView.setVisibility(View.VISIBLE);
			mNextGuideIV.setImageDrawable(icon);
		}
	}

	public void setSwicthCheckStat( boolean checked ){
		if (mSwitchShow) mSwitch.setChecked(mSwitchChecked = checked);
	}

	public Drawable getItemDrawable(){
		return mItemDrawable;
	}

	public String getCornerTitle(){
		return mCornerTitle;
	}

	public String getCornerSummary(){
		return mCornerSummary;
	}

	public Drawable getNextGuideDrawable(){
		return mNextGuideDrawable;
	}

	public boolean getSwitchCheckState(){
		return mSwitch.isChecked();
	}

	// ///////////////// setView listener///////////////////////
	public void setOnCheckedSwitchChangeListener( OnCheckedSwitchChangeListener onChangeListener ){
		mOnCheckedChangeListener = onChangeListener;
	}

	public interface OnCheckedSwitchChangeListener
	{
		public void onCheckedSwitchChanged( View view, boolean isChecked );
	}

	@Override
	public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ){
		if (mOnCheckedChangeListener != null)
			mOnCheckedChangeListener.onCheckedSwitchChanged(this, isChecked);
	}
}