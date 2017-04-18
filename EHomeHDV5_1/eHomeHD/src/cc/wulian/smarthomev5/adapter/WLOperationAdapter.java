package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cc.wulian.ihome.wan.util.StringUtil;

public abstract class WLOperationAdapter<D> extends BaseAdapter {

	private static final int TYPE_DATA = 0;
	private static final int TYPE_MENU = 1;

	protected Context mContext;
	protected List<D> mData;
	protected List<MenuItem> items;
	protected Resources mResources;
	protected LayoutInflater mInflater;

	public WLOperationAdapter(Context context, List<D> data,
			List<MenuItem> items) {
		this.mData = data;
		this.items = items;
		this.mContext = context;
		mResources = mContext.getResources();
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getItemViewType(int position) {
		if (position < mData.size())
			return TYPE_DATA;
		else
			return TYPE_MENU;
	}

	@Override
	public int getViewTypeCount() {
//		int count = 2;
//		if ((mData != null && mData.size() > 0)
//				|| (items != null && items.size() > 0))
//			count++;
		return 2;
	}

	public void swapData(List<D> newData) {
		if (newData == null) {
			if (mData != null) {
				mData.clear();
				notifyDataSetInvalidated();
			}
		} else {
			mData = newData;
			notifyDataSetChanged();
		}
	}

	public List<D> getData() {
		return mData;
	}

	public int getPosition(D d) {
		int index = -1;
		if (mData == null || isEmpty())
			return index;

		index = mData.indexOf(d);
		return index;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mData != null)
			count += mData.size();
		if (items != null) {
			count += items.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		if (position < mData.size())
			return mData.get(position);
		else
			return items.get(position - mData.size());
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		if (TYPE_DATA == type) {
			return getItemView(position, convertView, parent);
		} else {
			return getMenuItemView(position, convertView, parent);
		}
	}

	private View getMenuItemView(int position, View convertView,
			ViewGroup parent) {
		final MenuItem item = (MenuItem) getItem(position);
		View view = item.getView();
		view.setTag(TYPE_MENU);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				item.doSomething();
			}
		});
		return view;
	}

	private View getItemView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView != null
				&& StringUtil.toInteger(convertView.getTag()) == TYPE_DATA) {
			v = convertView;
		} else {
			v = newView(mContext, mInflater, parent, position);
		}
		v.setTag(TYPE_DATA);
		bindView(mContext, v, position, (D) getItem(position));
		return v;
	}

	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return null;
	}

	protected void bindView(Context context, View view, int pos, D item) {
	}

	public void swapMenuItem(List<MenuItem> items) {
		if (items == null) {
			if (this.items != null) {
				this.items.clear();
				notifyDataSetInvalidated();
			}
		} else {
			this.items = items;
			notifyDataSetChanged();
		}
	}

	public static abstract class MenuItem {
		public abstract void doSomething();

		public abstract View getView();
	}
}
