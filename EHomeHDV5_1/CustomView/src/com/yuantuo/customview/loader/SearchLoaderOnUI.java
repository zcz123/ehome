package com.yuantuo.customview.loader;

import android.app.Activity;
import android.content.DialogInterface;

import com.yuantuo.customview.loader.interfaces.SearchLoader;
import com.yuantuo.customview.loader.interfaces.Searcher;

final class SearchLoaderOnUI<Type> implements SearchLoader<Type>
{
	private SearchLoader<Type> mSearchLoader;

	public SearchLoaderOnUI( Activity activity, Searcher<Type> searchCallBack )
	{
		mSearchLoader = new SearchLoaderImpl<Type>(activity, searchCallBack);
	}

	@Override
	public void onPreExecute(){
		mSearchLoader.onPreExecute();
	}

	@Override
	public Type doInBackground( Void... params ){
		return mSearchLoader.doInBackground(params);
	}

	@Override
	public void onProgressUpdate( Object progress ){
		mSearchLoader.onProgressUpdate(progress);
	}

	@Override
	public void onPostExecute( Type result ){
		mSearchLoader.onPostExecute(result);
	}

	@Override
	public DialogInterface getDialogInterface(){
		return mSearchLoader.getDialogInterface();
	}

	public void execute( Void... params ){
		onPreExecute();
		onProgressUpdate(null);
		Type result = doInBackground(params);
		onPostExecute(result);
	}
}