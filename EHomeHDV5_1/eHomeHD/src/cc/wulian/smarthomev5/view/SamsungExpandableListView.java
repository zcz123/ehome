package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ExpandableListView;

public class SamsungExpandableListView extends ExpandableListView
{

	public SamsungExpandableListView( Context context )
	{
		super(context);
	}

	public SamsungExpandableListView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public SamsungExpandableListView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void dispatchDraw( Canvas canvas ) {
		try {
			super.dispatchDraw(canvas);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		catch (Error e) {

		}
	}

	@Override
	public boolean dispatchTouchEvent( MotionEvent ev ) {
		try {
			return super.dispatchTouchEvent(ev);
		}
		catch (Exception e) {
			return false;
		}
		catch (Error e) {
			return false;
		}
	}
}
