/**
 * 类名            VerticalScrollView.java
 * 说明            垂直滑动
 * 创建日期    2014年7月4日 下午5:03:38
 * 作者            gxy
 * 版权           【南京物联传感技术有限公司】
 */
package com.yuantuo.customview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

public class VerticalScrollView extends ViewGroup {

	private Scroller mScroller;

	/**
	 * 当前垂直滚轮坐标
	 */
	private int curY;

	public VerticalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public VerticalScrollView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	private void init(Context context) {
		mScroller = new Scroller(context,
				new AccelerateDecelerateInterpolator());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int width = getMeasureSize(widthMeasureSpec);
		int height = getMeasureSize(heightMeasureSpec);
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int totalHeight = 0;
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);

			view.layout(0, totalHeight, view.getMeasuredWidth(), totalHeight
					+ view.getMeasuredHeight());
			totalHeight += view.getMeasuredHeight();
		}
		
	}
	private int getMeasureSize(int spec) {
		int mode = MeasureSpec.getMode(spec);
		int size = 0;
		switch (mode) {
		case MeasureSpec.AT_MOST:
		case MeasureSpec.EXACTLY:
			size = MeasureSpec.getSize(spec);
			break;

		case MeasureSpec.UNSPECIFIED:

			break;
		}
		return size;
	}

	/**
	 * 
	 * @param startY
	 *            起始Y位置
	 * @param offsetY
	 *            移动Y偏移量 正数上移，负数下移
	 */
	public void scrollSmoothTo(int endY) {

		scrollSmoothTo(endY, 1000);
	}

	/**
	 * 
	 * @param startY
	 *            起始Y位置
	 * @param offsetY
	 *            移动Y偏移量 正数上移，负数下移
	 * @param duration
	 *            移动时间
	 */
	public void scrollSmoothTo(int endY, int duration) {

		if (curY == endY) {

			Log.d("scrollSmoothTo", "don't need move :" + curY);
			return;
		}
		mScroller.startScroll(0, curY, 0, (endY - curY), duration);
		invalidate();
		curY = endY;
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {

			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
		}
	}

	/**
	 * 初始化页面位置
	 * 
	 * @param x
	 * @param y
	 */
	public void init(int x, int y) {

		curY = y;
		scrollTo(x, y);
		invalidate();
	}

}
