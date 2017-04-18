package cc.wulian.smarthomev5.fragment.scene;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.device.AreaGroupMenuPopupWindow;

public class SceneRemindPopuwindow {

	private Context context;
	private LinearLayout contentView;
	private LayoutInflater inflater;
	private final AreaGroupMenuPopupWindow mPopMenu;
	
	public SceneRemindPopuwindow(Context context) {
		this.context = context;
		mPopMenu = new AreaGroupMenuPopupWindow(context);
		inflater = LayoutInflater.from(context);
		contentView = (LinearLayout) inflater.inflate(
				R.layout.scene_default_remind_popuwindow, null);
		mPopMenu.setContentView(contentView);
		
		contentView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public void dismiss() {
		mPopMenu.dismiss();
	}
	
	public void showBottom() {
		mPopMenu.showAtLocation(contentView, Gravity.TOP, 0, 0);
	}
}
