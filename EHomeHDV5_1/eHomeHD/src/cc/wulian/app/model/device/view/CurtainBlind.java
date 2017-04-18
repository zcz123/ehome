package cc.wulian.app.model.device.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import cc.wulian.app.model.device.view.BlindCover.OnAngleChangeListener;

public class CurtainBlind extends RelativeLayout
{
	private static final int SCROLL_MIN_DISTANCE = 1;

	public static interface OnCurtainScrollListener
	{
		public void onScrollUp();

		/**
		 * @param percent
		 *          [0,100]
		 */
		public void onScroll( int percent, boolean scrollUp );
		
		/**
		 * @param percent
		 *          [0,100]
		 */
		public void onScrollOver( int percent, boolean scrollUp, boolean fromUser );

		public void onScrollDown();
	}
	
	public static abstract class SimpleCurtainScrollListener implements OnCurtainScrollListener{
		@Override
		public void onScrollUp(){
		}

		@Override
		public void onScroll( int percent, boolean scrollUp ){
		}
		
		@Override
		public void onScrollOver( int percent, boolean scrollUp, boolean fromUser ){
		}

		@Override
		public void onScrollDown(){
		}
	}

	private BlindCover mCover;
	private final Scroller mScroller;
	private final GestureDetector mDetector;

	private int SCROLL_MAX_DISTANCE;
	private boolean mScrollUp;
	private boolean mFromUser;
	private boolean mCanScroll = true;
	private int mCurrentPercent;


	private OnCurtainScrollListener mScrollListener;

	public CurtainBlind( Context context )
	{
		this(context, null);
	}

	public CurtainBlind( Context context, AttributeSet attrs )
	{
		super(context, attrs);
		mScroller = new Scroller(context);
		mDetector = new GestureDetector(context, new ShadeGestureListener());
	}

	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();
		mCover = (BlindCover) getChildAt(0);

		int lameHeight = mCover.getStoreLameDrawableHeight();
		int ficelleHeight = mCover.getStoreFicelleDrawableHeight();
		SCROLL_MAX_DISTANCE = ficelleHeight - lameHeight;
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ){
		if (!mCanScroll) return super.onTouchEvent(event);
		mDetector.onTouchEvent(event);
		return true;
	}
	
	private class ShadeGestureListener extends SimpleOnGestureListener
	{
		@Override
		public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY ){
			mScrollUp = distanceY > 0;
			mFromUser = true;

			int currY = mScroller.getCurrY();
			int startY = currY + (int) distanceY;

			if (mScrollUp){
				if (startY <= SCROLL_MAX_DISTANCE) scroll(startY);
			}
			else{
				if (startY >= SCROLL_MIN_DISTANCE) scroll(startY);
			}
			return true;
		}
	}

	private void scroll( int startY ){
		mScroller.startScroll(0, startY, 0, 0);
		postInvalidate();
		if (mScrollUp){
			if (scrollListenerNotNull()) mScrollListener.onScrollUp();
		}
		else{
			if (scrollListenerNotNull()) mScrollListener.onScrollDown();
		}
	}

	private boolean scrollListenerNotNull(){
		return mScrollListener != null;
	}

	public void setOnCurtainScrollListener( OnCurtainScrollListener listener ){
		mScrollListener = listener;
	}
	
	public void setOnAngleChangeListener(OnAngleChangeListener listener){
		mCover.setOnAngleChangeListener(listener);
	}
	
	public void setBlindCanScroll( boolean enable ){
		mCanScroll = enable;
	}

	/**
	 * @param angle
	 * 			<b>[ 0, 100 ]</b>
	 */
	public void setAngle( int angle ){
		mCover.changeAngle(angle);
	}
	
	public int getAngle(){
		return Math.round(mCover.getAngle());
	}

	/**
	 * like 0(%), 20(%), 40(%), 75(%), 95(%), 100(%) <br/>
	 * <b><i>percent in [0,100]</i></b>
	 */
	public void setCurrentPercent( float percent ){
		float y = SCROLL_MAX_DISTANCE * (percent / 100);
		
		// over limit
		if(y < (SCROLL_MIN_DISTANCE - 1)  || y > SCROLL_MAX_DISTANCE) return;
		
		mFromUser = false;
		scroll((int) y);
	}

	@Override
	public void computeScroll(){
		if (mScroller.computeScrollOffset()){
			mCover.scrollTo(0, mScroller.getCurrY());
			postInvalidate();
			if (scrollListenerNotNull()){
				mCurrentPercent = Math.round((mScroller.getCurrY() / (SCROLL_MAX_DISTANCE * 1.0F)) * 100);
				mScrollListener.onScroll(mCurrentPercent, mScrollUp);
				
				if (mScroller.isFinished())
					mScrollListener.onScrollOver(mCurrentPercent, mScrollUp, mFromUser);
			}
		}
	}
}