/**
 * Project Name:  iCam
 * File Name:     SnapCache.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2015年4月9日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * @ClassName: SnapCache
 * @Function: 截屏缓存
 * @Date: 2015年4月9日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class SnapCache extends LruCache<String, Bitmap> {

	public SnapCache(int maxSize) {
		super(maxSize);
	}

    protected int sizeOf(Long key,Bitmap value){
        return value.getByteCount();
    }
}
