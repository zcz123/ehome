package cc.wulian.smarthomev5.activity;

import android.os.Bundle;

import cc.wulian.smarthomev5.fragment.more.littlewhite.LittleWhiteFragment;

/**
 * Created by hxc on 2016/11/15.
 */

public class LittleWhiteActivity extends EventBusActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new LittleWhiteFragment())
                    .commit();
        }
    }
}
