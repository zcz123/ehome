package cc.wulian.smarthomev5.fragment.monitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.QRScanActivity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.event.ScanEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

import com.yuantuo.customview.ui.WLToast;

public class EditMonitorInfoFragment extends WulianFragment {

	public static final int RESULT_OK = 0;
	public static final String RESULT_UID = "RESULT_UID";
	public static final String CAMERA_INFO = "camera_info";
	private AbstractMonitorView monitorView;
	private CameraInfo cameraInfo;
	private int CamType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cameraInfo = (CameraInfo) getArguments().getSerializable(CAMERA_INFO);
		CamType = cameraInfo.getCamType();
		initBar();
		switch (CamType) {
		case 1:// IP摄像机
			monitorView = new IPMonitorView(mActivity, cameraInfo);
			break;
		case 4:// 四路
			monitorView = new DVR4MonitorView(mActivity, cameraInfo);
			break;
		case 8:// 八路
			monitorView = new DVR4MonitorView(mActivity, cameraInfo);
			break;
		case 11:// 云一代
			monitorView = new CLOUD_1_MonitorView(mActivity, cameraInfo);
			break;
		case 12:// 云二代
			monitorView = new CLOUD_2_MonitorView(mActivity, cameraInfo);
			break;
		case 13:// 云三代
			monitorView = new CLOUD_3_MonitorView(mActivity, cameraInfo);
			break;
		case 21:// 物联摄像头A
			monitorView = new CLOUD_WL_MonitorView(mActivity, cameraInfo);
			break;
		case 22:// 物联摄像头A
		case 23:
			monitorView = new CLOUD_WL_MonitorView(mActivity, cameraInfo);
			break;
		default:
			break;

		}
	}

	private void initBar() {
		mActivity.resetActionMenu();
		if (cameraInfo.getCamId() == -1) {
			getSupportActionBar().setTitle(
					getResources()
							.getString(R.string.monitor_new_build_monitor));
		} else {
			getSupportActionBar().setTitle(
					getResources().getString(R.string.home_monitor_setting_monitor));
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.home_camera_monitor));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return monitorView.onCreateView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		monitorView.onViewCreated();
	}

	
	public void onEventMainThread(ScanEvent event) {
		mActivity.JumpForFragmentResult(this, QRScanActivity.class, 0,
				null);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String uid = data.getStringExtra(RESULT_UID);
					if (!StringUtil.isNullOrEmpty(uid))
						monitorView.setUID(uid);
				}
		}
		if (resultCode == 1) {
			if (data != null) {
				String uid = data.getStringExtra(CameraInfo.CAMERA_KEY_UID);
				if (!StringUtil.isNullOrEmpty(uid)){
					monitorView.setUID(uid);
					}
				else{
					WLToast.showToast(mActivity, getResources().getString(R.string.monitor_search_no_result), WLToast.TOAST_LONG);
				}
			}
		}
	}

}
