package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class WLBaseAdapter<D> extends BaseAdapter
{
	private List<D> mData;
	protected  Context mContext;
	protected  Resources mResources;
	protected  LayoutInflater mInflater;

	public WLBaseAdapter( Context context, List<D> data )
	{
		mContext = context;
		mResources = context.getResources();
		mData = data;
		mInflater = LayoutInflater.from(context);
	}

	public void swapData( List<D> newData ){
		if (newData == null){
			if (mData != null){
				mData.clear();
				notifyDataSetInvalidated();
			}
		}
		else{
			mData = newData;
			notifyDataSetChanged();
		}
	}

	public List<D> getData(){
		return mData;
	}

	public int getPosition( D d ){
		int index = -1;
		if (mData == null || isEmpty()) return index;

		index = mData.indexOf(d);
		return index;
	}

	@Override
	public int getCount(){
		if (mData == null || mData.isEmpty()){
			return 0;
		}
		else{
			return mData.size();
		}
	}

	@Override
	public D getItem( int position ){
		return mData.get(position);
	}

	@Override
	public long getItemId( int position ){
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ){
		View v;
		if (convertView == null){
			v = newView(mContext, mInflater, parent, position);
		}
		else{
			v = convertView;
		}
		bindView(mContext, v, position, getItem(position));
		return v;
	}

	protected View newView( Context context, LayoutInflater inflater, ViewGroup parent,int pos ){
		return null;
	}

	protected  void bindView( Context context, View view, int pos, D item ){
	}
}
