package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class FloorWarmSettingManagerAdapter extends WLBaseAdapter<AbstractSettingItem> {

	public FloorWarmSettingManagerAdapter(Context context) {
		super(context, new ArrayList<AbstractSettingItem>());
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View rootView = getItem(position).getShowView();
		rootView.setOnTouchListener(new OnTouchListener() {
		LinearLayout settingManagerItem = (LinearLayout) rootView.findViewById(R.id.setting_manager_item);
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
					case MotionEvent.ACTION_DOWN:

						return true;
					case MotionEvent.ACTION_MOVE:
						return false;
					case MotionEvent.ACTION_UP:

						getItem(position).doSomethingAboutSystem();
						return true;
				}
				return false;
			}
		});
		return rootView;
	}


	public void addSettingItem(AbstractSettingItem item){
		this.getData().add(item);
	}
}
