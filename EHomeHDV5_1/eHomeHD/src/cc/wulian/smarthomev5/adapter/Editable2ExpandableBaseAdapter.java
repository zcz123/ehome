package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;

public abstract class Editable2ExpandableBaseAdapter<G, C> extends WLBaseExpandableAdapter<G, C>
{
	protected boolean mIsEditingMode = false;
	protected boolean mUseEditingMode = true;

	public Editable2ExpandableBaseAdapter( Context context, List<G> groupData, List<List<C>> childData )
	{
		super(context, groupData, childData);
	}

	public boolean isInEditMode() {
		return mIsEditingMode;
	}

	public void setEditMode( boolean mode ) {
		if (mIsEditingMode == mode) return;
		mIsEditingMode = mode;
		notifyDataSetChanged();
	}

	public void toggleEditMode() {
		setEditMode(!mIsEditingMode);
	}

	public void setUseEditMode( boolean mode ) {
		if (mUseEditingMode == mode) return;

		mUseEditingMode = mode;
		notifyDataSetChanged();
	}

	public abstract View.OnClickListener setEditableGroupClickListener( int pos, G item );

	public abstract View.OnClickListener setEditableChildClickListener(int gpos, int cpos, C item );

	public void onDeleteGroupClick( G item ) {

	}

	public void onDeleteChildClick( int gpos ,C item ) {

	}

	public class DeleteGroupListener implements View.OnClickListener
	{
		final G item;

		public DeleteGroupListener( G item )
		{
			this.item = item;
		}

		@Override
		public void onClick( View v ) {
			onDeleteGroupClick(item);
		}
	}

	public class DeleteChildListener implements View.OnClickListener
	{
    final int gpos;
		final C item;

		public DeleteChildListener(int gpos, C item )
		{
			this.gpos = gpos;
			this.item = item;
		}

		@Override
		public void onClick( View v ) {
			onDeleteChildClick(gpos, item);
		}

	}

}
