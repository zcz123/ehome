package com.tutk.IOTC;

import android.view.MotionEvent;
import android.view.View;
import java.io.IOException;

public abstract interface IMonitor
{
  public static final int HARDWARE_DECODE_ALLOW_DELAYTIME = 30;
  public static final int SOFTWARE_DECODE_ALLOW_DELAYTIME = 1500;

  public abstract void HorizontalScrollTouch(View paramView, MotionEvent paramMotionEvent);

  public abstract void SetOnMonitorClickListener(MonitorClickListener paramMonitorClickListener);

  public abstract void attachCamera(Camera paramCamera, int paramInt);

  public abstract void cleanFrameQueue();

  public abstract void deattachCamera();

  public abstract void enableDither(boolean paramBoolean);

  public abstract void resetCodec();

  public abstract void setMaxZoom(float paramFloat);

  public abstract void setMediaCodecListener(MediaCodecListener paramMediaCodecListener);

  public abstract void setMonitorBackgroundColor(int paramInt);

  public abstract void setReceiveotListener(IReceiveSnapshotListener paramIReceiveSnapshotListener);

  public abstract void snapshot()
    throws IOException;
}

/* Location:           C:\Users\Administrator\Desktop\tutkdemo.jar
 * Qualified Name:     com.tutk.IOTC.IMonitor
 * JD-Core Version:    0.5.4
 */