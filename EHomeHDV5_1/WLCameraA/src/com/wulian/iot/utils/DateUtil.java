package com.wulian.iot.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.text.TextUtils;

public class DateUtil
{
	
	
	
	/**
	 * "yyyy-MM-dd HH.mm.ss"
	 */
	public static String getFormatIMGTime( long time ){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		return format.format(new Date(time));
	}
	
	
	
	/**
	 * @Function 将3转成03
	 * @author Wangjj
	 * @date 2015年5月28日
	 * @param in
	 * @return
	 */

	public static String fillZeroBeforeSingleNum(String in) {
		if (TextUtils.isEmpty(in)) {
			return "";
		}
		return in.length() == 1 ? "0" + in : in;
	}
	

	/**  
	    * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用 
	    *   
	    * @param src  
	    *            byte数组  
	    * @param offset  
	    *            从数组的第offset位开始  
	    * @return int数值  
	    */    
	public static short bytesToShortBig(byte[] src, int offset) {  
		short value;    
	    value =   (short) ((src[offset] & 0xFF<<8)   
	            | ((src[offset+1] & 0xFF))   
	           );  
	    return value;  
	} 
	/**  
	    * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用 
	    *   
	    * @param src  
	    *            byte数组  
	    * @param offset  
	    *            从数组的第offset位开始  
	    * @return int数值  
	    */    
	public static short bytesToShortLittle(byte[] src, int offset) {  
		short value;    
	    value = (short) ((src[offset] & 0xFF)   
	            | ((src[offset+1] & 0xFF<<8))   
	           );  
	    return value;  
	} 
	 public static int byteToChar(byte b) {
		   int c = (int) (((b & 0xFF) ) );
	        return c;
	    }
	
	/**
	 * @Function 将数组里的所有3转成03
	 * @author Wangjj
	 * @date 2015年5月28日
	 * @param ins
	 */

	public static void formatSingleNum(String[] ins) {
		for (int i = 0; i < ins.length; i++) {
			ins[i] = fillZeroBeforeSingleNum(ins[i]);
		}
	}
	
	/**  
	    * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用 
	    *   
	    * @param src  
	    *            byte数组  
	    * @param offset  
	    *            从数组的第offset位开始  
	    * @return int数值  
	    */    
	public static int bytesToInt(byte[] src, int offset) {  
	    int value;    
	    value =  ((src[offset] & 0xFF)   
	            | ((src[offset+1] & 0xFF)<<8)   
	            | ((src[offset+2] & 0xFF)<<16)   
	            | ((src[offset+3] & 0xFF)<<24));  
	    return value;  
	}  
	

	/**
	 * byte[] 转为String
	 */
	public static String Bytes2HexString(byte[] b) { 
	    String ret = ""; 
	    for (int i = 0; i < b.length; i++) { 
	        String hex = Integer.toHexString(b[i] & 0xFF); 
	        if (hex.length() == 1) { 
	            hex = '0' + hex; 
	        } 
	        ret += hex.toUpperCase(); 
	    } 
	    return ret; 
	} 
	
	/**
	 * byte[] 转为Char
	 */
	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	}
	/**bytes 转为 String*/
	public static  String bytesToStying(byte[] bytes) {
		StringBuffer  tStringBuf=new StringBuffer();
		char[] tChars=new char[bytes.length];
		for(int i=0;i<bytes.length;i++)
			tChars[i]=(char)bytes[i];
		tStringBuf.append(tChars);
		return tStringBuf.toString();
	}
	/**long 转换为byte*/
	 public static byte[] longToByte(long l) {
	        byte[] byt;
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        DataOutputStream dos = new DataOutputStream(baos);
	        try {
	            dos.writeLong(l);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        byt = baos.toByteArray();
	        return byt;
	    }
	/**
	 * 时区 时间转换方法:将当前时间（可能为其他时区）转化成目标时区对应的时间
	 * @param sourceTime 时间格式必须为：yyyy-MM-dd HH:mm:ss
	 * @return string 转化时区后的时间
	 */
	public static String timeToZeroZone(String sourceTime) {
		//获取当前的时区
		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = cal.getTimeZone();
		String sourceId = timeZone.getID();
		//目标时区  （一般是是零时区：取值UTC）
		String targetId = "UTC";
		//校验入参是否合法
		if (null == sourceId || "".equals(sourceId) || null == targetId
				|| "".equals(targetId) || null == sourceTime|| "".equals(sourceTime)){
			return "";
		}
		//校验 时间格式必须为：yyyy-MM-dd HH:mm:ss
		String reg = "^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$";
		if (!sourceTime.matches(reg)){
			return "";
		}
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			TimeZone sourceTimeZone = TimeZone.getTimeZone(sourceId);
			df.setTimeZone(sourceTimeZone);
			Date sourceDate = df.parse(sourceTime);
			TimeZone targetTimeZone = TimeZone.getTimeZone(targetId);
			df.setTimeZone(targetTimeZone);
			String targetTime = df.format(sourceDate);
			return targetTime;
		}catch (ParseException e){
			e.printStackTrace();
		}
		return "";
	}
}