package com.wulian.iot.server.controller.logic;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.provider.MediaStore;
import android.util.Log;

import com.wulian.iot.bean.GalleryInfo;
import com.wulian.iot.bean.GalleryItemInfo;
import com.wulian.iot.bean.GalleryRoute;
import com.wulian.iot.server.controller.IEagleGallery;
import com.wulian.iot.utils.EagleUtil;
import com.wulian.iot.utils.IotUtil;
public class EagleGalleryLogicImpl implements IEagleGallery{
	private final static String TAG = "EagleGalleryLogicImpl";
	@Override
	public String [] findGalleryFilesByFolder( String folder, String date) {
		        String []fileName = null;
				if(folder!=null&&!folder.trim().equals("")){
					File [] files = null;
					files = IotUtil.getFiles(folder);
					if(files == null){
						return fileName;
					}
				    fileName = new String[files.length];
					for(int var =0;var<files.length;var++){
						if(date != null){
							if(files[var].getName().equals(date)){
								fileName[0] = files[var].getName();
							}
							continue;
						}
						fileName[var] =  files[var].getName();
					}
				}
		return fileName;
	}
	@Override
	public List<GalleryInfo> findGalleryItemByFileName(String folder,
			String files) {
		String finalFile = null;
		File []fFiles = null;
		GalleryInfo galleryInfo = null;
		GalleryItemInfo galleryItemInfo = null;
		List<GalleryInfo> galleryInfos = new ArrayList<GalleryInfo>();
		List<GalleryItemInfo>galleryItemInfos = null;
		if (folder != null) {
			galleryInfo = new GalleryInfo();
			galleryInfo.setFilename(files);
			finalFile = folder + "/" +files;
			fFiles = IotUtil.getFiles(finalFile);
			if(fFiles == null){
				return galleryInfos;
			}
			galleryItemInfos = new ArrayList<GalleryItemInfo>();
			for(File obj:fFiles){
				galleryItemInfo = new GalleryItemInfo();
				galleryItemInfo.setCheck(false);
				galleryItemInfo.setSelectd(false);
				galleryItemInfo.setItemPath(obj.getPath());
				galleryItemInfo.setItemName(obj.getName());
				galleryItemInfo.setBitmap(IotUtil.pathImage(obj.getPath()));
				galleryItemInfos.add(galleryItemInfo);
			}
			if(galleryItemInfos.size()!=0){
				galleryInfo.setGalleryItemInfos(galleryItemInfos);
				galleryInfos.add(galleryInfo);
			}
		}
		return galleryInfos;
	}
	@Override
	public int delFileByWay(GalleryRoute galleryRoute) {
		File f = new File(galleryRoute.getFilePath().trim());
		File[] files = f.listFiles();
		if (files != null && files.length > 0) {
			for(File obj:files){
				if(obj.getName().equals(galleryRoute.getFileName())){
					 obj.delete();
				}
			}
		}
		return 0;
	}
}
