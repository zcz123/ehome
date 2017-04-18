/**
 * 类名            FontManager.java
 * 说明            自定义字体
 * 创建日期    2014年5月28日 下午5:03:38
 * 作者            gxy
 * 版权           【南京物联传感技术有限公司】
 */
package com.yuantuo.customview.font;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 使用时确保该上下文环境中assets/fonts/目录下有对应字体文件 
 * @author gxy
 *
 */
public class FontManager {

	/**
	 * 华文细黑
	 */
	public static final int FONT_TYPE_ST_XIHEI = 0;

	/**
	 * 黑体
	 */
	public static final int FONT_TYPE_SIM_HEI = 1;

	private static Typeface tfSTXiHei = null;
	private static Typeface tfSimHei = null;

	public static Typeface getFontSTXiHei(Context context) {
		if (tfSTXiHei == null) {
			tfSTXiHei = Typeface.createFromAsset(context.getAssets(),
					"fonts/STXIHEI.TTF");
		}
		return tfSTXiHei;
	}

	public static Typeface getFontSimHei(Context context) {
		if (tfSimHei == null) {
			tfSimHei = Typeface.createFromAsset(context.getAssets(),
					"fonts/simhei.ttf");
		}
		return tfSimHei;
	}

	private static Typeface getFont(Context context, int type) {

		Typeface tf = null;
		switch (type) {
		case 0:
			tf = getFontSTXiHei(context);
			break;
		case 1:

			tf = getFontSimHei(context);
			break;

		default:
			tf = getFontSTXiHei(context);
			break;
		}

		return tf;
	}

	/**
	 * 
	 * 功能说明 改变控件字体
	 * 
	 * @param root
	 * @param act
	 * @return void
	 */
	public static View changeViewGroupFonts(Context context, ViewGroup v,
			int fontType) {

		if (context == null || v == null) {
			return null;
		}
		for (int i = 0; i < v.getChildCount(); i++) {

			View view = v.getChildAt(i);
			if (view instanceof ViewGroup) {
				changeViewGroupFonts(context, (ViewGroup) view, fontType);
			}else{
				changeViewFonts(context, view, fontType);
			}
		}
		return v;
	}

	public static View changeViewFonts(Context context, View view, int fontType) {

		if (view instanceof TextView) {
			((TextView) view).setTypeface(getFont(context, fontType));
		} else if (view instanceof Button) {
			((Button) view).setTypeface(getFont(context, fontType));
		} else if (view instanceof EditText) {
			((EditText) view).setTypeface(getFont(context, fontType));
		}
		
		return view;
	}
}
