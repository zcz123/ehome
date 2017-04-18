package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class LayerLayout extends RelativeLayout
{

	public LayerLayout( Context context )
	{
		super(context);
	}

	public LayerLayout( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public LayerLayout( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ){
		int size = getChildCount();
		if (size < 2){
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		int maxW = -1;
		int maxH = -1;
		for (int i = 0; i < size; i++){
			View child = getChildAt(i);
			maxW = Math.max(maxW, child.getMeasuredWidth());
			maxH = Math.max(maxH, child.getMeasuredHeight());
		}
		if (maxW != -1 && maxH != -1){
			setMeasuredDimension(maxW, maxH);
		}
	}
}
