package com.yuantuo.customview.tabs.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import com.yuantuo.customview.tabs.interfaces.IPagerAction;
import com.yuantuo.customview.tabs.interfaces.OnTabAndPagerChanged;

public class TabPagerFragmentAdapter extends FragmentPagerAdapter implements IPagerAction
{
	private OnTabAndPagerChanged impl;

	public TabPagerFragmentAdapter( FragmentActivity mActivity )
	{
		super(mActivity.getSupportFragmentManager());
	}

	@Override
	public Fragment getItem( int position ){
		return impl.getItem(position);
	}

	@Override
	public int getCount(){
		return impl.getTabPagerCount();
	}

	@Override
	public void setOnTabAndPagerChanged( OnTabAndPagerChanged pagerChanged ){
		impl = pagerChanged;
	}

	@Override
	public OnTabAndPagerChanged geTabAndPagerChanged(){
		return impl;
	}
}