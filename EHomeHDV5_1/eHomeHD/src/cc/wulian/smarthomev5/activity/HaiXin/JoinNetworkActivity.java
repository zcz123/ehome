package cc.wulian.smarthomev5.activity.HaiXin;

import android.os.Bundle;

import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.HaiXin.JoinNetworkFragment;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.uei.UeiMatchFragment;

public class JoinNetworkActivity extends EventBusActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, new JoinNetworkFragment())
                .commit();
    }
}
