package cc.wulian.app.model.device.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cc.wulian.app.model.device.R;

public class BlindCover extends RelativeLayout
{
	private static final float MAX_ANGLE_DEFAULT = 90F;
	private static final float MAX_ANGLE = 85F;
	
	public interface OnAngleChangeListener
	{
		public void onAngleChanged( int angle );
	}

	private Drawable mStoreLameDrawable;
	private Drawable mStorFicelleDrawable;

	private LinearLayout mLameLayout;
	private float mAngle;
	private OnAngleChangeListener mListener;

	public BlindCover( Context context )
	{
		super(context);
	}

	public BlindCover( Context context, AttributeSet attrs )
	{
		super(context, attrs);

		mStoreLameDrawable = getResources().getDrawable(R.drawable.device_blind_blade_big);
		mStorFicelleDrawable = getResources().getDrawable(R.drawable.device_blind_ficelle_big);
	}

	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();

		mLameLayout = (LinearLayout) getChildAt(0);

		int thisH = mStorFicelleDrawable.getIntrinsicHeight();
		int lameH = mStoreLameDrawable.getIntrinsicHeight();
		int howMany = Math.round(thisH / (lameH * 1.0F));

		for (int i = 0; i < howMany; i++){
			ImageView view = new ImageView(getContext());
			view.setFocusable(false);
			view.setFocusableInTouchMode(false);
			view.setImageDrawable(mStoreLameDrawable);
			mLameLayout.addView(view);
		}
	}
	
	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ){
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++){
			View child = getChildAt(i);

			widthSize = Math.min(widthSize, getStoreFicelleDrawableHeight());
			child.measure(resolveSize(widthSize, widthMeasureSpec),
					heightMeasureSpec);
		}
		super.onMeasure(widthSize, heightMeasureSpec);
	}

	protected int getStoreLameDrawableHeight(){
		return mStoreLameDrawable.getIntrinsicHeight();
	}

	protected int getStoreFicelleDrawableHeight(){
		return mStorFicelleDrawable.getIntrinsicHeight();
	}

	private boolean angleChangeListenerNotNull(){
		return mListener != null;
	}
	
	protected void setOnAngleChangeListener(OnAngleChangeListener listener){
		mListener = listener;
	}

	protected void changeAngle( int angle ){
		float limitAngle = angle >= MAX_ANGLE ? MAX_ANGLE : angle;
		mAngle = angleConvertIn(limitAngle);
		
		if(angleChangeListenerNotNull()) mListener.onAngleChanged((int) limitAngle);
		int count = mLameLayout.getChildCount();
		for (int i = 0; i < count; i++){
			applyRotation(mLameLayout.getChildAt(i), mAngle);
		}
	}
	
	private float angleConvertOut(float in){
		return (MAX_ANGLE_DEFAULT * in) / MAX_ANGLE;
	}
	
	private float angleConvertIn(float in){
		return (MAX_ANGLE * in) / MAX_ANGLE_DEFAULT;
	}
	
	protected float getAngle(){
		return angleConvertOut(mAngle);
	}

	private void applyRotation( View view, float end ){
		Animation animation = view.getAnimation();
		if (animation != null && !animation.hasEnded()) return;

		float start = 0;
		Rotate3dAnimation rotation;
		if (animation instanceof Rotate3dAnimation){
			rotation = (Rotate3dAnimation) animation;
			start = rotation.getToDegrees();
		}

		final float centerX = mStoreLameDrawable.getIntrinsicWidth() / 2.0f;
		final float centerY = mStoreLameDrawable.getIntrinsicHeight() / 2.0f;

		rotation = new Rotate3dAnimation(start, end, centerX, centerY, 0, false,
					Rotate3dAnimation.ROTATE_X);
		
		rotation.setDuration(0);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new LinearInterpolator());
		view.startAnimation(rotation);
	}
}