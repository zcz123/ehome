package cc.wulian.app.model.device.view;

import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class AnimationUtil
{
	public static RotateAnimation getRotateAnimation( float speed ){
		RotateAnimation animation = new RotateAnimation(0F, speed, RotateAnimation.RELATIVE_TO_SELF,
				0.5F, RotateAnimation.RELATIVE_TO_SELF, 0.5F);
		animation.setDuration(10000);
		animation.setRepeatCount(RotateAnimation.INFINITE);
		animation.setInterpolator(new LinearInterpolator());
		return animation;
	};
}
