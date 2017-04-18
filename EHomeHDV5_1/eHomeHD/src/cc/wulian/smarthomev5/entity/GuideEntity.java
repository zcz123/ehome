package cc.wulian.smarthomev5.entity;

import android.view.View;

public class GuideEntity{
	private View actionView;
	private View targetView;
	private int gravity;
	
	public GuideEntity(View action,View target,int gravity){
		this.actionView=action;
		this.targetView=target;
		this.gravity=gravity;
	}

	public View getActionView() {
		return actionView;
	}

	public View getTargetView() {
		return targetView;
	}

	public int getGravity() {
		return gravity;
	}

}
