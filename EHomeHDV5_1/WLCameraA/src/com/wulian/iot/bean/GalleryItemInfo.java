package com.wulian.iot.bean;

import android.graphics.Bitmap;

public class GalleryItemInfo extends BaseCameraInfo{
	 private Bitmap bitmap;
	 private String itemPath;
	 private String itemName;
	 private boolean isCheck;
	 private boolean isSelectd;
	 public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	 public Bitmap getBitmap() {
		return bitmap;
	}
	 public void setItemPath(String itemPath) {
		this.itemPath = itemPath;
	}
	 public String getItemPath() {
		return itemPath;
	}
	 public boolean isCheck() {
		return isCheck;
	}
	 public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	 public void setSelectd(boolean isSelectd) {
		this.isSelectd = isSelectd;
	}
	 public boolean isSelectd() {
		return isSelectd;
	}
	 public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	 public String getItemName() {
		return itemName;
	}
}
