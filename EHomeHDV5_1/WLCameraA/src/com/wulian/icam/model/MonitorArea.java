/**
 * Project Name:  iCam
 * File Name:     MonitorArea.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2014年11月19日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

/**
 * @ClassName: MonitorArea
 * @Function: 区域对象
 * @Date: 2014年11月19日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class MonitorArea {
	public Point leftTopPoint = new Point();// 便于绘制，左上角
	public Point rightBottomPoint = new Point();// 右下角
	private Point pStatic = new Point();// 为了实现随意拖拽变形，增加对象。
	private Point pMove = new Point();

	Rect rect_background = new Rect();// 背景矩形
	Rect rect_stroke = new Rect();// 矩形框
	public int colorValue;
	public int circlrRadius = 18;
	public boolean isInit = false;

	private View parentView;
	// 用于判断
	public final int TOP_LEFT = 0;
	public final int TOP_RIGHT = 1;
	public final int BOTTOM_LEFT = 2;
	public final int BOTTOM_RIGHT = 3;
	public final int CENTER_BODY = 4;

	// int x1, y1, x2, y2;// 取消了左上角和右下角的逻辑限制后，加上坐标值分配变量

	// 用于排序,减少循环判断的时间复杂度
	// 1 1 1 -> 1 1 1
	// 2 2 2 -> 2 1 1
	// 3 3 3 -> 3 1 1
	// 4 4 4 -> 4 1 1
	enum WHERE {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER_BODY;
	}

	public final int OUT_BODY = 5;
	private LinkedList<WHERE> priority = new LinkedList<WHERE>();

	public final int MIN_DELATA = 2 * circlrRadius;// x、y轴最小距离限制，如果小于这个值，则不绘制。

	public MonitorArea(View parent, int colorValue) {
		parentView = parent;
		this.colorValue = colorValue;
		priority.add(WHERE.TOP_LEFT);
		priority.add(WHERE.TOP_RIGHT);
		priority.add(WHERE.BOTTOM_LEFT);
		priority.add(WHERE.BOTTOM_RIGHT);
		priority.add(WHERE.CENTER_BODY);
	}

	/**
	 * 
	 * @Function 检查给定点是否在范围内
	 * @author Wangjj
	 * @date 2014年11月18日
	 * @return
	 */

	public boolean isInBodyArea(Point p) {
		// Utils.sysoInfo(leftTopPoint.x + "-" + rightBottomPoint.x + "-"
		// + leftTopPoint.y + "-" + rightBottomPoint.y);
		// Utils.sysoInfo(p.x + "-" + p.y);
		// int x1 = Math.min(pStatic.x, pMove.x);
		// int y1 = Math.min(pStatic.y, pMove.y);
		// int x2 = Math.max(pStatic.x, pMove.x);
		// int y2 = Math.max(pStatic.y, pMove.y);
		// if (x1 < p.x && p.x < x2 && y1 < p.y && p.y < y2) {
		// return true;
		// }
		// 如果 updateLTRB调用时机不对，leftTopPoint和rightBottomPoint可能被重置
		if (leftTopPoint.x < p.x && p.x < rightBottomPoint.x
				&& leftTopPoint.y < p.y && p.y < rightBottomPoint.y) {
			return true;
		}
		return false;
	}

