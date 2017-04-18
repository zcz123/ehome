package cc.wulian.app.model.device.impls.controlable.newthermostat;

import java.math.BigDecimal;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class RangeProgressBar extends View {

	
	private Context mContext;
	// 背景画笔
	private Paint bgPaint;
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
	
	//最大值 最小值 
	public int maxValue;
	public int minValue;
	
	//-----------------------------------------------------
	private int processHeat;
	private int processCool;
	private double curDegreeHeat;
	private double curDegreeCool;
	// 内景进度画笔
	private Paint fgPaint;
	private Paint fgPaint2;
	//拖动球  画笔
	private Paint mThumbCoolPaint;
	private Paint mThumbHeatPaint;
	private int circleWidth = DisplayUtil.dip2Pix(getContext(), 13);
	//文字 画笔
	private Paint textPaint;
	private String textValueHeat = "";
	private String textValueCool = "";
	private int textPaintSize = DisplayUtil.dip2Pix(getContext(), 12);
	//拖动球 距离 圆弧 距离
	public int THUMB_SPACE = DisplayUtil.dip2Pix(getContext(), 22);

	private Point arcPointHeat = null;
	private Point arcPointCool = null;
	// arcPoint坐标
	int arcX = 0;
	int arcY = 0;
	int arcX2 = 0;
	int arcY2 = 0;
	
	private RectF thumbRectfHeat;
	private RectF thumbRectfCool;
	
	private boolean isThumbHeatselected = false;
	private boolean isThumbCoolselected = false;
	private boolean isMoved = false;
	
	public Point getArcPoint() {
		return arcPointHeat;
	}

	public int getProcess() {
		return processHeat;
	}
	
	public void setCurDegreeHeat(double curDegree) {
		this.curDegreeHeat = curDegree;
	}
	
	public void setCurDegreeCool(double curDegree2) {
		this.curDegreeCool = curDegree2;
	}

	public void setThumbRectfHeat(Point arcPoint) {
		thumbRectfHeat = new RectF(arcPoint.x-circleWidth, arcPoint.y-circleWidth,
				arcPoint.x+circleWidth, arcPoint.y+circleWidth);
	}

	public void setThumbRectfCool(Point arcPoint) {
		thumbRectfCool = new RectF(arcPoint.x-circleWidth, arcPoint.y-circleWidth,
				arcPoint.x+circleWidth, arcPoint.y+circleWidth);
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
	
	public int getProgressByTemp(String temperature){
		int progressValue;
		float i = Float.parseFloat(temperature);
		progressValue = (int)Math.round((i - minValue)*100/(maxValue - minValue));
		return progressValue;
	}
	
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
	
	//华氏 转 摄氏时  小数小于0.5 为0，大于0.5 为0.5
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
	private OnAutoMoveViewValueChanged mMove;
	private OnAutoUpViewValueChanged mUp;
	
	public interface OnAutoMoveViewValueChanged {
		public void onMoveChanged(String value1,String value2);
	}

	public interface OnDownViewValueChanged {
		public void onDownChanged(int value);
	}

	public interface OnAutoUpViewValueChanged {
		public void onUpChanged(String value1,String value2);
	}

	public void setOnAutoMoveViewValueChanged(OnAutoMoveViewValueChanged move) {
		mMove = move;
	}

	public void setOnAutoUpViewValueChanged(OnAutoUpViewValueChanged up) {
		mUp = up;
	}

	public RangeProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public RangeProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public RangeProgressBar(Context context) {
		super(context);
		mContext = context;
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

		mRadialScoreRect = new RectF(0, 0, mDiameter, mDiameter);
		centerPoint = new Point((int) (left + mRadius), (int) (top + mRadius));
		
		
		mThumbCoolPaint = new Paint();
		mThumbCoolPaint.setAntiAlias(true);
		mThumbCoolPaint.setColor(Color.parseColor("#7fa82f"));
		mThumbCoolPaint.setStrokeWidth(1);
		
		mThumbHeatPaint = new Paint();
		mThumbHeatPaint.setAntiAlias(true);
		mThumbHeatPaint.setColor(Color.parseColor("#ff0000"));
		mThumbHeatPaint.setStrokeWidth(1);
		
		//渐变色
		int[] colors = {0xFFFBA318,0xFFFAA81E,0xFFFFFFFF,0xFFE8EC70, 
				0xFFE8EA6E,0xFFF2C743, 0xFFF7B22A,0xFFFAA81E, 0xFFFBA318};
		SweepGradient mSweepGradient = new SweepGradient(360, 360, colors, null);
		// 内景Paint
		fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		fgPaint.setStyle(Style.STROKE);
		fgPaint.setStrokeWidth(fgThickness);
		fgPaint.setShader(mSweepGradient);
		
		fgPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		fgPaint2.setStyle(Style.STROKE);
		fgPaint2.setStrokeWidth(fgThickness);
		fgPaint2.setColor(mContext.getResources().getColor(R.color.v5_gray_light));
		
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.parseColor("#FFFFFF"));
		textPaint.setTextSize(textPaintSize);
		textPaint.setTextAlign(Paint.Align.CENTER);

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

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/*
		 * startAngle 开始角度 sweepAngle 跨越角度 false 不画中心和弧线的连线 drawArc 绘制弧线
		 */
		canvas.drawArc(mRadialScoreRect, startAngle, sweepAngle, false, bgPaint);
		
		if(!isMoved){
			setCurDegreeHeat(processHeat* 270 / 100);
			setArcPointHeat(curDegreeHeat);
			setCurDegreeCool(processCool* 270 / 100);
			setArcPointCool(curDegreeCool);
		}
		
		drawProgress(canvas);
		drawThumb(canvas);
		drawText(canvas);
		
	}
	
	private void drawThumb(Canvas canvas){
		
		if (arcPointHeat != null) {
			canvas.drawCircle(arcPointHeat.x, arcPointHeat.y, circleWidth, mThumbHeatPaint);
		}
		
		if (arcPointCool != null) {
			canvas.drawCircle(arcPointCool.x, arcPointCool.y, circleWidth, mThumbCoolPaint);
		}
	}
	
	private void drawProgress(Canvas canvas){
		
		RectF fgRect = new RectF(left, top, right, bottom);
		
		canvas.drawArc(fgRect, startAngle, (float)curDegreeCool, false, fgPaint);
		
		canvas.drawArc(fgRect, startAngle, (float)curDegreeHeat, false, fgPaint2);
		
	}
	
	private void drawText(Canvas canvas){
		
		if (arcPointHeat != null) {
			canvas.drawText(textValueHeat, arcPointHeat.x,arcPointHeat.y+circleWidth/2, textPaint);
		}
		
		if (arcPointCool != null) {
			canvas.drawText(textValueCool, arcPointCool.x,arcPointCool.y+circleWidth/2, textPaint);
		}
		
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = 0;
		float y = 0;
		x = event.getX();
		y = event.getY();

		degree = getAngle(x, y, centerPoint);
		
		if(thumbRectfHeat.contains(x, y)){
			setArcPointHeat(degree);
			setCurDegreeHeat(degree);
		}
		else if(thumbRectfCool.contains(x, y)){
			setArcPointCool(degree);
			setCurDegreeCool(degree);
			
		}else{
			return true;
		}

		switch (event.getAction()) {
		// 屏幕按下
		case MotionEvent.ACTION_DOWN:
			float x1 = event.getX();
			float y1 = event.getY();
			if(thumbRectfHeat.contains(x1, y1)){
				isThumbHeatselected = true;
			}
			else{
				isThumbHeatselected = false;
			}
			
			if(thumbRectfCool.contains(x, y)){
				isThumbCoolselected = true;
			}
			else{
				isThumbCoolselected = false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			isMoved = true;
			setCurrentValue((int) Math.round((degree * 100) / 270));
			if(isThumbHeatselected){
				textValueHeat = getTempByProgress(getCurrentValue());
				if(maxValue == 90){
					textValueHeat = Integer.parseInt(getTempByProgress(getCurrentValue()))+"";
				}
				setArcPointHeat(degree);
				setCurDegreeHeat(degree);

				if(curDegreeHeat >= curDegreeCool - 40){
					curDegreeCool = curDegreeHeat + 40;
					if(curDegreeCool > 270.0){
						curDegreeCool = 270.0;
						curDegreeHeat = curDegreeCool - 40;
					}
					setArcPointHeat(curDegreeHeat);
					setArcPointCool(curDegreeCool);
					textValueHeat = getTempByProgress((int) Math.round((curDegreeHeat * 100) / 270));
					textValueCool = getTempByProgress((int) Math.round((curDegreeCool * 100) / 270));
					if(maxValue == 90){
						textValueHeat = Integer.parseInt(textValueHeat)+"";
						textValueCool = Integer.parseInt(textValueCool)+"";
					}
				}
			}
			if(isThumbCoolselected){
				textValueCool = getTempByProgress(getCurrentValue());
				if(maxValue == 90){
					textValueCool = Integer.parseInt(getTempByProgress(getCurrentValue()))+"";
				}
				setArcPointCool(degree);
				setCurDegreeCool(degree);

				if(curDegreeCool <= curDegreeHeat + 40){
					curDegreeHeat = curDegreeCool - 40;
					if(curDegreeHeat < 0.0){
						curDegreeHeat = 0.0;
						curDegreeCool = curDegreeHeat + 40;
					}
					textValueHeat = getTempByProgress((int) Math.round((curDegreeHeat * 100) / 270));
					textValueCool = getTempByProgress((int) Math.round((curDegreeCool * 100) / 270));
					if(maxValue == 90){
						textValueHeat = Integer.parseInt(textValueHeat)+"";
						textValueCool = Integer.parseInt(textValueCool)+"";
					}
					setArcPointHeat(curDegreeHeat);
					setArcPointCool(curDegreeCool);
				}
			}
			
			if (mMove != null)
				mMove.onMoveChanged(textValueHeat,textValueCool);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			
			setUpValue((int) Math.round((degree * 100) / 270));
			setCurrentValue((int) Math.round((degree * 100) / 270));
			
			if(isThumbHeatselected){
				textValueHeat = getTempByProgress(getCurrentValue());
				if(maxValue == 90){
					textValueHeat = Integer.parseInt(getTempByProgress(getCurrentValue()))+"";
				}
				setArcPointHeat(degree);
				setCurDegreeHeat(degree);
				Log.i("cccccc", "curDegreeCool:"+curDegreeCool+"--curDegreeHeat:"+curDegreeHeat);
				if(curDegreeHeat >= curDegreeCool - 40){
					curDegreeCool = curDegreeHeat + 40;
					if(curDegreeCool > 270.0){
						curDegreeCool = 270.0;
						curDegreeHeat = curDegreeCool - 40;
					}
					textValueHeat = getTempByProgress((int) Math.round((curDegreeHeat * 100) / 270));
					textValueCool = getTempByProgress((int) Math.round((curDegreeCool * 100) / 270));
					if(maxValue == 90){
						textValueHeat = Integer.parseInt(textValueHeat)+"";
						textValueCool = Integer.parseInt(textValueCool)+"";
					}
					setArcPointHeat(curDegreeHeat);
					setArcPointCool(curDegreeCool);
				}
			}
			if(isThumbCoolselected){
				textValueCool = getTempByProgress(getCurrentValue());
				if(maxValue == 90){
					textValueCool = Integer.parseInt(getTempByProgress(getCurrentValue()))+"";
				}
				setArcPointCool(degree);
				setCurDegreeCool(degree);

				if(curDegreeCool <= curDegreeHeat + 40){
					curDegreeHeat = curDegreeCool - 40;
					if(curDegreeHeat < 0.0){
						curDegreeHeat = 0.0;
						curDegreeCool = curDegreeHeat + 40;
					}
					textValueHeat = getTempByProgress((int) Math.round((curDegreeHeat * 100) / 270));
					textValueCool = getTempByProgress((int) Math.round((curDegreeCool * 100) / 270));
					if(maxValue == 90){
						textValueHeat = Integer.parseInt(textValueHeat)+"";
						textValueCool = Integer.parseInt(textValueCool)+"";
					}
					setArcPointHeat(curDegreeHeat);
					setArcPointCool(curDegreeCool);
				}
			}
			
			if (mUp != null){
				mUp.onUpChanged(textValueHeat,textValueCool);
			}
			isThumbHeatselected = false;
			isThumbCoolselected = false;
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

	public void setArcPointHeat(double degree) {
		double radians = Math.toRadians(degree - 45);
		double incrementX = Math.cos(radians) * (mRadius+THUMB_SPACE);
		double incrementY = Math.sin(radians) * (mRadius+THUMB_SPACE);
		arcX = (int) (centerPoint.x - incrementX);
		arcY = (int) (centerPoint.y - incrementY);

		if (mRadius != 0) {
			arcPointHeat = new Point(arcX, arcY);
			thumbRectfHeat = new RectF(arcX-circleWidth-THUMB_SPACE, arcY-circleWidth-THUMB_SPACE,
					arcX+circleWidth+THUMB_SPACE, arcY+circleWidth+THUMB_SPACE);
		}
	}
	
	public void setArcPointCool(double degree2) {
		double radians = Math.toRadians(degree2 - 45);
		double incrementX = Math.cos(radians) * (mRadius+THUMB_SPACE);
		double incrementY = Math.sin(radians) * (mRadius+THUMB_SPACE);
		arcX2 = (int) (centerPoint.x - incrementX);
		arcY2 = (int) (centerPoint.y - incrementY);

		if (mRadius != 0) {
			arcPointCool = new Point(arcX2, arcY2);
			thumbRectfCool = new RectF(arcX2-circleWidth-THUMB_SPACE, arcY2-circleWidth-THUMB_SPACE,
					arcX2+circleWidth+THUMB_SPACE, arcY2+circleWidth+THUMB_SPACE);
		}
	}

	// 提供对应修改角度方法,实现point位置的显示
	public void setProcessHeat(String processValueHeat) {
		textValueHeat = processValueHeat;
		Log.i("cccccc", "textValueHeat:"+textValueHeat);
		this.processHeat = getProgressByTemp(processValueHeat);
		setCurDegreeHeat(processHeat* 270 / 100);
		setArcPointHeat(curDegreeHeat);
		isMoved = false;
		postInvalidate();
	}
	
	public void setProcessCool(String processValueCool) {
		textValueCool = processValueCool;
		Log.i("cccccc", "textValueCool:"+textValueCool);
		this.processCool = getProgressByTemp(processValueCool);
		setCurDegreeCool(processCool* 270 / 100);
		setArcPointCool(curDegreeCool);
		isMoved = false;
		postInvalidate();
	}

}
