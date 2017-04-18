package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public abstract class WLBaseExpandableAdapter<G, C> extends BaseExpandableListAdapter
{
	protected List<G> groupListData;
	protected List<List<C>> childListData;
	protected final Context mContext;
	protected final Resources mResources;
	protected final LayoutInflater mInflater;

	public WLBaseExpandableAdapter( Context context, List<G> groupData, List<List<C>> childData )
	{
		mContext = context;
		mResources = context.getResources();
		mInflater = LayoutInflater.from(context);
		groupListData = groupData;
		childListData = childData;
	}

	public void swapGroupData( List<G> newGroupData ) {

		if (newGroupData == null) {
			if (groupListData != null) {
				groupListData.clear();
				notifyDataSetInvalidated();
			}
		}
		else {
			groupListData = newGroupData;
			notifyDataSetChanged();
		}

	}
	public void swapData( List<G> newGroupData ,List<List<C>> newChildData) {
		if (newGroupData == null) {
			if (groupListData != null) {
				groupListData.clear();
			}
		}
		else {
			groupListData = newGroupData;
		}
		
		if (newChildData == null) {
			if (childListData != null) {
				childListData.clear();
			}
		}
		else {
			childListData = newChildData;
		}
		notifyDataSetChanged();
	}
	public void swapChildData( List<List<C>> newChildData ) {

		if (newChildData == null) {
			if (childListData != null) {
				childListData.clear();
				notifyDataSetInvalidated();
			}
		}
		else {
			childListData = newChildData;
			notifyDataSetChanged();
		}

	}

	public List<G> getGroupData() {
		return groupListData;
	}

	public List<List<C>> getChildData() {
		return childListData;
	}

	@Override
	public int getGroupCount() {
		if (groupListData == null || groupListData.isEmpty()) {
			return 0;
		}
		else {
			return groupListData.size();
		}
	}

	@Override
	public int getChildrenCount( int groupPosition ) {
		if (childListData == null || childListData.isEmpty()) return 0;
		List<C> tempList = childListData.get(groupPosition);

		if (tempList == null || tempList.isEmpty()) {
			return 0;
		}
		else {
			return tempList.size();
		}

	}

	public List<C> getChildrenList( int groupPosition ) {
		return childListData == null || childListData.isEmpty() ? null : childListData.get(groupPosition);
	}

	@Override
	public G getGroup( int groupPosition ) {
		return groupListData.get(groupPosition);
	}

	@Override
	public C getChild( int groupPosition, int childPosition ) {
		return childListData.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId( int groupPosition ) {
		return groupPosition;
	}

	@Override
	public long getChildId( int groupPosition, int childPosition ) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView( int groupPosition, boolean isExpanded, View convertView, ViewGroup parent ) {
		View groupView;
		if (convertView == null) {
			groupView = newGroupView(mContext, mInflater, parent, groupPosition, isExpanded);
		}
		else {
			groupView = convertView;
		}
		bindGroupView(mContext, groupView, groupPosition, isExpanded, getGroup(groupPosition));
		return groupView;
	}

	@Override
	public View getChildView( int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent ) {
		View childView;
		if (convertView == null) {
			childView = newChildView(mContext, mInflater, parent, groupPosition, childPosition, isLastChild);
		}
		else {
			childView = convertView;
		}
		bindChildView(mContext, childView, groupPosition, childPosition, isLastChild, getChild(groupPosition, childPosition));
		return childView;
	}

	@Override
	public boolean isChildSelectable( int groupPosition, int childPosition ) {
		return true;
	}

	protected  View newChildView( Context context, LayoutInflater inflater, ViewGroup parent, int groupPosition, int childPosition, boolean isLastChild ){
		return null;
	}

	protected  View newGroupView( Context context, LayoutInflater inflater, ViewGroup parent, int groupPosition, boolean isExpanded ){
		return null;
	}

	protected void bindChildView( Context context, View view, int groupPosition, int childPosition, boolean isLastChild, C childItem ){
		
	}

	protected void bindGroupView( Context context, View view, int groupPosition, boolean isExpanded, G groupItem ){
		
	}

}
