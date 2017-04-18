/**
 * xiaozhi
 * 2014-7-23
 */
package cc.wulian.smarthomev5.view;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import cc.wulian.ihome.wan.util.StringUtil;

/**
 * 用于显示a-z的字母边栏
 * 
 * @author xiaozhi
 * @创作日期 2014-7-23
 */
public class WLSideBar extends View {

    /**
     * 字母选中的回调函数
     */
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    private  ArrayList<String> letters = new ArrayList<String>();
    /**
     * 当前选中的字母
     */
    private int choose = -1;
    private Paint paint = new Paint();

    public WLSideBar(Context context) {
        super(context);
    }

    public WLSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnTouchingLetterChangedListener getOnTouchingLetterChangedListener() {
        return onTouchingLetterChangedListener;
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * @param 上下文
     * @param 属性
     * @param 默认样式
     */
    public WLSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 获取焦点改变背景颜色
         */

        /**
         * 获取高度和宽度
         */
        int height = getHeight();
        int width = getWidth();

        /**
         * 获取每个字母的高度
         */
        int singleHeight = 20;
        try{
        	singleHeight = height / letters.size();
        }catch(Exception e){
        }

        for (int i = 0; i < letters.size(); i++) {
            paint.setColor(Color.DKGRAY);
            paint.setTypeface(Typeface.SANS_SERIF);
            paint.setAntiAlias(true);
            paint.setTextSize((float)width);

            /**
             * 选中的状态
             */
            if (i == choose) {
                paint.setColor(Color.RED);
                paint.setFakeBoldText(true);
            }

            /**
             * x坐标等于中间-字符串宽度的一半
             */
            float xPos = width / 2 - paint.measureText(letters.get(i)) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(letters.get(i), xPos, yPos, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float currentYPos = event.getY();
        final int oldChoose = choose;
        final int c = (int) (currentYPos / getHeight() * letters.size());
        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            choose = -1;
            invalidate();
            break;

        default:
            if (oldChoose != c) {
                if (onTouchingLetterChangedListener != null) {
                    onTouchingLetterChangedListener
                            .onTouchingLetterChanged(letters.get(c));
                }

                choose = c;
                invalidate();
            }
            break;
        }
        return true;
    }
    public void clear(){
    	this.letters.clear();
    }
    public void add(String str){
    	if(StringUtil.isNullOrEmpty(str))
    		return;
    	if(str.length() >1){
    		str = str.substring(0,1);
    	}
    	if(!this.letters.contains(str)){
    		this.letters.add(str);
    		Collections.sort(this.letters);
    	}
    }
    public void remove(String str){
    	this.letters.remove(str);
    }
	public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }
}
