package com.yuantuo.customview.tabs;

import android.support.v4.app.Fragment;
import android.view.View;

import com.yuantuo.customview.tabs.TabView.OnTabChangedListener;
import com.yuantuo.customview.tabs.adapters.TabPagerFragmentAdapter;
import com.yuantuo.customview.tabs.impls.AbstractTabPager;
import com.yuantuo.customview.tabs.impls.OnPagerChangedImpl;

public class TabPagerFragment extends AbstractTabPager
{
	private TabPagerFragmentAdapter mFragmentAdapter;

	@Override
	public void addTabPager( TabPagerSpace space ){
		mListener.addTabPager(space);
	}

	@Override
	public void removeTabPager( int pos ){
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

	public Fragment getFragmentByPosition( int pos ){
		return (Fragment) mListener.getItem(pos);
	}

	@Override
	public void setOnTabChangedListener( OnTabChangedListener listener ){
		mListener.setOnTabChangedListener(listener);
	}

	@Override
	public void initOnTabAndPagerChanged(){
		mFragmentAdapter = new TabPagerFragmentAdapter(getActivity());
		mListener = new OnPagerChangedImpl(mTabContainer, mTabView, mTabContent, mFragmentAdapter);
	}
}