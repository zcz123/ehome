package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.smarthomev5.view.CheckableFrame;

public abstract class EditableBaseAdapter<D> extends WLBaseAdapter<D>
{
	protected boolean mIsEditingMode = false;
	protected boolean mUseEditingMode = true;

	public EditableBaseAdapter( Context context, List<D> data )
	{
		super(context, data);
	}

	public boolean isInEditMode(){
		return mIsEditingMode;
	}

	public void setEditMode( boolean mode ){
		// current mode is same, do nothing
		if (mIsEditingMode == mode) return;

		mIsEditingMode = mode;
		notifyDataSetChanged();
	}

	public void toggleEditMode(){
		setEditMode(!mIsEditingMode);
	}

	public void setUseEditMode( boolean mode ){
		if (mUseEditingMode == mode) return;

		mUseEditingMode = mode;
		notifyDataSetChanged();
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ){
		CheckableFrame frame = null;
		View v;
		if (convertView == null){
			frame = new CheckableFrame(mContext);
			v = newView(mContext, mInflater, parent, position);
			frame.addView(v);
		}
		else{
			frame = (CheckableFrame) convertView;
			v = frame.getChildAt(0);
		}

		bindView(mContext, v, position, getItem(position));

		// frame.setOnClickListener(setEditableClickListener(position, getItem(position)));
		return frame;
	}

	public abstract View.OnClickListener setEditableClickListener(int pos, D item);

	public void onDeleteClick( D item ){
	}

	/**
	 * simple delete listener
	 */
	public class DeleteListener implements View.OnClickListener
	{
		final D item;

		public DeleteListener( D item )
		{
			this.item = item;
		}

		@Override
		public void onClick( View v ){
			onDeleteClick(item);
		}
	}
}
