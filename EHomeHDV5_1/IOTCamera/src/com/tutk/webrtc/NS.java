package com.tutk.webrtc;

public class NS
{
  private static final int BUFFER_SIZE_16K = 160;
  private static final int BUFFER_SIZE_32K = 160;
  private static final int BUFFER_SIZE_8K = 80;
  public static final int NS_LEAVL_AGGRESSIVE = 2;
  public static final int NS_LEAVL_MEDUIM = 1;
  public static final int NS_LEAVL_MILD = 0;
  public static final long SAMPLE_RATE_16K = 16000L;
  public static final long SAMPLE_RATE_32K = 32000L;
  public static final long SAMPLE_RATE_8K = 8000L;
  private boolean isInit = false;
  private int mHandle;
  private long mSampleRate;

  static
  {
    try
    {
      System.loadLibrary("WebRtc");
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
    }
  }
  private native int nativeCreate();
  private native int nativeDo(int paramInt, short[] paramArrayOfShort1, short[] paramArrayOfShort2);
  private native int nativeFree(int paramInt);
  private native int nativeInit(int paramInt, long paramLong);
  private native int nativeSetNsLeavl(int paramInt1, int paramInt2);
  public boolean Create(int i)
  {
      mSampleRate = i;
      for(mHandle = nativeCreate(); mHandle == -1 || nativeInit(mHandle, i) == -1 || nativeSetNsLeavl(mHandle, 2) == -1;)
          return false;

      isInit = true;
      return true;
  }

  public void UpdateValToLeavl(short[] paramArrayOfShort, float paramFloat)
  {
		for (int i = 0; i < paramArrayOfShort.length; i++) {
			int j = (int) (paramFloat * paramArrayOfShort[i]);
			if ((j < 32767) && (j > -32768)) {
				paramArrayOfShort[i] = (short) j;
			} else {
				if (j > 32767) {
					paramArrayOfShort[i] = 32767;
				}
				if (j >= -32768) {
					paramArrayOfShort[i] = -32768;
				}
			}
		}
//    while (true)
//    {
//      ++i;
//      if (j > 32767)
//        paramArrayOfShort[i] = 32767;
//      if (j >= -32768)
//        continue;
//      paramArrayOfShort[i] = -32768;
//    }
  }

  public boolean isInit()
  {
    return isInit;
  }
  public boolean release()
  {
    if (nativeFree(mHandle) == -1){
      return false;
    }
    return true;
  }
  public boolean run(short[] paramArrayOfShort1, short[] paramArrayOfShort2, float paramFloat)
  {
    int i = 80;
    UpdateValToLeavl(paramArrayOfShort1, paramFloat);
    int j = 0;
    int k = 0;
    if (this.mSampleRate == 8000L)
    {
      j = -1;
      k = paramArrayOfShort1.length;
    }
    for (int l = 0; ; ++l)
    {
      if (l >= k / i)
      {
        if (j != -1)
          break;
        return false;
      }
      short[] arrayOfShort1 = new short[i];
      short[] arrayOfShort2 = new short[i];
      System.arraycopy(paramArrayOfShort1, i * l, arrayOfShort1, 0, i);
      j = nativeDo(this.mHandle, arrayOfShort1, arrayOfShort2);
      System.arraycopy(arrayOfShort2, 0, paramArrayOfShort2, i * l, i);
    }
    return true;
  }
}
