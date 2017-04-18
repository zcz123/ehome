package cc.wulian.app.model.device.impls.controlable.thermostat;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ArcProgressBar extends View {

	// 背景画笔
	private Paint bgPaint;
	// 内景画笔
	private Paint fgPaint;
	// 初始和跨越角度
	private float startAngle = 135.0f;
	private float sweepAngle = 270.0f;
	// 弧形条的宽度 即bgPaint的宽度
	private float bgThickness = 10;
	// 弧形条内部的宽度
	private float fgThickness = 8;
	// 画弧形的矩阵区域
	private RectF mRadialScoreRect;
	// 圆的直径
	private int mDiameter;
	// 矩形区域坐标
	float right;
	float bottom;
	float left;
	float top;
	// 弧形的半径
	protected float mRadius;
	// 弧形的圆心点
	public Point centerPoint = null;

	// 当前的进度所对应的角度
	public double degree;
	// 当前进度所对应的值
	public int currentValue;
	public int upValue;

	// 屏幕的宽高
	private int width;
	private int height;

	public int getUpValue() {
		return upValue;
	}

	public void setUpValue(int upValue) {
		this.upValue = upValue;
	}

	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

	/**
	 * 提供给外部访问值的接口
	 */
	private OnMoveViewValueChanged mMove;
	private OnUpViewValueChanged mUp;

	public interface OnMoveViewValueChanged {
		public void onMoveChanged(int value);
	}

	public interface OnDownViewValueChanged {
		public void onDownChanged(int value);
	}

	public interface OnUpViewValueChanged {
		public void onUpChanged(int value);
	}

	public void setOnMoveViewValueChanged(OnMoveViewValueChanged move) {
		mMove = move;
	}

	public void setOnUpViewValueChanged(OnUpViewValueChanged up) {
		mUp = up;
	}

	public ArcProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ArcProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ArcProgressBar(Context context) {
		super(context);
		init();
	}

	private void init() {
		// 背景Paint
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setStyle(Style.STROKE);
		bgPaint.setStrokeWidth(bgThickness);
		bgPaint.setColor(Color.parseColor("#ff9d9e9d"));
		// 设置背景为透明
		bgPaint.setAlpha(0);
		BlurMaskFilter blurMaskFilter = new BlurMaskFilter(1, Blur.INNER);
		bgPaint.setMaskFilter(blurMaskFilter);

		// 内景Paint
		fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		fgPaint.setStyle(Style.STROKE);
		fgPaint.setStrokeWidth(fgThickness);
		fgPaint.setColor(Color.parseColor("#ff00ff00"));
		BlurMaskFilter blurMaskFilter2 = new BlurMaskFilter(1, Blur.OUTER);
		fgPaint.setMaskFilter(blurMaskFilter2);

		mRadialScoreRect = new RectF(0, 0, mDiameter, mDiameter);
		centerPoint = new Point((int) (left + mRadius), (int) (top + mRadius));

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		/**
		 * Determine the diameter and the radius based on device orientation
		 * 为了与背景图片重合,按比例将圆弧半径缩小,其比例为7/8,刚好与背景图片中间的圆弧重合
		 */
		if (w > h) {
			mDiameter = h;
			mRadius = ((mDiameter / 2 - (getPaddingTop() + getPaddingBottom())) * 7) / 8;
		} else {
			mDiameter = w;
			mRadius = ((mDiameter / 2 - (getPaddingLeft() + getPaddingRight())) * 7) / 8;
		}
		// Init the draw arc Rect object
		left = (getWidth() / 2) - mRadius + getPaddingLeft();
		right = (getWidth() / 2) + mRadius - getPaddingRight();
		top = (getHeight() / 2) - mRadius + getPaddingTop();
		bottom = (getHeight() / 2) + mRadius - getPaddingBottom();
		mRadialScoreRect = new RectF(left, top, right, bottom);

		centerPoint = new Point((int) (left + mRadius), (int) (top + mRadius));

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/*
		 * startAngle 开始角度 sweepAngle 跨越角度 false 不画中心和弧线的连线 drawArc 绘制弧线
		 */
		canvas.drawArc(mRadialScoreRect, startAngle, sweepAngle, false, bgPaint);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = 0;
		float y = 0;
		x = event.getX();
		y = event.getY();

		if (!(x >= left && x <= right && y >= top && y <= (top + 1.8 * mRadius))) {
			return true;
		}

		degree = getAngle(x, y, centerPoint);

		switch (event.getAction()) {
		// 屏幕按下
		case MotionEvent.ACTION_DOWN:
			// if (degree >= 0) {
			// j = (int) (degree / angleUnit);
			// } else {
			// // j = (int) ((sweepAngle + degree) / angleUnit);
			// }

			// setCurrentValue((int) (degree * 14) / 270);
			// if (mDown != null)
			// mDown.onDownChanged(getCurrentValue());
			// invalidate();

			break;

		case MotionEvent.ACTION_MOVE:
			setCurrentValue((int) Math.round((degree * 14) / 270));
			if (mMove != null)
				mMove.onMoveChanged(getCurrentValue());
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			setUpValue((int) Math.round((degree * 14) / 270));
			if (mUp != null)
				mUp.onUpChanged(getUpValue());
			invalidate();
			break;
		}

		return true;
	}

	// 根据中心点计算手指touch的角度
	private double getAngle(Float x, Float y, Point point) {
		float a = x - point.x;
		float b = y - point.y;
		double angle = Math.toDegrees(Math.atan2(b, a)) + 225;
		if (angle > 360) {
			angle = angle - 360;
		}
		if (angle > 270) {
			// 在第三象限
			if (a < 0) {
				angle = 0;
			}
			// 在第二象限
			else {
				angle = 270;
			}
		}
		return angle;
	}

}
