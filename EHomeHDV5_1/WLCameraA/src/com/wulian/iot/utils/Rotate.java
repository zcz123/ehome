package com.wulian.iot.utils;

import android.content.Context;
import android.content.res.Configuration;

import android.view.OrientationEventListener;

public class Rotate extends OrientationEventListener{
      public Context context;
	  public Rotate(Context context) {
          super(context);
          this.context = context;
        	
      }
	@Override
      public void onOrientationChanged(int orientation) {
          //如果屏幕旋转被打开，则设置屏幕可旋转
          //0-57度 125-236度 306-360度  这些区间范围内为竖屏
          //58-124度 237-305度  这些区间范围内为横屏
          if ((orientation == -1 || (orientation >= 0) && (orientation <= 57)) || ((orientation >= 125) && (orientation <= 236)) || (orientation >= 306 && orientation <= 360)) {
            //竖屏
              callBack(true);
          } else if ((orientation >= 58 && orientation <= 124) || ((orientation >= 237 && orientation <= 305))) {
             //横屏
              callBack(false);
          }
      }
      public void callBack(boolean mScreenOrientation ){
    	  
      }
      public boolean isScreenChange() {

    	  Configuration mConfiguration = this.context.getResources().getConfiguration(); //获取设置的配置信息
    	  int ori = mConfiguration.orientation ; //获取屏幕方向

    	  if(ori == mConfiguration.ORIENTATION_LANDSCAPE){
    	  //横屏
    	  return true;
    	  }else if(ori == mConfiguration.ORIENTATION_PORTRAIT){
    	  //竖屏
    	  return false;
    	  }
    	  return false;
    	  }
}
