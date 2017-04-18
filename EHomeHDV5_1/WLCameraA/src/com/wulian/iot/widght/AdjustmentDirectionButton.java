package com.wulian.iot.widght;


import com.wulian.icam.R;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class AdjustmentDirectionButton extends View implements OnTouchListener {

	
	//上下文
	 
	private Context context;
	
	//按下时的x坐标
	
	private int downX;
	
	//当前位置
	
	private int lastX;
	
	//手指移动距离
	
	private int dis;
	
	//底层背景
	
	private Bitmap bgBmp;
	
	//上层按钮背景
	
	private Bitmap btnBmp;
	
	//画笔
	
	private Paint paint;
	
	//开关状态
	
	private boolean switchState = false;
	
	//上层按钮距离左边界距离
	
	private int btnLeft = 0;
	
	//最大滑动距离
	
	private int maxDis;
	
	//控件宽
	
	private int mWidth;
	
	//控件高
	
	private int mHeight;
	 
	//控件上按钮的宽
	
	private int mbtWidth;
	
	//按钮状态
	 
	private int btnState = 0;
	
	//检查按钮状态
	
	private int btnCheck = -1;

	public AdjustmentDirectionButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AdjustmentDirectionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;

		init();

		initAttrs(attrs);
	}

	//测量控件大小
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mWidth, mHeight);
	}

	//绘组件
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bgBmp, 0, 0, paint);
		canvas.drawBitmap(btnBmp, btnLeft, 0, paint);
	}

	//初始化
	
	private void init() {
		// 画笔
		paint = new Paint();
		// 抗锯齿
		paint.setAntiAlias(true);
		setOnTouchListener(this);
	}

	/**
	 * 获取xml配置文件中的属性值
	 * 
	 * @param attrs
	 */
	public void initAttrs(AttributeSet attrs) {
		TypedArray te = getContext().obtainStyledAttributes(attrs,
				R.styleable.AdjustmentDirection);

		// 获取xml中图片的id
		int bgId = te.getResourceId(R.styleable.AdjustmentDirection_pool_background, 0);
		int btnbgId = te.getResourceId(R.styleable.AdjustmentDirection_btn_background,
				0);
		// 根据id获取图片
		bgBmp = BitmapFactory.decodeResource(getResources(), bgId);
		btnBmp = BitmapFactory.decodeResource(getResources(), btnbgId);

		// 获取控件尺寸信息
		mWidth = (int) te.getDimension(R.styleable.AdjustmentDirection_mwidth, 0f);
		mHeight = (int) te.getDimension(R.styleable.AdjustmentDirection_mheight, 0f);
		mbtWidth = (int) te.getDimension(R.styleable.AdjustmentDirection_mbt_width, 0f);
		btnLeft = (mWidth-mbtWidth)/2;
		
		// 如果mwidth属性未配置
		if (mWidth == 0f) {
			mWidth = bgBmp.getWidth();
		}

		// 如果mheight属性未配置
		if (mHeight == 0f) {
			mHeight = bgBmp.getHeight();
		}

		// 如果mbt_width属性未配置
		if (mbtWidth == 0f) {
			mbtWidth = btnBmp.getWidth();
		}

		// 按xml中设置的尺寸改变bitmap
		bgBmp = Bitmap.createScaledBitmap(bgBmp, mWidth, mHeight, true);
		btnBmp = Bitmap.createScaledBitmap(btnBmp, mbtWidth, mHeight, true);
		// 计算最大滑动距离
		maxDis = (mWidth - mbtWidth)/2;

		te.recycle();
	}

	/**
	 * 滑动事件
	 */
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		// 按下
		case MotionEvent.ACTION_DOWN: {
			downX = lastX = (int) event.getX();
			Log.i("IOTCamera", "-----------MotionEvent.ACTION_DOWN"+downX+"lastX"+lastX);
			break;
		}
		// 移动
		case MotionEvent.ACTION_MOVE: {
			// 计算手指在屏幕上的移动距离
			dis = (int) (event.getX() - lastX);
			
			// 将本次的位置 设置给lastX
			lastX = (int) event.getX();
			// 根据移动距离计算左边距值
			btnLeft = btnLeft + dis;
			Log.i("IOTCamera", "-----------MotionEvent.ACTION_MOVE"+dis+"lastX"+lastX+"btnLeft"+btnLeft);
			change();

			break;
		}
		// 抬起
		case MotionEvent.ACTION_UP: {
			lastX = (int) event.getX();
			changeState();
			break;
		}
		}
		return true;
	}

	/**
	 * 改变按钮状态
	 */
	public void changeState() {
		btnLeft = maxDis;
		btnState = 0;
		setChange();

		invalidate();

		dis = 0;
	}

	/**
	 * 改变按钮位置
	 */
	public void change() {

		// 如果滑动距离超过最大距离
		if (btnLeft > (mWidth - mbtWidth)) {
			btnLeft = mWidth-mbtWidth;
			btnState = 2;
			setChange();
		}

		if (btnLeft < 0) {
			btnLeft = 0;
			btnState = 1;
			setChange();
		}

		invalidate();
	}
	
	//防止重复监听
	
	public void setChange(){
		if(btnState!=btnCheck){
			if (mListener != null) {
				mListener.onChange(this,btnState);
			}
		}
		btnCheck = btnState;
	}

	//设置监听事件
	private OnChangeListener mListener = null;

	public void setOnChangeListener(OnChangeListener listener) {
		mListener = listener;
	}

	// 监听时的接口
	public interface OnChangeListener {
		public void onChange(AdjustmentDirectionButton mtb,int btnState);
	}

}
