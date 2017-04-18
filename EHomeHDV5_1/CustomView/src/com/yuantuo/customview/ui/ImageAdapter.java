package com.yuantuo.customview.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter
{
	private Context mContext;
	private int[] mImageIds;
	private ImageView[] mImages;
	private TextPaint mPaint;
	private String[] mTexts;
	private CustomGallery.LayoutParams layoutParams;
	private float mTextSize;

	public ImageAdapter( Context c, int[] ImageIds, int[] texts )
	{
		mTexts = new String[texts.length];
		for (int i = 0; i < texts.length; i++){
			mTexts[i] = c.getString(texts[i]);
		}
		mContext = c;
		mImageIds = ImageIds;
		mImages = new ImageView[mImageIds.length];
		checkScreenAndTextSize();
		initPaint();
	}

	public ImageAdapter( Context c, int[] ImageIds, String[] texts )
	{
		mContext = c;
		mImageIds = ImageIds;
		mTexts = texts;
		mImages = new ImageView[mImageIds.length];
		checkScreenAndTextSize();
		initPaint();
	}

	private void initPaint(){
		mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		mPaint.density = ScreenSize.density;
		mPaint.setTextSize(mTextSize);
	}

	/**
	 * loop to create ImageView
	 */
	public void createImageViews(){
		for (int i = 0; i < mImageIds.length; i++){
			Bitmap bgBitmap = BitmapFactory.decodeResource(mContext.getResources(), mImageIds[i]);

			FontMetrics fm = mPaint.getFontMetrics();

			Bitmap canvasBitmap = Bitmap.createBitmap(bgBitmap.getWidth(), bgBitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(canvasBitmap);

			canvas.drawBitmap(bgBitmap, 0, 0, null);
			// fm.bottom * 3 only debug
			canvas.drawText(mTexts[i], bgBitmap.getWidth() / 2 - mPaint.measureText(mTexts[i]) / 2, bgBitmap.getHeight() - (fm.bottom * 3), mPaint);

			ImageView imageView = new ImageView(mContext);
			imageView.setImageBitmap(canvasBitmap);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			imageView.setLayoutParams(layoutParams);

			mImages[i] = imageView;
			bgBitmap.recycle();
			bgBitmap = null;
		}
	}

	private void checkScreenAndTextSize(){
		int w = ScreenSize.screenWidth;
		int h = ScreenSize.screenHeight;

		if (240 == w && 320 == h){
			layoutParams = new CustomGallery.LayoutParams(100, 80);
			mTextSize = 12.0f;
		}
		else if (320 == w && 480 == h){
			layoutParams = new CustomGallery.LayoutParams(110, 120);
			mTextSize = 14.0f;
		}
		// TODO
		else if ((320 == w && 533 == h)){
			layoutParams = new CustomGallery.LayoutParams(120, 120);
			mTextSize = 10.0f;
		}
		else if (480 == w && (800 == h || 854 == h)){
			if (1.5F == ScreenSize.density){
				layoutParams = new CustomGallery.LayoutParams(180, 200);
				mTextSize = 22.0f;
			}
			else{
				layoutParams = new CustomGallery.LayoutParams(180, 200);
				mTextSize = 18.0f;
			}
		}
		else if ((480 == w && 764 == h)){
			layoutParams = new CustomGallery.LayoutParams(180, 200);
			mTextSize = 14.0f;
		}
		// TODO
		else if ((600 == w && 800 == h)){
			layoutParams = new CustomGallery.LayoutParams(180, 140);
			mTextSize = 22.0f;
		}
		// TODO
		else if (480 == w && 960 == h){
			layoutParams = new CustomGallery.LayoutParams(180, 200);
			mTextSize = 22.0f;
		}
		// TODO
		else if (540 == w && 960 == h){
			layoutParams = new CustomGallery.LayoutParams(180, 200);
			mTextSize = 22.0f;
		}
		// TODO
		else if (640 == w && 960 == h){
			layoutParams = new CustomGallery.LayoutParams(280, 240);
			mTextSize = 22.0f;
		}
		// galaxy tablet 7' P3110
		else if (600 == w && (976 == h || 1024 == h)){
			layoutParams = new CustomGallery.LayoutParams(240, 250);
			mTextSize = 16.0f;
		}
		// TODO
		else if (768 == w && 1024 == h){
			layoutParams = new CustomGallery.LayoutParams(280, 240);
			mTextSize = 22.0f;
		}
		// 9250, one x
		else if (720 == w && 1184 == h){
			layoutParams = new CustomGallery.LayoutParams(280, 260);
			mTextSize = 28.0f;
		}
		else if (720 == w && 1280 == h){
			layoutParams = new CustomGallery.LayoutParams(280, 260);
			mTextSize = 28.0f;
		}
		// Galaxy Note 5.3, our moto pad (density 1.0)
		else if (800 == w && 1280 == h){
			if (1.0F == ScreenSize.density){
				layoutParams = new CustomGallery.LayoutParams(280, 270);
				mTextSize = 18.0f;
			}
			else{
				layoutParams = new CustomGallery.LayoutParams(290, 300);
				mTextSize = 26.0f;
			}
		}
		// for our moto pad(landscape)
		else if (1280 == w && 800 == h){
			layoutParams = new CustomGallery.LayoutParams(280, 220);
			mTextSize = 16.0f;
		}
		else{
			layoutParams = new CustomGallery.LayoutParams(180, 190);
			mTextSize = 20.0f;
		}
	}

	public int getCount(){
		if (mImageIds == null || mTexts == null){
			return 0;
		}
		else{
			return mImageIds.length;
		}
	}

	public void changeData( int[] ImageIds, String[] Texts ){
		mTexts = Texts;
		mImageIds = ImageIds;
		notifyDataSetChanged();
	}

	public Object getItem( int position ){
		return position;
	}

	public long getItemId( int position ){
		return position;
	}

	public View getView( int position, View convertView, ViewGroup parent ){
		return mImages[position];
	}
}