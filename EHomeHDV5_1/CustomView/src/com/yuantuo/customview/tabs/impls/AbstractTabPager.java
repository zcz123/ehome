package com.yuantuo.customview.tabs.impls;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.yuantuo.customview.R;
import com.yuantuo.customview.tabs.TabView;
import com.yuantuo.customview.tabs.interfaces.ITabAction;
import com.yuantuo.customview.tabs.interfaces.OnTabAndPagerChanged;

public abstract class AbstractTabPager extends Fragment implements ITabAction
{
	private static final String SAVE_KEY_CURRENT_TAB = "currentTab";

	protected HorizontalScrollView mTabContainer;
	protected TabView mTabView;
	protected ViewPager mTabContent;
	protected OnTabAndPagerChanged mListener;
	private boolean mWantScrollTabView;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
		if (mWantScrollTabView){
			return inflater.inflate(R.layout.large_tab_pager, container);
		}
		else{
			return inflater.inflate(R.layout.tab_pager, container);
		}
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState ){
		super.onViewCreated(view, savedInstanceState);

		// Mark: when we known we will have many tabs, use this scrollview to scroll tabs
		if (mWantScrollTabView){
			mTabContainer = (HorizontalScrollView) view.findViewById(R.id.tab_pager_container);
		}

		mTabView = (TabView) view.findViewById(R.id.tab_pager_tabs);
		mTabContent = (ViewPager) view.findViewById(R.id.tab_pager_content);

		initOnTabAndPagerChanged();
		if (savedInstanceState != null){
			mTabContent.setCurrentItem(savedInstanceState.getInt(SAVE_KEY_CURRENT_TAB));
		}
	}

	public abstract void initOnTabAndPagerChanged();

	public void setWantScrollTabView( boolean wantScroll ){
		mWantScrollTabView = wantScroll;
	}

	public void setTabIndicatorBackground( int resource ){
		mTabContainer.setBackgroundResource(resource);
	}

	public void setTabIndicatorResource( int resource ){
		mTabView.setTabIndicatorResource(resource);
	}

	public TabView getTabView(){
		return mTabView;
	}

	public ViewPager getTabContent(){
		return mTabContent;
	}
	
	public void setIndicatorBackground(int res){
		if(mWantScrollTabView){
			mTabContainer.setBackgroundResource(res);
		}
		else {
			mTabView.setBackgroundResource(res);
		}
	}

	@Override
	public void onSaveInstanceState( Bundle outState ){
		super.onSaveInstanceState(outState);
		outState.putInt(SAVE_KEY_CURRENT_TAB, mTabContent.getCurrentItem());
	}
}
