package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.smarthomev5.fragment.more.nfc.NFCControlItem;

public class NFCConfigureAdapter extends WLBaseAdapter<NFCControlItem>
{
	private boolean isEditMode = false;

	public NFCConfigureAdapter( Context context,List<NFCControlItem> infos)
	{
		super(context,infos);
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
