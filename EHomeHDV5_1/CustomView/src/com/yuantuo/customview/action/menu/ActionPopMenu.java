package com.yuantuo.customview.action.menu;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yuantuo.customview.R;
import com.yuantuo.customview.action.AbstractPopWindow;

public class ActionPopMenu extends AbstractPopWindow<CharSequence> implements OnItemClickListener
{
	protected ListView mListView;
	protected PopMenuAdapter mAdapter;
	private ListAdapter mNewAdapter;
	private DataSetOberver mDataSetOberver;

	private final View mTitleView;
	private final TextView mTitleTextView;
	private final View mBottomView;
	private final TextView alertbottom;

	public ActionPopMenu( Context context )
	{
		super(context);
		setContentView(R.layout.list_pop_menu_item_v5);
		setPopMenuEnable(true);
		mRootView.setBackgroundResource(R.drawable.btn_key_normal);
		mTitleView = findViewById(R.id.title_template);
		mBottomView = findViewById(R.id.bottom_template);
		alertbottom = (TextView) findViewById(R.id.alertbottom);
		mTitleTextView = (TextView) findViewById(R.id.alertTitle);
	}

	@Override
	public void addItem( CharSequence[] items ){
		super.addItem(items);
		commit();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public CharSequence removeItem( int pos ){
		CharSequence charSequence = super.removeItem(pos);
		mAdapter.notifyDataSetChanged();
		mCurrentPos = INVALID_POSITION;
		return charSequence;
	}

	@Override
	public boolean removeAll(){
		super.removeAll();
		commit();
		mAdapter.notifyDataSetChanged();
		mCurrentPos = INVALID_POSITION;
		return true;
	}

	@Override
	public boolean commit(){
		ensureList();
		return mListView != null;
	}

	@Override
	public void setPopMenuSelection( int pos ){
		super.setPopMenuSelection(pos);
		commit();
	}

	public void setAdapter( ListAdapter adapter ){
		mNewAdapter = adapter;
	}

	@Override
	protected void onMenuPrepareShow(){
		commit();
	}

	public void setDivider( Drawable divider ){
		commit();
		mListView.setDivider(divider);
	}

	public void setDividerHeight( int height ){
		commit();
		mListView.setDividerHeight(height);
	}

	public void setTitle( int res ){
		setTitle(mResources.getText(res));
	}

	public void setTitle( CharSequence title ){
		if (title != null){
			mTitleTextView.setVisibility(View.VISIBLE);
			mTitleTextView.setText(title);
		}
	}

	public void setBottom( int res ){
		setBottom(mResources.getText(res).toString());
	}
	
	public void setBottom( CharSequence bottom ){
		if (bottom != null){
			alertbottom.setVisibility(View.VISIBLE);
			alertbottom.setText(bottom);
		}
	}
	
	public void setTitleTextColor( int res ){
		mTitleTextView.setTextColor(mResources.getColor(res));
	}

	public void setTitleBackground( int res ){
		mTitleView.setBackgroundResource(res);
	}

	protected void ensureList(){
		if (mListView == null){
			if (mAdapter == null) mAdapter = new PopMenuAdapter();
			mListView = (ListView) findViewById(R.id.action_pop_menu_list);
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(this);
		}
		else{
			mListView.setEnabled(mEnable);
		}
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
		invokeActionPopMenuListener(position);
	}

	private class DataSetOberver extends DataSetObserver
	{
		@Override
		public void onChanged(){
			super.onChanged();
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onInvalidated(){
			super.onInvalidated();
			mAdapter.notifyDataSetInvalidated();
		}
	}

	public View getmBottomView() {
		return mBottomView;
	}

	class PopMenuAdapter extends BaseAdapter
	{
		@Override
		public int getCount(){
			if (mNewAdapter != null){
				return mNewAdapter.getCount();
			}
			else{
				return mDatas.size();
			}
		}

		@Override
		public Object getItem( int position ){
			if (mNewAdapter != null){
				return mNewAdapter.getItem(position);
			}
			else{
				return mDatas.get(position);
			}
		}

		@Override
		public long getItemId( int position ){
			if (mNewAdapter != null){
				return mNewAdapter.getItemId(position);
			}
			else{
				return 0;
			}
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ){
			if (mNewAdapter != null){
				return mNewAdapter.getView(position, convertView, parent);
			}
			else{
				if (convertView == null){
					convertView = mLayoutInflater.inflate(R.layout.list_pop_menu_item, null);
				}
				TextView textView = (TextView) convertView;
				textView.setText(mDatas.get(position));

				if (mCurrentPos == position){
					textView.setBackgroundDrawable(mResources.getDrawable(R.drawable.list_longpressed_holo));
				}
				else{
					textView.setBackgroundDrawable(null);
				}
				return convertView;
			}
		}
		
	}
}