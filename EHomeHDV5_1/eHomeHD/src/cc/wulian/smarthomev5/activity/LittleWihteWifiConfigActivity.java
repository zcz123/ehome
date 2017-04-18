package cc.wulian.smarthomev5.activity;

import android.os.Bundle;

import com.wulian.icam.view.base.BaseFragmentActivity;

import cc.wulian.smarthomev5.fragment.more.littlewhite.LittleWhiteFragment;
import cc.wulian.smarthomev5.fragment.more.littlewhite.LittleWhiteWifiConfigFragment;

/**
 * Created by Administrator on 2016/11/15.
 */

public class LittleWihteWifiConfigActivity  extends EventBusActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new LittleWhiteWifiConfigFragment())
                    .commit();
        }
    }
}
