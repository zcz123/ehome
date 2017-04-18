package cc.wulian.smarthomev5.activity.monitor;

import android.os.Bundle;

import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.monitor.OtherCameraSettingFragment;

/**
 * Created by Administrator on 2017/1/12.
 */

public class OtherCameraSettingActivity extends EventBusActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new OtherCameraSettingFragment())
                    .commit();
        }
    }

}
