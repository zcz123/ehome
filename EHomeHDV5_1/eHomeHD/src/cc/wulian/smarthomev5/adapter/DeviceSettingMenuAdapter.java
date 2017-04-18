package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;
import cc.wulian.smarthomev5.tools.MenuList.MenuItem;
import cc.wulian.smarthomev5.utils.DisplayUtil;


public class DeviceSettingMenuAdapter extends WLBaseAdapter<MenuItem>{

	public DeviceSettingMenuAdapter(Context context, List<MenuItem> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		TextView textViw = new TextView(mContext);
		textViw.setText("Setting");
		return textViw ;
	}

	@Override
	protected void bindView(Context context, View view, final int pos, MenuItem item) {
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = getItem(position).getView();
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT,DisplayUtil.dip2Pix(mContext, 50));
		view.setLayoutParams(param);
		view.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		return view;
	}
	

}
