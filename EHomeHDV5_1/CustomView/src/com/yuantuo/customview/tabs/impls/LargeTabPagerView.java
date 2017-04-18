package com.yuantuo.customview.tabs.impls;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuantuo.customview.tabs.TabPagerView;

public class LargeTabPagerView extends TabPagerView
{
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
		setWantScrollTabView(true);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}