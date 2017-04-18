package cc.wulian.app.model.device.impls.controlable.newthermostat.program;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

public class ProgramViewAuto extends View {
	private final String TAG = getClass().getSimpleName();
	
	private int width;
	private int height;
	
	//拖动球画笔
	private Paint thumbHeatPaint;
	private Paint thumbCoolPaint;
	//文字 画笔
	private Paint textPaint;
	
	private Paint tempPaint;
	//温度显示大圆 宽度
	private static final int TEMP_CIECLE_WIDTH = 50;
	//温度显示 与 拖动球之间距离
	private static final int TEMP_CIRCLE_SPACE = 100;
	// X 最大值 和最小值
	private int maxX = 1070;
	private int minX = 65;
	// Y 最大值 和最小值
	private int maxY = 515;
	private int minY = 75;
	private Paint timePaint;
	private Bitmap timeBitmap;
	
	private boolean isTempShow = false;
	private boolean isTimeShow = false;
	
	private List<ProgramBallAuto> thumbList;
	
	private ProgramBallAuto touchBall;
	
	private int maxTemp;
	private int minTemp;
	private boolean isTempUnitC;
	
	private boolean isTouchHeat;
	//判断是否被拖动
	private boolean isBallChanged = false;

	/**
	 * 提供给外部访问值的接口
	 */
	private OnMoveValueChangedable mMoveChanged;

	public interface OnMoveValueChangedable {
		public void onMoveChanged();
	}


	public ProgramViewAuto(Context context) {
		super(context);
		initDatas();
	}

	public ProgramViewAuto(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initDatas();
	}

	public ProgramViewAuto(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDatas();
	}
	
	public void setThumbList(List<ProgramBallAuto> mthumbList) {
		if(this.thumbList==null){
			this.thumbList=new ArrayList<>();
		}
		this.thumbList.clear();
		if(mthumbList!=null&&mthumbList.size()>0){
			for(ProgramBallAuto pball:mthumbList){
				ProgramBallAuto pbCopy=null;
				try {
					pbCopy = (ProgramBallAuto)pball.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				if(pbCopy!=null){
					this.thumbList.add(pbCopy);
				}
			}
			setThumbTempF(thumbList);
			setXbyTime(thumbList);
			setYbyTemp(thumbList);
			setTimeValue(thumbList);
		}
		postInvalidate();
	}

	public void setmMoveChanged(OnMoveValueChangedable mMoveChanged) {
		this.mMoveChanged = mMoveChanged;
	}

	public boolean isBallChanged() {
		return isBallChanged;
	}

	public void setBallChanged(boolean ballChanged) {
		isBallChanged = ballChanged;
	}

	public List<ProgramBallAuto> getThumbList() {
		return thumbList;
	}

	public void setMaxTemp(int maxTemp) {
		this.maxTemp = maxTemp;
	}

	public void setMinTemp(int minTemp) {
		this.minTemp = minTemp;
	}

	public void setTempUnitC(boolean isTempUnitC){
		this.isTempUnitC = isTempUnitC;
	}

	private void initDatas(){

		thumbCoolPaint = new Paint();
		thumbCoolPaint.setAntiAlias(true);
		thumbCoolPaint.setColor(Color.parseColor("#7fa82f"));
		thumbCoolPaint.setStrokeWidth(1);

		thumbHeatPaint = new Paint();
		thumbHeatPaint.setAntiAlias(true);
		thumbHeatPaint.setColor(Color.parseColor("#ff0000"));
		thumbHeatPaint.setStrokeWidth(1);
		
		tempPaint = new Paint();
		tempPaint.setAntiAlias(true);
		tempPaint.setColor(Color.parseColor("#7fa82f"));
		tempPaint.setStrokeWidth(1);
		
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.parseColor("#FFFFFF"));
		textPaint.setTextSize(25);
		textPaint.setTextAlign(Paint.Align.CENTER);
		
		timePaint = new Paint();
		timePaint.setAntiAlias(true);
		timePaint.setColor(Color.parseColor("#000000"));
		timePaint.setTextSize(25);
		timePaint.setTextAlign(Paint.Align.CENTER);
		
		timeBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.thermost_program_frame);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
	//	setThumbTempF(thumbList);
		setXbyTime(thumbList);
		setYbyTemp(thumbList);
		setTimeValue(thumbList);
		
