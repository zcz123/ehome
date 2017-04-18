package cc.wulian.smarthomev5.view.swipemenu;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuView.OnItemClickListener;


public class SwipeMenuAdapter<D> extends BaseAdapter implements OnItemClickListener{

	protected Context mContext;
	private OnMenuItemClickListener onMenuItemClickListener;
	private SwipeMenuCreator mMenuCreator;
	public List<D> mData;
	public SwipeMenuAdapter(Context context, List<D> mData) {
		this.mData = mData;
		mContext = context;
	}
	//add syf
	public void addData(List<D>newData){
		if(mData!=null){
			if(newData!=null&&newData.size()>0){
				mData.clear();
				mData.addAll(newData);
				this.notifyDataSetChanged();
			}
		}
	}
	public synchronized void swapData( List<D> newData ){
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
	
	public void removeItem(int position) {
		mData.remove(position);
		notifyDataSetChanged();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		SwipeMenuLayout layout = null;
		View rootView;
		if (convertView != null) {
			layout = (SwipeMenuLayout) convertView;
			layout.closeMenu();
			layout.setPosition(position);
			rootView = (View)layout.getTag();
//			View view = mAdapter.getView(position, layout.getContentView(),
//					parent);
		}else{
			rootView = newView(position, convertView, parent);
			layout = createMenuView(position, parent, rootView);
			layout.setTag(rootView);
		}
		bindView(mContext, rootView, position, getItem(position));
		return layout;
	}

	protected void bindView(Context mContext2, View convertView, int position,
			D item) {
		
	}

	protected SwipeMenuLayout createMenuView(int position, ViewGroup parent,
			View contentView) {
		SwipeMenuLayout layout;
		SwipeMenu menu = new SwipeMenu(mContext);
		createMenu(menu,position);
		SwipeMenuView menuView = new SwipeMenuView(menu,
				(SwipeMenuListView) parent);
		menuView.setOnItemClickListener(this);
		SwipeMenuListView listView = (SwipeMenuListView) parent;
		layout = new SwipeMenuLayout(mContext,contentView, menuView,
				listView.getCloseInterpolator(),
				listView.getOpenInterpolator());
		layout.setPosition(position);
		return layout;
	}
	protected View newView(int position, View convertView, ViewGroup parent){
		return null;
	}
	public void createMenu(SwipeMenu menu,int position) {
		if (mMenuCreator != null) {
			mMenuCreator.create(menu,position);
		}
	}

	@Override
	public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
		if (onMenuItemClickListener != null) {
			onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu,
					index);
		}
	}

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener) {
		this.onMenuItemClickListener = onMenuItemClickListener;
	}
	public void setMenuCreator(SwipeMenuCreator menuCreator) {
		this.mMenuCreator = menuCreator;
	}
	public static interface OnMenuItemClickListener {
		void onMenuItemClick(int position, SwipeMenu menu, int index);
	}
}
