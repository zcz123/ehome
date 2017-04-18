package com.yuantuo.customview.action;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.yuantuo.customview.R;

public abstract class AbstractPopWindow<Type>
{
	public interface OnActionPopMenuItemSelectListener
	{
		public void onActionPopMenuItemSelect( int pos );
	}

	protected static final int INVALID_POSITION = -1;
	protected boolean mEnable = false;
	protected int mCurrentPos = INVALID_POSITION;
	protected boolean isCommited;
	protected List<Type> mDatas = new ArrayList<Type>();

	private final PopupWindow mPopWindow;
	protected Context mContext;
	protected View mRootView;
	protected Resources mResources;
	protected Drawable mBackground;
	protected LayoutInflater mLayoutInflater;
	protected ViewGroup mContainer;
	protected OnActionPopMenuItemSelectListener mPopMenuItemSelectListener;

	public AbstractPopWindow( Context context )
	{
		mContext = context;
		mPopWindow = new PopupWindow(context);
		mResources = context.getResources();
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	protected void setContentView( int layout ){
		setContentView(mLayoutInflater.inflate(layout, null));
	}

	protected void setContentView( View view ){
		mRootView = view;
		mContainer = (ViewGroup) mRootView.findViewById(R.id.action_pop_menu_content);
		mContainer.setFocusableInTouchMode(true);
		mRootView
				.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		ensurePopMenu();
	}

	private void ensurePopMenu(){
		if (mBackground == null){
			mPopWindow.setBackgroundDrawable(new BitmapDrawable(mResources));
		}
		else{
			mPopWindow.setBackgroundDrawable(mBackground);
		}
		setLayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		mPopWindow.setTouchable(true);
		mPopWindow.setFocusable(true);
		mPopWindow.setOutsideTouchable(true);
		mPopWindow.setContentView(mRootView);

		mContainer.setOnKeyListener(new View.OnKeyListener()
		{
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event ){
				if (KeyEvent.KEYCODE_MENU == keyCode && isShown()){
					dismiss();
					return true;
				}
				return false;
			}
		});
	}

	public void setLayoutParams( int width, int height ){
		mPopWindow.setWidth(width);
		mPopWindow.setHeight(height);
	}

	public void setBackgroundDrawable( Drawable background ){
		mBackground = background;
		if (mRootView != null) mRootView.setBackgroundDrawable(background);
	}

	public View findViewById( int id ){
		return mRootView.findViewById(id);
	}

	public void setOnActionPopMenuItemSelectListener( OnActionPopMenuItemSelectListener listener ){
		mPopMenuItemSelectListener = listener;
	}

	protected void invokeActionPopMenuListener( int pos ){
		if (mPopMenuItemSelectListener != null)
			mPopMenuItemSelectListener.onActionPopMenuItemSelect(pos);
	}

	public void setOnDismissListener( PopupWindow.OnDismissListener listener ){
		mPopWindow.setOnDismissListener(listener);
	}

	public void dismiss(){
		mPopWindow.dismiss();
	}

	public boolean isShown(){
		return mPopWindow.isShowing();
	}

	public void setAnimationStyle( int style ){
		mPopWindow.setAnimationStyle(style);
	}

	public void showAtLocation( View parent, int gravity, int xOffset, int yOffset ){
		onMenuPrepareShow();
		mPopWindow.showAtLocation(parent, gravity, xOffset, yOffset);
	}

	public void showAtLocation( View parent, int gravity ){
		showAtLocation(parent, gravity, 0, 0);
	}

	public void showAtCenter( View parent ){
		showAtLocation(parent, Gravity.CENTER);
	}

	public void showAtBottom( View parent ){
		showAtLocation(parent, Gravity.BOTTOM);
	}

	public void showAsDropDown( View archor ){
		showAsDropDown(archor, archor.getMeasuredWidth(), -2);
	}

	public void showAsDropDown( View archor, int width, int height ){
		setLayoutParams(width, height);
		onMenuPrepareShow();
		mPopWindow.showAsDropDown(archor);
	}

	public void addItem( Type item ){
		mDatas.add(item);
	}

	public void addItem( Type[] items ){
		for (Type type : items){
			addItem(type);
		}
	}

	public Type removeItem( int pos ){
		Type type = mDatas.remove(pos);
		return type;
	}

	public boolean removeItem( Type type ){
		int pos = mDatas.indexOf(type);
		return removeItem(pos) != null;
	}

	public boolean removeAll(){
		mDatas.clear();
		return false;
	}

	public int getItemCount(){
		return mDatas.size();
	}

	public abstract boolean commit();

	protected void onMenuPrepareShow(){
		if (!isCommited) isCommited = commit();
	}

	public void setPopMenuEnable( boolean enable ){
		mEnable = enable;
	}

	public boolean isPopMenuEnable(){
		return mEnable;
	}

	public void setPopMenuSelection( int pos ){
		mCurrentPos = pos;
	}

	public int getPopMenuSelection(){
		return mCurrentPos;
	}
}