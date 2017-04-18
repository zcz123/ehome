package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StickyFooterListView extends ListView
{
	private View mStickyView;

	public StickyFooterListView( Context context )
	{
		super(context);
	}

	public StickyFooterListView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public StickyFooterListView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
	}

	public void setStickyView( View view ){
		mStickyView = view;
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final View stickView = mStickyView;
		if (stickView != null && stickView.getVisibility() == View.VISIBLE){
			int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
			int listViewMeasuredHeight = getMeasuredHeight();
			if (listViewMeasuredHeight <= 0){
				// if listview has 0 height, do nothing
				return;
			}
			//
			ViewGroup.LayoutParams lp = stickView.getLayoutParams();
			//
			int paddingWidth = stickView.getPaddingLeft() + stickView.getPaddingRight();
			int paddingHeight = stickView.getPaddingTop() + stickView.getPaddingBottom();
			if (lp instanceof MarginLayoutParams){
				MarginLayoutParams mlp = (MarginLayoutParams) lp;
				paddingWidth += mlp.leftMargin + mlp.rightMargin;
				paddingHeight += mlp.topMargin + mlp.bottomMargin;
			}
			//
			mStickyView.measure(getChildMeasureSpec(widthMeasureSpec, paddingWidth, lp.width),
					getChildMeasureSpec(heightMeasureSpec, paddingHeight, lp.height));
			//
			int followViweMeasuredHeight = stickView.getMeasuredHeight();
			// 如果剩余的高度不够view显示，那么约束ListView的尺寸，释放空间
			if (maxHeight - listViewMeasuredHeight < followViweMeasuredHeight){
				setMeasuredDimension(getMeasuredWidth(), listViewMeasuredHeight - followViweMeasuredHeight);
			}
		}
	}
}
