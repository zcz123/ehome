package com.yuantuo.customview.anim;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

public class CustomAnimationDrawable extends AnimationDrawable
{
	private Handler mHandler;
	private AnimaotionListener listener;

	public CustomAnimationDrawable( AnimationDrawable ad )
	{
		for (int i = 0; i < ad.getNumberOfFrames(); i++){
			this.addFrame(ad.getFrame(i), ad.getDuration(i));
		}
	}

	@Override
	public void start(){
		super.start();
		if (listener != null){
			mHandler = new Handler();
			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run(){
					listener.onAnimationEnd();
					listener = null;
				}
			}, getAllFrameDuration());
		}
	}

	private int getAllFrameDuration(){
		int index = 0;
		for (int i = 0; i < getNumberOfFrames(); i++){
			index += getDuration(i);
		}
		return index;
	}

	public void setListener( AnimaotionListener listener ){
		this.listener = listener;
	}

	public interface AnimaotionListener
	{
		public void onAnimationEnd();
	}
}