		drawThumb(canvas);
		drawText(canvas);
		drawTime(canvas);
	}
	
	private void drawThumb(Canvas canvas){
		if(thumbList != null && thumbList.size()>0){
			
			for (ProgramBallAuto ball : thumbList) {
				
				canvas.drawCircle(ball.x, ball.yHeat, ball.width, thumbHeatPaint);
				canvas.drawCircle(ball.x, ball.yCool, ball.width, thumbCoolPaint);
			}
		}
		
		if(isTempShow){

				canvas.drawCircle(touchBall.x, touchBall.yCool-TEMP_CIRCLE_SPACE, TEMP_CIECLE_WIDTH, tempPaint);
		}
		
	}
	
	private void drawText(Canvas canvas){
		
		if(thumbList != null && thumbList.size()>0){
			
			for (ProgramBallAuto ball : thumbList) {
				if(isTempUnitC){
					canvas.drawText(ball.tempHeat, ball.x, ball.yHeat+ball.width/4, textPaint);
					canvas.drawText(ball.tempCool, ball.x, ball.yCool+ball.width/4, textPaint);
				}else{
					canvas.drawText(ball.tempHeatF, ball.x, ball.yHeat+ball.width/4, textPaint);
					canvas.drawText(ball.tempCoolF, ball.x, ball.yCool+ball.width/4, textPaint);
				}
			}
		}
		
		if(isTempShow){
			if(isTempUnitC){
				if(isTouchHeat){
					canvas.drawText(touchBall.tempHeat, touchBall.x, touchBall.yCool-TEMP_CIRCLE_SPACE+TEMP_CIECLE_WIDTH/4, textPaint);
				}else{
					canvas.drawText(touchBall.tempCool, touchBall.x, touchBall.yCool-TEMP_CIRCLE_SPACE+TEMP_CIECLE_WIDTH/4, textPaint);
				}
			}else{
				if(isTouchHeat){
					canvas.drawText(touchBall.tempHeatF, touchBall.x, touchBall.yCool-TEMP_CIRCLE_SPACE+TEMP_CIECLE_WIDTH/4, textPaint);
				}else{
					canvas.drawText(touchBall.tempCoolF, touchBall.x, touchBall.yCool-TEMP_CIRCLE_SPACE+TEMP_CIECLE_WIDTH/4, textPaint);
				}
			}
		}
		
	}
	
	private void drawTime(Canvas canvas){
		
		if(thumbList != null && thumbList.size()>0){
			for (ProgramBallAuto ball : thumbList){
				canvas.drawText(ball.timeValue+"", ball.x, ball.yCool-ball.width, textPaint);
			}
			if(isTimeShow){
				canvas.drawBitmap(timeBitmap, touchBall.x - timeBitmap.getWidth()/2, touchBall.yCool-TEMP_CIRCLE_SPACE, null);
				canvas.drawText(touchBall.timeValue+"", touchBall.x, touchBall.yCool-TEMP_CIRCLE_SPACE+timeBitmap.getHeight()/2, timePaint);
			}
		}
		
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
		// width:1109  height:636
		Log.i(TAG, "width:"+width+"-height:"+height);

		initMaxXY();
	}

	private void initMaxXY(){
		minY = height/10;
		maxY = height - height/6;
		Log.i(TAG, "minY:"+minY+"-maxY:"+maxY);

		minX = width/26;
		maxX = width - width/26;
		Log.i(TAG, "minX:"+minX+"-maxX:"+maxX);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	private ProgramBallAuto getCurBall(float x,float y){
		ProgramBallAuto curBall = null;
		for (ProgramBallAuto ball : thumbList) {
			
			if (x > (ball.x-ball.width) && y > (ball.yHeat-ball.width) && x < (ball.x + ball.width*2)
					&& y < (ball.yHeat + ball.width*2)){
				isTouchHeat = true;
				curBall =  ball;
			}
			if(x > (ball.x-ball.width) && y > (ball.yCool-ball.width) && x < (ball.x + ball.width*2)
							&& y < (ball.yCool + ball.width*2)){
				isTouchHeat = false;
				curBall =  ball;
			}
			
		}
		
		return curBall;
	}
	
	
	private void setThumbTemp(List<ProgramBallAuto> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){
			
			for (ProgramBallAuto ball : mThumbList) {
				double dHeat =(double)((maxY - ball.yHeat)/(maxY - minY)*(maxTemp - minTemp)+minTemp);
				double dCool=(double)((maxY - ball.yCool)/(maxY - minY)*(maxTemp - minTemp)+minTemp);

				dHeat = foamatTemp(dHeat);
				dCool = foamatTemp(dCool);
				ball.tempHeat = Math.round(dHeat) + "";
				ball.tempCool = Math.round(dCool) + "";
				long tempHeatF = (Math.round(dHeat*1.8+32));
				long tempCoolF = (Math.round(dCool*1.8+32));
				if(tempHeatF < 50){
					tempHeatF = 50;
				}
				if(tempCoolF < 50){
					tempCoolF = 50;
				}
				ball.tempHeatF = tempHeatF +"";
				ball.tempCoolF = tempCoolF +"";

			}
		}
	}

	private double foamatTemp(double dTemp){
		BigDecimal bd = new BigDecimal(dTemp);
		bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
		String temp = TempFormat(bd.toString());
		double tempInt = Double.valueOf(temp);
		if(tempInt < 10){
			tempInt = 10;
		}
		return  tempInt;
	}
	
	private void setThumbTempF(List<ProgramBallAuto> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){
			
			for (ProgramBallAuto ball : mThumbList) {
				 ball.tempHeatF = (int)(Math.ceil(Float.parseFloat(ball.tempHeat)*1.8+32))+"";
				 ball.tempCoolF = (int)(Math.ceil(Float.parseFloat(ball.tempCool)*1.8+32))+"";
			}
		}
	}
	
	private void setThumbTime(List<ProgramBallAuto> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){
			
			for (ProgramBallAuto ball : mThumbList) {
				ball.time = Math.round((ball.x - minX)/(maxX - minX)*96) +"";
			}
		}
	}
	
	private void setYbyTemp(List<ProgramBallAuto> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){
			
			for (ProgramBallAuto ball : mThumbList) {
				if(maxTemp == 32){
					float tempHeat = Float.parseFloat(ball.tempHeat);
					ball.yHeat = maxY - (tempHeat - minTemp)/(maxTemp - minTemp)*(maxY - minY);
					float tempCool = Float.parseFloat(ball.tempCool);
					ball.yCool = maxY - (tempCool - minTemp)/(maxTemp - minTemp)*(maxY - minY);
				}else{
					float tempHeat = Float.parseFloat(ball.tempHeatF);
					ball.yHeat = maxY - (tempHeat - minTemp)/(maxTemp - minTemp)*(maxY - minY);
					float tempCool = Float.parseFloat(ball.tempCoolF);
					ball.yCool = maxY - (tempCool - minTemp)/(maxTemp - minTemp)*(maxY - minY);
				}
			}
		}
	}
	
	private void setXbyTime(List<ProgramBallAuto> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){
			
			for (ProgramBallAuto ball : mThumbList) {
				float time = Float.parseFloat(ball.time);
				ball.x = (time/96) * (maxX - minX) + minX;
			}
		}
	}

	
	private void setTimeValue(List<ProgramBallAuto> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){
			
			for (ProgramBallAuto ball : mThumbList) {
				
				String hours = (int)(Float.parseFloat(ball.time)/4)+"";
				String mins =  (Math.round(Float.parseFloat(ball.time))%4)*15 +"";
				mins = StringUtil.appendLeft(mins, 2, '0');
				ball.timeValue = hours + " : " +mins;
			}
		}
	}
	
	//屏幕按下时 点的X Y 值
	float dX ;
	float dY ;
	//防止单向拖动时 方向发生变化
	float dX1 ;
	float dY1 ;
		
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		float tX = event.getX();
		float tY = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			dX = event.getX();
			dY = event.getY();
			touchBall = getCurBall(tX, tY);
			dX1 = dX;
			dY1 = dY;
			break;
			
		case MotionEvent.ACTION_MOVE:
			
			if (tX > (maxX) || tY > (maxY)
					|| tX < minX|| tY < minY) {
				break;
			}
			if(touchBall != null){
				isBallChanged = true;
				mMoveChanged.onMoveChanged();
				float mX = event.getX();
				float mY = event.getY();
				
				float disX = mX - dX1;
				float disY = mY - dY1;
				
				if(Math.abs(disY) > Math.abs(disX)){
					isTempShow = true;
					isTimeShow = false;
					dX1 = mX;
					touchBall.x = dX;
					//判断调节制热还是制冷
					if(isTouchHeat){
						touchBall.yHeat = mY;
						if(touchBall.yHeat <= (touchBall.yCool+65)){
							touchBall.yCool = touchBall.yHeat - 65;
						}
						if(touchBall.yCool < minY){
							touchBall.yCool = minY;
							if(touchBall.yHeat < (touchBall.yCool+65)){
								touchBall.yHeat = (touchBall.yCool+65);
							}
						}
					}else{
						touchBall.yCool = mY;
						if(touchBall.yCool >= (touchBall.yHeat-65)){
							touchBall.yHeat = touchBall.yCool + 65;
						}
						if(touchBall.yHeat > maxY){
							touchBall.yHeat = maxY;
							if(touchBall.yCool > (touchBall.yHeat-65)){
								touchBall.yCool = (touchBall.yHeat-65);
							}
						}
					}
					
					Log.i(TAG, "x1:"+touchBall.x +"y1:"+touchBall.yHeat);
				}else{
					isTempShow = false;
					isTimeShow = true;
					dY1 = mY;
					touchBall.x = mX;
					if(isTouchHeat){
						touchBall.yHeat = dY;
					}else{
						touchBall.yCool = dY;
					}
					
					Log.i(TAG, "x2:"+touchBall.x +"y2:"+touchBall.yHeat);
				}
				
				int position = thumbList.indexOf(touchBall);
				Log.i(TAG, "position:"+position+"-thumbList:"+thumbList.size());
				// 设置当前拖动球 不能超过前一个
				if(position != 0){
					ProgramBallAuto ball1 = thumbList.get(position-1);
					if(touchBall.x <= (ball1.x+40)){
						touchBall.x = ball1.x + 40;
					}
				}
				// 设置当前拖动球不能超过后一个
				if(position != (thumbList.size()-1)){
					ProgramBallAuto ball1 = thumbList.get(position+1);
					if(touchBall.x >= (ball1.x-40)){
						touchBall.x = ball1.x - 40;
					}
				}
				
				setThumbTemp(thumbList);
				setThumbTime(thumbList);
				setTimeValue(thumbList);
				
				Log.i(TAG, "x:"+touchBall.x +"y:"+touchBall.yHeat);
				Log.i(TAG, "time:"+touchBall.time);
				Log.i(TAG, "timeValue:"+touchBall.timeValue);
			}
			
			break;
			
		case MotionEvent.ACTION_UP:
			isTempShow = false;
			isTimeShow = false;
			touchBall = null;
			break;

		}
		
		invalidate();
		return true;
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

}
