package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wulian.icam.R;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.doorlock.AbstractDoorLock;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.adapter.BindDoorLockAdapter;
import cc.wulian.smarthomev5.fragment.monitor.BindDoorlockFragment;
import cc.wulian.smarthomev5.fragment.more.littlewhite.LittleWhiteFragment;
import cc.wulian.smarthomev5.tools.SendMessage;

/**
 * Created by Administrator on 2016/11/15.
 */

public class BindDoorlockActivity extends EventBusActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new BindDoorlockFragment())
                    .commit();
        }
    }
}
