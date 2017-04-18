package com.wulian.icam.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.utils.Utils;

public class TitledListView extends ListView {

	private View mTitle;

	public TitledListView(Context context) {
		super(context);
	}

	public TitledListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TitledListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mTitle != null) {
			measureChild(mTitle, widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (mTitle != null) {
			mTitle.layout(0, 0, mTitle.getMeasuredWidth(),
					mTitle.getMeasuredHeight());

		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mTitle != null) {
			drawChild(canvas, mTitle, getDrawingTime());
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		mTitle = inflater.inflate(R.layout.layout_head_title, this, false);
	}

	public void hideTitle() {
		// mTitle.layout(0, -mTitle.getMeasuredHeight(),
		// mTitle.getMeasuredWidth(), 0);//无效
		// mTitle.setVisibility(View.GONE);//无效
		TextView title_text = (TextView) mTitle.findViewById(R.id.tv_time_head);
		title_text.setBackgroundColor(getResources().getColor(
				R.color.background01));
		title_text.setText("");
	}

	/**
	 * @Function 标题随动
	 * @author Wangjj
	 * @date 2015年6月19日
	 */

	public void moveTitleFollow() {
		View bottomChild = getChildAt(0);
		if (bottomChild != null) {
			int bottom = bottomChild.getBottom();
			Utils.sysoInfo("标题随动bottom" + bottom);
			int height = mTitle.getMeasuredHeight();
			int y = 0;
			if (bottom < height) {
				y = bottom - height;
			}
			mTitle.layout(0, y, mTitle.getMeasuredWidth(),
					mTitle.getMeasuredHeight() + y);
		}
	}

	/**
	 * @Function 同步下拉标题，上滑动就算了
	 * @author Wangjj
	 * @date 2015年6月19日
	 */

	public void moveTitleDown() {
		View bottomChild = getChildAt(0);
		if (bottomChild != null) {
			int top = bottomChild.getTop();
			Utils.sysoInfo("标题随动top" + top);
			if (top > 0) {// 可见项为第一个条目时，top > 0确保同步下拉，上滑就算了
				mTitle.layout(0, top, mTitle.getMeasuredWidth(),
						mTitle.getMeasuredHeight() + top);
			}
		}
	}

	public void updateTitle(String title) {
		Utils.sysoInfo("更新标题" + title);
		if (title != null) {
			TextView title_text = (TextView) mTitle
					.findViewById(R.id.tv_time_head);
			title_text.setBackgroundColor(getResources().getColor(
					R.color.background01));
			title_text.setText(title);
		}
		mTitle.layout(0, 0, mTitle.getMeasuredWidth(),
				mTitle.getMeasuredHeight());
	}
}
