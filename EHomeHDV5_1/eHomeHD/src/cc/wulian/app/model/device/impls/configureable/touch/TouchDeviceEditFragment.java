package cc.wulian.app.model.device.impls.configureable.touch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.DeviceTool;

public class TouchDeviceEditFragment extends WulianFragment {
	
	private TextView editAbstractTouchDeviceTextView;
	private LinearLayout editAbstractTouchDeviceLinearLayout;
	
	public WulianDevice mWulianDevice;
	
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	public static final String DEVICETYPE ="DEVICETYPE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		
		String gwID = (String) bundle.getString(GWID);
		String deviceID = (String) bundle.getString(DEVICEID);
		
		mWulianDevice = DeviceCache.getInstance(mActivity).getDeviceByID(mActivity, gwID, deviceID);
		initBar();
	}
	//actionBar
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(DeviceTool.getDeviceShowName(mWulianDevice));
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.device_edit_abstracttouchdevice_fragment,null);
		
		editAbstractTouchDeviceTextView = (TextView) view.findViewById(R.id.device_edit_abstracttouchdevice_edit_textview);
		
		editAbstractTouchDeviceTextView.setText(mActivity.getResources().getString(R.string.device_bind_scene));
		editAbstractTouchDeviceLinearLayout = (LinearLayout) view.findViewById(R.id.device_edit_abstracttouchdevice_edit_linearlayout);
		editAbstractTouchDeviceLinearLayout.addView( mWulianDevice.onCreateSettingView(inflater, container));
		
		return view;
	}
}
