package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import cc.wulian.smarthomev5.R;

public class CheckableFrame extends FrameLayout implements Checkable
{
	private boolean mChecked;

	public CheckableFrame( Context context ){
		super(context);
	}

	public CheckableFrame( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setChecked( boolean checked ){
		mChecked = checked;
		setBackgroundDrawable(checked ? new ColorDrawable(getResources().getColor(
				R.color.holo_blue_dark)) : null);

		// setBackgroundDrawable(checked
		// ? getResources().getDrawable(R.drawable.test_list_right_selector)
		// : null);
		dispatchSetChecked(checked);
		dispatchSetSelected(checked);
	}

	public void dispatchSetChecked( boolean selected ){
		final int count = getChildCount();
		for (int i = 0; i < count; i++){
			View view = getChildAt(i);
			if (view instanceof Checkable){
				((Checkable) view).setChecked(selected);
			}
		}
	}

	@Override
	public boolean isChecked(){
		return mChecked;
	}

	@Override
	public void toggle(){
		setChecked(!mChecked);
	}
}
