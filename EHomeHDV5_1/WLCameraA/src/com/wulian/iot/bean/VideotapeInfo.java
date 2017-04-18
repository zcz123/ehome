package com.wulian.iot.bean;

import java.io.Serializable;
import java.util.Date;

import android.graphics.Bitmap;

public class VideotapeInfo extends BaseCameraInfo{

	private String videoLocation;
    private String fileName;
    private Bitmap bitmap;
    private int videoType;//录像类型  0 手机本地录像 1摄像机
	
    public String getVideoLocation() {
		return videoLocation;
	}
	public void setVideoLocation(String videoLocation) {
		this.videoLocation = videoLocation;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}
	public int getVideoType() {
		return videoType;
	}
}
