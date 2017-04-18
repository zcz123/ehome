package com.wulian.iot;

public class HandlerConstant {

	public final static int SKIPHISTORY = 0001;
	
	public final static int INITDATA = 1000; 
	public final static int FILLDATA = 1001;
	
	public final static int SUCCESS =10000;
	public final static int ERROR   =10001;
	
	public final static int DOWNLOAD = 2000;
	public final static int DOWNLOAD_FIRMWARE = 2001;
	public final static int UPDATE_UI = 3000;
	public final static int DOWNLOAD_FINISH = 2002;
	
	public final static int INSTALL_SUCCESS = 2003;
	public final static int INSTALL_ERROR= 2004;
	
	public final static int IOC_SD_DAMAGE = 3000; 
	public final static int IOC_SD_FINE = 3001;
	public final static int IOC_SD_CHECK = 30002;
	
	public final static int BAIDU_REGISTER_SERVER_BY_GET = 40001;
	public final static int BAIDU_REGISTER_MAPPING_BY_GET = 40002;
	public final static int BAIDU_UNRE_MAPPING = 40003;
	
	public final static int GATEWAYINFOUSER = 50001;//获取用户绑定设备
	public final static int GATEDEVICEINFOBYGWID = 50002;//获取设备详细信息
	
	/***********设备绑定返回状态***************/
	public final static int AMS_COMMON_SUCCESSFUL = 0;//绑定成功
	public final static int AMS_COMMON_ERRORFUL = -1;//绑定成功
	public final static int AMS_DEVICE_HAVE_BINDED = 2103;//重复绑定设备 
	public final static int AMS_DEVICE_INFO_LOST = 2106;//设备信息不完整 
	public final static int AMS_ACCOUNT_TOKEN_FAILURE = 2000;
	public final static int DEVICE_ONLINE = 6000;//设备上线
	
	/*********************鹰眼配网************************/
	public final static int EAGLE_SETING_WIFI = 7000;
	
	public final static int TIEM_IS_UP = 8000;//门锁中 计时
}
