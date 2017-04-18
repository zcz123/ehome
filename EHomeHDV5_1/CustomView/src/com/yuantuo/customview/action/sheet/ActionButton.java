package com.yuantuo.customview.action.sheet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.yuantuo.customview.R;

public class ActionButton
{
	public interface OnClickActionButton
	{
		public void onClickActionButton( Context context, int actionId );
	}

	public enum ActionStyle
	{
		Warn( 0 ), Normal( 1 ), Cancel( 2 );

		int mName;

		private ActionStyle( int name )
		{
			mName = name;
		}

		public int getVaule(){
			return mName;
		}

		public Drawable getActionButtonBackground( Resources resources ){
			Drawable drawable = null;
			if (Warn.getVaule() == mName){
				drawable = resources.getDrawable(R.drawable.action_warn);
			}
			else if (Normal.getVaule() == mName){
				drawable = resources.getDrawable(R.drawable.action_normal);
			}
			else if (Cancel.getVaule() == mName){
				drawable = resources.getDrawable(R.drawable.action_cancel);
			}
			return drawable;
		}
	}

	private int id;
	private String name;
	private ActionStyle style;
	private OnClickActionButton onClickActionButton;

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public ActionStyle getStyle(){
		return style;
	}

	public OnClickActionButton getOnClickActionButton(){
		return onClickActionButton;
	}
	
	public static ActionButton[] addActionButtons( Context context, int[] ids, String[] names,
			ActionStyle[] styles, OnClickActionButton listener ){
		int num = ids.length;
		ActionButton[] actionButtons = new ActionButton[num];
		for (int i = 0; i < num; i++){
			actionButtons[i] = 
					new ActionButton.Builder(context)
			.setID(ids[i])
			.setName(names[i])
			.setStyle(styles[i])
			.setListener(listener)
			.create();
		}
		return actionButtons;
	}

	public static ActionButton[] addActionButtons( Context context, int[] ids, int[] names,
			ActionStyle[] styles, OnClickActionButton listener ){
		int num = ids.length;
		String[] namesStr = new String[num];
		for (int i = 0; i < num; i++){
			namesStr[i] = context.getString(names[i]);
		}
		return addActionButtons(context, ids, namesStr, styles, listener);
	}

	public static class Builder
	{
		private int id;
		private String name;
		private ActionStyle style;
		private OnClickActionButton onClickActionButton;
		private Resources mResources;

		public Builder( Context context )
		{
			mResources = context.getResources();
		}

		public Builder setID( int id ){
			this.id = id;
			return this;
		}

		public Builder setName( String name ){
			this.name = name;
			return this;
		}

		public Builder setName( int nameRes ){
			return setName(mResources.getString(nameRes));
		}

		public Builder setStyle( ActionStyle style ){
			this.style = style;
			return this;
		}

		public Builder setListener( OnClickActionButton listener ){
			onClickActionButton = listener;
			return this;
		}

		public ActionButton create(){
			ActionButton actionButton = new ActionButton();
			actionButton.id = id;
			actionButton.name = name;
			actionButton.style = style;
			actionButton.onClickActionButton = onClickActionButton;
			return actionButton;
		}
	}
}
