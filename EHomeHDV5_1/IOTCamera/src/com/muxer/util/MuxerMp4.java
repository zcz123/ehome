package com.muxer.util;

import android.util.Log;

public class MuxerMp4 {
	
	 
	private final static  int   NAL_TYPE_SPS = 7;
	private final static  int   NAL_TYPE_PPS = 8;
	private final static int   NAL_TYPE_SEI = 6;
	private final static int   NAL_TYPE_IDR = 5;
	
	private  static int sps_number;
	private  static int pps_number;
	
	private  static  byte[]  sps_byte;
	


	private  static  byte[]  pps_byte;
	
	static {
		
	  try{
		  System.loadLibrary("CameraShooting");  
	  }catch (UnsatisfiedLinkError localUnsatisfiedLinkError){
	      System.out.println("-------android-codec()," + localUnsatisfiedLinkError.getMessage());
	    }
	}

	
	public static native boolean mp4init(String paramInt1, int paramInt2,byte[] paramInt3,int paramInt4, byte[] paramInt5,int paramInt6,boolean paramInt7);

	public static native void mp4packVideo(byte[] paramInt1, int paramInt2, int paramInt3,boolean paramInt4);

	public static native void mp4packAudio(byte[] paramLong, int paramByteBuffer1);

	public static native void mp4close();
	
	public static native String stringFromJNI();
	
	  /**
		 * @Function 保存bitmap到sp中
		 * @author Guofeng
		 * @date 2016年3月27日
		 * @param key
		 *            主要使用场景：key为deviceid时，保存的是截屏；key为userid时，保存的是头像
		 * @param bitmap
		 * @param context
		 *  返回SEI长度，出错返回RBUFF_FAIL spsLen 长度 
		 */
/* example data
	0x00 0x00 0x00 0x01 0x27 0x42 0x80 0x1f 0xda 0x02 0xc0 0xf4 0xc0 0x55 0x20 0x00 
	0x00 0x03 0x00 0x20 0x00 0x00 0x12 0xcc 0x08 0x00 0x1f 0x40 0x00 0x01 0xf4 0x01 
	0x7b 0xdf 0x0b 0xc2 0x21 0x1a 0x80 0x00 0x00 0x00 0x01 0x28 0xce 0x3c 0x80 0x00 
	0x00 0x00 0x01 0x06 0x00 0x07 0x81 0x1e 0x59 0x80 0x00 0x00 0x40 0x01 0x06 0x00 
	0x00 0x96 0x00 0x00 0x03 0x00 0x80 0x00 0x00 0x00 0x01 0x25 0x88 0x84 0x09 0x7f 
	0xfe 0xdf 0xe0 0x35 0xf0 0x78 0x10 0xff 0xb6 0xa4 0xd3 0x66 0x19 0x8b 0x09 0x24 
	0x96 0x5b 0x28 0x36 0x09 0xe2 0x40 0xe5 0x68 0xfa 0x7f 0xe3 0xf8 0x7f 0xc7 0x08 
	0xad 0x86 0xcc 0x02 0xe7 0xc7 0x87 0x91 0x31 0x22 0xd7 0xf1 0x4c 0x45 0x13 0xf1 
	0x2b 0x58 0x11 0x5b 0x10 0xa8 0x88 0x5f 0xca 0xf1 0x10 0xb9 
 */

	
	
	public static	void SeparateIFrame_GetSpsPpsSeiLen(byte[] data)
	{

		Log.i("data",data.toString()+"");
		if (data == null) {
			return ;
		}
		int pos = 0;
		int  nalType = 0;
		int pps = 0, sps = 0;
		int numBegin = 0;
		boolean firstBegin = true;
		byte[]  ptr = new byte[100];  //保留data数据 这是风格，不能操作
		for(int i =0; i< 100 ; i++){
			ptr[i] = data[i];
			//Log.i("IOTCamera", "-------------guou"+ptr[i]);
		}
	    byte[] temp = ptr;

		while(pos < 100)
		{
			if(temp[pos] ==0 && temp[pos+1] == 0 && temp[pos+2] == 0 && temp[pos+3] == 1)
			{
				 if (firstBegin) {
					 firstBegin = false;
					 numBegin =pos;
				}
				    nalType = (temp[pos+4] & 0x1F);
	              	pos =pos+3;
			}
			switch(nalType)
			{
				case NAL_TYPE_SPS:
				    sps++;
				    break;

				case NAL_TYPE_PPS:
				    pps++;
				    break;
				case NAL_TYPE_SEI:
				    break;
				case NAL_TYPE_IDR:
				   pos  = 99;    //跳出循环
				     break;
					/* 到IDR帧，表示已经完成 排列顺序 [SPS|PPS|SEI|IDR] */

	        }    			
			pos++;
			//Log.e("IOTCamera", "--------spsguo"+sps+"pps"+pps+"nalType"+nalType);
	    }

		//拷贝 sps pps
		Log.e("IOTCamera", "--------sps"+sps+"pps"+pps+"nalType"+nalType);
		sps= 48;
		pps= 5;
		nalType = 5;
		sps_byte =new byte[sps-1];
		pps_byte =new byte[pps-1];
			System.arraycopy(ptr, numBegin+4, sps_byte, 0, sps-1);
			System.arraycopy(ptr, numBegin+4+4+sps-1, pps_byte, 0, pps-1);


			sps_number = sps - 1;
			pps_number =pps - 1;



			
	     
	}

	public static int getSps_number() {
		return sps_number;
	}

	public static int getPps_number() {
		return pps_number;
	}

	
	
	public static byte[] getSps() {
		return sps_byte;
	}

	public static byte[] getPps() {
		return pps_byte;
	}

	public static void setSps_byte(byte[] sps_byte) {
		MuxerMp4.sps_byte = sps_byte;
	}

	public static void setPps_byte(byte[] pps_byte) {
		MuxerMp4.pps_byte = pps_byte;
	}
	

}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
