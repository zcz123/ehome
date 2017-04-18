package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class SettingManagerAdapter extends WLBaseAdapter<AbstractSettingItem> {

	public SettingManagerAdapter(Context context) {
		super(context, new ArrayList<AbstractSettingItem>());
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rootView = getItem(position).getShowView();
		rootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getItem(position).doSomethingAboutSystem();
			}
		});
		return rootView;
	}


	public void addSettingItem(AbstractSettingItem item){
		this.getData().add(item);
	}
}
