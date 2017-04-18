package com.tutk.IOTC;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AVIOCTRLDEFs {

	public static final int 	IOTYPE_USER_IPCAM_GET_LANGUAGE_REQ = 0x2831;
	public static final int   IOTYPE_USER_IPCAM_GET_LANGUAGE_RESP = 0x2832;
	/**add  mabo  SD格式化*/
	public static final int IOTYPE_USER_IPCAM_SDCARD_FORMAT_REQ=0x2835;
	public static final int IOTYPE_USER_IPCAM_SDCARD_FORMAT_RESP=0x2836;
	/**add syf*/
	public static final int  IOTYPE_USER_IPCAM_GET_SDCARD_STATUS_REQ = 0x2829;
	public static final int  IOTYPE_USER_IPCAM_GET_SDCARD_STATUS_RESP = 0x2830;
	/* AVAPIs IOCTRL Message Type */
	public static final int IOTYPE_USER_IPCAM_START = 0x01FF;
	public static final int IOTYPE_USER_IPCAM_STOP = 0x02FF;

	public static final int IOTYPE_USER_IPCAM_AUDIOSTART = 0x0300;
	public static final int IOTYPE_USER_IPCAM_AUDIOSTOP = 0x0301;

	public static final int IOTYPE_USER_IPCAM_SPEAKERSTART = 0x0350;
	public static final int IOTYPE_USER_IPCAM_SPEAKERSTOP = 0x0351;

	public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ = 0x0320;
	public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_RESP = 0x0321;
	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_REQ = 0x0322;
	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_RESP = 0x0323;

	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ = 0x0324;
	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP = 0x0325;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ = 0x0326;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP = 0x0327;

	public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ = 0x0328;
	public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP = 0x0329;

	public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ = 0x032A;
	public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_RESP = 0x032B;

	public static final int IOTYPE_USER_IPCAM_DEVINFO_REQ = 0x0330;
	public static final int IOTYPE_USER_IPCAM_DEVINFO_RESP = 0x0331;

	//猫眼发送固件升级命令
	public static final int IOTYPE_USER_IPCAM_STARTFWUPGRADE_REQ = 0x800;
	public static final int IOTYPE_USER_IPCAM_STARTFWUPGRADE_RESP = 0x801;
	
	public static final int IOTYPE_USER_IPCAM_SETPASSWORD_REQ = 0x0332;
	public static final int IOTYPE_USER_IPCAM_SETPASSWORD_RESP = 0x0333;

	public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_REQ = 0x0340;
	public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_RESP = 0x0341;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ = 0x0342;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_RESP = 0x0343;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_REQ = 0x0344;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP = 0x0345;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ_2 = 0x0346;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP_2 = 0x0347;

	public static final int IOTYPE_USER_IPCAM_SETRECORD_REQ = 0x0310;
	public static final int IOTYPE_USER_IPCAM_SETRECORD_RESP = 0x0311;
	public static final int IOTYPE_USER_IPCAM_GETRECORD_REQ = 0x0312;
	public static final int IOTYPE_USER_IPCAM_GETRECORD_RESP = 0x0313;

	public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_REQ = 0x0314;
	public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_RESP = 0x0315;
	public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_REQ = 0x0316;
	public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_RESP = 0x0317;

	public static final int IOTYPE_USER_IPCAM_LISTEVENT_REQ = 0x0318;
	public static final int IOTYPE_USER_IPCAM_LISTEVENT_RESP = 0x0319;

	public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL = 0x031A;
	public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP = 0x031B;

	public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_REQ = 0x0400;
	public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_RESP = 0x0401;
	public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_REQ = 0x0402;
	public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_RESP = 0x0403;

	public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ = 0x0360;
	public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_RESP = 0x0361;
	public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_REQ = 0x0362;
	public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_RESP = 0x0363;

	public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ = 0x0370;
	public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_RESP = 0x0371;
	public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ = 0x0372;
	public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP = 0x0373;

	public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_REQ = 0x380;
	public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP = 0x381;

	public static final int IOTYPE_USER_IPCAM_PTZ_COMMAND = 0x1001;
	public static final int IOTYPE_USER_IPCAM_PTZ_COMMAND_ACK = 0x2002;
	
	public static final int IOTYPE_USER_IPCAM_EVENT_REPORT = 0x1FFF;
	
	public static final int IOTYPE_USER_IPCAM_ICLOUDCAM_REQ	= 0xA55A;
	public static final int IOTYPE_USER_IPCAM_ICLOUDCAM_RESP = 0xA5A5;	
	
	public static final int IOTYPE_USER_IPCAM_FW_UPDATA_REQ = 0x2003;
	public static final int IOTYPE_USER_IPCAM_FW_UPDATA_RESP = 0x2004;

	public static final int IOTYPE_USER_IPCAM_SET_PREVIEW_MODE_REQ	= 0x2801;
    public static final int IOTYPE_USER_IPCAM_SET_PREVIEW_MODE_RESP	= 0x2802;
	
    public static final int IOTYPE_USER_IPCAM_GET_PREVIEW_MODE_REQ	= 0x2803;
    public static final int	IOTYPE_USER_IPCAM_GET_PREVIEW_MODE_RESP	= 0x2804;
    		
    public static final int IOTYPE_USER_IPCAM_SET_LANGUAGE_REQ = 0x2833;
    public static final int IOTYPE_USER_IPCAM_SET_LANGUAGE_RESP = 0x2834;

	//播报音量
	public static final int IOTYPE_USER_IPCAM_GET_VOLUME_REQ = 0x283F;
	public static final int IOTYPE_USER_IPCAM_GET_VOLUME_RESP  = 0x2840;

	public static final int IOTYPE_USER_IPCAM_SET_VOLUME_REQ = 0x2841;
	public static final int IOTYPE_USER_IPCAM_SET_VOLUME_RESP  = 0x2842;

	//红外夜视 add by hxc
	public static final int IOTYPE_USER_IPCAM_GET_IRCARD_REQ = 0x283B;
	public static final int IOTYPE_USER_IPCAM_GET_IRCARD_RESP = 0x283C;

	public static final int IOTYPE_USER_IPCAM_SET_IRCARD_REQ = 0x283D;
	public static final int IOTYPE_USER_IPCAM_SET_IRCARD_RESP = 0x283E;

	/* AVAPIs IOCTRL Event Type */
	public static final int AVIOCTRL_EVENT_ALL = 0x00;
	public static final int AVIOCTRL_EVENT_MOTIONDECT = 0x01;
	public static final int AVIOCTRL_EVENT_VIDEOLOST = 0x02;
	public static final int AVIOCTRL_EVENT_IOALARM = 0x03;
	public static final int AVIOCTRL_EVENT_MOTIONPASS = 0x04;
	public static final int AVIOCTRL_EVENT_VIDEORESUME = 0x05;
	public static final int AVIOCTRL_EVENT_IOALARMPASS = 0x06;
	public static final int AVIOCTRL_EVENT_EXPT_REBOOT = 0x10;
	public static final int AVIOCTRL_EVENT_SDFAULT = 0x11;
	/** low voltage */
	public static final int IOTYPE_USER_IPCAM_GET_BATTERY_REQ=0x60A;
	public static final int IOTYPE_USER_IPCAM_GET_BATTERY_RESP = 0x60B;
	/* AVAPIs IOCTRL Play Record Command */
	public static final int AVIOCTRL_RECORD_PLAY_PAUSE = 0x00;
	public static final int AVIOCTRL_RECORD_PLAY_STOP = 0x01;
	public static final int AVIOCTRL_RECORD_PLAY_STEPFORWARD = 0x02;
	public static final int AVIOCTRL_RECORD_PLAY_STEPBACKWARD = 0x03;
	public static final int AVIOCTRL_RECORD_PLAY_FORWARD = 0x04;
	public static final int AVIOCTRL_RECORD_PLAY_BACKWARD = 0x05;
	public static final int AVIOCTRL_RECORD_PLAY_SEEKTIME = 0x06;
	public static final int AVIOCTRL_RECORD_PLAY_END = 0x07;
	public static final int AVIOCTRL_RECORD_PLAY_START = 0x10;

	// AVIOCTRL PTZ Command Value
	public static final int AVIOCTRL_PTZ_STOP = 0;
	public static final int AVIOCTRL_PTZ_UP = 1;
	public static final int AVIOCTRL_PTZ_DOWN = 2;
	public static final int AVIOCTRL_PTZ_LEFT = 3;
	public static final int AVIOCTRL_PTZ_LEFT_UP = 4;
	public static final int AVIOCTRL_PTZ_LEFT_DOWN = 5;
	public static final int AVIOCTRL_PTZ_RIGHT = 6;
	public static final int AVIOCTRL_PTZ_RIGHT_UP = 7;
	public static final int AVIOCTRL_PTZ_RIGHT_DOWN = 8;
	public static final int AVIOCTRL_PTZ_AUTO = 9;
	public static final int AVIOCTRL_PTZ_SET_POINT = 10;
	public static final int AVIOCTRL_PTZ_CLEAR_POINT = 11;
	public static final int AVIOCTRL_PTZ_GOTO_POINT = 12;
	public static final int AVIOCTRL_PTZ_SET_MODE_START = 13;
	public static final int AVIOCTRL_PTZ_SET_MODE_STOP = 14;
	public static final int AVIOCTRL_PTZ_MODE_RUN = 15;
	public static final int AVIOCTRL_PTZ_MENU_OPEN = 16;
	public static final int AVIOCTRL_PTZ_MENU_EXIT = 17;
	public static final int AVIOCTRL_PTZ_MENU_ENTER = 18;
	public static final int AVIOCTRL_PTZ_FLIP = 19;
	public static final int AVIOCTRL_PTZ_START = 20;

	public static final int AVIOCTRL_LENS_APERTURE_OPEN = 21;
	public static final int AVIOCTRL_LENS_APERTURE_CLOSE = 22;
	public static final int AVIOCTRL_LENS_ZOOM_IN = 23;
	public static final int AVIOCTRL_LENS_ZOOM_OUT = 24;
	public static final int AVIOCTRL_LENS_FOCAL_NEAR = 25;
	public static final int AVIOCTRL_LENS_FOCAL_FAR = 26;

	public static final int AVIOCTRL_AUTO_PAN_SPEED = 27;
	public static final int AVIOCTRL_AUTO_PAN_LIMIT = 28;
	public static final int AVIOCTRL_AUTO_PAN_START = 29;

	public static final int AVIOCTRL_PATTERN_START = 30;
	public static final int AVIOCTRL_PATTERN_STOP = 31;
	public static final int AVIOCTRL_PATTERN_RUN = 32;

	public static final int AVIOCTRL_SET_AUX = 33;
	public static final int AVIOCTRL_CLEAR_AUX = 34;
	public static final int AVIOCTRL_MOTOR_RESET_POSITION = 35;

	/* AVAPIs IOCTRL Quality Type */
	public static final int AVIOCTRL_QUALITY_UNKNOWN = 0x00;
	public static final int AVIOCTRL_QUALITY_MAX = 0x01;
	public static final int AVIOCTRL_QUALITY_HIGH = 0x02;
	public static final int AVIOCTRL_QUALITY_MIDDLE = 0x03;
	public static final int AVIOCTRL_QUALITY_LOW = 0x04;
	public static final int AVIOCTRL_QUALITY_MIN = 0x05;

	/* AVAPIs IOCTRL WiFi Mode */
	public static final int AVIOTC_WIFIAPMODE_ADHOC = 0x00;
	public static final int AVIOTC_WIFIAPMODE_MANAGED = 0x01;

	/* AVAPIs IOCTRL WiFi Enc Type */
	public static final int AVIOTC_WIFIAPENC_INVALID = 0x00;
	public static final int AVIOTC_WIFIAPENC_NONE = 0x01;
	public static final int AVIOTC_WIFIAPENC_WEP = 0x02;
	public static final int AVIOTC_WIFIAPENC_WPA_TKIP = 0x03;
	public static final int AVIOTC_WIFIAPENC_WPA_AES = 0x04;
	public static final int AVIOTC_WIFIAPENC_WPA2_TKIP = 0x05;
	public static final int AVIOTC_WIFIAPENC_WPA2_AES = 0x06;

	/* AVAPIs IOCTRL Recording Type */
	public static final int AVIOTC_RECORDTYPE_OFF = 0x00;
	public static final int AVIOTC_RECORDTYPE_FULLTIME = 0x01;
	public static final int AVIOTC_RECORDTYPE_ALAM = 0x02;
	public static final int AVIOTC_RECORDTYPE_MANUAL = 0x03;

	public static final int AVIOCTRL_ENVIRONMENT_INDOOR_50HZ = 0x00;
	public static final int AVIOCTRL_ENVIRONMENT_INDOOR_60HZ = 0x01;
	public static final int AVIOCTRL_ENVIRONMENT_OUTDOOR = 0x02;
	public static final int AVIOCTRL_ENVIRONMENT_NIGHT = 0x03;

	/* AVIOCTRL VIDEO MODE */
	public static final int AVIOCTRL_VIDEOMODE_NORMAL = 0x00;
	public static final int AVIOCTRL_VIDEOMODE_FLIP = 0x01;
	public static final int AVIOCTRL_VIDEOMODE_MIRROR = 0x02;
	public static final int AVIOCTRL_VIDEOMODE_FLIP_MIRROR = 0x03;
	//add by fan  回访控制协议
	public static final int	IOTYPE_USER_IPCAM_SEARCH_PLAYBACK_FILE_REQ  = 0x2005;// 扫描文件信息
	public static final int	IOTYPE_USER_IPCAM_SEARCH_PLAYBACK_FILE_ACK  = 0x2006;
		
	public static final int	IOTYPE_USER_IPCAM_GET_PLAYBACK_STREAM_REQ  = 0x2007; // 获取视频流
	public static final int	IOTYPE_USER_IPCAM_GET_PLAYBACK_STREAM_ACK  = 0x2008; 
		
		//回放控制
	public static final int	IOTYPE_USER_IPCAM_GET_PLAYBACK_STOP_REQ	= 0x2009; // 回放停止
	public static final int	IOTYPE_USER_IPCAM_GET_PLAYBACK_STOP_ACK	= 0x200A; 
		
	public static final int	IOTYPE_USER_IPCAM_GET_PLAYBACK_RUN_REQ 	= 0x200B; // 播放
    public static final int	IOTYPE_USER_IPCAM_GET_PLAYBACK_RUN_ACK 	= 0x200C;
		
    public static final int	IOTYPE_USER_IPCAM_GET_PLAYBACK_OVER_REQ	= 0x200D;// 播放结束	
    public static final int	IOTYPE_USER_IPCAM_GET_PLAYBACK_OVER_ACK	= 0x200E;

	public static class SFrameInfo {

		short codec_id;
		byte flags;
		byte cam_index;
		byte onlineNum;
		byte[] reserved = new byte[3];
		int reserved2;
		int timestamp;

		public static byte[] parseContent(short codec_id, byte flags, byte cam_index, byte online_num, int timestamp) {

			byte[] result = new byte[16];

			byte[] codec = Packet.shortToByteArray_Little(codec_id);
			System.arraycopy(codec, 0, result, 0, 2);

			result[2] = flags;
			result[3] = cam_index;
			result[4] = online_num;

			byte[] time = Packet.intToByteArray_Little(timestamp);
			System.arraycopy(time, 0, result, 12, 4);

			return result;
		}
	}

	public static class SMsgAVIoctrlAVStream {
		int channel = 0; // camera index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlEventConfig {
		long channel; // Camera Index
		byte mail; // enable send email
		byte ftp; // enable ftp upload photo
		byte localIO; // enable local io output
		byte p2pPushMsg; // enable p2p push msg
	}

	public static class SMsgAVIoctrlPtzCmd {
		byte control; // ptz control command
		byte speed; // ptz control speed
		byte point;
		byte limit;
		byte aux;
		byte channel; // camera index
		byte[] reserved = new byte[2];

		public static byte[] parseContent(byte control, byte speed, byte point, byte limit, byte aux, byte channel) {
			byte[] result = new byte[8];

			result[0] = control;
			result[1] = speed;
			result[2] = point;
			result[3] = limit;
			result[4] = aux;
			result[5] = channel;

			return result;
		}
	}

	public static class SMsgAVIoctrlSetStreamCtrlReq {
		int channel; // Camera Index
		byte quality; // AVIOCTRL_QUALITY_XXXX
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int channel, byte quality) {

			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			result[4] = quality;

			return result;
		}
	}

	public class SMsgAVIoctrlGetStreamCtrlResp {
		int channel; // Camera Index
		byte quality; // AVIOCTRL_QUALITY_XXXX
		byte[] reserved = new byte[3];
	}

	public class SMsgAVIoctrlSetStreamCtrlResp {
		int result;
		byte[] reserved = new byte[4];
	}

	public static class SMsgAVIoctrlGetStreamCtrlReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public static class SMsgAVIoctrlSetPreviewModeReq {
		

		public static byte[] parseContent(int preview) {

			byte[] result = new byte[4];
		
			byte[] sen = Packet.intToByteArray_Little(preview);

			System.arraycopy(sen, 0, result, 0, 4);
			

			return result;
		}
	}

	//creat by guofeng
