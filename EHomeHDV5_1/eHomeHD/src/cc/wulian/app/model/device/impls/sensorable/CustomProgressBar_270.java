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

public class CustomProgressBar_270 extends View {

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
	private String[] deviceReminder;

	private Matrix matrix;
	// 内圆背景图
	private Bitmap bitmapCircleBg;
	// 内圆旋转图
	private Bitmap bitmapCilcleMatrix;
	// 外圆弧形图
	private Bitmap bitmapProgressBarBg;
	private int srcId = cc.wulian.smarthomev5.R.drawable.device_progerss_270;
	public double getDegree() {
		return degree;
	}

	public void setDegree(double degree,int deviceSrcId) {
		this.degree = degree;
		startAngle = (float) (135 + degree);
		sweepAngle = (float) (270 - degree);
		setSrcId(deviceSrcId);
		roateBitmap();
		invalidate();
	}

	public CustomProgressBar_270(Context context) {
		super(context);
		init();
	}

	public CustomProgressBar_270(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomProgressBar_270(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setStyle(Style.STROKE);
		// bgPaint.setStrokeWidth(bgThickness);
		bgPaint.setColor(Color.parseColor("#ff9d9e9d"));
		BlurMaskFilter blurMaskFilter = new BlurMaskFilter(1, Blur.INNER);
		bgPaint.setMaskFilter(blurMaskFilter);

		bitmapCircleBg = BitmapFactory.decodeResource(getResources(),
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

		bgThickness = mDiameter * 2 / 11;
		bgPaint.setStrokeWidth(bgThickness);
		mRectF = new RectF(left, top, right, bottom);
		centerPoint = new Point((int) (left + mRadius), (int) (top + mRadius));

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int bitmapWidth = bitmapCilcleMatrix.getWidth();
		int bitmapHeight = bitmapCilcleMatrix.getHeight();
		canvas.drawBitmap(bitmapProgressBarBg,
				(centerPoint.x - bitmapProgressBarBg.getWidth() / 2),
				(centerPoint.y - bitmapProgressBarBg.getHeight() / 2), null);
		canvas.drawArc(mRectF, startAngle, sweepAngle, false, bgPaint);
		canvas.drawBitmap(bitmapCilcleMatrix,
				(centerPoint.x - bitmapWidth / 2),
				(centerPoint.y - bitmapHeight / 2), null);
	}

	private void roateBitmap() {
		matrix = new Matrix();
		matrix.postRotate((float) degree + 226);
//		matrix.postScale(0.5f,0.5f); 
		/**
		 * 从原始位图剪切图像,可以用Matrix(矩阵)来实现旋转等高级方式截图
		 * 
		 * @param Bitmap
		 *            source：要从中截图的原始位图 　　int x:起始x坐标 　　int y：起始y坐标 int
		 *            width：要截的图的宽度 int height：要截的图的宽度
		 */
		bitmapCilcleMatrix = Bitmap.createBitmap(bitmapCircleBg, 0, 0,
				bitmapCircleBg.getWidth(), bitmapCircleBg.getHeight(), matrix,
				true);
		bitmapProgressBarBg = BitmapFactory.decodeResource(getResources(),this.srcId);
	}

	public void setSrcId(int id) {
		srcId = id;
	}
}
