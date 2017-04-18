package com.yuantuo.customview.tabs.interfaces;

import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.yuantuo.customview.tabs.TabView.OnTabChangedListener;

public interface OnTabAndPagerChanged	extends	OnTabChangedListener,	OnPageChangeListener,	ITabAction
{
	public int getTabPagerCount();

	public <T> T getItem( int position );
}
