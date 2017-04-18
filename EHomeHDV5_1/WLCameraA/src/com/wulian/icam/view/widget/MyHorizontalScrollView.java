/**
 * Project Name:  iCam
 * File Name:     MyHorizontalScrollView.java
 * Package Name:  com.wulian.icam.widget
 * @Date:         2015年7月6日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * @ClassName: MyHorizontalScrollView
 * @Function: TODO
 * @Date: 2015年7月6日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class MyHorizontalScrollView extends HorizontalScrollView {

	public MyHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {

		super(context, attrs, defStyle);

	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {

		this(context, attrs, 0);

	}

	public MyHorizontalScrollView(Context context) {

		this(context, null);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// Utils.sysoInfo("MyHorizontalScrollView:"+ev.getPointerCount());
		return super.onInterceptTouchEvent(ev) && ev.getPointerCount() < 2;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (listener != null) {
			listener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

	private OnScrollChangedListener listener;

	public void setOnScrollChangedListener(OnScrollChangedListener listener) {
		this.listener = listener;
	}

	public interface OnScrollChangedListener {
		void onScrollChanged(HorizontalScrollView sv, int l, int t, int oldl,
				int oldt);
	}
}
