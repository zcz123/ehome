package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CustomProgressBar_360 extends View {

	private Point centerPoint;
	private float mRadius;
	private float mDiameter;
	private Paint bgPaint;
	private float bgThickness;
	private float startAngle;
	private float sweepAngle;
	private RectF mRectF;
	private float left;
	private float top;
	private float right;
	private float bottom;
	private double degree;

	Matrix matrix;
	private Bitmap bitmap1;
	private Bitmap bitmap2;
	private Bitmap bitmap3;
	private int srcId = cc.wulian.smarthomev5.R.drawable.device_progerss_360;
	
	public double getDegree() {
		return degree;
	}

	public void setDegree(double degree,int deviceSrcId) {
		this.degree = degree;
		startAngle = (float) (90 + degree);
		sweepAngle = (float) (360 - degree);
		setId(deviceSrcId);
		roateBitmap();
		invalidate();
	}

	public CustomProgressBar_360(Context context) {
		super(context);
		init();
	}

	public CustomProgressBar_360(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomProgressBar_360(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// 鑳屾櫙Paint
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setStyle(Style.STROKE);
		// bgPaint.setStrokeWidth(bgThickness);
		bgPaint.setColor(Color.parseColor("#ff9d9e9d"));
		BlurMaskFilter blurMaskFilter = new BlurMaskFilter(1, Blur.INNER);
		bgPaint.setMaskFilter(blurMaskFilter);
		bitmap1 = BitmapFactory.decodeResource(getResources(),
				cc.wulian.smarthomev5.R.drawable.device_circle);
		roateBitmap();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w > h) {
			mDiameter = h;
			mRadius = (mDiameter / 2 - (getPaddingTop() + getPaddingBottom())) * 7 / 11;
		} else {
			mDiameter = w;
			mRadius = (mDiameter / 2 - getPaddingTop() - getPaddingRight()) * 7 / 11;
		}
		left = getWidth() / 2 - mRadius + getPaddingLeft();
		top = getHeight() / 2 - mRadius + getPaddingTop();
		right = getWidth() / 2 + mRadius - getPaddingRight();
		bottom = getHeight() / 2 + mRadius - getPaddingBottom();

		bgThickness = mDiameter * 2 / 11-1;
		bgPaint.setStrokeWidth(bgThickness);
		mRectF = new RectF(left, top, right, bottom);
		centerPoint = new Point((int) (left + mRadius), (int) (top + mRadius));

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int bitmapWidth = bitmap2.getWidth();
		int bitmapHeight = bitmap2.getHeight();
		canvas.drawBitmap(bitmap3, (centerPoint.x - bitmap3.getWidth() / 2),
				(centerPoint.y - bitmap3.getHeight() / 2), null);
		canvas.drawArc(mRectF, startAngle, sweepAngle, false, bgPaint);
		canvas.drawBitmap(bitmap2, (centerPoint.x - bitmapWidth / 2),
				(centerPoint.y - bitmapHeight / 2), null);
	}

	private void roateBitmap() {
		matrix = new Matrix();
		matrix.postRotate((float) degree + 181);
//		matrix.postScale(0.5f,0.5f); 
		/**
		 * 从原始位图剪切图像,可以用Matrix(矩阵)来实现旋转等高级方式截图
		 * 
		 * @param Bitmap
		 *            source：要从中截图的原始位图 　　int x:起始x坐标 　　int y：起始y坐标 int
		 *            width：要截的图的宽度 int height：要截的图的宽度
		 */
		bitmap2 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(),
				bitmap1.getHeight(), matrix, true);
		
		bitmap3 = BitmapFactory.decodeResource(getResources(),srcId);
	}

	public void setSrcId(int id) {
		srcId = id;
	}
}
