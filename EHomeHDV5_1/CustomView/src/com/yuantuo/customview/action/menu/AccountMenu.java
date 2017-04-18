package com.yuantuo.customview.action.menu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yuantuo.customview.R;

public class AccountMenu extends ActionPopMenu
{
	private View mFooterView;

	public AccountMenu( Context context )
	{
		super(context);
		mRootView.setBackgroundResource(R.drawable.ios_pop_dialog_bg);
		setAdapter(new PopMenuContentFactory());
		initFooterView();
	}

	private void initFooterView(){
		mFooterView = mLayoutInflater.inflate(R.layout.list_account_menu_item, null);
		ImageView mImageView = (ImageView) mFooterView.findViewById(R.id.imageView_account_image);
		mImageView.setImageDrawable(mResources.getDrawable(R.drawable.icon_menu_change));
		TextView mTextView = (TextView) mFooterView.findViewById(R.id.textView_account);
		mTextView.setText(R.string.hint_account_mange);
	}

	@Override
	protected void ensureList(){
		if (mListView == null){
			if (mAdapter == null) mAdapter = new PopMenuAdapter();
			mListView = (ListView) findViewById(R.id.action_pop_menu_list);
			mListView.addFooterView(mFooterView);
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(this);
		}
		else{
			mListView.setEnabled(mEnable);
		}
	}

	private class PopMenuContentFactory extends BaseAdapter
	{
		@Override
		public int getCount(){
			return mDatas.size();
		}

		@Override
		public Object getItem( int position ){
			return mDatas.get(position);
		}

		@Override
		public long getItemId( int position ){
			return 0;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ){
			ViewHolder holder;
			if (convertView == null){
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.list_account_menu_item, null);

				holder.mAccountImage = (ImageView) convertView.findViewById(R.id.imageView_account_image);
				holder.mAccountTextView = (TextView) convertView.findViewById(R.id.textView_account);
				holder.mAccountCurrentImage = (ImageView) convertView
						.findViewById(R.id.imageView_account_current);

				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mAccountImage.setImageDrawable(mResources.getDrawable(R.drawable.icon_account));

			CharSequence name = mDatas.get(position);
			holder.mAccountTextView.setText(name);

			if (getPopMenuSelection() == position){
				holder.mAccountCurrentImage.setImageDrawable(mResources
						.getDrawable(R.drawable.icon_choosed));
			}
			else{
				holder.mAccountCurrentImage.setImageDrawable(mResources.getDrawable(R.drawable.translate));
			}
			return convertView;
		}
	}
	private static final class ViewHolder
	{
		ImageView mAccountImage;
		TextView mAccountTextView;
		ImageView mAccountCurrentImage;
	}
}