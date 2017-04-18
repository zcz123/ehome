package com.wulian.iot.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2016/11/10 0010.
 */

public class AnimationUtils {
    /**
     * 闪烁动画
     * @param view
     */
    public static void startFlick( View view ){
        if( null == view ){
            return;
        }
        Animation alphaAnimation = new AlphaAnimation( 1, 0 );
        alphaAnimation.setDuration( 300 );
        alphaAnimation.setInterpolator( new LinearInterpolator( ) );
        alphaAnimation.setRepeatCount( Animation.INFINITE );
        alphaAnimation.setRepeatMode( Animation.REVERSE );
        view.startAnimation( alphaAnimation );
    }

    /**
     * 取消动画
     * @param view
     */
    public static void stopFlick( View view ){
        if( null == view ){
            return;
        }
        view.clearAnimation( );
    }
}
