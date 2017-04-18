package cc.wulian.app.model.device.impls.nouseable;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.impls.AbstractDevice;
public class AbstractNotUseableDevice extends AbstractDevice
{
	public AbstractNotUseableDevice( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public void refreshDevice(){
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		TextView textView = new TextView(inflater.getContext());
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		textView.setLayoutParams(lp);
		textView.setText(cc.wulian.smarthomev5.R.string.device_not_controllable);
		LinearLayout rootView = (LinearLayout)inflater.inflate(R.layout.device_empty_content, null);
		rootView.addView(textView);
		return rootView;
	}
	@Override
	public boolean isDeviceUseable() {
		return false;
	}

	@Override
	public boolean isAutoControl(boolean isSimple) {
		return false;
	}

}