package cc.wulian.smarthomev5.fragment.setting;

import android.content.Context;
import android.widget.AbsListView.LayoutParams;
import cc.wulian.app.model.device.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class SpaceItem extends AbstractSettingItem{
	private int value=3;
	public SpaceItem(Context context) {
		super(context);
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,DisplayUtil.dip2Pix(mContext, value)));
		view.setBackgroundColor(mContext.getResources().getColor(R.color.trant));
	}
	@Override
	public void doSomethingAboutSystem() {
		
	}
	
	public void setSpaceValue(int value){
		this.value=value;
	}
}
