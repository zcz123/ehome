package cc.wulian.app.model.device.impls.controlable.thermostat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cc.wulian.smarthomev5.R;

public class MyArcProgressBar extends ArcProgressBar {
	private Paint paint;
	private Point arcPoint = null;
	private int process;

	private Bitmap bitmap1;
	private Bitmap bitmap2;

	/**
	 * Matrix的操作，总共分为translate(平移)，rotate(旋转)，scale(缩放)和skew(倾斜)四种
	 */
	Matrix matrix;
	/**
	 * 转动后的bitmap的宽
	 */
	int bitmapWidth = 0;
	int bitmapHeight = 0;

	// arcPoint坐标
	int arcX = 0;
	int arcY = 0;

	public MyArcProgressBar(Context context) {
		super(context);
		init(context);

	}

	public MyArcProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyArcProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}

	/**
	 * 初始化一些成员变量
	 * 
	 * @param context
	 */

	private void init(Context context) {

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.rgb(220, 115, 39));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(1.0f);
		/**
		 * 加载图片
		 */
		bitmap1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.device_music_box_thumb);
		roateBitmap();

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (arcPoint != null) {

			bitmapWidth = bitmap2.getWidth();
			bitmapHeight = bitmap2.getHeight();
			canvas.drawBitmap(bitmap2, arcPoint.x - bitmapWidth / 2, arcPoint.y
					- bitmapHeight / 2, null);

			/**
			 * java.lang.IllegalArgumentException: Cannot draw recycled bitmaps
			 */

			// if (!bitmap2.isRecycled()) {
			// bitmap2.recycle();
			// bitmap2 = null;
			// }

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// x = event.getX();
			// y = event.getY();
			// if (x < (arcPoint.x + bitmapWidth / 2)
			// && x > (arcPoint.x - bitmapWidth / 2)
			// && arcPoint.y > (arcPoint.y - bitmapHeight / 2)
			// && arcPoint.y < (arcPoint.y + bitmapHeight / 2)) {
			// bitmap1 = BitmapFactory.decodeResource(getResources(),
			// R.drawable.device_music_box_thumb);
			//
			// }
			setArcPoint(degree);
			roateBitmap();
			invalidate();
			break;

		case MotionEvent.ACTION_MOVE:
			setArcPoint(degree);
			roateBitmap();
			break;

		case MotionEvent.ACTION_UP:
			roateBitmap();
			setArcPoint(degree);
			invalidate();
			break;

		}

		return true;
	}

	/**
	 * 计算出当前点击位置对应的弧线上的坐标点
	 * 
	 * @param degree
	 */

	public void setArcPoint(double degree) {
		double radians = Math.toRadians(degree - 45);
		double incrementX = Math.cos(radians) * mRadius;
		double incrementY = Math.sin(radians) * mRadius;
		arcX = (int) (centerPoint.x - incrementX);
		arcY = (int) (centerPoint.y - incrementY);

		if (mRadius != 0) {
			arcPoint = new Point(arcX, arcY);
		}
	}

	/**
	 * 选择bitmap
	 */
	private void roateBitmap() {
		matrix = new Matrix();
		matrix.postRotate((float) degree - 135);
		/**
		 * 从原始位图剪切图像,可以用Matrix(矩阵)来实现旋转等高级方式截图
		 * 
		 * @param Bitmap
		 *            source：要从中截图的原始位图 　　int x:起始x坐标 　　int y：起始y坐标 int
		 *            width：要截的图的宽度 int height：要截的图的宽度
		 */
		bitmap2 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(),
				bitmap1.getHeight(), matrix, true);

	}

	/*
	 * private void initBitmap(double curDegree) { matrix = new Matrix();
	 * matrix.postRotate((float) curDegree - 135); bitmap2 =
	 * Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(),
	 * bitmap1.getHeight(), matrix, true); }
	 */
	public Point getArcPoint() {
		return arcPoint;
	}

	public int getProcess() {
		return process;
	}

	// 提供对应修改角度方法,实现point位置的显示
	public void setProcess(int process) {
		this.process = process;
		double curDegree = (process - 16) * 270 / 14;
		setArcPoint(curDegree);
		// initBitmap(curDegree);
		invalidate();
	}
}
