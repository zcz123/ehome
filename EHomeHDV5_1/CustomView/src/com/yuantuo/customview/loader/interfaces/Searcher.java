package com.yuantuo.customview.loader.interfaces;

import android.widget.ListAdapter;

public interface Searcher<Type>
{
	public interface OnResultItemSelectListener<Type>
	{
		public  void onResultItemSelectListener( ListAdapter adapter, Type data, int position,
				Searcher<Type> searcher );
	}

	public void onAttachSearchLoader( SearchLoader<Type> loader );
	
	public OnSearchCallBack<Type> getSearchCallBack();
	
	public OnResultCallBack<Type> getResultCallBack();
}
