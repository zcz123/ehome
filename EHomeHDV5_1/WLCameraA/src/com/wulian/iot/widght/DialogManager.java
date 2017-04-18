package com.wulian.iot.widght;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.wulian.icam.R;

public class DialogManager {
    private  Context context;
	public static Map<String,Integer> layoutMaps = null;
	public static final String iot_camera = "iot";
	public static final String animation = "animation";
	public static final String iot_dialog_style= "iot_style";
	private int width = LinearLayout.LayoutParams.MATCH_PARENT;
	private int height = LinearLayout.LayoutParams.MATCH_PARENT;
	private boolean cancelable = false;
	static{
		layoutMaps = new HashMap<String, Integer>();
		layoutMaps.put(iot_camera, R.layout.iot_loading_dialog);
		layoutMaps.put(animation, R.anim.load_animation);
		layoutMaps.put(iot_dialog_style,R.style.iot_style_loading_dialog);
	}
	public DialogManager(Context context){
		this.context = context;
	}
	public View getView(String key){
		LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(layoutMaps.get(key), null);// 得到加载view 
	}
	public Animation getAnimation(String key){
		return AnimationUtils.loadAnimation(context,layoutMaps.get(key));
	}
	public Dialog getDialog(String key,LinearLayout layout){
		Dialog log = new Dialog(context,layoutMaps.get(key));
		log.setCancelable(cancelable);
		log.setContentView(layout,new LinearLayout.LayoutParams(width,height));
		return log;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public Context getContext() {
		return context;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getHeight() {
		return height;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
	}
	public boolean isCancelable() {
		return cancelable;
	}
}
