package com.wulian.iot.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/4/28 0028.
 */
public class GalleryListInfo extends BaseCameraInfo{

    private List<GalleryListItemInfo> albums;
    private String imgDate;


    public List<GalleryListItemInfo> getAlbums() {
        return albums;
    }

    public void setAlbums(List<GalleryListItemInfo> albums) {
        this.albums = albums;
    }

    public String getImgDate() {
        return imgDate;
    }

    public void setImgDate(String imgDate) {
        this.imgDate = imgDate;
    }

}
