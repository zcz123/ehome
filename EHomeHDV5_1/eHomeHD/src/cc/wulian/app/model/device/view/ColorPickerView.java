package cc.wulian.app.model.device.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View
{
	private final boolean debug = true;
	private final String TAG = "ColorPicker";

	Context context;
	private String title;// 标题
	private int mInitialColor;// 初始颜色

	private Paint mPaint;// 渐变色环画笔
	private Paint mCenterPaint;// 中间圆画笔
	private Paint mLinePaint;// 分隔线画笔
	private Paint mRectPaint;// 渐变方块画笔

	private float rectLeft;// 渐变方块左x坐标
	private float rectTop;// 渐变方块右x坐标
	private float rectRight;// 渐变方块上y坐标
	private float rectBottom;// 渐变方块下y坐标

	private int[] mCircleColors;// 渐变色环颜色

	private int mHeight = 100;// View高
	private int mWidth = 100;// View宽
	private float r;// 色环半径(paint中部)
	private float centerRadius;// 中心圆半径

	private boolean downInCircle = true;// 按在渐变环上
	private boolean downInRect;// 按在渐变方块上
	private boolean highlightCenter;// 高亮
	private boolean highlightCenterLittle;// 微亮
	private OnColorChangedListener mListener;
	private float length;

	public static boolean isLan;

	long firstTime = System.currentTimeMillis();
	long lastTime;

	// GatewayInfo currentGatewayInfo = AccountManager.getAccountManger().getmCurrentInfo();

	public ColorPickerView( Context context, AttributeSet attrs, int defStyle )
	{

		super(context, attrs, defStyle);
	}

	public ColorPickerView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public ColorPickerView( Context context, int height, int width, int initialColor, OnColorChangedListener listener )
	{
		super(context);
		this.context = context;
		mListener = listener;
		mInitialColor = initialColor;
		this.mHeight = height;
		this.mWidth = width;
		setMinimumHeight(height);
		setMinimumWidth(width);
		// 渐变色环参数
		mCircleColors = new int[]{0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFFFFFFFF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};

		Shader s = new SweepGradient(0, 0, mCircleColors, null);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setShader(s);
		// mPaint.setStyle(Paint.Style.STROKE);
		// mPaint.setStrokeWidth(50);
		r = (mWidth < mHeight) ? mWidth / 2 : mHeight / 2;
		length = (mWidth < mHeight) ? mHeight / 2 : mWidth / 2;

		// 中心圆参数
		mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCenterPaint.setColor(mInitialColor);
		mCenterPaint.setStrokeWidth(5);
		centerRadius = (r - mPaint.getStrokeWidth() / 2) * 0.7f;

		// 边框参数
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(Color.parseColor("#72A1D1"));
		mLinePaint.setStrokeWidth(4);

		rectLeft = r - (r * 0.3f) / 2;
		rectTop = r - (r * 0.3f) / 2;

		rectRight = length;
		rectBottom = (r * 0.3f) / 2;
	}

	@Override
	protected void onDraw( Canvas canvas ) {

		// 移动中心
		canvas.translate(mWidth / 2, mHeight / 2);

		// 画色环
		canvas.drawOval(new RectF(-r, -r, r, r), mPaint);

		// mRectPaint.setShader(rectShader);
		// canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, mCenterPaint);
		canvas.drawCircle(rectLeft, rectTop, rectBottom, mCenterPaint);
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		float x = event.getX() - mWidth / 2;
		float y = event.getY() - mHeight / 2;
		boolean inCircle = inCenter(x, y, r);
		boolean inCenter = inCenter(x, y, r);
		boolean inRect = inRect(x, y);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				firstTime = System.currentTimeMillis();
				downInCircle = inCircle;
				downInRect = inRect;
				highlightCenter = inCenter;
			case MotionEvent.ACTION_MOVE :
				if (inCenter) {// down按在渐变色环内, 且move也在渐变色环内
					float angle = (float) Math.atan2(y, x);
					float unit = (float) (angle / (2 * Math.PI));
					if (unit < 0) {
						unit += 1;
					}
					if (isLan) {
						lastTime = System.currentTimeMillis();
						if ((lastTime - firstTime) > 300) {
							firstTime = lastTime;
							mListener.colorChanged(Integer.toHexString(mCenterPaint.getColor()));
						}
					}
					mCenterPaint.setColor(interpCircleColor(mCircleColors, unit));
					// mListener.colorChanged(Integer.toHexString(mCenterPaint.getColor()));
					if (debug) Log.v(TAG, "色环内, 坐标: " + x + "," + y);
				}
				// else if(downInRect && inRect) {//down在渐变方块内, 且move也在渐变方块内
				// mCenterPaint.setColor(interpRectColor(mRectColors, x));
				// }
				if (debug) Log.v(TAG, "[MOVE] 高亮: " + highlightCenter + "微亮: " + highlightCenterLittle + " 中心: " + inCenter);
				if ((highlightCenter && inCenter) || (highlightCenterLittle && inCenter)) {// 点击中心圆, 当前移动在中心圆
					highlightCenter = true;
					highlightCenterLittle = false;
				}
				else if (highlightCenter || highlightCenterLittle) {// 点击在中心圆, 当前移出中心圆
					highlightCenter = false;
					highlightCenterLittle = true;
				}
				else {
					highlightCenter = false;
					highlightCenterLittle = false;
				}
				invalidate();
				break;
			case MotionEvent.ACTION_UP :
				if (highlightCenter && inCenter) {// 点击在中心圆, 且当前启动在中心圆
					if (mListener != null) {
						mListener.colorChanged(Integer.toHexString(mCenterPaint.getColor()));
						// ColorPickerDialog.this.dismiss();
					}
				}
				if (downInCircle) {
					downInCircle = false;
				}
				if (downInRect) {
					downInRect = false;
				}
				if (highlightCenter) {
					highlightCenter = false;
				}
				if (highlightCenterLittle) {
					highlightCenterLittle = false;
				}
				invalidate();
				break;
		}
		return true;
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
		super.onMeasure(mWidth, mHeight);
	}

	/**
	 * 坐标是否在中心圆上
	 * 
	 * @param x
	 *          坐标
	 * @param y
	 *          坐标
	 * @param centerRadius
	 *          圆半径
	 * @return
	 */
	private boolean inCenter( float x, float y, float centerRadius ) {
		double centerCircle = Math.PI * centerRadius * centerRadius;
		double fingerCircle = Math.PI * (x * x + y * y);
		if (fingerCircle < centerCircle) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 坐标是否在渐变色中
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean inRect( float x, float y ) {
		if (x <= rectRight && x >= rectLeft && y <= rectBottom && y >= rectTop) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 获取圆环上颜色
	 * 
	 * @param colors
	 * @param unit
	 * @return
	 */
	private int interpCircleColor( int colors[], float unit ) {
		if (unit <= 0) { return colors[0]; }
		if (unit >= 1) { return colors[colors.length - 1]; }

		float p = unit * (colors.length - 1);
		int i = (int) p;
		p -= i;

		// now p is just the fractional part [0...1) and i is the index
		int c0 = colors[i];
		int c1 = colors[i + 1];
		int a = ave(Color.alpha(c0), Color.alpha(c1), p);
		int r = ave(Color.red(c0), Color.red(c1), p);
		int g = ave(Color.green(c0), Color.green(c1), p);
		int b = ave(Color.blue(c0), Color.blue(c1), p);

		return Color.argb(a, r, g, b);
	}

	private int ave( int s, int d, float p ) {
		return s + Math.round(p * (d - s));
	}

	/**
	 * 回调接口
	 * 
	 * @author <a href="clarkamx@gmail.com">LynK</a>
	 * 
	 *         Create on 2012-1-6 上午8:21:05
	 * 
	 */
	public interface OnColorChangedListener
	{
		/**
		 * 回调函数
		 * 
		 * @param color
		 *          选中的颜色
		 */
		void colorChanged( String color );
	}

	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public int getmInitialColor() {
		return mInitialColor;
	}

	public void setmInitialColor( int mInitialColor ) {
		this.mInitialColor = mInitialColor;
	}

	public void setColor( int color ) {
		mCenterPaint.setColor(color);
		invalidate();
	}

	public OnColorChangedListener getmListener() {
		return mListener;
	}

	public void setmListener( OnColorChangedListener mListener ) {
		this.mListener = mListener;
	}

}
