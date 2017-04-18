package com.wulian.iot.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/4/28 0028.
 */
public class GalleryInfo extends BaseCameraInfo {
    private String filename;
    private List<GalleryItemInfo> galleryItemInfos;
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getFilename() {
        return filename;
    }
    public void setGalleryItemInfos(List<GalleryItemInfo> galleryItemInfos) {
        this.galleryItemInfos = galleryItemInfos;
    }
    public List<GalleryItemInfo> getGalleryItemInfos() {
        return galleryItemInfos;
    }
}
