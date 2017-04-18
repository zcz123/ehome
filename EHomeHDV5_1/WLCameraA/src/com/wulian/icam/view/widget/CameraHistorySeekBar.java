/**
 * Project Name:  CameraHistorySeekBar
 * File Name:     CameraHistorySeekBar.java
 * Package Name:  com.wulian.icam.view.widget
 * @Date:         2016年2月22日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.icam.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

import com.wulian.icam.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CameraHistorySeekBar extends View {

	private final static int LITTLE_SCALE_FLAG = 1;
	private final static int MIDDLE_SCALE_FLAG = 2;
	private final static int LARGE_SCALE_FLAG = 4;
	public final static int LOW_SENSIBILITY = 5;// 低灵敏度度
	public final static int NORMAL_SENSIBILITY = 3;// 一般
	public final static int HIGH_SENSIBILITY = 1;// 高灵敏度

	private static final int DEFAULT_SECONDS_PER_MIN = 60;// 默认最小的缩放比例
	private static final float DEFAULT_MIN_SCALE = 1;// 默认最小的缩放比例
	private static final int DEFAULT_LINE_WIDTH = 1;// 默认线条的宽度dip
	private static final int DEFAULT_MID_LINE_WIDTH = 1;// 默认中间线条的宽度dip
	private static final int DEFAULT_MIN_UNIT_WIDTH = 1;// 默认最小间距的宽度dip
	private static final int DEFAULT_TRIANGLE_LINE_WIDTH = 15;// 默认三角形的长度dip
	private static final int DEFAULT_MIN_LINE_HEIGHT = 20;// 默认最短线条的高度dip
	private static final boolean DEFAULT_SIDES = true;// 选择对立布局默认是两边
	private static final int DEFAULT_LINE_COLOR = 0xFF000000;// 默认画线的颜色
	private static final int DEFAULT_TEXT_COLOR = 0xFF000000;// 默认文字的颜色
	private static final float DEFAULT_CALC_VALUE = (float) Math.sqrt(3);// 默认最小的缩放比例

	private static final int DEFAULT_WIDTH = 400;// 默认宽度dip
	private static final int DEFAULT_HEIGHT = 400;// 默认高度dip
	private static final int DEFAULT_TEXT_FONT_SIZE = 10;// 默认字体sp

	private static final int DEFAULT_LEFT_HAS_RECORDED_COLOR = 0xFF2D8A25;// 深绿
	private static final int DEFAULT_LEFT_SELECTED_CHANGE_COLOR = 0xFFFBC1BB;// 橘红
	private static final int MID_LINE_COLOR = 0xFF57BF4B;// 绿色

	// 以中间线为基准即mMinLineHeight
	private static final float DEFAULT_LINE_SHORT_SCALE = 3 / (float) 4;// 短线的缩放比例
	private static final float DEFAULT_LINE_LONG_SCALE = 2;// 长线的缩放比例

	private static final int SCALE_STATE_MESSAGE = 1;// 缩放状态消息
	private static final int SCALE_STATE_MESSAGE_DELAY_MILLS = 500;// 缩放状态消息延迟

	private ScaleGestureDetector mScaleGestureDetector = null;
	private Paint mPaint;
	private Paint mTextPaint;
	private Calendar mCalendar;

	private List<Pair<Integer, Integer>> mRecordList;
	private int mRecordListSize;
	private HistroySeekChangeListener mHistroySeekChangeListener;

	private int mTotalWidth;// View的宽度
	private int mTotalHeight;// View的高度
	private float mHalfWidth;// 一半宽度
	private int mScaledTouchSlop;

	private float mLineWidth;// 线宽
	private float mMidLineWidth;// 中间线宽
	private float mTriangleLineWidth;// 三角形线宽
	private float mMinUnitWidth;// 最小间距的宽度
	private float mMinLineHeight;// 最短线条的高度
	private boolean mIsTwoSides;// 选择对立布局
	private int mLineColor;// 画线的颜色
	private int mTextColor;// 文字的颜色
	private float mTextSize;// 文字的大小
	private int mLeftSelectedChangeColor;// 左边选择的变化颜色
	private int mLeftHasRecordedColor;// 左边已经录制的颜色
	private int mMidLineColor;// 中间线的颜色

	private long mLastActionUpTimeStamp;// 最后一次触摸提起时间
	private long mCurrentMidTimeStamp;// 当前中间线时间戳
	private SCALE_TYPE mCurrentScaleType;// 当前缩放状态
	private int mCurrentSensibility = HIGH_SENSIBILITY;// 当前灵敏度
	private float mCurrentScaleNum;// 当前缩放比例
	private Path mMidPath;// 中间线条的Path
	private RectF mRect;

	private static boolean mIsScaleState;
	private boolean isMidRecord = false;
	private boolean mIsActionEnable = true;
	private int mLastPointerCount = 0;
	private boolean mIsCanDrag;
	private float mLastX;
	private FINGER_STATE mFingerState = FINGER_STATE.ACTION_NONE;

	enum FINGER_STATE {
		ACTION_NONE, ACTION_DOWN, ACTION_MOVE, ACTION_UP, ACTION_SCALE
	}

	public enum SCALE_TYPE {
		LITTLE(LITTLE_SCALE_FLAG, 1, 4), // 最小缩放类型
		MIDDLE(MIDDLE_SCALE_FLAG, 2, 4), // 中间缩放类型
		LARGE(LARGE_SCALE_FLAG, 24, 4);// 最大缩放类型

		private int mFlag;// 标记
		private int mMins;// 最小间隔的分钟数
		private int mScaleNum;// 缩放比例

		SCALE_TYPE(int flag, int minValue, int scaleNum) {
			this.mFlag = flag;
			this.mMins = minValue;
			this.mScaleNum = scaleNum;
		}

		public boolean isLittle() {
			return this.mFlag == LITTLE_SCALE_FLAG;
		}

		public boolean isLarge() {
			return this.mFlag == LARGE_SCALE_FLAG;
		}

		public int getMins() {
			return mMins;
		}

		public int getScaleNum() {
			return mScaleNum;
		}

		public int getFlag() {
			return mFlag;
		}

		public static SCALE_TYPE getScaleTypeByFlag(int flag) {
			for (SCALE_TYPE item : SCALE_TYPE.values()) {
				if (item.getFlag() == flag) {
					return item;
				}
			}
			return null;
		}
	}

	public CameraHistorySeekBar(Context paramContext) {
		this(paramContext, null);
		initData();
	}

	public CameraHistorySeekBar(Context paramContext,
								AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet, 0);
		initData();
	}

	public CameraHistorySeekBar(Context context, AttributeSet attributeSet,
								int defStyleAttr) {
		super(context, attributeSet, defStyleAttr);
		mLineWidth = dip2px(getContext(), DEFAULT_LINE_WIDTH);
		mMidLineWidth = dip2px(getContext(), DEFAULT_MID_LINE_WIDTH);
		mTriangleLineWidth = dip2px(getContext(), DEFAULT_TRIANGLE_LINE_WIDTH);
		mMinUnitWidth = dip2px(getContext(), DEFAULT_MIN_UNIT_WIDTH);
		mMinLineHeight = dip2px(getContext(), DEFAULT_MIN_LINE_HEIGHT);
		mTextSize = dipFont2px(getContext(), DEFAULT_TEXT_FONT_SIZE);

		TypedArray typedArray = context.obtainStyledAttributes(attributeSet,
				R.styleable.Record_Ruler, defStyleAttr, 0);
		mMinUnitWidth = typedArray.getDimension(
				R.styleable.Record_Ruler_min_unit_width, mMinUnitWidth);
		mMinLineHeight = typedArray.getDimension(
				R.styleable.Record_Ruler_min_line_height, mMinLineHeight);
		mIsTwoSides = typedArray.getBoolean(R.styleable.Record_Ruler_sides,
				DEFAULT_SIDES);

		mLineColor = typedArray.getColor(R.styleable.Record_Ruler_line_color,
				DEFAULT_LINE_COLOR);
		mTextSize = typedArray.getDimension(R.styleable.Record_Ruler_text_size,
				mTextSize);
		mLineWidth = typedArray.getDimension(
				R.styleable.Record_Ruler_line_width, mLineWidth);
		mMidLineWidth = typedArray.getDimension(
				R.styleable.Record_Ruler_mid_line_width, mMidLineWidth);
		mTriangleLineWidth = typedArray.getDimension(
				R.styleable.Record_Ruler_triangle_line_width,
				mTriangleLineWidth);
		mTriangleLineWidth /= 2;
		mTextColor = typedArray.getColor(R.styleable.Record_Ruler_text_color,
				DEFAULT_TEXT_COLOR);
		mLeftSelectedChangeColor = typedArray.getColor(
				R.styleable.Record_Ruler_left_selected_change_color,
				DEFAULT_LEFT_SELECTED_CHANGE_COLOR);
		mLeftHasRecordedColor = typedArray.getColor(
				R.styleable.Record_Ruler_left_has_recorded_color,
				DEFAULT_LEFT_HAS_RECORDED_COLOR);
		mMidLineColor = typedArray.getColor(
				R.styleable.Record_Ruler_mid_line_color, MID_LINE_COLOR);
		mCurrentSensibility = typedArray.getInt(
				R.styleable.Record_Ruler_sensibility, NORMAL_SENSIBILITY);
		typedArray.recycle();

		initData();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width = 0, height = 0;
		int minWidth = getPaddingLeft() + getPaddingRight()
				+ getSuggestedMinimumWidth();
		int minHeight = getPaddingBottom() + getPaddingTop()
				+ getSuggestedMinimumHeight();
		switch (widthMode) {
		case MeasureSpec.AT_MOST:// wrap_content
			width = Math.max(widthSize, minWidth);
			break;
		case MeasureSpec.EXACTLY:// 确定值
			width = Math.max(widthSize, minWidth);
			break;
		case MeasureSpec.UNSPECIFIED:// 任意大
			width = Math.max(widthSize, DEFAULT_WIDTH);
			break;
		}
		switch (heightMode) {
		case MeasureSpec.AT_MOST:
			height = Math.max(heightSize, minHeight);
			break;
		case MeasureSpec.EXACTLY:
			height = Math.max(heightSize, minHeight);
			break;
		case MeasureSpec.UNSPECIFIED:
			height = Math.max(heightSize, DEFAULT_HEIGHT);
			break;
		}
		mTotalWidth = width;
		mTotalHeight = height;
		mHalfWidth = mTotalWidth / 2f;
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != oldw || h != oldh) {
			mTotalWidth = w;
			mTotalHeight = h;
			mHalfWidth = w / 2f;
		}
	}

	private void initData() {
		mCalendar = Calendar.getInstance(Locale.getDefault());

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		mRect = new RectF();
		mMidPath = new Path();

		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Paint.Align.LEFT);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(mTextColor);

		mHistroySeekChangeListener = null;
		mRecordList = new ArrayList<Pair<Integer, Integer>>();
		mRecordListSize = 0;

		mScaledTouchSlop = ViewConfiguration.get(getContext())
				.getScaledTouchSlop();
		mScaleGestureDetector = new ScaleGestureDetector(getContext(),
				new FingerScaleGestureListener());
		mCurrentScaleType = SCALE_TYPE.MIDDLE;
		mCurrentScaleNum = DEFAULT_MIN_SCALE;
		mIsScaleState = false;
		mLastActionUpTimeStamp = -1;
		mCurrentMidTimeStamp = 1456400730;// System.currentTimeMillis() / 1000;
	}

	public void setHistroySeekChangeListener(HistroySeekChangeListener listener) {
		mHistroySeekChangeListener = listener;
	}

	public void setMidTimeStamp(long time) {
		mCurrentMidTimeStamp = time;
		invalidate();
	}

	public void setRecordList(List<Pair<Integer, Integer>> list) {
		mRecordList.clear();
		if (list != null) {
			mRecordList.addAll(list);
			mRecordListSize = mRecordList.size();
		} else {
			mRecordListSize = 0;
		}
		invalidate();
	}

	public void setActionEnable(boolean isActionEnable) {
		mIsActionEnable = isActionEnable;
	}

	// 默认是最后
	public void addRecordPair(Pair<Integer, Integer> value) {
		mRecordList.add(value);
		mRecordListSize = mRecordList.size();
	}

	public long getTimeStamp() {
		return this.mCurrentMidTimeStamp;
	}

	public boolean getIsMidRecord() {
		return this.isMidRecord;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!isEnabled()) {
			return;
		}
		int perUnitSecondS = (int) (mCurrentScaleType.getMins() * DEFAULT_SECONDS_PER_MIN);
		float perUnitSize = mCurrentScaleNum * mMinUnitWidth;
		int midLeftRemainTimeStamp = (int) mCurrentMidTimeStamp
				% perUnitSecondS;
		int paddingTop = getPaddingTop();
		float midLeftFirstStart = 0;
		if (midLeftRemainTimeStamp == 0) {
			midLeftFirstStart = mLineWidth / 2f;
		} else {
			midLeftFirstStart = (midLeftRemainTimeStamp / (float) perUnitSecondS)
					* perUnitSize + mLineWidth;
		}
		int leftCount = (int) Math.ceil((mHalfWidth - midLeftFirstStart)
				/ (float) (perUnitSize + mLineWidth));
		float currentOffset = mHalfWidth - midLeftFirstStart - leftCount
				* (perUnitSize + mLineWidth);
		long currentTimeStamp = mCurrentMidTimeStamp - midLeftRemainTimeStamp
				- leftCount * perUnitSecondS;
		long lastTimeStamp = (long) (currentTimeStamp + (mTotalWidth - currentOffset)
				* perUnitSecondS / (float) (perUnitSize + mLineWidth));

		mPaint.setStrokeWidth(mLineWidth);

		// mPaint.setColor(mLeftSelectedColor);
		// mRect.set(0, paddingTop, mHalfWidth, mTotalHeight + paddingTop);
		// canvas.drawRect(mRect, mPaint);

		mPaint.setColor(mLineColor);
		canvas.drawLine(0, 0, mTotalWidth, 0, mPaint);// 最上面的线条
		if (mIsTwoSides) {
			canvas.drawLine(0, paddingTop + mTotalHeight, mTotalWidth,
					paddingTop + mTotalHeight, mPaint);// 最下面的线条
		}

		mPaint.setColor(mLeftHasRecordedColor);

		if (mRecordListSize > 0) {
			isMidRecord = false;
			for (int i = 0; i < mRecordListSize; i++) {
				Pair<Integer, Integer> pair = mRecordList.get(i);
				if (pair.first >= lastTimeStamp) {
					break;
				}
				if (mCurrentMidTimeStamp >= pair.first
						&& mCurrentMidTimeStamp <= pair.second) {
					isMidRecord = true;
				}
				if (pair.first <= lastTimeStamp
						&& pair.second > currentTimeStamp) {
					float startX = currentOffset
							+ (pair.first - currentTimeStamp)
							* (float) (perUnitSize + mLineWidth)
							/ perUnitSecondS;
					float endX = 0;
					if (pair.second >= lastTimeStamp) {
						endX = mTotalWidth;
					} else {
						endX = currentOffset + (pair.second - currentTimeStamp)
								* (float) (perUnitSize + mLineWidth)
								/ perUnitSecondS;
					}
					mRect.set(startX, paddingTop, endX, mTotalHeight
							+ paddingTop);
					canvas.drawRect(mRect, mPaint);
				}
			}
		}
		mPaint.setColor(mLineColor);
		while (currentOffset <= mTotalWidth) {
			mCalendar.setTimeInMillis(currentTimeStamp * 1000);
			int hours = mCalendar.get(Calendar.HOUR_OF_DAY);
			int mines = mCalendar.get(Calendar.MINUTE);
			int allMinsBy24Hours = (int) ((hours * DEFAULT_SECONDS_PER_MIN + mines) / mCurrentScaleType
					.getMins());
			int remainderBy6 = allMinsBy24Hours % 6;
			int remainderBy5 = allMinsBy24Hours % 5;
			if (remainderBy6 == 0 && remainderBy5 == 0) {
				canvas.drawLine(currentOffset, paddingTop, currentOffset,
						paddingTop + mMinLineHeight * DEFAULT_LINE_LONG_SCALE,
						mPaint);
				if (mIsTwoSides) {
					canvas.drawLine(currentOffset, mTotalHeight + paddingTop
							- mMinLineHeight * DEFAULT_LINE_LONG_SCALE,
							currentOffset, mTotalHeight + paddingTop
									* DEFAULT_LINE_LONG_SCALE, mPaint);
				}
				String text = "" + (hours < 10 ? "0" + hours : hours) + ":"
						+ (mines < 10 ? "0" + mines : mines);
				canvas.drawText(text, currentOffset + 5, paddingTop
						+ mMinLineHeight * DEFAULT_LINE_LONG_SCALE, mTextPaint);
			} else if (remainderBy6 != 0 && remainderBy5 == 0) {
				canvas.drawLine(currentOffset, paddingTop, currentOffset,
						paddingTop + mMinLineHeight, mPaint);
				if (mIsTwoSides) {
					canvas.drawLine(currentOffset, mTotalHeight + paddingTop
							- mMinLineHeight, currentOffset, paddingTop
							+ mTotalHeight, mPaint);
				}
			} else {
				canvas.drawLine(currentOffset, paddingTop, currentOffset,
						paddingTop + mMinLineHeight * DEFAULT_LINE_SHORT_SCALE,
						mPaint);
				if (mIsTwoSides) {
					canvas.drawLine(currentOffset, mTotalHeight + paddingTop
							- mMinLineHeight * DEFAULT_LINE_SHORT_SCALE,
							currentOffset, paddingTop + mTotalHeight, mPaint);
				}
			}

			currentTimeStamp += perUnitSecondS;
			currentOffset += (mLineWidth + perUnitSize);
		}

		mPaint.setColor(mMidLineColor);
		mMidPath.reset();
		mMidPath.moveTo(mHalfWidth - mTriangleLineWidth, paddingTop);
		mMidPath.lineTo(mHalfWidth + mTriangleLineWidth, paddingTop);
		mMidPath.lineTo(mHalfWidth, DEFAULT_CALC_VALUE * mTriangleLineWidth
				+ paddingTop);
		mMidPath.close();
		canvas.drawPath(mMidPath, mPaint);
		mMidPath.reset();
		mMidPath.moveTo(mHalfWidth - mTriangleLineWidth, paddingTop
				+ mTotalHeight);
		mMidPath.lineTo(mHalfWidth + mTriangleLineWidth, paddingTop
				+ mTotalHeight);
		mMidPath.lineTo(mHalfWidth, paddingTop + mTotalHeight
				- DEFAULT_CALC_VALUE * mTriangleLineWidth);
		mMidPath.close();
		canvas.drawPath(mMidPath, mPaint);

		mPaint.setStrokeWidth(mMidLineWidth);
		canvas.drawLine(mHalfWidth, paddingTop, mHalfWidth, paddingTop
				+ mTotalHeight, mPaint);
	}

	public class FingerScaleGestureListener implements
			ScaleGestureDetector.OnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			// 缩放比例
			float scale = detector.getScaleFactor();
			// float currentScale = (float) (mCurrentScaleNum * (1 + (scale - 1)
			// / (mCurrentSensibility * mCurrentSensibility)));
			float currentScale = (float) (mCurrentScaleNum * (1 + (scale - 1)
					/ (2 * 2)));
			if (scale > 1.0) {// 放大
				if (currentScale > mCurrentScaleType.getScaleNum()) {
					if (!mCurrentScaleType.isLittle()) {
						mCurrentScaleType = SCALE_TYPE
								.getScaleTypeByFlag(mCurrentScaleType.getFlag() >> 1);
						mCurrentScaleNum = DEFAULT_MIN_SCALE;
						invalidate();
					} else if (mCurrentScaleNum != mCurrentScaleType
							.getScaleNum()) {
						mCurrentScaleNum = mCurrentScaleType.getScaleNum();
						invalidate();
					}
				} else {
					mCurrentScaleNum = currentScale;
					invalidate();
				}
			} else if (scale < DEFAULT_MIN_SCALE) {// 缩小
				if (currentScale < 1) {
					if (!mCurrentScaleType.isLarge()) {
						mCurrentScaleType = SCALE_TYPE
								.getScaleTypeByFlag(mCurrentScaleType.getFlag() << 1);
						mCurrentScaleNum = mCurrentScaleType.getScaleNum();
						invalidate();
					} else if (mCurrentScaleNum != mCurrentScaleType
							.getScaleNum()) {
						mCurrentScaleNum = DEFAULT_MIN_SCALE;
						invalidate();
					}
				} else {
					mCurrentScaleNum = currentScale;
					invalidate();
				}
			}
			return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mIsScaleState = true;
			mHandler.removeMessages(SCALE_STATE_MESSAGE);
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			mHandler.sendEmptyMessageDelayed(SCALE_STATE_MESSAGE,
					SCALE_STATE_MESSAGE_DELAY_MILLS);
		}
	}

	static Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCALE_STATE_MESSAGE:
				mIsScaleState = false;
				break;
			default:
				break;
			}
		};
	};

	private boolean isCanDrag(float dx) {
		return Math.abs(dx) >= mScaledTouchSlop;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return true;
		}
		mScaleGestureDetector.onTouchEvent(event);
		float eventX = 0;
		final int pointerCount = event.getPointerCount();
		for (int i = 0; i < pointerCount; i++) {
			eventX += event.getX(i);
		}
		eventX = eventX / pointerCount;
		if (pointerCount != mLastPointerCount) {
			mIsCanDrag = false;
			mLastX = eventX;
		}
		mLastPointerCount = pointerCount;
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_UP:
			mLastPointerCount = 0;
			if (mFingerState == FINGER_STATE.ACTION_MOVE) {
				if (mHistroySeekChangeListener != null && mIsActionEnable) {
//					Log.d("PML", "onChangeSeekBarFinalAction is:"
//							+ mCurrentMidTimeStamp);
					mHistroySeekChangeListener.onChangeSeekBarFinalAction(
							mCurrentMidTimeStamp, isMidRecord);
				}
				mLastActionUpTimeStamp = mCurrentMidTimeStamp;
			}
			mFingerState = FINGER_STATE.ACTION_NONE;
			break;
		case MotionEvent.ACTION_CANCEL:
			mLastPointerCount = 0;
			break;
		case MotionEvent.ACTION_DOWN:
			mFingerState = FINGER_STATE.ACTION_DOWN;
			if (mHistroySeekChangeListener != null && mIsActionEnable) {
				mHistroySeekChangeListener.onActionDownMessage();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getPointerCount() == 1 && !mIsScaleState) {
				float dx = eventX - mLastX;
				if (!mIsCanDrag) {
					mIsCanDrag = isCanDrag(dx);
				}
				if (mIsCanDrag) {
					mFingerState = FINGER_STATE.ACTION_MOVE;
					int timeStampOffset = (int) (mCurrentScaleType.getMins()
							* DEFAULT_SECONDS_PER_MIN * ((dx) / (mCurrentScaleNum * (mMinUnitWidth + mLineWidth))));

					mCurrentMidTimeStamp = mCurrentMidTimeStamp
							- mCurrentMidTimeStamp % DEFAULT_SECONDS_PER_MIN;

					long tempTimeStamp = (long) (DEFAULT_SECONDS_PER_MIN * Math
							.round(timeStampOffset
									/ (float) DEFAULT_SECONDS_PER_MIN));
					tempTimeStamp = mCurrentMidTimeStamp - tempTimeStamp;
					if (tempTimeStamp != mCurrentMidTimeStamp) {
						mCurrentMidTimeStamp = tempTimeStamp;
						if (mHistroySeekChangeListener != null
								&& mIsActionEnable) {
//							Log.d("PML", "onChangeSeekBarTempAction is:"
//									+ mCurrentMidTimeStamp);
							mHistroySeekChangeListener
									.onChangeSeekBarTempAction(mCurrentMidTimeStamp);
						}
						invalidate();
						mLastX = eventX;
					}
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	public static int dip2px(Context context, float dp) {
		return (int) (context.getResources().getDisplayMetrics().density * dp);
	}

	public static int dipFont2px(Context context, float dp) {
		return (int) (context.getResources().getDisplayMetrics().scaledDensity * dp);
	}

	public interface HistroySeekChangeListener {
		public void onChangeSeekBarFinalAction(long timeStamp, boolean isRecord);

		public void onActionDownMessage();

		public void onChangeSeekBarTempAction(long timeStamp);
	}
}
