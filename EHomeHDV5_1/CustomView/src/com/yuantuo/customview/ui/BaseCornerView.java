package com.yuantuo.customview.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import com.yuantuo.customview.R;

public class BaseCornerView extends LinearLayout
{
	public static final int BACKGROUND_TYPE_SINGLE = 0;
	public static final int BACKGROUND_TYPE_TOP = 1;
	public static final int BACKGROUND_TYPE_CENTER = 2;
	public static final int BACKGROUND_TYPE_BOTTOM = 3;

	private static final int MIN_HEIGHT = 48;

	private int mCornerType;

	public BaseCornerView( Context context )
	{
		this(context, null);

	}

	public BaseCornerView( Context context, AttributeSet attrs )
	{
		this(context, attrs, R.attr.cornerViewStyle);
	}

	public BaseCornerView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs);
		initAttributeSet(context, attrs, defStyle);
	}

	protected void initAttributeSet( Context context, AttributeSet attrs, int defStyle ){
		 if (isInEditMode()) return;
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCornerView,
				defStyle, 0);

		mCornerType = typedArray
				.getInt(R.styleable.CustomCornerView_cornerType, BACKGROUND_TYPE_SINGLE);

		typedArray.recycle();

		int minHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_HEIGHT, context
				.getResources().getDisplayMetrics());

		if (getSuggestedMinimumHeight() > 0){
			minHeight = Math.max(minHeight, getSuggestedMinimumHeight());
		}
		setMinimumHeight(minHeight);
	}

	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();
		setCornerType(mCornerType);
	}

	public void setCornerType( int type ){
		mCornerType = type;
		Drawable background = null;
		Resources resources = getResources();
		switch (type){
			case BACKGROUND_TYPE_TOP :
				background = resources.getDrawable(R.drawable.cornerview_type_top);
				break;
			case BACKGROUND_TYPE_CENTER :
				background = resources.getDrawable(R.drawable.cornerview_type_center);
				break;
			case BACKGROUND_TYPE_BOTTOM :
				background = resources.getDrawable(R.drawable.cornerview_type_bottom);
				break;
			case BACKGROUND_TYPE_SINGLE :
			default :
				background = resources.getDrawable(R.drawable.cornerview_type_single);
				break;
		}
		setBackgroundDrawable(background);
	}

	public int getCornerType(){
		return mCornerType;
	}
}