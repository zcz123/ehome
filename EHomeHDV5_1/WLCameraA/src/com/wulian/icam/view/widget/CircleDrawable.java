package com.wulian.icam.view.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * @Function: 圆形图片： 存在尺寸限制的局限性
 * @date: 2015年7月11日
 * @author Wangjj
 */

public class CircleDrawable extends Drawable {
	private static final boolean USE_VIGNETTE = true;

	private final float mCornerRadius;
	private final RectF mRect = new RectF();
	private final BitmapShader mBitmapShader;
	private final Paint mPaint;
	private final int mMargin;

	public CircleDrawable(Bitmap bitmap) {
		this(bitmap, 0);
	}

	public CircleDrawable(Bitmap bitmap, int margin) {
		mCornerRadius = bitmap.getWidth() / 2;//如果ImageView尺寸大于bitmap的，图像会变形
		
		mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setShader(mBitmapShader);
		mMargin = margin;
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mRect.set(mMargin, mMargin, bounds.width() - mMargin, bounds.height()
				- mMargin);

		if (USE_VIGNETTE) {
			RadialGradient vignette = new RadialGradient(mRect.centerX(),
					mRect.centerY() * 1.0f / 0.7f, mRect.centerX() * 1.3f,
					new int[] { 0, 0, 0x7f000000 }, new float[] { 0.0f, 0.7f,
							1.0f }, Shader.TileMode.CLAMP);

			Matrix oval = new Matrix();
			oval.setScale(1.0f, 0.7f);
			vignette.setLocalMatrix(oval);

			mPaint.setShader(new ComposeShader(mBitmapShader, vignette,
					PorterDuff.Mode.SRC_OVER));
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
	}
}
