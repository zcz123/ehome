package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.util.AttributeSet;
import cc.wulian.smarthomev5.utils.DisplayUtil;

import com.yuantuo.customview.wheel.NumericWheelAdapter;
import com.yuantuo.customview.wheel.WheelView;

public class WLWheelView extends WheelView
{
	private final NumericWheelAdapter mAdapter;

	public WLWheelView( Context context )
	{
		this(context, null);
	}

	public WLWheelView( Context context, AttributeSet attrs )
	{
		this(context, attrs, 0);
	}

	public WLWheelView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);

		mAdapter = new NumericWheelAdapter(0, 59);

		setTextSize(DisplayUtil.dip2Pix(context, 32));
		setVisibleItems(1);
		setAdapter(mAdapter);

		setCurrentItem(0);
	}

	@Override
	protected void initResourcesIfNecessary(){
		super.initResourcesIfNecessary();
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
