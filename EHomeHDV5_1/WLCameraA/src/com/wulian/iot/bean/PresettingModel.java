package com.wulian.iot.bean;

import com.wulian.icam.R;

import android.content.Context;
import android.graphics.drawable.Drawable;

public  class PresettingModel extends BaseCameraInfo{
	public  final static int defaultPresetting = 0;
	public final static int gatewayPresetting = 1;
	private  String pName;
    private  Drawable pImg;
    private  boolean  exit;
    private int rotateIndex;
    public PresettingModel(){
    	
    }
    public PresettingModel(String pName,Drawable pImg,boolean exit){
    	this.setpImg(pImg);
    	this.setpName(pName);
    	this.setExit(exit);
    }
    public PresettingModel(String pName,Drawable pImg,boolean exit,int rotateIndex ){
    	this.setpImg(pImg);
    	this.setpName(pName);
    	this.setExit(exit);
    	this.setRotateIndex(rotateIndex);
    }
    public void setpImg(Drawable pImg) {
		this.pImg = pImg;
	}
    public Drawable getpImg() {
		return pImg;
	}
    public void setpName(String pName) {
		this.pName = pName;
	}
    public String getpName() {
		return pName;
	}
    public void setExit(boolean exit) {
		this.exit = exit;
	}
    public boolean isExit() {
		return exit;
	}
    public void setRotateIndex(int rotateIndex) {
		this.rotateIndex = rotateIndex;
	}
    public int getRotateIndex() {
		return rotateIndex;
	}
    public static PresettingModel defaultData(Context mContext){
    	return new PresettingModel("", mContext.getResources().getDrawable(R.drawable.test_add_pos), false);
    }
}
