package cc.wulian.smarthomev5.tools;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;

import com.yuantuo.customview.action.AbstractPopWindow;
import com.yuantuo.customview.action.menu.ActionPopMenu;
import com.yuantuo.customview.ui.ScreenSize;

public class SensorMenuList {
	private final Context mContext;
	private final ActionPopMenu mPopMenu;
	private final MenuAdapter mAdapter;
	private Resources resources;
	@SuppressLint("ResourceAsColor")
	public SensorMenuList( Context context,int titleRes)
	{
		this.mContext = context;
		resources = this.mContext.getResources();
		mAdapter = new MenuAdapter(context, null);
		mPopMenu = new ActionPopMenu(context);
		mPopMenu.setTitle(titleRes);		
		mPopMenu.setTitleTextColor(R.color.select_scene_title);
		mPopMenu.setTitleBackground(R.color.holo_gray_light);
		mPopMenu.setAdapter(mAdapter);
		mPopMenu.setOnActionPopMenuItemSelectListener(mActionPopMenuItemSelectListener);
		mPopMenu.getmBottomView().setVisibility(View.VISIBLE);
		mPopMenu.setBottom(mContext.getResources().getString(
				R.string.cancel));
		mPopMenu.getmBottomView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopMenu.isShown()) {
					mPopMenu.dismiss();
				}
			}
		});
	}
	public SensorMenuList(Context context){
		this.mContext = context;
		resources = this.mContext.getResources();
		mAdapter = new MenuAdapter(context, null);
		mPopMenu = new ActionPopMenu(context);
		mPopMenu.setAdapter(mAdapter);
		mPopMenu.setOnActionPopMenuItemSelectListener(mActionPopMenuItemSelectListener);
	}
	public boolean isShowing(){
		return mPopMenu.isShown();
	}
	public void dismiss(){
		mPopMenu.dismiss();
	}
	public void show( View view ){
		show(view,Gravity.CENTER,0,0);
	}
	public void show(View view,int gravity, int x, int y){
		mPopMenu.setBackgroundDrawable(new ColorDrawable(resources.getColor(R.color.holo_gray_light)));
		mPopMenu.setLayoutParams((int) (ScreenSize.screenWidth / 1.7), LayoutParams.WRAP_CONTENT);
		mPopMenu.showAtLocation(view, gravity, x, y);
	}
	private final AbstractPopWindow.OnActionPopMenuItemSelectListener mActionPopMenuItemSelectListener = new AbstractPopWindow.OnActionPopMenuItemSelectListener()
	{
		@Override
		public void onActionPopMenuItemSelect( int pos ){
			mAdapter.getItem(pos).doSomething();
		}
	};
	public void addMenu(List<SensorMenuItem> items){
		mAdapter.swapData(items);
	}
	public static abstract class SensorMenuItem{
		protected int icon;
		protected String name;
		protected Context context;
		protected LayoutInflater inflater;
		protected LinearLayout lineLayout ;
		protected TextView titleTextView;
		protected ImageView iconImageView;
		public SensorMenuItem(Context context){
			this.context = context;
			inflater = LayoutInflater.from(this.context);
			lineLayout = (LinearLayout)inflater.inflate(R.layout.menu_list_item, null);
			titleTextView = (TextView)lineLayout.findViewById(R.id.menu_item_title);
			iconImageView = (ImageView)lineLayout.findViewById(R.id.menu_item_icon);
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
		public abstract  void initSystemState();
		public abstract void doSomething();
		public View getView(){
			return lineLayout;
		}
	}
	public static class MenuAdapter extends WLBaseAdapter<SensorMenuItem>{

		public MenuAdapter(Context context, List<SensorMenuItem> data) {
			super(context, data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getItem(position).getView();
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return null;
		}

		@Override
		protected void bindView(Context context, View view, int pos,
				SensorMenuItem item) {
			
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			CheckedTextView view = (CheckedTextView)mInflater.inflate(android.R.layout.simple_spinner_dropdown_item,null);
			view.setText(getItem(position).getName());
			return view;
		}
		
	}
}
