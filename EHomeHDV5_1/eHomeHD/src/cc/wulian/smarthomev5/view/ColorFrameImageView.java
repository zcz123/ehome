package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class ColorFrameImageView extends ImageView
{
	private int mPadding;
	private Rect mFrameRect;
	private Paint mFramePaint;

	public ColorFrameImageView( Context context )
	{
		super(context);
		initUtil(context);
	}

	public ColorFrameImageView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
		initUtil(context);
	}

	public ColorFrameImageView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
		initUtil(context);
	}

	private void initUtil( Context context ){
		mPadding = DisplayUtil.dip2Pix(context, 8);
		mFramePaint = new Paint();
		mFramePaint.setStrokeWidth(mPadding);
		mFramePaint.setAntiAlias(true);
		mFramePaint.setARGB(255, 176, 187, 185);
		mFramePaint.setStyle(Style.STROKE);
	}

	public void setColor( int color ){
		mFramePaint.setColor(color);
	}

	public void setStrokeWidth( float width ){
		mFramePaint.setStrokeWidth(width);
	}

	@Override
	protected void onDraw( Canvas canvas ){
		final Drawable srcDrawable = getDrawable();
		if (srcDrawable == null) return;

		mFrameRect = canvas.getClipBounds();

		int w = getMeasuredWidth();
		int h = getMeasuredHeight();

		canvas.save();
		canvas.scale((w - mPadding) / (w * 1F), (h - mPadding) / (h * 1F), w / 2, h / 2);
		super.onDraw(canvas);
		canvas.restore();

		canvas.drawRect(mFrameRect, mFramePaint);
	}
}
