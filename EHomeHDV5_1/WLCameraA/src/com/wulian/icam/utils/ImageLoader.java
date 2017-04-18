/**
 * Project Name:  Z_BitmapfunTest
 * File Name:     ImageDownLoader.java
 * Package Name:  com.test.bitmap
 * @Date:         2015年3月30日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.wulian.icam.model.AlbumEntity;

/**
 * @Function: 精简版图片加载器
 * @date: 2015年6月23日
 * @author Wangjj
 */

public class ImageLoader {
	private LruCache<String, Bitmap> mMemoryCache;
	private ExecutorService mImageThreadPool = null;

	public ImageLoader(Context context) {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int mCacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	public ExecutorService getThreadPool() {
		if (mImageThreadPool == null) {
			synchronized (ExecutorService.class) {
				mImageThreadPool = Executors.newFixedThreadPool(2);
			}
		}
		return mImageThreadPool;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public void deleteBitmapFromMemCache(String key) {
		if (getBitmapFromMemCache(key) != null) {
			mMemoryCache.remove(key);
		}
	}

	/**
	 * @MethodName: deletAllBitmapFromMemCache
	 * @Function: 清除所有缓存
	 * @author: yuanjs
	 * @date: 2015年3月31日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param albumList
	 */
	public void deletAllBitmapFromMemCache() {
		mMemoryCache.evictAll();
	}

	public Bitmap loadImage(final String url,
			final OnImageLoaderListener listener, int width, int height) {
		Bitmap bitmap = showCacheBitmap(url, listener, width, height);
		if (bitmap != null) {
			return bitmap;
		} else
			return null;
	}

	public Bitmap showCacheBitmap(final String subUrl,
			final OnImageLoaderListener listener, final int width,
			final int height) {
		if (getBitmapFromMemCache(subUrl) != null) {
			return getBitmapFromMemCache(subUrl);
		} else {
			getThreadPool().execute(new Runnable() {
				Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						listener.onImageLoader((Bitmap) msg.obj, subUrl);
					}
				};

				@Override
				public void run() {
					Bitmap bitmap = resizeImage(subUrl, width, height);
					addBitmapToMemoryCache(subUrl, bitmap);
					Message mes = handler.obtainMessage();
					mes.obj = bitmap;
					handler.sendMessage(mes);
				}
			});
		}
		return null;
	}

	/**
	 * @MethodName: resizeImage
	 * @Function: 防止图片过大，超出内存
	 * @author: yuanjs
	 * @date: 2015年4月1日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap resizeImage(String path, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 不加载bitmap到内存中
		BitmapFactory.decodeFile(path, options);
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;
		if (width > reqWidth || height > reqHeight) {
			int widthRadio = Math.round(width * 1.0f / reqWidth);
			int heightRadio = Math.round(height * 1.0f / reqHeight);

			inSampleSize = Math.min(widthRadio, heightRadio);
		}
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	public synchronized void cancelTask() {
		if (mImageThreadPool != null) {
			mImageThreadPool.shutdownNow();//再接受任务会拒绝
			mImageThreadPool = null;//重置，便于下次新建
		}
	}

	public interface OnImageLoaderListener {
		/**
		 * @Function TODO
		 * @author Wangjj
		 * @date 2015年6月10日
		 * @param bitmap
		 *            解析好的图片
		 * @param url
		 *            标识请求路径，多个请求返回时，可以通过url区分
		 */

		void onImageLoader(Bitmap bitmap, String url);
	}
}
