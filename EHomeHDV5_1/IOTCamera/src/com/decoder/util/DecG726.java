package com.decoder.util;

public class DecG726 {
	  public static final int API_ER_ANDROID_NULL = -10000;
	  public static final byte FORMAT_ALAW = 1;
	  public static final byte FORMAT_LINEAR = 2;
	  public static final byte FORMAT_ULAW = 0;
	  public static final int G726_16 = 0;
	  public static final int G726_24 = 1;
	  public static final int G726_32 = 2;
	  public static final int G726_40 = 3;

	  public static native int g711_decode(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2);

	  public static native int g726_dec_state_create(byte paramByte1, byte paramByte2);

	  public static native void g726_dec_state_destroy();

	  public static native int g726_decode(byte[] paramArrayOfByte1, long paramLong, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
	static {
		try {
			System.loadLibrary("G726Android");
		} catch (UnsatisfiedLinkError ule) {
			System.out.println("loadLibrary(G726Android)," + ule.getMessage());
		}
	}
}
