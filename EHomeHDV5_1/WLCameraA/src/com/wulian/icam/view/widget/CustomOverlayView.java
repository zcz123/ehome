/**
 * Project Name:  iCam
 * File Name:     CustomOverlayView.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2014年11月19日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.widget;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.wulian.icam.R;
import com.wulian.icam.model.MonitorArea;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * @ClassName: CustomOverlayView
 * @Function: 自定义的区域框选择器
 * @Date: 2014年11月19日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class CustomOverlayView extends View implements OnGestureListener,
		OnDoubleTapListener {
	private Context context;
	Paint paint;
	GestureDetector mGestureDetector;
	Point startPoint = new Point(0, 0);
	Point tempStartPoint = new Point(0, 0);// 牛逼变量：起点在框外，值为startPoint,保持不变；起点在框内，随着endPoint变化
	Point endPoint = new Point(0, 0);
	Point oldEndPoint = new Point(0, 0);
	private final int CIRCLR_RADIOUS = 18;

	boolean isStopScroll;
	boolean isFirstShow = true;
	boolean isReset = false;
	boolean isRestore = false;

	public static int WIDTH = 320;// 测量宽度
	public static int HEIGHT = 240;// 测量高度

	MonitorArea m1 = new MonitorArea(this, Color.YELLOW);
//	MonitorArea m2 = new MonitorArea(this, Color.RED);
//	MonitorArea m3 = new MonitorArea(this, Color.BLUE);
//	MonitorArea m4 = new MonitorArea(this, Color.GREEN);

	public LinkedList<MonitorArea> mas = new LinkedList<MonitorArea>();

	MonitorArea currentArea = null;

	public CustomOverlayView(Context context, AttributeSet attrs,
			int defStyleAttr) {

		super(context, attrs, defStyleAttr);
		init(context);
	}

	public CustomOverlayView(Context context, AttributeSet attrs) {

		super(context, attrs);
		init(context);
	}

	public CustomOverlayView(Context context) {
		super(context);
		init(context);
	}

	public void init(Context context) {
		this.context = context;
		mas.add(m1);
//		mas.add(m2);
//		mas.add(m3);
//		mas.add(m4);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(5);
		paint.setStyle(Style.FILL);

		mGestureDetector = new GestureDetector(context, this);
		setLongClickable(true);// 这是坑，必须有
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// Utils.sysoInfo("ACTION_DOWN");
					isStopScroll = false;
					isFirstShow = false;
					startPoint.set((int) event.getX(), (int) event.getY());
					oldEndPoint.set((int) event.getX(), (int) event.getY());
					tempStartPoint.set((int) event.getX(), (int) event.getY());

				}
				if (event.getAction() == MotionEvent.ACTION_UP) {

					// Utils.sysoInfo("ACTION_UP");
					isStopScroll = true;

					invalidate();
				}

				return mGestureDetector.onTouchEvent(event);
			}
		});
	}

	/**
	 * @Function 发生碰撞的已初始化点 或者 未初始化的可用点
	 * @author Wangjj
	 * @date 2014年11月19日
	 * @param p
	 * @return
	 */

	public MonitorArea getUsefulArea(Point p) {
		// 一般而言，最后一个即为目标对象，所以从最后遍历，效率更高
		MonitorArea m;
		for (int i = mas.size() - 1; i >= 0; i--) {
			m = mas.get(i);
			if (m.inWhere(p) < m.OUT_BODY || !m.isInit) {
				return m;
			}
		}
		return null;
	}

	public MonitorArea getNextUninitArea() {
		// 一般而言，最后一个即为目标对象，所以从最后遍历，效率更高
		MonitorArea m;
		for (int i = mas.size() - 1; i >= 0; i--) {
			m = mas.get(i);
			if (!m.isInit) {
				return m;
			}
		}
		return null;
	}

	/**
	 * @Function 获取碰撞区域
	 * @author Wangjj
	 * @date 2014年11月19日
	 * @param p
	 * @return
	 */

	public MonitorArea getBumpArea(Point p) {
		// 一般而言，最后一个即为目标对象，所有从最后一遍历，效率更高
		MonitorArea m;
		for (int i = mas.size() - 1; i >= 0; i--) {
			m = mas.get(i);
			if (m.isInit && m.inWhere(p) < m.OUT_BODY) {
				return m;
			}
		}
		return null;
	}

	/**
	 * @Function 获取一个未初始化的区域
	 * @author Wangjj
	 * @date 2014年11月19日
	 * @return
	 */

	public MonitorArea getUnInitArea() {
		for (MonitorArea m : mas) {
			if (!m.isInit) {
				mas.remove(m);
				mas.addLast(m);
				return m;
			}
		}
		return null;
	}

	/**
	 * 
	 * @Function 获取已经初始化的区域个数
	 * @author Wangjj
	 * @date 2014年11月19日
	 * @return
	 */
	private int getInitedAreaCount() {
		int i = 0;
		for (MonitorArea m : mas) {
			i += m.isInit ? 1 : 0;
		}
		return i;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		if (isFirstShow) {
			return;
		}
		if (isRestore) {// 恢复现场，单独处理
			isRestore = false;
			showAllMonitorArea(canvas, paint);
			return;
		}

		if (!isStopScroll) {// 移动过程中
			currentArea = getUsefulArea(endPoint);// 发生碰撞的已初始化点（移动结束时需要切换）
													// 或者
													// 未初始化的可用点=>细化为2个方法?
			if (currentArea != null) {// 可用
				int endResult = currentArea.inWhere(endPoint);
				int tempStartResult = currentArea.inWhere(tempStartPoint);// 框外定值
				// Utils.sysoInfo(currentArea.isInit ? "已经初始化" : "未初始化");
				if (!currentArea.isInit
						|| (endResult == currentArea.OUT_BODY || tempStartResult == currentArea.OUT_BODY)) {// 点在外侧,绘制定位点，不和其他区域检查
					if (getInitedAreaCount() < mas.size()) {// 2点绘制
						// paint.setColor(currentArea.getColorValue());
						paint.setColor(Color.GRAY);
						paint.setStyle(Style.FILL);
						canvas.drawCircle(startPoint.x, startPoint.y,
								CIRCLR_RADIOUS, paint);
						canvas.drawCircle(endPoint.x, endPoint.y,
								CIRCLR_RADIOUS, paint);
						canvas.drawLine(startPoint.x, startPoint.y, endPoint.x,
								endPoint.y, paint);
					}
				} else {// 发生碰撞
					currentArea.offset(endPoint.x - oldEndPoint.x, endPoint.y
							- oldEndPoint.y, endResult);
					currentArea.reDrawArea(canvas, paint);
					oldEndPoint.x = endPoint.x;
					oldEndPoint.y = endPoint.y;

					// 为了确保 tempStartResult ==
					// currentArea.OUT_BODY为false，框内动值
					tempStartPoint.x = endPoint.x;
					tempStartPoint.y = endPoint.y;

					// 提升权限
					// mas.remove(currentArea);
					// mas.addFirst(currentArea);//第一个绘制在最下面
					// mas.addLast(currentArea);

					upPriority(currentArea);

				}
			}// 则currentArea==null,表示在4个区域之外，超过最多个数限制
			else {
				   if(context instanceof BaseFragmentActivity) {
					   ((BaseFragmentActivity)context).showMsg(R.string.protect_out_of_maxarea_limit);	
				   }else {
				   CustomToast.show(context, R.string.protect_out_of_maxarea_limit);
					}
			}
		} else {// 移动结束(手势结束，画布自动清空？)

			if (isReset) {// 调试定位，化繁为简
				isReset = false;
			} else {
				if (currentArea != null) {
					int endResult = currentArea.inWhere(endPoint);
					int tempStartResult = currentArea.inWhere(tempStartPoint);
					// 碰撞了已经初始化的区域&&还有未初始化的区域&&碰撞发生在内部&&起点在外部
					if (currentArea.isInit && getInitedAreaCount() < mas.size()
							&& endResult < currentArea.OUT_BODY
							&& tempStartResult == currentArea.OUT_BODY) {
						currentArea = getUnInitArea();// 将当前已经init的碰撞区域
														// 切换换为未init的
														// 以便将重绘旧的变成init新的
					}
					currentArea.offset(endPoint.x - oldEndPoint.x, endPoint.y
							- oldEndPoint.y, endResult);
					currentArea.initOrRefreshArea(canvas, paint, startPoint,
							endPoint);// 初始化或重绘
					if (currentArea.isInit) {
						// 距离太短时，初始化失败，所以只能对成功的提升权限
						// 否则提升权限的是个未初始化的对象
						upPriority(currentArea);
					}
				}
			}
		}
		showAllMonitorArea(canvas, paint);
	}

	public void upPriority(MonitorArea m) {
		if (mas.getLast() != m) {
			mas.remove(m);
			mas.addLast(m);
		}
	}

	private void showAllMonitorArea(Canvas canvas, Paint paint) {

		for (MonitorArea m : mas) {
			if (m.isInit) {
				paint.setColor(m.getColorValue());
				m.reDrawArea(canvas, paint);
				System.out.println(m.leftTopPoint + "-" + m.rightBottomPoint);
			}
		}
	}

	/**
	 * TODO:获取设置点的最终结果值 ，基于320*240
	 * 
	 * @Function 获取设置点的最终结果值 ，基于320*240
	 * @author Wangjj
	 * @date 2014年11月19日
	 */
	public String[] getPointResult() {
		StringBuilder sb = new StringBuilder();
		for (MonitorArea m : mas) {
			if (m.isInit) {
				sb.append((int) ((float) m.leftTopPoint.x / this.getWidth() * WIDTH)
						+ ","
						+ (int) ((float) m.leftTopPoint.y / this.getHeight() * HEIGHT)
						+ ","
						+ (int) ((float) m.rightBottomPoint.x / this.getWidth() * WIDTH)
						+ ","
						+ (int) ((float) m.rightBottomPoint.y
								/ this.getHeight() * HEIGHT) + ";");
			}
		}
		String result = sb.toString().trim();
		if ("".equals(result)) {//"".split(";").length=1 单独处理
			return new String[] {};
		} else {
			return sb.toString().split(";");
		}
	}

	public String getPointResultString() {
		StringBuilder sb = new StringBuilder();
		for (MonitorArea m : mas) {
			if (m.isInit) {
				sb.append((int) ((float) m.leftTopPoint.x / this.getWidth() * WIDTH)
						+ ","
						+ (int) ((float) m.leftTopPoint.y / this.getHeight() * HEIGHT)
						+ ","
						+ (int) ((float) m.rightBottomPoint.x / this.getWidth() * WIDTH)
						+ ","
						+ (int) ((float) m.rightBottomPoint.y
								/ this.getHeight() * HEIGHT) + ";");
			}
		}
		String result = sb.toString().trim();
		if ("".equals(result)) {//"".split(";").length=1 单独处理
			return ";";
		} else {
			return result;
		}
	}

	/**
	 * 
	 * @Function 恢复监测区域
	 * @author Wangjj
	 * @date 2014年12月26日
	 */
	public void restoreMonitorArea(String[] results) {

		for (int i = 0; i < results.length; i++) {
			String[] pointsValue = results[i].split(",");
			if (pointsValue.length == 4) {
				MonitorArea m = mas.getFirst();
				m.isInit = true;
				m.getpStatic().x = (int) (Float.parseFloat(pointsValue[0])
						/ WIDTH * this.getWidth());
				m.getpStatic().y = (int) (Float.parseFloat(pointsValue[1])
						/ HEIGHT * this.getHeight());
				m.getpMove().x = (int) (Float.parseFloat(pointsValue[2])
						/ WIDTH * this.getWidth());
				m.getpMove().y = (int) (Float.parseFloat(pointsValue[3])
						/ HEIGHT * this.getHeight());
				upPriority(m);
			}
		}
		if (results.length > 0) {
			isFirstShow = false;
			isRestore = true;
			invalidate();
		}
	}

	public void reset() {

		// Utils.sysoInfo("reset");
		for (MonitorArea m : mas) {
			m.reset();
		}
		isReset = true;
		invalidate();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// Utils.sysoInfo("onScroll");
		// e1对应的点不会变化，导致每次的偏移都是基于e1计算！~
		startPoint.x = (int) e1.getX();
		startPoint.y = (int) e1.getY();

		endPoint.x = (int) e2.getX();
		endPoint.y = (int) e2.getY();
		// Utils.sysoInfo("onScroll: " + endPoint);
		invalidate();

		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// Utils.sysoInfo("ondown" + e.getX() + " " + e.getY());
		// oldEndPoint.set((int) e.getX(), (int) e.getY());//已经设置过了

		endPoint.x = (int) e.getX();
		endPoint.y = (int) e.getY();
		invalidate();
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// 双击 取消绘制 => 调用onDown 造成 点击重绘
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// 双击抬起 取消绘制
		if (e.getAction() == MotionEvent.ACTION_UP) {
			MonitorArea m = getBumpArea(new Point((int) e.getX(),
					(int) e.getY()));
			if (m != null) {
				// 颜色值交换给下一个候选，增加体验
				MonitorArea nextM = getNextUninitArea();
				m.isInit = false;
				mas.remove(m);
				mas.addFirst(m);// 权限最低
				if (nextM != null) {
					int firstColor = m.getColorValue();
					int nextColor = nextM.getColorValue();
					nextM.setColorValue(firstColor);
					m.setColorValue(nextColor);
				}
			}
			invalidate();
		}
		return false;
	}

}
