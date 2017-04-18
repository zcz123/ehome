package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckedTextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public abstract class AbstractIconTextSimpleAdapter<D> extends EditableBaseAdapter<D>
{
	/**
	 * item padding each other
	 */
	protected final int mDefaultPadding;

	public AbstractIconTextSimpleAdapter( Context context, List<D> data )
	{
		super(context, data);

		mDefaultPadding = DisplayUtil.dip2Pix(context, 8);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected View newView( Context context, LayoutInflater inflater, ViewGroup parent, int pos ){
		CheckedTextView textView = new CheckedTextView(context);
		textView.setTextColor(context.getResources().getColorStateList(
				R.drawable.text_color_highlight));
		textView.setGravity(Gravity.CENTER);
		textView.setSingleLine();

		textView.setPadding(0, mDefaultPadding, 0, mDefaultPadding);
		textView.setCompoundDrawablePadding(mDefaultPadding);
		textView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		return textView;
	}
}
