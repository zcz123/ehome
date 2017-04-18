package cc.wulian.smarthomev5.fragment.setting.flower.items;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.PopupWindow;

public class CustomPopupWindow extends PopupWindow {

	private Activity context;
	
	private View layoutView;
	
	public CustomPopupWindow(Activity context,View view){
		this.context=context;
		layoutView=view;
		setContentView(layoutView);
	}
	
	public CustomPopupWindow(Activity context,int srcId){
		this.context=context;
		layoutView=View.inflate(context,srcId , null); //
		setContentView(layoutView);
	}
	
	public void initEvent(PopEvent event){
		if(event!=null)event.initWidget(layoutView);
		DisplayMetrics metrics=new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Rect outRect=new Rect();
		context.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
		setWidth(metrics.widthPixels);
		setHeight(metrics.heightPixels-outRect.top);
		setFocusable(true);
	    setTouchable(true);
	    setBackgroundDrawable(new ColorDrawable(0));
	    update();
	}
	
	public void setSize(int width,int height){
		 setWidth(width);
		 setHeight(height);
		 update();
	}
	
	public interface PopEvent{
		public void initWidget(View view);
	}
}
