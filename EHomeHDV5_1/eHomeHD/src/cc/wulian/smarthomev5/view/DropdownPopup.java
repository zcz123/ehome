package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.yuantuo.customview.ui.ListPopupWindow;

/**
 *	just for test
 */
class DropdownPopup extends ListPopupWindow
{
	// Only measure this many items to get a decent max width.
	private static final int MAX_ITEMS_MEASURED = 15;
	
	private final Rect mTempRect = new Rect();
	int mDropDownWidth;
	
	private ListAdapter mAdapter;
	
	private Context mContext;


	public DropdownPopup( Context context )
	{
		this(context, null);
	}
	
	public DropdownPopup( Context context, AttributeSet attrs )
	{
		this(context, attrs, 0, 0);
	}

	public DropdownPopup( Context context, AttributeSet attrs, int defStyleRes )
	{
		this(context, attrs, 0, defStyleRes);
	}
	

	public DropdownPopup( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		
		mContext = context;
		mDropDownWidth = ViewGroup.LayoutParams.WRAP_CONTENT;

		setModal(true);
	}
	
	@Override
	public void setAdapter( ListAdapter adapter ){
		super.setAdapter(adapter);
		mAdapter = adapter;
	}

	@Override
	public void show(){
		final Drawable background = getBackground();
		int bgOffset = 0;
		if (background != null){
			background.getPadding(mTempRect);
			bgOffset = -mTempRect.left;
		}
		else{
			mTempRect.left = mTempRect.right = 0;
		}

		final int spinnerPaddingLeft = getAnchorView().getPaddingLeft();
		if (mDropDownWidth == WRAP_CONTENT){
			final int spinnerWidth = getAnchorView().getWidth();
			final int spinnerPaddingRight = getAnchorView().getPaddingRight();

			int contentWidth = measureContentWidth((SpinnerAdapter) mAdapter, getBackground());
			final int contentWidthLimit = mContext.getResources().getDisplayMetrics().widthPixels
					- mTempRect.left - mTempRect.right;
			if (contentWidth > contentWidthLimit){
				contentWidth = contentWidthLimit;
			}

			setContentWidth(Math.max(contentWidth, spinnerWidth - spinnerPaddingLeft
					- spinnerPaddingRight));
		}
		else if (mDropDownWidth == MATCH_PARENT){
			final int spinnerWidth = getAnchorView().getWidth();
			final int spinnerPaddingRight = getAnchorView().getPaddingRight();
			setContentWidth(spinnerWidth - spinnerPaddingLeft - spinnerPaddingRight);
		}
		else{
			setContentWidth(mDropDownWidth);
		}
		setHorizontalOffset(bgOffset + spinnerPaddingLeft);
		setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
		super.show();
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}


	int measureContentWidth( SpinnerAdapter adapter, Drawable background ){
		if (adapter == null){ return 0; }

		int width = 0;
		View itemView = null;
		int itemType = 0;
		final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		// Make sure the number of items we'll measure is capped. If it's a huge data set
		// with wildly varying sizes, oh well.
		int start = Math.max(0, getSelectedItemPosition());
		final int end = Math.min(adapter.getCount(), start + MAX_ITEMS_MEASURED);
		final int count = end - start;
		start = Math.max(0, start - (MAX_ITEMS_MEASURED - count));
		for (int i = start; i < end; i++){
			final int positionType = adapter.getItemViewType(i);
			if (positionType != itemType){
				itemType = positionType;
				itemView = null;
			}
			itemView = adapter.getView(i, itemView, null);
			if (itemView.getLayoutParams() == null){
				itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
			}
			itemView.measure(widthMeasureSpec, heightMeasureSpec);
			width = Math.max(width, itemView.getMeasuredWidth());
		}

		// Add background padding to measured width
		if (background != null){
			background.getPadding(mTempRect);
			width += mTempRect.left + mTempRect.right;
		}

		return width;
	}
}
