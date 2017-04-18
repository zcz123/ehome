package com.tutk.IOTC;

public class AVFrame
{
  public static final int AUDIO_CHANNEL_MONO = 0;
  public static final int AUDIO_CHANNEL_STERO = 1;
  public static final int AUDIO_DATABITS_16 = 1;
  public static final int AUDIO_DATABITS_8 = 0;
  public static final int AUDIO_SAMPLE_11K = 1;
  public static final int AUDIO_SAMPLE_12K = 2;
  public static final int AUDIO_SAMPLE_16K = 3;
  public static final int AUDIO_SAMPLE_22K = 4;
  public static final int AUDIO_SAMPLE_24K = 5;
  public static final int AUDIO_SAMPLE_32K = 6;
  public static final int AUDIO_SAMPLE_44K = 7;
  public static final int AUDIO_SAMPLE_48K = 8;
  public static final int AUDIO_SAMPLE_8K = 0;
  public static final int FRAMEINFO_SIZE = 24;
  public static final byte FRM_STATE_COMPLETE = 0;
  public static final byte FRM_STATE_INCOMPLETE = 1;
  public static final byte FRM_STATE_LOSED = 2;
  public static final byte FRM_STATE_UNKOWN = -1;
  public static final int IPC_FRAME_FLAG_IFRAME = 1;
  public static final int IPC_FRAME_FLAG_IO = 3;
  public static final int IPC_FRAME_FLAG_MD = 2;
  public static final int IPC_FRAME_FLAG_PBFRAME = 0;
  public static final int MEDIA_CODEC_AUDIO_AAC = 136;
  public static final int MEDIA_CODEC_AUDIO_ADPCM = 139;
  public static final int MEDIA_CODEC_AUDIO_G711A = 138;
  public static final int MEDIA_CODEC_AUDIO_G711U = 137;
  public static final int MEDIA_CODEC_AUDIO_G726 = 143;
  public static final int MEDIA_CODEC_AUDIO_MP3 = 142;
  public static final int MEDIA_CODEC_AUDIO_PCM = 140;
  public static final int MEDIA_CODEC_AUDIO_SPEEX = 141;
  public static final int MEDIA_CODEC_UNKNOWN = 0;
  public static final int MEDIA_CODEC_VIDEO_H263 = 77;
  public static final int MEDIA_CODEC_VIDEO_H264 = 78;
  public static final int MEDIA_CODEC_VIDEO_MJPEG = 79;
  public static final int MEDIA_CODEC_VIDEO_MPEG4 = 76;
  private short codec_id = 0;
  private byte flags = -1;
  
  public byte[] frmData = null;
 
  
  private long frmNo = -1L;
  private int frmSize = 0;
  private byte frmState = 0;
  private byte onlineNum = 0;
  private int timestamp = 0;
  
  private int frameSequence = 0;
  
  private int videoHeight = 0;
  private int videoWidth = 0;
  
  private volatile int mKeepFramSize;

  public AVFrame(long paramLong, byte paramByte, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    this.codec_id = Packet.byteArrayToShort_Little(paramArrayOfByte1, 0);
    this.frmState = paramByte;
   
    
    this.flags = paramArrayOfByte1[2];
    this.onlineNum = paramArrayOfByte1[4];
    this.timestamp = Packet.byteArrayToInt_Little(paramArrayOfByte1, 12);
  
    this.frameSequence = Packet.byteArrayToInt_Little(paramArrayOfByte1,8);
    
    if (paramArrayOfByte1.length > 16);
    for (int i = Packet.byteArrayToInt_Little(paramArrayOfByte1, 16); ; i = 0)
    {
      this.videoWidth = i;
      int j = paramArrayOfByte1.length;
      int k = 0;
      if (j > 16)
        k = Packet.byteArrayToInt_Little(paramArrayOfByte1, 20);
      this.videoHeight = k;
      this.frmSize = paramInt;
   
      
      this.frmData = paramArrayOfByte2;
   
      
      
      this.frmNo = paramLong;
      return;
    }
  }

  public synchronized void setKeepFram(int keepFrame) {
      mKeepFramSize = keepFrame;
  }
  
  public static int getSamplerate(byte paramByte)
  {
    switch (paramByte >>> 2)
    {
    default:
      return 8000;
    case 0:
      return 8000;
    case 1:
      return 11025;
    case 2:
      return 12000;
    case 3:
      return 16000;
    case 4:
      return 22050;
    case 5:
      return 24000;
    case 6:
      return 32000;
    case 7:
      return 44100;
    case 8:
    }
    return 48000;
  }

  public static short parseCodecId(byte[] paramArrayOfByte)
  {
    return Packet.byteArrayToShort_Little(paramArrayOfByte, 0);
  }

  public static boolean parseIfIFrame(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length >= 3)
      return (0x1 & paramArrayOfByte[2]) == 1;
    return false;
  }

  public static byte parseOnlineNum(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length >= 5)
      return paramArrayOfByte[4];
    return -1;
  }

  public short getCodecId()
  {
    return this.codec_id;
  }

  public byte getFlags()
  {
    return this.flags;
  }

  public long getFrmNo()
  {
    return this.frmNo;
  }

  public int getFrmSize()
  {
    return this.frmSize;
  }

  public byte getFrmState()
  {
    return this.frmState;
  }

  public byte getOnlineNum()
  {
    return this.onlineNum;
  }

  public int getTimeStamp()
  {
    return this.timestamp;
  }
  
  
  public int getFrameSequence()
  {
    return this.frameSequence;
  }
  
  
  public int getVideoHeight()
  {
    return this.videoHeight;
  }

  public int getVideoWidth()
  {
    return this.videoWidth;
  }

  public boolean isIFrame()
  {
    return (0x1 & this.flags) == 1;
  }
}

/* Location:           C:\Users\Administrator\Desktop\tutkdemo.jar
 * Qualified Name:     com.tutk.IOTC.AVFrame
 * JD-Core Version:    0.5.4
 */