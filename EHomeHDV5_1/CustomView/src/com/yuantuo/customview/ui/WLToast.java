package com.yuantuo.customview.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuantuo.customview.R;

public class WLToast {

	public static final int TOAST_LONG = 1;
	public static final int TOAST_SHORT = 0;
	private static final String TAG = WLToast.class.getSimpleName();

	/**
	 * 布局初始化
	 * 
	 * @author gaoxy
	 * 
	 */
	public interface OnToastInitListener {
		public void init(View view);
	}

	/**
	 * 
	 * 创建默认布局Toast
	 * 
	 * @param context
	 *            上下文环境
	 * @param resLayout
	 *            资源布局文件
	 * @param listener
	 *            布局初始化
	 * @param duration
	 *            显示事件
	 * @param gravity
	 *            布局方式
	 * @param hasAnimation
	 *            是否动画
	 */
	private static void makeCustomToast(Context context, int resLayout,
			OnToastInitListener listener, int duration, int gravity,
			boolean hasAnimation) {
		Toast toast;
		if (hasAnimation) {
			toast = ToastProxy.createToast(context,R.style.toast_anim_v5);
		} else {
			toast = new Toast(context);
		}
		try {
			View view = LayoutInflater.from(context).inflate(resLayout, null);
			if (listener != null) {
				listener.init(view);
			}
			toast.setView(view);
			toast.setGravity(gravity, 0, 0);
			toast.setDuration(duration);
			toast.show();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
	}

	private static void makeToast(Context context, final int resImage,
			final String text, int duration, boolean hasAnimation) {

		makeCustomToast(
				context,
				R.layout.layout_toast_v5,
				new OnToastInitListener() {
					@Override
					public void init(View view) {
						// TODO Auto-generated method stub
						TextView tv = (TextView) view
								.findViewById(R.id.toast_text);
						tv.setText(text);

						if (resImage > 0) {
							ImageView img = (ImageView) view
									.findViewById(R.id.toast_img);
							img.setImageResource(resImage);
						}
					}
				}, duration, Gravity.CENTER | Gravity.FILL_HORIZONTAL,
				hasAnimation);
	}

	/**
	 * 主线程内默认动画显示Toast
	 * 
	 * @param context
	 *            上下文环境
	 * @param text
	 *            文字信息
	 * @param duration
	 *            显示事件
	 */
	public static void showToast(Context context, String text, int duration) {
		showToast(context, -1, text, duration, false, false);
	}
	
	public static void showToast(Context context, String text, int duration,boolean isInOtherThread) {
		showToast(context, -1, text, duration, false, isInOtherThread);
	}

	/**
	 * 主线程内自定义动画显示Toast
	 * 
	 * @param context
	 *            上下文环境
	 * @param text
	 *            文字信息
	 * @param duration
	 *            显示事件
	 */
	public static void showToastWithAnimation(Context context, String text,
			int duration) {
		showToast(context, -1, text, duration, true, false);
	}
	

	public static void showToastWithAnimation(Context context, String text,
			int duration, boolean isInOtherThread) {
		showToast(context, -1, text, duration, true, isInOtherThread);
	}

	/**
	 * 
	 * @param context
	 *            上下文环境
	 * @param resImage
	 *            图标信息
	 * @param text
	 *            文字信息
	 * @param duration
	 *            显示事件
	 * @param hasAnimatioin
	 *            是否显示自定义动画
	 * @param isInOtherThread
	 *            是否在别的线程
	 */
	public static void showToast(final Context context, final int resImage,
			final String text, final int duration, final boolean hasAnimatioin,
			boolean isInOtherThread) {
		if (isInOtherThread) {
			Handler handler = new Handler(context.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					makeToast(context, resImage, text, duration, hasAnimatioin);
				}
			});
		} else {
			makeToast(context, resImage, text, duration, hasAnimatioin);
		}
	}

	/**
	 * 
	 * 主线程内默认动画显示Toast
	 * 
	 * @param context
	 *            上下文环境
	 * @param resLayout
	 *            资源布局文件事件
	 * @param listener
	 *            布局初始化
	 * @param duration
	 *            显示事件
	 * @param gravity
	 *            布局方式
	 */
	public static void showCustomToast(Context context, int resLayout,
			OnToastInitListener listener, int duration, int gravity) {
		showCustomToast(context, resLayout, listener, duration, gravity, false,
				false);
	}

	/**
	 * 主线程内自定义动画显示Toast
	 * 
	 * @param context
	 *            上下文环境
	 * @param resLayout
	 *            资源布局文件事件
	 * @param listener
	 *            布局初始化
	 * @param duration
	 *            显示事件
	 * @param gravity
	 *            布局方式
	 */
	public static void showCustomToastWithAnimation(Context context,
			int resLayout, OnToastInitListener listener, int duration,
			int gravity) {
		showCustomToast(context, resLayout, listener, duration, gravity, true,
				false);
	}

	/**
	 * 
	 * @param context
	 *            上下文环境
	 * @param resLayout
	 *            资源布局文件
	 * @param listener
	 *            布局初始化事件
	 * @param duration
	 *            显示事件
	 * @param gravity
	 *            布局方式
	 * @param hasAnimation
	 *            是否自定义动画
	 * @param isInOtherThread
	 *            是否在别的线程
	 */
	public static void showCustomToast(final Context context,
			final int resLayout, final OnToastInitListener listener,
			final int duration, final int gravity, final boolean hasAnimation,
			boolean isInOtherThread) {
		if (isInOtherThread) {
			Handler handler = new Handler(context.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					makeCustomToast(context, resLayout, listener, duration,
							gravity, hasAnimation);
				}
			});
		} else {
			makeCustomToast(context, resLayout, listener, duration, gravity,
					hasAnimation);
		}
	}
	
	public static void showAlarmMessageToast(final Context context, String text, int duration,final Intent intent){
		try {
			Toast toast = new Toast(context);
			View view = LayoutInflater.from(context).inflate(R.layout.toast_alarm_message, null);
			TextView textView = (TextView)view.findViewById(R.id.toast_content_tv);
			textView.setText(text);
			if(intent != null){
				textView.setOnClickListener(new OnClickListener(){
					
					@Override
					public void onClick(View arg0) {
						context.startActivity(intent);
					}
				});
			}
			toast.setView(view);
			toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL, 0, 0);
			toast.setDuration(duration);
			ToastProxy.setToastAnim(toast, R.style.toast_anim_alarm);
			toast.show();
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
	}
}
