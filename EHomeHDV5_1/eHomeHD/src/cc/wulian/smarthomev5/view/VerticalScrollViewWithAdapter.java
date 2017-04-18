package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import cc.wulian.smarthomev5.view.HorizontalScrollViewWithAdapter.ScrollViewContent;

public class VerticalScrollViewWithAdapter extends ScrollView
{
	private ScrollViewContent mContent;

	public VerticalScrollViewWithAdapter( Context context )
	{
		super(context);
		addContent(context, null);
	}

	public VerticalScrollViewWithAdapter( Context context, AttributeSet attrs )
	{
		super(context, attrs);
		addContent(context, attrs);
	}

	public VerticalScrollViewWithAdapter( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
		addContent(context, attrs);
	}

	private void addContent( Context context, AttributeSet attrs ){
		mContent = new ScrollViewContent(context, attrs);
		mContent.setOrientation(ScrollViewContent.VERTICAL);
		addView(mContent);
	}

	public ListAdapter getAdapter(){
		return mContent.getAdapter();
	}

	public void setAdapter( ListAdapter adapter ){
		mContent.setAdapter(adapter);
	}

	public final void setEmptyView( View emptyView ){
		mContent.setEmptyView(emptyView);
	}

	public final View getEmptyView(){
		return mContent.getEmptyView();
	}

	public OnItemClickListener getOnItemClickListener(){
		return mContent.getOnItemClickListener();
	}

	public void setOnItemClickListener( OnItemClickListener onItemClickListener ){
		mContent.setOnItemClickListener(onItemClickListener);
	}

	public void setItemSpacing( int space ){
		mContent.setItemSpacing(space);
	}
}