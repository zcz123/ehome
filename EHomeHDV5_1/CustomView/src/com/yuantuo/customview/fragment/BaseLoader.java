package com.yuantuo.customview.fragment;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class BaseLoader<D> extends AsyncTaskLoader<D>
{
	private D mResult;

	public BaseLoader( Context context )
	{
		super(context);
	}

	@Override
	public void deliverResult( D data ){
		mResult = data;
		if (isStarted()){
			super.deliverResult(data);
		}
	}

	@Override
	protected void onStartLoading(){
		if (mResult != null) deliverResult(mResult);
		if (takeContentChanged() || mResult == null) forceLoad();
	}

	@Override
	protected void onReset(){
		super.onReset();
		onStopLoading();
		if (mResult != null) mResult = null;
	}

	@Override
	protected void onStopLoading(){
		cancelLoad();
	}
}