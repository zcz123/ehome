package cc.wulian.smarthomev5.fragment.device;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import cc.wulian.smarthomev5.R;

public class AreaGroupMenuPopupWindow extends PopupWindow {

	public AreaGroupMenuPopupWindow(Context context) {
		super(context);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
	}

}
