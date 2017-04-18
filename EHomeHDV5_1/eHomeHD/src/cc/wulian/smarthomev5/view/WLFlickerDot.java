package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * 闪烁的圆点
 * @author yuxiaoxuan
 * @date 2016年8月8日13:48:13
 */
public class WLFlickerDot extends View {
	
	public WLFlickerDot(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public WLFlickerDot(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public WLFlickerDot(Context context) {
		super(context);
		initView(context);
	}
	private ImageView lightImage=null;
	private LayoutInflater inflater;
	private void initView(Context context){
		inflater=LayoutInflater.from(context);
//		lightImage=inflater.i
	}
	
	/**
	 * 闪烁
	 */
	public void Flicking(){
		
	}

}
