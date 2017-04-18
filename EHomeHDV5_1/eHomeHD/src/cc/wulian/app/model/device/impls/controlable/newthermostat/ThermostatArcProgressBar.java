package cc.wulian.app.model.device.impls.controlable.newthermostat;

import java.math.BigDecimal;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class ThermostatArcProgressBar extends View {
	
	// 背景画笔
	private Paint bgPaint;
	// 内景画笔
	private Paint fgHeatPaint;
	private Paint fgCoolPaint;
	// 初始和跨越角度
	private float startAngle = 135.0f;
	private float sweepAngle = 270.0f;
	// 弧形条的宽度 即bgPaint的宽度
	private float bgThickness = DisplayUtil.dip2Pix(getContext(), 13);
	// 弧形条内部的宽度
	protected float fgThickness = DisplayUtil.dip2Pix(getContext(), 13);
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

	//模式 heat cool
	private String mode;
	//最大值 最小值 
	public int maxValue;
	public int minValue;
	private Point arcPoint = null;
	private int process;

	private double curDegree;
	
	//拖动球 制冷模式 画笔
	private Paint mThumbCoolPaint;
	//拖动球 制热模式 画笔
	private Paint mThumbHeatPaint;
	private int circleWidth = DisplayUtil.dip2Pix(getContext(), 13);
	//文字 画笔
	private Paint textPaint;
	private String textValue ="";
	private int textPaintSize = DisplayUtil.dip2Pix(getContext(), 12);
	//拖动球 距离 圆弧 距离
	public int THUMB_SPACE = DisplayUtil.dip2Pix(getContext(), 22);
	//拖动球所在区域
	private RectF thumbRectf;
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
	
	private boolean isMove = false;
	//是否按住小球
	private boolean isThumbSelected = false;

	public ThermostatArcProgressBar(Context context) {
		super(context);
		init(context);

	}

	public ThermostatArcProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ThermostatArcProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}
	
	public int getMaxValue() {
		return maxValue;
	}
	
	public int getMinValue() {
		return minValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	

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
	
	public double getDegree() {
		return degree;
	}

	public void setDegree(double degree) {
		this.degree = degree;
	}

	public Point getArcPoint() {
		return arcPoint;
	}

	public int getProcess() {
		return process;
	}
	
	public void setCurDegree(double curDegree) {
		this.curDegree = curDegree;
	}
	
	//根据温度计算进度
		public int getProgressByTemp(String temperature){
			int progressValue;
			float i = Float.parseFloat(temperature);
			progressValue = (int)Math.round((i - minValue)*100/(maxValue - minValue));
			return progressValue;
		}
		
		//根据进度计算温度
		public String getTempByProgress(int progress){
			String temp = "";
			double d = ((double)progress)*(maxValue - minValue)/100+minValue;
			BigDecimal bd = new BigDecimal(d);
			bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
			temp = TempFormat(bd.toString());
			if(maxValue == 90){
				temp = (int)d+"";
			}
			return temp;
		}
		
		//小数部分大于0.5设为0.5，小于0.5设为0
		private String TempFormat(String temp){
			double d = Double.valueOf(temp);
			if((d-0.5) < (int)d){
				d = (int)d;
			}
			if((d-0.5) > (int)d){
				d = (int)d + 0.5;
			}
			temp = d+"";
			return temp;
		}
		
		/**
		 * 提供给外部访问值的接口
		 */
		private OnMoveViewValueChanged mMove;
		private OnUpViewValueChanged mUp;
		
		public interface OnMoveViewValueChanged {
			public void onMoveChanged(String value);
		}

		public interface OnDownViewValueChanged {
			public void onDownChanged(int value);
		}

		public interface OnUpViewValueChanged {
			public void onUpChanged(String value);
		}

		public void setOnMoveViewValueChanged(OnMoveViewValueChanged move) {
			mMove = move;
		}

		public void setOnUpViewValueChanged(OnUpViewValueChanged up) {
			mUp = up;
		}

	/**
	 * 初始化一些成员变量
	 * 
	 * @param context
	 */

	private void init(Context context) {
		
		// 背景Paint
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setStyle(Style.STROKE);
		bgPaint.setStrokeWidth(bgThickness);
		bgPaint.setColor(Color.parseColor("#ff9d9e9d"));
		// 设置背景为透明
		bgPaint.setAlpha(0);
		BlurMaskFilter blurMaskFilter = new BlurMaskFilter(1, Blur.INNER);
		bgPaint.setMaskFilter(blurMaskFilter);

		mThumbCoolPaint = new Paint();
		mThumbCoolPaint.setAntiAlias(true);
		mThumbCoolPaint.setColor(Color.parseColor("#7fa82f"));
		mThumbCoolPaint.setStrokeWidth(1);

		mThumbHeatPaint = new Paint();
		mThumbHeatPaint.setAntiAlias(true);
		mThumbHeatPaint.setColor(Color.parseColor("#ff0000"));
		mThumbHeatPaint.setStrokeWidth(1);
		
		//渐变色
//		int[] colors = {0xFFE5BD7D, 0xFFFAAA64,0xFFFFFFFF, 0xFF6AE2FD,
//                0xFF8CD0E5, 0xFFA3CBCB,0xFFBDC7B3, 0xFFD1C299, 0xFFE5BD7D,};
		//0xFFE8EC70,0xFFE8EA6E,0xFFF2C743, 0xFFF7B22A,0xFFFAA81E, 0xFFFBA318 颜色加深
		int[] heatColors = {0xFFFBA318,0xFFFAA81E,0xFFFFFFFF,0xFFE8EC70,
				0xFFE8EA6E,0xFFF2C743, 0xFFF7B22A,0xFFFAA81E, 0xFFFBA318};
		SweepGradient mSweepGradientHeat = new SweepGradient(360, 360, heatColors, null);
		// 内景Paint
		fgHeatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		fgHeatPaint.setStyle(Style.STROKE);
		fgHeatPaint.setStrokeWidth(fgThickness);
		fgHeatPaint.setShader(mSweepGradientHeat);

		int[] coolColors = {0xFF7AC206, 0xFF7AC206,0xFFFFFFFF,0xFFB5E764,
				0xFFB5E764,0xFF9Ed93E, 0xFF9Ed93E,0xFF7AC206, 0xFF7AC206};
		SweepGradient mSweepGradientCool = new SweepGradient(360, 360, coolColors, null);
		fgCoolPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		fgCoolPaint.setStyle(Style.STROKE);
		fgCoolPaint.setStrokeWidth(fgThickness);
		fgCoolPaint.setShader(mSweepGradientCool);
		
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.parseColor("#FFFFFF"));
		textPaint.setTextSize(textPaintSize);
		textPaint.setTextAlign(Paint.Align.CENTER);
		
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
			mRadius = ((mDiameter / 2 - (getPaddingTop() + getPaddingBottom())) * 6) / 8;
		} else {
			mDiameter = w;
			mRadius = ((mDiameter / 2 - (getPaddingLeft() + getPaddingRight())) * 6) / 8;
		}
		// Init the draw arc Rect object
		left = (getWidth() / 2) - mRadius + getPaddingLeft();
		right = (getWidth() / 2) + mRadius - getPaddingRight();
		top = (getHeight() / 2) - mRadius + getPaddingTop();
		bottom = (getHeight() / 2) + mRadius - getPaddingBottom();
		mRadialScoreRect = new RectF(left, top, right, bottom);

		centerPoint = new Point((int) (left + mRadius), (int) (top + mRadius));
	}	

