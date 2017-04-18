package com.yuantuo.customview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class CustomBlind extends ImageView
{
	protected static final float MIN_DISTANCE = 20f;
	protected BlindMovementListener movementListener;
	private int mDownY;
	private int mYDistance;

	public interface BlindMovementListener
	{
		public void onFlingUp( CustomBlind blind, float moveImageY );

		public void onFlingDown( CustomBlind blind, float moveImageY );

		public void onClick( CustomBlind blind );
	}

	public CustomBlind( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
	}

	public CustomBlind( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public CustomBlind( Context context )
	{
		super(context);
	}

	public void setOnBlindMovement( BlindMovementListener movementListener ){
		this.movementListener = movementListener;
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ){
		switch (event.getAction()){
			case MotionEvent.ACTION_UP :
				mYDistance = (int) (event.getY() - mDownY);
				if (Math.abs(mYDistance) >= MIN_DISTANCE){
					if (mYDistance <= 0){
						if (movementListener != null) movementListener.onFlingUp(this, mYDistance);
					}
					else{
						if (movementListener != null) movementListener.onFlingDown(this, mYDistance);
					}
				}
				else{
					if (movementListener != null) movementListener.onClick(this);
				}
				break;
			case MotionEvent.ACTION_DOWN :
				mDownY = (int) event.getY();
				break;
		}
		return true;
	}
}
