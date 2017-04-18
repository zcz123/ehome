package com.yuantuo.customview.loader;

import android.app.Activity;

import com.yuantuo.customview.loader.impls.AbstractSearchCallBack;
import com.yuantuo.customview.loader.interfaces.OnResultCallBack;
import com.yuantuo.customview.loader.interfaces.OnSearchCallBack;
import com.yuantuo.customview.loader.interfaces.SearchLoaderType;
import com.yuantuo.customview.loader.interfaces.Searcher;

public final class SearchLoaderManager
{
	private static SearchLoaderManager mInstance;

	private SearchLoaderManager()
	{
	}

	public static SearchLoaderManager getInstance(){
		if (mInstance == null) mInstance = new SearchLoaderManager();
		return mInstance;
	}

	/**
	 * @param loaderType
	 *          1.LOADER_TYPE_ON_UI 
	 *          2.LOADER_TYPE_ASYNC
	 * @param searchCallBack
	 *          {@link OnSearchCallBack} {@link OnResultCallBack}
	 *          {@link AbstractSearchCallBack}
	 */
	public <Type> void startLoader( Activity activity, int loaderType, Searcher<Type> searchCallBack ){
		if (SearchLoaderType.LOADER_TYPE_ON_UI == loaderType){
			new SearchLoaderOnUI<Type>(activity, searchCallBack).execute();
		}
		else if (SearchLoaderType.LOADER_TYPE_ASYNC == loaderType){
			new SearchLoaderAsync<Type>(activity, searchCallBack).execute();
		}
	}
}