/**
 * Project Name:  iCam
 * File Name:     PagerViewAdapter.java
 * Package Name:  com.wulian.icam.adpter
 * @Date:         2015年4月2日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.album;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.photoview.PhotoView;
import com.photoview.PhotoViewAttacher;
import com.wulian.icam.R;
import com.wulian.icam.utils.ImageLoader;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.view.widget.CustomViewPager;
import com.wulian.siplibrary.utils.WulianLog;

/**
 * @ClassName: PagerViewAdapter
 * @Function: 自定义PagerAdapter适配器
 * @date: 2015年4月2日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class PagerViewAdapter extends PagerAdapter implements
		OnPageChangeListener {
	private List<String> imageUrls = new ArrayList<String>();
	private Context mContext;
	private ImageLoader mLoader;
	private DisplayMetrics metrics = null;
	private LayoutInflater inflater;
	private CustomViewPager viewPager;
	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);

	public PagerViewAdapter(Context mContext, ImageLoader mLoader,
			CustomViewPager viewPager) {
		this.mContext = mContext;
		this.inflater = LayoutInflater.from(mContext);
		this.mLoader = mLoader;
		metrics = Utils.getDeviceSize(mContext);
		this.viewPager = viewPager;
		this.viewPager.setOnPageChangeListener(this);
	}

	public void setSourceData(List<String> imageThumbUrls) {
		if (imageUrls != null) {
			imageUrls.clear();
		}
		imageUrls.addAll(imageThumbUrls);
	}

	@Override
	public int getCount() {
		return imageUrls.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (View) arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
		Utils.sysoInfo("destroyItem:" + position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
	}

	boolean isFirst = true;// 第一次加载该图片
	RectF initRectF = new RectF();// 图片原始RectF
	boolean flag = false;// true表示pagerView不能切换图片
	ViewGroup container;// 图片容器

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Utils.sysoInfo("instantiateItem:" + position);
		FrameLayout imageLayout = (FrameLayout) inflater.inflate(
				R.layout.item_pager_image, container, false);
		final PhotoView imageView = (PhotoView) imageLayout
				.findViewById(R.id.image);
		String[] strMetrics = Utils.getImageViewMetrics(imageView).split("#");
		int width = 0;
		int height = 0;
		if (strMetrics != null) {
			width = Integer.parseInt(strMetrics[0]);
			height = Integer.parseInt(strMetrics[1]);
		}
		imageView.setMaximumScale(8.0f);// 设置最大放大尺寸
		// imageView.setOnMatrixChangeListener(new MyMatrixChangedListener());
		final ProgressBar spinner = (ProgressBar) imageLayout
				.findViewById(R.id.loading);
		if (imageUrls.size() == 0) {
			imageView.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_default));
		} else {
			String dir = imageUrls.get(position);
			imageView.setTag(dir);
			Bitmap mBitmap = mLoader.loadImage(dir,
					new ImageLoader.OnImageLoaderListener() {
						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							if (imageView.getTag().equals(url)) {// 都是final对象，肯定相等，但是不一定是当前可见界面
								spinner.setVisibility(View.GONE);
								imageView.setImageBitmap(bitmap);
							}
						}
					}, width != 0 ? width : metrics.widthPixels,
					height != 0 ? height
							: (int) (metrics.widthPixels * 3.0 / 4.0));
			if (mBitmap != null) {
				spinner.setVisibility(View.GONE);
				imageView.setImageBitmap(mBitmap);
				imageView.getDisplayMatrix();
			} else {
				spinner.setVisibility(View.VISIBLE);
			}
			container.addView(imageLayout);
		}
		this.container = container;
		return imageLayout;
	}

	/**
	 * @MethodName: setPhotoViewMatrix
	 * @Function: 遍历容器，将里面所有图片尺寸还原
	 * @author: yuanjs
	 * @date: 2015年5月27日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param container
	 *            pagerView容器
	 */
	private void setPhotoViewMatrix(ViewGroup container) {
		int childCount = container.getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				View view = container.getChildAt(i);
				if (view instanceof FrameLayout) {
					int y = ((FrameLayout) view).getChildCount();
					if (y > 0) {
						for (int j = 0; j < y; j++) {
							View childView = ((FrameLayout) view).getChildAt(j);
							if (childView instanceof PhotoView) {
								((PhotoView) childView).setScale(1.0f);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @ClassName: MyMatrixChangedListener
	 * @Function: 用于监听图片是否经过缩放,缩放的话就不能切换图片
	 * @date: 2015年5月27日
	 * @author: yuanjs
	 * @email: yuanjsh@wuliangroup.cn
	 */
	private class MyMatrixChangedListener implements
			PhotoViewAttacher.OnMatrixChangedListener {
		@Override
		public void onMatrixChanged(RectF arg0) {
			if (isFirst) {
				initRectF.set(arg0);
				isFirst = false;
			}
			if (initRectF != null && initRectF.equals(arg0)) {
				flag = false;
			} else {
				flag = true;
			}
			viewPager.setNoScroll(flag);
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// WulianLog.i("setOnPageChangeListener", "onPageScrollStateChanged");
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// WulianLog.i("setOnPageChangeListener", "onPageScrolled");
	}

	@Override
	public void onPageSelected(int position) {
		if (container != null) {// viewPager.setCurrentItem时，container可能还没好呢。
			setPhotoViewMatrix(container);
		}
		((AlbumPicActivity) mContext).updatePositionAndTitle(position);
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		super.finishUpdate(container);
	}
}
