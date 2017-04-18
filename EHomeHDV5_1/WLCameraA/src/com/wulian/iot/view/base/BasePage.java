package com.wulian.iot.view.base;
import android.content.Context;
import android.view.View;
/**
 *  滑动页面基类
 * @author syf
 */
public abstract class BasePage {
	protected int viewPageType = -1;
	protected View v;
	protected Context context;
	public BasePage(Context context){
		this.context = context;
		v = this.initView();
		this.initEvents();
	}
	public abstract  View initView();
	public abstract  void initData();
	public abstract void initEvents();
	public abstract void showGallery(String date);
	public View getView(){
		return v;
	}
}