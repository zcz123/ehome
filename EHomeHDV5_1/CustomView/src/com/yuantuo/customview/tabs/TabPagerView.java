package com.yuantuo.customview.tabs;

import android.view.View;

import com.yuantuo.customview.tabs.TabView.OnTabChangedListener;
import com.yuantuo.customview.tabs.adapters.TabPagerViewAdapter;
import com.yuantuo.customview.tabs.impls.AbstractTabPager;
import com.yuantuo.customview.tabs.impls.OnPagerChangedImpl;

public class TabPagerView extends AbstractTabPager
{
	private TabPagerViewAdapter mViewAdapter;

	@Override
	public void addTabPager( TabPagerSpace space ){
		mViewAdapter.addPager(space);
		mListener.addTabPager(space);
	}

	@Override
	public void removeTabPager( int pos ){
		mViewAdapter.removePager(pos);
		mListener.removeTabPager(pos);
	}
	
	@Override
	public void setCurrentTabPager( int pos ){
		mListener.setCurrentTabPager(pos);
	}
	
	@Override
	public int getCurrentTabPager(){
		return mListener.getCurrentTabPager();
	}

	@Override
	public View getCurrentIndicatorView( int pos ){
		return mListener.getCurrentIndicatorView(pos);
	}

	@Override
	public void setOnTabChangedListener( OnTabChangedListener listener ){
		mListener.setOnTabChangedListener(listener);
	}

	@Override
	public void initOnTabAndPagerChanged(){
		mViewAdapter = new TabPagerViewAdapter();
		mListener = new OnPagerChangedImpl(mTabContainer, mTabView, mTabContent, mViewAdapter);
	}
}