/**
 * 更新到绘图用的 LEFT_TOP RIGHT_BOTTOM
	 * @Function    使得 'x1'<'x2' 'y1'<'y2'
	 * @author      Wangjj
	 * @date        2014年11月21日
	 */

	public void updateLTRB() {
		// 如果动态点越界，则取消更新最终的绘制坐标
		if (pMove.x < 0 || pMove.y < 0 || pMove.x > parentView.getWidth()
				|| pMove.y > parentView.getHeight())
			return;
		leftTopPoint.x = Math.min(pStatic.x, pMove.x);
		leftTopPoint.y = Math.min(pStatic.y, pMove.y);
		rightBottomPoint.x = Math.max(pStatic.x, pMove.x);
		rightBottomPoint.y = Math.max(pStatic.y, pMove.y);
	}

	// 是否窄边框
	public boolean isSlim() {
		return Math.abs(leftTopPoint.x - rightBottomPoint.x) < 2 * circlrRadius + 5
				|| Math.abs(leftTopPoint.y - rightBottomPoint.y) < 2 * circlrRadius + 5;
	}

	/**
	 * 
	 * @Function 是否位于圆角范围内
	 * @author Wangjj
	 * @date 2014年11月18日
	 * @param p
	 * @return
	 */
	public boolean isInCircleArea(int circleX, int circlrY, Point p) {
		int cx1 = circleX - circlrRadius * 3;
		int cy1 = circlrY - circlrRadius * 3;
		int cx2 = circleX + circlrRadius * 3;
		int cy2 = circlrY + circlrRadius * 3;

		if (cx1 < p.x && p.x < cx2 && cy1 < p.y && p.y < cy2) {
			return true;
		}

		return false;

	}

	/**
	 * 
	 * @Function 判断点位于哪个热区，为了实现随意拖拽效果，热区也要加上动态权限
	 * @author Wangjj
	 * @date 2014年11月18日
	 * @param p
	 * @return
	 */

	public int inWhere(Point p) {// inWhere->offset->reDraw
		// updateLTRB();//不合理的位置调用
		for (WHERE where : priority) {// 权限遍历
			// Utils.sysoInfo("where 遍历");
			switch (where) {
			case TOP_LEFT:
				if (isInCircleArea(leftTopPoint.x, leftTopPoint.y, p)) {
					if (priority.getFirst() != WHERE.TOP_LEFT) {
						priority.remove(WHERE.TOP_LEFT);
						priority.addFirst(WHERE.TOP_LEFT);

					}
					// pStatic锚点 变化太频繁，不同状态，锚点不同.
					// 对于宅边框 锚点不变 平滑变化
					// if (!isSlim()) {// 宽边框
					// TODO:这种新设计的问题：处理边界重合不够平滑，有待优化
					pMove.x = leftTopPoint.x;
					pMove.y = leftTopPoint.y;
					pStatic.x = rightBottomPoint.x;
					pStatic.y = rightBottomPoint.y;

					// } // 窄边框

					// Utils.sysoInfo("左上角");
					// Utils.sysoInfo(priority);
					return TOP_LEFT;
				}
				break;
			case TOP_RIGHT:
				if (isInCircleArea(rightBottomPoint.x, leftTopPoint.y, p)) {
					if (priority.getFirst() != WHERE.TOP_RIGHT) {
						priority.remove(WHERE.TOP_RIGHT);
						priority.addFirst(WHERE.TOP_RIGHT);
					}
					pMove.x = rightBottomPoint.x;
					pMove.y = leftTopPoint.y;
					pStatic.x = leftTopPoint.x;
					pStatic.y = rightBottomPoint.y;
					// Utils.sysoInfo("右上角");
					// Utils.sysoInfo(priority);
					return TOP_RIGHT;
				}
				break;
			case BOTTOM_LEFT:

				if (isInCircleArea(leftTopPoint.x, rightBottomPoint.y, p)) {
					if (priority.getFirst() != WHERE.BOTTOM_LEFT) {

						priority.remove(WHERE.BOTTOM_LEFT);
						priority.addFirst(WHERE.BOTTOM_LEFT);
					}
					pMove.x = leftTopPoint.x;
					pMove.y = rightBottomPoint.y;
					pStatic.x = rightBottomPoint.x;
					pStatic.y = leftTopPoint.y;
					// Utils.sysoInfo("左下角");
					// Utils.sysoInfo(priority);
					return BOTTOM_LEFT;
				}
				break;
			case BOTTOM_RIGHT:

				if (isInCircleArea(rightBottomPoint.x, rightBottomPoint.y, p)) {

					if (priority.getFirst() != WHERE.BOTTOM_RIGHT) {
						priority.remove(WHERE.BOTTOM_RIGHT);
						priority.addFirst(WHERE.BOTTOM_RIGHT);
					}
					pMove.x = rightBottomPoint.x;
					pMove.y = rightBottomPoint.y;
					pStatic.x = leftTopPoint.x;
					pStatic.y = leftTopPoint.y;
					// Utils.sysoInfo("右下角");
					// Utils.sysoInfo(priority);
					return BOTTOM_RIGHT;
				}
				break;
			case CENTER_BODY:// 无需确定动态点和静态点
				if (isInBodyArea(p)) {// 最小化时候，判断失败
					if (priority.getFirst() != WHERE.CENTER_BODY) {
						priority.remove(WHERE.CENTER_BODY);
						priority.addFirst(WHERE.CENTER_BODY);
					}
					// pMove.x = rightBottomPoint.x;
					// pMove.y = rightBottomPoint.y;
					// pStatic.x = leftTopPoint.x;
					// pStatic.y = leftTopPoint.y;
					// Utils.sysoInfo("内部");
					// Utils.sysoInfo(priority);

					return CENTER_BODY;
				}
				break;
			}
		}
		// Utils.sysoInfo("外部");
		return OUT_BODY;
	}

	/**
	 * @Function 计算偏移位置，绝对反应到 pStart pMove
	 * @author Wangjj
	 * @date 2014年11月19日
	 * @param deltaX
	 *            x轴方向偏移量
	 * @param deltaY
	 *            y轴方向偏移量
	 * @param where
	 *            热区
	 */

	public void offset(int deltaX, int deltaY, int where) {

		if (!isInit || (deltaX == 0 && deltaY == 0)) {
			return;
		}
		// 边角无法移动
		// if (leftTopPoint.x + deltaX < 0
		// || rightBottomPoint.x + deltaX > parentView.getWidth()
		// || leftTopPoint.y + deltaY < 0
		// || rightBottomPoint.y + deltaY > parentView.getHeight())
		// return;
		/*
		 * if (leftTopPoint.x + deltaX < 0 || rightBottomPoint.x + deltaX >
		 * parentView.getWidth() || leftTopPoint.y + deltaY < 0 ||
		 * rightBottomPoint.y + deltaY > parentView.getHeight())
		 */

		// updateLTRB();
		// if (leftTopPoint.x + deltaX < 0
		// || rightBottomPoint.x + deltaX > parentView.getWidth()
		// || leftTopPoint.y + deltaY < 0
		// || rightBottomPoint.y + deltaY > parentView.getHeight())
		// return;

		// 起点不变，不可以累减

		switch (where) {
		case TOP_LEFT:
			// leftTopPoint点已经失去逻辑约束,需要判断哪个点在左上，以合理改变 leftTopPoint的坐标
			// leftTopPoint.x += deltaX;
			// leftTopPoint.y += deltaY;
			// 长或者宽为0 时外部定位判断可能有误差，所以这里要平滑过渡

		case TOP_RIGHT:
			// rightBottomPoint.x += deltaX;
			// leftTopPoint.y += deltaY;

		case BOTTOM_LEFT:
			// leftTopPoint.x += deltaX;
			// rightBottomPoint.y += deltaY;

		case BOTTOM_RIGHT:
			// rightBottomPoint.x += deltaX;
			// rightBottomPoint.y += deltaY;
			pMove.x += deltaX;
			pMove.y += deltaY;
			break;
		case CENTER_BODY:
			// 使得最终图形越界，则不更改坐标
			if (leftTopPoint.x + deltaX < 0
					|| rightBottomPoint.x + deltaX > parentView.getWidth()
					|| leftTopPoint.y + deltaY < 0
					|| rightBottomPoint.y + deltaY > parentView.getHeight())
				break;
			pStatic.x += deltaX;
			pStatic.y += deltaY;
			pMove.x += deltaX;
			pMove.y += deltaY;
			break;

		}
		// 依然保持leftTopPoint、rightBottomPoint的逻辑约束，更加方便点
		// 移动之后，x1、x2大小会变化，不一定x1<x2了，所以需要再次判断

		// leftTopPoint.x = Math.min(x1, x2);
		// leftTopPoint.y = Math.min(y1, y2);
		// rightBottomPoint.x = Math.max(x1, x2);
		// rightBottomPoint.y = Math.max(y1, y2);

		// 阻止越界 即 最小范围限制
		// 如果不处理越界，则要重新处理左上角点和右下角点的坐标值，确保 x1<x2,y1<y2。否则无法绘制矩形。这又是另外一种效果了。

		// minAreaLimit();//原始设计无法满足 平滑的任意滑动->更改设计？更改算法？

	}

	/**
	 * @Function 阻止越界 即 最小范围限制
	 * @author Wangjj
	 * @date 2014年11月20日
	 */

	private void minAreaLimit() {
		if (leftTopPoint.x + 2 * circlrRadius > rightBottomPoint.x) {
			leftTopPoint.x = rightBottomPoint.x - 3 * circlrRadius;
		}
		if (leftTopPoint.y + 2 * circlrRadius > rightBottomPoint.y) {
			leftTopPoint.y = rightBottomPoint.y - 3 * circlrRadius;
		}
	}

	/**
	 * 自动判断初始化矩形区域或者重绘
	 * 
	 * @author Wangjj
	 * @date 2014年11月18日
	 * @param canvas
	 * @param start
	 *            初始化时，该点为起点,一直不变
	 * @param end
	 */
	public void initOrRefreshArea(Canvas canvas, Paint paint, Point start,
			Point end) {
		// Utils.sysoInfo("drawArea" + start + end);

		if (!isInit) {
			int x1 = Math.min(start.x, end.x);
			int x2 = Math.max(start.x, end.x);
			int y1 = Math.min(start.y, end.y);
			int y2 = Math.max(start.y, end.y);

			// 偏移量太小，取消绘制
			if (x2 - x1 < MIN_DELATA || y2 - y1 < MIN_DELATA) {
				return;
			}

			// 状态栏、菜单栏也可以点击，算越界
			if (x1 < 0 || x2 > parentView.getWidth() || y1 < 0
					|| y2 > parentView.getHeight()) {
				return;
			}

			// 初始化时候，给个最小距离限制 影响到 pStatic pMove leftTopPoint rightBottomPoint
			// if (x1 + 3 * circlrRadius > x2) {
			// x1 = x2 - 3 * circlrRadius;
			// }
			// if (y1 + 3 * circlrRadius > y2) {
			// y1 = y2 - 3 * circlrRadius;
			// }
			// 将pStatic pMove初始化一下 否则 一步错 步步错
			pStatic.x = x1;
			pStatic.y = y1;
			pMove.x = x2;
			pMove.y = y2;
			// updateLTRB();
			// 或则直接赋值
			leftTopPoint.x = x1;
			leftTopPoint.y = y1;
			rightBottomPoint.x = x2;
			rightBottomPoint.y = y2;

			// 初始化时候，给个最小距离限制
			// minAreaLimit();

			// leftTopPoint.x等不一定满足'间距'需求，所以不能直接调用
			// drawShape(canvas, paint, leftTopPoint.x,
			// leftTopPoint.y,rightBottomPoint.x, rightBottomPoint.y);

			// reDrawArea(canvas, paint);// 让showAll调用reDrawArea

			isInit = true;
		} else {
			reDrawArea(canvas, paint);
		}
	}

	/**
	 * 
	 * @Function 绘制形状
	 * @author Wangjj
	 * @date 2014年11月19日
	 * @param canvas
	 * @param paint
	 * @param x1
	 *            x1小于x2。
	 * @param x2
	 * 
	 * @param y1
	 *            y1小于y2
	 * @param y2
	 * 
	 */
	private void drawShape(Canvas canvas, Paint paint, int x1, int y1, int x2,
			int y2) {

		paint.setColor(this.colorValue);
		// 四个圆点
		paint.setStyle(Style.FILL);
		canvas.drawCircle(x1, y1, circlrRadius, paint);
		canvas.drawCircle(x1, y2, circlrRadius, paint);
		canvas.drawCircle(x2, y1, circlrRadius, paint);
		canvas.drawCircle(x2, y2, circlrRadius, paint);
		// 矩形背景
		paint.setColor(Color.argb(0x55, Color.red(this.colorValue),
				Color.green(this.colorValue), Color.blue(this.colorValue)));
		// 高频调用方法，不可以new
		// Rect r = new Rect(x1, y1, x2, y2);
		rect_background.set(x1, y1, x2, y2);
		canvas.drawRect(rect_background, paint);
		// 矩形框
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(4);
		paint.setColor(this.colorValue);
		// Rect r2 = new Rect(x1, y1, x2, y2);
		rect_stroke.set(x1, y1, x2, y2);
		canvas.drawRect(rect_stroke, paint);
	}

	/**
	 * 
	 * @Function 重绘
	 * @author Wangjj
	 * @date 2014年11月19日
	 * @param canvas
	 * @param paint
	 */
	public void reDrawArea(Canvas canvas, Paint paint) {
		// 如果不处理越界，则在这里重新处理左上角点和右下角点的坐标值，确保 x1<x2,y1<y2。否则无法绘制矩形。这又是另外一种效果了。
		// if (leftTopPoint.x > rightBottomPoint.x) {
		// changeX();
		// }
		// if (leftTopPoint.y > rightBottomPoint.y) {
		// changeY();
		// }

		// 为了实现随意滑动变形， 左上角坐标 和 右下角坐标 不做大小限制了。
		updateLTRB();// 且，仅且 在这里更新最终（绘制、计算）坐标。否则导致逻辑错误。

		// 加入该判断，注定无法处理‘平滑过渡’
		// TODO:如果去掉，‘平滑过渡’不平滑，快速滑动才有效果，这是设计的bug
		// 左边可以推动右边、上边可以推动下面
		if (rightBottomPoint.x - leftTopPoint.x < 3 * circlrRadius) {
			int tx = leftTopPoint.x + 3 * circlrRadius;
			if (tx < parentView.getWidth()) {
				rightBottomPoint.x = tx;
			} else {// tx右侧越界
				leftTopPoint.x = rightBottomPoint.x - 3 * circlrRadius;
			}
			// bug：没反应到pStatic 和 pMove中，导致最小化时，CENTER_BODY判断失败

		}
		if (rightBottomPoint.y - leftTopPoint.y < 3 * circlrRadius) {
			int ty = leftTopPoint.y + 3 * circlrRadius;
			if (ty < parentView.getHeight()) {
				rightBottomPoint.y = ty;
			} else {// ty下侧越界
				leftTopPoint.y = rightBottomPoint.y - 3 * circlrRadius;
			}
		}

		drawShape(canvas, paint, leftTopPoint.x, leftTopPoint.y,
				rightBottomPoint.x, rightBottomPoint.y);
	}

	public void reset() {
		this.isInit = false;
		this.leftTopPoint.x = 0;
		this.leftTopPoint.y = 0;
		this.rightBottomPoint.x = 0;
		this.rightBottomPoint.y = 0;
	}

	public Point getpStatic() {
		return pStatic;
	}

	public Point getpMove() {
		return pMove;
	}

	public Point getLeftTopPoint() {
		return leftTopPoint;
	}

	public void setLeftTopPoint(Point leftTopPoint) {
		this.leftTopPoint = leftTopPoint;
	}

	public Point getRightBottomPoint() {
		return rightBottomPoint;
	}

	public void setRightBottomPoint(Point rightBottomPoint) {
		this.rightBottomPoint = rightBottomPoint;
	}

	public int getColorValue() {
		return colorValue;
	}

	public void setColorValue(int colorValue) {
		this.colorValue = colorValue;
	}
}
