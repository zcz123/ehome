package com.yuantuo.customview.ui;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ScreenSize
{
	public static int screenWidth;
	public static int screenHeight;
	public static float density;

	public static void getScreenSize( Activity context ){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
		density = displayMetrics.density;
//		System.out.println(displayMetrics.toString() + 
//											 ",densityDpi=" + displayMetrics.densityDpi + 
//											 "screenWidth x screenHeight=" + screenWidth + " x " + screenHeight);
	}
}