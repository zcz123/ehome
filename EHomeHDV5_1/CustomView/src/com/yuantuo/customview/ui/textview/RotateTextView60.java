package com.yuantuo.customview.ui.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class RotateTextView60 extends TextView{

	public RotateTextView60(Context context) {
		super(context);
	}

	public RotateTextView60(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.rotate(60, getMeasuredWidth()/3, getMeasuredHeight()/3);
		super.onDraw(canvas);
	}
	
	
}
