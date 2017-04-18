/**
 * Project Name:  iCam
 * File Name:     ListViewForScrollView.java
 * Package Name:  com.wulian.icam.widget
 * @Date:         2015年9月23日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.icam.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @ClassName: ListViewForScrollView
 * @Function:  TODO
 * @Date:      2015年9月23日
 * @author     Yanmin
 * @email      min.yan@wuliangroup.cn
 */
public class ListViewForScrollView extends ListView {
	public ListViewForScrollView(Context context) {
        super(context);
    }
    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ListViewForScrollView(Context context, AttributeSet attrs,
        int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
        MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

