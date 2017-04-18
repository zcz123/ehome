package com.wulian.iot.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/4/29 0029.
 */
public class GalleryListItemInfo extends BaseCameraInfo{

    private Bitmap bitmap;
    private int isOver;
    private int isCheck;
    private String videoPath;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public int getIsOver() {
        return isOver;
    }

    public void setIsOver(int isOver) {
        this.isOver = isOver;
    }
    public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}
    public String getVideoPath() {
		return videoPath;
	}
}
