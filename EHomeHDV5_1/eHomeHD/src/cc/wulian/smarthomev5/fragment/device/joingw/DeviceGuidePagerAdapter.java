package cc.wulian.smarthomev5.fragment.device.joingw;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class DeviceGuidePagerAdapter extends PagerAdapter {

	private final List<View> viewList;

	public DeviceGuidePagerAdapter(List<View> list) {
		this.viewList = list;
	}

	@Override
	public int getCount() {

		return viewList != null ? viewList.size() : 0;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position), 0);
		return viewList.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position));
	}

}
