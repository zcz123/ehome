package cc.wulian.smarthomev5.activity.uei;

import android.app.Activity;
import android.os.Bundle;

import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.uei.UeiMatchFragment;

public class UeiMatchActivity extends EventBusActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=null;
        WulianFragment wulianFragment=null;
        if(getIntent()!=null){
            wulianFragment=new UeiMatchFragment();
            args=getIntent().getBundleExtra("args");
        }
        if(wulianFragment!=null){
            wulianFragment.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, wulianFragment)
                    .commit();
        }
    }
}
