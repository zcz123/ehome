package cc.wulian.smarthomev5.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InputMethodUtils {


	/**
	 * 显示输入法
	 * 
	 * @param context
	 * @param focusView
	 */
	
	private static InputMethodManager getInputMethodManager(Context context){
		return (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	public static void show(Context context, View focusView) {
		InputMethodManager imm = getInputMethodManager(context);
		imm.showSoftInput(focusView, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 隐藏输入法
	 * 
	 * @param context
	 */
	
	public static void hide(Context context){
		Activity activity = ((Activity) context);
		hide(activity,activity.getWindow().getDecorView());
	}
	public static void hide(Context context,View view){
		Activity activity = ((Activity) context);
		InputMethodManager imm = getInputMethodManager(activity);
		if(imm.isActive())
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	/**
	 * 切换输入法状态 键盘若显示则隐藏; 隐藏则显示
	 * 
	 * @param context
	 */
	public static void toggle(Context context) {
		InputMethodManager imm = getInputMethodManager(context);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 判断InputMethod的当前状态
	 * 
	 * @param context
	 * @param focusView
	 * @return
	 */
	public static boolean isShow(Context context) {
		InputMethodManager imm = getInputMethodManager(context);
		return imm.isActive();
	}

}
