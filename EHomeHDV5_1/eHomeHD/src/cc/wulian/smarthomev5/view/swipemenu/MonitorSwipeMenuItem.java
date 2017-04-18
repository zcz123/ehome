package cc.wulian.smarthomev5.view.swipemenu;

import android.content.Context;
import cc.wulian.smarthomev5.entity.BaseCameraEntity;
import cc.wulian.smarthomev5.utils.DisplayUtil;

/**
 * Created by yanzy on 2016-6-17
 * Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
 **/
public abstract class MonitorSwipeMenuItem extends SwipeMenuItem {
	public MonitorSwipeMenuItem(Context context) {
		super(context);
		setWidth(DisplayUtil.dip2Pix(context,100));
	}
	
	public abstract void onClick(int columnPosition);
	

}
