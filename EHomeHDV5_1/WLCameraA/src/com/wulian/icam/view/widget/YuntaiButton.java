/**
 * Project Name:  iCam
 * File Name:     YuntaiButton.java
 * Package Name:  com.wulian.icam.widget
 * @Date:         2015年9月23日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wulian.icam.R;
import com.wulian.icam.utils.Utils;

/**
 * @ClassName: YuntaiButton
 * @Function: 自定义云台控件
 * @Date: 2015年9月23日
 * @author: yuanjs
 * @email: jiansheng.yuan@wuliangroup.com.cn
 */
public class YuntaiButton extends View {
	private Paint paint;// 画笔
	private Bitmap backBitmap; // 正常状态底部图片
	private Bitmap backBitmapLeft;
	private Bitmap backBitmapUp;
	private Bitmap backBitmapRight;
	private Bitmap backBitmapDown;
	private Bitmap ballBitmap; // 白球图片
	private int backRadius;// 背景图片半径
	private int radius;// 浮动白圈半径
	private int circleX;// 白圈圆心x坐标
	private int circleY;// 白圈圆心y坐标
	private Direction direction = Direction.none;
	private OnDirectionLisenter directionLisenter;
	private static final String TAG = "YuntaiButton";

	public enum Direction {
		up, down, left, right, none;
	}

	private Direction lastDirection = Direction.none;

	public YuntaiButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public YuntaiButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public YuntaiButton(Context context) {
		this(context, null);
	}

