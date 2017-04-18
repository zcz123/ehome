package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class ViewPagerGallery extends Gallery
{
	private ViewGroup mPager;

	public ViewPagerGallery( Context context )
	{
		super(context);
	}

	public ViewPagerGallery( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public ViewPagerGallery( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
	}

	public void setViewPager( ViewGroup mPager ){
		this.mPager = mPager;
	}

	@Override
	public boolean dispatchTouchEvent( MotionEvent ev ){
		mPager.requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent( MotionEvent ev ){
		mPager.requestDisallowInterceptTouchEvent(true);
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ){
		mPager.requestDisallowInterceptTouchEvent(MotionEvent.ACTION_UP == event.getAction());
		return super.onTouchEvent(event);
	}
}
