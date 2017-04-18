package cc.wulian.smarthomev5.activity;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.iotc.config.IOTCDevConfigActivity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.fragment.monitor.EditMonitorInfoFragment;
import cc.wulian.smarthomev5.tools.Preference;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MonitoringConnectionActivity extends BaseActivity implements
        OnClickListener {
    private TextView mIpCamera, mHardCamera, mCloud3Camera,mCloud2Camera, mEagleCamera, mTitle;
    private ImageView mBcak, mDevices;
    private final static String TAG = "MonitoringConnectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_connection);
        initView();
        initEvent();
    }

    public void initView() {
        mIpCamera = (TextView) findViewById(R.id.tv_monitor_ip_video_camera);
        mHardCamera = (TextView) findViewById(R.id.tv_monitor_hard_disk_video_camera);
        mCloud2Camera = (TextView) findViewById(R.id.tv_monitor_cloud_two_video_camera);
        mCloud3Camera = (TextView) findViewById(R.id.tv_monitor_cloud_three_video_camera);
        mEagleCamera = (TextView) findViewById(R.id.tv_monitor_eagle_camera);
        mBcak = (ImageView) findViewById(com.wulian.icam.R.id.iv_scan_titlebar_back);
        mTitle = (TextView) findViewById(com.wulian.icam.R.id.titlebar_title);
        mTitle.setText(R.string.device_alarm_monitor_connect);
        mDevices = (ImageView) findViewById(com.wulian.icam.R.id.titlebar_devices_manual_input);
        mDevices.setVisibility(View.GONE);
    }

    public void initEvent() {
        mIpCamera.setOnClickListener(this);
        mHardCamera.setOnClickListener(this);
        mCloud2Camera.setOnClickListener(this);
        mCloud3Camera.setOnClickListener(this);
        mEagleCamera.setOnClickListener(this);
        mBcak.setOnClickListener(this);
    }
   //modfi syf
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_monitor_ip_video_camera:
            case R.id.tv_monitor_hard_disk_video_camera:
            case R.id.tv_monitor_cloud_two_video_camera:
            case R.id.tv_monitor_cloud_three_video_camera:
                Log.i(TAG,v.getTag().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable(EditMonitorInfoFragment.CAMERA_INFO, getCameraInfo(v.getTag().toString()));
                this.JumpTo(EditMonitorInfoActivity.class, bundle);
                break;
            case R.id.tv_monitor_eagle_camera:
                if(Preference.getPreferences().isUseAccount()){
                    showDialog();
                }else {
                    WLToast.showToast(getApplicationContext(),getResources().getString(R.string.cateye_setWifi_permissions_hit),0);
                }
                break;
            case R.id.iv_scan_titlebar_back:
                finish();
                break;
        }
    }

    private void showDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle("提示");
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(
                R.layout.device_door_lock_setting_account_dynamic, null);
        TextView textView = (TextView) view
                .findViewById(R.id.device_new_door_lock_account_dynamic_textview);
        textView.setText(getResources().getString(R.string.cateye_set_wifi_hint));//more_i_see

        builder.setContentView(view);
        builder.setPositiveButton(getResources().getString(com.wulian.icam.R.string.more_i_see));
        builder.setNegativeButton(null);
        final WLDialog mMessageDialog = builder.create();
        mMessageDialog.show();
        builder.setListener(new MessageListener() {
            @Override
            public void onClickPositive(View contentViewLayout) {
                Intent it = new Intent(MonitoringConnectionActivity.this, IOTCDevConfigActivity.class);
                it.putExtra("msgData", "");
                it.putExtra(IOTCDevConfigActivity.WIFI_CONFIG_TYPE, 0);
                startActivity(it);
                MonitoringConnectionActivity.this.finish();
                mMessageDialog.dismiss();
            }

            @Override
            public void onClickNegative(View contentViewLayout) {

            }
        });
    }

    /**
     * 生成三种类型的相机对象Info
     *
     * @param type
     * @return
     */
    public CameraInfo getCameraInfo(String type) {
        Resources resources = this.getResources();
        CameraInfo info = new CameraInfo();
        info.camId = -1;
        if (type.equals("1")) {
            info.camType = CameraInfo.CAMERA_TYPE_IP;
            info.camName = resources
                    .getString(R.string.monitor_ip_video_camera);
        }
        if (type.equals("2")) {
            info.camType = CameraInfo.CAMERA_TYPE_DVR_4;
            info.camName = resources
                    .getString(R.string.monitor_hard_disk_video_camera);
        }
        if (type.equals("3")) {
            info.camType = CameraInfo.CAMERA_TYPE_CLOUD_3;
            info.camName = resources
                    .getString(R.string.monitor_cloud_three_video_camera);
        }
        if (type.equals("4")) {
            info.camType = CameraInfo.CAMERA_TYPE_CLOUD_2;
            info.camName = resources
                    .getString(R.string.monitor_cloud_two_video_camera);
        }
        return info;
    }
}
