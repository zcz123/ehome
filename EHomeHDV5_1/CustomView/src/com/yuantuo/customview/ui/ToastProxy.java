package com.yuantuo.customview.ui;

import java.lang.reflect.Field;

import com.yuantuo.customview.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

public class ToastProxy extends Toast {

	private ToastProxy(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static Toast createToast(Context context,int animateRes) {
		Toast toast = new Toast(context);
		setToastAnim(toast,animateRes);
		return toast;
	}

	public static void setToastAnim(Toast toast,int animateRes) {

		if (toast != null) {
			try {
				Object mTN = getField(toast, "mTN");
				if (mTN != null) {
					Object mParams = getField(mTN, "mParams");
					if (mParams != null
							&& mParams instanceof WindowManager.LayoutParams) {
						WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
						params.windowAnimations = animateRes;
					}
				}
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Object getField(Object instance, String name)
			throws NoSuchFieldException, IllegalAccessException,
			IllegalArgumentException {
		Object object = null;
		if (instance != null && !TextUtils.isEmpty(name)) {
			Field field = instance.getClass().getDeclaredField(name);
			if (field != null) {
				field.setAccessible(true);
				object = field.get(instance);
			}
		}
		return object;
	}
}
