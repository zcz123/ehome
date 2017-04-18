/**
 * Project Name:  iCam
 * File Name:     AngleMeter.java
 * Package Name:  com.wulian.icam.widget
 * @Date:         2015年7月6日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.wulian.icam.R;

/**
 * @ClassName: AngleMeter
 * @Function: 角度仪表
 * @Date: 2015年7月6日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class AngleMeter extends View {
	private Paint paint;
	private static int width;// 整个ui的宽度,布局设置时最好比例是5:1,比如200dp:40dp
	private static int radius;
	private static final int paddingTop = 20;
	private static final int paddingLeftOrRight = 60;
	private static final int lineDeltaXY = 10;
	private float leftCirclePointX, leftCirclePointY, rightCirclePointX,
			rightCirclePointY, circleX, circleY, pointerX = -1, pointerY = -1;
	private RectF archRectF;
	private Bitmap indicateBitmap;
	private int bitmapWidth, bitmapHeight;

	public AngleMeter(Context context) {
		this(context, null);
	}

	public AngleMeter(Context context, AttributeSet attrs) {

		this(context, attrs, 0);
	}

	public AngleMeter(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1.5f);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		indicateBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.icon_pointer);
		bitmapWidth = indicateBitmap.getWidth();
		bitmapHeight = indicateBitmap.getHeight();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		radius = width - 2 * paddingLeftOrRight;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 左参考点
		leftCirclePointX = paddingLeftOrRight;
		leftCirclePointY = (float) (paddingTop + (2 - Math.sqrt(3)) * radius
				/ 2);// radius/2为1个单位长度

		// 默认绘图点
		if (pointerX == -1) {
			pointerX = leftCirclePointX;
		}
		if (pointerY == -1) {
			pointerY = leftCirclePointY;
		}
		// 右参考点
		rightCirclePointX = width - paddingLeftOrRight;
		rightCirclePointY = leftCirclePointY;
		// 圆心
		circleX = width / 2;
		circleY = radius + paddingTop;
		// 画圆边
		// canvas.drawCircle(circleX, circleY, radius, paint);
		if (archRectF == null) {
			archRectF = new RectF(circleX - radius, circleY - radius, circleX
					+ radius, circleY + radius);
		}
		// 画弧形
		canvas.drawArc(archRectF, 240, 60, false, paint);

		// 左边的线
		canvas.drawLine(leftCirclePointX - lineDeltaXY, leftCirclePointY
				- lineDeltaXY, leftCirclePointX + lineDeltaXY, leftCirclePointY
				+ lineDeltaXY, paint);
		// 右边的线

		canvas.drawLine(rightCirclePointX + lineDeltaXY, rightCirclePointY
				- lineDeltaXY, rightCirclePointX - lineDeltaXY,
				rightCirclePointY + lineDeltaXY, paint);
		// 中间的线

		canvas.drawLine(width / 2, paddingTop, width / 2, rightCirclePointY
				- lineDeltaXY * 2, paint);

		// 左边的文本
		canvas.drawText("0°", leftCirclePointX - paddingLeftOrRight / 2,
				leftCirclePointY + paddingTop / 2, paint);
		// 右边的文本
		canvas.drawText("111°", rightCirclePointX + paddingLeftOrRight / 4,
				rightCirclePointY + paddingTop / 2, paint);

		// 画指示点
		canvas.drawBitmap(indicateBitmap, pointerX - bitmapWidth / 2, pointerY
				- bitmapHeight / 2, paint);

		// 辅助线
		// canvas.drawRect(new RectF(paddingLeftOrRight, paddingTop,
		// paddingLeftOrRight + radius, leftCirclePointY), paint);
		// canvas.drawLine(width / 2, radius + paddingTop, width / 2 + radius,
		// radius + paddingTop, paint);
		// canvas.drawLine(width / 2, radius + paddingTop, width / 2 + radius /
		// 2,
		// leftCirclePointY, paint);
		// canvas.drawLine(width / 2, radius + paddingTop, width / 2,
		// leftCirclePointY, paint);

	}

	/**
	 * @Function 旋转函数
	 * @author Wangjj
	 * @date 2015年7月8日
	 * @param p
	 *            旋转的百分比
	 */

	public void refreshAngle(double p) {

		if (p < 0) {
			p = 0;
		}
		if (p > 1) {
			p = 1;
		}
		double theta = Math.toRadians((p - 0.5) * 60);
		double delatX = Math.sin(theta) * radius;
		double delatY = Math.cos(theta) * radius;
		pointerX = (float) (circleX + delatX);
		pointerY = (float) (circleY - delatY);
		invalidate();
	}
}
