package com.wulian.iot.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.tutk.IOTC.Packet;
import com.wulian.iot.bean.GalleryAlarmInfo;
import com.wulian.iot.bean.GalleryInfo;
import com.wulian.iot.bean.VideotapeInfo;

/**
 * Created by Administrator on 2016/4/28 0028.
 */
public class EagleUtil {
    public static Bitmap albumCompress(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        int wratio = (int) Math.ceil(opts.outWidth / 500);
        int hratio = (int) Math.ceil(opts.outHeight / 500);
        if (wratio > 1 && hratio > 1) {
            if (wratio > hratio) {
                opts.inSampleSize = wratio;
            } else {
                opts.inSampleSize = hratio;
            }
        }
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, opts);
        return bitmap;
    }
    public static String DataToInt(Date dateImg){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
        String strDate = sdf.format(dateImg);
        String[] strs = strDate.split("/");
        int[] ints = {Integer.parseInt(strs[0]),Integer.parseInt(strs[1]),Integer.parseInt(strs[2])};
        return ints[0]+"-"+ints[1]+"-"+ints[2];
    }
    
    
    public static Map<String, String> getDate(String date){
    	Map<String, String> mdate=null;
    	if (date!=null) {
    		mdate=new HashMap<>();
    		String []s=date.split("-");
			int year,month,day;
			year= Integer.valueOf(s[0]);
			month=Integer.valueOf(s[1]);
			mdate.put("year", year+"");
			if (month<=9){
				mdate.put("month", "0"+month);
			}else {
				mdate.put("month", month+"");
			}
			day=Integer.valueOf(s[2]);
			if (day<=9){
				mdate.put("day", "0"+day);
			}else {
				mdate.put("day", day+"");
			}
			System.out.println("------>查询时间"+Integer.valueOf(s[0])+Integer.valueOf(s[1])+Integer.valueOf(s[2]));
		}
    	System.out.println(mdate.get("year"));
    	return mdate;
    }
    /**
     * video info
     * mabo  2016-6-25
     * @param path
     * @return List<VideotapeInfo>
     */
    public static List<VideotapeInfo> getVideoInfo(String path){
    	List<VideotapeInfo> video=null;
    	VideotapeInfo videoifo=null;
    	File file=new File(path);
    	File [] files= file.listFiles();
    	 // 将所有文件存入list中  
        if(files != null){  
        	video=new ArrayList<VideotapeInfo>();
            int count = files.length;// 文件个数  
            System.out.println("count:"+count);
            for (int i = 0; i < count; i++) {  
                File fl = files[i];  
//                video.add(fl.getName()); 
//                video.add(fl.getPath());
                videoifo=new VideotapeInfo();
                System.out.println("count:"+fl.getName()+fl.getPath());
                videoifo.setFileName(fl.getName());
                videoifo.setVideoLocation(fl.getPath());
//                videoifo.setBitmap(bitmap)
//                fl.lastModified(); 时间
                video.add(videoifo);
            }  
        }
    	return video;
    }
	/**
	 * 猫眼 处理version
	 * mabo
	 * @param version 
	 * @return versionName
	 */
	public static String interceptionString(int version){
		String st=Integer.toHexString(version).toString();
		int a=Integer.valueOf(st.substring(0, 1));
		int b=Integer.valueOf(st.substring(1, 3));
		int c=Integer.valueOf(st.substring(3, 5));
		st="v"+a+"."+b+"."+c;
		return st;
	}
	
	  /**
     * 解析字节数组 获取视屏列表
     * mabo 
     * @param data
     * 修改与7 月 19号
     */
	public static List<GalleryAlarmInfo> parsePlayBackFileInfo(byte[] data) {
		int channel;		//Camera Index
		int total;			//Total event amount in this search session
		                     //short  index;           // char  index;            //package index 0,1,2....
		short endflag;            //  char endflag;           // end flag endflag = 1 means this package is last one
		short count;        // how much events in this package 
		
		short year;
		int month;
		int day;
		int hour;
		int minutes;
		int second;
		channel = DateUtil.bytesToInt(data, 0);
		Log.i("IOTCamera", "------------channel:"+channel);
		total = DateUtil.bytesToInt(data, 4);
		Log.i("IOTCamera", "------------total:"+total);
		endflag = DateUtil.bytesToShortBig(data, 8);
		Log.i("IOTCamera", "------------endflag:"+endflag);
		count = DateUtil.bytesToShortLittle(data, 10);
		Log.i("IOTCamera", "------------count:"+count);
		if (total < count ) {	
			Log.i("IOTCamera", "------------total < count");
			return null;
		}
		//获取设备FileName
		   // 
		byte[] ipFileName= new byte[12];
	    
		List<GalleryAlarmInfo> videoList=new ArrayList<GalleryAlarmInfo>();
		for (int i = 0; i < count; i++) {
			GalleryAlarmInfo gInfo = new GalleryAlarmInfo();
			byte[] tempSetTime = new byte[6];
			System.arraycopy(data, 12+12*i, ipFileName, 0, 12);
			year = Packet.byteArrayToShort_Little(ipFileName, 0);
			month = DateUtil.byteToChar(ipFileName[2]);
				tempSetTime[0] = ipFileName[2];
			day = DateUtil.byteToChar(ipFileName[3]);
				tempSetTime[1] = ipFileName[3];
				tempSetTime[2] = 0;
			hour = DateUtil.byteToChar(ipFileName[5])+8; //时区的缘故这个地方加8 应该做正确的时区处理方法，有时间的话。
				tempSetTime[3] = ipFileName[5];
			minutes = DateUtil.byteToChar(ipFileName[6]);
				tempSetTime[4] = ipFileName[6];
			second = DateUtil.byteToChar(ipFileName[7]);
				tempSetTime[5] = ipFileName[7];
			String vlist=year+"."+month+"."+day+"  "+hour+":"+minutes+":"+second;
			gInfo.setTitle(vlist);
			gInfo.setYear(year);
			gInfo.setTimeAck(tempSetTime);
			gInfo.setTimeReceive(ipFileName);
			Log.i("IOTCamera", "----------------year:"+year+"month"+month+"day"+day+"hour"+hour+"minutes"+minutes+"second"+second);
			videoList.add(gInfo);
		}
	 return videoList;
	}
	public static byte  stringToHexBytes(int obj){
		byte hex = 0;
		String aim = null;
		aim = String.valueOf(obj);
		if(aim !=null&&!aim.trim().equals(" ")){
			hex =  Byte.parseByte(aim, 16);
		}
		return hex;
	}
	public static Bitmap rotateBitmap(Bitmap bitmap ,int degree) throws Exception{
		Bitmap res = null;
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(),
				matrix, true);
		if(dstbmp!=null){
			res = dstbmp;
		}
		return res;
	}
	public static byte convertToByte(int input){
		byte out=0x00;
		String str16=Integer.toHexString(input);
		out=Byte.parseByte(str16,16);
		return out;
	}

	/**
	 * 处理零时区的时间
	 * @param zerotime
	 * @return byte[] 发送命令所需的格式
     */
	public static byte[] timeTobyte(String zerotime){
		String str []=null;
		if (zerotime!=null){
			 str=zerotime.split(" ");//按空格把年月日和时分秒分开
		}
		String [] str1=str[0].split("-");//年月日
		String [] str2=str[1].split(":");//时分秒
		//年在这里不需要，byte数组是16进制的且格式为：月，日，0x00,时，分，秒
		byte [] by={convertToByte(Integer.parseInt(str1[1])),convertToByte(Integer.parseInt(str1[2])),0x00,convertToByte(Integer.parseInt(str2[0])),
				convertToByte(Integer.parseInt(str2[1])),convertToByte(Integer.parseInt(str2[2]))};
		return  by;
	}

}
