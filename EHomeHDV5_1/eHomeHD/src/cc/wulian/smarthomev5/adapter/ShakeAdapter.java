package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.smarthomev5.fragment.more.shake.ShakeControlItem;

public class ShakeAdapter extends WLBaseAdapter<ShakeControlItem>
{
	private boolean isEditMode = false;

	public ShakeAdapter( Context context, List<ShakeControlItem> data )
	{
		super(context, data);
	}

	public void toggleEditMode() {
		isEditMode = !isEditMode;
	}

	public boolean getEditMode() {
		return isEditMode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItem(position).getView();
	}
}
