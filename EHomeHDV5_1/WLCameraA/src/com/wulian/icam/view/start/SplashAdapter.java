/**
 * Project Name:  FamilyRoute
 * File Name:     SplashAdapter.java
 * Package Name:  com.wulian.familyroute.adpter
 * @Date:         2014年9月23日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.start;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @Function: 引导页适配器
 * @date: 2014年9月23日
 * @author Wangjj
 */
public class SplashAdapter extends PagerAdapter {
	private ArrayList<View> views;
	private ArrayList<String> titles;

	public SplashAdapter(ArrayList<View> views, ArrayList<String> titles) {
		super();
		this.views = views;
		this.titles = titles;
	}

	@Override
	public int getCount() {

		return views.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {

		return view == object;
	}

	@Override
	public Object instantiateItem(View container, int position) {

		((ViewPager) container).addView(views.get(position));
		return views.get(position);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

}
