/**
 * Project Name:  iCam
 * File Name:     CustomViewPager.java
 * Package Name:  com.wulian.icam.widget
 * @Date:         2015年5月8日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @ClassName: CustomViewPager
 * @Function: 用于消除photoView+ViewPager IllegalArgumentException: pointerIndex out
 *            of range 来自：<h1>
 *            http://blog.csdn.net/com314159/article/details/41245329</h1>
 * @Date: 2015年5月8日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class CustomViewPager extends ViewPager {
	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private boolean mIsDisallowIntercept = false;
	private boolean noScroll = false;

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		// keep the info about if the innerViews do
		// requestDisallowInterceptTouchEvent
		mIsDisallowIntercept = disallowIntercept;
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// the incorrect array size will only happen in the multi-touch
		// scenario.
		if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
			requestDisallowInterceptTouchEvent(false);
			boolean handled = super.dispatchTouchEvent(ev);
			requestDisallowInterceptTouchEvent(true);
			return handled;
		} else {
			return super.dispatchTouchEvent(ev);
		}
	}

	/*
	 * @Override public boolean onInterceptTouchEvent(MotionEvent arg0) {
	 * //有warn 不完美 try { return super.onInterceptTouchEvent(arg0); } catch
	 * (IllegalArgumentException e) { e.printStackTrace(); } catch
	 * (ArrayIndexOutOfBoundsException e) { e.printStackTrace(); } return false;
	 * }
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (noScroll) 
				return false;
		else
			return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (noScroll) 
			return false;
		else
			return super.onTouchEvent(arg0);
	}

	public void setNoScroll(boolean noScroll) {
		this.noScroll = noScroll;
	}
	
	
}