	private void init() {
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1.5f);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		backBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.video_control_panel_portrait);
		backBitmapLeft = BitmapFactory.decodeResource(getResources(),
				R.drawable.video_control_panel_left);
		backBitmapUp = BitmapFactory.decodeResource(getResources(),
				R.drawable.video_control_panel_up);
		backBitmapRight = BitmapFactory.decodeResource(getResources(),
				R.drawable.video_control_panel_right);
		backBitmapDown = BitmapFactory.decodeResource(getResources(),
				R.drawable.video_control_panel_down);
		ballBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.video_control_button);
		backRadius = backBitmap.getWidth() / 2;
		radius = ballBitmap.getWidth() / 2;
		circleX = backRadius;
		circleY = backRadius;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width = 0, height = 0;
		// 大小为图片背景大小，不可设置
		switch (widthMode) {
		case MeasureSpec.AT_MOST:// wrap_content
			width = Math.min(widthSize, backRadius * 2);
			break;
		case MeasureSpec.EXACTLY:// 确定值
			width = backRadius * 2;
			break;
		case MeasureSpec.UNSPECIFIED:// 任意大
			width = backRadius * 2;
			break;
		}
		switch (heightMode) {
		case MeasureSpec.AT_MOST:
			height = Math.min(heightSize, backRadius * 2);
			break;
		case MeasureSpec.EXACTLY:
			height = backRadius * 2;
			break;
		case MeasureSpec.UNSPECIFIED:
			height = backRadius * 2;
			break;
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(backBitmap, 0, 0, paint);
		switch (direction) {
		case left:
			canvas.drawBitmap(backBitmapLeft, 0, 0, paint);
			break;
		case up:
			canvas.drawBitmap(backBitmapUp, 0, 0, paint);
			break;
		case right:
			canvas.drawBitmap(backBitmapRight, 0, 0, paint);
			break;
		case down:
			canvas.drawBitmap(backBitmapDown, 0, 0, paint);
			break;
		case none:
			break;
		}
		canvas.drawBitmap(ballBitmap, circleX - radius, circleY - radius, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int xDown = (int) event.getX();
			int yDown = (int) event.getY();
			if (isValidArea(xDown, yDown)) {
				Utils.sysoInfo(TAG + ":DOWN");
				circleX = xDown;
				circleY = yDown;
				direction = getDirection(circleX, circleY);
				invalidate();
				if (lastDirection != direction) {
					lastDirection = direction;
					directionLisenter.directionLisenter(direction);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int xMove = (int) event.getX();
			int yMove = (int) event.getY();
			Utils.sysoInfo(TAG + ":MOVE");
			if (isValidArea(xMove, yMove)) {
				circleX = xMove;
				circleY = yMove;
			} else {
				touchOutViewHandle(xMove, yMove);
			}
			direction = getDirection(circleX, circleY);
			invalidate();
			if (lastDirection != direction) {
				lastDirection = direction;
				directionLisenter.directionLisenter(direction);
			}
			break;
		case MotionEvent.ACTION_UP:
			Utils.sysoInfo(TAG + ":UP");
			circleX = backRadius;
			circleY = backRadius;
			direction = Direction.none;
			invalidate();
			if (lastDirection != direction) {
				lastDirection = direction;
				directionLisenter.directionLisenter(direction);
			}
			break;
		}
		return true;
	}

	// XXX 滑动到view外面的处理
	private void touchOutViewHandle(int x, int y) {
		if (x != backRadius && y != backRadius) {
			// 与（backRadius）之间的正弦值
			double angle = Math.abs((y - backRadius) / (x - backRadius));
			double width = (backRadius - radius)
					/ Math.sqrt((1 + Math.pow(angle, 2)));
			if (angle < 1) {
				// L,R
				if (x < backRadius) {
					if (y < backRadius) {
						circleX = (int) (backRadius - width);
						circleY = (int) (backRadius - width * angle);
					} else {
						circleX = (int) (backRadius - width);
						circleY = (int) (backRadius + width * angle);
					}
				} else {
					if (y < backRadius) {
						circleX = (int) (backRadius + width);
						circleY = (int) (backRadius - width * angle);
					} else {
						circleX = (int) (backRadius + width);
						circleY = (int) (backRadius + width * angle);
					}
				}
			} else {
				// U,D
				if (y < backRadius) {
					if (x < backRadius) {
						circleX = (int) (backRadius - width);
						circleY = (int) (backRadius - width * angle);
					} else {
						circleX = (int) (backRadius + width);
						circleY = (int) (backRadius - width * angle);
					}
				} else {
					if (x < backRadius) {
						circleX = (int) (backRadius - width);
						circleY = (int) (backRadius + width * angle);
					} else {
						circleX = (int) (backRadius + width);
						circleY = (int) (backRadius + width * angle);
					}
				}
			}
		} else if (x == backRadius && y != backRadius) {
			if (y < backRadius) {
				circleX = backRadius;
				circleY = radius;
			} else {
				circleX = backRadius;
				circleY = 2 * backRadius - radius;
			}
		} else if (x != backRadius && y == backRadius) {
			if (x < backRadius) {
				circleX = radius;
				circleY = backRadius;
			} else {
				circleX = 2 * backRadius - radius;
				circleY = backRadius;
			}
		} else {
			circleX = backRadius;
			circleY = backRadius;
		}
	}

	// 有效功能范围[radius,++]
	private Direction getDirection(int ballCircleX, int ballCircleY) {
		// 距离圆心（backRadius）之间的距离
		double length = Math.sqrt(Math.pow((double) (ballCircleX - backRadius),
				2) + Math.pow((double) (ballCircleY - backRadius), 2));
		if (length > (double) (radius)) {
			if (ballCircleX != backRadius && ballCircleY != backRadius) {
				// 与（backRadius）之间的正弦值
				double angle = Math.abs((ballCircleY - backRadius)
						/ (ballCircleX - backRadius));
				if (angle < 1) {
					// L,R
					if (ballCircleX < backRadius) {
						return Direction.left;
					} else {
						return Direction.right;
					}
				} else {
					// U,D
					if (ballCircleY < backRadius) {
						return Direction.up;
					} else {
						return Direction.down;
					}
				}
			} else if (ballCircleX == backRadius && ballCircleY != backRadius) {
				if (ballCircleY < backRadius) {
					return Direction.up;
				} else {
					return Direction.down;
				}
			} else if (ballCircleX != backRadius && ballCircleY == backRadius) {
				if (ballCircleX < backRadius) {
					return Direction.left;
				} else {
					return Direction.right;
				}
			} else {
				return Direction.none;
			}
		} else {
			return Direction.none;
		}
	}

	// 有效活动范围[0,backRadius-radius]
	private boolean isValidArea(int ballCircleX, int ballCircleY) {
		// 距离圆心（backRadius）之间的距离
		double length = Math.sqrt(Math.pow((double) (ballCircleX - backRadius),
				2) + Math.pow((double) (ballCircleY - backRadius), 2));
		if (length >= (float) (backRadius - radius)) {
			return false;
		} else {
			return true;
		}
	}

	public interface OnDirectionLisenter {
		void directionLisenter(Direction direction);
	}

	public void setOnDirectionLisenter(OnDirectionLisenter l) {
		if (l != null) {
			this.directionLisenter = l;
		}
	}

	public void setBackground(Context context, int R) {
		backBitmap = BitmapFactory.decodeResource(context.getResources(), R);
		invalidate();
	}
}
