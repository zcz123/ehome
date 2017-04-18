package com.yuantuo.customview.tabs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuantuo.customview.R;

public class TabView extends LinearLayout
{
	private Context mContext;

	private int mSelectedTab = -1;
	private Drawable mDividerDrawable;
	private int mIndicatorRes;
	private int mIndicatorHeight;
	private int mTabLayoutId;
	private int mTabCount;

	private OnTabChangedListener mOnTabChangedListener;

	public TabView( Context context )
	{
		this(context, null);
	}

	public TabView( Context context, AttributeSet attrs )
	{
		this(context, attrs, R.attr.tabViewStyle);
	}

	public TabView( Context context, AttributeSet attrs, int defStyleAttr )
	{
		super(context, attrs);
		mContext = context;

		final TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.TabView, defStyleAttr, 0);
		setTabLayoutId(a.getResourceId(R.styleable.TabView_tabLayout, 0));
		setDividerDrawable(a.getDrawable(R.styleable.TabView_tabDivider));
		setTabIndicatorResource(a.getResourceId(R.styleable.TabView_indicator, 0));
		mTabCount = a.getInt(R.styleable.TabView_tabCount, 0);
		a.recycle();

		initTabWidget();
		setTabCount(mTabCount);
	}

	private void initTabWidget(){
		setOrientation(LinearLayout.HORIZONTAL);
		mIndicatorHeight = getResources().getDimensionPixelSize(R.dimen.tabView_default);

		setFocusable(true);
	}

	void setTabLayoutId( int id ){
		mTabLayoutId = id;
		if (id == 0){
			mTabLayoutId = R.layout.tab_indicator;
		}
	}

	void setTabCount( int count ){
		mTabCount = count;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < count; i++){
			View tabIndicator = inflater.inflate(mTabLayoutId, null);

			final TextView tv = (TextView) tabIndicator.findViewById(R.id.tab_view_indicator_title);
			tv.setText("Tab#" + i);
			addView(tabIndicator);
			if (mSelectedTab == -1){
				mSelectedTab = 0;
				setCurrentTab(0);
			}
		}
	}

	public View getIndicatorViewAt( int index ){
		View child = getChildTabViewAt(index);
		if (child == null) return null;
		return child.findViewById(R.id.tab_view_indicator_title);
	}

	public void addTab( TabPagerSpace space ){
		View tabIndicatorView = space.mIndicator.createIndicatorView(mTabLayoutId);
		addView(tabIndicatorView);
		if (mSelectedTab == -1){
			mSelectedTab = 0;
			setCurrentTab(0);
		}
	}

	public void setTabGone( int whichTab ){
		getChildTabViewAt(whichTab).setVisibility(GONE);
		if (mDividerDrawable != null){
			whichTab *= 2;
			View view = getChildAt(++whichTab);
			if (view == null) return;
			view.setVisibility(GONE);
		}
	}

	@Override
	public void removeViewAt( int index ){
		View tab = getChildTabViewAt(index);
		if (tab != null){
			removeViewInLayout(tab);

			if (mDividerDrawable != null){
				if (index != 0) ++index;
				View view = getChildAt(index);
				if (view != null) removeViewInLayout(view);
			}

			if (mSelectedTab != 0) mSelectedTab--;
			setCurrentTab(mSelectedTab);

			requestLayout();
			invalidate();
		}
	}

	public int getCurrentTab(){
		return mSelectedTab;
	}

	public void setTabIndicatorResource( int resource ){
		if (mIndicatorRes == resource) return;

		if (resource != 0){
			mIndicatorRes = resource;
		}
	}

	public int getTabIndicatorResource(){
		return mIndicatorRes;
	}

	@Override
	protected int getChildDrawingOrder( int childCount, int i ){
		if (i == childCount - 1){
			return mSelectedTab;
		}
		else if (i >= mSelectedTab){
			return i + 1;
		}
		else{
			return i;
		}
	}

	/**
	 * Returns the tab indicator view at the given index.
	 * 
	 * @param index
	 *          the zero-based index of the tab indicator view to return
	 * @return the tab indicator view at the given index
	 */
	public View getChildTabViewAt( int index ){
		// If we are using dividers, then instead of tab views at 0, 1, 2, ...
		// we have tab views at 0, 2, 4, ...
		if (mDividerDrawable != null){
			index *= 2;
		}
		return getChildAt(index);
	}

	/**
	 * Returns the number of tab indicator views.
	 * 
	 * @return the number of tab indicator views.
	 */
	public int getTabCount(){
		int children = getChildCount();

		// If we have dividers, then we will always have an odd number of
		// children: 1, 3, 5, ... and we want to convert that sequence to
		// this: 1, 2, 3, ...
		if (mDividerDrawable != null){
			children = (children + 1) / 2;
		}
		return children;
	}

	/**
	 * Sets the drawable to use as a divider between the tab indicators.
	 */
	public void setDividerDrawable( Drawable drawable ){
		mDividerDrawable = drawable;
		requestLayout();
		invalidate();
	}

	/**
	 * Sets the drawable to use as a divider between the tab indicators.
	 */
	public void setDividerDrawable( int resId ){
		mDividerDrawable = mContext.getResources().getDrawable(resId);
		requestLayout();
		invalidate();
	}

	public void setCurrentTab( int index ){
		if (index < 0 || index >= getTabCount()){ return; }
		// if (mSelectedTab == index) return;

		getChildTabViewAt(mSelectedTab).setSelected(false);
		mSelectedTab = index;
		getChildTabViewAt(mSelectedTab).setSelected(true);
	}

	@Override
	public void setEnabled( boolean enabled ){
		super.setEnabled(enabled);
		int count = getTabCount();

		for (int i = 0; i < count; i++){
			View child = getChildTabViewAt(i);
			child.setEnabled(enabled);
		}
	}

	@Override
	public void addView( View child ){
		if (child.getLayoutParams() == null){
			final int height = mIndicatorHeight;
			final LinearLayout.LayoutParams lp = new LayoutParams(0, height, 1.0f);
			lp.setMargins(0, 0, 0, 0);
			child.setLayoutParams(lp);
		}

		final int indicatorRes = mIndicatorRes;
		child.setBackgroundResource(indicatorRes);

		child.setFocusable(true);
		child.setClickable(true);

		// If we have dividers between the tabs and we already have at least one
		// tab, then add a divider before adding the next tab.
		if (mDividerDrawable != null && getTabCount() > 0){
			ImageView divider = new ImageView(mContext);
			final LinearLayout.LayoutParams lp = new LayoutParams(mDividerDrawable.getIntrinsicWidth(),
					LayoutParams.FILL_PARENT);
			lp.setMargins(0, 5, 0, 5);
			divider.setAlpha(85);
			divider.setLayoutParams(lp);
			divider.setBackgroundDrawable(mDividerDrawable);
			super.addView(divider);
		}
		super.addView(child);

		TabClickListener tabClickListener = new TabClickListener(getTabCount() - 1);
		child.setOnClickListener(tabClickListener);
		child.setOnLongClickListener(tabClickListener);
	}

	public void setOnTabChangedListener( OnTabChangedListener listener ){
		mOnTabChangedListener = listener;
	}

	private class TabClickListener implements OnClickListener, OnLongClickListener
	{

		private final int mTabIndex;

		private TabClickListener( int tabIndex )
		{
			mTabIndex = tabIndex;
		}

		@Override
		public void onClick( View v ){
			boolean isHandled = mOnTabChangedListener == null ? false : mOnTabChangedListener.onPreTabChange(mTabIndex);
			if(!isHandled){
				setCurrentTab(mTabIndex);
				if (mOnTabChangedListener != null){
					mOnTabChangedListener.onTabChanged(mTabIndex);
				}
			}
		}

		@Override
		public boolean onLongClick( View v ){
			if (mOnTabChangedListener != null){
				mOnTabChangedListener.onTabLongClicked(mTabIndex);
				return true;
			}
			return false;
		}
	}

	/**
	 * when tab changed, notify this event
	 */
	public interface OnTabChangedListener
	{
		/**
		 * before tab {@link TabView#setCurrentTab(int)},
		 * before {@link #onTabChanged(int)}
		 * 
		 * @return 
		 * 	isHandled the tab change event,
		 * 	false means can change tab,
		 * 	true means can not change tab
		 */
		public boolean onPreTabChange(int index);
		
		public void onTabChanged( int tabIndex );

		public void onTabLongClicked( int tabIndex );
	}
}