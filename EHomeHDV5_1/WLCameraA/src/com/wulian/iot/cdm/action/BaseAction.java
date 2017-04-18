package com.wulian.iot.cdm.action;
import com.wulian.iot.cdm.factory.ConcreateCameraFactory;
import com.wulian.iot.cdm.AbstractFactory;

import android.content.Context;

public class BaseAction {
   protected String TAG = getClass().getSimpleName().toString();
	protected Context context;
	protected AbstractFactory fty = null;
	
	public BaseAction(){
		
	}
	public BaseAction(Context context){
		
		this.context = context;
		fty = new ConcreateCameraFactory();
		
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public Context getContext() {
		return context;
	}
	public void setFty(AbstractFactory fty) {
		this.fty = fty;
	}
	public AbstractFactory getFty() {
		return fty;
	}
}
