package cc.wulian.smarthomev5.fragment.setting;

import android.content.Context;
import android.widget.AbsListView.LayoutParams;
import cc.wulian.app.model.device.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class EmptyItem extends AbstractSettingItem{
	public EmptyItem(Context context) {
		super(context);
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,DisplayUtil.dip2Pix(mContext, 20)));
		view.setBackgroundColor(mContext.getResources().getColor(R.color.trant));
	}
	@Override
	public void doSomethingAboutSystem() {
		
	}
}
