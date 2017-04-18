/**
 * Project Name:  iCam
 * File Name:     CheckableFrameLayout.java
 * Package Name:  com.wulian.icam.view.widget
 * @Date:         2015年10月10日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

/**
 * @ClassName: CheckableFrameLayout
 * @Function: 该控件便于listView中Item含有Checkable特征的运用
 * @Date: 2015年10月10日
 * @author: yuanjs
 * @email: jiansheng.yuan@wuliangroup.com.cn
 */
public class CheckableFrameLayout extends FrameLayout implements Checkable {

	private boolean mChecked = false;

	public CheckableFrameLayout(Context context) {
		super(context);
	}

	public CheckableFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();
			//如果有实现Checkable的子控件，其状态值会改变
			for (int i = 0, len = getChildCount(); i < len; i++) {
				View child = getChildAt(i);
				if (child instanceof Checkable) {
					((Checkable) child).setChecked(checked);
				}
			}
		}
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}

}
