package com.yuantuo.customview.loader.impls;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;

import com.yuantuo.customview.R;
import com.yuantuo.customview.loader.interfaces.OnResultCallBack;
import com.yuantuo.customview.loader.interfaces.OnSearchCallBack;
import com.yuantuo.customview.loader.interfaces.SearchLoader;
import com.yuantuo.customview.ui.CustomProgressDialog;

public abstract class AbstractSearchCallBack<Type>
		implements
			OnResultCallBack<Type>,
			OnSearchCallBack<Type>
{
	private SearchLoader<Type> mSearchLoader;
	protected Context mContext;
	protected Resources mResources;
	protected OnResultItemSelectListener<Type> mOnResultItemSelectListener;

	public AbstractSearchCallBack( Context context )
	{
		this.mContext = context;
		this.mResources = context.getResources();
	}

	public void setOnResultItemSelectListener( OnResultItemSelectListener<Type> listener ){
		mOnResultItemSelectListener = listener;
	}

	@Override
	public void onAttachSearchLoader( SearchLoader<Type> loader ){
		mSearchLoader = loader;
	}

	public DialogInterface getDialogInterface(){
		return mSearchLoader.getDialogInterface();
	}

	public SearchLoader<Type> getSearchLoader(){
		return mSearchLoader;
	}
	
	@Override
	public OnSearchCallBack<Type> getSearchCallBack(){
		return this;
	}
	
	@Override
	public OnResultCallBack<Type> getResultCallBack(){
		return this;
	}

	@Override
	public boolean setProgressDialogShown(){
		return true;
	}

	@Override
	public String getSearchHint(){
		return null;
	}

	@Override
	public int getSearchShowTime(){
		return CustomProgressDialog.DELAYMILLIS_30;
	}

	@Override
	public String getSearchNoResultContent(){
		return "No Data";
	}

	@Override
	public void onSearchSuccess( Type results ){
	}

	@Override
	public void onSearchFail(){
	}

	@Override
	public void onSearchNoResult(){
	}

	@Override
	public String getResultDialogTilte(){
		return null;
	}

	@Override
	public int getResultDialogIcon(){
		return R.drawable.ic_menu_alarm;
	}

	@Override
	public int getResultDialogButton(){
		return R.string.operation_sure;
	}

	@Override
	public CharSequence[] setResultDialogContent( Type results ){
		return null;
	}

	@Override
	public View setResultDialogViewContent( Type results ){
		return null;
	}

	@Override
	public void onResultDialogButtonClicked( DialogInterface dialog ){
		if (dialog != null) dialog.dismiss();
	}

	@Override
	public void onResultDialogSelected( DialogInterface dialog, Type results, int which ){
		if (dialog != null) dialog.dismiss();
	}

	@Override
	public boolean isSingleResultAutoChoose(){
		return false;
	}

	@Override
	public boolean isWantShowResult(){
		return true;
	}
}