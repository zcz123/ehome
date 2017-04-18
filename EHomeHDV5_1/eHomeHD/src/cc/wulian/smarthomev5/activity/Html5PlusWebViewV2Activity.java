package cc.wulian.smarthomev5.activity;


import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.common.Html5PlusWebViewV2Fragment;

public class Html5PlusWebViewV2Activity extends EventBusActivity{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new Html5PlusWebViewV2Fragment())
                    .commit();
        }
    }

    @Override
    protected boolean finshSelf() {
        return false;
    }

    @Override
    public boolean fingerRightFromCenter() {
        return false;
    }

}
