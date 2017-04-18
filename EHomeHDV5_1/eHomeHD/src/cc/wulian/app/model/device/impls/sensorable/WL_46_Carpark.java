package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * 车位距离与状态:int(单位MM),int(状态)<br/>
 * 注:0表示无遮挡,1表示有遮挡,2表示异常<br/>
 * Mark: why not use proxy create attr here??
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_CARPARK }, category = Category.C_OTHER)
public class WL_46_Carpark extends SensorableDeviceImpl {
	private static final String DATA_STATE_CLOSE_0 = "0";
	private static final String DATA_STATE_OPEN_1 = "1";

	private static final String UNIT_DOU = ",";
	private static final String UNIT_MM = " mm";

	private static final int SMALL_OPEN_D = R.drawable.device_carpark_obstructed;
	private static final int SMALL_CLOSE_D = R.drawable.device_carpark_unobstructed;

	private static final int BIG_OPEN_D = R.drawable.device_carpark_obstructed_big;
	private static final int BIG_CLOSE_D = R.drawable.device_carpark_unobstructed_big;

	private ImageView mBottomView;
	private TextView mView;
	private TextView mView1;

	private String park_space = getString(R.string.device_exception);

	public WL_46_Carpark(Context context, String type) {
		super(context, type);
	}

	@Override
	public Drawable getSensorStateSmallIcon() {
		return isOpened() ? getDrawable(SMALL_OPEN_D)
				: isClosed() ? getDrawable(SMALL_CLOSE_D) : WL_46_Carpark.this
						.getDefaultStateSmallIcon();
	}

	// Mark: why not use proxy create attr here??
	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		drawables[0] = isOpened() ? getDrawable(BIG_OPEN_D)
				: isClosed() ? getDrawable(BIG_CLOSE_D)
						: getDrawable(BIG_CLOSE_D);
		return drawables;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		if (!isNull(epData) && epData.contains(UNIT_DOU)) {
			String[] temp = epData.split(UNIT_DOU);
			sb.append(temp[0]);
			sb.append(UNIT_MM);
			return sb;
		}
		return sb;
	}

	public boolean isOpened() {
		if (isNull(epData) || !epData.contains(UNIT_DOU))
			return false;

		return isSameAs(getOpenProtocol(), epData.split(UNIT_DOU)[0]);
	}

	public boolean isClosed() {
		if (isNull(epData) || !epData.contains(UNIT_DOU))
			return true;

		return isSameAs(getCloseProtocol(), epData.split(UNIT_DOU)[0]);
	}

	public String getOpenProtocol() {
		return DATA_STATE_OPEN_1;
	}

	public String getCloseProtocol() {
		return DATA_STATE_CLOSE_0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		View view = super.onCreateView(inflater, container, saveState);
		view = inflater.inflate(R.layout.device_ems, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mBottomView = (ImageView) view.findViewById(R.id.dev_state_imageview_0);
		mView = (TextView) view.findViewById(R.id.dev_state_textview_0);
		mView1 = (TextView) view.findViewById(R.id.dev_state_textview_1);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if (!isNull(epData) && epData.contains(UNIT_DOU)) {
			String[] temp = epData.split(UNIT_DOU);
			if (temp[0].equals("65535")) {
				mView.setText(temp[0] + UNIT_MM);
			} else {
				mView.setText(getString(R.string.device_parkspace) + temp[0]
						+ UNIT_MM);
				park_space = isOpened() ? getString(R.string.device_park_no_obstacle)
						: isClosed() ? getString(R.string.device_park_has_obstacle)
								: getString(R.string.device_exception);
				mView1.setText(park_space);
			}
		}
		mBottomView.setImageDrawable(getStateBigPictureArray()[0]);
	}

	@Override
	public String unit(String ep,String epType) {
		return "MM";
	}

	@Override
	public String unitName() {
		return mResources.getString(R.string.device_parkspace);
	}

}