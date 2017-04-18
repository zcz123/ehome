package com.yuantuo.customview.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

public class CustomListView extends ListView implements OnGestureListener
{
	private boolean outBound = false;
	private int distance;
	private int firstOut;
	private GestureDetector gestureDetector;

	public CustomListView( Context c )
	{
		super(c);
		init();
	}

	public CustomListView( Context c, AttributeSet attrs )
	{
		super(c, attrs);
		init();
	}

	public CustomListView( Context c, AttributeSet attrs, int defStyle )
	{
		super(c, attrs, defStyle);
		init();
	}

	private void init(){
		gestureDetector = new GestureDetector(this);
	}

	@Override
	public boolean dispatchTouchEvent( MotionEvent ev ){
		int act = ev.getAction();
		if ((act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_CANCEL) && outBound){
			outBound = false;
		}
		if (!gestureDetector.onTouchEvent(ev)){
			outBound = false;
		}
		else{
			outBound = true;
		}
		Rect rect = new Rect();
		getLocalVisibleRect(rect);
		TranslateAnimation am = new TranslateAnimation(0, 0, -rect.top, 0);
		am.setDuration(300);
		startAnimation(am);
		scrollTo(0, 0);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onDown( MotionEvent e ){
		return false;
	}

	@Override
	public void onShowPress( MotionEvent e ){
	}

	@Override
	public boolean onSingleTapUp( MotionEvent e ){
		return false;
	}

	@Override
	public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY ){
		int firstPos = getFirstVisiblePosition();
		int lastPos = getLastVisiblePosition();
		int itemCount = getCount();
		if (outBound && firstPos != 0 && lastPos != (itemCount - 1)){
			scrollTo(0, 0);
			return false;
		}
		View firstView = getChildAt(firstPos);
		if (!outBound){
			firstOut = (int) e2.getRawY();
		}
		if (firstView != null){
			distance = firstOut - (int) e2.getRawY();
			scrollTo(0, distance / 2);
			return true;
		}
		return true;
	}

	@Override
	public void onLongPress( MotionEvent e ){
	}

	@Override
	public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY ){
		return false;
	};
}