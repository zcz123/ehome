package com.yuantuo.customview.tabs.interfaces;

import android.view.View;

import com.yuantuo.customview.tabs.TabPagerSpace;
import com.yuantuo.customview.tabs.TabView.OnTabChangedListener;

public interface ITabAction
{
	public void addTabPager( TabPagerSpace space );
	
	public void removeTabPager(int pos);

	public void setCurrentTabPager( int pos );

	public int getCurrentTabPager();
	
	public View getCurrentIndicatorView(int pos);

	public void setOnTabChangedListener( OnTabChangedListener listener );
}
