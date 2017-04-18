package com.wulian.iot.server.controller;
import java.util.List;

import android.content.Context;

import com.wulian.iot.bean.GalleryInfo;
import com.wulian.iot.bean.GalleryRoute;
public interface IEagleGallery {
	 String [] findGalleryFilesByFolder(String folder, String date);
	List<GalleryInfo> findGalleryItemByFileName(String folder, String files);
	int  delFileByWay(GalleryRoute galleryRoute);
}
