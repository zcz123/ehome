package com.wulian.iot.connect;

import com.wulian.iot.bean.CameraEagleUpdateInfo;

public abstract class ConnectEagleManage {

	public  String error(Throwable throwable){
		return throwable.getMessage();
	}
	public  String error(String msg){
		return msg;
	}
	public void success(CameraEagleUpdateInfo mCameraEagleUpdateInfo){
		
	}
	public void success(String msg){
	}
}
