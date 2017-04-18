package com.yuantuo.customview.tabs.adapters;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.yuantuo.customview.tabs.TabPagerSpace;
import com.yuantuo.customview.tabs.interfaces.IPagerAction;
import com.yuantuo.customview.tabs.interfaces.OnTabAndPagerChanged;

public class TabPagerViewAdapter extends PagerAdapter implements IPagerAction
{
	private List<View> mTabViews;
	private OnTabAndPagerChanged impl;

	public TabPagerViewAdapter()
	{
		mTabViews = new ArrayList<View>();
	}

	public void addPager( TabPagerSpace space ){
		mTabViews.add((View)space.mPagerContent.createContent());
	}
	
	public void removePager(int pos){
		mTabViews.remove(pos);
	}

	@Override
	public int getCount(){
		return impl.getTabPagerCount();
	}

	@Override
	public boolean isViewFromObject( View view, Object object ){
		return view == object;
	}

	@Override
	public Object instantiateItem( ViewGroup container, int position ){
		View view = mTabViews.get(position);
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem( ViewGroup container, int position, Object object ){
		container.removeView((View) object);
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