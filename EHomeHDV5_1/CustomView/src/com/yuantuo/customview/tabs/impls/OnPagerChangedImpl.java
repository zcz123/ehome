package com.yuantuo.customview.tabs.impls;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.yuantuo.customview.tabs.PagerFragment;
import com.yuantuo.customview.tabs.TabPagerSpace;
import com.yuantuo.customview.tabs.TabPagerSpace.TabPagerContent;
import com.yuantuo.customview.tabs.TabView;
import com.yuantuo.customview.tabs.TabView.OnTabChangedListener;
import com.yuantuo.customview.tabs.interfaces.IPagerAction;
import com.yuantuo.customview.tabs.interfaces.OnTabAndPagerChanged;

public class OnPagerChangedImpl implements OnTabAndPagerChanged
{
	private HorizontalScrollView mTabContainer;
	private TabView mTabs;
	private ViewPager mTabContent;
	private List<TabPagerSpace> mTabSpaces;
	private OnTabChangedListener mOnTabChangedListener;
	private PagerAdapter mAdapter;

	public OnPagerChangedImpl( HorizontalScrollView mTabContainer, TabView mTabs,
			ViewPager mTabContent, PagerAdapter mAdapter )
	{
		this.mTabContainer = mTabContainer;
		this.mTabs = mTabs;
		this.mTabContent = mTabContent;
		mTabSpaces = new ArrayList<TabPagerSpace>();
		this.mAdapter = mAdapter;
		
		((IPagerAction) this.mAdapter).setOnTabAndPagerChanged(this);
		initListener();
	}

	private void initListener(){
		mTabs.setOnTabChangedListener(this);
		mTabContent.setOnPageChangeListener(this);
		mTabContent.setAdapter(mAdapter);
	}

	@Override
	public boolean onPreTabChange( int index ){
		if (mOnTabChangedListener != null){ return mOnTabChangedListener.onPreTabChange(index); }
		return false;
	}

	@Override
	public void onTabChanged( int tabIndex ){
		mTabContent.setCurrentItem(tabIndex);
		if (mOnTabChangedListener != null){
			mOnTabChangedListener.onTabChanged(tabIndex);
		}
	}

	@Override
	public void onTabLongClicked( int tabIndex ){
		if (mOnTabChangedListener != null){
			mOnTabChangedListener.onTabLongClicked(tabIndex);
		}
	}

	@Override
	public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ){
	}

	@Override
	public void onPageSelected( int position ){
		mTabs.setCurrentTab(position);
		animateTo(position);
	}

	private void animateTo( int pos ){
		if (mTabContainer == null || !mTabContainer.isShown()) return;

		View view = mTabs.getChildTabViewAt(pos);
		final int scrollPos = view.getLeft() - (mTabContainer.getWidth() - view.getWidth()) / 2;
		mTabContainer.smoothScrollTo(scrollPos, 0);
	}

	@Override
	public void onPageScrollStateChanged( int state ){
	}

	@Override
	public void setCurrentTabPager( int pos ){
		mTabContent.setCurrentItem(pos);
	}

	@Override
	public void setOnTabChangedListener( OnTabChangedListener listener ){
		mOnTabChangedListener = listener;
	}

	@Override
	public int getCurrentTabPager(){
		return mTabs.getCurrentTab();
	}

	@Override
	public View getCurrentIndicatorView( int pos ){
		return mTabs.getIndicatorViewAt(pos);
	}

	@Override
	public void addTabPager( TabPagerSpace space ){
		mTabs.addTab(space);
		mTabSpaces.add(space);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void removeTabPager( int pos ){
		if (pos < 0 || pos >= mTabSpaces.size()){ return; }
		mTabs.removeViewAt(pos);
		mTabSpaces.remove(pos);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public int getTabPagerCount(){
		return mTabSpaces.size();
	}

	@Override
	public <T> T getItem( int position ){
		TabPagerSpace space = mTabSpaces.get(position);
		TabPagerContent<T> content = space.mPagerContent;
		T t = content.createContent();
		if (t instanceof PagerFragment){
			((PagerFragment) t).setParent(this);
		}
		return t;
	}
}