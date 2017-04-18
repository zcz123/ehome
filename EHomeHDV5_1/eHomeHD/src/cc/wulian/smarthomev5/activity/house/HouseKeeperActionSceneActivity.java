package cc.wulian.smarthomev5.activity.house;

import android.os.Bundle;

import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionSceneFragment;

/**
 * Created by Administrator on 2017/1/19 0019.
 */

public class HouseKeeperActionSceneActivity extends EventBusActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            HouseKeeperActionSceneFragment fragment = new HouseKeeperActionSceneFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }
}
