package com.yuantuo.customview.tabs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuantuo.customview.R;

public class TabPagerSpace
{
	private Context mContext;
	public Indicator mIndicator;
	public TabPagerContent mPagerContent;

	public TabPagerSpace( Context mContext )
	{
		this.mContext = mContext;
	}

	public TabPagerSpace setIndicator( CharSequence label ){
		return setIndicator(label, null);
	}

	public TabPagerSpace setIndicator( CharSequence label, Drawable icon ){
		mIndicator = new LabelAndIconIndicatorImpl(label, icon, 0);
		return this;
	}

	public TabPagerSpace setIndicator( CharSequence label, int indicatorColor ){
		mIndicator = new LabelAndIconIndicatorImpl(label, null, indicatorColor);
		return this;
	}

	public void setFragment( PagerFragment _Fragment ){
		mPagerContent = new FragmentContentImpl(_Fragment);
	}

	public void setView( View _View ){
		mPagerContent = new ViewContentImpl(_View);
	}

	private class LabelAndIconIndicatorImpl implements Indicator
	{
		private final CharSequence mLabel;
		private final Drawable mIcon;
		private final int mLabelColor;

		private LabelAndIconIndicatorImpl( CharSequence label, Drawable icon, int labelIndicatorColor )
		{
			mLabel = label;
			mIcon = icon;
			mLabelColor = labelIndicatorColor;
		}

		public View createIndicatorView( int tabLayoutId ){
			final Context context = mContext;
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View tabIndicator = inflater.inflate(tabLayoutId, null);

			final TextView tv = (TextView) tabIndicator.findViewById(R.id.tab_view_indicator_title);
			tv.setText(mLabel);
			if (mLabelColor != 0) tv.setTextColor(mContext.getResources().getColor(mLabelColor));

			if (mIcon != null){
				final ImageView iv = (ImageView) tabIndicator.findViewById(R.id.tab_view_indicator_icon);
				iv.setVisibility(View.VISIBLE);
				iv.setImageDrawable(mIcon);
			}
			return tabIndicator;
		}
	}

	private class FragmentContentImpl implements TabPagerContent<PagerFragment>
	{
		final PagerFragment mFragment;

		public FragmentContentImpl( PagerFragment fragment )
		{
			mFragment = fragment;
		}

		@Override
		public PagerFragment createContent(){
			return mFragment;
		}
	}

	private class ViewContentImpl implements TabPagerContent<View>
	{
		final View mView;

		public ViewContentImpl( View view )
		{
			mView = view;
		}

		@Override
		public View createContent(){
			return mView;
		}
	}

	public interface Indicator
	{
		View createIndicatorView( int tabLayoutId );
	}

	public interface TabPagerContent<T>
	{
		T createContent();
	}

}