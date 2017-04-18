package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;

public abstract class SensorableDeviceCirlce270 extends SensorableDeviceImpl
{
	protected CustomProgressBar_270 mCustomView;
	protected TextView mNumText;
	protected ImageView mImageView;
	protected TextView mUnit;
	public SensorableDeviceCirlce270(Context context, String type) {
		super(context, type);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_sensor_circle_270, null);
	}
	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mCustomView = (CustomProgressBar_270) view
				.findViewById(R.id.device_cthv_custom_view);
		mUnit = (TextView) view.findViewById(R.id.device_unit);
		mNumText = (TextView) view.findViewById(R.id.device_cthv_num);
		mImageView = (ImageView) view.findViewById(R.id.device_cthv_image);
	}
}