//	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		/*
		 * startAngle 开始角度 sweepAngle 跨越角度 false 不画中心和弧线的连线 drawArc 绘制弧线
		 */
		canvas.drawArc(mRadialScoreRect, startAngle, sweepAngle, false, bgPaint);
		
		if(!isMove){
			setCurDegree(process* 270 / 100);
			setArcPoint(curDegree);
		}
		
		drawProgress(canvas);
		drawThumb(canvas);
		drawText(canvas);
		
	}
	
	private void drawThumb(Canvas canvas){
		
		if (arcPoint != null) {
			if (!StringUtil.isNullOrEmpty(mode)){
				if(StringUtil.equals(mode,"01")){
					canvas.drawCircle(arcPoint.x, arcPoint.y, circleWidth, mThumbHeatPaint);
				}else{
					canvas.drawCircle(arcPoint.x, arcPoint.y, circleWidth, mThumbCoolPaint);
				}
			}
		}
	}
	
	private void drawProgress(Canvas canvas){
		RectF fgRect = new RectF(left, top, right, bottom);
		if(!StringUtil.isNullOrEmpty(mode)){
			if(StringUtil.equals(mode,"01")){
				canvas.drawArc(fgRect, startAngle, (float)curDegree, false, fgHeatPaint);
			}else{
				canvas.drawArc(fgRect, startAngle, (float)curDegree, false, fgCoolPaint);
			}
		}
	}
	
	private void drawText(Canvas canvas){
		
		if (arcPoint != null) {
			canvas.drawText(textValue, arcPoint.x,arcPoint.y+circleWidth/2, textPaint);
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = 0;
		float y = 0;
		x = event.getX();
		y = event.getY();

		degree = getAngle(x, y, centerPoint);
		if(thumbRectf.contains(x, y)){
			setArcPoint(degree);
			setCurDegree(degree);
		}else{
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			float x1 = event.getX();
			float y1 = event.getY();
			if(thumbRectf.contains(x1, y1)){
				isThumbSelected = true;
			}
			else{
				isThumbSelected = false;
			}
			
			break;

		case MotionEvent.ACTION_MOVE:
			isMove =true;
			setCurrentValue((int) Math.round((degree * 100) / 270));
			if(isThumbSelected){
				textValue = getTempByProgress(getCurrentValue());
				if(maxValue == 90){
				textValue = Integer.parseInt(getTempByProgress(getCurrentValue()))+"";
				}
				setArcPoint(degree);
				setCurDegree(degree);
			}
			if (mMove != null)
				mMove.onMoveChanged(getTempByProgress(getCurrentValue()));
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			setUpValue((int) Math.round((degree * 100) / 270));
			if(isThumbSelected){
				textValue = getTempByProgress(getCurrentValue());
				if(maxValue == 90){
				textValue = Integer.parseInt(getTempByProgress(getCurrentValue()))+"";
				}
				setArcPoint(degree);
				setCurDegree(degree);
			}
			
			if (mUp != null){
				mUp.onUpChanged(getTempByProgress(getUpValue()));
			}
			isThumbSelected = false;
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

	/**
	 * 计算出当前点击位置对应的弧线上的坐标点
	 * 
	 * @param degree
	 */

	public void setArcPoint(double degree) {
		double radians = Math.toRadians(degree - 45);
		double incrementX = Math.cos(radians) * (mRadius+THUMB_SPACE);
		double incrementY = Math.sin(radians) * (mRadius+THUMB_SPACE);
		arcX = (int) (centerPoint.x - incrementX);
		arcY = (int) (centerPoint.y - incrementY);

		if (mRadius != 0) {
			arcPoint = new Point(arcX, arcY);
			thumbRectf= new RectF(arcX-circleWidth-THUMB_SPACE, arcY-circleWidth-THUMB_SPACE,
					arcX+circleWidth+THUMB_SPACE, arcY+circleWidth+THUMB_SPACE);
		}
	}

	// 提供对应修改角度方法,实现point位置的显示
	public void setProcess(String processValue) {
		textValue = processValue;
		this.process = getProgressByTemp(processValue);
		setCurDegree(process* 270 / 100);
		setArcPoint(curDegree);
		isMove = false;
		postInvalidate();
	}

	public void setMode(String mode){
		this.mode = mode;
	}

}
