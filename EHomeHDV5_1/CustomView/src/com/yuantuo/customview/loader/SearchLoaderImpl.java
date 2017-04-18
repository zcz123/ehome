package com.yuantuo.customview.loader;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;

import com.yuantuo.customview.loader.interfaces.OnResultCallBack;
import com.yuantuo.customview.loader.interfaces.OnSearchCallBack;
import com.yuantuo.customview.loader.interfaces.SearchLoader;
import com.yuantuo.customview.loader.interfaces.Searcher;
import com.yuantuo.customview.ui.CustomDialog;
import com.yuantuo.customview.ui.CustomProgressDialog;
import com.yuantuo.customview.ui.CustomProgressDialog.OnDialogDismissListener;
import com.yuantuo.customview.ui.WLDialog;

final class SearchLoaderImpl<Type>
		implements
			SearchLoader<Type>,
			OnDialogDismissListener,
			OnDismissListener,
			OnCancelListener,
			OnClickListener
{
	private Activity mActivity;
	private CustomProgressDialog mProgressDialog;
	private OnSearchCallBack<Type> mOnSearchCallBack;
	private OnResultCallBack<Type> mOnResultCallBack;
	private Type mSearchResult;
	private CustomDialog mCustomDialog;

	public SearchLoaderImpl( Activity activity, Searcher<Type> searchCallBack )
	{
		mActivity = activity;
		mProgressDialog = new CustomProgressDialog(activity);

		if (searchCallBack == null)
			throw new NullPointerException("onSearchCallBack == null || onResultCallBack == null ");

		searchCallBack.onAttachSearchLoader(this);
		mOnSearchCallBack = searchCallBack.getSearchCallBack();
		mOnResultCallBack = searchCallBack.getResultCallBack();
	}

	@Override
	public void onPreExecute(){
		if (mOnSearchCallBack.setProgressDialogShown()){
			mProgressDialog.showDialog( mOnSearchCallBack.getSearchHint(),
					mOnSearchCallBack.getSearchShowTime());
			mProgressDialog.setOnDialogDismissListener(this);
		}
	}

	@Override
	public Type doInBackground( Void... params ){
		return mOnSearchCallBack.onSearching();
	}

	@Override
	public void onPostExecute( Type result ){
		mSearchResult = result;
		handleSearchResult();
	}

	@Override
	public void onProgressUpdate( Object progress ){
	}

	@Override
	public final DialogInterface getDialogInterface(){
		return mCustomDialog;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void handleSearchResult(){
		final Type temp = mSearchResult;
		Collection result = null;
		if (temp instanceof Collection){
			result = (Collection) temp;
		}
		else if (temp instanceof Object[]){
			Object[] objects = (Object[]) temp;
			result = new ArrayList();
			for (int i = 0; i < objects.length; i++){
				result.add(objects[i]);
			}
		}

		if (result == null || result.isEmpty()){
			if (mOnSearchCallBack.wantHandleSearchNoResult()){
				mProgressDialog.dismissProgressDialog();
				mOnSearchCallBack.onSearchNoResult();
			}
			else {
				mProgressDialog.dismissProgressDialog(-1);
			}
		}
		else if(!mOnResultCallBack.isWantShowResult()){
			mProgressDialog.dismissProgressDialog();
			mOnSearchCallBack.onSearchSuccess(temp);
		}
		else{
			mProgressDialog.dismissProgressDialog();
			showSearchResultDialog();
			if (result.size() == 1 && mOnResultCallBack.isSingleResultAutoChoose()){
				mOnResultCallBack.onResultDialogSelected(mCustomDialog, temp, 0);
			}
		}
	}

	private void showSearchResultDialog(){
		CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);
		builder.setIcon(mOnResultCallBack.getResultDialogIcon());
		builder.setTitle(mOnResultCallBack.getResultDialogTilte());
		builder.setPositiveButton(mOnResultCallBack.getResultDialogButton(), this);
		builder.setAutoDismiss(false);
		if (mOnResultCallBack.isCustomResultView()){
			builder.setContentView(mOnResultCallBack.setResultDialogViewContent(mSearchResult));
		}
		else{
			CharSequence[] charSequences = mOnResultCallBack.setResultDialogContent(mSearchResult);
			builder.setItems(charSequences, this);
		}
		mCustomDialog = builder.create(false, false);
		mCustomDialog.setOnCancelListener(this);
		mCustomDialog.setOnDismissListener(this);
		mCustomDialog.show();
	}

	// dialog
	@Override
	public void onClick( DialogInterface dialog, int which ){
		if (DialogInterface.BUTTON_POSITIVE == which){
			mOnResultCallBack.onResultDialogButtonClicked(dialog);
		}
		else if (DialogInterface.BUTTON_NEGATIVE == which){

		}
		else if (DialogInterface.BUTTON_NEUTRAL == which){

		}
		else{
			mOnResultCallBack.onResultDialogSelected(dialog, mSearchResult, which);
		}
	}

	// dialog
	@Override
	public void onCancel( DialogInterface dialog ){
		mOnSearchCallBack.onSearchFail();
	}

	// dialog
	@Override
	public void onDismiss( DialogInterface dialog ){
//		mOnSearchCallBack.onSearchFail();
	}

	// progressDialog
	@Override
	public void onDismiss( CustomProgressDialog progressDialog, int result ){
		if (result != 0){
			new WLDialog.Builder(mActivity)
//			.setIcon(mOnResultCallBack.getResultDialogIcon())
			.setTitle(mOnResultCallBack.getResultDialogTilte())
			.setMessage(mOnSearchCallBack.getSearchNoResultContent())
			.setPositiveButton(mOnResultCallBack.getResultDialogButton())
//			.setAutoDismiss(true)
//			.setSingleton(false)
//			.create(true, false)
			.create()
			.show();
			mOnSearchCallBack.onSearchFail();
		}
		else{
			mOnSearchCallBack.onSearchSuccess(mSearchResult);
		}
	}
}