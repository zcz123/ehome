package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ExpandableListView;

public class CheckedExpanableListView extends ExpandableListView
{
	public CheckedExpanableListView( Context context )
	{
		super(context);
	}

	public CheckedExpanableListView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public CheckedExpanableListView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
	}

	public void setChildItemChecked( int groupPos, int childPos ){
		if (getChoiceMode() != CHOICE_MODE_SINGLE) return;

		int pos = getFlatListPosition(getPackedPositionForChild(groupPos, childPos));
		setItemChecked(pos, true);
		// boolean success = setSelectedChild(groupPos, childPos, true);
		//
		// if (!success) return;

		int count = getChildCount();
		for (int i = 0; i < count; i++){
			View view = getChildAt(i);

			if (view instanceof Checkable){
				((Checkable) view).setChecked(getSelectedItemPosition() == i);
			}
		}
	}

	public void clearCheckedState(){
		clearChoices();
		int count = getChildCount();
		for (int i = 0; i < count; i++){
			View view = getChildAt(i);

			if (view instanceof Checkable){
				((Checkable) view).setChecked(false);
			}
		}
	}
}
