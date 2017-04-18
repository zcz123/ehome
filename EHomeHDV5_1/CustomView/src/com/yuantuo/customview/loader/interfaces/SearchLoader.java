package com.yuantuo.customview.loader.interfaces;

import android.content.DialogInterface;

public interface SearchLoader<Type>
{
	public void onPreExecute();

	public Type doInBackground( Void... params );

	public void onProgressUpdate( Object progress );

	public void onPostExecute( Type result );

	public DialogInterface getDialogInterface();
}
