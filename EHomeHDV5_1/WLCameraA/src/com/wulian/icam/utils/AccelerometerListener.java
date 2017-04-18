/**
 * Project Name:  iCam
 * File Name:     AccelerometerListener.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2015年4月29日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.icam.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * @ClassName: AccelerometerListener
 * @Function:  加速度监视器
 * @Date:      2015年4月29日
 * @author     Puml
 * @email      puml@wuliangroup.cn
 */
public class AccelerometerListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mOrientation;
    private int mPendingOrientation;
    private OrientationListener mListener;
    
    public static final int ORIENTATION_UNKNOWN = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    public static final int ORIENTATION_HORIZONTAL = 2;

    private static final int ORIENTATION_CHANGED = 1234;

    private static final int VERTICAL_DEBOUNCE = 100;
    private static final int HORIZONTAL_DEBOUNCE = 500;
    private static final double VERTICAL_ANGLE = 50.0;
    
    public interface OrientationListener {
        public void orientationChanged(int orientation);
    }
    
    public AccelerometerListener(Context context, OrientationListener listener) {
    	 mListener = listener;
    	 mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
    	 mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
    
    public void enable1(boolean enable) {
    	synchronized (this) {
    		if (enable) {
    			
    		}
    	}
    }
}

