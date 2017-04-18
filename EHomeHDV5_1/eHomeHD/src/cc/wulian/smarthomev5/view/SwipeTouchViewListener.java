package cc.wulian.smarthomev5.view;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.yuantuo.customview.nineoldandroids.view.ViewHelper;
import com.yuantuo.customview.nineoldandroids.view.ViewPropertyAnimator;

public class SwipeTouchViewListener implements OnTouchListener {

	private View frontView = null, backView = null;
	private float oldPointX = 0;
	private float offsetLeft = 0;
	private float animateOffset = 0;
	private boolean isOpen = false;

	public SwipeTouchViewListener(View frontView, View backView) {
		super();
		this.frontView = frontView;
		this.backView = backView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent motionEvent) {
		// TODO Auto-generated method stub

		switch (MotionEventCompat.getActionMasked(motionEvent)) {
			case MotionEvent.ACTION_DOWN: {
	
				// LogUtils.d("ACTION_DOWN");
				offsetLeft = backView.getWidth();
				animateOffset = offsetLeft/3;
				oldPointX = motionEvent.getRawX();
				return false;
			}
	
			case MotionEvent.ACTION_UP: {
				float deltaX = motionEvent.getRawX() - oldPointX;
				if(deltaX >0 && deltaX < animateOffset && isOpen){
					generateSwipeAnimate(frontView, -offsetLeft);
				}else if(deltaX <0 && deltaX > -animateOffset && !isOpen){
					generateSwipeAnimate(frontView, 0);
				}
			}
			case MotionEvent.ACTION_CANCEL:{
				float deltaX = motionEvent.getRawX() - oldPointX;
				if(deltaX >0 && deltaX < animateOffset && isOpen){
					generateSwipeAnimate(frontView, -offsetLeft);
				}else if(deltaX <0 && deltaX > -animateOffset && !isOpen){
					generateSwipeAnimate(frontView, 0);
				}
			}
			case MotionEvent.ACTION_MOVE: {
				float deltaX = motionEvent.getRawX() - oldPointX;
				if (deltaX >= animateOffset && isOpen) {
					generateSwipeAnimate(frontView, 0);
					isOpen = false;
					return true;
				}else if(deltaX >0 && deltaX < animateOffset&& isOpen){
					move(-offsetLeft + deltaX);
					return true;
				}
				else if (deltaX < -animateOffset && !isOpen) {
					generateSwipeAnimate(frontView, -offsetLeft);
					isOpen = true;
					return true;
				}else if(deltaX <0 && deltaX > -animateOffset && !isOpen){
					move(deltaX);
					return true;
				}
			}
		}
		return false;
	}

	private void move(float deltaX) {
		if (Math.abs(deltaX) <= offsetLeft) {
			ViewHelper.setTranslationX(frontView, deltaX);
		}
	}

	private void generateSwipeAnimate(final View view, final float dx) {
		ViewPropertyAnimator.animate(view).translationX(dx).setDuration(0);
	}
}