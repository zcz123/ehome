package com.yuantuo.customview.tabs;

import android.support.v4.app.Fragment;

import com.yuantuo.customview.tabs.interfaces.ITabAction;

public class PagerFragment extends Fragment
{
	private ITabAction mTabAction;

	public void setParent( ITabAction action ){
		mTabAction = action;
	}

	public ITabAction getParent(){
		return mTabAction;
	}
}
