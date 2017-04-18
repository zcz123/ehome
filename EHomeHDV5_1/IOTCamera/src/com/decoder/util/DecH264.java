package com.decoder.util;

import android.util.Log;

public class DecH264{
  private final static String TAG = "DecH264";
  public static native int DecoderNal(byte[] paramArrayOfByte1, int paramInt, int[] paramArrayOfInt, byte[] paramArrayOfByte2, boolean paramBoolean);

  public static native int DecoderNalV2(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, int[] paramArrayOfInt, byte[] paramArrayOfByte2, boolean paramBoolean);

  public static native int DeinitDecoderV2(int paramInt);

  public static native int InitDecoder();

  public static native int InitDecoderV2(int[] paramArrayOfInt);

  public static native void SetMaxAVCodecCtxNum(int paramInt);

  public static native int UninitDecoder();
/* Location:           C:\Users\Administrator\Desktop\tutkdemo.jar
 * Qualified Name:     com.decoder.util.DecH264
 * JD-Core Version:    0.5.4
 */

	static {
		try {
			System.loadLibrary("H264Android_DESK");
		} catch (UnsatisfiedLinkError ule) {
			Log.e(TAG,ule.getMessage());
		}
	}
}
