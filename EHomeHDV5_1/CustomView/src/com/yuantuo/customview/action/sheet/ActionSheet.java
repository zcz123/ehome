package com.yuantuo.customview.action.sheet;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yuantuo.customview.R;
import com.yuantuo.customview.action.AbstractPopWindow;
import com.yuantuo.customview.action.sheet.ActionButton.ActionStyle;
import com.yuantuo.customview.action.sheet.ActionButton.OnClickActionButton;

public class ActionSheet extends AbstractPopWindow<ActionButton>
{
	private static final int FLAG_NO_OPERATION = 0X00000000;
	private static final int FLAG_ADD_ITEM = 0X00000001;
	private static final int FLAG_REMOVE_ITEM = 0X00000002;
	private int mFlag;
	
	private Runnable mDismissRunnable = new Runnable()
	{
		@Override
		public void run(){
			dismiss();
		}
	};

	public ActionSheet( Context context )
	{
		super(context);
		setContentView(R.layout.actionsheet);
	}

	@Override
	public void addItem( ActionButton[] items ){
		super.addItem(items);
		setFlag(FLAG_ADD_ITEM);
		commit();
	}
	
	@Override
	public ActionButton removeItem( int pos ){
		ActionButton actionButton = super.removeItem(pos);
		setFlag(FLAG_REMOVE_ITEM);
		return actionButton;
	}

	@Override
	public boolean removeAll(){
		super.removeAll();
		setFlag(FLAG_REMOVE_ITEM);
		return true;
	}

	public void setFlag( int flag ){
		mFlag = flag;
	}

	@Override
	public boolean commit(){
		if ((mFlag & FLAG_ADD_ITEM) == FLAG_ADD_ITEM){
			addActionButton();
			setFlag(FLAG_NO_OPERATION);
			return true;
		}
		else if ((mFlag & FLAG_REMOVE_ITEM) == FLAG_REMOVE_ITEM){
			reCreateActionButton();
			setFlag(FLAG_NO_OPERATION);
			return true;
		}
		else{
			return false;
		}
	}

	private void addActionButton(){
		final List<ActionButton> buttons = mDatas;
		final ViewGroup container = mContainer;
		int size = buttons.size();
		for (int i = 0; i < size; i++){
			final ActionButton actionButton = buttons.get(i);

			String btnName = actionButton.getName();
			ActionStyle style = actionButton.getStyle();
			Drawable background = style.getActionButtonBackground(mResources);

			Button mButton = new Button(mContext);
			mButton.setTextColor(style == ActionStyle.Normal ? Color.BLACK : Color.WHITE);
			mButton.setTextSize(18F);
			mButton.setText(btnName);
			if (background != null){
				mButton.setBackgroundDrawable(background);
			}
			else{
				mButton.setVisibility(View.GONE);
			}
			mButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick( View v ){
					OnClickActionButton onClickActionButton = actionButton.getOnClickActionButton();
					if (onClickActionButton != null)
						onClickActionButton.onClickActionButton(mContext, actionButton.getId());
					v.post(mDismissRunnable);
				}
			});

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			if (style != ActionStyle.Cancel){
				params.bottomMargin = 10;
			}
			else{
				params.bottomMargin = 5;
			}
			if (style == ActionStyle.Warn){
				params.topMargin = 15;
			}
			else if (style == ActionStyle.Cancel){
				params.topMargin = 10;
			}
			params.leftMargin = 10;
			params.rightMargin = 10;
			container.addView(mButton, i, params);
		}
	}

	private void reCreateActionButton(){
		mContainer.removeAllViews();
		addActionButton();
	}
}
