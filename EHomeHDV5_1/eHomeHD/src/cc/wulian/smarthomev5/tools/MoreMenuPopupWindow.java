package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ScrollView;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class MoreMenuPopupWindow {

	private LinearLayout rootView;
	private LayoutInflater inflater;
	private Context context;
	private PopupWindow popupWindow;
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();

	public MoreMenuPopupWindow(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		rootView = (LinearLayout) inflater.inflate(
				R.layout.device_setting_more_content, null);
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public boolean isShown() {
		if (popupWindow == null)
			return false;
		return popupWindow.isShowing();
	}

	public void show(View view, int width) {
		rootView.removeAllViews();
		for (MenuItem item : this.menuItems) {
			rootView.addView(item.getView());
		}
		if (popupWindow == null) {
			popupWindow = new PopupWindow(this.context);
			popupWindow.setBackgroundDrawable(this.context.getResources()
					.getDrawable(R.drawable.popwindow_bg));
			/**
			 * 指定popupwindow的宽和高
			 */
			popupWindow.setWidth(width);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setContentView(rootView);
		}
		popupWindow.showAsDropDown(view, -10, 2);
		popupWindow.setFocusable(true);
		popupWindow.update();
	}

	public void show(View view) {
		rootView.removeAllViews();
		for (MenuItem item : this.menuItems) {
			rootView.addView(item.getView());
		}
		if (popupWindow == null) {
			popupWindow = new PopupWindow(this.context);
			popupWindow.setBackgroundDrawable(this.context.getResources()
					.getDrawable(R.drawable.popwindow_bg));
			/**
			 * 指定popupwindow的宽和高
			 */
			popupWindow.setWidth(DisplayUtil.dip2Pix(context, 150));
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setContentView(rootView);
		}
		popupWindow.showAsDropDown(view, -10, 2);
		popupWindow.setFocusable(true);
		popupWindow.update();
	}

	public void show(View view, int x, int y, int width) {
		rootView.removeAllViews();

        int itemNum = 0;

		LinearLayout linearLayout = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(params);

		for (MenuItem item : this.menuItems) {
			linearLayout.addView(item.getView());
            itemNum++;
		}

		ScrollView scrollView = new ScrollView(context);
		scrollView.setBackgroundColor(Color.TRANSPARENT);
		scrollView.setVerticalScrollBarEnabled(false);
		scrollView.addView(linearLayout);

		rootView.addView(scrollView);
		if (popupWindow == null) {
			popupWindow = new PopupWindow(this.context);
			popupWindow.setBackgroundDrawable(this.context.getResources()
					.getDrawable(R.drawable.popwindow_bg));
			/**
			 * 指定popupwindow的宽和高
			 */
			popupWindow.setWidth(DisplayUtil.dip2Pix(context, width));
            if (itemNum <= 7){
                popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
            }else {
                popupWindow.setHeight(DisplayUtil.dip2Pix(context, 380));
            }
            popupWindow.setContentView(rootView);
		}
		popupWindow.showAsDropDown(view, x, y);
		popupWindow.setFocusable(true);
		popupWindow.update();
	}

	/**
	 * <h1>隐藏Pop</h1>
	 */
	public void dismiss() {
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
	}

	public void setOnDismissListener(OnDismissListener listener) {
		if (popupWindow != null)
			popupWindow.setOnDismissListener(listener);
	}

	public static abstract class MenuItem {
		protected int icon;
		protected String name;
		protected Context context;
		protected LayoutInflater inflater;
		protected LinearLayout lineLayout;
		protected TextView titleTextView;
		protected ImageView iconImageView;
		protected ImageView iconImageViewRight;

		public MenuItem(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(this.context);
			lineLayout = (LinearLayout) inflater.inflate(
					R.layout.device_detail_setting_more_item, null);
			titleTextView = (TextView) lineLayout
					.findViewById(R.id.device_setting_more_title_text);
			iconImageView = (ImageView) lineLayout
					.findViewById(R.id.device_setting_more_icon);
			iconImageViewRight = (ImageView) lineLayout
					.findViewById(R.id.device_setting_more_icon_right);
			initSystemState();
			lineLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					doSomething();
				}
			});
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
			return lineLayout;
		}
	}
}
