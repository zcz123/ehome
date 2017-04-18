package cc.wulian.app.model.device.impls.controlable;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.utils.SpannableUtil;

public abstract class AbstractSwitchDevice extends ControlableDeviceImpl
{
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, null,true);
		}
	};
	private ImageView mBottomView;
	public AbstractSwitchDevice( Context context, String type )
	{
		super(context, type);
	}

	/**
	 * 开启时小图标
	 */
	public abstract int getOpenSmallIcon();

	/**
	 * 关闭时小图标
	 */
	public abstract int getCloseSmallIcon();

	/**
	 * 开启时大图
	 */
	public abstract int getOpenBigPic();

	/**
	 * 关闭时大图
	 */
	public abstract int getCloseBigPic();

	@Override
	public boolean isOpened() {
		return isSameAs(getOpenProtocol(), epData);
	}

	@Override
	public boolean isClosed() {
		return isSameAs(getCloseProtocol(), epData);
	}

	@Override
	public Drawable getStateSmallIcon() {
		return isOpened() ? getDrawable(getOpenSmallIcon()) : isClosed() ? getDrawable(getCloseSmallIcon()) : AbstractSwitchDevice.this
				.getDefaultStateSmallIcon();
	}

	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		drawables[0] = isOpened() ? getDrawable(getOpenBigPic()) : isClosed() ? getDrawable(getCloseBigPic()) : getDrawable(getCloseBigPic());
		return drawables;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		String state = "";
		int color = COLOR_NORMAL_ORANGE;

		if (isOpened()) {
			state = getString(R.string.device_state_open);
			color = COLOR_CONTROL_GREEN;
		}
		else if (isClosed()) {
			state = getString(R.string.device_state_close);
			color = COLOR_NORMAL_ORANGE;
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(getColor(color)));
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ) {
		return inflater.inflate(R.layout.device_two_state, container, false);
	}

	@Override
	public void onViewCreated( View view, Bundle saveState ) {
		super.onViewCreated(view, saveState);
		mBottomView = (ImageView) view.findViewById(R.id.dev_state_imageview_0);
		mBottomView.setOnClickListener(clickListener);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		Drawable drawable = getStateBigPictureArray()[0];
		mBottomView.setImageDrawable(drawable);
		drawable = mBottomView.getDrawable();
		if (drawable instanceof AnimationDrawable) {
			((AnimationDrawable) drawable).start();
		}
	}

}
