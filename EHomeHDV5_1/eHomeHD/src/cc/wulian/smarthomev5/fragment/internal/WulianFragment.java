package cc.wulian.smarthomev5.fragment.internal;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.LayoutInflater;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.event.DialogEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import com.actionbarsherlock.app.SherlockFragment;

import cc.wulian.smarthomev5.tools.SendMessage;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;

public abstract class WulianFragment extends SherlockFragment {
	protected EventBus mEventBus = EventBus.getDefault();
	protected MainApplication mApplication = MainApplication.getApplication();
	protected AccountManager mAccountManger = AccountManager.getAccountManger();
	protected ProgressDialogManager mDialogManager = ProgressDialogManager
			.getDialogManager();
	protected ActionBarCompat mActionBarCompat;
	protected BaseActivity mActivity;
	protected Resources resources;
	protected LayoutInflater inflater;

	public EventBus getEventBus() {
		return mEventBus;
	}

	public MainApplication getApplication() {
		return mApplication;
	}

	public AccountManager getAccountManger() {
		return mAccountManger;
	}

	public ActionBarCompat getSupportActionBar() {
		return mActionBarCompat;
	}

	public ProgressDialogManager getDialogManager() {
		return mDialogManager;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		resources = activity.getResources();
		inflater = activity.getLayoutInflater();
		mActivity = (BaseActivity) activity;
		mActionBarCompat = mActivity.getCompatActionBar();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		mEventBus.register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		mEventBus.unregister(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	/*
	 * @Override public boolean onOptionsItemSelected( MenuItem item ){ if
	 * (item.getItemId() == android.R.id.home){ popBackStack(); return false; }
	 * return super.onOptionsItemSelected(item); }
	 */

	public void reloadData(int id, LoaderCallbacks<?> callback) {
		getLoaderManager().restartLoader(id, null, callback);
	}

	public void loadDataOnce(int id, LoaderCallbacks<?> callback) {
		getLoaderManager().initLoader(id, null, callback);
	}

	public void startFragment(int viewLayoutID, Fragment fragment,
			boolean needBack, boolean replace, String tag) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (replace) {
			ft.replace(viewLayoutID, fragment, tag);
		} else {
			ft.add(viewLayoutID, fragment, tag);
		}
		if (needBack)
			ft.addToBackStack(null);
		ft.commit();
	}

	public void showFragment(int viewLayoutID, Fragment fragment,
			boolean hideParent, String tag) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (hideParent)
			ft.hide(this);
		ft.add(viewLayoutID, fragment, tag);
		ft.addToBackStack(null);
		ft.commit();
	}

	public Fragment findFragment(String tag) {
		return getFragmentManager().findFragmentByTag(tag);
	}

	public Fragment findFragment(int viewLayoutID) {
		return getFragmentManager().findFragmentById(viewLayoutID);
	}

	public void removeFragment(int viewLayoutID) {
		Fragment fragment = findFragment(viewLayoutID);
		removeFragment(fragment);
	}

	public void removeFragment(String tag) {
		Fragment fragment = findFragment(tag);
		removeFragment(fragment);
	}

	protected void removeFragment(Fragment fragment) {
		if (fragment != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.remove(fragment).commit();
		}
	}

	public void popBackStack() {
		getFragmentManager().popBackStack();
	}

	protected boolean isUsedLeftSliding() {
		return false;
	}

	public void onShow() {
	}

	public void onHide() {

	}

	public void onEvent(NoSubscriberEvent event) {

	}

	public void onEventMainThread(DialogEvent event) {
		ProgressDialogManager dialogManager = getDialogManager();
		if (dialogManager.containsDialog(SendMessage.ACTION_SET_DEVICE
				+ event.actionKey)) {
			dialogManager.dimissDialog(SendMessage.ACTION_SET_DEVICE
					+ event.actionKey, event.resultCode);
		} else if (dialogManager.containsDialog(event.actionKey)) {
			dialogManager.dimissDialog(event.actionKey, event.resultCode);
		}
	}

}