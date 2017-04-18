package cc.wulian.smarthomev5.fragment.monitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat;

/**
 * Created by hxc on 2017/1/12.
 */

public class OtherCameraSettingFragment extends WulianFragment {

    public static final int RESULT_OK = 0;
    public static final String RESULT_UID = "RESULT_UID";
    public static final String CAMERA_INFO = "camerainfo";
    private AbstractMonitorView monitorView;
    private CameraInfo cameraInfo;
    private int CamType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraInfo = (CameraInfo) getActivity().getIntent().getSerializableExtra(CAMERA_INFO);
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
        // this.mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayIconEnabled(true);
        getSupportActionBar().setDisplayIconTextEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowMenuEnabled(false);
        getSupportActionBar().setDisplayShowMenuTextEnabled(false);
        getSupportActionBar().setIconText(
                getResources().getString(R.string.device_ir_back));
        getSupportActionBar().setTitle(
                getResources().getString(R.string.device_ir_setting));
        getSupportActionBar().setLeftIconClickListener(
                new ActionBarCompat.OnLeftIconClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }
                });
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
}
