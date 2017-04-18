package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.device.AreaGroupMenuPopupWindow;

public class DownUpMenuList {

	private final Context mContext;
	private LinearLayout contentView;
	private LayoutInflater inflater;
	private final AreaGroupMenuPopupWindow mPopMenu;
	private List<DownUpMenuItem> list = new ArrayList<DownUpMenuList.DownUpMenuItem>();

	public DownUpMenuList(Context context) {
		this.mContext = context;
		mPopMenu = new AreaGroupMenuPopupWindow(mContext);
		inflater = LayoutInflater.from(mContext);
		contentView = (LinearLayout) inflater.inflate(
				R.layout.downup_menu_list, null);
		mPopMenu.setContentView(contentView);
	}

	public void dismiss() {
		mPopMenu.dismiss();
	}

	public void showBottom(View view) {
		contentView.removeAllViews();
		for (final DownUpMenuItem item : list) {
			View contentLineLayout = item.getView();
			contentView.addView(item.getView());
			contentLineLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					item.doSomething();
				}
			});
			// OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
			contentView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {

					int height = item.getView().getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							dismiss();
						}
					}
					return true;
				}
			});
		}
		mPopMenu.showAtLocation(view, Gravity.BOTTOM, 0, 0);
	}

	public void setMenu(List<DownUpMenuItem> items) {
		list.addAll(items);
	}

	public static abstract class DownUpMenuItem {
		protected int icon;
		protected String name;
		protected Context context;
		protected LayoutInflater inflater;
		protected LinearLayout linearLayout;
		protected TextView mTitleTextView;
		protected ImageView iconImageView;
		protected View downup_menu_view;

		public DownUpMenuItem(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(this.context);
			linearLayout = (LinearLayout) inflater.inflate(
					R.layout.downup_menu_item, null);
			mTitleTextView = (TextView) linearLayout
					.findViewById(R.id.downup_menu_item_title_btn);
			downup_menu_view = (View) linearLayout.findViewById(R.id.downup_menu_item_view);
			iconImageView = (ImageView) linearLayout
					.findViewById(R.id.downup_menu_item_icon);
			initSystemState();
		}

		public int getIcon() {
			return icon;
		}

		public void setIcon(int icon) {
			this.icon = icon;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public abstract void initSystemState();

		public abstract void doSomething();

		public View getView() {
			return linearLayout;
		}
	}

}
