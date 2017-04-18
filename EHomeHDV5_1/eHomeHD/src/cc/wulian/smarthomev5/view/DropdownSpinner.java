package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.yuantuo.customview.ui.ListPopupWindow;

public class DropdownSpinner extends AbsSpinner
{
	// Only measure this many items to get a decent max width.
	private static final int MAX_ITEMS_MEASURED = 15;

	private final DropdownPopup mPopup;
	private DropDownAdapter mTempAdapter;
	int mDropDownWidth;

	private int mGravity;
	private final boolean mDisableChildrenWhenDisabled;

	private final Rect mTempRect = new Rect();

	public DropdownSpinner( Context context )
	{
		this(context, null);
	}

	public DropdownSpinner( Context context, AttributeSet attrs )
	{
		this(context, attrs, 0);
	}

	public DropdownSpinner( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);

		DropdownPopup popup = new DropdownPopup(context, attrs, defStyle);

		mDropDownWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
		mPopup = popup;

		mGravity = Gravity.CENTER;
		mDisableChildrenWhenDisabled = false;

		// Base constructor can call setAdapter before we initialize mPopup.
		// Finish setting things up if this happened.
		if (mTempAdapter != null){
			mPopup.setAdapter(mTempAdapter);
			mTempAdapter = null;
		}
	}

	/**
	 * Set the background drawable for the spinner's popup window of choices. Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in other modes.
	 * 
	 * @param background
	 *          Background drawable
	 * 
	 * @attr ref android.R.styleable#Spinner_popupBackground
	 */
	public void setPopupBackgroundDrawable( Drawable background ){
		if (!(mPopup instanceof DropdownPopup)){
			return;
		}
		mPopup.setBackgroundDrawable(background);
	}

	/**
	 * Set the background drawable for the spinner's popup window of choices. Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in other modes.
	 * 
	 * @param resId
	 *          Resource ID of a background drawable
	 * 
	 * @attr ref android.R.styleable#Spinner_popupBackground
	 */
	public void setPopupBackgroundResource( int resId ){
		setPopupBackgroundDrawable(getContext().getResources().getDrawable(resId));
	}

	/**
	 * Get the background drawable for the spinner's popup window of choices. Only valid in {@link #MODE_DROPDOWN}; other modes will return null.
	 * 
	 * @return background Background drawable
	 * 
	 * @attr ref android.R.styleable#Spinner_popupBackground
	 */
	public Drawable getPopupBackground(){
		return mPopup.getBackground();
	}

	/**
	 * Set a vertical offset in pixels for the spinner's popup window of choices. Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in other modes.
	 * 
	 * @param pixels
	 *          Vertical offset in pixels
	 * 
	 * @attr ref android.R.styleable#Spinner_dropDownVerticalOffset
	 */
	public void setDropDownVerticalOffset( int pixels ){
		mPopup.setVerticalOffset(pixels);
	}

	/**
	 * Get the configured vertical offset in pixels for the spinner's popup window of choices. Only valid in {@link #MODE_DROPDOWN}; other modes will return 0.
	 * 
	 * @return Vertical offset in pixels
	 * 
	 * @attr ref android.R.styleable#Spinner_dropDownVerticalOffset
	 */
	public int getDropDownVerticalOffset(){
		return mPopup.getVerticalOffset();
	}

	/**
	 * Set a horizontal offset in pixels for the spinner's popup window of choices. Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in other modes.
	 * 
	 * @param pixels
	 *          Horizontal offset in pixels
	 * 
	 * @attr ref android.R.styleable#Spinner_dropDownHorizontalOffset
	 */
	public void setDropDownHorizontalOffset( int pixels ){
		mPopup.setHorizontalOffset(pixels);
	}

	/**
	 * Get the configured horizontal offset in pixels for the spinner's popup window of choices. Only valid in {@link #MODE_DROPDOWN}; other modes will return 0.
	 * 
	 * @return Horizontal offset in pixels
	 * 
	 * @attr ref android.R.styleable#Spinner_dropDownHorizontalOffset
	 */
	public int getDropDownHorizontalOffset(){
		return mPopup.getHorizontalOffset();
	}

	/**
	 * Set the width of the spinner's popup window of choices in pixels. This value may also be set to {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT} to
	 * match the width of the Spinner itself, or {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} to wrap to the measured size of contained dropdown list
	 * items.
	 * 
	 * <p>
	 * Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in other modes.
	 * </p>
	 * 
	 * @param pixels
	 *          Width in pixels, WRAP_CONTENT, or MATCH_PARENT
	 * 
	 * @attr ref android.R.styleable#Spinner_dropDownWidth
	 */
	public void setDropDownWidth( int pixels ){
		mDropDownWidth = pixels;
	}

	/**
	 * Get the configured width of the spinner's popup window of choices in pixels. The returned value may also be
	 * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT} meaning the popup window will match the width of the Spinner itself, or
	 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} to wrap to the measured size of contained dropdown list items.
	 * 
	 * @return Width in pixels, WRAP_CONTENT, or MATCH_PARENT
	 * 
	 * @attr ref android.R.styleable#Spinner_dropDownWidth
	 */
	public int getDropDownWidth(){
		return mDropDownWidth;
	}

	@Override
	public void setEnabled( boolean enabled ){
		super.setEnabled(enabled);
		if (mDisableChildrenWhenDisabled){
			final int count = getChildCount();
			for (int i = 0; i < count; i++){
				getChildAt(i).setEnabled(enabled);
			}
		}
	}

	/**
	 * Describes how the selected item view is positioned. Currently only the horizontal component is used. The default is determined by the current theme.
	 * 
	 * @param gravity
	 *          See {@link android.view.Gravity}
	 * 
	 * @attr ref android.R.styleable#Spinner_gravity
	 */
	public void setGravity( int gravity ){
		if (mGravity != gravity){
			if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == 0){
				gravity |= Gravity.LEFT;
			}
			mGravity = gravity;
			requestLayout();
		}
	}

	/**
	 * Describes how the selected item view is positioned. The default is determined by the current theme.
	 * 
	 * @return A {@link android.view.Gravity Gravity} value
	 */
	public int getGravity(){
		return mGravity;
	}

	@Override
	public void setAdapter( SpinnerAdapter adapter ){
		super.setAdapter(adapter);

		if (mPopup != null){
			mPopup.setAdapter(new DropDownAdapter(adapter));
		}
		else{
			mTempAdapter = new DropDownAdapter(adapter);
		}
	}

	@Override
	protected void onDetachedFromWindow(){
		super.onDetachedFromWindow();

		if (mPopup != null && mPopup.isShowing()){
			mPopup.dismiss();
		}
	}

	/**
	 * <p>
	 * A spinner does not support item click events. Calling this method will raise an exception.
	 * </p>
	 * 
	 * @param l
	 *          this listener will be ignored
	 */
	@Override
	public void setOnItemClickListener( OnItemClickListener l ){
		throw new RuntimeException("setOnItemClickListener cannot be used with a spinner.");
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mPopup != null && MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST){
			final int measuredWidth = getMeasuredWidth();
			setMeasuredDimension(Math.min(
					Math.max(measuredWidth, measureContentWidth(getAdapter(), getBackground())),
					MeasureSpec.getSize(widthMeasureSpec)), getMeasuredHeight());
		}
	}

	@Override
	public boolean performClick(){
		boolean handled = super.performClick();

		if (!handled){
			handled = true;

			if (!mPopup.isShowing()){
				mPopup.show();
			}
		}

		return handled;
	}


	int measureContentWidth( SpinnerAdapter adapter, Drawable background ){
		if (adapter == null){ return 0; }

		int width = 0;
		View itemView = null;
		int itemType = 0;
		final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		// Make sure the number of items we'll measure is capped. If it's a huge data set
		// with wildly varying sizes, oh well.
		int start = Math.max(0, getSelectedItemPosition());
		final int end = Math.min(adapter.getCount(), start + MAX_ITEMS_MEASURED);
		final int count = end - start;
		start = Math.max(0, start - (MAX_ITEMS_MEASURED - count));
		for (int i = start; i < end; i++){
			final int positionType = adapter.getItemViewType(i);
			if (positionType != itemType){
				itemType = positionType;
				itemView = null;
			}
			itemView = adapter.getView(i, itemView, this);
			if (itemView.getLayoutParams() == null){
				itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
			}
			itemView.measure(widthMeasureSpec, heightMeasureSpec);
			width = Math.max(width, itemView.getMeasuredWidth());
		}

		// Add background padding to measured width
		if (background != null){
			background.getPadding(mTempRect);
			width += mTempRect.left + mTempRect.right;
		}

		return width;
	}

	/**
	 * <p>
	 * Wrapper class for an Adapter. Transforms the embedded Adapter instance into a ListAdapter.
	 * </p>
	 */
	private static class DropDownAdapter implements ListAdapter, SpinnerAdapter
	{
		private final SpinnerAdapter mAdapter;
		private ListAdapter mListAdapter;

		/**
		 * <p>
		 * Creates a new ListAdapter wrapper for the specified adapter.
		 * </p>
		 * 
		 * @param adapter
		 *          the Adapter to transform into a ListAdapter
		 */
		public DropDownAdapter( SpinnerAdapter adapter )
		{
			this.mAdapter = adapter;
			if (adapter instanceof ListAdapter){
				this.mListAdapter = (ListAdapter) adapter;
			}
		}

		@Override
		public int getCount(){
			return mAdapter == null ? 0 : mAdapter.getCount();
		}

		@Override
		public Object getItem( int position ){
			return mAdapter == null ? null : mAdapter.getItem(position);
		}

		@Override
		public long getItemId( int position ){
			return mAdapter == null ? -1 : mAdapter.getItemId(position);
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ){
			return getDropDownView(position, convertView, parent);
		}

		@Override
		public View getDropDownView( int position, View convertView, ViewGroup parent ){
			return mAdapter == null ? null : mAdapter.getDropDownView(position, convertView, parent);
		}

		@Override
		public boolean hasStableIds(){
			return mAdapter != null && mAdapter.hasStableIds();
		}

		@Override
		public void registerDataSetObserver( DataSetObserver observer ){
			if (mAdapter != null){
				mAdapter.registerDataSetObserver(observer);
			}
		}

		@Override
		public void unregisterDataSetObserver( DataSetObserver observer ){
			if (mAdapter != null){
				mAdapter.unregisterDataSetObserver(observer);
			}
		}

		/**
		 * If the wrapped SpinnerAdapter is also a ListAdapter, delegate this call. Otherwise, return true.
		 */
		@Override
		public boolean areAllItemsEnabled(){
			final ListAdapter adapter = mListAdapter;
			if (adapter != null){
				return adapter.areAllItemsEnabled();
			}
			else{
				return true;
			}
		}

		/**
		 * If the wrapped SpinnerAdapter is also a ListAdapter, delegate this call. Otherwise, return true.
		 */
		@Override
		public boolean isEnabled( int position ){
			final ListAdapter adapter = mListAdapter;
			if (adapter != null){
				return adapter.isEnabled(position);
			}
			else{
				return true;
			}
		}

		@Override
		public int getItemViewType( int position ){
			return 0;
		}

		@Override
		public int getViewTypeCount(){
			return 1;
		}

		@Override
		public boolean isEmpty(){
			return getCount() == 0;
		}
	}

	private class DropdownPopup extends ListPopupWindow
	{
		private ListAdapter mAdapter;

		public DropdownPopup( Context context, AttributeSet attrs, int defStyleRes )
		{
			super(context, attrs, 0, defStyleRes);

			setAnchorView(DropdownSpinner.this);
			setModal(true);
			setPromptPosition(POSITION_PROMPT_ABOVE);
			setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView<?> parent, View v, int position, long id ){
					DropdownSpinner.this.setSelection(position);
					if (getOnItemClickListener() != null){
						DropdownSpinner.this.performItemClick(v, position, mAdapter.getItemId(position));
					}
					dismiss();
				}
			});
		}

		@Override
		public void setAdapter( ListAdapter adapter ){
			super.setAdapter(adapter);
			mAdapter = adapter;
		}

		@Override
		public void show(){
			final Drawable background = getBackground();
			int bgOffset = 0;
			if (background != null){
				background.getPadding(mTempRect);
				bgOffset = -mTempRect.left;
			}
			else{
				mTempRect.left = mTempRect.right = 0;
			}

			final int spinnerPaddingLeft = DropdownSpinner.this.getPaddingLeft();
			if (mDropDownWidth == WRAP_CONTENT){
				final int spinnerWidth = DropdownSpinner.this.getWidth();
				final int spinnerPaddingRight = DropdownSpinner.this.getPaddingRight();

				int contentWidth = measureContentWidth((SpinnerAdapter) mAdapter, getBackground());
				final int contentWidthLimit = getContext().getResources().getDisplayMetrics().widthPixels
						- mTempRect.left - mTempRect.right;
				if (contentWidth > contentWidthLimit){
					contentWidth = contentWidthLimit;
				}

				setContentWidth(Math.max(contentWidth, spinnerWidth - spinnerPaddingLeft
						- spinnerPaddingRight));
			}
			else if (mDropDownWidth == MATCH_PARENT){
				final int spinnerWidth = DropdownSpinner.this.getWidth();
				final int spinnerPaddingRight = DropdownSpinner.this.getPaddingRight();
				setContentWidth(spinnerWidth - spinnerPaddingLeft - spinnerPaddingRight);
			}
			else{
				setContentWidth(mDropDownWidth);
			}
			setHorizontalOffset(bgOffset + spinnerPaddingLeft);
			setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
			super.show();
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			setSelection(DropdownSpinner.this.getSelectedItemPosition());
		}
	}
}
