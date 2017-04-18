package com.tutk.IOTC;

import java.io.PrintStream;

public class AVAPIs
{
  public static final int API_ER_ANDROID_NULL = -10000;
  public static final int AV_ER_BUFPARA_MAXSIZE_INSUFF = -20001;
  public static final int AV_ER_CLIENT_EXIT = -20018;
  public static final int AV_ER_CLIENT_NOT_SUPPORT = -20020;
  public static final int AV_ER_CLIENT_NO_AVLOGIN = -20008;
  public static final int AV_ER_DATA_NOREADY = -20012;
  public static final int AV_ER_EXCEED_MAX_ALARM = -20005;
  public static final int AV_ER_EXCEED_MAX_CHANNEL = -20002;
  public static final int AV_ER_EXCEED_MAX_SIZE = -20006;
  public static final int AV_ER_FAIL_CREATE_THREAD = -20004;
  public static final int AV_ER_INCOMPLETE_FRAME = -20013;
  public static final int AV_ER_INVALID_ARG = -20000;
  public static final int AV_ER_INVALID_SID = -20010;
  public static final int AV_ER_LOSED_THIS_FRAME = -20014;
  public static final int AV_ER_MEM_INSUFF = -20003;
  public static final int AV_ER_NOT_INITIALIZED = -20019;
  public static final int AV_ER_NO_PERMISSION = -20023;
  public static final int AV_ER_NoERROR = 0;
  public static final int AV_ER_REMOTE_TIMEOUT_DISCONNECT = -20016;
  public static final int AV_ER_SENDIOCTRL_ALREADY_CALLED = -20021;
  public static final int AV_ER_SENDIOCTRL_EXIT = -20022;
  public static final int AV_ER_SERVER_EXIT = -20017;
  public static final int AV_ER_SERV_NO_RESPONSE = -20007;
  public static final int AV_ER_SESSION_CLOSE_BY_REMOTE = -20015;
  public static final int AV_ER_TIMEOUT = -20011;
  public static final int AV_ER_WRONG_ACCPWD_LENGTH = -20024;
  public static final int AV_ER_WRONG_VIEWACCorPWD = -20009;
  public static final int IOTYPE_INNER_SND_DATA_DELAY = 255;
  public static final int TIME_DELAY_DELTA = 1;
  public static final int TIME_DELAY_INITIAL = 0;
  public static final int TIME_DELAY_MAX = 500;
  public static final int TIME_DELAY_MIN = 4;
  public static final int TIME_SPAN_LOSED = 1000;
  public static final int UNKNOWN = -14;
  static
  {
    try
    {
      System.loadLibrary("AVAPIs");
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      System.out.println("loadLibrary(AVAPIs)," + localUnsatisfiedLinkError.getMessage());
    }
  }

  public static native void AV_Set_Log_Path(String paramString, int paramInt);

  public static native int avCheckAudioBuf(int paramInt);

  public static native int avClientCleanAudioBuf(int paramInt);

  public static native int avClientCleanBuf(int paramInt);

  public static native int avClientCleanVideoBuf(int paramInt);

  public static native void avClientExit(int paramInt1, int paramInt2);

  public static native void avClientSetMaxBufSize(int paramInt);    

  public static int avClientStart(int paramInt1, String paramString1, String paramString2, int paramInt2, int[] paramArrayOfInt, int paramInt3)
  {
    byte[] arrayOfByte = paramString2.getBytes();
    return avClientStart(paramInt1, paramString1.getBytes(), arrayOfByte, paramInt2, paramArrayOfInt, paramInt3);
  }

  public static native int avClientStart(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2, int[] paramArrayOfInt, int paramInt3);

  public static native int avClientStart2(int paramInt1, String paramString1, String paramString2, int paramInt2, int[] paramArrayOfInt1, int paramInt3, int[] paramArrayOfInt2);

  public static native void avClientStop(int paramInt);

  public static native int avDeInitialize();

  public static native int avGetAVApiVer();

  public static native int avInitialize(int paramInt);

  public static native int avRecvAudioData(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int[] paramArrayOfInt);

  public static native int avRecvFrameData(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int[] paramArrayOfInt);

  public static native int avRecvFrameData2(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, byte[] paramArrayOfByte2, int paramInt3, int[] paramArrayOfInt3, int[] paramArrayOfInt4);

  public static native int avRecvIOCtrl(int paramInt1, int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt2, int paramInt3);

  public static native float avResendBufUsageRate(int paramInt);

  public static native int avSendAudioData(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3);

  public static native int avSendFrameData(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3);

  public static native int avSendIOCtrl(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3);

  public static native int avSendIOCtrlExit(int paramInt);

  public static native void avServExit(int paramInt1, int paramInt2);

  public static native int avServResetBuffer(int paramInt1, int paramInt2, int paramInt3);

  public static native int avServSetDelayInterval(int paramInt1, int paramInt2, int paramInt3);

  public static native void avServSetResendSize(int paramInt1, int paramInt2);

  public static native int avServStart(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4);

  public static native int avServStart2(int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, int paramInt4);

  public static native int avServStart3(int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt);

  public static native void avServStop(int paramInt);



}

/* Location:           C:\Users\Administrator\Desktop\tutkdemo.jar
 * Qualified Name:     com.tutk.IOTC.AVAPIs
 * JD-Core Version:    0.5.4
 */