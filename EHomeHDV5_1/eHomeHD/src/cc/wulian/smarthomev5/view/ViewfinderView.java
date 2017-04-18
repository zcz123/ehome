package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.library.R;

public class ViewfinderView extends View {

	/**
	 * 2014年7月7日 17:56:02 修改扫描界面 gaoxy
	 */

	// ----------------start-------------------------------

	/**
	 * 刷新界面的时间
	 */
	private static final int REFRESH_DELAY = 10;
	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 5;

	/**
	 * 四个绿色边角对应的宽度
	 */
	private static float cornerWidth;

	/**
	 * 四个绿色边角对应的长度
	 */
	private static float cornerLength;

	/**
	 * 扫描线条大小
	 */
	private Rect lineRect = new Rect();

	/**
	 * 扫描的线条
	 */
	private Bitmap scanLine;

	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop = 0;

	/**
	 * 用来缩放的matrix
	 */
	Matrix matrix = new Matrix();

	private final Paint paint;
	private Bitmap resultBitmap;

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint = new Paint();
		Resources resources = getResources();

		cornerWidth = resources.getDimension(R.dimen.corner_width);
		cornerLength = resources.getDimension(R.dimen.corner_length);
		scanLine = BitmapFactory.decodeResource(getResources(),
				R.drawable.qrcode_scan_line);
	}

	private int width;
	private int height;

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		// 获取画布的宽和高
		width = canvas.getWidth();
		height = canvas.getHeight();

		/**
		 * 画扫描框边上的角，总共8个部分
		 */
		paint.setColor(Color.GREEN);
		canvas.drawRect(0, 0, cornerLength, cornerWidth, paint);
		canvas.drawRect(0, 0, cornerWidth, cornerLength, paint);
		canvas.drawRect(width - cornerLength, 0, width, cornerWidth, paint);
		canvas.drawRect(width - cornerWidth, 0, width, cornerLength, paint);
		canvas.drawRect(0, height - cornerWidth, 0 + cornerLength, height,
				paint);
		canvas.drawRect(0, height - cornerLength, 0 + cornerWidth, height,
				paint);
		canvas.drawRect(width - cornerLength, height - cornerWidth, width,
				height, paint);
		canvas.drawRect(width - cornerWidth, height - cornerLength, width,
				height, paint);

		// 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
		slideTop += SPEEN_DISTANCE;
		if (slideTop > height) {
			slideTop = 0;
		}
		lineRect.left = 0;
		lineRect.right = width;
		lineRect.top = slideTop;
		lineRect.bottom = slideTop + 18;
		canvas.drawBitmap(scanLine, null, lineRect, paint);

		if (resultBitmap != null) {
			canvas.save();
			canvas.translate(0, 0);
			matrix.setScale(width * 1.0f / resultBitmap.getWidth(), height
					* 1.0f / resultBitmap.getHeight());
			canvas.drawBitmap(resultBitmap, matrix, paint);
			canvas.restore();
		}

//		int testcolor=getResources().getColor(cc.wulian.smarthomev5.R.color.red);
//		canvas.drawColor(testcolor);
		postInvalidateDelayed(REFRESH_DELAY, 0, 0, width, height);
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}
}
