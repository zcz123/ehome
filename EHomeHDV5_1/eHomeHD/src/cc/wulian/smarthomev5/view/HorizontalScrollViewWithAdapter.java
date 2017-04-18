package cc.wulian.smarthomev5.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class HorizontalScrollViewWithAdapter extends HorizontalScrollView
{
	private ScrollViewContent mContent;

	public HorizontalScrollViewWithAdapter( Context context )
	{
		super(context);
		addContent(context, null);
	}

	public HorizontalScrollViewWithAdapter( Context context, AttributeSet attrs )
	{
		super(context, attrs);
		addContent(context, attrs);
	}

	public HorizontalScrollViewWithAdapter( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
		addContent(context, attrs);
	}

	private void addContent( Context context, AttributeSet attrs ){
		mContent = new ScrollViewContent(context, attrs);
		mContent.setOrientation(ScrollViewContent.HORIZONTAL);
		addView(mContent);
	}

	public ListAdapter getAdapter(){
		return mContent.getAdapter();
	}

	public void setAdapter( ListAdapter adapter ){
		mContent.setAdapter(adapter);
	}

	public final void setEmptyView( View emptyView ){
		mContent.setEmptyView(emptyView);
	}

	public final View getEmptyView(){
		return mContent.getEmptyView();
	}

	public OnItemClickListener getOnItemClickListener(){
		return mContent.getOnItemClickListener();
	}

	public void setOnItemClickListener( OnItemClickListener onItemClickListener ){
		mContent.setOnItemClickListener(onItemClickListener);
	}

	public void setItemSpacing( int space ){
		mContent.setItemSpacing(space);
	}

	public static class ScrollViewContent extends LinearLayout
	{
		private OnItemClickListener mOnItemClickListener;
		private ListAdapter mAdapter;
		private AdapterDataSetObserver mDataSetObserver;
		private int mItemCount;
		private int mOldItemCount;
		private boolean mDataChanged;
		private View mEmptyView;
		private int mItemSpacing;

		public ScrollViewContent( Context context, AttributeSet attrs )
		{
			super(context, attrs);
		}

		public ScrollViewContent( Context context )
		{
			super(context);
		}

		public OnItemClickListener getOnItemClickListener(){
			return mOnItemClickListener;
		}

		public void setOnItemClickListener( OnItemClickListener onItemClickListener ){
			mOnItemClickListener = onItemClickListener;
		}

		public ListAdapter getAdapter(){
			return mAdapter;
		}

		public void setAdapter( ListAdapter adapter ){
			if (null != mAdapter){
				mAdapter.unregisterDataSetObserver(mDataSetObserver);
				resetList();
			}

			mAdapter = adapter;

			if (mAdapter != null){
				mOldItemCount = mItemCount;
				mItemCount = mAdapter.getCount();
				checkFocus();

				mDataSetObserver = new AdapterDataSetObserver();
				mAdapter.registerDataSetObserver(mDataSetObserver);

				final int position = mItemCount > 0 ? 0 : -1;

				// setSelectedPositionInt(position);
				// setNextSelectedPositionInt(position);

				if (mItemCount == 0){
					// Nothing selected
					// checkSelectionChanged();
				}

			}
			else{
				checkFocus();
				resetList();
				// Nothing selected
				// checkSelectionChanged();
			}

			// requestLayout();
			requestLayoutChild();
		}

		public void requestLayoutChild(){
			removeAllViewsInLayout();
			addChild();
			requestLayout();
			invalidate();
		}

		private void addChild(){
			final ListAdapter adapter = getAdapter();
			if (adapter == null) return;

			int count = adapter.getCount();
			for (int i = 0; i < count; i++){
				View view = adapter.getView(i, null, this);
				if (view == null) throw new NullPointerException("getView == null");

				LayoutParams params = (LayoutParams) view.getLayoutParams();
				if (params == null){
					params = generateDefaultLayoutParams();
				}
				params.setMargins(mItemSpacing, mItemSpacing, mItemSpacing, mItemSpacing);

				final int index = i;
				view.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick( View v ){
						if (getOnItemClickListener() != null){
							getOnItemClickListener().onItemClick(null, v, index, index);
						}
					}
				});
				addViewInLayout(view, -1, params);
			}
		}

		public void setItemSpacing( int space ){
			mItemSpacing = space;
		}

		void checkFocus(){
			final ListAdapter adapter = getAdapter();
			final boolean empty = adapter == null || adapter.getCount() == 0;
			final boolean focusable = !empty || isInFilterMode();
			// The order in which we set focusable in touch mode/focusable may matter
			// for the client, see View.setFocusableInTouchMode() comments for more
			// details
			super.setFocusableInTouchMode(focusable);
			super.setFocusable(focusable);
			if (mEmptyView != null){
				updateEmptyStatus((adapter == null) || adapter.isEmpty());
			}
		}

		@SuppressLint("WrongCall")
		private void updateEmptyStatus( boolean empty ){
			if (isInFilterMode()){
				empty = false;
			}

			if (empty){
				if (mEmptyView != null){
					mEmptyView.setVisibility(View.VISIBLE);
					setVisibility(View.GONE);
				}
				else{
					// If the caller just removed our empty view, make sure the list view is visible
					setVisibility(View.VISIBLE);
				}

				// We are now GONE, so pending layouts will not be dispatched.
				// Force one here to make sure that the state of the list matches
				// the state of the adapter.
				if (mDataChanged){
					this.onLayout(false, getLeft(), getTop(), getRight(), getBottom());
				}
			}
			else{
				if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
				setVisibility(View.VISIBLE);
			}
		}

		/**
		 * Sets the view to show if the adapter is empty.
		 * 
		 * @param emptyView
		 *          the view to show when the adapter is empty
		 */
		public final void setEmptyView( final View emptyView ){
			mEmptyView = emptyView;

			final ListAdapter adapter = getAdapter();
			final boolean empty = ((adapter == null) || adapter.isEmpty());
			updateEmptyStatus(empty);
		}

		public final View getEmptyView(){
			return mEmptyView;
		}

		final boolean isInFilterMode(){
			return false;
		}

		final void resetList(){
			mDataChanged = false;
			// mNeedSync = false;

			removeAllViewsInLayout();
			// mOldSelectedPosition = INVALID_POSITION;
			// mOldSelectedRowId = INVALID_ROW_ID;

			// setSelectedPositionInt(INVALID_POSITION);
			// setNextSelectedPositionInt(INVALID_POSITION);
			invalidate();
		}

		class AdapterDataSetObserver extends DataSetObserver
		{

			private Parcelable mInstanceState = null;

			@Override
			public void onChanged(){
				mDataChanged = true;
				mOldItemCount = mItemCount;
				mItemCount = getAdapter().getCount();

				// Detect the case where a cursor that was previously invalidated has
				// been repopulated with new data.
				if (getAdapter().hasStableIds() && mInstanceState != null && mOldItemCount == 0
						&& mItemCount > 0){
					onRestoreInstanceState(mInstanceState);
					mInstanceState = null;
				}
				else{
					// rememberSyncState();
				}
				checkFocus();
				// requestLayout();
				requestLayoutChild();
			}

			@Override
			public void onInvalidated(){
				mDataChanged = true;

				if (getAdapter().hasStableIds()){
					// Remember the current state for the case where our hosting activity is being
					// stopped and later restarted
					mInstanceState = onSaveInstanceState();
				}

				// Data is invalid so we should reset our state
				mOldItemCount = mItemCount;
				mItemCount = 0;
				// mSelectedPosition = INVALID_POSITION;
				// mSelectedRowId = INVALID_ROW_ID;
				// mNextSelectedPosition = INVALID_POSITION;
				// mNextSelectedRowId = INVALID_ROW_ID;
				// mNeedSync = false;
				// checkSelectionChanged();

				checkFocus();
				// requestLayout();
				requestLayoutChild();
			}

			public void clearSavedState(){
				mInstanceState = null;
			}
		}
	}
}
