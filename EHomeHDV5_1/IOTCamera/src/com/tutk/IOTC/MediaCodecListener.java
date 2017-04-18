package com.tutk.IOTC;

import android.view.MotionEvent;

public abstract interface MediaCodecListener
{
  public abstract void Unavailable();

  public abstract void touchSurface(MotionEvent paramMotionEvent);

  public abstract void zoomSurface(float paramFloat);
}

/* Location:           C:\Users\Administrator\Desktop\tutkdemo.jar
 * Qualified Name:     com.tutk.IOTC.MediaCodecListener
 * JD-Core Version:    0.5.4
 */