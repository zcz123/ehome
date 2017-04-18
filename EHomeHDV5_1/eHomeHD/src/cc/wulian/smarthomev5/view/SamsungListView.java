package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class SamsungListView extends ListView
{
	public SamsungListView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
	}

	public SamsungListView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public SamsungListView( Context context )
	{
		super(context);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		/**
		 * This is a bit hacky, but Samsung's ListView has got a bug in it
		 * when using Header/Footer Views and the list is empty. This masks
		 * the issue so that it doesn't cause an FC. See Issue #66.
		 */
		try {
			super.dispatchDraw(canvas);
		}
		catch (Exception e){
		}
		catch (Error e){
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		/**
		 * This is a bit hacky, but Samsung's ListView has got a bug in it
		 * when using Header/Footer Views and the list is empty. This masks
		 * the issue so that it doesn't cause an FC. See Issue #66.
		 */
		try {
			return super.dispatchTouchEvent(ev);
		}
		catch (Exception e){
			return false;
		}
		catch (Error e){
			return false;
		}
	}
}
