package cc.wulian.smarthomev5.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.icam.view.base.BaseFragmentActivity;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.CameraDao;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;

/**
 * Created by Administrator on 2016/11/15.
 */

public class Cloud2DeviceSettingActivity extends BaseFragmentActivity implements View.OnClickListener {
    private LinearLayout ll_delete_device;
    private CameraInfo cameraInfo;
    private ImageView titlebar_back;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_cloud2_device_setting);
        initView();
        setListener();
        initData();
    }

    private void initView() {
        ll_delete_device = (LinearLayout) findViewById(R.id.ll_delete_device);
        ((TextView) findViewById(com.wulian.icam.R.id.titlebar_title)).setText(getResources().getString(R.string.setting_device_setting));
        titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
    }

    private void initData() {
        cameraInfo = (CameraInfo) getIntent().getSerializableExtra("camerainfo");
    }

    private void setListener() {
        ll_delete_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CameraDao().delete(cameraInfo);//删除摄像机
                Cloud2DeviceSettingActivity.this.finish();
            }
        });
        titlebar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