//	public static class SMsgAVIoctrlSetMotionDetectReq {
//		int channel; // Camera Index
//		int sensitivity; /* 0(disbale) ~ 100(MAX) */
//       
//		
//		public static byte[] parseContent(int channel,int enableswitch, int sensitivity,int[] area,int defenceused,int week,byte[] moveTime) {
//
//			
//			
//			byte[] result = new byte[104];
//			
//			byte[] ch = Packet.intToByteArray_Little(channel);   //4
//			System.arraycopy(ch, 0, result, 0, 4);
//			
//			
//			byte[] enableSw = Packet.intToByteArray_Little(enableswitch);  //4
//			System.arraycopy(enableSw, 0, result, 4, 4);
//			
//			
//			byte[] sen = Packet.intToByteArray_Little(sensitivity);  //4
//			System.arraycopy(sen, 0, result, 8, 4);
//			
//		
//			
//			for(int i=0; i < area.length;i++ ){         //80
//				
//				byte[]   temp = Packet.intToByteArray_Little(area[i]);
//				System.arraycopy(temp, 0, result, (4*i)+12, 4);
//			}
//			
//			
//            byte[] defenc = Packet.intToByteArray_Little(defenceused);  //4
//               System.arraycopy(defenc, 0, result, 92, 4);
//            
//            
//            byte[] we =Packet.intToByteArray_Little(week);     //4
//            System.arraycopy(we, 0, result, 96, 4);
//            
//         
//            System.arraycopy(moveTime, 0, result, 100, 4);   //4
//			
//
//			return result;
//		}
//	}
	
	
	public static class SMsgAVIoctrlSetMotionDetectReq {
		int channel; // Camera Index
		int sensitivity; /* 0(disbale) ~ 100(MAX) */
       
		
		public static byte[] parseContent(int enableswitch, int sensitivity,int[] area,int defenceused,int week,byte[] moveTime) {

			
			
			byte[] result = new byte[124];
			
			
			
			byte[] enableSw = Packet.intToByteArray_Little(enableswitch);  //4
			System.arraycopy(enableSw, 0, result, 0, 4);
			
			
			byte[] sen = Packet.intToByteArray_Little(sensitivity);  //4
			System.arraycopy(sen, 0, result, 4, 4);
			
		
			
			for(int i=0; i < area.length;i++ ){         //80
				
				byte[]   temp = Packet.intToByteArray_Little(area[i]);
				System.arraycopy(temp, 0, result, (4*i)+8, 4);
			}
			
			
            byte[] defenc = Packet.intToByteArray_Little(defenceused);  //4
               System.arraycopy(defenc, 0, result, 88, 4);
            
            
            byte[] we =Packet.intToByteArray_Little(week);     //4
            System.arraycopy(we, 0, result, 92, 4);
            
         
            System.arraycopy(moveTime, 0, result, 96, 4);   //4
            System.arraycopy(moveTime, 0, result, 100, 4);   //4
            System.arraycopy(moveTime, 0, result, 104, 4);   //4
            System.arraycopy(moveTime, 0, result, 108, 4);   //4
            System.arraycopy(moveTime, 0, result, 112, 4);   //4
            System.arraycopy(moveTime, 0, result, 116, 4);   //4
            System.arraycopy(moveTime, 0, result, 120, 4);   //4

			return result;
		}
	}
	public class SMsgAVIoctrlSetMotionDetectResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetMotionDetectReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlGetMotionDetectResp {
		int channel; // Camera Index
		int sensitivity; /* 0(disbale) ~ 100(MAX) */
	}

	public static class SMsgAVIoctrlDeviceInfoReq {

		static byte[] reserved = new byte[4];;

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public class SMsgAVIoctrlDeviceInfoResp {
		byte[] model = new byte[16];
		byte[] vendor = new byte[16];
		int version;
		int channel;
		int total; /* MByte */
		int free; /* MByte */
		byte[] reserved = new byte[8];
	}

	public static class SMsgAVIoctrlSetPasswdReq {
		byte[] oldPasswd = new byte[32];
		byte[] newPasswd = new byte[32];

		public static byte[] parseContent(String oldPwd, String newPwd) {

			byte[] oldpwd = oldPwd.getBytes();
			byte[] newpwd = newPwd.getBytes();
			byte[] result = new byte[64];

			System.arraycopy(oldpwd, 0, result, 0, oldpwd.length);
			System.arraycopy(newpwd, 0, result, 32, newpwd.length);

			return result;
		}
	}

	public class SMsgAVIoctrlSetPasswdResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlListWifiApReq {

		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {

			return reserved;
		}
	}

	public static class SWifiAp {
		public byte[] ssid = new byte[32];
		public byte mode;
		public byte enctype;
		public byte signal;
		public byte status;

		public static int getTotalSize() {
			return 36;
		}

		public SWifiAp(byte[] data) {

			System.arraycopy(data, 1, ssid, 0, data.length);
			mode = data[32];
			enctype = data[33];
			signal = data[34];
			status = data[35];
		}

		public SWifiAp(byte[] bytsSSID, byte bytMode, byte bytEnctype, byte bytSignal, byte bytStatus) {

			System.arraycopy(bytsSSID, 0, ssid, 0, bytsSSID.length);
			mode = bytMode;
			enctype = bytEnctype;
			signal = bytSignal;
			status = bytStatus;
		}
	}

	public class SMsgAVIoctrlListWifiApResp {
		int number; // MAX: 1024/36= 28
		SWifiAp stWifiAp;
	}

	public static class SMsgAVIoctrlSetWifiReq {
		byte[] ssid = new byte[32];
		byte[] password = new byte[32];
		byte mode;
		byte enctype;
		byte[] reserved = new byte[10];

		public static byte[] parseContent(byte[] ssid, byte[] password, byte mode, byte enctype) {

			byte[] result = new byte[76];

			System.arraycopy(ssid, 0, result, 0, ssid.length);
			System.arraycopy(password, 0, result, 32, password.length);
			result[64] = mode;
			result[65] = enctype;

			return result;
		}
	}

	public class SMsgAVIoctrlSetWifiResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetWifiReq {

		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public class SMsgAVIoctrlGetWifiResp {
		byte[] ssid = new byte[32];
		byte[] password = new byte[32];
		byte mode;
		byte enctype;
		byte signal;
		byte status;
	}

	public static class SMsgAVIoctrlSetRecordReq {
		int channel; // Camera Index
		int recordType;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel, int recordType) {

			byte[] result = new byte[12];
			byte[] ch = Packet.intToByteArray_Little(channel);
			byte[] type = Packet.intToByteArray_Little(recordType);

			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(type, 0, result, 4, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlSetRecordResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetRecordReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlGetRecordResp {
		int channel; // Camera Index
		int recordType;
	}

	public class SMsgAVIoctrlSetRcdDurationReq {
		int channel; // Camera Index
		int presecond;
		int durasecond;
	}

	public class SMsgAVIoctrlSetRcdDurationResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public class SMsgAVIoctrlGetRcdDurationReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];
	}

	public class SMsgAVIoctrlGetRcdDurationResp {
		int channel; // Camera Index
		int presecond;
		int durasecond;
	}

	public static class STimeDay {

		private byte[] mBuf;
		public short year;
		public byte month;
		public byte day;
		public byte wday;
		public byte hour;
		public byte minute;
		public byte second;

		public STimeDay(byte[] data) {

			mBuf = new byte[8];
			System.arraycopy(data, 0, mBuf, 0, 8);

			year = Packet.byteArrayToShort_Little(data, 0);
			month = data[2];
			day = data[3];
			wday = data[4];
			hour = data[5];
			minute = data[6];
			second = data[7];	
		}

		public long getTimeInMillis() {

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			cal.set(year, month, day, hour, minute, second);

			return cal.getTimeInMillis();
		}

		public String getLocalTime() {

			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			calendar.setTimeInMillis(getTimeInMillis());
			calendar.add(Calendar.MONTH, -1);

			SimpleDateFormat dateFormat = new SimpleDateFormat();
			dateFormat.setTimeZone(TimeZone.getDefault());

			return dateFormat.format(calendar.getTime());
		}

		public byte[] toByteArray() {
			return mBuf;
		}

		public static byte[] parseContent(int year, int month, int day, int wday, int hour, int minute, int second) {

			byte[] result = new byte[8];

			byte[] y = Packet.shortToByteArray_Little((short) year);
			System.arraycopy(y, 0, result, 0, 2);

			result[2] = (byte) month;
			result[3] = (byte) day;
			result[4] = (byte) wday;
			result[5] = (byte) hour;
			result[6] = (byte) minute;
			result[7] = (byte) second;

			return result;
		}
	}

	public static class SMsgAVIoctrlListEventReq {

		int channel; // Camera Index
		byte[] startutctime = new byte[8];
		byte[] endutctime = new byte[8];
		byte event;
		byte status;
		byte[] reversed = new byte[2];

		public static byte[] parseConent(int channel, long startutctime, long endutctime, byte event, byte status) {

			Calendar startCal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			Calendar stopCal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			startCal.setTimeInMillis(startutctime);
			stopCal.setTimeInMillis(endutctime);

			System.out.println("search from " + startCal.get(Calendar.YEAR) + "/" + startCal.get(Calendar.MONTH) + "/" + startCal.get(Calendar.DAY_OF_MONTH)
					+ " " + startCal.get(Calendar.HOUR_OF_DAY) + ":" + startCal.get(Calendar.MINUTE) + ":" + startCal.get(Calendar.SECOND));
			System.out.println("       to   " + stopCal.get(Calendar.YEAR) + "/" + stopCal.get(Calendar.MONTH) + "/" + stopCal.get(Calendar.DAY_OF_MONTH) + " "
					+ stopCal.get(Calendar.HOUR_OF_DAY) + ":" + stopCal.get(Calendar.MINUTE) + ":" + stopCal.get(Calendar.SECOND));

			byte[] result = new byte[24];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] start = STimeDay.parseContent(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH) + 1, startCal.get(Calendar.DAY_OF_MONTH),
					startCal.get(Calendar.DAY_OF_WEEK), startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), 0);
			System.arraycopy(start, 0, result, 4, 8);

			byte[] stop = STimeDay.parseContent(stopCal.get(Calendar.YEAR), stopCal.get(Calendar.MONTH) + 1, stopCal.get(Calendar.DAY_OF_MONTH),
					stopCal.get(Calendar.DAY_OF_WEEK), stopCal.get(Calendar.HOUR_OF_DAY), stopCal.get(Calendar.MINUTE), 0);
			System.arraycopy(stop, 0, result, 12, 8);

			result[20] = event;
			result[21] = status;

			return result;
		}
		 public static byte[] parseConent(int channel,short year,byte[] startutctime, byte[] endutctime, byte event, byte status) {

				

				byte[] result = new byte[24];

				byte[] ch = Packet.intToByteArray_Little(channel);
				System.arraycopy(ch, 0, result, 0, 4);

				byte[] ye = Packet.shortToByteArray_Little(year);
				System.arraycopy(ye, 0, result, 4, 2);			
				System.arraycopy(startutctime, 0, result, 6, 6);

				System.arraycopy(ye, 0, result, 12, 2);
				System.arraycopy(endutctime, 0, result, 14, 6);

				result[20] = event;
				result[21] = status;

				return result;
			}
	}

	public static class SAvEvent {
		byte[] utctime = new byte[8];
		byte event;
		byte status;
		byte[] reserved = new byte[2];

		public static int getTotalSize() {
			return 12;
		}
	}

	public class SMsgAVIoctrlListEventResp {
		int channel; // Camera Index
		int total;
		byte index;
		byte endflag;
		byte count;
		byte reserved;
		SAvEvent stEvent;
	}

	public static class SMsgAVIoctrlPlayRecord {
		int channel; // Camera Index
		int command; // play record command
		int Param;
		byte[] stTimeDay = new byte[8];
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel, int command, int param, long time) {

			byte[] result = new byte[24];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] cmd = Packet.intToByteArray_Little(command);
			System.arraycopy(cmd, 0, result, 4, 4);

			byte[] p = Packet.intToByteArray_Little(param);
			System.arraycopy(p, 0, result, 8, 4);

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			cal.setTimeInMillis(time);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.add(Calendar.DATE, 1);
			byte[] timedata = STimeDay.parseContent(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			System.arraycopy(timedata, 0, result, 12, 8);

			return result;
		}

		public static byte[] parseContent(int channel, int command, int param, byte[] time) {

			byte[] result = new byte[24];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] cmd = Packet.intToByteArray_Little(command);
			System.arraycopy(cmd, 0, result, 4, 4);

			byte[] p = Packet.intToByteArray_Little(param);
			System.arraycopy(p, 0, result, 8, 4);

			System.arraycopy(time, 0, result, 12, 12);// last :6

			return result;
		}
		
		public static byte[] parseContent(int channel, int command,int paramuser, short param, byte[] time) {

			byte[] result = new byte[24];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] cmd = Packet.intToByteArray_Little(command);
			System.arraycopy(cmd, 0, result, 4, 4);

			byte[] cmd1 = Packet.intToByteArray_Little(paramuser);
			System.arraycopy(cmd1, 0, result, 8, 4);
			
			byte[] p = Packet.shortToByteArray_Little(param);
			System.arraycopy(p, 0, result, 12, 2);

			System.arraycopy(time, 0, result, 14, 6);

			return result;
		}	
		
	}

	// only for play record start command
	public class SMsgAVIoctrlPlayRecordResp {
		int channel;
		int result;
		byte[] reserved = new byte[4];
	} // only for play record start command

	public class SMsgAVIoctrlEvent {
		STimeDay stTime; // 8 bytes
		int channel; // Camera Index
		int event; // Event Type
		byte[] reserved = new byte[4];
	}

	public static class SMsgAVIoctrlSetVideoModeReq {
		int channel; // Camera Index
		byte mode; // Video mode
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int channel, byte mode) {
			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			result[4] = mode;

			return result;
		}
	}

	public class SMsgAVIoctrlSetVideoModeResp {
		int channel; // Camera Index
		byte result; // 1 - succeed, 0 - failed
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetVideoModeReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlGetVideoModeResp {
		int channel; // Camera Index
		byte mode; // Video Mode
		byte[] reserved = new byte[3];
	}

	
	
	public static class SMsgAVIoctrlGetPlayBackFileReq {

		public static byte[] parseContent(byte[] startTime,byte[] endTime) {
			byte[] result = new byte[20];
//			byte[] ch = Packet.intToByteArray_Little(0);
			byte[] ch = new byte[]{0x1f,0x00,0x00,0x00};//modfi syf默认查询所有类型
			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(startTime, 0, result, 4, 8);
			System.arraycopy(endTime, 0, result, 12, 8);
			
			
			return result;
		}
	}
	
	
	public static class SMsgAVIoctrlSetEnvironmentReq {
		int channel; // Camera Index
		byte mode; // Environment mode
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int channel, byte mode) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			result[4] = mode;

			return result;
		}
	}

	public class SMsgAVIoctrlSetEnvironmentResp {

		int channel; // Camera Index
		byte result; // 1 - succeed, 0 - failed
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetEnvironmentReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlGetEnvironmentResp {
		int channel; // Camera Index
		byte mode; // Environment Mode
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlFormatExtStorageReq {

		int storage; // Storage index (ex. sdcard slot = 0, internal flash = 1,
						// ...)
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int storage) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(storage);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlFormatExtStorageResp {

		int storage; // Storage index
		byte result; // 0: success;
						// -1: format command is not supported.
						// otherwise: failed.

		byte[] reserved = new byte[3];
	}

	public static class SStreamDef {

		public int index; // the stream index of camera
		public int channel; // the channel index used in AVAPIs

		public SStreamDef(byte[] data) {

			index = Packet.byteArrayToShort_Little(data, 0);
			channel = Packet.byteArrayToShort_Little(data, 2);
		}

		public String toString() {
			return ("CH" + String.valueOf(index + 1));
		}
	}

	public static class SMsgAVIoctrlGetSupportStreamReq {

		public static byte[] parseContent() {

			return new byte[4];
		}

		public static int getContentSize() {
			return 4;
		}
	}

	public class SMsgAVIoctrlGetSupportStreamResp {

		public SStreamDef mStreamDef[];
		public long number;
	}

	public static class SMsgAVIoctrlGetAudioOutFormatReq {
		public static byte[] parseContent() {
			return new byte[8];
		}
	}

	public class SMsgAVIoctrlGetAudioOutFormatResp {
		public int channel;
		public int format;
	}

	public class SMsgAVIoctrliCloudCamReq {
		public int session;
		byte[] reserved = new byte[4];
	}
	public class SMsgAVIoctrliCloudCamResq {
		public int session;
		public int iCloudCam;
		byte[] reserved = new byte[4];
	}
	// add syf 转换开始时间
	public static class SMsgAVIoctrlSetPlayBackFileNowReq {
        public SMsgAVIoctrlSetPlayBackFileNowReq() {
        }
        public static byte[] parseContent(byte[] startTime, byte[] filename) {
            byte[] result = new byte[44];
            System.arraycopy(startTime, 0, result, 0, 8);
            System.arraycopy(filename, 0, result, 8, 33);
            return result;
        }
    }
	 public static class SMsgAVIoctrlSetHistoryStop {
	        public SMsgAVIoctrlSetHistoryStop() {
	        }

	        public static byte[] parseContent(int channel) {
	            byte[] result = new byte[4];
	            byte[] ch = Packet.intToByteArray_Little(channel);
	            System.arraycopy(ch, 0, result, 0, 4);
	            return result;
	        }
	    }
	 public static class SMsgAVIoctrlSetMotionPir {
			int channel; // Camera Index
			int sensitivity; /* 0(disbale) ~ 100(MAX) */
			public static byte[] parseContent(int channel, int sensitivity) {

				byte[] result = new byte[8];
				byte[] ch = Packet.intToByteArray_Little(channel);
				byte[] sen = Packet.intToByteArray_Little(sensitivity);

				System.arraycopy(ch, 0, result, 0, 4);
				System.arraycopy(sen, 0, result, 4, 4);
				return result;
			}
		}
}
