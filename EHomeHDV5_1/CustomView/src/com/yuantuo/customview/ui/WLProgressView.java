package com.yuantuo.customview.ui;

import java.util.Vector;

import com.yuantuo.customview.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class WLProgressView extends View {

	public WLProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public WLProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public WLProgressView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private class Circle {

		private int x;
		private int y;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}

	private Vector<Circle> circles = new Vector<Circle>(12);
	private boolean hasInit = false;
	private Paint paint;

	private int centerX;
	private int centerY;
	private int r;

	private int curPos = 0;

	private static final int NUM = 12;

	private void init() {

		centerX = getWidth() / 2;
		centerY = getHeight() / 2;
		r = getWidth() / 4;
		paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(getResources().getColor(R.color.v5_green_light));
		paint.setStyle(Style.FILL);

		for (int i = 0; i < NUM; i++) {
			Circle circle = new Circle();
			circle.setX((int) (centerX + r
					* Math.cos(360 / NUM * i * Math.PI / 180)));
			circle.setY((int) (centerY + r
					* Math.sin(360 / NUM * i * Math.PI / 180)));
			circles.add(circle);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		if (!hasInit) {
			init();
			hasInit = true;
		}

		for (int i = 0; i < NUM; i++) {
			canvas.drawCircle(circles.get(i).getX(), circles.get(i).getY(),
					(curPos + i) % NUM * 2 + 3, paint);
		}

		if (--curPos < 0) {
			curPos += NUM;
		}
	}

	public void play() {

		if (isPlay()) {
			stop();
		}
		mHandler.sendEmptyMessageDelayed(PLAY, 100);
		curStatus = PLAY;
	}

	public void stop() {
		mHandler.sendEmptyMessage(STOP);
		curStatus = STOP;
	}

	public boolean isPlay() {

		return this.curStatus == PLAY;
	}

	public static final int PLAY = 0;
	public static final int STOP = 1;
	private int curStatus = STOP;

	private Handler mHandler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case PLAY:
				mHandler.sendEmptyMessageDelayed(PLAY, 100);
				invalidate();
				break;

			case STOP:
				mHandler.removeMessages(PLAY);
				break;
			}
		}
	};
}
