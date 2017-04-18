/**
 * Project Name:  WulianICamH264
 * File Name:     H264CustomeView.java
 * Package Name:  com.wulian.h264decoder.widget
 * @Date:         2015年5月28日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.widget;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import org.webrtc.videoengine.FileUtils;

import com.wulian.siplibrary.utils.WulianLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * @ClassName: H264CustomeView
 * @Function: H264显示自定义布局
 * @Date: 2015年5月28日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class H264CustomeView extends View {
	private static final int START_STATE = 1;
	private static final int STOP_STATE = 2;

	private static final int DEFAULT_WIDTH = 640;// 默认宽度dip
	private static final int DEFAULT_HEIGHT = 480;// 默认高度dip

	byte[] mPixel;// 获取到的字节
	int mWidth = -1; // 此处设定不同的分辨率
	int mHeight = -1;
	int mArrayCopyLength = 0;
	ByteBuffer mByteBuffer;// 字节Buffer
	Bitmap mVideoBitmap;//
	private int mTotalWidth;// View的宽度
	private int mTotalHeight;// View的高度
	private int mState = STOP_STATE;// 当前状态
	private Rect mSrcRect;
	private Rect mDetRect;
	private boolean isTakePicNow;

	public H264CustomeView(Context context) {
		super(context);
		setFocusable(true);
		isTakePicNow = false;
	}

	public H264CustomeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		isTakePicNow = false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width = 0, height = 0;
		int minWidth = getPaddingLeft() + getPaddingRight()
				+ getSuggestedMinimumWidth();
		int minHeight = getPaddingBottom() + getPaddingTop()
				+ getSuggestedMinimumHeight();
		switch (widthMode) {
		case MeasureSpec.AT_MOST:// wrap_content
			width = Math.max(widthSize, minWidth);
			break;
		case MeasureSpec.EXACTLY:// 确定值
			width = Math.max(widthSize, minWidth);
			break;
		case MeasureSpec.UNSPECIFIED:// 任意大
			width = Math.max(widthSize, DEFAULT_WIDTH);
			break;
		}
		switch (heightMode) {
		case MeasureSpec.AT_MOST:
			height = Math.max(heightSize, minHeight);
			break;
		case MeasureSpec.EXACTLY:
			height = Math.max(heightSize, minHeight);
			break;
		case MeasureSpec.UNSPECIFIED:
			height = Math.max(heightSize, DEFAULT_HEIGHT);
			break;
		}
		mTotalWidth = width;
		mTotalHeight = height;
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != oldw || h != oldh) {
			mTotalWidth = w;
			mTotalHeight = h;
		}
	}

	private void initData(int width, int height) {
		if (width <= 0 || height <= 0) {
			// 不应该出现这种情况
			return;
		}
		mState = START_STATE;
		this.mWidth = width;
		this.mHeight = height;
		this.mArrayCopyLength = width * height * 2;
		mPixel = new byte[mArrayCopyLength];
		int i = mPixel.length;
		for (i = 0; i < mPixel.length; i++) {
			mPixel[i] = (byte) 0x00;
		}
		mByteBuffer = ByteBuffer.wrap(mPixel);

		mSrcRect = new Rect();
		mSrcRect.left = 0;
		mSrcRect.top = 0;
		mDetRect = new Rect();
		mDetRect.left = 0;
		mDetRect.top = 0;
		mVideoBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
	}

	private void destroyData() {
		this.mWidth = -1;
		this.mHeight = -1;
		mPixel = null;
		mByteBuffer = null;
		if (mVideoBitmap != null && !mVideoBitmap.isRecycled()) {
			mVideoBitmap.recycle();
			mVideoBitmap = null;
		}
	}

	public interface TakePictureCallBack {
		void TakePicture(boolean isFileOk, Bitmap bmp);
	}

	public void setTakePicture(final String filePath, final TakePictureCallBack callback) {
//		Bitmap savePic = null;
//		if (mVideoBitmap != null) {
//			savePic = Bitmap.createBitmap(mVideoBitmap);
//			if (savePic != null) {
//				try {
//					FileOutputStream fops = new FileOutputStream(filePath);
//					// fops.write(data);
//					mVideoBitmap.compress(Bitmap.CompressFormat.PNG, 90, fops);
//					fops.flush();
//					fops.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//					savePic = null;
//				}
//			}
//			if (callback != null) {
//				callback.TakePicture(savePic == null ? false : true, savePic);
//			}
//		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mVideoBitmap != null) {
					try {
						FileOutputStream fops = new FileOutputStream(filePath);
						// fops.write(data);
						mVideoBitmap.compress(Bitmap.CompressFormat.PNG, 90, fops);
						fops.flush();
						fops.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (callback != null) {
						callback.TakePicture(true,mVideoBitmap);
					}
				}
				else {
					if (callback != null) {
						callback.TakePicture(false,mVideoBitmap);
					}
				}
			}
		}).start();
		
	}

	public Bitmap getBitmap() {
		if (mState == START_STATE) {
			return mVideoBitmap;
		} else {
			return null;
		}
	}

	public void PlayVideo(byte[] SockBuf, int width, int height) {
		// mByteBuffer = ByteBuffer.wrap(SockBuf);
		if (mState == STOP_STATE) {
			initData(width, height);
		} else if (mState == START_STATE
				&& (width != mWidth || height != mHeight)) {
			destroyData();
			initData(width, height);
		}
		System.arraycopy(SockBuf, 0, mPixel, 0, mArrayCopyLength);
		postInvalidate();
	}

	public void stopVideo() {
		mState = STOP_STATE;
	}

	public boolean isStopVideo() {
		return mState == STOP_STATE;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mByteBuffer != null && mVideoBitmap != null) {
			// mByteBuffer.position(0);
			mByteBuffer.rewind();
			mVideoBitmap.copyPixelsFromBuffer(mByteBuffer);
		}
		if (mVideoBitmap != null) {
			mSrcRect.right = mWidth;
			mSrcRect.bottom = mHeight;
			mDetRect.right = mTotalWidth;
			mDetRect.bottom = mTotalHeight;
			canvas.drawBitmap(mVideoBitmap, mSrcRect, mDetRect, null);
			// canvas.drawBitmap(mVideoBitmap, 0, 0, null);
		}
	}
}
