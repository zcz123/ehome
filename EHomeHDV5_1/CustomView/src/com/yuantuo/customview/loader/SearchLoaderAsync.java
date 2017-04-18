package com.yuantuo.customview.loader;

import android.app.Activity;
import android.os.AsyncTask;

import com.yuantuo.customview.loader.interfaces.SearchLoader;
import com.yuantuo.customview.loader.interfaces.Searcher;

final class SearchLoaderAsync<Type> extends AsyncTask<Void, Void, Type>
{
	private SearchLoader<Type> mSearchLoader;

	public SearchLoaderAsync( Activity activity, Searcher<Type> searchCallBack )
	{
		super();
		mSearchLoader = new SearchLoaderImpl<Type>(activity, searchCallBack);
	}

	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		mSearchLoader.onPreExecute();
	}

	@Override
	protected Type doInBackground( Void... params ){
		return mSearchLoader.doInBackground(params);
	}

	@Override
	protected void onPostExecute( Type result ){
		super.onPostExecute(result);
		mSearchLoader.onPostExecute(result);
	}
}